package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Timer;

public class Failsafe implements KNode {

	@Override
	public boolean canActivate() {
		return FightWorms.getBestGrot() != null && !Settings.GROT_CAVE.contains(Players.getLocal());
	}

	@Override
	public void activate() {
		final Timer timer = new Timer(5000);
		while(timer.isRunning() && Settings.GROT_CENTER_TILE.distanceTo() > 5) {
			Walking.walk(Settings.GROT_CENTER_TILE);
		}
	}

}
