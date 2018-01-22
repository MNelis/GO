package client.viewer;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import server.model.GOClientHandler;

public class GOClientTUI implements Observer, ClientView{
	private GOClientHandler handler;
	private Scanner line;
	
	public GOClientTUI (GOClientHandler handler) {
		this.handler = handler;
	}
	
	
	public void start() {
		boolean running = true; 
		String answer;
		line = new Scanner(System.in);
		
		while (running) {
			System.out.print("What is your command? ");
			answer = line.hasNextLine() ? line.nextLine() : null;	
			String[] words = answer.split(" ");
			if (words.length == 3 && words[0].equals("ADD")
					&& words[1].equals("PARTY")) {
				
			} 
			else if (words.length == 2 && words[0].equals("VOTE")){
				
			}
			else if (words.length == 1 && words[0].equals("PARTIES")) {
				
			} 
			else if (words.length == 1 && words[0].equals("VOTES")) {
				
			} 
			else if (words.length == 1 && words[0].equals("EXIT")) {
				running = false;
			} 
			else {
				running = true;
			}
			} 
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}
