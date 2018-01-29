package server.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import game.online.GOGame;
import general.Protocol.Client;
import general.Protocol.General;
import general.Protocol.Server;
import general.ServerMessages;

public class ClientHandler extends Thread {
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
			// TODO message for server.
			sendMessage(ServerMessages.incompatibleError(clientVersion));
			disconnect();
		} else if (server.containsClientName(clientName)) {
			// TODO message for server.
			sendMessage(ServerMessages.nameTakenError(clientName));
			disconnect();
		} else {
			// TODO message for server.
			sendMessage(ServerMessages.welcomeMessage(clientName));
			if (clientExtension[0].equals("1")) {
				sendMessage(ServerMessages.CHATENABLEDMESSAGE);
			}
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

	/** Sends message to client. */
	public void sendMessage(String msg) {
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/** Gets name of client. */
	public String getClientName() {
		return clientName;
	}

	/** Sets a game handler to the client handler (thus indirectly add client to
	 * game handler). */
	public void setGame(GOGame game) {
		this.game = game;
		inGame = true;
	}

	public void removeGame() {
		this.game = null;
		inGame = false;
		server.removeInGame(this);
		sendMessage(ServerMessages.CHAT + "Welcome back in the lobby.");
	}

	/** @throws InterruptedException */
	public void makeMove() throws InterruptedException {
		currentTurn = true;
		waitForInputs();
		currentTurn = false;
	}

	/** Shutdown. */
	private void shutdown() {
		server.removeFromLobby(this);
		server.print(clientName + " has left.");
	}

	/** Disconnect. */
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
					if (currentTurn) {
						game.makeMove(this, splitInput[1]);
						notifier();
					} else {
						sendMessage(ServerMessages.NOTYOURTURN);
					}
					break;

				case Client.QUIT:
					game.quit(this);
					notifier();
					break;

				case Client.SETTINGS:
					List<String> colors = new ArrayList<String>();
					colors.add(General.BLACK);
					colors.add(General.WHITE);
					if (splitInput.length == 3 && colors.contains(splitInput[1])
							&& splitInput[2].matches("\\d+")) {
						game.setSettings(splitInput[1], Integer.parseInt(splitInput[2]));
					} else {
						sendMessage(ServerMessages.INVALIDSETTINGS);
					}
					break;

				default:
					if (!input.equals("")) {
						sendMessage(ServerMessages.UNKNOWNCOMMAND + General.DELIMITER1 + input);
					}

			}
		} else {
			switch (splitInput[0]) {
				case Client.CHAT:
					server.broadcast(Client.CHAT + General.DELIMITER1 + this.getClientName()
							+ General.DELIMITER1 + input.substring(5));
					break;
				case Client.REQUESTGAME:
					server.print(ServerMessages.newRequestMessage(clientName));
					sendMessage(ServerMessages.REQUESTEDGAME);
					server.addRequestedGame(this);
					break;
				case Client.QUIT:
					server.removeRequestedGame(this);
					server.addToLobby(this);
					server.print(ServerMessages.quitRequestMessage(clientName));
					sendMessage(ServerMessages.QUITREQUEST);
					break;
				default:
					if (!input.equals("")) {
						sendMessage(ServerMessages.UNKNOWNCOMMAND + General.DELIMITER1 + input);
					}
			}
		}

	}

	public void notifier() {
		synchronized (this) {
			notifyAll();
		}
	}

}
