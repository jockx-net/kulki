package net.jockx.kulki.controller;

import net.jockx.kulki.model.*;

public class GameLoop {

	private final GameEngine engine;
	private final GameEventBus bus;

	private Cell lastMoveTarget;

	public GameLoop(GameEngine engine, GameEventBus bus) {
		this.engine = engine;
		this.bus = bus;
	}

	public boolean submitMove(Cell from, Cell to) {
		StateTransition result = engine.tryMove(from, to);
		if (!result.hasPath()) {
			return false;
		}
		lastMoveTarget = to;
		bus.publish(GameEvent.BALL_MOVED, result);
		return true;
	}

	public void submitInitialPlacement() {
		StateTransition placed = engine.placeNextBalls();
		engine.generateNextBalls();
		bus.publish(GameEvent.BALLS_PLACED, placed);
	}

	public void onMoveAnimationFinished() {
		StateTransition result;
		if (lastMoveTarget != null && !lastMoveTarget.isFree()) {
			result = engine.checkMatches(lastMoveTarget);
		} else {
			result = StateTransition.empty();
		}
		lastMoveTarget = null;

		if (result.hasMatches()) {
			bus.publish(GameEvent.MATCHES_IDENTIFIED, result);
		} else {
			doPlaceBalls();
		}
	}

	public void onRemoveAnimationFinished() {
		engine.finalizeRemoval();

		if (engine.isGameOver()) {
			bus.publish(GameEvent.GAME_OVER, StateTransition.gameOverResult());
			return;
		}

		if (engine.getBoard().getBalls().isEmpty()) {
			doPlaceBalls();
			return;
		}

		StateTransition result = engine.checkAllMatches();
		if (result.hasMatches()) {
			bus.publish(GameEvent.MATCHES_IDENTIFIED, result);
		} else {
			bus.publish(GameEvent.TURN_COMPLETE, StateTransition.empty());
		}
	}

	public void onAppearAnimationFinished() {
		StateTransition result = engine.checkAllMatches();
		if (result.hasMatches()) {
			bus.publish(GameEvent.MATCHES_IDENTIFIED, result);
		} else if (engine.isGameOver()) {
			bus.publish(GameEvent.GAME_OVER, StateTransition.gameOverResult());
		} else {
			bus.publish(GameEvent.TURN_COMPLETE, StateTransition.empty());
		}
	}

	private void doPlaceBalls() {
		StateTransition result = engine.placeNextBalls();
		engine.generateNextBalls();
		bus.publish(GameEvent.BALLS_PLACED, result);
	}
}
