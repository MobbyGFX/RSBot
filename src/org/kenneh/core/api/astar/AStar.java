package org.kenneh.core.api.astar;

import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Tile;

import java.util.ArrayList;

/**
 * User: Manner
 * Date: 2/24/13
 * Time: 5:12 PM
 */
public class AStar {

	public static int findDistance(Tile tile) {
		return findPath(tile).size() - 1;
	}

	 public static ArrayList<Tile> findPath(Tile tile) {
	        ANode.setEnd(new ANode(tile));
	        ANode.setStart(new ANode(Players.getLocal().getLocation(), null));
	        ArrayList<ANode> closed = new ArrayList<ANode>();
	        ArrayList<ANode> open = new ArrayList<ANode>();
	        open.add(ANode.getStart());
	        Timer t = new Timer(4000);
	        while (t.isRunning()) {
	            ANode currentNode = null;
	            for (ANode node : open) {
	                if (currentNode == null) currentNode = node;
	                else if (node.getF() < currentNode.getF())
	                    currentNode = node;
	            }
	            if (currentNode != null) {
	                if (currentNode.getTile().equals(ANode.getEnd().getTile())) {
	                    ArrayList<Tile> path = new ArrayList<Tile>();
	                    path.add(currentNode.getTile());
	                    ANode node = currentNode;
	                    while (node.getParent() != null) {
	                        node = node.getParent();
	                        path.add(node.getTile());
	                    }
	                    return path;
	                } else {
	                    open.remove(currentNode);
	                    closed.add(currentNode);
	                    for (ANode neighbor : getNeighbors(currentNode)) {
	                        if (contains(closed, neighbor)) {
	                            if (currentNode.getG() < neighbor.getG()) {
	                                neighbor.setParent(currentNode);
	                            }
	                        } else if (contains(open, neighbor)) {
	                            if (currentNode.getG() < neighbor.getG()) {
	                                neighbor.setParent(currentNode);
	                            }
	                        } else {
	                            open.add(neighbor);
	                        }
	                    }
	                }
	            }
	        }
	        return new ArrayList<Tile>();
	    }

	private static boolean contains(ArrayList<ANode> list, ANode node) {
		for (ANode n : list) {
			if (n.getTile().equals(node.getTile())) return true;
		}
		return false;
	}

	private static ArrayList<ANode> getNeighbors(ANode tile) {
		ArrayList<ANode> nodes = new ArrayList<ANode>();
		if (tile.getTile().derive(1, 0).canReach()) nodes.add(new ANode(tile.getTile().derive(1, 0), tile));
		if (tile.getTile().derive(0, 1).canReach()) nodes.add(new ANode(tile.getTile().derive(0, 1), tile));
		if (tile.getTile().derive(-1, 0).canReach()) nodes.add(new ANode(tile.getTile().derive(-1, 0), tile));
		if (tile.getTile().derive(0, -1).canReach()) nodes.add(new ANode(tile.getTile().derive(0, -1), tile));
		return nodes;
	}


}