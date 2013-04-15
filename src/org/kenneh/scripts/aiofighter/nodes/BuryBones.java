package org.kenneh.scripts.aiofighter.nodes;

import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.tab.Inventory;

public class BuryBones extends Node {

	@Override
	public boolean activate() {
		if(Tabs.getCurrent() != Tabs.INVENTORY) {
			return false;
		}
		return Inventory.getCount(bonesarray) > 0 && Inventory.isFull();
	}
	int[] bonesarray = {526, 528, 530, 532, 534, 2530, 2859, 3123, 3125,
			3180, 3181, 3182, 3183, 3184, 3185, 3186, 3187, 4812, 4830,
			4834, 6729, 6712, 14793};

	@Override
	public void execute() {
		while(Inventory.getItem(bonesarray) != null) {
			Inventory.getItem(bonesarray).getWidgetChild().interact("Bury");
			Task.sleep(500);
		}
	}

}
