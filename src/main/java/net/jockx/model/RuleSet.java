package net.jockx.model;

import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mateusz on 2014-05-09.
 *
 */
public class RuleSet {

	int boardWidth = 9;
	int boardHeight = 9;
	int minimalMatch = 5;
	int newBallCount = 3;
	int numberOfColors = 6;
	int perBallScore = 20;
	int perLineScoreModifier = 2;

	boolean isDiagonalMatchAllowed = true;
	boolean isSimultaneousMatchAllowed = true;

	final static List<Color> colorList = Arrays.asList(
	/* 0*/	Color.RED,
			Color.GREEN,
			Color.BLUE,
			Color.YELLOW,
			Color.MAGENTA,
			Color.CYAN,
	/* 6*/	Color.ORANGE,
			Color.PINK,
			Color.WHITE,
			Color.BLACK,
			Color.GRAY,
			Color.BROWN,
	/*12*/	Color.KHAKI );

	public RuleSet setBoardWidth(int boardWidth){
		this.boardWidth = boardWidth;
		return this;
	}

	public RuleSet setBoardHeight(int boardHeight){
		this.boardHeight = boardHeight;
		return this;
	}

	public RuleSet setBoardSize (int boardWidth, int boardHeight){
		setBoardWidth(boardWidth);
		setBoardHeight(boardHeight);
		return this;
	}

	public RuleSet setMinimalMatch(int minimalMatch){
		this.minimalMatch = minimalMatch;
		return this;
	}

	public RuleSet setDiagonalMatchAllowed (boolean allowed){
		this.isDiagonalMatchAllowed = allowed;
		return this;
	}

	public RuleSet setSimultaneousMatchAllowed(boolean allowed){
		this.isSimultaneousMatchAllowed = allowed;
		return this;
	}

	public RuleSet setNewBallCount(int newBallCount){
		this.newBallCount = newBallCount;
		return this;
	}

	public RuleSet setNumberOfColors(int numberOfColors){
		this.numberOfColors = numberOfColors;
		return this;
	}

	public RuleSet setPerLineScoreModifier(int perLineScoreModifier) {
		this.perLineScoreModifier = perLineScoreModifier;
		return this;
	}

	public RuleSet setPerBallScore(int perBallScore){
		this.perBallScore = perBallScore;
		return this;
	}

	public static List<Color> getColorList() {
		return colorList;
	}

	public static Color getColor(int i) {
		return colorList.get(i);
	}

	public int getNewBallCount() {
		return newBallCount;
	}
}
