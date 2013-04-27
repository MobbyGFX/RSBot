package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.kenneh.scripts.AIOFishingGuild.Misc;
import org.powerbot.game.api.methods.Widgets;

public class HandlePin implements KNode {

	public boolean isBankPinOpen() {
		return Widgets.get(13, 0).isOnScreen();
	}
	
	@Override
	public boolean canActivate() {
		return isBankPinOpen() && Settings.getPin() != 0000;
	}

	@Override
	public void activate() {
		final String pin = String.valueOf(Settings.getPin());
		String msg = Widgets.get(13, 28).getText();
		if(msg.contains("FIRST")) {
			Widgets.get(13, Integer.parseInt(String.valueOf(pin.charAt(0))) + 6).click(true);
			Misc.sleep(750, 1200);
		}
		if(msg.contains("SECOND")) {
			Widgets.get(13,  Integer.parseInt(String.valueOf(pin.charAt(1))) + 6).click(true);
			Misc.sleep(750, 1200);
		}
		if(msg.contains("THIRD")) {
			Widgets.get(13, Integer.parseInt(String.valueOf(pin.charAt(2))) + 6).click(true);
			Misc.sleep(750, 1200);
		}
		if(msg.contains("FOURTH")) {
			Widgets.get(13, Integer.parseInt(String.valueOf(pin.charAt(3))) + 6).click(true);
			Misc.sleep(750, 1200);
		}
	}

}
