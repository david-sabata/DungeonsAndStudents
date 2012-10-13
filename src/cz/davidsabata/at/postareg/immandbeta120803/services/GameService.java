package cz.davidsabata.at.postareg.immandbeta120803.services;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import cz.davidsabata.at.postareg.immandbeta120803.R;
import cz.davidsabata.at.postareg.immandbeta120803.exceptions.InvalidGameStateException;
import cz.davidsabata.at.postareg.immandbeta120803.locator.DatabaseHandler;
import cz.davidsabata.at.postareg.immandbeta120803.locator.DatabaseTableItemPos;
import cz.davidsabata.at.postareg.immandbeta120803.locator.Wifi;
import cz.davidsabata.at.postareg.immandbeta120803.locator.WifiLogger;
import cz.davidsabata.at.postareg.immandbeta120803.missions.BaseMission;
import cz.davidsabata.at.postareg.immandbeta120803.missions.Mission667;
import cz.davidsabata.at.postareg.immandbeta120803.missions.Mission668;
import cz.davidsabata.at.postareg.immandbeta120803.missions.ShockMission;
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
	 * Zalozit novou hru a pridat sebe jako hrace
	 */
	public void hostNewGame() {
		if (isThereAGame())
			throw new InvalidGameStateException("Cannot start new game. Other game already in progress");

		mGameInfo = new GameInfo();
		mGameInfo.addPlayer(createSelfPlayer(true));
	}

	/**
	 * Seznam hracu
	 */
	public List<Player> getPlayers() {
		return mGameInfo.getPlayers();
	}

	/**
	 * Vytvori strukturu lokalniho hrace
	 */
	protected Player createSelfPlayer(boolean isHost) {
		Player p = new Player();
		p.role = Math.random() > 0.5 ? Role.AGENT : Role.GUARD;
		p.macAddr = wifi.getSelfMacAddress();
		p.nickname = Player.generateCoolNickname();
		p.isHost = isHost;

		return p;
	}

	/**
	 * Ziska lokalniho hrace z pole vsech hracu anebo null pokud neni
	 */
	public Player getLocalPlayer() {
		for (Player p : getPlayers()) {
			if (p.macAddr.equals(wifi.getSelfMacAddress())) {
				return p;
			}
		}

		return null;
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

	/**
	 * Vrati objekty vsech dostupnych misi
	 */
	public static List<BaseMission> getAllMissions() {
		List<BaseMission> l = new ArrayList<BaseMission>();

		l.add(new ShockMission());
		l.add(new Mission667());
		l.add(new Mission668());

		return l;
	}

	/**
	 * Vraci list resource idcek achievmentu
	 */
	public static List<Integer> getAchievmentsResIds() {
		List<Integer> l = new ArrayList<Integer>();

		l.add(R.drawable.achiev_shock);

		return l;
	}


	// ---------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------


	protected Wifi wifi;
	protected WifiLogger wifiLogger;
	protected DatabaseHandler db;


	public void init(Wifi wifi, Context context) {
		db = new DatabaseHandler(context);

		this.wifi = wifi;
		wifiLogger = new WifiLogger(wifi, db);
	}


	public String logPosition(int x, int y, int floor) {
		return wifiLogger.Log(x, y, floor);
	}

	public List<DatabaseTableItemPos> getSavedPositions() {
		return db.getSavedPositions();
	}

	public void clearDatabase() {
		db.clearDatabase();
	}

	public List<DatabaseTableItemPos> getBestMatchingPos() {
		return db.getBestMatchingPos(wifi.getDetectedNetworks());
	}

	public void savePositionsToSd() {
		wifiLogger.serializeToSDcardJson("DungeonsAndStudentsWifi.txt", true);
	}

	// ---------------------------------------------------------------------------------
	// ---------------------------------------------------------------------------------





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