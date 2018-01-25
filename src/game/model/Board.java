package game.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nedap.go.gui.GoGUIIntegrator;

public class Board {
	public static int DIM;
	private Stone[][] board;
	private int passCounter;
	private boolean enabledGUI;
	boolean enabledHistory;
	private GoGUIIntegrator goGUI;
	private List<Group> groups = new ArrayList<>();
	private List<Area> areas = new ArrayList<>();
	private List<Board> history = new ArrayList<>();

	/**
	 * Initiates new square board with given dimension. Makes al fields empty.
	 * 
	 * @param dim
	 *            dimension of the board
	 */
	// @requires dim > 0
	public Board(int dim, boolean enableGUI, boolean enableHistory) {
		DIM = dim;
		enabledGUI = enableGUI;
		enabledHistory = enableHistory;
		passCounter = 0;
		board = new Stone[DIM][DIM];
		for (int x = 0; x < DIM; x++) {
			for (int y = 0; y < DIM; y++) {
				board[x][y] = Stone.EMPTY;
			}
		}
		if (enabledGUI) {
			goGUI = new GoGUIIntegrator(false, true, DIM);
			goGUI.startGUI();
			goGUI.setBoardSize(DIM);
		} else if (enabledHistory) {
			history.add(deepCopy());
		}
	}

	/** Makes deep copy of the board. */
	public Board deepCopy() {
		Board copyBoard = new Board(DIM, false, false);
		for (int i  = 0; i < DIM; i++) {
			copyBoard.board[i] = Arrays.copyOf(board[i], board[i].length);
		}
		for (Group g: groups) {
			Group copyGroup = new Group(copyBoard,g.getColor());
			copyGroup.setList(g.getList());
			copyBoard.groups.add(copyGroup);
		}
//		copyBoard.board = board.clone();
		return copyBoard;
	}

	/** Checks if a node is on the board. */
	public boolean isNode(int x, int y) {
		return (0 <= x && x < DIM) && (0 <= y && y < DIM);
	}

	/** Gets the stone of a given node */
	public Stone getStone(int x, int y) {
		return board[x][y];
	}

	/** Checks if a node is empty. */
	public boolean isEmpty(int x, int y) {
		return (board[x][y] == Stone.EMPTY);
	}

	/** Checks if a node is on the board and empty. */
	public boolean isValid(int x, int y, Stone stone) {
		boolean koRule = koRule(x,y,stone);		
		return (isNode(x, y) && isEmpty(x, y) && koRule);
	}
	
	public boolean koRule(int x, int y, Stone stone) {
		boolean koRule = true;
		Board copyBoard = deepCopy();
		System.out.println(copyBoard.toString());
		copyBoard.addStone(x, y, stone); // this doesnt work propperly
		System.out.println(copyBoard.toString());
		for (Board b: history) {
			if (b.equals(copyBoard)) {
				koRule = false;
				break;
			}
		}
		return koRule;
	}

//	private boolean isFull() {
//		boolean isFull = true;
//		for (int x = 0; x < DIM; x++) {
//			for (int y = 0; y < DIM; y++) {
//				if (isEmpty(x, y)) {
//					isFull = false;
//					break;
//				}
//			}
//		}
//		return isFull;
//	}

	/** Adds a stone on a node. */
	public void addStone(int x, int y, Stone stone) {
		board[x][y] = stone;
		if (enabledGUI) {
			goGUI.addStone(y, x, (stone.equals(Stone.WHITE)));
		}
		updateGroups(x, y, stone);
		checkCaptures(x, y, stone);
		if (enabledHistory) {			
			history.add(deepCopy());
		}
	}

