package org.kenneh.scripts;

import java.awt.BasicStroke;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.powerbot.core.event.events.MessageEvent;
import org.powerbot.core.event.listeners.MessageListener;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.interactive.Player;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.wrappers.widget.WidgetChild;



@Manifest(hidden = true, authors = { "Kenneh" }, name = "Auto Fishing Guild", description = "Fishes everything at the fishing guild!", version = 2.0)
public class AIOFishingGuild extends ActiveScript implements PaintListener, MessageListener, MouseListener {
	MousePaint mt = new MousePaint();
	AntiBan ab = new AntiBan();

	private String pin = "";
	private String  fishType = "none";
	private long startTime;
	private final static Tile BANK_TILE = new Tile(2585, 3423, 0);
	private final static Tile FISH_TILE = new Tile(2599, 3421, 0);
	private final static int FISHING_SPOT_1 = 312;
	private final static int FISHING_SPOT_2 = 313;
	private final static int HARPOON_ID = 311;
	private final static int TUNA_ID = 359;
	private final static int SWORDFISH_ID = 371;
	private final static int LOBSTER_ID = 377;
	private final static int SHARK_ID = 383;
	private final static int SPIN_TICKET = 24154;
	private final static int SKILL_ID = Skills.FISHING;
	private int startLevel = 0;
	private int startExp = 0;
	private int expGained = 0;
	private int currLevel = 0;
	private int levelDiff = 0;
	@SuppressWarnings("unused")
	private int fishID = 0;
	private int fishPrice = 0;
	private int tunaCaught = 0;
	private int swordfishCaught = 0;
	private int lobsterCaught = 0;
	private int sharksCaught = 0;
	private int pin1 = 0;
	private int pin2 = 0;
	private int pin3 = 0;
	private int pin4 = 0;
	private int moneyGained = 0;
	private int antibanCount = 0;
	private boolean showTab1 = true;
	private boolean showTab2 = false;
	private boolean showTab3 = false;
	private boolean showTab4 = false;
	private boolean tunaBox = false;
	private boolean lobsterBox = false;
	private boolean swordfishBox = false;
	private boolean sharkBox = false;
	private boolean dropTunaBox = false;
	private boolean dropTuna = false;
	private final static Rectangle tab1show = new Rectangle(7, 345, 78, 17);
	private final static Rectangle tab2show = new Rectangle(92, 345, 78, 17);
	private final static Rectangle tab3show = new Rectangle(348, 345, 78, 17);
	private final static Rectangle tab4show = new Rectangle(433, 345, 78, 17);
	private final static Rectangle tunabox = new Rectangle(25, 401, 78, 18);
	private final static Rectangle lobsterbox = new Rectangle(154, 401, 78, 18);
	private final static Rectangle swordfishbox = new Rectangle(282, 401, 78, 18);
	private final static Rectangle sharkbox = new Rectangle(413, 401, 78, 18);
	private final static Rectangle droptunabox = new Rectangle(111, 445, 77, 16);
	private final static Rectangle pinbox = new Rectangle(317, 440, 76, 15);
	private final static Font font1 = new Font("Calibri", 0, 12);
	private final static Font font2 = new Font("Calibri", 0, 16);
	private Image tab1 = null;
	private Image tab2 = null;
	private Image tab3 = null;
	private Image selectbox = null;
	private Image emptybox = null;
	private Image skillicons = null;
	public static Player me = null;
	public enum State {FISH, BANK, WALK_TO_FISH, WALK_TO_BANK, LOBBY_LOGIN, SLEEP, BANK_PIN, DROP_TUNA, SPIN_TICKETS, OPEN_INV_TAB, RELOAD_CLIENT, ANTIBAN};

	/**
	 * Drops all tuna in the inventory when called
	 */
	public void dropTuna() {
		Inventory.dropAllFast(TUNA_ID);
	}

	/**
	 * Logic that tells the script what action to perform
	 */
	public State getStage() {
		if(!fishType.equals("none")) {
			if(Tabs.getCurrent() != Tabs.INVENTORY) {
				return State.OPEN_INV_TAB;
			}
			if(isBankPinOpen()) {
				return State.BANK_PIN;
			}
			if(atLobby()) {
				return State.LOBBY_LOGIN;
			}
			if(!Game.isLoggedIn()) {
				return State.RELOAD_CLIENT;
			}
			if(Misc.isIdle()) {
				if(Inventory.contains(SPIN_TICKET)) {
					return State.SPIN_TICKETS;
				}
				if(Misc.inventoryIsFull()) {
					if(fishType.equals("swordfish") && Inventory.contains(TUNA_ID) && dropTuna) {
						return State.DROP_TUNA;
					}
					if(Misc.distanceFromMe(BANK_TILE) > 3) {
						return State.WALK_TO_BANK;
					} else {
						return State.BANK;
					}
				} else {
					if(Misc.distanceFromMe(FISH_TILE) > 10) {
						return State.WALK_TO_FISH;
					}
					return State.FISH;
				}
			} else {
				if(me.getInteracting() != null && getSpot() != -1) {
					if(me.getInteracting().getId() != getSpot()) {
						return State.FISH;
					}
				}
				if(me.getInteracting() != null) {
					if(me.getInteracting().getName().contains("spot".toLowerCase())) {
						return State.ANTIBAN;
					}
				}
			}
		}
		return State.SLEEP;
	}


	/**
	 * Grabs messages from the chatbox
	 */
	@Override
	public void messageReceived(MessageEvent arg0) {
		String msg = arg0.getMessage();
		if(msg.equals("You catch a tuna.")) {
			tunaCaught++;
		}
		if(msg.equals("You catch a swordfish.")) {
			swordfishCaught++;
		}
		if(msg.equals("Your quick reactions allow you to catch two tuna.")) {
			tunaCaught +=2;
		}
		if(msg.equals("Your quick reactions allow you to catch two swordfish.")) {
			swordfishCaught +=2;
		}
		if(msg.equals("You catch a lobster.")) {
			lobsterCaught++;
		}
		if(msg.equals("You catch a shark.")) {
			sharksCaught++;
		}
		if(msg.equals("Your quick reactions allow you to catch two sharks.")) {
			sharksCaught +=2;
		}
	}

