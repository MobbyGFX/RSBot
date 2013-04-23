package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.kenneh.core.api.utils.MCamera;
import org.kenneh.core.api.utils.Misc;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Summoning;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.interactive.NPC;

import sk.general.TimedCondition;

public class AttackOneOf implements KNode {

	public static Filter<NPC> AGGRO_FILTER = new Filter<NPC>() {
		@Override
		public boolean accept(NPC mob) {
			return mob != null 
					&& mob.getInteracting() != null
					&& !mob.equals(Summoning.getFamiliar())
					&& mob.getInteracting().equals(Players.getLocal());
		}
	};

	public static NPC getNearest() {
		return NPCs.getNearest(AGGRO_FILTER);
	}


	@Override
	public boolean canActivate() {
		return Alching.alchable() == null 
				&& getNearest() != null 
				&& Players.getLocal().getInteracting() == null;
	}

	@Override
	public void activate() {
		if(LootItems.getLoot() != null) {
			LootItems.loot();
		}
		if(Players.getLocal().getInteracting() == null) {
			final NPC mob = getNearest();
			if(mob != null) {
				Settings.setStatus("Attacking closest aggressive npc");
				if(!Misc.isOnScreen(mob)) {
					MCamera.turnTo(mob, 50);
				}
				mob.interact("Attack", mob.getName());
				new TimedCondition(1500) {
					@Override
					public boolean isDone() {
						return Players.getLocal().getInteracting() != null;
					}
				}.waitStop();
			}
		}

	}
}