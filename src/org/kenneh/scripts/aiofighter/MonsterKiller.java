package org.kenneh.scripts.aiofighter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.kenneh.core.api.Misc;
import org.kenneh.core.graphics.Logger;
import org.kenneh.scripts.aiofighter.constants.Constants;
import org.kenneh.scripts.aiofighter.nodes.AbilityHandler;
import org.kenneh.scripts.aiofighter.nodes.Expandbar;
import org.kenneh.scripts.aiofighter.nodes.LootHandler;
import org.kenneh.scripts.aiofighter.nodes.Prayer;
import org.kenneh.scripts.aiofighter.nodes.PriceChecker;
import org.kenneh.scripts.aiofighter.nodes.SprinkleNeem;
import org.powerbot.core.event.events.MessageEvent;
import org.powerbot.core.event.listeners.MessageListener;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.core.script.job.state.Tree;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.input.Mouse.Speed;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Equipment;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.SkillData;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Item;

import sk.action.ActionBar;


@Manifest(authors = { "Kenneh" }, name = "Kenneh's AIO Fighter", 
description = "Select stuff, fight mobs, loot things, gain xp. :3 \nBring a teleport tablet for safety", 
version = 2.48,
website = "http://loot-files.atspace.com",
vip = true)
public class MonsterKiller extends ActiveScript implements PaintListener, MouseListener, MessageListener, MouseMotionListener {

	Timer t = new Timer(0);
	SkillData sd = null;

	public Image mouseimg = null;

	public static long startTime;

	long lastcall  = 0;
	long elapsed = 0;

	public static String status = "Starting up...";

	public static Tile myPos = null;
	public static int radius = 10;

	public static boolean isInArea(Locatable loc) {
		return Calculations.distance(myPos, loc) <= radius;
	}

	public static boolean isInLootArea(Locatable arg0) {
		return Calculations.distance(myPos, arg0) <= radius + 2;
	}

	public static void drawArea(Graphics g2d) {
		try {
			Color color = new Color(Color.cyan.getRed(), Color.cyan.getGreen(),
					Color.cyan.getBlue(), 150);
			g2d.setColor(color);
			Point p = Calculations.worldToMap(myPos.getX(), myPos.getY());
			g2d.fillOval(p.x - (radius * 5), p.y - (radius * 5),
					5 * (radius * 2), 5 * (radius * 2));
		} catch (Exception a) {
		}
	}

	@Override
	public void onRepaint(Graphics g) {
		try {
			final long starttime = System.currentTimeMillis();
			int fps = (int) (1000 / (starttime - lastcall));
			lastcall = starttime;
			if (FighterGUI.getFrame() != null) {
				if (FighterGUI.getFrame().isVisible()) {
					myPos = Players.getLocal().getLocation();
					if (!FighterGUI.radiusIsNull()) {
						radius = FighterGUI.getRadius();
					}
				}
			}
			Graphics2D g1 = (Graphics2D) g;
			g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			drawArea(g1);
			g1.setFont(new Font("Calibri", Font.PLAIN, 12));
			elapsed = System.currentTimeMillis() - starttime;
			paint(g);
			g.setColor(Color.CYAN);
			g.drawString("FPS: " + fps, 5, 45);
		} catch (Exception a) {
			a.printStackTrace();
		}
	}


	Logger logger;

	public void onStop() {
		logger.dispose();
		screenCap();
		Misc.showMessage("Kenneh's AIO Fighter", "Script stopped!", MonsterKiller.img);
	}

	public static int antiPotAtValue = 0;

	public static int[] antifires;

	public int[] determineAntifires() {
		if (Misc.contains(Constants.ANTIFIRE)) {
			antiPotAtValue = 1;
			return Constants.ANTIFIRE;
		} else if (Misc.contains(Constants.SUPER_ANTIFIRE)) {
			antiPotAtValue = 32;
			return Constants.SUPER_ANTIFIRE;
		}
		return null;
	}

	public boolean hasAntifires() {
		return Misc.contains(Constants.ANTIFIRE)
				|| Misc.contains(Constants.SUPER_ANTIFIRE);
	}

	public void paint(Graphics arg0) {
		Graphics2D g1 = (Graphics2D) arg0;
		g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int x = 8, y = 90;
		g1.setColor(Color.WHITE);
		g1.setFont(new Font("Calibri", Font.PLAIN, 12));
		g1.drawString("Kennehs AIO Fighter - Runtime: " + t.toElapsedString() + " - Status: " + status, x, y);
		g1.drawString("Total looted value - "  + LootHandler.totalValue + "(+" + Misc.perHour(startTime, LootHandler.totalValue) + ")", x, y + 12);
		if(mouseimg != null) {
			drawMouse(arg0);
		}
		g1.setColor(Color.WHITE);
		y = 396;
		if(show) {
			g1.setFont(new Font("Calibri", Font.PLAIN, 14));
			for(int i = 0; i < Misc.SKILL_NAMES.length -1; i++) {
				if(sd.experience(i) > 0) {
					Misc.drawProgressBar(g1, x, y, 487, 17, Color.BLACK, Misc.getSkillColor(i), 150, Misc.getPercentToNextLevel(i));
					g1.setColor(Color.WHITE);
					g1.drawString(Misc.generateString(sd, i), x + 5, y + 13);
					y += 18;
				}
			}
		}
	}

	public void drawMouse(Graphics g) {
		int centerX = mouseimg.getWidth(null) / 2;
		int centerY = mouseimg.getHeight(null) / 2;
		Point center = new Point(Mouse.getX() - centerX, Mouse.getY() - centerY);
		g.drawImage(mouseimg,  center.x, center.y, null);
	}
	
