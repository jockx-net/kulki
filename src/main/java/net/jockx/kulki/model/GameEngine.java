package net.jockx.kulki.model;

import java.util.List;

public interface GameEngine {

    void start();

    StateTransition tryMove(Cell from, Cell to);

    StateTransition checkMatches(Cell cell);

    StateTransition finalizeRemoval();

    StateTransition checkAllMatches();

    StateTransition placeNextBalls();

    void generateNextBalls();

    List<Ball> getNextBalls();

    boolean isGameOver();

    int getScore();

    Board getBoard();

}