	/**
	 * Parses the string "Pin" into 4 integers
	 */
	public void parsePin() {
		if(pin.length() == 4) {
			pin1 = Character.getNumericValue(pin.charAt(0));
			pin2 = Character.getNumericValue(pin.charAt(1));
			pin3 = Character.getNumericValue(pin.charAt(2));
			pin4 = Character.getNumericValue(pin.charAt(3));
		}
		log.info("Using pin: " + pin1 + "" + pin2 + "" + pin3 + "" + pin4);
	}

	/**
	 * Returns true when bank interface is open
	 */
	public boolean isBankPinOpen() {
		return Widgets.get(13, 0).isOnScreen();
	}

	/**
	 * Returns true when lobby widget is visible
	 */
	public boolean atLobby() {
		return Widgets.get(906, 195).isOnScreen();
	}

	/**
	 * Gets the type of fish being caught
	 */
	public int getFishCaught() {
		String msg2 = fishType.toLowerCase();
		if(msg2.equals("lobsters")) {
			return lobsterCaught;
		} else if(msg2.equals("swordfish")) {
			return swordfishCaught;
		} else if(msg2.equals("shark")) {
			return sharksCaught;
		} else if(msg2.equals("tuna")) {
			return tunaCaught;
		}
		return 0;
	}

	/**
	 * Calculates time running
	 * @Param i
	 */
	public String runTime(long i) {
		long millis = System.currentTimeMillis() - i;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		return  hours + ":" + minutes + ":" + seconds;
	}

	/**
	 * Calculates time to level
	 * @param SkillID
	 */
	public String TTL(int skillID) {
		try {
			int millis = (int) (Skills.getExpToNextLevel(skillID) / (((expGained) * 3600000D / (System.currentTimeMillis() - startTime))) * 3600000);
			NumberFormat nf = NumberFormat.getInstance();
			long t_seconds = millis / 1000;
			long t_minutes = t_seconds / 60;
			long t_hours = t_minutes / 60;
			int seconds = (int) (t_seconds % 60);
			int minutes = (int) (t_minutes % 60);
			int hours = (int) (t_hours % 60);
			return "Next level in: " + nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(seconds);
		} catch(Exception a) {
			a.printStackTrace();
			return "Next level in: -1:-1:-1";
		}
	}

	/**
	 * Formats long numbers into an easier to read number
	 * @param Start
	 */
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

	/**
	 * Calculates a "per hour" rate based on the param (gained)
	 */
	public String perHour(int gained) {
		return formatNumber((int) ((gained) * 3600000D / (System.currentTimeMillis() - startTime)));
	}

	/**
	 * Draws information onto the gamescreen
	 */
	public void onRepaint(Graphics g1) {
		
		Graphics2D g = (Graphics2D)g1;
		
		/*
		 * private final static Rectangle tab1show = new Rectangle(7, 345, 78, 17);
	private final static Rectangle tab2show = new Rectangle(92, 345, 78, 17);
	private final static Rectangle tab3show = new Rectangle(348, 345, 78, 17);
	private final static Rectangle tab4show = new Rectangle(433, 345, 78, 17);
	private final static Rectangle tunabox = new Rectangle(25, 401, 78, 18);
	private final static Rectangle lobsterbox = new Rectangle(154, 401, 78, 18);
	private final static Rectangle swordfishbox = new Rectangle(282, 401, 78, 18);
	private final static Rectangle sharkbox = new Rectangle(413, 401, 78, 18);
	private final static Rectangle droptunabox = new Rectangle(111, 445, 77, 16);
	private final static Rectangle pinbox = new Rectangle(317, 440, 76, 15);
		 */
		
		g.draw(tab1show);
		g.draw(tab2show);
		g.draw(tab3show);
		g.draw(tab4show);

		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		currLevel = Skills.getCurrentLevel(Skills.FISHING);
		levelDiff = currLevel - startLevel;
		expGained = Skills.getCurrentExp(Skills.FISHING) - startExp;
		moneyGained = fishPrice * getFishCaught();
		int percent = (int) (Skills.getPercentToNextLevel(SKILL_ID) * 400) / 100; // where 325 is the length of the rectangle
		Color color4 = new Color(255, 0, 0, 196);
		Color color3 = new Color(0, 255, 0, 192);
		Color color8 = new Color(255, 255, 255, 75);
		BasicStroke stroke1 = new BasicStroke(1);
		if(showTab1) {
			g.drawImage(tab1, 0, 338, null);
			g.drawImage(skillicons, 0, 338, null);
			g.setColor(color4);
			g.fillRect(41, 446, 400, 20);
			g.setColor(color3);
			g.fillRect(41, 446, percent, 20);
			g.setColor(color8);
			g.fillRect(41, 446, 400, 10);
			g.setColor(Color.BLACK);
			g.setStroke(stroke1);
			g.drawRect(41, 446, 400, 20);
			g.setFont(font1);
			g.drawString("Fish /hr: " + perHour(getFishCaught()) + "(+" + formatNumber(getFishCaught()) + ")", 41, 399);
			g.drawString("Profit /hr: " + perHour(moneyGained) + "(+" + formatNumber(moneyGained) + ")", 41, 430);
			g.drawString("Level " + currLevel + "(+" + levelDiff + ")" + "  Exp /hr: " + perHour(expGained) + "(" + formatNumber(expGained) + ")   " + TTL(SKILL_ID) + "   Stage: " + getStage().toString() , 44, 462);
		}
		if(showTab2) {
			
			g.setColor(Color.CYAN);
			g.draw(tunabox);
			g.draw(lobsterbox);
			g.draw(swordfishbox);
			g.draw(sharkbox);
			g.draw(droptunabox);
			g.draw(pinbox);
			
			g.setColor(Color.BLACK);
			g.drawImage(tab2, 0, 338, null);
			if(swordfishBox) {
				g.drawImage(selectbox , 276, 395, null);
				g.setFont(font1);
				g.drawString("Drop Tuna?", 19, 461);
				if(dropTunaBox) {
					g.drawImage(emptybox, 103, 438, null);
					g.drawString("No", 138, 459);
				} else {
					g.drawImage(selectbox, 103, 438, null);
					g.drawString("Yes", 138, 459);
				}
			} else {
				g.drawImage(emptybox, 276, 395, null);
			}
			if(tunaBox) {
				g.drawImage(selectbox , 18, 395, null);
			} else {
				g.drawImage(emptybox, 18, 395, null);
			}
			if(lobsterBox){
				g.drawImage(selectbox , 146, 395, null);
			} else {
				g.drawImage(emptybox , 146, 395, null);
			}
			if(sharkBox) {
				g.drawImage(selectbox , 406, 395, null);
			} else {
				g.drawImage(emptybox , 406, 395, null);
			}
			g.setFont(font1);
			if(pin.equals("") || pin.equals("0000")) {
				g.drawImage(emptybox, 309, 438, null);
				g.drawString("(NONE)", 338, 457);
			} else {
				g.drawImage(selectbox, 309, 438, null);
				g.drawString("(" + pin + ")", 338, 457);
			}
			g.drawString("Pin: ", 275, 461);
			g.drawString("What fish would you like to catch?", 149, 389);
			g.setFont(font2);
			g.drawString("Tuna", 51, 415);
			g.drawString("Lobsters", 170, 415);
			g.drawString("Swordfish", 294, 415);
			g.drawString("Sharks", 434, 415);
		}
		if(showTab3) {
			g.setColor(Color.BLACK);
			g.drawImage(tab3, 0, 338, null);
			g.setFont(font2);
			g.drawString("Recent Updates:", 10, 389);
			g.setFont(font1);
			g.drawString("Updated Paint", 10, 403);
			g.drawString("Fixed a bug where stuff doesn't work right", 10, 416);
			g.drawString("Made everything else sexy", 10, 429);
			g.drawString("Something else here :D", 10, 442);
			g.drawString("v 1.7", 452, 469);
		}
		if(showTab4) {
			g.drawImage(selectbox , 426, 338, null);
		}
		g.setFont(font1);
		g.setColor(Color.BLACK);
		if(showTab4) {
			g.drawString("Show Paint", 442, 357);
		} else {
			g.drawString("Hide Paint", 442, 357);
			g.drawString("Information", 13, 358);
			g.drawString("Setup", 114, 358);
			g.drawString("Changelog", 358, 358);
			g.setFont(font2);
			g.drawString("Auto Fishing Guild", 198, 362);
			g.setFont(font1);
			if(showTab1) {
				g.drawString("Runtime: " + runTime(startTime), 212, 377);
			}
			if(!showTab3) {
				g.drawString("By Kenneh", 452, 469);
			}
		}
		mt.add(Mouse.getLocation());
		mt.drawCursor(g);
		mt.drawTrail(g);
	}

