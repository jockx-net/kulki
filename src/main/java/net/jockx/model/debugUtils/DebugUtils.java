package net.jockx.model.debugUtils;

import net.jockx.model.Board;
import net.jockx.model.Cell;

import java.util.LinkedList;

/**
 * Created by JockX on 2014-06-02.
 *
 */
@SuppressWarnings("UnusedDeclaration")
public class DebugUtils {
	/**
	 * Uncomment to see the progress in real time
	 *
	 * */
	public static void printPath(LinkedList<Cell> path, Board board, int delay) {
		System.out.println("\nPath: ");
		if (path == null)
			return;
		for (int i = 0; i < board.height; i++) {
			for (int j = 0; j < board.width; j++) {
				Cell c = board.getCell(j, i);
				if (path.contains(c)) {
					int n = path.indexOf(c);
					String s = Integer.toString(n);
					if (n < 10)
						s = " " + s;
					System.out.print("|" + s);
				} else if (c.getBall() != null) {
					System.out.print("| X");
				} else {
					System.out.print("|  ");
				}
			}
			System.out.println("|");
		}
		System.out.println();
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void  printBoard(Board board){
		for (int i = 0; i < board.height; i++){
			for (int j = 0; j < board.width; j++){
				String c;
				if (board.getCell(j, i).isFree()){
					c = " .";
				} else {
					c = " X";
				}
				System.out.print(c);
			}
			System.out.println();
		}
	}
}
