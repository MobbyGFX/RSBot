package org.kenneh.scripts;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.kenneh.core.api.ScriptBase;
import org.kenneh.core.api.astar.AStar;
import org.kenneh.scripts.aiofighter.nodes.AbilityHandler;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.map.TilePath;


@Manifest(authors = { "Kenneh" }, description = "Testy test", name = "Test Script", hidden = true)
public class TestScript extends ScriptBase implements PaintListener {

	public static int distanceTo(NPC t) {
		return AStar.findDistance(t.getLocation());
	}

	public TilePath toPath(ArrayList<Tile> tiles) {
		return Walking.newTilePath((Tile[])tiles.toArray());
	}

	public static NPC getNearest(NPC[] mobs) {
		int distance = 9999;
		NPC temp = null;
		for(NPC n : mobs) {
			int tempd = distanceTo(n);
			if(tempd < distance) {
				temp = n;
				distance = tempd;
			}
		}
		return temp;
	}

	public final Filter<NPC> FILTER = new Filter<NPC>() {
		@Override
		public boolean accept(NPC arg0) {
			return arg0.getName().equals("Abyssal demon");
		}
	};

	@Override
	public void onRepaint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		closest = getNearest(NPCs.getLoaded(FILTER));
		path = AStar.findPath(closest.getLocation());
		closest.draw(g2d);

		for(Tile t : path) {
			t.draw(g2d);
		}

		int abs = (int) closest.getLocation().distanceTo();
		int astar = distanceTo(closest);
		int x = (int) closest.getCentralPoint().getX();
		int y = (int) closest.getCentralPoint().getY();

		g.drawString("Absolute distance: " + abs, x, y);
		y+= 13;
		g.drawString("AStar distance: "+ astar, x, y);

	}

	public NPC closest = null;
	public ArrayList<Tile> path = null;
	
	public class Script extends LoopTask {

		@Override
		public int loop() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}


	@Override
	public void onStart() {
		provide(new AbilityHandler());
		
	}
}
