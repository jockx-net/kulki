package net.jockx.kulki.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.jockx.kulki.controller.PropertiesReader;
import net.jockx.kulki.model.BallColor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SettingsPanel {
    private static final int BOARD_MIN = 3;
    private static final int BOARD_MAX = 20;
    private static final int MATCH_MIN = 2;
    private static final int MATCH_MAX = 20;
    private static final int NEW_BALLS_MIN = 1;
    private static final int NEW_BALLS_MAX = 20;

    private static final double DESIGN_WIDTH = 350;
    private static final double DESIGN_HEIGHT = 500;
    private static final double PANEL_HEIGHT_RATIO = 0.8;
    private static final double LABEL_WIDTH = 130;
    private static final double SPINNER_WIDTH = 70;

    private final Pane parent;
    private final Runnable onStart;
    private final Pane overlay;
    private final VBox panel;
    private final Spinner<Integer> boardSizeSpinner;
    private final Spinner<Integer> colorsSpinner;
    private final Spinner<Integer> matchSpinner;
    private final Spinner<Integer> newBallsSpinner;
    private final Label matchWarning;
    private final Label newBallsWarning;

    public SettingsPanel(Pane parent, Runnable onStart) {
        this.parent = parent;
        this.onStart = onStart;

        int maxColors = BallColor.values().length;

        boardSizeSpinner = spinner(BOARD_MIN, BOARD_MAX, loadInt("board.size"));
        colorsSpinner = spinner(1, maxColors, loadInt("numberOfColors"));
        matchSpinner = spinner(MATCH_MIN, MATCH_MAX, loadInt("minimalMatch"));
        newBallsSpinner = spinner(NEW_BALLS_MIN, NEW_BALLS_MAX, loadInt("newBallCount"));

        matchWarning = new Label();
        matchWarning.getStyleClass().add("settings-warning");
        newBallsWarning = new Label();
        newBallsWarning.getStyleClass().add("settings-warning");

        boardSizeSpinner.valueProperty().addListener((_, _, _) -> validate());
        matchSpinner.valueProperty().addListener((_, _, _) -> validate());
        newBallsSpinner.valueProperty().addListener((_, _, _) -> validate());
        validate();

        overlay = new Pane();
        overlay.getStyleClass().add("dialog-overlay");

        panel = new VBox();
        panel.getStyleClass().addAll("dialog-panel", "settings-panel");
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setMinSize(DESIGN_WIDTH, DESIGN_HEIGHT);
        panel.setPrefSize(DESIGN_WIDTH, DESIGN_HEIGHT);
        panel.setMaxSize(DESIGN_WIDTH, DESIGN_HEIGHT);

        Label title = new Label("Game Settings");
        title.getStyleClass().add("settings-title");

        Button startButton = new Button("Start");
        startButton.getStyleClass().addAll("settings-button", "button-start");
        startButton.setDefaultButton(true);
        startButton.setOnAction(_ -> onStartClick());

        Button defaultsButton = new Button("Defaults");
        defaultsButton.getStyleClass().addAll("settings-button", "button-defaults");
        defaultsButton.setOnAction(_ -> onDefaultsClick());

        HBox buttons = new HBox(8);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(startButton, defaultsButton);

        panel.getChildren().addAll(
                title,
                row("Board size", boardSizeSpinner),
                row("Colors (" + maxColors + " max)", colorsSpinner),
                row("Match size", matchSpinner, matchWarning),
                row("New Balls", newBallsSpinner, newBallsWarning),
                buttons
        );
        overlay.getChildren().add(panel);

        parent.widthProperty().addListener((_, _, _) -> reposition());
        parent.heightProperty().addListener((_, _, _) -> reposition());
    }

    public void show() {
        parent.getChildren().add(overlay);
        Platform.runLater(this::reposition);
    }

    public void hide() {
        parent.getChildren().remove(overlay);
    }

    private void reposition() {
        double pw = parent.getWidth();
        double ph = parent.getHeight();
        if (pw <= 0 || ph <= 0) return;

        overlay.setLayoutX(0);
        overlay.setLayoutY(0);
        overlay.setPrefSize(pw, ph);
        overlay.setMinSize(pw, ph);
        overlay.setMaxSize(pw, ph);

        double scale = ph * PANEL_HEIGHT_RATIO / DESIGN_HEIGHT;
        if (DESIGN_WIDTH * scale > pw) {
            scale = pw / DESIGN_WIDTH;
        }

        panel.setScaleX(scale);
        panel.setScaleY(scale);
        panel.setLayoutX((pw - DESIGN_WIDTH) / 2);
        panel.setLayoutY((ph - DESIGN_HEIGHT) / 2);
    }

    private void validate() {
        int bs = boardSizeSpinner.getValue();
        int ms = matchSpinner.getValue();
        int nb = newBallsSpinner.getValue();

        if (ms > bs) {
            matchWarning.setText("Match size exceeds board dimensions");
        } else {
            matchWarning.setText("");
        }

        if (nb > bs * bs) {
            newBallsWarning.setText("New balls should not exceed board capacity");
        } else {
            newBallsWarning.setText("");
        }
    }

    private void onStartClick() {
        save("board.size", boardSizeSpinner.getValue());
        save("numberOfColors", colorsSpinner.getValue());
        save("minimalMatch", matchSpinner.getValue());
        save("newBallCount", newBallsSpinner.getValue());

        hide();
        onStart.run();
    }

    private void onDefaultsClick() {
        boardSizeSpinner.getValueFactory().setValue(loadDefaultInt("board.size"));
        colorsSpinner.getValueFactory().setValue(loadDefaultInt("numberOfColors"));
        matchSpinner.getValueFactory().setValue(loadDefaultInt("minimalMatch"));
        newBallsSpinner.getValueFactory().setValue(loadDefaultInt("newBallCount"));
    }

    private static int loadDefaultInt(String key) {
        Properties defaults = new Properties();
        try (InputStream is = SettingsPanel.class.getClassLoader().getResourceAsStream("size.properties")) {
            if (is != null) {
                defaults.load(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(defaults.getProperty(key));
    }

    private static Spinner<Integer> spinner(int min, int max, int value) {
        Spinner<Integer> s = new Spinner<>(min, max, value);
        s.getStyleClass().add("settings-spinner");
        s.setMaxWidth(SPINNER_WIDTH);
        s.setPrefWidth(SPINNER_WIDTH);
        s.setMinWidth(SPINNER_WIDTH);
        s.setEditable(true);
        s.setOnScroll(e -> {
            if (e.getDeltaY() > 0) {
                s.increment();
            } else if (e.getDeltaY() < 0) {
                s.decrement();
            }
            e.consume();
        });
        return s;
    }

    private static VBox row(String labelText, Spinner<Integer> spinner) {
        HBox hb = new HBox(8);
        hb.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(labelText);
        l.getStyleClass().add("settings-field-label");
        l.setMinWidth(LABEL_WIDTH);
        hb.getChildren().addAll(l, spinner);
        VBox vb = new VBox(2);
        Label spacer = new Label();
        spacer.setMinHeight(14);
        vb.getChildren().addAll(hb, spacer);
        return vb;
    }

    private static VBox row(String labelText, Spinner<Integer> spinner, Label warning) {
        VBox vb = new VBox(2);
        HBox hb = new HBox(8);
        hb.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(labelText);
        l.getStyleClass().add("settings-field-label");
        l.setMinWidth(LABEL_WIDTH);
        hb.getChildren().addAll(l, spinner);
        vb.getChildren().addAll(hb, warning);
        return vb;
    }

    private static void save(String key, int value) {
        PropertiesReader.setProperty(key, String.valueOf(value));
    }

    private static int loadInt(String key) {
        return Integer.parseInt(PropertiesReader.getProperty(key));
    }
}
