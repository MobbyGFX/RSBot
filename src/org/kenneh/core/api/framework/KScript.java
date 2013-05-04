package org.kenneh.core.api.framework;

import java.awt.Graphics;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.powerbot.core.Bot;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.widget.WidgetCache;
import org.powerbot.game.client.Client;

public abstract class KScript extends ActiveScript implements PaintListener {

	private final Set<KNode> container = Collections.synchronizedSet(new HashSet<KNode>());
	private Iterator<KNode> task = null;
	private volatile boolean canRun = false;

	public abstract boolean init();
	public void close() {
	}

	public void setRun(final boolean b) {
		canRun = b;
	}

	public synchronized final void submit(final KNode... nodes) {
		if(nodes == null) 
			return;
		for(KNode node : nodes) {
			if(node != null && !container.contains(node)) 
				container.add(node);
		}
	}

	public synchronized final void revoke(final KNode... nodes) {
		if(nodes == null) 
			return;
		for(KNode node : nodes) {
			if(node != null && container.contains(node))
				container.remove(node);
		}
	}

	@Override
	public void onStart() {
		if(init()) {
			canRun = true;
		} else {
			log.warning("Conditions to run not met. Aborting script.");
			stop();
		}
	}

	@Override
	public void onRepaint(final Graphics g) {
		paint(g);
	}

	public abstract void paint(final Graphics g);

	@Override
	public void onStop() {
		canRun = false;
		close();
	}

	private Client client = Bot.client();

	@Override
	public int loop() {

		try {

			if (Game.getClientState() != Game.INDEX_MAP_LOADED) {
				return 1000;
			}

			if (client != Bot.client()) {
				WidgetCache.purge();
				Bot.context().getEventManager().addListener(this);
				client = Bot.client();
			}

			if(canRun && Game.isLoggedIn()) {
				synchronized(container) { 
					if(task == null || !task.hasNext()) {
						task = container.iterator();
					} else {
						final KNode curr = task.next();
						if(curr != null && curr.canActivate()) {
							System.out.println(curr);
							curr.activate();
						}
					}
				}
			}
		} catch(Exception a) {
			System.out.println("Some more internal errors..");
		}
		return 50;
	}

}
