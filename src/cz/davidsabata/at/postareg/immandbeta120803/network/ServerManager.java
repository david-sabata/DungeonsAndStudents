package cz.davidsabata.at.postareg.immandbeta120803.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;


public class ServerManager {

	public static final int SERVER_PORT = 25436;

	Server server;

	public void StartServer(Listener listener) {

		ServerSocket server;

		try {
			server = new ServerSocket(SERVER_PORT);

			while (true) {
				Socket client = server.accept();
				Log.d("client connected", client.toString());

				ClientThread th = new ClientThread(client);
				th.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}



		//		server = new Server();
		//
		//
		//		Log.d("ServerManager", "Server bound?");
		//		server.addListener(new Listener() {
		//			public void received(Connection connection, Object object) {
		//				Log.d("serverListener", "incoming!");
		//			};
		//
		//			public void disconnected(Connection c) {
		//				Log.d("serverListener", "disconnected");
		//			};
		//		});
		//
		//
		//		try {
		//			server.bind(SERVER_PORT);
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		server.start();


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
}
