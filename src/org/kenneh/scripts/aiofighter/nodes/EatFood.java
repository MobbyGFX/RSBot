package org.kenneh.scripts.aiofighter.nodes;

import java.util.Arrays;
import java.util.List;

import org.kenneh.core.api.utils.Misc;
import org.kenneh.core.graphics.Logger;
import org.kenneh.scripts.aiofighter.MonsterKiller;
import org.kenneh.scripts.aiofighter.Settings;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Summoning;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.WidgetChild;



public class EatFood extends Node {

	
	
	@Override
	public boolean activate() {
		if(Tabs.getCurrent() != Tabs.INVENTORY) {
			return false;
		}
		return Misc.getHpPercent() <= Settings.getEatPercent() && edible() != null;
	}

	public static boolean canHealFromUni() {
		return Summoning.isFamiliarSummoned() 
				&& Summoning.getFamiliar().getName().equals("Unicorn stallion")
				&& Summoning.getSpecialPoints() >= 20
				&& Inventory.getItem(12434) != null;
	};
	
	public static Item edible() {
        Item[] is = Inventory.getItems();
        for (Item i : is) {
            String[] s = i.getWidgetChild().getActions();
            if (s != null) {
                List<String> l = Arrays.asList(s);
                if (l.contains("Eat")) {
                    return i;
                }
            }
        }
        return null;
    }

	@Override
	public void execute() {
		AbilityHandler.waitingForRejuv = false;
		if(canHealFromUni()) {
			Logger.log("Healing from familiar");
			MonsterKiller.status = "Healing from familiar";
			while(Players.getLocal().getHealthPercent() < 100 && Summoning.getSpecialPoints() >= 20) {
				WidgetChild i  = Widgets.get(747, 0);
				i.interact("Cast");
				Task.sleep(500);
			}
		} else if(edible() != null){
			Logger.log("Eating food");
			MonsterKiller.status = "Eating food";
			edible().getWidgetChild().interact("Eat");
		} else {
			Item i = Misc.returnTeletab();
			if(i != null) {
				i.getWidgetChild().click(true);
			}
			MonsterKiller.stopScript = true;
			Task.sleep(5000);
		}
		Task.sleep(2000);
	}

}