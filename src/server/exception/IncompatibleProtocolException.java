package server.exception;

import general.Protocol.Server;

public class IncompatibleProtocolException extends Exception {
	private static final long serialVersionUID = 1L;

	public IncompatibleProtocolException(String message) {
		super(Server.ERROR + " " + Server.INCOMPATIBLEPROTOCOL + ": " + message);
	}

	public IncompatibleProtocolException() {
		super(Server.ERROR + " " + Server.INCOMPATIBLEPROTOCOL);
	}

}
