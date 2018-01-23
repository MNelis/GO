package game.model;

import java.util.List;
import java.util.Vector;

import general.Protocol.Client;
import general.Protocol.General;
import general.Protocol.Server;
import server.model.GOClientHandler;
import server.model.GOServer;

public class GameHandler extends Thread {
	private List<GOClientHandler> players;
	private GOClientHandler p1;
	private GOClientHandler p2;
	private GOServer server;
	private int numberOfPlayers = 2;
	private int dim;
	private String colorP1;
	private String colorP2;
	private boolean turnP1 = false;

	public GameHandler(GOClientHandler p1, GOClientHandler p2, GOServer server) {
		this.p1 = p1;
		this.p2 = p2;
		players = new Vector<GOClientHandler>();
		players.add(this.p1);
		players.add(this.p2);
		this.server = server;
	}

	// Gets arguments necessary to start a game.
	public void initiate() {
		String startMsg0 = Server.START + General.DELIMITER1 + numberOfPlayers;
		// p1.sendMessage("You entered a game with " + p2.getClientName() + ".");
		// p2.sendMessage("You entered a game with " + p1.getClientName() + ".");
		server.print("[" + p1.getClientName() + " and " + p2.getClientName() + " entered a game.]");
		// TODO some message about the usage in-game.
		p1.sendMessage(startMsg0);
		try {
			waitForInputs();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Runs game.
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

	public void broadcast(String msg) {
		(new Vector<>(players)).forEach(player -> player.sendMessage(msg));
	}

	public void waitForInputs() throws InterruptedException {
		synchronized (this) {
			wait();
		}
	}

	public void startGame() {
		// Node stone;
		// if (colorP1.equals(General.BLACK)) {
		// stone = Node.BLACK;
		// }
		// else {
		// stone = Node.WHITE;
		// }
		// Player p1 = new HumanPlayer(this.p1.getClientName(), stone);
		// Player p2 = new HumanPlayer(this.p2.getClientName(), stone.Other());
		//
		// (new Game(p1, p2, dim)).start();

		server.print("[Game will start here.]");
	}

	public void makeMove(GOClientHandler player, String move) {
		String[] splitMove = move.split(General.DELIMITER2);
		if (splitMove[0].equals(Client.PASS)) {
			broadcast(Server.TURN + General.DELIMITER1 + player.getClientName() + General.DELIMITER1 + Server.PASS
					+ General.DELIMITER1 + other(player).getClientName());
		} else if (splitMove[0].matches("\\d+") && splitMove[1].matches("\\d+")) {
			broadcast(Server.TURN + General.DELIMITER1 + player.getClientName() + General.DELIMITER1 + move
					+ General.DELIMITER1 + other(player).getClientName());
		}

		server.print("[Stufff to handle a move will come here.]");
	}

	public void quit(GOClientHandler player) {
		server.print("[Stuff to handle a quit comes here.]");
	}

	public void sendChat(GOClientHandler player, String msg) {
		broadcast(Client.CHAT + General.DELIMITER1 + player.getClientName() + General.DELIMITER1 + msg);
	}

	public void setTurn(GOClientHandler player) {
		server.print("[Stuff to set the current turn.]");
	}

	public boolean currentTurn(GOClientHandler player) {
		if (player.equals(p1)) {
			return turnP1;
		} else {
			return !turnP1;
		}
	}

	public GOClientHandler other(GOClientHandler player) {
		if (p1.equals(player)) {
			return p2;
		} else {
			return p1;
		}
	}
}
