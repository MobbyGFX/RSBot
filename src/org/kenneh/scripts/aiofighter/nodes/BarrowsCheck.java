package org.kenneh.scripts.aiofighter.nodes;

import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.interactive.Players;

public class BarrowsCheck extends Node {
	
	int[] degraded = {4980, 4986, 4992, 4998, 4884, 4890, 4896, 4902, 4860, 4866, 4872,
			4878, 4956, 4962, 4968, 4974, 4932, 4938, 4944, 4950, 21742, 21750, 21758,
			21766, 18350, 18352, 18354, 18356, 18358, 18360, 25992, 25994, 25996
	};

	public static boolean teleOut = false;
	
	
	@Override
	public boolean activate() {
		for(int wornItem : Players.getLocal().getAppearance()) {
			for(int degrade : degraded) {
				if(wornItem == degrade) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void execute() {
		teleOut = true;
	}

}
