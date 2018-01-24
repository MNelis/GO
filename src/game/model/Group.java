package game.model;

import java.util.ArrayList;
import java.util.List;

public class Group {
	private List<Integer[]> stones;
	private Board board;
	private Stone stone;

	/** Constructs a new group. */
	public Group(Board board, Stone stone) {
		stones = new ArrayList<Integer[]>();
		this.board = board;
		this.stone = stone;
	}

	/** Adds a stone to the group. */
	public void addStone(int x, int y) {
		stones.add(new Integer[] { x, y });
	}

	/** Empties the group. */
	public void emptyList() {
		stones.removeAll(stones);
	}

	/** Merges another group with this group */
	public void joinGroup(Group group) {
		List<Integer[]> list = group.getList();
		list.forEach((stone) -> stones.add(stone));
		group.emptyList();
	}

	/** Gets the number of liberties of the group. */
	public int getLiberties() {
		List<Integer> libertiesList = new ArrayList<Integer>();
		stones.forEach((stone) -> libertiesList.add(board.getLiberties(stone[0], stone[1])));
		int sum = 0;
		for (int i : libertiesList) {
			sum += i;
		}
		return sum;
	}

	/** Checks if the group contains a given stone. */
	public boolean containsStone(Integer[] stone) {
		// TODO must be a better way to formulate this.
		boolean x = false;
		for (Integer[] s : stones) {
			if (s[0].equals(stone[0]) && (s[1].equals(stone[1]))) {
				x = true;
			}
		}
		return x;
	}
	
	/** Checks if the group contains a given stone. */
	public boolean containsStone(int x, int y) {
		Integer[] stone = { x, y };
		return (containsStone(stone));
	}

	/** Gets list of stones in group. */
	public List<Integer[]> getList() {
		return stones;
	}

	/** Gets the color of the stones of group. */
	public Stone getColor() {
		return stone;
	}
}
