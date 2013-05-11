package minerscript;

import java.util.ArrayList;

import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.Manifest;

@Manifest(authors = { "Kenneh" }, description = "The purrfect miner", name = "MeowMiner", hidden = true)
public class Miner extends ActiveScript {
	
	public void onStart() {
		nodes.add(new MeowMine());
		nodes.add(new MeowDrop());
	}
	
	private ArrayList<Node> nodes = new ArrayList<Node>();

	@Override
	public int loop() {
		for(Node meow : nodes) {
			if(meow.activate()) {
				meow.execute();
			}
		}
		return 50;
	}

}
