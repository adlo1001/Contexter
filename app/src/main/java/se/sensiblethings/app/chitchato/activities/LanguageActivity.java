package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import se.sensiblethings.disseminationlayer.communication.Message;

public class LanguageActivity extends Activity {
    // Englishs.> 0
    protected int lang_number = 0;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("language", 0);
        language = mPrefs.getString("language", "En");
        custom = new Customizations(this, -1);
        setContentView(custom.getLanguageScreen());
        setTitle("");
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");

        tv = (TextView) findViewById(R.id.tv_empty_bar_languagescreen);
        tv.setTypeface(tf);
        lv = (ListView) findViewById(R.id.lv_language);

        String[] languages = getResources().getStringArray(R.array.languages);

        ArrayList<String> al_ = new ArrayList<String>();
        for (String temp : languages) {
            al_.add(temp);
        }

        // ///

        list_adapter = new ListAdapter(this, 0, true, al_, 2);
        list_adapter.setPosition(this.getLanguage(language));
        lv.setAdapter(list_adapter);
        lv.setVisibility(View.VISIBLE);

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {

                list_adapter.setPosition(position);
                lv.setAdapter(list_adapter);
                lang_number = position;
                Log.w("Checked-->", position + "");

                SharedPreferences.Editor ed = mPrefs.edit();


                if (lang_number == 0)
                    language = "En";
                else if (lang_number == 1) {
                    language = "Sv";
                    // startActivity(new Intent(Settings.ACTION_SETTINGS));
                } else if (lang_number == 2)
                    language = "Sp";
                else if (lang_number == 3)
                    language = "Pr";
                else if (lang_number == 4)
                    language = "Fr";


                ed.putString("language", language);
                ed.commit();


                /*
                displayCustomizedToast(LanguageActivity.this, "Add Your Keyboard.");
                Intent intent_00 = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
                startActivity(intent_00);
               */


                Intent intent = new Intent(LanguageActivity.this,
                        LanguageActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                intent = new Intent(LanguageActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_advanced:
                intent = new Intent(LanguageActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(LanguageActivity.this,
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

        SharedPreferences.Editor ed = mPrefs.edit();
        String language = "En";
        if (lang_number == 0)
            language = "En";
        else if (lang_number == 1)
            language = "Sv";
        else if (lang_number == 2)
            language = "Sp";
        else if (lang_number == 3)
            language = "Pr";
        else if (lang_number == 4)
            language = "Fr";
        else if (lang_number == 5)
            language = "Am";
        mPrefs = getSharedPreferences("cache", 0);
        ed = mPrefs.edit();
        ed.putBoolean("LA_VISITED", true);
        ed.commit();
    }

    public int getLanguage(String lan) {
        int lang_serial = 0;
        if (lan.contains("Sv")) {
            lang_serial = 1;
        } else if (lan.contains("Sp")) {
            lang_serial = 2;
        } else if (lan.contains("Pr")) {
            lang_serial = 3;
        } else if (lan.contains("Fr")) {
            lang_serial = 4;
        } else if (lan.contains("Am")) {
            lang_serial = 5;
        } else
            ;
        return lang_serial;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LanguageActivity.this, ChoicesActivity.class);
        startActivity(intent);
        return;
    }


    public void displayCustomizedToast(final Context _context_, String message) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view_ = inflater.inflate(R.layout.dialog_one, null);
        TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);
        tv_0_0_.setText(message);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = new Toast(_context_);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view_);
                toast.show();
            }
        });
    }


}
