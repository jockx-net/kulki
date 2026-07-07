package net.jockx.kulki.model;

public record Ball(BallColor color) {

    public Ball() {
        this(BallColor.WHITE);
    }
}
