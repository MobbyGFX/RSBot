package minerscript;

import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.node.SceneObject;

public class MeowMine extends Node {

	public int[] rocks = {15505, 15503};
	
	@Override
	public boolean activate() {
		return !Inventory.isFull();
	}

	@Override
	public void execute() {
		System.out.println("Meow");
		SceneObject rock = SceneEntities.getNearest(rocks);
		if(rock != null) {
			if(Players.getLocal().getAnimation() == -1) {
				rock.interact("Mine");
			}
		}
	}

}
