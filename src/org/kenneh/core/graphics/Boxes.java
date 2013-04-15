package org.kenneh.core.graphics;

import java.awt.Rectangle;
import org.powerbot.core.script.job.Task;

public class Boxes {

	public static void main(String[] args) {
		Boxes boxes = new Boxes(5, 50, 50);
		boxes.init();
	}

	private int bwidth, bheight;

	Rectangle rect[] = null;

	public void init() {
		int x = 5, y = 70;
		for(int i = 0; i < rect.length; i++) {
			rect[i] = new Rectangle(x, y, bwidth, bheight);
			y += bheight + 2;
		}
	}

	public Rectangle[] getRect() {
		return rect;
	}

	public void open(int index) {
		int maxWidth = getRect()[index].width;
		for(int i = 0; i <= maxWidth; i++) {
			rect[index] = new Rectangle(5, 70, i, i);
			Task.sleep(20);
			System.out.println(i +":"+i);
		}
		//rect[index] = new Rectangle(5, 70, maxWidth, maxWidth);
	}

	public void close(int index) {
		int maxWidth = getRect()[index].width;
		for(int i = maxWidth; i >= 0; i--) {
			rect[index] = new Rectangle(5, 70, i, i);
			Task.sleep(20);
			System.out.println(i +":"+i);
		}
		//rect[index] = new Rectangle(5, 70, maxWidth, maxWidth);
	}

	public Boxes(int amount) {
		rect = new Rectangle[amount];
		bwidth = 25; bheight = 25;
		for(int i = 0; i < amount; i++) {
			rect[i] = new Rectangle(5, 70, bwidth, bheight);
		}
		System.out.println("Creating array of " + amount + " rectangles with default dimensions");
	}

	public Boxes(int amount, int width, int height) {
		rect = new Rectangle[amount];
		bwidth = width; bheight = height;
		for(int i = 0; i < amount; i++) {
			rect[i] = new Rectangle(5, 70, bwidth, bheight);
		}
		System.out.println("Creating array of " + amount + " rectangles with " + bwidth + "x" + bheight + " dimensions");
	}

	public int getX(int index) {
		if(index > rect.length) throw new IndexOutOfBoundsException();
		return rect[index].x;
	}

}
