package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.kenneh.core.api.utils.MCamera;
import org.kenneh.core.api.utils.Misc;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.GroundItems;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.node.GroundItem;

public class LootItems implements KNode {

	private final static Filter<GroundItem> LOOT_FILTER = new Filter<GroundItem>() {

		@Override
		public boolean accept(GroundItem g) {
			if(Settings.isInArea(g)) {
				for(int i : Settings.getLoot()) {
					if(i == g.getId()) {
						return true;
					}
				}
			}
			return false;
		}

	};

	public static GroundItem getLoot() {
		return GroundItems.getNearest(LOOT_FILTER);
	}

	@Override
	public boolean canActivate() {
		return Players.getLocal().getInteracting() == null
				&& !Players.getLocal().isMoving()
				&& FightWorms.getBestGrot() != null
				&& getLoot() != null;
	}

	public static void loot() {
		final GroundItem loot = getLoot();
		if(loot != null) {
			if(loot.getLocation().distanceTo() > 5) {
				Walking.walk(loot.getLocation());
			} else {
				if(!Misc.isOnScreen(loot)) {
					MCamera.turnTo(loot, 50);
				} else {
					Settings.setStatus("Looting " + loot.getGroundItem().getStackSize() + "x " + loot.getGroundItem().getName());
					if(loot.interact("Take", loot.getGroundItem().getName())) {
						System.out.println("Looting "+ loot.getGroundItem().getName() + " worth " + Settings.pw.getPrice(loot.getId()) + " each!");
						Settings.setValue(Settings.pw.getPrice(loot.getId()) * loot.getGroundItem().getStackSize()); 
						final Timer timer = new Timer(2000);
						while(timer.isRunning() && loot != null) {
							Task.sleep(20);
						}
					}
				}
			}
		}
	}

	@Override
	public void activate() {
		loot();
	}

}
