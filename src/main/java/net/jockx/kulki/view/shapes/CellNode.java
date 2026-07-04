package net.jockx.kulki.view.shapes;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import net.jockx.kulki.controller.EventHandlers;
import net.jockx.kulki.controller.GameController;

public class CellNode extends Group {

	public static final double CELL_GAP = 5.0;

	private final Rectangle CellShape;
	double centerX, centerY;

	public void setCenter(double x, double y) {
		centerX = x;
		centerY = y;
	}
	private BallShape ball;

	private final int column;
	private final int row;
	public CellNode(double width, double height, int x, int y) {
		this.CellShape = new Rectangle(width, height, Color.CORNFLOWERBLUE);
		this.column = x;
		this.row = y;

		getChildren().add(CellShape);
		addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, EventHandlers.onMouseOver);
		addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, EventHandlers.onMouseAway);
		addEventHandler(MouseEvent.MOUSE_CLICKED, EventHandlers.onClick);
		centerX = x * (width + CELL_GAP) + width / 2;
		centerY = y * (height + CELL_GAP) + height / 2;
	}

	public CellNode (double width, double height){
		this.CellShape = new Rectangle(width, height, Color.CORNFLOWERBLUE);
		getChildren().add(CellShape);

		addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, EventHandlers.onMouseOverNext);
		addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, EventHandlers.onMouseAwayNext);

		this.column = -1;
		this.row = -1;
	}

	public void updateSize(double newWidth, double newHeight, double offsetX, double offsetY) {
		CellShape.setWidth(newWidth);
		CellShape.setHeight(newHeight);
		if (column >= 0 && row >= 0) {
			centerX = column * (newWidth + CELL_GAP) + newWidth / 2 + offsetX;
			centerY = row * (newHeight + CELL_GAP) + newHeight / 2 + offsetY;
		}
	}

	public double getBallCenterX() {
		return centerX;
	}

	public double getBallCenterY() {
		return centerY;
	}

	public void unMarkHovered() {
		if( !equals(GameController.getInstance().getSourceCell()) ) {
			setFill(Color.CORNFLOWERBLUE);
		}
	}

	public void markHovered() {
		if( !equals(GameController.getInstance().getSourceCell()) ) {
			setFill(Color.BURLYWOOD);
		}
	}

	public void unMarkAsSelected(){
		CellNode sourceCell = GameController.getInstance().getSourceCell();
		if(sourceCell != null && sourceCell.equals(this)){
			GameController.getInstance().setSourceCell(null);
		}
		setFill(Color.CORNFLOWERBLUE);
		if (!isFree()) {
			getBall().setScaleX(1.0);
			getBall().setScaleY(1.0);
		}
	}

	public void markAsSelected(){
		GameController.getInstance().setSourceCell(this);
		setFill(Color.CORAL);
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

	public void setBallFirstTime(BallShape ball){
		ball.setLayoutX(centerX);
		ball.setLayoutY(centerY);
		setBall(ball);
	}


	public void setFill(Color color){
		CellShape.setFill(color);
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
