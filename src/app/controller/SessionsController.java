package app.controller;

import app.Main;
import app.model.Session;
import app.service.AuthSession;
import app.service.SessionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SessionsController extends NavigationController {

    @FXML
    private TableView<Session> sessionTable;

    @FXML
    private TableColumn<Session, String> colFormation;

    @FXML
    private TableColumn<Session, String> colFormateur;

    @FXML
    private TableColumn<Session, String> colDate;

    @FXML
    private TableColumn<Session, String> colSalle;

    @FXML
    private TableColumn<Session, String> colEtat;

    @FXML
    private TableColumn<Session, Void> colDetail;

    @FXML
    private TableColumn<Session, Void> colAction;

    @FXML
    private Label messageLabel;

    private final SessionService sessionService = new SessionService();

    @FXML
    private void initialize() {
        configurerColonnes();
        sessionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        rafraichirSessions();
    }

    @FXML
    protected void rafraichirSessions() {
        String email = AuthSession.getEmail();
        try {
            var sessions = sessionService.getSessionsVorApprenant(email);
            ObservableList<Session> data = FXCollections.observableArrayList(sessions);
            sessionTable.setItems(data);
            messageLabel.setText(String.format("%d session(s) accessibles", sessions.size()));
        } catch (Exception e) {
            messageLabel.setText("Erreur chargement sessions : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurerColonnes() {
        colFormation.setCellValueFactory(new PropertyValueFactory<>("formation"));
        colFormateur.setCellValueFactory(new PropertyValueFactory<>("formateur"));
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDateFormattee()));
        colSalle.setCellValueFactory(new PropertyValueFactory<>("salle"));
        colEtat.setCellValueFactory(cell -> {
            Session session = cell.getValue();
            if (session.estPasse()) {
                return new SimpleStringProperty("Terminée");
            }
            return new SimpleStringProperty(session.isPresent() ? "Présent" : "Absent");
        });

        colAction.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Session, Void> call(final TableColumn<Session, Void> param) {
                final TableCell<Session, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Marquer présent");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Session data = getTableView().getItems().get(getIndex());
                            if (data.estPasse()) {
                                messageLabel.setText("Action impossible : session déjà passée");
                                return;
                            }
                            data.setPresent(true);
                            sessionTable.refresh();
                            messageLabel.setText("Présence enregistrée pour " + data.getFormation());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Session session = getTableView().getItems().get(getIndex());
                            btn.setDisable(session.estPasse() || session.isPresent());
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });

        colDetail.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Session, Void> call(final TableColumn<Session, Void> param) {
                final TableCell<Session, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Voir détail");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Session data = getTableView().getItems().get(getIndex());
                            ouvrirDetailSession(data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });
    }

    private void ouvrirDetailSession(Session session) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/view/sessions-detail.fxml"));
            Parent root = loader.load();

            SessionDetailController controller = loader.getController();
            controller.setSession(session);

            Stage stage = (Stage) sessionTable.getScene().getWindow();
            stage.setTitle("Détail Session - " + session.getFormation());
            Scene scene = new Scene(root, 1280, 820);
            Main.applyTheme(scene);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            messageLabel.setText("Erreur ouverture détail: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
