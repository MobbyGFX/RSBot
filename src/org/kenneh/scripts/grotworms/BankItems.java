package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.powerbot.game.api.methods.tab.Equipment;
import org.powerbot.game.api.methods.tab.Equipment.Slot;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.wrappers.node.Item;

public class BankItems implements KNode {

	@Override
	public boolean canActivate() {
		return Settings.BANK_TILE.distanceTo() <= 5 && 
				!Inventory.containsAll(new int[] { Settings.NATURE_RUNE, Settings.FIRE_RUNE, Settings.TELETAB, Settings.FOOD_ID }) 
				|| Inventory.contains(Settings.POLYPORE_STICK) || Inventory.contains(Settings.POLYPORE_STAFF);
	}

	@Override
	public void activate() {
		if(Inventory.contains(Settings.POLYPORE_STAFF)) {
			final Item item = Inventory.getItem(Settings.POLYPORE_STAFF);
			if(item != null) {
				Settings.setStatus("Equipping new staff");
				item.getWidgetChild().interact("Wield");
				return;
			}
		}
		if(!Bank.isOpen()) {
			Settings.setStatus("Opening bank");
			Bank.open();
		} else {
			if(Inventory.contains(Settings.POLYPORE_STICK)) {
				Bank.deposit(Settings.POLYPORE_STICK, 0);
				Bank.close();
				return;
			}
			if(Inventory.getCount() != 0) {
				Settings.setStatus("Depositing inventory");
				Bank.depositInventory();
			} else {
				if(Equipment.getAppearanceId(Slot.WEAPON) == Settings.POLYPORE_STICK) {
					Settings.setStatus("Withdrawing polypore staff");
					Bank.withdraw(Settings.POLYPORE_STAFF, 1);
					Bank.close();
				}
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
