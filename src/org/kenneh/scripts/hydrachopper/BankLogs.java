package org.kenneh.scripts.hydrachopper;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.wrappers.node.Item;

public class BankLogs implements Node {

	@Override
	public boolean activate() {
		return Calculations.distanceTo(Settings.bankTile) <=5 && Inventory.isFull();
	}

	@Override
	public void execute() {
		if(!Bank.isOpen()) {
			Bank.open();
		} else {
			Bank.deposit(Settings.myTree.getLogId(), 0);
			if(Inventory.getItem(Settings.hydraPouch) == null) {
				Bank.withdraw(Settings.hydraPouch, 1);
			}
			if(Inventory.getItem(Settings.restores) == null) {
				Item i =  Bank.getItem(Settings.restores);
				if(i != null) {
					Bank.withdraw(i.getId(), 1);
				}
			}
			if(!Inventory.isFull()) {
				Bank.close();
			}
		}
	}

}
