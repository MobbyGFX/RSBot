package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.core.api.Misc;
import org.kenneh.core.graphics.Logger;
import org.kenneh.scripts.aiofighter.MonsterKiller;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.wrappers.node.Item;


public class Potions extends Node {

	public static final int[] antifire = {2452, 2454, 2456, 2458};

	public static final int[] superantifire = {11505, 11507, 23363, 23365, 23367, 23369,
			23371, 23373, 23489, 23490, 23491, 23492, 23493, 23494, 15305, 15307, 15304, 15306};

	public static final int[] attackpots = {121,123,125,145,147,149,2428,2436,15308,15309,15310,15311, 23195, 23197, 23199, 23201, 23203, 23205, 23255, 23257, 23259,
			23261, 23263, 23265, 23495, 23496, 23497, 23498, 23499, 23500, 9739, 9741, 9743, 9745, 23447, 23449, 23451, 23453, 23455, 23457,
			15332, 15333, 15334, 15335, 23531, 23532, 23533, 23534, 23535, 23536}; // includes normal/super/extremes/combats/ovls

	public static final int[] strengthpots = {113,115,117,119, 157, 159, 161, 2440,15312, 15313, 15314, 15315, 23207, 23207, 23209, 23211, 23213, 23215, 23217, 23279, 23281,
			23283, 23285, 23287, 23289, 23501, 23502, 23503, 23504, 23505, 23506, 9739, 9741, 9743, 9745, 23447, 23449, 23451, 23453, 23455, 23457,
			5332, 15333, 15334, 15335, 23531, 23532, 23533, 23534, 23535, 23536}; 

	public static final int[] defensepots = {133, 135, 137, 163, 165, 167, 2442, 2432, 15316, 15317, 15318, 15319, 23231, 23233, 23235, 23237, 23239, 23241, 23291, 23293,
			23295, 23297, 23299, 23301, 23507, 23508, 23509, 23510, 23511, 23512, 9739, 9741, 9743, 9745, 23447, 23449, 23451, 23453, 23455, 23457,
			5332, 15333, 15334, 15335, 23531, 23532, 23533, 23534, 23535, 23536};

	public static final int[] rangepots = {169, 171, 173, 2444, 15324, 15325, 15326, 15327, 23303, 23305, 23307, 23309, 23311, 23313, 23519, 23520, 23521, 23522, 23523,
			23524, 23531, 23532, 23533, 23534, 23535, 23536};

	public static final int[] magicpots = {23531, 23532, 23533, 23534, 23535, 23536, 3040, 3042, 3044, 3046, 15320, 15321, 15322, 15323, 23423, 23425, 23427, 23429,
			23431,23433, 23513, 23514, 23515, 23516, 23517, 23518};

	public int getLevelDiff(int index) {
		int[] bottomskillls = Skills.getBottomLevels();
		int[] topskills = Skills.getTopLevels();
		return topskills[index] - bottomskillls[index];
	}

	int[] potSkills = {Skills.RANGE, Skills.MAGIC, Skills.ATTACK, Skills.STRENGTH, Skills.DEFENSE};
	String[] skillNames = {"Range", "Magic", "Attack", "Strength", "Defense"};

	public int[] getPotions(int index) {
		switch(index) {
		case Skills.RANGE:
			return rangepots;
		case Skills.ATTACK:
			return attackpots;
		case Skills.STRENGTH:
			return strengthpots;
		case Skills.DEFENSE:
			return defensepots;
		case Skills.MAGIC:
			return magicpots;
		}
		return null;
	}
	
	public boolean getPot() {
		for(int i = 0; i < potSkills.length; i++) {
			if(needsToPot(potSkills[i])) {
				return true;
			}
		}
		return false;
	}

	public boolean needsToPot(int index) {
			return Misc.contains(getPotions(index)) && getLevelDiff(index) <= 3;
	}

	public void pot(int index) {
		Item pot = Inventory.getItem(getPotions(index));
		pot.getWidgetChild().interact("Drink");
		Task.sleep(1000);
	}

	@Override
	public boolean activate() {
	/*	for(int i = 0; i < potSkills.length; i++) {
			if(needsToPot(potSkills[i])) {
				return true;
			}
		}*/
		return canAntifire() || getPot();
	}
	
	public static boolean isAntifired() {
		int curr = Settings.get(1299);
		if(curr > MonsterKiller.antiPotAtValue) {
			return true;
		}
		return false;
	}

	public boolean canAntifire() {
		return !isAntifired() && MonsterKiller.antifires != null && Misc.contains(MonsterKiller.antifires);
	}
	
	@Override
	public void execute() {
		for(int i = 0; i < potSkills.length; i++) {
			if(needsToPot(potSkills[i])) {
				Logger.log("Potting for skill: "+ skillNames[i]);
				pot(potSkills[i]);
			}
		}
		if(canAntifire()) {
			Item pot = Inventory.getItem(MonsterKiller.antifires);
			pot.getWidgetChild().interact("Drink");
			Task.sleep(1000);
		}
		if(getPot()) {
			
		}
		for(int i : potSkills) {
			if(needsToPot(i)) {
				Logger.log("Potting for skill: " + skillNames[i]);
			}
		}
	}

}
