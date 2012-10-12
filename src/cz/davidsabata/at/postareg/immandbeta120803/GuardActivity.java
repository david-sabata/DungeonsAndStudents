package cz.davidsabata.at.postareg.immandbeta120803;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class GuardActivity extends Activity implements OnTouchListener {
	// map of floors
	List<Integer> floorsId = new ArrayList<Integer>();
	ImageView activeFloor;
	ImageView whereIAm;

	RelativeLayout mainLayout;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guard);



		// set floors control (seek bar)
		getHandlersToImages();
		activeFloor = (ImageView) findViewById(R.id.activeFloor);
		activeFloor.setImageResource(floorsId.get(0));
		setSeekFloor();

		// set where i am (red point on map)
		whereIAm = (ImageView) findViewById(R.id.whereIAm);

		// set on touch
		activeFloor.setOnTouchListener(this);

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
		floorsId.add(R.drawable.basement_2nd);
		floorsId.add(R.drawable.basement_1st);
		floorsId.add(R.drawable.floor_1st);
		floorsId.add(R.drawable.floor_2nd);
		floorsId.add(R.drawable.floor_3nd);
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
				int imgIndex = progress / 25;
				activeFloor.setImageResource(floorsId.get(imgIndex));


			}
		});
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		final int historySize = event.getHistorySize();
		final int pointerCount = event.getPointerCount();

		for (int i = 0; i < pointerCount; ++i) {
			Log.i("Klik Cislo:", Integer.toString(event.getPointerId(i)));
			Log.i("X:", Float.toString(event.getX(i)));
			Log.i("Y:", Float.toString(event.getY(i)));
		}

		// only touch one finger
		if (pointerCount == 1) {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(Math.round(event.getX(0)), Math.round(event.getY(0)), 0, 0);
			whereIAm.setLayoutParams(lp);
		}


		return true;
	}



}
