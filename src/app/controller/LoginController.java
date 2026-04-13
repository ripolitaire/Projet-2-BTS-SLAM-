package app.controller;

import app.Main;
import app.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField fieldEmail;

    @FXML
    private PasswordField fieldMotDePasse;

    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void initialize() {
        if (messageLabel != null && (messageLabel.getText() == null || messageLabel.getText().isBlank())) {
            messageLabel.setText("Comptes demo: admin@local/admin ou user@local/user");
        }
    }

    @FXML
    private void connecter() {
        String email = fieldEmail.getText() == null ? "" : fieldEmail.getText().trim();
        String motDePasse = fieldMotDePasse.getText() == null ? "" : fieldMotDePasse.getText().trim();

        if (email.isEmpty() || motDePasse.isEmpty()) {
            messageLabel.setText("Saisis ton email et ton mot de passe.");
            return;
        }

        try {
            authService.login(email, motDePasse);
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/app/view/index.fxml"));
                Stage stage = (Stage) fieldEmail.getScene().getWindow();
                stage.setTitle("Tableau de bord");
                Scene scene = new Scene(root, 1280, 820);
                Main.applyTheme(scene);
                stage.setScene(scene);
                stage.show();
            } catch (Exception uiError) {
                System.out.println("Erreur chargement interface apres connexion:");
                uiError.printStackTrace();
                messageLabel.setText("Connexion reussie, mais impossible d'afficher le tableau de bord.");
            }
        } catch (Exception e) {
            System.out.println("Erreur login:");
            e.printStackTrace();
            messageLabel.setText("Connexion impossible: " + e.getMessage());
        }
    }

    @FXML
    private void ouvrirRegister(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/app/view/register.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1180, 760);
            Main.applyTheme(scene);
            stage.setTitle("Inscription");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            messageLabel.setText("Impossible d'ouvrir l'ecran d'inscription.");
            e.printStackTrace();
        }
    }
}
