package cz.davidsabata.at.postareg.immandbeta120803.agent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import cz.davidsabata.at.postareg.immandbeta120803.R;
import cz.davidsabata.at.postareg.immandbeta120803.missions.BaseMission;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameInfo.State;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService.GameStateListener;

public class AgentActivity extends Activity implements GameStateListener {

	PopupWindow briefPopup;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agent);

		GameService gameService = GameService.getInstance();
		final BaseMission mission = gameService.getCurrentMission();

		ImageView targetImg = (ImageView) findViewById(R.id.targetImage);
		targetImg.setImageResource(mission.getImageResId());

		final Context context = this;


		final ImageButton btnOpenPopup = (ImageButton) findViewById(R.id.btnBriefing);
		btnOpenPopup.setOnClickListener(new ImageButton.OnClickListener() {

			public void onClick(View arg0) {
				if (briefPopup != null && briefPopup.isShowing()) {
					briefPopup.dismiss();
					briefPopup = null;
					return;
				}

				LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
				View popupView = layoutInflater.inflate(R.layout.popup_briefing, null);
				briefPopup = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				TextView title = (TextView) popupView.findViewById(R.id.missionTitle);
				title.setText(getResources().getString(mission.getTitleResId()));

				TextView text = (TextView) popupView.findViewById(R.id.missionBrief);
				text.setText(getResources().getString(mission.getBriefingResId()));

				Button btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
				btnDismiss.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						briefPopup.dismiss();
					}
				});


				briefPopup.showAtLocation(findViewById(R.id.main), Gravity.CENTER, 0, 0);
			}
		});


		findViewById(R.id.btnMap).setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				startActivity(new Intent(context, MapActivity.class));
			}
		});

		findViewById(R.id.btnCamera).setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				startActivity(new Intent(context, CameraActivity.class));
			}
		});

		gameService.setGameStateListener(this);
	}


	@Override
	protected void onStop() {
		if (briefPopup != null && briefPopup.isShowing())
			briefPopup.dismiss();

		super.onStop();
	}


	@Override
	protected void onResume() {
		super.onResume();

		onGameChange();
		//		if (GameService.getInstance().getGameState() != GameInfo.State.CHASING) {
		//			finish();
		//		}
	}


	public void onGameChange() {
		Log.d("Agent GameChange", GameService.getInstance().getGameState().toString());

		if (GameService.getInstance().getGameState() == State.ROUND_ENDED) {
			finish();
		}
	}





}
