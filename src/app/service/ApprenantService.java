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

import app.model.Apprenant;

public class ApprenantService {
    private final HttpClient httpClient;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);

    public ApprenantService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();
    }

    // Recuperer tous les apprenants
    public List<Apprenant> getAllApprenants() throws Exception {
        String url = ApiConfig.apiPath("/api/apprenants");
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
            return parseApprenantsJson(response.body());
        } else {
            throw new Exception("Erreur recuperation apprenants: " + response.statusCode());
        }
    }

    // Ajouter un apprenant
    public Apprenant ajouterApprenant(Apprenant apprenant) throws Exception {
        String url = ApiConfig.apiPath("/api/apprenants");
        String body = "{\"prenom\":\"" + apprenant.getPrenom() + "\",\"nom\":\"" + apprenant.getNom() + "\",\"email\":\"" + apprenant.getEmail() + "\",\"mot_de_passe\":\"" + apprenant.getMot_de_passe() + "\",\"role\":\"" + apprenant.getRole() + "\"}";
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
        if (response.statusCode() == 201) {
            return apprenant; // L'API devrait retourner l'objet créé
        } else {
            throw new Exception("Erreur ajout apprenant: " + response.statusCode());
        }
    }

    // Parser JSON pour liste d'apprenants
    private List<Apprenant> parseApprenantsJson(String json) {
        List<Apprenant> apprenants = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{[^}]*\\}");
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            String objJson = matcher.group();
            Apprenant a = parseApprenantJson(objJson);
            if (a != null && a.getId_apprenant() > 0) {
                apprenants.add(a);
            }
        }
        return apprenants;
    }

    // Parser JSON pour un apprenant
    private Apprenant parseApprenantJson(String json) {
        Apprenant a = new Apprenant();

        // Extraire id_apprenant
        Pattern idPattern = Pattern.compile("\"id_apprenant\"\\s*:\\s*(\\d+)");
        Matcher idMatcher = idPattern.matcher(json);
        if (idMatcher.find()) {
            a.setId_apprenant(Integer.parseInt(idMatcher.group(1)));
        }

        // Extraire pseudo
        Pattern pseudoPattern = Pattern.compile("\"pseudo\"\\s*:\\s*\"([^\"]+)\"");
        Matcher pseudoMatcher = pseudoPattern.matcher(json);
        if (pseudoMatcher.find()) {
            a.setPseudo(pseudoMatcher.group(1));
        }

        // Extraire prenom
        Pattern prenomPattern = Pattern.compile("\"prenom\"\\s*:\\s*\"([^\"]+)\"");
        Matcher prenomMatcher = prenomPattern.matcher(json);
        if (prenomMatcher.find()) {
            a.setPrenom(prenomMatcher.group(1));
        }

        // Extraire nom
        Pattern nomPattern = Pattern.compile("\"nom\"\\s*:\\s*\"([^\"]+)\"");
        Matcher nomMatcher = nomPattern.matcher(json);
        if (nomMatcher.find()) {
            a.setNom(nomMatcher.group(1));
        }

        // Extraire email
        Pattern emailPattern = Pattern.compile("\"email\"\\s*:\\s*\"([^\"]+)\"");
        Matcher emailMatcher = emailPattern.matcher(json);
        if (emailMatcher.find()) {
            a.setEmail(emailMatcher.group(1));
        }

        // Extraire telephone
        Pattern telPattern = Pattern.compile("\"telephone\"\\s*:\\s*\"([^\"]+)\"");
        Matcher telMatcher = telPattern.matcher(json);
        if (telMatcher.find()) {
            a.setTelephone(telMatcher.group(1));
        }

        // Extraire mot_de_passe
        Pattern mdpPattern = Pattern.compile("\"mot_de_passe\"\\s*:\\s*\"([^\"]+)\"");
        Matcher mdpMatcher = mdpPattern.matcher(json);
        if (mdpMatcher.find()) {
            a.setMot_de_passe(mdpMatcher.group(1));
        }

        // Extraire role
        Pattern rolePattern = Pattern.compile("\"role\"\\s*:\\s*\"([^\"]+)\"");
        Matcher roleMatcher = rolePattern.matcher(json);
        if (roleMatcher.find()) {
            a.setRole(roleMatcher.group(1));
        }

        return a;
    }
}
