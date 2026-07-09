package net.jockx.kulki.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import net.jockx.kulki.i18n.Messages;
import net.jockx.kulki.view.shapes.CellNode;

public class GameView {
    private static final double CELL_SIZE = 50;
    private static final double GAP = CellNode.CELL_GAP;
    private static final double LABEL_FONT = 18;
    private static final double PADDING = 10;
    private static final double MENU_SIZE = LABEL_FONT * 1.3;
    private static final double SCORE_TEXT_WIDTH = 100;
    private static final double SCORE_DIGIT_WIDTH = 60;
    private static final double SCORE_AREA_MIN_WIDTH = SCORE_TEXT_WIDTH + SCORE_DIGIT_WIDTH;

    private final Pane root;
    private Pane boardPane;
    private GridPane nextBallsPane;
    private CellNode[][] cellNodes;
    private CellNode[] nextCellNodes;
    private Button menuButton;
    private Label scoreTextLabel;
    private Label scoreLabel;
    private Label nextLabel;
    private double ballRadius;
    private double boardScale;
    private double designWidth;
    private double designHeight;

    public GameView() {
        root = new Pane();
    }

    public void configure(int bw, int bh, int newBallCount) {
        root.getChildren().clear();

        double headerWidth = MENU_SIZE + GAP + SCORE_AREA_MIN_WIDTH;
        designWidth = headerWidth + 2 * PADDING;

        double innerWidth = designWidth - 2 * PADDING;
        double internalBoardWidth = bw * CELL_SIZE + (bw - 1) * GAP;
        boardScale = innerWidth / internalBoardWidth;

        ballRadius = CELL_SIZE * 0.45;

        Pane boardPane = createBoardGrid(bw, bh);
        boardPane.getTransforms().add(new Scale(boardScale, boardScale, 0, 0));

        GridPane nextBallsPane = createNextBallsPanel(bw, newBallCount);
        nextBallsPane.getTransforms().add(new Scale(boardScale, boardScale, 0, 0));

        createLabels();
        createMenuButton();

        layout(bw, bh, newBallCount, boardPane, nextBallsPane);
    }

    private Pane createBoardGrid(int bw, int bh) {
        GridPane cellsGrid = new GridPane();
        cellsGrid.setHgap(GAP);
        cellsGrid.setVgap(GAP);

        cellNodes = new CellNode[bw][bh];
        for (int i = 0; i < bh; i++) {
            for (int j = 0; j < bw; j++) {
                cellNodes[j][i] = new CellNode(CELL_SIZE, CELL_SIZE, j, i);
                cellsGrid.add(cellNodes[j][i], j, i);
            }
        }

        boardPane = new Pane(cellsGrid);
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
        scoreTextLabel.setAlignment(Pos.CENTER_RIGHT);

        scoreLabel = new Label("0");
        scoreLabel.getStyleClass().add("game-view-label");
        scoreLabel.setAlignment(Pos.CENTER_RIGHT);

        nextLabel = new Label(Messages.get("gameView.next"));
        nextLabel.getStyleClass().add("game-view-label");
    }

    private void createMenuButton() {
        menuButton = new Button();

        VBox hamburger = new VBox(3);
        hamburger.setAlignment(Pos.CENTER);
        double lineW = 14;
        double lineH = 2.5;
        for (int i = 0; i < 3; i++) {
            Rectangle line = new Rectangle(lineW, lineH);
            line.setFill(Color.WHITE);
            line.setArcWidth(2);
            line.setArcHeight(2);
            hamburger.getChildren().add(line);
        }
        menuButton.setGraphic(hamburger);
        menuButton.getStyleClass().add("menu-button");
        menuButton.setFocusTraversable(false);
        menuButton.setPrefSize(MENU_SIZE, MENU_SIZE);
        menuButton.setMinSize(MENU_SIZE, MENU_SIZE);
        menuButton.setMaxSize(MENU_SIZE, MENU_SIZE);
    }

    private void layout(int bw, int bh, int newBallCount, Pane boardPane, GridPane nextBallsPane) {
        double internalBoardWidth = bw * CELL_SIZE + (bw - 1) * GAP;
        double internalBoardHeight = bh * CELL_SIZE + (bh - 1) * GAP;
        double bhPx = internalBoardHeight * boardScale;
        int nextRows = (newBallCount + bw - 1) / bw;
        double internalNextHeight = nextRows * CELL_SIZE + (nextRows - 1) * GAP;
        double nextAreaH = internalNextHeight * boardScale;
        double labelH = LABEL_FONT * 1.3;
        double ox = PADDING;
        double sy = PADDING;

        double scoreLx = designWidth - PADDING - SCORE_DIGIT_WIDTH - SCORE_TEXT_WIDTH;
        designHeight = PADDING + labelH + GAP + bhPx + GAP + labelH + GAP + nextAreaH + PADDING;

        menuButton.setLayoutX(ox);
        menuButton.setLayoutY(sy);

        scoreTextLabel.setLayoutX(scoreLx);
        scoreTextLabel.setLayoutY(sy);
        scoreTextLabel.setPrefWidth(SCORE_TEXT_WIDTH);

        scoreLabel.setLayoutX(scoreLx + SCORE_TEXT_WIDTH);
        scoreLabel.setLayoutY(sy);
        scoreLabel.setPrefWidth(SCORE_DIGIT_WIDTH);

        double by = PADDING + labelH + GAP;
        boardPane.setLayoutX(ox);
        boardPane.setLayoutY(by);

        for (int i = 0; i < bh; i++) {
            for (int j = 0; j < bw; j++) {
                cellNodes[j][i].setBoardOffset();
            }
        }

        double nly = by + bhPx + GAP;
        nextLabel.setLayoutX(ox);
        nextLabel.setLayoutY(nly);
        nextLabel.setPrefWidth(designWidth - ox - PADDING);

        double ny = nly + labelH + GAP;
        nextBallsPane.setLayoutX(ox);
        nextBallsPane.setLayoutY(ny);
        nextBallsPane.setPrefSize(internalBoardWidth, internalNextHeight);

        root.setMinSize(designWidth, designHeight);
        root.setPrefSize(designWidth, designHeight);
        root.setMaxSize(designWidth, designHeight);

        root.getChildren().addAll(boardPane, menuButton, scoreTextLabel, scoreLabel, nextLabel, nextBallsPane);
    }

    public void reposition(double containerWidth, double containerHeight) {
        if (containerWidth <= 0 || containerHeight <= 0) return;
        if (designWidth <= 0 || designHeight <= 0) return;
        double scale = Math.min(containerWidth / designWidth, containerHeight / designHeight);
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
        return boardPane;
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
        boardPane = null;
        menuButton = null;
        scoreTextLabel = null;
        scoreLabel = null;
        nextLabel = null;
        ballRadius = 0;
        boardScale = 0;
        designWidth = 0;
        designHeight = 0;
    }
}
