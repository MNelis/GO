package fp.GO;

public class Board {
	public static int DIM;
	private Node[][] board;
	private int passCounter;
	
	/**
	 * Initiates new square board with given dimension. Makes al fields empty.
	 * @param dim dimension of the board
	 */
	// @requires dim > 0
	public Board(int dim) {
		DIM = dim;
		passCounter = 0;
		board = new Node[DIM][DIM];
		for (int x = 0; x < DIM; x++) {
			for (int y = 0; y < DIM; y++) {
				removeStone(x,y);
			}			
		}
	}
	
	
	public Board deepCopy() {
		Board copyBoard = new Board(DIM);
		copyBoard.board = board.clone();
		return copyBoard;
	}
	
	
	public boolean isNode(int x, int y) {
		return (0 <= x && x < DIM) && (0 <= y && y < DIM);
	}
	
	public Node getStone(int x, int y) {
		return board[x][y];
	}
	
	public boolean isEmpty(int x, int y) {
		return (board[x][y] == Node.EMPTY);
	}
	
	public boolean isValid(int x, int y) {
		return (isNode(x,y) && isEmpty(x,y));
	}
	
	public void addStone(int x, int y, Node stone) {
		board[x][y] = stone;
	}
	
	public void removeStone(int x, int y) {
		if (!isEmpty(x,y)) {
			board[x][y] = Node.EMPTY;
		}
	}
	
	public void reset() {
		for (int x = 0; x < DIM; x++) {
			for (int y = 0; y < DIM; y++) {
				removeStone(x,y);
			}			
		}
	}
	
	
	
	public void increasePassCounter() {
		passCounter++;
	}
	
	public void resetPassCounter() {
		passCounter = 0;
	}
	
	public int getPassCounter() {
		return passCounter;
	}
	
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
                } 
                else {
                	s = s + "\n" + getStone(x, y) + " ";
                	currentX = x;
                }
                
            }
        }
        return s;
	}
}
