package game.players;

import java.util.Scanner;

import game.model.Board;
import game.model.Stone;

public class HumanPlayer extends Player {

	public HumanPlayer(String name, Stone stone) {
		super(name, stone);
	}

	public Integer[] determineMove(Board board) {

		String prompt = "> " + getName() + " (" + getStone().toString() + ")"
				+ ", enter coordinates(<row> <column>) or pass (enter PASS): ";
		Integer[] choice = readInt(prompt);

		while (!((board.isValid(choice[0], choice[1], getStone()) && choice[2] == 0) || choice[2] == -1)) {
			System.err.println("ERROR: field (" + choice[0] + "," + choice[1] + ") is no valid choice.");
			choice = readInt(prompt);
		}
		return choice;

	}

	// TODO currently prints everything in console, might not be handy
	//
	private Integer[] readInt(String prompt) {
		Integer[] value = new Integer[]{0, 0, 0};
		boolean moveRead = false;
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		do {
			System.out.println(prompt);
			String line = scanner.nextLine();
			String[] lineVector = line.split(" ");
			if (lineVector.length < 3) {
				try (Scanner lineScanner = new Scanner(line)) {
					int i = 0;
					while (lineScanner.hasNextInt() && i < 2) {
						value[i] = lineScanner.nextInt();
						i++;
					}
					if (i == 0 && lineScanner.hasNext() && lineScanner.next().equals("PASS")) {
						value[2] = -1;
						moveRead = true;
					}
					else if (i == 2) {
						value[2] = 0;
						moveRead = true;
					}
				}
			}
			else {
				System.err.println("ERROR: invalid input");
			}
		}
		while (!moveRead);
		return value;
	}

}
