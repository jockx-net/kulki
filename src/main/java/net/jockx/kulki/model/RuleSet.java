package net.jockx.kulki.model;

import java.util.List;

public class RuleSet {

    int boardSize = 9;
    int minimalMatch = 5;
    int newBallCount = 3;
    int numberOfColors = 6;
    int perBallScore = 20;

    boolean isDiagonalMatchAllowed = true;

    static final List<BallColor> colorList = List.of(BallColor.values());

    public RuleSet setBoardSize(int boardSize){
        this.boardSize = boardSize;
        return this;
    }

    public RuleSet setMinimalMatch(int minimalMatch){
        this.minimalMatch = minimalMatch;
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

    public RuleSet setPerBallScore(int perBallScore){
        this.perBallScore = perBallScore;
        return this;
    }

    public static BallColor getColor(int i) {
        return colorList.get(i);
    }
}
