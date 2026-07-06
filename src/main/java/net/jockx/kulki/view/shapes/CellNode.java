package net.jockx.kulki.view.shapes;

import javafx.css.PseudoClass;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import net.jockx.kulki.controller.EventHandlers;

public class CellNode extends Group {

    public static final double CELL_GAP = 5.0;

    private static final PseudoClass HOVERED = PseudoClass.getPseudoClass("hovered");
    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    private static final PseudoClass INVALID_TARGET = PseudoClass.getPseudoClass("invalid-target");

    private final Rectangle CellShape;
    double centerX, centerY;
    private double boardOffsetX, boardOffsetY;
    private BallShape ball;
    private boolean selected;

    private final int column;
    private final int row;

    public CellNode(double width, double height, int x, int y) {
        this.CellShape = new Rectangle(width, height);
        this.CellShape.setArcWidth(10);
        this.CellShape.setArcHeight(10);
        this.CellShape.getStyleClass().add("cell-rect");
        this.column = x;
        this.row = y;

        boundsInParentProperty().addListener((_, _, newBounds) -> {
            centerX = boardOffsetX + newBounds.getCenterX();
            centerY = boardOffsetY + newBounds.getCenterY();
            if (ball != null) {
                ball.setLayoutX(centerX);
                ball.setLayoutY(centerY);
            }
        });

        getChildren().add(CellShape);
        addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, EventHandlers.onMouseOver);
        addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, EventHandlers.onMouseAway);
        addEventHandler(MouseEvent.MOUSE_CLICKED, EventHandlers.onClick);
    }

    public CellNode(double width, double height) {
        this.CellShape = new Rectangle(width, height);
        this.CellShape.setArcWidth(8);
        this.CellShape.setArcHeight(8);
        this.CellShape.getStyleClass().add("cell-rect");
        getChildren().add(CellShape);
        addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, EventHandlers.onMouseOverNext);
        addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, EventHandlers.onMouseAwayNext);
        this.column = -1;
        this.row = -1;
    }

    public double getBallCenterX() {
        return centerX;
    }

    public double getBallCenterY() {
        return centerY;
    }

    public void unMarkHovered() {
        if (!selected) {
            CellShape.pseudoClassStateChanged(HOVERED, false);
            CellShape.pseudoClassStateChanged(INVALID_TARGET, false);
        }
    }

    public void markHovered() {
        if (!selected) {
            CellShape.pseudoClassStateChanged(HOVERED, true);
            CellShape.pseudoClassStateChanged(INVALID_TARGET, false);
        }
    }

    public void markAsInvalidTarget() {
        if (!selected) {
            CellShape.pseudoClassStateChanged(HOVERED, false);
            CellShape.pseudoClassStateChanged(INVALID_TARGET, true);
        }
    }

    public void unMarkAsSelected() {
        selected = false;
        CellShape.pseudoClassStateChanged(SELECTED, false);
        if (!isFree()) {
            getBall().setScaleX(1.0);
            getBall().setScaleY(1.0);
        }
    }

    public void markAsSelected() {
        selected = true;
        CellShape.pseudoClassStateChanged(SELECTED, true);
        if (!isFree()) {
            getBall().setScaleX(1.12);
            getBall().setScaleY(1.12);
        }
    }

    public boolean isFree() {
        return (ball == null);
    }

    public Rectangle getCellShape() {
        return CellShape;
    }

    public BallShape getBall() {
        return ball;
    }

    public void setBall(BallShape ball) {
        this.ball = ball;
    }

    public void setBallFirstTime(BallShape ball) {
        ball.setLayoutX(boardOffsetX + getBoundsInParent().getCenterX());
        ball.setLayoutY(boardOffsetY + getBoundsInParent().getCenterY());
        setBall(ball);
    }

    public void setBoardOffset(double x, double y) {
        boardOffsetX = x;
        boardOffsetY = y;
        if (column >= 0 && row >= 0) {
            double cw = CellShape.getWidth();
            double ch = CellShape.getHeight();
            centerX = x + column * (cw + CELL_GAP) + cw / 2;
            centerY = y + row * (ch + CELL_GAP) + ch / 2;
        }
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public BallShape removeBall() {
        BallShape old = getBall();
        setBall(null);
        return old;
    }
}
