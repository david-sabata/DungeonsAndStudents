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
import cz.davidsabata.at.postareg.immandbeta120803.agent.CameraActivity;
import cz.davidsabata.at.postareg.immandbeta120803.exceptions.InvalidGameStateException;
import cz.davidsabata.at.postareg.immandbeta120803.guard.GuardActivity;
import cz.davidsabata.at.postareg.immandbeta120803.locator.LocatorActivity;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService.GameServiceBinder;

public class MainActivity extends Activity implements OnClickListener {

	private final static String LOG_TAG = "MainActivity";

	protected GameService mGameService;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// hook buttons
		findViewById(R.id.wifi).setOnClickListener(this);
		findViewById(R.id.host_game).setOnClickListener(this);
		findViewById(R.id.connect_game).setOnClickListener(this);
		findViewById(R.id.agent).setOnClickListener(this);
		findViewById(R.id.guard).setOnClickListener(this);

		Log.d(LOG_TAG, "service is " + (GameService.getInstance() == null ? "null" : "not null"));

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


		case R.id.agent:
			Intent agentIntent = new Intent(this, CameraActivity.class);
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
		}
	}



	// pripojovadlo do service
	private final ServiceConnection modelServiceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			if (service instanceof GameServiceBinder) {
				GameServiceBinder binder = (GameServiceBinder) service;
				mGameService = binder.getService();
				Log.d(LOG_TAG, "Game service connected");
			}
			if (service instanceof GameServiceBinder) {
				GameServiceBinder binder = (GameServiceBinder) service;
				mGameService = binder.getService();
				Log.d(LOG_TAG, "Game service connected");
			}
		}


		public void onServiceDisconnected(ComponentName arg0) {
			mGameService = null;
			Log.d(LOG_TAG, "Service disconnected");
		}
	};

}
