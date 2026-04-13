package app.controller;

import app.model.Formateur;
import app.service.FormateurService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AjoutFormateurController {
    @FXML
    private TextField fieldPrenom;

    @FXML
    private TextField fieldNom;

    @FXML
    private TextField fieldEmail;

    @FXML
    private PasswordField fieldMotDePasse;

    @FXML
    private Label messageLabel;

    private final FormateurService formateurService = new FormateurService();

    @FXML
    private void ajouterFormateur() {
        String prenom = fieldPrenom.getText() == null ? "" : fieldPrenom.getText().trim();
        String nom = fieldNom.getText() == null ? "" : fieldNom.getText().trim();
        String email = fieldEmail.getText() == null ? "" : fieldEmail.getText().trim();
        String motDePasse = fieldMotDePasse.getText() == null ? "" : fieldMotDePasse.getText().trim();

        if (prenom.isEmpty() || nom.isEmpty() || email.isEmpty() || motDePasse.isEmpty()) {
            messageLabel.setText("Prenom, nom, email et mot de passe sont obligatoires.");
            return;
        }

        Formateur formateur = new Formateur();
        formateur.setPrenom(prenom);
        formateur.setNom(nom);
        formateur.setEmail(email);
        // Envoie les 2 noms de champ pour rester compatible avec le backend.
        formateur.setMot_de_passe(motDePasse);
        formateur.setPassword(motDePasse);
        formateur.setRole("formateur");

        try {
            Formateur saved = formateurService.ajouterFormateur(formateur);
            String id = saved.getId_formateur() > 0 ? String.valueOf(saved.getId_formateur()) : "genere";
            messageLabel.setText("Formateur ajoute avec succes (id: " + id + ").");
            clearForm();
            Stage stage = (Stage) fieldEmail.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.out.println("Erreur ajout formateur:");
            e.printStackTrace();
            messageLabel.setText("Echec de l'ajout: " + e.getMessage());
        }
    }

    private void clearForm() {
        fieldPrenom.clear();
        fieldNom.clear();
        fieldEmail.clear();
        fieldMotDePasse.clear();
    }
}
