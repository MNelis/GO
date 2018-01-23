package server.model;

import java.io.*;
import java.net.Socket;
import game.model.GameHandler;
import general.Protocol.Client;
import general.Protocol.General;
import general.Protocol.Server;

public class GOClientHandler extends Thread {
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

	public GOClientHandler(GOServer server, Socket sock) throws IOException {
		this.server = server;
		this.sock = sock;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}

	public void announce() throws IOException {
		// Reads initial message
		// TODO make readable message
		String initialMessage = in.readLine();
		// server.print(initialMessage);

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
			sendMessage("[Welcome to this server.]");
			// TODO add some message about the usage.
			// server.print("[" + initialMessage + "]");
		}
	}

	// Sends message to client
	public void sendMessage(String msg) {
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	// Get name of the client
	public String getClientName() {
		return clientName;
	}

	// Sets a game handler to the client handler (thus indirectly add client to game
	// handler)
	public void setGameHandler(GameHandler game) {
		this.game = game;
		inGame = true;
	}

	private void shutdown() {
		server.removeHandler(this);
		server.print("[" + clientName + " has left.]");
	}

	private void disconnect() {
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			// reads and processes message from the client to the server.
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

	// Processes the input from client
	private void processInput(String input) {
		String[] splitInput = input.split("\\" + General.DELIMITER1);
		String result;
		server.print(splitInput[0] + " " + splitInput[1]);
		if (inGame) {
			switch (splitInput[0]) {
			case Client.SETTINGS:
				result = "Settings given.";
				game.setSettings(splitInput[1], Integer.parseInt(splitInput[2]));
				break;

			case Client.MOVE:
				result = "Move.";
				game.makeMove(this, splitInput[1]);
				break;

			case Client.QUIT:
				result = "Quit.";
				game.quit(this);
				break;

			case Client.CHAT:
				game.sendChat(this, input.substring(5));
				result = "Chat.";
				break;

			default:
				result = "No clue what " + clientName + " wants.";
			}
			server.print(result);
		} else {
			switch (splitInput[0]) {
			case Client.CHAT:
				server.broadcast(input);
				result = "Chat outside game.";
				break;

			default:
				server.print(input);
				result = "Unknown command outside game.";
			}
			server.print(result);
		}

	}

}
