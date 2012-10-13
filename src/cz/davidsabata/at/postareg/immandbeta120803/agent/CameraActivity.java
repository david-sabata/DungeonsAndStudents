package cz.davidsabata.at.postareg.immandbeta120803.agent;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.arlab.callbacks.ARmatcherImageCallBack;
import com.arlab.imagerecognition.ARmatcher;

import cz.davidsabata.at.postareg.immandbeta120803.R;

public class CameraActivity extends Activity implements ARmatcherImageCallBack {

	/** The matcher instance */
	ARmatcher aRmatcher;

	int screenheight;
	int screenwidth;

	/** Array that holds added images id in the matching pool */
	private final ArrayList<Integer> imageIdHolder = new ArrayList<Integer>();
	private static final String TAG = "HELLO";


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


		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.achiev_shock);
		int imagePool_Id = aRmatcher.addImage(bmp);
		if (imagePool_Id != -1) {
			imageIdHolder.add(imagePool_Id);
			Log.i(TAG, "image added to the pool with id: " + imagePool_Id);
		} else {
			Log.i(TAG, "image not added to the pool");
		}
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





	public void onImageRecognitionResult(int res) {
		if (res != -1) {
			// Found images with certain id
			Log.i("HELLO", "Image Recognized:" + res);

			String resourceName = getResources().getResourceName(res);
			Toast.makeText(getApplicationContext(), "Hit on " + resourceName, Toast.LENGTH_LONG).show();
		} else {
			// Nothing was found
		}
	}

}
