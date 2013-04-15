package org.kenneh.scripts.hydrachopper;

import java.awt.Font;

import org.powerbot.game.api.wrappers.Tile;

public class Settings {

	public static Font paintFont = new Font("Calibri", Font.PLAIN, 13);
	public static Tile bankTile = new Tile(3012, 3355, 0);
	public static Tile treeTile = new Tile(3002, 3373, 0);
	public static int[] restores = {
			3024, 3026, 3028, 3030,23399, 23401, 23403, 23405, 23407, 23409, 
			12140, 12142, 12144, 12146, 23621, 23623, 23625, 23627, 23629, 23631
	};
	public static int hydraScroll = 12442, hydraPouch = 12025;
	public static Tree myTree;
	public static int scrollsUsed = 0, logsChopped = 0, logPrice = 0;
}
