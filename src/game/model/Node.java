package game.model;

public enum Node {
	
	EMPTY, BLACK, WHITE;
	
	public Node Other() {
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
    		return "B";
    	} else {
    		return "W";
    	}
	}

}
