package cz.davidsabata.at.postareg.immandbeta120803;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import cz.davidsabata.at.postareg.immandbeta120803.locator.LocatorActivity;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.wifi).setOnClickListener(this);
		findViewById(R.id.agent).setOnClickListener(this);
		findViewById(R.id.guard).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}



	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.agent:
			Intent agentIntent = new Intent(this, AgentActivity.class);
			startActivity(agentIntent);
			break;

		case R.id.wifi:
			Intent wifiIntent = new Intent(this, LocatorActivity.class);
			startActivity(wifiIntent);
			break;
		}
	}


}
