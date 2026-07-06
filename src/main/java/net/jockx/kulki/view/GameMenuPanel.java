package net.jockx.kulki.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import net.jockx.kulki.i18n.Messages;

import java.util.Locale;
import java.util.function.Consumer;

public class GameMenuPanel {
    private static final double DESIGN_WIDTH = 260;
    private static final double DESIGN_HEIGHT = 360;
    private static final double PANEL_HEIGHT_RATIO = 0.7;

    private final Pane parent;
    private final Runnable onNewGame;
    private final Runnable onSettings;
    private final Runnable onExit;
    private final Runnable onBackToGame;
    private final Consumer<Locale> onLanguageChange;
    private final Pane overlay;
    private final VBox panel;
    private final Button backToGameButton;

    public record Lang(Locale locale, String imagePath) {}

    private static final Lang[] LANGUAGES = {
            new Lang(Locale.of("en"), "flag-gb.png"),
            new Lang(Locale.of("pl"), "flag-pl.png"),
            new Lang(Locale.of("es"), "flag-es.png"),
            new Lang(Locale.of("de"), "flag-de.png"),
            new Lang(Locale.of("zh"), "flag-cn.png"),
            new Lang(Locale.of("ja"), "flag-jp.png"),
            new Lang(Locale.of("pt", "BR"), "flag-br.png"),
            new Lang(Locale.of("uk"), "flag-ua.png"),
    };

    public GameMenuPanel(Pane parent, Runnable onNewGame, Runnable onSettings, Runnable onExit,
                         Runnable onBackToGame, Consumer<Locale> onLanguageChange) {
        this.parent = parent;
        this.onNewGame = onNewGame;
        this.onSettings = onSettings;
        this.onExit = onExit;
        this.onBackToGame = onBackToGame;
        this.onLanguageChange = onLanguageChange;

        overlay = new Pane();
        overlay.getStyleClass().add("dialog-overlay");

        Text title = new Text(Messages.get("menu.title"));
        title.getStyleClass().add("gameover-title");

        backToGameButton = new Button(Messages.get("menu.backToGame"));
        backToGameButton.getStyleClass().addAll("game-menu-button", "game-menu-button-back");
        backToGameButton.setFocusTraversable(false);
        backToGameButton.setMaxWidth(Double.MAX_VALUE);
        backToGameButton.setOnAction(_ -> onBackToGameClick());

        Region spacer = new Region();
        spacer.setMinHeight(16);
        spacer.setPrefHeight(16);

        Button newGameButton = new Button(Messages.get("menu.newGame"));
        newGameButton.getStyleClass().addAll("game-menu-button", "button-start");
        newGameButton.setFocusTraversable(false);
        newGameButton.setMaxWidth(Double.MAX_VALUE);
        newGameButton.setOnAction(_ -> onNewGameClick());

        Button settingsButton = new Button(Messages.get("menu.settings"));
        settingsButton.setFocusTraversable(false);
        settingsButton.getStyleClass().add("game-menu-button");
        settingsButton.setMaxWidth(Double.MAX_VALUE);
        settingsButton.setOnAction(_ -> onSettingsClick());

        Button exitButton = new Button(Messages.get("menu.exit"));
        exitButton.setFocusTraversable(false);
        exitButton.getStyleClass().addAll("game-menu-button", "button-defaults");
        exitButton.setMaxWidth(Double.MAX_VALUE);
        exitButton.setOnAction(_ -> onExitClick());

        HBox flags = new HBox(4);
        flags.setAlignment(Pos.CENTER);
        for (Lang lang : LANGUAGES) {
            var is = getClass().getClassLoader().getResourceAsStream(lang.imagePath());
            ImageView iv = new ImageView(new Image(is));
            iv.setFitHeight(12);
            iv.setPreserveRatio(true);
            Button fb = new Button();
            fb.setGraphic(iv);
            fb.getStyleClass().add("flag-button");
            fb.setFocusTraversable(false);
            fb.setOnAction(_ -> onLanguageChange.accept(lang.locale()));
            flags.getChildren().add(fb);
        }

        panel = new VBox(12, title, backToGameButton, spacer, newGameButton, settingsButton, exitButton, flags);
        panel.setFillWidth(true);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().addAll("dialog-panel", "menu-panel");
        panel.setMinSize(DESIGN_WIDTH, DESIGN_HEIGHT);
        panel.setPrefSize(DESIGN_WIDTH, DESIGN_HEIGHT);
        panel.setMaxSize(DESIGN_WIDTH, DESIGN_HEIGHT);

        overlay.getChildren().add(panel);

        parent.widthProperty().addListener((_, _, _) -> reposition());
        parent.heightProperty().addListener((_, _, _) -> reposition());
    }

    public Pane getOverlay() {
        return overlay;
    }

    public void setGameInProgress(boolean inProgress) {
        backToGameButton.setVisible(inProgress);
        backToGameButton.setManaged(inProgress);
    }

    public void show() {
        if (!parent.getChildren().contains(overlay)) {
            parent.getChildren().add(overlay);
        }
        Platform.runLater(this::reposition);
    }

    public void hide() {
        parent.getChildren().remove(overlay);
        parent.requestFocus();
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

    private void onNewGameClick() {
        hide();
        onNewGame.run();
    }

    private void onSettingsClick() {
        hide();
        onSettings.run();
    }

    private void onExitClick() {
        hide();
        onExit.run();
    }

    private void onBackToGameClick() {
        hide();
        onBackToGame.run();
    }
}
