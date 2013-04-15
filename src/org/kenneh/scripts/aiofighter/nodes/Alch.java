package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.core.api.Misc;
import org.kenneh.scripts.aiofighter.MonsterKiller;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.node.Item;

import sk.action.Ability;
import sk.action.ActionBar;
import sk.action.book.magic.Spell;
import sk.general.TimedCondition;


public class Alch extends Node {

	public static boolean hasRunes() {
		return Inventory.getItem(554) != null && Inventory.getItem(561) != null;
	}
	
	public static Item getAlchableItem() {
		for(int i : MonsterKiller.alchs) {
			if(Inventory.getItem(i) != null) return Inventory.getItem(i);
		}
		return null;
	}
	
	@Override
	public boolean activate() {
		if(Tabs.getCurrent() != Tabs.INVENTORY) {
			return false;
		}
		return ActionBar.findAbility(Spell.HIGH_LEVEL_ALCHEMY) != -1 && Players.getLocal().getInteracting() == null && getAlchableItem() != null;
	}

	@Override
	public void execute() {
		final Item i = Inventory.getItem(Misc.convertIntegers(MonsterKiller.alchs));
		final Ability alch = ActionBar.getAbilityInSlot(ActionBar.findAbility(Spell.HIGH_LEVEL_ALCHEMY));
		ActionBar.useAbility(alch);
		i.getWidgetChild().interact("Cast");
		new TimedCondition(1500) {
			@Override
			public boolean isDone() {
				return i == null;
			}
		}.waitStop();
		
	}

}
