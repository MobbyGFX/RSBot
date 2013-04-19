package org.kenneh.scripts.aiofighter.nodes;

import java.util.Hashtable;

import org.kenneh.core.api.PriceWrapper;
import org.kenneh.core.graphics.Logger;
import org.kenneh.scripts.aiofighter.MonsterKiller;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.game.api.methods.node.GroundItems;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.node.GroundItem;


public class PriceChecker extends LoopTask {

	public static int[] untradable = {15398, 24909, 10976, 298};

	public static Hashtable<Integer, Integer> lootlist = new Hashtable<Integer, Integer>();
	//public static Hashtable<Integer, String> namelist = new Hashtable<Integer, String>();
	
	public static PriceWrapper priceWrapper = new PriceWrapper();

	public static void main(String[] args) {
		//System.out.println(getPrice(451));
	}

//	public static int getPrice(final int id) {
//		try {
//			if(id == 995) return 1;
//			if(Misc.arrayContains(id, Constants.charms)) return 0;
//			if(Misc.arrayContains(id, untradable)) return 0;
//			String price;
//			final URL url = new URL("http://www.tip.it/runescape/json/ge_single_item?item=" + id);
//			final URLConnection con = url.openConnection();
//			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
//			final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//			String line;
//			while ((line = in.readLine()) != null) {
//				if (line.contains("mark_price")) {
//					price = line.substring(line.indexOf("mark_price") + 13, line.indexOf(",\"daily_gp") - 1);
//					price = price.replace(",", "");
//					in.close();
//					return Integer.parseInt(price);
//				}
//			}
//		} catch (final Exception ignored) {
//			return -1;
//		}
//		return -1;
//	}

	public Filter<GroundItem> G_ITEM_FILTER = new Filter<GroundItem>() {

		@Override
		public boolean accept(GroundItem arg0) {
			if(arg0 != null) {
				int id = arg0.getId();
				String name = arg0.getGroundItem().getName();
				if(name == null) {
					name = "null";
				}
				int stacksize = arg0.getGroundItem().getStackSize();
				if(lootlist.containsKey(id)) {
					int totalPrice = stacksize * lootlist.get(id);
					if (totalPrice >= MonsterKiller.overXValue) {
						return true;
					}
				} else {
					//System.out.println("Searching price for item:  "+ name+"("+id+")");
					if(priceWrapper.getPrice(id) != -1) {
						System.out.println("Price for "+ name+"("+id+") found!");
						//namelist.put(id, name);
						lootlist.put(id,  priceWrapper.getPrice(id));
					} else {
						//System.out.println(name+"("+id+") not found.. searching noted value of " + (id + 1));
						if(priceWrapper.getPrice(id - 1) != -1) {
							System.out.println("Noted price for "+ name+"("+id+") found!");
							//namelist.put(id, name +"(noted)");
							lootlist.put(id,  priceWrapper.getPrice(id - 1));
						} else {
							//System.out.println("Unable to find price for " + name+"("+id+").. Setting value to 0");
							//namelist.put(id, name);
							lootlist.put(id, 0);
						}
					}
				}
			}
			return false;
		}

	};

	@Override
	public int loop() {
		GroundItem[] all = GroundItems.getLoaded(G_ITEM_FILTER);
		if (all != null) {
			for (GroundItem indiv : all) {
				if (indiv != null) {
					if (!MonsterKiller.loot.contains(indiv.getId())) {
						MonsterKiller.loot.add(indiv.getId());
						Logger.log("Added: " + indiv.getGroundItem().getName() + " to list!");
					}
				}
			}
		}
		return 50;
	}

}
