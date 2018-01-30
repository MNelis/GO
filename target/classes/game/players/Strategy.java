package game.players;

import game.model.*;

public interface Strategy {
	
	public String getName();

	public Integer[] determineMove(Board board, Stone color);
}
