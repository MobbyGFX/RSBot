package org.kenneh.core.api;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class PriceWrapper {
	
	private final Map<Integer, Integer> prices = new HashMap<Integer, Integer>();

	public PriceWrapper() {
		// init wrapper
	}
	
	public int getPrice(int itemId) {
		if(!prices.containsKey(itemId)) {
			System.out.println("Price not cached, grabbing..");
			store(itemId);
		}
		return prices.get(itemId);
	}
	
	public void store(final int... ids) {
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
