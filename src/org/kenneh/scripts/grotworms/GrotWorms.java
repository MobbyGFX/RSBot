package org.kenneh.scripts.grotworms;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.kenneh.core.api.framework.KNode;
import org.kenneh.core.api.framework.KScript;
import org.kenneh.core.api.utils.AbilityHandler;
import org.kenneh.core.api.utils.Misc;
import org.kenneh.core.graphics.PaintUtils;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.input.Mouse.Speed;
import org.powerbot.game.api.util.SkillData;
import org.powerbot.game.api.util.Timer;

@Manifest(authors = { "Kenneh" }, description = "Test", name = "Kenneh's Grotworms", hidden = false)
public class GrotWorms extends KScript {

	private final SkillData sd = new SkillData();
	private final Timer timer = new Timer(0);
	private long startTime = 0;
	
	@Override
	public boolean init() {
		Mouse.setSpeed(Speed.VERY_FAST);
		Settings.setLoot(Settings.RARE_DROP_TABLE);
		Settings.setLoot(Settings.GROTWORM_LOOT);
		Settings.pw.storePrice(Settings.RARE_DROP_TABLE);
		Settings.pw.storePrice(Settings.GROTWORM_LOOT);
		Settings.pw.storePrice(995, 1);
		Settings.pw.storePrice(385, 0);
		Settings.pw.storePrice(450, Settings.pw.getPrice(449));
		Settings.pw.storePrice(1180, Settings.pw.getPrice(1179));
		getContainer().submit(new AbilityHandler());
		final KNode[] nodes = {
				new Failsafe(),  new FightWorms(), new Eating(), new GoToBank(), new LootItems(), new WalkToGrots(),
				new Alching(), new BankItems(), 
		};
		submit(nodes);
		startTime = System.currentTimeMillis();
		return true;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void paint(final Graphics g) {
		final Graphics2D g2d = (Graphics2D) g;
		g.setColor(Color.WHITE);
		g.drawString("Runtime: "+ timer.toElapsedString(), 5, 112);
		g.drawString("Status: " + Settings.getStatus(), 5, 124);
		g.drawString("Profit: "+ Misc.perHour(startTime, Settings.getLootValue()) + "(+" + Settings.getLootValue() + ")", 5, 136);
		g.drawString("Distance to bank: " + Settings.BANK_TILE.distanceTo(), 5, 148);
		g.setFont(new Font("Calibri", Font.PLAIN, 14));
		int x = 8, y = 396;
		for(int i = 0; i < PaintUtils.SKILL_NAMES.length -1; i++) {
			if(sd.experience(i) > 0) {
				PaintUtils.drawProgressBar(g2d, x, y, 487, 17, Color.BLACK, PaintUtils.getSkillColor(i), 150, PaintUtils.getPercentToNextLevel(i));
				g.setColor(Color.WHITE);
				g.drawString(PaintUtils.generateString(sd, i), x + 5, y + 13);
				y += 18;
			}
		}
//		for(Tile t : Settings.GROT_CAVE.getTileArray()) {
//			if(t.isOnScreen()) {
//				t.draw(g);
//			}
//		}
	}

}
