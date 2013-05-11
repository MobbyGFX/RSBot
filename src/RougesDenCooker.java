import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Set;

import org.kenneh.core.api.framework.RandomEventDisabler;
import org.kenneh.core.api.net.PriceWrapper;
import org.kenneh.core.api.utils.MCamera;
import org.kenneh.core.api.utils.Misc;
import org.kenneh.core.graphics.MouseTrail;
import org.kenneh.core.graphics.PaintUtils;
import org.powerbot.core.Bot;
import org.powerbot.core.event.events.MessageEvent;
import org.powerbot.core.event.listeners.MessageListener;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.input.Mouse.Speed;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.methods.widget.WidgetCache;
import org.powerbot.game.api.util.SkillData;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.client.Client;


@Manifest(authors = { "Kenneh" }, description = "Cooks food at the Rouges Den.", name = "Rogues Den Cooker")
public class RougesDenCooker extends ActiveScript implements PaintListener, MessageListener, MouseListener {

	private final MouseTrail mt = new MouseTrail(Color.RED);
	private final SkillData sd = new SkillData();
	private final Timer timer = new Timer(0);

	private final static int FULL_URN = 20378;
	private final static int EMPTY_URN =  20376;
	private final static int STARTED_URN = 20377;
	private final static int FIRE_ID = 2732;
	private final static int EMERALD_BENEDICT = 14707;

	public final static Font PROGRESS_BAR_FONT = new Font("Calibri", Font.PLAIN, 14);
	public final static Font PAINT_TEXT_FONT = new Font("Calibri", Font.PLAIN, 12);
	public final static Font PAINT_TITLE_FONT = new Font("Calibri", Font.BOLD, 18);

	public final static Rectangle CHATBOX_AREA = new Rectangle(7, 395, 490, 114);
	public final static Color BLACK_TRANS = new Color(0, 0, 0, 175);

	public static boolean useUrns = false;
	public static Food food = Food.LOBSTER;
	public static boolean start = false;
	public static char[] pin = {0, 0, 0, 0};

	private final long startTime = System.currentTimeMillis();

	private Client client = Bot.client();

	private boolean show = true;
	private int cooked = 0;
	private int teleported = 0;
	private String status = "Waiting for GUI";

	private int cookedPrice = 0;
	private int rawPrice = 0;

	private int getPriceDif() {
		return cooked * (cookedPrice - rawPrice);
	}

	private String getHeader() {
		return getPriceDif() >= 0 ? "Profit: " : "Loss: ";
	}

	private boolean validate(final WidgetChild widget) {
		return widget.validate() && widget.isOnScreen();
	}

	public boolean isBankPinOpen() {
		return Widgets.get(13, 0).isOnScreen();
	}

	public boolean depositAllExcept(final int... ids) {
		final Set<Integer> depositable = new HashSet<Integer>();
		for(Item i : Inventory.getItems()) {
			depositable.add(i.getId());
		}
		System.out.println("Initial size: " + depositable.size());
		Integer[] arr = depositable.toArray(new Integer[depositable.size()]);
		for(int id : ids) {
			depositable.remove(id);
		}
		System.out.println("Subtracted size: "+ depositable.size());
		arr = depositable.toArray(new Integer[depositable.size()]);
		for(Integer item : arr) {
			if(Bank.deposit(item, 0)) {
				System.out.println("Depositing item: " + item);
				depositable.remove(item);
			}
		}
		return depositable.isEmpty();
	}

	public void onStart() {
		RDCGui gui = new RDCGui();
		gui.show();

		Mouse.setSpeed(Speed.VERY_FAST);
		getContainer().submit(new RandomEventDisabler());
	}

