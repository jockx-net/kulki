package net.jockx.model;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by JockX on 2014-05-11.
 *
 */
public class Game {

	RuleSet ruleSet;
	Board board;
	int score;
	int highScore;
	boolean gameOver = true;

	List<Ball> nextBalls;
	List<Color> colorList;



	public  Game (RuleSet ruleSet){
		this.ruleSet = ruleSet;
	}

	public Game (){
		this.ruleSet = new RuleSet();
	}

	public void start (){
		board = new Board(ruleSet);
		score = 0;
		selectColors();
		createNextBalls();
		gameOver = false;
	}

	public Cell getNextRandomCell() {
		if(nextBalls.isEmpty())
			return null;
		List<Cell> emptyCells = board.getFreeCells();
		int random = new Random().nextInt(emptyCells.size());
		Cell randomCell = emptyCells.remove(random);
		return randomCell;
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

	private void createNextBalls() {
		if(nextBalls == null){
			nextBalls = new LinkedList<Ball>();
		}

		for(int i = 0; i < ruleSet.newBallCount; i++){
			int color = new Random().nextInt(ruleSet.numberOfColors);
			nextBalls.add( new Ball( colorList.get(color)) );
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
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public List<Ball> getNextBalls() {
		return nextBalls;
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


}
