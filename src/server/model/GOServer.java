package server.model;

import java.io.IOException;
import java.net.*;
import java.util.*;

import game.model.GameHandler;
import general.Protocol.General;

public class GOServer {
	// private static final String USAGE = "Usage: " + GOServer.class.getName();

	/** Starts a Server-application. */
	public static void main(String[] args) throws IOException {
		// if (args.length != 1) {
		// System.out.println(USAGE);
		// System.exit(0);
		// }

		GOServer server = new GOServer(General.DEFAULT_PORT);
		server.run();
	}

	private int port;
	private List<GOClientHandler> threads;

	public GOServer(int port) {
		this.port = port;
		this.threads = new Vector<GOClientHandler>();
	}

	public void run() {
		// try to open ServerSocket
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			// print(InetAddress.getLocalHost().getHostAddress() + "\n" +
			// InetAddress.getLocalHost().getHostName());
			// int i = 0;
			while (true) {
				Socket sock = serverSocket.accept();
				GOClientHandler handler = new GOClientHandler(this, sock);
				handler.announce();
				handler.start();
				addHandler(handler);
				// Creates game if there are enough clients.
				if (threads.size() > 1) {
					GOClientHandler p1 = threads.get(0);
					GOClientHandler p2 = threads.get(1);
					GameHandler game = new GameHandler(p1, p2, this);
					p1.setGameHandler(game);
					p2.setGameHandler(game);
					removeHandler(p1);
					removeHandler(p2);
					game.initiate();
					game.start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Prints message
	public void print(String message) {
		System.out.println(message);
	}

	// Sends message to every client in the list 'threads'.
	public void broadcast(String msg) {
		print(msg);
		(new Vector<>(threads)).forEach(handler -> handler.sendMessage(msg));
	}

	// Adds handler to list 'threads'.
	public void addHandler(GOClientHandler handler) {
		threads.add(handler);
	}

	// Removes handler from list 'threads'.
	public void removeHandler(GOClientHandler handler) {
		threads.remove(handler);
	}

	// Checks whether or not a given client name is in the list 'threads'.
	public boolean containsClientName(String clientName) {
		List<String> clientNames = new ArrayList<>();
		threads.forEach((client) -> clientNames.add(client.getClientName()));
		return clientNames.contains(clientName);
	}
} // end of class Server
