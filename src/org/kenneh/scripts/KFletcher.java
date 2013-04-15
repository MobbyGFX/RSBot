package org.kenneh.scripts;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.util.Timer;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.util.SkillData;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.WidgetChild;


@Manifest(authors = { "Kenneh" }, description = "An AIO Fletching Script", name = "KFletcher", hidden = true)
public class KFletcher extends ActiveScript implements PaintListener {

	public enum Log {
		MAGIC(1513);

		private int logId;

		Log(int logId) {
			this.logId = logId;
		}

		public int getLogId() {
			return logId;
		}

	}
	
	public enum Type {
		SHORTBOW("shortbow", new Point(43, 152));
		
		private String text;
		private Point loc;
		
		Type(String text, Point loc) {
			this.text = text;
			this.loc = loc;
		}
		
		public String getWidgetText() {
			return text;
		}
		
		public Point getWidgetLocation() {
			return loc;
		}
		
	}
	
	private final Timer timer = new Timer(0);
	private SkillData sd = new SkillData();
	private final int SKILLID = Skills.FLETCHING;
	private Log myLog;
	private Type myType;
	private boolean running = false;
	private String status = "Initializing";

	public void onStart() {
		myLog = Log.MAGIC;
		myType = Type.SHORTBOW;
		running = true;
	}

	@Override
	public int loop() {
		if(running) {
			if(!Inventory.contains(myLog.getLogId())) {
				if(!Bank.isOpen()) {
					status = "Opening bank";
					Bank.open();
				} else {
					if(!Inventory.contains(myLog.getLogId())) {
						if(Inventory.getCount() != 0) {
							status = "Depositing inventory";
							Bank.depositInventory();
						} else {
							if(Bank.getItem(myLog.getLogId()) == null) {
								log.warning("Out of logs!");
								stop();
							} else {
								status = "Withdrawing logs";
								Bank.withdraw(myLog.getLogId(), 0);
								if(Inventory.contains(myLog.getLogId())) {
									status = "Closing bank";
									Bank.close();
								}
							}
						}
					}
				}
			} else {
				if(Players.getLocal().getAnimation() == -1) {
					final Timer timer = new Timer(1000);
					while(timer.isRunning()) {
						status = "Ensuring that fletching has stopped";
						Task.sleep(20);
					}
					if(Players.getLocal().getAnimation() == -1 && Inventory.contains(myLog.getLogId())) {
						final Item log = Inventory.getItem(myLog.getLogId());
						if(log != null) {
							status = "Crafting log";
							log.getWidgetChild().interact("Craft");
						}
					}
				}
				final WidgetChild fletchInterface = Widgets.get(1370, 56);
				if(fletchInterface != null && fletchInterface.getAbsoluteLocation().x != -1) {
					if(fletchInterface.getText().toLowerCase().contains(myType.getWidgetText())) {
						final WidgetChild fletchButton = Widgets.get(1370, 35).getChild(0);
						if(fletchButton != null) {
							status = "Clicking fletch button";
							fletchButton.click(true);
						}
					} else {
						status = "Selecting correct log type";
						Mouse.click(myType.getWidgetLocation(), true);
					}
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
		sb.append("Fletching: ");
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
		g.drawString("KFletcher - Runtime: " + timer.toElapsedString() + " - Status: " + status, x, y - 22);
		drawProgressBar(g, x, y, 487, 17, Color.BLACK, Color.ORANGE, 150, getPercentToNextLevel(SKILLID));
		g.setColor(Color.WHITE);
		g.drawString(generateString(SKILLID), x + 5, y + 13);
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
