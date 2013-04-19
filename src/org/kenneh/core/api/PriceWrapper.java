package org.kenneh.core.api;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.kenneh.core.graphics.Logger;
import org.kenneh.scripts.aiofighter.constants.Constants;


public class PriceWrapper {

	private final Map<Integer, Integer> prices = new HashMap<Integer, Integer>();

	public static void main(String[] args) {
		final PriceWrapper pw = new PriceWrapper();
		for(Entry<Integer, Integer> i : pw.prices.entrySet()) {
			System.out.println(i);
		}
	}
	
	public boolean contains(final int id) {
		return prices.containsKey(id);
	}

	public PriceWrapper() {
		storePrice(995, 1); 
		storePrice(5317, 1);
		storePrice(Constants.RARE_DROP_TABLE);
		storePrice(Constants.UNTRADABLE);
		storePrice(Constants.CHAMPION_SCROLLS);
		storePrice(Constants.CHARMS);
	}

	public int getPrice(final int itemId) {
		if(!contains(itemId)) {
			Logger.log("Price not cached for item " + itemId + ", grabbing..");
			storePrice(itemId);
		}
		return prices.get(itemId);
	}

	public void storePrice(final int id, final int value) {
		prices.put(id, value);
	}
	
	public void storePrice(final int... ids) {
		String add = "http://scriptwith.us/api/?return=text&item=";
		for (int i = 0; i < ids.length; i++) {
			add += (i == ids.length - 1) ? ids[i] : ids[i] + ",";
		}
		try {
			final BufferedReader in = new BufferedReader(new InputStreamReader(
					new URL(add).openConnection().getInputStream()));
			final String line = in.readLine();
			in.close();
			final String[] sets = line.split("[;]");
			for (String s : sets) {
				final String[] set = s.split("[:]");
				prices.put(Integer.parseInt(set[0]), Integer.parseInt(set[1]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
