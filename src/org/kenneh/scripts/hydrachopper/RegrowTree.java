package org.kenneh.scripts.hydrachopper;

import org.kenneh.core.api.Misc;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.util.Timer;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Summoning;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

public class RegrowTree implements Node {

	@Override
	public boolean activate() {
		return Misc.distanceTo(Constants.treeTile) <= 5 && Constants.myTree.isStump() 
				&& !Inventory.isFull() && Summoning.isFamiliarSummoned() 
				&& Inventory.contains(Constants.hydraScroll);
	}

	@Override
	public void execute() {
		final SceneObject tree = SceneEntities.getNearest(Constants.myTree.getObjectId());
		final WidgetChild orb = Widgets.get(Summoning.WIDGET_SUMMONING_ORB, 0);
		if(orb != null) {
			orb.interact("Cast");
		}
		if(tree != null) {
			tree.interact("Cast");
		}
		final Timer t = new Timer(2000);
		while(t.isRunning() && Constants.myTree.isStump()) {
			Task.sleep(20);
		}
		if(Constants.myTree.isTree()) {
			Constants.scrollsUsed++;
		}
	}

}
