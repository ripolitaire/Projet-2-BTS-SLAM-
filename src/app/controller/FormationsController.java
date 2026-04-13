package app.controller;

import java.util.List;

import app.model.Formation;
import app.service.FormationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FormationsController extends NavigationController {

    @FXML
    private TableView<Formation> formationsTable;

    @FXML
    private TableColumn<Formation, String> nomColumn;

    @FXML
    private TableColumn<Formation, String> descriptionColumn;

    @FXML
    private TableColumn<Formation, String> dureeColumn;

    @FXML
    private TableColumn<Formation, String> niveauColumn;

    @FXML
    private TextField searchField;

    private FormationService formationService = new FormationService();
    private ObservableList<Formation> formationsList = FXCollections.observableArrayList();
    private FilteredList<Formation> filteredFormations;

    @FXML
    public void initialize() {
        // Configurer les colonnes
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dureeColumn.setCellValueFactory(new PropertyValueFactory<>("duree"));
        niveauColumn.setCellValueFactory(new PropertyValueFactory<>("niveau"));

        // Charger les formations
        chargerFormations();

        // Configurer la recherche
        filteredFormations = new FilteredList<>(formationsList, p -> true);
        formationsTable.setItems(filteredFormations);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredFormations.setPredicate(formation -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return formation.getNom().toLowerCase().contains(lowerCaseFilter) ||
                       formation.getDescription().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void chargerFormations() {
        try {
            List<Formation> formations = formationService.getAllFormations();
            formationsList.clear();
            formationsList.addAll(formations);
        } catch (Exception e) {
            System.out.println("Erreur chargement formations: " + e.getMessage());
            // Pour l'instant, on peut ajouter des données mockées en fallback
            formationsList.add(new Formation(1, "JavaFX Basics", "Apprendre les bases de JavaFX", "2 jours", "debutant"));
            formationsList.add(new Formation(2, "Spring Boot", "Developpement web avec Spring", "3 jours", "intermediaire"));
        }
    }
}