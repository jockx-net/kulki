package net.jockx.kulki.view.shapes;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.Rectangle;
import net.jockx.kulki.controller.EventHandlers;
import net.jockx.kulki.controller.GameController;

/**
 * Created by JockX on 2014-05-12.
 *
 */
public class CellNode extends Group {

	private final Rectangle CellShape;
	final LineTo moveToPosition;
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
		moveToPosition = new LineTo(x * (width + 5) + getCellShape().getWidth() / 2,
									y * (height + 5) + getCellShape().getHeight() / 2);

	}

	public CellNode (double width, double height){
		this.CellShape = new Rectangle(width, height, Color.CORNFLOWERBLUE);
		getChildren().add(CellShape);

		addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, EventHandlers.onMouseOverNext);
		addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, EventHandlers.onMouseAwayNext);

		this.column = -1;
		this.row = -1;
		moveToPosition = null;
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
		resizeBall(BallShape.radius);
	}

	public void markAsSelected(){
		GameController.getInstance().setSourceCell(this);
		setFill(Color.CORAL);
		resizeBall(BallShape.extendedRadius);
	}

	public void resizeBall(double radius) {
		if(!isFree()){
			getBall().setScaleX(radius / getBall().getRadius());
			getBall().setScaleY(radius / getBall().getRadius());
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
		ball.setLayoutX(moveToPosition.getX());
		ball.setLayoutY(moveToPosition.getY());
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
