package cz.davidsabata.at.postareg.immandbeta120803;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cz.davidsabata.at.postareg.immandbeta120803.agent.AgentActivity;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameInfo;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService.GameStateListener;

public class PlayersSetupActivity extends Activity implements OnClickListener, GameStateListener {

	private static final String LOG_TAG = "PlayersSetupActivity";

	protected final Activity activity = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_players_setup);

		GameService gameService = GameService.getInstance();
		gameService.setGameStateListener(this);

		ListAdapter adapter = new PlayerListAdapter(getLayoutInflater(), gameService.getPlayers());
		ListView list = (ListView) findViewById(R.id.playerList);
		list.setAdapter(adapter);


		findViewById(R.id.run).setOnClickListener(this);
	}





	@Override
	protected void onResume() {
		super.onResume();

		GameService gameService = GameService.getInstance();
		if (gameService.getGameState() != GameInfo.State.WAITING_FOR_CONNECTION) {
			// FU, tady nema user co delat
			finish();
		}

		// nacteni IP do textview
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
						NetworkInterface intf = en.nextElement();
						for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress.isLoopbackAddress()) {
								final String ip = inetAddress.getHostAddress().toString();
								Runnable rnbl = new Runnable() {
									public void run() {
										TextView text = (TextView) activity.findViewById(R.id.ipValue);
										text.setText(ip);
									}
								};
								activity.runOnUiThread(rnbl);
							}
						}
					}
				} catch (SocketException ex) {
					Log.e(LOG_TAG, ex.toString());
				}
			}
		});

		// show my IP if I am the host
		if (GameService.getInstance().getLocalPlayer().isHost) {
			t.run();
		} else {
			findViewById(R.id.ipTitle).setVisibility(View.GONE);
			findViewById(R.id.ipValue).setVisibility(View.GONE);
		}
	}





	public void onClick(View v) {
		if (v.getId() == R.id.run) {
			GameService gameService = GameService.getInstance();

			// kontrola jestli jsou hraci nastaveni jak maji
			int res = gameService.checkPlayers();
			if (res != -1) {
				Toast.makeText(getApplicationContext(), getResources().getString(res), Toast.LENGTH_LONG).show();
				return;
			}

			gameService.startGame();

			startActivity(new Intent(this, AgentActivity.class));
		}
	}





	public void onGameChange() {

	}


}
