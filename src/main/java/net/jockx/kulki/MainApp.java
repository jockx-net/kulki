package net.jockx.kulki;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import net.jockx.kulki.controller.GameController;
import net.jockx.kulki.controller.PropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    public void start(Stage stage) {

        Font.loadFont(getClass().getResourceAsStream("/fonts/FredokaOne-Regular.ttf"), 14);
        log.debug("Showing JFX scene");

        GameController controller = new GameController();

        double width = Double.parseDouble(PropertiesReader.getProperty("scene.width"));
        double height = Double.parseDouble(PropertiesReader.getProperty("scene.height"));
        Scene scene = new Scene(controller.getRootPane(), width, height);

        scene.widthProperty().addListener((obs, oldVal, newVal) ->
                PropertiesReader.setProperty("scene.width", Double.toString(newVal.doubleValue())));
        scene.heightProperty().addListener((obs, oldVal, newVal) ->
                PropertiesReader.setProperty("scene.height", Double.toString(newVal.doubleValue())));

        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        stage.setTitle("Kulki");
        stage.setScene(scene);
        stage.setMinWidth(390);
        stage.setMinHeight(550);
        stage.show();
        controller.showSettingsDialog();
    }
}
