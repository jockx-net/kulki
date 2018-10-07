package net.jockx.kulki;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.jockx.kulki.controller.GameController;
import net.jockx.kulki.controller.PropertiesReader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MainApp extends Application {

    private static final Logger log = LogManager.getLogger(MainApp.class);

    public static void main(String[] args) throws Exception {
		launch(args);
    }

    public void start(Stage stage) throws Exception {

        String fxmlFile = "/fxml/kulki.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = loader.load(getClass().getResourceAsStream(fxmlFile));

        log.debug("Showing JFX scene");

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        PropertiesReader.setProperty("scene.width", Double.toString(primaryScreenBounds.getMaxX()));
        PropertiesReader.setProperty("scene.height", Double.toString(primaryScreenBounds.getMaxY()));
        Scene scene = new Scene(rootNode, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
        scene.getStylesheets().add("/styles/styles.css");

        stage.setTitle("Kulki");
        stage.setScene(scene);
        stage.show();
		GameController.getInstance().showSettingsDialog();
	}
}
