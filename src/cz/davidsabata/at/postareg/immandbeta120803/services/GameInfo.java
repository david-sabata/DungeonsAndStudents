package cz.davidsabata.at.postareg.immandbeta120803.services;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import cz.davidsabata.at.postareg.immandbeta120803.R;
import cz.davidsabata.at.postareg.immandbeta120803.exceptions.InvalidPlayersException;
import cz.davidsabata.at.postareg.immandbeta120803.missions.BaseMission;
import cz.davidsabata.at.postareg.immandbeta120803.missions.ShockMission;
import cz.davidsabata.at.postareg.immandbeta120803.services.Player.Role;

public class GameInfo {

	private static final String LOG_TAG = "GameInfo";

	public static enum State {
		WAITING_FOR_CONNECTION, CHASING, ROUND_ENDED
	};


	private State mState = State.WAITING_FOR_CONNECTION;

	private BaseMission mCurrentMission;


	private final List<Player> mPlayers = new ArrayList<Player>();




	public State getState() {
		return mState;
	}


	/**
	 * Pridani hrace. Je povolen jen jeden hrac s unikatni MAC. Tim je mozne
	 * vicekrat pridat hrace se stejnou MAC pro dosazeni aktualizace udaju.
	 */
	public void addPlayer(Player p) {
		// drop any previous player with same MAC
		for (int i = 0; i < mPlayers.size(); i++) {
			if (mPlayers.get(i).macAddr.equals(p.macAddr)) {
				mPlayers.remove(i);
				break;
			}
		}

		mPlayers.add(p);
	}


	public List<Player> getPlayers() {
		return mPlayers;
	}



	/**
	 * Vraci resourceID chybove hlasky anebo -1 pokud je vse v poradku
	 */
	public int checkPlayers() {
		if (mPlayers.size() == 0)
			throw new InvalidPlayersException("No players");

		boolean haveAgent = false;

		for (Player p : mPlayers) {
			if (p.role == Role.AGENT && haveAgent) {
				return R.string.too_many_agents;
			}
			if (p.role == Role.AGENT && !haveAgent) {
				haveAgent = true;
			}
		}

		if (!haveAgent) {
			return R.string.no_agent_in_game;
		}

		return -1;
	}


	/**
	 * Zacatek hry - nastavit state a vygenerovat quest
	 */
	public void startGame() {
		mState = State.CHASING;
		mCurrentMission = pickMission();
		Log.d(LOG_TAG, "Game started");
		Log.d(LOG_TAG, mCurrentMission.toString());
	}


	/**
	 * TODO: random
	 */
	public BaseMission pickMission() {
		return new ShockMission();
		//		return new Mission001();
	}


	public BaseMission getCurrentMisssion() {
		return mCurrentMission;
	}


}
