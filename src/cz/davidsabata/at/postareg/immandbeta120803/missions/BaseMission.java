package cz.davidsabata.at.postareg.immandbeta120803.missions;


public abstract class BaseMission {

	protected int imageResId;

	protected int titleResId;

	protected int briefingResId;

	protected int locX, locY;


	public int getImageResId() {
		return imageResId;
	}

	public int getTitleResId() {
		return titleResId;
	}

	public int getBriefingResId() {
		return briefingResId;
	}

	public int getLocX() {
		return locX;
	}

	public int getLocY() {
		return locY;
	}

	@Override
	public String toString() {
		return "Mission [imageResId=" + imageResId + ", titleResId=" + titleResId + ", briefingResId=" + briefingResId + ", locX=" + locX + ", locY=" + locY + "]";
	}

}
