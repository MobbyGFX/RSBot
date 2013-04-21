package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.core.api.utils.Misc;
import org.kenneh.core.graphics.Logger;
import org.kenneh.scripts.aiofighter.MonsterKiller;
import org.kenneh.scripts.aiofighter.constants.Constants;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.wrappers.node.Item;


public class Potions extends Node {

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
			return Constants.RANGE;
		case Skills.ATTACK:
			return Constants.ATTACK;
		case Skills.STRENGTH:
			return Constants.STRENGTH;
		case Skills.DEFENSE:
			return Constants.DEFENSE;
		case Skills.MAGIC:
			return Constants.MAGIC;
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
