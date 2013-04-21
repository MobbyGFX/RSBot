package org.kenneh.scripts.hydrachopper;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.wrappers.node.SceneObject;

public class ChopTree implements Node {

	@Override
	public boolean activate() {
		return Calculations.distanceTo(Settings.treeTile) <= 5 && Settings.myTree.isTree() && Players.getLocal().getAnimation() == -1 && !Inventory.isFull();
	}

	@Override
	public void execute() {
		final SceneObject tree = SceneEntities.getNearest(Settings.myTree.getObjectId());
		if(tree != null) {
			if(!tree.isOnScreen()) {
				Camera.turnTo(tree);
			} else {
				tree.interact("Chop");
			}
		}
	}

}
