package org.kenneh.core.api;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.kenneh.core.api.astar.AStar;
import org.kenneh.scripts.aiofighter.FighterGUI;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.SkillData;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.graphics.CapturedModel;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.node.GroundItem;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

import ch.swingfx.twinkle.NotificationBuilder;
import ch.swingfx.twinkle.style.theme.DarkDefaultNotification;
import ch.swingfx.twinkle.window.Positions;


public class Misc {
	
	public static int[] setToArray(final Set<Integer> set) {
		final int length = set.size();
		final Iterator<Integer> it = set.iterator();
		int[] temp = new int[length];
		int index = 0;
		while(it != null && it.hasNext()) {
			temp[index] = it.next();
			index++;
		}
		return temp;
	}

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

	public static void showMessage(final String title, final String message, final Image img) {
		if(FighterGUI.showPopups){ 
			try {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						new NotificationBuilder()
						.withStyle(new DarkDefaultNotification()
						.withMessageFont(new Font("Calibri", Font.PLAIN, 13))
						.withWindowCornerRadius(25))
						.withTitle(title)
						.withMessage(message)
						.withDisplayTime(3000)
						.withPosition(Positions.SOUTH_EAST)
						.withIcon(new ImageIcon(img))
						.showNotification();
					}
				});
			} catch(SecurityException a) {

			}
		}
	}

	public static String capitalize(String toCaps) {
		if(toCaps.length() == 0) {
			return null;
		} else {
			final String first = String.valueOf(toCaps.charAt(0));
			return toCaps.replaceFirst(first, String.valueOf(first).toUpperCase());
		}
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
		sb.append(capitalize(name) +": ");
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

	public static final String[] SKILL_NAMES = {"attack", "defence",
		"strength", "constitution", "range", "prayer", "magic", "cooking",
		"woodcutting", "fletching", "fishing", "firemaking", "crafting",
		"smithing", "mining", "herblore", "agility", "thieving", "slayer",
		"farming", "runecrafting", "hunter", "construction", "summoning",
		"dungeoneering", "-unused-"};

	public static NPC getNearest(NPC[] mobs) {
		int distance = 9999;
		NPC temp = null;
		for(NPC n : mobs) {
			int tempd = distanceTo(n);
			if(tempd < distance) {
				temp = n;
				distance = tempd;
			}
		}
		return temp;
	}

	public static int distanceTo(Locatable t) {
		return AStar.findDistance(t.getLocation());
	}

	public static NPC getNearest(final String name) {
		return getNearest(NPCs.getLoaded(new Filter<NPC>() {
			@Override
			public boolean accept(NPC arg0) {
				return arg0.getName().equals(name);
			}
		}));
	}

	public static SceneObject getNearest(SceneObject[] mobs) {
		int distance = 9999;
		SceneObject temp = null;
		for(SceneObject n : mobs) {
			int tempd = distanceTo(n);
			if(tempd < distance) {
				temp = n;
				distance = tempd;
			}
		}
		return temp;
	}

	public static SceneObject getNearest(final int... id) {
		return getNearest(SceneEntities.getLoaded(id));
	}

	public static String formatNumber(int start) {
		DecimalFormat nf = new DecimalFormat("0.0");
		double i = start;
		if(i >= 1000000) {
			return nf.format((i / 1000000)) + "m";
		}
		if(i >=  1000) {
			return nf.format((i / 1000)) + "k";
		}
		return ""+start;
	}

	public static String perHour(long startTime, int gained) {
		return formatNumber((int) ((gained) * 3600000D / (System.currentTimeMillis() - startTime)));
	}

	public static boolean arrayContains(int id, int[] array) {
		for(int i : array) {
			if(i == id) return true;
		}
		return false;
	}

	public static Item returnTeletab() {
		for(Item i : Inventory.getItems()) {
			if(i != null) {
				if(i.getName().toLowerCase().contains("teleport")) {
					return i;
				}
			}
		}
		return null;
	}

	public static int getAntifireTimer() {
		return Settings.get(1299);
	}

	public static boolean isSuperAntifired() {
		return getAntifireTimer() > 32;
	}

	public static boolean isAntifired() {
		return getAntifireTimer() > 2;
	}

	public static int getHpPercent() {
		return Players.getLocal().getHealthPercent();
	}

	public static boolean contains(int[] i) {
		if(Tabs.getCurrent() != Tabs.INVENTORY){
			return false;
		}
		for(int item : i) {
			Item a = Inventory.getItem(item);
			if(a != null) {
				return true;
			}
		}
		return false;
	}

	public static boolean contains(ArrayList<Integer> i) {
		if(Tabs.getCurrent() != Tabs.INVENTORY){
			return false;
		}
		for(int item : i) {
			Item a = Inventory.getItem(item);
			if(a != null) {
				return true;
			}
		}
		return false;
	}

	public static NPC[] convertNPCArray(ArrayList<NPC> integers) {
		NPC[] ret = new NPC[integers.size()];
		for (int i=0; i < ret.length; i++) {
			ret[i] = integers.get(i);
		}
		return ret;
	}

	public static int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		for (int i=0; i < ret.length; i++) {
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}

	public static boolean isOnScreen(Entity e) {
		if(e == null) return false;
		CapturedModel model = null;
		if(model == null)model = e instanceof GroundItem ? (((GroundItem) e).getModel()) : null;
		if(model == null)model = e instanceof SceneObject ? (((SceneObject) e).getModel()) : null;
		if(model == null) return false;

		WidgetChild ab = Widgets.get(640, 6);

		if(ab != null && !ab.visible() || model.getTriangles().length == 0) return e.isOnScreen();

		for(Polygon p : model.getTriangles()) {
			for(int i = 0; i < p.npoints; i++) {
				Point a = new Point(p.xpoints[i], p.ypoints[i]);
				if(ab.contains(a) ||!e.isOnScreen()) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isRunEnabled() {
		return (Settings.get(463) == 1) ? true : false;
	}

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
				//	g.setColor(new Color(255, 255, 255));
				g.drawString("" + text.charAt(i),
						x + FONTMETRICS.stringWidth(text.substring(0, i)) + 4,
						y + 15);
			}
		}
	}

	public static NPC[] convertIntegers(ArrayList<NPC> attackable) {
		NPC[] ret = new NPC[attackable.size()];
		for (int i=0; i < ret.length; i++) {
			ret[i] = attackable.get(i);
		}
		return ret;
	}

}
