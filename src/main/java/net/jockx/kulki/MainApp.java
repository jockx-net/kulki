package net.jockx.kulki;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.jockx.kulki.controller.GameController;
import net.jockx.kulki.controller.PropertiesReader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MainApp extends Application {

    private static final Logger log = LogManager.getLogger(MainApp.class);

    public static void main(String[] args) {
		launch(args);
    }

    public void start(Stage stage) throws Exception {

        String fxmlFile = "/fxml/kulki.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = loader.load(getClass().getResourceAsStream(fxmlFile));

        log.debug("Showing JFX scene");

        double width = Double.parseDouble(PropertiesReader.getProperty("scene.width"));
        double height = Double.parseDouble(PropertiesReader.getProperty("scene.height"));
        Scene scene = new Scene(rootNode, width, height);

        scene.widthProperty().addListener((obs, oldVal, newVal) ->
                PropertiesReader.setProperty("scene.width", Double.toString(newVal.doubleValue())));
        scene.heightProperty().addListener((obs, oldVal, newVal) ->
                PropertiesReader.setProperty("scene.height", Double.toString(newVal.doubleValue())));

        scene.getStylesheets().add("/styles/styles.css");

        stage.setTitle("Kulki");
        stage.setScene(scene);
        stage.show();
		GameController.getInstance().showSettingsDialog();
	}
}
