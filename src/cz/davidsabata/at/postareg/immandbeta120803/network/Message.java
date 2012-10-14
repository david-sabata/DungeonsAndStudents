package cz.davidsabata.at.postareg.immandbeta120803.network;

public class Message {

	public enum Type {
		PREPARING, INGAME, GUARD_WON, AGENT_WON, QUIT
	};

	public enum Role {
		AGENT, GUARD
	};

	public Type type;

	public String playerMac;

	public Role playerRole;

	public String nickname;

	public int lastX;
	public int lastY;


}
