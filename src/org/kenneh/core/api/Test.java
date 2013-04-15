package org.kenneh.core.api;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map.Entry;


public class Test {

	public static Hashtable<Integer, String> pricelist = new Hashtable<Integer, String>();

	public static int getNotedId(int id) {
		if(getName(id) != null) {
			if(getName(id + 1).contains("noted")) {
				return id + 1;
			}
		}
		return -1;
	}
	
	public static int getId(String name) {
		for(Entry<Integer, String> entry : pricelist.entrySet()){
			String formattedEntry = entry.getValue().split("=")[1].split(":")[0];
			if(formattedEntry.toLowerCase().equals(name.toLowerCase())){
				return entry.getKey();
			}
		}
		return -1;
	}
	
	public static int getPrice(String name) {
		for(Entry<Integer, String> entry : pricelist.entrySet()){
			String formattedEntry = entry.getValue().split("=")[1].split(":")[0];
			if(formattedEntry.toLowerCase().equals(name.toLowerCase())){
				return getPrice(entry.getKey());
			}
		}
		return -1;
	}

	public static void addToHashtable() throws Exception {
		URL url = new URL("http://pastebin.com/raw.php?i=DzkxMvYU");

		InputStreamReader stream = new InputStreamReader(url.openStream());
		BufferedReader reader = new BufferedReader(stream);

		String line = null;
		while ((line = reader.readLine()) != null) {
			int key = Integer.parseInt(line.split("=")[0]);
			pricelist.put(key, line);
		}
	}

	public static int getPrice(int id) {
		if(pricelist.containsKey(id)) {
			return Integer.parseInt(pricelist.get(id).split(":")[1]);
		} else if(pricelist.containsKey(id - 1)) {
			return Integer.parseInt(pricelist.get(id - 1).split(":")[1]);
		}
		return -1;
	}

	public static String getName(int id) {
		if(pricelist.containsKey(id)) {
			return pricelist.get(id).split("=")[1].split(":")[0];
		} else if(pricelist.containsKey(id - 1)) {
			return pricelist.get(id - 1).split("=")[1].split(":")[0]+"(noted)"; // bad! never do this ever! not reliable
		}
		return null;
	}

}
