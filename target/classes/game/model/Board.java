package game.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import com.nedap.go.gui.GoGUIIntegrator;

public class Board {
	private int dimension;
	private Stone[][] board;
	private int[] numberStones;
	private int passCounter;
	private boolean enabledGUI;
	private boolean enabledHistory;
	private GoGUIIntegrator goGUI;
	private List<Group> groups;
	private List<Area> areas;
	private List<Board> history;
	private boolean playerQuits = false;

	/** Initiates new square board with given dimension. Makes all fields empty.
	 * @param dim dimension of the board */
	// @requires dim > 0
	public Board(int dim, boolean enableGUI, boolean enableHistory) {
		groups = new ArrayList<>();
		areas = new ArrayList<>();
		history = new ArrayList<>();
		numberStones = new int[2];

		dimension = dim;
		enabledGUI = enableGUI;
		enabledHistory = enableHistory;
		passCounter = 0;
		board = new Stone[dimension][dimension];
		numberStones[0] = (int) Math.ceil((dimension * dimension) / 2.0);
		numberStones[1] = (int) Math.floor((dimension * dimension) / 2.0);
		for (int r = 0; r < dimension; r++) {
			for (int c = 0; c < dimension; c++) {
				board[r][c] = Stone.EMPTY;
			}
		}
		if (enabledGUI) {
			goGUI = new GoGUIIntegrator(false, true, dimension);
			goGUI.startGUI();
			goGUI.setBoardSize(dimension);
		} else if (enabledHistory) {
			history.add(deepCopy());
		}
	}

	/** Makes deep copy of the board.
	 * @return deepCopy */
	public Board deepCopy() {
		Board copyBoard = new Board(dimension, false, false);
		for (int i = 0; i < dimension; i++) {
			copyBoard.board[i] = Arrays.copyOf(board[i], board[i].length);
		}
		for (Group g : groups) {
			Group copyGroup = new Group(copyBoard, g.getColor());
			List<Integer[]> list = new ArrayList<>();
			for (Integer[] item : g.getList()) {
				list.add(Arrays.copyOf(item, item.length));
			}
			copyGroup.setList(list);
			copyBoard.groups.add(copyGroup);
		}
		return copyBoard;
	}

	/** Checks if a node is on the board.
	 * @param r row.
	 * @param c column.
	 * @return true if it is a node on the board, false otherwise. */
	public boolean isNode(int r, int c) {
		return (0 <= r && r < dimension) && (0 <= c && c < dimension);
	}

	/** Gets the stone of a given node.
	 * @param r row.
	 * @param c column.
	 * @return BLACK, WHITE, or EMPTY. */
	public Stone getStone(int r, int c) {
		return board[r][c];
	}

	/** Checks if a node is empty.
	 * @param r row.
	 * @param c column.
	 * @return true if node is empty, false otherwise. */
	public boolean isEmpty(int r, int c) {
		return board[r][c] == Stone.EMPTY;
	}

	/** Checks is the given move is valid.
	 * @param r row.
	 * @param c column.
	 * @param color color of the stone.
	 * @return true if valid, false otherwise. */
	public boolean isValid(int r, int c, Stone color) {
		if (enabledHistory) {
			return isNode(r, c) && isEmpty(r, c) && !isFull() && koRule(r, c, color);
		} else {
			return isNode(r, c) && isEmpty(r, c) && !isFull();
		}

	}

	/** Checks is the given move is does not create a previous board situation.
	 * @param r row.
	 * @param c column.
	 * @param color color of the stone.
	 * @return true if it does not create a previous board situation, false
	 *         otherwise. */
	public boolean koRule(int r, int c, Stone color) {
		boolean koRule = true;
		Board copyBoard = deepCopy();
		copyBoard.addStone(r, c, color);
		for (Board b : history) {
			if (b.equals(copyBoard)) {
				koRule = false;
				break;
			}
		}
		return koRule;
	}

	/** Checks if the board is full.
	 * @return true if full, false otherwise. */
	private boolean isFull() {
		boolean isFull = true;
		for (int r = 0; r < dimension; r++) {
			for (int c = 0; c < dimension; c++) {
				if (isEmpty(r, c)) {
					isFull = false;
					break;
				}
			}
		}
		return isFull;
	}

	/** Gets the number of remaining stones of both colors.
	 * @return array with number of BLACK stones and number of WHITE stones. */
	public int[] getNumberStones() {
		return numberStones;
	}

