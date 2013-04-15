package org.kenneh.scripts.hydrachopper;

import org.kenneh.core.api.Misc;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.wrappers.node.Item;

public class BankLogs implements Node {

	@Override
	public boolean activate() {
		return Misc.distanceTo(Constants.bankTile) <=5 && Inventory.isFull();
	}

	@Override
	public void execute() {
		if(!Bank.isOpen()) {
			Bank.open();
		} else {
			Bank.deposit(Constants.myTree.getLogId(), 0);
			if(Inventory.getItem(Constants.hydraPouch) == null) {
				Bank.withdraw(Constants.hydraPouch, 1);
			}
			if(Inventory.getItem(Constants.restores) == null) {
				Item i =  Bank.getItem(Constants.restores);
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
