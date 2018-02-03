package game.players;

import java.util.ArrayList;
import java.util.List;
import game.model.Board;
import game.model.Stone;

public class BetterStrategy implements Strategy {
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
			Board deepCopy2 = board.deepCopy();
			int[] scores = board.determineScores();
			int currentScore;

			if (color.equals(Stone.BLACK)) {
				currentScore = scores[0] - scores[1];
			} else {
				currentScore = scores[1] - scores[0];
			}
			List<Integer[]> bestMoves = new ArrayList<>();

			int bestImprovement = -100;
			// int dimension = board.getDimension();
			for (Integer[] m : goodMoves) {
				deepCopy2.addStone(m[0], m[1], color);
				int[] newScores = deepCopy2.determineScores();
				int newScore;
				if (color.equals(Stone.BLACK)) {
					newScore = newScores[0] - newScores[1];
				} else {
					newScore = newScores[1] - newScores[0];
				}
				int improvement = newScore - currentScore;
				if (improvement > bestImprovement) {
					bestImprovement = improvement;
					// bestMove = new Integer[]{r, c, 0};
					bestMoves.clear();
					bestMoves.add(m);
				} else if (improvement == bestImprovement) {
					bestMoves.add(m);
				}
				deepCopy2 = board.deepCopy();
			}
			if (!bestMoves.isEmpty()) {
				int index = (int) Math.floor(Math.random() * bestMoves.size());
				return bestMoves.get(index);
			} else {
				int index = (int) Math.floor(Math.random() * goodMoves.size());
				return goodMoves.get(index);
			}

			
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
		return "Better";
	}
}