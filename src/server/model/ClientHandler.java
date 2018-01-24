package server.model;

import java.io.*;
import java.net.Socket;
import general.Protocol.*;
import game.model.GameHandler;

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
	private GameHandler game;
	private boolean inGame = false;

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
			server.broadcast("[" + clientName + " has entered the server.]");
			sendMessage("[Welcome to this server, " + clientName + ".]");
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
	public void setGameHandler(GameHandler game) {
		this.game = game;
		inGame = true;
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
	private void processInput(String input) {
		String[] splitInput = input.split("\\" + General.DELIMITER1);
		String result;
		if (inGame) {
			switch (splitInput[0]) {
			case Client.SETTINGS:
				result = "[CH: Settings given.]";
				game.setSettings(splitInput[1], Integer.parseInt(splitInput[2]));
				break;

			case Client.MOVE:
				result = "[CH: Move.]";
				game.makeMove(this, splitInput[1]);
				break;

			case Client.QUIT:
				result = "[CH: Quit.]";
				game.quit(this);
				break;

			case Client.CHAT:
				game.sendChat(this, input.substring(5));
				result = "[CH: Chat.]";
				break;

			default:
				result = "[CH: No clue what " + clientName + " wants.]";
			}
			server.print(result);
		} else {
			switch (splitInput[0]) {
			case Client.CHAT:
				server.broadcast(input);
				result = "[CH: Chat outside game.]";
				break;

			default:
				server.print(input);
				result = "[CH: Unknown command outside game.]";
			}
			server.print(result);
		}

	}

}
