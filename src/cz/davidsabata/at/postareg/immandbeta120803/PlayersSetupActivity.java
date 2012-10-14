package cz.davidsabata.at.postareg.immandbeta120803;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cz.davidsabata.at.postareg.immandbeta120803.agent.AgentActivity;
import cz.davidsabata.at.postareg.immandbeta120803.guard.GuardActivity;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameInfo;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameInfo.State;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService.GameStateListener;
import cz.davidsabata.at.postareg.immandbeta120803.services.Player.Role;

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
				GameService.getInstance();
				final String ip = GameService.getSelfIP();
				if (ip != null) {
					Runnable rnbl = new Runnable() {
						public void run() {
							TextView text = (TextView) activity.findViewById(R.id.ipValue);
							text.setText(ip);
						}
					};
					activity.runOnUiThread(rnbl);
				}
			}
		});

		// show my IP if I am the host
		if (GameService.getInstance().getLocalPlayer().isHost) {
			t.run();
		} else {
			findViewById(R.id.ipTitle).setVisibility(View.GONE);
			findViewById(R.id.ipValue).setVisibility(View.GONE);
			findViewById(R.id.run).setVisibility(View.GONE);
		}
	}




	/**
	 * Provadi pouze host
	 */
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

			gameService.reportGameStart();

			if (gameService.getLocalPlayer().role == Role.AGENT)
				startActivity(new Intent(this, AgentActivity.class));
			else
				startActivity(new Intent(this, GuardActivity.class));
		}
	}





	public void onGameChange() {
		if (GameService.getInstance().getGameState() == State.WAITING_FOR_CONNECTION) {
			// reload adapter
			runOnUiThread(new Runnable() {
				public void run() {
					GameService gameService = GameService.getInstance();
					ListAdapter adapter = new PlayerListAdapter(getLayoutInflater(), gameService.getPlayers());
					ListView list = (ListView) findViewById(R.id.playerList);
					list.setAdapter(adapter);
				}
			});
		} else if (GameService.getInstance().getGameState() == State.CHASING) {
			if (GameService.getInstance().getLocalPlayer().role == Role.AGENT) {
				startActivity(new Intent(this, AgentActivity.class));
			} else {
				startActivity(new Intent(this, GuardActivity.class));
			}
		}
	}


}
