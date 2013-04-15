package org.kenneh.scripts.aiofighter.constants;

public class SlayerConstants {
	public static int[] ABERRANT_SPECTRES_LOOT = {21620,5296,5298,5301,5302,5303,5300,5304,3053,1073,1163,4103,2485,217};
	public static int[] ABERRANT_SPECTRES_ALCH = {3053,1073,1163,4103};

	public enum Task {
		ABERRANT_SPECTRES("Aberrant spectre", 104, 50, 60, "Ranged/Bolt", ABERRANT_SPECTRES_LOOT, ABERRANT_SPECTRES_ALCH);

		String name, weakness; int level, exp, slayerReq; int[] loot, alch;
		Task(String name, int level, int exp, int slayerReq, String weakness, int[] loot, int[] alch) {
			this.name = name;
			this.weakness = weakness;
			this.level = level;
			this.slayerReq = slayerReq;
			this.loot = loot;
			this.alch = alch;
		}

		public String getName() { return name; }
	}

}
