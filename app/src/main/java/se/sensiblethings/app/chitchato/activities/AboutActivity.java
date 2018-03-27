package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.disseminationlayer.communication.Message;

public class AboutActivity extends Activity {

    TextView tv;
    EditText edt;
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
        setContentView(custom.getAboutScreen());
        setTitle("");

        tv = (TextView) findViewById(R.id.tv_empty_bar_aboutscreen);
        edt = (EditText) findViewById(R.id.edt_about_box);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView_);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");

        Typeface tf_pala = Typeface.createFromAsset(getAssets(),
                "fonts/pala.ttf");

        Bundle extras = getIntent().getExtras();
        String cat = extras.getString("CAT");
        if(cat!=null)
            if(cat.equals("COPYRIGHT"))
                tv.setText("Copyright");

        tv.setTypeface(tf);
        edt.setTypeface(tf_pala);
        scrollView.setVisibility(View.GONE);
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE)
            scrollView.setVisibility(View.INVISIBLE);

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
                intent = new Intent(AboutActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_advanced:

                intent = new Intent(AboutActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(AboutActivity.this,
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

    // @Override
    public void onClick(View arg0) {

    }

}
