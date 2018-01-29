package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import game.model.Board;
import game.model.Stone;

public class BoardTest {
	public static final int DIM = 4;
	private Board board;

	@Before
	public void setUp() {
		board = new Board(DIM, false, true);
	}

	@Test
	public void testDeepCopy() {
		board.addStone(0, 0, Stone.BLACK);

		Board copyBoard = board.deepCopy();
		copyBoard.addStone(0, 0, Stone.WHITE);

		assertEquals(Stone.BLACK, board.getStone(0, 0));
		assertEquals(Stone.WHITE, copyBoard.getStone(0, 0));
	}

	@Test
	public void testSetup() {
		assertEquals(Stone.EMPTY, board.getStone(0, 0));
		assertEquals(Stone.EMPTY, board.getStone(DIM - 1, DIM - 1));
	}

	@Test
	public void testIsNode() {
		assertFalse(board.isNode(-1, 0));
		assertTrue(board.isNode(0, 0));
		assertTrue(board.isNode(DIM - 1, DIM - 1));
		assertFalse(board.isNode(DIM - 1, DIM));
	}

	@Test
	public void testAddAndGetStone() {
		board.addStone(0, 0, Stone.BLACK);
		board.addStone(0, 1, Stone.WHITE);

		assertEquals(board.getStone(0, 0), Stone.BLACK);
		assertEquals(board.getStone(0, 1), Stone.WHITE);
		assertEquals(board.getStone(1, 1), Stone.EMPTY);
	}

	@Test
	public void testIsEmpty() {
		board.addStone(0, 0, Stone.BLACK);

		assertFalse(board.isEmpty(0, 0));
		assertTrue(board.isEmpty(0, 1));
	}

	@Test
	public void testCaptureAndKoRule() {
		board.addStone(1, 0, Stone.BLACK);
		board.addStone(0, 0, Stone.WHITE);
		board.addStone(0, 1, Stone.BLACK);

		assertTrue(board.isEmpty(0, 0));
		assertFalse(board.koRule(0, 0, Stone.WHITE));
	}

	@Test
	public void testIsValid() {
		board.addStone(1, 0, Stone.BLACK);
		board.addStone(0, 0, Stone.WHITE);
		board.addStone(0, 1, Stone.BLACK);

		assertFalse(board.isValid(0, 0, Stone.WHITE));
		assertTrue(board.isValid(0, 0, Stone.BLACK));
		assertFalse(board.isValid(0, 1, Stone.WHITE));
		assertFalse(board.isValid(0, 1, Stone.BLACK));
		assertFalse(board.isValid(0, -1, Stone.WHITE));
	}

	@Test
	public void testCheckCapture() {
		board.addStone(1, 0, Stone.BLACK);
		board.addStone(0, 2, Stone.WHITE);
		board.addStone(0, 1, Stone.BLACK);
		board.addStone(1, 3, Stone.WHITE);
		board.addStone(2, 1, Stone.BLACK);
		board.addStone(2, 2, Stone.WHITE);
		board.addStone(1, 2, Stone.BLACK);
		board.addStone(1, 1, Stone.WHITE);

		assertTrue(board.isEmpty(1, 2));
		assertEquals(board.getStone(1, 1), Stone.WHITE);
	}

	@Test
	public void testRemoveStone() {
		board.addStone(0, 0, Stone.BLACK);
		assertEquals(board.getStone(0, 0), Stone.BLACK);

		board.removeStone(0, 0);
		assertEquals(board.getStone(0, 0), Stone.EMPTY);
	}

	@Test
	public void testGetLiberties() {
		board.addStone(0, 0, Stone.BLACK);
		assertEquals(board.getLiberties(0, 0), 2);

		board.addStone(0, 1, Stone.BLACK);
		assertEquals(board.getLiberties(0, 0), 1);
		assertEquals(board.getLiberties(0, 1), 2);
	}

	@Test
	public void testReset() {
		board.addStone(0, 0, Stone.BLACK);
		assertFalse(board.isEmpty(0, 0));

		board.reset();
		assertTrue(board.isEmpty(0, 0));
	}

	@Test
	public void testDetermineScore() {
		board.addStone(1, 0, Stone.BLACK);
		board.addStone(0, 2, Stone.WHITE);
		board.addStone(0, 1, Stone.BLACK);
		board.addStone(1, 3, Stone.WHITE);
		board.addStone(2, 1, Stone.BLACK);
		board.addStone(2, 2, Stone.WHITE);
		board.addStone(1, 2, Stone.BLACK);
		board.addStone(1, 1, Stone.WHITE);

		int[] scores = board.determineScores();
		assertEquals(scores[0], 4);
		assertEquals(scores[1], 6);
	}

}
