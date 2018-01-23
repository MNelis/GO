package game.model;

public class GO {

	public static void main(String[] args) {
		// input <name p1> <name p2> <boardsize>
		Player[] player = new Player[2];
		Node[] stone = { Node.BLACK, Node.WHITE };

		for (int i = 0; i < 2; i++) {
			player[i] = new HumanPlayer(args[i], stone[i]);
		}
		int dim = Integer.parseInt(args[2]);
		(new Game(player[0], player[1], dim)).start();
	}
}
