package org.kenneh.core.api.astar;

import org.powerbot.game.api.wrappers.Tile;

/**
 * User: Manner
 * Date: 3/2/13
 * Time: 9:37 PM
 */
public class ANode {

    private static ANode start;
    private static ANode end;

    private double g;
    private double h;
    private Tile tile;
    private ANode parent;

    public ANode(Tile tile, ANode parent) {
        this.parent = parent;
        this.tile = tile;
        this.h = Math.abs(tile.getX() - end.getTile().getX()) + Math.abs(tile.getY() - end.getTile().getY());
        if (parent == null) {
            g = 0;
        } else {
            this.g = parent.getG() + 1.0;
        }
    }

    public ANode(Tile tile) {
        this.g = 0;
        this.h = 0;
        this.tile = tile;
        this.parent = null;
    }

    public static ANode getStart() {
        return start;
    }

    public static void setStart(ANode start) {
        ANode.start = start;
    }

    public static void setEnd(ANode end) {
        ANode.end = end;
    }

    public static ANode getEnd() {
        return end;
    }

    public Tile getTile() {
        return tile;
    }

    public double getG() {
        return g;
    }

    public double getF() {
        return h + g;
    }

    public double getH() {
        return h;
    }

    public void setParent(ANode parent) {
        this.parent = parent;
        if (parent == null) {
            g = 0;
        } else {
            this.g = parent.getG() + 1.0;
        }
    }

    public ANode getParent() {
        return parent;
    }

}
