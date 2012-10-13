package cz.davidsabata.at.postareg.immandbeta120803.locator;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

		for (WifiInfo wi : wifiInfoList) {

			// String selectQuery = "SELECT * FROM APRecords WHERE mac = \"" +
			// wi.bssid + "\" AND dbm BETWEEN " + (wi.dbm - 10) + " AND " +
			// (wi.dbm + 10) + "";

			String selectQuery = "SELECT * FROM APRecords WHERE mac = \"" + wi.bssid + "\" ORDER BY ABS(" + wi.dbm + "-dbm) ASC LIMIT 5";

			Cursor cur = db.rawQuery(selectQuery, null);

			if (cur.moveToFirst()) {
				do {
					int posId = Integer.parseInt(cur.getString(1));
					int dbm = Integer.parseInt(cur.getString(4));




					// String


					// Log.i("db", cur.getString(2));

				} while (cur.moveToNext());
			}

			// wi.dbm;
			// wi.bssid;

		}


		DatabaseTableItemPos ret = new DatabaseTableItemPos();

		return ret;
	}


}
