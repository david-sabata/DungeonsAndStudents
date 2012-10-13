package cz.davidsabata.at.postareg.immandbeta120803.agent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import cz.davidsabata.at.postareg.immandbeta120803.R;
import cz.davidsabata.at.postareg.immandbeta120803.missions.BaseMission;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;

public class AgentActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agent);

		GameService gameService = GameService.getInstance();
		BaseMission mission = gameService.getCurrentMission();

		ImageView targetImg = (ImageView) findViewById(R.id.targetImage);
		targetImg.setImageResource(mission.getImageResId());
	}

}
