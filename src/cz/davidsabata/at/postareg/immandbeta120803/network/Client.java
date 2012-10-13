package cz.davidsabata.at.postareg.immandbeta120803.network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;

public class Client {
	public static final int SERVER_PORT = 25437;

	Socket socket;
	String ip;

	public Client(String ip) {
		this.ip = ip;
	}

	public void send() {
		try {
			InetAddress address = InetAddress.getByName(ip);
			socket = new Socket(address, SERVER_PORT);
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		OutputStream socketOutputStream = null;
		try {
			socketOutputStream = socket.getOutputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Message msg = new Message();
		msg.lastX = 4;
		msg.playerMac = "ggg";
		msg.playerIp = ip;

		Gson gson = new Gson();


		try {
			socketOutputStream.write((gson.toJson(msg) + "\n").getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
