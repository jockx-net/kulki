package net.jockx.model;

import java.awt.*;

/**
 * Created by Mateusz on 2014-05-09.
 */
public class Ball {
	private final Color color;
	public Ball(Color color) {
		this.color = color;
	}

	public Ball (){
		this(Color.WHITE);
	}

	public Color getColor() {
		return color;
	}
}
