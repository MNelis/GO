package server.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class GOServer {
	private static final String USAGE = "Usage: " + GOServer.class.getName() + " <port number>";

	/** Starts a Server-application. */
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println(USAGE);
			System.exit(0);
		}

		GOServer server = new GOServer(Integer.parseInt(args[0]));
		server.run();
	}

	private int port;
	private List<ClientHandler> threads;

	public GOServer(int port) {
		this.port = port;
		this.threads = new Vector<ClientHandler>();
	}

	public void run() {

		// try to open ServerSocket
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			int i = 0;
			while (true) {
				Socket sock = serverSocket.accept();
				ClientHandler handler = new ClientHandler(this, sock);
				print("[ client no . " + (++i) + " connected .] ");
				handler.announce();
				handler.start();
				addHandler(handler);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void print(String message) {
		System.out.println(message);
	}

	public void broadcast(String msg) {
		print(msg);
		(new Vector<>(threads)).forEach(handler -> handler.sendMessage(msg));
	}

	public void addHandler(ClientHandler handler) {
		threads.add(handler);
	}

	public void removeHandler(ClientHandler handler) {
		threads.remove(handler);
	}

} // end of class Server
