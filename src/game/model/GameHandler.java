package game.model;

import java.util.List;
import server.model.*;
import java.util.Vector;
import general.Protocol.*;

public class GameHandler extends Thread {
	private List<ClientHandler> players;
	private ClientHandler p1;
	private ClientHandler p2;
	private GOServer server;
	private int numberOfPlayers = 2;
	private int dim;
	private String colorP1;
	private String colorP2;
	private boolean turnP1 = false;

	public GameHandler(ClientHandler p1, ClientHandler p2, GOServer server) {
		this.p1 = p1;
		this.p2 = p2;
		players = new Vector<ClientHandler>();
		players.add(this.p1);
		players.add(this.p2);
		this.server = server;
	}

	/** Initiates game, acquires settings from first player. */
	public void initiate() {
		String startMsg0 = Server.START + General.DELIMITER1 + numberOfPlayers;
		p1.sendMessage("[You entered a game with " + p2.getClientName() + ".]");
		p2.sendMessage("[You entered a game with " + p1.getClientName() + ".]");
		server.print("[" + p1.getClientName() + " and " + p2.getClientName() + " entered a game.]");
		// TODO some message about the usage in-game.
		p1.sendMessage(startMsg0);
		p2.sendMessage("[Waiting on the settings, that are chosen by " + p1.getClientName() + ".]");
		try {
			waitForInputs();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** Runs game. */
	public void run() {
		String startMsg1 = Server.START + General.DELIMITER1 + numberOfPlayers + General.DELIMITER1 + colorP1
				+ General.DELIMITER1 + dim + General.DELIMITER1 + p1.getClientName() + General.DELIMITER1
				+ p2.getClientName();
		String startMsg2 = Server.START + General.DELIMITER1 + numberOfPlayers + General.DELIMITER1 + colorP2
				+ General.DELIMITER1 + dim + General.DELIMITER1 + p1.getClientName() + General.DELIMITER1
				+ p2.getClientName();

		p1.sendMessage(startMsg1);
		p2.sendMessage(startMsg2);

		startGame();
		broadcast("[Nothing happens here yet.]");
	}

	/** Sets the settings. */
	public void setSettings(String color, int DIM) {
		dim = DIM;
		colorP1 = color;
		if (colorP1.equals(General.BLACK)) {
			colorP2 = General.WHITE;
		} else {
			colorP2 = General.BLACK;
		}
		synchronized (this) {
			notifyAll();
		}
	}

	/** Sends message to all player in the game. */
	public void broadcast(String msg) {
		(new Vector<>(players)).forEach(player -> player.sendMessage(msg));
	}

	/** Waits for input of the settings. */
	public void waitForInputs() throws InterruptedException {
		synchronized (this) {
			wait();
		}
	}

	/** Starts game */
	public void startGame() {
		// TODO start game here.
		server.print("[GH: Game will start here.]");
	}

	/** Currently: broadcasts a move made by a client in propper format. */
	public void makeMove(ClientHandler player, String move) {
		String[] splitMove = move.split(General.DELIMITER2);
		if (splitMove[0].equals(Client.PASS)) {
			broadcast(Server.TURN + General.DELIMITER1 + player.getClientName() + General.DELIMITER1 + Server.PASS
					+ General.DELIMITER1 + other(player).getClientName());
		} else if (splitMove[0].matches("\\d+") && splitMove[1].matches("\\d+")) {
			broadcast(Server.TURN + General.DELIMITER1 + player.getClientName() + General.DELIMITER1 + move
					+ General.DELIMITER1 + other(player).getClientName());
		}

		// TODO handle a move, check if it is valid etc.
		server.print("[GH: Stuff to handle a move will come here.]");
	}

	/** Handles a player who quits the game. */
	public void quit(ClientHandler player) {
		// TODO handle a client that quits.
		server.print("[GH: Stuff to handle a quit comes here.]");
	}

	/** Broadcasts chat message from given player to all the players in the game. */
	public void sendChat(ClientHandler player, String msg) {
		broadcast(Client.CHAT + General.DELIMITER1 + player.getClientName() + General.DELIMITER1 + msg);
	}

	/** Set the turn or something. Not sure what I wanted with this method. */
	public void setTurn(ClientHandler player) {
		// TODO
		server.print("[GH: Stuff to set the current turn.]");
	}

	/** Checks if it is the turn of the given player. */
	public boolean currentTurn(ClientHandler player) {
		if (player.equals(p1)) {
			return turnP1;
		} else {
			return !turnP1;
		}
	}

	/** Returns the other player than the given player (Assumes 2-player game). */
	public ClientHandler other(ClientHandler player) {
		if (p1.equals(player)) {
			return p2;
		} else {
			return p1;
		}
	}
}
