package org.kenneh.core.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import org.powerbot.game.api.methods.input.Mouse;

public class SmoothMouse {

	/**
	 *  Length of the trail
	 */
	private final int SIZE = 50;
	/**
	 * Gets the color of the trail depending on the SIZE
	 */
	private final float rainbowStep = (float) (1.0/SIZE);
	/**
	 * Gets the alpha of the trail depending on the SIZE
	 */
	private final double alphaStep = (255.0/SIZE);

	/**
	 * Declares the mouse points
	 */
	private Point[] points;
	/**
	 * Counts up the points
	 */
	private int index;
	/**
	 * Trail offset
	 */
	private float offSet = 0.05f;
	/**
	 * Trail start
	 */
	private float start = 0;

	/**
	 * Constructor for MousePaint()
	 */
	public SmoothMouse() {
		points = new Point[SIZE];
		index = 0;
	}

	/**
	 * Adds the current mouse location as a point to draw the trail
	 * @param p MouseLocation()
	 */
	public void add(Point p) {
		points[index++] = p;
		index %= SIZE;
	}

	/**
	 * Draws the cursor on the screen
	 * @param graphics
	 */
	public void drawCursor(Graphics graphics) {
		int x = (int)Mouse.getLocation().getX();
		int y = (int)Mouse.getLocation().getY();
		Graphics2D g2D = (Graphics2D) graphics;
		graphics.setColor(new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));
		Graphics2D spinner = (Graphics2D) g2D.create();
		spinner.rotate(System.currentTimeMillis() % 2000d / 2000d * (360d) * 2 * Math.PI / 180.0, x, y);
		spinner.drawLine(x - 6, y, x + 6, y);
		spinner.drawLine(x, y - 6, x, y +6);
	}

	/**
	 * Draws the trail on screen
	 * @param graphics
	 */
	public void drawTrail(Graphics2D g2D) {
		g2D.setStroke(new BasicStroke(1F));
		double alpha = 0;
		float rainbow = start;

		start += offSet;
		if (start > 1) {
			start -= 1;
		}

		for (int i = index; i != (index == 0 ? SIZE-1 : index-1); i = (i+1)%SIZE) {
			if (points[i] != null && points[(i+1)%SIZE] != null) {
				int rgb = Color.HSBtoRGB(rainbow, 0.9f, 0.9f);
				rainbow += rainbowStep;

				if (rainbow > 1) {
					rainbow -= 1;
				}
				g2D.setColor(new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, (int)alpha));
				g2D.drawLine(points[i].x, points[i].y, points[(i+1)%SIZE].x, points[(i+1)%SIZE].y);

				alpha += alphaStep;
			}
		}
	}
}
