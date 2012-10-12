package cz.davidsabata.at.postareg.immandbeta120803.services;

import java.util.ArrayList;
import java.util.List;

public class GameInfo {


	public static enum State {
		WAITING_FOR_CONNECTION, PLAYERS_READY, CHASING, ROUND_ENDED
	};


	private final State mState = State.WAITING_FOR_CONNECTION;



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

}
