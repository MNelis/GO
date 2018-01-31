package server.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import game.online.GOGame;
import general.Protocol.General;
import general.errors.OtherException;
import server.ServerMessages;

public class GOServer {
	private int port;
	private List<ClientHandler> lobby;
	private List<ClientHandler> requestedGames;
	private List<ClientHandler> inGame;
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	/** Starts a Server-application. */
	public static void main(String[] args) throws IOException {
		GOServer server = new GOServer(General.DEFAULT_PORT);
		server.run();
	}

	/** Constructs new GOServer instance for given port.
	 * @param port port number. */
	public GOServer(int port) {
		this.port = port;
		lobby = new ArrayList<>();
		requestedGames = new ArrayList<>();
		inGame = new ArrayList<>();
	}

	/** Runs server, starts ClientHandler for each new client. */
	private void run() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			print(ServerMessages.STARTSERVER);
			while (true) {
				Socket sock = serverSocket.accept();
				ClientHandler handler = new ClientHandler(this, sock);
				handler.announce();
				handler.start();
				print(ServerMessages.newClientMessage(handler.getClientName()));
				addToLobby(handler);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Gets current date/time.
	 * @return */
	private String getDate() {
		return "[" + dateFormat.format(new Date()) + "] ";
	}

	/** Prints message with date/time.
	 * @param message */
	public void print(String message) {
		System.out.println(getDate() + message);
	}

	/** Sends message to every client in lobby and requestedGame.
	 * @param msg message */
	public void broadcast(String msg) {
		lobby.forEach(handler -> handler.sendMessage(msg));
		requestedGames.forEach(handler -> handler.sendMessage(msg));
	}

	/** Adds handler to lobby.
	 * @param handler ClientHandler of the client. */
	public void addToLobby(ClientHandler handler) {
		if (!lobby.contains(handler)) {
			lobby.add(handler);
		}
	}

	/** Removes client from lobby.
	 * @param handler ClientHandler of the client. */
	public void removeFromLobby(ClientHandler handler) {
		if (lobby.contains(handler)) {
			lobby.remove(handler);
		}
	}

	/** Adds client to requestedGame, removes from lobby and checks it enough
	 * clients in requestedGame to start a game.
	 * @param handler ClientHandler of the client.
	 * @throws OtherException if already in requestedGame. */
	public void addRequestedGame(ClientHandler handler) throws OtherException {
		if (!requestedGames.contains(handler)) {
			requestedGames.add(handler);
			removeFromLobby(handler);
			checkEnoughPlayers();
		} else {
			throw new OtherException("You already requested a game.");
		}
	}

	/** Removes client from requestedGame if on that list.
	 * @param handler ClientHandler of the client.
	 * @throws OtherException if client not on the list. */
	public void removeRequestedGame(ClientHandler handler) throws OtherException {
		if (requestedGames.contains(handler)) {
			requestedGames.remove(handler);
		} else {
			throw new OtherException("You already revoked your request.");
		}

	}

	/** Adds client to inGame and removes it from requestedGame.
	 * @param handler ClientHandler of the client.
	 * @throws OtherException */
	private void addInGame(ClientHandler handler) throws OtherException {
		inGame.add(handler);
		removeRequestedGame(handler);
	}

	/** Removes client from list inGame and adds to lobby.
	 * @param handler ClientHandler of the client. */
	public void removeInGame(ClientHandler handler) {
		inGame.remove(handler);
		lobby.add(handler);
	}

	/** Checks if there are enough clients in the list requestedGame to start a
	 * game. If so, a game is started.
	 * @throws OtherException */
	private void checkEnoughPlayers() throws OtherException {
		if (requestedGames.size() > 1) {
			ClientHandler[] players = {requestedGames.get(0), requestedGames.get(1)};
			print(ServerMessages.gameStartedMessage(players));
			GOGame game = new GOGame(players[0], players[1]);
			for (ClientHandler p : players) {
				p.setGame(game);
				addInGame(p);
			}
			game.initiate();
			game.start();
		}
	}

	/** Checks whether or not a given client name is already taken.
	 * @param clientName name of the client.
	 * @return true if taken, false otherwise. */
	public boolean containsClientName(String clientName) {
		List<String> clientNames = new ArrayList<>();
		lobby.forEach(client -> clientNames.add(client.getClientName()));
		requestedGames.forEach(client -> clientNames.add(client.getClientName()));
		inGame.forEach(client -> clientNames.add(client.getClientName()));
		return clientNames.contains(clientName);
	}
}
