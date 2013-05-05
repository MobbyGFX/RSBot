/*
 * @author - Jeremy Trifilo (Digistr).
*/

package com;

import org.powerbot.core.script.job.Task;

import java.awt.Point;
import java.awt.Graphics;

public abstract class ScriptTask extends Task
{
	public abstract void sendMessage(char[] message);
	public abstract void draw(Graphics g);
	public abstract void move(Point p);
}