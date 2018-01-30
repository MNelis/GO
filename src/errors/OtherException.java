package errors;

import general.Protocol.General;
import general.Protocol.Server;

public class OtherException extends Exception {
	private static final long serialVersionUID = 1L;

	public OtherException(String message) {
		super(Server.ERROR + General.DELIMITER1 + Server.OTHER + General.DELIMITER1 + ": "
				+ message);
	}

	public OtherException() {
		super(Server.ERROR + General.DELIMITER1 + Server.OTHER);
	}

}
