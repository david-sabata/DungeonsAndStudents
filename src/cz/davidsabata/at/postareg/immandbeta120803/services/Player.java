package cz.davidsabata.at.postareg.immandbeta120803.services;

import java.util.Random;

import cz.davidsabata.at.postareg.immandbeta120803.R;

public class Player {

	public static enum Role {
		AGENT, GUARD
	};


	public String macAddr;

	public String nickname;

	public boolean isHost;

	public int lastKnownX;
	public int lastKnownY;

	public Role role = Role.GUARD;



	/**
	 * @return resource id
	 */
	public int getRoleIcon() {
		return (role == Role.GUARD) ? R.drawable.role_guard : R.drawable.role_agent;
	}


	/** 
	 * Vygeneruje ubercool nickname 
	 */
	public static String generateCoolNickname() {
		Random r = new Random();
		String[] preps = { "Wealthy", "Sleepy", "Pink", "Mighty", "Mrs", "Saint", "Frightened" };
		String[] nicks = { "Jughead", "Bulldozer", "Sentinel", "SlimShady", "Bear", "Rocker", "Rimmer", "ChuckNorris", "BigBoy", "Wolowizard", "Sneaky", "Ironman", "Batguy" };
		return preps[r.nextInt(preps.length)] + "_" + nicks[r.nextInt(nicks.length)] + "_" + Math.round(Math.random() * 1000);
	}
}
