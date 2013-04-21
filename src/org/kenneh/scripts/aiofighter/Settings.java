package org.kenneh.scripts.aiofighter;

import java.util.Set;
import java.util.TreeSet;

import org.kenneh.core.api.utils.Misc;
import org.powerbot.game.api.methods.input.Mouse.Speed;
import org.powerbot.game.api.wrappers.Tile;

public class Settings {
	
	public static boolean DEBUG = true;
	
	public static void main(String[] args) {
		setLootIds(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
		for(int i = 0; i < getLootArray().length; i++) {
			System.out.println(getLootArray()[i]);
		}
	}

	
	private static boolean lootClues = false;
	
	private static int radius = 5;
	private static String status = "";
	private static int teletab = 9007;
	private static int foodId = 385;
	private static Speed mouseSpeed = Speed.VERY_FAST;
	private static Tile startTile;
	private static int lootValue = 9001;

	private static final Set<Integer> lootIds = new TreeSet<Integer>();
	private static final Set<Integer> alchIds = new TreeSet<Integer>();
	private static final Set<Integer> monsterIds = new TreeSet<Integer>();
	
	public static boolean lootClueScrolls() {
		return lootClues;
	}
	
	public static void setLootClues(final boolean t) {
		lootClues = t;
	}
	
	public static int[] getMonsterArray() {
		return Misc.setToArray(monsterIds);
	}
	
	public static Set<Integer> getMonsterSet() {
		return monsterIds;
	}
	
	public static void setMonsterIds(int... ids) {
		for(int i : ids)
			monsterIds.add(i);
	}
	
	public static int[] getAlchArray() {
		return Misc.setToArray(alchIds);
	}
	
	public static Set<Integer> getAlchSet() {
		return alchIds;
	}
	
	public static void setAlchIds(int... ids) {
		for(int i : ids) 
			alchIds.add(i);
	}
	
	public static int[] getLootArray() {
		return Misc.setToArray(lootIds);
	}
	
	public static Set<Integer> getLootSet() {
		return lootIds;
	}
	
	public static void setLootIds(int... ids) {
		for(int i : ids) 
			lootIds.add(i);
	}

	public static void setLootValue(final int v) {
		lootValue = v;
	}

	public static int getLootValue() {
		return lootValue;
	}

	public static Tile getStartTile() {
		return startTile;
	}

	public static void setStartTile(final Tile t) {
		startTile = t;
	}

	public static void setRadius(final int r) {
		radius = r;
	}

	public static int getRadius() {
		return radius;
	}

	public static void setMouseSpeed(final Speed s) {
		mouseSpeed = s;
	}

	public static Speed getMouseSpeed() {
		return mouseSpeed;
	}

	public static void setFoodId(final int i) {
		foodId = i;
	}

	public static int getFoodId() {
		return foodId;
	}

	public static void setTeletab(final int i) {
		teletab = i;
	}

	public static int getTeletab() {
		return teletab;
	}

	public static void setStatus(final String s) {
		status = s;
	}

	public static String getStatus() {
		return status;
	}

}
