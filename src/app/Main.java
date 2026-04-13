package app;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {

    public static final String THEME_CSS = "/app/view/app-theme.css";

    public static void applyTheme(Scene scene) {
        if (scene == null) {
            return;
        }
        URL cssUrl = Main.class.getResource(THEME_CSS);
        if (cssUrl == null) {
            System.out.println("Theme CSS introuvable: " + THEME_CSS);
            return;
        }
        String css = cssUrl.toExternalForm();
        if (!scene.getStylesheets().contains(css)) {
            scene.getStylesheets().add(css);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/app/view/accueil.fxml"));
        
        stage.setTitle("Accueil");
        Scene scene = new Scene(root, 1280, 820);
        applyTheme(scene);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


