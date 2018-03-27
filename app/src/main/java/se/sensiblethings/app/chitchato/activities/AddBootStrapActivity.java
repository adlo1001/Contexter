package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
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
import se.sensiblethings.disseminationlayer.communication.Message;

public class AddBootStrapActivity extends Activity {

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

    SharedPreferences mPrefs;
    Set<String> bootstraps;
    //bootstrap ip is public -- default --false
    boolean _BOOTSTRAP_IP_ = false;
    boolean _IS_NODE_BOOTSTRAP_ = false;
    ListAdapter list_adapter_1, list_adapter_2, list_adapter_3;
    private boolean LA_VISISTED = false;
    private Customizations custom;
    protected int lang_number = 0;
    protected TreeMap bs_map = new TreeMap<String, String>();
    public static String PREFERRED_IP = "0.0.0.0";
    private DialogOne dialog_one;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("bootstraps", 0);
        bootstraps = mPrefs.getStringSet("bootstraps", bs_map.keySet());
        _BOOTSTRAP_IP_ = mPrefs.getBoolean("BOOTSTRAP_IP", false);
        _IS_NODE_BOOTSTRAP_ = mPrefs.getBoolean("_IS_NODE_BOOTSTRAP_", false);
        PREFERRED_IP = mPrefs.getString("PREFERRED_IP", PREFERRED_IP);
        custom = new Customizations(this, -1);

        setContentView(custom.getBSScreen());
        setTitle("");

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
        Typeface tf_pala = Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");

        btn = (Button) findViewById(R.id.button_refresh_my_bs);
        btn_ = (Button) findViewById(R.id.button_clear_my_bs);
        btn_exit = (Button) findViewById(R.id.button_exit_my_bs);
        tv = (TextView) findViewById(R.id.tv_empty);
        tv_1 = (TextView) findViewById(R.id.tv_bootstrp_one);
        tv_2 = (TextView) findViewById(R.id.tv_bootstrp_two);
        tv_3 = (TextView) findViewById(R.id.tv_default_bootstrap_ip);
        tv_4 = (TextView) findViewById(R.id.tv_bootstrp_name);
        edt_1 = (EditText) findViewById(R.id.edt_peerPro_name);
        dialog_one = new DialogOne(this, false, 130);

        // lv_1 = (ListView) findViewById(R.id.lv_social);

        btn.setTypeface(tf_pala);
        btn_.setTypeface(tf_pala);
        tv.setTypeface(tf);
        tv_1.setTypeface(tf);
        tv_2.setTypeface(tf);
        tv_4.setTypeface(tf);
        edt_1.setTypeface(tf);
        btn_exit.setTypeface(tf_pala);

        spinner = (Spinner) findViewById(R.id.spinner_my_profile_portofolio);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item_one);

        //adapter.add("185.102.215.188");
        //adapter.add("193.10.119.42");

        //PREFERRED_IP = tv.getText().toString();
        Iterator itr = bootstraps.iterator();
        Map.Entry me;
        String ip_ = "";
        while (itr.hasNext()) {
            ip_ = (String) itr.next();
            bs_map.put(ip_, ip_);
            adapter.add(ip_);
        }
        spinner.setAdapter(adapter);


        // Display Preferred IP
        if (PREFERRED_IP.equalsIgnoreCase("0.0.0.0")) ;
            //PREFERRED_IP = adapter.getItem(0);
        else {
            spinner.setSelection(adapter.getPosition(PREFERRED_IP));
            //adapter.insert(PREFERRED_IP,0);
        }
        tv_3.setText(PREFERRED_IP);

        tv_2.setText("BootStraps" + "[" + bootstraps.size() + "]");
        // Populate Spinner


        String ip_address_pattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        final String ip_address_pattern_public = "^([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(?<!172\\." +
                "(16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31))(?<!127)(?<!^10)(?<!^0)\\.([0-9]|[1-9]" +
                "[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(?<!192\\.168)(?<!172\\.(16|17|18|19|20|21|22|23|24|25" +
                "|26|27|28|29|30|31))\\.([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9]" +
                "{2}|2[0-4][0-9]|25[0-5])(?<!\\.0$)(?<!\\.255$)$";

        final Pattern pattern = Pattern.compile(ip_address_pattern);
        final Pattern pattern_public = Pattern.compile(ip_address_pattern_public);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                tv_3.setText(tv.getText());
                PREFERRED_IP = tv_3.getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Matcher matcher, matcher_public_ip;
                if (bootstraps == null)
                    bootstraps = bs_map.keySet();

                String temp_ip_address = edt_1.getText().toString();
                matcher = pattern.matcher(temp_ip_address);
                if (matcher.matches() || (temp_ip_address.trim().isEmpty() && bootstraps.size() > 0)) {
                    if (temp_ip_address.trim().isEmpty() && bootstraps.size() > 0) {
                        bs_map.remove(PREFERRED_IP);
                        bs_map.put(PREFERRED_IP, PREFERRED_IP);
                    } else {
                        bs_map.put(temp_ip_address, temp_ip_address);
                        PREFERRED_IP = temp_ip_address;
                    }
                    bootstraps = bs_map.keySet();
                    matcher_public_ip = pattern_public.matcher(temp_ip_address);
                    if (matcher_public_ip.matches())
                        _BOOTSTRAP_IP_ = true;
                    SharedPreferences.Editor ed = mPrefs.edit();
                    ed.putStringSet("bootstraps", bootstraps);
                    ed.putBoolean("BOOTSTRAP_IP", _BOOTSTRAP_IP_);
                    ed.putString("PREFERRED_IP", PREFERRED_IP);
                    ed.putBoolean("_IS_NODE_BOOTSTRAP_", false);
                    custom.set_IS_NODE_BOOTSTRAP_(false);
                    ed.commit();

                    displayCustomizedToast(AddBootStrapActivity.this, "BootStrap List Updated!");
                    Intent intent = new Intent(AddBootStrapActivity.this,
                            AddBootStrapActivity.class);
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
                bootstraps.clear();
                _BOOTSTRAP_IP_ = false;

                ed.putStringSet("bootstraps", bootstraps);
                ed.putBoolean("BOOTSTRAP_IP", _BOOTSTRAP_IP_);
                ed.putString("PREFERRED_IP", "");
                ed.commit();

                displayCustomizedToast(AddBootStrapActivity.this, "BootStrap Removed!");
                Intent intent = new Intent(AddBootStrapActivity.this,
                        AddBootStrapActivity.class);
                startActivity(intent);

            }
        });


        // Exit BootStrap
        btn_exit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(AddBootStrapActivity.this,
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

                intent = new Intent(AddBootStrapActivity.this,
                        ChoicesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_advanced:

                intent = new Intent(AddBootStrapActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(AddBootStrapActivity.this,
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

    public void displayCustomizedToast(Context _context_, String message) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_ = inflater.inflate(R.layout.dialog_one, null);
        TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);
        tv_0_0_.setText(message);
        Toast toast = new Toast(_context_);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view_);
        toast.show();
    }
}
