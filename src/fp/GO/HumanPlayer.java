package fp.GO;

import java.util.Scanner;

import fp.GO.Board;

public class HumanPlayer extends Player {

	public  HumanPlayer (String name, Node stone) {
		super(name, stone);
	}
	public int[] determineMove(Board board) {
        String prompt = "> " + getName() + " (" + getStone().toString() + ")"
                + ", enter coordinates(<x> <y>) or pass (enter PASS): ";
        int[] choice = readInt(prompt);
        
        while (!((board.isValid(choice[0],choice[1]) && choice[2]==0) || choice[2] == -1 )) {
            System.out.println("ERROR: field (" + choice[0] + "," + choice[1]
                    + ") is no valid choice.");
            choice = readInt(prompt);
        }
        return choice;
    }
	
	private int[] readInt(String prompt) {
        int[] value = new int[3];
        boolean moveRead = false;
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.print(prompt);
            String line = scanner.nextLine();
            String[] lineVector = line.split(" ");
            if (lineVector.length < 3) {
            	try (Scanner lineScanner = new Scanner(line)) {
            		int i = 0;
            		while (lineScanner.hasNextInt() && i < 2) {
            			value[i] = lineScanner.nextInt();
            			i++;
            		}
            		
            		if (i == 0 && lineScanner.next().equals("PASS")) {
            			value[2] = -1;
            			moveRead = true;
            		} 
            		else if(i == 2) {
            			moveRead = true;
            		}
            	}
            }
            else {
            	System.out.println("ERROR: to many input arguments.");
            }
        } while (!moveRead);
        return value;
	}
}
