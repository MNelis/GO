package game.model;

public abstract class Player {
	private String name;
	private Stone stone;
	
	public Player(String name, Stone stone) {
		this.name = name;
		this.stone = stone;
	}
	
	public String getName() {
		return name;
	}
	
	public Stone getStone() {
		return stone;
	}
	
	public abstract int[] determineMove(Board board, boolean offline);
	
	public void makeMove(Board board, boolean offline) {
		int[] move = determineMove(board, offline);
		if (move[2] == -1) {
			board.increasePassCounter();
		}
		else {
			board.resetPassCounter();
			board.addStone(move[0], move[1], getStone());
		}
		
	}

}
