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
import org.powerbot.core.event.events.MessageEvent;
import org.powerbot.core.event.listeners.MessageListener;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.SkillData;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;

@Manifest(authors = { "Kenneh" }, description = "Chops and burns curly roots in the Jadinko lair.", name = "JadinkoChopper")
public class JadinkoChopper extends ActiveScript implements PaintListener, MessageListener, MouseListener {

	public final static Font PROGRESS_BAR_FONT = new Font("Calibri", Font.PLAIN, 14);
	public final static Font PAINT_TEXT_FONT = new Font("Calibri", Font.PLAIN, 12);
	public final static Font PAINT_TITLE_FONT = new Font("Calibri", Font.BOLD, 18);

	public final static Rectangle CHATBOX_AREA = new Rectangle(7, 395, 490, 114);

	public final static Color BLACK_TRANS = new Color(0, 0, 0, 175);

	public final static int INVENTORY_ROOT_ID = 21350;
	public final static int CURLY_ROOT_ID = 12274;
	public final static int CUT_CURLY_ROOT_ID = 12279;
	public final static int LIT_FIRE_PIT = 12286;
	public final static int EMPTY_FIRE_PIT = 12284;
	public final static int UNLIT_FIRE_PIT = 12285;
	public final static int INVENTORY_STRAIGHT_ROOT_ID = 21349;

	private final SkillData sd = new SkillData();
	private final Timer timer = new Timer(0);
	private final MouseTrail mt = new MouseTrail(Color.PINK);
	private final long startTime = System.currentTimeMillis();

	private boolean burning = false;
	private boolean show = true;

	private int burned = 0;
	private int chopped = 0;

	private final static Filter<SceneObject> CUT_ROOT_FILTER = new Filter<SceneObject>() {

		@Override
		public boolean accept(SceneObject obj) {
			return obj.getId() == CUT_CURLY_ROOT_ID && obj.getLocation().distanceTo() <= 10;
		}

	};

	private boolean doAction(final String action, final SceneObject obj) {
		if(Players.getLocal().getAnimation() == -1 && !Players.getLocal().isMoving()) {
			if(obj.getLocation().distanceTo() > 5) {
				Walking.walk(obj.getLocation());
			} else {
				if(!obj.isOnScreen()) {
					Camera.turnTo(obj);
				} else {
					if(obj.interact(action, obj.getDefinition().getName())) {
						final Timer timeout = new Timer(3000);
						while(Players.getLocal().getAnimation() == -1 && timeout.isRunning()) {
							Task.sleep(20);
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public int loop() {
		if(!Inventory.contains(INVENTORY_ROOT_ID)) {
			burning = false;
		}
		if(Inventory.contains(INVENTORY_STRAIGHT_ROOT_ID)) {
			final Item straightRoot = Inventory.getItem(INVENTORY_STRAIGHT_ROOT_ID);
			if(straightRoot != null) {
				straightRoot.getWidgetChild().interact("Drop");
			}
		}
		if(!Inventory.isFull()) {
			if(!burning) {
				final SceneObject cutRoot = SceneEntities.getNearest(CUT_ROOT_FILTER);
				if(cutRoot != null) {
					doAction("Collect", cutRoot);
				} else {
					final SceneObject uncutRoot = SceneEntities.getNearest(CURLY_ROOT_ID);
					if(uncutRoot != null) {
						doAction("Chop", uncutRoot);
					}
				}
			}
		} else {
			SceneObject firePit = SceneEntities.getNearest(LIT_FIRE_PIT);
			if(firePit != null) {
				burning = doAction("Add", firePit);
			} else {
				firePit = SceneEntities.getNearest(UNLIT_FIRE_PIT);
				if(firePit != null) {
					burning = doAction("Light", firePit);
				} else {
					firePit = SceneEntities.getNearest(EMPTY_FIRE_PIT);
					if(firePit != null) {
						burning = doAction("Add", firePit);
					}
				}
			}
		}
		return 50;
	}

	@Override
	public void onRepaint(Graphics g) {

		final Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if(show) {

			g2d.setColor(BLACK_TRANS);
			g2d.fill(CHATBOX_AREA);

			g2d.setColor(Color.PINK);
			g2d.setFont(PAINT_TITLE_FONT);
			g2d.drawString("Jadinko Firemaking", 170, 410);
			g2d.drawString("Time running: "+ timer.toElapsedString(), 158, 425);

			PaintUtils.drawProgressBar(g2d, 6, 435, 490, 17, Color.BLACK, Color.GREEN, 150, PaintUtils.getPercentToNextLevel(Skills.WOODCUTTING));
			PaintUtils.drawProgressBar(g2d, 6, 453, 490, 17, Color.BLACK, Color.ORANGE, 150, PaintUtils.getPercentToNextLevel(Skills.FIREMAKING));

			g2d.setColor(Color.BLACK);
			g2d.setFont(PROGRESS_BAR_FONT);
			g2d.drawString(PaintUtils.generateString(sd, Skills.WOODCUTTING), 10, 448);
			g2d.drawString(PaintUtils.generateString(sd, Skills.FIREMAKING), 10, 466);

			g2d.setColor(Color.PINK);
			g2d.setFont(PAINT_TEXT_FONT);
			g2d.drawString("Roots chopped: " + Misc.perHourInfo(startTime, chopped), 10, 482);
			g2d.drawString("Roots burned: "+ Misc.perHourInfo(startTime, burned), 10, 494);
			g2d.drawString("Burning: " + burning, 10, 506);
			g2d.drawString("- Kenneh", 435, 506);

		}

		mt.add(Mouse.getLocation());
		mt.draw(g);

	}

	@Override
	public void onStop() {
		Misc.savePaint(7, 395, 490, 114);
	}

	@Override
	public void messageReceived(MessageEvent arg0) {
		final String msg = arg0.getMessage().toLowerCase();
		if(msg.contains("jade roots")) {
			chopped += 4;
		}
		if(msg.contains("refuel the fire")) {
			burned++;
		}
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
