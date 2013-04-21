package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.core.api.utils.Misc;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.WidgetChild;


public class SpinTicket extends Node {

	public boolean hasFullUrn() {
		return Inventory.getItem(20426) != null;
	}
	
	public void teleportUrn() {
		WidgetChild urn = Inventory.getItem(20426).getWidgetChild();
		if(urn.validate()) {
			urn.click(true);
		}
	}
	
	@Override
	public boolean activate() {
		if(Tabs.getCurrent() != Tabs.INVENTORY) {
			return false;
		}
		return hasSpinTicket() || hasVials() || combineKeys() || hasFullUrn();
	}

	public boolean combineKeys() {
		return Inventory.getItem(985) != null && Inventory.getItem(987) != null;
	}

	public void combine() {
		WidgetChild TOOTH = Inventory.getItem(985).getWidgetChild();
		WidgetChild LOOP = Inventory.getItem(987).getWidgetChild();
		if(TOOTH.validate() && LOOP.validate()) {
			TOOTH.click(true);
			Task.sleep(1500);
			if(Inventory.isItemSelected()) {
				LOOP.click(true);
			}
		}
	}

	public void dropVials() {
			Item item = Inventory.getItem(229);
			if(item != null) {
				WidgetChild VIAL = item.getWidgetChild();
				if(VIAL.validate()) {
					VIAL.interact("Drop");
				}
			}
	}

	public void claimSpinTicket() {
		WidgetChild ticket = Inventory.getItem(24154).getWidgetChild();
		if(ticket.validate()) {
			ticket.click(true);
		}
	}

	public boolean hasVials() {
		return Misc.contains(new int[] {229});
	}

	public boolean hasSpinTicket() {
		return Inventory.getItem(24154) != null;
	}

	@Override
	public void execute() {
		if(hasVials()) {
			dropVials();
		}
		if(combineKeys()) {
			combine();
		}
		if(hasSpinTicket()) {
			claimSpinTicket();
		}
		if(hasFullUrn()) {
			teleportUrn();
		}
	}

}
