package org.kenneh.scripts.hydrachopper;

import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.util.Timer;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Summoning;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

public class RegrowTree implements Node {

	@Override
	public boolean activate() {
		return Calculations.distanceTo(Settings.treeTile) <= 5 && Settings.myTree.isStump() 
				&& !Inventory.isFull() && Summoning.isFamiliarSummoned() 
				&& Inventory.contains(Settings.hydraScroll);
	}

	@Override
	public void execute() {
		final SceneObject tree = SceneEntities.getNearest(Settings.myTree.getObjectId());
		final WidgetChild orb = Widgets.get(Summoning.WIDGET_SUMMONING_ORB, 0);
		if(orb != null) {
			orb.interact("Cast");
		}
		if(tree != null) {
			tree.interact("Cast");
		}
		final Timer t = new Timer(2000);
		while(t.isRunning() && Settings.myTree.isStump()) {
			Task.sleep(20);
		}
		if(Settings.myTree.isTree()) {
			Settings.scrollsUsed++;
		}
	}

}
