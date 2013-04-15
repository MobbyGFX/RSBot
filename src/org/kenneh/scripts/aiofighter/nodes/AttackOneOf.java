package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.core.api.MCamera;
import org.kenneh.core.api.Misc;
import org.kenneh.scripts.aiofighter.MonsterKiller;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Summoning;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.interactive.NPC;

import sk.general.TimedCondition;


public class AttackOneOf extends Node {

	public static NPC[] getNearestAgressiveMob() {
		return NPCs.getLoaded(new Filter<NPC>() {
			@Override
			public boolean accept(NPC mob) {
				return mob != null 
						&& mob.getInteracting() != null
						&& !mob.equals(Summoning.getFamiliar())
						&& mob.getInteracting().equals(Players.getLocal());
			}
		});
	}

	public static NPC getNearest() {
		return Misc.getNearest(getNearestAgressiveMob());
	}
	
//	public static NPC getNearest() {
//		for(NPC i : getNearestAgressiveMob()) {
//			if(i.getInteracting() != null
//					&& i.getInteracting().equals(Players.getLocal()))
//				return i;
//		}
//		return null;
//	}

	@Override
	public boolean activate() {
		return !AbilityHandler.waitForAbility 
				&& Alch.getAlchableItem() == null
				&& !AbilityHandler.waitingForRejuv 
				&& getNearest() != null && Players.getLocal().getInteracting() == null;
	}

	@Override
	public void execute() {
		if(LootHandler.getLoot() != null) {
			LootHandler.loot();
		}
		if(Players.getLocal().getInteracting() == null) {
			final NPC mob = getNearest();
			if(mob != null) {
				//Logger.log("Attacking closest aggressive npc");
				MonsterKiller.status = "Attacking closest aggressive npc";
				if(!Misc.isOnScreen(mob)) {
					//Camera.turnTo(mob, 5);
					MCamera.turnTo(mob, 50);
				}
				//Misc.interact(mob, "Attack");
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
