package errors;

import general.Protocol.General;
import general.Protocol.Server;

public class NameTakenException extends Exception {
	private static final long serialVersionUID = 1L;

	public NameTakenException(String message) {
		super(Server.ERROR + General.DELIMITER1 + Server.NAMETAKEN + General.DELIMITER1 + ": "
				+ message);
	}

	public NameTakenException() {
		super(Server.ERROR + General.DELIMITER1 + Server.NAMETAKEN);
	}

}
