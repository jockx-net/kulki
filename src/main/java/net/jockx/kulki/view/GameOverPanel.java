package net.jockx.kulki.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class GameOverPanel {
    private static final double DESIGN_WIDTH = 260;
    private static final double DESIGN_HEIGHT = 150;
    private static final double PANEL_HEIGHT_RATIO = 0.3;


    private final Pane parent;
    private final Runnable onRetry;
    private final Pane overlay;
    private final VBox panel;

    public GameOverPanel(Pane parent, Runnable onRetry) {
        this.parent = parent;
        this.onRetry = onRetry;

        overlay = new Pane();
        overlay.getStyleClass().add("dialog-overlay");

        Button button = new Button("Try Again");
        button.setFocusTraversable(false);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().addAll("game-menu-button", "button-start");
        button.setOnAction(_ -> onRetryClick());

        Text gameOverText = new Text("Game Over");
        gameOverText.getStyleClass().add("gameover-title");

        panel = new VBox(12, gameOverText, button);
        panel.setFillWidth(true);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().addAll("dialog-panel", "gameover-panel");
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

    public void show() {
        if (!parent.getChildren().contains(overlay)) {
            parent.getChildren().add(overlay);
        }
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

    private void onRetryClick() {
        hide();
        onRetry.run();
    }
}
