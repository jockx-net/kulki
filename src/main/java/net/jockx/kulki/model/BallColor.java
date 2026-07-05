package net.jockx.kulki.model;

import java.util.List;

public enum BallColor {
    RED,
    GREEN,
    BLUE,
    YELLOW,
    MAGENTA,
    CYAN,
    ORANGE,
    PINK,
    WHITE,
    BLACK,
    GRAY,
    BROWN,
    KHAKI;

    public static final List<BallColor> DEFAULT_COLORS = List.of(
            RED, GREEN, BLUE, YELLOW, MAGENTA, CYAN
    );

    public static BallColor fromIndex(int i) {
        return values()[i];
    }
}
