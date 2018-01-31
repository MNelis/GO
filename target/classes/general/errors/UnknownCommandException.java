package general.errors;

import general.Protocol.General;
import general.Protocol.Server;

public class UnknownCommandException extends Exception {
	private static final long serialVersionUID = 1L;

	public UnknownCommandException(String message) {
		super(Server.ERROR + General.DELIMITER1 + Server.UNKNOWN + General.DELIMITER1 + ": "
				+ message);
	}

	public UnknownCommandException() {
		super(Server.ERROR + General.DELIMITER1 + Server.UNKNOWN);
	}
}
