package net.jockx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.jockx.model.Ball;
import net.jockx.model.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class MainApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) throws Exception {
		Board board = new Board();
		board.placeBall(new Ball(Color.GREEN), 3,3);
		board.placeBall(new Ball(Color.GREEN), 2,3);
		board.placeBall(new Ball(Color.GREEN), 1,3);
		board.placeBall(new Ball(Color.GREEN), 1,4);
		board.placeBall(new Ball(Color.BLUE), 4,3);


		//launch(args);
    }

    public void start(Stage stage) throws Exception {

        log.info("Starting Hello JavaFX and Maven demonstration application");

        String fxmlFile = "/fxml/hello.fxml";
        log.debug("Loading FXML for main view from: {}", fxmlFile);
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));

        log.debug("Showing JFX scene");
        Scene scene = new Scene(rootNode, 400, 200);
        scene.getStylesheets().add("/styles/styles.css");

        stage.setTitle("Hello JavaFX and Maven");
        stage.setScene(scene);
        stage.show();
    }
}
