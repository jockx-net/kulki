package net.jockx.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JockX on 2014-05-11.
 *
 */
public class PathFinder {

	private final Board board;

	PathFinder(Board board){
		this.board = board;
	}

	LinkedList<Cell> findPathToCell(Cell from, Cell to, LinkedList<Cell> path, LinkedList<Cell> verified ){
		if(verified == null){
			verified = new LinkedList<Cell>();
		}
		if(!verified.contains(from)) {
			verified.add(from);

			// Uncomment to see the visual
			//PathFinderPrint.printPath(verified, board, 300);
		}

		Cell[] neighbours = getSmartNeighbours(from, to);

		for (Cell next : neighbours){
			LinkedList<Cell> tryPath = new LinkedList<Cell>(path);
			tryPath.add(from);

			if (tryPath.contains(to)){
				return tryPath;
			}

			if (next == null || !next.isFree() || verified.contains(next)){
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
		Arrays.sort(neighbours, new Comparator<Cell>() {
			@Override
			public int compare(Cell o1, Cell o2) {
				double d1 = getDistanceEuclidean(o1, to);
				double d2 = getDistanceEuclidean(o2, to);
				if (d1 > d2) {
					return 1;
				} else if (d1 < d2) {
					return -1;
				} else {
					return 0;
				}

			}
		});

		return neighbours;
	}

	private void removeCells (LinkedList<Cell> listToRemoveFrom, List<Cell> cellToRemove){
		for(Cell c : cellToRemove){
			listToRemoveFrom.remove(c);
		}
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
}
