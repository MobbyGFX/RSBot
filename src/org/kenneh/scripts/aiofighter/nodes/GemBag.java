package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.core.api.Misc;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.tab.Inventory;


public class GemBag extends Node {
	
	public static boolean hasGemBag() {
		return Tabs.getCurrent() == Tabs.INVENTORY && Inventory.getItem(18338) != null;
	}
	
	int[] gems = {1617, 1619, 1621, 1623};

	@Override
	public boolean activate() {
		return Misc.contains(gems) && hasGemBag();
	}

	@Override
	public void execute() {
		Inventory.getItem(gems).getWidgetChild().interact("Use");
		Task.sleep(1000);
		Inventory.getItem(18338).getWidgetChild().click(true);
		Task.sleep(1000);
	}

}
