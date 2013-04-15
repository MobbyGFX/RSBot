package org.kenneh.scripts.hydrachopper;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.wrappers.Tile;

public class Antiban implements Node {
	// really just a failsafe to prevent idle log
	
	public int distanceTo(final Tile tile) {
		return (int) Calculations.distanceTo(tile);
	}
	
	@Override
	public boolean activate() {
		return Players.getLocal().getAnimation() != -1 && distanceTo(Constants.treeTile) <= 5;
	}

	@Override
	public void execute() {
		int rnd = Random.nextInt(0, 300);
		if(rnd == Random.nextInt(100, 200)) {
			System.out.println("[Antiban] Changing camera angle..");
			Camera.setAngle(Random.nextInt(0, 360));
		}
	}
	
}
