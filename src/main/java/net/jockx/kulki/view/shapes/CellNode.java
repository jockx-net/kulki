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

    private final Rectangle cellShape;
    double centerX, centerY;
    private BallShape ball;
    private boolean selected;

    private final int column;
    private final int row;

    public CellNode(double width, double height, int x, int y) {
        this.cellShape = createCellRect(width, height, 10);
        this.column = x;
        this.row = y;

        boundsInParentProperty().addListener((obs, oldVal, newBounds) -> {
            centerX = newBounds.getCenterX();
            centerY = newBounds.getCenterY();
            if (ball != null) {
                ball.setLayoutX(centerX);
                ball.setLayoutY(centerY);
            }
        });

        getChildren().add(cellShape);
        addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, EventHandlers.onMouseOver);
        addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, EventHandlers.onMouseAway);
        addEventHandler(MouseEvent.MOUSE_CLICKED, EventHandlers.onClick);
    }

    public CellNode(double width, double height) {
        this.cellShape = createCellRect(width, height, 8);
        this.column = -1;
        this.row = -1;

        getChildren().add(cellShape);
        addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, EventHandlers.onMouseOverNext);
        addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, EventHandlers.onMouseAwayNext);
    }

    private static Rectangle createCellRect(double width, double height, double arc) {
        Rectangle rect = new Rectangle(width, height);
        rect.setArcWidth(arc);
        rect.setArcHeight(arc);
        rect.getStyleClass().add("cell-rect");
        return rect;
    }

    public double getBallCenterX() {
        return centerX;
    }

    public double getBallCenterY() {
        return centerY;
    }

    public void unMarkHovered() {
        if (!selected) {
            cellShape.pseudoClassStateChanged(HOVERED, false);
            cellShape.pseudoClassStateChanged(INVALID_TARGET, false);
        }
    }

    public void markHovered() {
        if (!selected) {
            cellShape.pseudoClassStateChanged(HOVERED, true);
            cellShape.pseudoClassStateChanged(INVALID_TARGET, false);
        }
    }

    public void markAsInvalidTarget() {
        if (!selected) {
            cellShape.pseudoClassStateChanged(HOVERED, false);
            cellShape.pseudoClassStateChanged(INVALID_TARGET, true);
        }
    }

    public void unMarkAsSelected() {
        selected = false;
        cellShape.pseudoClassStateChanged(SELECTED, false);
        if (!isFree()) {
            getBall().setScaleX(1.0);
            getBall().setScaleY(1.0);
        }
    }

    public void markAsSelected() {
        selected = true;
        cellShape.pseudoClassStateChanged(SELECTED, true);
        if (!isFree()) {
            getBall().setScaleX(1.12);
            getBall().setScaleY(1.12);
        }
    }

    public boolean isFree() {
        return (ball == null);
    }

    public Rectangle getCellShape() {
        return cellShape;
    }

    public BallShape getBall() {
        return ball;
    }

    public void setBall(BallShape ball) {
        this.ball = ball;
    }

    public void setBallFirstTime(BallShape ball) {
        ball.setLayoutX(getBoundsInParent().getCenterX());
        ball.setLayoutY(getBoundsInParent().getCenterY());
        setBall(ball);
    }

    public void setBoardOffset() {
        if (column >= 0 && row >= 0) {
            double cw = cellShape.getWidth();
            double ch = cellShape.getHeight();
            centerX = column * (cw + CELL_GAP) + cw / 2;
            centerY = row * (ch + CELL_GAP) + ch / 2;
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
