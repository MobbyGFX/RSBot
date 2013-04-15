package org.kenneh.scripts.hydrachopper;

import org.kenneh.core.api.Misc;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.tab.Inventory;

public class WalkToBank implements Node {

	@Override
	public boolean activate() {
		return Misc.distanceTo(Constants.bankTile) > 5 && Inventory.isFull();
	}

	@Override
	public void execute() {
		Walking.walk(Constants.bankTile);
	}

}