	public static void setSpeed(Speed s) {
		System.out.println("Setting default mouse speed to: " + s);
		Mouse.setSpeed(s);
	}

	@Override
	public void onStart() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new FighterGUI();
			}
		});
		sd = new SkillData(t);
		logger = new Logger();
		logger.display();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					img = Toolkit.getDefaultToolkit().getImage(new URL("http://puu.sh/2CPmc.gif"));
					System.out.println("Gif loaded");
					mouseimg = ImageIO.read(new URL("http://i.imgur.com/WDgWvVu.png"));
//					Test.addToHashtable();
//					Logger.log("Items loaded: " + Test.pricelist.size());
					Environment.enableRandom(org.powerbot.core.randoms.SpinTickets.class, false);
				} catch (Exception a) {
					a.printStackTrace();
				}
			}
		});
		t.start();
		
		if (!hasAntifires()) {
			Logger.log("You're out of antifires..");
		} else {
			antifires = determineAntifires();
			Item antifire = Inventory.getItem(antifires);
			Logger.log("Using antifire type: " + antifire.getName());
			Logger.log("Using antifire value: " + antiPotAtValue);
		}
		if (Misc.returnTeletab() != null) {
			Item tab = Misc.returnTeletab();
			Logger.log("Detected tab: " + tab.getName());
			Settings.setTeletab(tab.getId());
		} else {
			Logger.log("No teletab detected, stopping script!");
		}

		for(Item i : Inventory.getItems()) {
			for(int i2 : Constants.SHIELDS) {
				if(i.getId() == i2) {
					Logger.log("Found shield: " + Inventory.getItem(i2).getName());
					shieldId = i2;
					break;
				}
			}
		}
		if(shieldId == -1) {
			Logger.log("No shield detected..");
		}

		if(Expandbar.isInvVisible()) {
			checkInv = true;
		}

		mainWeapon = Equipment.getAppearanceIds()[3];
		offWeapon = Equipment.getAppearanceIds()[5];
		barIndex = ActionBar.getCurrentBar();
		Logger.log("Current bar: "+ barIndex);
		getContainer().submit(new AbilityHandler());
		getContainer().submit(new PriceChecker());
		getContainer().submit(new Prayer());
		startTime = System.currentTimeMillis();
		provide(new SprinkleNeem());
		Logger.log("ShieldId: "+ shieldId + " WeaponId: " + mainWeapon);
		Mouse.setSpeed(Speed.VERY_FAST);
	}
	
	public static Image img = null;

	Rectangle chat = Widgets.get(137, 0).getBoundingRectangle();
	public boolean show = true;

	public static boolean checkInv = false;
	public static int mainWeapon = -1;
	public static int shieldId = -1;
	public static int offWeapon = -1;
	public static int barIndex = -1;

	private static final List<Node> jobsCollection = Collections.synchronizedList(new ArrayList<Node>());

	private static Tree jobContainer = null;

	public static synchronized final void provide(final Node... jobs) {
		for (final Node job : jobs) {
			if (!jobsCollection.contains(job)) {
				Logger.log("Providing: " + job.getClass().getSimpleName());
				jobsCollection.add(job);
			}
		}
		jobContainer = new Tree(jobsCollection.toArray(new Node[jobsCollection.size()]));
	}

	String currNode = "";

	@Override
	public int loop() {

		if (stopScript) {
			Item i = Misc.returnTeletab();
			if(i != null) {
				i.getWidgetChild().click(true);
			}
			shutdown();
		}

		if (Game.getClientState() != Game.INDEX_MAP_LOADED) {
			return 1000;
		}

		while (FighterGUI.getFrame() != null && FighterGUI.getFrame().isVisible()) {
			status = "Sleeping for GUI";
			Task.sleep(20);
		}

		if (jobContainer != null && Game.isLoggedIn()) {
			try {
				final Node job = jobContainer.state();
				if (job != null) {
					jobContainer.set(job);
					getContainer().submit(job);
					job.join();
				}
			} catch (Exception a) {
				a.printStackTrace();
			}
		}

		return 50;
	}

	public static boolean stopScript = false;

	public static int[] summoning = {12029, 12039, 3024, 3026, 3028, 3030};

	public static void screenCap() {
		try {
			DateFormat dateFormat = new SimpleDateFormat(
					"dd MMM yyyy hh-mm-ss a");
			Date date = new Date();
			Environment.saveScreenCapture(dateFormat.format(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(chat.contains(arg0.getPoint())) {
			show = !show;
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent source) {
	}

	@Override
	public void messageReceived(MessageEvent arg0) {
		String text = arg0.getMessage().toLowerCase();
		if(text.contains("resistance to dragon")) {
			status = "Detected antifire message";
			if(antifires != null && Inventory.getItem(antifires) != null) {
				Item i = Inventory.getItem(antifires);
				i.getWidgetChild().click(true);
				Task.sleep(500);
			}
		}
		if(text.contains("a level")) {
			Misc.showMessage("Kenneh's AIO Fighter", text, MonsterKiller.img);
		}
		if(text.contains("are dead")) {
			Misc.showMessage("Kenneh's AIO Fighter", "Somehow we've died!", MonsterKiller.img);
		}
		if(text.contains("down here to kill those.")) {
			stopScript=true;
		}
	}

	Point mouse;

	@Override
	public void mouseDragged(MouseEvent arg0) {

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		mouse = arg0.getPoint();
	}

}
