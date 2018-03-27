package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
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
import android.widget.Toast;

import java.util.ArrayList;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.adapters.ListAdapter;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.kernel.Groups;
import se.sensiblethings.disseminationlayer.communication.Message;

public class RemoveGroupsActivity extends Activity {

	TextView tv;
	Button btn;
	ListView lv;
	long l;
	String temp;
	ArrayList activenodes;
	String[] a;

	Message message;
	String nodes = "%";
	ListView lv_1, lv_2;
	GridView gv_1;
	ArrayList<String> al_;
    protected Customizations custom;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		custom = new Customizations(this, -1);
		setContentView(custom.getDeleteGroupsScreen());
		setTitle("Delete");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		tv = (TextView) findViewById(R.id.tv_delete_groups_title);
		btn = (Button) findViewById(R.id.button1_delete_group);

		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
		Typeface tf_pala = Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");

		tv.setTypeface(tf);
		btn.setTypeface(tf_pala);

		

		lv = (ListView) findViewById(R.id.listView2_delete_Groups);

		Groups _mGroups = new Groups(RemoveGroupsActivity.this, "", false);
	    al_ = _mGroups.getmGroups();
		ArrayList<String> al__ = new ArrayList<String>();


		// List of groups preferred by user
		for (String temp : al_) {
			if (temp.contains(":::")) {
				String _temp_0 = temp.split(":::")[1];
				if (_temp_0.contains("~::~")) {
					String[] _temp_ = _temp_0.split("~::~");
					if (_temp_.length > 4) {
						String group_name =  _temp_[0];
						String group_intereste = _temp_[1];
						String group_leader =  _temp_[2];
						String group_age_limit = _temp_[4];
						String CreationDate =  _temp_[5];
						//
						temp = group_name;
					}
				}
			}
			al__.add(temp);
		}




		if (al_.isEmpty() || al_ == null || al_.get(0).contains("None!")) {
			Toast.makeText(this, "Groups not Imported ", Toast.LENGTH_SHORT)
					.show();

		}

		final ListAdapter list_adapter = new ListAdapter(this, 0, true, al__, 3);
       
		lv.setAdapter(list_adapter);
		final ArrayList<String> list_to_be_removed = new ArrayList<String>();
		final ArrayList<String> updated_al = al_;
		lv.setOnItemClickListener(new OnItemClickListener() {

			CheckBox check_box;
			TextView tv;

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				check_box = (CheckBox) view.findViewById(R.id.lang_chat_radio);
				tv = (TextView) view.findViewById(R.id.tv_lang_checkbox);

				check_box.setChecked(!check_box.isChecked());
				if (check_box.isChecked())
					list_to_be_removed.add(tv.getText().toString().trim());
				// Log.w("groups positon-->", position + "");

			}
		});
		// lv.setOnItemSelectedListener(new OnItemSelectedListener(){});

		btn.setOnClickListener(new OnClickListener() {

			final Groups groups = new Groups(getBaseContext());
			ArrayList<String> arr_toSave = new ArrayList<String>();

			@Override
			public void onClick(View arg0) {
				int i = 0;







					for (String temp : al_) {
						if (temp.contains(":::")) {
							String _temp_0 = temp.split(":::")[1];
							if (_temp_0.contains("~::~")) {
								String[] _temp_ = _temp_0.split("~::~");
								if (_temp_.length > 4) {
									String group_name =  _temp_[0];
									String group_intereste = _temp_[1];
									String group_leader =  _temp_[2];
									String group_age_limit = _temp_[4];
									String CreationDate =  _temp_[5];
									//
									if(list_to_be_removed.contains(group_name))
									{
										//al__.remove()
									}
									else
									{
										arr_toSave.add(temp);
									}
								}
							}
						}
					groups.clearGroups();
					groups.saveNewGroup("groups","groups",arr_toSave);
				}

				al_ = groups.getmGroups();
				final ListAdapter list_adapter_ = new ListAdapter(getBaseContext(), 0, true, al_, 3);
				lv.setAdapter(list_adapter_);
				Toast.makeText(getBaseContext(), "Groups Removed!", Toast.LENGTH_SHORT)
						.show();
				
				
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

			intent = new Intent(RemoveGroupsActivity.this,
					ChoicesActivity.class);
			startActivity(intent);
			return true;

		case R.id.action_advanced:

			intent = new Intent(RemoveGroupsActivity.this, PurgeActivity.class);
			startActivity(intent);
			return true;
			
		case R.id.action_sensor_en:

			intent = new Intent(RemoveGroupsActivity.this, SensorReadingsActivity.class);
			startActivity(intent);
			return true;
			
			/*
			 * case R.id.action_create:
			 * 
			 * intent = new Intent(AboutActivity.this, AboutActivity.class);
			 * startActivity(intent); return true; case R.id.action_settings:
			 * 
			 * intent = new Intent(AboutActivity.this, PurgeActivity.class);
			 * startActivity(intent); return true; case R.id.action_search:
			 * 
			 * intent = new Intent(AboutActivity.this,
			 * SearchGroupActivity.class); startActivity(intent); return true;
			 * case R.id.action_language:
			 * 
			 * intent = new Intent(AboutActivity.this, LanguageActivity.class);
			 * startActivity(intent); return true; case R.id.action_register:
			 * 
			 * intent = new Intent(AboutActivity.this, RegisterActivity.class);
			 * startActivity(intent); return true; case R.id.action_help:
			 * 
			 * intent = new Intent(AboutActivity.this, HelpActivity.class);
			 * startActivity(intent); return true; case R.id.action_about:
			 * 
			 * intent = new Intent(AboutActivity.this, AboutActivity.class);
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

	// @Override
	public void onClick(View arg0) {

	}

}
