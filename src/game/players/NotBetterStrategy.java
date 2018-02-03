package game.players;

import java.util.ArrayList;
import java.util.List;
import game.model.Board;
import game.model.Stone;

// BetterStrategy is not better than BasicStrategy :P
public class NotBetterStrategy implements Strategy {
	public String getName() {
		return toString();
	}

	public Integer[] determineMove(Board board, Stone color) {
		Board deepCopy = board.deepCopy();
		int[] scores = board.determineScores();
		int currentScore;
		if (color.equals(Stone.BLACK)) {
			currentScore = scores[0] - scores[1];
		} else {
			currentScore = scores[1] - scores[0];
		}
//		Integer[] bestMove = new Integer[]{-1, -1, 0};
		List<Integer[]> bestMoves = new ArrayList<>();
		
		int bestImprovement = -100;
		int dimension = board.getDimension();
		for (int r = 0; r < dimension; r++) {
			for (int c = 0; c < dimension; c++) {
				if (board.isValid(r, c, color)) {
					deepCopy.addStone(r, c, color);
					int[] newScores = deepCopy.determineScores();
					int newScore;
					if (color.equals(Stone.BLACK)) {
						newScore = newScores[0] - newScores[1];
					} else {
						newScore = newScores[1] - newScores[0];
					}
					int improvement = newScore - currentScore;
					if (improvement > bestImprovement) {
						bestImprovement = improvement;
//						bestMove = new Integer[]{r, c, 0};
						bestMoves.clear();
						bestMoves.add(new Integer[]{r, c, 0});
					} else if (improvement == bestImprovement) {
						bestMoves.add(new Integer[]{r, c, 0});
					}
					deepCopy = board.deepCopy();

				}
			}
		}
//		System.out.print(bestImprovement);
		if (!bestMoves.isEmpty()) {
			int index = (int) Math.floor(Math.random() * bestMoves.size());
			return bestMoves.get(index);
		} else {
			return new Integer[]{0, 0, -1}; // PASS
		}
	}

	public String toString() {
		return "Better";
	}
}
