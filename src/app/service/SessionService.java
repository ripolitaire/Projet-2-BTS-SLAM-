package app.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.model.Session;

public class SessionService {
    private final HttpClient httpClient;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);
    private final InscriptionService inscriptionService = new InscriptionService();

    public SessionService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();
    }

    // Recuperer toutes les sessions depuis l'API
    public List<Session> getToutesSessions() throws Exception {
        String url = ApiConfig.apiPath("/api/sessions");
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
            return parseSessionsJson(response.body());
        } else {
            throw new Exception("Erreur recuperation sessions: " + response.statusCode());
        }
    }

    // Recuperer les sessions accessibles pour un apprenant (via le service d'inscriptions)
    public List<Session> getSessionsVorApprenant(String email) throws Exception {
        Set<Integer> sessionIds = inscriptionService.getSessionIdsPourApprenant(email);
        if (sessionIds.isEmpty()) {
            return List.of();
        }

        List<Session> toutes = getToutesSessions();
        List<Session> result = new ArrayList<>();
        for (Session session : toutes) {
            if (session != null && sessionIds.contains(session.getId())) {
                result.add(session);
            }
        }
        return result;
    }

    // Recuperer une session par ID
    public Session getSessionById(int id) throws Exception {
        String url = ApiConfig.apiPath("/api/sessions/" + id);
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
            return parseSessionJson(response.body());
        } else {
            throw new Exception("Erreur recuperation session: " + response.statusCode());
        }
    }

    // Parser JSON pour liste de sessions
    private List<Session> parseSessionsJson(String json) {
        List<Session> sessions = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{[^}]*\\}");
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            String objJson = matcher.group();
            Session s = parseSessionJson(objJson);
            if (s != null && s.getId() > 0) {
                sessions.add(s);
            }
        }
        return sessions;
    }

    // Parser JSON pour une session
    private Session parseSessionJson(String json) {
        Session s = new Session();

        // Extraire id (ou id_session)
        Pattern idPattern = Pattern.compile("\"(?:id|id_session)\"\\s*:\\s*(\\d+)");
        Matcher idMatcher = idPattern.matcher(json);
        if (idMatcher.find()) {
            s.setId(Integer.parseInt(idMatcher.group(1)));
        }

        // Extraire formation (nom de formation)
        Pattern formationPattern = Pattern.compile("\"formation\"\\s*:\\s*\"([^\"]+)\"");
        Matcher formationMatcher = formationPattern.matcher(json);
        if (formationMatcher.find()) {
            s.setFormation(formationMatcher.group(1));
        }

        // Extraire formateur (nom du formateur)
        Pattern formateurPattern = Pattern.compile("\"formateur\"\\s*:\\s*\"([^\"]+)\"");
        Matcher formateurMatcher = formateurPattern.matcher(json);
        if (formateurMatcher.find()) {
            s.setFormateur(formateurMatcher.group(1));
        }

        // Extraire date
        Pattern datePattern = Pattern.compile("\"date\"\\s*:\\s*\"([^\"]+)\"");
        Matcher dateMatcher = datePattern.matcher(json);
        if (dateMatcher.find()) {
            try {
                s.setDate(LocalDate.parse(dateMatcher.group(1), DateTimeFormatter.ISO_LOCAL_DATE));
            } catch (Exception e) {
                // Si format different, essayer autre chose
                s.setDate(LocalDate.now());
            }
        }

        // Extraire salle
        Pattern sallePattern = Pattern.compile("\"salle\"\\s*:\\s*\"([^\"]+)\"");
        Matcher salleMatcher = sallePattern.matcher(json);
        if (salleMatcher.find()) {
            s.setSalle(salleMatcher.group(1));
        }

        return s;
    }
}
