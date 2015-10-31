package game;

public abstract class BadGuy {

	public int x;
	public float y;
	public float totalLive;
	public float live;
	public float speed;
	public String textureName;

	public boolean isBeingHit;
	public float hitBy;
	public float hittingProgress;

	public boolean isDead;

	public BadGuy() {
		this.totalLive = 1;
		this.live = 1;
		this.speed = 1;
		this.textureName = "BasicBadGuy.png";
	}

	public BadGuy(float totalLive, float speed, String textureName) {
		this.totalLive = totalLive;
		this.live = totalLive;
		this.speed = speed;
		this.textureName = textureName;
	}

	public void hit(float hitPower) {
		if (this.isBeingHit) {
			if (this.hitBy + hitPower >= this.live) {
				this.hittingProgress = PaintUtils.shiftedArcsine(PaintUtils.shiftedSine(this.hitBy) * this.hittingProgress / this.live);
				this.isDead = true;
				this.hitBy = this.live;
			}
			else {
				this.hittingProgress = PaintUtils.shiftedArcsine(PaintUtils.shiftedSine(this.hitBy) * this.hittingProgress / (this.hitBy + hitPower));
				this.hitBy = this.hitBy + hitPower;
			}
		}
		else {
			this.isBeingHit = true;
			this.hittingProgress = 0;
			if (hitPower >= this.live) {
				this.isDead = true;
				this.hitBy = this.live;
			}
			else {
				this.hitBy = hitPower;
			}
		}
	}

	public float getShownLive() {
		if (this.isBeingHit) {
			return (this.live - PaintUtils.shiftedSine(hittingProgress) * hitBy) / this.totalLive;
		}
		else {
			return this.live / this.totalLive;
		}
	}

	public void move() {
		this.y += this.speed / (float) 512;
	}

	public abstract Coin getCoin();
}
