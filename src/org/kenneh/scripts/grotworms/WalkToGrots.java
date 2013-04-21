package org.kenneh.scripts.grotworms;

import org.kenneh.core.api.framework.KNode;
import org.kenneh.core.api.utils.Lodestone;
import org.kenneh.scripts.AIOFishingGuild.Inventory;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.widget.Bank;
import org.powerbot.game.api.wrappers.node.SceneObject;

public class WalkToGrots implements KNode {

	@Override
	public boolean canActivate() {
		return FightWorms.getBestGrot() == null
				&& Inventory.contains(Settings.TELETAB)
				&& !Bank.isOpen()
				&& Inventory.getCount(Settings.FOOD_ID) >= 2;
	}

	@Override
	public void activate() {
		if(Settings.BANK_TILE.distanceTo() <= 5) {
			Settings.setStatus("Waiting for lodestone teleport to complete");
			Lodestone.teleport(Lodestone.PORT_SARIM);
		} else {
			final SceneObject outsideCave = SceneEntities.getNearest(70792);
			final SceneObject insideCave = SceneEntities.getNearest(70795);
			if(Settings.OUTSIDE_GROT_CAVE_TILE.distanceTo() < 50 && Settings.INSIDE_GROT_CAVE_TILE.distanceTo() > 100) {
				if(outsideCave != null && Settings.OUTSIDE_GROT_CAVE_TILE.distanceTo() < 5) {
					Settings.setStatus("Entering cave");
					outsideCave.interact("Enter");
				} else {
					Settings.setStatus("Walking to outside cave");
					Walking.walk(Settings.OUTSIDE_GROT_CAVE_TILE);
				}
			} else if(Settings.OUTSIDE_GROT_CAVE_TILE.distanceTo() > 100 && Settings.INSIDE_GROT_CAVE_TILE.distanceTo() < 50) {
				if(insideCave != null && Settings.INSIDE_GROT_CAVE_TILE.distanceTo() < 5) {
					Settings.setStatus("Sliding down");
					insideCave.interact("Slide");
				} else {
					Settings.setStatus("Walking to inside tunnel");
					Walking.walk(Settings.INSIDE_GROT_CAVE_TILE);
				}
			}
		}
		
	}

}
