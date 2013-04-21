package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;

public class WalkToCenter implements KNode {

	@Override
	public boolean canActivate() {
		return FightWorms.getBestGrot() != null 
				&& !Players.getLocal().isInCombat() 
				&& Players.getLocal().getInteracting() == null
				&& LootItems.getLoot() == null
				&& Settings.GROT_CENTER_TILE.distanceTo() > 15;
	}

	@Override
	public void activate() {
		Settings.setStatus("Walking to center tile");
		Walking.walk(Settings.GROT_CENTER_TILE);
	}

}
