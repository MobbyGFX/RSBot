package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.core.api.utils.Misc;
import org.kenneh.scripts.aiofighter.FighterGUI;
import org.kenneh.scripts.aiofighter.constants.Constants;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;


public class Prayer extends LoopTask {

	public int getPrayerPoints() {
		return Integer.parseInt(Widgets.get(749, 6).getText());
	}

	public boolean potPray() {
		return Misc.contains(Constants.PRAYER_POTIONS) && getPrayerPoints() < 200;
	}

	public boolean isRenewPotted() {
		return Settings.get(902) > 0;
	}

	public boolean potRenewal() {
		return Misc.contains(Constants.PRAYER_RENEWALS) && !isRenewPotted();
	}

	public boolean prayerFruit() {
		return Misc.contains(Constants.PRAYER_FRUIT) && getPrayerPoints() < 200;
	}
	
	public void eatPrayerFruit() {
		Inventory.getItem(Constants.PRAYER_FRUIT).getWidgetChild().interact("Eat");
		Task.sleep(1000);
	}
	
	public void drinkPrayerPot() {
		Inventory.getItem(Constants.PRAYER_POTIONS).getWidgetChild().interact("Drink");
		Task.sleep(1000);
	}

	public boolean prayerIsOn() {
		return org.powerbot.game.api.methods.tab.Prayer.isQuickOn();
	}

	public void drinkRenewal() {
		Inventory.getItem(Constants.PRAYER_RENEWALS).getWidgetChild().interact("Drink");
		Task.sleep(1000);
	}

	public void disableQuickPrayer() {
		Keyboard.sendKey((char) 48);
		Task.sleep(1000);
	}

	public void enableQuickPrayer() {
		Keyboard.sendKey((char) 48);
		Task.sleep(1000);
	}

	@Override
	public int loop() {
		if(FighterGUI.useQuickPrayer) {
			if(Players.getLocal().isInCombat() && AttackOneOf.getNearest() != null) {
				if(prayerIsOn()) {
					if(prayerFruit()) { // prayer fruit
						eatPrayerFruit(); // eat it if we have it before resorting to prayer potions
					}
					if(potPray()) {
						drinkPrayerPot();
					} else {
						// everything should be okay
					}
				} else {
					// fighting, but prayer is off
					if(getPrayerPoints() > 0) {
						// if we actually have prayer points..
						enableQuickPrayer();
					}
				}
			} else {
				if(prayerIsOn()) {
					// prayer is on and not fighting
					disableQuickPrayer();
				}
			}
		}
		return 250;
	}

}
