package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.adapters.ListAdapter;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.disseminationlayer.communication.Message;

public class HelpContainerActivity extends Activity {

	TextView tv, tv_1, tv_2, tv_3, tv_4, tv_5;
	CheckBox ch_box_1, ch_box_2, ch_box_3, ch_box_4, ch_box_5;
	ListView lv_1, lv_2, lv_3, lv_4;
	Button btn;
	long l;
	String temp;
	ArrayList activenodes;
	String[] a;

	Message message;
	String nodes = "%";
	GridView gv_1;
	SharedPreferences mPrefs;
	String language = "En";
	ListAdapter list_adapter_1, list_adapter_2, list_adapter_3;
	private boolean LA_VISISTED = false;
	private Customizations custom;

	protected int lang_number = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPrefs = getSharedPreferences("language", 0);
		language = mPrefs.getString("language", "En");
		//ActionBar ab = getActionBar();
		//ab.hide();
		custom = new Customizations(this, -1);
		setContentView(custom.getHelpContainerScreen());
		setTitle("");
		// if(width <= 480) {
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// }
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
		Typeface tf_pala= Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");

		btn = (Button) findViewById(R.id.btn_more_screen_one);

		lv_3 = (ListView) findViewById(R.id.lv_help_container);
		tv = (TextView) findViewById(R.id.tv_empty_bar_m2mscreen);

		tv.setTypeface(tf);
		btn.setTypeface(tf_pala);

		list_adapter_3 = new ListAdapter(this);
		String[] settings = getResources().getStringArray(
				R.array.help_container_en);

		ArrayList<String> al_ = new ArrayList<String>();
		for (String temp : settings) {
			al_.add(temp);
		}

		// ///
		list_adapter_3 = new ListAdapter(this, 0, true, al_, 9);
		list_adapter_3.setLanguage_code(custom.getLanguage());
		lv_3.setAdapter(list_adapter_3);

		// lv = (ListView) findViewById(R.id.lv_m2m_list);

		String[] languages = getResources().getStringArray(
				R.array.help_container_en);

		ArrayList<String> al_lv = new ArrayList<String>();
		for (String temp : languages) {
			al_lv.add(temp);
		}

		lv_3.setOnItemClickListener(new OnItemClickListener() {

			String url_1 = "http://chitchato.com";
			String url_2 = "http://contexter.mobi/terms.html";
			String url_3 = "http://chitchato.com";

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				if (position == 0) {
					Intent intent = new Intent(HelpContainerActivity.this,
							TipsActivity.class);
					startActivity(intent);
				} else if (position == 1) {
					Intent intent = new Intent(HelpContainerActivity.this,
							HelpActivity.class);
					startActivity(intent);
				} else if (position == 2) {

					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url_2));
					startActivity(intent);
				} else if (position == 3) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url_2));
					startActivity(intent);
				} else if (position == 4) {
					Intent intent = new Intent(HelpContainerActivity.this,
							AboutActivity.class);
					intent.putExtra("CAT","COPYRIGHT");
					startActivity(intent);
				} else if (position == 5) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url_3));
					startActivity(intent);
				}

			}
		});

		// lv_3.setAdapter(list_adapter_3);

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(HelpContainerActivity.this,
						ChoicesActivity.class);
				startActivity(intent);
			}
		});

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

			intent = new Intent(HelpContainerActivity.this,
					ChoicesActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_advanced:

			intent = new Intent(HelpContainerActivity.this, PurgeActivity.class);
			startActivity(intent);
			return true;

		case R.id.action_sensor_en:

			intent = new Intent(HelpContainerActivity.this,
					SensorReadingsActivity.class);
			startActivity(intent);
			return true;
			/*
			 * case R.id.action_create:
			 * 
			 * intent = new Intent(LanguageActivity.this,
			 * LanguageActivity.class); startActivity(intent); return true; case
			 * R.id.action_settings:
			 * 
			 * intent = new Intent(LanguageActivity.this, PurgeActivity.class);
			 * startActivity(intent); return true; case R.id.action_search:
			 * 
			 * intent = new Intent(LanguageActivity.this,
			 * SearchGroupActivity.class); startActivity(intent); return true;
			 * case R.id.action_language:
			 * 
			 * intent = new Intent(LanguageActivity.this,
			 * LanguageActivity.class); startActivity(intent); return true; case
			 * R.id.action_register:
			 * 
			 * intent = new Intent(LanguageActivity.this,
			 * RegisterActivity.class); startActivity(intent); return true; case
			 * R.id.action_help:
			 * 
			 * intent = new Intent(LanguageActivity.this, HelpActivity.class);
			 * startActivity(intent); return true; case R.id.action_about:
			 * 
			 * intent = new Intent(LanguageActivity.this, AboutActivity.class);
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
		// if (sensibleThingsPlatform == null) {
		// sensibleThingsPlatform = new SensibleThingsPlatform(this);
		// }
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	// @Override
	public void onClick(View arg0) {

	}

}
