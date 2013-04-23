package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.kenneh.core.api.utils.MCamera;
import org.kenneh.core.api.utils.Misc;
import org.kenneh.scripts.aiofighter.nodes.PriceChecker;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.GroundItems;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.node.GroundItem;
import org.powerbot.game.api.wrappers.node.Item;

import sk.general.TimedCondition;

public class LootItems implements KNode {

	public static int totalValue = 0;

	public static void loot() {
		final GroundItem item = getLoot();
		int distance = (int)Calculations.distanceTo(item);
		if(distance > 5) {
			Walking.walk(item);
		}
		if(!Misc.isOnScreen(item)) {
			MCamera.turnTo(item, 50);
		}
		if(Inventory.isFull()) {
			if(Inventory.getItem(item.getId()) != null) { // inventory contains the item
				if(Inventory.getItem(item.getId()).getStackSize() > 1) { // item is stackable
					take(item);
				} else { // item is in inv, but isnt stackable. Make space for new loot
					Item food = Eating.edible();
					food.getWidgetChild().interact("Eat"); // eat food if it exists before dropping items.
					Task.sleep(650);
				}
			} else {
				Item food = Eating.edible();
				if(food != null) {
					food.getWidgetChild().interact("Eat"); // eat food if it exists before dropping items.
					Task.sleep(650);
				}
			}
		} else {
			take(item);
		}
	}

	public static boolean isValid() {
		return getLoot() != null;
	}

	public static Filter<GroundItem> LOOT = new Filter<GroundItem>() {

		@Override
		public boolean accept(GroundItem arg0) {
			if(arg0.getId() == 385 && Inventory.getCount(385) <= 3) {
				return false;
			}
			if(arg0.getId() == 22448 && arg0.getGroundItem().getStackSize() < 10) {
				return false;
			}
			if(arg0.getId() == 22445 && arg0.getGroundItem().getStackSize() >= 100) {
				return true;
			}
			for(int i : Settings.getLoot()) {
				if(arg0.getId() == i && Settings.isInArea(arg0)) 
					return true;
			}
			return false;
		}
	};

	public static GroundItem getLoot() {
		return GroundItems.getNearest(LOOT);
	}

	@Override
	public boolean canActivate() {
		return Settings.GROT_CAVE.contains(Players.getLocal()) && isValid() && Players.getLocal().getInteracting() == null && !Players.getLocal().isMoving();
	}

	public static void take(final GroundItem item) {
		if(item == null) return;
		int id = item.getId();
		int stack = item.getGroundItem().getStackSize();
		if(item.interact("Take", item.getGroundItem().getName())) {
			Settings.setStatus("Looting " + item.getGroundItem().getStackSize() + "x " + item.getGroundItem().getName());
			new TimedCondition(2000) {
				@Override
				public boolean isDone() {
					return item == null;
				}
			}.waitStop();
			try {
				int value = 0;
				if(PriceChecker.priceWrapper.contains(id)) {
					value = PriceChecker.priceWrapper.getPrice(id);
				}
				if(value == 0) {
					value = PriceChecker.priceWrapper.getPrice(id - 1);
					if(value != 0) {
						int price = value * stack;
						totalValue += price;
						Settings.setValue(price);
					}
				} else {
					totalValue += value * stack;
					Settings.setValue(value * stack);
				}
			} catch(Exception a) {

			}
		}
	}

	@Override
	public void activate() {
		loot();
	}

}
