package cz.davidsabata.at.postareg.immandbeta120803.guard;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import cz.davidsabata.at.postareg.immandbeta120803.R;

public class GuardActivity extends Activity implements OnTouchListener {
	protected static final double MIN_ZOOM = 1f;
	protected static final float MAX_ZOOM = 5f;
	// map of floors
	private final List<Integer> floorsId = new ArrayList<Integer>();
	private ImageView activeFloor;
	private Float lastFloorWidth;
	private Float lastFloorHeight;

	// where i am
	private ImageView whereIAm;

	private RelativeLayout mainLayout;

	private static Context context;

	// scaling image
	private float scaleFactor = 1.0f;
	private ScaleGestureDetector scaleDetector;
	private float scalePointX;
	private float scalePointY;
	private final Matrix mtrx = new Matrix();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guard);



		// set floors control (seek bar)
		getHandlersToImages();
		activeFloor = (ImageView) findViewById(R.id.activeFloor);
		activeFloor.setImageResource(floorsId.get(0));

		lastFloorWidth = (float) activeFloor.getWidth();
		lastFloorHeight = (float) activeFloor.getHeight();

		setSeekFloor();

		// set where i am (red point on map)
		whereIAm = (ImageView) findViewById(R.id.whereIAm);

		// set on touch
		activeFloor.setOnTouchListener(this);

		/*
		 * SCALING DETECTOR
		 */

		scaleDetector = new ScaleGestureDetector(this, new OnScaleGestureListener() {
			public boolean onScale(ScaleGestureDetector detector) {
				// Auto-generated method stub
				scaleFactor *= (scaleDetector.getScaleFactor());
				scaleFactor = (float) Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));

				// getScalePoints scalePointX = scaleDetector.getFocusX();
				scalePointY = scaleDetector.getFocusY();

				// Log.i("ScalePointX", Float.toString(scalePointX)); //
				Log.i("lastFloorWidth", Float.toString(lastFloorWidth));

				mtrx.reset();
				float diffWidth = (activeFloor.getWidth() * scaleFactor) - activeFloor.getWidth();
				float diffHeight = (activeFloor.getHeight() * scaleFactor) - activeFloor.getHeight();
				Log.i("diffWidth", Float.toString(diffWidth));
				Log.i("diffHeight", Float.toString(diffHeight));

				mtrx.postTranslate(diffWidth, diffHeight);
				mtrx.postScale(scaleFactor, scaleFactor);

				activeFloor.setImageMatrix(mtrx);
				// sactiveFloor.invalidate(); //
				Log.i("ScaleFactor", Float.toString(scaleFactor));
				return true;
			}

			public boolean onScaleBegin(ScaleGestureDetector detector) {
				return true;
			}

			public void onScaleEnd(ScaleGestureDetector detector) { // TODO
				Log.i("prestavam", "scalovat");

			}
		});
	}


	@Override
	public boolean onTouchEvent(MotionEvent ev) { // Let the
		// ScaleGestureDetector inspect all events.
		scaleDetector.onTouchEvent(ev);

		final int action = ev.getAction();

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			final float x = ev.getX() / scaleFactor;
			final float y = ev.getX() / scaleFactor;
		}

		case MotionEvent.ACTION_MOVE: {
			Log.i("Tahnu", Float.toString(scaleFactor));
		}
		case MotionEvent.ACTION_UP: {

		}

		}

		return true;
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
	 * android.view.MotionEvent) Getting coordinates on the screen
	 */

	public boolean onTouch(View v, MotionEvent event) {

		final int historySize = event.getHistorySize();
		final int pointerCount = event.getPointerCount();

		// for (int i = 0; i < pointerCount; ++i) {
		// Log.i("Klik Cislo:", Integer.toString(event.getPointerId(i)));
		// Log.i("X:", Float.toString(event.getX(i))); // Log.i("Y:",
		// Float.toString(event.getY(i))); // }

		// only touch one finger
		if (pointerCount == 1) {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.setMargins(Math.round(event.getX(0)), Math.round(event.getY(0)), 0, 0);
			whereIAm.setLayoutParams(lp);
		}


		return false;
	}





}
