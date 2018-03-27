package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.adapters.ListAdapter;
import se.sensiblethings.app.chitchato.context.ContextManager;
import se.sensiblethings.app.chitchato.context.SoundPressureLevel;
import se.sensiblethings.app.chitchato.extras.Busy;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.DrawGraphFour;
import se.sensiblethings.app.chitchato.extras.DrawGraphOne;
import se.sensiblethings.app.chitchato.extras.DrawGraphThree;
import se.sensiblethings.app.chitchato.extras.DrawGraphTwo;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public class SensorReadingsActivity extends Activity {

    // The Platform API

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
    SensibleThingsPlatform sensibleThingsPlatform;
    Communication communication;
    SensibleThingsNode sensiblethingsnode;
    SharedPreferences mPrefs;
    String language = "En";
    ListAdapter list_adapter;
    private boolean LA_VISISTED = false;
    private Customizations custom;
    // Englishs.> 0
    protected int lang_number = 0;

    private DrawGraphOne dg_view_acc;
    private DrawGraphTwo dg_view_limu;
    private DrawGraphThree dg_view_sound;
    private DrawGraphFour dg_view_gps;
    private Float interval = 0.0f;
    public static boolean READ_SENSORS = false;
    SensorManager mSensorManager;
    ContextManager context_manager;
    Busy busy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("language", 0);
        language = mPrefs.getString("language", "En");
        custom = new Customizations(this, -1);

        setContentView(custom.getSensorReadingscreen());
        setTitle("");
        // if(width <= 480) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // }
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");

        tv = (TextView) findViewById(R.id.tv_empty_bar_m2mscreen);
        tv.setTypeface(tf);
        // lv = (ListView) findViewById(R.id.lv_m2m_list);

        String[] languages = getResources().getStringArray(R.array.appliances);

        ArrayList<String> al_ = new ArrayList<String>();
        for (String temp : languages) {
            al_.add(temp);
        }


        busy = new Busy(this, false, -1);
        busy.setCancelable(true);
        busy.setDialog_title(" Initiating  ");
        busy.setCancelable(true);
        busy.show();


        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        context_manager = new se.sensiblethings.app.chitchato.context.ContextManager(
                this, "", mSensorManager);
        BKGroundTask_0 background_task = new BKGroundTask_0();
        background_task.execute();

        //
        ArrayList<String> al_sense = new ArrayList<String>();
        al_sense.add("20.00");
        al_sense.add("0.00");
        al_sense.add("-10.00");
        al_sense.add("-30.00");
        al_sense.add("-60.00");
        al_sense.add("-70.00");
        al_sense.add("-40.00");
        al_sense.add("-90.00");
        al_sense.add("-5.00");
        al_sense.add("0.0");
        al_sense.add("10.00");

        dg_view_acc = (DrawGraphOne) findViewById(R.id.custview_acc);
        dg_view_limu = (DrawGraphTwo) findViewById(R.id.custview_limo);
        dg_view_sound = (DrawGraphThree) findViewById(R.id.custview_sound);
        dg_view_gps = (DrawGraphFour) findViewById(R.id.custview_gps);

        dg_view_acc.setDailyValue(al_sense);
        dg_view_acc.setMaxValue("-100.00");
        dg_view_acc.setMinValue("-100.00");
        dg_view_acc.setContextManager(context_manager);
        dg_view_acc.setReadSensors(READ_SENSORS);

        interval = Float.valueOf("20");

        dg_view_acc.setValueOne("-200.00");
        dg_view_acc.setValueTwo("-180.00");
        dg_view_acc.setValueThree("-160.00");
        dg_view_acc.setValueFour("-140.00");
        dg_view_acc.setValueFive("-120.00");
        dg_view_acc.setValueSix("-100.00");
        dg_view_acc.setValueSeven("-80");
        dg_view_acc.setValueEight("-60");
        dg_view_acc.setValueNine("-40");
        dg_view_acc.setValueTen("-20");
        // dg_view_acc.setMonth("Add ");

        dg_view_acc.setDailyValue(al_sense);
        dg_view_acc.setMaxValue("-100.00");
        dg_view_acc.setMinValue("-100.00");

        dg_view_limu.setValueOne("-200.00");
        dg_view_limu.setValueTwo("-180.00");
        dg_view_limu.setValueThree("-160.00");
        dg_view_limu.setValueFour("-140.00");
        dg_view_limu.setValueFive("-120.00");
        dg_view_limu.setValueSix("-100.00");
        dg_view_limu.setValueSeven("-80");
        dg_view_limu.setValueEight("-60");
        dg_view_limu.setValueNine("-40");
        dg_view_limu.setValueTen("-20");
        dg_view_limu.setDailyValue(al_sense);
        dg_view_limu.setMaxValue("-100.00");
        dg_view_limu.setMinValue("-100.00");

        dg_view_sound.setValueOne("-200.00");
        dg_view_sound.setValueTwo("-180.00");
        dg_view_sound.setValueThree("-160.00");
        dg_view_sound.setValueFour("-140.00");
        dg_view_sound.setValueFive("-120.00");
        dg_view_sound.setValueSix("-100.00");
        dg_view_sound.setValueSeven("-80");
        dg_view_sound.setValueEight("-60");
        dg_view_sound.setValueNine("-40");
        dg_view_sound.setValueTen("-20");
        dg_view_sound.setDailyValue(al_sense);
        dg_view_sound.setMaxValue("-100.00");
        dg_view_sound.setMinValue("-100.00");

        dg_view_gps.setValueOne("-200.00");
        dg_view_gps.setValueTwo("-180.00");
        dg_view_gps.setValueThree("-160.00");
        dg_view_gps.setValueFour("-140.00");
        dg_view_gps.setValueFive("-120.00");
        dg_view_gps.setValueSix("-100.00");
        dg_view_gps.setValueSeven("-80");
        dg_view_gps.setValueEight("-60");
        dg_view_gps.setValueNine("-40");
        dg_view_gps.setValueTen("-20");
        dg_view_gps.setDailyValue(al_sense);
        dg_view_gps.setMaxValue("-100.00");
        dg_view_gps.setMinValue("-100.00");

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

                intent = new Intent(SensorReadingsActivity.this,
                        ChoicesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_advanced:

                intent = new Intent(SensorReadingsActivity.this,
                        PurgeActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_sensor_en:

                intent = new Intent(SensorReadingsActivity.this,
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
        READ_SENSORS = true;
        dg_view_acc.setReadSensors(READ_SENSORS);

    }

    @Override
    protected void onStop() {
        super.onStop();
        READ_SENSORS = false;
        SoundPressureLevel.FLAG = true;
        dg_view_acc.setReadSensors(READ_SENSORS);
        context_manager.spl.stopRecorder();

    }

    // @Override
    public void onClick(View arg0) {

    }

    public class BKGroundTask_0 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... arg0) {
            try {
                while (READ_SENSORS) {
                    SensorReadingsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String temp_location;
                            dg_view_acc.setContextManager(context_manager);
                            dg_view_acc.setPoint_y(Float.parseFloat(context_manager
                                    .getUserContext().getAcce()));
                            dg_view_limu.setContextManager(context_manager);
                            dg_view_limu.setLimu(Float.parseFloat(context_manager
                                    .getUserContext().getLimunosity()));
                            dg_view_sound.setContextManager(context_manager);
                            dg_view_sound.setSound(Float.parseFloat(context_manager
                                    .getUserContext().getSoundLevel()));
                            dg_view_gps.setContextManager(context_manager);
                            dg_view_gps.setLocation(context_manager.getUserContext()
                                    .getLocation());

                            temp_location = context_manager.getUserContext()
                                    .getLocation();
                            if (temp_location.contains(",")) {

                                dg_view_gps.setLongitude(Float
                                        .parseFloat(context_manager.getUserContext()
                                                .getLocation().split(",")[0]));
                                dg_view_gps.setLatitude(Float
                                        .parseFloat(context_manager.getUserContext()
                                                .getLocation().split(",")[1]));
                                dg_view_gps.setAddress(context_manager.getUserContext()
                                        .getAddress());
                            }


                            if (custom.getLanguage() == 1) {
                                dg_view_acc
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_acceleration_sv));
                                dg_view_sound
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_sound_sv));
                                dg_view_limu
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_limunosity_sv));
                                dg_view_gps
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_location_sv));

                            } else if (custom.getLanguage() == 2) {

                                dg_view_acc
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_acceleration_sp));
                                dg_view_sound
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_sound_sp));
                                dg_view_limu
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_limunosity_sp));
                                dg_view_gps
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_location_sp));

                            } else if (custom.getLanguage() == 3) {
                                dg_view_acc
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_acceleration_pr));
                                dg_view_sound
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_sound_pr));
                                dg_view_limu
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_limunosity_pr));
                                dg_view_gps
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_location_pr));

                            } else if (custom.getLanguage() == 4) {
                                dg_view_acc
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_acceleration_fr));
                                dg_view_sound
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_sound_fr));
                                dg_view_limu
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_limunosity_fr));
                                dg_view_gps
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_location_fr));

                            } else if (custom.getLanguage() == 5) {
                                dg_view_acc
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_acceleration_am));
                                dg_view_sound
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_sound_am));
                                dg_view_limu
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_limunosity_am));
                                dg_view_gps
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_location_am));

                            } else {
                                dg_view_acc
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_acceleration_en));
                                dg_view_sound
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_sound_en));
                                dg_view_limu
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_limunosity_en));
                                dg_view_gps
                                        .setSensor_name(getBaseContext()
                                                .getResources()
                                                .getString(
                                                        R.string.action_location_en));

                            }

                            if (Float.parseFloat(context_manager.getUserContext().getLimunosity()) != 0.0)
                                // System.out.print("Limunosity" + context_manager.getUserContext().getLimunosity());
                                busy.dismiss();
                            dg_view_acc.invalidate();
                            dg_view_limu.invalidate();
                            dg_view_sound.invalidate();
                            dg_view_gps.invalidate();

                        }
                    });

                    // System.out.println(" YY:" + dg_view_acc.getPoint_y());
                    Thread.sleep(1000);
                    if (!context_manager.getUserContext().getAddress().equalsIgnoreCase("Address not detected")) {
                        busy.dismiss();
                    }

                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

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
