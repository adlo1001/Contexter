package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.adapters.ListAdapter;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.DialogOne;
import se.sensiblethings.app.chitchato.extras.WifiApiManager;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

/**
 * Created by user on 10/31/2016.
 */


public class GlobalBootStrapsActivity extends Activity {

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
    Set<String> bootstraps;

    //bootstrap ip is public -- default --false
    boolean _BOOTSTRAP_IP_ = false;
    ListAdapter list_adapter_1, list_adapter_2, list_adapter_3;
    private boolean LA_VISISTED = false;
    private Customizations custom;
    protected int lang_number = 0;
    protected TreeMap bs_map = new TreeMap<String, String>();
    private DialogOne dialog_one;


    WifiApiManager wifiApManager;
    List<ScanResult> _available_ssids_result;
    ArrayList<HashMap<String, String>> _available_ssids_ = new ArrayList<HashMap<String, String>>();
    String ITEM_KEY = "key";
    int _num_of_ssids_ = 0;
    boolean exit = false;
    ArrayList<String> array_list_ = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("bootstraps", 0);
        bootstraps = mPrefs.getStringSet("bootstraps", bs_map.keySet());
        _BOOTSTRAP_IP_ = mPrefs.getBoolean("BOOTSTRAP_IP", false);
        custom = new Customizations(this, -1);

        setContentView(custom.getGlobalBSScreen());
        setTitle("");
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
        Typeface tf_pala = Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");
        btn_exit = (Button) findViewById(R.id.button_bootstrp_exit);
        tv = (TextView) findViewById(R.id.tv_empty_bar_m2mscreen);
        lv_1 = (ListView) findViewById(R.id.lv_bootstrap_list);
        dialog_one = new DialogOne(this, false, 130);

        btn_exit.setTypeface(tf_pala);
        tv.setTypeface(tf);


        array_list_.add("193.10.119.42");
        list_adapter_1 = new ListAdapter(this, 0, true, array_list_, 14);
        lv_1.setAdapter(list_adapter_1);


        // begin background task -- scan if there are any adverts
        //BKGroundTask_0_scan_ads back_ground_task_0_scan_ads = new BKGroundTask_0_scan_ads();
        //back_ground_task_0_scan_ads.execute();



        wifiApManager = new WifiApiManager(this);


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

        // Exit BootStrap
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(GlobalBootStrapsActivity.this,
                        MoreActivity.class);
                exit = true;
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


    public ArrayList<HashMap<String, String>> getListofSSID(final WifiManager _wifi_manager_) {
        _available_ssids_result = new ArrayList<ScanResult>();
        if (!_wifi_manager_.isWifiEnabled()) {
            _wifi_manager_.setWifiEnabled(true);

        }
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                _available_ssids_result = _wifi_manager_.getScanResults();
                _num_of_ssids_ = _available_ssids_result.size();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


        _wifi_manager_.startScan();

        _num_of_ssids_ = _num_of_ssids_ - 1;
        while (_num_of_ssids_ >= 0) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put(ITEM_KEY, _available_ssids_result.get(_num_of_ssids_).SSID + " " + _available_ssids_result.get(_num_of_ssids_).capabilities);
            _available_ssids_.add(item);

            System.out.print("get Scan Results: " + _available_ssids_result.get(_num_of_ssids_).SSID);

        }

        return _available_ssids_;
    }


    public void displayCustomizedToast(Context _context_, String message) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_ = inflater.inflate(R.layout.dialog_one, null);
        TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);
        tv_0_0_.setText(message);
        Toast toast = new Toast(_context_);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view_);
        toast.show();
    }


    /*Keep Scanning Advs*/
    public class BKGroundTask_0_scan_ads extends AsyncTask<String, String, String> {
        public BKGroundTask_0_scan_ads() {

        }

        @Override
        protected String doInBackground(String... arg0) {
            ArrayList<String> temp_ar = new ArrayList<String>();
            try {
                while (!exit) {
                    ArrayList<HashMap<String, String>> _arr_list_ = getListofSSID(wifiApManager.get_mWifiManager());
                    if (_arr_list_.size() > 0) {
                        HashMap<String, String> item_0_0 = _arr_list_.get(0);
                        if (item_0_0.size() > 0) {
                            array_list_.add(item_0_0.get("key"));
                            System.out.println("__________________________________item.key:" + item_0_0.get("key"));

                        }
                        GlobalBootStrapsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lv_1.setAdapter(list_adapter_1);
                            }
                        });
                    }


                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Intent intent = new Intent(GlobalBootStrapsActivity.this,
                        ErrorActivity.class);
                intent.putExtra("error",
                        "System Error Encountered ");
                startActivity(intent);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}

