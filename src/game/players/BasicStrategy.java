package game.players;

import java.util.ArrayList;
import java.util.List;
import game.model.Board;
import game.model.Stone;

public class BasicStrategy implements Strategy {
	public String getName() {
		return toString();
	}

	public Integer[] determineMove(Board board, Stone color) {
		Board deepCopy = board.deepCopy();
		List<Integer[]> goodMoves = new ArrayList<>();
		List<Integer[]> neutralMoves = new ArrayList<>();
		List<Integer[]> badMoves = new ArrayList<>();
		int dimension = board.getDimension();
		for (int r = 0; r < dimension; r++) {
			for (int c = 0; c < dimension; c++) {
				if (board.isValid(r, c, color)) {
					int result = deepCopy.addStone(r, c, color);
					if (result == 1) {
						goodMoves.add(new Integer[]{r, c, 0});
					} else if (result == 0) {
						neutralMoves.add(new Integer[]{r, c, 0});
					} else {
						badMoves.add(new Integer[]{r, c, 0});
					}

				}
			}
		}
		if (!goodMoves.isEmpty()) {
			int index = (int) Math.floor(Math.random() * goodMoves.size());
			return goodMoves.get(index);
		} else if (!neutralMoves.isEmpty()) {
			int index = (int) Math.floor(Math.random() * neutralMoves.size());
			return neutralMoves.get(index);
		} else if (!badMoves.isEmpty()) {
			int index = (int) Math.floor(Math.random() * badMoves.size());
			return badMoves.get(index);
		} else {
			return new Integer[]{0, 0, -1}; // PASS
		}
	}

	public String toString() {
		return "Basic";
	}
}