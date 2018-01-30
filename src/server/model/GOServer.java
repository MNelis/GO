package server.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import errors.OtherException;
import game.online.GOGame;
import general.Protocol.General;
import general.ServerMessages;

public class GOServer {
	// private static final String USAGE = "Usage: " + GOServer.class.getName();
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

	public GOServer(int port) {
		this.port = port;
		lobby = new ArrayList<>();
		requestedGames = new ArrayList<>();
		inGame = new ArrayList<>();
	}

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

	/** Prints message. */
	private String getDate() {
		return "[" + dateFormat.format(new Date()) + "] ";
	}

	public void print(String message) {
		System.out.println(getDate() + message);
	}

	/** Sends message to every client in the list 'threads'. */
	public void broadcast(String msg) {
		lobby.forEach(handler -> handler.sendMessage(msg));
		requestedGames.forEach(handler -> handler.sendMessage(msg));
	}

	/** Adds handler to list 'threads'. */
	public void addToLobby(ClientHandler handler) {
		if (!lobby.contains(handler)) {
			lobby.add(handler);
		}
	}

	/** Removes handler from list 'threads'. */
	public void removeFromLobby(ClientHandler handler) {
		if (lobby.contains(handler)) {
			lobby.remove(handler);
		}
	}

	public void addRequestedGame(ClientHandler handler) throws OtherException {
		if (!requestedGames.contains(handler)) {
			requestedGames.add(handler);
			removeFromLobby(handler);
			checkEnoughPlayers();
		} else {
			throw new OtherException("You already requested a game.");
		}
	}

	public void removeRequestedGame(ClientHandler handler) throws OtherException {
		if (requestedGames.contains(handler)) {
			requestedGames.remove(handler);
		} else {
			throw new OtherException("You already revoked your request.");
		}

	}

	public void addInGame(ClientHandler handler) throws OtherException {
		inGame.add(handler);
		removeRequestedGame(handler);
	}

	public void removeInGame(ClientHandler handler) {
		inGame.remove(handler);
		lobby.add(handler);
	}

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

	/** Checks whether or not a given client name is in the list 'threads'. */
	public boolean containsClientName(String clientName) {
		List<String> clientNames = new ArrayList<>();
		lobby.forEach(client -> clientNames.add(client.getClientName()));
		requestedGames.forEach(client -> clientNames.add(client.getClientName()));
		inGame.forEach(client -> clientNames.add(client.getClientName()));
		return clientNames.contains(clientName);
	}
}
