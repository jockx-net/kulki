package net.jockx.kulki.controller;

import javafx.scene.layout.Pane;
import net.jockx.kulki.model.Ball;
import net.jockx.kulki.model.Cell;
import net.jockx.kulki.model.Game;
import net.jockx.kulki.model.GameEngine;
import net.jockx.kulki.model.GameEvent;
import net.jockx.kulki.model.GameEventBus;
import net.jockx.kulki.model.RuleSet;
import net.jockx.kulki.model.StateTransition;
import net.jockx.kulki.view.GameOverPanel;
import net.jockx.kulki.view.GameView;
import net.jockx.kulki.view.SettingsPanel;
import net.jockx.kulki.view.shapes.BallShape;
import net.jockx.kulki.view.shapes.CellNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameController {
    private static GameController instance;

    private final Pane rootPane;
    private final GameView gameView;

    private GameEngine game;
    private GameLoop gameLoop;
    private GameEventBus eventBus;

    private CellNode sourceCell;
    private CellNode targetCell;
    private boolean turnLocked = false;

    public static GameController getInstance() {
        return instance;
    }

    public Pane getRootPane() {
        return rootPane;
    }

    public GameController() {
        instance = this;
        rootPane = new Pane();
        gameView = new GameView();
        rootPane.getChildren().add(gameView.getRoot());

        rootPane.widthProperty().addListener((_, _, _) -> repositionView());
        rootPane.heightProperty().addListener((_, _, _) -> repositionView());
    }

    private void repositionView() {
        gameView.reposition(rootPane.getWidth(), rootPane.getHeight());
    }

    private void startGame() {
        int minimalMatch = PropertiesReader.getInt("minimalMatch");
        int newBallCount = PropertiesReader.getInt("newBallCount");
        int numberOfColors = PropertiesReader.getInt("numberOfColors");
        int ballScore = PropertiesReader.getInt("ballScore");
        int boardWidth = PropertiesReader.getInt("board.width");
        int boardHeight = PropertiesReader.getInt("board.height");

        RuleSet ruleSet = new RuleSet()
                .setMinimalMatch(minimalMatch)
                .setBoardSize(boardWidth, boardHeight)
                .setNewBallCount(newBallCount)
                .setNumberOfColors(numberOfColors)
                .setPerBallScore(ballScore);

        game = new Game(ruleSet);
        eventBus = new GameEventBus();
        gameLoop = new GameLoop(game, eventBus);

        subscribeToEvents();

        gameView.configure(boardWidth, boardHeight, newBallCount);
        repositionView();

        game.start();
        gameLoop.submitInitialPlacement();
    }

    private void subscribeToEvents() {
        eventBus.subscribe(GameEvent.BALL_MOVED, this::onBallMoved);
        eventBus.subscribe(GameEvent.MATCHES_IDENTIFIED, this::onMatchesIdentified);
        eventBus.subscribe(GameEvent.BALLS_PLACED, this::onBallsPlaced);
        eventBus.subscribe(GameEvent.TURN_COMPLETE, this::onTurnComplete);
        eventBus.subscribe(GameEvent.GAME_OVER, this::onGameOverEvent);
    }

    private void onBallMoved(StateTransition t) {
        CellNode fromNode = gameView.getCellNode(t.source().x, t.source().y);
        CellNode toNode = gameView.getCellNode(t.target().x, t.target().y);
        BallShape ball = fromNode.getBall();

        List<CellNode> nodePath = new ArrayList<>();
        for (Cell c : t.path()) {
            nodePath.add(gameView.getCellNode(c.x, c.y));
        }

        unHighlightPath();
        fromNode.unMarkAsSelected();
        toNode.setBall(ball);
        fromNode.setBall(null);

        ball.moveTo(nodePath, () -> gameLoop.onMoveAnimationFinished());
    }

    private void onMatchesIdentified(StateTransition t) {
        gameView.updateScore(game.getScore());

        Set<BallShape> ballShapes = new HashSet<>();
        List<CellNode> matchedNodes = new ArrayList<>();
        for (Cell c : t.matchedCells()) {
            CellNode cellNode = gameView.getCellNode(c.x, c.y);
            BallShape ballShape = cellNode.getBall();
            if (ballShape != null) {
                ballShapes.add(ballShape);
                matchedNodes.add(cellNode);
            }
        }

        if (sourceCell != null && matchedNodes.contains(sourceCell)) {
            unHighlightPath();
            sourceCell.unMarkAsSelected();
            sourceCell = null;
        }

        BallShape.remove(ballShapes, false, gameView.getBallsPane(), () -> {
            for (CellNode node : matchedNodes) {
                node.unMarkAsSelected();
                node.removeBall();
            }
            gameLoop.onRemoveAnimationFinished();
        });
    }

    private void onBallsPlaced(StateTransition t) {
        List<BallShape> ballShapes = new ArrayList<>();
        for (int i = 0; i < t.placedCells().size(); i++) {
            Cell cell = t.placedCells().get(i);
            Ball ball = t.placedBalls().get(i);
            BallShape ballShape = new BallShape(ball, gameView.getBallRadius());
            ballShapes.add(ballShape);
            gameView.getCellNode(cell.x, cell.y).setBallFirstTime(ballShape);
        }

        updateNextBallsArea();
        BallShape.appearNewBalls(ballShapes, t.placedCells().size(), gameView.getBallsPane(),
                () -> gameLoop.onAppearAnimationFinished());
    }

    private void onTurnComplete(StateTransition t) {
        turnLocked = false;
        if (sourceCell != null && sourceCell.isFree()) {
            sourceCell = null;
        }
    }

    private void onGameOverEvent(StateTransition t) {
        turnLocked = false;
        showExitDialog();
    }

    public void onCellClick(CellNode clicked) {
        if (sourceCell == null) {
            if (turnLocked && clicked.isFree()) {
                return;
            }
            sourceCell = clicked;
            clicked.markAsSelected();
            return;
        }

        if (clicked.equals(sourceCell)) {
            unHighlightPath();
            clicked.unMarkAsSelected();
            sourceCell = null;
            return;
        }

        if (turnLocked) {
            if (clicked.isFree()) {
                return;
            }
            unHighlightPath();
            sourceCell.unMarkAsSelected();
            clicked.markAsSelected();
            sourceCell = clicked;
            return;
        }

        handleSecondClick(clicked);
    }

    private void handleSecondClick(CellNode clicked) {
        if (sourceCell.isFree()) {
            unHighlightPath();
            sourceCell.unMarkAsSelected();
            clicked.markAsSelected();
            sourceCell = clicked;
            return;
        }

        if (!clicked.isFree()) {
            unHighlightPath();
            sourceCell.unMarkAsSelected();
            clicked.markAsSelected();
            sourceCell = clicked;
            return;
        }

        Cell from = game.getBoard().getCell(sourceCell.getColumn(), sourceCell.getRow());
        Cell to = game.getBoard().getCell(clicked.getColumn(), clicked.getRow());

        if (gameLoop.submitMove(from, to)) {
            unHighlightPath();
            sourceCell.unMarkAsSelected();
            sourceCell = null;
            turnLocked = true;
        }
    }

    private void updateNextBallsArea() {
        List<BallShape> nextBallShapes = new ArrayList<>();
        double br = gameView.getBallRadius();
        for (Ball ball : game.getNextBalls()) {
            nextBallShapes.add(new BallShape(ball, br));
        }
        for (int i = 0; i < gameView.getNextCellNodes().length; i++) {
            BallShape ball = nextBallShapes.get(i);
            ball.setLayoutX(gameView.getCellNode(0, 0).getCellShape().getWidth() / 2);
            ball.setLayoutY(gameView.getCellNode(0, 0).getCellShape().getHeight() / 2);
        }
        BallShape.appearNext(nextBallShapes, gameView.getNextBallsPane());
    }

    public void showSettingsDialog() {
        new SettingsPanel(rootPane, this::startGame).show();
    }

    private void showExitDialog() {
        new GameOverPanel(rootPane, this::onRetry).show();
    }

    private void onRetry() {
        rootPane.getChildren().removeIf(child -> child instanceof BallShape);
        gameView.clear();
        game = null;
        showSettingsDialog();
    }

    public CellNode getSourceCell() {
        return sourceCell;
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
        if (sourceCell == null || targetCell == null || sourceCell.isFree()) {
            return;
        }
        int w = game.getBoard().width;
        int h = game.getBoard().height;
        List<CellNode> path = getPath(sourceCell, targetCell);
        boolean reachable = !path.isEmpty() && targetCell.isFree();
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                CellNode c = gameView.getCellNode(j, i);
                if (c.equals(targetCell) && !reachable) {
                    c.markAsInvalidTarget();
                } else if (path.contains(c) && !path.getFirst().equals(c)) {
                    c.markHovered();
                } else if (!c.equals(sourceCell)) {
                    c.unMarkHovered();
                }
            }
        }
    }

    public void unHighlightPath() {
        int w = game.getBoard().width;
        int h = game.getBoard().height;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                gameView.getCellNode(j, i).unMarkHovered();
            }
        }
    }

    private List<CellNode> getPath(CellNode pathStart, CellNode pathEnd) {
        Cell from = game.getBoard().getCell(pathStart.getColumn(), pathStart.getRow());
        Cell to = game.getBoard().getCell(pathEnd.getColumn(), pathEnd.getRow());

        List<Cell> cellPath = game.getBoard().findShortestPathToCell(from, to);
        List<CellNode> nodePath = new ArrayList<>();
        if (cellPath == null) {
            return nodePath;
        }
        for (Cell c : cellPath) {
            nodePath.add(gameView.getCellNode(c.x, c.y));
        }
        return nodePath;
    }
}
