package server.model;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
	private GOServer server;
	private BufferedReader in;
	private BufferedWriter out;
	private String clientName;

	public ClientHandler(GOServer server, Socket sock) throws IOException {
		this.server = server;

		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}

	public void announce() throws IOException {
		clientName = in.readLine();
		server.broadcast("[" + clientName + " has entered ]");
	}

	public void run() {
		try {
			String msg = in.readLine();
			while (msg != null) {
				server.broadcast(clientName + ": " + msg);
				msg = in.readLine();
			}
			shutdown();
		} catch (IOException e) {
			shutdown();
		}

	}

	public void sendMessage(String msg) {
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	private void shutdown() {
		server.removeHandler(this);
		server.broadcast("[" + clientName + " has left ]");
	}

}
