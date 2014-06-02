package net.jockx.view;

import javafx.animation.PathTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import net.jockx.model.Ball;

import java.util.List;

/**
 * Created by JockX on 2014-05-13.
 *
 */
public class BallShape  extends Circle{

	public static Pane pane;

	public BallShape(Ball ball){
		super(25.0);
		RadialGradient gradient = new RadialGradient(
				0, 0.1, -10, -10, 30, false,
				CycleMethod.NO_CYCLE,
				new Stop(1,  ball.getColor().darker().darker().darker()),
				new Stop(0.7, ball.getColor().darker()),
				new Stop(0.25, ball.getColor()),
				new Stop(0,  Color.WHITE ));
		this.setFill(gradient);
		this.setMouseTransparent(true);
	}

	public void moveTo(List<CellNode> cellsInPath){

		Path path = new Path();
		CellNode lastNode = cellsInPath.get(cellsInPath.size()-1);
		double oldLayoutX = getLayoutX();
		double oldLayoutY = getLayoutY();
		setLayoutX(lastNode.moveToPosition.getX());
		setLayoutY(lastNode.moveToPosition.getY());

		MoveTo start = new MoveTo(oldLayoutX - getLayoutX(), oldLayoutY - getLayoutY());
		path.getElements().add(start);

		for (CellNode cell : cellsInPath){
			LineTo relativeLine = new LineTo(cell.moveToPosition.getX() -  getLayoutX(), cell.moveToPosition.getY() - getLayoutY());
			path.getElements().add(relativeLine);
		}

		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.millis(200 * cellsInPath.size()));
		pathTransition.setPath(path);

		pathTransition.setNode(this);
		pathTransition.setOrientation(PathTransition.OrientationType.NONE);
		pathTransition.play();
	}

}
