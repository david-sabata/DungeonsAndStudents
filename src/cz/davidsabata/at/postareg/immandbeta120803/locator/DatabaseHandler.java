package cz.davidsabata.at.postareg.immandbeta120803.locator;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.SparseArray;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "WifiManager";

	// private static final String TABLE_NAME = "WifiRecords";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_POS_TABLE = "CREATE TABLE PosRecords (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "posx INTEGER, " + "posy INTEGER, " + "floor INTEGER )";
		db.execSQL(CREATE_POS_TABLE);

		String CREATE_AP_TABLE = "CREATE TABLE APRecords (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "posId INTEGER, " + "mac TEXT, " + "ssid TEXT, " + "dbm INTEGER )";
		db.execSQL(CREATE_AP_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS PosRecords");
		db.execSQL("DROP TABLE IF EXISTS APRecords");
		onCreate(db);
	}

	public void InsertLocations(List<LocationInfo> locs) {
		SQLiteDatabase db = this.getWritableDatabase();

		for (LocationInfo li : locs) {

			if (li.wifiInfo.isEmpty())
				continue;

			ContentValues posVals = new ContentValues();
			posVals.put("posx", li.x);
			posVals.put("posy", li.y);
			posVals.put("floor", li.floor);

			long fk = db.insert("PosRecords", null, posVals);

			for (WifiInfo wi : li.wifiInfo) {

				ContentValues apVals = new ContentValues();
				apVals.put("posId", fk);
				apVals.put("mac", wi.bssid);
				apVals.put("ssid", wi.ssid);
				apVals.put("dbm", wi.dbm);

				db.insert("APRecords", null, apVals);
			}
		}
	}

	public DatabaseTableItemPos getBestMatchingPos(List<WifiInfo> wifiInfoList) {
		SQLiteDatabase db = this.getWritableDatabase();

		SparseArray<List<DbItem>> data = new SparseArray<List<DbItem>>();

		List<DbItem> myData = new ArrayList<DbItem>();

		for (WifiInfo wi : wifiInfoList) {

			DbItem myItem = new DbItem(-1, wi.dbm, wi.bssid);
			myData.add(myItem);

			// String selectQuery = "SELECT * FROM APRecords WHERE mac = \"" +
			// wi.bssid + "\" AND dbm BETWEEN " + (wi.dbm - 10) + " AND " +
			// (wi.dbm + 10) + "";

			String selectQuery = "SELECT * FROM APRecords WHERE mac = \"" + wi.bssid + "\" ORDER BY ABS(" + wi.dbm + "-dbm) ASC LIMIT 5";

			Cursor cur = db.rawQuery(selectQuery, null);

			if (cur.moveToFirst()) {
				do {
					int posId = Integer.parseInt(cur.getString(1));
					int dbm = Integer.parseInt(cur.getString(4));
					String mac = cur.getString(2);

					DbItem item = new DbItem(posId, dbm, mac);

					if (data.get(posId) == null) {
						List<DbItem> newarr = new ArrayList<DbItem>();
						newarr.add(item);
						data.put(posId, newarr);
					} else {
						data.get(posId).add(item);
					}
				} while (cur.moveToNext());
			}
		}


		for (int i = 0; i < data.size(); i++) {
			Log.d("ID", "" + data.keyAt(i));
		}

		int bestMatch = 99999;
		int bestId = -1;

		for (int i = 0; i < data.size(); i++) {
			int key = data.keyAt(i);
			List<DbItem> items = data.get(key);

			int rating = ratePositions(myData, items);
			if (rating < bestMatch) {
				bestMatch = rating;
				bestId = key;
			}
		}


		if (bestId == -1) {
			throw new RuntimeException("no best id");
		}


		Log.d("BEST ID", "" + bestId);

		DatabaseTableItemPos ret = new DatabaseTableItemPos();


		String selectQuery = "SELECT * FROM PosRecords WHERE id = \"" + bestId + "\" LIMIT 1";
		Cursor cur = db.rawQuery(selectQuery, null);
		if (cur.moveToFirst()) {
			do {
				ret.id = bestId;
				ret.posx = Integer.parseInt(cur.getString(1));
				ret.posy = Integer.parseInt(cur.getString(2));
				ret.floor = Integer.parseInt(cur.getString(3));
			} while (cur.moveToNext());
		}


		return ret;
	}

	protected int ratePositions(List<DbItem> my, List<DbItem> ref) {

		int rate = 0;

		for (DbItem me : my) {

			// najdeme zaznam o odpovidajici mac
			DbItem refItem = null;
			for (DbItem r : ref) {
				if (r.mac.equals(me.mac)) {
					refItem = r;
					break;
				}
			}

			// jestli neni tak ignorujeme
			if (refItem == null)
				continue;

			int subRate = (int) Math.round(Math.pow((me.dbm - refItem.dbm), 2));
			rate += subRate;
		}

		return rate;
	}




	protected class DbItem {
		public int posId;
		public int dbm;
		public String mac;

		public DbItem(int posId, int dbm, String mac) {
			this.posId = posId;
			this.dbm = dbm;
			this.mac = mac;
		}

	}




}
