import org.powerbot.core.script.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;

@Manifest(hidden = true, authors = { "your name goes here" }, description = "what the script does", name = "name of the script")
public class ExampleScript extends ActiveScript {

	private int[] treeIds = {
			0, 1, 2, 3
	}; // every object in runescape has an ID that you can find it by. These are just examples and wont work :p

	private int logId = 5; // items have ids too.. I don't know what the id for the log is so you'll have to change this aswell.

	@Override
	public int loop() {
		final SceneObject tree = SceneEntities.getNearest(treeIds); // we get our tree by the ids this is OUR TREE
		if(Inventory.isFull()) { // if our inventory is full
			final Timer timer = new Timer(10000); // failsafe. If it doesnt drop all of them by the time this is finished, go out of the loop
			// time in java is counted by milliseconds, 10000 = 10 seconds.
			while(timer.isRunning() && Inventory.contains(logId)) { // as long as this timer is running, and we have the logs in our inventory,
				// the code in this section will activate
				final Item log = Inventory.getItem(logId); // this is the log we're dropping
				if(log != null) { // make sure the log exists before doing anything with it
					log.getWidgetChild().interact("Drop"); // drop our log, we dont need these anyway
				}

			}
		} else {
			if(Players.getLocal().getAnimation() == -1) { // this means if our players animation is -1, aka standing there doing nothing, do stuff below it
				if(tree != null) { // make sure the tree exists
					if(!tree.isOnScreen()) { // if the tree isnt on screen
						Camera.turnTo(tree); // turn the camera to the tree
					} else {
						tree.interact("Chop"); // this is where the script clicks the tree to chop
					}
				}
			}
		}
		return 250; // how fast the script loops, you wont need to mess with this
	}

}
