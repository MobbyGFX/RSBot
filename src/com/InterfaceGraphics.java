package com;

import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.node.ItemDefinition;

import org.powerbot.game.api.wrappers.widget.WidgetChild;

import java.awt.Font;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Rectangle;

import java.util.Deque;
import java.util.ArrayDeque;

public class InterfaceGraphics
{
	private static final StringBuilder sb = new StringBuilder();

	private static RSItemDefinition NATURE;

	private static short Y_LENGTH;

	private InterfaceGraphics() 
	{

	}

	public static int getNaturePrice() 
	{
		if (NATURE == null)
		{
			NATURE = OnDemandLoader.getRSItemDefinition((short)561);
			return -1;
		}
		return NATURE.getGrandExchangePrice();
	}

	public static void drawComponents(WidgetChild widget, Graphics g, Point mouse) 
	{
		update(widget);
		draw(widget, g, mouse);
	}

	private static void draw(WidgetChild hovered, Graphics g, Point mouse)
	{
		int Y_POS = 90;

		int DRAW_X = mouse.getX() < 382 ? 382 : 16;

		g.setColor(new Color(0, 0, 0, 255));
		g.drawRect(DRAW_X, Y_POS - 16, 364, Y_LENGTH); 

		g.setColor(new Color(16, 16, 16, 100));
		g.fillRect(DRAW_X, Y_POS - 16, 364, Y_LENGTH);

		g.setFont(new Font("Arial", Font.BOLD, 14));

		g.setColor(Color.WHITE);

		sb.setLength(0);

		sb.append("Widget Explorer: ( Parent: ");

		int parent = hovered.getId() >> 0x10;
		int child = hovered.getId() - (parent << 0x10);
		short index = (short)hovered.getChildIndex();
		sb.append(parent);
		sb.append(", Child: ");
		sb.append(child);

		if (index != -1)
		{
			sb.append(", Idx: ");
			sb.append(index);
		}

		sb.append(" )");

		g.drawString(sb.toString(), DRAW_X + 8, Y_POS);

		g.setColor(new Color(255, 255, 255, 127));
		g.drawLine(DRAW_X + 1, Y_POS + 6, DRAW_X + 363, Y_POS + 6); 
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 12));

		sb.setLength(0);

		Rectangle bounds = getBoundingRectangle(hovered);

		sb.append("Bounds: ( X: ");
		sb.append(bounds.getX());
		sb.append(" , Y: ");
		sb.append(bounds.getY());
		sb.append(" , Width: ");
		sb.append(bounds.getWidth());
		sb.append(" , Height: ");
		sb.append(bounds.getHeight());
		sb.append(" )");

		g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 20);

		if (hovered.getText() != null && !hovered.getText().isEmpty())
		{
			sb.setLength(0);
			sb.append("Text: ");
			String text = hovered.getText();
			int lines = text.length() / 52;
			if (lines > 0)
			{
				sb.append(text.substring(0, 52));
				g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);
				for (int i = 1; i < lines; i++)
				{
					sb.setLength(0);
					sb.append(text.substring(i * 52, (i + 1) * 52));
					g.drawString(sb.toString(), DRAW_X + 40, Y_POS += 16);
				}
				sb.setLength(0);
				sb.append(text.substring(lines * 52, text.length()));
				g.drawString(sb.toString(), DRAW_X + 40, Y_POS += 16);
			} else {
				sb.append(text);
				g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);
			}
	
			sb.setLength(0);
			sb.append("Text Length: ");
			sb.append(text.length());
			g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);

			sb.setLength(0);
			sb.append("Text Color: ( RGB [ ");
			int rgb = hovered.getTextColor();
			int red = (rgb >> 0x10) & 0xFF;
			int green = (rgb >> 0x8) & 0xFF;
			int blue = rgb & 0xFF;
			sb.append(red);
			sb.append(" , ");
			sb.append(green);
			sb.append(" , ");
			sb.append(blue);
			sb.append(" ] ) , Hex: 0x");
			sb.append(Integer.toHexString(rgb).toUpperCase());
			g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);
		}

		if (hovered.getChildId() > -1)
		{
			Item item = new Item(hovered);

			ItemDefinition itemInformation = item.getDefinition();

			if (itemInformation != null)
			{
				short itemId = (short)hovered.getChildId();
		
				RSItemDefinition definitions = OnDemandLoader.getRSItemDefinition(itemId);
	
				if (definitions != null)
				{	
					sb.setLength(0);
					sb.append("Item Name: ");
					sb.append(definitions.getName());
					g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);

					sb.setLength(0);
					sb.append("Item ID: ");
					sb.append(itemId);

					int amount = item.getStackSize();
					if (amount < 0)
						amount = -amount;

					sb.append(", Amount: ");
					sb.append(amount);
					g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);

					sb.setLength(0);
					sb.append("Member Item: ");
					sb.append(itemInformation.isMembers());
					g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);

					if (definitions.isNote())
					{
						sb.setLength(0);
						sb.append("Unoted Id: ");
						sb.append(definitions.getUnotedId());
					} else {
						sb.setLength(0);
						sb.append("Noted Id: ");
						int noteId = definitions.getNotedId();
						sb.append(noteId > 0 ? noteId : "N/A");
					}

					if (definitions.getLentId() > 0)
					{
						sb.append(", Lend ID: ");
						sb.append(definitions.getLentId());
					}
					g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);

					sb.setLength(0);
					sb.append("Stackable: ");
					sb.append(definitions.isStackable());
					g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);

					int shopBuy = definitions.getStoreBuyPrice();
					int shopSell = definitions.getStoreSellPrice();
					int highAlch = definitions.getHighAlchemyValue();
					int lowAlch = definitions.getLowAlchemyValue();
					int geValue = definitions.getGrandExchangePrice();

					sb.setLength(0);
					sb.append("Equip Location: ");
					sb.append(definitions.getEquipmentType());
					g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);

					sb.setLength(0);
					sb.append("High Alchemy Price: ");
					sb.append(highAlch > 0 ? highAlch : "N/A");
					g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);

					sb.setLength(0);
					sb.append("Grand Exchange Med Price: ");
					sb.append(geValue > 0 ? geValue : "N/A");
					g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);

					sb.setLength(0);
					sb.append("Shop Sell To Price: ");
					sb.append(shopBuy > 0 ? shopBuy : "N/A");
					sb.append(", Buy From Price: ");
					sb.append(shopSell > 0 ? shopSell : "N/A");
					g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);

					if (geValue > 0 && highAlch > 0)
					{
						int naturePrice = getNaturePrice();
						if (naturePrice > 0)
						{
							int profit = highAlch - geValue - naturePrice;
							if (profit >= ~(naturePrice * 2))
							{
								sb.setLength(0);
								if (profit >= 0)
									sb.append("Profit Per High Alch: ");
								else
									sb.append("Loss Per High Alch: ");
								sb.append(profit);
								g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);
								
								sb.setLength(0);
								if (profit >= 0)
									sb.append("Total High Alch Profit: ");
								else
									sb.append("Total High Alch Loss: ");
								sb.append(profit * amount);
								g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);
							}
						}
					}
			
					int slot = -1;

					for (String action : itemInformation.getGroundActions())
					{
						if (action == null)
							continue;
						sb.setLength(0);
						sb.append("Inventory Action[ ");
						sb.append(++slot);
						sb.append(" ]: ");
						sb.append(action);
						g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);
					}
	
					slot = -1;

					for (String action : itemInformation.getActions())
					{
						if (action == null)
							continue;
						sb.setLength(0);
						sb.append("Ground Item Action[ ");
						sb.append(++slot);
						sb.append(" ]: ");
						sb.append(action);
						g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);
					}
				}

				sb.setLength(0);
				sb.append("Model Zoom: ");
				sb.append(hovered.getModelZoom());
				g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);

			}
		}

		sb.setLength(0);
		boolean wrote = false;
		if (hovered.getTextureId() != -1)
		{
			sb.append("Texture Id: ");
			sb.append(hovered.getTextureId());
			wrote = true;
		}
		if (hovered.getType() != -1)
		{
			if (wrote)
				sb.append(", Type: ");
			else
				sb.append("Type: ");
			sb.append(hovered.getType());
			wrote = true;
		}

		if (hovered.getSpecialType() != -1)
		{
			if (wrote)
				sb.append(", Special Type: ");
			else
				sb.append("Special Type: ");
			sb.append(hovered.getSpecialType());
			wrote = true;			
		}
		if (wrote)
			g.drawString(sb.toString(), DRAW_X + 8, Y_POS += 16);
	}

	private static void update(WidgetChild widget) 
	{
		Y_LENGTH = 56;

		short index = (short)widget.getChildIndex();

		String text = widget.getText();
		if (text != null && !text.isEmpty())
		{
			int lines = 3 + (text.length() / 52);
			Y_LENGTH += (16 * lines);
		}
		if (widget.getChildId() != -1)
		{
			Item item = new Item(widget);

			ItemDefinition itemInformation = item.getDefinition();

			if (itemInformation == null)
				return;

			Y_LENGTH += 192;

			for (String s : itemInformation.getGroundActions())
				if (s != null)
					Y_LENGTH += 16;
			for (String s : itemInformation.getActions())
				if (s != null)
					Y_LENGTH += 16;
		}
	}

	private static Rectangle getBoundingRectangle(WidgetChild child) 
	{
		final Point p = child.getAbsoluteLocation();
		return new Rectangle(p.x, p.y, child.getWidth(), child.getHeight());
	}
}