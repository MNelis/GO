package client.viewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;

import client.model.GOClient;

public class GOClientTUI implements Observer, ClientView {
	private GOClient client;

	/** Constructs a TUI for a given client. @param client */
	public GOClientTUI(GOClient client) {
		this.client = client;
	}

	/** Starts the TUI, reads input written by the client on the TUI. */
	public void start() {
		print("Started TUI.");
		do {
			String input = readString("");
			client.sendMessage(input);
		} while (true);
	}

	/** Prints given message on the TUI. */
	public void print(String msg) {
		System.out.println(msg);
	}

	/** Prints given error message on the TUI. */
	public void error(String msg) {
		System.err.println(msg);
	}

	/** Reads input written on the TUI. */
	public String readString(String msg) {
		System.out.print(msg);
		String answ = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			answ = in.readLine();
		} catch (IOException e) {
		}
		return (answ == null) ? "" : answ;
	}

	@Override
	public void update(Observable o, Object arg) {
		print("Updated " + arg + ".");
	}
}
