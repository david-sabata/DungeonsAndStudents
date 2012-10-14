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

public class Client {
	public static final int SERVER_PORT = 25437;

	Socket socket;
	String ip;

	public Client(String ip) {
		this.ip = ip;
	}

	public void Connect() {
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
	}

	private void OnMessageReceived(Message m) {

		switch (m.type) {

		case AGENT_WON:
			break;

		case GUARD_WON:
			break;

		case INGAME:
			break;

		case PREPARING:
			break;

		case QUIT:
			break;

		default:
			break;
		}
	}

	public void Send(Message m) {

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
