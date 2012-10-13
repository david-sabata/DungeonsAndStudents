package cz.davidsabata.at.postareg.immandbeta120803.locator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class WifiLogger {

	List<LocationInfo> mLocInfoList;
	Wifi wifi;
	DatabaseHandler db;

	public WifiLogger(Wifi wifi, DatabaseHandler db) {
		this.wifi = wifi;
		this.db = db;
		mLocInfoList = new ArrayList<LocationInfo>();
	}

	public List<LocationInfo> getLocationInfo() {
		return mLocInfoList;
	}

	public String Log(int x, int y, int floor) {

		List<WifiInfo> wi = wifi.getDetectedNetworks();
		if (wi.isEmpty())
			return "None";

		LocationInfo li = new LocationInfo(x, y, floor, wi);
		mLocInfoList.add(li);

		db.InsertLocation(li);

		return li.toString();
	}

	public void serializeToSDcard(String filename) {
		serializeToSDcardStatic(filename, mLocInfoList);
	}

	public static void serializeToSDcardStatic(String filename, List<LocationInfo> liList) {
		String path = Environment.getExternalStorageDirectory() + "/" + filename;

		try {
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream outStream = new ObjectOutputStream(fileOut);
			outStream.writeObject(liList);
			outStream.flush();
			outStream.close();
			fileOut.close();

		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void deserializeFromSDcard(String filename) {
		String path = Environment.getExternalStorageDirectory() + "/" + filename;
		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Object obj = in.readObject();
			if (obj instanceof List<?>)
				mLocInfoList = (List<LocationInfo>) obj;
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			Log.e("WifiLogger", "Employee class not found.");
			c.printStackTrace();
		}
	}

	public void serializeToSDcardJson(String filename, boolean nice) {
		serializeToSDcardJsonStatic(filename, mLocInfoList, nice);
	}

	public static void serializeToSDcardJsonStatic(String filename, List<LocationInfo> liList, boolean nice) {
		String path = Environment.getExternalStorageDirectory() + "/" + filename;

		try {
			FileOutputStream fileOut = new FileOutputStream(path);
			OutputStreamWriter outStream = new OutputStreamWriter(fileOut);

			Gson gson = null;
			if (nice)
				gson = new GsonBuilder().setPrettyPrinting().create();
			else
				gson = new Gson();

			outStream.write(gson.toJson(liList));

			outStream.flush();
			outStream.close();
			fileOut.close();

		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	public void deserializeFromSDcardJson(String filename) {
		mLocInfoList = deserializeFromSDcardJsonStatic(filename);
	}

	public static List<LocationInfo> deserializeFromSDcardJsonStatic(String filename) {
		String path = Environment.getExternalStorageDirectory() + "/" + filename;

		String content = null;
		try {
			content = new Scanner(new File(path)).useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Gson gson = new Gson();
		Type collectionType = new TypeToken<List<LocationInfo>>() {
		}.getType();
		return gson.fromJson(content, collectionType);
	}
}
