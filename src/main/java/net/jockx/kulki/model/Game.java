package net.jockx.kulki.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Game implements GameEngine {

    RuleSet ruleSet;
    Board board;
    int score;
    int highScore;

    List<Cell> nextCells;
    List<Ball> nextBalls;
    List<BallColor> colorList;
    Set<Cell> cellsToRemove;

    public Game(RuleSet ruleSet) {
        this.ruleSet = ruleSet;
    }

    @Override
    public void start() {
        score = 0;
        board = new Board(ruleSet);
        selectColors();
        createNextBalls();
        createNextCells();
    }

    public void createNextBalls() {
        nextBalls = new LinkedList<>();

        for (int i = 0; i < ruleSet.newBallCount; i++) {
            int color = new Random().nextInt(ruleSet.numberOfColors);
            nextBalls.add(new Ball(colorList.get(color)));
        }
    }

    public void createNextCells() {
        List<Cell> emptyCells = board.getFreeCells();
        List<Cell> randomCells = new ArrayList<>();
        for (int i = 0; i < ruleSet.newBallCount; i++) {
            if (emptyCells.isEmpty()) {
                break;
            }
            int random = new Random().nextInt(emptyCells.size());
            randomCells.add(emptyCells.remove(random));
        }
        nextCells = randomCells;
    }

    private void selectColors() {
        if (colorList == null) {
            colorList = new ArrayList<>();
        }
        int limit = ruleSet.numberOfColors;
        for (int i = 0; i < limit; i++) {
            colorList.add(RuleSet.getColor(i));
        }
    }

    @Override
    public StateTransition tryMove(Cell from, Cell to) {
        List<Cell> path = board.moveBallToCell(from, to);
        if (path == null || path.isEmpty()) {
            return StateTransition.empty();
        }
        return StateTransition.moveResult(from, to, path);
    }

    @Override
    public StateTransition checkMatches(Cell cell) {
        Set<Cell> matches = board.getAllMatchingLines(cell);
        if (matches == null || matches.isEmpty()) {
            cellsToRemove = null;
            return StateTransition.empty();
        }
        cellsToRemove = matches;
        score += ruleSet.perBallScore * matches.size();
        return StateTransition.matchResult(matches, ruleSet.perBallScore * matches.size());
    }

    @Override
    public StateTransition finalizeRemoval() {
        if (cellsToRemove == null || cellsToRemove.isEmpty()) {
            return StateTransition.empty();
        }
        for (Cell c : cellsToRemove) {
            board.removeBall(c);
        }
        Set<Cell> removed = cellsToRemove;
        cellsToRemove = null;
        return StateTransition.matchResult(removed, 0);
    }

    @Override
    public StateTransition checkAllMatches() {
        Set<Cell> allMatches = new HashSet<>();
        for (Cell cell : board.getCells()) {
            Set<Cell> matches = board.getAllMatchingLines(cell);
            if (matches != null) {
                allMatches.addAll(matches);
            }
        }
        if (allMatches.isEmpty()) {
            cellsToRemove = null;
            return StateTransition.empty();
        }
        cellsToRemove = allMatches;
        score += ruleSet.perBallScore * allMatches.size();
        return StateTransition.matchResult(allMatches, ruleSet.perBallScore * allMatches.size());
    }

    @Override
    public StateTransition placeNextBalls() {
        createNextCells();
        List<Cell> placementCells = new ArrayList<>(nextCells);
        List<Ball> placementBalls = new ArrayList<>();
        for (int i = 0; i < nextCells.size(); i++) {
            Ball ball = nextBalls.get(i);
            placementBalls.add(ball);
            nextCells.get(i).setBall(ball);
        }
        return StateTransition.ballsPlaced(placementCells, placementBalls);
    }

    @Override
    public void generateNextBalls() {
        createNextBalls();
    }

    public void setRuleSet(RuleSet ruleSet) {
        this.ruleSet = ruleSet;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
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

    @Override
    public boolean isGameOver() {
        return board.getFreeCells().isEmpty();
    }

    @Override
    public List<Ball> getNextBalls() {
        return nextBalls;
    }

    @Override
    public List<Cell> getNextCells() {
        return nextCells;
    }

    public List<BallColor> getColorList() {
        return colorList;
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public RuleSet getRuleSet() {
        return ruleSet;
    }

    public Set<Cell> getBallsToRemove() {
        return cellsToRemove;
    }
}
