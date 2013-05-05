/*
 * @author - Jeremy Trifilo (Digistr).
*/

package com;

public class ItemContainer
{
	private class Item 
	{
		private boolean deleted;
		private short id;

		private Item(short id) 
		{
			this.id = id;
		}
	}

	private Item[] cache;
	private int size = 0;
	private int readerIndex;

	public ItemContainer(int capacity) 
	{
		if (capacity > Short.MAX_VALUE)
			capacity = Short.MAX_VALUE;
		cache = new Item[capacity];
		readerIndex = capacity - 1;
	}

	public short poll()
	{
		if (readerIndex < 0)
		{
			readerIndex = cache.length - 1;
			return -1;
		}
		if (cache[readerIndex] == null || cache[readerIndex].deleted)
		{
			if (size > 0)
			{
				--readerIndex;
				return poll();
			}
			readerIndex = cache.length - 1;
			return -1;
		}
		--size;
		cache[readerIndex].deleted = true;
		return cache[readerIndex--].id;
	}

	public int size()
	{
		return size;
	}

	public boolean add(short id)
	{
		int available = -1;
		for (int idx = -1; ++idx < cache.length; )
		{
			if (cache[idx] == null || cache[idx].deleted)
			{
				available = idx;
				continue;
			}
			if (cache[idx].id == id)
				return false;	
		}

		if (available == -1)
			return false;

		if (cache[available] == null)
			cache[available] = new Item(id);
		else
		{
			cache[available].deleted = false;
			cache[available].id = id;
		}
		++size;
		return true;
	}
}