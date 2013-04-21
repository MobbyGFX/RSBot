package org.kenneh.scripts.grotworms;

import java.util.Arrays;
import java.util.List;

import org.kenneh.core.api.framework.KNode;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.node.Item;

public class Eating implements KNode {

	public static Item edible() {
		final Item[] is = Inventory.getItems();
		for (Item i : is) {
			String[] s = i.getWidgetChild().getActions();
			if (s != null) {
				List<String> l = Arrays.asList(s);
				if (l.contains("Eat")) {
					return i;
				}
			}
		}
		return null;
	}

	@Override
	public boolean canActivate() {
		return edible() != null && Players.getLocal().getHealthPercent() < 50 || (edible() != null && Inventory.isFull() && LootItems.getLoot() != null);
	}

	@Override
	public void activate() {
		final Item food = edible();
		if(food != null) {
			Settings.setStatus("Eating food!");
			food.getWidgetChild().interact("Eat");
		}
	}

}
