package org.kenneh.scripts.hydrachopper;

import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Summoning;
import org.powerbot.game.api.methods.tab.Summoning.Familiar;
import org.powerbot.game.api.wrappers.node.Item;

public class ResummonHydra implements Node {

	@Override
	public boolean activate() {
		return !Summoning.isFamiliarSummoned() && Inventory.contains(Constants.hydraPouch) && Constants.myTree.isStump();
	}

	@Override
	public void execute() {
		if(Summoning.getPoints() >= Familiar.HYDRA.getRequiredPoints()) {
			final Item i = Inventory.getItem(Constants.hydraPouch);
			if(i != null) {
				i.getWidgetChild().interact("Summon");
			}
		} else {
			final Item restore = Inventory.getItem(Constants.restores);
			if(restore != null) {
				restore.getWidgetChild().interact("Drink");
			}
		}
	}

}