	@Override
	public int loop() {
		try {
			if(start) {

				if(cookedPrice == 0) {
					cookedPrice = PriceWrapper.getSinglePrice(food.getCookedId());
				}

				if(rawPrice == 0) {
					rawPrice = PriceWrapper.getSinglePrice(food.getRawId());
				}

				if (Game.getClientState() != Game.INDEX_MAP_LOADED) {
					return 1000;
				}

				if (client != Bot.client()) {
					WidgetCache.purge();
					Bot.context().getEventManager().addListener(this);
					client = Bot.client();
				}

				if(Bank.isOpen() && Inventory.contains(food.getRawId())) {
					Bank.close();
				}

				if(isBankPinOpen()) {

					status = "Solving bank pin random..";
					log.info("RUNNING BANK PIN SCRIPT!");
					String msg = Widgets.get(13, 28).getText();
					if(msg.contains("FIRST")) {
						Widgets.get(13, Character.getNumericValue(pin[0]) + 6).click(true);
						sleep(750, 1200);
					}
					if(msg.contains("SECOND")) {
						Widgets.get(13,  Character.getNumericValue(pin[1]) + 6).click(true);
						sleep(750, 1200);
					}
					if(msg.contains("THIRD")) {
						Widgets.get(13, Character.getNumericValue(pin[2]) + 6).click(true);
						sleep(750, 1200);
					}
					if(msg.contains("FOURTH")) {
						Widgets.get(13, Character.getNumericValue(pin[3]) + 6).click(true);
						sleep(750, 1200);
					}
					return 50;

				} else {

					final WidgetChild selectionWidget = Widgets.get(1370, 20);
					final WidgetChild waitForWidget = Widgets.get(1251, 5);
					final Item fullUrn = Inventory.getItem(FULL_URN);
					if(fullUrn != null && !validate(waitForWidget) && !validate(selectionWidget)) {
						if(fullUrn.getWidgetChild().interact("Teleport")) {
							status = "Teleporting full urn..";
							final Timer timeout = new Timer(6000);
							while(fullUrn != null && timeout.isRunning()) {
								Task.sleep(50);
							}
						}
					}

					if(!validate(waitForWidget) && !validate(selectionWidget) && Inventory.contains(food.getRawId()) && !Bank.isOpen()) {
						final SceneObject fire = SceneEntities.getNearest(FIRE_ID);
						if(fire != null) {
							if(!fire.isOnScreen()) {
								MCamera.turnTo(fire, 50);
							} else {
								if(fire.interact("Cook")) {
									status = "Clicking the fire..";
									final Timer timeout = new Timer(6000);
									while(!validate(selectionWidget) && timeout.isRunning()) {
										Task.sleep(50);
									}
								}
							}
						}
					}

					if(validate(waitForWidget)) {
						status = "Cooking food..";
					}

					if(validate(selectionWidget)) {
						if(selectionWidget.click(true)) {
							status = "Clicking the cook button..";
							final Timer timeout = new Timer(6000);
							while(validate(selectionWidget) && timeout.isRunning()) {
								Task.sleep(50);
							}
						}
					}

					final NPC banker = NPCs.getNearest(EMERALD_BENEDICT);
					if(!validate(waitForWidget) && !Inventory.contains(food.getRawId()) && banker != null) {
						if(!banker.isOnScreen()) {
							MCamera.turnTo(banker, 50);
						} else {
							int count = Inventory.getCount(EMPTY_URN);
							if(!Bank.isOpen()) {
								if(banker.interact("Bank")) {
									status = "Opening bank";
									final Timer timeout = new Timer(6000);
									while(!Bank.isOpen() && timeout.isRunning()) {
										Task.sleep(20);
									}
								}
							} else {
								depositAllExcept(FULL_URN, food.getRawId(), EMPTY_URN, STARTED_URN);

								if(Inventory.contains(food.getBurnedId())) {
									status = "Depositing burned food";
									Bank.deposit(food.getBurnedId(), 0);
								}
								if(Inventory.contains(food.getCookedId())) {
									status = "Depositing cooked food";
									Bank.deposit(food.getCookedId(), 0);
								}
								if(useUrns) {

									if(Bank.getItem(STARTED_URN) != null) {
										System.out.println("Withdrawing started urn");
										Bank.withdraw(STARTED_URN, 1);
									}

									if(Bank.getItem(EMPTY_URN) != null) {
										System.out.println("Urns in bank");
										System.out.println(count +  " empty urns in inventory");
										if(count > 1) {
											System.out.println("Too manu urns, depositing " + (count - 1));
											Bank.deposit(EMPTY_URN, count - 1);
										}
										if(count == 0) {
											System.out.println("No urns in inv, withdrawing");
											Bank.withdraw(EMPTY_URN, 1);
										}
									} else {
										System.out.println("No urns in bank, stopping checks.");
										useUrns = false;
									}
								}
								if(Inventory.getCount() <= 5) {
									status = "Withdrawing raw food";
									Bank.withdraw(food.getRawId(), 0);
								}
								Bank.close();
								final Timer timeout = new Timer(6000);
								while(timeout.isRunning() && Bank.isOpen()) {
									Task.sleep(50);
								}
							}
						}
					}
				}
			}
		} catch(Exception a) {
			a.printStackTrace();
		}
		return 50;
	}

	@Override
	public void messageReceived(MessageEvent arg0) {
		if(arg0.getMessage().toLowerCase().contains("roast a")) {
			cooked++;
		}
		if(arg0.getMessage().toLowerCase().contains("activate the rune")) {
			teleported++;
		}
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
			g2d.drawString("Rouges Den Cooker", 170, 410);
			g2d.drawString("Time running: "+ timer.toElapsedString(), 158, 425);

			PaintUtils.drawProgressBar(g2d, 6, 435, 490, 17, Color.BLACK, Color.ORANGE, 150, PaintUtils.getPercentToNextLevel(Skills.COOKING));

			g2d.setColor(Color.BLACK);
			g2d.setFont(PROGRESS_BAR_FONT);
			g2d.drawString(PaintUtils.generateString(sd, Skills.COOKING), 10, 448);

			g2d.setColor(Color.PINK);
			g2d.setFont(PAINT_TEXT_FONT);
			g2d.drawString("Status - " + status, 10, 470);
			g2d.drawString(getHeader() + Misc.perHourInfo(startTime, Math.abs(getPriceDif())), 10, 482);
			g2d.drawString(food + " cooked: "+ Misc.perHourInfo(startTime, cooked), 10, 494);
			g2d.drawString("Urns teleported: " + Misc.perHourInfo(startTime, teleported), 10, 506);
			g2d.drawString("- Kenneh", 435, 506);

		}

		mt.add(Mouse.getLocation());
		mt.draw(g);
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
