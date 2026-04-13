package app.controller;

import app.model.Apprenant;
import app.service.ApprenantService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AjoutApprenantController {
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

    private final ApprenantService apprenantService = new ApprenantService();

    @FXML
    private void ajouterApprenant() {
        String prenom = fieldPrenom.getText() == null ? "" : fieldPrenom.getText().trim();
        String nom = fieldNom.getText() == null ? "" : fieldNom.getText().trim();
        String email = fieldEmail.getText() == null ? "" : fieldEmail.getText().trim();
        String motDePasse = fieldMotDePasse.getText() == null ? "" : fieldMotDePasse.getText().trim();
        if (prenom.isEmpty() || nom.isEmpty() || email.isEmpty() || motDePasse.isEmpty()) {
            messageLabel.setText("Prenom, nom, email et mot de passe sont obligatoires.");
            return;
        }

        Apprenant apprenant = new Apprenant();
        apprenant.setPrenom(prenom);
        apprenant.setNom(nom);
        apprenant.setEmail(email);
        apprenant.setMot_de_passe(motDePasse);
        apprenant.setRole("apprenant");

        try {
            Apprenant saved = apprenantService.ajouterApprenant(apprenant);
            String id = saved.getId_apprenant() > 0 ? String.valueOf(saved.getId_apprenant()) : "genere";
            messageLabel.setText("Apprenant ajoute avec succes (id: " + id + ").");
            clearForm();
        } catch (Exception e) {
            System.out.println("Erreur ajout apprenant:");
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
