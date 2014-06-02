package net.jockx.controller;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import net.jockx.view.BallShape;
import net.jockx.view.CellNode;
import net.jockx.view.utils.CellNodeUtils;

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
			if( !source.equals(GameController.getInstance().getSourceCell()) ) {
				((CellNode) event.getSource()).setFill(Color.BURLYWOOD);
			}

			GameController.getInstance().highlightPath();
		}
	};

	public static EventHandler<MouseEvent> onMouseAway = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			CellNode source = ((CellNode) event.getSource());

			GameController.getInstance().setTargetCell(null);

			if( !source.equals(GameController.getInstance().getSourceCell()) ) {
				source.setFill(Color.CORNFLOWERBLUE);
			}
		}
	};

	public static EventHandler<MouseEvent> onClick = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			CellNode cellClicked = ((CellNode) event.getSource());
			CellNode cellClickedBefore = GameController.getInstance().getSourceCell();

			// First click
			if(cellClickedBefore == null){
				CellNodeUtils.markAsSelected(cellClicked);
				return;
			}

			// Second click on the same node
			if (cellClicked.equals(cellClickedBefore) ){
				CellNodeUtils.unMarkAsSelected(cellClicked);
			}

			// Second click on different node
			else {
				handleSecondClick(cellClickedBefore, cellClicked);
			}
		}

		private void handleSecondClick(CellNode cellClickedBefore, CellNode cellClicked) {
			// Move selection from empty cell
			if(cellClickedBefore.isFree()){
				CellNodeUtils.unMarkAsSelected(cellClickedBefore);
				CellNodeUtils.markAsSelected(cellClicked);
				return;
			}

			// Move selection to different occupied cell
			if(!cellClicked.isFree()) {
				CellNodeUtils.unMarkAsSelected(cellClickedBefore);
				CellNodeUtils.markAsSelected(cellClicked);
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
				CellNodeUtils.unMarkAsSelected(cellFrom);
				CellNodeUtils.unMarkAsSelected(cellTo);
				cellTo.setBall(ball);
				cellFrom.setBall(null);
			}
		}

	};

}
