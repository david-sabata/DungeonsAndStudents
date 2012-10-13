package cz.davidsabata.at.postareg.immandbeta120803.network;

public class Message {

	public enum Type {
		PREPARING, INGAME, GUARD_WON, AGENT_WON, QUIT
	};

	public Type type;

	public String playerMac;

	public String playerRole;

	public String nickname;

	public int lastX;
	public int lastY;


}
