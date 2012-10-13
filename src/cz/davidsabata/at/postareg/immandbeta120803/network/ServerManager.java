package cz.davidsabata.at.postareg.immandbeta120803.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;



public class ServerManager {

	public static final int SERVER_PORT = 25437;


	public void StartServer() {

		ServerSocket server;

		try {
			server = new ServerSocket(SERVER_PORT);
			Log.d("server", "starting");

			while (true) {
				Socket client = server.accept();
				Log.d("client connected", client.toString());

				ClientThread th = new ClientThread(client);
				th.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

}
