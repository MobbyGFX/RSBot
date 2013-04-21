package org.kenneh.scripts.grotworms;

import java.awt.Graphics;

import org.kenneh.core.api.framework.KScript;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.input.Mouse.Speed;
import org.powerbot.game.api.util.Timer;

@Manifest(authors = { "Kenneh" }, description = "Test", name = "Kenneh's Grotworms", hidden = false)
public class GrotWorms extends KScript implements PaintListener {

	private final Timer timer = new Timer(0);
	
	@Override
	public boolean init() {
		Mouse.setSpeed(Speed.VERY_FAST);
		Settings.setLoot(Settings.RARE_DROP_TABLE);
		Settings.setLoot(Settings.GROTWORM_LOOT);
		submit(new FightWorms());
		submit(new Eating());
		submit(new GoToBank());
		submit(new BankItems());
		submit(new LootItems());
		submit(new WalkToGrots());
		submit(new WalkToCenter());
		return true;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onRepaint(final Graphics g) {
		g.drawString("Runtime: "+ timer.toElapsedString(), 5, 108);
		g.drawString("Status: " + Settings.getStatus(), 5, 120);
	}

}
