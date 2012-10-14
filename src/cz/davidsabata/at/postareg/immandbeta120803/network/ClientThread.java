package cz.davidsabata.at.postareg.immandbeta120803.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import cz.davidsabata.at.postareg.immandbeta120803.network.Message.Type;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameInfo;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;
import cz.davidsabata.at.postareg.immandbeta120803.services.Player;
import cz.davidsabata.at.postareg.immandbeta120803.services.Player.Role;

public class ClientThread extends Thread {

	public Socket client;

	private final GameService gameService;

	private final ServerManager serverManager;


	public ClientThread(Socket socket, ServerManager manager) {
		gameService = GameService.getInstance();

		serverManager = manager;

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

			Player p = new Player(msg);

			// info o hraci pri skladani lidi na hru
			if (gameService.getGameState() == GameInfo.State.WAITING_FOR_CONNECTION && msg.type == Type.PREPARING) {
				boolean isNew = gameService.addPlayer(p);

				// rozeslat info ostatnim
				serverManager.sendMessage(msg, client);

				// rozeslat info zdroji o ostatnich
				for (Player pp : gameService.getPlayers()) {
					if (!pp.equals(pp)) {
						Message newMsg = new Message();
						newMsg.type = Type.PREPARING;
						newMsg.nickname = pp.nickname;
						newMsg.playerMac = pp.macAddr;
						newMsg.playerRole = pp.role == Role.AGENT ? Message.Role.AGENT : Message.Role.GUARD;
						newMsg.lastX = pp.lastKnownX;
						newMsg.lastY = pp.lastKnownY;

						serverManager.sendMessageSingle(newMsg, client);
					}
				}

				return;
			}

			// hra konci
			if (gameService.getGameState() == GameInfo.State.CHASING) {
				if (msg.type == Type.AGENT_WON) {
					gameService.setMissionCompleted();
				}
				if (msg.type == Type.GUARD_WON) {
					gameService.setMissionFailed();
				}
			}

		} catch (JsonSyntaxException e) {
			Log.d("ClientThread-Error", e.toString());
		}
	}



}
