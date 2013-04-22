package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.kenneh.core.api.utils.MCamera;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.interactive.NPC;

public class FightWorms implements KNode {

	private static final Filter<NPC> WORM = new Filter<NPC>() {
		@Override
		public boolean accept(NPC npc) {
			return Settings.GROT_CAVE.contains(npc) && npc.getId() == 15463 && !npc.isInCombat();
		}
	};

	public static NPC getBestGrot() {
		return NPCs.getNearest(WORM);
	}

	@Override
	public boolean canActivate() {
		return Players.getLocal().getInteracting() == null 
				&& Players.getLocal().isMoving()
				&& Alching.alchable() == null
				&& Settings.GROT_CAVE.contains(Players.getLocal())
				&& LootItems.getLoot() == null
				&& Players.getLocal().getHealthPercent() > 40
				&& getBestGrot() != null;
	}

	@Override
	public void activate() {
		final NPC grot = getBestGrot();
		Settings.setStatus("Aquiring best target");
		if(grot != null && !grot.isInCombat()) {
			Settings.setStatus("Target aquired");
			if(grot.getLocation().distanceTo() >= 10) {
				Walking.walk(grot.getLocation());
			} else {
				if(!grot.isOnScreen()) {
					Settings.setStatus("Turning camera to target");
					MCamera.turnTo(grot, 50);
				} else {
					Settings.setStatus("Initiating combat with target");
					grot.interact("Attack");
				}
			}
		} else {
			Settings.setStatus("No suitable targets found!");
		}

	}

}
