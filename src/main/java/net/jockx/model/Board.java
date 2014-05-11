package net.jockx.model;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by JockX on 2014-05-09.
 *
 */
public class Board {

	public RuleSet getRuleSet() {
		return ruleSet;
	}

	public Cell getCell(int x, int y) {
		return cells[x][y];
	}

	enum MatchDirection {
		HORIZONTAL, VERTICAL, MAIN_DIAGONAL, ANTI_DIAGONAL
	}

	/**
	 * A Cell is a board field that can hold single Ball and
	 * has links to neighbouring Cells
	 */
	public static class Cell{
		Cell up, down, left, right,
			 upLeft, upRight, downLeft, downRight;

		final int x, y;

		public Cell(int x, int y){
			this.x = x;
			this.y = y;
		}
		private Ball ball;

		static Cell createCells(int x, int y, Cell[][] grid){

			Cell me;
			try {
				if(grid[x][y] == null){
					grid[x][y] = new Cell(x,y);
					me = grid[x][y];
				} else {
					return grid[x][y];
				}
			} catch (ArrayIndexOutOfBoundsException e){
				return null;
			}

			me.left      = createCells(x - 1, y,     grid);
			me.right     = createCells(x + 1, y,     grid);
			me.up        = createCells(x    , y - 1, grid);
			me.down      = createCells(x    , y + 1, grid);

			me.upLeft    = createCells(x - 1, y - 1, grid);
			me.upRight   = createCells(x + 1, y - 1, grid);
			me.downLeft  = createCells(x - 1, y + 1, grid);
			me.downRight = createCells(x + 1, y + 1, grid);
			return me;
		}

		public static Cell[][] createCellArray(int width, int height) {
			Cell[][] cells = new Cell[width][height];
			cells[0][0] = createCells(0,0, cells);
			return cells;
		}

		public Ball getBall() {
			return ball;
		}

		public void setBall(Ball ball) {
			this.ball = ball;
		}
	}

