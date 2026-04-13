package app.controller;

import java.util.List;

import app.model.Avis;
import app.model.Presence;
import app.model.Session;
import app.service.AuthSession;
import app.service.AvisService;
import app.service.FormationService;
import app.service.PresenceService;
import app.service.SessionService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

public class IndexController extends NavigationController {

    @FXML
    private Label navUserLabel;

    @FXML
    private Label sessionCountLabel;

    @FXML
    private Label currentProfileLabel;

    @FXML
    private Label currentProfileHintLabel;

    @FXML
    private Label formationsCountLabel;

    @FXML
    private Label tauxPresenceLabel;

    @FXML
    private Label moyenneAvisLabel;

    @FXML
    private PieChart sessionsDonutChart;

    @FXML
    private Label sessionsDonutValueLabel;

    @FXML
    private PieChart presenceDonutChart;

    @FXML
    private Label presenceDonutValueLabel;

    @FXML
    private PieChart avisDonutChart;

    @FXML
    private Label avisDonutValueLabel;

    private FormationService formationService = new FormationService();
    private SessionService sessionService = new SessionService();

    @FXML
    public void initialize() {
        String email = normalize(AuthSession.getEmail());
        String role = normalize(AuthSession.getRole());

        initialiserCharts();

        if (email == null && role == null) {
            navUserLabel.setText("visiteur@local");
            currentProfileLabel.setText("Visiteur");
            currentProfileHintLabel.setText("Profil courant");
        } else {
            String displayRole = role == null ? "Utilisateur" : capitalize(role);
            String displayEmail = email == null ? "email indisponible" : email;

            navUserLabel.setText(displayEmail);
            currentProfileLabel.setText(displayRole);
            currentProfileHintLabel.setText(displayEmail);
        }

        // Charger les statistiques
        chargerStatistiques();
    }

    private void chargerStatistiques() {
        try {
            // Nombre de formations
            int nbFormations = formationService.getAllFormations().size();
            formationsCountLabel.setText(String.valueOf(nbFormations));

            // Nombre de sessions
            List<Session> sessions = sessionService.getToutesSessions();
            int nbSessions = sessions.size();
            sessionCountLabel.setText(String.valueOf(nbSessions));

            // Taux de présence moyen
            double tauxPresence = calculerTauxPresenceMoyen();
            // Affichage sans décimale (arrondi à l'entier) pour une meilleure lisibilité
            tauxPresenceLabel.setText(String.format("%.0f%%", tauxPresence));

            // Moyenne des avis
            double moyenneAvis = calculerMoyenneAvis();
            moyenneAvisLabel.setText(String.format("%.1f/5", moyenneAvis));

            long sessionsPassees = sessions.stream().filter(Session::estPasse).count();
            long sessionsActives = nbSessions - sessionsPassees;
            if (sessionsDonutValueLabel != null) {
                sessionsDonutValueLabel.setText(String.valueOf(sessionsActives));
            }
            updateDonut(sessionsDonutChart, sessionsActives, sessionsPassees, "Actives", "Passees");

            if (presenceDonutValueLabel != null) {
                presenceDonutValueLabel.setText(String.format("%.0f%%", tauxPresence));
            }
            updateDonut(presenceDonutChart, tauxPresence, Math.max(0.0, 100.0 - tauxPresence), "Present", "Absent");

            if (avisDonutValueLabel != null) {
                avisDonutValueLabel.setText(String.format("%.1f/5", moyenneAvis));
            }
            updateDonut(avisDonutChart, moyenneAvis, Math.max(0.0, 5.0 - moyenneAvis), "Note", "Reste");

        } catch (Exception e) {
            System.out.println("Erreur chargement stats: " + e.getMessage());
            // Valeurs par défaut
            formationsCountLabel.setText("0");
            sessionCountLabel.setText("0");
            tauxPresenceLabel.setText("0%");
            moyenneAvisLabel.setText("0/5");

            if (sessionsDonutValueLabel != null) {
                sessionsDonutValueLabel.setText("0");
            }
            if (presenceDonutValueLabel != null) {
                presenceDonutValueLabel.setText("0%");
            }
            if (avisDonutValueLabel != null) {
                avisDonutValueLabel.setText("0/5");
            }

            setNoData(sessionsDonutChart, "Aucune");
            setNoData(presenceDonutChart, "Aucune");
            setNoData(avisDonutChart, "Aucune");
        }
    }

    private double calculerTauxPresenceMoyen() throws Exception {
        PresenceService presenceService = new PresenceService();
        List<Presence> allPresences = presenceService.getAllPresences();
        if (allPresences.isEmpty()) return 0.0;

        long presents = allPresences.stream().filter(Presence::isPresent).count();
        return (double) presents / allPresences.size() * 100;
    }

    private double calculerMoyenneAvis() throws Exception {
        AvisService avisService = new AvisService();
        List<Avis> allAvis = avisService.getAllAvis();
        if (allAvis.isEmpty()) return 0.0;

        double somme = allAvis.stream().mapToInt(Avis::getNote).sum();
        return somme / allAvis.size();
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String clean = value.trim();
        return clean.isEmpty() ? null : clean;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private void initialiserCharts() {
        configureDonut(sessionsDonutChart);
        configureDonut(presenceDonutChart);
        configureDonut(avisDonutChart);

        setNoData(sessionsDonutChart, "Aucune");
        setNoData(presenceDonutChart, "Aucune");
        setNoData(avisDonutChart, "Aucune");
    }

    private void configureDonut(PieChart chart) {
        if (chart == null) {
            return;
        }
        chart.setLegendVisible(false);
        chart.setLabelsVisible(false);
        chart.setStartAngle(90);
        chart.setClockwise(true);
    }

    private void updateDonut(PieChart chart, double value, double remainder, String valueLabel, String remainderLabel) {
        if (chart == null) {
            return;
        }

        double v = Math.max(0.0, value);
        double r = Math.max(0.0, remainder);
        if (v <= 0.0 && r <= 0.0) {
            setNoData(chart, "Aucune");
            return;
        }

        var filled = new PieChart.Data(valueLabel, v <= 0.0 ? 0.0001 : v);
        var empty = new PieChart.Data(remainderLabel, r <= 0.0 ? 0.0001 : r);
        chart.getStyleClass().remove("donut-empty");
        chart.setData(FXCollections.observableArrayList(filled, empty));
    }

    private void setNoData(PieChart chart, String label) {
        if (chart == null) {
            return;
        }
        if (!chart.getStyleClass().contains("donut-empty")) {
            chart.getStyleClass().add("donut-empty");
        }
        chart.setData(FXCollections.observableArrayList(new PieChart.Data(label, 1)));
    }
}
