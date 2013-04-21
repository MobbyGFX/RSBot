package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.node.Item;

import sk.action.Ability;
import sk.action.ActionBar;
import sk.action.book.magic.Spell;

public class Alching implements KNode {

	@Override
	public boolean canActivate() {
		return Inventory.contains(Settings.FIRE_RUNE) && Inventory.contains(Settings.NATURE_RUNE) && alchable() != null && Players.getLocal().getInteracting() == null; 
	}
	
	public static Item alchable() {
		return Inventory.getItem(new Filter<Item>() {

			@Override
			public boolean accept(Item arg0) {
				for(int i : Settings.ALCHABLE) {
					if(arg0.getId() == i) {
						return true;
					}
				}
				return false;
			}
			
		});
	}

	@Override
	public void activate() {
		final Item i = alchable();
		final Ability alch = ActionBar.getAbilityInSlot(ActionBar.findAbility(Spell.HIGH_LEVEL_ALCHEMY));
		ActionBar.useAbility(alch);
		i.getWidgetChild().interact("Cast");
		final Timer timer = new Timer(2000);
		while(timer.isRunning() && i != null) {
			Task.sleep(20);
		}
	}

}
