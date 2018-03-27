package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.adapters.ImageListAdapter;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerNode;
import se.sensiblethings.app.chitchato.extras.Busy;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.DialogOne;
import se.sensiblethings.app.chitchato.kernel.ChitchatoGroups;

public class ChoicesActivity extends Activity {
    protected SharedPreferences mPrefs, mPrefs_;
    protected boolean CHAT_MODE = true;
    TextView tv_1;
    Button btn_1, btn_2_;
    long l;
    String temp;
    ArrayList activenodes;
    String[] a;
    Button btn_1_, btn_2, btn_3, btn_4, btn_5, btn_5_, btn_6, btn_7, btn_8, btn_9, btn_10;
    String nodes = "%";
    GridView gv_1;
    boolean _BOUND = false;
    // Service Messagenger---
    android.os.Messenger mPlatformManagerNodeMessenger;
    android.os.Message msg = android.os.Message.obtain(null,
            PlatformManagerNode.NEW_MESSAGE_FLAG, 0, 0);
    android.os.Messenger mActivityMessenger = new Messenger(
            new ActivityHandler(this));
    ArrayList<String> temp_list_of_messages = new ArrayList<String>();
    Intent service_intent;
    ImageListAdapter imageListAdapter;
    PlatformManagerNode update_service;
    private ChitchatoGroups groups;
    private Customizations custom;
    private boolean MA_VISITED = false;
    private boolean CGA_VISITED = false;
    private boolean RA_VISITED = false;
    private boolean LA_VISITED = false;
    private boolean HA_VISITED = false;
    private boolean About_VISITED = false;
    private boolean SGA_VISITED = false;
    private String incoming_message = "#";
    private Busy busy;

