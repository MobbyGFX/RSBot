package org.kenneh.scripts.hydrachopper;

import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.wrappers.node.SceneObject;

public enum Tree {
	YEW(8389, 155, 224, 1515),
	MAGIC(8389, 1965, 910, 1513);

	private int sceneId, grownPolys, choppedPolys, logId;
	Tree(int sceneId, int grownPolys, int choppedPolys, int logId) {
		this.sceneId = sceneId;
		this.grownPolys = grownPolys;
		this.choppedPolys = choppedPolys;
		this.logId = logId;
	}

	public int getObjectId() {
		return sceneId;
	}

	public int getLogId() {
		return logId;
	}

	public boolean isStump() {
		final SceneObject tree = SceneEntities.getNearest(getObjectId());
		return tree != null && tree.getModel().getTriangles().length == choppedPolys;
	}

	public boolean isTree() {
		final SceneObject tree = SceneEntities.getNearest(getObjectId());
		return tree != null && tree.getModel().getTriangles().length == grownPolys;
	}

}
