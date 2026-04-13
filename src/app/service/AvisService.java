package app.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import app.model.Avis;

public class AvisService {
    private final HttpClient httpClient;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);

    public AvisService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();
    }

    // Recuperer les avis pour une session
    public List<Avis> getAvisBySession(int idSession) throws Exception {
        // Recuperer tous les avis puis filtrer
        List<Avis> allAvis = getAllAvis();
        return allAvis.stream()
            .filter(a -> a.getId_session() == idSession)
            .collect(Collectors.toList());
    }

    // Recuperer tous les avis (public pour stats)
    public List<Avis> getAllAvis() throws Exception {
        String url = ApiConfig.apiPath("/api/avis");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(REQUEST_TIMEOUT)
            .GET();

        String authorization = AuthSession.getAuthorizationHeaderValue();
        if (authorization != null) {
            requestBuilder.header("Authorization", authorization);
        }

        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return parseAvisListJson(response.body());
        } else {
            throw new Exception("Erreur recuperation avis: " + response.statusCode());
        }
    }

    // Ajouter un avis
    public void ajouterAvis(Avis avis) throws Exception {
        String url = ApiConfig.apiPath("/api/avis/me");
        String body = "{\"id_session\":" + avis.getId_session() + ",\"commentaire\":\"" + avis.getCommentaire() + "\",\"note\":" + avis.getNote() + "}";
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .timeout(REQUEST_TIMEOUT)
            .POST(HttpRequest.BodyPublishers.ofString(body));

        String authorization = AuthSession.getAuthorizationHeaderValue();
        if (authorization != null) {
            requestBuilder.header("Authorization", authorization);
        }

        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new Exception("Erreur ajout avis: " + response.statusCode());
        }
    }

    // Parser JSON pour liste d'avis
    private List<Avis> parseAvisListJson(String json) {
        List<Avis> avis = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{[^}]*\\}");
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            String objJson = matcher.group();
            Avis a = parseSingleAvisJson(objJson);
            if (a != null && a.getId_avis() > 0) {
                avis.add(a);
            }
        }
        return avis;
    }

    // Parser JSON pour un avis
    private Avis parseSingleAvisJson(String json) {
        Avis a = new Avis();

        // Extraire id_avis
        Pattern idPattern = Pattern.compile("\"id_avis\"\\s*:\\s*(\\d+)");
        Matcher idMatcher = idPattern.matcher(json);
        if (idMatcher.find()) {
            a.setId_avis(Integer.parseInt(idMatcher.group(1)));
        }

        // Extraire id_session
        Pattern sessionPattern = Pattern.compile("\"id_session\"\\s*:\\s*(\\d+)");
        Matcher sessionMatcher = sessionPattern.matcher(json);
        if (sessionMatcher.find()) {
            a.setId_session(Integer.parseInt(sessionMatcher.group(1)));
        }

        // Extraire id_apprenant
        Pattern apprenantPattern = Pattern.compile("\"id_apprenant\"\\s*:\\s*(\\d+)");
        Matcher apprenantMatcher = apprenantPattern.matcher(json);
        if (apprenantMatcher.find()) {
            a.setId_apprenant(Integer.parseInt(apprenantMatcher.group(1)));
        }

        // Extraire commentaire
        Pattern commentairePattern = Pattern.compile("\"commentaire\"\\s*:\\s*\"([^\"]+)\"");
        Matcher commentaireMatcher = commentairePattern.matcher(json);
        if (commentaireMatcher.find()) {
            a.setCommentaire(commentaireMatcher.group(1));
        }

        // Extraire note
        Pattern notePattern = Pattern.compile("\"note\"\\s*:\\s*(\\d+)");
        Matcher noteMatcher = notePattern.matcher(json);
        if (noteMatcher.find()) {
            a.setNote(Integer.parseInt(noteMatcher.group(1)));
        }

        return a;
    }
}
