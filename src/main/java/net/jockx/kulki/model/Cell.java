package net.jockx.kulki.model;

/**
 * A Cell is a board field that can hold single Ball and
 * has links to neighbouring Cells
 */
public class Cell {
	Cell up, down, left, right,
		 upLeft, upRight, downLeft, downRight;

	public final int x, y;

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

	public boolean isFree() {
		return (getBall() == null);
	}
}
