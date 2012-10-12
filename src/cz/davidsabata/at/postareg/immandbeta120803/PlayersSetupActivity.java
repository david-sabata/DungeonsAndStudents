package cz.davidsabata.at.postareg.immandbeta120803;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameInfo;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;

public class PlayersSetupActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_players_setup);

		GameService gameService = GameService.getInstance();
		if (gameService.getGameState() != GameInfo.State.WAITING_FOR_CONNECTION) {
			// FU, tady nema user co delat
			finish();
		}

		ListAdapter adapter = new PlayerListAdapter(getLayoutInflater(), gameService.getPlayers());
		ListView list = (ListView) findViewById(R.id.playerList);
		list.setAdapter(adapter);
	}


}
