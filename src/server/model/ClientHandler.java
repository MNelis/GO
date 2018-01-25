package server.model;

import java.io.*;
import java.net.Socket;
import general.Protocol.*;
import game.model.GOGame;

public class ClientHandler extends Thread {
	private static final String INCOMPATIBLEPROTOCOL = Server.ERROR + " " + Server.INCOMPATIBLEPROTOCOL + ": ";
	private static final String NAMETAKEN = Server.ERROR + " " + Server.NAMETAKEN + ": ";

	private GOServer server;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private String clientName;
	private int clientVersion;
	private String[] clientExtension;
	private GOGame game;
	private boolean inGame = false;
	private boolean currentTurn = false;

	/** Constructs client handler. */
	public ClientHandler(GOServer server, Socket sock) throws IOException {
		this.server = server;
		this.sock = sock;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}

	public void announce() throws IOException {
		String initialMessage = in.readLine();
		String[] splitMessage = initialMessage.split("\\" + General.DELIMITER1);

		clientName = splitMessage[1];
		clientVersion = Integer.parseInt(splitMessage[3]);
		clientExtension = new String[7];

		for (int i = 0; i < 7; i++) {
			clientExtension[i] = splitMessage[i + 5];
		}
		// Checks if protocol versions are compatible and name not taken.
		if (clientVersion != Server.VERSIONNO) {
			server.print(INCOMPATIBLEPROTOCOL + "Server v." + Server.VERSIONNO + ", Client v." + clientVersion);
			sendMessage(INCOMPATIBLEPROTOCOL + "Server v." + Server.VERSIONNO + ", Client v." + clientVersion
					+ ". Please reconnect with protocol version " + Server.VERSIONNO);
			disconnect();
		} else if (server.containsClientName(clientName)) {
			server.print(NAMETAKEN + clientName + " already taken.");
			sendMessage(
					NAMETAKEN + " Name '" + clientName + "' already taken, please reconnect " + "with another name.");
			disconnect();
		} else {
			server.broadcast("  [" + clientName + " has entered the server.]");
			sendMessage("  [Welcome to this server, " + clientName + ".]");
		}
	}

	/** Starts reading and processing messages from client to server. */
	public void run() {
		try {
			String msg = in.readLine();
			while (msg != null) {
				processInput(msg);
				msg = in.readLine();
			}
			shutdown();
		} catch (IOException e) {
			shutdown();
		}
	}

	/** Waits for input of the move. */
	private void waitForInputs() throws InterruptedException {
		synchronized (this) {
			wait();
		}
	}

	/** Sends message to client */
	public void sendMessage(String msg) {
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/** Gets name of client */
	public String getClientName() {
		return clientName;
	}

	/**
	 * Sets a game handler to the client handler (thus indirectly add client to game
	 * handler).
	 */
	public void setGameHandler(GOGame game) {
		this.game = game;
		inGame = true;
	}

	/**
	 * @throws InterruptedException
	 */
	public void makeMove() throws InterruptedException {
		currentTurn = true;
		sendMessage(">> Enter a move (MOVE <row> <column> or MOVE PASS):");
		waitForInputs();
		currentTurn = false;
	}

	/** Shutdown */
	private void shutdown() {
		server.removeHandler(this);
		server.print("[" + clientName + " has left.]");
	}

	/** Disconnent */
	private void disconnect() {
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Processes the input from client to server. */
	// TODO do we want to log every action from all clients?
	private void processInput(String input) {
		String[] splitInput = input.split("\\" + General.DELIMITER1);
		if (inGame) {
			switch (splitInput[0]) {
			case Client.CHAT:
				game.sendChat(this, input.substring(5));
				break;

			case Client.MOVE:
				// TODO check if valid move.
				if (currentTurn) {
					game.makeMove(this, splitInput[1]);
					synchronized (this) {
						notifyAll();
					}
				} else {
					sendMessage("  [It's not your turn. You cannot make a move.]");
				}
				break;

			case Client.QUIT:
				game.quit(this);
				break;

			case Client.SETTINGS:
				// TODO check is player is authorizided to set settings.
				game.setSettings(splitInput[1], Integer.parseInt(splitInput[2]));
				break;

			default:
			}
		} else {
			switch (splitInput[0]) {
			case Client.CHAT:
				server.broadcast(input);
				break;

			default:
				server.print(input);
			}
		}

	}

}
