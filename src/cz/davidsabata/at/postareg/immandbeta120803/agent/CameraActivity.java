package cz.davidsabata.at.postareg.immandbeta120803.agent;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.Toast;

import com.arlab.callbacks.ARmatcherImageCallBack;
import com.arlab.imagerecognition.ARmatcher;

import cz.davidsabata.at.postareg.immandbeta120803.R;
import cz.davidsabata.at.postareg.immandbeta120803.missions.BaseMission;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;

public class CameraActivity extends Activity implements ARmatcherImageCallBack {

	private static final String LOG_TAG = "AgentCameraActivity";

	/** The matcher instance */
	ARmatcher aRmatcher;

	int screenheight;
	int screenwidth;


	/**
	 * Map of pool ids to res ids
	 */
	private final SparseIntArray imageIds = new SparseIntArray();



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/** Get full screen size */
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screenheight = displaymetrics.heightPixels;
		screenwidth = displaymetrics.widthPixels;

		/** Create an instance of the ARmatcher object. */
		aRmatcher = new ARmatcher(this, "5dSmy0JrgAaUN/WTR0i9/3caEseNJ3VvZ6OtbcoSJ4g1Uks19URUhqhKOGPbQYM=", ARmatcher.SCREEN_ORIENTATION_PORTRAIT, screenwidth, screenheight, true);

		/** Add camera view instance to content view */
		setContentView(aRmatcher.getCameraViewInstance());

		/** Set image and QR matching callbacks */
		aRmatcher.setImageRecognitionCallback(this);

		/** Set the type of the matching. */
		aRmatcher.setMatchingType(ARmatcher.IMAGE_MATCHER);

		/**
		 * Enable median filter ,which helps to reduce noise and mismatches in
		 * IMAGE matching .(Optional)
		 */
		aRmatcher.enableMedianFilter(true);

		/**
		 * Set minimum image quality threshold, for image to be accepted in the
		 * image pool (0 - 10)
		 */
		aRmatcher.setImageQuality(0);



		/**
		 * Load images on another thread
		 */
		new Thread(new Runnable() {
			public void run() {
				BaseMission mission = GameService.getInstance().getCurrentMission();
				List<Integer> ress = new ArrayList<Integer>();
				ress.add(mission.getImageResId());
				ress.addAll(GameService.getAchievmentsResIds());

				for (int resId : ress) {
					Bitmap bmp = BitmapFactory.decodeResource(getResources(), resId);
					int imagePool_Id = aRmatcher.addImage(bmp);
					if (imagePool_Id != -1) {
						imageIds.put(imagePool_Id, mission.getImageResId());
					} else {
						Log.w(LOG_TAG, getResources().getResourceName(resId) + " image not added to the pool");
					}
				}
			}
		}).start();


	}


	@Override
	protected void onResume() {
		super.onResume();
		/** Start matching */
		aRmatcher.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		/** Stop matching */
		aRmatcher.stop();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();

		try {
			/** Empty image matching pool */
			aRmatcher.releaseResources();
		} catch (Exception e) {
		}
		System.gc();
	}





	public void onImageRecognitionResult(int poolId) {
		if (poolId != -1) {
			int resId = imageIds.get(poolId);

			BaseMission mission = GameService.getInstance().getCurrentMission();
			if (mission.getImageResId() == resId) {
				GameService.getInstance().setMissionCompleted();
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.mission_acomplished), Toast.LENGTH_LONG).show();

				finish(); // suicide
			} else {
				// achievment?
			}

		} else {
			// Nothing was found
		}
	}

}
