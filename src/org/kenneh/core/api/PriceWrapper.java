package org.kenneh.core.api;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.kenneh.core.graphics.Logger;


public class PriceWrapper {

	private final Map<Integer, Integer> prices = new HashMap<Integer, Integer>();
	private final Map<Integer, String> names = new HashMap<Integer, String>();

	public static void main(String[] args) {
		final PriceWrapper pw = new PriceWrapper();
		System.out.println(pw.getName(1215) + ":" + pw.getPrice(1215));
	}

	public PriceWrapper() {
		prices.put(995, 1); 
		prices.put(5317, 1);
	}

	public int getPrice(final int itemId) {
		if(!prices.containsKey(itemId)) {
			Logger.log("Price not cached for item " + itemId + ", grabbing..");
			storePrice(itemId);
		}
		return prices.get(itemId);
	}

	public String getName(final int itemId) {
		if(!names.containsKey(itemId)) {
			Logger.log("Name not cached for item " + itemId + ", grabbing..");
			storeName(itemId);
		}
		return names.get(itemId);
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

	public void storeName(final int id) {
		String name = null;
		try {
			final URL url = new URL("http://www.tip.it/runescape/json/ge_single_item?item=" + id);
			final URLConnection con = url.openConnection();
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			final String line = in.readLine();
			in.close();
			name = line.substring(line.indexOf("name") + 7, line.indexOf("mark_price") - 3).replace(",", "");
			names.put(id, name);
		} catch (final Exception ignored) {
			ignored.printStackTrace();
		}
	}

}
