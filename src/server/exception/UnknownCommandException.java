package server.exception;

import general.Protocol.Server;

public class UnknownCommandException extends Exception {
	private static final long serialVersionUID = 1L;

	public UnknownCommandException(String message) {
		super(Server.ERROR + " " + Server.INCOMPATIBLEPROTOCOL + ": " + message);
	}
	
	public UnknownCommandException() {
		super(Server.ERROR + " " + Server.INCOMPATIBLEPROTOCOL);
	}
}
