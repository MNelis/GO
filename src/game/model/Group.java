package game.model;

import java.util.List;
import java.util.Vector;

public class Group {
	private List<int[]> stones;
	private Board board;

	/** Constructs a new group. */
	public Group(Board board) {
		stones = new Vector<int[]>();
		this.board = board;
	}

	/** Add a stone to the group. */
	public void addStone(int x, int y) {
		stones.add(new int[] { x, y });
	}

	/** Gets the number of liberties of the group. */
	public int getLiberties() {
		List<Integer> libertiesList = new Vector<Integer>();
		stones.forEach((stone) -> libertiesList.add(board.getLiberties(stone[0], stone[1])));
		int sum = 0;
		for (int i : libertiesList) {
			sum += i;
		}
		return sum;
	}

	
}
