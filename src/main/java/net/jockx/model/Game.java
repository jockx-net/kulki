package net.jockx.model;

import javafx.scene.paint.Color;

import java.util.*;

/**
 * Created by JockX on 2014-05-11.
 *
 */
public class Game {

	RuleSet ruleSet;
	Board board;
	int score;
	int highScore;

	List<Cell> nextCells;
	List<Ball> nextBalls;
	List<Color> colorList;
	Set<Cell> cellsToRemove;





	public  Game (RuleSet ruleSet){
		this.ruleSet = ruleSet;
	}

//	public Game (){
//		this.ruleSet = new RuleSet();
//	}

	public void start (){
		score = 0;
		board = new Board(ruleSet);
		selectColors();
		createNextBalls();
		createNextCells();
	}

	public void createNextBalls() {
		nextBalls = new LinkedList<Ball>();

		for(int i = 0; i < ruleSet.newBallCount; i++){
			int color = new Random().nextInt(ruleSet.numberOfColors);
			nextBalls.add( new Ball( colorList.get(color)) );
		}
	}

	public void createNextCells() {
		List<Cell> emptyCells = board.getFreeCells();
		List<Cell> randomCells = new ArrayList<Cell>();
		for (int i = 0; i < ruleSet.newBallCount; i++){
			if(emptyCells.isEmpty()){
				break;
			}
			int random = new Random().nextInt(emptyCells.size());
			randomCells.add(emptyCells.remove(random));
		}
		nextCells = randomCells;
	}

	public boolean validateMove(Cell cell){
		cellsToRemove = board.getAllMatchingLines(cell);
		score += ruleSet.perBallScore * cellsToRemove.size();
		return !(cellsToRemove == null ||
				cellsToRemove.isEmpty());
	}

	public boolean validateAddedBalls(){
		cellsToRemove = new HashSet<Cell>();
		for (Cell cell: board.getCells()){
			Set<Cell> matches = board.getAllMatchingLines(cell);
			if(matches != null) {
				cellsToRemove.addAll(matches);
			}
		}

		score += ruleSet.perBallScore * cellsToRemove.size();
		return !(cellsToRemove == null ||
				cellsToRemove.isEmpty());
	}

	private void selectColors() {
		if(colorList == null){
			colorList = new ArrayList<Color>();
		}
		int limit = ruleSet.numberOfColors;
		for (int i = 0; i < limit; i++){
			colorList.add(RuleSet.getColor(i));
		}
	}


	/*
	 *	Getters and setters
	 */
	public void setRuleSet(RuleSet ruleSet) {
		this.ruleSet = ruleSet;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getHighScore() {
		return highScore;
	}

	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}

	public boolean isGameOver() {
		return board.getFreeCells().isEmpty();
	}

	public List<Ball> getNextBalls() {
		return nextBalls;
	}

	public List<Cell> getNextCells() {
		return nextCells;
	}

	public List<Color> getColorList() {
		return colorList;
	}

	public Board getBoard() {
		return board;
	}

	public RuleSet getRuleSet(){
		return ruleSet;
	}


	public Set<Cell> getBallsToRemove() {
		return cellsToRemove;
	}
}
