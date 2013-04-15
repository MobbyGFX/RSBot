package org.kenneh.core.graphics;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.SkillData;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.SkillData.Rate;
import org.powerbot.game.api.util.Timer;

public class Paint {

	public String status = "";
	public String name = "";
	public String version = "";

	public Timer t = new Timer(0); 
	public int expGain[] = new int[25];
	public int expGainHr[] = new int[25];
	public SkillData sd = new SkillData(t);
	
	public static boolean[] expGained = new boolean[25];
	public static long ttl[] = new long[25];
	public int[] levelsGained = new int[25];

	public final Font title = new Font("Calibri", Font.BOLD, 16);
	public final Font info = new Font("Calibri", Font.BOLD, 13);

	public String[] ttl() {
		String[] i = new String[25];
		for(int i2 = 0; i2 < 25; i2++) {
			if(expGained[i2]) {
				switch(i2) {
				case Skills.ATTACK:
					i[i2] = "Attack: " + ttl[i2];
					break;
				case Skills.DEFENSE:
					i[i2] = "Defense: " + ttl[i2];
					break;
				case Skills.RANGE:
					i[i2] = "Ranged: " + ttl[i2];
					break;
				case Skills.CONSTITUTION:
					i[i2] = "Constitution: " +ttl[i2];
				}
			}
		}
		return i;
	}

	public void xpTest() {
		for(int i = 0; i < Skills.getBottomLevels().length; i++) {
			expGain[i] = sd.experience(i);
			expGainHr[i] = sd.experience(Rate.HOUR, i);
			expGained[i] = expGain[i] > 0;
			ttl[i] = sd.timeToLevel(Rate.HOUR, i);
			levelsGained[i] = sd.level(i);
		}
	}

	public static int getTrueCount() {
		int t = 0;
		for(int i = 0; i < Skills.getBottomLevels().length; i++) {
			if(expGained[i]) {
				t++;
			}
		}
		return t;
	}

	private int getPercentToNextLevel(int index) {
		int lvl = Skills.getRealLevel(index);
		if (lvl == 99) {
			return 100;
		}
		int xpTotal = Skills.XP_TABLE[lvl + 1] - Skills.XP_TABLE[lvl];
		if (xpTotal == 0) {
			return 0;
		}
		int xpDone = Skills.getExperience(index) - Skills.XP_TABLE[lvl];
		return 100 * xpDone / xpTotal;
	}

	public void drawPercentBar(int x, int y, int width, int height, Color color, String text, Graphics g, int skill) {
		int percentTil = getPercentToNextLevel(skill);
		double barFill = (percentTil * width) / 100;
		g.setColor(color);
		g.drawRect(x, y, width, height);
		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
		g.fillRect(x, y, width, height);
		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 200));
		g.fillRect(x, y, (int)barFill, height);
		g.setColor(new Color(Color.white.getRed(), Color.white.getGreen(), Color.blue.getBlue(), 100));
		g.fillRect(x, y, (int)barFill, (height/2));
		if(skill == 18) {
			g.setColor(Color.WHITE);
		} else {
			g.setColor(Color.BLACK);
		}
		g.drawString(text, x + 3, y + (height / 2) + (height / 3));
	}

	public int getTotalGain() {
		int total = 0;
		for(int i = 0; i < 25; i++) {
			if(expGained[i]) {
				total += expGain[i];
			}
		}
		return total;
	}

	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		g1.setColor(new Color(Color.BLACK.getRed(), Color.black.getGreen(), Color.black.getBlue(), 150));
		g1.fillRect(7, 394, 489, 16 * getTrueCount());
		g1.fillRect(0, 0, 765, 50);		
		g1.setColor(Color.CYAN);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(title);
		g.drawString(name + " - Running for: " + t.toElapsedString() + " - Status: " + status, 5, 15);
		g.setFont(info);
		g.drawString("Version: " + version, 10, 27);
		int y = 395;
		for(int i3 = 0; i3 < 25; i3++) {
			if(expGained[i3]) {
				switch(i3) {
				case Skills.SUMMONING:
					drawPercentBar(7, y, 489, 15, Color.WHITE, "Summoning" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g, i3);
					y += 16;
					break;
				case 0:
					drawPercentBar(7, y, 489, 15, Color.RED, "Attack" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g, i3);
					y += 16;
					break;
				case 1:
					drawPercentBar(7, y, 489, 15, Color.BLUE, "Defence" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g, i3);
					y += 16;
					break;
				case 2:
					drawPercentBar(7, y, 489, 15, Color.GREEN, "Strength" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g, i3);
					y += 16;
					break;
				case 3:
					drawPercentBar(7, y, 489, 15, Color.PINK, "Constitution" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g, i3);
					y += 16;
					break;
				case 4:
					drawPercentBar(7, y, 489, 15, Color.ORANGE, "Range" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g, i3);
					y += 16;
					break;
				case Skills.PRAYER:
					drawPercentBar(7, y, 489, 15, Color.WHITE, "Prayer" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g, i3);
					y += 16;
					break;
				case Skills.MAGIC:
					drawPercentBar(7, y, 489, 15, Color.CYAN, "Magic" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g, i3);
					y += 16;
					break;
				case 18:
					drawPercentBar(7, y, 489, 15, Color.GRAY, "Slayer" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]) + " Mobs left: " + (Settings.get(183)), g, i3);
					y += 16;
					break;
				case 15:
					drawPercentBar(7, y, 489, 15, Color.GREEN.brighter(), "Herblore" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g, i3);
					y += 16;
					break;
				case 10:
					drawPercentBar(7, y, 489, 15, Color.CYAN, "Fishing" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g, i3);
					y += 16;
					break;
				default:
					drawPercentBar(7, y, 489, 15, Color.MAGENTA, "Skill ID: " + i3 +  ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g, i3);
					y += 16;
					break;
				}
			}
		}
		xpTest();
	}

}
