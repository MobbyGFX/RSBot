package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.node.Item;

public class GoToBank implements KNode {

	@Override
	public boolean canActivate() {
		return (Inventory.isFull() && Eating.edible() == null) || (Eating.edible() == null && Players.getLocal().getHealthPercent() < 50) || (!Inventory.isFull() && !Inventory.contains(Settings.TELETAB));
	}

	@Override
	public void activate() {
		final Item teleport = Inventory.getItem(Settings.TELETAB);
		if(FightWorms.getBestGrot() != null) {
			if(teleport != null) {
				Settings.setStatus("Breaking tab");
				teleport.getWidgetChild().interact("Break");
				final Timer timer = new Timer(3000);
				while(timer.isRunning() && FightWorms.getBestGrot() != null) {
					Settings.setStatus("Waiting to appear in falador");
					Task.sleep(20);
				}
			}
		} else {
			if(Settings.BANK_TILE.distanceTo() > 5) {
				Settings.setStatus("Walking to bank");
				Walking.walk(Settings.BANK_TILE);
			}
		}
	}

}
