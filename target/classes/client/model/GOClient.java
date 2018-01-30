package client.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import client.viewer.ClientView;
import client.viewer.GOClientTUI;
import errors.InvalidMoveException;
import errors.OtherException;
import errors.UnknownCommandException;
import game.model.Board;
import game.model.Stone;
import game.players.ComputerPlayer;
import game.players.NaiveStrategy;
import game.players.Player;
import general.ClientMessages;
import general.Protocol.Client;
import general.Protocol.General;
import general.Protocol.Server;

public class GOClient extends Thread {
	private static final String USAGE = "Usage : " + GOClient.class.getName() + " <name> <address>";
	private static final String STARTAI = "STARTAI";
	private static final String ENDCAI = "ENDAI";

	/** Starts the client application and connects to server.
	 * @param args */
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
	private Player computerPlayer;
	private boolean humanPlayer = true;

	/** Constructs new client with TUI.
	 * @param name name of the client.
	 * @param host address of the server.
	 * @param port port number.
	 * @throws IOException */
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
		} catch (IOException | InterruptedException e) {
			shutdown();
		}
	}

	/** Processes and sends given message from client to server.
	 * @param msg */
	public void sendMessage(String msg) {
		try {
			out.write(processOutput(msg));
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		} catch (InvalidMoveException | OtherException e) {
			error(ClientMessages.errorMessage(e.getMessage()));
		}
	}

	/** Closes socket and terminates GOClient instance. */
	private void shutdown() {
		print("  Closing the socket connection.");
		try {
			sock.close();
		} catch (IOException e) {
			error("ERROR: error closing the socket connection.");
		}
		System.exit(0);
	}

	/** Prints message on TUI.
	 * @param msg */
	private void print(String msg) {
		goTUI.print(msg);
	}

	/** Prints error message on TUI.
	 * @param msg */
	private void error(String msg) {
		goTUI.error(msg);
	}

	/** Processes input from server to something readable for client.
	 * @param msg message from the server.
	 * @return processed message from the server.
	 * @throws InterruptedException
	 * @throws UnknownCommandException */
	private String processInput(String msg) throws InterruptedException {
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
				String result = "";
				if (!(splitMessage.length == 2)) {
					startGame(splitMessage[3]);
					if (splitMessage[2].equals(General.BLACK)) {
						stone = Stone.BLACK;
						computerPlayer = new ComputerPlayer(stone, new NaiveStrategy());
						result = ClientMessages.startMessage(msg);
						// }
					} else {
						stone = Stone.WHITE;
						computerPlayer = new ComputerPlayer(stone, new NaiveStrategy());
						result = ClientMessages.startMessage(msg);
					}

				} else {
					result = ClientMessages.startMessage(msg);
				}
				return result;

			case Server.TURN:
				Thread.sleep(10);
				int x;
				int y;
				String[] splitMove = splitMessage[2].split(General.DELIMITER2);
				if (splitMove.length == 2 && splitMove[0].matches("\\d+")
						&& splitMove[1].matches("\\d+")) {
					x = Integer.parseInt(splitMove[0]);
					y = Integer.parseInt(splitMove[1]);
					if (splitMessage[1].equals(clientName)) {
						makeMove(x, y, stone);
					} else {
						makeMove(x, y, stone.other());
					}
				}
				if (splitMessage[3].equals(getClientName()) && !humanPlayer) {
					return ClientMessages.turnMessage(msg, splitMessage[3].equals(clientName),
							humanPlayer) + "\n" + determineMoveAI(board);
				} else {
					return ClientMessages.turnMessage(msg, splitMessage[3].equals(clientName),
							humanPlayer);
				}

			default:
				error("?");
				return "";
		}
	}

	/** Processes input given by the client to a correct format (to match the
	 * protocol).
	 * @param msg the message provided by the client.
	 * @return a adjusted message.
	 * @throws InvalidMoveException
	 * @throws OtherException */
	private String processOutput(String msg) throws InvalidMoveException, OtherException {
		String[] splitMessage = msg.split(" ");
		switch (splitMessage[0]) {
			case Client.CHAT:
				return msg.replaceFirst(" ", "\\" + General.DELIMITER1);
			case Client.MOVE:

				if (splitMessage.length == 2 && splitMessage[1].equals(Client.PASS)) {
					return msg.replace(" ", General.DELIMITER1);
				} else if (splitMessage.length == 3
						&& (splitMessage[1].matches("\\d+") && splitMessage[2].matches("\\d+"))
						&& board.isValid(Integer.parseInt(splitMessage[1]),
								Integer.parseInt(splitMessage[2]), stone)) {
					return ((msg.replaceFirst(" ", "\\" + General.DELIMITER1)).replaceFirst(" ",
							General.DELIMITER2)).replace(" ", General.DELIMITER1);
				} else {
					throw new InvalidMoveException("Invalid move. Try something else.");
				}

			case Client.REQUESTGAME:
				return ClientMessages.REQUESTGAME;

			case STARTAI:
				if (humanPlayer) {
					humanPlayer = false;
					print("  Activated the computer player.\n  Enter ENDAI to deactivate it.");
				} else {
					throw new OtherException("You already activated the computer player.");
				}
				return "";

			case ENDCAI:
				if (!humanPlayer) {
					humanPlayer = true;
					print("  Deactivated the computer player.\n  Enter STARTAI to reactivate it.");
				} else {
					throw new OtherException("You already deactivated the computer player.");
				}
				return "";

			default:
				return msg.replace(" ", General.DELIMITER1);
		}
	}

	/** Gets name of the client.
	 * @return clientName. */
	public String getClientName() {
		return clientName;
	}

	/** Initializes a board and GUI. Here the current state of the game is
	 * displayed.
	 * @param dim */
	private synchronized void startGame(String dim) {

		if (board == null) {
			board = new Board(Integer.parseInt(dim), true, true);
		} else {
			board.setDimension(Integer.parseInt(dim));
		}

	}

	/** Determines a move made by the computer player.
	 * @param b Current board.
	 * @return move. */
	private synchronized String determineMoveAI(Board b) {
		Integer[] move = computerPlayer.determineMove(b);
		if (move[2] == -1) {
			sendMessage("MOVE PASS");
			return "  The computer passed:";
		} else {
			sendMessage("MOVE " + move[0] + " " + move[1]);
			return "  The computer made a move.";
		}
	}

	/** Updates the board and GUI. A stone is added and there may be some stones
	 * removed due to a capture.
	 * @param x row of the added stone.
	 * @param y column of the added stone.
	 * @param color color of the added stone. */
	private void makeMove(int x, int y, Stone color) {
		board.addStone(x, y, color);
	}

	/** Sets computer player.
	 * @param player */
	public void setPlayer(Player player) {
		computerPlayer = player;
	}

	/** Gets computer player.
	 * @return computerPlayer. */
	public Player getPlayer() {
		return computerPlayer;
	}

	/** Gets TUI. 
	 * @return goTUI.*/
	private ClientView getTUI() {
		return goTUI;
	}
}
