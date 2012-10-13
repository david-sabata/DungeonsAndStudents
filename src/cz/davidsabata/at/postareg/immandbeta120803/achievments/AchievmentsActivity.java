package cz.davidsabata.at.postareg.immandbeta120803.achievments;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import cz.davidsabata.at.postareg.immandbeta120803.R;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;

public class AchievmentsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_achievments);

		GameService gameService = GameService.getInstance();
		ListAdapter adapter = new ChievosListAdapter(getResources(), getLayoutInflater(), gameService.getAllAchievments());
		ListView list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
	}

}
