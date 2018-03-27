package se.sensiblethings.app.chitchato.extras;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.activities.ChoicesActivity;
import se.sensiblethings.app.chitchato.activities.MoreActivity;
import se.sensiblethings.app.chitchato.activities.PurgeActivity;
import se.sensiblethings.app.chitchato.activities.SensorReadingsActivity;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public class MessageNotification extends Activity {

	// The Platform API

	TextView tv;
	EditText edt;
	long l;
	String temp;
	ArrayList activenodes;
	String[] a;

	Message message;
	String nodes = "%";
	ListView lv_1, lv_2;
	GridView gv_1;
	SensibleThingsPlatform sensibleThingsPlatform;
	Communication communication;
	SensibleThingsNode sensiblethingsnode;
	private boolean About_VISISTED = false;
	private Customizations custom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		custom = new Customizations(this, -1);
		setContentView(custom.getAboutScreen());
		setTitle("");
		tv = (TextView) findViewById(R.id.tv_empty_bar_aboutscreen);
		edt = (EditText) findViewById(R.id.edt_about_box);
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");

		Typeface tf_pala = Typeface.createFromAsset(getAssets(),
				"fonts/pala.ttf");

		tv.setTypeface(tf);
		edt.setTypeface(tf_pala);
	}

	public void MessageNotifier(View view)
	{
     Intent intent = new Intent(this, MoreActivity.class);
		PendingIntent pintent = PendingIntent.getActivity(this, (int)System.currentTimeMillis(),intent,0);
		Notification _mNotification = new Notification.Builder(this).setContentTitle("New Message From X")
				.setContentText("Content--Contexter").setSmallIcon(R.drawable.ic_launcher).setContentIntent(pintent)
				.addAction(R.drawable.ic_launcher,"Text",pintent)
				.addAction(R.drawable.ic_launcher,"Context",pintent)
				.addAction(R.drawable.ic_launcher,"And ,more", pintent).build();


		NotificationManager _notification_manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		_mNotification.flags|=Notification.FLAG_AUTO_CANCEL;
		_notification_manager.notify(0,_mNotification);

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		getMenuInflater().inflate(R.menu.chatscreen, menu);
		MenuItem menuItem_1 = menu.getItem(0);
		MenuItem menuItem_2 = menu.getItem(1);
		MenuItem menuItem_3 = menu.getItem(2);

		if (custom.getLanguage() == 1) {
			menuItem_1.setTitle(R.string.action_chitchato_sv);
			menuItem_2.setTitle(R.string.action_advanced_sv);
			menuItem_3.setTitle(R.string.action_sensor_en);

		} else if (custom.getLanguage() == 2) {

			menuItem_1.setTitle(R.string.action_chitchato_sp);
			menuItem_2.setTitle(R.string.action_advanced_sp);
			menuItem_3.setTitle(R.string.action_sensor_en);

		} else if (custom.getLanguage() == 3) {

			menuItem_1.setTitle(R.string.action_chitchato_pr);
			menuItem_2.setTitle(R.string.action_advanced_pr);
			menuItem_3.setTitle(R.string.action_sensor_en);

		} else if (custom.getLanguage() == 4) {

			menuItem_1.setTitle(R.string.action_chitchato_fr);
			menuItem_2.setTitle(R.string.action_advanced_fr);
			menuItem_3.setTitle(R.string.action_sensor_en);

		} else if (custom.getLanguage() == 5) {

			menuItem_1.setTitle(R.string.action_chitchato_am);
			menuItem_2.setTitle(R.string.action_advanced_am);
			menuItem_3.setTitle(R.string.action_sensor_en);

		}

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.action_chitchato:
			intent = new Intent(MessageNotification.this, ChoicesActivity.class);
			startActivity(intent);
			return true;

		case R.id.action_advanced:

			intent = new Intent(MessageNotification.this, PurgeActivity.class);
			startActivity(intent);
			return true;

		case R.id.action_sensor_en:

			intent = new Intent(MessageNotification.this,
					SensorReadingsActivity.class);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Create the platform itself
		// if (sensibleThingsPlatform == null) {
		// sensibleThingsPlatform = new SensibleThingsPlatform(this);
		// }
	}

	// @Override
	public void onClick(View arg0) {

	}

}
