package fp.GO;

public abstract class Player {
	private String name;
	private Node stone;
	
	public Player(String name, Node stone) {
		this.name = name;
		this.stone = stone;
	}
	
	public String getName() {
		return name;
	}
	
	public Node getStone() {
		return stone;
	}
	
	public abstract int[] determineMove(Board board);
	
	public void makeMove(Board board) {
		int[] move = determineMove(board);
		if (move[2] == -1) {
			board.increasePassCounter();
		}
		else {
			board.resetPassCounter();
			board.addStone(move[0], move[1], getStone());
		}
		
	}

}
