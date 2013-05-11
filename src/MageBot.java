import org.powerbot.core.script.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.GroundItems;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.wrappers.node.GroundItem;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.SceneObject;


@Manifest(authors = { "Foxeh" }, description = "Start inside enchantment room in the mage arena with cosmic runes and some sort of fire staff", name = "MTA Bot", hidden = true)

public class MageBot extends ActiveScript {

	@Override
	public int loop() {
		if(Inventory.isFull()) {
			if (Inventory.getCount(orbID) >= 25) {
				SceneObject depositHole = SceneEntities.getNearest(depositID);
				if(depositHole != null) {
					if (depositHole.getLocation().distanceTo()>3) {
						Walking.walk(depositHole.getLocation());
					} else {
						if (depositHole.isOnScreen()) {
							depositHole.interact("Deposit"); 
							sleep(1000, 1500);
						} else {
							Camera.turnTo(depositHole);
						}
					}
				}
			} else {
				Keyboard.sendKey((char) 49);
				sleep(1000, 1500);
				Item cylinder = Inventory.getItem(cylinderID, dragonstoneID);
				if(cylinder != null)  { 
					cylinder.getWidgetChild().interact("Cast");
				}
			}
		} else {
			GroundItem dragonStone = GroundItems.getNearest (dragonstoneID);
			if(dragonStone !=null) {
				if (dragonStone.getLocation().distanceTo()>3) {
					Walking.walk(dragonStone.getLocation()); 
				} else {
					if(dragonStone.isOnScreen() && !Players.getLocal().isMoving()) {
						dragonStone.interact("Take");

					} else {
						Camera.turnTo(dragonStone);
					}
				}
			} else {
				SceneObject cylinderPile = SceneEntities.getNearest(cylinderpileID);
				if(cylinderPile != null) {
					if (cylinderPile.getLocation().distanceTo()>3) {
						Walking.walk(cylinderPile.getLocation());
					} else {
						if (cylinderPile.isOnScreen()) {
							cylinderPile.interact("Take-from");
							sleep(1000, 1500);
						} else {
							Camera.turnTo(cylinderPile);
						}
					}
				}
			}
		}
		return 50;
	}

	public int cylinderpileID = 10800 ;
	public int depositID = 10803 ;
	public int orbID = 6902 ;
	public int cylinderID = 6898 ;
	public int dragonstoneID = 6903;
}