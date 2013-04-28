package me.asofold.bpl.archer.core.contest;

public enum HitResult {
	NOT_HIT_FINISHED(false, true),
	NOT_HIT(false, false),
	HIT_FINISHED(true, true),
	HIT(true, false);
	
	public final boolean hit;
	
	public final boolean finished;

	private HitResult(boolean hit, boolean finished){
		this.hit = hit;
		this.finished = finished;
	}
	
	public HitResult max(final HitResult other){
		if (hit != other.hit) return hit ? this : other;
		else if (finished != other.finished) return finished ? other : this;
		else return this;
	}
}