	/**
	 * Gets the action of the fishing spot depending on the type of fish you select
	 * @return action
	 */
	public String getAction() {
		String msg = fishType.toLowerCase();
		if(msg.contains("lobster")) {
			return "Cage";
		} else if(msg.contains("shark") || msg.contains("tuna") || msg.contains("swordfish")) {
			return "Harpoon";
		}
		return "";
	}

	/**
	 * Gets the ID of the fishing spot depending on the type of fish you select
	 * @return spotID
	 */
	public int getSpot() {
		String msg2 = fishType.toLowerCase();
		if(msg2.contains("lobster") || msg2.contains("swordfish") || msg2.contains("tuna")) {
			return FISHING_SPOT_1;
		} else if(msg2.contains("shark")) {
			return FISHING_SPOT_2;
		}
		return -1;
	}

	/**
	 * Calls all the methods to start the script
	 */
	public void onStart() {
		startExp = Skills.getCurrentExp(Skills.FISHING);
		startLevel = Skills.getCurrentLevel(Skills.FISHING);
		startTime = System.currentTimeMillis();
		me = Players.getLocal();
	}

	/**
	 * Saves and loads a selected image from a URL
	 * @param url Link to the picture
	 * @param name Name of the file
	 * @return The image
	 */
	public Image getImage(String url, String name) {
		try {
			File dir = new File(Environment.getStorageDirectory().getAbsolutePath() + System.getProperty("file.seperator") + name);
			if(!dir.exists()) {
				ImageIO.write(ImageIO.read(new URL(url)), url.substring(url.lastIndexOf('.')).substring(1), dir);
			}
			return ImageIO.read(dir);
		} catch(Exception a) {
			a.printStackTrace();
		}
		return null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = new Point(Mouse.getLocation());
		if(tab1show.contains(p)) {
			showTab4 = false;
			showTab3 = false;
			showTab2 = false;
			showTab1 = true;
		}
		if(tab2show.contains(p)) {
			showTab4 = false;
			showTab3 = false;
			showTab1 = false;
			showTab2 = true;
		}
		if(tab3show.contains(p)) {
			showTab4 = false;
			showTab1 = false;
			showTab2 = false;
			showTab3 = true;
		}
		if(tab4show.contains(p)) {
			if(showTab4) {
				showTab1 = true;
				showTab4 = false;
			} else {
				showTab1 = false;
				showTab2 = false;
				showTab3 = false;
				showTab4 = true;
			}
		}
		if(showTab2) {
			try {
				if(pinbox.contains(p)) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							pin = JOptionPane.showInputDialog(null, "Enter your bank pin here", "0000");
						}
					});
				}
			} catch(Exception a) {
				pin = "0000";
			}
			if(tunabox.contains(p)) {
				tunaBox = true;
				swordfishBox = false;
				lobsterBox = false;
				sharkBox = false;
				dropTunaBox = false;
				fishType = "tuna";
				fishID = TUNA_ID;
				fishPrice = GeItem.getGuidePrice(TUNA_ID);
				System.out.println("Updated settings! Fishing " + fishType);
			}
			if(swordfishbox.contains(p)) {
				swordfishBox = true;
				tunaBox = false;
				lobsterBox = false;
				sharkBox = false;
				dropTunaBox = true;
				fishType = "swordfish";
				fishID = SWORDFISH_ID;
				fishPrice = GeItem.getGuidePrice(SWORDFISH_ID);
				System.out.println("Updated Settings! Fishing " + fishType);
			}
			if(lobsterbox.contains(p)) {
				tunaBox = false;
				swordfishBox = false;
				lobsterBox = true;
				sharkBox = false;
				dropTunaBox = false;
				fishType = "lobsters";
				fishID = LOBSTER_ID;
				fishPrice = GeItem.getGuidePrice(LOBSTER_ID);
				System.out.println("Updated settings! Fishing " + fishType);
			}
			if(sharkbox.contains(p)) {
				tunaBox = false;
				swordfishBox = false;
				lobsterBox = false;
				sharkBox = true;
				dropTunaBox = false;
				fishID = SHARK_ID;
				fishType = "shark";
				fishPrice = GeItem.getGuidePrice(SHARK_ID);
				System.out.println("Updated settings! Fishing " + fishType);
			}
			if(droptunabox.contains(p) && swordfishBox) {
				if(dropTunaBox) {
					System.out.println("Updated settings! Not dropping tuna");
					dropTunaBox = false;
				} else {
					System.out.println("Updated settings! Dropping tuna");
					dropTunaBox = true;
				}
			}
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
	public void mouseReleased(MouseEvent arg0) {

	}

	public class MousePaint {
		/**
		 *  Length of the trail
		 */
		private final int SIZE = 50;
		/**
		 * Gets the color of the trail depending on the SIZE
		 */
		private final float rainbowStep = (float) (1.0/SIZE);
		/**
		 * Gets the alpha of the trail depending on the SIZE
		 */
		private final double alphaStep = (255.0/SIZE);

		/**
		 * Declares the mouse points
		 */
		private Point[] points;
		/**
		 * Counts up the points
		 */
		private int index;
		/**
		 * Trail offset
		 */
		private float offSet = 0.05f;
		/**
		 * Trail start
		 */
		private float start = 0;

		/**
		 * Construter for MousePaint()
		 */
		public MousePaint() {
			points = new Point[SIZE];
			index = 0;
		}

		/**
		 * Adds the current mouse location as a point to draw the trail
		 * @param p MouseLocation()
		 */
		public void add(Point p) {
			points[index++] = p;
			index %= SIZE;
		}

		/**
		 * Draws the cursor on the screen
		 * @param graphics
		 */
		public void drawCursor(Graphics graphics) {
			int x = (int)Mouse.getLocation().getX();
			int y = (int)Mouse.getLocation().getY();
			Graphics2D g2D = (Graphics2D) graphics;
			graphics.setColor(new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));
			Graphics2D spinner = (Graphics2D) g2D.create();
			spinner.rotate(System.currentTimeMillis() % 2000d / 2000d * (360d) * 2 * Math.PI / 180.0, x, y);
			spinner.drawLine(x - 6, y, x + 6, y);
			spinner.drawLine(x, y - 6, x, y +6);
		}

		/**
		 * Draws the trail on screen
		 * @param graphics
		 */
		public void drawTrail(Graphics graphics) {
			Graphics2D g2D = (Graphics2D) graphics;
			g2D.setStroke(new BasicStroke(1F));
			double alpha = 0;
			float rainbow = start;

			start += offSet;
			if (start > 1) {
				start -= 1;
			}

			for (int i = index; i != (index == 0 ? SIZE-1 : index-1); i = (i+1)%SIZE) {
				if (points[i] != null && points[(i+1)%SIZE] != null) {
					int rgb = Color.HSBtoRGB(rainbow, 0.9f, 0.9f);
					rainbow += rainbowStep;

					if (rainbow > 1) {
						rainbow -= 1;
					}
					g2D.setColor(new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, (int)alpha));
					g2D.drawLine(points[i].x, points[i].y, points[(i+1)%SIZE].x, points[(i+1)%SIZE].y);


					alpha += alphaStep;
				}
			}
		}
	}

	public static class Misc {

		/**
		 * Calculates the distance between the local player and the given tile
		 * @param tile
		 * @return (int) Distance from the param
		 */
		public static int distanceFromMe(Tile tile) {
			return (int) (Calculations.distance(me.getLocation(), tile));
		}

		/**
		 * Determines wether the inventory is full or not
		 * @return true if inventory is full
		 */
		public static boolean inventoryIsFull() {
			return Inventory.getCount() == 28;
		}

		/**
		 *  Creates a random integer between the given values
		 * @param x lowest
		 * @param y highest
		 */
		public static int random(int x, int y) {
			return Random.nextInt(x, y);
		}

		/**
		 * Checks if the character is idle
		 * @return if the character is idle, true else false
		 */
		public static boolean isIdle() {
			return me.getAnimation() == -1 && !me.isMoving();
		}

		/**
		 * Sleeps for a set amount of time between the two values
		 * @param x low
		 * @param y high
		 */
		public static void sleep(int x, int y) {
			Task.sleep(random(x,  y));
		}

		/**
		 * Filters the nearest npc with the given ID
		 * @param ids
		 * @return NPC
		 */
		public static NPC getNearestNPC(final int... ids) {
			return NPCs.getNearest(new Filter<NPC>() {
				@Override
				public boolean accept(NPC npc) {
					for (int id : ids) {
						if (npc.getId() == id) {
							return true;
						}
					}
					return false;
				}
			});
		}
	}


	public static class Skills extends org.powerbot.game.api.methods.tab.Skills {

		public static final int ATTACK = 0;
		public static final int DEFENSE = 1;
		public static final int STRENGTH = 2;
		public static final int CONSTITUTION = 3;
		public static final int RANGE = 4;
		public static final int PRAYER = 5;
		public static final int MAGIC = 6;
		public static final int COOKING = 7;
		public static final int WOODCUTTING = 8;
		public static final int FLETCHING = 9;
		public static final int FISHING = 10;
		public static final int FIREMAKING = 11;
		public static final int CRAFTING = 12;
		public static final int SMITHING = 13;
		public static final int MINING = 14;
		public static final int HERBLORE = 15;
		public static final int AGILITY = 16;
		public static final int THIEVING = 17;
		public static final int SLAYER = 18;
		public static final int FARMING = 19;
		public static final int RUNECRAFTING = 20;
		public static final int HUNTER = 21;
		public static final int CONSTRUCTION = 22;
		public static final int SUMMONING = 23;
		public static final int DUNGEONEERING = 24;

		public static final String[] SKILL_NAMES = {"attack", "defence",
			"strength", "constitution", "range", "prayer", "magic", "cooking",
			"woodcutting", "fletching", "fishing", "firemaking", "crafting",
			"smithing", "mining", "herblore", "agility", "thieving", "slayer",
			"farming", "runecrafting", "hunter", "construction", "summoning",
			"dungeoneering", "-unused-"};

		/**
		 * Returns your current experience in the given skill
		 * @param index
		 * @return
		 */
		public static int getCurrentExp(final int index) {
			if (index > SKILL_NAMES.length - 1) {
				return -1;
			}
			final int[] skills = Skills.getExperiences();

			if (index > skills.length - 1) {
				return -1;
			}
			return Skills.getExperiences()[index];
		}

		/**
		 * Returns your current level
		 * @param index SkillID
		 * @return
		 */
		public static int getCurrentLevel(final int index) {
			if (index > SKILL_NAMES.length - 1) {
				return -1;
			}
			return Skills.getBottomLevels()[index];
		}

		/**
		 * Returns the percent to your next level
		 * @param index
		 * @return
		 */
		public static int getPercentToNextLevel(final int index) {
			if (index > SKILL_NAMES.length - 1) {
				return -1;
			}
			final int lvl = getRealLevel(index);
			return getPercentToLevel(index, lvl + 1);
		}

		/**
		 * Returns the percent to a given level
		 * @param index
		 * @param endLvl
		 * @return
		 */
		public static int getPercentToLevel(final int index, final int endLvl) {
			if (index > SKILL_NAMES.length - 1) {
				return -1;
			}
			final int lvl = getRealLevel(index);
			if (index == DUNGEONEERING && (lvl == 120 || endLvl > 120)) {
				return 0;
			} else if (lvl == 99 || endLvl > 99) {
				return 0;
			}
			final int xpTotal = XP_TABLE[endLvl] - XP_TABLE[lvl];
			if (xpTotal == 0) {
				return 0;
			}
			final int xpDone = getCurrentExp(index) - XP_TABLE[lvl];
			return 100 * xpDone / xpTotal;
		}

		/**
		 * Returns the exp needed to your next level
		 * @param index
		 * @return
		 */
		public static int getExpToNextLevel(final int index) {
			if (index > SKILL_NAMES.length - 1) {
				return -1;
			}
			final int lvl = getRealLevel(index);
			return getExpToLevel(index, lvl + 1);
		}

		/**
		 * Returns the experience to the provided level
		 * @param index
		 * @param endLvl
		 * @return
		 */
		public static int getExpToLevel(final int index, final int endLvl) {
			if (index > SKILL_NAMES.length - 1) {
				return -1;
			}
			final int lvl = getRealLevel(index);
			if (index == DUNGEONEERING && (lvl == 120 || endLvl > 120)) {
				return 0;
			} else if (lvl == 99 || endLvl > 99) {
				return 0;
			}
			return XP_TABLE[endLvl] - getCurrentExp(index);
		}

	}

	public static class Bank {

		private static final int[] BANKERS = {44, 45, 494, 495, 496, 497,
			498, 499, 553, 909, 958, 1036, 2271, 2354, 2355, 2718, 2759, 3198,
			3293, 3416, 3418, 3824, 4456, 4457, 4458, 4459, 5488, 5901, 5912,
			6362, 6532, 6533, 6534, 6535, 7605, 8948, 9710, 14367};
		private static final int[] BANK_BOOTHS = {782, 2213, 2995, 5276,
			6084, 10517, 11402, 11758, 12759, 14367, 19230, 20325, 24914, 11338,
			25808, 26972, 29085, 52589, 34752, 35647, 36786, 2012, 2015, 2019,
			42217, 42377, 42378};
		private static final int[] BANK_CHESTS = {2693, 4483, 8981, 12308, 21301, 20607,
			21301, 27663, 42192};
		private static final int[] DEPOSIT_BOXES = {2045, 9398, 20228, 24995, 25937,
			26969, 32924, 32930, 32931, 34755, 36788, 39830, 45079};

		private static final int WIDGET_BANK = 762;
		private static final int WIDGET_BANK_BUTTON_CLOSE = 45;
		private static final int WIDGET_DEPOSIT_BOX = 11;
		private static final int WIDGET_DEPOSIT_BOX_BUTTON_CLOSE = 15;
		private static final int WIDGET_DEPOSIT_BOX_INVENTORY = 11;
		private static final int WIDGET_DEPOSIT_BUTTON_DEPOSIT_CARRIED_ITEMS = 19;
		private static final int WIDGET_BANK_BUTTON_DEPOSIT_CARRIED_ITEMS = 34;

		/**
		 * Gets the nearest bankable entity
		 * @return
		 */
		public static Entity findBank() {

			final SceneObject BANK_BOOTH = SceneEntities.getNearest(BANK_BOOTHS);
			final SceneObject BANK_CHEST = SceneEntities.getNearest(BANK_CHESTS);
			final SceneObject DEPOSIT_BOX = SceneEntities.getNearest(DEPOSIT_BOXES);
			final NPC BANKER = NPCs.getNearest(new Filter<NPC>() {
				public boolean accept(NPC npc) {
					for (final int BANKER : BANKERS) {
						if (npc.getId() == BANKER) {
							return true;
						}
					}
					return false;
				}
			});
			Entity toBankWith = null;
			if (BANK_BOOTH != null) {
				if (BANKER != null) {
					if (Random.nextInt(1, 11) < 7) {
						toBankWith = BANK_BOOTH;
					} else {
						toBankWith = BANKER;
					}
				} else {
					toBankWith = BANK_BOOTH;
				}
			} else if (BANKER != null) {
				toBankWith = BANKER;
			} else if (BANK_CHEST != null) {
				toBankWith = BANK_CHEST;
			} else if (DEPOSIT_BOX != null) {
				toBankWith = DEPOSIT_BOX;
			}
			return toBankWith;
		}

		/**
		 * Returns true if the bank interface is open
		 * @return
		 */
		public static boolean isOpen() {
			return Widgets.get(WIDGET_BANK).validate();
		}

		/**
		 * Returns true if the deposit interface is open
		 * @return
		 */
		public static boolean isDepositOpen() {
			return Widgets.get(WIDGET_DEPOSIT_BOX).validate();
		}

		/**
		 * If the bank is open, close it
		 * @return
		 */
		public static boolean close() {
			if (Widgets.get(WIDGET_BANK, WIDGET_BANK_BUTTON_CLOSE).interact("Close") ||
					Widgets.get(WIDGET_DEPOSIT_BOX, WIDGET_DEPOSIT_BOX_BUTTON_CLOSE).interact("Close")) {
				for (int i = 0; i < 10 && !(isDepositOpen() || isOpen()); i++) {
					Task.sleep(150);
				}
			}
			return !isOpen() && !isDepositOpen();
		}

		/**
		 * Gets the amount of items in your inventory
		 * @param countStacked
		 * @return
		 */
		private static int getInventoryCount(final boolean countStacked) {
			int count = 0;
			for (Item item : Inventory.getItems()) {
				if (countStacked) {
					count += item.getStackSize();
				} else {
					count++;
				}
			}
			return count;
		}

		/**
		 * Gets the amount of items in your inventory
		 * @param countStacked
		 * @param ids
		 * @return
		 */
		private static int getInventoryCount(final boolean countStacked, final int... ids) {
			int count = 0;
			for (final int id : ids) {
				count += Inventory.getCount(countStacked, id);
			}
			return count;
		}

		public static int getBoxCount(final boolean countStacks) {
			int count = 0;
			if (Widgets.get(WIDGET_DEPOSIT_BOX, WIDGET_DEPOSIT_BOX_INVENTORY).validate()) {
				for (int i = 0; i < 28; i++) {
					if (Widgets.get(WIDGET_DEPOSIT_BOX, WIDGET_DEPOSIT_BOX_INVENTORY).getChildren()[i].getChildId() != -1) {
						if (!countStacks) {
							count++;
						} else {
							count += Widgets.get(WIDGET_DEPOSIT_BOX, WIDGET_DEPOSIT_BOX_INVENTORY).getChildren()[i].getChildStackSize();
						}
					}
				}
			}
			return count;
		}

		public static int getBoxCount(final boolean countStacks, final int... ids) {
			int count = 0;
			if (Widgets.get(WIDGET_DEPOSIT_BOX, WIDGET_DEPOSIT_BOX_INVENTORY).validate()) {
				for (int i = 0; i < 28; ++i) {
					for (final int id : ids) {
						if (Widgets.get(WIDGET_DEPOSIT_BOX, WIDGET_DEPOSIT_BOX_INVENTORY).getChildren()[i].getChildId() == id) {
							if (!countStacks) {
								count++;
							} else {
								count += Widgets.get(WIDGET_DEPOSIT_BOX, WIDGET_DEPOSIT_BOX_INVENTORY)
										.getChildren()[i].getChildStackSize();
							}
						}
					}
				}
			}
			return count;
		}

		private static Item[] getBoxItems() {
			List<Item> items = new ArrayList<Item>();
			if (Widgets.get(WIDGET_DEPOSIT_BOX, WIDGET_DEPOSIT_BOX_INVENTORY).validate()) {
				for (int i = 0; i < 28; i++) {
					if (Widgets.get(WIDGET_DEPOSIT_BOX, WIDGET_DEPOSIT_BOX_INVENTORY).getChildren()[i].getChildId() != -1) {
						items.add(new Item(Widgets.get(WIDGET_DEPOSIT_BOX, WIDGET_DEPOSIT_BOX_INVENTORY)));
					}
				}
			}
			return items.toArray(new Item[items.size()]);
		}

		/**
		 * Deposits everything in your inventory into the bank
		 * @return
		 */
		public static boolean depositAll() {
			return Widgets.get(WIDGET_BANK, WIDGET_BANK_BUTTON_DEPOSIT_CARRIED_ITEMS).click(true)
					|| Widgets.get(WIDGET_DEPOSIT_BOX, WIDGET_DEPOSIT_BUTTON_DEPOSIT_CARRIED_ITEMS).interact("Deposit");
		}

		/**
		 * Gets the location of the item in your inventory
		 * @param id
		 * @return
		 */
		public static Item getBoxItem(final int id) {
			if (Widgets.get(WIDGET_DEPOSIT_BOX, WIDGET_DEPOSIT_BOX_INVENTORY).validate()) {
				for (int i = 0; i < 28; i++) {
					if (Widgets.get(WIDGET_DEPOSIT_BOX, WIDGET_DEPOSIT_BOX_INVENTORY).getChildren()[i].getChildId() == id) {
						return new Item(Widgets.get(WIDGET_DEPOSIT_BOX, WIDGET_DEPOSIT_BOX_INVENTORY).getChildren()[i]);
					}
				}
			}
			return null;
		}

		/**
		 * Gets the item from your inventory by the ID
		 * @param id
		 * @return
		 */
		private static Item getInventoryItem(final int id) {
			for (Item item : Inventory.getItems()) {
				if (item.getId() == id) {
					return new Item(item.getWidgetChild());
				}
			}
			return null;
		}

		/**
		 * Deposits the given item and amount into your bank
		 * @param itemID
		 * @param amount
		 * @return
		 */
		public static boolean deposit(final int itemID, final int amount) {
			if (isOpen() || isDepositOpen()) {
				int count;
				WidgetChild item;
				if (amount < 0) {
					throw new IllegalArgumentException("number < 0 (" + amount + ")");
				}
				if (!isOpen()) {
					count = getBoxCount(true, itemID);
					item = getBoxItem(itemID).getWidgetChild();
				} else {
					count = Inventory.getCount(true, itemID);
					item = getInventoryItem(itemID).getWidgetChild();
				}
				if (item == null) {
					return true;
				}
				switch (amount) {
				case 0:
					item.interact(count > 1 ? "Deposit-All" : "Deposit");
					break;
				case 1:
					item.interact("Deposit");
					break;
				case 5:
					item.interact("Deposit-" + amount);
					break;
				default:
					if (!item.interact("Deposit-" + amount)) {
						if (item.interact("Deposit-X")) {
							Task.sleep(Random.nextInt(1000, 1300));
							Keyboard.sendText(String.valueOf(amount), true);
						}
					}
					break;
				}
				int cInvCount = isOpen() ? Inventory.getCount(true, itemID) : getBoxCount(true, itemID);
				for (int i = 0; i < 100; i++) {
					cInvCount = isOpen() ? Inventory.getCount(true, itemID) : getBoxCount(true, itemID);
					if (cInvCount < count || cInvCount == 0) {
						break;
					}
					Task.sleep(Random.nextInt(10, 15));
				}
				Task.sleep(Random.nextInt(100, 125));
				return cInvCount < count || cInvCount == 0;
			}
			return false;
		}

		/**
		 * Deposits all except the given items into your bank
		 * @param itemIDs
		 * @return
		 */
		public static boolean depositAllExcept(final int... itemIDs) {
			if (isOpen() || isDepositOpen()) {
				if (isOpen() ? getInventoryCount(true, itemIDs) == 0 : getBoxCount(true, itemIDs) == 0) {
					return depositAll();
				}
				boolean found = false;
				Item[] items = isOpen() ? Inventory.getItems() : getBoxItems();
				for (Item item : items) {
					if (item != null && item.getId() != -1) {
						for (final int itemID : itemIDs) {
							if (item.getId() == itemID) {
								found = true;
							}
						}
						if (!found) {
							for (int j = 0; j < 5; j++) {
								if (deposit(item.getId(), 0)) {
									j = 5;
								}
							}
						}
						found = false;
					}
				}
				return isOpen() ? getInventoryCount(true) - getInventoryCount(true, itemIDs) == 0
						: getBoxCount(true) - getBoxCount(true, itemIDs) == 0;
			}
			return false;
		}

	}

	public static class Inventory extends org.powerbot.game.api.methods.tab.Inventory {

		/**
		 * Drops all of the given item in your inventory
		 * @param itemID
		 */
		public static void dropAll(int itemID) {
			Item[] items = Inventory.getItems();
			for(Item i : items) {
				if(i != null) {
					if(i.getId() == itemID) {
						i.getWidgetChild().interact("Drop");
						Task.sleep(Random.nextInt(75, 150));
					}
				}
			}
		}

		/**
		 * Drops all of the given item in your inventory using mousekeys
		 * @param itemID
		 */
		public static void dropAllFast(int itemID) {
			int[] path =   { 0, 4, 8, 12, 16, 20, 24, 25, 21, 17, 13, 9, 5, 1, 2, 6, 10, 14, 18, 22, 26, 27, 23, 19, 15, 11, 7, 3 };
			for (int slot = 0; slot < path.length; slot++) {
				Item item = Inventory.getItemAt(path[slot]);
				if(item.getId() == itemID) {
					Mouse.hop(item.getWidgetChild().getAbsoluteX(), item.getWidgetChild().getAbsoluteY());
					if(!Menu.isOpen()) {
						item.getWidgetChild().click(false);
					}
					if(Menu.contains("Drop")) {
						Menu.select("Drop");
					}
				}
			}
		}

		/**
		 * Gets the widgetchild of a given item ID
		 * @param itemID
		 * @return item widgetChild
		 */
		public static WidgetChild getItem(int itemID) {
			Item[] items = Inventory.getItems();
			for(Item i : items) {
				if(i != null) {
					if(i.getId() == itemID) {
						return i.getWidgetChild();
					}
				}
			}
			return null;
		}

		/**
		 * Returns true if the inventory contains the itemID
		 * @param itemID
		 * @return
		 */
		public static boolean contains(int itemID) {
			Item[] items = Inventory.getItems();
			for(Item i : items) {
				if(i.getId() == itemID) {
					return true;
				}
			}
			return false;
		}


	}

	public static class GeItem {

		/**
		 * Find item price on the grand exchange
		 * @param itemName The name of the item
		 * @return Price of the item
		 */
		public static int getGuidePrice(final String itemName) {
			return Integer.parseInt(lookup(itemName)[1]);
		}

		/**
		 * Find item price on the grand exchange
		 * @param itemID The id of the item.
		 * @return Price of the item
		 */
		public static int getGuidePrice(final int itemID) {
			return Integer.parseInt(lookup(itemID)[1]);
		}

		/**
		 * Get the ID of itemname (used for injecton bots)
		 * @param itemName: name of the item
		 * @return : item id as an integer
		 */
		public static int getID(final String itemName) {
			return Integer.parseInt(lookup(itemName)[2]);
		}

		/**
		 * Gets the itemname given the itemID
		 * @param itemID : id of the item
		 * @return name of the item
		 */
		public static String getName(final int itemID) {
			return lookup(itemID)[0];
		}

		/**
		 * Looks up grand exchange information and returns a string array with the following contents
		 * String[0] = item name
		 * String[1] = item price
		 * String[2] = item id
		 * @param itemName: name of the item to grab information about on the grandexchange website
		 * @return : a string array of grand exchange information on the item id provided
		 */
		public static String[] lookup(final String itemName) {
			try {
				final URL url = new URL("http://services.runescape.com/m=itemdb_rs/results.ws?query=" + itemName);
				final BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
				String input;
				while ((input = br.readLine()) != null) {
					if(input.toLowerCase().contains("alt=\"" + itemName.toLowerCase().trim() + "\"")) {
						return lookup(Integer.parseInt(input.substring(input.indexOf("id=") + 3, input.lastIndexOf("\" alt"))));
					}
				}
			} catch (final Exception ignored) { }
			return null;
		}

		/**
		 * Looks up grand exchange information and returns a string array with the following contents
		 * String[0] = item name
		 * String[1] = item price
		 * String[2] = item id
		 * @param itemID for the item being looked up on the grand exchange
		 * @return : a string array of grand exchange information on the item id provided
		 */
		public static String[] lookup(final int itemID) {
			try {
				String[] info = {"0", "0", "0", "0"};
				final URL url = new URL("http://services.runescape.com/m=itemdb_rs/viewitem.ws?obj=" + itemID);
				final BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
				String input;
				while ((input = br.readLine()) != null) {
					if(input.startsWith("<meta name=\"keywords")) {
						info[0] = input.substring(input.lastIndexOf(",") + 1, input.lastIndexOf("\"")).trim();
						if(info[0].equals("java")) return null;
					}
					if(input.contains("Current guide price:")) {
						input = br.readLine();
						info[1] = formatter(input.substring(4, input.lastIndexOf('<')));
						info[2] = ("" + itemID);
						return info;
					}
				}
			} catch (final Exception ignored) {}
			return null;
		}

		/**
		 * Formats a string removing all abbreviations and commas out of it and returning just the raw integer value as a string
		 * @param num : string number to be formatted. Used for numbers like 3.2m or 20.6k
		 * @return : a complete number without any abbreviations in it.
		 */
		public static String formatter(String num) {
			try {
				return num.replaceAll("\\.","").replaceAll("m", "00000").replaceAll("k", "00").replaceAll(",", "");
			} catch (Exception e) {}
			return "0";
		}

	}

	public class AntiBan {
		/**
		 * Starts the antiban sequence
		 */
		public void start() {
			int rnd = Random.nextInt(0, 500);
			switch(rnd) {
			case 1:
				log.info("stats antiban");
				Tabs.STATS.open();
				WidgetChild fishWidget = Widgets.get(320).getChild(29);
				fishWidget.hover();
				antibanCount ++;
				Misc.sleep(1500, 2000);
				log.info("Antiban count: " + antibanCount);
				break;
			case 7:
				log.info("examining a random npc");
				NPC[] object = NPCs.getLoaded();
				if(object != null) {
					NPC i2 = NPCs.getNearest(object[0].getId());
					if(object[0].isOnScreen() && object[0] != null) {
						i2.interact("Examine");
					}
				}
				antibanCount ++;
				log.info("Antiban count: " + antibanCount);
				break;
			case 52:
				log.info("opening friends list");
				Tabs.FRIENDS.open();
				antibanCount ++;
				log.info("Antiban count: " + antibanCount);
				break;
			case 79: case 80: case 81: case 23: case 53: case 94:
				log.info("random camera angle");
				Camera.setAngle(Random.nextInt(20, 300));
				antibanCount ++;
				log.info("Antiban count: " + antibanCount);
				break;
			case 10: case 22: case 33: case 44: case 56: case 99:
				log.info("random camera pitch");
				Camera.setPitch(Random.nextInt(0, 90));
				antibanCount ++;
				log.info("Antiban count: " + antibanCount);
				break;
			}
		}
	}


	@Override
	public int loop() {
		switch(getStage()) {
		case LOBBY_LOGIN:
			log.info("Logging in from lobby");
			Widgets.get(906, 195).click(true);
			Misc.sleep(5000, 7500);
			break;
		case OPEN_INV_TAB:
			Tabs.INVENTORY.open();
			break;
		case DROP_TUNA:
			dropTuna();
			break;
		case SPIN_TICKETS:
			WidgetChild i = Inventory.getItem(SPIN_TICKET);
			if(i != null) {
				i.click(true);
			}
			break;
		case BANK_PIN:
			parsePin();
			log.info("RUNNING BANK PIN SCRIPT!");
			String msg = Widgets.get(13, 28).getText();
			if(msg.contains("FIRST")) {
				Widgets.get(13, pin1 + 6).click(true);
				Misc.sleep(750, 1200);
			}
			if(msg.contains("SECOND")) {
				Widgets.get(13,  pin2 + 6).click(true);
				Misc.sleep(750, 1200);
			}
			if(msg.contains("THIRD")) {
				Widgets.get(13, pin3 + 6).click(true);
				Misc.sleep(750, 1200);
			}
			if(msg.contains("FOURTH")) {
				Widgets.get(13, pin4 + 6).click(true);
				Misc.sleep(750, 1200);
			}
			break;
		case WALK_TO_BANK:
			log.info("WALKING TO BANK");
			//Walking.walk(BANK_TILE);
			BANK_TILE.clickOnMap();
			break;
		case WALK_TO_FISH:
			log.info("WALKING TO FISH");
			FISH_TILE.clickOnMap();
			break;
		case BANK:
			log.info("BANKING");
			NPC bank2 = (NPC)Bank.findBank();
			Camera.turnTo(bank2);
			if(bank2 != null) {
				if(Bank.isOpen()) {
					Bank.depositAllExcept(HARPOON_ID);
					Bank.close();
				} else {
					bank2.interact("Bank");
					Misc.sleep(1500, 2000);
				}
			}
			break;
		case FISH:
			NPC spot = Misc.getNearestNPC(getSpot());
			if(!spot.isOnScreen()) {
				Camera.turnTo(spot);
			}
			if(Misc.distanceFromMe(spot.getLocation()) > 5) {
				spot.getLocation().clickOnMap();
			}
			spot.interact(getAction());
			Misc.sleep(1500, 2000);
			if(Players.getLocal().getAnimation() == 9980) {
				Misc.sleep(4000, 4500);
			}
			Misc.sleep(1992, 2353);
			break;
		case ANTIBAN:
			ab.start();
			break;
		case SLEEP:
			Misc.sleep(1000, 1500);
			break;
		default:
			break;
		}
		return 50;
	}

}
