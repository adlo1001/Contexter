package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.adapters.ListAdapter;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.DialogOne;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public class AddDevicesActivity extends Activity {
	TextView tv, tv_1, tv_2, tv_3, tv_4, tv_5;
	EditText edt_1, edt_2;
	CheckBox ch_box_1, ch_box_2, ch_box_3, ch_box_4, ch_box_5;
	ListView lv_1, lv_2, lv_3, lv_4;
	Button btn, btn_, btn_exit;
	Spinner spinner;
	long l;
	String temp;
	ArrayList activenodes;
	String[] a;

	Message message;
	String nodes = "%";
	GridView gv_1;
	SensibleThingsPlatform sensibleThingsPlatform;
	Communication communication;
	SensibleThingsNode sensiblethingsnode;
	SharedPreferences mPrefs;
	Set<String> devices;
	ListAdapter list_adapter_1, list_adapter_2, list_adapter_3;
	private boolean LA_VISISTED = false;
	private Customizations custom;
	// Englishs.> 0
	protected int lang_number = 0;
	protected TreeMap devices_map = new TreeMap<String, String>();
	private DialogOne dialog_one;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPrefs = getSharedPreferences("devices", 0);
		devices = mPrefs.getStringSet("devices", devices_map.keySet());

		custom = new Customizations(this, -1);
		setContentView(custom.getDevicesScreen());
		setTitle("");

		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
		Typeface tf_pala = Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");

		btn = (Button) findViewById(R.id.button_add_devices);
		btn_ = (Button) findViewById(R.id.button_clr_devices);
		btn_exit = (Button) findViewById(R.id.button_exit_devices);
		tv = (TextView) findViewById(R.id.tv_empty);
		tv_1 = (TextView) findViewById(R.id.tv_bootstrp_one);
		tv_2 = (TextView) findViewById(R.id.tv_bootstrp_two);
		tv_3 = (TextView) findViewById(R.id.tv_bootstrp_zero);
		edt_1 = (EditText) findViewById(R.id.edt_device_name);
		edt_2 = (EditText) findViewById(R.id.edt_device_ip);

		btn.setTypeface(tf_pala);
		btn_.setTypeface(tf_pala);
		btn_exit.setTypeface(tf_pala);
		tv.setTypeface(tf);
		tv_1.setTypeface(tf);
		tv_2.setTypeface(tf);
		tv_3.setTypeface(tf);
		edt_1.setTypeface(tf);

		dialog_one = new DialogOne(this, false, 13);

		spinner = (Spinner) findViewById(R.id.spinner_my_devices);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.spinner_item_one);

		Iterator itr = devices.iterator();
		Map.Entry me;
		String ip_ = "", device_name = "";
		while (itr.hasNext()) {

			ip_ = (String) itr.next();
			if (ip_.split(":").length > 1) {
				device_name = ip_.split(":")[0];
				adapter.add(device_name);
				devices_map.put(device_name, device_name);
			}

		}
		// Populate Spinner
		spinner.setAdapter(adapter);
		String ip_address_pattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		final Pattern pattern = Pattern.compile(ip_address_pattern);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Matcher matcher;
				if (devices == null)
					devices = devices_map.keySet();

				String temp_ip_address = edt_2.getText().toString();
				String temp_device_name = edt_1.getText().toString();

				matcher = pattern.matcher(temp_ip_address);

				if (matcher.matches()) {
					devices_map.put(temp_device_name + ":" + temp_ip_address,
							temp_ip_address);
					devices = devices_map.keySet();
					SharedPreferences.Editor ed = mPrefs.edit();
					ed.putStringSet("devices", devices);
					ed.commit();

					Toast.makeText(getApplicationContext(), "Device Added!",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(AddDevicesActivity.this,
							AddDevicesActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(),
							"Invalid IP Address", Toast.LENGTH_SHORT).show();
				}

			}
		});

		btn_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				SharedPreferences.Editor ed = mPrefs.edit();
				devices.clear();
				ed.putStringSet("devices", devices);
				ed.commit();

				Toast.makeText(getApplicationContext(), "Devices Removed!",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(AddDevicesActivity.this,
						AddDevicesActivity.class);
				startActivity(intent);

			}
		});


		// Exit BootStrap
		btn_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(AddDevicesActivity.this,
						MoreActivity.class);
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

			intent = new Intent(AddDevicesActivity.this, ChoicesActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_advanced:

			intent = new Intent(AddDevicesActivity.this, PurgeActivity.class);
			startActivity(intent);
			return true;

		case R.id.action_sensor_en:

			intent = new Intent(AddDevicesActivity.this,
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
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	// @Override
	public void onClick(View arg0) {

	}

	@Override
	public void onBackPressed() {
		dialog_one.show();
		return;
	}

}
