package org.kenneh.scripts.aiofighter.nodes;

import org.kenneh.core.api.Misc;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;



public class EnableRun extends Node {

	@Override
	public boolean activate() {
		return !Misc.isRunEnabled() && Walking.getEnergy() >= 20;
	}
	
	public void setRun(boolean active) {
		if(active && Misc.isRunEnabled()) {
			return;
		}
		if(active && !Misc.isRunEnabled()) {
			Widgets.get(750, 3).interact("Turn run mode on");
		}
		if(!active && !Misc.isRunEnabled()) {
			return;
		}
		if(!active && Misc.isRunEnabled()) {
			Widgets.get(750, 3).interact("Turn run mode off");
		}
	}

	@Override
	public void execute() {
		setRun(true);
		Task.sleep(2000);
	}

}
