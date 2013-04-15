package org.kenneh.scripts;

import java.util.ArrayList;

import org.kenneh.core.api.Drop;
import org.kenneh.core.graphics.Logger;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.SkillData;
import org.powerbot.game.api.util.SkillData.Rate;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.node.SceneObject;

import sk.general.TimedCondition;


@Manifest(authors = { "Kenneh" }, description = "Steals and drops tea in varrock", name = "Tea Stealer")
public class TeaStealer extends ActiveScript {

	ArrayList<Node> nodes = new ArrayList<Node>();
	Logger logger = new Logger();
	Timer timer = new Timer(0);
	SkillData sd = new SkillData(timer);

	public int getExperiencePerHour() {
		return sd.experience(Rate.HOUR, Skills.THIEVING);
	}

	public void onStart() {
		nodes.add(new Steal());
		nodes.add(new Dropper());
		logger.display();
		Logger.log("Script initialized!");
		logger.setTitle("Kenneh's Tea Stealer");
	}

	public class Dropper extends Node {

		@Override
		public boolean activate() {
			return Inventory.isFull() && Inventory.getItem(1978) != null;
		}

		@Override
		public void execute() {
			while(Inventory.getItem(1978) != null) {
				Drop.drop(1978);
			}
		}

	}

	public class Steal extends Node {

		SceneObject stall;

		@Override
		public boolean activate() {
			stall = SceneEntities.getNearest(635);
			return !Inventory.isFull() && stall != null;
		}

		@Override
		public void execute() {
			new TimedCondition(3000) {
				@Override
				public boolean isDone() {
					Logger.log("Stealing tea");
					return stall.interact("Steal");
				}
				
			}.waitStop();
		}

	}

	public void onStop() {
		logger.dispose();
	}

	@Override
	public int loop() {
		if(logger != null) {
			logger.setTitle("Kenneh's Tea Stealer \t Experience per hour: " + getExperiencePerHour() + "\t Runtime: " + timer.toElapsedString());
		}
		if(nodes.size() > 0) {
			for(Node node : nodes) {
				if(node.activate()) {
					Logger.log("Executing: " + node.getClass().getSimpleName());
					node.execute();
				}
			}
		}
		return 50;
	}

}
