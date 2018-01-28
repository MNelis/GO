package game.players;

import game.model.Board;
import game.model.Stone;

public class ComputerPlayer extends Player {
	private Strategy strategy;

	public ComputerPlayer(Stone color, Strategy strategy) {
		super("COM-" + strategy.toString(), color);
		this.strategy = strategy;
	}

	public ComputerPlayer(Stone color) {
		this(color, new NaiveStrategy());
	}

	@Override
	public Integer[] determineMove(Board board) {
		Integer[] move = strategy.determineMove(board, getStone());
		if (move[2] == 0) {
			System.out.println(
					getName() + " (" + getStone().toString() + ") added a stone on (" + move[0] + ", " + move[1] + "!");
		}
		else {
			System.out.println(getName() + " (" + getStone().toString() + ") passed!");
		}

		return move;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
}