    //private MediaPlayer mp_clicking;
    private DialogOne dialog_one, dialog_two, dialog_three;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mPlatformManagerNodeMessenger = new Messenger(service);
            _BOUND = true;

        }

        public void onServiceDisconnected(ComponentName className) {
            update_service = null;
            _BOUND = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("preference_1", 0);
        CHAT_MODE = mPrefs.getBoolean("OChat", true);

        mPrefs_ = getSharedPreferences("cache", 0);
        MA_VISITED = mPrefs.getBoolean("MA_VISITED", false);
        CGA_VISITED = mPrefs.getBoolean("CGA_VISITED", false);
        RA_VISITED = mPrefs.getBoolean("RA_VISITED", false);
        LA_VISITED = mPrefs.getBoolean("LA_VISITED", false);
        HA_VISITED = mPrefs.getBoolean("HA_VISITED", false);
        About_VISITED = mPrefs.getBoolean("About_VISITED", false);
        SGA_VISITED = mPrefs.getBoolean("SGA_VISITED", false);

        custom = new Customizations(this, -1);
        setContentView(custom.getChoicesScreen());
        setTitle("");
        tv_1 = (TextView) findViewById(R.id.tv_empty_bar_choicescreen);
        btn_1 = (Button) findViewById(R.id.button_choicescreen_profile_edit);
        btn_2_ = (Button) findViewById(R.id.m2m_btn);
        btn_6 = (Button) findViewById(R.id.btn_settings);
        btn_7 = (Button) findViewById(R.id.btn_sensors);
        btn_8 = (Button) findViewById(R.id.btn_purge);
        btn_9 = (Button) findViewById(R.id.img_btn_debug);
        btn_8 = (Button) findViewById(R.id.btn_purge);
        btn_10 = (Button) findViewById(R.id.img_btn_debug);
        dialog_one = new DialogOne(this, false, 9);
        dialog_two = new DialogOne(this, false, 14);
        dialog_three = new DialogOne(this, false, 1);

        if (custom.is_NOTIFICATIONS_AVAILABLE()) {

        }
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");
        tv_1.setTypeface(tf);
        btn_1.setTypeface(tf);
        btn_9.setTypeface(tf);
        btn_6.setTypeface(tf);
        btn_7.setTypeface(tf);
        btn_8.setTypeface(tf);
        btn_10.setTypeface(tf);

        // retrieve_existing_groups
        imageListAdapter = new ImageListAdapter(this,
                false, CHAT_MODE, 1);
        imageListAdapter.setLanguage(custom.getLanguage());

        // Cache-->recent history
        imageListAdapter.setAbout_VISITED(About_VISITED);
        imageListAdapter.setCGA_VISITED(CGA_VISITED);
        imageListAdapter.setHA_VISITED(HA_VISITED);
        imageListAdapter.setLA_VISITED(LA_VISITED);
        imageListAdapter.setMA_VISITED(MA_VISITED);
        imageListAdapter.setRA_VISITED(RA_VISITED);
        imageListAdapter.setSGA_VISITED(SGA_VISITED);

        gv_1 = (GridView) findViewById(R.id.gridView1_choice);
        gv_1.setAdapter(imageListAdapter);
        busy = new Busy(this, false, -1);

        // Tell ST is no up
        if (!custom.isST_UP()) {
            // dialog_three = new DialogOne(this, false, 0);
            //dialog_three.show();
        }


        service_intent = new Intent(this, PlatformManagerNode.class);


        /// bind service if Internet is available
        if (isInternetAvailable())
            bindService(service_intent, mConnection,
                    Context.BIND_AUTO_CREATE);
        else {
            if (custom.getLanguage() == 0) {
                displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_search_one_2));
            } else if (custom.getLanguage() == 1) {
                displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_search_one_2_sv));
            } else if (custom.getLanguage() == 2) {
                displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_search_one_2_sp));
            } else if (custom.getLanguage() == 3) {
                displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_search_one_2_pr));
            } else if (custom.getLanguage() == 4) {
                displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_search_one_2_fr));
            } else
                displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_search_one_2));
        }


        gv_1.setOnItemClickListener(new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> arg0, final View arg1,
                                                                final int position, long arg3) {
                                            Runnable _runnable_00 = new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent intent = new Intent(ChoicesActivity.this,
                                                            ChoicesActivity.class);

                                                    //  SoundEffectManager.getInstance();
                                                    //  SoundEffectManager.initSounds(ChoicesActivity.this);
                                                    //  SoundEffectManager.loadSounds();
                                                    MediaPlayer mp = MediaPlayer.create(ChoicesActivity.this, R.raw.button_click_one);

                                                    if (position == 0) {
                                                        if (!custom.isSilent_One()) mp.start();
                                                        if (custom.isLOCAL_PEER_REGISTERED()) {
                                                            ChoicesActivity.this.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    ///  busy.show();
                                                                }
                                                            });
                                                            intent = new Intent(ChoicesActivity.this,
                                                                    MainActivity.class);
                                                            intent.putExtra("INDEX", 1);
                                                        } else {
                                                            if (custom.getLanguage() == 1) {
                                                                dialog_two.setDialog_message(getResources().getString(R.string.notification_one_sv));
                                                                dialog_two.show();
                                                            } else if (custom.getLanguage() == 2) {
                                                                dialog_two.setDialog_message(getResources().getString(R.string.notification_one_sp));
                                                                dialog_two.show();
                                                            } else if (custom.getLanguage() == 3) {
                                                                dialog_two.setDialog_message(getResources().getString(R.string.notification_one_pr));
                                                                dialog_two.show();
                                                            } else if (custom.getLanguage() == 4) {
                                                                dialog_two.setDialog_message(getResources().getString(R.string.notification_one_fr));
                                                                dialog_two.show();
                                                            } else if (custom.getLanguage() == 5) {
                                                                dialog_two.setDialog_message(getResources().getString(R.string.notification_one_am));
                                                                dialog_two.show();
                                                            } else {
                                                                dialog_two.setDialog_message(getResources().getString(R.string.notification_one_en));
                                                                dialog_two.show();
                                                            }


                                                        }

                                                    } else if (position == 1) {


                                                        if (!custom.isSilent_One()) mp.start();
                                                        if (custom.isLOCAL_PEER_REGISTERED()) {
                                                            intent = new Intent(ChoicesActivity.this,
                                                                    GroupContainerActivity.class);
                                                            intent.putExtra("INDEX", "GROUPS");
                                                        } else {
                                                            if (custom.getLanguage() == 1) {
                                                                dialog_two.setDialog_message(getResources().getString(R.string.notification_one_sv));
                                                                dialog_two.show();
                                                            } else if (custom.getLanguage() == 2) {
                                                                dialog_two.setDialog_message(getResources().getString(R.string.notification_one_sp));
                                                                dialog_two.show();
                                                            } else if (custom.getLanguage() == 3) {
                                                                dialog_two.setDialog_message(getResources().getString(R.string.notification_one_pr));
                                                                dialog_two.show();
                                                            } else if (custom.getLanguage() == 4) {
                                                                dialog_two.setDialog_message(getResources().getString(R.string.notification_one_fr));
                                                                dialog_two.show();
                                                            } else if (custom.getLanguage() == 5)

                                                            {
                                                                dialog_two.setDialog_message(getResources().getString(R.string.notification_one_am));
                                                                dialog_two.show();
                                                            } else {
                                                                dialog_two.setDialog_message(getResources().getString(R.string.notification_one_en));
                                                                dialog_two.show();
                                                            }


                                                        }


                                                    } else if (position == 2) {

                                                        if (!custom.isSilent_One()) mp.start();
                                                        intent = new Intent(ChoicesActivity.this,
                                                                GroupContainerActivity.class);
                                                        intent.putExtra("INDEX", "UCIs");
                                                    } else if (position == 3) {
                                                        if (!custom.isSilent_One()) mp.start();
                                                        intent = new Intent(ChoicesActivity.this,
                                                                GroupContainerActivity.class);
                                                        intent.putExtra("INDEX", "SEARCH");

                                                    } else if (position == 4) {
                                                        if (!custom.isSilent_One()) mp.start();
                                                        intent = new Intent(ChoicesActivity.this,
                                                                LanguageActivity.class);
                                                    } else if (position == 5) {
                                                        if (!custom.isSilent_One()) mp.start();
                                                        intent = new

                                                                Intent(ChoicesActivity.this,
                                                                HelpContainerActivity.class);
                                                    } else if (position == 6) {
                                                        if (!custom.isSilent_One()) mp.start();
                                                        intent = new

                                                                Intent(ChoicesActivity.this,
                                                                AboutActivity.class);
                                                        intent.putExtra("CAT", "ABOUT");
                                                    } else if (position == 7)

                                                    {
                                                        if (!custom.isSilent_One()) mp.start();
                                                        try {
                                                            ///Save service states
                                                            custom.setSERVICE_UP(false);
                                                            SharedPreferences _mPrefs_ = getSharedPreferences("myprofile", 0);
                                                            SharedPreferences.Editor ed = _mPrefs_.edit();
                                                            ed.putBoolean("IS_SERVICE_UP", false);
                                                            ed.putBoolean("IS_ST_UP", false);
                                                            ed.commit();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                        intent = new Intent(ChoicesActivity.this,
                                                                WelcomeActivity.class);
                                                        intent.putExtra("from", 1);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    } else ;
                                                    ChoicesActivity.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            imageListAdapter.setPosition(position);
                                                            TextView btnn_ = (TextView) arg1.findViewById(R.id.choices_btn);
                                                            Button btnn__ = (Button) arg1
                                                                    .findViewById(R.id.choices_btn_concentric);
                                                            TextView sub_item = (TextView) arg1
                                                                    .findViewById(R.id.tv_choices_sub_item);
                                                            sub_item.setVisibility(View.VISIBLE);
                                                            btnn_.setBackgroundResource(R.drawable.circlebtn_blue_2);
                                                        }
                                                    });

                                                    startActivity(intent);


                                                }
                                            };

                                            Thread _mthread_00 = new Thread(_runnable_00);
                                            _mthread_00.start();
                                        }
                                    }

        );
        gv_1.setOnHoverListener(new

                                        OnHoverListener() {

                                            @Override
                                            public boolean onHover(View view, MotionEvent arg1) {
                                                // TODO Auto-generated method stub
                                                // view.announceForAccessibility("Click buttons");

                                                Toast.makeText(ChoicesActivity.this, ":D tap",
                                                        Toast.LENGTH_SHORT).show();

                                                return false;
                                            }
                                        }

        );
        gv_1.setOnItemLongClickListener(new

                                                OnItemLongClickListener() {

                                                    @Override
                                                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                                                                   int arg2, long arg3) {
                                                        // TODO Auto-generated method stub

                                                        // Intent intent = new Intent(ChoicesActivity.this,
                                                        // PrivateChatActivity.class);
                                                        // startActivity(intent);

                                                        return false;
                                                    }
                                                }

        );

        btn_1.setOnClickListener(new

                                         OnClickListener() {
                                             @Override
                                             public void onClick(View arg0) {
                                                 Intent intent = new Intent(ChoicesActivity.this,
                                                         EditProfileActivity.class);
                                                 startActivity(intent);

                                             }
                                         }

        );
        btn_2_.setOnClickListener(new

                                          OnClickListener() {
                                              @Override
                                              public void onClick(View arg0) {
                                                  Intent intent = new Intent(ChoicesActivity.this,
                                                          M2MActivity.class);
                                                  startActivity(intent);

                                              }
                                          }

        );
        btn_6.setOnClickListener(new

                                         OnClickListener() {
                                             @Override
                                             public void onClick(View arg0) {
                                                 Intent intent = new Intent(ChoicesActivity.this,
                                                         MoreActivity.class);
                                                 startActivity(intent);

                                             }
                                         }

        );
        btn_7.setOnClickListener(new

                                         OnClickListener() {
                                             @Override
                                             public void onClick(View arg0) {
                                                 Intent intent = new Intent(ChoicesActivity.this,
                                                         GroupContainerActivity.class);
                                                 intent.putExtra("INDEX", "Sense");
                                                 startActivity(intent);

                                             }
                                         }

        );
        btn_8.setOnClickListener(new

                                         OnClickListener() {
                                             @Override
                                             public void onClick(View arg0) {
                                                 Intent intent = new Intent(ChoicesActivity.this,
                                                         PurgeActivity.class);
                                                 startActivity(intent);

                                             }
                                         }

        );
        btn_9.setOnClickListener(new

                                         OnClickListener() {
                                             @Override
                                             public void onClick(View arg0) {


                                                 try {
                                                     if (isInternetAvailable()) {
                                                         custom = new Customizations(ChoicesActivity.this, -1);
                                                         if (custom.isST_UP()) {
                                                             Intent intent = new Intent(ChoicesActivity.this,
                                                                     DebugActivity.class);
                                                             startActivity(intent);
                                                         } else if (custom.getPLATFORM_STARTUP_TRIAL() >= 120) {

                                                             LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                             View view_ = inflater.inflate(R.layout.dialog_one, null);
                                                             TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);
                                                             if (custom.getPreferred_bs_ip().equals("193.10.119.42")) {
                                                                 tv_0_0_.setText("Add Bootstrap!  MediaSense needs bootstrap ip.");

                                                                 if (custom.getLanguage() == 0) {
                                                                     displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_main_one_13));
                                                                 } else if (custom.getLanguage() == 1) {
                                                                     displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_main_one_13_sv));
                                                                 } else if (custom.getLanguage() == 2) {
                                                                     displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_main_one_13_sp));
                                                                 } else if (custom.getLanguage() == 3) {
                                                                     displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_main_one_13_pr));
                                                                 } else if (custom.getLanguage() == 4) {
                                                                     displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_main_one_13_fr));
                                                                 } else
                                                                     displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_main_one_13));

                                                             } else {

                                                                 if (custom.getLanguage() == 0) {
                                                                     displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_search_one_1));
                                                                 } else if (custom.getLanguage() == 1) {
                                                                     displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_search_one_1_sv));
                                                                 } else if (custom.getLanguage() == 2) {
                                                                     displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_search_one_1_sp));
                                                                 } else if (custom.getLanguage() == 3) {
                                                                     displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_search_one_1_pr));
                                                                 } else if (custom.getLanguage() == 4) {
                                                                     displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_search_one_1_fr));
                                                                 } else
                                                                     displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_search_one_1));

                                                             }

                                                         } else if (custom.isFLAG_DISP_NO_IP()) {
                                                             if (custom.getLanguage() == 0) {
                                                                 displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.no_valid_working_bs_ip));
                                                             } else if (custom.getLanguage() == 1) {
                                                                 displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.no_valid_working_bs_ip_sv));
                                                             } else if (custom.getLanguage() == 2) {
                                                                 displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.no_valid_working_bs_ip_sp));
                                                             } else if (custom.getLanguage() == 3) {
                                                                 displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.no_valid_working_bs_ip_pr));
                                                             } else if (custom.getLanguage() == 4) {
                                                                 displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.no_valid_working_bs_ip_fr));
                                                             } else
                                                                 displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.no_valid_working_bs_ip));

                                                             startService(service_intent);


                                                         } else {
                                                             if (custom.getLanguage() == 0) {
                                                                 displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_choices_one_0));
                                                             } else if (custom.getLanguage() == 1) {
                                                                 displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_choices_one_0_sv));
                                                             } else if (custom.getLanguage() == 2) {
                                                                 displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_choices_one_0_sp));
                                                             } else if (custom.getLanguage() == 3) {
                                                                 displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_choices_one_0_pr));
                                                             } else if (custom.getLanguage() == 4) {
                                                                 displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_choices_one_0_fr));
                                                             } else
                                                                 displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_choices_one_0_sp));

                                                         }

                                                     } else {
                                                         if (custom.getLanguage() == 0) {
                                                             displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_choices_one_0));
                                                         } else if (custom.getLanguage() == 1) {
                                                             displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_choices_one_0_sv));
                                                         } else if (custom.getLanguage() == 2) {
                                                             displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_choices_one_0_sp));
                                                         } else if (custom.getLanguage() == 3) {
                                                             displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_choices_one_0_pr));
                                                         } else if (custom.getLanguage() == 4) {
                                                             displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_choices_one_0_fr));
                                                         } else
                                                             displayCustomizedToast(ChoicesActivity.this, getResources().getString(R.string.string_choices_one_0_sp));

                                                     }


                                                 } catch (Exception e) {
                                                     e.printStackTrace();
                                                     Intent intent = new Intent(ChoicesActivity.this, ErrorActivity.class);
                                                     intent.putExtra("error", e.getStackTrace());
                                                     intent.putExtra("ACTIVITY", "DebugActivity");
                                                     startActivity(intent);
                                                 }


                                             }
                                         }

        );

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

                intent = new Intent(ChoicesActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_advanced:

                intent = new Intent(ChoicesActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(ChoicesActivity.this,
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

    public void onClick(View arg0) {

    }

    // @Override

    // Check if the Internet is Available
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        if (_BOUND) unbindService(mConnection);
        dialog_one.show();
        return;
    }

    @Override
    public void onStop() {
        super.onStop();


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

    class ActivityHandler extends Handler {
        private final WeakReference<ChoicesActivity> mActivity;

        public ActivityHandler(ChoicesActivity activity) {
            mActivity = new WeakReference<ChoicesActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PlatformManagerNode.NEW_MESSAGE_FLAG: {

                    Toast.makeText(mActivity.get(),
                            msg.getData().getString("NEW_MESSAGE_FLAG"),
                            Toast.LENGTH_SHORT).show();
                    incoming_message = msg.getData().getString("NEW_MESSAGE_FLAG");

                    // ShowMessages(al_chats);

                }
                case PlatformManagerNode.NEW_MESSAGE_FLAG_FA: {

                    Toast.makeText(mActivity.get(),
                            msg.getData().getString("NEW_MESSAGE_FLAG"),
                            Toast.LENGTH_SHORT).show();
                    incoming_message = msg.getData().getString("NEW_MESSAGE_FLAG");

                }
                case PlatformManagerNode.NEW_MESSAGE_FLAG_TEST: {
                    final String _received_message = msg.getData().getString("_mess");
                    {
                        ChoicesActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                android.os.Message msg = android.os.Message.obtain(null,
                                        PlatformManagerNode.NEW_MESSAGE_FLAG_TEST, 0, 0);
                                msg.replyTo = mActivityMessenger;
                                try {
                                    mPlatformManagerNodeMessenger.send(msg);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }

                                String _tmp_msg_0_0;
                                if (_received_message != null)
                                    if (_received_message.contains("[Resolve Response]")) {
                                        {
                                            if (temp_list_of_messages.contains(_received_message) == false) {
                                                temp_list_of_messages.add(_received_message);
                                            }
                                        }
                                    } else if (_received_message.contains(":100~::~")) {
                                        _tmp_msg_0_0 = _received_message.substring(_received_message.indexOf(":") + 1);
                                        if (temp_list_of_messages.contains(_tmp_msg_0_0) == false) {
                                            imageListAdapter.setIsPlatformRecieving(true);

                                        }
                                    } else if (_received_message.contains(":101~::~")) {
                                    } else if (_received_message.split("]").length > 1) {
                                        if (temp_list_of_messages.contains(_received_message.split("]")[1]) == false) {
                                        }
                                    } else {
                                    }


                            }
                        });

                    }

                }

                default: {

                }
            }
        }

    }

}
