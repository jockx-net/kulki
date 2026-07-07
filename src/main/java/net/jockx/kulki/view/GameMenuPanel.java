package net.jockx.kulki.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import net.jockx.kulki.i18n.Messages;

import java.util.Locale;
import java.util.function.Consumer;

public class GameMenuPanel extends OverlayPanel {
    private static final double DESIGN_WIDTH = 260;
    private static final double DESIGN_HEIGHT = 400;
    private static final double PANEL_HEIGHT_RATIO = 0.7;

    private final Button backToGameButton;
    private final Button newGameButton;
    private final Button resignButton;

    public GameMenuPanel(Pane parent, Runnable onNewGame, Runnable onSettings, Runnable onTopScore,
                         Runnable onResign, Runnable onExit, Runnable onBackToGame,
                         Consumer<Locale> onLanguageChange) {
        super(parent, DESIGN_WIDTH, DESIGN_HEIGHT, PANEL_HEIGHT_RATIO);

        panel.getStyleClass().add("menu-panel");
        panel.setSpacing(12);
        panel.setAlignment(Pos.CENTER);

        Text title = new Text(Messages.get("menu.title"));
        title.getStyleClass().add("gameover-title");

        backToGameButton = createButton(Messages.get("menu.backToGame"),
            () -> {
                hide();
                onBackToGame.run();
            },
            "game-menu-button", "game-menu-button-back");

        Region spacer = new Region();
        spacer.setMinHeight(16);
        spacer.setPrefHeight(16);

        newGameButton = createButton(Messages.get("menu.newGame"),
            () -> {
                hide();
                onNewGame.run();
            },
            "game-menu-button", "button-start");

        var topScoreButton = createButton(Messages.get("menu.topScore"),
            () -> {
                hide();
                onTopScore.run();
            },
            "game-menu-button");

        var settingsButton = createButton(Messages.get("menu.settings"),
            () -> {
                hide();
                onSettings.run();
            },
            "game-menu-button");

        resignButton = createButton(Messages.get("menu.resign"),
            () -> {
                hide();
                onResign.run();
            },
            "game-menu-button", "button-defaults");

        var exitButton = createButton(Messages.get("menu.exit"),
            () -> {
                hide();
                onExit.run();
            },
            "game-menu-button", "button-defaults");

        var flags = FlagSelector.createFlagSelector(onLanguageChange);

        panel.getChildren().addAll(title, backToGameButton, spacer, newGameButton,
            topScoreButton, settingsButton, resignButton, exitButton, flags);
    }

    public void setGameInProgress(boolean inProgress) {
        backToGameButton.setVisible(inProgress);
        backToGameButton.setManaged(inProgress);
        newGameButton.setVisible(!inProgress);
        newGameButton.setManaged(!inProgress);
        resignButton.setVisible(inProgress);
        resignButton.setManaged(inProgress);
    }

    @Override
    public void hide() {
        super.hide();
        parent.requestFocus();
    }
}
