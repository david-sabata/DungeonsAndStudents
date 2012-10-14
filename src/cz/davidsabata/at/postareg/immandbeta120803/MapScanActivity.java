package cz.davidsabata.at.postareg.immandbeta120803;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import cz.davidsabata.at.postareg.immandbeta120803.guard.MapCoordinatesWorker;
import cz.davidsabata.at.postareg.immandbeta120803.guard.RealCoordinates;
import cz.davidsabata.at.postareg.immandbeta120803.locator.DatabaseTableItemPos;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;

public class MapScanActivity extends Activity implements OnTouchListener {

	protected static final double MIN_ZOOM = 0.5f;
	protected static final float MAX_ZOOM = 10f;

	// map of floors
	private final List<Integer> floorsId = new ArrayList<Integer>();
	private final List<ImageView> crossesInMap = new ArrayList<ImageView>();
	private ImageView activeFloor;
	private int imgActiveFloor;

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
	private float dx = 0;
	private float dy = 0;
	private float mPosX = 0;
	private float mPosY = 0;
	private int mActivePointerId;
	private static final int INVALID_POINTER_ID = -1;

	private final int mActiveFloorI = 0;


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

		map = new MapCoordinatesWorker(this, (RelativeLayout) findViewById(R.id.floorMap), R.drawable.ic_launcher, 720, 1000);
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
				//update scale factor in mapper
				map.setScaleFactor(scaleFactor);
				map.setPosX(mPosX);
				map.setPosY(mPosY);
				panObjectsWithMap();
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
			crossesInMap.add(map.addCrossToMap(pos.posx, pos.posy, R.drawable.point_pick, imgActiveFloor));
		}


	}

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
				dx = x - lastX;
				dy = y - lastY;

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

				panObjectsWithMap();

				//update scale factor in mapper
				map.setScaleFactor(scaleFactor);
				map.setPosX(mPosX);
				map.setPosY(mPosY);
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
	 * Actualize objects on map
	 */
	private void panObjectsWithMap() {
		for (ImageView img : crossesInMap) {
			if (((Integer) img.getTag(R.id.imgFloorIndex)) == imgActiveFloor) {
				Matrix tmpMatrix = new Matrix();
				tmpMatrix.set(mtrx);
				tmpMatrix.postTranslate((Float) (img.getTag(R.id.idWidth)) * scaleFactor, (Float) (img.getTag(R.id.idHeight)) * scaleFactor);
				img.setImageMatrix(tmpMatrix);
				img.setVisibility(View.VISIBLE);
			} else {
				img.setVisibility(View.INVISIBLE);
			}
		}
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
				imgActiveFloor = progress / 25;
				activeFloor.setImageResource(floorsId.get(imgActiveFloor));
				panObjectsWithMap();
			}
		});
	}




	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent) Getting coordinates on the screen
	 */

	@Override
	protected void onStop() {
		GameService.getInstance().savePositionsToSd();

		super.onStop();
	}

	public boolean onTouch(View v, MotionEvent event) {

		//add cross to map on position 250px* 250px -> in original image

		RealCoordinates coordReal = map.getRealFromRelativeCoord(Math.round(event.getX(0)), Math.round(event.getY(0)));

		String res = GameService.getInstance().logPosition(coordReal.getX(), coordReal.getY(), imgActiveFloor);
		if (res != "None") {
			Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();

			// example of adding image to RelativeFramework
			crossesInMap.add(map.addCrossToMap(coordReal.getX(), coordReal.getY(), R.drawable.point_pick, imgActiveFloor));
			panObjectsWithMap();
		}


		return false;
	}
}
