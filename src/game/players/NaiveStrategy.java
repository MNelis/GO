package game.players;

import java.util.ArrayList;
import java.util.List;
import game.model.Board;
import game.model.Stone;

public class NaiveStrategy implements Strategy {
	public String getName() {
		return toString();
	}

	public Integer[] determineMove(Board board, Stone color) {
		List<Integer[]> validMoves = new ArrayList<>();
		for (int r = 0; r < board.getDimension(); r++) {
			for (int c = 0; c < board.getDimension(); c++) {
				if (board.isValid(r, c, color)) {
					validMoves.add(new Integer[]{r, c, 0});
				}
			}
		}
		if (validMoves.isEmpty()) {
			return new Integer[]{0, 0, -1}; // PASS
		} else {
			int index = (int) Math.floor(Math.random() * validMoves.size());
			return validMoves.get(index);
		}

	}

	public String toString() {
		return "Naive";
	}
}
