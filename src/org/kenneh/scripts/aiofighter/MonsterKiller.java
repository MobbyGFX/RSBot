package org.kenneh.scripts.aiofighter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.kenneh.core.api.Misc;
import org.kenneh.core.api.Test;
import org.kenneh.core.graphics.Logger;
import org.kenneh.scripts.aiofighter.constants.Constants;
import org.kenneh.scripts.aiofighter.nodes.AbilityHandler;
import org.kenneh.scripts.aiofighter.nodes.Alch;
import org.kenneh.scripts.aiofighter.nodes.AttackOneOf;
import org.kenneh.scripts.aiofighter.nodes.Expandbar;
import org.kenneh.scripts.aiofighter.nodes.LootHandler;
import org.kenneh.scripts.aiofighter.nodes.Potions;
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
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Equipment;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.SkillData;
import org.powerbot.game.api.util.SkillData.Rate;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.Item;

import sk.action.ActionBar;


@Manifest(authors = { "Kenneh" }, name = "MonsterKiller", 
description = "Select stuff, fight mobs, loot things, gain xp. :3 \nBring a teleport tablet for safety", 
version = 2.38,
website = "http://loot-files.atspace.com",
vip = true)
public class MonsterKiller extends ActiveScript implements PaintListener, MouseListener, MessageListener, MouseMotionListener {

	Timer t = new Timer(0);
	SkillData sd = null;

	public Image[] paintboxes = new Image[4];
	public Image hover[] = new Image[3];
	public Image mouseimg = null;
	public Image percentbar = null;

