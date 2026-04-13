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

import app.model.Presence;

public class PresenceService {
    private final HttpClient httpClient;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);

    public PresenceService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();
    }

    // Recuperer les presences pour une session
    public List<Presence> getPresencesBySession(int idSession) throws Exception {
        // Recuperer toutes les presences puis filtrer
        List<Presence> allPresences = getAllPresences();
        return allPresences.stream()
            .filter(p -> p.getId_session() == idSession)
            .collect(Collectors.toList());
    }

    // Recuperer toutes les presences (public pour stats)
    public List<Presence> getAllPresences() throws Exception {
        String url = ApiConfig.apiPath("/api/presences");
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
            return parsePresencesJson(response.body());
        } else {
            throw new Exception("Erreur recuperation presences: " + response.statusCode());
        }
    }

    // Mettre a jour une presence
    public void updatePresence(Presence presence) throws Exception {
        String url = ApiConfig.apiPath("/api/presences/" + presence.getId_presence());
        String body = "{\"id_session\":" + presence.getId_session() + ",\"id_apprenant\":" + presence.getId_apprenant() + ",\"present\":" + presence.isPresent() + "}";
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .timeout(REQUEST_TIMEOUT)
            .PUT(HttpRequest.BodyPublishers.ofString(body));

        String authorization = AuthSession.getAuthorizationHeaderValue();
        if (authorization != null) {
            requestBuilder.header("Authorization", authorization);
        }

        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Erreur mise a jour presence: " + response.statusCode());
        }
    }

    // Parser JSON pour liste de presences
    private List<Presence> parsePresencesJson(String json) {
        List<Presence> presences = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{[^}]*\\}");
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            String objJson = matcher.group();
            Presence p = parsePresenceJson(objJson);
            if (p != null && p.getId_presence() > 0) {
                presences.add(p);
            }
        }
        return presences;
    }

    // Parser JSON pour une presence
    private Presence parsePresenceJson(String json) {
        Presence p = new Presence();

        // Extraire id_presence
        Pattern idPattern = Pattern.compile("\"id_presence\"\\s*:\\s*(\\d+)");
        Matcher idMatcher = idPattern.matcher(json);
        if (idMatcher.find()) {
            p.setId_presence(Integer.parseInt(idMatcher.group(1)));
        }

        // Extraire id_session
        Pattern sessionPattern = Pattern.compile("\"id_session\"\\s*:\\s*(\\d+)");
        Matcher sessionMatcher = sessionPattern.matcher(json);
        if (sessionMatcher.find()) {
            p.setId_session(Integer.parseInt(sessionMatcher.group(1)));
        }

        // Extraire id_apprenant
        Pattern apprenantPattern = Pattern.compile("\"id_apprenant\"\\s*:\\s*(\\d+)");
        Matcher apprenantMatcher = apprenantPattern.matcher(json);
        if (apprenantMatcher.find()) {
            p.setId_apprenant(Integer.parseInt(apprenantMatcher.group(1)));
        }

        // Extraire present
        Pattern presentPattern = Pattern.compile("\"present\"\\s*:\\s*(true|false)");
        Matcher presentMatcher = presentPattern.matcher(json);
        if (presentMatcher.find()) {
            p.setPresent(Boolean.parseBoolean(presentMatcher.group(1)));
        }

        return p;
    }
}
