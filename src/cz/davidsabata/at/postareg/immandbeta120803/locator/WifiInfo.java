package cz.davidsabata.at.postareg.immandbeta120803.locator;

public class WifiInfo implements java.io.Serializable {
	private static final long serialVersionUID = -3103040451251673383L;

	public String ssid;
	public String bssid;
	public int channel;
	public int dbm;

	public WifiInfo(String ssid, String bssid, int channel, int dbm) {
		this.ssid = ssid;
		this.bssid = bssid;
		this.channel = channel;
		this.dbm = dbm;
	}

	@Override
	public String toString() {
		return "\nssid=" + ssid + "\nbssid=" + bssid + ", channel=" + channel + ", dbm=" + dbm;
	}
}
