package cz.davidsabata.at.postareg.immandbeta120803.locator;

public class DatabaseTableItemPos implements Comparable<DatabaseTableItemPos> {
	public int id;
	public int posx;
	public int posy;
	public int floor;

	public int matchQuality = -1;


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


	public int compareTo(DatabaseTableItemPos o1) {
		if (this.matchQuality == o1.matchQuality)
			return 0;
		else if ((this.matchQuality) > o1.matchQuality)
			return 1;
		else
			return -1;
	}


}
