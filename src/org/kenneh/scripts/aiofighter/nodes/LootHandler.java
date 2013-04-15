package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.core.api.MCamera;
import org.kenneh.core.api.Misc;
import org.kenneh.core.api.Test;
import org.kenneh.scripts.aiofighter.DropHandler;
import org.kenneh.scripts.aiofighter.MonsterKiller;
import org.kenneh.scripts.aiofighter.constants.Constants;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.GroundItems;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.node.GroundItem;
import org.powerbot.game.api.wrappers.node.Item;

import sk.general.TimedCondition;


public class LootHandler extends Node {

	static DropHandler dh = new DropHandler();
	
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
					Item food = EatFood.edible();
					if(food != null) {
						food.getWidgetChild().interact("Eat"); // eat food if it exists before dropping items.
						Task.sleep(650);
					} else {
						Item drop = getLowestPricedItem(); // if there is no food in the inventory but we still need space
						if(drop.getId() == item.getId()) {
							MonsterKiller.stopScript = true;;
						}
						if(drop != null) { // if the inventory contains priced items
							drop.getWidgetChild().interact("Drop"); // drop the lowest priced item in the inventory
							Task.sleep(650);
						}
					}
				}
			} else {
				Item food = EatFood.edible();
				if(food != null) {
					food.getWidgetChild().interact("Eat"); // eat food if it exists before dropping items.
					Task.sleep(650);
				} else {
					MonsterKiller.stopScript = true;
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
			for(int i : MonsterKiller.loot) {
				if(arg0.getId() == i
						&& MonsterKiller.isInLootArea(arg0)) return true;
			}
			return false;
		}
	};

	public static GroundItem getLoot() {
		return GroundItems.getNearest(LOOT);
	}

	@Override
	public boolean activate() {
		return !AbilityHandler.waitForAbility && !AbilityHandler.waitingForRejuv && isValid() && Players.getLocal().getInteracting() == null && !Players.getLocal().isMoving();
	}

	public static Item getLowestPricedItem() {
		if(Tabs.getCurrent() != Tabs.INVENTORY) return null;
		int price = Integer.MAX_VALUE;
		Item item = null;
		for(Item i : Inventory.getItems()) {
			if(PriceChecker.lootlist.containsKey(i.getId())) {
				if(PriceChecker.lootlist.get(i.getId()) * i.getStackSize() < price && i.getStackSize() == 1) {
					if(i.getId() != Constants.foodID 
							&& i.getId() != MonsterKiller.shieldId
							&& i.getId() != Constants.teletab 
							&& Test.getName(i.getId()) != null 
							&& i.getId() != 954
							&& !Misc.arrayContains(i.getId(), MonsterKiller.summoning)
							&& !Test.getName(i.getId()).contains("noted")) {

						price = PriceChecker.lootlist.get(i.getId()) * i.getStackSize();
						item = i;
					}
				}
			}
		}
		return item;
	}
	
	//static DropHandler dh = new DropHandler();

	public static void take(final GroundItem item) {
		if(item == null) return;
		int id = item.getId();
		int stack = item.getGroundItem().getStackSize();
		if(item.interact("Take", item.getGroundItem().getName())) {
			//Logger.log("Looting " + item.getGroundItem().getStackSize() + "x " + item.getGroundItem().getName());
			MonsterKiller.status = "Looting " + item.getGroundItem().getStackSize() + "x " + item.getGroundItem().getName();
			new TimedCondition(2000) {
				@Override
				public boolean isDone() {
					return item == null;
				}
			}.waitStop();
			try {
				int value = 0;
				if(PriceChecker.lootlist.containsKey(id)) {
					value = PriceChecker.lootlist.get(id);
				}
				if(value == -1) {
					value = PriceChecker.lootlist.get(id + 1);
					if(value != -1) {
						totalValue += value * stack;
					}
				} else {
					totalValue += value * stack;
				}
			} catch(Exception a) {

			}
			DropHandler.populate(id, stack);
		}
	}

	@Override
	public void execute() {
		loot();
	}

}
