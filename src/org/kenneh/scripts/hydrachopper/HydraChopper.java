package org.kenneh.scripts.hydrachopper;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.kenneh.core.api.Misc;
import org.powerbot.core.event.events.MessageEvent;
import org.powerbot.core.event.listeners.MessageListener;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.util.Timer;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.SkillData;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.net.GeItem;
import org.powerbot.game.api.wrappers.node.SceneObject;

@Manifest(authors = { "Kenneh" }, description = "Uses a spirit hydra to regrow trees to chop in falador park", name = "HydraChopper")
public class HydraChopper extends ActiveScript implements PaintListener, MessageListener {

	private long startTime;
	private final Set<Node> container = new HashSet<Node>();
	private final Timer timer = new Timer(0);
	private final SkillData skillData = new SkillData();

	private Iterator<Node> task = null;
	private volatile boolean running = false;

	public void onStart() {
		final HCGui hcgui = new HCGui();
		while(hcgui.getFrame() != null && hcgui.getFrame().isVisible()) {
			Task.sleep(20);
		}
		Constants.myTree = hcgui.getMySelection();
		Constants.logPrice = GeItem.lookup(Constants.myTree.getLogId()).getPrice();
		startTime = System.currentTimeMillis();
		container.add(new ResummonHydra());
		container.add(new ChopTree());
		container.add(new RegrowTree());
		container.add(new WalkToTree());
		container.add(new WalkToBank());
		container.add(new BankLogs());
		container.add(new Antiban());
		running = true;
	}

	@Override
	public int loop() {
		if(running) {
			if(task == null || !task.hasNext()) {
				task = container.iterator();
			} else {
				final Node curr = task.next();
				if(curr.activate()) {
					curr.execute();
				}
			}
		}
		return 50;
	}

	@Override
	public void onRepaint(Graphics g) {
		final Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(Constants.paintFont);
		g2d.drawString(getClass().getAnnotation(Manifest.class).name() + " Running for " + timer.toElapsedString(), 5, 100);
		g2d.drawString(generateString(Skills.WOODCUTTING), 5, 112);
		g2d.drawString("Regrowth scrolls used: "+ Constants.scrollsUsed, 5, 124);
		g2d.drawString("Logs chopped: " + Misc.perHour(startTime, Constants.logsChopped) + "(+" + Constants.logsChopped + ")", 5, 136);
		g2d.drawString("Profit made: " + Misc.perHour(startTime, Constants.logsChopped * Constants.logPrice) + "(+" + Constants.logsChopped *  Constants.logPrice + ")", 5, 148);
		
		final SceneObject tree = SceneEntities.getNearest(8389);
		if(tree != null) {
			g2d.drawString("Poly amount: "+ tree.getModel().getTriangles().length, 5, 160);
		}

	}

	public String generateString(int index) {
		StringBuilder sb = new StringBuilder();
		sb.append("Woodcutting: ");
		sb.append(Skills.getRealLevel(index) + "(+" + skillData.level(index) + ") ");
		sb.append("Experience: " + skillData.experience(SkillData.Rate.HOUR, index) + "(+" + skillData.experience(index) + ") ");
		sb.append("TTL: " + Time.format(skillData.timeToLevel(SkillData.Rate.HOUR, index)));
		return sb.toString();
	}

	@Override
	public void messageReceived(MessageEvent arg0) {
		if(arg0.getMessage().contains("get some")) {
			Constants.logsChopped++;
		}
	}

}
