package cz.davidsabata.at.postareg.immandbeta120803.network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;



public class ServerManager {

	public static final int SERVER_PORT = 25437;

	public List<ClientThread> clThreads = new ArrayList<ClientThread>();

	public void StartServer() {

		ServerSocket server;

		try {
			server = new ServerSocket(SERVER_PORT);
			Log.d("server", "starting");

			while (true) {
				Socket client = server.accept();
				Log.d("client connected", client.toString());

				ClientThread th = new ClientThread(client, this);
				th.start();
				clThreads.add(th);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}


	}



	public void sendMessage(Message msg, Socket except) {
		synchronized (clThreads) {
			try {
				for (ClientThread cl : clThreads) {
					if (cl.client.equals(except)) {
						continue;
					}

					OutputStream os = cl.client.getOutputStream();
					Gson g = new Gson();
					String str = g.toJson(msg) + "\n";
					os.write(str.getBytes());
					Log.d("ServerManager", "Msg sent");
				}
			} catch (IOException e) {
				Log.e("ServerManager", "Error while sending message");
				e.printStackTrace();
			}
		}
	}



	public void sendMessageSingle(Message msg, Socket onlyClient) {
		synchronized (clThreads) {
			try {
				for (ClientThread cl : clThreads) {
					if (!cl.client.equals(onlyClient)) {
						continue;
					}

					OutputStream os = cl.client.getOutputStream();
					Gson g = new Gson();
					String str = g.toJson(msg) + "\n";
					os.write(str.getBytes());
					Log.d("ServerManager", "Msg sent");
				}
			} catch (IOException e) {
				Log.e("ServerManager", "Error while sending message");
				e.printStackTrace();
			}
		}
	}

}
