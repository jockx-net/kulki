package net.jockx.kulki.controller;

import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import net.jockx.kulki.model.Ball;
import net.jockx.kulki.model.Cell;
import net.jockx.kulki.model.Game;
import net.jockx.kulki.model.RuleSet;
import net.jockx.kulki.view.shapes.BallShape;
import net.jockx.kulki.view.shapes.CellNode;

import java.net.URL;
import java.util.*;

/**
 * Created by JockX on 2014-05-11.
 *
 */
public class GameController implements Initializable{
	private static GameController instance;

	@FXML private GridPane boardPane;
	@FXML private FlowPane nextBallsPane;
	@FXML private Pane topPane;

	@FXML private Label scoreLabel;
	private IntegerProperty scoreValue;

	private Game game;
	private CellNode[][] cellNodes;
	private CellNode[] nextCellNodes;

	private CellNode sourceCell;
	private CellNode targetCell;



	public static GameController getInstance(){
		return instance;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		instance = this;
	}

	private void scaleShapes(int boardWidth, int boardHeight) {
		double cellSide;
		double radius;
		int screenX = PropertiesReader.getInt("scene.width");
		int screenY = PropertiesReader.getInt("scene.height");
		if(screenX < screenY){
			cellSide = (screenX - (5 * (boardWidth + 1))) / boardWidth;
		} else {
			cellSide = (screenY - (5 * (boardHeight + 1) )) / (boardHeight+6);
		}
		radius = cellSide * 0.45;
		PropertiesReader.setProperty("cell.size", String.valueOf(cellSide));
		PropertiesReader.setProperty("ball.size", String.valueOf(radius));
		BallShape.radius = radius;
	}

	private void startGame() {
		int minimalMatch = PropertiesReader.getInt("minimalMatch");
		int boardWidth = PropertiesReader.getInt("board.width");
		int boardHeight = PropertiesReader.getInt("board.height");
		int newBallCount = PropertiesReader.getInt("newBallCount");
		int numberOfColors = PropertiesReader.getInt("numberOfColors");
		int ballScore = PropertiesReader.getInt("ballScore");

		scaleShapes(boardWidth, boardHeight);

		BallShape.mainBoardPane = topPane;
		BallShape.nextBallsPane = nextBallsPane;

		RuleSet ruleSet = new RuleSet()
				.setMinimalMatch(minimalMatch)
				.setBoardSize(boardWidth, boardHeight)
				.setNewBallCount(newBallCount)
				.setNumberOfColors(numberOfColors)
				.setPerBallScore(ballScore);
		game = new Game(ruleSet);

		boardPane.getChildren().clear();
		nextBallsPane.getChildren().clear();
		game.start();

		int x = game.getBoard().width;
		int y = game.getBoard().height;

		cellNodes = new CellNode[x][y];

		double cellSize = PropertiesReader.getDouble("cell.size");

		for(int i = 0; i < y; i++){
			for(int j = 0; j < x; j++){
				CellNode cellNode = new CellNode(cellSize, cellSize, j, i);
				boardPane.add(cellNode, j, i);
				cellNodes[j][i] = cellNode;
			}
		}

		nextCellNodes = new CellNode[game.getRuleSet().getNewBallCount()];
		for (int i = 0; i < nextCellNodes.length; i++){
			CellNode nextCellNode = new CellNode(cellSize, cellSize);
			nextCellNodes[i] = nextCellNode;
			nextBallsPane.getChildren().add(nextCellNode);
		}
		addBalls();
	}

