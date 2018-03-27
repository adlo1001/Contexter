package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.chitchato_services.MyStartServiceReceiver;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerNode;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.kernel.Groups;
import se.sensiblethings.app.chitchato.kernel.PublicChats;
import se.sensiblethings.disseminationlayer.communication.Message;

public class WelcomeActivity extends Activity {


    protected SharedPreferences mPrefs;
    TextView tv, textView, tv_, tv_3, tv_4, tv_5;
    Button btn;
    RadioButton rd_btn_1, rd_btn_2;
    LinearLayout ll_1, ll_2, ll;
    long l;
    String temp;
    ArrayList activenodes;
    String[] a;
    Message message;
    String nodes = "%";
    ListView lv_1, lv_2;
    GridView gv_1;
    Customizations custom;
    Intent service_intent = null;
    android.os.Messenger mPlatformManagerNodeMessenger;
    boolean _BOUND = false;
    ArrayList<String> temp_ar;
    String device_uniqe_number = "00000";
    private boolean ordinary_chat = true, context_chat = false;
    private Groups groups;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mPlatformManagerNodeMessenger = new Messenger(service);
            _BOUND = true;

        }

        public void onServiceDisconnected(ComponentName className) {
            Toast.makeText(WelcomeActivity.this, "Disconnected",
                    Toast.LENGTH_SHORT).show();

            _BOUND = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences("preference_1", 0);

        ordinary_chat = mPrefs.getBoolean("OChat", true);
        context_chat = mPrefs.getBoolean("CChat", false);
        custom = new Customizations(this, -1);
        setContentView(custom.getWelComeScreen());
        setTitle("");


        this.btn = (Button) findViewById(R.id.btn_welcome_);
        this.tv = (TextView) findViewById(R.id.tv_welcomescreen);
        this.tv_ = (TextView) findViewById(R.id.tv_welcomescreen_2);
        this.rd_btn_1 = (RadioButton) findViewById(R.id.radio0_ordinary_chat_mode);
        this.rd_btn_2 = (RadioButton) findViewById(R.id.radio1_context_chat_mode);

        tv_3 = (TextView) findViewById(R.id.tv_welcomescreen_copyright);
        tv_4 = (TextView) findViewById(R.id.tv_welcomescreen_terms_policies_1);
        tv_5 = (TextView) findViewById(R.id.tv_welcomescreen_terms_policies_2);

        ll = (LinearLayout) findViewById(R.id.ll_welcomescreen);
        ll_1 = (LinearLayout) findViewById(R.id.ll_copy_right_welcome);
        ll_2 = (LinearLayout) findViewById(R.id.ll_terms_policies_welcome);
        ImageView img = (ImageView) findViewById(R.id.imageView2);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
        Typeface tf_pala = Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");

        tv.setTypeface(tf);
        btn.setTypeface(tf_pala);
        rd_btn_1.setTypeface(tf_pala);
        rd_btn_2.setTypeface(tf_pala);

        tv_3.setTypeface(tf_pala);
        tv_4.setTypeface(tf_pala);




        // Show welcome animation if not coming from inside the other activities
        Bundle extras = getIntent().getExtras();
        int from = -1;
        if (extras != null)
            from = extras.getInt("from", -1);
        if (from == -1) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.move_tv_to_top_one_sec);
            // ll.setAnimation(animation);
            tv.setAnimation(animation);
            tv_.setAnimation(animation);
        }
        // End --Welcome Animation

        String htmlString_rules = "<u>rules</u>";
        String htmlString_tips = "<u>tips</u>";
        //If the user is  not first timer-- then hide the agreements and terms
        if (custom.IS_FIRST_TIME_LOGIN()) {
            tv_4.setText("By selecting next you agree to our");
            ll_1.setVisibility(View.VISIBLE);
            ll_2.setVisibility(View.VISIBLE);
        } else {
            tv_4.setText("Read privacy " + Html.fromHtml(htmlString_rules) + " and user " + Html.fromHtml(htmlString_tips));
            // ll_2.setVisibility(View.GONE);
        }
        try {
            ///Change flags to restart services
            custom.setSERVICE_UP(false);
            custom.setST_UP(false);
            SharedPreferences _mPrefs_ = getSharedPreferences("myprofile", 0);
            SharedPreferences.Editor _ed_ = _mPrefs_.edit();
            _ed_.putBoolean("IS_SERVICE_UP", false);
            _ed_.putBoolean("IS_ST_UP", false);
            _ed_.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }


        service_intent = new Intent(this, PlatformManagerNode.class);
        if (!PlatformManagerNode.ST_PLATFORM_IS_UP) {
            if (isInternetAvailable()) startService(service_intent);
            else {
                // displayCustomizedToast(WelcomeActivity.this, "No Connection. Platform is down  ");
            }
        }


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                device_uniqe_number = telephonyManager.getSimSerialNumber();
                if (device_uniqe_number == null) device_uniqe_number = "00000";
                PublicChats _thread_0 = new PublicChats(getBaseContext(), device_uniqe_number, "nicks");
                temp_ar = _thread_0.getmPublicChat("*ALL#");
                //peers = new Peers(MainActivity.this);

                if (temp_ar == null) temp_ar = new ArrayList<String>();
            }
        };

        Thread _mthread = new Thread(runnable);
        _mthread.start();
        //
        btn.setOnClickListener(new OnClickListener() {
            Intent intent = new Intent(WelcomeActivity.this,
                    ChoicesActivity.class);


            @Override
            public void onClick(View arg0) {

                if (!custom.IS_FIRST_TIME_LOGIN() && temp_ar.size() > 0) {
                    intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.putExtra("INDEX", 1);
                }

                if (rd_btn_1.isChecked()) {
                    ordinary_chat = true;
                    context_chat = false;


                } else if (rd_btn_2.isChecked()) {
                    ordinary_chat = false;
                    context_chat = true;

                } else {
                    ordinary_chat = true;
                    context_chat = false;
                }


                //Mode
                SharedPreferences.Editor ed = mPrefs.edit();
                ed.putBoolean("OChat", ordinary_chat);
                ed.putBoolean("CChat", context_chat);
                ed.commit();


                //Start Service -- Platform-- checker-- checks if the Platform is up every 30 sec
                if (!custom.isSERVICE_UP()) {
                    // custom.set_IS_NODE_BOOTSTRAP_(false);
                    AlarmManager service = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent i = new Intent(WelcomeActivity.this, MyStartServiceReceiver.class);
                    PendingIntent pending = PendingIntent.getBroadcast(WelcomeActivity.this, 0, i,
                            PendingIntent.FLAG_CANCEL_CURRENT);
                    Calendar cal = Calendar.getInstance();
                    // start 10 seconds after
                    cal.add(Calendar.SECOND, 10);
                    // fetch every 30 seconds
                    service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                            cal.getTimeInMillis(), 1000 * 30, pending);
                }
                ///////End ////////

                //If the user is  not first timer-- then hide the agreements and terms
                custom.setFIRST_TIME_LOGIN(false);
                SharedPreferences _mPrefs_ = getSharedPreferences("myprofile", 0);
                SharedPreferences.Editor ed__ = _mPrefs_.edit();
                ed__.putBoolean("IS_FIRST_TIME_LOGIN", false);
                ed__.commit();


                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        tv_5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_2 = "http://contexter.mobi/terms.html";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url_2));
                startActivity(intent);
            }
        });
        tv_4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_2 = "http://contexter.mobi/terms.html";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url_2));
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // /getMenuInflater().inflate(R.menu.chatscreen, menu);
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
    public void onResume() {
        super.onResume();
        // custom = new Customizations(this, -1);
        // setContentView(custom.getWelComeScreen());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    public void onStop() {
        super.onStop();

        System.out.println("I am within Stop");
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("OChat", ordinary_chat);
        ed.putBoolean("CChat", context_chat);
        ed.commit();

    }

    // Check if the Internet is Available
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

}