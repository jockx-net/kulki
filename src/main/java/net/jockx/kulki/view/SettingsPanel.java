package net.jockx.kulki.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.jockx.kulki.controller.PropertiesReader;
import net.jockx.kulki.model.BallColor;

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


    private final Pane parent;
    private final Runnable onStart;
    private final Pane overlay;
    private final VBox panel;
    private final Spinner<Integer> columnsSpinner;
    private final Spinner<Integer> rowsSpinner;
    private final Spinner<Integer> colorsSpinner;
    private final Spinner<Integer> matchSpinner;
    private final Spinner<Integer> newBallsSpinner;
    private final Label matchWarning;
    private final Label newBallsWarning;

    public SettingsPanel(Pane parent, Runnable onStart) {
        this.parent = parent;
        this.onStart = onStart;

        int maxColors = BallColor.values().length;

        columnsSpinner = new Spinner<>(BOARD_MIN, BOARD_MAX, loadInt("board.width"));
        rowsSpinner = new Spinner<>(BOARD_MIN, BOARD_MAX, loadInt("board.height"));
        colorsSpinner = new Spinner<>(1, maxColors, loadInt("numberOfColors"));
        matchSpinner = new Spinner<>(MATCH_MIN, MATCH_MAX, loadInt("minimalMatch"));
        newBallsSpinner = new Spinner<>(NEW_BALLS_MIN, NEW_BALLS_MAX, loadInt("newBallCount"));

        matchWarning = new Label();
        matchWarning.getStyleClass().add("settings-warning");
        newBallsWarning = new Label();
        newBallsWarning.getStyleClass().add("settings-warning");

        columnsSpinner.valueProperty().addListener((_, _, _) -> validate());
        rowsSpinner.valueProperty().addListener((_, _, _) -> validate());
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

        Button button = new Button("Start");
        button.getStyleClass().add("settings-button");
        button.setDefaultButton(true);
        button.setOnAction(_ -> onStartClick());

        panel.getChildren().addAll(
                title,
                label("Board X"), columnsSpinner,
                label("Board Y"), rowsSpinner,
                label("Colors (" + maxColors + " max)"), colorsSpinner,
                label("Match size"), matchSpinner, matchWarning,
                label("New Balls"), newBallsSpinner, newBallsWarning,
                button
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
        int bx = columnsSpinner.getValue();
        int by = rowsSpinner.getValue();
        int ms = matchSpinner.getValue();
        int nb = newBallsSpinner.getValue();

        if (ms > bx || ms > by) {
            matchWarning.setText("Match size exceeds board dimensions");
        } else {
            matchWarning.setText("");
        }

        if (nb > bx * by) {
            newBallsWarning.setText("New balls should not exceed board capacity");
        } else {
            newBallsWarning.setText("");
        }
    }

    private void onStartClick() {
        save("board.width", columnsSpinner.getValue());
        save("board.height", rowsSpinner.getValue());
        save("numberOfColors", colorsSpinner.getValue());
        save("minimalMatch", matchSpinner.getValue());
        save("newBallCount", newBallsSpinner.getValue());

        hide();
        onStart.run();
    }

    private static Label label(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("settings-field-label");
        return l;
    }

    private static void save(String key, int value) {
        PropertiesReader.setProperty(key, String.valueOf(value));
    }

    private static int loadInt(String key) {
        return Integer.parseInt(PropertiesReader.getProperty(key));
    }
}
