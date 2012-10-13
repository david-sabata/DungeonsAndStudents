package cz.davidsabata.at.postareg.immandbeta120803;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import cz.davidsabata.at.postareg.immandbeta120803.exceptions.InvalidGameStateException;
import cz.davidsabata.at.postareg.immandbeta120803.guard.GuardActivity;
import cz.davidsabata.at.postareg.immandbeta120803.locator.LocatorActivity;
import cz.davidsabata.at.postareg.immandbeta120803.locator.Wifi;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService.GameServiceBinder;

public class MainActivity extends Activity implements OnClickListener {

	private final static String LOG_TAG = "MainActivity";

	protected GameService mGameService;


	protected final MainActivity self = this;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// hook buttons
		findViewById(R.id.wifi).setOnClickListener(this);
		findViewById(R.id.host_game).setOnClickListener(this);
		findViewById(R.id.connect_game).setOnClickListener(this);
		findViewById(R.id.scanPositions).setOnClickListener(this);
		findViewById(R.id.guard).setOnClickListener(this);
		findViewById(R.id.clearDb).setOnClickListener(this);

		Log.d(LOG_TAG, "service is " + (GameService.getInstance() == null ? "null" : "not null"));

		//		// start service
		//		if (NetworkService.getInstance() == null) {
		//			startService(new Intent(this, NetworkService.class));
		//		}
		//
		//		// grab service
		//		if (mNetworkService == null) {
		//			Intent intent = new Intent(this, NetworkService.class);
		//			getApplicationContext().bindService(intent, networkServiceConnection, Context.BIND_AUTO_CREATE);
		//		}

		// start service
		if (GameService.getInstance() == null) {
			startService(new Intent(this, GameService.class));
		} else {
			mGameService = GameService.getInstance();
		}

		// grab service
		if (mGameService == null) {
			Intent intent = new Intent(this, GameService.class);
			getApplicationContext().bindService(intent, modelServiceConnection, Context.BIND_AUTO_CREATE);
		}
	}



	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.host_game:
			try {
				mGameService.hostNewGame();
			} catch (InvalidGameStateException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				return;
			}

		case R.id.connect_game:
			Intent hostGameIntent = new Intent(this, PlayersSetupActivity.class);
			startActivity(hostGameIntent);
			break;


		case R.id.scanPositions:
			Intent agentIntent = new Intent(this, MapScanActivity.class);
			startActivity(agentIntent);
			break;

		case R.id.guard:
			Intent guardIntent = new Intent(this, GuardActivity.class);
			startActivity(guardIntent);
			break;

		case R.id.wifi:
			Intent wifiIntent = new Intent(this, LocatorActivity.class);
			startActivity(wifiIntent);
			break;

		case R.id.clearDb:
			mGameService.clearDatabase();
			Toast.makeText(getApplicationContext(), "We are clear now", Toast.LENGTH_SHORT).show();
			break;
		}
	}



	// pripojovadlo do service
	private final ServiceConnection modelServiceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			if (service instanceof GameServiceBinder) {
				GameServiceBinder binder = (GameServiceBinder) service;
				mGameService = binder.getService();
				Log.d(LOG_TAG, "Game service connected");

				Wifi w = new Wifi(getSystemService(Context.WIFI_SERVICE));
				mGameService.init(w, self);
			}
		}

		public void onServiceDisconnected(ComponentName arg0) {
			mGameService = null;
			Log.d(LOG_TAG, "Game service disconnected");
		}
	};



	//	private final ServiceConnection networkServiceConnection = new ServiceConnection() {
	//
	//		public void onServiceConnected(ComponentName className, IBinder service) {
	//			if (service instanceof NetworkService) {
	//				NetworkServiceBinder binder = (NetworkServiceBinder) service;
	//				mNetworkService = binder.getService();
	//				Log.d(LOG_TAG, "Network service connected");
	//
	//				mNetworkService.init(new Wifi(getSystemService(Context.WIFI_SERVICE)));
	//			}
	//		}
	//
	//		public void onServiceDisconnected(ComponentName arg0) {
	//			mNetworkService = null;
	//			Log.d(LOG_TAG, "Network service disconnected");
	//		}
	//	};



}
