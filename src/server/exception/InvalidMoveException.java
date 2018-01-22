package server.exception;

import general.Protocol.Server;

public class InvalidMoveException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidMoveException(String message) {
		super(Server.ERROR + " " + Server.INCOMPATIBLEPROTOCOL + ": " + message);
	}
	
	public InvalidMoveException() {
		super(Server.ERROR + " " + Server.INCOMPATIBLEPROTOCOL);
	}
}
