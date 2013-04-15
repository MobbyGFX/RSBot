package org.kenneh.scripts;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.randoms.SpinTickets;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.util.Timer;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.GroundItems;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.SkillData;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.node.GroundItem;
import org.powerbot.game.api.wrappers.node.SceneObject;


@Manifest(authors = { "Kenneh" }, description = "Chops Ivy in Ardy", name = "KChopper")
public class KChopper extends ActiveScript implements PaintListener {

	public Timer timer = new Timer(0);
	public SkillData sd = new SkillData();

	public int SKILLID = Skills.WOODCUTTING;

	public String status = "Initialzing";
	
	@Override
	public void onStart() {
		Environment.enableRandom(SpinTickets.class, false);
	}

	public final int[] IVY = {
			46322, 46318, 46324
	};

	public final int[] NESTS = {
			5071, 5072, 5073, 5074, 5075, 5076, 7413, 11966
	};

	public GroundItem getNests() {
		return GroundItems.getNearest(NESTS);
	}

	@Override
	public int loop() {
		final GroundItem nest = getNests();
		if(nest != null) {
			status = "Picking up nest";
			nest.interact("Take");
		} else {
			if(Players.getLocal().getAnimation() == -1) {
				Timer timer = new Timer(1000);
				while(timer.isRunning()) {
					status = "Ensuring animation is done..";
					Task.sleep(20);
				}
				if(Players.getLocal().getAnimation() == -1) {
					final SceneObject ivy = SceneEntities.getNearest(IVY);
					if(ivy != null) {
						if(!ivy.isOnScreen()) {
							status = "Turning to ivy";
							Camera.turnTo(ivy);
						} else {
							status = "Interacting with ivy";
							ivy.interact("Chop");
						}
					} else {
						status = "Waiting for trees to respawn";
					}
				}
			} else {
				status = "Idling while chopping";
				int rnd = Random.nextInt(0, 500);
				switch(rnd) {
				case 275:
					Camera.setAngle(Random.nextInt(0, 360));
					break;
				}
			}
		}
		return 50;
	}

	public static void drawProgressBar(Graphics2D g, final int x, final int y,
			final int width, final int height, final Color main,
			final Color progress, final int alpha, final int percentage) {
		g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON));
		final GradientPaint base = new GradientPaint(x, y, new Color(200, 200,
				200, alpha), x, y + height, main);
		final GradientPaint overlay = new GradientPaint(x, y, new Color(200,
				200, 200, alpha), x, y + height, progress);
		if (height > width) {
			g.setPaint(base);
			g.fillRect(x, y, width, height);
			g.setPaint(overlay);
			g.fillRect(x,
					y + (height - (int) (height * (percentage / 100.0D))),
					width, (int) (height * (percentage / 100.0D)));
		} else {
			g.setPaint(base);
			g.fillRect(x, y, width, height);
			g.setPaint(overlay);
			g.fillRect(x, y, (int) (width * (percentage / 100.0D)), height);
		}
		g.setColor(Color.BLACK);
		g.drawRect(x, y, width, height);
	}

	public String generateString(int index) {
		StringBuilder sb = new StringBuilder();
		sb.append("Woodcutting: ");
		sb.append(Skills.getRealLevel(index) + "(+" + sd.level(index) + ") ");
		sb.append("Experience: " + sd.experience(SkillData.Rate.HOUR, index) + "(+" + sd.experience(index) + ") ");
		sb.append("TTL: " + Time.format(sd.timeToLevel(SkillData.Rate.HOUR, index)));
		return sb.toString();
	}

	@Override
	public void onRepaint(Graphics arg0) {
		Graphics2D g = (Graphics2D)arg0;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int y = 396; int x = 8;
		g.setFont(new Font("Calibri", 0, 14));
		g.setColor(Color.WHITE);
		g.drawString("kChopper - Runtime: " + timer.toElapsedString() + " - Status: " + status, x, y - 21);
		drawProgressBar(g, x, y, 487, 17, Color.BLACK, Color.MAGENTA, 150, getPercentToNextLevel(SKILLID));
		g.setColor(Color.WHITE);
		g.drawString(generateString(8), x + 5, y + 13);
	}

	public int getPercentToNextLevel(final int index) {
		if (index > SKILL_NAMES.length - 1) {
			return -1;
		}
		final int lvl = Skills.getRealLevel(index);
		return getPercentToLevel(index, lvl + 1);
	}

	public int getPercentToLevel(final int index, final int endLvl) {
		if (index > SKILL_NAMES.length - 1) {
			return -1;
		}
		final int lvl = Skills.getRealLevel(index);
		if (lvl == 99 || endLvl > 99) {
			return 0;
		}
		final int xpTotal = Skills.XP_TABLE[endLvl] - Skills.XP_TABLE[lvl];
		if (xpTotal == 0) {
			return 0;
		}
		final int xpDone = Skills.getExperience(index) - Skills.XP_TABLE[lvl];
		return 100 * xpDone / xpTotal;
	}

	public final String[] SKILL_NAMES = {"attack", "defence",
			"strength", "constitution", "range", "prayer", "magic", "cooking",
			"woodcutting", "fletching", "fishing", "firemaking", "crafting",
			"smithing", "mining", "herblore", "agility", "thieving", "slayer",
			"farming", "runecrafting", "hunter", "construction", "summoning",
			"dungeoneering", "-unused-"};

}
