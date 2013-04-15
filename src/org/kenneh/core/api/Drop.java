package org.kenneh.core.api;

import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

public class Drop {
	
	public static final void dropAll() {
		dropAll(false);
	}

	public static final void dropAll(final boolean MouseKeys) {
		dropDown(MouseKeys, true);
	}

	public static final void dropAllExcept(final int... Ids) {
		dropAllExcept(false, Ids);
	}

	public static final void dropAllExcept(final boolean MouseKeys, final int... Ids) {
		dropDown(MouseKeys, true, Ids);
	}
	
	public static final void drop(final int... Ids) {
		drop(true, Ids);
	}
	
	public static final void drop(final boolean MouseKeys, final int... Ids) {
		dropDown(MouseKeys, false, Ids);
	}

	private static final void dropDown(final boolean MouseKeys, final boolean Skip, final int... Ids) {
		final WidgetChild inv = Widgets.get(679, 0);
		for (int x = 0; x < 4; x++) {
			for (int y = x; y < 28; y += 4) {
				final WidgetChild spot = inv.getChild(y);
				if (spot != null && checkID(spot.getChildId(), Skip, Ids)) {
					clickItem(spot, MouseKeys);
				}
			}
		}
	}

	private static final boolean checkID(final int ID, final boolean Skip, final int... Ids) {
		if (ID == -1) {
			return false;
		}

		for (int curID : Ids) {
			if (curID == ID) {
				return !Skip;
			}
		}
		return Skip;
	}

	private static final void clickItem(final WidgetChild item, final boolean MouseKeys) {
		if (!item.getBoundingRectangle().contains(Mouse.getLocation())) {
			Move(item.getCentralPoint().x, item.getAbsoluteY() + 5, MouseKeys);
		}
		Mouse.click(false);
		Move(Mouse.getX(), getDropActionYLocation(), MouseKeys);
		Mouse.click(true);
	}

	private static final void Move(final int x, final int y, final boolean Hop) {
		if (Hop) {
			Mouse.hop(x, y, 3, 3);
		} else {
			Mouse.move(x, y, 3, 3);
		}			
	}

	private static final int getDropActionYLocation() {
		final String[] actions = Menu.getItems();
		for (int i = 0; i < actions.length; i++) {
			if (actions[i].contains("Drop ")) {
				return Menu.getLocation().y + 21 + 16 * i + Random.nextInt(3, 6);
			}
		}
		return Menu.getLocation().y + 40;
	}
}