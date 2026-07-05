package net.jockx.kulki.model;

import java.util.HashSet;
import java.util.Set;

public class MatchFinder {

	static enum MatchDirection {
		HORIZONTAL, VERTICAL, MAIN_DIAGONAL, ANTI_DIAGONAL
	}

	private final RuleSet ruleSet;

	MatchFinder (Board board){
		this.ruleSet = board.getRuleSet();
	}

	Set<Cell> getMatchingLine(Set<Cell> matchedList, Cell toMatch, MatchDirection direction){
		if ( toMatch == null || toMatch.isFree() ) {
			return matchedList;
		}

		if (matchedList == null){
			matchedList = new HashSet<>();
		}

		if (matchedList.isEmpty()){
			matchedList.add(toMatch);
		}

		BallColor color = matchedList.iterator().next().getBall().getColor();
		if ( toMatch.getBall().getColor() != color ) {
			return matchedList;
		}

		if(!matchedList.contains(toMatch)){
			matchedList.add(toMatch);
		}
		switch (direction){
			case HORIZONTAL:
				if(!matchedList.contains(toMatch.left)){
					matchedList = getMatchingLine(matchedList, toMatch.left, direction);
				}
				if(!matchedList.contains(toMatch.right)){
					matchedList = getMatchingLine(matchedList, toMatch.right, direction);
				}
				break;
			case VERTICAL:
				if(!matchedList.contains(toMatch.up)){
					matchedList = getMatchingLine(matchedList, toMatch.up, direction);
				}
				if(!matchedList.contains(toMatch.down)){
					matchedList = getMatchingLine(matchedList, toMatch.down, direction);
				}
				break;
			case MAIN_DIAGONAL:
				if(!matchedList.contains(toMatch.upLeft)){
					matchedList = getMatchingLine(matchedList, toMatch.upLeft, direction);
				}
				if(!matchedList.contains(toMatch.downRight)){
					matchedList = getMatchingLine(matchedList, toMatch.downRight, direction);
				}
				break;
			case ANTI_DIAGONAL:
				if(!matchedList.contains(toMatch.upRight)){
					matchedList = getMatchingLine(matchedList, toMatch.upRight, direction);
				}
				if(!matchedList.contains(toMatch.downLeft)){
					matchedList = getMatchingLine(matchedList, toMatch.downLeft, direction);
				}
				break;
		}
		return matchedList;
	}

	public Set<Cell> getAllMatchingLines(Cell toMatch){
		if (toMatch.getBall() == null){
			return null;
		}
		Set<Cell> cellsInLines = new HashSet<>();
		for (MatchDirection direction : MatchDirection.values()) {

			if( (!ruleSet.isDiagonalMatchAllowed) &&
					(direction == MatchDirection.ANTI_DIAGONAL ||
							direction == MatchDirection.MAIN_DIAGONAL ) ){
				continue;
			}

			Set<Cell> singleLine = (getMatchingLine(null, toMatch, direction));
			if ( singleLine.size() >= ruleSet.minimalMatch ){
				cellsInLines.addAll(singleLine);
			}
		}

		return cellsInLines;
	}
}
