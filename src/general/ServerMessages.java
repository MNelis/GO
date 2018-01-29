package general;

import general.Protocol.General;
import general.Protocol.Server;
import server.model.ClientHandler;

public class ServerMessages {
	// Messages on the console of the server
	public static final String CHAT = Server.CHAT + General.DELIMITER1 + "  " + General.DELIMITER1;
	public static final String STARTSERVER = "Successfully started the GO-server.";

	public static String newClientMessage(String name) {
		return name + " has connected.";
	}

	public static String newRequestMessage(String name) {
		return name + " made a game request.";
	}

	public static String quitRequestMessage(String name) {
		return name + " revoked the game request.";
	}

	public static String gameStartedMessage(ClientHandler[] players) {
		return players[0].getClientName() + " and " + players[1].getClientName()
				+ " entered a game together.";
	}

	// Messages sent from the server.
	public static final String CHATENABLEDMESSAGE = CHAT + "Enter CHAT <your message> "
			+ "to sent a message to everyone in the lobby.";
	public static final String NOTYOURTURN = Server.ERROR + General.DELIMITER1
			+ " It is not your turn. You cannot make a move.";
	public static final String INVALIDSETTINGS = Server.ERROR + General.DELIMITER1 + Server.UNKNOWN
			+ General.DELIMITER1 + " Wrong settings input. Please re-enter settings.";
	public static final String REQUESTEDGAME = CHAT + "Thank you for requesting a game. "
			+ "Please wait on a opponent. \n" + CHAT + "Enter QUIT to revoke your request.";
	public static final String QUITREQUEST = CHAT + "Game request revoked.";
	public static final String UNKNOWNCOMMAND = Server.ERROR + General.DELIMITER1 + Server.UNKNOWN
			+ General.DELIMITER1 + " Unknown command:";
	public static final String INVALIDMOVE = Server.ERROR + General.DELIMITER1 + Server.INVALID
			+ General.DELIMITER1 + " Invalid move.";

	public static String incompatibleError(int version) {
		return Server.ERROR + General.DELIMITER1 + Server.INCOMPATIBLEPROTOCOL
				+ " You tried to connect with protocol version " + version
				+ ". Please reconnect with protocol version " + Server.VERSIONNO;
	}

	public static String nameTakenError(String name) {
		return Server.ERROR + General.DELIMITER1 + Server.NAMETAKEN + " Name " + name
				+ " is already taken." + ". Please reconnect with another name.";
	}

	public static String welcomeMessage(String name) {
		return CHAT + "Welcome to the lobby of this GO-server, " + name + ".\n" + CHAT
				+ "Enter REQUESTGAME to request a game. \n" + CHAT
				+ "Enter STARTAI to activate the computer player.";
	}

}
