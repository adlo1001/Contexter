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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.adapters.ListAdapter;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.DialogOne;
import se.sensiblethings.disseminationlayer.communication.Message;

public class M2MActivity extends Activity {

    TextView tv;
    CheckBox ch_box_1, ch_box_2, ch_box_3, ch_box_4, ch_box_5;
    ListView lv;
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
    ListAdapter list_adapter;
    private boolean LA_VISISTED = false;
    private Customizations custom;
    // Englishs.> 0
    protected int lang_number = 0;
    private DialogOne dialogOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("language", 0);
        language = mPrefs.getString("language", "En");
        custom = new Customizations(this, -1);
        setContentView(custom.getM2MScreen());
        setTitle("");
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
        Typeface tf_pala= Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");

        tv = (TextView) findViewById(R.id.tv_empty_bar_m2mscreen);
        btn = (Button) findViewById(R.id.button_m2m_exit);
        tv.setTypeface(tf);
        btn.setTypeface(tf_pala);
        dialogOne = new DialogOne(this, false, 13);

        lv = (ListView) findViewById(R.id.lv_m2m_list);

        Set<String> devices;
        TreeMap devices_map = new TreeMap<String, String>();
        ArrayList<String> al_ = new ArrayList<String>();
        SharedPreferences mPrefs;
        mPrefs = getSharedPreferences("devices", 0);
        devices = mPrefs.getStringSet("devices", devices_map.keySet());

        Iterator itr = devices.iterator();
        Map.Entry me;
        String ip_ = "", device_name = "";
        while (itr.hasNext()) {
            device_name = (String) itr.next();
            al_.add(device_name);
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%% device_name" + device_name);


        }

        list_adapter = new ListAdapter(this, 0, true, al_, 7);
        lv.setAdapter(list_adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {

                final TextView item = (TextView) view
                        .findViewById(R.id.btn_m2m_on_off_btn);

                TextView btnn_ = (TextView) view
                        .findViewById(R.id.btn_m2m_on_off_btn);

                // item.setBackgroundResource(R.drawable.circlebtn_red_with_border);

                if (item.getText().toString().contains("OFF")) {
                    {
                        item.setText("ON");
                        item.setBackground(getBaseContext().getResources()
                                .getDrawable(
                                        R.drawable.circlebtn_limon_with_border));
                    }
                } else if (item.getText().toString().contains("ON")) {
                    {
                        item.setText("OFF");
                        item.setBackground(getBaseContext().getResources()
                                .getDrawable(
                                        R.drawable.circlebtn_red_with_border));
                    }
                } else
                    item.setText("NA");


                item.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (item.getText().toString().contains("OFF")) {
                            {
                                item.setText("ON");
                                item.setBackground(getBaseContext().getResources()
                                        .getDrawable(
                                                R.drawable.circlebtn_limon_with_border));
                            }
                        } else if (item.getText().toString().contains("ON")) {
                            {
                                item.setText("OFF");
                                item.setBackground(getBaseContext().getResources()
                                        .getDrawable(
                                                R.drawable.circlebtn_red_with_border));
                            }
                        } else
                            item.setText("NA");
                    }
                });

            }
        });

        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(M2MActivity.this,
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

                intent = new Intent(M2MActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_advanced:

                intent = new Intent(M2MActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(M2MActivity.this, SensorReadingsActivity.class);
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
        dialogOne.show();
        return;
    }


}
