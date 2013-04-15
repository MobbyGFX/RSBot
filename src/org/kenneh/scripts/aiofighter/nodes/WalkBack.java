package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.scripts.aiofighter.MonsterKiller;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;


public class WalkBack extends Node {

	@Override
	public boolean activate() {
		return !MonsterKiller.isInLootArea(Players.getLocal())  && Players.getLocal().getInteracting() == null;
	}

	@Override
	public void execute() {
		while(!MonsterKiller.isInArea(Players.getLocal())) {
			if(!MonsterKiller.myPos.canReach()) {
				MonsterKiller.stopScript = true;
			} else {
				Walking.walk(MonsterKiller.myPos);
			}
		}
	}

}
