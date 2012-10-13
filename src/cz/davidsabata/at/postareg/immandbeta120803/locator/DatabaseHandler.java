package cz.davidsabata.at.postareg.immandbeta120803.locator;

import java.util.ArrayList;
import java.util.Collections;
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


	public void InsertLocations(List<LocationInfo> li) {
		for (LocationInfo l : li) {
			InsertLocation(l);
		}
	}

	public void InsertLocation(LocationInfo loc) {
		SQLiteDatabase db = this.getWritableDatabase();

		if (loc.wifiInfo.isEmpty())
			return;

		ContentValues posVals = new ContentValues();
		posVals.put("posx", loc.x);
		posVals.put("posy", loc.y);
		posVals.put("floor", loc.floor);

		long fk = db.insert("PosRecords", null, posVals);

		for (WifiInfo wi : loc.wifiInfo) {

			ContentValues apVals = new ContentValues();
			apVals.put("posId", fk);
			apVals.put("mac", wi.bssid);
			apVals.put("ssid", wi.ssid);
			apVals.put("dbm", wi.dbm);

			db.insert("APRecords", null, apVals);
		}

	}

	public List<LocationInfo> databaseToList() {
		SQLiteDatabase db = this.getWritableDatabase();

		List<LocationInfo> liList = new ArrayList<LocationInfo>();

		String selectQuery = "SELECT * FROM PosRecords";
		Cursor cur = db.rawQuery(selectQuery, null);
		if (cur.moveToFirst()) {
			do {
				//String query = "SELECT * FROM APRecords JOIN PosRecords ON PosRecords.id = APRecords.posId";
				LocationInfo li = new LocationInfo();
				li.x = cur.getInt(1);
				li.y = cur.getInt(2);
				li.floor = cur.getInt(3);

				li.wifiInfo = new ArrayList<WifiInfo>();


				String query = "SELECT * FROM APRecords WHERE posId = " + cur.getInt(0);
				Cursor cur2 = db.rawQuery(query, null);
				if (cur2.moveToFirst()) {
					do {
						li.wifiInfo.add(new WifiInfo(cur2.getString(3), cur2.getString(2), 0, cur2.getInt(4)));
					} while (cur2.moveToNext());
				}
				cur2.close();

				liList.add(li);

			} while (cur.moveToNext());
		}
		cur.close();

		return liList;
	}

	public List<DatabaseTableItemPos> getBestMatchingPos(List<WifiInfo> wifiInfoList) {
		SQLiteDatabase db = this.getWritableDatabase();

		SparseArray<List<DbItem>> data = new SparseArray<List<DbItem>>();

		List<DbItem> myData = new ArrayList<DbItem>();

		for (WifiInfo wi : wifiInfoList) {

			DbItem myItem = new DbItem(-1, wi.dbm, wi.bssid);
			myData.add(myItem);

			String selectQuery = "SELECT * FROM APRecords WHERE mac = \"" + wi.bssid + "\" ORDER BY ABS(" + wi.dbm + "-dbm) ASC LIMIT 10";

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

			cur.close();
		}




		List<DatabaseTableItemPos> ret = new ArrayList<DatabaseTableItemPos>();

		for (int i = 0; i < data.size(); i++) {
			int key = data.keyAt(i);
			List<DbItem> items = data.get(key);

			int rating = ratePositions(myData, items);
			if (rating < 99999) {
				DatabaseTableItemPos posItem = getPosItem(key);
				posItem.matchQuality = rating;
				ret.add(posItem);

				Log.d("RATING", key + " : " + rating);
			}
		}

		// sort
		Collections.sort(ret);

		for (int i = 0; i < ret.size(); i++) {
			Log.d("POSTSORT", i + " : " + ret.get(i).matchQuality);
		}

		// slice ret
		List<DatabaseTableItemPos> retCut = new ArrayList<DatabaseTableItemPos>();
		for (int i = 0; i < Math.min(2, ret.size()); i++)
			retCut.add(ret.get(i));

		return retCut;
	}

	protected int ratePositions(List<DbItem> my, List<DbItem> ref) {

		boolean any = false;
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
				//				continue;
				return 99999;

			// jestli je nektery ze signalu pod -90 ignorujeme
			//if (refItem.dbm < -80 || me.dbm < -80)
			//	continue;

			int subRate = (int) Math.round(Math.pow((me.dbm - refItem.dbm), 2));
			rate += subRate;
			any = true;
		}

		return any ? rate : 99999;
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




	private DatabaseTableItemPos getPosItem(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		DatabaseTableItemPos item = new DatabaseTableItemPos();

		String selectQuery = "SELECT * FROM PosRecords WHERE id = \"" + id + "\" LIMIT 1";
		Cursor cur = db.rawQuery(selectQuery, null);
		if (cur.moveToFirst()) {
			do {
				item.id = id;
				item.posx = Integer.parseInt(cur.getString(1));
				item.posy = Integer.parseInt(cur.getString(2));
				item.floor = Integer.parseInt(cur.getString(3));
			} while (cur.moveToNext());
		}
		cur.close();

		return item;
	}



	public List<DatabaseTableItemPos> getSavedPositions() {
		SQLiteDatabase db = this.getWritableDatabase();
		List<DatabaseTableItemPos> items = new ArrayList<DatabaseTableItemPos>();

		String selectQuery = "SELECT * FROM PosRecords";
		Cursor cur = db.rawQuery(selectQuery, null);
		if (cur.moveToFirst()) {
			do {
				DatabaseTableItemPos item = new DatabaseTableItemPos();
				item.id = Integer.parseInt(cur.getString(0));
				item.posx = Integer.parseInt(cur.getString(1));
				item.posy = Integer.parseInt(cur.getString(2));
				item.floor = Integer.parseInt(cur.getString(3));
				items.add(item);
			} while (cur.moveToNext());
		}
		cur.close();

		return items;
	}


	public void clearDatabase() {
		onUpgrade(this.getWritableDatabase(), 0, 0);
	}



}
