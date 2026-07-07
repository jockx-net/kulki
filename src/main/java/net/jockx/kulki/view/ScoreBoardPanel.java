package net.jockx.kulki.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import net.jockx.kulki.i18n.Messages;
import net.jockx.kulki.model.ScoreBoard;

public class ScoreBoardPanel extends OverlayPanel {
    private static final double DESIGN_WIDTH = 340;
    private static final double DESIGN_HEIGHT = 440;
    private static final double PANEL_HEIGHT_RATIO = 0.8;

    private final Runnable onClose;

    public ScoreBoardPanel(Pane parent, ScoreBoard scoreBoard, Runnable onClose) {
        super(parent, DESIGN_WIDTH, DESIGN_HEIGHT, PANEL_HEIGHT_RATIO);
        this.onClose = onClose;

        panel.getStyleClass().add("scoreboard-panel");
        panel.setAlignment(Pos.TOP_CENTER);

        Text title = new Text(Messages.get("scoreboard.title"));
        title.getStyleClass().add("gameover-title");

        GridPane grid = createScoreGrid(scoreBoard);

        Button closeButton = OverlayPanel.createButton(Messages.get("scoreboard.close"),
            () -> {
                hide();
                onClose.run();
            },
            "game-menu-button", "game-menu-button-back");

        VBox listArea = new VBox(grid);
        listArea.setAlignment(Pos.CENTER);
        VBox.setVgrow(listArea, Priority.ALWAYS);

        panel.getChildren().addAll(title, listArea, closeButton);
    }

    private static GridPane createScoreGrid(ScoreBoard scoreBoard) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("scoreboard-grid");
        grid.setAlignment(Pos.CENTER);

        var rankCol = new javafx.scene.layout.ColumnConstraints();
        rankCol.setPercentWidth(15);
        var nameCol = new javafx.scene.layout.ColumnConstraints();
        nameCol.setPercentWidth(55);
        var scoreCol = new javafx.scene.layout.ColumnConstraints();
        scoreCol.setPercentWidth(30);
        grid.getColumnConstraints().addAll(rankCol, nameCol, scoreCol);

        ScoreBoard.Entry highlighted = scoreBoard.getHighlightedEntry();
        boolean highlightInEntries = highlighted != null
            && scoreBoard.getEntries().stream().anyMatch(e -> e == highlighted);

        int entryCount = scoreBoard.getEntries().size();
        int totalRows = highlightInEntries ? entryCount : entryCount + 2;
        double dataPercent = entryCount > 0 ? 100.0 / totalRows : 100.0;

        for (int i = 0; i < entryCount; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(dataPercent);
            grid.getRowConstraints().add(rc);

            ScoreBoard.Entry e = scoreBoard.getEntries().get(i);
            boolean isHighlighted = highlighted != null && e == highlighted;
            addRow(grid, i, String.valueOf(i + 1), e.name(), String.valueOf(e.score()), isHighlighted);
        }

        if (highlighted != null && !highlightInEntries) {
            RowConstraints gapRc = new RowConstraints();
            gapRc.setPercentHeight(dataPercent * 0.6);
            gapRc.setMinHeight(10);
            grid.getRowConstraints().add(gapRc);

            RowConstraints highlightRc = new RowConstraints();
            highlightRc.setPercentHeight(dataPercent);
            grid.getRowConstraints().add(highlightRc);

            addRow(grid, entryCount + 1, "11", highlighted.name(),
                String.valueOf(highlighted.score()), true);
        }
        return grid;
    }

    private static void addRow(GridPane grid, int row, String rank, String name, String score, boolean highlighted) {
        Text rankText = new Text(rank);
        rankText.getStyleClass().add("scoreboard-rank");
        GridPane.setConstraints(rankText, 0, row);

        Text nameText = new Text(name);
        nameText.getStyleClass().add("scoreboard-name");
        GridPane.setConstraints(nameText, 1, row);

        Text scoreText = new Text(score);
        scoreText.getStyleClass().add("scoreboard-score");
        GridPane.setConstraints(scoreText, 2, row);

        if (highlighted) {
            rankText.setFill(Color.RED);
            nameText.setFill(Color.RED);
            scoreText.setFill(Color.RED);
        }

        grid.getChildren().addAll(rankText, nameText, scoreText);
    }
}
