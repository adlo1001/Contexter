package se.sensiblethings.app.chitchato.extras;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.activities.WelcomeActivity;
import se.sensiblethings.disseminationlayer.communication.Message;

public class SplashScreen extends Activity {

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
        setContentView(R.layout.splashzero);
        setTitle("");


        ImageView img = (ImageView) findViewById(R.id.imageView2);

        if (custom.IS_FIRST_TIME_LOGIN()) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.move_tv_to_top_one_sec);
            img.setAnimation(animation);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this,
                            WelcomeActivity.class);

                    try {
                        Thread.sleep(1900);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            };
            Thread _mthread = new Thread(runnable);
            _mthread.start();
        } else {
            Intent intent = new Intent(SplashScreen.this,
                    WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.chatscreen, menu);
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

    // @Override
    public void onClick(View arg0) {

    }

}
