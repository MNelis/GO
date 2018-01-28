package client.model;

import general.Protocol.Client;
import general.Protocol.General;

public class ClientMessages {

	// Input for the client.
	public static String chatMessage(String msg) {
		String[] splitMessage = msg.split("\\" + General.DELIMITER1);
		return splitMessage[1] + ": " + (msg.replaceFirst("\\" + General.DELIMITER1, " "))
				.substring(splitMessage[1].length() + 6);
	}

	public static String endGameMessage(String msg) {
		// TODO make nice output for client
		return msg.replace(General.DELIMITER1, " ");
	}

	public static String errorMessage(String msg) {
		// TODO make nice output for client
		return msg.replace(General.DELIMITER1, " ");
	}

	public static String startMessage(String msg) {
		String[] splitMessage = msg.split("\\" + General.DELIMITER1);
		if (splitMessage.length == 2) {
			return ">> You entered a game for " + splitMessage[1]
					+ " players. Set your color and boardsize: (" + Client.SETTINGS
					+ " <BLACK or WHITE> <boardsize>)";
		} else {
			return "  A game has started between " + splitMessage[4] + " and " + splitMessage[5]
					+ ". The boardsize is " + splitMessage[3] + "x" + splitMessage[3]
					+ " and your color is " + splitMessage[2] + ".]";
		}
	}

	public static String turnMessage(String msg, boolean current) {
		String[] splitMessage = msg.split("\\" + General.DELIMITER1);
		String[] splitMove = splitMessage[2].split(General.DELIMITER2);
		String result = "";
		if (!splitMove[0].equals(Client.PASS)) {
			result = "  " + splitMessage[1] + " added a stone on (" + splitMove[0] + ","
					+ splitMove[1] + "). It's " + splitMessage[3] + "'s turn now.";
		} else {
			result = "  " + splitMessage[1] + " passes. It's " + splitMessage[3] + "'s turn now.";
		}
		if (current) {
			result += "\n>> Enter a move (MOVE <row> <column> or MOVE PASS):";
		}
		return result;
	}

	// Output from the client.
	private static final String EXTENSIONS = "1$0$0$0$0$0$0";

	public static String initMessage(String arg) {
		return Client.NAME + General.DELIMITER1 + arg + General.DELIMITER1 + Client.VERSION
				+ General.DELIMITER1 + Client.VERSIONNO + General.DELIMITER1 + Client.EXTENSIONS
				+ General.DELIMITER1 + EXTENSIONS;
	}

}
