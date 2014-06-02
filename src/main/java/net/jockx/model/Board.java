package net.jockx.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by JockX on 2014-05-09.
 *
 */
public class Board {

	public final int height;
	public final int width;

	private final PathFinder pathFinder;
	private final MatchFinder matchFinder;

	private RuleSet ruleSet;
	private Cell[][] cells;


	public Board () {
		this(new RuleSet());
	}

	public Board (RuleSet ruleSet) {
		this.ruleSet =ruleSet;
		this.height = ruleSet.boardHeight;
		this.width = ruleSet.boardWidth;
		this.cells = Cell.createCellArray(width, height);
		this.pathFinder = new PathFinder(this);
		this.matchFinder = new MatchFinder(this);
	}

	public RuleSet getRuleSet() {
		return ruleSet;
	}

	public Cell getCell(int x, int y) {
		return cells[x][y];
	}

	public void placeBall(Ball ball, Cell cell){
		cell.setBall(ball);
	}

	public void placeBall(Ball ball, int x, int y){
		placeBall(ball, cells[x][y]);
	}

	public List<Cell> getFreeCells() {
		List<Cell> freeCells = new LinkedList<Cell>();
		for (int i = 0; i < height; i++){
			for (int j = 0; j < width; j++){
				Cell cell = getCell(j, i);
				if (cell.isFree()){
					freeCells.add(cell);
				}
			}
		}
		return freeCells;
	}

	public LinkedList<Cell> findShortestPathToCell(Cell from, Cell to) {
		return pathFinder.findShortestPathToCell(from, to);
	}

	public Set<Cell> getAllMatchingLines(Cell cell) {
		return matchFinder.getAllMatchingLines(cell);
	}

	public List<Cell> moveBallToCell(Cell from, Cell to){
		Ball ball = from.getBall();
		if(ball == null){
			return null;
		}
		LinkedList<Cell> path = findShortestPathToCell(from, to);
		if(!path.isEmpty()){
			from.setBall(null);
			to.setBall(ball);
			return path;
		} else {
			return null;
		}

	}


}
