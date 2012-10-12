package cz.davidsabata.at.postareg.immandbeta120803.services;

import cz.davidsabata.at.postareg.immandbeta120803.R;

public class Player {

	public static enum Role {
		AGENT, GUARD
	};


	public String macAddr;

	public String nickname;

	public int lastKnownX;
	public int lastKnownY;

	public Role role = Role.GUARD;



	/**
	 * @return resource id
	 */
	public int getRoleIcon() {
		return (role == Role.GUARD) ? R.drawable.role_guard : R.drawable.role_agent;
	}

}
