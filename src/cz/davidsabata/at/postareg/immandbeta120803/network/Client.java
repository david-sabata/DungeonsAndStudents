package cz.davidsabata.at.postareg.immandbeta120803.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import com.google.gson.Gson;

import cz.davidsabata.at.postareg.immandbeta120803.network.Message.Type;
import cz.davidsabata.at.postareg.immandbeta120803.services.GameService;
import cz.davidsabata.at.postareg.immandbeta120803.services.Player;
import cz.davidsabata.at.postareg.immandbeta120803.services.Player.Role;

public class Client {
	public static final int SERVER_PORT = 25437;

	private final GameService gameService = GameService.getInstance();

	Socket socket = null;
	String ip;

	public Client(String ip) {
		this.ip = ip;
	}

	public boolean Connect() {
		InetAddress address = null;
		try {
			address = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		try {
			socket = new Socket(address, SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						StringBuilder total = new StringBuilder();
						String line;
						while ((line = r.readLine()) != null) {
							total.append(line);
							Log.d("Client: line from server received:", line);

							Gson gson = new Gson();
							OnMessageReceived(gson.fromJson(line, Message.class));
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		return true;
	}

	private void OnMessageReceived(Message msg) {

		switch (msg.type) {

		case AGENT_WON:
			break;

		case GUARD_WON:
			break;

		case INGAME:
			break;

		case PREPARING:
			Player p = new Player();
			p.isHost = false;
			p.role = msg.playerRole == Message.Role.AGENT ? Role.AGENT : Role.GUARD;
			p.nickname = msg.nickname;
			p.macAddr = msg.playerMac;
			p.lastKnownX = msg.lastX;
			p.lastKnownY = msg.lastY;

			// info o hraci pri skladani lidi na hru
			if (gameService.getGameState() == cz.davidsabata.at.postareg.immandbeta120803.services.GameInfo.State.WAITING_FOR_CONNECTION && msg.type == Type.PREPARING)
				gameService.addPlayer(p);

			break;

		case QUIT:
			break;

		default:
			break;
		}
	}

	public void Send(Message m) {

		if (socket == null)
			return;

		OutputStream socketOutputStream = null;
		try {
			socketOutputStream = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Gson gson = new Gson();
		String json = gson.toJson(m, Message.class);

		try {
			socketOutputStream.write((json + "\n").getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.d("Client sent to server", json);
	}

	public void Close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
