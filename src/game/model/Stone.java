package game.model;

public enum Stone {
	
	EMPTY, BLACK, WHITE;
	
	public Stone Other() {
		if (this == BLACK) {
            return WHITE;
        } else if (this == WHITE) {
            return BLACK;
        } else {
            return EMPTY;
        }
	}
	
	public String toString() {
		if (this == EMPTY) {
    		return "+";
    	} else if (this == BLACK) {
    		return "BLACK";
    	} else {
    		return "WHITE";
    	}
	}

}
