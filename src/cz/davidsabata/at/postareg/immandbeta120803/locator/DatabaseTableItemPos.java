package cz.davidsabata.at.postareg.immandbeta120803.locator;

public class DatabaseTableItemPos {
	public int id;
	public int posx;
	public int posy;
	public int floor;

	public DatabaseTableItemPos() {
	}

	public DatabaseTableItemPos(int id, int posx, int posy, int floor) {
		this.id = id;
		this.posx = posx;
		this.posy = posy;
		this.floor = floor;
	}

	@Override
	public String toString() {
		return "id=" + id + ", posx=" + posx + ", posy=" + posy + ", floor=" + floor;
	}


}
