package net.jockx.kulki.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.jockx.kulki.i18n.Messages;
import net.jockx.kulki.view.shapes.CellNode;

public class GameView {
    private static final double CELL_SIZE = 50;
    private static final double GAP = CellNode.CELL_GAP;
    private static final double LABEL_FONT = 18;
    private static final double PADDING = 10;

    private final Pane root;
    private GridPane nextBallsPane;
    private CellNode[][] cellNodes;
    private CellNode[] nextCellNodes;
    private Button menuButton;
    private Label scoreTextLabel;
    private Label scoreLabel;
    private Label nextLabel;
    private double ballRadius;
    private double designWidth;
    private double designHeight;

    public GameView() {
        root = new Pane();
    }

    public void configure(int bw, int bh, int newBallCount) {
        root.getChildren().clear();

        ballRadius = CELL_SIZE * 0.45;

        GridPane boardPane = createBoardGrid(bw, bh);
        GridPane nextBallsPane = createNextBallsPanel(bw, newBallCount);
        createLabels();
        createMenuButton();

        layout(bw, bh, newBallCount, boardPane, nextBallsPane);
    }

    private GridPane createBoardGrid(int bw, int bh) {
        GridPane boardPane = new GridPane();
        boardPane.setHgap(GAP);
        boardPane.setVgap(GAP);

        cellNodes = new CellNode[bw][bh];
        for (int i = 0; i < bh; i++) {
            for (int j = 0; j < bw; j++) {
                cellNodes[j][i] = new CellNode(CELL_SIZE, CELL_SIZE, j, i);
                boardPane.add(cellNodes[j][i], j, i);
            }
        }
        return boardPane;
    }

    private GridPane createNextBallsPanel(int bw, int newBallCount) {
        nextBallsPane = new GridPane();
        nextBallsPane.setHgap(GAP);
        nextBallsPane.setVgap(GAP);

        nextCellNodes = new CellNode[newBallCount];
        for (int i = 0; i < newBallCount; i++) {
            nextCellNodes[i] = new CellNode(CELL_SIZE, CELL_SIZE);
            nextBallsPane.add(nextCellNodes[i], i % bw, i / bw);
        }
        return nextBallsPane;
    }

    private void createLabels() {
        scoreTextLabel = new Label(Messages.get("gameView.score"));
        scoreTextLabel.getStyleClass().add("game-view-label");

        scoreLabel = new Label("0");
        scoreLabel.getStyleClass().add("game-view-label");
        scoreLabel.setAlignment(Pos.CENTER_RIGHT);

        nextLabel = new Label(Messages.get("gameView.next"));
        nextLabel.getStyleClass().add("game-view-label");
    }

    private void createMenuButton() {
        double labelH = LABEL_FONT * 1.3;
        menuButton = new Button("☰");
        menuButton.getStyleClass().add("menu-button");
        menuButton.setFocusTraversable(false);
        menuButton.setPrefSize(labelH, labelH);
        menuButton.setMinSize(labelH, labelH);
        menuButton.setMaxSize(labelH, labelH);
    }

    private void layout(int bw, int bh, int newBallCount, GridPane boardPane, GridPane nextBallsPane) {
        double bwPx = bw * CELL_SIZE + (bw - 1) * GAP;
        double bhPx = bh * CELL_SIZE + (bh - 1) * GAP;
        int nextRows = (newBallCount + bw - 1) / bw;
        double nextAreaH = nextRows * CELL_SIZE + (nextRows - 1) * GAP;
        double labelH = LABEL_FONT * 1.3;

        designWidth = bwPx + 2 * PADDING;
        designHeight = PADDING + labelH + GAP + bhPx + GAP + labelH + GAP + nextAreaH + PADDING;

        double ox = PADDING;
        double sy = PADDING;

        menuButton.setLayoutX(ox);
        menuButton.setLayoutY(sy);

        double scoreLx = ox + labelH + GAP;
        scoreTextLabel.setLayoutX(scoreLx);
        scoreTextLabel.setLayoutY(sy);
        scoreLabel.setLayoutX(scoreLx);
        scoreLabel.setLayoutY(sy);
        scoreLabel.setPrefWidth(bwPx - labelH - GAP);

        double by = PADDING + labelH + GAP;
        boardPane.setLayoutX(ox);
        boardPane.setLayoutY(by);

        for (int i = 0; i < bh; i++) {
            for (int j = 0; j < bw; j++) {
                cellNodes[j][i].setBoardOffset(ox, by);
            }
        }

        double nly = by + bhPx + GAP;
        nextLabel.setLayoutX(ox);
        nextLabel.setLayoutY(nly);

        double ny = nly + labelH + GAP;
        nextBallsPane.setLayoutX(ox);
        nextBallsPane.setLayoutY(ny);
        nextBallsPane.setPrefSize(bwPx, nextAreaH);

        for (CellNode cell : nextCellNodes) {
            cell.getCellShape().setWidth(CELL_SIZE);
            cell.getCellShape().setHeight(CELL_SIZE);
        }

        root.setMinSize(designWidth, designHeight);
        root.setPrefSize(designWidth, designHeight);
        root.setMaxSize(designWidth, designHeight);

        root.getChildren().addAll(boardPane, menuButton, scoreTextLabel, scoreLabel, nextLabel, nextBallsPane);
    }

    public void reposition(double containerWidth, double containerHeight) {
        if (containerWidth <= 0 || containerHeight <= 0) return;
        if (designWidth <= 0 || designHeight <= 0) return;
        double scale = Math.min(containerWidth / designWidth, containerHeight / designHeight);
        scale = Math.min(scale, 3);
        root.setScaleX(scale);
        root.setScaleY(scale);
        root.setLayoutX((containerWidth - designWidth) / 2);
        root.setLayoutY((containerHeight - designHeight) / 2);
    }

    public Pane getRoot() {
        return root;
    }

    public CellNode getCellNode(int x, int y) {
        return cellNodes[x][y];
    }

    public void updateTexts() {
        if (scoreTextLabel != null) {
            scoreTextLabel.setText(Messages.get("gameView.score"));
        }
        if (nextLabel != null) {
            nextLabel.setText(Messages.get("gameView.next"));
        }
    }

    public void updateScore(int score) {
        scoreLabel.textProperty().setValue(String.valueOf(score));
    }

    public double getBallRadius() {
        return ballRadius;
    }

    public Button getMenuButton() {
        return menuButton;
    }

    public Pane getBallsPane() {
        return root;
    }

    public GridPane getNextBallsPane() {
        return nextBallsPane;
    }

    public CellNode[] getNextCellNodes() {
        return nextCellNodes;
    }

    public void clear() {
        root.getChildren().clear();
        cellNodes = null;
        nextCellNodes = null;
        menuButton = null;
        scoreTextLabel = null;
        scoreLabel = null;
        nextLabel = null;
        ballRadius = 0;
        designWidth = 0;
        designHeight = 0;
    }
}
