package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.core.api.Misc;
import org.kenneh.scripts.aiofighter.constants.Constants;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Summoning;
import org.powerbot.game.api.wrappers.node.Item;


public class SummoningHandler extends Node {

	private final int[] pouches = {12029, 12039};

	@Override
	public boolean activate() {
		return Tabs.getCurrent() == Tabs.INVENTORY && !isSummoned() && Inventory.getItem(pouches) != null;
	}

	public boolean isSummoned() {
		return Settings.get(1786) != 0;
	}

	@Override
	public void execute() {
		Item pouch = Inventory.getItem(pouches);
		int summPoints = Integer.parseInt(Widgets.get(747, 23).getText());
		if(summPoints <= 20 && Misc.contains(Constants.SUMMONING_RESTORE)) {
			Inventory.getItem(Constants.SUMMONING_RESTORE).getWidgetChild().interact("Drink");
			Task.sleep(1000);
		} else if(summPoints > Summoning.Familiar.UNICORN_STALLION.getRequiredPoints()){
			pouch.getWidgetChild().interact("Summon");
		}
	}

}
