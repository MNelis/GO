package test;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import game.model.Board;
import game.model.Stone;

class BoardTest {
	private Board board;

	@Before
	public void setUp() {
		board = new Board(5, true, true);
	}

	@Test
	void testDeepCopy() {
		board.addStone(0, 0, Stone.BLACK);

		Board copyBoard = board.deepCopy();
		copyBoard.addStone(0, 0, Stone.WHITE);

		assertEquals(Stone.BLACK, board.getStone(0, 0));
		assertEquals(Stone.WHITE, copyBoard.getStone(0, 0));
	}

	int[] scores;

	// Board copyBoard = board.deepCopy();
	//
	// board.addStone(0, 0, Stone.BLACK);
	// copyBoard.addStone(0, 0, Stone.WHITE);
	//
	// System.out.println(board.getStone(0, 0));
	// System.out.println(copyBoard.getStone(0, 0));

	// System.out.println(board.koRule(0, 1, Stone.BLACK));
	// System.out.println(board.getStone(0, 1));
	// System.out.println(board.isEmpty(0, 1));
	// System.out.println(board.isNode(0, 1));

	// System.out.println(board.isValid(0, 1, Stone.BLACK));
	// board.addStone(1, 0, Stone.BLACK);
	// board.addStone(0, 2, Stone.WHITE);
	// board.addStone(0, 1, Stone.BLACK);
	// board.addStone(1, 3, Stone.WHITE);
	// board.addStone(2, 1, Stone.BLACK);
	// board.addStone(2, 2, Stone.WHITE);
	// System.out.println(board.isValid(1, 2, Stone.BLACK));
	// board.addStone(1, 2, Stone.BLACK);
	// System.out.println(board.getStone(1, 1));
	// System.out.println(board.koRule(1, 1, Stone.WHITE));
	// System.out.println(board.isEmpty(1, 1));
	// System.out.println(board.isNode(1, 1));
	// System.out.println(board.isValid(1, 1, Stone.WHITE));
	// board.addStone(1, 1, Stone.WHITE);
	//
	// System.out.println(board.koRule(1, 2, Stone.BLACK));
	// System.out.println(board.isEmpty(1, 2));
	// System.out.println(board.isNode(1, 2));
	// System.out.println(board.isValid(1, 2, Stone.BLACK));
	// board.addStone(3, 0, Stone.WHITE);
	// board.addStone(3, 1, Stone.WHITE);
	// board.addStone(4, 4, Stone.BLACK);

	// scores = board.determineScores();

	// System.out.println("BLACK: " + scores[0] + ", WHITE: " + scores[1]);

}
