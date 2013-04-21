package org.kenneh.scripts.hydrachopper;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.tab.Inventory;

public class WalkToBank implements Node {

	@Override
	public boolean activate() {
		return Calculations.distanceTo(Settings.bankTile) > 5 && Inventory.isFull();
	}

	@Override
	public void execute() {
		Walking.walk(Settings.bankTile);
	}

}