package server.exception;

import general.Protocol.Server;

public class NameTakenException extends Exception {
	private static final long serialVersionUID = 1L;

	public NameTakenException(String message) {
		super(Server.ERROR + " " + Server.INCOMPATIBLEPROTOCOL + ": " + message);
	}
	
	public NameTakenException() {
		super(Server.ERROR + " " + Server.INCOMPATIBLEPROTOCOL);
	}


}
