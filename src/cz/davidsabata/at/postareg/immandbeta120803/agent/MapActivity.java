package cz.davidsabata.at.postareg.immandbeta120803.agent;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import cz.davidsabata.at.postareg.immandbeta120803.R;
import cz.davidsabata.at.postareg.immandbeta120803.guard.MapCoordinatesWorker;
import cz.davidsabata.at.postareg.immandbeta120803.locator.DatabaseTableItemPos;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;

public class MapActivity extends Activity implements OnTouchListener {

	protected static final double MIN_ZOOM = 0.5f;
	protected static final float MAX_ZOOM = 10f;

	// map of floors
	private final List<Integer> floorsId = new ArrayList<Integer>();
	private final List<ImageView> crossesInMap = new ArrayList<ImageView>();
	private ImageView activeFloor;

	private MapCoordinatesWorker map;

	// scaling image
	private final Matrix mtrx = new Matrix();
	private float scaleFactor = 1.0f;
	private ScaleGestureDetector scaleDetector;

	//pan image
	private final Matrix mtrxTran = new Matrix();
	private final Matrix mtrxTranBegin = new Matrix();
	private float lastX;
	private float lastY;
	private float mPosX = 0;
	private float mPosY = 0;
	private int mActivePointerId;
	private static final int INVALID_POINTER_ID = -1;

	private int mActiveFloorI = 0;

	private List<ImageView> selfIcons;

	private final Handler handler = new Handler();
	private boolean stopService = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guard);

		// set floors control (seek bar)
		getHandlersToImages();
		activeFloor = (ImageView) findViewById(R.id.activeFloor);
		activeFloor.setImageResource(floorsId.get(0));
		mtrxTranBegin.set(activeFloor.getImageMatrix());

		//set seek bar to control floor
		setSeekFloor();

		map = new MapCoordinatesWorker(this, (RelativeLayout) findViewById(R.id.floorMap), R.drawable.ic_launcher_red, 720, 1000);
		// set on touch listener on ScaleDetector
		activeFloor.setOnTouchListener(this);
		scaleDetector = new ScaleGestureDetector(this, new OnScaleGestureListener() {
			public boolean onScale(ScaleGestureDetector detector) {
				scaleFactor *= (scaleDetector.getScaleFactor());
				scaleFactor = (float) Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));

				float diffWidth = (activeFloor.getWidth() * scaleFactor) - activeFloor.getWidth();
				float diffHeight = (activeFloor.getHeight() * scaleFactor) - activeFloor.getHeight();

				mtrx.reset();
				mtrxTran.set(mtrxTranBegin);
				mtrxTran.postTranslate(mPosX * scaleFactor, mPosY * scaleFactor);

				mtrx.set(mtrxTran);
				mtrx.postTranslate((-diffWidth / 2), (-diffHeight / 2));
				mtrx.preScale(scaleFactor, scaleFactor);
				activeFloor.setImageMatrix(mtrx);

				return true;
			}

			public boolean onScaleBegin(ScaleGestureDetector detector) {
				return true;
			}

			public void onScaleEnd(ScaleGestureDetector detector) { // TODO
			}
		});


		// vytvorit ikony pro existujici mista
		List<DatabaseTableItemPos> positions = GameService.getInstance().getSavedPositions();
		for (DatabaseTableItemPos pos : positions) {
			crossesInMap.add(map.addCrossToMap(pos.posx, pos.posy, R.drawable.ic_launcher, mActiveFloorI));
		}


		stopService = false;
		handler.post(timedTask);
	}


	@Override
	protected void onStop() {
		stopService = true;
		super.onStop();
	}


	private final Runnable timedTask = new Runnable() {
		public void run() {
			updateIcons();

			if (!stopService)
				handler.postDelayed(timedTask, 500);
		}
	};



	@Override
	public boolean onTouchEvent(MotionEvent ev) { // Let the
		// ScaleGestureDetector inspect all events.
		scaleDetector.onTouchEvent(ev);

		final int action = ev.getAction();

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			final float x = ev.getX();
			final float y = ev.getY();
			mActivePointerId = ev.getPointerId(0);
			lastX = x;
			lastY = y;

			break;
		}

		case MotionEvent.ACTION_MOVE: {
			final int pointerIndex = ev.findPointerIndex(mActivePointerId);
			final float x = ev.getX();
			final float y = ev.getY();


			// Only move if the ScaleGestureDetector isn't processing a gesture.
			if (!scaleDetector.isInProgress()) {
				final float dx = x - lastX;
				final float dy = y - lastY;

				mPosX += dx;
				mPosY += dy;

				float diffWidth = (activeFloor.getWidth() * scaleFactor) - activeFloor.getWidth();
				float diffHeight = (activeFloor.getHeight() * scaleFactor) - activeFloor.getHeight();

				mtrx.reset();
				mtrxTran.set(mtrxTranBegin);
				mtrxTran.postTranslate(mPosX * scaleFactor, mPosY * scaleFactor);

				mtrx.set(mtrxTran);
				mtrx.postTranslate((-diffWidth / 2), (-diffHeight / 2));
				mtrx.preScale(scaleFactor, scaleFactor);
				activeFloor.setImageMatrix(mtrx);
				//activeFloor.invalidate();
			}



			lastX = x;
			lastY = y;
			break;

		}
		case MotionEvent.ACTION_UP: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_CANCEL: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_POINTER_UP: {
			final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int pointerId = ev.getPointerId(pointerIndex);
			if (pointerId == mActivePointerId) {
				// This was our active pointer going up. Choose a new
				// active pointer and adjust accordingly.
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				lastX = ev.getX(newPointerIndex);
				lastY = ev.getY(newPointerIndex);
				mActivePointerId = ev.getPointerId(newPointerIndex);
			}
			break;
		}
		}

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
				mActiveFloorI = progress / 25;
				activeFloor.setImageResource(floorsId.get(mActiveFloorI));
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
		updateIcons();

		return false;
	}


	private void updateIcons() {
		List<DatabaseTableItemPos> positions;

		try {
			positions = GameService.getInstance().getBestMatchingPos();
		} catch (RuntimeException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return;
		}

		//		Toast.makeText(getApplicationContext(), "Match quality: " + pos.matchQuality, Toast.LENGTH_SHORT).show();

		if (selfIcons == null)
			selfIcons = new ArrayList<ImageView>();

		if (selfIcons.size() > 0) {
			for (int i = 0; i < selfIcons.size(); i++) {
				RelativeLayout rel = (RelativeLayout) selfIcons.get(i).getParent();
				rel.removeView(selfIcons.get(i));
			}
			selfIcons.clear();
		}

		for (DatabaseTableItemPos pos : positions) {
			ImageView im = map.addCrossToMap(pos.posx, pos.posy, R.drawable.ic_launcher_red, mActiveFloorI);
			selfIcons.add(im);
		}
	}
}
