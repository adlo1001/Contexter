package se.sensiblethings.app.chitchato.kernel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ListAdapter;

public class ChitchatoPeers extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "groupsDB";
	static final String GROUP_NAME = "group_name";
	static final String PEER_NAME = "peer_name";
	static final String PEER_NICK_NAME = "peer_nick_name";
	static final String PEER_AGE = "peer_age";
	static final String PEER_IP_ADDRESS ="127.0.0.1";
	static final String PEER_PORT ="9009";
	

	protected ListAdapter list_adapter;
	
	public ChitchatoPeers(Context context) {
		super(context, DATABASE_NAME, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE peers(_id INTEGER PRIMARY KEY AUTOINCREMENT,group_name VARCHAR(30), "
				+ "peer_name VARCHAR(30), peer_nick_name VARCHAR(30), peer_age INTEGER );");
		ContentValues cv = new ContentValues();
		cv.put(GROUP_NAME, "#none#");
		cv.put(PEER_NAME, "addis");
		cv.put(PEER_NICK_NAME, "#none#");
		cv.put(PEER_AGE, 9999);

		db.insert("peers", GROUP_NAME, cv);

		cv.put(GROUP_NAME, "chase_challenge");
		cv.put(PEER_NAME, "addis");
		cv.put(PEER_NICK_NAME, "addis");
		cv.put(PEER_AGE, 37);

		db.insert("peers", GROUP_NAME, cv);

		cv.put(GROUP_NAME, "chase_challenge");
		cv.put(PEER_NAME, "Natan");
		cv.put(PEER_NICK_NAME, "Natan");
		cv.put(PEER_AGE, 28);

		db.insert("peers", GROUP_NAME, cv);
		cv.put(GROUP_NAME, "chase_challenge");
		cv.put(PEER_NAME, "Jimmy");
		cv.put(PEER_NICK_NAME, "Jimmy");
		cv.put(PEER_AGE, 26);

		db.insert("peers", GROUP_NAME, cv);

		cv.put(GROUP_NAME, "chase_challenge");
		cv.put(PEER_NAME, "Lidia");
		cv.put(PEER_NICK_NAME, "Lidia");
		cv.put(PEER_AGE, 27);

		db.insert("peers", GROUP_NAME, cv);

		cv.put(GROUP_NAME, "chase_challenge");
		cv.put(PEER_NAME, "John");
		cv.put(PEER_NICK_NAME, "John");
		cv.put(PEER_AGE, 29);

		db.insert("peers", GROUP_NAME, cv);

		cv.put(GROUP_NAME, "Dating");
		cv.put(PEER_NAME, "Stefan");
		cv.put(PEER_NICK_NAME, "Stefan");
		cv.put(PEER_AGE, 40);

		db.insert("groups", GROUP_NAME, cv);
		cv.put(GROUP_NAME, "Dating");
		cv.put(PEER_NAME, "Eric");
		cv.put(PEER_NICK_NAME, "Eric");
		cv.put(PEER_AGE, 34);

		db.insert("peers", GROUP_NAME, cv);

		cv.put(GROUP_NAME, "Dating");
		cv.put(PEER_NAME, "Emma");
		cv.put(PEER_NICK_NAME, "Emma");
		cv.put(PEER_AGE, 24);

		db.insert("peers", GROUP_NAME, cv);

		cv.put(GROUP_NAME, "Politics");
		cv.put(PEER_NAME, "Teddy");
		cv.put(PEER_NICK_NAME, "Teddy");
		cv.put(PEER_AGE, 25);

		db.insert("groups", GROUP_NAME, cv);

		cv.put(GROUP_NAME, "Politics");
		cv.put(PEER_NAME, "addis");
		cv.put(PEER_NICK_NAME, "addis");
		cv.put(PEER_AGE, 20);

		db.insert("peers", GROUP_NAME, cv);
		cv.put(GROUP_NAME, "deviants");
		cv.put(PEER_NAME, "Jeffery");
		cv.put(PEER_NICK_NAME, "Jeffery");
		cv.put(PEER_AGE, 30);

		db.insert("peers", GROUP_NAME, cv);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE if EXISTS peers;");
		onCreate(db);

	}

	public Cursor getvalues(SQLiteDatabase db) {

		String[] FROM = { "_id", GROUP_NAME, PEER_NAME, PEER_NICK_NAME,
				PEER_AGE };
		Cursor cursor = db.query("peers", FROM, null, null, null, null,
				"group_name");

		return cursor;
	}
	


	private void addValues(SQLiteDatabase db, String[] str) {
		// Insert a new record into the Events data source.

		ContentValues cv = new ContentValues();
		cv.put(GROUP_NAME, str[0]);
		cv.put(PEER_NAME, str[1]);
		cv.put(PEER_NICK_NAME, str[2]);
		cv.put(PEER_AGE, str[3]);
		db.insertOrThrow("peers", null, cv);
	}
}
