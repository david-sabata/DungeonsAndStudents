package cz.davidsabata.at.postareg.immandbeta120803;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cz.davidsabata.at.postareg.immandbeta120803.achievments.AchievmentsActivity;
import cz.davidsabata.at.postareg.immandbeta120803.agent.AgentActivity;
import cz.davidsabata.at.postareg.immandbeta120803.exceptions.InvalidGameStateException;
import cz.davidsabata.at.postareg.immandbeta120803.guard.GuardActivity;
import cz.davidsabata.at.postareg.immandbeta120803.locator.Wifi;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameInfo.State;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService.GameServiceBinder;
import cz.davidsabata.at.postareg.immandbeta120803.services.Player.Role;

public class MainActivity extends Activity implements OnClickListener {

	private final static String LOG_TAG = "MainActivity";

	protected GameService mGameService;

	protected final MainActivity self = this;

	protected AlertDialog mQuitDialog;
	protected AlertDialog mAbortGameDialog;
	protected AlertDialog mConnectDialog;

	protected EditText mIpInput;

	protected enum start {
		HOST, JOIN
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mIpInput = new EditText(this);

		// hook buttons
		findViewById(R.id.wifi).setOnClickListener(this);
		findViewById(R.id.host_game).setOnClickListener(this);
		findViewById(R.id.connect_game).setOnClickListener(this);
		findViewById(R.id.scanPositions).setOnClickListener(this);
		findViewById(R.id.clearDb).setOnClickListener(this);
		findViewById(R.id.btnExit).setOnClickListener(this);
		findViewById(R.id.btnContinue).setOnClickListener(this);
		findViewById(R.id.btnAchievments).setOnClickListener(this);

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




	@Override
	protected void onResume() {
		super.onResume();

		// zobrazit/skryt tlacitka podle toho jestli je aktivni nejaka hra
		if (mGameService != null) {
			if (mGameService.isThereAGame()) {
				findViewById(R.id.btnContinue).setBackgroundResource(R.drawable.button_green);
				findViewById(R.id.connect_game).setBackgroundResource(R.drawable.button_black);
				findViewById(R.id.host_game).setBackgroundResource(R.drawable.button_black);

				TextView t = (TextView) findViewById(R.id.btnContinue);
				if (mGameService.getGameState() == State.ROUND_ENDED) {
					t.setText(R.string.next_round);
				} else {
					t.setText(R.string.continue_game);
				}
			} else {
				findViewById(R.id.btnContinue).setBackgroundResource(R.drawable.button_black);
			}
		}
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//Handle the back button
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showQuitDialog();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}


	public void showQuitDialog() {
		mQuitDialog = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.exit).setMessage(R.string.quit_confirm)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						stopService(new Intent(self, GameService.class));
						self.finish();
					}
				}).setNegativeButton(R.string.no, null).show();
	}


	public void showAbortGameDialog(final start s) {
		mAbortGameDialog = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.abort_game_title).setMessage(R.string.abort_game_text)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (s == start.HOST) {

							new Thread(new Runnable() {
								public void run() {
									mGameService.quitGame();
									mGameService.hostNewGame();
								}
							}).start();

							Intent hostGameIntent = new Intent(self, PlayersSetupActivity.class);
							startActivity(hostGameIntent);
						} else {
							mGameService.quitGame();
							showConnectDialog();
						}
					}
				}).setNegativeButton(R.string.no, null).show();
	}


	public void showConnectDialog() {
		if (mConnectDialog != null) {
			mConnectDialog.show();
		} else {
			mConnectDialog = new AlertDialog.Builder(self).setTitle(R.string.server_ip).setView(mIpInput).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					final String ip = mIpInput.getText().toString();

					new Thread(new Runnable() {
						public void run() {
							GameService.getInstance().connectToGame(ip);
						}
					}).start();

					Intent connectGameIntent = new Intent(self, PlayersSetupActivity.class);
					startActivity(connectGameIntent);
				}
			}).setNegativeButton("Cancel", null).show();
		}
	}

	@Override
	protected void onStop() {
		if (mQuitDialog != null && mQuitDialog.isShowing()) {
			mQuitDialog.dismiss();
			mQuitDialog = null;
		}
		if (mAbortGameDialog != null && mAbortGameDialog.isShowing()) {
			mAbortGameDialog.dismiss();
			mAbortGameDialog = null;
		}
		if (mConnectDialog != null && mConnectDialog.isShowing()) {
			mConnectDialog.dismiss();
			mConnectDialog = null;
		}

		super.onStop();
	}





	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.host_game:
			try {

				if (mGameService.isThereAGame()) {
					showAbortGameDialog(start.HOST);
				} else {
					new Thread(new Runnable() {
						public void run() {
							mGameService.hostNewGame();
						}
					}).start();
				}

				Intent hostGameIntent = new Intent(this, PlayersSetupActivity.class);
				startActivity(hostGameIntent);
			} catch (InvalidGameStateException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				//				showAbortGameDialog(start.HOST);
				return;
			}
			break;

		case R.id.connect_game:
			try {
				if (mGameService.isThereAGame())
					showAbortGameDialog(start.JOIN);
				else
					showConnectDialog();
			} catch (InvalidGameStateException e) {
				//Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				showAbortGameDialog(start.JOIN);
				return;
			}
			break;

		case R.id.btnExit:
			showQuitDialog();
			break;

		case R.id.btnContinue:
			if (mGameService.isThereAGame()) {
				if (mGameService.getGameState() == State.WAITING_FOR_CONNECTION) {
					startActivity(new Intent(this, PlayersSetupActivity.class));
				} else if (mGameService.getGameState() == State.CHASING) {
					if (mGameService.getLocalPlayer() != null && mGameService.getLocalPlayer().role == Role.AGENT) {
						startActivity(new Intent(this, AgentActivity.class));
					} else {
						startActivity(new Intent(this, GuardActivity.class));
					}
				}
			}
			break;

		case R.id.btnAchievments:
			Intent chievosIntent = new Intent(this, AchievmentsActivity.class);
			startActivity(chievosIntent);
			break;

		case R.id.scanPositions:
			Intent agentIntent = new Intent(this, MapScanActivity.class);
			startActivity(agentIntent);
			break;

		case R.id.wifi:
			mGameService.exportDatabase("DaS_DB_backup.txt");
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




}
