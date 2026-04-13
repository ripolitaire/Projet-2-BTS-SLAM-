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

import app.model.Formation;

public class FormationService {
    private final HttpClient httpClient;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);

    public FormationService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();
    }

    // Recuperer toutes les formations depuis l'API
    public List<Formation> getAllFormations() throws Exception {
        String url = ApiConfig.apiPath("/api/formations");
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(REQUEST_TIMEOUT)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return parseFormationsJson(response.body());
        } else {
            throw new Exception("Erreur recuperation formations: " + response.statusCode());
        }
    }

    // Recuperer une formation par ID depuis l'API
    public Formation getFormationById(int id) throws Exception {
        String url = ApiConfig.apiPath("/api/formations/" + id);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(REQUEST_TIMEOUT)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return parseFormationJson(response.body());
        } else {
            throw new Exception("Erreur recuperation formation: " + response.statusCode());
        }
    }

    // Parser JSON simple pour liste de formations
    private List<Formation> parseFormationsJson(String json) {
        List<Formation> formations = new ArrayList<>();
        // Parsing simple : chercher les objets dans le tableau
        Pattern pattern = Pattern.compile("\\{[^}]*\\}");
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            String objJson = matcher.group();
            Formation f = parseFormationJson(objJson);
            if (f != null && f.getId_formation() > 0) {
                formations.add(f);
            }
        }
        return formations;
    }

    // Parser JSON simple pour une formation
    private Formation parseFormationJson(String json) {
        Formation f = new Formation();

        // Extraire id_formation
        Pattern idPattern = Pattern.compile("\"id_formation\"\\s*:\\s*(\\d+)");
        Matcher idMatcher = idPattern.matcher(json);
        if (idMatcher.find()) {
            f.setId_formation(Integer.parseInt(idMatcher.group(1)));
        }

        // Extraire nom
        Pattern nomPattern = Pattern.compile("\"nom\"\\s*:\\s*\"([^\"]+)\"");
        Matcher nomMatcher = nomPattern.matcher(json);
        if (nomMatcher.find()) {
            f.setNom(nomMatcher.group(1));
        }

        // Extraire description
        Pattern descPattern = Pattern.compile("\"description\"\\s*:\\s*\"([^\"]+)\"");
        Matcher descMatcher = descPattern.matcher(json);
        if (descMatcher.find()) {
            f.setDescription(descMatcher.group(1));
        }

        // Extraire duree
        Pattern dureePattern = Pattern.compile("\"duree\"\\s*:\\s*\"([^\"]+)\"");
        Matcher dureeMatcher = dureePattern.matcher(json);
        if (dureeMatcher.find()) {
            f.setDuree(dureeMatcher.group(1));
        }

        // Extraire niveau
        Pattern niveauPattern = Pattern.compile("\"niveau\"\\s*:\\s*\"([^\"]+)\"");
        Matcher niveauMatcher = niveauPattern.matcher(json);
        if (niveauMatcher.find()) {
            f.setNiveau(niveauMatcher.group(1));
        }

        return f;
    }
}