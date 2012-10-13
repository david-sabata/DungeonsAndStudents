package cz.davidsabata.at.postareg.immandbeta120803.locator;

import java.util.List;

public class LocationInfo implements java.io.Serializable {
	private static final long serialVersionUID = -2080886851777838277L;

	public int x, y, floor;
	public List<WifiInfo> wifiInfo;

	public LocationInfo(int x, int y, int floor, List<WifiInfo> wifiInfo) {
		this.x = x;
		this.y = y;
		this.floor = floor;
		this.wifiInfo = wifiInfo;
	}

	@Override
	public String toString() {
		return "x=" + x + ", y=" + y + ", floor=" + floor + ", wifiInfo" + wifiInfo;
	}
}
