package app.controller;

import app.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AuthViewController {

    @FXML
    private void ouvrirAccueil(ActionEvent event) {
        changerVue(event, "accueil.fxml", "Accueil", 1280, 820);
    }

    @FXML
    private void ouvrirLogin(ActionEvent event) {
        changerVue(event, "login.fxml", "Connexion Admin", 1180, 760);
    }

    @FXML
    private void ouvrirRegister(ActionEvent event) {
        changerVue(event, "register.fxml", "Inscription", 1180, 760);
    }

    private void changerVue(ActionEvent event, String fichierFxml, String titre, int largeur, int hauteur) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/app/view/" + fichierFxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, largeur, hauteur);
            Main.applyTheme(scene);
            stage.setTitle(titre);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Erreur chargement de " + fichierFxml + " :");
            e.printStackTrace();
        }
    }
}