	public String formatNumber(int start) {
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

	public String perHour(int gained) {
		return formatNumber((int) ((gained) * 3600000D / (System.currentTimeMillis() - startTime)));
	}

	public static long startTime;

	private final static Timer timer = new Timer(0);

	public static ArrayList<Integer> fighting = new ArrayList<Integer>();
	public static ArrayList<Integer> loot = new ArrayList<Integer>();
	public static ArrayList<Integer> alchs = new ArrayList<Integer>();

	public double getVersion() {
		return MonsterKiller.class.getAnnotation(Manifest.class).version();
	}

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

	Tile tile = new Tile(3405, 3571, 2);

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
			g1.setFont(new Font("Calibri", Font.PLAIN, 13));
			elapsed = System.currentTimeMillis() - starttime;
			paint(g);
			g.setColor(Color.CYAN);
			g.drawString("FPS: " + fps, 5, 45);
		} catch (Exception a) {
			a.printStackTrace();
		}
		xpTest();
	}

	public static int getTotalGain() {
		int total = 0;
		for(int i = 0; i < 25; i++) {
			if(expGained[i]) {
				total += expGain[i];
			}
		}
		return total;
	}

	Logger logger;

	public static void submitData() {
		try {
			URL url = null;
			if (getTotalGain() > 0) {

				System.out.println("Data submission");
				url = new URL(
						String.format(
								"http://geektalk.pro/sigs/aiofighter/sig/update.php?username=%s&time=%s&exp=%s&profit=%s",
								Environment.getDisplayName(),
								String.valueOf(timer.getElapsed() / 1000),
								String.valueOf(getTotalGain()),
								String.valueOf(LootHandler.totalValue > 0 ? LootHandler.totalValue
										: "zero")));

				URLConnection con = url.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				in.close();
				System.out.println("Data submission complete");
			}
		} catch (Exception e) {
			System.out.println("Data submission error!");
			e.printStackTrace();
		}
	}

	public void onStop() {
		submitData();
		logger.dispose();
		screenCap();
	}

	public static int antiPotAtValue = 0;

	public static int[] antifires;

	public int[] determineAntifires() {
		if (Misc.contains(Potions.antifire)) {
			antiPotAtValue = 1;
			return Potions.antifire;
		} else if (Misc.contains(Potions.superantifire)) {
			antiPotAtValue = 32;
			return Potions.superantifire;
		}
		return null;
	}

	public boolean hasAntifires() {
		return Misc.contains(Potions.antifire)
				|| Misc.contains(Potions.superantifire);
	}

	public void drawPercentBar(int x, int y, int width, int height, Color color, String text, Graphics g, int skill) {
		int percentTil = getPercentToNextLevel(skill);
		double barFill = (percentTil * width) / 100;
		g.setColor(color);
		if(percentbar != null) {
			g.drawImage(percentbar, x, y, null); // i changed to img, but you can just make it a rectangle with the height/width parameters
		}
		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
		g.fillRect(x, y, width, height);
		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 200));
		g.fillRect(x, y, (int)barFill, height);
		g.setColor(new Color(Color.white.getRed(), Color.white.getGreen(), Color.blue.getBlue(), 100));
		g.fillRect(x, y, (int)barFill, (height/2));
		g.setColor(skill == 18 ? Color.white : Color.BLACK);
		g.drawString(text, x + 3, y + (height / 2) + (height / 3));
	}

	public void paint(Graphics arg0) {
		Graphics2D g1 = (Graphics2D) arg0;
		g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g1.setFont(new Font("Calibri", Font.PLAIN, 13));
		int x = 5, y = 90;
		for(int i = 0; i < paintboxes.length; i++) {
			if(paintboxes[i] != null) {
				arg0.drawImage(paintboxes[i], x, y, null);
				if(mouse != null && getRectFromImage(paintboxes[i], x, y).contains(mouse) || clicked[i]) {

					if(rect[i] == null) {
						initRects();
					}

					if(i != 2) {
						g1.setColor(new Color(0, 0, 0, 100));
						g1.fill(rect[i]);
						g1.setColor(Color.WHITE.darker().darker());
						g1.draw(rect[i]);
					}
					switch(i) {
					case 1:
						arg0.drawImage(hover[0], x, y, null);
						g1.setColor(Color.WHITE);
						g1.drawString("Settings..", rect[i].x + 5, rect[i].y + 14);
						g1.drawString(LootHandler.getLowestPricedItem() == null ? "Item to drop:   None" : "Item to drop: " +  LootHandler.getLowestPricedItem().getName(), rect[i].x + 5, rect[i].y + 30);
						g1.drawString("Nearest agressive mob: " + ((AttackOneOf.getNearestAgressiveMob().length > 0) ? AttackOneOf.getNearestAgressiveMob().length : 0), rect[i].x + 5, rect[i].y + 42);
						g1.drawString("Currently attacking: " + (AttackOneOf.getNearest() != null ? AttackOneOf.getNearest().getName() : "none"), rect[i].x + 5, rect[i].y + 54);
						g1.drawString(Alch.getAlchableItem() == null ? "Alchable item:   None" : "Alchable item: " +  Alch.getAlchableItem().getName(), rect[i].x + 5, rect[i].y + 66);
						g1.drawString(LootHandler.getLoot() == null ? "Lootable item: none" : "Lootable item: " + LootHandler.getLoot().getGroundItem().getName(), rect[i].x + 5, rect[i].y + 78);
						g1.drawString("Kills left: " + (Settings.get(183)) + " " + formatNumber(Skills.getExperienceToLevel(Skills.SLAYER, Skills.getRealLevel(Skills.SLAYER) + 1 )) + " xp til level " + (Skills.getRealLevel(Skills.SLAYER) + 1), rect[i].x+5, rect[i].y+90);
						break;
					case 0:
						arg0.drawImage(hover[1], x, y, null);
						g1.setColor(Color.WHITE);
						g1.drawString("Kenneh's AIO Fighter", rect[i].x + 5, rect[i].y + 14);
						g1.drawString("Runtime: " + t.toElapsedString(), rect[i].x + 5, rect[i].y + 30);
						g1.drawString("Experience: " + formatNumber(getTotalGain()) + " (+ " + perHour(getTotalGain() )+")", rect[i].x + 5, rect[i].y + 42);
						g1.drawString("Profit: " + formatNumber(LootHandler.totalValue) + " (+ " + perHour(LootHandler.totalValue) + ")", rect[i].x + 5, rect[i].y + 54);
						g1.drawString("Status: " + status, rect[i].x + 5, rect[i].y +70);
						g1.drawString("Version: " + getVersion() + " by Kenneh", rect[i].x + 60, rect[i].y + 94);
						break;
					case 2:
						arg0.drawImage(hover[0], x, y, null);
						g1.setColor(Color.WHITE);
						g1.drawString("Click to re-open drop table..", rect[i].x+5, rect[i].y+14);
						break;
					case 3:
						arg0.drawImage(hover[2], x, y, null);
						g1.setColor(Color.WHITE);
						g1.drawString("Donation information..", rect[i].x +5, rect[i].y + 14);
						g1.drawString("- Cash donations can be sent to", rect[i].x + 5, rect[i].y + 30);
						g1.drawString("  kenneh_ftw@live.com", rect[i].x + 5, rect[i].y + 42);
						g1.drawString("- RSGP donations can be sent by", rect[i].x+5, rect[i].y+54);
						g1.drawString("  pming me in forums..", rect[i].x + 5, rect[i].y + 66);
						g1.drawString("Note: This is completly optional.",rect[i].x +5, rect[i].y + 94);
						break;
					}
				}
				y += paintboxes[i].getHeight(null);			
			}
		}
		if(mouseimg != null) {
			//m.paintMouse(arg0);
			drawMouse(arg0);
		}
		g1.setColor(Color.WHITE);
		y = 395;
		if(show) {
			for(int i3 = 0; i3 < 25; i3++) {
				if(expGained[i3]) {
					switch(i3) {
					case Skills.SUMMONING:
						drawPercentBar(7, y, 489, 15, Color.WHITE, "Summoning" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g1, i3);
						y += 16;
						break;
					case 0:
						drawPercentBar(7, y, 489, 15, Color.RED, "Attack" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g1, i3);
						y += 16;
						break;
					case 1:
						drawPercentBar(7, y, 489, 15, Color.BLUE, "Defence" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g1, i3);
						y += 16;
						break;
					case 2:
						drawPercentBar(7, y, 489, 15, Color.GREEN, "Strength" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g1, i3);
						y += 16;
						break;
					case 3:
						drawPercentBar(7, y, 489, 15, Color.PINK, "Constitution" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g1, i3);
						y += 16;
						break;
					case 4:
						drawPercentBar(7, y, 489, 15, Color.ORANGE, "Range" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g1, i3);
						y += 16;
						break;
					case Skills.PRAYER:
						drawPercentBar(7, y, 489, 15, Color.WHITE, "Prayer" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g1, i3);
						y += 16;
						break;
					case Skills.MAGIC:
						drawPercentBar(7, y, 489, 15, Color.CYAN, "Magic" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g1, i3);
						y += 16;
						break;
					case 18:
						drawPercentBar(7, y, 489, 15, Color.GRAY, "Slayer" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]) + " Mobs left: " + (Settings.get(183)), g1, i3);
						y += 16;
						break;
					case 15:
						drawPercentBar(7, y, 489, 15, Color.GREEN.brighter(), "Herblore" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g1, i3);
						y += 16;
						break;
					case 10:
						drawPercentBar(7, y, 489, 15, Color.CYAN, "Fishing" + ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g1, i3);
						y += 16;
						break;
					default:
						drawPercentBar(7, y, 489, 15, Color.MAGENTA, "Skill ID: " + i3 +  ": Level: " + Skills.getRealLevel(i3) + "(+" + levelsGained[i3] + ") " + "Experience: " + expGainHr[i3] + " (+" + expGain[i3] + ") - Time to level: " + Time.format(ttl[i3]), g1, i3);
						y += 16;
						break;
					}
				}
			}
		}
	}

	public static int expGain[] = new int[25];
	public int expGainHr[] = new int[25];
	public static boolean[] expGained = new boolean[25];
	public static long ttl[] = new long[25];
	public int[] levelsGained = new int[25];

	public void xpTest() {
		for(int i = 0; i < Skills.getBottomLevels().length; i++) {
			if(sd != null) {
				expGain[i] = sd.experience(i);
				expGainHr[i] = sd.experience(Rate.HOUR, i);
				expGained[i] = expGain[i] > 0;
				ttl[i] = sd.timeToLevel(Rate.HOUR, i);
				levelsGained[i] = sd.level(i);
			}
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

	DropHandler dh = new DropHandler();

	public Rectangle getRectFromImage(Image img, int x, int y) {
		return new Rectangle(x, y, img.getWidth(null), img.getHeight(null));
	}

	public void drawMouse(Graphics g) {

		int centerX = mouseimg.getWidth(null) / 2;
		int centerY = mouseimg.getHeight(null) / 2;
		Point center = new Point(Mouse.getX() - centerX, Mouse.getY() - centerY);
		g.drawImage(mouseimg,  center.x, center.y, null);
	}

	Rectangle[] rect = new Rectangle[paintboxes.length];

	Rectangle[] box = new Rectangle[paintboxes.length];

	public void initRects() {
		for(int i = 0; i < rect.length; i++) {
			rect[i] = new Rectangle(box[i].width + 7, box[i].y, 200, 100);
		}
	}


	@Override
	public void onStart() {
		if(Game.getClientState() != 11) {
			final Timer login = new Timer(25000);
			while(Game.getClientState() != 11) {
				Task.sleep(100);
				if(Game.getClientState() == 11)
					break;
				if(!login.isRunning()) {
					log.info("Please login");
				}
			}
		}
		sd = new SkillData(t);
		dh.init();
		logger = new Logger();
		try {
			box[0] = new Rectangle(5,90,36,34);
			box[1] = new Rectangle(5,124,36,34);
			box[2] = new Rectangle(5,158,36,34);
			box[3] = new Rectangle(5,192,36,33);
			new Thread() {
				public void run() {
					try {
						paintboxes[0] = ImageIO.read(new URL("http://i.imgur.com/qRMwrIm.png"));
						paintboxes[1] = ImageIO.read(new URL("http://i.imgur.com/v083834.png"));
						paintboxes[2] = ImageIO.read(new URL("http://i.imgur.com/jqbP3FN.png"));
						paintboxes[3] = ImageIO.read(new URL("http://i.imgur.com/UpEVWWc.png"));
						hover[0] = ImageIO.read(new URL("http://i.imgur.com/q13bYqd.png"));
						hover[1] = ImageIO.read(new URL("http://i.imgur.com/1xNmiRE.png"));
						hover[2] = ImageIO.read(new URL("http://i.imgur.com/e0kTZAj.png"));
						mouseimg = ImageIO.read(new URL("http://i.imgur.com/WDgWvVu.png"));
					} catch(Exception a) {
						a.printStackTrace();
					}
				}
			}.start();
			Test.addToHashtable();
			//Environment.enableRandom(org.powerbot.core.randoms.SpinTickets.class, false);
		} catch (Exception a) {
			a.printStackTrace();
		}
		Logger.log("Items loaded: " + Test.pricelist.size());
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
			Constants.teletab = tab.getId();
		} else {
			Logger.log("No teletab detected, stopping script!");
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new FighterGUI();
			}
		});

		Mouse.setSpeed(Mouse.Speed.VERY_FAST);

		for(Item i : Inventory.getItems()) {
			for(int i2 : Constants.shields) {
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
		startTime = System.currentTimeMillis();
		provide(new SprinkleNeem());
		Logger.log("ShieldId: "+ shieldId + " WeaponId: " + mainWeapon);
	}

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

	public static int overXValue = 90000;

	public Rectangle[] rectangles() {
		Rectangle[] rect = new Rectangle[4];
		int x = 5, h = 30, w = 30, y = 75;
		for(int i = 0; i <  rect.length; i++) {
			rect[i] = new Rectangle(x, y, w , h);
			y += 32;
		}
		return rect;
	}

	public Image[] sideicons = new Image[rectangles().length];

	boolean[] clicked = new boolean[rectangles().length];

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

	Rectangle[] boxes = new Rectangle[rectangles().length];

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(chat.contains(arg0.getPoint())) {
			show = !show;
		}
		for(int i = 0; i < paintboxes.length; i++) {
			if(box[i].contains(arg0.getPoint())) {
				for(int i2 = 0; i2 < clicked.length; i2++) {
					if(i2 != i) {
						clicked[i2] = false;
						rect[i] = null;
					}
				}
				clicked[i] = !clicked[i];
			}
		}
		if(dh != null && !dh.isVisible() && box[2].contains(mouse)) {
			dh.setVisible(true);
			dh.toFront();
			clicked[2] = false;
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
		String text = arg0.getMessage();
		if(text.contains("resistance to dragon")) {
			status = "Detected antifire message";
			if(antifires != null && Inventory.getItem(antifires) != null) {
				Item i = Inventory.getItem(antifires);
				i.getWidgetChild().click(true);
				Task.sleep(500);
			}
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
