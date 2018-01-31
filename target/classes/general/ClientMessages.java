package general;

import general.Protocol.Client;
import general.Protocol.General;
import general.Protocol.Server;

public class ClientMessages {

	// Input for the client.
	public static String chatMessage(String msg) {
		String[] splitMessage = msg.split("\\" + General.DELIMITER1);
		if (splitMessage[1].equals("  ")) {
			return splitMessage[1] + (msg.replaceFirst("\\" + General.DELIMITER1, " "))
					.substring(splitMessage[1].length() + 6);
		} else {
			return splitMessage[1] + ": " + (msg.replaceFirst("\\" + General.DELIMITER1, " "))
					.substring(splitMessage[1].length() + 6);
		}

	}

	public static String endGameMessage(String msg) {
		String result = "";
		String[] splitMessage = msg.split("\\" + General.DELIMITER1);
		if (splitMessage[1].equals(Server.ABORTED)) {
			result += "  The game has been aborted.";
		} else if (splitMessage[1].equals(Server.FINISHED)) {
			result += "  The game is finished.";
		} else if (splitMessage[1].equals(Server.TIMEOUT)) {
			result += "  The game has been aborted due to a timeout.";
		}
		result += "\n\n  SCORES";
		result += "\n  " + splitMessage[2] + ":   \t" + splitMessage[3];
		result += "\n  " + splitMessage[4] + ":   \t" + splitMessage[5];
		if (splitMessage[3].equals(splitMessage[5])) {
			result += "\n  It's a draw. There is no winner.";
		} else {
			result += "\n  " + splitMessage[2] + " is the winner! \n";
		}
		return result;
	}

	public static String errorMessage(String msg) {
		return msg.replace(General.DELIMITER1, " ");
	}

	public static String startMessage(String msg) {
		String[] splitMessage = msg.split("\\" + General.DELIMITER1);
		if (splitMessage.length == 2) {
			return "  Set your color and boardsize: (" + Client.SETTINGS
					+ " <BLACK or WHITE> <boardsize in range (5,19)>)";
		} else {
			return "  The boardsize is " + splitMessage[3] + "x" + splitMessage[3]
					+ " and your color is " + splitMessage[2] + ".";
		}
	}

	public static String turnMessage(String msg, boolean current, boolean human) {
		String[] splitMessage = msg.split("\\" + General.DELIMITER1);
		String[] splitMove = splitMessage[2].split(General.DELIMITER2);
		String result = "";
		if (splitMove.length == 2 && splitMove[0].matches("\\d+") && splitMove[1].matches("\\d+")) {
			result = "  " + splitMessage[1] + " added a stone on (" + splitMove[0] + ","
					+ splitMove[1] + "). It's " + splitMessage[3] + "'s turn now.";
		} else if (splitMove[0].equals(Client.PASS)) {
			result = "  " + splitMessage[1] + " passes. It's " + splitMessage[3] + "'s turn now.";
		} else if (splitMove[0].equals(Server.FIRST)) {
			result = "  " + splitMessage[1] + " starts. It's " + splitMessage[3] + "'s turn now.";
		}
		if (current && human) {
			result += "\n  Enter a move (MOVE <row> <column> or MOVE PASS):";
		}
		return result;
	}

	// Output from the client.
	public static final String REQUESTGAME = Client.REQUESTGAME + General.DELIMITER1 + "2"
			+ General.DELIMITER1 + Client.RANDOM;
	private static final String EXTENSIONS = "1$0$0$0$0$0$0";

	public static String initMessage(String arg) {
		return Client.NAME + General.DELIMITER1 + arg + General.DELIMITER1 + Client.VERSION
				+ General.DELIMITER1 + Client.VERSIONNO + General.DELIMITER1 + Client.EXTENSIONS
				+ General.DELIMITER1 + EXTENSIONS;
	}

}
