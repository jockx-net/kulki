package net.jockx.kulki.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import net.jockx.kulki.model.*;
import net.jockx.kulki.view.shapes.BallShape;
import net.jockx.kulki.view.shapes.CellNode;

import java.net.URL;
import java.util.*;

public class GameController implements Initializable {
	private static GameController instance;

	@FXML
	private FlowPane nextBallsPane;
	@FXML
	private Pane topPane;
	@FXML
	private Label scoreLabel;

	private GameEngine game;
	private GameLoop gameLoop;
	private GameEventBus eventBus;
	private GridPane boardPane;
	private CellNode[][] cellNodes;
	private CellNode[] nextCellNodes;

	private CellNode sourceCell;
	private CellNode targetCell;
	private boolean turnLocked = false;

	private final int MIN_CELL_SIZE = 35;
	private static final int BOARD_PADDING = 10;
	private boolean listenersAdded = false;
	private BallShape[] currentNextBalls;
	private double ballRadius;

	public static GameController getInstance() {
		return instance;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		instance = this;
	}

	private void startGame() {
		int minimalMatch = PropertiesReader.getInt("minimalMatch");
		int newBallCount = PropertiesReader.getInt("newBallCount");
		int numberOfColors = PropertiesReader.getInt("numberOfColors");
		int ballScore = PropertiesReader.getInt("ballScore");
		int boardWidth = PropertiesReader.getInt("board.width");
		int boardHeight = PropertiesReader.getInt("board.height");

		RuleSet ruleSet = new RuleSet()
				.setMinimalMatch(minimalMatch)
				.setBoardSize(boardWidth, boardHeight)
				.setNewBallCount(newBallCount)
				.setNumberOfColors(numberOfColors)
				.setPerBallScore(ballScore);

		game = new Game(ruleSet);
		eventBus = new GameEventBus();
		gameLoop = new GameLoop(game, eventBus);

		subscribeToEvents();

		if (boardPane == null) {
			boardPane = new GridPane();
			boardPane.setHgap(CellNode.CELL_GAP);
			boardPane.setVgap(CellNode.CELL_GAP);
			topPane.getChildren().add(0, boardPane);
		}
		boardPane.getChildren().clear();
		nextBallsPane.getChildren().clear();
		game.start();

		int x = game.getBoard().width;
		int y = game.getBoard().height;

		cellNodes = new CellNode[x][y];

		double tempCellSize = 50;
		ballRadius = tempCellSize * 0.45;

		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				CellNode cellNode = new CellNode(tempCellSize, tempCellSize, j, i);
				boardPane.add(cellNode, j, i);
				cellNodes[j][i] = cellNode;
			}
		}

		nextCellNodes = new CellNode[game.getRuleSet().getNewBallCount()];
		for (int i = 0; i < nextCellNodes.length; i++) {
			CellNode nextCellNode = new CellNode(tempCellSize, tempCellSize);
			nextCellNodes[i] = nextCellNode;
			nextBallsPane.getChildren().add(nextCellNode);
		}

		updateBoardLayout();
		gameLoop.submitInitialPlacement();

		if (!listenersAdded) {
			topPane.widthProperty().addListener((obs, old, nv) -> updateBoardLayout());
			topPane.heightProperty().addListener((obs, old, nv) -> updateBoardLayout());
			listenersAdded = true;
		}
	}

	private void subscribeToEvents() {
		eventBus.subscribe(GameEvent.BALL_MOVED, this::onBallMoved);
		eventBus.subscribe(GameEvent.MATCHES_IDENTIFIED, this::onMatchesIdentified);
		eventBus.subscribe(GameEvent.BALLS_PLACED, this::onBallsPlaced);
		eventBus.subscribe(GameEvent.TURN_COMPLETE, this::onTurnComplete);
		eventBus.subscribe(GameEvent.GAME_OVER, this::onGameOverEvent);
	}

	private void onBallMoved(StateTransition t) {
		CellNode fromNode = cellNodes[t.source().x][t.source().y];
		CellNode toNode = cellNodes[t.target().x][t.target().y];
		BallShape ball = fromNode.getBall();

		List<CellNode> nodePath = new ArrayList<>();
		for (Cell c : t.path()) {
			nodePath.add(cellNodes[c.x][c.y]);
		}

		unHighlightPath();
		toNode.setBall(ball);
		fromNode.setBall(null);

		ball.moveTo(nodePath, () -> gameLoop.onMoveAnimationFinished());
	}

	private void onMatchesIdentified(StateTransition t) {
		updateScore();

		Set<BallShape> ballShapes = new HashSet<>();
		for (Cell c : t.matchedCells()) {
			BallShape ballShape = cellNodes[c.x][c.y].getBall();
			if (ballShape != null) {
				ballShapes.add(ballShape);
			}
			cellNodes[c.x][c.y].removeBall();
		}

		BallShape.remove(ballShapes, false, topPane, () -> gameLoop.onRemoveAnimationFinished());
	}

	private void onBallsPlaced(StateTransition t) {
		List<BallShape> ballShapes = new ArrayList<>();
		for (int i = 0; i < t.placedCells().size(); i++) {
			Cell cell = t.placedCells().get(i);
			Ball ball = t.placedBalls().get(i);
			BallShape ballShape = new BallShape(ball, ballRadius);
			ballShapes.add(ballShape);
			cellNodes[cell.x][cell.y].setBallFirstTime(ballShape);
		}

		updateNextBallsArea();
		BallShape.appearNewBalls(ballShapes, t.placedCells().size(), topPane,
				() -> gameLoop.onAppearAnimationFinished());
	}

	private void onTurnComplete(StateTransition t) {
		turnLocked = false;
	}

	private void onGameOverEvent(StateTransition t) {
		turnLocked = false;
		endGame();
	}

	public void onCellClick(CellNode clicked) {
		if (turnLocked) {
			return;
		}

		if (sourceCell == null) {
			sourceCell = clicked;
			clicked.markAsSelected();
			return;
		}

		if (clicked.equals(sourceCell)) {
			clicked.unMarkAsSelected();
			sourceCell = null;
			return;
		}

		handleSecondClick(clicked);
	}

	private void handleSecondClick(CellNode clicked) {
		if (sourceCell.isFree()) {
			sourceCell.unMarkAsSelected();
			clicked.markAsSelected();
			sourceCell = clicked;
			return;
		}

		if (!clicked.isFree()) {
			sourceCell.unMarkAsSelected();
			clicked.markAsSelected();
			sourceCell = clicked;
			return;
		}

		Cell from = game.getBoard().getCell(sourceCell.getColumn(), sourceCell.getRow());
		Cell to = game.getBoard().getCell(clicked.getColumn(), clicked.getRow());

		unHighlightPath();
		sourceCell.unMarkAsSelected();
		sourceCell = null;

		if (gameLoop.submitMove(from, to)) {
			turnLocked = true;
		}
	}

	private void updateBoardLayout() {
		if (game == null) return;

		int bw = game.getBoard().width;
		int bh = game.getBoard().height;

		for (int i = 0; i < bh; i++) {
			for (int j = 0; j < bw; j++) {
				CellNode cn = cellNodes[j][i];
				if (!cn.isFree()) {
					cn.getBall().stopAnimation();
				}
			}
		}

		double containerWidth = topPane.getWidth();
		double containerHeight = topPane.getHeight();
		if (containerWidth <= 0 || containerHeight <= 0) return;

		double gap = CellNode.CELL_GAP;

		double availWidth = containerWidth - 2.0 * BOARD_PADDING;
		double availHeight = containerHeight - 2.0 * BOARD_PADDING;

		double cellSizeByWidth = (availWidth - (bw - 1) * gap) / bw;
		double cellSizeByHeight = (availHeight - (bh - 1) * gap) / bh;
		double cellSize = Math.max(MIN_CELL_SIZE, Math.min(cellSizeByWidth, cellSizeByHeight));

		double boardPixelWidth = bw * cellSize + (bw - 1) * gap;
		double boardPixelHeight = bh * cellSize + (bh - 1) * gap;

		double offsetX = Math.max(BOARD_PADDING, (containerWidth - boardPixelWidth) / 2);
		double offsetY = Math.max(BOARD_PADDING, (containerHeight - boardPixelHeight) / 2);

		ballRadius = cellSize * 0.45;

		boardPane.setLayoutX(offsetX);
		boardPane.setLayoutY(offsetY);

		for (int i = 0; i < bh; i++) {
			for (int j = 0; j < bw; j++) {
				cellNodes[j][i].updateSize(cellSize, cellSize, offsetX, offsetY);
			}
		}

		boardPane.autosize();
		boardPane.layout();

		for (int i = 0; i < bh; i++) {
			for (int j = 0; j < bw; j++) {
				CellNode cn = cellNodes[j][i];
				Bounds b = cn.getBoundsInParent();
				cn.setCenter(
						boardPane.getLayoutX() + b.getMinX() + b.getWidth() / 2,
						boardPane.getLayoutY() + b.getMinY() + b.getHeight() / 2
				);

				if (!cn.isFree()) {
					BallShape ball = cn.getBall();
					ball.updateGradient(ballRadius);
					ball.setLayoutX(cn.getBallCenterX());
					ball.setLayoutY(cn.getBallCenterY());
				}
			}
		}

		if (nextCellNodes != null) {
			double nextCellSize = Math.max(MIN_CELL_SIZE, cellSize * 0.55);
			double nextBallRadius = nextCellSize * 0.45;
			for (int i = 0; i < nextCellNodes.length; i++) {
				CellNode cell = nextCellNodes[i];
				cell.getCellShape().setWidth(nextCellSize);
				cell.getCellShape().setHeight(nextCellSize);
				if (currentNextBalls != null && i < currentNextBalls.length) {
					BallShape ball = currentNextBalls[i];
					ball.updateGradient(nextBallRadius);
					ball.setLayoutX(nextCellSize / 2);
					ball.setLayoutY(nextCellSize / 2);
				}
			}
			nextBallsPane.requestLayout();
		}

		boardPane.requestLayout();
	}

	public void showSettingsDialog() {
		final TextField columnsField = new TextField(PropertiesReader.getProperty("board.width"));
		final TextField rowsField = new TextField(PropertiesReader.getProperty("board.height"));
		final TextField colorsField = new TextField(PropertiesReader.getProperty("numberOfColors"));
		final TextField matchField = new TextField(PropertiesReader.getProperty("minimalMatch"));
		final TextField newBallsField = new TextField(PropertiesReader.getProperty("newBallCount"));
		final Button button = new Button("Start");
		final VBox settings = new VBox(
				new Text("Board X"), columnsField,
				new Text("Board Y"), rowsField,
				new Text("Colors"), colorsField,
				new Text("Match size"), matchField,
				new Text("New Balls"), newBallsField,
				button);
		settings.setFillWidth(true);
		settings.setPadding(new Insets(CellNode.CELL_GAP));
		settings.setAlignment(Pos.CENTER);
		topPane.getChildren().add(settings);

		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				PropertiesReader.setProperty("board.width", String.valueOf(columnsField.getText()));
				PropertiesReader.setProperty("board.height", String.valueOf(rowsField.getText()));
				PropertiesReader.setProperty("numberOfColors", String.valueOf(colorsField.getText()));
				PropertiesReader.setProperty("minimalMatch", String.valueOf(matchField.getText()));
				PropertiesReader.setProperty("newBallCount", String.valueOf(newBallsField.getText()));
				topPane.getChildren().remove(settings);
				startGame();
			}
		});
	}

	private void endGame() {
		showExitDialog();
	}

	private void showExitDialog() {
		Button button = new Button("Try Again");
		final VBox gameOver = new VBox(new Text("Game Over"), button);
		gameOver.setFillWidth(true);
		gameOver.setAlignment(Pos.CENTER);
		gameOver.setPadding(new Insets(CellNode.CELL_GAP));

		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int x = game.getBoard().width;
				int y = game.getBoard().height;

				Collection<BallShape> balls = new ArrayList<>();

				for (int i = 0; i < y; i++) {
					for (int j = 0; j < x; j++) {
						balls.add(cellNodes[j][i].removeBall());
					}
				}
				nextCellNodes = null;
				currentNextBalls = null;
				BallShape.remove(balls, true, topPane, null);
				topPane.getChildren().remove(gameOver);

				showSettingsDialog();
			}
		});
		topPane.getChildren().add(gameOver);
	}

	private void updateNextBallsArea() {
		List<BallShape> nextBallShapes = new ArrayList<>();
		double nextCellSize = nextCellNodes[0].getCellShape().getWidth();
		double nextBallRadius = nextCellSize * 0.45;
		for (Ball ball : game.getNextBalls()) {
			nextBallShapes.add(new BallShape(ball, nextBallRadius));
		}
		for (int i = 0; i < nextCellNodes.length; i++) {
			BallShape ball = nextBallShapes.get(i);
			ball.setLayoutX(nextCellSize / 2);
			ball.setLayoutY(nextCellSize / 2);
		}
		BallShape.appearNext(nextBallShapes, nextBallsPane);
		currentNextBalls = nextBallShapes.toArray(new BallShape[0]);
	}

	public CellNode getSourceCell() {
		return sourceCell;
	}

	public void setSourceCell(CellNode sourceCell) {
		this.sourceCell = sourceCell;
	}

	public CellNode getTargetCell() {
		return targetCell;
	}

	public void setTargetCell(CellNode targetCell) {
		this.targetCell = targetCell;
	}

	public void highlightPath() {
		CellNode targetCell = getTargetCell();
		CellNode sourceCell = getSourceCell();
		if (sourceCell == null || targetCell == null || sourceCell.isFree()) {
			return;
		}
		List<CellNode> path = getPath(sourceCell, targetCell);
		for (int i = 0; i < game.getBoard().height; i++) {
			for (int j = 0; j < game.getBoard().width; j++) {
				CellNode c = cellNodes[j][i];
				if (path.contains(c) && !path.get(0).equals(c)) {
					c.markHovered();
				} else if (!c.equals(sourceCell)) {
					c.unMarkHovered();
				}
			}
		}
	}

	public void unHighlightPath() {
		for (int i = 0; i < game.getBoard().height; i++) {
			for (int j = 0; j < game.getBoard().width; j++) {
				cellNodes[j][i].unMarkHovered();
			}
		}
	}

	private List<CellNode> getPath(CellNode pathStart, CellNode pathEnd) {
		Cell from = game.getBoard().getCell(pathStart.getColumn(), pathStart.getRow());
		Cell to = game.getBoard().getCell(pathEnd.getColumn(), pathEnd.getRow());

		List<Cell> cellPath = game.getBoard().findShortestPathToCell(from, to);
		List<CellNode> nodePath = new ArrayList<>();
		if (cellPath == null) {
			return nodePath;
		}
		for (Cell c : cellPath) {
			nodePath.add(cellNodes[c.x][c.y]);
		}
		return nodePath;
	}

	private void updateScore() {
		scoreLabel.textProperty().setValue(String.valueOf(game.getScore()));
	}
}
