package org.kenneh.core.api;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.kenneh.core.api.astar.AStar;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.graphics.CapturedModel;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.node.GroundItem;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.wrappers.widget.WidgetChild;


public class Misc {

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

	public static int distanceTo(SceneObject t) {
		return AStar.findDistance(t.getLocation());
	}

	public static SceneObject getNearest(final int... id) {
		return getNearest(SceneEntities.getLoaded(id));
	}

	public static boolean depositAllExcept(final int... itemIDs) {
		for(Integer i : itemIDs) {
			if(i == null) {
				return false;
			}
		}
		if (Bank.isOpen()) {
			if (Inventory.getCount(true) - Inventory.getCount(true, itemIDs) <= 0) {
				return true;
			}
			if (Inventory.getCount() == 0) {
				return true;
			}
			if (Inventory.getCount(true, itemIDs) == 0) {
				return Bank.depositInventory();
			}
			outer:
				for (final Item item : Inventory.getItems()) {
					if (item != null && item.getId() != -1) {
						for (final int itemID : itemIDs) {
							if (item.getId() == itemID) {
								continue outer;
							}
						}
						for (int j = 0; j < 5 && Inventory.getCount(item.getId()) != 0; j++) {
							if (Bank.deposit(item.getId(), 0)) {
								Task.sleep(40, 120);
							}
						}
					}
				}
			return Inventory.getCount(true) - Inventory.getCount(true, itemIDs) <= 0;
		}
		return false;
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

	/**
	 * Calculates a "per hour" rate based on the param (gained)
	 */
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
