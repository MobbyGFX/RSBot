/*
 * @author - Jeremy Trifilo (Digistr).
*/

package com;

import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Rectangle;

public class WidgetExplorer extends ScriptTask
{
	private Point mouse = new Point(0, 0);
	private WidgetChild hovered = null;
	private boolean moving = false;

	@Override
	public void execute()
	{
		if (moving)
		{
			moving = false;
			hovered = getWidget();
		}
	}

	@Override
	public void draw(Graphics g)
	{
		if (hovered != null)
		{
			InterfaceGraphics.drawComponents(hovered, g, mouse);
		}
		drawHoveredInterface(g);
		drawMouse(g, 2, 10, 8);
	}

	@Override
	public void move(Point p)
	{
		mouse = p;
		moving = true;
	}

	@Override
	public void sendMessage(char[] message)
	{
		
	}

	public Rectangle getBoundingRectangle(WidgetChild child) 
	{
		final Point p = child.getAbsoluteLocation();
		return new Rectangle(p.x, p.y, child.getWidth(), child.getHeight());
	}

	private WidgetChild getWidget()
	{
		WidgetChild closest = null;
		int smallest = 500000;
		for (Widget widget : Widgets.getLoaded()) 
		{
			WidgetChild[] children = widget.getChildren();
			for (WidgetChild child : widget.getChildren()) 
			{
				if (child.visible() && getBoundingRectangle(child).contains(mouse)) 
				{
					int area = child.getWidth() * child.getHeight();
					if (area > 0 && area < smallest && child.validate())
					{
						smallest = area;
						closest = child;
					}
					for (WidgetChild inner : child.getChildren())
					{
						if (inner.visible() && getBoundingRectangle(inner).contains(mouse)) 
						{
							area = inner.getWidth() * inner.getHeight();
							if (area > 0 && area < smallest && inner.validate())
							{
								smallest = area;
								closest = inner;
							}
						}		
					}
				}
			}
		}
		return closest;
	}

	private void drawMouse(Graphics g, int thickness, int radius, int gap)
	{
		g.setColor(new Color(255, 255, 255, 60));
		g.fillRect(0, (int)mouse.getY() - thickness/2, (int)mouse.getX() - gap, thickness);
		g.fillRect((int)mouse.getX() + gap, (int)mouse.getY() - thickness/2, 765 - (int)mouse.getX() - gap, thickness);
		g.fillRect((int)mouse.getX() - thickness/2, 0, thickness, (int)mouse.getY() - gap);
		g.fillRect((int)mouse.getX() - thickness/2, (int)mouse.getY() + gap, thickness, 553 - (int)mouse.getY() - gap);
		g.setColor(new Color(200, 200, 200, 100));
		g.fillOval((int)mouse.getX() - radius, (int)mouse.getY() - radius, radius * 2, radius * 2);
		g.setColor(new Color(50, 50, 50, 200));
		g.drawOval((int)mouse.getX() - radius, (int)mouse.getY() - radius, radius * 2, radius * 2);
	}

	private void drawHoveredInterface(Graphics g)
	{
		if (hovered == null)
			return;
		Rectangle rect = hovered.getBoundingRectangle();
		g.setColor(new Color(255, 0, 255));
		g.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
	}
}