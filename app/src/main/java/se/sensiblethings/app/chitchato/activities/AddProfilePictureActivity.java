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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.adapters.ImageAdapter;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.SysError;
import se.sensiblethings.app.chitchato.kernel.ChitchatoGroups;
import se.sensiblethings.app.chitchato.kernel.ChitchatoParametersRetrieval;
import se.sensiblethings.app.chitchato.kernel.Peers;
import se.sensiblethings.disseminationlayer.communication.Message;

public class AddProfilePictureActivity extends Activity {

	TextView tv_1;
	Button btn_1;

	ImageView image_view;
	long l;
	String temp;
	ArrayList activenodes;
	String[] a;

	Message message;
	String nodes = "%";
	GridView gv_1;

	ChitchatoParametersRetrieval parms;
	protected SharedPreferences mPrefs, mPrefs_;
	protected boolean CHAT_MODE = true;
	private ChitchatoGroups groups;
	private Customizations custom;
	private Class<?> first_activity;
	private ImageAdapter imageAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPrefs = getSharedPreferences("preference_1", 0);
		CHAT_MODE = mPrefs.getBoolean("OChat", true);

		mPrefs_ = getSharedPreferences("cache", 0);
		custom = new Customizations(this, -1);
		setContentView(custom.getGalleryScreen());
		setTitle("");
		image_view = (ImageView) findViewById(R.id.img_view_gallery);
		try {

			Peers peers = new Peers(this);

			Peers.ProfileImage profile_image = peers.profileImage;
			imageAdapter = new ImageAdapter(this, -1);
			Typeface tf = Typeface.createFromAsset(getAssets(),
					"fonts/COMIC.TTF");

			gv_1 = (GridView) findViewById(R.id.gridView1_profile_pic);
			btn_1 = (Button) findViewById(R.id.btn_Add_image_gallery);
			tv_1 = (TextView) findViewById(R.id.tv_empty_bar_profile_pic);

			btn_1.setTypeface(tf);
			tv_1.setTypeface(tf);

			gv_1.setAdapter(imageAdapter);

			Bundle extras = getIntent().getExtras();

			first_activity = Class
					.forName("se.sensiblethings.app.chitchato.activities."
							+ extras.getString("ACTIVITY"));

			gv_1.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View view,
						int position, long arg3) {

					ImageView iv = (ImageView) view;
					image_view.setScaleType(ImageView.ScaleType.CENTER_CROP);
					image_view.setPadding(32, 32, 32, 32);

					imageAdapter.setIndex(position);
					image_view = imageAdapter.getSelectedImage();

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			btn_1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(AddProfilePictureActivity.this,
							first_activity);
					startActivityForResult(intent, 1337);
					// startActivity(intent);

				}
			});

		} catch (Exception e) {
			// TODO Auto-generated catch block

			SysError error = new SysError(this, false);
			error.show();
			e.printStackTrace();
		}
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

			intent = new Intent(AddProfilePictureActivity.this,
					ChoicesActivity.class);
			startActivity(intent);
			return true;

		case R.id.action_advanced:

			intent = new Intent(AddProfilePictureActivity.this,
					PurgeActivity.class);
			startActivity(intent);
			return true;

		case R.id.action_sensor_en:

			intent = new Intent(AddProfilePictureActivity.this,
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

	}

	// @Override
	public void onClick(View arg0) {

	}

}
