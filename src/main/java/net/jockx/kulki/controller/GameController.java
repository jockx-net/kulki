package net.jockx.kulki.controller;

import javafx.application.Platform;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import net.jockx.kulki.i18n.Messages;
import net.jockx.kulki.model.Ball;
import net.jockx.kulki.model.Cell;
import net.jockx.kulki.model.Game;
import net.jockx.kulki.model.GameEngine;
import net.jockx.kulki.model.GameEvent;
import net.jockx.kulki.model.GameEventBus;
import net.jockx.kulki.model.RuleSet;
import net.jockx.kulki.model.ScoreBoard;
import net.jockx.kulki.model.StateTransition;
import net.jockx.kulki.view.GameMenuPanel;
import net.jockx.kulki.view.GameOverPanel;
import net.jockx.kulki.view.GameView;
import net.jockx.kulki.view.PlayerNamePrompt;
import net.jockx.kulki.view.ScoreBoardPanel;
import net.jockx.kulki.view.SettingsPanel;
import net.jockx.kulki.view.shapes.BallShape;
import net.jockx.kulki.view.shapes.CellNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class GameController {
    private static GameController instance;

    private final Pane rootPane;
    private final GameView gameView;

    private GameEngine game;
    private GameLoop gameLoop;
    private GameEventBus eventBus;
    private GameMenuPanel gameMenuPanel;

    private PlayerNamePrompt playerNamePrompt;
    private SettingsPanel currentSettingsPanel;
    private GameOverPanel currentGameOverPanel;
    private ScoreBoardPanel currentScoreBoardPanel;
    private final ScoreBoard scoreBoard;

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
        rootPane.setFocusTraversable(true);
        gameView = new GameView();
        rootPane.getChildren().add(gameView.getRoot());
        scoreBoard = new ScoreBoard();

        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> repositionView());
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> repositionView());

        rootPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
                if (event.getTarget() instanceof TextInputControl) {
                    return;
                }
                event.consume();
            } else if (code == KeyCode.ESCAPE) {
                event.consume();
                handleEscape();
            }
        });
    }

    private void repositionView() {
        gameView.reposition(rootPane.getWidth(), rootPane.getHeight());
    }

    private void startGame() {
        int minimalMatch = PropertiesReader.getInt("minimalMatch");
        int newBallCount = PropertiesReader.getInt("newBallCount");
        int numberOfColors = PropertiesReader.getInt("numberOfColors");
        int ballScore = PropertiesReader.getInt("ballScore");
        int boardSize = PropertiesReader.getInt("board.size");

        RuleSet ruleSet = new RuleSet()
            .setMinimalMatch(minimalMatch)
            .setBoardSize(boardSize)
            .setNewBallCount(newBallCount)
            .setNumberOfColors(numberOfColors)
            .setPerBallScore(ballScore);

        game = new Game(ruleSet);
        eventBus = new GameEventBus();
        gameLoop = new GameLoop(game, eventBus);

        subscribeToEvents();

        gameView.configure(boardSize, boardSize, newBallCount);
        repositionView();

        game.start();
        prefillNextBallsPanel();
        gameLoop.submitInitialPlacement();

        gameView.getMenuButton().setOnAction(e -> onGameMenuRequested());
        gameView.getMenuButton().setVisible(true);
        Platform.runLater(rootPane::requestFocus);
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

        ball.moveTo(nodePath, () -> {
            if (gameLoop != null) gameLoop.onMoveAnimationFinished();
        });
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

        BallShape.remove(ballShapes, gameView.getBallsPane(), () -> {
            for (CellNode node : matchedNodes) {
                node.unMarkAsSelected();
                node.removeBall();
            }
            if (gameLoop != null) gameLoop.onRemoveAnimationFinished();
        });
    }

    private void onBallsPlaced(StateTransition t) {
        int placedCount = t.placedCells().size();
        List<BallShape> ballShapes = new ArrayList<>();
        for (int i = 0; i < placedCount; i++) {
            Cell cell = t.placedCells().get(i);
            Ball ball = t.placedBalls().get(i);
            BallShape ballShape = new BallShape(ball, gameView.getBallRadius());
            ballShapes.add(ballShape);
            gameView.getCellNode(cell.x, cell.y).setBallFirstTime(ballShape);
        }

        updateNextBallsArea(placedCount);
        BallShape.appearNewBalls(ballShapes, gameView.getBallsPane(),
            () -> {
            if (gameLoop != null) gameLoop.onAppearAnimationFinished();
        });
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

    private void prefillNextBallsPanel() {
        double br = gameView.getBallRadius();
        List<Ball> nextBalls = game.getNextBalls();
        CellNode[] nextNodes = gameView.getNextCellNodes();
        double layoutX = gameView.getCellNode(0, 0).getCellShape().getWidth() / 2;
        double layoutY = gameView.getCellNode(0, 0).getCellShape().getHeight() / 2;
        for (int i = 0; i < nextBalls.size(); i++) {
            BallShape ball = new BallShape(nextBalls.get(i), br);
            ball.setLayoutX(layoutX);
            ball.setLayoutY(layoutY);
            nextNodes[i].getChildren().add(ball);
        }
    }

    private void updateNextBallsArea(int placedCount) {
        List<BallShape> nextBallShapes = new ArrayList<>();
        if (!game.isGameOver()) {
            double br = gameView.getBallRadius();
            List<Ball> nextBalls = game.getNextBalls();
            for (int i = 0; i < placedCount && i < nextBalls.size(); i++) {
                BallShape ball = new BallShape(nextBalls.get(i), br);
                ball.setLayoutX(gameView.getCellNode(0, 0).getCellShape().getWidth() / 2);
                ball.setLayoutY(gameView.getCellNode(0, 0).getCellShape().getHeight() / 2);
                nextBallShapes.add(ball);
            }
        }
        BallShape.appearNext(nextBallShapes, gameView.getNextBallsPane(), placedCount);
    }

    private void handleEscape() {
        if (playerNamePrompt != null
            && rootPane.getChildren().contains(playerNamePrompt.getOverlay())) {
            // prompt handles itself via Enter/OK button — ignore
        } else if (currentGameOverPanel != null
            && rootPane.getChildren().contains(currentGameOverPanel.getOverlay())) {
            // game over is top-level modal — ignore
        } else if (currentScoreBoardPanel != null
            && rootPane.getChildren().contains(currentScoreBoardPanel.getOverlay())) {
            currentScoreBoardPanel.hide();
            currentScoreBoardPanel = null;
            showGameMenu();
        } else if (currentSettingsPanel != null
            && rootPane.getChildren().contains(currentSettingsPanel.getOverlay())) {
            currentSettingsPanel.cancel();
            currentSettingsPanel = null;
            rootPane.requestFocus();
        } else if (gameMenuPanel != null
            && rootPane.getChildren().contains(gameMenuPanel.getOverlay())) {
            if (game != null) {
                gameMenuPanel.hide();
                rootPane.requestFocus();
            }
        } else if (game != null) {
            showGameMenu();
        }
    }

    private void onLocaleChanged(Locale locale) {
        hideAllOverlays();
        Messages.setLocale(locale);
        persistLocale(locale);
        if (game != null) {
            gameView.updateTexts();
        }
        showGameMenu();
    }

    private static void persistLocale(Locale locale) {
        PropertiesReader.setProperty("locale", locale.toString());
    }

    private void hideAllOverlays() {
        if (gameMenuPanel != null) {
            gameMenuPanel.hide();
        }
        if (currentSettingsPanel != null) {
            currentSettingsPanel.hide();
            currentSettingsPanel = null;
        }
        if (currentGameOverPanel != null) {
            currentGameOverPanel.hide();
            currentGameOverPanel = null;
        }
        if (currentScoreBoardPanel != null) {
            currentScoreBoardPanel.hide();
            currentScoreBoardPanel = null;
        }
    }

    public void showGameMenu() {
        boolean inProgress = game != null;
        gameMenuPanel = new GameMenuPanel(rootPane,
            this::onNewGameFromMenu,
            this::onSettingsFromMenu,
            this::onTopScoreFromMenu,
            this::onResignFromMenu,
            this::onExitFromMenu,
            this::onBackToGame,
            this::onLocaleChanged);
        gameMenuPanel.setGameInProgress(inProgress);
        gameMenuPanel.show();
    }

    public void showSettingsDialog(Runnable onSave, Runnable onCancel) {
        currentSettingsPanel = new SettingsPanel(rootPane, onSave, onCancel, locale -> {
            Messages.setLocale(locale);
            persistLocale(locale);
            if (game != null) {
                gameView.updateTexts();
            }
            currentSettingsPanel.refreshTexts();
        });
        currentSettingsPanel.show();
    }

    private void showExitDialog() {
        currentGameOverPanel = new GameOverPanel(rootPane, this::onGameOverOk, game.getScore());
        currentGameOverPanel.show();
    }

    private void onGameOverOk() {
        int score = currentGameOverPanel != null ? currentGameOverPanel.getScore() : 0;
        currentGameOverPanel = null;
        stopCurrentGame();
        addScoreAndShowBoard(score);
    }

    public void showPlayerNamePrompt(Runnable onDone) {
        String prefill = System.getProperty("user.name", "");
        playerNamePrompt = new PlayerNamePrompt(rootPane, prefill,
            () -> {
                playerNamePrompt = null;
                onDone.run();
            },
            locale -> {
                Messages.setLocale(locale);
                persistLocale(locale);
                if (game != null) {
                    gameView.updateTexts();
                }
                if (playerNamePrompt != null) {
                    playerNamePrompt.refreshTexts();
                }
            });
        playerNamePrompt.show();
    }

    public static String getPlayerName() {
        String name = PropertiesReader.getProperty("player.name");
        if (name == null || name.isBlank()) {
            return System.getProperty("user.name", "Player");
        }
        return name;
    }

    private void addScoreAndShowBoard(int score) {
        String name = getPlayerName();
        if (scoreBoard.isHighScore(score)) {
            scoreBoard.addAndHighlight(name, score);
        } else {
            scoreBoard.highlightOnly(name, score);
        }
        showScoreBoard();
    }

    private void stopCurrentGame() {
        if (game != null) {
            gameView.clear();
            game = null;
            gameLoop = null;
            eventBus = null;
            sourceCell = null;
            targetCell = null;
            turnLocked = false;
            if (gameView.getMenuButton() != null) {
                gameView.getMenuButton().setVisible(false);
            }
        }
    }

    private void onNewGameFromMenu() {
        stopCurrentGame();
        startGame();
    }

    private void onTopScoreFromMenu() {
        scoreBoard.clearHighlight();
        showScoreBoard();
    }

    private void showScoreBoard() {
        currentScoreBoardPanel = new ScoreBoardPanel(rootPane, scoreBoard, () -> {
            currentScoreBoardPanel = null;
            showGameMenu();
        });
        currentScoreBoardPanel.show();
    }

    private void onSettingsFromMenu() {
        showSettingsDialog(this::showGameMenu, this::showGameMenu);
    }

    private void onExitFromMenu() {
        Platform.exit();
    }

    private void onResignFromMenu() {
        int score = game != null ? game.getScore() : 0;
        stopCurrentGame();
        addScoreAndShowBoard(score);
    }

    private void onBackToGame() {
        rootPane.requestFocus();
    }

    private void onGameMenuRequested() {
        showGameMenu();
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
                } else if (path.contains(c) && !path.get(0).equals(c)) {
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
