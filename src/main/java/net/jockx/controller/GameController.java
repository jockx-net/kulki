package net.jockx.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import net.jockx.model.Ball;
import net.jockx.model.Cell;
import net.jockx.model.Game;
import net.jockx.model.RuleSet;
import net.jockx.view.BallShape;
import net.jockx.view.CellNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by JockX on 2014-05-11.
 *
 */
public class GameController implements Initializable{
	private static GameController instance;

//	@FXML private AnchorPane rootPane;
	@FXML private GridPane boardPane;
//	@FXML private GridPane nextBallsPane;
	@FXML private Pane topPane;

	private Game game;
	private CellNode[][] cellNodes;

	private CellNode sourceCell;
	private CellNode targetCell;

	public static GameController getInstance(){
		return instance;
	}


	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

		instance = this;
		BallShape.pane = topPane;

		RuleSet ruleSet = new RuleSet()
				.setMinimalMatch(3)
				.setBoardSize(9, 9)
				.setNewBallCount(8)
				.setNumberOfColors(8);

		game = new Game(ruleSet);
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


		for (int i = 0; i < ruleSet.getNewBallCount(); i++) {
			Ball ball = game.getNextBalls().get(i);
			Cell randomCell = game.getNextRandomCell();
			randomCell.setBall(ball);

			BallShape ballShape = new BallShape(ball);
			addBallShapeAt(ballShape, randomCell);
		}
	}


	private void addBallShapeAt(BallShape ball, Cell cell){
		int x = cell.x;
		int y = cell.y;
		cellNodes[x][y].setBallFirstTime(ball);
		topPane.getChildren().add(ball);

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
		if(sourceCell == null || targetCell == null) {
			return;
		}
		List<CellNode> path =  getPath(sourceCell, targetCell);
		for(int i = 0; i < game.getBoard().height; i++){
			for (int j = 0; j < game.getBoard().width; j++){
				CellNode c = cellNodes[j][i];
				if (path.contains(c)){
					c.setFill(Color.BURLYWOOD);
				} else {
					c.setFill(Color.CORNFLOWERBLUE);
				}
			}

		}
	}

	public void unHighlightPath(){
		for(int i = 0; i < game.getBoard().height; i++) {
			for (int j = 0; j < game.getBoard().width; j++) {
				cellNodes[j][i].setFill(Color.CORNFLOWERBLUE);
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
}

