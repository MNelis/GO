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
import game.model.Board;
import game.model.Stone;
//import game.players.BasicStrategy;
//import game.players.BetterStrategy;
import game.players.BettererStrategy;
import game.players.NotBetterStrategy;
import game.players.ComputerPlayer;
//import game.players.NaiveStrategy;
import game.players.Player;
import client.ClientMenu;
import client.ClientMessages;
import general.Protocol.Client;
import general.Protocol.General;
import general.Protocol.Server;
import general.errors.InvalidMoveException;
import general.errors.OtherException;
import general.errors.UnknownCommandException;

public class GOClient extends Thread {
	private static final String USAGE = "Usage : " + GOClient.class.getName()
			+ " <name> <address> <port>";
	// private static final String STARTAI = "STARTAI";
	// private static final String ENDCAI = "ENDAI";

	/** Starts the client application and connects to server.
	 * @param args */
	public static void main(String[] args) {
		int port = 0;
		if (args.length == 3 && args[2].matches("\\d+")) {
			port = Integer.parseInt(args[2]);
		} else if (args.length > 3 || args.length < 2) {
			System.out.println(USAGE);
			System.exit(0);
		} else {
			port = General.DEFAULT_PORT;
		}
		InetAddress host = null;

		try {
			host = InetAddress.getByName(args[1]);
		} catch (UnknownHostException e) {
			System.err.println("ERROR: invalid hostname.");
			System.exit(0);
		}
		// try {
		// } catch (NumberFormatException e) {
		// System.err.println("ERROR: invalid default port number.");
		// System.exit(0);
		// }
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
	private boolean inGame = false;
	private boolean hasChat = false;

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
		} catch (InvalidMoveException | OtherException | UnknownCommandException e) {
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
			case Server.NAME:
				print("  Welcome " + clientName + ", you connected to " + splitMessage[1] + ".\n");
				if (splitMessage[5].equals("1")) {
					hasChat = true;
					print(ClientMenu.LOBBYMENU);
				} else {
					print(ClientMenu.CLOBBYMENU);
				}

				return "";

			case Server.CHAT:
				return ClientMessages.chatMessage(msg);

			case Server.ENDGAME:
				board.determineScores(true);
				board.quitGame();
				inGame = false;
				print(ClientMessages.endGameMessage(msg, clientName));
				Thread.sleep(30);
				if (hasChat) {
					print(ClientMenu.LOBBYMENU);
				} else {
					print(ClientMenu.CLOBBYMENU);
				}

				return "";

			case Server.ERROR:
				error(ClientMessages.errorMessage(msg));
				return "";

			case Server.START:
				String result = "";
				if (!(splitMessage.length == 2)) {
					startGame(splitMessage[3]);
					inGame = true;
					if (hasChat) {
						print(ClientMenu.GAMEMENU);
					} else {
						print(ClientMenu.CGAMEMENU);
					}
					if (splitMessage[2].equals(General.BLACK)) {
						stone = Stone.BLACK;
						computerPlayer = new ComputerPlayer(stone, new BettererStrategy());
						result = ClientMessages.startMessage(msg);
						// }
					} else {
						stone = Stone.WHITE;
						computerPlayer = new ComputerPlayer(stone, new NotBetterStrategy());
						result = ClientMessages.startMessage(msg);
					}

				} else {
					print(ClientMessages.startMessage(msg));
					result = "";
				}
				return result;

			case Server.TURN:
				Thread.sleep(30);
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
							humanPlayer) + determineMoveAI(board);
				} else {
					return ClientMessages.turnMessage(msg, splitMessage[3].equals(clientName),
							humanPlayer);
				}

			default:
				return "";
		}
	}

	/** Processes input given by the client to a correct format (to match the
	 * protocol).
	 * @param msg the message provided by the client.
	 * @return a adjusted message.
	 * @throws InvalidMoveException
	 * @throws OtherException
	 * @throws UnknownCommandException */
	private String processOutput(String msg)
			throws InvalidMoveException, OtherException, UnknownCommandException {
		String[] splitMessage = msg.split(" ");
		switch (splitMessage[0].toUpperCase()) {
			case "0":
				if (msg.length() > 2) {
					return Client.CHAT + General.DELIMITER1 + msg.substring(2);
				} else {
					return Client.CHAT + General.DELIMITER1 + " ";
				}

			case "1":
				if (msg.length() < 3) {
					if (!inGame) {
						return ClientMessages.REQUESTGAME;
					} else {
						return Client.QUIT;
					}
				} else {
					throw new UnknownCommandException();
				}

			case "2":
				if (msg.length() < 3) {
					if (humanPlayer) {
						humanPlayer = false;
						print("  Activated the computer player.");
					} else {
						humanPlayer = true;
						print("  Deactivated the computer player.");
					}
					return "";
				} else {
					throw new OtherException("Command too long.");
				}

			case "3":
				if (msg.length() < 3) {
					return Client.EXIT;
				} else {
					throw new UnknownCommandException();
				}

			case "HELP":
				if (hasChat) {
					print(ClientMenu.HELPMENU);
				} else {
					print(ClientMenu.CHELPMENU);
				}

				return "";

			case Client.MOVE:
				if (inGame) {
					if (splitMessage.length == 2
							&& (splitMessage[1].toUpperCase()).equals(Client.PASS)) {
						return Client.MOVE + General.DELIMITER1 + Client.PASS;
						// return msg.replace(" ", General.DELIMITER1);
					} else if (splitMessage.length == 3 && (splitMessage[1].matches("\\d+")
							&& splitMessage[2].matches("\\d+"))) {
						int r = Integer.parseInt(splitMessage[1]);
						int c = Integer.parseInt(splitMessage[2]);
						if (board.isValid(r, c, stone)) {

							return Client.MOVE + General.DELIMITER1 + splitMessage[1]
									+ General.DELIMITER2 + splitMessage[2];
						} else {
							String cause = "";
							if (!board.isNode(r, c)) {
								cause = " is not a node.";
							} else if (!board.isEmpty(r, c)) {
								cause = " is not an empty node.";
							} else if (!board.stonesLeft()) {
								cause = " is not valid, because there are no stones left.";
							} else if (!board.koRule(r, c, stone)) {
								cause = " is not valid due to the ko rule.";
							}
							throw new InvalidMoveException("(" + r + "," + c + ")" + cause);
						}

						// return ((msg.replaceFirst(" ", "\\" + General.DELIMITER1)).replaceFirst("
						// ",
						// General.DELIMITER2)).replace(" ", General.DELIMITER1);
					} else {
						return "??";
						// throw new InvalidMoveException("Invalid move. Try something else.");
					}
				} else {
					throw new OtherException("You cannot made moves outside a game.");
				}

			case "SET":
				if (msg.length() > 4) {
					if ((splitMessage[1].toUpperCase()).equals("W")
							&& splitMessage[2].matches("\\d+")) {
						return Client.SETTINGS + General.DELIMITER1 + General.WHITE
								+ General.DELIMITER1 + splitMessage[2];
					} else if ((splitMessage[1].toUpperCase()).equals("B")
							&& splitMessage[2].matches("\\d+")) {
						return Client.SETTINGS + General.DELIMITER1 + General.BLACK
								+ General.DELIMITER1 + splitMessage[2];
					} else {
						throw new UnknownCommandException();
					}

				} else {
					throw new UnknownCommandException();
				}

			case Client.SETTINGS:
				if (msg.length() > 4) {
					return Client.SETTINGS + (msg.substring(3)).replace(" ", General.DELIMITER1);
				} else {
					throw new UnknownCommandException();
				}

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
	private void startGame(String dim) {

		if (board == null) {
			board = new Board(Integer.parseInt(dim), true, true);
		} else {
			board.setDimension(Integer.parseInt(dim));
		}

	}

	/** Determines a move made by the computer player.
	 * @param b Current board.
	 * @return move. */
	private String determineMoveAI(Board b) {
		Integer[] move = computerPlayer.determineMove(b);
		if (move[2] == -1) {
			sendMessage("MOVE PASS");
			return " ";
		} else {
			sendMessage("MOVE " + move[0] + " " + move[1]);
			return "";
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
	 * @return goTUI. */
	private ClientView getTUI() {
		return goTUI;
	}
}
