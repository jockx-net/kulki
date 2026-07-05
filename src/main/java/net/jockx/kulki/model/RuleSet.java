package net.jockx.kulki.model;

import java.util.Arrays;
import java.util.List;

public class RuleSet {

    int boardSize = 9;
    int minimalMatch = 5;
    int newBallCount = 3;
    int numberOfColors = 6;
    int perBallScore = 20;
    int perLineScoreModifier = 2;

    boolean isDiagonalMatchAllowed = true;
    boolean isSimultaneousMatchAllowed = true;

    final static List<BallColor> colorList = Arrays.asList(
            BallColor.RED,
            BallColor.GREEN,
            BallColor.BLUE,
            BallColor.YELLOW,
            BallColor.MAGENTA,
            BallColor.CYAN,
            BallColor.ORANGE,
            BallColor.PINK,
            BallColor.WHITE,
            BallColor.BLACK,
            BallColor.GRAY,
            BallColor.BROWN,
            BallColor.KHAKI );

    public RuleSet setBoardSize(int boardSize){
        this.boardSize = boardSize;
        return this;
    }

    public RuleSet setMinimalMatch(int minimalMatch){
        this.minimalMatch = minimalMatch;
        return this;
    }

    public RuleSet setDiagonalMatchAllowed (boolean allowed){
        this.isDiagonalMatchAllowed = allowed;
        return this;
    }

    public RuleSet setSimultaneousMatchAllowed(boolean allowed){
        this.isSimultaneousMatchAllowed = allowed;
        return this;
    }

    public RuleSet setNewBallCount(int newBallCount){
        this.newBallCount = newBallCount;
        return this;
    }

    public RuleSet setNumberOfColors(int numberOfColors){
        this.numberOfColors = numberOfColors;
        return this;
    }

    public RuleSet setPerLineScoreModifier(int perLineScoreModifier) {
        this.perLineScoreModifier = perLineScoreModifier;
        return this;
    }

    public RuleSet setPerBallScore(int perBallScore){
        this.perBallScore = perBallScore;
        return this;
    }

    public static List<BallColor> getColorList() {
        return colorList;
    }

    public static BallColor getColor(int i) {
        return colorList.get(i);
    }

    public int getNewBallCount() {
        return newBallCount;
    }
}