	/** Checks if both colors have stones left.
	 * @return true if both color have stones left, false otherwise. */
	public boolean stonesLeft() {
		return numberStones[0] > 0 && numberStones[0] > 0;
	}

	/** Adds a stone on a node.
	 * @param r row.
	 * @param c column.
	 * @param color color of the stone. */
	public void addStone(int r, int c, Stone color) {
		board[r][c] = color;
		if (color.equals(Stone.BLACK)) {
			numberStones[0] -= 1;
		} else {
			numberStones[1] -= 1;
		}
		if (enabledGUI) {
			goGUI.addStone(c, r, color.equals(Stone.WHITE));
		}
		updateGroups(r, c, color);
		checkCaptures(r, c, color);
		if (enabledHistory) {
			history.add(deepCopy());
		}
	}

	/** Updates the groups on the board around the added stone.
	 * @param r row.
	 * @param c column.
	 * @param color color of the added stone. */
	public void updateGroups(int r, int c, Stone color) {
		Group addedTo = null;
		List<Group> toRemove = new ArrayList<>();
		Integer[][] neighborStones = {{r - 1, c}, {r + 1, c}, {r, c - 1}, {r, c + 1}};

		for (Integer[] s : neighborStones) {
			if (isNode(s[0], s[1]) && getStone(s[0], s[1]) == color) {
				for (Group g : groups) {
					if (g.containsStone(s)) {
						if (addedTo == null) {
							g.addStone(r, c);
							addedTo = g;
						} else if (g.containsStone(s) && !addedTo.containsStone(s)) {
							addedTo.joinGroup(g);
							toRemove.add(g);
						}

					}
				}

			}
		}
		// Creates new group, if stone not added to existing group.
		if (addedTo == null) {
			Group group = new Group(this, color);
			group.addStone(r, c);
			groups.add(group);
		}
		toRemove.forEach(group -> group.emptyList());
		toRemove.forEach(group -> removeGroup(group));
	}

	/** Checks if the added stone captures a group or is captured itself.
	 * @param r row.
	 * @param c column.
	 * @param color color of the stone. */
	public void checkCaptures(int r, int c, Stone color) {
		Integer[][] neighborStones = {{r - 1, c}, {r + 1, c}, {r, c - 1}, {r, c + 1}};
		boolean capturesGroup = false;
		for (Integer[] s : neighborStones) {
			if (isNode(s[0], s[1]) && getStone(s[0], s[1]) == color.other()) {
				Iterator<Group> g = groups.iterator();
				while (g.hasNext()) {
					Group group = g.next();
					if (group.containsStone(s) && group.getLiberties() == 0) {
						capturesGroup = true;
						removeGroup(group);
						group.emptyList();
						break;
					}
				}
			}
		}
		if (!capturesGroup) {
			Iterator<Group> g = groups.iterator();
			while (g.hasNext()) {
				Group group = g.next();
				if (group.containsStone(r, c) && group.getLiberties() == 0) {
					removeGroup(group);
					group.emptyList();
					break;
				}
			}
		}
	}

	/** Removes a stone from a node.
	 * @param r row.
	 * @param c column. */
	public void removeStone(int r, int c) {
		if (!isEmpty(r, c)) {
			board[r][c] = Stone.EMPTY;
			if (enabledGUI) {
				goGUI.removeStone(c, r);
			}
		}
	}

	/** Removes all stones from a group.
	 * @param group group. */
	public void removeGroup(Group group) {
		for (Integer[] s : group.getList()) {
			removeStone(s[0], s[1]);
		}
		groups.remove(group);
	}

	/** Gets the number of liberties of a stone on a given location.
	 * @param r row.
	 * @param c column.
	 * @return number of liberties. */
	public int getLiberties(int r, int c) {
		int numberOfLiberties = 0;
		Integer[][] neighborStones = {{r - 1, c}, {r + 1, c}, {r, c - 1}, {r, c + 1}};
		if (isNode(r, c) && isEmpty(r, c)) {
			numberOfLiberties = -1;
		} else {
			for (Integer[] s : neighborStones) {
				if (isNode(s[0], s[1]) && isEmpty(s[0], s[1])) {
					numberOfLiberties++;
				}
			}
		}
		return numberOfLiberties;
	}

