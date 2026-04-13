package app.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthService {

    private final HttpClient httpClient;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);

    public AuthService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();
    }

    public void login(String email, String motDePasse) throws Exception {
        AuthSession.clear();

        String cleanEmail = email == null ? "" : email.trim();
        String cleanMotDePasse = motDePasse == null ? "" : motDePasse.trim();

        if (cleanEmail.isEmpty() || cleanMotDePasse.isEmpty()) {
            throw new Exception("Email et mot de passe requis");
        }

        // Supporte plusieurs backends : certains attendent password, d'autres mot_de_passe (et certains valident strictement le payload).
        String[] bodies = new String[] {
            "{"
                + "\"email\":\"" + escapeJson(cleanEmail) + "\","
                + "\"password\":\"" + escapeJson(cleanMotDePasse) + "\""
                + "}",
            "{"
                + "\"email\":\"" + escapeJson(cleanEmail) + "\","
                + "\"mot_de_passe\":\"" + escapeJson(cleanMotDePasse) + "\""
                + "}",
            "{"
                + "\"email\":\"" + escapeJson(cleanEmail) + "\","
                + "\"password\":\"" + escapeJson(cleanMotDePasse) + "\","
                + "\"mot_de_passe\":\"" + escapeJson(cleanMotDePasse) + "\""
                + "}"
        };

        // Chemins courants : selon que le router est monté sur /auth, /api/auth, etc.
        String[] candidates = new String[] {
            "/api/auth/login",
            "/auth/login",
            "/api/login",
            "/login"
        };

        HttpResponse<String> response = null;
        String lastTriedUrl = null;
        Exception lastNetworkError = null;

        outer:
        for (String path : candidates) {
            lastTriedUrl = ApiConfig.apiPath(path);
            for (String body : bodies) {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(lastTriedUrl))
                        .header("Content-Type", "application/json")
                        .timeout(REQUEST_TIMEOUT)
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                    response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    // Si 404, on tente le chemin suivant
                    if (response.statusCode() == 404) {
                        break;
                    }

                    // Si 400/401/403, le backend peut être strict sur le nom du champ : on essaie le payload suivant sur le même endpoint.
                    if (response.statusCode() == 400 || response.statusCode() == 401 || response.statusCode() == 403) {
                        continue;
                    }

                    break outer;
                } catch (Exception network) {
                    lastNetworkError = network;
                }
            }
        }

        if (response == null) {
            String detail = lastNetworkError == null ? "" : (": " + lastNetworkError.getMessage());
            throw new Exception("Impossible de contacter l'API d'authentification" + detail);
        }

        int status = response.statusCode();
        String responseBody = response.body() == null ? "" : response.body();

        if (status == 200 || status == 201) {
            String token = extractToken(responseBody);
            String role = extractRole(responseBody);
            String emailFromApi = extractEmail(responseBody);

            if (token == null || token.isBlank()) {
                throw new Exception("Connexion OK mais token manquant (verifie la reponse JSON de /login)");
            }

            AuthSession.setSession(token, role == null ? "user" : role, emailFromApi == null ? cleanEmail : emailFromApi);
            return;
        }

        if (status == 401 || status == 403) {
            String snippet = responseBody.length() > 200 ? responseBody.substring(0, 200) + "…" : responseBody;
            if (!snippet.isBlank()) {
                throw new Exception("Email ou mot de passe incorrect (" + status + ") sur " + lastTriedUrl + " : " + snippet);
            }
            throw new Exception("Email ou mot de passe incorrect (" + status + ") sur " + lastTriedUrl);
        }

        String snippet = responseBody.length() > 200 ? responseBody.substring(0, 200) + "…" : responseBody;
        throw new Exception("Erreur API login (" + status + ") sur " + lastTriedUrl + " : " + snippet);
    }

    private static String extractToken(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }

        Pattern p = Pattern.compile("\"(?:token|accessToken|jwt)\"\\s*:\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private static String extractRole(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }

        Pattern p = Pattern.compile("\"role\"\\s*:\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private static String extractEmail(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }

        Pattern p = Pattern.compile("\"email\"\\s*:\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1).trim().toLowerCase();
        }
        return null;
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"");
    }
}
