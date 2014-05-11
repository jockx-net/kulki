package net.jockx.model;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.*;
import java.util.*;

public class BoardTest {

	@Test
	public void testCreateCellArray() throws Exception {
		Board.Cell[][] cells = Board.Cell.createCellArray(10, 10);
		for (int i = 0; i < 10; i++){
			for (int j = 0; j < 10; j++){
				Assert.assertNotNull(cells[i][j], "Cell at x:" + i + " y: " + j);
			}
		}
	}

	@Test void testRuleSetDefaultConstructor() throws Exception {
		RuleSet ruleSet = new RuleSet();
		Assert.assertNotNull (ruleSet);
		Assert.assertEquals(ruleSet.boardHeight, 9, "Wrong default Board height");
		Assert.assertEquals(ruleSet.boardWidth, 9, "Wrong default Board width");
		Assert.assertEquals(ruleSet.minimalMatch, 5, "Wrong default Board minimalMatch");
	}

	@Test void testBoardConstructor() throws Exception {
		Board board = new Board();
		Assert.assertNotNull (board);
		Assert.assertNotNull(board.getRuleSet());
		Assert.assertEquals(board.height, 9, "Wrong default Board height");
		Assert.assertEquals(board.width, 9, "Wrong default Board width");
	}

	@Test
	public void testGetMatchingLine() throws Exception {
		Board board = getSmallBoard();

		Set<Board.Cell> horizontal = new HashSet<Board.Cell>();
		Set<Board.Cell> vertical = new HashSet<Board.Cell>();
		Set<Board.Cell> mainDiagonal = new HashSet<Board.Cell>();
		Set<Board.Cell> antiDiagonal = new HashSet<Board.Cell>();
		Set<Board.Cell> noneHorizontal = new HashSet<Board.Cell>();
		Set<Board.Cell> noneVertical = new HashSet<Board.Cell>();
		Set<Board.Cell> noneMainDiagonal = new HashSet<Board.Cell>();
		Set<Board.Cell> noneAntiDiagonal = new HashSet<Board.Cell>();

		horizontal = board.getMatchingLine(horizontal, board.getCell(1,0), Board.MatchDirection.HORIZONTAL);
		vertical = board.getMatchingLine(vertical, board.getCell(0,2), Board.MatchDirection.VERTICAL);
		mainDiagonal = board.getMatchingLine(mainDiagonal, board.getCell(2,2), Board.MatchDirection.MAIN_DIAGONAL);
		antiDiagonal = board.getMatchingLine(antiDiagonal, board.getCell(2,1),Board.MatchDirection.ANTI_DIAGONAL);
		noneHorizontal = board.getMatchingLine(noneHorizontal, board.getCell(1,1), Board.MatchDirection.HORIZONTAL);
		noneVertical = board.getMatchingLine(noneVertical, board.getCell(1,1), Board.MatchDirection.VERTICAL);
		noneMainDiagonal = board.getMatchingLine(noneMainDiagonal, board.getCell(1,1), Board.MatchDirection.MAIN_DIAGONAL);
		noneAntiDiagonal = board.getMatchingLine(noneAntiDiagonal, board.getCell(1,1), Board.MatchDirection.ANTI_DIAGONAL);

		printSet("- [1:0]", horizontal);
		printSet("| [0:2]", vertical);
		printSet("\\ [2:2]", mainDiagonal);
		printSet("/ [2:1]", antiDiagonal);
		printSet("- [1:1]", noneHorizontal);
		printSet("| [1:1]", noneVertical);
		printSet("\\ [1:1]", noneMainDiagonal);
		printSet("/ [1:1]", noneAntiDiagonal);

		Assert.assertEquals(horizontal.size(), 4, "Horizontal [1:0] didn't match");
		Assert.assertEquals(vertical.size(), 3, "Diagonal [0:2] didn't match");
		Assert.assertEquals(mainDiagonal.size(), 4, "Vertical [2:2] didn't match");
		Assert.assertEquals(antiDiagonal.size(), 3, "Anti-diagonal  [2:1] didn't match");
		Assert.assertEquals(noneHorizontal.size(), 1, "Horizontal [1:1] is not Empty");
		Assert.assertEquals(noneVertical.size(), 2, "Vertical [1:1] is not Empty");
		Assert.assertEquals(noneMainDiagonal.size(), 4, "Main-Diagonal [1:1] is not Empty");
		Assert.assertEquals(noneAntiDiagonal.size(), 2, "Anti-Diagonal [1:1] is not Empty");
	}

