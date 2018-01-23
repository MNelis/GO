package client.model;

import java.io.*;
import java.net.*;

import com.nedap.go.gui.GoGUIIntegrator;

import client.viewer.ClientView;
import client.viewer.GOClientTUI;
import game.model.*;
import general.Protocol.General;
import general.Protocol.Server;
import general.Protocol.Client;

public class GOClient extends Thread {
	private static final String USAGE = "Usage : " + GOClient.class.getName() + " <name> <address>";
	private static final String EXTENSIONS = "1$0$0$0$0$0$0";

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
			// Initial fixed message from client to server.
			String initialMessage = Client.NAME + General.DELIMITER1 + args[0] + General.DELIMITER1 + Client.VERSION
					+ General.DELIMITER1 + Client.VERSIONNO + General.DELIMITER1 + Client.EXTENSIONS
					+ General.DELIMITER1 + EXTENSIONS;
			
			client.sendMessage(initialMessage);
			client.start();
			client.getTUI().start();
		} catch (IOException e) {
			System.err.println("ERROR: couldn ’t construct a client object !");
			System.exit(0);
		}
	}

	private String clientName;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;

	private GoGUIIntegrator goGUI;
	private ClientView goTUI;
	private Board board;
	private Node stone;

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
	public void shutdown() {
		print("Closing socket connection.");
		try {
			sock.close();
		} catch (IOException e) {
			print("ERROR: error closing the socket connection!");
		}
	}

	// prints on tui
	private void print(String message) {
		goTUI.print(message);
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
	 * Processes input from server to something readable for the client.
	 * 
	 * @param msg
	 *            message from the server.
	 * @return proccesed messege from the server.
	 */
	public String processInput(String msg) {
		String[] splitMessage = msg.split("\\" + General.DELIMITER1);
		switch (splitMessage[0]) {
		case Server.CHAT:
			return splitMessage[1] + ": "
					+ (msg.replaceFirst("\\" + General.DELIMITER1, " ")).substring(splitMessage[1].length() + 6);

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
				startGame(splitMessage[3]);
				if (splitMessage[2].equals("BLACK")) {
					stone = Node.BLACK;
				} else {
					stone = Node.WHITE;
				}
				return "A game has started between " + splitMessage[4] + " and " + splitMessage[5]
						+ ". The boardsize is " + splitMessage[3] + "x" + splitMessage[3] + " and your color is "
						+ splitMessage[2] + ".";
			}

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
				print(board.toString());
				return "[" + splitMessage[1] + " added a stone on (" + splitMove[0] + "," + splitMove[1]
						+ ").] \n[It's " + splitMessage[3] + "'s turn now.]";
			} else {
				return "[" + splitMessage[1] + " passes.]\n[It's " + splitMessage[3] + "'s turn now.]";
			}

		default:
			return msg.replace(General.DELIMITER1, " ");
		}

	}

	/**
	 * Processes input given by the client to a propper format (to match the
	 * protocol).
	 * 
	 * @param msg
	 *            the message provided by the client.
	 * @return a adjusted message.
	 */
	public String processOutput(String msg) {
		String[] splitMessage = msg.split(" ");
		switch (splitMessage[0]) {
		case Client.CHAT:
			return msg.replaceFirst(" ", "\\" + General.DELIMITER1);
		case Client.MOVE:
			return ((msg.replaceFirst(" ", "\\" + General.DELIMITER1)).replaceFirst(" ", General.DELIMITER2))
					.replace(" ", General.DELIMITER1);
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
	public void startGame(String dim) {
		goGUI = new GoGUIIntegrator(false, true, Integer.parseInt(dim));
		goGUI.startGUI();
		goGUI.setBoardSize(Integer.parseInt(dim));
		board = new Board(Integer.parseInt(dim));
		print(board.toString());
	}

	/**
	 * Updates the board and GUI. A stone is added and there may be some stones
	 * removed due to a capture. *
	 * 
	 * @param x
	 *            row of the added stone.
	 * @param y
	 *            column of the added stone.
	 * @param stone
	 *            color of the added stone.
	 */
	public void makeMove(int x, int y, Node stone) {
		// TODO currently only adds a stone. Must implement capture rules.
		board.addStone(x, y, stone);
		goGUI.addStone(y, x, (stone.equals(Node.WHITE)));
	}

	public ClientView getTUI() {
		return goTUI;
	}
}
