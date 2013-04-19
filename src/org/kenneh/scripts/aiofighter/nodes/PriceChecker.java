package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.core.api.PriceWrapper;
import org.kenneh.core.graphics.Logger;
import org.kenneh.scripts.aiofighter.MonsterKiller;
import org.kenneh.scripts.aiofighter.Settings;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.game.api.methods.node.GroundItems;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.node.GroundItem;

public class PriceChecker extends LoopTask {

	public static PriceWrapper priceWrapper = new PriceWrapper();

	public Filter<GroundItem> G_ITEM_FILTER = new Filter<GroundItem>() {

		@Override
		public boolean accept(GroundItem arg0) {
			if(arg0 != null) {
				int id = arg0.getId();
				String name = arg0.getGroundItem().getName();
				if(name == null) {
					name = "null";
				}
				int stacksize = arg0.getGroundItem().getStackSize();
				if(priceWrapper.contains(id)) {
					int totalPrice = stacksize * priceWrapper.getPrice(id);
					if (totalPrice >= MonsterKiller.overXValue) {
						return true;
					}
				} else {
					if(priceWrapper.getPrice(id) != -1) {
						System.out.println("Price for "+ name+"("+id+") found!");
						priceWrapper.storePrice(id);
					} else {
						if(priceWrapper.getPrice(id - 1) != -1) {
							System.out.println("Noted price for "+ name+"("+id+") found!");
							priceWrapper.storePrice(id - 1);
						} else {
							priceWrapper.storePrice(id, 0);
						}
					}
				}
			}
			return false;
		}

	};

	@Override
	public int loop() {
		GroundItem[] all = GroundItems.getLoaded(G_ITEM_FILTER);
		if (all != null) {
			for (GroundItem indiv : all) {
				if (indiv != null) {
					if (!Settings.getLootSet().contains(indiv.getId())) {
						Settings.getLootSet().add(indiv.getId());
						Logger.log("Added: " + indiv.getGroundItem().getName() + " to list!");
					}
				}
			}
		}
		return 50;
	}

}
