package client.model;

import java.io.*;
import java.net.*;

public class GOClient extends Thread {
	private static final String USAGE = " usage : java week7 . cmdchat . Client <name > <address > <port >";

	/** Start een Client-applicatie op. */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println(USAGE);
			System.exit(0);
		}
		InetAddress host = null;
		int port = 0;
		try {
			host = InetAddress.getByName(args[1]);
		} catch (UnknownHostException e) {
			print(" ERROR : no valid hostname !");
			System.exit(0);
		}
		try {
			port = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			print(" ERROR : no valid portnummer !");
			System.exit(0);
		}
		try {
			GOClient client = new GOClient(args[0], host, port);
			client.sendMessage(args[0]);
			client.start();
			do {
				String input = readString("");
				client.sendMessage(input);
			} while (true);
		} catch (IOException e) {
			print(" ERROR : couldn ’t construct a client object !");
			System.exit(0);
		}
	}

	private String clientName;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;

	public GOClient(String name, InetAddress host, int port) throws IOException {
		this.clientName = name;
		sock = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}

	public void run() {
		try {
			String msg = in.readLine();
			while (msg != null) {
				print(msg);
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

	public void shutdown() {
		print(" Closing socket connection ... ");
		try {
			sock.close();
		} catch (IOException e) {
			print(" ERROR : error closing the socket connection !");
		}
	}

	public String getClientName() {
		return clientName;
	}

	private static void print(String message) {
		System.out.println(message);
	}

	public static String readString(String tekst) {
		System.out.print(tekst);
		String antw = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			antw = in.readLine();
		} catch (IOException e) {
		}
		return (antw == null) ? "" : antw;
	}

}
