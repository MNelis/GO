package game.model;

import server.model.*;
import general.Protocol.*;

public class GOGame extends Thread {
	private static final int NUMBEROFPLAYERS = 2;

	private ClientHandler[] players = new ClientHandler[NUMBEROFPLAYERS];
	private Stone[] colors = new Stone[NUMBEROFPLAYERS];
	private GOServer server;
	private Board board;
	private int dim;
	private int current = 0;

	public GOGame(ClientHandler p1, ClientHandler p2, GOServer server) {
		players[0] = p1;
		players[1] = p2;
		this.server = server;
	}

	/** Initiates game, acquires settings from first player. */
	public void initiate() {
		String startMsg0 = Server.START + General.DELIMITER1 + NUMBEROFPLAYERS;
		for (int i : new int[] { 0, 1 }) {
			players[i].sendMessage("  You entered a game with " + players[(i + 1) % 2].getClientName() + ".");
		}

		server.print("[" + players[0].getClientName() + " and " + players[1].getClientName() + " entered a game.]");
		// TODO some message about the usage in-game.
		players[0].sendMessage(startMsg0);
		players[1].sendMessage(
				"  Waiting on the settings. These are determined by " + players[0].getClientName() + ".");
		try {
			waitForInputs();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** Runs game. */
	public void run() {
		for (int i : new int[] { 0, 1 }) {
			players[i].sendMessage(Server.START + General.DELIMITER1 + NUMBEROFPLAYERS + General.DELIMITER1
					+ colors[i].toString() + General.DELIMITER1 + dim + General.DELIMITER1 + players[0].getClientName()
					+ General.DELIMITER1 + players[1].getClientName());
		}
		try {
			startGame();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** Sets the settings. */
	public void setSettings(String color, int DIM) {
		dim = DIM;
		if (color.equals(General.BLACK)) {
			colors[0] = Stone.BLACK;
			current = 0;
		} else {
			colors[0] = Stone.WHITE;
			current = 1;
		}
		colors[1] = colors[0].Other();
		synchronized (this) {
			notifyAll();
		}
	}

	/** Sends message to all player in the game. */
	private void broadcast(String msg) {
		for (ClientHandler p : players) {
			p.sendMessage(msg);
		}
	}

	/** Waits for input of the settings. */
	private void waitForInputs() throws InterruptedException {
		synchronized (this) {
			wait();
		}
	}

	/**
	 * Starts game
	 * 
	 * @throws InterruptedException
	 */
	private void startGame() throws InterruptedException {
		board = new Board(dim, true, true);
		server.print("[GH: Game starts here.]");
		play();
	}

	private void play() throws InterruptedException {
		while (!board.gameOver()) {
			players[current].makeMove();
			current = (current + 1) % NUMBEROFPLAYERS;
		}
		broadcast("  [Game over!]");
		int[] scores = board.determineScores();
		if (colors[0].equals(Stone.BLACK)) {
			broadcast("SCORES\n" + players[0].getClientName() + ":\t " + scores[0] + "\n" + players[1].getClientName()
					+ ":\t " + scores[1]);
		} else {
			broadcast("SCORES\n" + players[0].getClientName() + ":\t " + scores[1] + "\n" + players[1].getClientName()
					+ ":\t " + scores[0]);
		}
		players[0].removeGame();
		players[1].removeGame();
	}

	/** Currently: broadcasts a move made by a client in propper format. */
	public void makeMove(ClientHandler player, String move) {
		String[] splitMove = move.split(General.DELIMITER2);
		Stone color;
		if (player.equals(players[0])) {
			color = colors[0];
		} else {
			color = colors[1];
		}

		if (splitMove[0].equals(Client.PASS)) {
			broadcast(Server.TURN + General.DELIMITER1 + player.getClientName() + General.DELIMITER1 + Server.PASS
					+ General.DELIMITER1 + other(player).getClientName());

			board.increasePassCounter();

		} else if (splitMove[0].matches("\\d+") && splitMove[1].matches("\\d+")) {
			if (board.isValid(Integer.parseInt(splitMove[0]), Integer.parseInt(splitMove[1]), color)) {
				broadcast(Server.TURN + General.DELIMITER1 + player.getClientName() + General.DELIMITER1 + move
						+ General.DELIMITER1 + other(player).getClientName());
				int x = Integer.parseInt(splitMove[0]);
				int y = Integer.parseInt(splitMove[1]);
				board.addStone(x, y, color);
				board.resetPassCounter();
			} else {
				player.sendMessage("  [GH: invalid move.]");
			}
		}

		// TODO handle a move, check if it is valid etc.
		// server.print("[GH: Stuff to handle a move will come here.]");
	}

	/** Handles a player who quits the game. */
	public void quit(ClientHandler player) {
		board.quitGame();
		// TODO find out it current player quits or the other
		players[0].notifier();
		players[1].notifier();
		broadcast("  " + player.getClientName() + " could not handle the pressure and gave up.");
		broadcast(Server.ENDGAME);
	}

	/** Broadcasts chat message from given player to all the players in the game. */
	public void sendChat(ClientHandler player, String msg) {
		broadcast(Client.CHAT + General.DELIMITER1 + player.getClientName() + General.DELIMITER1 + msg);
	}

	/** Returns the other player than the given player (Assumes 2-player game). */
	private ClientHandler other(ClientHandler player) {
		if (players[0].equals(player)) {
			return players[1];
		} else {
			return players[0];
		}
	}
}
