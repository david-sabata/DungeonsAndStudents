package cz.davidsabata.at.postareg.immandbeta120803.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import android.util.Log;

import com.google.gson.Gson;

public class ClientThread extends Thread {

	Socket client;


	public ClientThread(Socket socket) {
		client = socket;
		Log.d("CLIENT", client.toString());
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
		Message msg = g.fromJson(line, Message.class);
		Log.d("LOADED MSG", msg.toString());
	}

}
