package game.model;

public class Board {
	public static int DIM;
	private Stone[][] board;
	private int passCounter;

	/**
	 * Initiates new square board with given dimension. Makes al fields empty.
	 * 
	 * @param dim
	 *            dimension of the board
	 */
	// @requires dim > 0
	public Board(int dim) {
		DIM = dim;
		passCounter = 0;
		board = new Stone[DIM][DIM];
		for (int x = 0; x < DIM; x++) {
			for (int y = 0; y < DIM; y++) {
				removeStone(x, y);
			}
		}
	}

	/** Makes deep copy of the board. */
	public Board deepCopy() {
		Board copyBoard = new Board(DIM);
		copyBoard.board = board.clone();
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
	public boolean isValid(int x, int y) {
		return (isNode(x, y) && isEmpty(x, y));
	}

	/** Adds a stone on a node. */
	public void addStone(int x, int y, Stone stone) {
		board[x][y] = stone;
	}

	/** Removes a stone from a node. */
	public void removeStone(int x, int y) {
		if (!isEmpty(x, y)) {
			board[x][y] = Stone.EMPTY;
		}
	}
	
	/** Gets the numebr of liberties of a stone on a given location */
	public int getLiberties(int x, int y) {
		if (isEmpty(x, y)) {
			return -1;
		} else {
			int numberOfLiberties = 0;
			for (int d = -1; d < 2; d = d+2) {
				if (!isEmpty(x+d, y)) {
					numberOfLiberties++;
				} else if (!isEmpty(x, y+d)) {
					numberOfLiberties++;
				}				
			}
			return numberOfLiberties;
		}
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
}
