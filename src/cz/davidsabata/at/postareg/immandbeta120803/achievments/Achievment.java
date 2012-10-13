package cz.davidsabata.at.postareg.immandbeta120803.achievments;

public class Achievment {

	public int imageResId;

	public int matchResId;

	public int titleResId;

	public boolean isDone = false;


	public Achievment(int imageResId, int matchResId, int titleResId, boolean isDone) {
		this.imageResId = imageResId;
		this.matchResId = matchResId;
		this.titleResId = titleResId;
		this.isDone = isDone;
	}

}
