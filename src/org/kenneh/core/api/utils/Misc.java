package org.kenneh.core.api.utils;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.kenneh.core.graphics.Logger;
import org.kenneh.scripts.aiofighter.FighterGUI;
import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.Entity;
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

	public static void savePaint(final int x, final int y, final int w, final int h) {
		final File path = new File(Environment.getStorageDirectory().getPath(), System.currentTimeMillis() + ".png");
		final BufferedImage img = Environment.captureScreen().getSubimage(x, y, w, h);
		try {
			ImageIO.write(img, "png", path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int[] setToArray(final Set<Integer> set) {
		final int length = set.size();
		final Iterator<Integer> it = set.iterator();
		final int[] temp = new int[length];
		int index = 0;
		while(it != null && it.hasNext()) {
			temp[index] = it.next();
			index++;
		}
		return temp;
	}

	public static void showMessage(final String title, final String message, final Image img) {
		Logger.log(message);
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

	public static String perHourInfo(long startTime, int gained) {
		return formatNumber(gained) + "(+" + perHour(startTime, gained) + ")";
	}

	public static String perHour(long startTime, int gained) {
		return formatNumber((int) ((gained) * 3600000D / (System.currentTimeMillis() - startTime)));
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
		return Settings.get(463) == 1;
	}

	public static NPC[] convertIntegers(ArrayList<NPC> attackable) {
		NPC[] ret = new NPC[attackable.size()];
		for (int i=0; i < ret.length; i++) {
			ret[i] = attackable.get(i);
		}
		return ret;
	}

}
