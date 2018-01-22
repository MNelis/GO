package game.model;

import general.Protocol.General;
import general.Protocol.Server;
import server.model.GOClientHandler;
import server.model.GOServer;

public class GameHandler extends Thread {
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
		this.server = server;
	}

	// Gets arguments necessary to start a game.
	public void initiate() {
		String startMsg0 = Server.START + General.DELIMITER1 + numberOfPlayers;
		// p1.sendMessage("You entered a game with " + p2.getClientName() + ".");
		// p2.sendMessage("You entered a game with " + p1.getClientName() + ".");
		server.print("[" + p1.getClientName() + " and " + p2.getClientName() + " entered a game.]");

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
		p1.sendMessage("Nothing happens here yet.");
		p2.sendMessage("Nothing happens here yet.");
	}

	public void setSettings(String color, int DIM) {
		dim = DIM;
		colorP1 = color;
		if (colorP1.equals(General.BLACK)) {
			colorP2 = General.WHITE;
		} 
		else {
			colorP2 = General.BLACK;
		}
		synchronized (this) {
			notifyAll();
		}
	}

	public void waitForInputs() throws InterruptedException {
		synchronized (this) {
			wait();
		}
	}
	
	public void startGame() {
		server.print("Game will start here.");
	}
	
	public void makeMove(GOClientHandler player) {
		server.print("Stufff to handle a move will come here.");
	}
	
	public void quit(GOClientHandler player) {
		server.print("Stuff to handle a quit comes here.");
	}
	
	public void sendChat(GOClientHandler player, String msg)	{
		server.print("Stuff to handle a chat comes here.");
	}
	
	public void setTurn(GOClientHandler player) {
		server.print("Stuff to set the current turn.");
	}
	
	public boolean currentTurn( GOClientHandler player) {
		return turnP1;
	}
}