	/** Resets the board, makes all nodes empty. */
	public void reset() {
		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				removeStone(x, y);
			}
		}
		history.clear();
		groups.clear();
	}

	/** Increases a counter which keeps track of the number of consecutive
	 * PASS-moves. */
	public void increasePassCounter() {
		passCounter++;
	}

	/** Resets the PASS-counter. */
	public void resetPassCounter() {
		passCounter = 0;
	}

	/** Gets the current count of the PASS-counter.
	 * @return current count. */
	public int getPassCounter() {
		return passCounter;
	}

	/** Checks if a node is on the board.
	 * @return true if game over, false otherwise. */
	public boolean gameOver() {
		return passCounter > 1 || playerQuits || !stonesLeft();
	}

	/** Determines the scores of the players.
	 * @return array with scores of BLACK and WHITE. */
	public int[] determineScores() {
		int scoreBLACK = 0;
		int scoreWHITE = 0;
		for (int r = 0; r < dimension; r++) {
			for (int c = 0; c < dimension; c++) {
				if (isEmpty(r, c)) {
					Area area = updateAreas(r, c);
					Integer[][] neighborStones = {{r - 1, c}, {r + 1, c}, {r, c - 1}, {r, c + 1}};
					for (Integer[] s : neighborStones) {
						if (isNode(s[0], s[1]) && !isEmpty(s[0], s[1])) {
							if (!area.getStoneArea() && area.getStone().equals(Stone.EMPTY)) {
								area.setStoneArea(true);
								area.setStone(getStone(s[0], s[1]));
							} else if (area.getStoneArea()
									&& !area.getStone().equals(getStone(s[0], s[1]))) {
								area.setStoneArea(false);
							}
						}
					}
				} else if (getStone(r, c).equals(Stone.BLACK)) {
					scoreBLACK++;
				} else {
					scoreWHITE++;
				}
			}
		}
		for (Area a : areas) {
			if (a.getStoneArea()) {
				if (enabledGUI) {
					for (Integer[] i : a.getList()) {
						goGUI.addAreaIndicator(i[1], i[0], a.getStone().equals(Stone.WHITE));
					}
				}
				if (a.getStone().equals(Stone.BLACK)) {
					scoreBLACK += a.length();
				} else {
					scoreWHITE += a.length();
				}
			}
		}
		areas.removeAll(areas);
		return new int[]{scoreBLACK, scoreWHITE};
	}

	/** Updates the areas.
	 * @param r row.
	 * @param c column.
	 * @return area of the node. */
	public Area updateAreas(int r, int c) {
		Area addedTo = null;
		List<Area> toRemove = new ArrayList<>();
		Integer[][] neighborNodes = {{r - 1, c}, {r, c - 1}};

		for (Integer[] s : neighborNodes) {
			if (isNode(s[0], s[1]) && getStone(s[0], s[1]) == Stone.EMPTY) {
				for (Area a : areas) {
					if (a.containsNode(s)) {
						if (addedTo == null) {
							a.addNode(r, c);
							addedTo = a;
						} else if (a.containsNode(s) && !addedTo.containsNode(s)) {
							addedTo.joinArea(a);
							toRemove.add(a);
						}

					}
				}

			}
		}
		// Creates new area, if node not added to existing area.
		if (addedTo == null) {
			Area area = new Area();
			area.addNode(r, c);
			areas.add(area);
			return area;
		} else {
			toRemove.forEach(area -> area.emptyList());
			toRemove.forEach(area -> areas.remove(area));
			return addedTo;
		}
	}

	/** Sets playerQuits to true. */
	public void quitGame() {
		playerQuits = true;
	}

	/** Sets dimension of the board.
	 * @param dim dimension. */
	public void setDimension(int dim) {
		dimension = dim;
		reset();
		board = new Stone[dimension][dimension];
		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				board[x][y] = Stone.EMPTY;
			}
		}

		if (enabledGUI) {
			goGUI.clearBoard();
			goGUI.setBoardSize(dimension);
		}
	}

	/** Gets dimension of the board.
	 * @return dimension. */
	public int getDimension() {
		return dimension;
	}

	@Override
	public String toString() {
		String s = "";
		int currentX = 0;
		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				if (currentX == x) {
					s = s + getStone(x, y) + " ";
				} else {
					s = s + "\n" + getStone(x, y) + " ";
					currentX = x;
				}
			}
		}
		return s;
	}

	@Override
	public boolean equals(Object b) {
		boolean equal = true;
		for (int x = 0; x < dimension; x++) {
			for (int y = 0; y < dimension; y++) {
				if (!((Board) b).getStone(x, y).equals(this.getStone(x, y))) {
					equal = false;
				}
			}
		}
		return equal;
	}
}
