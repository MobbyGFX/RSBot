package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.core.api.utils.Misc;
import org.kenneh.scripts.aiofighter.MonsterKiller;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.wrappers.node.Item;


public class Teleport extends Node {
	
	public boolean outtaAntifire() {
		return MonsterKiller.antifires != null && !Misc.contains(MonsterKiller.antifires) && !Potions.isAntifired();
	}

	@Override
	public boolean activate() {
		if(Tabs.getCurrent() != Tabs.INVENTORY) {
			return false;
		}
		return Misc.getHpPercent() < 40  && EatFood.edible() == null  || BarrowsCheck.teleOut || outtaAntifire();
	}

	@Override
	public void execute() {
		MonsterKiller.status = "Teleporting";
		Item tab = Misc.returnTeletab();
		if(tab != null) {
			tab.getWidgetChild().interact("Break");
		}
		Task.sleep(500);
		MonsterKiller.stopScript = true;
	}

}
