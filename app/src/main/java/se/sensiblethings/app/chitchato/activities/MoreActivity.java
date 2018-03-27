package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.adapters.ListAdapter;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.DialogOne;
import se.sensiblethings.disseminationlayer.communication.Message;

public class MoreActivity extends Activity {

    // Englishs.> 0
    protected int lang_number = 0;
    TextView tv, tv_1, tv_2, tv_3, tv_4, tv_5, tv_6;
    ListView lv_1, lv_2, lv_3, lv_4;
    Button btn;
    ImageButton face_book, google_plus, twitter, dribble, global_bs_imgbtn, imgbtn_broad_cast;
    long l;
    String temp;
    ArrayList activenodes;
    String[] a;
    Message message;
    String nodes = "%";
    GridView gv_1;
    SharedPreferences mPrefs, mPrefs_;
    String language = "En";
    ListAdapter list_adapter_3;
    private boolean LA_VISISTED = false;
    private Customizations custom;
    private DialogOne dialog_one, dialog_two;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("language", 0);
        language = mPrefs.getString("language", "En");
        mPrefs_ = getSharedPreferences("myprofile", 0);
        String peer_name = mPrefs_.getString("peer_name", "Unknown Peer");
        custom = new Customizations(this, -1);
        setContentView(custom.getMoreScreen());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle("");
        final Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
        final Typeface tf_pala = Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");
        btn = (Button) findViewById(R.id.btn_more_screen_one);
        tv = (TextView) findViewById(R.id.tv_empty_bar_m2mscreen);
        tv_1 = (TextView) findViewById(R.id.tv_more_screen_one);
        tv_2 = (TextView) findViewById(R.id.tv_more_screen_two);
        tv_3 = (TextView) findViewById(R.id.tv_more_screen_three);
        tv_4 = (TextView) findViewById(R.id.tv_more_screen_four);
        tv_6 = (TextView) findViewById(R.id.tv_more_screen_one_);
        // lv_1 = (ListView) findViewById(R.id.lv_social);
        //lv_2 = (ListView) findViewById(R.id.lv_ads);
        lv_3 = (ListView) findViewById(R.id.lv_settings);

        global_bs_imgbtn = (ImageButton) findViewById(R.id.imageButton1);
        imgbtn_broad_cast = (ImageButton) findViewById(R.id.imageButton_ads);


        btn.setTypeface(tf_pala);
        tv.setTypeface(tf);
        tv_1.setTypeface(tf_pala);
        tv_2.setTypeface(tf_pala);
        tv_3.setTypeface(tf_pala);
        tv_4.setTypeface(tf);
        tv_6.setTypeface(tf_pala);

        face_book = (ImageButton) findViewById(R.id.imgv_more_screen_fb);
        google_plus = (ImageButton) findViewById(R.id.imgv_more_screen_gp);
        twitter = (ImageButton) findViewById(R.id.imgv_more_screen_tt);
        tv_1.setText(peer_name);


        list_adapter_3 = new ListAdapter(this);
        String[] settings = getResources().getStringArray(R.array.settings_en);

        ArrayList<String> al_ = new ArrayList<String>();
        for (String temp : settings) {
            al_.add(temp);
        }

        // ///

        list_adapter_3 = new ListAdapter(this, 0, true, al_, 8);
        list_adapter_3.setLanguage_code(custom.getLanguage());
        lv_3.setAdapter(list_adapter_3);

        String[] languages = getResources().getStringArray(R.array.settings_en);

        ArrayList<String> al_lv = new ArrayList<String>();
        for (String temp : languages) {
            al_lv.add(temp);
        }

        dialog_one = new DialogOne(this, false, 5);
        dialog_two = new DialogOne(this, false, 13);
        lv_3.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {

                if (position == 0) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_ = inflater.inflate(R.layout.dialog_one, null);
                    TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);


