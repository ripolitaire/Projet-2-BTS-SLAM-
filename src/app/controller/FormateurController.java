package app.controller;

import java.util.List;

import app.Main;
import app.model.Formateur;
import app.service.FormateurService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class FormateurController extends NavigationController {
    @FXML
    private TableView<Formateur> ForTable;

    @FXML
    private TableColumn<Formateur, String> colPrenom;

    @FXML
    private TableColumn<Formateur, String> colNom;

    @FXML
    private TableColumn<Formateur, String> colEmail;

    private final FormateurService formateurService = new FormateurService();

    @FXML
    public void initialize() {
        configurerColonnes();
        ForTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        chargerFormateurs();
    }

    private void configurerColonnes() {
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void chargerFormateurs() {
        try {
            List<Formateur> liste = formateurService.getAllFormateurs();
            ObservableList<Formateur> data = FXCollections.observableArrayList(liste);
            ForTable.setItems(data);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement des formateurs :");
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirFormulaireFormateur() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/app/view/ajout-formateur.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Ajouter un formateur");
            Scene scene = new Scene(root, 560, 620);
            Main.applyTheme(scene);
            stage.setScene(scene);
            stage.setOnHidden(event -> chargerFormateurs());
            stage.show();
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ouverture du formulaire formateur :");
            e.printStackTrace();
        }
    }
}
