package app.controller;

import java.util.List;

import app.Main;
import app.model.Apprenant;
import app.service.ApprenantService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class ApprenantController extends NavigationController {

    @FXML
    private TableView<Apprenant> apprenantTable;

    @FXML
    private TableColumn<Apprenant, String> colPrenom;

    @FXML
    private TableColumn<Apprenant, String> colNom;

    @FXML
    private TableColumn<Apprenant, String> colEmail;

    @FXML
    private Label messageLabel;

    private final ApprenantService apprenantService = new ApprenantService();

    @FXML
    public void initialize() {
        configurerColonnes();
        apprenantTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        rafraichirApprenants();
    }

    @FXML
    protected void rafraichirApprenants() {
        try {
            List<Apprenant> liste = apprenantService.getAllApprenants();
            ObservableList<Apprenant> data = FXCollections.observableArrayList(liste);
            apprenantTable.setItems(data);
            if (liste.isEmpty()) {
                messageLabel.setText("Aucun apprenant recu depuis l'API.");
            } else {
                messageLabel.setText(liste.size() + " apprenant(s) charge(s) depuis l'API.");
            }
        } catch (Exception e) {
            messageLabel.setText("Erreur chargement API apprenants: " + e.getMessage());
            System.out.println("Erreur chargement apprenants:");
            e.printStackTrace();
        }
    }

    @FXML
    @Override
    protected void ouvrirFormulaireApprenant() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/app/view/ajout-apprenant.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Ajouter un apprenant");
            Scene scene = new Scene(root, 560, 620);
            Main.applyTheme(scene);
            stage.setScene(scene);
            stage.setOnHidden(event -> rafraichirApprenants());
            stage.show();
        } catch (Exception e) {
            System.out.println("Erreur ouverture formulaire apprenant :");
            e.printStackTrace();
        }
    }

    private void configurerColonnes() {
        colPrenom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDisplayPrenom()));
        colNom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDisplayNom()));
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().getEmail())));
    }

    private String safe(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        return value;
    }
}
