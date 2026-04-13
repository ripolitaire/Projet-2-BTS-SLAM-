package app.controller;

import app.Main;
import app.service.AuthSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationController {

    @FXML
    protected void ouvrirIndex(ActionEvent event) {
        changerVue(event, "index.fxml", "Tableau de bord", 1280, 820);
    }

    @FXML
    protected void ouvrirFormations(ActionEvent event) {
        changerVue(event, "formations.fxml", "Liste des Formations", 1280, 820);
    }

    @FXML
    protected void ouvrirSessions(ActionEvent event) {
        changerVue(event, "sessions.fxml", "Liste des Sessions", 1280, 820);
    }

    @FXML
    protected void ouvrirFormateurs(ActionEvent event) {
        changerVue(event, "formateurs.fxml", "Liste des Formateurs", 1280, 820);
    }

    @FXML
    protected void ouvrirApprenants(ActionEvent event) {
        changerVue(event, "apprenants.fxml", "Liste des Apprenants", 1280, 820);
    }

    @FXML
    protected void ouvrirInscriptions(ActionEvent event) {
        changerVue(event, "inscriptions.fxml", "Liste des Inscriptions", 1280, 820);
    }

    @FXML
    protected void ouvrirPresences(ActionEvent event) {
        changerVue(event, "presences.fxml", "Liste des Presences", 1280, 820);
    }

    @FXML
    protected void ouvrirAvis(ActionEvent event) {
        changerVue(event, "avis.fxml", "Liste des Avis", 1280, 820);
    }

    @FXML
    protected void deconnecter(ActionEvent event) {
        AuthSession.clear();
        changerVue(event, "login.fxml", "Connexion Admin", 1180, 760);
    }

    @FXML
    protected void ouvrirFormulaireApprenant() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/app/view/ajout-apprenant.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Ajouter un apprenant");
            Scene scene = new Scene(root, 560, 620);
            Main.applyTheme(scene);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Erreur ouverture formulaire apprenant :");
            e.printStackTrace();
        }
    }

    protected void changerVue(ActionEvent event, String fichierFxml, String titre, int largeur, int hauteur) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/app/view/" + fichierFxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, largeur, hauteur);
            Main.applyTheme(scene);
            stage.setTitle(titre);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Erreur navigation vers " + fichierFxml + " :");
            e.printStackTrace();
        }
    }
}
