package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.core.api.MCamera;
import org.kenneh.core.api.Misc;
import org.kenneh.scripts.aiofighter.FighterGUI;
import org.kenneh.scripts.aiofighter.MonsterKiller;
import org.kenneh.scripts.aiofighter.Settings;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.interactive.NPC;

import sk.general.TimedCondition;



public class FightEntity extends Node {

	public boolean getActivate() {
		return FighterGUI.waitForLoot ? !Players.getLocal().isInCombat() : Players.getLocal().getInteracting() == null;
	}

	@Override
	public boolean activate() {
		return getActivate()
				&& Alch.getAlchableItem() == null
				&& !AbilityHandler.waitingForRejuv 
				&& !AbilityHandler.waitForAbility
				&& !LootHandler.isValid() 
				&& AttackOneOf.getNearest() == null;
	}

	public static Filter<NPC> NPC_FILTER = new Filter<NPC>() {
		@Override
		public boolean accept(NPC npc) {
			for(int ids : Settings.getMonsterArray()) {
				if(npc != null
						&& npc.getId() == ids
						&& !npc.isInCombat()
						&& MonsterKiller.isInArea(npc)) return true;
			}
			return false;
		}
	};

	public static NPC getNearestNpc() {
		return NPCs.getNearest(NPC_FILTER);
		//return Misc.getNearest(NPCs.getLoaded(NPC_FILTER));
	}

	@Override
	public void execute() {
		final NPC mob = getNearestNpc();
		if(mob != null) {
			if(!Misc.isOnScreen(mob)) {
				if(FighterGUI.useFastCamera) {
					MCamera.turnTo(mob, 50);
				} else {
					Camera.turnTo(mob, 5);
				}
			}
			int dist = (int)mob.getLocation().distanceTo();
			if(dist > 7 && !Players.getLocal().isMoving()) {
				Walking.walk(mob);
			} else {
				if(!mob.isInCombat()) {
					MonsterKiller.status = "Attacking " + mob.getName();
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
}
