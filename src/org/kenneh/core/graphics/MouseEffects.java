package org.kenneh.core.graphics;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.powerbot.game.api.methods.input.Mouse;

public class MouseEffects extends JPanel implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 847224818622632956L;
	long lastcall  = 0;
	long elapsed = 0;

	public MouseEffects(){
		this.addMouseListener(this);
		addMouseMotionListener(this);
		Frame frame = new JFrame();
		frame.add(this);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
	
	public MouseEffects(boolean pb) {
		
	}

	public static void main(String[] args) {
		new MouseEffects();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	private Point spot;

	final Object lock = new Object();

	final ArrayList<Particle> particles = new ArrayList<Particle>();

	public void paint(Graphics g) {
		final long starttime = System.currentTimeMillis();
		int fps = -1;
		if((starttime - lastcall) != 0) {
			fps = (int) (1000 / (starttime - lastcall));
		}
		lastcall = starttime;
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		repaint(g);
		g.setColor(Color.WHITE);
		elapsed = System.currentTimeMillis() - starttime;
		g.drawString("FPS: " + fps, 5, 45);
	}
	
	public void paintMouse(Graphics g) {
		Point p = Mouse.getLocation();
		if (p != null){
			int x = p.x;
			int y = p.y;
			int color = new Random().nextInt(3);//(0, 3);
			if (Mouse.isPressed()) {
				synchronized (lock) {
					for (int i = 0; i < 50; i++){
						particles.add(new Particle(x,y,color));
					}
				}
			}
			synchronized (lock) {
				Iterator<Particle> piter = particles.iterator();
				while (piter.hasNext()) {
					Particle part = piter.next();
					if (!part.handle(g)) {
						piter.remove();
					}
				}
			}
		}
	}

	public void repaint(Graphics g) {
		Point p = spot;
		if (p != null){
			int x = p.x;
			int y = p.y;
			int color = new Random().nextInt(4);//(0, 3);
			if (pressed) {
				synchronized (lock) {
					for (int i = 0; i < 50; i++){
						particles.add(new Particle(x,y,color));
					}
				}
			}
			synchronized (lock) {
				Iterator<Particle> piter = particles.iterator();
				while (piter.hasNext()) {
					Particle part = piter.next();
					if (!part.handle(g)) {
						piter.remove();
					}
				}
			}
		}
		try {
			Thread.sleep(15); // without this, it runs WAY to fast.. 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint();
	}

	private static class Particle {

		private double posX;
		private double posY;
		private double movX;
		private double movY;
		private int alpha = 255, color = -1;
		java.util.Random generator = new java.util.Random();

		Particle(int pos_x, int pos_y, int color) {
			posX = (double) pos_x;
			posY = (double) pos_y;
			movX = ((double) generator.nextInt(40) - 20) / 15;
			movY = ((double) generator.nextInt(40) - 20) / 15;
			this.color = color;

		}
		
		public Color setColor(Color color, int alpha) {
			return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
		}

		public boolean handle(Graphics page) {
			Graphics2D g2d = (Graphics2D)page;
			alpha -= 7;
			if (alpha <= 0)
				return false;
			switch (color) {
			case 0:
				g2d.setColor(setColor(Color.WHITE, alpha));
				break;
			case 1:
				g2d.setColor(setColor(Color.BLACK, alpha));
				break;
			case 2:
				g2d.setColor(setColor(Color.RED, alpha));
				break;
			case 3:
				g2d.setColor(setColor(Color.MAGENTA, alpha));
				break;
			case 4:
				g2d.setColor(setColor(Color.GREEN.brighter(), alpha));
				break;
			}
			page.drawLine((int) posX, (int) posY, (int) posX, (int) posY);
			posX += movX;
			posY += movY;
			return true;
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override

	public void mouseExited(MouseEvent arg0) {

	}

	private boolean pressed = true;

	@Override
	public void mousePressed(MouseEvent arg0) {
		//pressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		//pressed = false;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		spot = arg0.getPoint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		spot = arg0.getPoint();
	}

}