package cz.davidsabata.at.postareg.immandbeta120803.services;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import cz.davidsabata.at.postareg.immandbeta120803.exceptions.InvalidGameStateException;
import cz.davidsabata.at.postareg.immandbeta120803.locator.Wifi;
import cz.davidsabata.at.postareg.immandbeta120803.locator.WifiLogger;
import cz.davidsabata.at.postareg.immandbeta120803.missions.BaseMission;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameInfo.State;
import cz.davidsabata.at.postareg.immandbeta120803.services.Player.Role;

public class GameService extends Service {

	private static final String LOG_TAG = "GameService";


	/** Instance of self */
	private static GameService mInstance;



	// ---------------------------------------------------------------------------------

	/**
	 * Instance getter
	 */
	public static GameService getInstance() {
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


	private GameInfo mGameInfo;


	/**
	 * Existuje objekt hry?
	 */
	public boolean isThereAGame() {
		return mGameInfo != null;
	}

	/**
	 * Stav hry nebo null pokud hra neni
	 */
	public GameInfo.State getGameState() {
		if (isThereAGame())
			return mGameInfo.getState();
		else
			return null;
	}

	/**
	 * Zalozit novou hru
	 */
	public void hostNewGame() {
		if (isThereAGame())
			throw new InvalidGameStateException("Cannot start new game. Other game already in progress");

		mGameInfo = new GameInfo();

		Player p = new Player();
		p.nickname = "SerialKiller" + Math.round(Math.random() * 1000);
		p.macAddr = "BABA666";
		mGameInfo.addPlayer(p);

		Player p2 = new Player();
		p2.nickname = "Butcher" + Math.round(Math.random() * 1000);
		p2.macAddr = "AAAA";
		mGameInfo.addPlayer(p2);

		Player p3 = new Player();
		p3.nickname = "Sneaky" + Math.round(Math.random() * 1000);
		p3.macAddr = "BBBB";
		p3.role = Role.AGENT;
		mGameInfo.addPlayer(p3);

		Player p4 = new Player();
		p4.nickname = "Jughead" + Math.round(Math.random() * 1000);
		p4.macAddr = "CCCC";
		p4.role = Role.GUARD;
		mGameInfo.addPlayer(p4);
	}

	/**
	 * Seznam hracu
	 */
	public List<Player> getPlayers() {
		return mGameInfo.getPlayers();
	}


	/**
	 * Vraci resourceID chybove hlasky anebo -1 pokud je vse v poradku
	 */
	public int checkPlayers() {
		if (!isThereAGame())
			throw new InvalidGameStateException("Cannot check players, no game in progress");

		return mGameInfo.checkPlayers();
	}


	public void startGame() {
		if (mGameInfo.getState() == State.WAITING_FOR_CONNECTION) {
			mGameInfo.startGame();
		} else {
			throw new InvalidGameStateException("Cannot start the game in state " + mGameInfo.getState().toString());
		}
	}

	public BaseMission getCurrentMission() {
		return mGameInfo.getCurrentMisssion();
	}


	// ---------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------


	protected Wifi wifi;
	protected WifiLogger wifiLogger;

	public void init(Wifi wifi) {
		this.wifi = wifi;
		wifiLogger = new WifiLogger(wifi);
	}


	public String logPosition(int x, int y, int floor) {
		return wifiLogger.Log(x, y, floor);
	}



	// ---------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------



	// Bound Service
	// ------------------------------------------------------------------

	public class GameServiceBinder extends Binder {
		public GameService getService() {
			Log.d("GameServiceBinder", "getService");
			return GameService.this;
		}
	};

	private final IBinder mBinder = new GameServiceBinder();


	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(LOG_TAG, "onBind");

		return mBinder;
	}


	// --------------------------------------------------------------------------------



}