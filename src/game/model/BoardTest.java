package game.model;

public class BoardTest {
	public static void main(String[] args) {
		Board board = new Board(5, true, false);
		int[] scores;

		board.addStone(1, 0, Stone.BLACK);
		board.addStone(0, 2, Stone.WHITE);
		board.addStone(0, 1, Stone.BLACK);
		board.addStone(1, 3, Stone.WHITE);
		board.addStone(2, 1, Stone.BLACK);
		board.addStone(2, 2, Stone.WHITE);
		board.addStone(1, 2, Stone.BLACK);
		board.addStone(1, 1, Stone.WHITE);
		board.addStone(3, 0, Stone.WHITE);
		board.addStone(3, 1, Stone.WHITE);
		board.addStone(4, 4, Stone.BLACK);

		scores = board.determineScores();

		System.out.println("BLACK: " + scores[0] + ", WHITE: " + scores[1]);
	}
}
