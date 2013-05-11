package org.kenneh.core.api.framework;

import org.powerbot.core.randoms.BankPin;
import org.powerbot.core.randoms.SpinTickets;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.Environment;

public class RandomEventDisabler extends Task {
	
	public void log(String s) {
		System.out.println("[RANDOM] " + s);
	}

	@Override
	public void execute() {
		Task.sleep(5000); // hopefully time this is done, the random handler will be initialized..
		Environment.enableRandom(BankPin.class, false);
		log("BankPin disabled!");
		Environment.enableRandom(SpinTickets.class, false);
		log("SpinTickets disabled!");
	}

}
