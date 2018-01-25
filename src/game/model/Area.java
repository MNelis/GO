package game.model;

import java.util.ArrayList;
import java.util.List;

public class Area {
	private List<Integer[]> nodes;
	private boolean stoneArea = false;
	private Stone stone = Stone.EMPTY;

	/** Constructs a new area. */
	public Area() {
		nodes = new ArrayList<Integer[]>();
	}

	/** Adds a node to the area. */
	public void addNode(int x, int y) {
		nodes.add(new Integer[] { x, y });
	}

	/** Empties the area. */
	public void emptyList() {
		nodes.removeAll(nodes);
	}

	/** Merges another area with this area */
	public void joinArea(Area area) {
		List<Integer[]> list = area.getList();
		list.forEach((node) -> nodes.add(node));
		area.emptyList();
		setStoneArea(getStoneArea() && area.getStoneArea());
		if (!area.getStone().equals(getStone())) {
			setStoneArea(false);
		}
	}

	/** Checks if the area contains a given node. */
	public boolean containsNode(Integer[] node) {
		boolean x = false;
		for (Integer[] n : nodes) {
			if (n[0].equals(node[0]) && (n[1].equals(node[1]))) {
				x = true;
			}
		}
		return x;
	}

	/** Checks if the area contains a given node. */
	public boolean containsNode(int x, int y) {
		return (containsNode(new Integer[] { x, y }));
	}

	/** Gets list of node in area. */
	public List<Integer[]> getList() {
		return nodes;
	}

	/** Sets the area stone. */
	public void setStone(Stone stone) {
		this.stone = stone;
	}

	/** Gets the area stone, if it is an area. */
	public Stone getStone() {
		return stone;
	}

	/** Sets the area to an stone */
	public void setStoneArea(boolean bool) {
		this.stoneArea = bool;
	}

	/** Gets the area to an stone */
	public boolean getStoneArea() {
		return this.stoneArea;
	}
	
	public int length() {
		return nodes.size();
	}

}
