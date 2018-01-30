package errors;

import general.Protocol.General;
import general.Protocol.Server;

public class IncompatibleProtocolException extends Exception {
	private static final long serialVersionUID = 1L;

	public IncompatibleProtocolException(String message) {
		super(Server.ERROR + General.DELIMITER1 + Server.INCOMPATIBLEPROTOCOL + General.DELIMITER1
				+ ": " + message);
	}

	public IncompatibleProtocolException() {
		super(Server.ERROR + General.DELIMITER1 + Server.INCOMPATIBLEPROTOCOL);
	}

}
