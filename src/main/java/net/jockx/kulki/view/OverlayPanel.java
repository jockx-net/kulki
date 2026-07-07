package net.jockx.kulki.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public abstract class OverlayPanel {
    protected final Pane parent;
    protected final Pane overlay;
    protected final VBox panel;
    protected final double designWidth;
    protected final double designHeight;
    protected final double panelHeightRatio;

    protected OverlayPanel(Pane parent, double designWidth, double designHeight, double panelHeightRatio) {
        this.parent = parent;
        this.designWidth = designWidth;
        this.designHeight = designHeight;
        this.panelHeightRatio = panelHeightRatio;

        overlay = new Pane();
        overlay.getStyleClass().add("dialog-overlay");

        panel = new VBox();
        panel.setFillWidth(true);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("dialog-panel");
        panel.setMinSize(designWidth, designHeight);
        panel.setPrefSize(designWidth, designHeight);
        panel.setMaxSize(designWidth, designHeight);

        overlay.getChildren().add(panel);

        parent.widthProperty().addListener((_, _, _) -> reposition());
        parent.heightProperty().addListener((_, _, _) -> reposition());
    }

    public Pane getOverlay() {
        return overlay;
    }

    public void show() {
        if (!parent.getChildren().contains(overlay)) {
            parent.getChildren().add(overlay);
        }
        Platform.runLater(this::reposition);
    }

    public void hide() {
        parent.getChildren().remove(overlay);
    }

    protected void reposition() {
        double pw = parent.getWidth();
        double ph = parent.getHeight();
        if (pw <= 0 || ph <= 0) return;

        overlay.setLayoutX(0);
        overlay.setLayoutY(0);
        overlay.setPrefSize(pw, ph);
        overlay.setMinSize(pw, ph);
        overlay.setMaxSize(pw, ph);

        double scale = ph * panelHeightRatio / designHeight;
        if (designWidth * scale > pw) {
            scale = pw / designWidth;
        }

        panel.setScaleX(scale);
        panel.setScaleY(scale);
        panel.setLayoutX((pw - designWidth) / 2);
        panel.setLayoutY((ph - designHeight) / 2);
    }

    protected static Button createButton(String text, Runnable action, String... styleClasses) {
        Button btn = new Button(text);
        btn.setFocusTraversable(false);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().addAll(styleClasses);
        btn.setOnAction(_ -> action.run());
        return btn;
    }
}
