import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.kenneh.core.api.utils.Misc;
import org.kenneh.core.graphics.MouseTrail;
import org.kenneh.core.graphics.PaintUtils;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.SkillData;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

@Manifest(authors = { "Kenneh" }, description = "Fletches broad arrows", name = "BroadArrowFletcher")
public class ArrowFletcher extends ActiveScript implements PaintListener, MouseListener {

	public final static Font PROGRESS_BAR_FONT = new Font("Calibri", Font.PLAIN, 14);
	public final static Font PAINT_TEXT_FONT = new Font("Calibri", Font.PLAIN, 12);
	public final static Font PAINT_TITLE_FONT = new Font("Calibri", Font.BOLD, 18);

	public final static Rectangle CHATBOX_AREA = new Rectangle(7, 395, 490, 114);

	public final static Color BLACK_TRANS = new Color(0, 0, 0, 175);

	private final static int HEADLESS_ARROW_ID = 53;
	private final static int BROAD_ARROW_TIP_ID = 13278;
	private final static int BROAD_ARROW_ID = 4160;

	private final SkillData sd = new SkillData();
	private final Timer timer = new Timer(0);
	private final MouseTrail mt = new MouseTrail(Color.GREEN);
	private final long startTime = System.currentTimeMillis();

	private int count = 0;
	private boolean show = true;

	@Override
	public void onRepaint(Graphics g) {
		final Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if(Inventory.contains(BROAD_ARROW_ID) && count == 0) {
			final Item arrow = Inventory.getItem(BROAD_ARROW_ID);
			count = arrow.getStackSize();
		}

		if(show) {

			g2d.setColor(BLACK_TRANS);
			g2d.fill(CHATBOX_AREA);

			g2d.setColor(Color.PINK);
			g2d.setFont(PAINT_TITLE_FONT);
			g2d.drawString("Broad Arrow Fletcher", 170, 410);
			g2d.drawString("Time running: "+ timer.toElapsedString(), 158, 425);

			PaintUtils.drawProgressBar(g2d, 6, 435, 490, 17, Color.BLACK, Color.YELLOW, 150, PaintUtils.getPercentToNextLevel(Skills.FLETCHING));

			g2d.setColor(Color.BLACK);
			g2d.setFont(PROGRESS_BAR_FONT);
			g2d.drawString(PaintUtils.generateString(sd, Skills.FLETCHING), 10, 448);

			g2d.setColor(Color.PINK);
			g2d.setFont(PAINT_TEXT_FONT);
			final Item arrow = Inventory.getItem(BROAD_ARROW_ID);
			g2d.drawString("Arrows made: " + Misc.perHourInfo(startTime, arrow != null ? arrow.getStackSize() - count : 0), 10, 482);
			g2d.drawString("- Kenneh", 435, 506);

		}

		mt.add(Mouse.getLocation());
		mt.draw(g2d);
	}

	@Override
	public int loop() {
		final WidgetChild fletchProgress = Widgets.get(1251, 5);
		final WidgetChild fletchScreen = Widgets.get(1370, 20);
		if(!fletchProgress.validate() && !fletchProgress.isOnScreen()) {
			final Item arrow = Inventory.getItem(HEADLESS_ARROW_ID);
			final Item head = Inventory.getItem(BROAD_ARROW_TIP_ID);
			if(!Inventory.isItemSelected()) {
				arrow.getWidgetChild().interact("Use");
			} else {
				if(Inventory.getSelectedItem().getId() == HEADLESS_ARROW_ID) {
					if(head.getWidgetChild().click(true)) {
						final Timer timeout = new Timer(2000);
						while(!fletchScreen.validate() && !fletchScreen.isOnScreen() && timeout.isRunning()) {
							Task.sleep(20);
						}
					}
				}
			}
		}
		if(fletchScreen.validate() && fletchScreen.isOnScreen()) {
			if(fletchScreen.interact("Make")) {
				final Timer timeout = new Timer(2000);
				while(!fletchProgress.validate() && !fletchProgress.isOnScreen() && timeout.isRunning()) {
					Task.sleep(20);
				}
			}
		}
		return 50;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(CHATBOX_AREA.contains(arg0.getPoint())) {
			show = !show;
		}
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

}
