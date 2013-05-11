package minerscript;

import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.node.Item;

public class MeowDrop extends Node {

	private int coal = 434;
	
	private boolean keyboardDropping = true;
	
	@Override
	public boolean activate() {
		return Inventory.isFull();
	}

	@Override
	public void execute() {
		Timer timeout = new Timer(10000);
		while(timeout.isRunning() && Inventory.contains(coal)) {
			if(keyboardDropping) {
				Keyboard.sendKey((char) 56);
				Task.sleep(100);
			} else {
				Item ore = Inventory.getItem(coal);
				if(ore != null) {
					ore.getWidgetChild().interact("Drop");
				}
			}
		}
		
	}

}
