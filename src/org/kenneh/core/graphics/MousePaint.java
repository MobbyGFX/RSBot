package org.kenneh.core.graphics;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import org.powerbot.game.api.methods.input.Mouse;

public class MousePaint {
	
	MouseTrail mouseTrail = new MouseTrail();

	public void drawMouse(Graphics g) {
		Graphics2D g1 = (Graphics2D) g;

		// Mouse Path
		mouseTrail.add(Mouse.getLocation());
		mouseTrail.draw(g1);
	}

	private final static class MouseTrail {
		private final int SIZE = 50;
		private final double ALPHA_STEP = (255.0 / SIZE);
		private final Point[] points;
		private int index;
		public MouseTrail() {
			points = new Point[SIZE];
			index = 0;
		}
		public void add(final Point p) {
			points[index++] = p;
			index %= SIZE;
		}

		public void draw(final Graphics g) {
			double alpha = 0;
			for (int i = index; i != (index == 0 ? SIZE - 1 : index - 1); i = (i + 1)
					% SIZE) {
				if (points[i] != null && points[(i + 1) % SIZE] != null) {
					Color rainbow = Color.getHSBColor((float)(alpha / 255), 1, 1);
					g.setColor(new Color(rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), (int)alpha));

					g.fillOval(points[i].x - ((int) alpha / 20) / 2,
							   points[i].y - ((int) alpha / 20) / 2,
							   (int) alpha / 20, (int) alpha / 20);

					alpha += ALPHA_STEP;
				}
			}
		}
	}

}
