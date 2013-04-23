package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.kenneh.scripts.AIOFishingGuild.Inventory;
import org.powerbot.game.api.methods.widget.Bank;

public class BankItems implements KNode {

	@Override
	public boolean canActivate() {
		return Settings.BANK_TILE.distanceTo() <= 5 && 
				!Inventory.containsAll(new int[] { Settings.NATURE_RUNE, Settings.FIRE_RUNE, Settings.TELETAB, Settings.FOOD_ID });
	}

	@Override
	public void activate() {
		if(!Bank.isOpen()) {
			Settings.setStatus("Opening bank");
			Bank.open();
		} else {
			if(Inventory.getCount() != 0) {
				Settings.setStatus("Depositing inventory");
				Bank.depositInventory();
			} else {
				if(!Inventory.contains(Settings.TELETAB)) {
					Settings.setStatus("Withdrawing teleport tab");
					Bank.withdraw(Settings.TELETAB, 1);
				}
				if(!Inventory.contains(Settings.FIRE_RUNE)) { 
					Settings.setStatus("Withdrawing fire runes");
					Bank.withdraw(Settings.FIRE_RUNE, 1000);
				}
				if(!Inventory.contains(Settings.NATURE_RUNE)){
					Settings.setStatus("Withdrawing nature runes");
					Bank.withdraw(Settings.NATURE_RUNE, 200);
				}
				if(!Inventory.contains(Settings.FOOD_ID)) {
					Settings.setStatus("Withdrawing food");
					Bank.withdraw(Settings.FOOD_ID, 2);
				}
				Settings.setStatus("Closing bank");
				Bank.close();
			}
			
		}

	}

}
