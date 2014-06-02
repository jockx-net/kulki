package net.jockx.view.utils;

import javafx.scene.paint.Color;
import net.jockx.controller.GameController;
import net.jockx.view.CellNode;

/**
 * Created by JockX on 2014-06-02.
 *
 */
public class CellNodeUtils {
	public static void resizeBall(CellNode cell, double radius) {
		if(!cell.isFree()){
			cell.getBall().setRadius(radius);
		}
	}

	public static void markAsSelected(CellNode cell){
		GameController.getInstance().setSourceCell(cell);
		cell.setFill(Color.CORAL);
		resizeBall(cell, 28.0);
	}

	public static void unMarkAsSelected(CellNode cell){
		CellNode sourceCell = GameController.getInstance().getSourceCell();
		if(sourceCell != null && sourceCell.equals(cell)){
			GameController.getInstance().setSourceCell(null);
		}
		cell.setFill(Color.CORNFLOWERBLUE);
		resizeBall(cell, 25.0);
	}
}
