package net.jockx.kulki.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.jockx.kulki.controller.GameController;
import net.jockx.kulki.controller.PropertiesReader;
import net.jockx.kulki.i18n.Messages;
import net.jockx.kulki.model.BallColor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.function.Consumer;

public class SettingsPanel extends OverlayPanel {
    private static final int BOARD_MIN = 3;
    private static final int BOARD_MAX = 20;
    private static final int MATCH_MIN = 2;
    private static final int MATCH_MAX = 20;
    private static final int NEW_BALLS_MIN = 1;
    private static final int NEW_BALLS_MAX = 20;

    private static final double DESIGN_WIDTH = 340;
    private static final double DESIGN_HEIGHT = 500;
    private static final double PANEL_HEIGHT_RATIO = 0.85;
    private static final double SPINNER_WIDTH = 70;

    private final Runnable onSave, onCancel;
    private final Spinner<Integer> boardSizeSpinner;
    private final Spinner<Integer> colorsSpinner;
    private final Spinner<Integer> matchSpinner;
    private final Spinner<Integer> newBallsSpinner;
    private final TextField playerNameField;
    private final Label matchWarning;
    private final Label newBallsWarning;
    private final int origBoardSize, origColors, origMatch, origNewBalls;
    private final String origPlayerName;
    private final Label title;
    private final Button saveButton, cancelButton, defaultsButton;
    private final Label[] rowLabels = new Label[5];

