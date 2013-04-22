package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.scripts.aiofighter.Settings;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.interactive.NPC;

public class KillCount extends LoopTask {

	@Override
	public int loop() {
		if(Players.getLocal().isInCombat()) {
			final NPC interacting = (NPC) Players.getLocal().getInteracting();
			if(interacting != null) {
				if(interacting.getHealthPercent() == 0) {
					Settings.killCount++; 
					final Timer timer = new Timer(1000);
					while(timer.isRunning() && interacting != null) {
						sleep(20);
					}
				}
			}
		}
		return 200;
	}

}
