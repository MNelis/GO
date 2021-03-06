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
import general.errors.IncompatibleProtocolException;
import general.errors.InvalidMoveException;
import general.errors.NameTakenException;
import general.errors.OtherException;
import general.errors.UnknownCommandException;
import server.ServerMessages;

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
	private boolean gameStarted = false;
	private boolean currentTurn = false;
	private boolean hasChat = false;

	/** Constructs client handler.
	 * @param server given server.
	 * @param sock given socket.
	 * @throws IOException */
	public ClientHandler(GOServer server, Socket sock) throws IOException {
		this.server = server;
		this.sock = sock;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}

	/** Initiates connection with the client.
	 * @throws IOException
	 * @throws OtherException */
	public void announce() throws IOException, OtherException {

		sendMessage(Server.NAME + General.DELIMITER1 + "FrauNelis" + General.DELIMITER1
				+ Server.VERSION + General.DELIMITER1 + Server.VERSIONNO + General.DELIMITER1
				+ Server.EXTENSIONS + General.DELIMITER1 + "1$0$0$0$0$0$0");

		String initialMessage = in.readLine();
		server.print(initialMessage);
		String[] splitMessage = initialMessage.split("\\" + General.DELIMITER1);

		clientName = splitMessage[1];
		clientVersion = Integer.parseInt(splitMessage[3]);
		clientExtension = new String[7];

		for (int i = 0; i < 7; i++) {
			clientExtension[i] = splitMessage[i + 5];
		}
		// Checks if protocol versions are compatible and name not taken.
		try {
			if (clientVersion != Server.VERSIONNO) {
				throw new IncompatibleProtocolException(
						ServerMessages.incompatibleError(clientVersion));
			} else if (server.containsClientName(clientName)) {
				throw new NameTakenException(ServerMessages.nameTakenError(clientName));
			} else {
				if (clientExtension[0].equals("1")) {
					hasChat = true;
				}
			}
		} catch (IncompatibleProtocolException | NameTakenException e) {
			server.print(e.getMessage());
			sendMessage(e.getMessage());
			disconnect();
			throw new OtherException("Client couldnot connect.");
		}

	}

	/** Starts reading and processing messages from client to server. */
	public void run() {
		try {
			String msg = in.readLine();
			while (msg != null) {
				try {
					processInput(msg);
				} catch (UnknownCommandException | OtherException | InvalidMoveException e) {
					server.print(clientName + ": " + e.getMessage());
					sendMessage(e.getMessage());
				}
				msg = in.readLine();
			}
			if (gameStarted) {
				game.quit(this);
			}
			server.removeRequestedGame(this);
			server.removeInGame(this);
			server.removeFromLobby(this);
			shutdown();
		} catch (IOException | OtherException e) {
			if (gameStarted) {
				game.quit(this);
			}
			try {
				shutdown();
			} catch (OtherException e1) {

			}

		}
	}

	/** Waits for input of the move. */
	private void waitForMove() throws InterruptedException {
		synchronized (this) {
			wait();
		}
	}

	/** Sends message to client.
	 * @param msg message. */
	public void sendMessage(String msg) {
		try {
			String[] split = msg.split("\\" + General.DELIMITER1);
			if (!(!hasChat && split[0].equals(Server.CHAT))) {
				out.write(msg);
				out.newLine();
				out.flush();
			}

		} catch (IOException e) {
			try {
				shutdown();
			} catch (OtherException e1) {

			}
		}
	}

	/** Gets name of client.
	 * @return clientName. */
	public String getClientName() {
		return clientName;
	}

	/** Sets a game to the client handler (thus indirectly add client to game).
	 * @param game game. */
	public void setGame(GOGame game) {
		this.game = game;
		inGame = true;
	}

	/** Sets startedGame to true. */
	public void startedGame() {
		gameStarted = true;
	}

	/** Removes game from client. */
	public void removeGame() {
		this.game = null;
		inGame = false;
		gameStarted = false;
		server.removeInGame(this);
		server.addToLobby(this);
		server.print(clientName + " left the game and returned to the lobby.");
	}

	/** Sets currentTurn to true while until client makes a move.
	 * @throws InterruptedException */
	public void makeMove() throws InterruptedException {
		currentTurn = true;
		waitForMove();
		currentTurn = false;
	}

	/** Tells server that client has left.
	 * @throws OtherException */
	private void shutdown() throws OtherException {
		server.removeName(this);
		server.print(clientName + " has left.");
	}

	/** Disconnect the client. */
	private void disconnect() {
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Processes the input from client to server.
	 * @param input input message.
	 * @throws UnknownCommandException
	 * @throws OtherException
	 * @throws InvalidMoveException */
	private void processInput(String input)
			throws UnknownCommandException, OtherException, InvalidMoveException {
		String[] splitInput = input.split("\\" + General.DELIMITER1);

		if (inGame && gameStarted) {
			switch (splitInput[0]) {
				case Client.CHAT:
					if (input.length() > 4) {
						server.print(clientName + " sends a chat message in game.");
						game.sendChat(this, input.substring(5));
					}
					break;

				case Client.EXIT:
					game.quit(this);
					disconnect();
					break;

				case Client.MOVE:
					if (currentTurn) {
						game.makeMove(this, splitInput[1]);
						notifier();
					} else {
						throw new OtherException(ServerMessages.NOTYOURTURN);
					}
					break;

				case Client.QUIT:
					server.print(clientName + "quits the game.");
					game.quit(this);
					notifier();
					break;

				default:
					if (!input.equals("")) {
						throw new UnknownCommandException();
					}
			}
		} else if (inGame && !gameStarted) {
			switch (splitInput[0]) {
				case Client.CHAT:
					if (input.length() > 4) {
						server.print(clientName + " sends a chat message in game.");
						game.sendChat(this, input.substring(5));
					}
					break;

				case Client.EXIT:
					disconnect();
					break;

				case Client.MOVE:
					throw new OtherException("Game has not started yet.");

				case Client.QUIT:
					server.print(clientName + "quits the game.");
					game.quit(this);
					break;

				case Client.SETTINGS:
					List<String> colors = new ArrayList<String>();
					colors.add(General.BLACK);
					colors.add(General.WHITE);
					if (splitInput.length == 3 && colors.contains(splitInput[1])
							&& splitInput[2].matches("\\d+")) {
						int dimension = Integer.parseInt(splitInput[2]);
						if (Integer.parseInt(splitInput[2]) < 5) {
							dimension = 5;
						} else if (Integer.parseInt(splitInput[2]) > 19) {
							dimension = 19;
						}
						game.setSettings(splitInput[1], dimension);
					} else {
						throw new OtherException(ServerMessages.INVALIDSETTINGS);
					}
					break;

				default:
					if (!input.equals("")) {
						throw new UnknownCommandException();
					}
			}

		} else {
			switch (splitInput[0]) {
				case Client.CHAT:
					if (input.length() > 4) {
						server.print(clientName + " sends a chat message in the lobby.");
						server.broadcast(Client.CHAT + General.DELIMITER1 + this.getClientName()
								+ General.DELIMITER1 + input.substring(5));
					}
					break;
				case Client.EXIT:
					// shutdown();
					disconnect();
					break;

				// case Client.MOVE:
				// break;

				// case Client.QUIT:
				// server.removeRequestedGame(this);
				// server.addToLobby(this);
				// server.print(ServerMessages.quitRequestMessage(clientName));
				// sendMessage(ServerMessages.QUITREQUEST);
				// break;

				case Client.REQUESTGAME:
					if (splitInput.length == 3 && splitInput[1].equals("2")
							&& splitInput[2].equals(Client.RANDOM)) {
						server.print(ServerMessages.newRequestMessage(clientName));
						server.addRequestedGame(this);
						if (hasChat) {
							sendMessage(ServerMessages.REQUESTEDGAME);
						}
						break;
					} else {
						throw new UnknownCommandException("Unknown game request. ");
					}

				default:
					if (!input.equals("")) {
						throw new UnknownCommandException();
					}
			}
		}

	}

	/** Notifies that move has been made. */
	public void notifier() {
		synchronized (this) {
			notifyAll();
		}
	}

}
