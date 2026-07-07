package net.jockx.kulki.view;

import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import net.jockx.kulki.i18n.Messages;

public class GameOverPanel extends OverlayPanel {
    private static final double DESIGN_WIDTH = 300;
    private static final double DESIGN_HEIGHT = 180;
    private static final double PANEL_HEIGHT_RATIO = 0.3;

    private final int score;

    public int getScore() {
        return score;
    }

    public GameOverPanel(Pane parent, Runnable onOk, int score) {
        super(parent, DESIGN_WIDTH, DESIGN_HEIGHT, PANEL_HEIGHT_RATIO);
        this.score = score;

        panel.getStyleClass().add("gameover-panel");
        panel.setSpacing(12);
        panel.setAlignment(Pos.CENTER);

        Text gameOverText = new Text(Messages.get("gameOver.title"));
        gameOverText.getStyleClass().add("gameover-title");

        Text scoreText = new Text(Messages.get("gameOver.score", score));
        scoreText.getStyleClass().add("gameover-score");

        var button = createButton(Messages.get("gameOver.ok"),
            () -> {
                hide();
                onOk.run();
            },
            "game-menu-button", "button-start");

        panel.getChildren().addAll(gameOverText, scoreText, button);
    }
}
