package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.kenneh.core.api.utils.MCamera;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.GroundItems;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.node.GroundItem;

public class LootItems implements KNode {
	
	private final static Filter<GroundItem> LOOT_FILTER = new Filter<GroundItem>() {

		@Override
		public boolean accept(GroundItem g) {
			for(int i : Settings.getLoot()) {
				if(i == g.getId()) {
					return true;
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
				&& FightWorms.getBestGrot() != null
				&& getLoot() != null;
	}

	@Override
	public void activate() {
		final GroundItem loot = getLoot();
		if(loot != null) {
			if(!loot.isOnScreen()) {
				MCamera.turnTo(loot, 50);
			} else {
				if(loot.getLocation().distanceTo() > 5) {
					Walking.walk(loot.getLocation());
				} else {
					Settings.setStatus("Looting " + loot.getGroundItem().getStackSize() + "x " + loot.getGroundItem().getName());
					loot.interact("Take", loot.getGroundItem().getName());
				}
			}
		}
	}

}