    public SettingsPanel(Pane parent, Runnable onSave, Runnable onCancel, Consumer<Locale> onLanguageChange) {
        super(parent, DESIGN_WIDTH, DESIGN_HEIGHT, PANEL_HEIGHT_RATIO);

        this.onSave = onSave;
        this.onCancel = onCancel;

        panel.getStyleClass().add("settings-panel");
        panel.setAlignment(Pos.TOP_CENTER);

        int maxColors = BallColor.values().length;

        origBoardSize = loadInt("board.size");
        origColors = loadInt("numberOfColors");
        origMatch = loadInt("minimalMatch");
        origNewBalls = loadInt("newBallCount");

        boardSizeSpinner = spinner(BOARD_MIN, BOARD_MAX, loadInt("board.size"));
        colorsSpinner = spinner(1, maxColors, loadInt("numberOfColors"));
        matchSpinner = spinner(MATCH_MIN, MATCH_MAX, loadInt("minimalMatch"));
        newBallsSpinner = spinner(NEW_BALLS_MIN, NEW_BALLS_MAX, loadInt("newBallCount"));

        origPlayerName = GameController.getPlayerName();
        playerNameField = new TextField();
        playerNameField.setText(origPlayerName);
        playerNameField.setAlignment(Pos.CENTER);
        playerNameField.getStyleClass().add("player-name-field");
        playerNameField.setPromptText(Messages.get("playerName.prompt"));
        playerNameField.setMaxWidth(140);
        playerNameField.setTextFormatter(new TextFormatter<String>(change -> {
            if (change.getControlNewText().length() > 10) {
                return null;
            }
            return change;
        }));

        matchWarning = new Label();
        matchWarning.getStyleClass().add("settings-warning");
        newBallsWarning = new Label();
        newBallsWarning.getStyleClass().add("settings-warning");

        boardSizeSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validate());
        matchSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validate());
        newBallsSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validate());
        validate();

        title = new Label(Messages.get("settings.title"));
        title.getStyleClass().add("settings-title");

        saveButton = createButton(Messages.get("settings.save"), this::onSaveClick,
            "settings-button", "button-save");
        saveButton.setMaxWidth(Double.MAX_VALUE);

        cancelButton = createButton(Messages.get("settings.cancel"), this::onCancelClick,
            "settings-button", "button-cancel");
        cancelButton.setMaxWidth(Double.MAX_VALUE);

        defaultsButton = createButton(Messages.get("settings.restoreDefaults"), this::onDefaultsClick,
            "settings-button", "button-defaults");
        defaultsButton.setMaxWidth(Double.MAX_VALUE);

        HBox row1 = new HBox(8);
        row1.setAlignment(Pos.CENTER);
        row1.getChildren().addAll(saveButton, cancelButton);

        HBox row2 = new HBox(8);
        row2.setAlignment(Pos.CENTER);
        row2.getChildren().add(defaultsButton);

        VBox buttons = new VBox(8);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(row1, row2);

        var flags = FlagSelector.createFlagSelector(onLanguageChange);

        Region spacer = new Region();
        spacer.setMinHeight(8);

        rowLabels[0] = new Label(Messages.get("settings.boardSize"));
        rowLabels[1] = new Label(Messages.get("settings.colors"));
        rowLabels[2] = new Label(Messages.get("settings.matchSize"));
        rowLabels[3] = new Label(Messages.get("settings.newBalls"));
        rowLabels[4] = new Label(Messages.get("settings.playerName"));

        overlay.setOnMouseClicked(e -> {
            if (e.getTarget() == overlay) {
                parent.requestFocus();
            }
        });
        panel.setOnMouseClicked(e -> {
            if (e.getTarget() == panel) {
                parent.requestFocus();
            }
        });

        panel.getChildren().addAll(
            title,
            row(rowLabels[0], boardSizeSpinner),
            row(rowLabels[1], colorsSpinner),
            row(rowLabels[2], matchSpinner, matchWarning),
            row(rowLabels[3], newBallsSpinner, newBallsWarning),
            row(rowLabels[4], playerNameField),
            buttons,
            spacer,
            flags
        );
    }

    public void cancel() {
        onCancelClick();
    }

    public void refreshTexts() {
        title.setText(Messages.get("settings.title"));
        saveButton.setText(Messages.get("settings.save"));
        cancelButton.setText(Messages.get("settings.cancel"));
        defaultsButton.setText(Messages.get("settings.restoreDefaults"));
        rowLabels[0].setText(Messages.get("settings.boardSize"));
        rowLabels[1].setText(Messages.get("settings.colors"));
        rowLabels[2].setText(Messages.get("settings.matchSize"));
        rowLabels[3].setText(Messages.get("settings.newBalls"));
        rowLabels[4].setText(Messages.get("settings.playerName"));
        playerNameField.setPromptText(Messages.get("playerName.prompt"));
        validate();
    }

    private void validate() {
        int bs = boardSizeSpinner.getValue();
        int ms = matchSpinner.getValue();
        int nb = newBallsSpinner.getValue();

        if (ms > bs) {
            matchWarning.setText(Messages.get("settings.warning.matchSize"));
        } else {
            matchWarning.setText("");
        }

        if (nb > bs * bs) {
            newBallsWarning.setText(Messages.get("settings.warning.newBalls"));
        } else {
            newBallsWarning.setText("");
        }
    }

    private void onSaveClick() {
        save("board.size", boardSizeSpinner.getValue());
        save("numberOfColors", colorsSpinner.getValue());
        save("minimalMatch", matchSpinner.getValue());
        save("newBallCount", newBallsSpinner.getValue());
        PropertiesReader.setProperty("player.name", playerNameField.getText().strip());

        hide();
        onSave.run();
    }

    private void onCancelClick() {
        boardSizeSpinner.getValueFactory().setValue(origBoardSize);
        colorsSpinner.getValueFactory().setValue(origColors);
        matchSpinner.getValueFactory().setValue(origMatch);
        newBallsSpinner.getValueFactory().setValue(origNewBalls);
        playerNameField.setText(origPlayerName);
        hide();
        onCancel.run();
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
            throw new RuntimeException("Failed to load size.properties", e);
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
        s.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                String text = s.getEditor().getText().trim();
                try {
                    int val = Integer.parseInt(text);
                    if (val >= min && val <= max) {
                        s.getValueFactory().setValue(val);
                        return;
                    }
                } catch (NumberFormatException ignored) {}
                s.getEditor().setText(String.valueOf(s.getValue()));
            }
        });
        return s;
    }

    private static VBox row(Label l, Node input) {
        l.getStyleClass().add("settings-field-label");
        HBox hb = new HBox(8);
        hb.setAlignment(Pos.CENTER_LEFT);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        hb.getChildren().addAll(l, gap, input);
        VBox vb = new VBox(2);
        Label spacer = new Label();
        spacer.setMinHeight(14);
        vb.getChildren().addAll(hb, spacer);
        return vb;
    }

    private static VBox row(Label l, Node input, Label warning) {
        l.getStyleClass().add("settings-field-label");
        HBox hb = new HBox(8);
        hb.setAlignment(Pos.CENTER_LEFT);
        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        hb.getChildren().addAll(l, gap, input);
        VBox vb = new VBox(2);
        vb.getChildren().addAll(hb, warning);
        return vb;
    }

    private static void save(String key, int value) {
        PropertiesReader.setProperty(key, String.valueOf(value));
    }

    private static int loadInt(String key) {
        return PropertiesReader.getInt(key);
    }
}
