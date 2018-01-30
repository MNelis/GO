package game.offline;

import game.model.Stone;
import game.players.ComputerPlayer;
import game.players.HumanPlayer;
import game.players.NaiveStrategy;
import game.players.Player;

public class OfflineGO {
	final static String USAGE = "USAGE: " + OfflineGO.class.getName()
			+ " <name p0> <name p1> <board size> \nEnter -N as name to construct a computerplayer.";

	public static void main(String[] args) {
		// input <name p1> <name p2> <board size>
		if (args.length == 3 && args[2].matches("\\d+") && Integer.parseInt(args[2]) > 0
				&& Integer.parseInt(args[2]) < 20) {
			Player[] player = new Player[2];
			Stone[] stone = {Stone.BLACK, Stone.WHITE};

			for (int i = 0; i < 2; i++) {
				if (args[i].equals("-N")) {
					player[i] = new ComputerPlayer(stone[i], new NaiveStrategy());
				} else {
					player[i] = new HumanPlayer(args[i], stone[i]);
				}

			}
			int dim = Integer.parseInt(args[2]);
			(new OfflineGOGame(player[0], player[1], dim)).start();
		} else {
			System.err.println(USAGE);
		}

	}
}
