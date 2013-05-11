package org.kenneh.scripts.grotworms;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.kenneh.core.api.framework.KNode;
import org.kenneh.core.api.framework.KScript;
import org.kenneh.core.api.utils.AbilityHandler;
import org.kenneh.core.api.utils.Misc;
import org.kenneh.core.graphics.MouseTrail;
import org.kenneh.core.graphics.PaintUtils;
import org.powerbot.core.event.events.MessageEvent;
import org.powerbot.core.event.listeners.MessageListener;
import org.powerbot.core.script.Script;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.input.Mouse.Speed;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.SkillData;
import org.powerbot.game.api.util.Timer;

import sk.action.ActionBar;

@Manifest(authors = { "Kenneh" }, description = "Requires Sharks, Falador teleports, Fire runes and Nature runes. Start in fally bank.", name = "Kenneh's Grotworms", version = 1.2)
public class GrotWorms extends KScript implements Script, MouseMotionListener, MouseListener, MessageListener {

	private final SkillData sd = new SkillData();
	private final Timer timer = new Timer(0);
	private final Color blackT = new Color(0, 0, 0, 150);
	private final Color goldT = new Color(255, 215, 0, 150);
	private final Color gold = new Color(255,215,0);
	private final Color whiteT = new Color(255, 255, 255, 125);
	private final Font font = new Font("Calibri", Font.PLAIN, 13);
	private final Rectangle nameText = new Rectangle(5, 86, 236, 17);
	private final Rectangle nameTextGlow = new Rectangle(5, 86, 236, 8);
	private final MouseTrail mouseTrail = new MouseTrail(gold);
	private long startTime = 0;
	private Point mouse;

	@Override
	public boolean init() {
		Mouse.setSpeed(Speed.VERY_FAST);
		Settings.setLoot(Settings.RARE_DROP_TABLE);
		Settings.setLoot(Settings.GROTWORM_LOOT);
		Settings.pw.storePrice(Settings.RARE_DROP_TABLE);
		Settings.pw.storePrice(Settings.GROTWORM_LOOT);
		Settings.pw.storePrice(995, 1);
		Settings.pw.storePrice(385, 0);
		Settings.pw.storePrice(450, Settings.pw.getPrice(449));
		Settings.pw.storePrice(1180, Settings.pw.getPrice(1179));
		Settings.setBar(ActionBar.getCurrentBar());
		getContainer().submit(new AbilityHandler());
		final KNode[] nodes = {
				new Alching(), new Failsafe(),  new FightWorms(), new Eating(), new GoToBank(),
				new LootItems(), new WalkToGrots(), new BankItems(), new AttackOneOf(), new Expandbar(),
				new HandlePin()
		};
		submit(nodes);
		startTime = System.currentTimeMillis();
		return true;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	private static int lastCount = 0;
	private static int currCount = 0;

	public static int amount = 0;

	@Override
	public void paint(final Graphics g) {
		currCount = Skills.getExperience(Skills.CONSTITUTION);

		if(lastCount != currCount)
			amount++;

		final Graphics2D g1 = (Graphics2D) g;
		g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g1.setColor(blackT);
		g1.setFont(font);

		int x = 5, y = 80;
		final int fontH = 17;
		final int width = 236;
		final int height = fontH * 4;

		g1.fillRoundRect(x, y, width, height, 10, 10);
		g1.setColor(gold);
		g1.drawRoundRect(x, y, width, height, 10, 10);

		g1.draw(nameText);
		g1.setColor(whiteT);
		g1.fill(nameTextGlow);
		g1.setColor(Color.WHITE);
		final int tX = 10;
		int tY = 98;
		g1.drawString("Kenneh's Grotworms - Runtime: " + timer.toElapsedString(), tX, tY);
		tY += 19;
		g1.drawString("Status: " + Settings.getStatus(), tX, tY);
		tY += 12;
		g1.drawString("Total looted value - "  + Misc.formatNumber(Settings.getLootValue()) + "(+" + Misc.perHour(startTime, Settings.getLootValue()) + ")", tX, tY);
		tY += 12;
		g1.drawString("Monsters killed - " + amount+ "(+" + Misc.perHour(startTime, amount) + ")", tX, tY);
		g1.setColor(Color.WHITE);

		g.setFont(new Font("Calibri", Font.PLAIN, 14));
		x = 8; 
		y = 396;
		for(int i = 0; i < PaintUtils.SKILL_NAMES.length -1; i++) {
			if(sd.experience(i) > 0) {
				PaintUtils.drawProgressBar(g1, x, y, 487, 17, Color.BLACK, PaintUtils.getSkillColor(i), 150, PaintUtils.getPercentToNextLevel(i));
				g.setColor(Color.WHITE);
				g.drawString(PaintUtils.generateString(sd, i), x + 5, y + 13);
				y += 18;
			}
		}

		drawAuthorBox(g1);
		drawMouse(g1);
		drawArea(g1);
		
		lastCount = currCount;
	}

	public  void drawArea(Graphics g2d) {
		try {
			g2d.setColor(goldT);
			Point p = Calculations.worldToMap(Settings.GROT_CENTER_TILE.getX(), Settings.GROT_CENTER_TILE.getY());
			g2d.fillOval(p.x - (Settings.getRadius() * 5), p.y - (Settings.getRadius() * 5), 5 * (Settings.getRadius() * 2), 5 * (Settings.getRadius() * 2));
			g2d.setColor(gold);
			g2d.drawOval(p.x - (Settings.getRadius() * 5), p.y - (Settings.getRadius() * 5), 5 * (Settings.getRadius() * 2), 5 * (Settings.getRadius() * 2));
		} catch (Exception a) {
		}
	}

	public void drawAuthorBox(final Graphics2D g) {
		if(nameText.contains(mouse)) {
			g.setFont(font);
			final Rectangle box = new Rectangle(mouse.x,  mouse.y - 17, 90, 17);
			g.setColor(Color.BLACK);
			g.fill(box);
			g.setColor(gold);
			g.draw(box);
			g.setColor(Color.WHITE);
			g.drawString("Author: Kenneh", mouse.x + 4, mouse.y - 4);
		}
	}

	public void drawMouse(final Graphics2D g) {
		mouseTrail.add(Mouse.getLocation());
		mouseTrail.draw(g);
		g.setColor(gold);
		g.drawOval(Mouse.getX() - 5, Mouse.getY() - 5, 11, 11);
		g.fillOval(Mouse.getX() - 2, Mouse.getY() - 2, 5, 5);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		mouse = arg0.getPoint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		mouse = arg0.getPoint();
	}

	@Override
	public void messageReceived(MessageEvent arg0) {
		final String line = arg0.getMessage().toLowerCase();
		if(line.contains("cya nerds")) {
			Keyboard.sendText("Bye!", true);
			Game.logout(true);
			stop();
		}

	}

}
