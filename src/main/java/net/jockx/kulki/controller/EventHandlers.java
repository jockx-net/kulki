package net.jockx.kulki.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import net.jockx.kulki.view.shapes.BallShape;
import net.jockx.kulki.view.shapes.CellNode;

import java.util.List;

/**
 * Created by JockX on 2014-05-13.
 *
 */
public class EventHandlers {

	/*
	 * Event handlers for CellShape
	 */
	public static EventHandler<MouseEvent> onMouseOver = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			CellNode source = ((CellNode) event.getSource());
			GameController.getInstance().setTargetCell(source);

			source.markHovered();
			GameController.getInstance().highlightPath();
		}
	};

	public static EventHandler<MouseEvent> onMouseAway = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			GameController.getInstance().setTargetCell(null);

			CellNode source = ((CellNode) event.getSource());
			source.unMarkHovered();
		}
	};

	public static EventHandler<MouseEvent> onMouseOverNext = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			CellNode source = ((CellNode) event.getSource());
			source.markHovered();
		}
	};

	public static EventHandler<MouseEvent> onMouseAwayNext = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			CellNode source = ((CellNode) event.getSource());
			source.unMarkHovered();
		}
	};

	public static EventHandler<MouseEvent> onClick = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			CellNode cellClicked = ((CellNode) event.getSource());
			CellNode cellClickedBefore = GameController.getInstance().getSourceCell();

			// First click
			if(cellClickedBefore == null){
				cellClicked.markAsSelected();
				return;
			}

			// Second click on the same node
			if (cellClicked.equals(cellClickedBefore) ){
				cellClicked.unMarkAsSelected();
			}

			// Second click on different node
			else {
				handleSecondClick(cellClickedBefore, cellClicked);
			}
		}

		private void handleSecondClick(CellNode cellClickedBefore, CellNode cellClicked) {
			// Move selection from empty cell
			if(cellClickedBefore.isFree()){
				cellClickedBefore.unMarkAsSelected();
				cellClicked.markAsSelected();
				return;
			}

			// Move selection to different occupied cell
			if(!cellClicked.isFree()) {
				cellClickedBefore.unMarkAsSelected();
				cellClicked.markAsSelected();
				return;
			}

			// Try to move ball
			BallShape ball = cellClickedBefore.getBall();
			moveBallIfPossible(cellClickedBefore, cellClicked, ball);
		}

		private void moveBallIfPossible(CellNode cellFrom, CellNode cellTo, BallShape ball) {
			List<CellNode> path = GameController.getInstance().getPathAndMove(cellFrom, cellTo);
			if(!path.isEmpty()){

				GameController.getInstance().unHighlightPath();

				ball.moveTo(path);
				cellFrom.unMarkAsSelected();
				cellTo.unMarkAsSelected();
				cellTo.setBall(ball);
				cellFrom.setBall(null);
				endMovecellTo = cellTo;
			}
		}

	};

	public static EventHandler<ActionEvent> onRandomBallsPlaced = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			GameController.getInstance().removeBalls();
		}
	};

	static CellNode endMovecellTo;
	public static EventHandler<ActionEvent> onMoveFinished = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			GameController.getInstance().endTurn(endMovecellTo.getColumn(), endMovecellTo.getRow());
		}
	};
}
