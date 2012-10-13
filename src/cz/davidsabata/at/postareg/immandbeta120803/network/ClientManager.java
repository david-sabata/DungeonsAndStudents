package cz.davidsabata.at.postareg.immandbeta120803.network;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;

public class ClientManager {

	public static final int SERVER_PORT = 25436;
	public static final int TIMEOUT = 5000;
	Client client = null;

	public void Connect(Listener clientListener, String serverIP) {
		client = new Client();
		try {
			client.connect(TIMEOUT, serverIP, SERVER_PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		client.addListener(clientListener);
	}

	public void Send(Object obj) {
		client.sendTCP(obj);
	}

	public void Close() {
		client.close();
	}
}
