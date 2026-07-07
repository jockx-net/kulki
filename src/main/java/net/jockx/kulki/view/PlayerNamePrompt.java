package net.jockx.kulki.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import net.jockx.kulki.controller.PropertiesReader;
import net.jockx.kulki.i18n.Messages;

import java.util.Locale;
import java.util.function.Consumer;

public class PlayerNamePrompt extends OverlayPanel {
    private static final double DESIGN_WIDTH = 320;
    private static final double DESIGN_HEIGHT = 260;
    private static final double PANEL_HEIGHT_RATIO = 0.5;

    private final Text title;
    private final TextField nameField;
    private final Button okButton;
    private final Runnable onNameSet;

    public PlayerNamePrompt(Pane parent, String prefill, Runnable onNameSet,
                            Consumer<Locale> onLanguageChange) {
        super(parent, DESIGN_WIDTH, DESIGN_HEIGHT, PANEL_HEIGHT_RATIO);
        this.onNameSet = onNameSet;

        panel.getStyleClass().add("settings-panel");
        panel.setAlignment(Pos.CENTER);
        panel.setSpacing(16);

        title = new Text(Messages.get("playerName.title"));
        title.getStyleClass().add("gameover-title");

        nameField = new TextField();
        nameField.setText(prefill);
        nameField.setMaxWidth(260);
        nameField.setAlignment(Pos.CENTER);
        nameField.getStyleClass().add("player-name-prompt-field");
        nameField.setPromptText(Messages.get("playerName.prompt"));
        nameField.setTextFormatter(new TextFormatter<String>(change -> {
            if (change.getControlNewText().length() > 20) {
                return null;
            }
            return change;
        }));
        nameField.setOnAction(_ -> confirm());

        okButton = createButton(Messages.get("playerName.ok"), this::confirm,
            "settings-button", "button-save");

        var flags = FlagSelector.createFlagSelector(onLanguageChange);

        overlay.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                event.consume();
                confirm();
            }
        });

        panel.getChildren().addAll(title, nameField, okButton, flags);
    }

    public void refreshTexts() {
        title.setText(Messages.get("playerName.title"));
        nameField.setPromptText(Messages.get("playerName.prompt"));
        okButton.setText(Messages.get("playerName.ok"));
    }

    @Override
    public void show() {
        super.show();
        Platform.runLater(nameField::requestFocus);
    }

    private void confirm() {
        String name = nameField.getText().strip();
        PropertiesReader.setProperty("player.name", name);
        hide();
        onNameSet.run();
    }
}
