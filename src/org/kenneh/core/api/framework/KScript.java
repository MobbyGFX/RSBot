package org.kenneh.core.api.framework;

import java.awt.Graphics;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.kenneh.scripts.aiofighter.Settings;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.game.api.methods.Game;

public abstract class KScript extends ActiveScript implements PaintListener {

	private final Set<KNode> container = Collections.synchronizedSet(new HashSet<KNode>());
	private Iterator<KNode> task = null;
	private KNode curr = null;
	private volatile boolean canRun = false;

	public abstract boolean init();
	public abstract void close();

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
		if(curr != null && Settings.DEBUG) {
			g.drawString("Current node: "+ curr.getClass().getName(), 5, 100);
		}
	}

	@Override
	public void onStop() {
		canRun = false;
		close();
	}

	@Override
	public int loop() {
		if(canRun && Game.isLoggedIn()) {
			synchronized(container) { 
				if(task == null || !task.hasNext()) {
					task = container.iterator();
				} else {
					curr = task.next();
					if(curr != null && curr.canActivate()) {
						curr.activate();
					}
				}
			}
		}
		return 50;
	}

}
