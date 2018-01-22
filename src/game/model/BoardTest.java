package game.model;

public class BoardTest {
	public static void main(String[] args) {
		Board board = new Board(5);
		Player P1 = new HumanPlayer("P1", Node.BLACK);
		
		while (!board.gameOver()) {
			P1.makeMove(board);
			System.out.println(board.toString());
		}
		
	}
}