	@Test
	public void testGetAllMatchingLines() throws Exception {
		Board board = getSmallBoard();

		Set<Board.Cell> match0_0 = board.getAllMatchingLines(board.getCell(0,0));
		Set<Board.Cell> match1_1 = board.getAllMatchingLines(board.getCell(1,1));
		Set<Board.Cell> match0_2 = board.getAllMatchingLines(board.getCell(0,2));
		Set<Board.Cell> match2_1 = board.getAllMatchingLines(board.getCell(2,1));
		Set<Board.Cell> match3_1 = board.getAllMatchingLines(board.getCell(3,1));
		Set<Board.Cell> match1_3 = board.getAllMatchingLines(board.getCell(1,3));
		printSet("0:0", match0_0);
		printSet("1:1", match1_1);
		printSet("0:2", match0_2);
		printSet("2:1", match2_1);
		printSet("1:3", match1_3);
		printSet("3:1", match3_1);
		Assert.assertEquals(match0_0.size(), 7, "Horizontal + diagonal [0:0] didn't match"); // 7 green matches
		Assert.assertEquals(match1_1.size(), 4, "Diagonal [1:1] didn't match"); // 4 green matches
		Assert.assertEquals(match0_2.size(), 3, "Vertical [0:2] didn't match"); // 3 blue matches
		Assert.assertEquals(match2_1.size(), 3, "Anti-diagonal  [2:1] didn't match"); // 3 blue matches
		Assert.assertEquals(match1_3.size(), 3, "Horizontal [1:3] didn't match"); // 3 blue matches
		Assert.assertEquals(match3_1.size(), 0, "Empty [3:1] didn't match"); // 2 blue matches
	}

	@Test
	public void testFindPathToCell() throws Exception {
		Board board = getMazeBoard();
		Board.Cell from = board.getCell(7,6);
		Board.Cell to1 = board.getCell(2,5);
		Board.Cell to2 = board.getCell(5,3);
		Board.Cell to3 = board.getCell(6,5);
		Board.Cell to4 = board.getCell(0,0); // Unaccessible
		Board.Cell to5 = board.getCell(0,1); // Taken

		LinkedList<Board.Cell> path1 = new LinkedList<Board.Cell>();
		LinkedList<Board.Cell> path2 = new LinkedList<Board.Cell>();
		LinkedList<Board.Cell> path3 = new LinkedList<Board.Cell>();
		LinkedList<Board.Cell> path4 = new LinkedList<Board.Cell>();
		LinkedList<Board.Cell> path5 = new LinkedList<Board.Cell>();
		path1 = board.findPathToCell(from, to1, path1, null);
		path2 = board.findPathToCell(from, to2, path2, null);
		path3 = board.findPathToCell(from, to3, path3, null);
		path4 = board.findPathToCell(from, to4, path4, null);
		path5 = board.findPathToCell(from, to5, path5, null);
		printPath(path1, board);
		printPath(path2, board);
		printPath(path3, board);
		printPath(path4, board);
		printPath(path5, board);
		Assert.assertNotNull(path1, "Returned path null, populated list expected");
		Assert.assertNotNull(path2, "Returned path null, populated list expected");
		Assert.assertNotNull(path3, "Returned path null, populated list expected");
		Assert.assertNull(path4, "Expected null for impossible matches");
		Assert.assertNull(path5, "Expected null for impossible matches");

	}

	@Test
	public void testFindShortestPathToCell() throws Exception {
		Board board = getMazeBoard();
		Board.Cell from = board.getCell(7,6);
		Board.Cell to1 = board.getCell(2,5);
		Board.Cell to2 = board.getCell(5,3);
		Board.Cell to3 = board.getCell(6,5);
		Board.Cell to4 = board.getCell(0,0); // Unaccessible
		Board.Cell to5 = board.getCell(0,1); // Taken

		LinkedList<Board.Cell> path1 = board.findShortestPathToCell(from, to1);
		LinkedList<Board.Cell> path2 = board.findShortestPathToCell(from, to2);
		LinkedList<Board.Cell> path3 = board.findShortestPathToCell(from, to3);
		LinkedList<Board.Cell> path4 = board.findShortestPathToCell(from, to4);
		LinkedList<Board.Cell> path5 = board.findShortestPathToCell(from, to5);
		printPath(path1, board);
		printPath(path2, board);
		printPath(path3, board);
		printPath(path4, board);
		printPath(path5, board);

		Assert.assertNotNull(path1, "Returned path null, populated list expected");
		Assert.assertNotNull(path2, "Returned path null, populated list expected");
		Assert.assertNotNull(path3, "Returned path null, populated list expected");
		Assert.assertNotNull(path4, "Returned path null, and not empty");
		Assert.assertNotNull(path5, "Returned path null, and not empty");
	}


	public void printSet(String message, Collection<Board.Cell> set){
		System.out.print(message + ": ");
		Iterator<Board.Cell> it = set.iterator();
		while (it.hasNext()){
			Board.Cell c = it.next();
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

	public void printCell(Board.Cell c){
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
		printSet("\nIn path: ", path);

		System.out.println();
	}
}