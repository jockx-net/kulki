package net.jockx.kulki.view.shapes;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
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
import net.jockx.kulki.model.Ball;
import net.jockx.kulki.model.BallColor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BallShape extends Circle {

    private static final Map<BallColor, Color> FX_COLORS = Map.ofEntries(
            Map.entry(BallColor.RED, Color.RED),
            Map.entry(BallColor.GREEN, Color.GREEN),
            Map.entry(BallColor.BLUE, Color.BLUE),
            Map.entry(BallColor.YELLOW, Color.YELLOW),
            Map.entry(BallColor.MAGENTA, Color.MAGENTA),
            Map.entry(BallColor.CYAN, Color.CYAN),
            Map.entry(BallColor.ORANGE, Color.ORANGE),
            Map.entry(BallColor.PINK, Color.PINK),
            Map.entry(BallColor.WHITE, Color.WHITE),
            Map.entry(BallColor.BLACK, Color.BLACK),
            Map.entry(BallColor.GRAY, Color.GRAY),
            Map.entry(BallColor.BROWN, Color.BROWN),
            Map.entry(BallColor.KHAKI, Color.KHAKI)
    );

    private static Color toFx(BallColor ballColor) {
        return FX_COLORS.get(ballColor);
    }

    private final Ball ball;
    private PathTransition pathTransition;
    private Runnable moveFinishedCallback;

    public BallShape(Ball ball, double radius) {
        super(radius);
        this.ball = ball;
        setFill(createGradient(ball, radius));
        this.setMouseTransparent(true);
    }

    private static RadialGradient createGradient(Ball ball, double r) {
        Color fxColor = toFx(ball.getColor());
        return new RadialGradient(
                0, 0.1, -r * 0.35, -r * 0.35, r * 1.2, false,
                CycleMethod.NO_CYCLE,
                new Stop(1, fxColor.darker().darker().darker()),
                new Stop(0.7, fxColor.darker()),
                new Stop(0.25, fxColor),
                new Stop(0, Color.WHITE));
    }

    public void updateGradient(double newRadius) {
        setRadius(newRadius);
        setFill(createGradient(ball, newRadius));
    }

    public void moveTo(List<CellNode> cellsInPath, Runnable onFinished) {
        Path path = new Path();
        CellNode lastNode = cellsInPath.get(cellsInPath.size() - 1);
        double oldLayoutX = getLayoutX();
        double oldLayoutY = getLayoutY();
        setLayoutX(lastNode.getBallCenterX());
        setLayoutY(lastNode.getBallCenterY());

        MoveTo start = new MoveTo(oldLayoutX - getLayoutX(), oldLayoutY - getLayoutY());
        path.getElements().add(start);

        for (CellNode cell : cellsInPath) {
            LineTo relativeLine = new LineTo(cell.getBallCenterX() - getLayoutX(), cell.getBallCenterY() - getLayoutY());
            path.getElements().add(relativeLine);
        }

        moveFinishedCallback = onFinished;
        PathTransition pathTransition = new PathTransition();
        int transitionDuration = 200 * cellsInPath.size();
        pathTransition.setDuration(Duration.millis(transitionDuration));
        pathTransition.setPath(path);
        pathTransition.setNode(this);
        pathTransition.setOrientation(PathTransition.OrientationType.NONE);
        pathTransition.setOnFinished(event -> {
            this.pathTransition = null;
            this.moveFinishedCallback = null;
            if (onFinished != null) {
                onFinished.run();
            }
        });
        this.pathTransition = pathTransition;
        pathTransition.play();
    }

    public void stopAnimation() {
        if (pathTransition != null) {
            pathTransition.stop();
            pathTransition = null;
            setTranslateX(0);
            setTranslateY(0);
            if (moveFinishedCallback != null) {
                Runnable cb = moveFinishedCallback;
                moveFinishedCallback = null;
                cb.run();
            }
        }
    }

    public static int remove(Collection<BallShape> balls, boolean gameOver, Pane mainBoardPane, Runnable onFinished) {
        int duration = 500;
        ParallelTransition parallelTransition = new ParallelTransition();
        if (!gameOver && onFinished != null) {
            parallelTransition.setOnFinished(event -> onFinished.run());
        }

        for (final BallShape ball : balls) {
            ScaleTransition st = new ScaleTransition(Duration.millis(duration), ball);
            st.setByX(-0.5f);
            st.setByY(-0.5f);

            FadeTransition ft = new FadeTransition(Duration.millis(duration), ball);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(event -> {
                mainBoardPane.getChildren().remove(ball);
            });
            parallelTransition.getChildren().add(st);
            parallelTransition.getChildren().add(ft);
        }
        parallelTransition.play();
        return duration;
    }

    public static void appearNewBalls(List<BallShape> balls, int actuallyShown, Pane mainBoardPane, Runnable onFinished) {
        int duration = 300;
        SequentialTransition sequentialFade = new SequentialTransition();
        SequentialTransition sequentialScale = new SequentialTransition();
        if (onFinished != null) {
            sequentialFade.setOnFinished(event -> onFinished.run());
        }

        for (int i = 0; i < actuallyShown; i++) {
            BallShape ball = balls.get(i);
            mainBoardPane.getChildren().add(ball);
            ball.setScaleX(0.01f);
            ball.setScaleY(0.01f);

            FadeTransition hide = new FadeTransition(Duration.millis(0), ball);
            hide.setFromValue(0.0);
            hide.setToValue(0.0);
            hide.play();

            FadeTransition ft = new FadeTransition(Duration.millis(duration + 10), ball);
            ft.setFromValue(0.01);
            ft.setToValue(1.0);

            ScaleTransition st = new ScaleTransition(Duration.millis(duration), ball);
            st.setToX(1.0);
            st.setToY(1.0);

            sequentialScale.getChildren().add(st);
            sequentialFade.getChildren().add(ft);
        }
        sequentialFade.play();
        sequentialScale.play();
    }

    public static void appearNext(List<BallShape> nextBalls, Pane nextBallsPane) {
        int duration = 300;
        SequentialTransition sequentialFade = new SequentialTransition();
        SequentialTransition sequentialScale = new SequentialTransition();

        for (int i = 0; i < nextBallsPane.getChildren().size(); i++) {
            final CellNode node = (CellNode) nextBallsPane.getChildren().get(i);

            if (node.getChildren().size() < 2) {
                continue;
            }
            Node ball = node.getChildren().get(1);
            ScaleTransition st = new ScaleTransition(Duration.millis(duration), ball);
            st.setByX(-0.5f);
            st.setByY(-0.5f);

            FadeTransition ft = new FadeTransition(Duration.millis(duration), ball);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);

            ft.setOnFinished(event -> node.getChildren().remove(1));
            sequentialScale.getChildren().add(st);
            sequentialFade.getChildren().add(ft);
            node.getChildren().removeAll();
        }

        for (int i = 0; i < nextBalls.size(); i++) {
            BallShape ball = nextBalls.get(i);
            CellNode node = (CellNode) nextBallsPane.getChildren().get(i);
            node.getChildren().add(ball);
            ball.setVisible(true);
            ball.setScaleX(0.01f);
            ball.setScaleY(0.01f);

            FadeTransition hide = new FadeTransition(Duration.millis(0), ball);
            hide.setFromValue(0.0);
            hide.setToValue(0.0);
            hide.play();

            FadeTransition ft = new FadeTransition(Duration.millis(100), ball);
            ft.setFromValue(0.01);
            ft.setToValue(1.0);

            ScaleTransition st = new ScaleTransition(Duration.millis(100), ball);
            st.setToX(1.0);
            st.setToY(1.0);

            sequentialScale.getChildren().add(st);
            sequentialFade.getChildren().add(ft);
        }
        sequentialFade.play();
        sequentialScale.play();
    }
}