	final int height;
	final int width;
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
	}

	public void placeBall(Ball ball, Cell cell){
		cell.setBall(ball);
	}

	public void placeBall(Ball ball, int x, int y){
		placeBall(ball, cells[x][y]);
	}

	public Set<Cell> getMatchingLine(Set<Cell> matchedList, Cell toMatch, MatchDirection direction){
		// End of the  line;
		if ( toMatch == null || toMatch.getBall() == null ) {
			return matchedList;
		}

		if (matchedList == null){
			matchedList = new HashSet<Cell>();
		}

		if (matchedList.isEmpty()){
			matchedList.add(toMatch);
		}

		// Color mismatch
		Color color = matchedList.iterator().next().getBall().getColor();
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
			case MAIN_DIAGONAL: //:		\
				if(!matchedList.contains(toMatch.upLeft)){
					matchedList = getMatchingLine(matchedList, toMatch.upLeft, direction);
				}
				if(!matchedList.contains(toMatch.downRight)){
					matchedList = getMatchingLine(matchedList, toMatch.downRight, direction);
				}
				break;
			case ANTI_DIAGONAL: //:		/
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
		Set<Cell> cellsInLines = new HashSet<Cell>();
		for (MatchDirection direction : MatchDirection.values()) {

			// Diagonal lines might be ignored
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

	LinkedList<Cell> findPathToCell(Cell from, Cell to, LinkedList<Cell> path, LinkedList<Cell> verified ){
		if(verified == null){
			verified = new LinkedList<Cell>();
		}
		if(!verified.contains(from)) {
			verified.add(from);
			/*
			// Uncomment to see the visual
			 printPath(verified, this);
			 try {
			 	Thread.sleep(300);
			} catch (InterruptedException e) {
			 	e.printStackTrace();
			}
			*/

		}

		if (from.getBall() != null){
			return null;
		}

		Cell[] neighbours = getSmartNeighbours(from, to);

		for (Cell next : neighbours){
			LinkedList<Cell> tryPath = new LinkedList<Cell>(path);
			tryPath.add(from);

			if (tryPath.contains(to)){
				return tryPath;
			}

			if (next == null || next.getBall() != null || verified.contains(next)){
				continue;
			}
			if((!tryPath.contains(next))) {
				tryPath = findPathToCell(next, to, tryPath, verified);
				if(tryPath != null && tryPath.contains(to)) {
					return tryPath;
				}
			}
		}
		// Dead End
		return null;
	}

	public LinkedList<Cell> findShortestPathToCell(Cell from, Cell to){
		LinkedList<Cell> empty = new LinkedList<Cell>();
		LinkedList<Cell> path = empty;
		path = findPathToCell(from, to, path, null);
		if(path == null){
			return empty;
		}

		LinkedList<Cell> indicesToRemove = new LinkedList<Cell>();
		for (int i = 0; i < path.size(); i++){
			Cell cellFromStart = path.get(i);
			for (int j = path.size()-1; j > i ; j--){
				Cell cellFromEnd = path.get(j);
				Cell[] neighbours = getSimpleNeighbours(cellFromStart, true);
				for (Cell neighbour : neighbours){
					if(neighbour != null && neighbour.equals(cellFromEnd)){
						for(int n = i+1; n < j; j--){
							path.remove(path.get(n));
							i = n;
						}
					}
				}
			}
		}
		removeCells(path, indicesToRemove);
		return path;
	}

	private void removeCells (LinkedList<Cell> listToRemoveFrom, List<Cell> cellToRemove){
		for(Cell c : cellToRemove){
			listToRemoveFrom.remove(c);
		}
	}

	private Cell[] getSimpleNeighbours(Cell cell, boolean clockWise) {
		Cell[] neighbours =  new Cell[4];
		if(clockWise) {
			neighbours[0] = cell.up;
			neighbours[1] = cell.right;
			neighbours[2] = cell.down;
			neighbours[3] = cell.left;
		} else {
			neighbours[3] = cell.up;
			neighbours[2] = cell.right;
			neighbours[1] = cell.down;
			neighbours[0] = cell.left;
		}
		return  neighbours;
	}

	private Cell[] getSmartNeighbours(Cell from, final Cell to){
		Cell[] neighbours = getSimpleNeighbours(from, true);
		Arrays.sort(neighbours, new Comparator<Cell>(){
			@Override
			public int compare(Cell o1, Cell o2) {
				double d1 = getDistanceEuclidean(o1, to);
				double d2 = getDistanceEuclidean(o2, to);
				if ( d1 > d2 ){
					return 1;
				} else if (d1 < d2){
					return -1;
				} else {
					return 0;
				}

			}
		});

		return neighbours;
	}

	/**
	private int getDistanceManhattan (Cell from, Cell to){
		if(from == null){
			return Integer.MAX_VALUE;
		} else return Math.abs(to.x - from.x) + Math.abs(to.y - from.y);
	}
	*/

	private double getDistanceEuclidean (Cell from , Cell to){
		if(from == null){
			return Double.MAX_VALUE;
		} else return Math.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.y - from.y, 2));
	}


	/**
	 * Uncomment to see the progress in real time
	 * @param path
	 * @param board

	public void printPath(LinkedList<Board.Cell> path, Board board){
		System.out.println("\nPath: ");
		if(path == null)
			return;
		for (int i = 0; i < board.height; i++){
			for (int j = 0; j < board.width; j++){
				Board.Cell c = board.getCell(j, i);
				if (path.contains(c)){
					int n = path.indexOf(c);
					String s = Integer.toString(n);
					if(n < 10)
						s = " " + s;
					System.out.print("|" + s);
				}
				else if (c.getBall() != null){
					System.out.print("| X");
				}
				else {
					System.out.print("|  ");
				}
			}
			System.out.println("|");
		}
		//printSet("\nIn path: ", path);

		System.out.println();
	}
	//*/
}
