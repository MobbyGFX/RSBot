package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.kenneh.core.api.utils.MCamera;
import org.kenneh.core.api.utils.Misc;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.interactive.NPC;

import sk.general.TimedCondition;

public class FightWorms implements KNode {

	private static final Filter<NPC> WORM = new Filter<NPC>() {
		@Override
		public boolean accept(NPC npc) {
			return Settings.isInArea(npc) && npc.getId() == 15463 && !npc.isInCombat();
		}
	};

	public static NPC getBestGrot() {
		return NPCs.getNearest(WORM);
	}

	@Override
	public boolean canActivate() {
		return Players.getLocal().getInteracting() == null 
				&& Alching.alchable() == null
				&& Settings.isInArea(Players.getLocal())
				&& LootItems.getLoot() == null
				&& Players.getLocal().getHealthPercent() > 40
				&& getBestGrot() != null;
	}

	@Override
	public void activate() {
		final NPC mob = getBestGrot();
		if(mob != null) {
			if(!Misc.isOnScreen(mob)) {
				MCamera.turnTo(mob, 50);
			}
			try {
				int dist = (int) mob.getLocation().distanceTo();
				if(dist > 7 && !Players.getLocal().isMoving()) {
					Walking.walk(mob);
				} else {
					if(!mob.isInCombat()) {
						Settings.setStatus("Initiating combat with target");
						mob.interact("Attack", mob.getName());
						new TimedCondition(1500) {
							@Override
							public boolean isDone() {
								return Players.getLocal().getInteracting() != null;
							}
						}.waitStop();
					}
				}
			} catch(Exception a) {
				System.out.println("Fix internal errors pls ");
			}
		}
	}

}
