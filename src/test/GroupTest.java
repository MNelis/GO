package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import game.model.Board;
import game.model.Group;
import game.model.Stone;

public class GroupTest {
	public static final int DIM = 4;
	private Board board;
	private Stone color = Stone.BLACK;

	@Before
	public void setUp() {
		board = new Board(DIM, false, true);
	}

	@Test
	public void testSetup() {
		Group group = new Group(board, color);
		assertEquals(group.getColor(), Stone.BLACK);
		assertEquals(group.getList().size(), 0);
	}

	@Test
	public void testAddAndContainsStone() {
		Group group = new Group(board, color);
		group.addStone(0, 0);

		assertTrue(group.containsStone(0, 0));
	}

	@Test
	public void testEmptyList() {
		Group group = new Group(board, color);
		group.addStone(0, 0);
		group.addStone(0, 1);
		group.addStone(0, 2);
		assertEquals(group.getList().size(), 3);

		group.emptyList();
		assertTrue(group.getList().isEmpty());
	}

	@Test
	public void testJoinGroup() {
		Group group0 = new Group(board, color);
		Group group1 = new Group(board, color);
		group0.addStone(0, 0);
		group1.addStone(0, 1);

		group0.joinGroup(group1);
		assertTrue(group0.containsStone(0, 0));
		assertTrue(group0.containsStone(0, 1));
		assertTrue(group1.getList().isEmpty());
	}

	@Test
	public void testGetLiberties() {
		Group group = new Group(board, color);
		group.addStone(1, 1);
		group.addStone(1, 2);

		board.addStone(1, 1, color);
		board.addStone(1, 2, color);
		assertEquals(board.getLiberties(1, 1), 3);
		assertEquals(board.getLiberties(1, 2), 3);
		assertEquals(group.getLiberties(), 6);
	}

	@Test
	public void testGetAndSetList() {
		Group group0 = new Group(board, color);
		Group group1 = new Group(board, color);
		group0.addStone(0, 0);
		group0.addStone(0, 1);

		group1.setList(group0.getList());
		assertTrue(group1.containsStone(0, 0));
		assertTrue(group1.containsStone(0, 1));
	}

	@Test
	public void testGetColor() {
		Group group = new Group(board, color);
		assertEquals(group.getColor(), color);
	}

}
