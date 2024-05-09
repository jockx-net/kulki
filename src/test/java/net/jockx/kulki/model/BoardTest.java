package net.jockx.kulki.model;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class BoardTest {

	@Test
	public void testCreateCellArray() {
		Cell[][] cells = Cell.createCellArray(10, 10);
		for (int i = 0; i < 10; i++){
			for (int j = 0; j < 10; j++){
				Assertions.assertNotNull(cells[i][j], "Cell at x:" + i + " y: " + j);
			}
		}
	}

	@Test void testRuleSetDefaultConstructor() {
		RuleSet ruleSet = new RuleSet();
		Assertions.assertNotNull (ruleSet);
		Assertions.assertEquals(ruleSet.boardHeight, 9, "Wrong default Board height");
		Assertions.assertEquals(ruleSet.boardWidth, 9, "Wrong default Board width");
		Assertions.assertEquals(ruleSet.minimalMatch, 5, "Wrong default Board minimalMatch");
	}

	@Test void testBoardConstructor() {
		Board board = new Board();
		Assertions.assertNotNull (board);
		Assertions.assertNotNull(board.getRuleSet());
		Assertions.assertEquals(board.height, 9, "Wrong default Board height");
		Assertions.assertEquals(board.width, 9, "Wrong default Board width");
	}

	@Test
	public void testGetMatchingLine() {
		Board board = getSmallBoard();

		Set<Cell> horizontal = new HashSet<>();
		Set<Cell> vertical = new HashSet<>();
		Set<Cell> mainDiagonal = new HashSet<>();
		Set<Cell> antiDiagonal = new HashSet<>();
		Set<Cell> noneHorizontal = new HashSet<>();
		Set<Cell> noneVertical = new HashSet<>();
		Set<Cell> noneMainDiagonal = new HashSet<>();
		Set<Cell> noneAntiDiagonal = new HashSet<>();

		MatchFinder matchFinder = new MatchFinder(board);
		horizontal = matchFinder.getMatchingLine(horizontal, board.getCell(1,0), MatchFinder.MatchDirection.HORIZONTAL);
		vertical = matchFinder.getMatchingLine(vertical, board.getCell(0,2), MatchFinder.MatchDirection.VERTICAL);
		mainDiagonal = matchFinder.getMatchingLine(mainDiagonal, board.getCell(2,2), MatchFinder.MatchDirection.MAIN_DIAGONAL);
		antiDiagonal = matchFinder.getMatchingLine(antiDiagonal, board.getCell(2,1), MatchFinder.MatchDirection.ANTI_DIAGONAL);
		noneHorizontal = matchFinder.getMatchingLine(noneHorizontal, board.getCell(1,1), MatchFinder.MatchDirection.HORIZONTAL);
		noneVertical = matchFinder.getMatchingLine(noneVertical, board.getCell(1,1), MatchFinder.MatchDirection.VERTICAL);
		noneMainDiagonal = matchFinder.getMatchingLine(noneMainDiagonal, board.getCell(1,1), MatchFinder.MatchDirection.MAIN_DIAGONAL);
		noneAntiDiagonal = matchFinder.getMatchingLine(noneAntiDiagonal, board.getCell(1,1), MatchFinder.MatchDirection.ANTI_DIAGONAL);

		printSet("- [1:0]", horizontal);
		printSet("| [0:2]", vertical);
		printSet("\\ [2:2]", mainDiagonal);
		printSet("/ [2:1]", antiDiagonal);
		printSet("- [1:1]", noneHorizontal);
		printSet("| [1:1]", noneVertical);
		printSet("\\ [1:1]", noneMainDiagonal);
		printSet("/ [1:1]", noneAntiDiagonal);

		Assertions.assertEquals(horizontal.size(), 4, "Horizontal [1:0] didn't match");
		Assertions.assertEquals(vertical.size(), 3, "Diagonal [0:2] didn't match");
		Assertions.assertEquals(mainDiagonal.size(), 4, "Vertical [2:2] didn't match");
		Assertions.assertEquals(antiDiagonal.size(), 3, "Anti-diagonal  [2:1] didn't match");
		Assertions.assertEquals(noneHorizontal.size(), 1, "Horizontal [1:1] is not Empty");
		Assertions.assertEquals(noneVertical.size(), 2, "Vertical [1:1] is not Empty");
		Assertions.assertEquals(noneMainDiagonal.size(), 4, "Main-Diagonal [1:1] is not Empty");
		Assertions.assertEquals(noneAntiDiagonal.size(), 2, "Anti-Diagonal [1:1] is not Empty");
	}

	@Test
	public void testGetAllMatchingLines() {
		Board board = getSmallBoard();

		Set<Cell> match0_0 = board.getAllMatchingLines(board.getCell(0,0));
		Set<Cell> match1_1 = board.getAllMatchingLines(board.getCell(1,1));
		Set<Cell> match0_2 = board.getAllMatchingLines(board.getCell(0,2));
		Set<Cell> match2_1 = board.getAllMatchingLines(board.getCell(2,1));
		Set<Cell> match3_1 = board.getAllMatchingLines(board.getCell(3,1));
		Set<Cell> match1_3 = board.getAllMatchingLines(board.getCell(1,3));
		printSet("0:0", match0_0);
		printSet("1:1", match1_1);
		printSet("0:2", match0_2);
		printSet("2:1", match2_1);
		printSet("1:3", match1_3);
		printSet("3:1", match3_1);
		Assertions.assertEquals(match0_0.size(), 7, "Horizontal + diagonal [0:0] didn't match"); // 7 green matches
		Assertions.assertEquals(match1_1.size(), 4, "Diagonal [1:1] didn't match"); // 4 green matches
		Assertions.assertEquals(match0_2.size(), 3, "Vertical [0:2] didn't match"); // 3 blue matches
		Assertions.assertEquals(match2_1.size(), 3, "Anti-diagonal  [2:1] didn't match"); // 3 blue matches
		Assertions.assertEquals(match1_3.size(), 3, "Horizontal [1:3] didn't match"); // 3 blue matches
		Assertions.assertEquals(match3_1.size(), 0, "Empty [3:1] didn't match"); // 2 blue matches
	}

	@Test
	public void testFindPathToCell() {
		Board board = getMazeBoard();
		Cell from = board.getCell(7,6);
		Cell to1 = board.getCell(2,5);
		Cell to2 = board.getCell(5,3);
		Cell to3 = board.getCell(6,5);
		Cell to4 = board.getCell(0,0); // Unaccessible
		Cell to5 = board.getCell(0,1); // Taken

		LinkedList<Cell> path1 = new LinkedList<>();
		LinkedList<Cell> path2 = new LinkedList<>();
		LinkedList<Cell> path3 = new LinkedList<>();
		LinkedList<Cell> path4 = new LinkedList<>();
		LinkedList<Cell> path5 = new LinkedList<>();

		PathFinder pathFinder = new PathFinder(board);
		path1 = pathFinder.findPathToCell(from, to1, path1, null);
		path2 = pathFinder.findPathToCell(from, to2, path2, null);
		path3 = pathFinder.findPathToCell(from, to3, path3, null);
		path4 = pathFinder.findPathToCell(from, to4, path4, null);
		path5 = pathFinder.findPathToCell(from, to5, path5, null);
		printPath(path1, board);
		printPath(path2, board);
		printPath(path3, board);
		printPath(path4, board);
		printPath(path5, board);
		Assertions.assertNotNull(path1, "Returned path null, populated list expected");
		Assertions.assertNotNull(path2, "Returned path null, populated list expected");
		Assertions.assertNotNull(path3, "Returned path null, populated list expected");
		Assertions.assertNull(path4, "Expected null for impossible matches");
		Assertions.assertNull(path5, "Expected null for impossible matches");

	}

	@Test
	public void testFindShortestPathToCell() {
		Board board = getMazeBoard();
		Cell from = board.getCell(7,6);
		Cell to1 = board.getCell(2,5);
		Cell to2 = board.getCell(5,3);
		Cell to3 = board.getCell(6,5);
		Cell to4 = board.getCell(0,0); // Unaccessible
		Cell to5 = board.getCell(0,1); // Taken

		LinkedList<Cell> path1 = board.findShortestPathToCell(from, to1);
		LinkedList<Cell> path2 = board.findShortestPathToCell(from, to2);
		LinkedList<Cell> path3 = board.findShortestPathToCell(from, to3);
		LinkedList<Cell> path4 = board.findShortestPathToCell(from, to4);
		LinkedList<Cell> path5 = board.findShortestPathToCell(from, to5);
		printPath(path1, board);
		printPath(path2, board);
		printPath(path3, board);
		printPath(path4, board);
		printPath(path5, board);

		Assertions.assertNotNull(path1, "Returned path null, populated list expected");
		Assertions.assertNotNull(path2, "Returned path null, populated list expected");
		Assertions.assertNotNull(path3, "Returned path null, populated list expected");
		Assertions.assertNotNull(path4, "Returned path null, and not empty");
		Assertions.assertNotNull(path5, "Returned path null, and not empty");
	}


	public void printSet(String message, Collection<Cell> set){
		System.out.print(message + ": ");
        for (Cell c : set) {
            printCell(c);
        }
		System.out.println();
	}

	public void printBoard(Board board){
		for (int i = 0; i < board.height; i++){
			for (int j = 0; j < board.width; j++){
				printCell(board.getCell(j, i));
			}
			System.out.println();
		}
		System.out.println();
	}

	public void printCell(Cell c){
		String color = " ";
		if (c.getBall() != null && c.getBall().getColor().equals(Color.BLUE)){
			color = "B";
		}
		if(c.getBall() != null && c.getBall().getColor().equals(Color.GREEN)){
			color = "G";
		}
		if(c.getBall() != null && c.getBall().getColor().equals(Color.WHITE)){
			color = "W";
		}
		System.out.print("|" + c.x + ":" + c.y + ":" + color + "|");
	}

	public Board getSmallBoard(){
		Board board = new Board(new RuleSet().setBoardSize(4, 4).setMinimalMatch(3));

		board.placeBall(new Ball(Color.GREEN), 0,0);
		board.placeBall(new Ball(Color.GREEN), 1,0);
		board.placeBall(new Ball(Color.GREEN), 2,0);
		board.placeBall(new Ball(Color.GREEN), 3,0);

		board.placeBall(new Ball(Color.BLUE), 0,1);
		board.placeBall(new Ball(Color.GREEN), 1,1);
		board.placeBall(new Ball(Color.BLUE), 2,1);
		board.placeBall(new Ball(Color.BLUE), 3,1);

		board.placeBall(new Ball(Color.BLUE), 0,2);
		board.placeBall(new Ball(Color.BLUE), 1,2);
		board.placeBall(new Ball(Color.GREEN), 2,2);
		board.placeBall(new Ball(Color.BLUE), 3,2);

		board.placeBall(new Ball(Color.BLUE), 0,3);
		board.placeBall(new Ball(Color.BLUE), 1,3);
		board.placeBall(new Ball(Color.BLUE), 2,3);
		board.placeBall(new Ball(Color.GREEN), 3,3);

		printBoard(board);
		return board;
	}

	public Board getMazeBoard(){
		Board board = new Board(new RuleSet());
		board.placeBall(new Ball(), 2,0);
		board.placeBall(new Ball(), 0,1);
		board.placeBall(new Ball(), 1,1);

		board.placeBall(new Ball(), 4, 2 );
		board.placeBall(new Ball(), 5, 2 );
		board.placeBall(new Ball(), 6, 2 );
		board.placeBall(new Ball(), 4, 3 );
		board.placeBall(new Ball(), 6, 3 );

		board.placeBall(new Ball(), 1, 4 );
		board.placeBall(new Ball(), 1, 5 );
		board.placeBall(new Ball(), 3, 5 );
		board.placeBall(new Ball(), 1, 6 );
		board.placeBall(new Ball(), 2, 6 );
		board.placeBall(new Ball(), 3, 6 );

		board.placeBall(new Ball(), 7, 5 );
		board.placeBall(new Ball(), 8, 5 );
		board.placeBall(new Ball(), 6, 6 );
		board.placeBall(new Ball(), 6, 7 );

		return board;
	}

	public void printPath(LinkedList<Cell> path, Board board){
		System.out.println("\nPath: ");
		if(path == null)
			return;
		for (int i = 0; i < board.height; i++){
			for (int j = 0; j < board.width; j++){
				Cell c = board.getCell(j, i);
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
		printSet("\nIn path: ", path);

		System.out.println();
	}
}