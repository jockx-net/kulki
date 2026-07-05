package net.jockx.kulki.model;

public class Ball {
	private final BallColor color;
	public Ball(BallColor color) {
		this.color = color;
	}

	public Ball (){
		this(BallColor.WHITE);
	}

	public BallColor getColor() {
		return color;
	}
}
