package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.core.api.Misc;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Summoning;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.node.Item;


public class SprinkleNeem extends Node {

	NPC interacting = null;

	int[] polypore = {14690, 14696,14700, 14688};

	@Override
	public boolean activate() {
		if(Players.getLocal().getInteracting() != null
				&& Misc.arrayContains(Players.getLocal().getInteracting().getId(), polypore)) {
			if(Summoning.isFamiliarSummoned()) {
				if(Players.getLocal().getInteracting().getId() != Summoning.getFamiliar().getId()) {
					interacting = (NPC)Players.getLocal().getInteracting();
					return true;
				}
			} else {
				interacting = (NPC)Players.getLocal().getInteracting();
				return true;
			}
		}
		return false;
	}



	@Override
	public void execute() {
		Item neem = Inventory.getItem(22444);
		if(neem != null && interacting != null) {
			neem.getWidgetChild().interact("Sprinkle");
			Task.sleep(500);
			interacting.interact("Attack", interacting.getName());
			Task.sleep(500);
		}

	}

}
