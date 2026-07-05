package net.jockx.kulki.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public record StateTransition(
		Cell source,
		Cell target,
		List<Cell> path,
		Set<Cell> matchedCells,
		List<Cell> placedCells,
		List<Ball> placedBalls,
		int scoreDelta,
		boolean gameOver
) {
	public boolean hasPath() {
		return path != null && !path.isEmpty();
	}

	public boolean hasMatches() {
		return matchedCells != null && !matchedCells.isEmpty();
	}

	public boolean hasPlacedBalls() {
		return placedCells != null && !placedCells.isEmpty();
	}

	public static StateTransition moveResult(Cell source, Cell target, List<Cell> path) {
		return new StateTransition(source, target, path, null, null, null, 0, false);
	}

	public static StateTransition matchResult(Set<Cell> matchedCells, int scoreDelta) {
		return new StateTransition(null, null, Collections.emptyList(), matchedCells, null, null, scoreDelta, false);
	}

	public static StateTransition ballsPlaced(List<Cell> cells, List<Ball> balls) {
		return new StateTransition(null, null, Collections.emptyList(), null, cells, balls, 0, false);
	}

	public static StateTransition empty() {
		return new StateTransition(null, null, Collections.emptyList(), null, null, null, 0, false);
	}

	public static StateTransition gameOverResult() {
		return new StateTransition(null, null, Collections.emptyList(), null, null, null, 0, true);
	}
}
