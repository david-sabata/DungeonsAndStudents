package cz.davidsabata.at.postareg.immandbeta120803.network;

public class Message {

	public String playerMac;

	public String playerRole;

	public String playerIp;

	public int lastX;
	public int lastY;

	public String status;

	@Override
	public String toString() {
		return "Message [playerMac=" + playerMac + ", playerRole=" + playerRole + ", playerIp=" + playerIp + ", lastX=" + lastX + ", lastY=" + lastY + ", status=" + status + "]";
	}




}
