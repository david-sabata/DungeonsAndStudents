package cz.davidsabata.at.postareg.immandbeta120803.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class NetworkService extends Service {

	private static final String LOG_TAG = "NetworkService";


	/** Instance of self */
	private static NetworkService mInstance;



	// ---------------------------------------------------------------------------------

	/**
	 * Instance getter
	 */
	public static NetworkService getInstance() {
		return mInstance;
	}


	/**
	 * Service has just been created
	 */
	@Override
	public void onCreate() {
		Log.d(LOG_TAG, "service created");

		mInstance = this;
	}


	/**
	 * Service is shutting down
	 */
	@Override
	public void onDestroy() {
		mInstance = null;

		Log.d(LOG_TAG, "service destroyed");
	}


	@Override
	public boolean onUnbind(Intent intent) {
		mInstance = null;

		Log.d(LOG_TAG, "service unbound");

		return false;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		Log.d(LOG_TAG, "onStartCommand");

		return START_STICKY;
	}


	// --------------------------------------------------------
	// --------------------------------------------------------





	// ---------------------------------------------------------------------------------



	// Bound Service
	// ------------------------------------------------------------------

	public class ModelServiceBinder extends Binder {
		public NetworkService getService() {
			Log.d("NetworkServiceBinder", "getService");
			return NetworkService.this;
		}
	};

	private final IBinder mBinder = new ModelServiceBinder();


	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(LOG_TAG, "onBind");

		return mBinder;
	}


	// --------------------------------------------------------------------------------



}