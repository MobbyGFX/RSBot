package org.kenneh.core.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.core.script.job.state.Tree;
import org.powerbot.game.api.methods.Game;

public abstract class ScriptBase extends ActiveScript {

	public abstract void onStart();

	private static List<Node> jobsCollection = Collections.synchronizedList(new ArrayList<Node>());

	private static Tree jobContainer = null;

	public void log(String txt, boolean severe) {
		if(severe)
			log.warning(txt);
		else
			log.info(txt);
	}
	
	public void provide(final LoopTask task) {
		getContainer().submit(task);
	}

	public synchronized void provide(final Node... jobs) {
		for (final Node job : jobs) {
			if (!jobsCollection.contains(job)) {
				log("Providing: " + job.getClass().getSimpleName(), false);
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
			final Node job = jobContainer.state();
			if (job != null) {
				jobContainer.set(job);
				getContainer().submit(job);
				job.join();
			}
		}
		return 50;
	}

}
