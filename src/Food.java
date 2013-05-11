

public enum Food {

	SHRIMP(-1, -1, -1),
	ANCHOVIE(-1, -1, -1),
	LOBSTER(377, 379, 381);
	
	private int rawId, cookedId, burnedId;
	Food(final int rawId, final int cookedId, final int burnedId) {
		this.rawId = rawId;
		this.cookedId = cookedId;
	}

	public int getRawId() {
		return rawId;
	}
	
	public int getCookedId() {
		return cookedId;
	}
	
	public int getBurnedId() {
		return burnedId;
	}
	
}
