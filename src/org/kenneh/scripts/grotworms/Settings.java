package org.kenneh.scripts.grotworms;

import java.util.Set;
import java.util.TreeSet;

import org.kenneh.core.api.net.PriceWrapper;
import org.kenneh.core.api.utils.Misc;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.Tile;

public class Settings { // Everything is just thrown in here.

	// Constants
	public static final Area GROT_CAVE = new Area(new Tile(1206, 6514, 0), new Tile(1120, 6468, 0));

	public static final int FOOD_ID = 385;
	
	public static final int POLYPORE_STICK = 22498;
	public static final int POLYPORE_STAFF = 22494;

	public static final int NATURE_RUNE = 561;
	public static final int FIRE_RUNE = 554;

	public static final Tile BANK_TILE = new Tile(2946, 3368, 0);
	public static final Tile OUTSIDE_GROT_CAVE_TILE = new Tile(2990, 3236, 0);
	public static final Tile INSIDE_GROT_CAVE_TILE = new Tile(1177, 6357, 0);

	public static final int TELETAB = 8009;

	public static final Tile GROT_CENTER_TILE = new Tile(1175, 6500, 0);

	public static int RARE_DROP_TABLE[] = {
		452,574,9342,5289,570,1392,3001,2364,384,1215,1216,450,20667,2362,270,
		5304,1201,2366,1149,892,7937,454,258,2999,6686,5315,5316,1516
	};

	public static int GROTWORM_LOOT[] = {
		1213,1432,1303,1147,1111,5298,5303,5302,5304,5300,1780,451,450,24372,
		385,995,565,2362,563,217,2485,1183
	};

	public static int ALCHABLE[] = {
		1213,1432,1303,1147,1111, 1183
	};

	// Script variables
	public static final PriceWrapper pw = new PriceWrapper();
	private static String status = "Initializing";
	private static Set<Integer> loot = new TreeSet<Integer>();
	private static int totalLootValue = 0;
	private static int radius = 23;
	private static int currBar = 0;

	public static int getKillCount() {
		return org.kenneh.scripts.aiofighter.Settings.killCount;
	}
	
	public static int getRadius() {
		return radius;
	}
	
	public static void setBar(final int bar) {
		currBar = bar;
	}

	public static int getBar() {
		return currBar;
	}

	public static boolean isInArea(Locatable loc) {
		return Calculations.distance(GROT_CENTER_TILE, loc) <= getRadius();
	}

	public static void setValue(final int i) {
		System.out.println("Adding " + i + " to the loot value!");
		totalLootValue = totalLootValue + i;
	}

	public static int getLootValue() {
		return totalLootValue;
	}

	public static void setLoot(int...is) {
		for(int i : is)
			loot.add(i);
	}

	public static int[] getLoot() {
		return Misc.setToArray(loot);
	}

	public static void setStatus(final String s) {
		status = s;
	}

	public static String getStatus() {
		return status;
	}
}
