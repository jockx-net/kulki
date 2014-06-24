package net.jockx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) throws Exception {
		launch(args);
    }

    public void start(Stage stage) throws Exception {

        String fxmlFile = "/fxml/kulki.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = loader.load(getClass().getResourceAsStream(fxmlFile));

        log.debug("Showing JFX scene");


        Scene scene = new Scene(rootNode);
        //scene.getStylesheets().add("/styles/styles.css");
		//stage.setResizable(false);
        stage.setTitle("FX Kulki");
        stage.setScene(scene);
        stage.show();

	}
}
