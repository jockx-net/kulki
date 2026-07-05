package net.jockx.kulki.model;

import java.util.List;
import java.util.Set;

public interface GameEngine {

	void start();

	StateTransition tryMove(Cell from, Cell to);

	StateTransition checkMatches(Cell cell);

	StateTransition finalizeRemoval();

	StateTransition checkAllMatches();

	StateTransition placeNextBalls();

	void generateNextBalls();

	List<Ball> getNextBalls();

	List<Cell> getNextCells();

	boolean isGameOver();

	int getScore();

	Board getBoard();

	RuleSet getRuleSet();
}
