package game.model;

public class Game {
	public static final int NUMBER_PLAYERS = 2;
	private Board board;
	private Player[] players;
	private int current;

	public Game(Player s0, Player s1, int dim) {
		board = new Board(dim);
		players = new Player[NUMBER_PLAYERS];
		players[0] = s0;
		players[1] = s1;
		current = 0;
	}

	public void start() {
		boolean doorgaan = true;
		while (doorgaan) {
			reset();
			play();
		}
		System.out.println("\nGood-bye!");
	}

	private void reset() {
		current = 0;
		board.reset();
	}

	private void play() {
		update();
		while (!board.gameOver()) {
			players[current].makeMove(board);
			update();
			current = (current + 1) % 2;

		}
	}

	private void update() {
		System.out.println("\nCurrent game situation: \n\n" + board.toString() + "\n");
	}
}
