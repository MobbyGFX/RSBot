package org.kenneh.scripts.hydrachopper;

import org.kenneh.core.api.Misc;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.tab.Inventory;

public class WalkToTree implements Node {

	@Override
	public boolean activate() {
		return !Inventory.isFull() && Misc.distanceTo(Settings.treeTile) > 5;
	}

	@Override
	public void execute() {
		Walking.walk(Settings.treeTile);
	}

}
