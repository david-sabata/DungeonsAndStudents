package cz.davidsabata.at.postareg.immandbeta120803.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import cz.davidsabata.at.postareg.immandbeta120803.network.Message.Type;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;
import cz.davidsabata.at.postareg.immandbeta120803.services.Player;
import cz.davidsabata.at.postareg.immandbeta120803.services.Player.Role;

public class ClientThread extends Thread {

	public Socket client;

	private final GameService gameService;

	private final List<ClientThread> clThreads;


	public ClientThread(Socket socket, List<ClientThread> threads) {
		gameService = GameService.getInstance();

		clThreads = threads;

		client = socket;
		Log.d("ClientThread", client.toString());
	}


	@Override
	public void run() {
		super.run();

		BufferedReader r;
		try {
			r = new BufferedReader(new InputStreamReader(client.getInputStream()));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
				parseLine(line);
				Log.d("LINE", line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.d("CLIENT", "client thread ends | " + client.toString());
	}



	public void parseLine(String line) {
		Gson g = new Gson();
		try {
			Message msg = g.fromJson(line, Message.class);
			Log.d("ClientThread", msg.toString());

			Player p = new Player();
			p.isHost = false;
			p.role = msg.playerRole == "AGENT" ? Role.AGENT : Role.GUARD;
			p.nickname = msg.nickname;
			p.macAddr = msg.playerMac;
			p.lastKnownX = msg.lastX;
			p.lastKnownY = msg.lastY;

			// info o hraci pri skladani lidi na hru
			if (gameService.getGameState() == cz.davidsabata.at.postareg.immandbeta120803.services.GameInfo.State.WAITING_FOR_CONNECTION && msg.type == Type.PREPARING) {
				gameService.addPlayer(p);

				// rozeslat info
				sendMessage(msg, client);

				return;
			}




		} catch (JsonSyntaxException e) {
			Log.d("ClientThread-Error", e.toString());
		}
	}


	private void sendMessage(Message msg, Socket except) {
		synchronized (clThreads) {
			try {
				for (ClientThread cl : clThreads) {
					if (cl.client.equals(except)) {
						continue;
					}

					OutputStream os = cl.client.getOutputStream();
					Gson g = new Gson();
					String str = g.toJson(msg);
					os.write(str.getBytes());
					Log.d("ClientThread", "Msg sent");
				}
			} catch (IOException e) {
				Log.e("ClientThread", "Error while sending message");
				e.printStackTrace();
			}
		}
	}

}
