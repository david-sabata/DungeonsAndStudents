package cz.davidsabata.at.postareg.immandbeta120803;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.SeekBar;

public class AgentActivity extends Activity {

	ImageView floor_3rd;
	ImageView floor_2nd;
	ImageView floor_1st;
	ImageView basement_1st;
	ImageView basement_2nd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agent);

		// get handlers to images
		getHandlersToImages();
		setSeekFloor();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}


	/**
	 * 
	 */
	private void getHandlersToImages() {
		// TODO Auto-generated method stub
		floor_3rd = (ImageView) findViewById(R.id.floor_3rd);
		floor_2nd = (ImageView) findViewById(R.id.floor_2nd);
		floor_1st = (ImageView) findViewById(R.id.floor_1st);

		basement_1st = (ImageView) findViewById(R.id.basement_1st);
		basement_2nd = (ImageView) findViewById(R.id.basement_2nd);
	}


	/**
	 * 
	 */
	public void setSeekFloor() {
		SeekBar seekFloors = (SeekBar) findViewById(R.id.seekSetFloor);

		// set listener on change
		seekFloors.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				Log.i("Seek bar percentage", Integer.toString(progress));

			}
		});
	}
}
