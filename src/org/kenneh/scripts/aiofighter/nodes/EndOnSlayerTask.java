package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.scripts.aiofighter.MonsterKiller;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.node.Item;


public class EndOnSlayerTask extends Node {

	public static boolean end() {
		return Tabs.getCurrent() == Tabs.INVENTORY && Inventory.getItem(4155) != null && Settings.get(183) == 0;
	}
	
	@Override
	public boolean activate() {
		return end();
	}
	
	public Item returnTeletab() {
		for(Item i : Inventory.getItems()) {
			if(i != null) {
				if(i.getName().toLowerCase().contains("teleport")) {
					return i;
				}
			}
		}
		return null;
	}

	@Override
	public void execute() {
		Item i = Inventory.getItem(returnTeletab().getId());
		if(i != null) {
			i.getWidgetChild().click(true);
			Task.sleep(5000);
		}
		MonsterKiller.stopScript = true;
	}

}
