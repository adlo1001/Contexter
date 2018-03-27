package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
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
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.disseminationlayer.communication.Message;

public class TipsActivity extends Activity {
	TextView tv;
	EditText edt;
	EditText ed_1, ed_2, ed_3, ed_4, ed_5, ed_6, ed_7, ed_8, ed_9, ed_10;
	long l;
	String temp;
	ArrayList activenodes;
	String[] a;

	Message message;
	String nodes = "%";
	ListView lv_1, lv_2;
	GridView gv_1;
	private boolean About_VISISTED = false;
	private Customizations custom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		custom = new Customizations(this, -1);
		setContentView(R.layout.about);
		setTitle("");
		tv = (TextView) findViewById(R.id.tv_empty_bar_aboutscreen);
		edt = (EditText) findViewById(R.id.edt_about_box);
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
		Typeface tf_pala = Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");
		//edt.setText(R.string.tips_text_en);
		tv.setText("Tips");
		edt.setText(getResources().getString(R.string.tips_text_en));
		if(custom.getLanguage()==1)
		{
			String[] helps = getResources().getStringArray(R.array.help_container_sv);
			tv.setText(helps[0]);
			edt.setText(getResources().getString(R.string.tips_text_sv));
		}
		else if(custom.getLanguage()==2){
			String[] helps = getResources().getStringArray(R.array.help_container_sp);
			tv.setText(helps[0]);
			edt.append(getResources().getString(R.string.tips_text_sp));
			edt.append(getResources().getString(R.string.help_text_1_1_en));

		}
		else if(custom.getLanguage()==3){
			String[] helps = getResources().getStringArray(R.array.help_container_pr);
			tv.setText(helps[0]);
			edt.setText(getResources().getString(R.string.tips_text_pr));
		}
		else if(custom.getLanguage()==4){
			String[] helps = getResources().getStringArray(R.array.help_container_fr);
			tv.setText(helps[0]);
			edt.setText(getResources().getString(R.string.tips_text_fr));
		}
		else if(custom.getLanguage()==5){
			tv.setText("Tips");
			edt.setText(getResources().getString(R.string.tips_text_am));
		}
		else
		{
			tv.setText("Tips");
			edt.setText(getResources().getString(R.string.tips_text_en));
		}

		tv.setTypeface(tf);
		edt.setTypeface(tf_pala);

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

		ed_1.setTypeface(tf_pala);
		ed_2.setTypeface(tf_pala);
		ed_3.setTypeface(tf_pala);
		ed_4.setTypeface(tf_pala);
		ed_5.setTypeface(tf_pala);
		ed_6.setTypeface(tf_pala);
		ed_7.setTypeface(tf_pala);
		ed_8.setTypeface(tf_pala);
		ed_9.setTypeface(tf_pala);
		ed_10.setTypeface(tf_pala);

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

			intent = new Intent(TipsActivity.this, ChoicesActivity.class);
			startActivity(intent);
			return true;

		case R.id.action_advanced:

			intent = new Intent(TipsActivity.this, PurgeActivity.class);
			startActivity(intent);
			return true;

		case R.id.action_sensor_en:

			intent = new Intent(TipsActivity.this, SensorReadingsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	// @Override
	public void onClick(View arg0) {

	}

}