	public void showSettingsDialog() {
		final TextField columnsField = new TextField(PropertiesReader.getProperty("board.width"));
		final TextField rowsField = new TextField(PropertiesReader.getProperty("board.height"));
		final TextField colorsField = new TextField(PropertiesReader.getProperty("numberOfColors"));
		final TextField matchField = new TextField(PropertiesReader.getProperty("minimalMatch"));
		final TextField newBallsField = new TextField(PropertiesReader.getProperty("newBallCount"));
		final Button button = new Button("Start");
		final VBox settings = new VBox(
				new Text("Board X"), 	columnsField,
				new Text("Board Y"),	rowsField,
				new Text("Colors"),		colorsField,
				new Text("Match size"),	matchField,
				new Text("New Balls"),	newBallsField,
				button);
		settings.setPadding(new Insets(5));
		settings.setAlignment(Pos.CENTER);
		topPane.getChildren().add(settings);



		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				PropertiesReader.setProperty("board.width",String.valueOf(columnsField.getText()));
				PropertiesReader.setProperty("board.height", String.valueOf(rowsField.getText()));
				PropertiesReader.setProperty("numberOfColors",String.valueOf(colorsField.getText()));
				PropertiesReader.setProperty("minimalMatch",String.valueOf(matchField.getText()));
				PropertiesReader.setProperty("newBallCount",String.valueOf(newBallsField.getText()));
				topPane.getChildren().remove(settings);
				startGame();
			}
		});


	}

	private void endGame(){
		showExitDialog();
	}

	private void showExitDialog() {

		Button button = new Button("Try Again");
		final VBox gameOver = new VBox(new Text("Game Over"), button);
		gameOver.setFillWidth(true);
		gameOver.setAlignment(Pos.CENTER);
		gameOver.setPadding(new Insets(5));

		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int x = game.getBoard().width;
				int y = game.getBoard().height;

				Collection<BallShape> balls = new ArrayList<BallShape>();

				for(int i = 0; i < y; i++){
					for(int j = 0; j < x; j++){
						balls.add(cellNodes[j][i].removeBall());
					}
				}
				nextCellNodes = null;
				BallShape.remove(balls, true);
				topPane.getChildren().remove(gameOver);

				showSettingsDialog();
			}
		});
		topPane.getChildren().add(gameOver);

	}

	private void updateNextBallsArea() {
		List<BallShape> nextBallShapes = createNextBallShapes();
		for (int i = 0; i < nextCellNodes.length; i++){
			nextBallShapes.get(i).setLayoutX(nextCellNodes[i].getCellShape().getWidth() / 2);
			nextBallShapes.get(i).setLayoutY(nextCellNodes[i].getCellShape().getHeight() / 2);
		}
		BallShape.appearNext(nextBallShapes);

	}

	public void endTurn(int x, int y){
		boolean goodMove = game.validateMove(game.getBoard().getCell(x, y));
		if (goodMove){
			removeBalls();
		}
		if (!goodMove || game.getBoard().getBalls().isEmpty()){
			addBalls();
		}
	}

	public void verifyEndTurn() {
		if(game.isGameOver()){
			System.out.println("Game over");
			endGame();
			return;
		}
		if(game.getBoard().getBalls().isEmpty()){
			addBalls();
		}
	}

	private boolean addBalls() {
		game.createNextCells();

		List<BallShape> ballShapes = createNextBallShapes();
		for (int i = 0; i < game.getNextCells().size(); i++) {
			Ball ball = game.getNextBalls().get(i);
			Cell randomCell = game.getNextCells().get(i);

			BallShape ballShape = ballShapes.get(i);

			randomCell.setBall(ball);
			addBallShapeAt(ballShape, randomCell);
		}
		game.createNextBalls();
		updateNextBallsArea();
		game.validateAddedBalls();
		BallShape.appearNewBalls(ballShapes, game.getNextCells().size());

		return game.isGameOver();
	}

	private List<BallShape> createNextBallShapes(){
		List<BallShape> ballShapes = new ArrayList<BallShape>();
		for (int i = 0; i < game.getNextBalls().size(); i++) {
			Ball ball = game.getNextBalls().get(i);
			BallShape ballShape = new BallShape(ball);
			ballShapes.add(ballShape);
		}
		return ballShapes;
	}

	void removeBalls() {
		Set<Cell> ballsToRemove = game.getBallsToRemove();
		Set<BallShape> ballShapes = new HashSet<BallShape>();
		for (Cell c : ballsToRemove){
			game.getBoard().removeBall(c);

			BallShape ballShape = cellNodes[c.x][c.y].getBall();
			if(ballShape != null){
				ballShapes.add(ballShape);
			}
			cellNodes[c.x][c.y].removeBall();
		}
		if(ballShapes.isEmpty()){
			verifyEndTurn();
		} else {
			BallShape.remove(ballShapes, false);
		}
	}

	private void addBallShapeAt(BallShape ball, Cell cell){
		int x = cell.x;
		int y = cell.y;
		cellNodes[x][y].setBallFirstTime(ball);
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
		if( sourceCell == null || targetCell == null ||	sourceCell.isFree() ) {
			return;
		}
		List<CellNode> path =  getPath(sourceCell, targetCell);
		for(int i = 0; i < game.getBoard().height; i++){
			for (int j = 0; j < game.getBoard().width; j++){
				CellNode c = cellNodes[j][i];

				if (path.contains(c) && !path.get(0).equals(c)){
					c.markHovered();

				} else if(!c.equals(sourceCell)) {
					c.unMarkHovered();
				}
			}
		}
	}

	public void unHighlightPath(){
		for(int i = 0; i < game.getBoard().height; i++) {
			for (int j = 0; j < game.getBoard().width; j++) {
				cellNodes[j][i].unMarkHovered();
			}
		}
	}

	public List<CellNode> getPathAndMove(CellNode pathStart, CellNode pathEnd) {
		Cell from = game.getBoard().getCell(pathStart.getColumn(), pathStart.getRow());
		Cell to   = game.getBoard().getCell(pathEnd.getColumn(), pathEnd.getRow());

		List<Cell> cellPath = game.getBoard().moveBallToCell(from, to);
		List<CellNode> nodePath = new ArrayList<CellNode>();
		if (cellPath == null){
			return nodePath;
		}
		for (Cell c : cellPath){
			nodePath.add(cellNodes[c.x][c.y]);
		}

		return nodePath;
	}

	public List<CellNode> getPath(CellNode pathStart, CellNode pathEnd) {
		Cell from = game.getBoard().getCell(pathStart.getColumn(), pathStart.getRow());
		Cell to   = game.getBoard().getCell(pathEnd.getColumn(), pathEnd.getRow());

		List<Cell> cellPath = game.getBoard().findShortestPathToCell(from, to);
		List<CellNode> nodePath = new ArrayList<CellNode>();
		if (cellPath == null){
			return nodePath;
		}
		for (Cell c : cellPath){
			nodePath.add(cellNodes[c.x][c.y]);
		}
		return nodePath;
	}

	public void updateScore() {
		scoreLabel.textProperty().setValue(String.valueOf(game.getScore()));
	}


}

