package client.model;

import java.io.*;
import java.net.*;
import game.model.*;
import client.viewer.*;
import general.Protocol.*;

public class GOClient extends Thread {
	private static final String USAGE = "Usage : " + GOClient.class.getName() + " <name> <address>";

	/** Starts the client application. */
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
			System.err.println("ERROR: invalid hostname.");
			System.exit(0);
		}
		try {
			port = General.DEFAULT_PORT;
		} catch (NumberFormatException e) {
			System.err.println("ERROR: invalid defeault portnumber.");
			System.exit(0);
		}
		try {
			GOClient client = new GOClient(args[0], host, port);
			client.sendMessage(ClientMessages.initMessage(args[0]));
			client.start();
			client.getTUI().start();
		} catch (IOException e) {
			System.err.println("ERROR: couldn't construct a client object.");
			System.exit(0);
		}
	}

	private String clientName;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private ClientView goTUI;
	private Board board;
	private Stone stone;

	/** Constructs new client with TUI. */
	public GOClient(String name, InetAddress host, int port) throws IOException {
		this.clientName = name;
		sock = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		goTUI = new GOClientTUI(this);
	}

	/** Reads, processes, and prints incoming messages from server to client. */
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

	/** Processes and sends given message from client to server. */
	public void sendMessage(String msg) {
		try {
			out.write(processOutput(msg));
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	// closes socket
	private void shutdown() {
		print("  Closing the socket connection.");
		try {
			sock.close();
		} catch (IOException e) {
			error("ERROR: error closing the socket connection.");
		}
	}

	// prints on tui
	private void print(String message) {
		goTUI.print(message);
	}

	private void error(String message) {
		goTUI.error(message);
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

	//
	/**
	 * Processes input from server to something readable for client.
	 * 
	 * @param msg
	 *            message from the server.
	 * @return processed message from the server.
	 */
	private String processInput(String msg) {
		String[] splitMessage = msg.split("\\" + General.DELIMITER1);
		switch (splitMessage[0]) {
		case Server.CHAT:
			return ClientMessages.chatMessage(msg);

		case Server.ENDGAME:
			board.determineScores();
			board.quitGame();
			return ClientMessages.endGameMessage(msg);

		case Server.ERROR:
			error(ClientMessages.errorMessage(msg));
			return "";

		case Server.START:
			if (!(splitMessage.length == 2)) {
				startGame(splitMessage[3]);
				if (splitMessage[2].equals(General.BLACK)) {
					stone = Stone.BLACK;
				} else {
					stone = Stone.WHITE;
				}
			}
			return ClientMessages.startMessage(msg);

		case Server.TURN:
			int x;
			int y;
			String[] splitMove = splitMessage[2].split(General.DELIMITER2);
			if (!splitMove[0].equals(Client.PASS)) {
				x = Integer.parseInt(splitMove[0]);
				y = Integer.parseInt(splitMove[1]);
				if (splitMessage[1].equals(clientName)) {
					makeMove(x, y, stone);
				} else {
					makeMove(x, y, stone.Other());
				}
			}
			return ClientMessages.turnMessage(msg, (splitMessage[3].equals(clientName)));

		default:
			return msg.replace(General.DELIMITER1, " ");
		}
	}

	/**
	 * Processes input given by the client to a correct format (to match the
	 * protocol).
	 * 
	 * @param msg
	 *            the message provided by the client.
	 * @return a adjusted message.
	 */
	private String processOutput(String msg) {
		String[] splitMessage = msg.split(" ");
		switch (splitMessage[0]) {
		case Client.CHAT:
			return msg.replaceFirst(" ", "\\" + General.DELIMITER1);
		case Client.MOVE:
			if (splitMessage[1].equals(Client.PASS)) {
				return msg.replace(" ", General.DELIMITER1);
			} else if ((splitMessage[1].matches("\\d+") && splitMessage[2].matches("\\d+"))
					&& board.isValid(Integer.parseInt(splitMessage[1]), Integer.parseInt(splitMessage[2]), stone)) {
				return ((msg.replaceFirst(" ", "\\" + General.DELIMITER1)).replaceFirst(" ", General.DELIMITER2))
						.replace(" ", General.DELIMITER1);
			} else {
				error(Server.ERROR + General.DELIMITER1 + Server.INVALID + General.DELIMITER1 + " Invalid move, try something else.");
				return "";
			}

		default:
			return msg.replace(" ", General.DELIMITER1);
		}
	}

	/**
	 * Gets name of the client.
	 * 
	 * @return clientName.
	 */
	public String getClientName() {
		return clientName;
	}

	/**
	 * Initializes a board and GUI. Here the current state of the game is displayed.
	 * 
	 * @param dim
	 */
	private void startGame(String dim) {
		board = new Board(Integer.parseInt(dim), true, true);
	}

	/**
	 * Updates the board and GUI. A stone is added and there may be some stones
	 * removed due to a capture.
	 * 
	 * @param x
	 *            row of the added stone.
	 * @param y
	 *            column of the added stone.
	 * @param stone
	 *            color of the added stone.
	 */
	private void makeMove(int x, int y, Stone stone) {
		board.addStone(x, y, stone);
	}

	/** Gets TUI. */
	private ClientView getTUI() {
		return goTUI;
	}
}
