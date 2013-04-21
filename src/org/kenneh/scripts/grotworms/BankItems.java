package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.kenneh.scripts.AIOFishingGuild.Inventory;
import org.powerbot.game.api.methods.widget.Bank;

public class BankItems implements KNode {

	@Override
	public boolean canActivate() {
		return Settings.BANK_TILE.distanceTo() <= 5 && (Inventory.isFull() || !Inventory.contains(Settings.TELETAB) || !Inventory.contains(Settings.FOOD_ID)); 
	}

	@Override
	public void activate() {
		if(!Bank.isOpen()) {
			Settings.setStatus("Opening bank");
			Bank.open();
		} else {
			if(Inventory.isFull()) {
				Settings.setStatus("Depositing inventory");
				Bank.depositInventory();
			} else {
				if(!Inventory.contains(Settings.TELETAB)) {
					Settings.setStatus("Withdrawing teleport tab");
					Bank.withdraw(Settings.TELETAB, 1);
				}
				if(Inventory.getCount(Settings.FOOD_ID) < 2) {
					Settings.setStatus("Withdrawing food");
					Bank.withdraw(Settings.FOOD_ID, 2);
				}
			}
			if(Inventory.contains(Settings.TELETAB) && Inventory.contains(Settings.FOOD_ID) && Bank.isOpen()) {
				Settings.setStatus("Closing bank");
				Bank.close();
			}
		}

	}

}
