package app.controller;

import java.util.List;

import app.model.Avis;
import app.model.Presence;
import app.model.Session;
import app.service.AvisService;
import app.service.AuthSession;
import app.service.PresenceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class SessionDetailController extends NavigationController {

    @FXML
    private Label sessionTitleLabel;

    @FXML
    private Label formationLabel;

    @FXML
    private Label formateurLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label salleLabel;

    @FXML
    private ListView<String> presencesListView;

    @FXML
    private Label tauxPresenceLabel;

    @FXML
    private ListView<String> avisListView;

    @FXML
    private Label moyenneAvisLabel;

    @FXML
    private Label navUserLabel;

    private Session session; // La session selectionnee
    private PresenceService presenceService = new PresenceService();
    private AvisService avisService = new AvisService();

    // Methode pour definir la session (appelee depuis SessionsController)
    public void setSession(Session session) {
        this.session = session;
        afficherDetails();
    }

    @FXML
    public void initialize() {
        navUserLabel.setText(AuthSession.getEmail() != null ? AuthSession.getEmail() : "visiteur@local");
    }

    private void afficherDetails() {
        if (session == null) return;

        sessionTitleLabel.setText("Session: " + session.getFormation());
        formationLabel.setText("Formation: " + session.getFormation());
        formateurLabel.setText("Formateur: " + session.getFormateur());
        dateLabel.setText("Date: " + session.getDate());
        salleLabel.setText("Salle: " + session.getSalle());

        chargerPresences();
        chargerAvis();
    }

    private void chargerPresences() {
        try {
            List<Presence> presences = presenceService.getPresencesBySession(session.getId());
            ObservableList<String> presencesDisplay = FXCollections.observableArrayList();

            int total = presences.size();
            int presents = 0;

            for (Presence p : presences) {
                String status = p.isPresent() ? "Présent" : "Absent";
                presencesDisplay.add("Apprenant " + p.getId_apprenant() + ": " + status);
                if (p.isPresent()) presents++;
            }

            presencesListView.setItems(presencesDisplay);

            double taux = total > 0 ? (double) presents / total * 100 : 0;
            // Affichage sans décimale (arrondi à l'entier) pour une meilleure lisibilité
            tauxPresenceLabel.setText(String.format("%.0f%%", taux));

        } catch (Exception e) {
            System.out.println("Erreur chargement presences: " + e.getMessage());
            // Donnees mockees
            presencesListView.setItems(FXCollections.observableArrayList(
                "Apprenant 1: Présent",
                "Apprenant 2: Absent",
                "Apprenant 3: Présent"
            ));
            tauxPresenceLabel.setText("67%");
        }
    }

    private void chargerAvis() {
        try {
            List<Avis> avis = avisService.getAvisBySession(session.getId());
            ObservableList<String> avisDisplay = FXCollections.observableArrayList();

            double sommeNotes = 0;
            for (Avis a : avis) {
                avisDisplay.add("Note: " + a.getNote() + "/5 - " + a.getCommentaire());
                sommeNotes += a.getNote();
            }

            avisListView.setItems(avisDisplay);

            double moyenne = avis.size() > 0 ? sommeNotes / avis.size() : 0;
            moyenneAvisLabel.setText(String.format("%.1f/5", moyenne));

        } catch (Exception e) {
            System.out.println("Erreur chargement avis: " + e.getMessage());
            // Donnees mockees
            avisListView.setItems(FXCollections.observableArrayList(
                "Note: 4/5 - Très bonne formation",
                "Note: 5/5 - Excellent formateur"
            ));
            moyenneAvisLabel.setText("4.5/5");
        }
    }

    @FXML
    private void retourSessions() {
        ouvrirSessions(null);
    }
}
