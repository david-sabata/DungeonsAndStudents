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

	private Button btn, btn2, btn3;
	private TextView tw;
	private Wifi wifi;
	WifiLogger wifiLogger;
	DatabaseHandler db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locator);

		btn = (Button) this.findViewById(R.id.button1);
		btn2 = (Button) this.findViewById(R.id.button2);
		btn3 = (Button) this.findViewById(R.id.button3);
		tw = (TextView) this.findViewById(R.id.textView1);
		wifi = new Wifi(getSystemService(Context.WIFI_SERVICE));
		wifiLogger = new WifiLogger(wifi);

		db = new DatabaseHandler(this);
		db.onUpgrade(db.getWritableDatabase(), 0, 0);


		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) { // TODO Auto-generated method stub

				try {
					tw.setText("Logged: " + wifiLogger.Log(5, 10, 2) + "\n");

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
					db.InsertLocations(wifiLogger.getLocationInfo());

					// wifiLogger.deserializeFromSDcardJson("DungeonsAndStudentsWifi.txt");

				} catch (Exception e) {
					// tw.append(e.getMessage());
					Log.e("save to SD card", e.getMessage());
				}


			}
		});

		btn3.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				tw.setText(db.getBestMatchingPos(wifi.getDetectedNetworks()).toString());

			}
		});
	}

}
