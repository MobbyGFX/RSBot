package org.kenneh.core.api.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class MouseTrail {
	private final int SIZE = 25;
	private final double ALPHA_STEP = (255.0 / SIZE);
	private final Point[] points;
	private int index;
	private Color color;

	public MouseTrail(final Color color) {
		this.color = color;
		points = new Point[SIZE];
		index = 0;
	}

	public void add(final Point p) {
		points[index++] = p;
		index %= SIZE;
	}

	public void draw(final Graphics g) {
		double alpha = 0;

		for (int i = index; i != (index == 0 ? SIZE - 1 : index - 1); i = (i + 1) % SIZE) {
			if (points[i] != null && points[(i + 1) % SIZE] != null) {
				g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) alpha));
				g.drawLine(points[i].x, points[i].y, points[(i + 1) % SIZE].x, points[(i + 1) % SIZE].y);
				alpha += ALPHA_STEP;
			}
		}
	}
}