	/** Updates the groups on the board around the added stone. */
	public void updateGroups(int x, int y, Stone stone) {
		Group addedTo = null;
		List<Group> toRemove = new ArrayList<>();
		Integer[][] neighborStones = { { x - 1, y }, { x + 1, y }, { x, y - 1 }, { x, y + 1 } };

		for (Integer[] s : neighborStones) {
			if (isNode(s[0], s[1]) && getStone(s[0], s[1]) == stone) {
				for (Group g : groups) {
					if (g.containsStone(s)) {
						if (addedTo == null) {
							g.addStone(x, y);
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
			Group group = new Group(this, stone);
			group.addStone(x, y);
			groups.add(group);
		}
		toRemove.forEach((group) -> group.emptyList());
		toRemove.forEach((group) -> removeGroup(group));
	}

	/** Checks if the added stone captures a group or is captured intself. */
	public void checkCaptures(int x, int y, Stone stone) {
		Integer[][] neighborStones = { { x - 1, y }, { x + 1, y }, { x, y - 1 }, { x, y + 1 } };
		List<Group> toRemove = new ArrayList<>();
		boolean capturesGroup = false;

		// first checks
		for (Integer[] s : neighborStones) {
			if (isNode(s[0], s[1]) && getStone(s[0], s[1]) == stone.Other()) {
				for (Group g : groups) {
					if (g.containsStone(s) && g.getLiberties() == 0) {
						capturesGroup = true;
						removeGroup(g);
						toRemove.add(g);
						break;
					}
				}
			}
		}
		if (!capturesGroup) {
			for (Group g : groups) {
				if (g.containsStone(x, y) && g.getLiberties() == 0) {
					removeGroup(g);
					toRemove.add(g);
					break;
				}
			}
		}
		toRemove.forEach((group) -> group.emptyList());
		toRemove.forEach((group) -> removeGroup(group));
	}

	/** Removes a stone from a node. */
	public void removeStone(int x, int y) {
		if (!isEmpty(x, y)) {
			board[x][y] = Stone.EMPTY;
			if (enabledGUI) {
				goGUI.removeStone(y, x);
			}
		}
	}

	/** Removes a stone from a node. */
	public void removeGroup(Group group) {
		for (Integer[] s : group.getList()) {
			removeStone(s[0], s[1]);
		}
		groups.remove(group);
	}

	/** Gets the numebr of liberties of a stone on a given location */
	public int getLiberties(int x, int y) {
		int numberOfLiberties = 0;
		Integer[][] neighborStones = { { x - 1, y }, { x + 1, y }, { x, y - 1 }, { x, y + 1 } };
		if (isNode(x, y) && isEmpty(x, y)) {
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
		for (int x = 0; x < DIM; x++) {
			for (int y = 0; y < DIM; y++) {
				removeStone(x, y);
			}
		}
	}

	/**
	 * Increases a counter which keeps track of the number of consecutive
	 * PASS-moves.
	 */
	public void increasePassCounter() {
		passCounter++;
	}

	/** Resets the PASS-counter. */
	public void resetPassCounter() {
		passCounter = 0;
	}

	/** Gets the current count of the PASS-counter. */
	public int getPassCounter() {
		return passCounter;
	}

	/** Checks if a node is on the board. */
	public boolean gameOver() {
		return (passCounter > 1);
	}

	/** Determines the scores of the players. */
	public int[] determineScores() {
		int scoreBLACK = 0;
		int scoreWHITE = 0;
		for (int x = 0; x < DIM; x++) {
			for (int y = 0; y < DIM; y++) {
				if (isEmpty(x, y)) {
					Area area = updateAreas(x, y);
					Integer[][] neighborStones = { { x - 1, y }, { x + 1, y }, { x, y - 1 }, { x, y + 1 } };
					for (Integer[] s : neighborStones) {
						if (isNode(s[0], s[1]) && !isEmpty(s[0], s[1])) {
							if (!area.getStoneArea() && area.getStone().equals(Stone.EMPTY)) {
								area.setStoneArea(true);
								area.setStone(getStone(s[0], s[1]));
							} else if (area.getStoneArea() && !area.getStone().equals(getStone(s[0], s[1]))) {
								area.setStoneArea(false);
							}
						}
					}
				} else if (getStone(x, y).equals(Stone.BLACK)) {
					scoreBLACK++;
				} else {
					scoreWHITE++;
				}
			}
		}
		for (Area a : areas) {
			if (a.getStoneArea()) {
				if (a.getStone().equals(Stone.BLACK)) {
					scoreBLACK += a.length();
				} else {
					scoreWHITE += a.length();
				}
			}
		}
		areas.removeAll(areas);
		return new int[] { scoreBLACK, scoreWHITE };
	}

	public Area updateAreas(int x, int y) {
		Area addedTo = null;
		List<Area> toRemove = new ArrayList<>();
		Integer[][] neighborNodes = { { x - 1, y }, { x, y - 1 } };

		for (Integer[] s : neighborNodes) {
			if (isNode(s[0], s[1]) && getStone(s[0], s[1]) == Stone.EMPTY) {
				for (Area a : areas) {
					if (a.containsNode(s)) {
						if (addedTo == null) {
							a.addNode(x, y);
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
			area.addNode(x, y);
			areas.add(area);
			return area;
		} else {
			toRemove.forEach((area) -> area.emptyList());
			toRemove.forEach((area) -> areas.remove(area));
			return addedTo;
		}

	}

	@Override
	public String toString() {
		String s = "";
		int currentX = 0;
		for (int x = 0; x < DIM; x++) {
			for (int y = 0; y < DIM; y++) {
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
	public boolean equals(Object board) {
		boolean equal = true;
		for (int x = 0; x < DIM; x++) {
			for (int y = 0; y < DIM; y++) {
				if (!((Board) board).getStone(x, y).equals(this.getStone(x, y))) {
					equal = false;
				}
			}
		}
		return equal;
	}
}
