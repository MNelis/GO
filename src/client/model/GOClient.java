package client.model;

import java.io.*;
import java.net.*;
import general.Protocol.General;
import general.Protocol.Server;
import general.Protocol.Client;

public class GOClient extends Thread {
	private static final String USAGE = "Usage : " + GOClient.class.getName() + " <name> <address>";
	private static final String EXTENSIONS = "0$0$0$0$0$0$0";

	/** Start een Client-applicatie op. */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println(USAGE);
			System.exit(0);
		}
		InetAddress host = null;
		int port = 0;
		try {
			host = InetAddress.getByName(args[1]);
		} catch (UnknownHostException e) {
			print("ERROR: invalid hostname.");
			System.exit(0);
		}
		try {
			port = General.DEFAULT_PORT;
		} catch (NumberFormatException e) {
			print("ERROR: invalid defeault portnumber.");
			System.exit(0);
		}
		try {
			GOClient client = new GOClient(args[0], host, port);
			// first message to the server:
			String initialMessage = Client.NAME + General.DELIMITER1 + args[0] + General.DELIMITER1 
					+ Client.VERSION + General.DELIMITER1 + Client.VERSIONNO + General.DELIMITER1 
					+ Client.EXTENSIONS + General.DELIMITER1 + EXTENSIONS;

			client.sendMessage(initialMessage);
			client.start();

			// sends
			do {
				String input = readString("");
				client.sendMessage(input);
			} while (true);
		} catch (IOException e) {
			print("ERROR: couldn �t construct a client object !");
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

	// Reads, processes, and prints incoming messages
	public void run() {
		try {
			String msg = in.readLine();
			while (msg != null) {
				print(processInput(msg));
				msg = in.readLine();
			}
			shutdown();
		} catch (IOException e) {
			shutdown();
		}
	}

	// Processes and sends messages to server.
	public void sendMessage(String msg) {
		try {
			out.write(processOutput(msg));
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	public void shutdown() {
		print("Closing socket connection.");
		try {
			sock.close();
		} catch (IOException e) {
			print("ERROR: error closing the socket connection!");
		}
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

	// processes input from server to something readable.
	public String processInput(String msg) {
		String[] splitMessage = msg.split("\\" + General.DELIMITER1);
		switch (splitMessage[0]) {
		case Server.CHAT:
			return splitMessage[1] + ": " + (msg.replaceFirst("\\" + General.DELIMITER1, " ")).substring(2);
		case Server.ENDGAME:
			// gives message that the game has ended
			return msg.replace(General.DELIMITER1, " ");
		case Server.ERROR:
			// gives an error message
			return msg.replace(General.DELIMITER1, " ");
		case Server.START:
			if (splitMessage.length == 2) {

				return "Entered a game with " + splitMessage[1] + " players. Set color and " + "boardsize: ("
						+ Client.SETTINGS + " <color> <boardsize>)";
			} else {
				// TODO start game
				startGame();
				return msg.replace(General.DELIMITER1, " ");
			}

		case Server.TURN:
			// gives turn to a player
			return msg.replace(General.DELIMITER1, " ");
		default:
			return msg.replace(General.DELIMITER1, " ");
		}

	}

	// processes input given by the client to propper format
	public String processOutput(String msg) {
		String[] splitMessage = msg.split(" ");
		switch (splitMessage[0]) {
		case Client.CHAT:
			return msg.replaceFirst(" ", "\\" + General.DELIMITER1);
		case Client.MOVE: // TODO specify move format
			return ((msg.replaceFirst(" ", "\\" + General.DELIMITER1)).replaceFirst(" ", General.DELIMITER2))
					.replace(" ", General.DELIMITER1);
		default:
			return msg.replace(" ", General.DELIMITER1);
		}

	}

	public String getClientName() {
		return clientName;
	}
	
	public void startGame() {
		print("Start GO!");
	}

}