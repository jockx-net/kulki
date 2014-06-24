package net.jockx.controller;

import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import net.jockx.model.Ball;
import net.jockx.model.Cell;
import net.jockx.model.Game;
import net.jockx.model.RuleSet;
import net.jockx.view.BallShape;
import net.jockx.view.CellNode;

import java.net.URL;
import java.util.*;

/**
 * Created by JockX on 2014-05-11.
 *
 */
public class GameController implements Initializable{
	private static GameController instance;

//	@FXML private AnchorPane rootPane;
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
		BallShape.mainBoardPane = topPane;
		BallShape.nextBallsPane = nextBallsPane;


		RuleSet ruleSet = new RuleSet()
				.setMinimalMatch(2)
				.setBoardSize(3, 3)
				.setNewBallCount(5)
				.setNumberOfColors(7)
				.setPerBallScore(1);

		game = new Game(ruleSet);
		startGame();

		//StringProperty sp = new SimpleStringProperty("Hello");
		//scoreLabel.textProperty().bind(String.valueOf(game.getScore()));
	}

	private void startGame() {
		boardPane.getChildren().clear();
		nextBallsPane.getChildren().clear();
		game.start();

		int x = game.getBoard().width;
		int y = game.getBoard().height;

		cellNodes = new CellNode[x][y];

		for(int i = 0; i < y; i++){
			for(int j = 0; j < x; j++){
				CellNode cellNode = new CellNode(60.0, 60.0, j, i);
				boardPane.add(cellNode, j, i);
				cellNodes[j][i] = cellNode;
			}
		}

		nextCellNodes = new CellNode[game.getRuleSet().getNewBallCount()];
		for (int i = 0; i < nextCellNodes.length; i++){
			CellNode nextCellNode = new CellNode(60.0, 60.0);
			nextCellNodes[i] = nextCellNode;
			nextBallsPane.getChildren().add(nextCellNode);
		}
		addBalls();
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
			boolean gameOver = addBalls();
			if(gameOver){
				System.out.println("Game over - restart not yet implemented");
			}
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
		BallShape.appear(ballShapes, game.getNextCells().size());

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

	public void handleRandomlyAddedMatches(){
		if(game.validateAddedBalls()){
			removeBalls();
		}
	}


	private void removeBalls() {
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

		BallShape.remove(ballShapes);
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

