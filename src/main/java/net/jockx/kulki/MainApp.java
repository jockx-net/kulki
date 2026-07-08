package net.jockx.kulki;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import net.jockx.kulki.controller.GameController;
import net.jockx.kulki.controller.PropertiesReader;
import net.jockx.kulki.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

public class MainApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    public void start(Stage stage) {

        loadFont("/fonts/UnboundedSans-Regular.ttf");
        log.debug("Showing JFX scene");

        applySavedLocale();

        GameController controller = new GameController();

        double width = Double.parseDouble(PropertiesReader.getProperty("scene.width"));
        double height = Double.parseDouble(PropertiesReader.getProperty("scene.height"));
        Scene scene = new Scene(controller.getRootPane(), width, height);

        scene.widthProperty().addListener((_, _, newVal) ->
                PropertiesReader.setProperty("scene.width", Double.toString(newVal.doubleValue())));
        scene.heightProperty().addListener((_, _, newVal) ->
                PropertiesReader.setProperty("scene.height", Double.toString(newVal.doubleValue())));

        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        stage.setTitle(Messages.get("title.kulki"));
        stage.setScene(scene);
        stage.setMinWidth(390);
        stage.setMinHeight(550);
        stage.show();

        String playerName = PropertiesReader.getProperty("player.name");
        if (playerName == null || playerName.isBlank()) {
            controller.showPlayerNamePrompt(controller::showGameMenu);
        } else {
            controller.showGameMenu();
        }
    }

    private static void loadFont(String path) {
        try (var is = MainApp.class.getResourceAsStream(path)) {
            if (is == null) {
                log.warn("Font resource not found: {}", path);
                return;
            }
            var tempFile = File.createTempFile("kulki-", ".ttf");
            tempFile.deleteOnExit();
            Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            var loaded = Font.loadFont(tempFile.toURI().toURL().toExternalForm(), 14);
            if (loaded == null) {
                log.warn("Failed to load font: {}", path);
            }
        } catch (IOException e) {
            log.warn("Failed to load font {}: {}", path, e.getMessage());
        }
    }

    private static void applySavedLocale() {
        String localeStr = PropertiesReader.getProperty("locale");
        if (localeStr != null && !localeStr.isBlank()) {
            String[] parts = localeStr.split("_");
            Locale locale = parts.length == 2
                ? Locale.of(parts[0], parts[1])
                : Locale.of(parts[0]);
            Messages.setLocale(locale);
        }
    }
}
