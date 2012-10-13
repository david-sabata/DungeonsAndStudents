package cz.davidsabata.at.postareg.immandbeta120803.locator;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cz.davidsabata.at.postareg.immandbeta120803.R;

public class LocatorActivity extends Activity {

	private Button btn, btn2;
	private TextView tw;
	private Wifi wifi;
	WifiLogger wifiLogger;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locator);

		btn = (Button) this.findViewById(R.id.button1);
		btn2 = (Button) this.findViewById(R.id.button2);
		tw = (TextView) this.findViewById(R.id.textView1);
		wifi = new Wifi(getSystemService(Context.WIFI_SERVICE));
		wifiLogger = new WifiLogger(wifi);


		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) { // TODO Auto-generated method stub

				try {
					tw.setText("Logged: " + wifiLogger.Log(5, 10, 2) + "\n");




					/*
					 * wifi.startScan(); List<ScanResult> sr =
					 * wifi.getScanResults(); Iterator<ScanResult> it =
					 * sr.iterator();
					 * 
					 * tw.setText("");
					 * 
					 * while (it.hasNext()) { ScanResult current = it.next();
					 * tw.append("\nSSID: " + current.SSID + "\n MAC: " +
					 * current.BSSID + " Freq: " + current.frequency + " dBm:" +
					 * current.level + "\n"); }
					 * 
					 * WifiInfo info = wifi.getConnectionInfo();
					 * 
					 * tw.append("\nConnected MAC: " + info.getBSSID());
					 * 
					 * // tw.append("\n\nWiFi Status: " + info.toString());
					 */
				} catch (Exception e) {
					// tw.append(e.getMessage());
					Log.e("moje", e.getMessage());
				}
			}
		});

		btn2.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					wifiLogger.serializeToSDcardJson("DungeonsAndStudentsWifi.txt", true);

					wifiLogger.deserializeFromSDcardJson("DungeonsAndStudentsWifi.txt");

				} catch (Exception e) {
					// tw.append(e.getMessage());
					Log.e("save to SD card", e.getMessage());
				}


			}
		});
	}

}
