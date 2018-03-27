package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.disseminationlayer.communication.Message;

public class HelpActivity extends Activity {
	TextView tv;
	EditText ed_1, ed_2, ed_3, ed_4, ed_5, ed_6, ed_7, ed_8, ed_9, ed_10;
	Button btn;
	long l;
	String temp;
	ArrayList activenodes;
	String[] a;

	Message message;
	String nodes = "%";
	ListView lv_1, lv_2;
	GridView gv_1;
	private boolean HA_VISISTED = false;
	protected SharedPreferences mPrefs;
	protected Customizations custom;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		custom = new Customizations(this, -1);
		setContentView(custom.getHelpScreen());
		setTitle("");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		tv = (TextView) findViewById(R.id.tv_empty_bar_helpscreen);
		ed_1 = (EditText) findViewById(R.id.edt_help_box_1);
		ed_2 = (EditText) findViewById(R.id.edt_help_box_1_1);
		ed_3 = (EditText) findViewById(R.id.edt_help_box_2);
		ed_4 = (EditText) findViewById(R.id.edt_help_box_2_1);
		ed_5 = (EditText) findViewById(R.id.edt_help_box_3);
		ed_6 = (EditText) findViewById(R.id.edt_help_box_3_1);
		ed_7 = (EditText) findViewById(R.id.edt_help_box_4);
		ed_8 = (EditText) findViewById(R.id.edt_help_box_4_1);
		ed_9 = (EditText) findViewById(R.id.edt_help_box_5);
		ed_10 = (EditText) findViewById(R.id.edt_help_box_5_1);

		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
		tv.setTypeface(tf);
		ed_1.setTypeface(tf);
		ed_2.setTypeface(tf);
		ed_3.setTypeface(tf);
		ed_4.setTypeface(tf);
		ed_5.setTypeface(tf);
		ed_6.setTypeface(tf);
		ed_7.setTypeface(tf);
		ed_8.setTypeface(tf);
		ed_9.setTypeface(tf);
		ed_10.setTypeface(tf);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

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

			intent = new Intent(HelpActivity.this, ChoicesActivity.class);
			startActivity(intent);
			return true;

		case R.id.action_advanced:

			intent = new Intent(HelpActivity.this, PurgeActivity.class);
			startActivity(intent);
			return true;
			
		case R.id.action_sensor_en:

			intent = new Intent(HelpActivity.this, SensorReadingsActivity.class);
			startActivity(intent);
			return true;
	

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		mPrefs = getSharedPreferences("cache", 0);
		SharedPreferences.Editor ed = mPrefs.edit();
		ed.putBoolean("HA_VISITED", true);
		ed.commit();

	}

}
