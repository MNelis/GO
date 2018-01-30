package game.online;

import errors.OtherException;
import game.model.Board;
import game.model.Stone;
import general.Protocol.Client;
import general.Protocol.General;
import general.Protocol.Server;
import general.ServerMessages;
import server.model.ClientHandler;

public class GOGame extends Thread {
	private static final int NUMBEROFPLAYERS = 2;

	private ClientHandler[] players = new ClientHandler[NUMBEROFPLAYERS];
	private Stone[] colors = new Stone[NUMBEROFPLAYERS];
	private Board board;
	private int dim;
	private int current = 0;
	private boolean gameStarted = false;
	private boolean abortedGame = false;

	/** Construct new GOGame with two players.
	 * @param p1
	 * @param p2 */
	public GOGame(ClientHandler p1, ClientHandler p2) {
		players[0] = p1;
		players[1] = p2;
	}

	/** Initiates game, acquires settings from first player. */
	public void initiate() {
		String startMsg0 = Server.START + General.DELIMITER1 + NUMBEROFPLAYERS;
		for (int i : new int[]{0, 1}) {
			players[i].sendMessage(ServerMessages.CHAT + "You entered a game with "
					+ players[(i + 1) % 2].getClientName() + ".");
		}
		broadcast(ServerMessages.CHAT
				+ "Enter CHAT <your message> to sent a message to your opponent.");
		players[0].sendMessage(startMsg0);
		players[1].sendMessage(
				ServerMessages.CHAT + "Waiting on the settings. These are determined by "
						+ players[0].getClientName() + ".");
		try {
			waitForSettings();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** Runs game. */
	public void run() {
		for (int i : new int[]{0, 1}) {
			players[i].sendMessage(Server.START + General.DELIMITER1 + NUMBEROFPLAYERS
					+ General.DELIMITER1 + colors[i].toString() + General.DELIMITER1 + dim
					+ General.DELIMITER1 + players[0].getClientName() + General.DELIMITER1
					+ players[1].getClientName());
		}
		try {
			startGame();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** Sets the settings.
	 * @param color color of the stone.
	 * @param size dimension of the board. */
	public void setSettings(String color, int size) {
		dim = size;
		if (color.equals(General.BLACK)) {
			colors[0] = Stone.BLACK;
			current = 0;
		} else {
			colors[0] = Stone.WHITE;
			current = 1;
		}
		colors[1] = colors[0].other();
		synchronized (this) {
			notifyAll();
		}
	}

	/** Sends message to all player in the game.
	 * @param msg message */
	private void broadcast(String msg) {
		for (ClientHandler p : players) {
			p.sendMessage(msg);
		}
	}

	/** Waits for input of the settings. */
	private void waitForSettings() throws InterruptedException {
		synchronized (this) {
			wait();
		}
	}

	/** Starts game.
	 * @throws InterruptedException */
	private void startGame() throws InterruptedException {
		players[0].startedGame();
		players[1].startedGame();
		board = new Board(dim, false, true);
		gameStarted = true;
		if (colors[0].equals(Stone.BLACK)) {
			broadcast(Server.TURN + General.DELIMITER1 + players[0].getClientName()
					+ General.DELIMITER1 + Server.FIRST + General.DELIMITER1
					+ players[0].getClientName());
		} else {
			broadcast(Server.TURN + General.DELIMITER1 + players[1].getClientName()
					+ General.DELIMITER1 + Server.FIRST + General.DELIMITER1
					+ players[1].getClientName());
		}
		play();
	}

	/** Plays the game until it is game over.
	 * @throws InterruptedException */
	private void play() throws InterruptedException {
		while (!board.gameOver()) {
			players[current].makeMove();
			current = (current + 1) % NUMBEROFPLAYERS;
		}
		gameStarted = false;

		if (!abortedGame) {
			int[] scores = board.determineScores();
			if (scores[0] > scores[1]) {
				if (colors[0].equals(Stone.BLACK)) {
					broadcast(ServerMessages.finishedGame(scores[0], scores[1], players[0],
							players[1]));
				} else {
					broadcast(ServerMessages.finishedGame(scores[1], scores[0], players[1],
							players[0]));
				}
			} else {
				if (colors[0].equals(Stone.BLACK)) {
					broadcast(ServerMessages.finishedGame(scores[1], scores[0], players[1],
							players[0]));
				} else {
					broadcast(ServerMessages.finishedGame(scores[0], scores[1], players[0],
							players[1]));
				}
			}
		}
		players[0].removeGame();
		players[1].removeGame();
	}

	/** Broadcasts a move made by a player in correct format.
	 * @param player player.
	 * @param move move.
	 * @throws OtherException */
	public void makeMove(ClientHandler player, String move) throws OtherException {
		String[] splitMove = move.split(General.DELIMITER2);
		Stone color;
		if (player.equals(players[0])) {
			color = colors[0];
		} else {
			color = colors[1];
		}
		if (splitMove[0].equals(Client.PASS)) {
			board.increasePassCounter();
			broadcast(Server.TURN + General.DELIMITER1 + player.getClientName() + General.DELIMITER1
					+ Server.PASS + General.DELIMITER1 + other(player).getClientName());

		} else if (splitMove[0].matches("\\d+") && splitMove[1].matches("\\d+")) {
			if (board.isValid(Integer.parseInt(splitMove[0]), Integer.parseInt(splitMove[1]),
					color)) {
				broadcast(Server.TURN + General.DELIMITER1 + player.getClientName()
						+ General.DELIMITER1 + move + General.DELIMITER1
						+ other(player).getClientName());
				int x = Integer.parseInt(splitMove[0]);
				int y = Integer.parseInt(splitMove[1]);
				board.addStone(x, y, color);
				board.resetPassCounter();
			} else {
				throw new OtherException(ServerMessages.INVALIDMOVE);
			}
		}
	}

	/** Handles a player who quits the game.
	 * @param player player who quits. */
	public void quit(ClientHandler player) {
		abortedGame = true;
		broadcast(ServerMessages.CHAT + player.getClientName()
				+ " could not handle the pressure and gave up.");
		if (gameStarted) {
			int[] scores = board.determineScores();
			board.quitGame();
			players[0].notifier();
			players[1].notifier();
			gameStarted = false;
			if (players[0].equals(player)) {
				scores[0] = 0;
				broadcast(ServerMessages.quitGame(scores[1], scores[0], players[1], players[0]));
			} else {
				scores[1] = 0;
				broadcast(ServerMessages.quitGame(scores[0], scores[1], players[0], players[1]));
			}
		}
	}

	/** Broadcasts chat message from given player to all the players in the game.
	 * @param player player.
	 * @param msg message. */
	public void sendChat(ClientHandler player, String msg) {
		broadcast(Client.CHAT + General.DELIMITER1 + player.getClientName() + General.DELIMITER1
				+ msg);
	}

	/** Returns the other player than the given player (Assumes 2-player game).
	 * @param player given player.
	 * @return other player. */
	private ClientHandler other(ClientHandler player) {
		if (players[0].equals(player)) {
			return players[1];
		} else {
			return players[0];
		}
	}
}
