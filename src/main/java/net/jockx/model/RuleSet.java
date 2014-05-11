package net.jockx.model;

/**
 * Created by Mateusz on 2014-05-09.
 */
public class RuleSet {
	int boardWidth = 9;
	int boardHeight = 9;
	int minimalMatch = 5;

	boolean isDiagonalMatchAllowed = true;
	boolean isSimultaneousMatchAllowed = true;

	RuleSet setBoardWidth(int boardWidth){
		this.boardWidth = boardWidth;
		return this;
	}

	RuleSet setBoardHeight(int boardHeight){
		this.boardHeight = boardHeight;
		return this;
	}

	RuleSet setBoardSize (int boardWidth, int boardHeight){
		setBoardWidth(boardWidth);
		setBoardHeight(boardHeight);
		return this;
	}

	RuleSet setMinimalMatch(int minimalMatch){
		this.minimalMatch = minimalMatch;
		return this;
	}

	RuleSet setDiagonalMatchAllowed (boolean allowed){
		this.isDiagonalMatchAllowed = allowed;
		return this;
	}

	RuleSet setSimultaneousMatchAllowed(boolean allowed){
		this.isSimultaneousMatchAllowed = allowed;
		return this;
	}
}
