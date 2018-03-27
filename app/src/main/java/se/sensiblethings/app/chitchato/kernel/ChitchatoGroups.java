package se.sensiblethings.app.chitchato.kernel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.TreeMap;

public class ChitchatoGroups extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "groupsDB";
	static final String GROUP_NAME = "group_name";
	static final String GROUP_LEADER = "group_leader";
	static final String GROUP_INTEREST = "group_interest";
	static final String GROUP_AGE_LIMIT = "group_age_limit";
	static final String GROUP_LEADER_IP_ADDRESS ="127.0.0.1";
	static final String GROUP_LEADER_PORT ="9009";

	public ChitchatoGroups(Context context) {
		super(context, DATABASE_NAME, null, 1);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE groups_(_id INTEGER PRIMARY KEY AUTOINCREMENT,group_name VARCHAR(30), "
				+ "group_leader VARCHAR(30), group_interest VARCHAR(30), group_age_limit INTEGER );");
		ContentValues cv = new ContentValues();
		/*
		 * cv.put(GROUP_NAME, "#none#"); cv.put(GROUP_LEADER, "addis");
		 * cv.put(GROUP_INTEREST, "#none#"); cv.put(GROUP_AGE_LIMIT, 9999);
		 * 
		 * db.insert("groups", GROUP_NAME, cv);
		 */

		cv.put(GROUP_NAME, "chase_challenge");
		cv.put(GROUP_LEADER, "addis");
		cv.put(GROUP_INTEREST, "Chase Game");
		cv.put(GROUP_AGE_LIMIT, 15);

		db.insert("groups_", GROUP_NAME, cv);

		cv.put(GROUP_NAME, "football");
		cv.put(GROUP_LEADER, "Natan");
		cv.put(GROUP_INTEREST, "football");
		cv.put(GROUP_AGE_LIMIT, 10);

		db.insert("groups_", GROUP_NAME, cv);
		cv.put(GROUP_NAME, "politics");
		cv.put(GROUP_LEADER, "Jimmy");
		cv.put(GROUP_INTEREST, "politics");
		cv.put(GROUP_AGE_LIMIT, 15);

		db.insert("groups_", GROUP_NAME, cv);

		cv.put(GROUP_NAME, "Ladies");
		cv.put(GROUP_LEADER, "Lidia");
		cv.put(GROUP_INTEREST, "ladies");
		cv.put(GROUP_AGE_LIMIT, 15);

		db.insert("groups_", GROUP_NAME, cv);

		cv.put(GROUP_NAME, "dating");
		cv.put(GROUP_LEADER, "John");
		cv.put(GROUP_INTEREST, "dating");
		cv.put(GROUP_AGE_LIMIT, 15);

		db.insert("groups_", GROUP_NAME, cv);

		cv.put(GROUP_NAME, "fungroup");
		cv.put(GROUP_LEADER, "Stefan");
		cv.put(GROUP_INTEREST, "fun");
		cv.put(GROUP_AGE_LIMIT, 15);

		db.insert("groups_", GROUP_NAME, cv);
		cv.put(GROUP_NAME, "religious");
		cv.put(GROUP_LEADER, "Eric");
		cv.put(GROUP_INTEREST, "religion");
		cv.put(GROUP_AGE_LIMIT, 15);

		db.insert("groups_", GROUP_NAME, cv);

		cv.put(GROUP_NAME, "Youth");
		cv.put(GROUP_LEADER, "Emma");
		cv.put(GROUP_INTEREST, "other");
		cv.put(GROUP_AGE_LIMIT, 15);

		db.insert("groups_", GROUP_NAME, cv);

		cv.put(GROUP_NAME, "Habeshas");
		cv.put(GROUP_LEADER, "Teddy");
		cv.put(GROUP_INTEREST, "ethiopia");
		cv.put(GROUP_AGE_LIMIT, 15);

		db.insert("groups_", GROUP_NAME, cv);

		cv.put(GROUP_NAME, "the immortals");
		cv.put(GROUP_LEADER, "addis");
		cv.put(GROUP_INTEREST, "philosophy");
		cv.put(GROUP_AGE_LIMIT, 15);

		db.insert("groups_", GROUP_NAME, cv);
		cv.put(GROUP_NAME, "deviants");
		cv.put(GROUP_LEADER, "Jeffery");
		cv.put(GROUP_INTEREST, "other");
		cv.put(GROUP_AGE_LIMIT, 15);

		db.insertOrThrow("groups_", GROUP_NAME, cv);
		db.close();

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE if EXISTS groups_;");
		onCreate(db);

	}

	public Cursor getvalues(SQLiteDatabase db) {

		String[] FROM = { "_id", GROUP_NAME, GROUP_LEADER, GROUP_INTEREST,
				GROUP_AGE_LIMIT };
		Cursor cursor = db.query("groups_", FROM, null, null, null, null,
				"group_name");

		// startManagingCursor(cursor);
		return cursor;
	}

	/*
	 * private void addValues(SQLiteDatabase db, String[] str) { // Insert a new
	 * record into the Events data source.
	 * 
	 * ContentValues cv = new ContentValues(); cv.put(GROUP_NAME, str[0]);
	 * cv.put(GROUP_LEADER, str[1]); cv.put(GROUP_INTEREST, str[2]);
	 * cv.put(GROUP_AGE_LIMIT, str[3]); db.insertOrThrow("groups", null, cv); }
	 */

	public TreeMap<String, String> getGroups() {
		TreeMap<String, String> tm = new TreeMap<String, String>();
		Cursor cursor = getvalues(getReadableDatabase());
		Log.i("DB ", new String(getReadableDatabase() + ""));
		StringBuilder builder = new StringBuilder("Saved groups-> \n");
		StringBuilder tempBuilder;
		while (cursor.moveToNext()) {
			tempBuilder = new StringBuilder();
			long id = cursor.getLong(0);
			String group_name = cursor.getString(1);
			String group_leader = cursor.getString(2);
			String group_interest = cursor.getString(3);
			long group_age_limit = cursor.getLong(4);

			builder.append(id).append(": ");
			builder.append(group_name).append(": ");
			builder.append(group_leader).append(":");
			builder.append(group_interest).append(": ");
			builder.append(group_age_limit).append("\n");

			tempBuilder.append(id).append(":");
			tempBuilder.append(group_name).append(":");
			tempBuilder.append(group_leader).append(":");
			tempBuilder.append(group_interest).append(":");
			tempBuilder.append(group_age_limit).append("");

			Log.i("From SQLite DB ", new String(builder));
			tm.put(group_name, new String(builder));

		}

		return tm;

	}

}
