package net.jockx.kulki.controller;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import net.jockx.kulki.view.shapes.CellNode;

public class EventHandlers {

    public static EventHandler<MouseEvent> onMouseOver = event -> {
        CellNode source = ((CellNode) event.getSource());
        GameController.getInstance().setTargetCell(source);
        source.markHovered();
        GameController.getInstance().highlightPath();
    };

    public static EventHandler<MouseEvent> onMouseAway = event -> {
        GameController.getInstance().setTargetCell(null);
        CellNode source = ((CellNode) event.getSource());
        source.unMarkHovered();
    };

    public static EventHandler<MouseEvent> onMouseOverNext = event -> {
        CellNode source = ((CellNode) event.getSource());
        source.markHovered();
    };

    public static EventHandler<MouseEvent> onMouseAwayNext = event -> {
        CellNode source = ((CellNode) event.getSource());
        source.unMarkHovered();
    };

    public static EventHandler<MouseEvent> onClick = event -> {
        CellNode cellClicked = ((CellNode) event.getSource());
        GameController.getInstance().onCellClick(cellClicked);
    };
}
