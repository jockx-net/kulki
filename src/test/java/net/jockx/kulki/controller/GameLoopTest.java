package net.jockx.kulki.controller;

import net.jockx.kulki.model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class GameLoopTest {

	@Test
	void submitMoveReturnsFalseWhenNoPath() {
		FakeEngine engine = new FakeEngine();
		engine.tryMoveReturnsPath = false;
		GameEventBus bus = new GameEventBus();
		GameLoop loop = new GameLoop(engine, bus);

		Cell from = engine.board.getCell(0, 0);
		Cell to = engine.board.getCell(1, 1);

		AtomicReference<StateTransition> captured = capturePublishedEvent(bus, GameEvent.BALL_MOVED);
		boolean result = loop.submitMove(from, to);

		assertFalse(result);
		assertTrue(engine.tryMoveCalled);
		assertNull(captured.get());
	}

	@Test
	void submitMovePublishesBallMovedAndReturnsTrue() {
		FakeEngine engine = new FakeEngine();
		engine.tryMoveReturnsPath = true;
		GameEventBus bus = new GameEventBus();
		GameLoop loop = new GameLoop(engine, bus);

		Cell from = engine.board.getCell(0, 0);
		from.setBall(new Ball(BallColor.RED));
		Cell to = engine.board.getCell(1, 1);

		AtomicReference<StateTransition> captured = capturePublishedEvent(bus, GameEvent.BALL_MOVED);
		boolean result = loop.submitMove(from, to);

		assertTrue(result);
		assertTrue(engine.tryMoveCalled);
		assertNotNull(captured.get());
		assertTrue(captured.get().hasPath());
	}

	@Test
	void onMoveAnimationFinishedWithMatchPublishesMatchesIdentified() {
		FakeEngine engine = new FakeEngine();
		engine.tryMoveReturnsPath = true;
		engine.checkMatchesReturnsMatch = true;
		GameEventBus bus = new GameEventBus();
		GameLoop loop = new GameLoop(engine, bus);

		Cell from = engine.board.getCell(0, 0);
		from.setBall(new Ball(BallColor.RED));
		Cell to = engine.board.getCell(1, 1);

		loop.submitMove(from, to);

		AtomicReference<StateTransition> captured = capturePublishedEvent(bus, GameEvent.MATCHES_IDENTIFIED);
		loop.onMoveAnimationFinished();

		assertTrue(engine.checkMatchesCalled);
		assertNotNull(captured.get());
		assertTrue(captured.get().hasMatches());
	}

	@Test
	void onMoveAnimationFinishedWithoutMatchPublishesBallsPlaced() {
		FakeEngine engine = new FakeEngine();
		engine.tryMoveReturnsPath = true;
		engine.checkMatchesReturnsMatch = false;
		GameEventBus bus = new GameEventBus();
		GameLoop loop = new GameLoop(engine, bus);

		Cell from = engine.board.getCell(0, 0);
		from.setBall(new Ball(BallColor.RED));
		Cell to = engine.board.getCell(1, 1);

		loop.submitMove(from, to);

		AtomicReference<StateTransition> captured = capturePublishedEvent(bus, GameEvent.BALLS_PLACED);
		loop.onMoveAnimationFinished();

		assertTrue(engine.checkMatchesCalled);
		assertTrue(engine.placeNextBallsCalled);
		assertTrue(engine.generateNextBallsCalled);
		assertNotNull(captured.get());
	}

	@Test
	void onMoveAnimationFinishedWhenLastMoveTargetNullDoesNotCheckMatches() {
		FakeEngine engine = new FakeEngine();
		GameEventBus bus = new GameEventBus();
		GameLoop loop = new GameLoop(engine, bus);

		loop.onMoveAnimationFinished();

		assertFalse(engine.checkMatchesCalled);
		assertTrue(engine.placeNextBallsCalled);
		assertTrue(engine.generateNextBallsCalled);
	}

	@Test
	void onRemoveAnimationFinishedGameOverPublishesGameOver() {
		FakeEngine engine = new FakeEngine();
		engine.isGameOverValue = true;
		GameEventBus bus = new GameEventBus();
		GameLoop loop = new GameLoop(engine, bus);

		AtomicReference<StateTransition> captured = capturePublishedEvent(bus, GameEvent.GAME_OVER);
		loop.onRemoveAnimationFinished();

		assertTrue(engine.finalizeRemovalCalled);
		assertTrue(engine.isGameOverCalled);
		assertNotNull(captured.get());
		assertTrue(captured.get().gameOver());
	}

	@Test
	void onRemoveAnimationFinishedBoardEmptyPublishesBallsPlaced() {
		FakeEngine engine = new FakeEngine();
		engine.isGameOverValue = false;
		engine.boardHasBalls = false;
		GameEventBus bus = new GameEventBus();
		GameLoop loop = new GameLoop(engine, bus);

		AtomicReference<StateTransition> captured = capturePublishedEvent(bus, GameEvent.BALLS_PLACED);
		loop.onRemoveAnimationFinished();

		assertTrue(engine.finalizeRemovalCalled);
		assertTrue(engine.isGameOverCalled);
		assertTrue(engine.getBoardCalled);
		assertTrue(engine.placeNextBallsCalled);
		assertTrue(engine.generateNextBallsCalled);
		assertNotNull(captured.get());
	}

	@Test
	void onRemoveAnimationFinishedWithMatchPublishesMatchesIdentified() {
		FakeEngine engine = new FakeEngine();
		engine.isGameOverValue = false;
		engine.boardHasBalls = true;
		engine.checkAllMatchesReturnsMatch = true;
		GameEventBus bus = new GameEventBus();
		GameLoop loop = new GameLoop(engine, bus);

		AtomicReference<StateTransition> captured = capturePublishedEvent(bus, GameEvent.MATCHES_IDENTIFIED);
		loop.onRemoveAnimationFinished();

		assertTrue(engine.finalizeRemovalCalled);
		assertTrue(engine.isGameOverCalled);
		assertTrue(engine.checkAllMatchesCalled);
		assertNotNull(captured.get());
		assertTrue(captured.get().hasMatches());
	}

	@Test
	void onRemoveAnimationFinishedTurnCompletePublishesTurnComplete() {
		FakeEngine engine = new FakeEngine();
		engine.isGameOverValue = false;
		engine.boardHasBalls = true;
		engine.checkAllMatchesReturnsMatch = false;
		GameEventBus bus = new GameEventBus();
		GameLoop loop = new GameLoop(engine, bus);

		AtomicReference<StateTransition> captured = capturePublishedEvent(bus, GameEvent.TURN_COMPLETE);
		loop.onRemoveAnimationFinished();

		assertTrue(engine.finalizeRemovalCalled);
		assertTrue(engine.isGameOverCalled);
		assertTrue(engine.checkAllMatchesCalled);
		assertNotNull(captured.get());
	}

	@Test
	void onAppearAnimationFinishedWithMatchPublishesMatchesIdentified() {
		FakeEngine engine = new FakeEngine();
		engine.checkAllMatchesReturnsMatch = true;
		GameEventBus bus = new GameEventBus();
		GameLoop loop = new GameLoop(engine, bus);

		AtomicReference<StateTransition> captured = capturePublishedEvent(bus, GameEvent.MATCHES_IDENTIFIED);
		loop.onAppearAnimationFinished();

		assertTrue(engine.checkAllMatchesCalled);
		assertNotNull(captured.get());
		assertTrue(captured.get().hasMatches());
	}

	@Test
	void onAppearAnimationFinishedGameOverPublishesGameOver() {
		FakeEngine engine = new FakeEngine();
		engine.checkAllMatchesReturnsMatch = false;
		engine.isGameOverValue = true;
		GameEventBus bus = new GameEventBus();
		GameLoop loop = new GameLoop(engine, bus);

		AtomicReference<StateTransition> captured = capturePublishedEvent(bus, GameEvent.GAME_OVER);
		loop.onAppearAnimationFinished();

		assertTrue(engine.checkAllMatchesCalled);
		assertTrue(engine.isGameOverCalled);
		assertNotNull(captured.get());
		assertTrue(captured.get().gameOver());
	}

	@Test
	void onAppearAnimationFinishedTurnCompletePublishesTurnComplete() {
		FakeEngine engine = new FakeEngine();
		engine.checkAllMatchesReturnsMatch = false;
		engine.isGameOverValue = false;
		GameEventBus bus = new GameEventBus();
		GameLoop loop = new GameLoop(engine, bus);

		AtomicReference<StateTransition> captured = capturePublishedEvent(bus, GameEvent.TURN_COMPLETE);
		loop.onAppearAnimationFinished();

		assertTrue(engine.checkAllMatchesCalled);
		assertTrue(engine.isGameOverCalled);
		assertNotNull(captured.get());
	}

	@Test
	void submitInitialPlacementPublishesBallsPlaced() {
		FakeEngine engine = new FakeEngine();
		GameEventBus bus = new GameEventBus();
		GameLoop loop = new GameLoop(engine, bus);

		AtomicReference<StateTransition> captured = capturePublishedEvent(bus, GameEvent.BALLS_PLACED);
		loop.submitInitialPlacement();

		assertTrue(engine.placeNextBallsCalled);
		assertTrue(engine.generateNextBallsCalled);
		assertNotNull(captured.get());
	}

	private AtomicReference<StateTransition> capturePublishedEvent(GameEventBus bus, GameEvent event) {
		AtomicReference<StateTransition> captured = new AtomicReference<>();
		bus.subscribe(event, captured::set);
		return captured;
	}

	static class FakeEngine implements GameEngine {

		boolean tryMoveCalled;
		boolean checkMatchesCalled;
		boolean checkAllMatchesCalled;
		boolean finalizeRemovalCalled;
		boolean placeNextBallsCalled;
		boolean generateNextBallsCalled;
		boolean isGameOverCalled;
		boolean getBoardCalled;

		boolean tryMoveReturnsPath;
		boolean checkMatchesReturnsMatch;
		boolean checkAllMatchesReturnsMatch;
		boolean isGameOverValue;
		boolean boardHasBalls;

		final Board board = new Board(new RuleSet().setBoardSize(3, 3));
		final List<Cell> fakeCells = new ArrayList<>(List.of(new Cell(0, 0)));
		final List<Ball> fakeBalls = new ArrayList<>(List.of(new Ball(BallColor.RED)));

		@Override
		public void start() {}

		@Override
		public StateTransition tryMove(Cell from, Cell to) {
			tryMoveCalled = true;
			if (tryMoveReturnsPath) {
				to.setBall(new Ball(BallColor.RED));
				return StateTransition.moveResult(from, to, List.of(to));
			}
			return StateTransition.empty();
		}

		@Override
		public StateTransition checkMatches(Cell cell) {
			checkMatchesCalled = true;
			if (checkMatchesReturnsMatch) {
				return StateTransition.matchResult(Set.of(cell), 10);
			}
			return StateTransition.empty();
		}

		@Override
		public StateTransition finalizeRemoval() {
			finalizeRemovalCalled = true;
			return StateTransition.empty();
		}

		@Override
		public StateTransition checkAllMatches() {
			checkAllMatchesCalled = true;
			if (checkAllMatchesReturnsMatch) {
				return StateTransition.matchResult(Set.of(new Cell(0, 0)), 10);
			}
			return StateTransition.empty();
		}

		@Override
		public StateTransition placeNextBalls() {
			placeNextBallsCalled = true;
			return StateTransition.ballsPlaced(fakeCells, fakeBalls);
		}

		@Override
		public void generateNextBalls() {
			generateNextBallsCalled = true;
		}

		@Override
		public List<Ball> getNextBalls() { return null; }

		@Override
		public List<Cell> getNextCells() { return null; }

		@Override
		public boolean isGameOver() {
			isGameOverCalled = true;
			return isGameOverValue;
		}

		@Override
		public int getScore() { return 0; }

		@Override
		public Board getBoard() {
			getBoardCalled = true;
			if (boardHasBalls && board.getBalls().isEmpty()) {
				board.placeBall(new Ball(BallColor.RED), 0, 0);
			}
			return board;
		}

		@Override
		public RuleSet getRuleSet() { return null; }
	}
}
