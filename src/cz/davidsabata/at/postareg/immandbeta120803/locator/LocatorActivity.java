package cz.davidsabata.at.postareg.immandbeta120803.locator;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cz.davidsabata.at.postareg.immandbeta120803.R;

public class LocatorActivity extends Activity {

	private Button btn;
	private TextView tw;
	private WifiManager wifi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locator);

		btn = (Button) this.findViewById(R.id.button1);
		tw = (TextView) this.findViewById(R.id.textView1);
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);


		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) { // TODO Auto-generated method stub

				// if (wifi.isWifiEnabled())

				try {
					wifi.startScan();
					List<ScanResult> sr = wifi.getScanResults();
					Iterator<ScanResult> it = sr.iterator();

					tw.setText("");

					while (it.hasNext()) {
						ScanResult current = it.next();
						tw.append("\nSSID: " + current.SSID + "\n MAC: " + current.BSSID + " Freq: " + current.frequency + " dBm:" + current.level + "\n");
					}

					WifiInfo info = wifi.getConnectionInfo();

					tw.append("\nConnected MAC: " + info.getBSSID());

					// tw.append("\n\nWiFi Status: " + info.toString());

				} catch (Exception e) {
					tw.append(e.getMessage());
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_locator, menu);
		return true;
	}
}
