package errors;

import general.Protocol.General;
import general.Protocol.Server;

public class InvalidMoveException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidMoveException(String message) {
		super(Server.ERROR + General.DELIMITER1 + Server.INVALID + General.DELIMITER1 + ": "
				+ message);
	}

	public InvalidMoveException() {
		super(Server.ERROR + General.DELIMITER1 + Server.INVALID);
	}
}
