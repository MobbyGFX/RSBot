package org.kenneh.scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kenneh.core.api.Misc;
import org.kenneh.core.graphics.MousePaint;
import org.kenneh.core.graphics.Paint;
import org.kenneh.scripts.aiofighter.nodes.AbilityHandler;
import org.kenneh.scripts.aiofighter.nodes.LootHandler;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.core.script.job.state.Tree;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.GroundItems;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.net.GeItem;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.map.TilePath;
import org.powerbot.game.api.wrappers.node.GroundItem;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import sk.action.Ability;
import sk.action.ActionBar;
import sk.action.book.magic.Spell;
import sk.general.TimedCondition;


@Manifest(authors = { "Kenneh" }, description = "Kills Warped bats in Lumbridget catacombs.", name = "WarpedBatKiller")
public class WarpedBatKiller extends ActiveScript implements PaintListener {

	TilePath CATACOMB_EXIT_TO_BATS = new TilePath(new Tile[] {new Tile(3972, 5564, 0),
			new Tile(3970, 5555, 0), new Tile(3985, 5553, 0), new Tile(3993, 5542, 0),
			new Tile(4007, 5543, 0), new Tile(4020, 5544, 0), new Tile(4023, 5534, 0), 
			new Tile(4024, 5521, 0)
	});

	TilePath LOADSTONE_TO_ENTRANCE = new TilePath(new Tile[] {new Tile(3233, 3221, 0),
			new Tile(3235, 3206, 0), new Tile(3245, 3199, 0)
	});

	public SceneObject edgeBank() {
		return SceneEntities.getNearest(42378);
	}

	public class CatacombToEdge extends Node {

		@Override
		public boolean activate() {
			return Players.getLocal().getAnimation() == -1 && Players.getLocal().getInteracting() == null && Inventory.isFull();
		}

		Ability teleport = ActionBar.getAbilityInSlot(ActionBar.findAbility(Spell.HOME_TELEPORT));

		@Override
		public void execute() {

			if(teleport != null) {
				if(edgeBank() == null) {
					ActionBar.useAbility(teleport);
					final WidgetChild edgeWidget = Widgets.get(1092, 45);
					new TimedCondition(1500) {
						@Override
						public boolean isDone() {
							return edgeWidget.visible();
						}
					}.waitStop();
					edgeWidget.click(true);
					new TimedCondition(1500) {
						@Override
						public boolean isDone() {
							return !edgeWidget.visible();
						}
					}.waitStop();
					Task.sleep(1000);
				} else {
					SceneObject bank = (SceneObject) Bank.getNearest();
					if(bank.getLocation().distanceTo() > 5) {
						Walking.walk(bank.getLocation());
					}
					if(!Bank.isOpen() && Inventory.isFull()) {
						Bank.open();
					} else {
						Bank.depositInventory();
						Bank.close();
					}
				}
				if(!Inventory.isFull()) {
					ActionBar.useAbility(teleport);
					final WidgetChild lumWidget = Widgets.get(1092, 47);
					new TimedCondition(1500) {
						@Override
						public boolean isDone() {
							return lumWidget.visible();
						}
					}.waitStop();
					lumWidget.click(true);
					new TimedCondition(1500) {
						@Override
						public boolean isDone() {
							return !lumWidget.visible();
						}
					}.waitStop();
					Task.sleep(1000);
				}
			}
		}

	}

	public class LumToCatacombs extends Node {

		@Override
		public boolean activate() {
			return !Players.getLocal().isMoving() && !Inventory.isFull() && getNpc() == null;
		}

		@Override
		public void execute() {
			SceneObject exit = SceneEntities.getNearest(48797);
			status = "Walking to bats";
			LOADSTONE_TO_ENTRANCE.traverse();
			if(exit != null) {
				if(exit.getLocation().distanceTo() <= 7) {
					Camera.turnTo(exit);
					exit.interact("Climb-down");
					Task.sleep(1000);
				}
			};
			exit = SceneEntities.getNearest(48678);
			if(exit != null) {
				Walking.walk(exit);
				Camera.turnTo(exit);
				exit.interact("Climb-down");
				Task.sleep(1000);
			}
			exit = SceneEntities.getNearest(48688);
			if(exit != null) {
				Walking.walk(exit);
				Camera.turnTo(exit);
				exit.interact("Climb-down");
				Task.sleep(1000);
			}
			CATACOMB_EXIT_TO_BATS.traverse();
		}

	}

	Paint p = new Paint();
	MousePaint mp = new MousePaint();
	String status = "";

	int price = 0;

	public void onStart() {
		startTime = System.currentTimeMillis();
		getContainer().submit(new AbilityHandler());
		provide(new Loot());
		provide(new Attack());
		provide(new LumToCatacombs());
		provide(new CatacombToEdge());
		price = GeItem.lookup(25549).getPrice();
		status = "Starting up..";
	}

	private final List<Node> jobsCollection = Collections .synchronizedList(new ArrayList<Node>());

	private Tree jobContainer = null;

	public synchronized final void provide(final Node... jobs) {
		for (final Node job : jobs) {
			if (!jobsCollection.contains(job)) {
				log.info("Providing: " + job.getClass().getSimpleName());
				jobsCollection.add(job);
			}
		}
		jobContainer = new Tree(jobsCollection.toArray(new Node[jobsCollection.size()]));
	}

	@Override
	public int loop() {

		if (Game.getClientState() != Game.INDEX_MAP_LOADED) {
			return 1000;
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

	public GroundItem loot() {
		return GroundItems.getNearest(25549);
	}

	public NPC getNpc() {
		return NPCs.getNearest(new Filter<NPC>(){
			@Override
			public boolean accept(NPC arg0) {
				return arg0 != null
						&& arg0.getName().equals("Warped bat")
						&& !arg0.isInCombat();
			}
		});
	}

	public class Loot extends Node {

		GroundItem loot = null;

		@Override
		public boolean activate() {
			loot = loot();
			return !AbilityHandler.waitForAbility && !Inventory.isFull() && Players.getLocal().getInteracting() == null && loot != null;
		}

		@Override
		public void execute() {
			if(!Misc.isOnScreen(loot)) {
				status = "Rotating camera to loot..";
				Camera.turnTo(loot);
			} else {
				loot.interact("Take", loot.getGroundItem().getName());
				status = "Looting " + loot.getGroundItem().getName();
				new TimedCondition(2000) {
					@Override
					public boolean isDone() {
						return loot == null;
					}
				}.waitStop();
				looted++;
			}
		}
	}

	public int looted = 0;

	public class Attack extends Node {

		NPC bat = null;

		@Override
		public boolean activate() {
			bat = getNpc();
			return !AbilityHandler.waitForAbility && !Inventory.isFull() && LootHandler.getLoot() == null && bat != null && Players.getLocal().getInteracting() == null;
		}

		@Override
		public void execute() {
			if(!Misc.isOnScreen(bat)) {
				status = "Rotating camera to bat..";
				Camera.turnTo(bat);
			} else {
				bat.interact("Attack", bat.getName());
				status = "Attacking " + bat.getName();
				new TimedCondition(1500) {
					@Override
					public boolean isDone() {
						return Players.getLocal().getInteracting() != null;
					}
				}.waitStop();
			}

		}

	}


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

	@Override
	public void onRepaint(Graphics arg0) {
		p.name = "WarpedBatKiller";

		p.version = "0.1";
		p.status = status;
		p.paint(arg0);
		mp.drawMouse(arg0);
		arg0.setColor(Color.CYAN);
		int total = price * looted;
		arg0.drawString("Money gained: " + perHour(total) + " (" + total + ")", 5, 90);
	}

}
