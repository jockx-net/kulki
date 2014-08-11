package net.jockx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.jockx.controller.GameController;
import net.jockx.controller.PropertiesReader;
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


		int width = PropertiesReader.getInt("scene.width");
		int height = PropertiesReader.getInt("scene.height");
        Scene scene = new Scene(rootNode, width, height);
        scene.getStylesheets().add("/styles/styles.css");
		//stage.setResizable(false);

        stage.setTitle("FX Kulki");
        stage.setScene(scene);
        stage.show();
		GameController.getInstance().showSettingsDialog();
	}
}
