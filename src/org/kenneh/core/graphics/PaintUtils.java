package org.kenneh.core.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.net.URL;

import javax.imageio.ImageIO;

import org.kenneh.core.api.utils.Misc;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.SkillData;
import org.powerbot.game.api.util.Time;

public class PaintUtils {

	public static Color getSkillColor(int index) {
		switch(index) {
		case Skills.ATTACK:
			return Color.red;
		case Skills.STRENGTH:
			return Color.green;
		case Skills.DEFENSE:
			return Color.blue;
		case Skills.RANGE:
			return Color.orange;
		case Skills.MAGIC:
			return Color.blue;
		case Skills.CONSTITUTION:
			return Color.pink;
		case Skills.SUMMONING:
			return Color.cyan;
		case Skills.SLAYER:
			return Color.gray;
		}
		return Color.MAGENTA;
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

	public static String generateString(SkillData sd, int index) {
		StringBuilder sb = new StringBuilder();
		String name = SKILL_NAMES[index];
		sb.append(Misc.capitalize(name) +": ");
		sb.append(Skills.getRealLevel(index) + "(+" + sd.level(index) + ") ");
		sb.append("Experience: " + sd.experience(SkillData.Rate.HOUR, index) + "(+" + sd.experience(index) + ") ");
		sb.append("TTL: " + (Skills.getRealLevel(index) == 99 ? "N/A" : Time.format(sd.timeToLevel(SkillData.Rate.HOUR, index))));
		if(index == Skills.SLAYER) {
			sb.append(" NPCS Left: " + Settings.get(183));
		}
		return sb.toString();
	}

	public static int getPercentToNextLevel(final int index) {
		if (index > SKILL_NAMES.length - 1) {
			return -1;
		}
		final int lvl = Skills.getRealLevel(index);
		return getPercentToLevel(index, lvl + 1);
	}

	public static int getPercentToLevel(final int index, final int endLvl) {
		if (index > SKILL_NAMES.length - 1) {
			return -1;
		}
		final int lvl = Skills.getRealLevel(index);
		if (lvl == 99 || endLvl > 99) {
			return 100;
		}
		final int xpTotal = Skills.XP_TABLE[endLvl] - Skills.XP_TABLE[lvl];
		if (xpTotal == 0) {
			return 0;
		}
		final int xpDone = Skills.getExperience(index) - Skills.XP_TABLE[lvl];
		return 100 * xpDone / xpTotal;
	}

	public static final String[] SKILL_NAMES = {
		"attack", "defence",
		"strength", "constitution", "range", "prayer", "magic", "cooking",
		"woodcutting", "fletching", "fishing", "firemaking", "crafting",
		"smithing", "mining", "herblore", "agility", "thieving", "slayer",
		"farming", "runecrafting", "hunter", "construction", "summoning",
		"dungeoneering", "-unused-"
	};

	public static Image getImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch(Exception a) {
			a.printStackTrace();
		}
		return null;
	}


	public static void drawGradientText(Graphics g, String text, int x, int y, Color c) {
		g.setFont(new Font("Calibri", 13, Font.PLAIN));
		Graphics2D g2 = (Graphics2D) g;
		Color color3 = new Color(51, 51, 51, 205);
		Font font1 = new Font("Arial", 0, 12);
		g.setFont(font1);
		FontMetrics FONTMETRICS = g.getFontMetrics();

		Rectangle textBox = new Rectangle(x, y - g.getFont().getSize(),
				(int) FONTMETRICS.getStringBounds(text, g).getWidth() + 8,
				(int) FONTMETRICS.getStringBounds(text, g).getHeight() + 5);

		Paint defaultPaint = g2.getPaint();

		g2.setPaint(new RadialGradientPaint(new Point.Double(textBox.x
				+ textBox.width / 2.0D, textBox.y + textBox.height / 2.0D),
				(float) (textBox.getWidth() / 2.0D),
				new float[] { 0.5F, 1.0F }, new Color[] {
			new Color(color3.getRed(), color3.getGreen(), color3
					.getBlue(), 175),
					new Color(0.0F, 0.0F, 0.0F, 0.8F) }));

		g.fillRect(textBox.x, textBox.y + 12, textBox.width, textBox.height);
		g2.setPaint(defaultPaint);
		g.setColor(Color.BLACK);
		g.drawRect(textBox.x, textBox.y + 12, textBox.width, textBox.height);
		g.setColor(c);
		g.drawString(text, x + 4, y + 15);
		for (int i = 0; i < text.length(); i++) {
			if (Character.isDigit(text.charAt(i))) {
				g.drawString("" + text.charAt(i),
						x + FONTMETRICS.stringWidth(text.substring(0, i)) + 4,
						y + 15);
			}
		}
	}

}
