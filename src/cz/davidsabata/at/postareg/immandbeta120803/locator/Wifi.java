package cz.davidsabata.at.postareg.immandbeta120803.locator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class Wifi {

	WifiManager mWifiManager;
	private final static ArrayList<Integer> channelsFrequency = new ArrayList<Integer>(Arrays.asList(0, 2412, 2417, 2422, 2427, 2432, 2437, 2442, 2447, 2452, 2457, 2462, 2467, 2472, 2484));

	// getSystemService(Context.WIFI_SERVICE)
	public Wifi(Object wifiManager) {
		this.mWifiManager = (WifiManager) wifiManager;
	}

	public List<ScanResult> getScanResults() {
		mWifiManager.startScan();
		return mWifiManager.getScanResults();
	}

	public List<WifiInfo> getDetectedNetworks() {
		List<ScanResult> scanResultList = getScanResults();
		Iterator<ScanResult> it = scanResultList.iterator();

		List<WifiInfo> wifiList = new ArrayList<WifiInfo>();

		while (it.hasNext()) {
			ScanResult sr = it.next();
			wifiList.add(new WifiInfo(sr.SSID, sr.BSSID, getChannelFromFrequency(sr.frequency), sr.level));
		}

		return wifiList;
	}

	public android.net.wifi.WifiInfo getConnectedWifiInfo() {
		return mWifiManager.getConnectionInfo();
	}

	public static Integer getFrequencyFromChannel(int channel) {
		return channelsFrequency.get(channel);
	}

	public static int getChannelFromFrequency(int frequency) {
		return channelsFrequency.indexOf(Integer.valueOf(frequency));
	}
}