                    if (custom.getLanguage() == 0) {
                        displayCustomizedToast(MoreActivity.this, getResources().getString(R.string.string_more_one_0));
                        tv_0_0_.setText( getResources().getString(R.string.string_more_one_0));
                        tv_0_0_.setTypeface(tf);
                        tv_0_0_.setTextColor(getResources().getColor(R.color.chitchato_mebratu_blue));
                    } else if (custom.getLanguage() == 1) {
                        displayCustomizedToast(MoreActivity.this, getResources().getString(R.string.string_more_one_0_sv));
                        tv_0_0_.setText( getResources().getString(R.string.string_more_one_0));
                        tv_0_0_.setTypeface(tf);
                        tv_0_0_.setTextColor(getResources().getColor(R.color.chitchato_mebratu_blue));
                    } else if (custom.getLanguage() == 2) {
                        displayCustomizedToast(MoreActivity.this, getResources().getString(R.string.string_more_one_0_sp));
                        tv_0_0_.setText( getResources().getString(R.string.string_more_one_0));
                        tv_0_0_.setTypeface(tf);
                        tv_0_0_.setTextColor(getResources().getColor(R.color.chitchato_mebratu_blue));

                    } else if (custom.getLanguage() == 3) {
                        displayCustomizedToast(MoreActivity.this, getResources().getString(R.string.string_more_one_0_pr));
                        tv_0_0_.setText( getResources().getString(R.string.string_more_one_0));
                        tv_0_0_.setTypeface(tf);
                        tv_0_0_.setTextColor(getResources().getColor(R.color.chitchato_mebratu_blue));

                    } else if (custom.getLanguage() == 4) {
                        displayCustomizedToast(MoreActivity.this, getResources().getString(R.string.string_more_one_0_fr));
                        tv_0_0_.setText( getResources().getString(R.string.string_more_one_0));
                        tv_0_0_.setTypeface(tf);
                        tv_0_0_.setTextColor(getResources().getColor(R.color.chitchato_mebratu_blue));

                    } else {
                        displayCustomizedToast(MoreActivity.this, getResources().getString(R.string.string_more_one_0));
                        tv_0_0_.setText( getResources().getString(R.string.string_more_one_0));
                        tv_0_0_.setTypeface(tf);
                        tv_0_0_.setTextColor(getResources().getColor(R.color.chitchato_mebratu_blue));

                    }

                    Toast toast = new Toast(MoreActivity.this);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(view_);
                    toast.show();


                    String url_1 = "https://buy.coinbase.com?code=9023urn3f8934hg34&amount=10&address=1JcssT2Cr2xhnfcYscLL1bZPbojg4rUC2X&crypto_currency=BTC";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url_1));
                    startActivity(intent);


                }
                if (position == 1) {
                    Intent intent = new Intent(MoreActivity.this,
                            AddBootStrapActivity.class);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(MoreActivity.this,
                            RegisterActivity.class);
                    startActivity(intent);
                } else if (position == 3) {
                    Intent intent = new Intent(MoreActivity.this,
                            AddDevicesActivity.class);
                    startActivity(intent);
                } else if (position == 4) {
                    String url_1 = "http://contexter.mobi";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url_1));
                    startActivity(intent);
                } else if (position == 5) {
                    Intent intent = new Intent(MoreActivity.this,
                            GroupContainerActivity.class);
                    intent.putExtra("INDEX", "NOTIFICATIONS");
                    startActivity(intent);

                } else if (position == 6) {
                    Intent intent = new Intent(MoreActivity.this,
                            GroupContainerActivity.class);
                    intent.putExtra("INDEX", "M_AND_C");
                    startActivity(intent);
                } else if (position == 7) {
                    Intent intent = new Intent(MoreActivity.this,
                            InitializeLocalBTSRPActivity.class);
                    startActivity(intent);

                } else if (position == 8) {
                    if (custom.getLanguage() == 0)
                        displayCustomizedToast(MoreActivity.this, getResources().getString(R.string.string_create_group_two_0));
                    else if (custom.getLanguage() == 1)
                        displayCustomizedToast(MoreActivity.this, getResources().getString(R.string.string_create_group_two_0_sv));
                    else if (custom.getLanguage() == 2)
                        displayCustomizedToast(MoreActivity.this, getResources().getString(R.string.string_create_group_two_0_sp));
                    else if (custom.getLanguage() == 3)
                        displayCustomizedToast(MoreActivity.this, getResources().getString(R.string.string_create_group_two_0_pr));
                    else if (custom.getLanguage() == 4)
                        displayCustomizedToast(MoreActivity.this, getResources().getString(R.string.string_create_group_two_0_fr));
                    else
                        displayCustomizedToast(MoreActivity.this, getResources().getString(R.string.string_create_group_two_0));
                } else if (position == 9) {
                    //dialog_one.show();
                    Intent intent = new Intent(MoreActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "OTHER");
                    startActivity(intent);


                }

            }
        });
        global_bs_imgbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent _intent = new Intent(MoreActivity.this, GlobalBootStrapsActivity.class);
                startActivity(_intent);
            }
        });


        imgbtn_broad_cast.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreActivity.this,
                        AdvertiseLocalGroupActivity.class);
                startActivity(intent);
            }
        });

        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(MoreActivity.this,
                        ChoicesActivity.class);
                startActivity(intent);
            }
        });

        google_plus.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url_1 = "http://chitchato.com";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url_1));
                startActivity(intent);

            }
        });
        face_book.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url_1 = "https://www.facebook.com/chitchat0";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url_1));
                startActivity(intent);

            }
        });
        twitter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url_1 = "http://twitter.com";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url_1));
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

                intent = new Intent(MoreActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_advanced:

                intent = new Intent(MoreActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(MoreActivity.this, SensorReadingsActivity.class);
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

    }

    // @Override
    public void onClick(View arg0) {

    }


    @Override
    public void onBackPressed() {
        dialog_two.show();
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
                toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 150);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view_);
                toast.show();

            }
        });

    }

}
