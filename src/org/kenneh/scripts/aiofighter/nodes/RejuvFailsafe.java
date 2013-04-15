package org.kenneh.scripts.aiofighter.nodes;

import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.util.Timer;

public class RejuvFailsafe extends Node {

	@Override
	public boolean activate() {
		return AbilityHandler.waitingForRejuv;
	}

	@Override
	public void execute() {
		Timer timer = new Timer(1000 * 10); // 10 secs
		while(timer.isRunning() && AbilityHandler.waitingForRejuv) {
			Task.sleep(100);
		}
		AbilityHandler.waitingForRejuv = false;
	}

}
