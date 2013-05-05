/*
 * @author - Jeremy Trifilo (Digistr).
*/

package com;

public class RSItemDefinition
{

	private static final String[] EQUIP_NAMES = 
	{
		"N/A", "Head", "Cape", "Neck",
		"Weapon", "Torso", "Shield", "N/A", 
		"Legs", "N/A", "Hands", "Feet", 
		"N/A", "Ring", "Ammunition",
	};

	private String name;
	private int value;
	private int exchangePrice;
	private short noteId;
	private short lentId;
	private byte wieldLocation;
	private boolean stackable;

	protected RSItemDefinition() 
	{
		value = 0;
		noteId = -1;
		lentId = -1;
		wieldLocation = -1;
		exchangePrice = -1;
		stackable = false;
	}
	
	protected RSItemDefinition(FileBuilder stream, int ge_value)
	{
		name = stream.readString();
		value = stream.readInt();
		noteId = (short)stream.readSignedShort();
		lentId = (short)stream.readSignedShort();
		wieldLocation = stream.readSignedByte();
		stackable = stream.readBoolean();
		exchangePrice = ge_value;
	}

	public String getName() {
		return name;
	}

	public boolean isNote()
	{
		return stackable && noteId != -1;
	}

	public boolean isStackable()
	{
		return stackable;
	}

	public short getNotedId()
	{
		if (!stackable)
			return noteId;
		return -1;
	}

	public short getUnotedId()
	{
		if (stackable)
			return noteId;
		return -1;
	}

	public short getLentId()
	{
		return lentId;
	}

	public int getGrandExchangePrice()
	{
		if (exchangePrice < 1 && isNote())
		{
			RSItemDefinition unoted = OnDemandLoader.getRSItemDefinition(getUnotedId());
			if (unoted != null)
			{
				exchangePrice = unoted.exchangePrice;
			}
		}
		return exchangePrice;
	}

	public int getStoreSellPrice()
	{
		return value;
	}

	public int getStoreBuyPrice()
	{
		return (int)(value * 0.3);
	}

	public int getHighAlchemyValue()
	{
		return (int)(value * 0.6);
	}

	public int getLowAlchemyValue()
	{
		return (int)(value * 0.4);
	}

	public String getEquipmentType() 
	{
		return EQUIP_NAMES[wieldLocation + 1];
	}

	public byte getWieldLocation()
	{
		return wieldLocation;
	}

	public boolean isEquipable()
	{
		return wieldLocation > -1;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		sb.append(':');
		sb.append(isNote());
		sb.append(':');
		sb.append(isStackable());
		sb.append(':');
		sb.append(getNotedId());
		sb.append(':');
		sb.append(getUnotedId());
		sb.append(':');
		sb.append(getLentId());
		sb.append(':');
		sb.append(getStoreSellPrice());
		sb.append(':');
		sb.append(getStoreBuyPrice());
		sb.append(':');
		sb.append(getGrandExchangePrice());
		sb.append(':');
		sb.append(getHighAlchemyValue());
		sb.append(':');
		sb.append(getLowAlchemyValue());
		sb.append(':');
		sb.append(getWieldLocation());
		sb.append(':');
		sb.append(isEquipable());
		return sb.toString();
	}
}