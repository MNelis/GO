package game.offline;

import java.util.Scanner;
import game.model.Board;
import game.players.Player;

public class OfflineGOGame {
	public static final int NUMBER_PLAYERS = 2;
	private Board board;
	private Player[] players;
	private int current;
	private Scanner in;

	public OfflineGOGame(Player p0, Player p1, int dim) {
		board = new Board(dim, true, true);
		players = new Player[NUMBER_PLAYERS];
		players[0] = p0;
		players[1] = p1;
		current = 0;
	}

	public void start() {
		boolean nextGame = true;
		while (nextGame) {
			reset();
			play();
			nextGame = readBoolean("\n> Play another time? (y/n)? ", "y", "n");
		}
		System.out.println("\nGood-bye!");
	}

	private void reset() {
		current = 0;
		board.reset();
	}

	private void play() {
		while (!board.gameOver()) {
			players[current].makeMove(board);
			current = (current + 1) % 2;
		}
		printResult();
	}

	private boolean readBoolean(String prompt, String yes, String no) {
		String answer;
		do {
			System.out.print(prompt);
			in = new Scanner(System.in);
			answer = in.hasNextLine() ? in.nextLine() : null;
		} while (answer == null || (!answer.equals(yes) && !answer.equals(no)));
		return answer.equals(yes);
	}

	private void printResult() {
		int[] scores = board.determineScores();
		if (scores[0] > scores[1]) {
			System.out.println(players[0].getName() + " has won!");
		} else if (scores[0] == scores[1]) {
			System.out.println("Draw. There is no winner!");
		} else {
			System.out.println(players[1].getName() + " has won!");
		}
		System.out.println(players[0].getName() + ": \t" + scores[0]);
		System.out.println(players[1].getName() + ": \t" + scores[1]);
	}

}
