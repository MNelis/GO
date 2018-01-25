package game.model;

public class GameOFFLINE {
	public static final int NUMBER_PLAYERS = 2;
	private Board board;
	private Player[] players;
	private int current;
	private boolean offline; // necessary?
	
	public GameOFFLINE(Player s0, Player s1, int dim, boolean offline) {
		board = new Board(dim, false, true);
		players = new Player[NUMBER_PLAYERS];
		players[0] = s0;
		players[1] = s1;
		current = 0;
		this.offline = offline;
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
			players[current].makeMove(board, offline);
			update();
			current = (current + 1) % 2;

		}
	}

	private void update() {
		System.out.println("\nCurrent game situation: \n\n" + board.toString() + "\n");
	}
}
