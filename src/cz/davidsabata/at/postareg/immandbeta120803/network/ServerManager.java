package cz.davidsabata.at.postareg.immandbeta120803.network;

import java.io.IOException;

import android.util.Log;

import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;


public class ServerManager {

	public static final int SERVER_PORT = 25437;

	Server server;

	public void StartServer(Listener listener) {

		server = new Server();
		server.start();
		try {
			server.bind(SERVER_PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("ServerManager", "Server bound?");
		server.addListener(listener);



		/*{
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof Player) {

				} else if (object instanceof GameInfo) {

				}
			}
		});*/
	}

	public void Broadcast(Object obj) {
		server.sendToAllTCP(obj);
	}

	public void Unicast() {
		//server.se
	}

	public void Close() {
		server.close();
	}
}
