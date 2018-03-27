package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.adapters.ImageListAdapter;
import se.sensiblethings.app.chitchato.adapters.ListAdapter;
import se.sensiblethings.app.chitchato.context.ContextManager;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.kernel.ChitchatoParametersRetrieval;
import se.sensiblethings.app.chitchato.kernel.ChitchatoPlatform;
import se.sensiblethings.app.chitchato.kernel.Groups;
import se.sensiblethings.app.chitchato.kernel.PublicChats;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class BusyActivity extends Activity {

	// The Platform API

	private ProgressDialog prgDialog;
	long l;
	String temp;
	String[] a;

	Message message;
	String nodes = "%";
	ChitchatoPlatform sensibleThingsPlatform;
	Communication communication;
	SensibleThingsNode sensiblethingsnode;
	ChitchatoParametersRetrieval parms;
	protected SharedPreferences mPrefs;
	protected boolean CHAT_MODE = true;
	protected String MODE = "Ordinary Mode";
	private Groups groups;
	private PublicChats public_chats;
	private String Message = "";
	private String chatMessage = "";
	private String contextMessage = "";
	private String localPeerName = "";
	private String groupName = "";
	private boolean MA_VISITED = false;
	private ArrayList<String> al_chats = null;
	private ImageListAdapter imageListAdapter = null;
	private ListAdapter listadapter;
	protected Customizations custom;
	protected BKGroundTask_Chat background_task_chat;
	protected BusyThread busy_thread;
	protected boolean SHOWGROUPS = true;
	protected ContextManager context_manager;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPrefs = getSharedPreferences("preference_1", 0);
		CHAT_MODE = mPrefs.getBoolean("OChat", true);

		if (CHAT_MODE == false)
			MODE = "Context Mode";
		else
			MODE = "Ordinary Mode";

		custom = new Customizations(this, -1);
		setContentView(R.layout.splash);
		// setTitle(MODE);

		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
		// Font face

		// //////////

		groups = new Groups(this);

		ArrayList<String> al_ = groups.getGroups();

		if (al_.isEmpty() || al_ == null || al_.get(0).contains("None!")) {
			Toast.makeText(this, "Groups not Imported. Please Import Groups",
					Toast.LENGTH_LONG).show();

			// Intent intent = new Intent(BusyActivity.this,
			// SearchGroupActivity.class);
			Intent intent = new Intent(BusyActivity.this, MainActivity.class);

			startActivity(intent);

			// groups = new Groups(this, null);
			// al_ = groups.getGroups("");
			// if (al_.isEmpty() || al_ == null)

		}

		listadapter = new ListAdapter(this, -1, CHAT_MODE, al_, 0);
		final ArrayList<String> peers = new ArrayList<String>();

		peers.add("Click On your Favorite Group to public Chitchat!");
		peers.add("Long-Click On your Favorite Group to private Chitchat!");

		imageListAdapter = new ImageListAdapter(getBaseContext(), false,
				CHAT_MODE, 0, peers);
		//imageListAdapter.setSetProfilePictureGone(true);

		// Context Information

		//context_manager = new ContextManager(this, localPeerName);

		sensibleThingsPlatform = parms.getChitchatoPlatform();
		// sensibleThingsPlatform.getSensibleThingsPlatform().shutdown();
		// If platform is not intialized show error
		if (!sensibleThingsPlatform.getSensibleThingsPlatform().isInitalized()) {
			Intent intent = new Intent(BusyActivity.this, ErrorActivity.class);
			intent.putExtra("error", "Platform is not initialized!");
			startActivity(intent);
		}

		//

		busy_thread = new BusyThread();
		busy_thread.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		getMenuInflater().inflate(R.menu.chatscreen, menu);
		MenuItem menuItem_1 = menu.getItem(0);
		MenuItem menuItem_2 = menu.getItem(1);

		if (custom.getLanguage() == 1) {

			menuItem_1.setTitle(R.string.action_chitchato_sv);
			menuItem_2.setTitle(R.string.action_advanced_sv);

		} else if (custom.getLanguage() == 2) {

			menuItem_1.setTitle(R.string.action_chitchato_sp);
			menuItem_2.setTitle(R.string.action_advanced_sp);

		} else if (custom.getLanguage() == 3) {

			menuItem_1.setTitle(R.string.action_chitchato_pr);
			menuItem_2.setTitle(R.string.action_advanced_pr);

		} else if (custom.getLanguage() == 4) {

			menuItem_1.setTitle(R.string.action_chitchato_fr);
			menuItem_2.setTitle(R.string.action_advanced_fr);

		} else if (custom.getLanguage() == 5) {

			menuItem_1.setTitle(R.string.action_chitchato_am);
			menuItem_2.setTitle(R.string.action_advanced_am);

		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		/*
		 * case R.id.action_chitchato:
		 * 
		 * intent = new Intent(BusyActivity.this, ChoicesActivity.class);
		 * startActivity(intent); return true; case R.id.action_advanced:
		 * 
		 * intent = new Intent(BusyActivity.this, PurgeActivity.class);
		 * startActivity(intent); return true;
		 * 
		 * 
		 * case R.id.action_create:
		 * 
		 * intent = new Intent(MainActivity.this, CreateGroupActivity.class);
		 * startActivity(intent); return true; case R.id.action_settings:
		 * 
		 * intent = new Intent(MainActivity.this, PurgeActivity.class);
		 * startActivity(intent); return true; case R.id.action_search:
		 * 
		 * intent = new Intent(MainActivity.this, SearchGroupActivity.class);
		 * startActivity(intent); return true;
		 * 
		 * case R.id.action_language:
		 * 
		 * intent = new Intent(MainActivity.this, LanguageActivity.class);
		 * startActivity(intent); return true; case R.id.action_register:
		 * 
		 * intent = new Intent(MainActivity.this, RegisterActivity.class);
		 * startActivity(intent); return true;
		 * 
		 * case R.id.action_help:
		 * 
		 * intent = new Intent(MainActivity.this, HelpActivity.class);
		 * startActivity(intent); return true; case R.id.action_about:
		 * 
		 * intent = new Intent(MainActivity.this, HelpActivity.class);
		 * startActivity(intent); return true;
		 */

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Create the platform itself
		if (sensibleThingsPlatform == null) {
			// sensibleThingsPlatform = new ChitchatoPlatform();
		}
	}

	// @Override
	public void onClick(View arg0) {

	}

	@Override
	public void onStop() {
		super.onStop();
		mPrefs = getSharedPreferences("cache", 0);
		SharedPreferences.Editor ed = mPrefs.edit();
		ed.putBoolean("MA_VISITED", true);

		ed.commit();

	}

	public class BKGroundTask extends AsyncTask<String, String, String> {

		boolean flag = false;

		@Override
		protected String doInBackground(String... arg0) {
			this.flag = false;
			String s = "", str = "", XML_DOC = "";
			ArrayList<String> peers = null;
			try {
				Groups groups = new Groups(getApplicationContext());

				XML_DOC = groups.retrieveGroupFromBT(getBaseContext(),
						groupName);
				Log.w("XML_DOC", XML_DOC);
				peers = groups.getPeers(XML_DOC);

				for (String temp : peers) {
					Log.w("Peers-->Main-->test", temp);
				}

				// ShowResults(peers, "");
			}

			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Intent intent = new Intent(BusyActivity.this,
						ErrorActivity.class);
				intent.putExtra("error", "System Error!! ");
				startActivity(intent);
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				result = "done";
				this.flag = true;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public class BKGroundTask_Chat extends AsyncTask<String, String, String> {

		protected boolean flag = false;

		@Override
		protected String doInBackground(String... arg0) {
			this.flag = false;
			String s = "", str = "", XML_DOC = "";
			ArrayList<String> peers = null;
			try {

				public_chats = new PublicChats(getBaseContext());
				XML_DOC = public_chats.getPublicChat(getBaseContext(),
						groupName);
				Log.w("Messages-->Main-->XML_DOC", XML_DOC);
				al_chats = public_chats.getPublicChat(XML_DOC);

				// for (String temp : al_chats) {
				// Log.w("Messages-->Main-->test", temp);
				// }
				this.flag = true;

				// update_chat_message = new UpdateChatMessages(
				// al_chats);
				// /update_chat_message.start();
				// /ShowMessages(al_chats);

				// Intent intent = new Intent();
				// intent.putStringArrayListExtra("al_chats", al_chats);
				// getApplicationContext().startActivity(intent);

			}

			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Intent intent = new Intent(BusyActivity.this,
						ErrorActivity.class);
				intent.putExtra("error", "System Error!! ");
				startActivity(intent);
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				result = "done";
				this.flag = true;
				// Log.w("OnPost Execute", "Here I am !");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/*
		 * public ImageListAdapter ShowChatMessages(ArrayList<String> al) {
		 * imageListAdapter = new ImageListAdapter(getBaseContext(), false,
		 * CHAT_MODE, 0, al); imageListAdapter.setSetProfilePictureGone(false);
		 * 
		 * return imageListAdapter; }
		 */

	}

	public class BusyThread extends Thread {
		public BusyThread() {

		}

		public BusyThread(ArrayList<String> array_list) {

		}

		@Override
		public void run() {

			while (true) {

				try {
					// check if the buttons are visible for the last 2 minutes
					this.sleep(5000);
					Intent intent = new Intent(BusyActivity.this,
							MainActivity.class);
					intent.putExtra("error", "System Error!! ");
					startActivity(intent);
					this.interrupt();

				} catch (Exception e) {
					Log.v("Busy", "Move Forward!");
					// e.printStackTrace();
					return;
				}

			}
		}
	}

}
