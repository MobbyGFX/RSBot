package org.kenneh.core.api;

import java.awt.event.MouseEvent;

import org.powerbot.core.Bot;
import org.powerbot.core.script.util.Random;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.wrappers.Locatable;

/**
 * User: Manner
 * Date: 2/8/13
 * Time: 8:06 PM
 */
public class MCamera {

	/**
	 * Uses the mouse to turn the camera to an object
	 *
	 * @param locatable
	 * The object you want to turn the camera to
	 *
	 * @param degreesDeviation
	 * The amount of deviation of the angle
	 *
	 */
	//	public static void turnTo(final Locatable l, final int deviation) {
	//		//new Thread() {
	//		//	public void run() {
	//				Camera.turnTo(l, deviation);
	//			//}
	//		//}.start();
	//	}

	public static boolean turnTo(Locatable locatable, int degreesDeviation){
		return setAngle(Camera.getMobileAngle(locatable), degreesDeviation);
	}

	/**
	 * Uses the mouse to turn the camera to the specified angle
	 *
	 * @param degrees
	 * The angle to set the camera to
	 *
	 * @param degreesDeviation
	 * The amount of deviation of the angle
	 */
	public static boolean setAngle(int degrees, int degreesDeviation) {
		final double DEGREES_PER_PIXEL_X = 0.35;
		degrees %= 360;
		int angleTo = Camera.getAngleTo(degrees);
		while (Math.abs(angleTo) > degreesDeviation) {
			angleTo = Camera.getAngleTo(degrees);
			int pixelsTo = (int) Math.abs(angleTo / DEGREES_PER_PIXEL_X)
					+ Random.nextInt(-(int)(degreesDeviation / DEGREES_PER_PIXEL_X) + 1,
							(int)(degreesDeviation / DEGREES_PER_PIXEL_X) - 1);
			if(pixelsTo > 450) pixelsTo = pixelsTo / 450 * 450;
			int startY = Random.nextInt(-85, 85) + 200;
			if (angleTo > degreesDeviation) {//right
				int startX = (500 - pixelsTo) - Random.nextInt(0,500 - pixelsTo - 10);
				dragMouse(startX,startY,startX + pixelsTo,startY+Random.nextInt(-10,10));
			} else if (angleTo < -degreesDeviation) {//left
				int startX = (pixelsTo + 10) + Random.nextInt(0,500 - pixelsTo + 10);
				dragMouse(startX,startY,startX - pixelsTo,startY+Random.nextInt(-10,10));
			}
		}
		return Math.abs(Camera.getAngleTo(degrees)) <= degreesDeviation;
	}

	/**
	 * Middle clicks and drags the mouse from the specified points
	 *
	 * @param x1
	 * The first points x value
	 * @param y1
	 * The first points y value
	 * @param x2
	 * The second points x value
	 * @param y2
	 * The second points y value
	 */
	public static boolean dragMouse(int x1, int y1, int x2, int y2) {
		final org.powerbot.game.client.input.Mouse mouse = Bot.client().getMouse();
		final java.awt.Component target = Bot.context().getLoader().getComponent(0);
		Mouse.move(x1, y1);
		mouse.sendEvent(
				new MouseEvent(target, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, Mouse.getX(), Mouse.getY(), 1, false, MouseEvent.BUTTON2)
				);
		Mouse.move(x2, y2);
		mouse.sendEvent(
				new MouseEvent(target, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, Mouse.getX(), Mouse.getY(), 1, false, MouseEvent.BUTTON2)
				);
		return Mouse.getX() == x2 && Mouse.getY() == y2 && !Mouse.isPressed();
	}

	/**
	 * Uses the mouse to change the camera to the specified pitch
	 *
	 * @param pitch
	 * The pitch you want to set the camera to
	 *
	 * @param deviation
	 * The amount of deviation of the pitch
	 *
	 * @return <tt>true</tt> if the pitch is successful set
	 */
	public static boolean setPitch(int pitch, int deviation){
		final double DEGREES_PER_PIXEL_Y = 0.39;
		while(Math.abs(Camera.getPitch() - pitch) > deviation){
			boolean up = Camera.getPitch() < pitch;
			int startX = Random.nextInt(-200, 200) + 250;
			int pixels = (int) (Math.abs(Camera.getPitch() - pitch) / DEGREES_PER_PIXEL_Y)
					+ Random.nextInt(-(int)(deviation / DEGREES_PER_PIXEL_Y) + 1,
							(int)(deviation / DEGREES_PER_PIXEL_Y) - 1);
			if(pixels > 270) pixels = pixels/270 * 270;
			if(up){
				int startY = (300 - pixels - 10) - Random.nextInt(0,300 - pixels - 65);
				dragMouse(startX,startY,startX + Random.nextInt(-10,10),startY + pixels);

			} else{
				int startY = (60 + pixels + 10) + Random.nextInt(0,300 - pixels - 70);
				dragMouse(startX,startY,startX + Random.nextInt(-10,10),startY - pixels);
			}
		}
		return Math.abs(Camera.getPitch() - pitch) <= deviation;
	}

}