package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.adapters.ImageListAdapter;
import se.sensiblethings.app.chitchato.adapters.ListAdapter;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerBootstrap;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerNode;
import se.sensiblethings.app.chitchato.context.ContextManager;
import se.sensiblethings.app.chitchato.extras.Busy;
import se.sensiblethings.app.chitchato.extras.Constants;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.DialogOne;
import se.sensiblethings.app.chitchato.extras.SysError;
import se.sensiblethings.app.chitchato.kernel.Groups;
import se.sensiblethings.app.chitchato.kernel.Peers;
import se.sensiblethings.app.chitchato.kernel.PublicChats;



public class MainActivity extends Activity {
    private static final int SELECT_PROFILE_IMAGE = 1;
    static public String SELECTED_GROUP = "*ALL#";
    static public ArrayList<String> PEERS_IN_SELECTED_GROUP = new ArrayList<String>();
    static public TreeMap<String, String> PEERS_UCI_IN_SELECTED_GROUP = new TreeMap<String, String>();
    //15 is default num of messages to display
    public static int THREAD_LENGTH_TO_DISP = 15;
    // Date Parameters
    static String date;
    // Service Messagenger---
    static Messenger mPlatformManagerNodeMessenger;
    static boolean AUTO_SCROLL_MESSAGE_LIST = false;
    private static ArrayList<String> al_chats = null;
    final Long systimemillis = System.currentTimeMillis();
    public String SELECTED_PEER = "";
    protected SharedPreferences mPrefs;
    protected boolean CHAT_MODE = true;
    protected String MODE = "Ordinary Mode";
    protected boolean PRIVATE_MODE = false;
    protected boolean PUBLIC_MODE = true;
    protected Customizations custom;
    protected ShowButtonsThread show_buttons_thread;
    protected boolean SHOWGROUPS = true;
    protected ContextManager context_manager;
    TextView tv, tv_;
    Button btn;
    ImageButton img_btn, img_btn_hide, img_btn_show;
    ImageView image_view_profile = null, image_to_send = null;
    ProgressBar progress_bar;
    EditText edit_txt;
    //ProgressBar progress = null;
    long l;
    String temp;
    String[] a;
    Message message;
    String nodes = "%";
    ListView lv_1, lv_2;
    GridView gv_1, gv_adv_small;
    LinearLayout linear_layout;
    // Platform status -- for notification purpose
    boolean PREV_ST_STATUS = false;
    String date_pattern = "yyyy-MM-dd HH:mm:ss";
    Format format = new SimpleDateFormat(date_pattern);
    android.os.Message msg = android.os.Message.obtain(null,
            PlatformManagerNode.NEW_MESSAGE_FLAG, 0, 0);
    android.os.Message msg_image = android.os.Message.obtain(null,
            PlatformManagerNode.NEW_IMAGE_MESSAGE_FLAG, 0, 0);
    android.os.Message msg_ = android.os.Message.obtain(null,
            PlatformManagerNode.NEW__GROUP_MESSAGE_FLAG, 0, 0);
    android.os.Message msg_msg = android.os.Message.obtain(null,
            PlatformManagerNode.NEW_MESSAGE_FLAG_TEST, 0, 0);
    Messenger mActivityMessenger = new Messenger(
            new ActivityHandler(this));
    //PlatformManagerNode service;
    PlatformManagerBootstrap service_bt;
    String _received_message = "Initial";
    ArrayList<String> temp_list_of_messages = new ArrayList<String>();
    boolean isLoadPreviousDone = false;
    Intent service_intent;
    boolean _BOUND = false;
    //group  differntial relative to local
    Long gr_clk = 0L;
    // Sensor Manager
    SensorManager mSensorManager;
    // previuos messaages
    PublicChats _thread_0;
    PlatformManagerNode update_service;
    int previous_arr_size = 0;
    int current_arr_size = 0;
    private ProgressDialog prgDialog;
    private Groups groups, groups_inits;
    private Peers peers;
    private String Message = "";
    private String chatMessage = "";
    private String contextMessage = "";
    private String localPeerName = "";
    private String groupName = "#None";
    private boolean MA_VISITED = false;
    private ArrayList<String> al_chats_err = null;
    private ImageListAdapter imageListAdapter = null;
    private ListAdapter listadapter;
    private Busy busy;
    private SysError error;
    private EditProfileActivity.GetProfile profile;
    private String selectedImagePath;
    private String filemanagerstring;
    private String incoming_message = "#";
    private DialogOne dialog_one;
    private String uri_str_mimage_messsage = "#Nothing#";
    private int forwarding_from = 0;
    private  MediaPlayer  mp, mp_long_tap, mp_mess_out , mp_failed, mp_platform_up, mp_platform_down;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            mPlatformManagerNodeMessenger = new Messenger(binder);
            _BOUND = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            update_service = null;

            if (custom.getLanguage() == 0)
                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_9));
            else if (custom.getLanguage() == 1)
                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_9_sv));
            else if (custom.getLanguage() == 1)
                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_9_sp));
            else if (custom.getLanguage() == 1)
                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_9_pr));
            else if (custom.getLanguage() == 1)
                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_9_fr));
            else
                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_9));
            _BOUND = false;
        }
    };

    public static String getAppFolder(String file_name) {

        //file_name_== media --images holder
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + file_name + "/";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        error = new SysError(this, false);
        try {
            mPrefs = getSharedPreferences("preference_1", 0);
            CHAT_MODE = mPrefs.getBoolean("OChat", true);
            if (CHAT_MODE == false)
                MODE = "Context Mode";
            else
                MODE = "Ordinary Mode";
            custom = new Customizations(this, -1);

            // Vibrationfor public and private mode
            final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            mp = MediaPlayer.create(MainActivity.this, R.raw.button_click_one);
            mp_long_tap = MediaPlayer.create(MainActivity.this, R.raw.sms_alert_daniel_simon);
            mp_mess_out = MediaPlayer.create(MainActivity.this, R.raw.tiny_button_push);
            mp_failed = MediaPlayer.create(MainActivity.this, R.raw.outgoing_fg);
            mp_platform_up = MediaPlayer.create(MainActivity.this, R.raw.activate_secondary);
            mp_platform_down = MediaPlayer.create(MainActivity.this, R.raw.pin_drop);


            //Force Close Error Handler
            setContentView(custom.getMainScreen());


            // Keep the orientation portrait
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            lv_2 = (ListView) findViewById(R.id.listView2);
            gv_1 = (GridView) findViewById(R.id.gridView1);
            btn = (Button) findViewById(R.id.btn_send_chatscreen);
            img_btn = (ImageButton) findViewById(R.id.img_btn_addfile);
            img_btn_show = (ImageButton) findViewById(R.id.btn_show);
            img_btn_hide = (ImageButton) findViewById(R.id.btn_hide);
            edit_txt = (EditText) findViewById(R.id.edt_chatScreen);
            tv_ = (TextView) findViewById(R.id.tv_empty_bar_chatscreen);
            //progress = (ProgressBar) findViewById(0x7f03000e);
            linear_layout = (LinearLayout) findViewById(R.id.linear1_1);
            progress_bar = (ProgressBar) findViewById(R.id.progressBar1);
            dialog_one = new DialogOne(this, false, 12);
            busy = new Busy(this, false, -1);
            busy.setCancelable(true);


            Typeface tf = Typeface.createFromAsset(getAssets(),
                    "fonts/COMIC.TTF");
            Typeface tf_pala = Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");

            // Font face
            edit_txt.setTypeface(tf);
            btn.setTypeface(tf_pala);
            tv_.setTypeface(tf);

            forwarding_from = getIntent().getIntExtra("INDEX", 0); // 1- from choices menu--, 0-from welcomescreen--default
            final ArrayList<String> al_ = new ArrayList<String>();

            // Progress bar -- displays while the platform is converging---
            if (isInternetAvailable())
                if (custom.getPLATFORM_STARTUP_TRIAL() < 30)
                    if (!PlatformManagerBootstrap.ST_PLATFORM_IS_UP || !PlatformManagerNode.ST_PLATFORM_IS_UP) {
                        if (!custom.getPreferred_bs_ip().isEmpty() && custom.getPLATFORM_STARTUP_TRIAL() < 60) {
                            Toast.makeText(this, "Tap to end.", Toast.LENGTH_LONG).show();
                            busy.setDialog_title("MediaSense Converging " + custom.getPreferred_bs_ip());
                            busy.setCancelable(true);
                            busy.show();
                            if (forwarding_from == 1) new BKGroundTask_DialogTask().execute();

                        } else {
                            if (custom.getLanguage() == 0)
                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_13));
                            else if (custom.getLanguage() == 1)
                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_13_sv));
                            else if (custom.getLanguage() == 2)
                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_13_sp));
                            else if (custom.getLanguage() == 3)
                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_13_pr));
                            else if (custom.getLanguage() == 4)
                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_13_fr));
                            else
                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_13));
                        }
                    }

            Runnable runnable_00 = new Runnable() {
                @Override
                public void run() {

                    groups = new Groups(MainActivity.this);
                    peers = new Peers(MainActivity.this);

                    // Imported Groups
                    ArrayList<String> al__ = groups.getmGroups();

                    // Default Group --
                    if (al_.size() == 0) {
                        al_.add("Contexter");
                    }
                    // List of groups preferred by user
                    for (String temp : al__) {
                        if (temp.contains(":::")) {
                            String _temp_0 = temp.split(":::")[1];
                            if (_temp_0.contains("~::~")) {
                                String[] _temp_ = _temp_0.split("~::~");
                                if (_temp_.length > 4) {
                                    String group_name = _temp_[0];
                                    String group_intereste = _temp_[1];
                                    String group_leader = _temp_[2];
                                    String group_age_limit = _temp_[4];
                                    String CreationDate = _temp_[5];
                                    temp = group_name;
                                }

                            }
                        }
                        if (!al_.contains(temp))
                            al_.add(temp);
                    }
                    if (al_.isEmpty() || al_ == null || al_.get(0).contains("None!")) {
                        if (custom.getLanguage() == 0)
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_10));
                        else if (custom.getLanguage() == 1)
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_10_sv));
                        else if (custom.getLanguage() == 2)
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_10_sp));
                        else if (custom.getLanguage() == 3)
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_10_pr));
                        else if (custom.getLanguage() == 4)
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_10_fr));
                        else
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_10));
                    }
                    SELECTED_PEER = custom.getPeer_name();


                    // Context Information//
                    if (!CHAT_MODE) {
                        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                        context_manager = new ContextManager(getBaseContext(),
                                localPeerName, mSensorManager);
                        context_manager.updateContextData();
                    }

                }
            };

            Thread _mthread = new Thread(runnable_00);
            _mthread.run();

            if (custom.IS_NODE_BOOTSTRAP_())
                service_intent = new Intent(this, PlatformManagerBootstrap.class);
            else
                service_intent = new Intent(this, PlatformManagerNode.class);
            bindService(service_intent, mConnection,
                    Context.BIND_AUTO_CREATE);

            final ArrayList<String> peers = new ArrayList<String>();
            listadapter = new ListAdapter(this, -1, CHAT_MODE, al_, 0);
            if (forwarding_from == 1) ;

            if (custom.getLanguage() == 0)
                peers.add(getResources().getString(R.string.string_dialog_one_4_0) + "\n\n" + getResources().getString(R.string.string_dialog_one_4_0));
            else if (custom.getLanguage() == 1)
                peers.add(getResources().getString(R.string.string_dialog_one_4_0_sv) + "\n\n" + getResources().getString(R.string.string_dialog_one_4_0_sv));
            else if (custom.getLanguage() == 2)
                peers.add(getResources().getString(R.string.string_dialog_one_4_0_sp) + "\n\n" + getResources().getString(R.string.string_dialog_one_4_0_sp));
            else if (custom.getLanguage() == 3)
                peers.add(getResources().getString(R.string.string_dialog_one_4_0_pr) + "\n\n" + getResources().getString(R.string.string_dialog_one_4_0_pr));
            else if (custom.getLanguage() == 4)
                peers.add(getResources().getString(R.string.string_dialog_one_4_0_fr) + "\n\n" + getResources().getString(R.string.string_dialog_one_4_0_fr));

            imageListAdapter = new ImageListAdapter(getBaseContext(), false,
                    CHAT_MODE, 0, peers);
            imageListAdapter.setSetProfilePictureGone(false);
            imageListAdapter.setIsPlatformRecieving(false);
            gv_1.setAdapter(imageListAdapter);

            // Groups List//
            try {
                lv_2.setAdapter(listadapter);
            } finally {
                // groups.close();
            }
            al_chats = new ArrayList<String>();
            al_chats_err = new ArrayList<String>();
            al_chats_err.add("Oops ... Nothing to show! ");

            lv_2.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, final View view,
                                        final int position, long arg3) {
                    PRIVATE_MODE = false;
                    PUBLIC_MODE = true;

                   // vibrator.vibrate(20);
                    if(!custom.isSilent_One()) mp.start();
                    if (progress_bar.isShown()) {
                        progress_bar.setVisibility(View.GONE);
                    } else {
                        progress_bar.setVisibility(View.VISIBLE);
                        gv_1.setVisibility(View.GONE);
                    }
                    TextView sub_item = (TextView) view
                            .findViewById(R.id.tv_chat_screen_sub_item);
                    sub_item.setVisibility(View.INVISIBLE);
                    TextView item = (TextView) view
                            .findViewById(R.id.favorite_group_btn);

                    //groupName = listadapter.getItem(position);
                    SELECTED_GROUP = sub_item.getText().toString();
                    groupName = SELECTED_GROUP;

                    //Load Previous Messages
                    try {
                        Runnable runnable_00_0 = new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageListAdapter = new ImageListAdapter(getBaseContext(), false,
                                                CHAT_MODE, 0, peers);
                                        listadapter.setPosition(position);
                                        lv_2.setAdapter(listadapter);
                                        gv_1.setAdapter(imageListAdapter);
                                        gv_1.setSelection(imageListAdapter.getCount() - 1);
                                        lv_2.setSelection(position);
                                        if (!SELECTED_GROUP.equalsIgnoreCase("*ALL#"))
                                            tv_.setText(SELECTED_GROUP);
                                        else
                                            tv_.setText(SELECTED_GROUP);
                                    }
                                });

                                // Load Group information -- Such as group pass-- group clock-- group
                                groups_inits = new Groups(getBaseContext(), "params");
                                ArrayList<String> registered_groups = groups_inits.getmGroups();
                                String gr_nm_ = "~::~UNREGISTERED~::~";
                                for (String str : registered_groups) {
                                    gr_nm_ = str.split("~::~")[0];
                                    String nk_nm = str.split("~::~")[1];
                                    String gr_pw = str.split("~::~")[2];
                                    gr_clk = Long.parseLong(str.split("~::~")[3]);
                                    if (gr_nm_.equalsIgnoreCase(SELECTED_GROUP)) break;
                                    else {
                                        gr_nm_ = "~::~UNREGISTERED~::~";
                                        gr_clk = 0L;
                                    }
                                }
                                // Default Group owned by no one
                                if (SELECTED_GROUP.equalsIgnoreCase("Contexter"))
                                    gr_nm_ = "Contexter";

                                if (gr_nm_.equalsIgnoreCase("~::~UNREGISTERED~::~")) {

                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {


                                            if (custom.getLanguage() == 0)
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_11));
                                            else if (custom.getLanguage() == 1)
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_11_sv));
                                            else if (custom.getLanguage() == 2)
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_11_sp));
                                            else if (custom.getLanguage() == 3)
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_11_pr));
                                            else if (custom.getLanguage() == 4)
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_11_fr));
                                            else
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_11));
                                            progress_bar.setVisibility(View.GONE);
                                        }
                                    });

                                } else {
                                    isLoadPreviousDone = false;
                                    //
                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            gv_1.setVisibility(View.GONE);
                                        }
                                    });
                                    if (mPlatformManagerNodeMessenger == null) {
                                        if (_BOUND) {
                                            unbindService(mConnection);
                                            bindService(service_intent, mConnection,
                                                    Context.BIND_AUTO_CREATE);
                                        } else
                                            bindService(service_intent, mConnection,
                                                    Context.BIND_AUTO_CREATE);
                                    }
                                    Bundle b = new Bundle();
                                    b.putString("NEW_MESSAGE_FLAG", SELECTED_GROUP + "@"
                                            + "~::~" + "");
                                    b.putString("NEW_MESSAGE_FLAG_FA", "");
                                    android.os.Message replyMsg = android.os.Message
                                            .obtain(null,
                                                    PlatformManagerNode.NEW_MESSAGE_FLAG, 0,
                                                    0);
                                    replyMsg.setData(b);
                                    msg.replyTo = mActivityMessenger;

                                    if (mPlatformManagerNodeMessenger != null) {
                                        android.os.Message message_ = new Message();
                                        message_.setData(b);
                                        try {
                                            mPlatformManagerNodeMessenger.send(message_);


                                            // ---Initiate Receiving New Message Flag---//
                                            android.os.Message msg = android.os.Message.obtain(null,
                                                    PlatformManagerNode.NEW_MESSAGE_FLAG_TEST, 0, 0);
                                            msg.replyTo = mActivityMessenger;
                                            mPlatformManagerNodeMessenger.send(msg);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                    } else ;

                                    al_chats.clear();
                                    _thread_0 = new PublicChats(getBaseContext(), MainActivity.SELECTED_GROUP);
                                    ArrayList<String> temp_ar = _thread_0.getmPublicChat(SELECTED_GROUP, THREAD_LENGTH_TO_DISP);
                                    if (_thread_0.getmPublicChatSize() >= 15)
                                        al_chats.add("~PLATFORM~MORE~");

                                    TreeMap<Integer, String> seq_map = new TreeMap<Integer, String>();
                                    Time t = null;

                                    for (String tmp : temp_ar) {

                                        String[] tmp_0_0 = tmp.split("~::~");
                                        // Just Texting
                                        if (tmp_0_0.length > 4 && tmp_0_0.length < 7) {
                                            seq_map.put(Integer.parseInt(tmp_0_0[5]), tmp);
                                        } else if (tmp_0_0.length > 7) {
                                            seq_map.put(Integer.parseInt(tmp_0_0[9]), tmp);
                                        } else
                                            al_chats.add(tmp);

                                    }
                                    Set set = seq_map.entrySet();
                                    Iterator itr = set.iterator();

                                    Map.Entry me = null;
                                    while (itr.hasNext()) {
                                        me = (Map.Entry) itr.next();
                                        String str_tmp = String.valueOf(me.getValue());
                                        al_chats.add(str_tmp);

                                    }

                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            imageListAdapter = new ImageListAdapter(getBaseContext(), false,
                                                    CHAT_MODE, 0, peers);
                                            gv_1.setAdapter(imageListAdapter);
                                            gv_1.setSelection(imageListAdapter.getCount() - 1);

                                        }
                                    });

                                    // Collections.reverse(al_chats);
                                    ShowMessages(al_chats);
                                    isLoadPreviousDone = true;
                                }
                            }
                        };
                        Thread _mthread_00 = new Thread(runnable_00_0);
                        _mthread_00.start();


                        //Hide Group buttons
                        img_btn_show.setVisibility(View.VISIBLE);
                        img_btn_hide.setVisibility(View.GONE);

                        if (!SELECTED_GROUP.equalsIgnoreCase("*ALL#"))
                            tv_.setText(SELECTED_GROUP);
                        LinearLayout ll = (LinearLayout) findViewById(R.id.linear_chatscreen_3);
                        ll.setVisibility(View.GONE);
                        SHOWGROUPS = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent intent = new Intent(MainActivity.this, ErrorActivity.class);
                        intent.putExtra("error", e.getLocalizedMessage());
                        startActivity(intent);
                    }

                }


            });

            lv_2.setOnItemLongClickListener(new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                               int position, long arg3) {
                    mp_long_tap.start();

                    PRIVATE_MODE = true;
                    PUBLIC_MODE = false;

                    try {
                        TextView item = (TextView) view
                                .findViewById(R.id.tv_chat_screen_sub_item);
                        groupName = item.getText().toString();
                        ShowResults(al_chats, "");
                    } catch (Exception e) {
                        e.printStackTrace();

                        if (custom.getLanguage() == 0)
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_0));
                        else if (custom.getLanguage() == 1)
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_0_sv));
                        else if (custom.getLanguage() == 2)
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_0_sp));
                        else if (custom.getLanguage() == 3)
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_0_pr));
                        else if (custom.getLanguage() == 4)
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_0_fr));
                        else
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_0));
                    }
                    return false;
                }
            });

            // Local Peer Profile
            final EditProfileActivity edt_pro = new EditProfileActivity();
            profile = edt_pro.new GetProfile();
            if (!incoming_message.contains("#")) {
                al_chats.add(Message);
                imageListAdapter.addItem(al_chats);
                imageListAdapter.setSetProfilePictureGone(false);
                image_view_profile = imageListAdapter.getProfile_image();
                if (image_view_profile != null)
                    imageListAdapter.setProfile_image(image_view_profile);
                gv_1.setAdapter(imageListAdapter);
            }

            btn.setOnClickListener(new OnClickListener() {

                                       @Override
                                       public void onClick(View arg0) {
                                           chatMessage = edit_txt.getText().toString();

                                           edit_txt.setText("");

                                           Runnable runnable_00 = new Runnable() {
                                               @Override
                                               public void run() {
                                                   try {
                                                          if (SELECTED_GROUP.equals("*ALL#")) {
                                                           if (custom.getLanguage() == 0)
                                                               displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_1));
                                                           else if (custom.getLanguage() == 1)
                                                               displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_1_sv));
                                                           else if (custom.getLanguage() == 2)
                                                               displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_1_sp));
                                                           else if (custom.getLanguage() == 3)
                                                               displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_1_pr));
                                                           else if (custom.getLanguage() == 4)
                                                               displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_1_fr));
                                                           else
                                                               displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_1));

                                                       } else if (progress_bar.isShown()) {
                                                           Toast.makeText(getBaseContext(),
                                                                   "Oops!... Just a momonent ",
                                                                   Toast.LENGTH_SHORT).show();

                                                       } else {
                                                           if (chatMessage.trim().isEmpty()) {
                                                               // Toast.makeText(getBaseContext(),
                                                               //       "Invalid Message! Enter Text",
                                                               //     Toast.LENGTH_SHORT).show();
                                                           } else if (CHAT_MODE == false) {
                                                               //chatMessage = edit_txt.getText().toString().trim();
                                                               contextMessage =
                                                                       context_manager.getUserContext()
                                                                               .getTemprature()
                                                                               + "~::~"
                                                                               + context_manager.getUserContext()
                                                                               .getLimunosity()
                                                                               + "~::~"
                                                                               + context_manager.getUserContext()
                                                                               .getAcce()
                                                                               + "~::~"
                                                                               + context_manager.getUserContext()
                                                                               .getAddress() + "~::~";
                                                               if (custom.getPeer_nick_name() == null)
                                                                   custom.setPeer_nick_name(PlatformManagerNode._mUCI);
                                                               Message = custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~"
                                                                       + chatMessage + "~::~";

                                                               if (PlatformManagerNode.ST_PLATFORM_IS_UP || PlatformManagerBootstrap.ST_PLATFORM_IS_UP) {
                                                                   Message = Message + contextMessage + format.format(new Date()) + "~::~";
                                                                   if(!custom.isSilent_One())mp_mess_out.start();
                                                               } else {
                                                                   Message = Message + contextMessage + format.format(new Date()) + "failed" + "~::~";
                                                                   if(!custom.isSilent_One())mp_failed.start();
                                                               }



                                                               Message = Constants.PUBLIC + "~::~" + SELECTED_GROUP + "~::~" + Message + "" + getUniqueSerialNumber(3, 9, gr_clk) + "~::~" + "123456";
                                                               al_chats.add(Message + "");

                                                               AUTO_SCROLL_MESSAGE_LIST = true;
                                                               ShowMessages(al_chats);


                                                               if (custom.getPeer_nick_name().equalsIgnoreCase("contexter_0_0") || custom.getPeer_nick_name().startsWith("@")) {
                                                                   if (custom.getLanguage() == 0)
                                                                       displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_2));
                                                                   else if (custom.getLanguage() == 1)
                                                                       displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_2_sv));
                                                                   else if (custom.getLanguage() == 2)
                                                                       displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_2_sp));
                                                                   else if (custom.getLanguage() == 3)
                                                                       displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_2_pr));
                                                                   else if (custom.getLanguage() == 4)
                                                                       displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_2_fr));
                                                                   else
                                                                       displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_2));

                                                               } else {

                                                                   if (PlatformManagerNode.ST_PLATFORM_IS_UP || PlatformManagerBootstrap.ST_PLATFORM_IS_UP)
                                                                       if (_BOUND) {
                                                                           // ---Send New Message Flag---//
                                                                           Bundle b = new Bundle();
                                                                           b.putString("NEW_MESSAGE_FLAG", Message);
                                                                           b.putString("NEW_MESSAGE_FLAG_FA", Message);
                                                                           android.os.Message replyMsg = android.os.Message
                                                                                   .obtain(null, PlatformManagerNode.NEW_MESSAGE_FLAG, 0, 0);
                                                                           replyMsg.setData(b);
                                                                           msg.replyTo = mActivityMessenger;
                                                                           mPlatformManagerNodeMessenger.send(replyMsg);

                                                                           // ---Initiate Receiving New Message Flag---//
                                                                           android.os.Message msg = android.os.Message.obtain(null,
                                                                                   PlatformManagerNode.NEW_MESSAGE_FLAG_TEST, 0, 0);
                                                                           msg.replyTo = mActivityMessenger;
                                                                           mPlatformManagerNodeMessenger.send(msg);
                                                                       } else {
                                                                           MainActivity.this.runOnUiThread(new Runnable() {
                                                                               @Override
                                                                               public void run() {
                                                                                   edit_txt.setText(chatMessage);
                                                                               }
                                                                           });
                                                                           if (isInternetAvailable() && !custom.IS_NODE_BOOTSTRAP_()) {
                                                                               if (custom.getLanguage() == 0)
                                                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_3));
                                                                               else if (custom.getLanguage() == 1)
                                                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_3_sv));
                                                                               else if (custom.getLanguage() == 2)
                                                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_3_sp));
                                                                               else if (custom.getLanguage() == 3)
                                                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_3_pr));
                                                                               else if (custom.getLanguage() == 4)
                                                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_3_fr));
                                                                               else
                                                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_3));

                                                                               startService(service_intent);
                                                                           }
                                                                       }
                                                               }


                                                           } else if (CHAT_MODE == true) {
                                                               if (custom.getPeer_nick_name() == null)
                                                                   custom.setPeer_nick_name(PlatformManagerNode._mUCI);

                                                               if (PlatformManagerNode.ST_PLATFORM_IS_UP || PlatformManagerBootstrap.ST_PLATFORM_IS_UP) {
                                                                   Message = custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~"
                                                                           + chatMessage + "~::~"
                                                                           + format.format(new Date());
                                                                   if(!custom.isSilent_One())mp_mess_out.start();
                                                               } else {
                                                                   Message = custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~"
                                                                           + chatMessage + "~::~"
                                                                           + format.format(new Date()) + "failed";
                                                                   if(!custom.isSilent_One())mp_failed.start();
                                                               }


                                                               // Message format--
                                                               // ME-------------------------------------------------------------------------------------------SSAGETYPE~::~GROUPNAME~::~UCI~::~MESSAGE~::~TIME~::~MESSAGESERIAL~::~PW
                                                               Message = Constants.PUBLIC + "~::~" + SELECTED_GROUP + "~::~" + Message + "~::~" + getUniqueSerialNumber(3, 9, gr_clk) + "~::~" + "123456";
                                                               al_chats.add(Message + "");

                                                               AUTO_SCROLL_MESSAGE_LIST = true;
                                                               ShowMessages(al_chats);

                                                               // ---Send New Message Flag---//

                                                               if (custom.getPeer_nick_name().equalsIgnoreCase("contexter_0_0") || custom.getPeer_nick_name().startsWith("@")) {
                                                                   if (custom.getLanguage() == 0)
                                                                       displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_2));
                                                                   else if (custom.getLanguage() == 1)
                                                                       displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_2_sv));
                                                                   else if (custom.getLanguage() == 2)
                                                                       displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_2_sp));
                                                                   else if (custom.getLanguage() == 3)
                                                                       displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_2_pr));
                                                                   else if (custom.getLanguage() == 4)
                                                                       displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_2_fr));
                                                                   else
                                                                       displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_2));
                                                               } else {
                                                                   if (PlatformManagerNode.ST_PLATFORM_IS_UP || PlatformManagerBootstrap.ST_PLATFORM_IS_UP)
                                                                       if (_BOUND) {
                                                                           Bundle b = new Bundle();
                                                                           b.putString("NEW_MESSAGE_FLAG", Message);
                                                                           b.putString("NEW_MESSAGE_FLAG_FA", Message);
                                                                           android.os.Message replyMsg = android.os.Message
                                                                                   .obtain(null,
                                                                                           PlatformManagerNode.NEW_MESSAGE_FLAG,
                                                                                           0, 0);
                                                                           replyMsg.setData(b);

                                                                           msg.replyTo = mActivityMessenger;
                                                                           mPlatformManagerNodeMessenger.send(replyMsg);

                                                                           // ---Initiate Receiving New Message Flag---//
                                                                           android.os.Message msg = android.os.Message.obtain(null,
                                                                                   PlatformManagerNode.NEW_MESSAGE_FLAG_TEST, 0, 0);
                                                                           msg.replyTo = mActivityMessenger;
                                                                           mPlatformManagerNodeMessenger.send(msg);
                                                                       } else {
                                                                           MainActivity.this.runOnUiThread(new Runnable() {
                                                                               @Override
                                                                               public void run() {
                                                                                   edit_txt.setText(chatMessage);
                                                                               }
                                                                           });
                                                                           if (isInternetAvailable() && !custom.IS_NODE_BOOTSTRAP_()) {
                                                                               if (custom.getLanguage() == 0)
                                                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_3));
                                                                               else if (custom.getLanguage() == 1)
                                                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_3_sv));
                                                                               else if (custom.getLanguage() == 2)
                                                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_3_sp));
                                                                               else if (custom.getLanguage() == 3)
                                                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_3_pr));
                                                                               else if (custom.getLanguage() == 4)
                                                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_3_fr));
                                                                               else
                                                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_3));

                                                                               startService(service_intent);
                                                                           }
                                                                       }

                                                               }
                                                           }

                                                       }
                                                   } catch (RemoteException re) {
                                                       re.printStackTrace();
                                                       Intent intent = new Intent(MainActivity.this, ErrorActivity.class);
                                                       intent.putExtra("error", re.getLocalizedMessage());
                                                       startActivity(intent);
                                                   } catch (Exception e) {
                                                       e.printStackTrace();
                                                       Intent intent = new Intent(MainActivity.this, ErrorActivity.class);
                                                       intent.putExtra("error", e.getLocalizedMessage());
                                                       startActivity(intent);
                                                   }

                                               }
                                           };

                                           Thread _mthread_00 = new Thread(runnable_00);
                                           _mthread_00.start();


                                           if (!isInternetAvailable() && !custom.IS_NODE_BOOTSTRAP_()) {
                                               if (custom.getLanguage() == 0)
                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_4));
                                               else if (custom.getLanguage() == 1)
                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_4_sv));
                                               else if (custom.getLanguage() == 2)
                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_4_sp));
                                               else if (custom.getLanguage() == 3)
                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_4_pr));
                                               else if (custom.getLanguage() == 4)
                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_4_fr));
                                               else
                                                   displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_4));

                                           } else {
                                           }
                                       }

                                   }
            );

            edit_txt.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //Hide Group buttons
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    img_btn_show.setVisibility(View.VISIBLE);
                                    img_btn_hide.setVisibility(View.GONE);

                                    if (!SELECTED_GROUP.equalsIgnoreCase("*ALL#"))
                                        tv_.setText(SELECTED_GROUP);
                                    LinearLayout ll = (LinearLayout) findViewById(R.id.linear_chatscreen_3);
                                    ll.setVisibility(View.GONE);
                                    SHOWGROUPS = true;
                                }
                            });

                        }
                    };
                    return false;

                }
            });
            //
            img_btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {


                    if (SELECTED_GROUP.equals("*ALL#")) {
                        Toast.makeText(getBaseContext(),
                                "Invalid Option!  Choose Your Group. ",
                                Toast.LENGTH_SHORT).show();

                    } else if (progress_bar.getVisibility() != View.GONE) {
                        Toast.makeText(getBaseContext(),
                                "Sending ...Just a momonent ",
                                Toast.LENGTH_SHORT).show();
                    } else {


                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.putExtra("ACTIVITY", "MainActivity");

                        startActivityForResult(
                                Intent.createChooser(intent, "Select Picture"),
                                SELECT_PROFILE_IMAGE);
                    }

                }
            });

            img_btn_show.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    img_btn_show.setVisibility(View.GONE);
                                    img_btn_hide.setVisibility(View.VISIBLE);
                                    tv_.setText("");
                                    LinearLayout ll = (LinearLayout) findViewById(R.id.linear_chatscreen_3);
                                    ll.setVisibility(View.VISIBLE);
                                    SHOWGROUPS = false;
                                    progress_bar.setVisibility(View.GONE);
                                    gv_1.setVisibility(View.VISIBLE);
                                }
                            });

                        }
                    };

                    progress_bar.setVisibility(View.VISIBLE);
                    gv_1.setVisibility(View.GONE);

                    Thread _mthread = new Thread(runnable);
                    _mthread.start();


                }
            });

            img_btn_hide.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {

                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    img_btn_show.setVisibility(View.VISIBLE);
                                    img_btn_hide.setVisibility(View.GONE);

                                    if (!SELECTED_GROUP.equalsIgnoreCase("*ALL#"))
                                        tv_.setText(SELECTED_GROUP);
                                    LinearLayout ll = (LinearLayout) findViewById(R.id.linear_chatscreen_3);
                                    ll.setVisibility(View.GONE);
                                    SHOWGROUPS = true;
                                    progress_bar.setVisibility(View.GONE);
                                    gv_1.setVisibility(View.VISIBLE);
                                }
                            });

                        }
                    };
                    progress_bar.setVisibility(View.VISIBLE);
                    gv_1.setVisibility(View.GONE);
                    Thread _mthread = new Thread(runnable);
                    _mthread.start();

                }
            });


            gv_1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {

                    final ImageView image_msg = (ImageView) view
                            .findViewById(R.id.image_tosend_chat_screen);
                    image_msg.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            String _tag_ = image_msg.getTag().toString();
                            if (_tag_ != null)
                                if (_tag_.equalsIgnoreCase("~PLATFORM~MORE~")) {
                                    progress_bar.setVisibility(View.VISIBLE);
                                    gv_1.setVisibility(View.GONE);

                                    Runnable runnable_004 = new Runnable() {
                                        @Override
                                        public void run() {

                                            THREAD_LENGTH_TO_DISP = THREAD_LENGTH_TO_DISP + 10;
                                            al_chats.clear();
                                            _thread_0 = new PublicChats(getBaseContext(), MainActivity.SELECTED_GROUP);
                                            ArrayList<String> temp_ar = _thread_0.getmPublicChat(SELECTED_GROUP, THREAD_LENGTH_TO_DISP);
                                            if (_thread_0.getmPublicChatSize() >= THREAD_LENGTH_TO_DISP)
                                                al_chats.add("~PLATFORM~MORE~");

                                            TreeMap<Integer, String> seq_map = new TreeMap<Integer, String>();
                                            Time t = null;

                                            for (String tmp : temp_ar) {

                                                String[] tmp_0_0 = tmp.split("~::~");
                                                // Just Texting
                                                if (tmp_0_0.length > 4 && tmp_0_0.length < 7) {
                                                    seq_map.put(Integer.parseInt(tmp_0_0[5]), tmp);
                                                } else if (tmp_0_0.length > 7) {
                                                    seq_map.put(Integer.parseInt(tmp_0_0[9]), tmp);
                                                } else
                                                    al_chats.add(tmp);

                                            }
                                            Set set = seq_map.entrySet();
                                            Iterator itr = set.iterator();

                                            Map.Entry me = null;
                                            while (itr.hasNext()) {
                                                me = (Map.Entry) itr.next();
                                                String str_tmp = String.valueOf(me.getValue());
                                                al_chats.add(str_tmp);

                                            }

                                            MainActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    imageListAdapter = new ImageListAdapter(getBaseContext(), false,
                                                            CHAT_MODE, 0, peers);
                                                    gv_1.setAdapter(imageListAdapter);
                                                    gv_1.setSelection(imageListAdapter.getCount() - 1);

                                                }
                                            });

                                            ShowMessages(al_chats);
                                            isLoadPreviousDone = true;

                                        }
                                    };

                                    Thread _thread = new Thread(runnable_004);
                                    _thread.start();
                                }
                        }
                    });

                    return false;
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(this, ErrorActivity.class);
            intent.putExtra("error", e.getLocalizedMessage());
            startActivity(intent);


        }
        try {
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    public String get_received_message() {
        return _received_message;
    }

    public void set_received_message(String _received_message) {
        this._received_message = _received_message;
    }

    protected String getChithChatTime() {
        String chitchat_time = "";
        Calendar cal;
        Date date;
        cal = new GregorianCalendar();
        date = new Date();
        cal.setTime(date);

        chitchat_time = cal.get(Calendar.DATE) + "/" + cal.get(Calendar.MONTH)
                + "/" + cal.get(Calendar.YEAR) + "  " + +cal.get(Calendar.HOUR)
                + ":" + cal.get(Calendar.MINUTE) + ":"
                + cal.get(Calendar.SECOND);
        return chitchat_time;
    }

    public void ShowResults(ArrayList<String> al, String str) {
        Intent intent = new Intent(MainActivity.this, PrivateActivity.class);
        intent.putStringArrayListExtra("Peers", al);
        intent.putExtra("groupName", groupName);
        startActivity(intent);
    }

    public void ShowMessages(final ArrayList<String> al) {
        Runnable _runnable_00 = new Runnable() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if (_thread_0 != null && al_chats != null)
                            if (_thread_0.getmPublicChatSize() > custom.getPub_message_thread_len() - 5 && custom.getPub_message_thread_len() - al_chats.size() > 0) {
                                if (al_chats.size() % 2 == 0) {
                                    if (custom.getLanguage() == 0)
                                        displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_5 + (custom.getPub_message_thread_len() - _thread_0.getmPublicChatSize())));
                                    else if (custom.getLanguage() == 1)
                                        displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_5_sv + (custom.getPub_message_thread_len() - _thread_0.getmPublicChatSize())));
                                    else if (custom.getLanguage() == 2)
                                        displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_5_sp + (custom.getPub_message_thread_len() - _thread_0.getmPublicChatSize())));
                                    else if (custom.getLanguage() == 3)
                                        displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_5_pr + (custom.getPub_message_thread_len() - _thread_0.getmPublicChatSize())));
                                    else if (custom.getLanguage() == 4)
                                        displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_5_fr + (custom.getPub_message_thread_len() - _thread_0.getmPublicChatSize())));
                                    else
                                        displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_5 + (custom.getPub_message_thread_len() - _thread_0.getmPublicChatSize())));

                                }
                            }
                        ;
                        gv_1.setVisibility(View.VISIBLE);
                        progress_bar.setVisibility(View.GONE);

                        if (al.size() == 0) {
                            if (custom.getLanguage() == 0)
                                al.add(getResources().getString(R.string.string_main_one_13_0));
                            else if (custom.getLanguage() == 1)
                                al.add(getResources().getString(R.string.string_main_one_13_0_sv));
                            else if (custom.getLanguage() == 2)
                                al.add(getResources().getString(R.string.string_main_one_13_0_sp));
                            else if (custom.getLanguage() == 3)
                                al.add(getResources().getString(R.string.string_main_one_13_0_pr));
                            else if (custom.getLanguage() == 4)
                                al.add(getResources().getString(R.string.string_main_one_13_0_fr));
                            else
                                al.add(getResources().getString(R.string.string_main_one_13_0));
                        }

                        //every 10 messages
                        if (al_chats.size() % 10 == 0) al.add("~ADV~PLATFORM~");
                        imageListAdapter.setSetProfilePictureGone(false);
                        imageListAdapter.addItem(al);

                        if (AUTO_SCROLL_MESSAGE_LIST) {
                            gv_1.setSelection(imageListAdapter.getCount() - 1);
                            AUTO_SCROLL_MESSAGE_LIST = false;
                        } else {
                            gv_1.setSelection(imageListAdapter.getCount() - 1);
                        }
                        // Saves  Messages ...
                        if (al.size() == 1 && _thread_0 != null) {
                            _thread_0.savePublicChats(SELECTED_GROUP, "pbchat", al);
                        } else
                            _thread_0.savePublicChats(SELECTED_GROUP, "pbchat", al);


                    }
                });
            }
        };
        Thread _mthread = new Thread(_runnable_00);
        _mthread.start();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        try {
            Runnable runnable_00 = new Runnable() {
                @Override
                public void run() {
                    if (requestCode == SELECT_PROFILE_IMAGE) {
                        unbindService(mConnection);
                        Uri selectedImageUri = data.getData();
                        File file = new File(getPath(selectedImageUri));

                        if (file.length() <= 1048576) {
                            // OI FILE Manager
                            filemanagerstring = selectedImageUri.getPath();

                            // MEDIA GALLERY
                            selectedImagePath = getPath(selectedImageUri);
                            uri_str_mimage_messsage = selectedImagePath.toString();

                            // Message Format ---
                            // MESSAGETYPE~::~GROUPNAME~::~UCI~::~MESSAGE~::~TIME~::~MESSAGESERIAL~::~PW


                            Message = Constants.PUBLICIMAGEFILE + "~::~" + SELECTED_GROUP + "~::~" + custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~" + selectedImagePath + "~::~" + format.format(new Date()) + "~::~" + getUniqueSerialNumber(3, 9, gr_clk) + "~::~" + "123456";
                            al_chats.add(Message);
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageListAdapter.setMessage_imageURI(Uri.parse(selectedImagePath));
                                    imageListAdapter.setSetProfilePictureGone(false);
                                    imageListAdapter.addItem(al_chats);
                                    imageListAdapter.set_image_message_uri(Uri.parse(uri_str_mimage_messsage));
                                    imageListAdapter.set_image_message_ready(true);
                                    custom.set_IMAGE_SEND_(true);
                                }
                            });


                            // Inform ---image is waiting to be sent
                            mPrefs = getSharedPreferences("myprofile", 0);
                            SharedPreferences.Editor ed = mPrefs.edit();
                            ed.putBoolean("_IMAGE_SEND_", true);
                            ed.putString("URI-IMAGE", uri_str_mimage_messsage);
                            ed.commit();

                            // show images
                            ShowMessages(al_chats);
                        }
                    } else {
                        if (custom.getLanguage() == 0)
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_6));
                        else if (custom.getLanguage() == 1)
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_6_sv));
                        else if (custom.getLanguage() == 2)
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_6_sp));
                        else if (custom.getLanguage() == 3)
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_6_pr));
                        else if (custom.getLanguage() == 4)
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_6_fr));
                        else
                            displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_6));

                    }
                }
            };

            Thread _mthread = new Thread(runnable_00);
            _mthread.start();

        } catch (Exception e) {
            bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);
            e.printStackTrace();
        } finally {
            bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);

            Runnable runnable_00_1 = new Runnable() {
                @Override
                public void run() {
                    //Image Send test//
                    try {
                        if(!custom.isSilent_One())mp_mess_out.start();
                        String test_img_link = "102~::~Contexter~::~addisphone@miun.se/mobi~::~" + uri_str_mimage_messsage + "~::~2016-12-18 16:43:40~::~2068620~::~123456";
                        Bundle b = new Bundle();
                        b.putString("NEW_MESSAGE_FLAG", Message);
                        b.putString("NEW_MESSAGE_FLAG_FA", Message);
                        bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);
                        android.os.Message replyMsg = android.os.Message
                                .obtain(null, PlatformManagerNode.NEW_MESSAGE_FLAG, 0, 0);
                        replyMsg.setData(b);
                        msg.replyTo = mActivityMessenger;
                        mPlatformManagerNodeMessenger.send(replyMsg);


                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent intent = new Intent(MainActivity.this,
                                ErrorActivity.class);
                        intent.putExtra("error",
                                e.getLocalizedMessage());
                        startActivity(intent);
                    }
                }
            };

            Thread _mthread = new Thread(runnable_00_1);
            _mthread.start();

        }
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

                intent = new Intent(MainActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_advanced:

                intent = new Intent(MainActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(MainActivity.this, SensorReadingsActivity.class);
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
    protected void onStart() {
        super.onStart();
        client.connect();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://se.sensiblethings.app.chitchato.activities/http/host/path")
        );
    }

    @Override
    public void onStop() {
        System.gc();
        super.onStop();

        if (_BOUND) {
//            unbindService(mConnection);
        }


        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://se.sensiblethings.app.chitchato.activities/http/host/path")
        );
        mPrefs = getSharedPreferences("cache", 0);

        // save preferences
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("MA_VISITED", true);
        ed.commit();



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {

            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    @Override
    public void onBackPressed() {

        if (SELECTED_GROUP.equalsIgnoreCase("*ALL#"))
            dialog_one = new DialogOne(this, false, 13);
        else {
            {
                SELECTED_GROUP = "*ALL#";
                forwarding_from = 100;
            }
        }
        dialog_one.show();
        return;
    }

    //  Set Path for Home Folder for media
    public void setAppFolder(String file_name) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/Media");
        if (!file.exists()) {
            new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/Media").mkdir();
        }
    }

    public int getUniqueSerialNumber(int init_index, int fin_index) {
        int unique_number = 0;
        long l = System.currentTimeMillis();
        long l_ = l / 1000;
        unique_number = Integer.parseInt(String.valueOf(l_).substring(init_index));
        return unique_number;
    }

    public int getUniqueSerialNumber(int init_index, int fin_index, Long precision_val) {
        int unique_number = 0;
        long l = System.currentTimeMillis() + precision_val;
        long l_ = l / 1000;
        unique_number = Integer.parseInt(String.valueOf(l_).substring(init_index));
        return unique_number;
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

    // Check if the Internet is Available
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class ActivityHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;
        int count = 1;

        public ActivityHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {

            switch (msg.what) {
                case PlatformManagerNode.NEW_MESSAGE_FLAG: {
                    incoming_message = msg.getData().getString("NEW_MESSAGE_FLAG");
                }
                case PlatformManagerNode.NEW_MESSAGE_FLAG_FA: {
                    incoming_message = msg.getData().getString("NEW_MESSAGE_FLAG_FA");
                }
                case PlatformManagerNode.NEW_MESSAGE_FLAG_TEST: {
                    _received_message = msg.getData().getString("_mess");
                    set_received_message(msg.getData().getString("_mess"));
                    {
                        MainActivity.this.runOnUiThread(new Runnable() {

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
                                if (get_received_message() != null)
                                    if (get_received_message().contains("[Resolve Response]")) {
                                        {
                                            if (temp_list_of_messages.contains(get_received_message()) == false) {
                                                temp_list_of_messages.add(get_received_message());
                                            }
                                        }
                                    } else if (get_received_message().contains(":100~::~")) {
                                        _tmp_msg_0_0 = get_received_message().substring(get_received_message().indexOf(":") + 1);
                                        if (temp_list_of_messages.contains(_tmp_msg_0_0) == false) {
                                            if (_tmp_msg_0_0.contains("~::~" + SELECTED_GROUP + "~::~")) {
                                                if (!al_chats.contains(_tmp_msg_0_0)) {
                                                    al_chats.add(_tmp_msg_0_0);

                                                    ShowMessages(al_chats);
                                                    if(!custom.isSilent_One())mp_mess_out.start();
                                                    temp_list_of_messages.add(_tmp_msg_0_0);
                                                }
                                            } else {
                                                listadapter.setCurrActivePosition(_tmp_msg_0_0.split("~::~")[1]);
                                                //lv_2.setAdapter(listadapter);
                                            }
                                        }
                                    } else if (get_received_message().contains(":101~::~")) {
                                    } else if (get_received_message().contains(":102~::~")) {
                                        _tmp_msg_0_0 = get_received_message().substring(get_received_message().indexOf(":") + 1);
                                        if (temp_list_of_messages.contains(_tmp_msg_0_0) == false) {
                                            if (_tmp_msg_0_0.contains("~::~" + SELECTED_GROUP + "~::~")) {
                                                if (!al_chats.contains(_tmp_msg_0_0)) {
                                                    al_chats.add(_tmp_msg_0_0);
                                                    ShowMessages(al_chats);
                                                    if(!custom.isSilent_One())mp_mess_out.start();
                                                    temp_list_of_messages.add(_tmp_msg_0_0);
                                                }
                                            } else {
                                                listadapter.setCurrActivePosition(_tmp_msg_0_0.split("~::~")[1]);
                                                //lv_2.setAdapter(listadapter);
                                            }
                                        }

                                    } else if (get_received_message().split("]").length > 1) {
                                        if (temp_list_of_messages.contains(get_received_message().split("]")[1]) == false) {
                                        }
                                    } else {
                                    }


                                if (!custom.IS_NODE_BOOTSTRAP_()) {
                                    if (PREV_ST_STATUS != PlatformManagerNode.ST_PLATFORM_IS_UP) {
                                        if (!PlatformManagerNode.ST_PLATFORM_IS_UP) {
                                            al_chats.add("~PLATFORM~DOWN~");
                                            ShowMessages(al_chats);
                                            if(!custom.isSilent_One()) mp_platform_down.start();
                                        } else {
                                            al_chats.add("~PLATFORM~UP~");
                                            ShowMessages(al_chats);
                                            if(!custom.isSilent_One())mp_platform_up.start();
                                        }
                                        PREV_ST_STATUS = PlatformManagerNode.ST_PLATFORM_IS_UP;
                                    }
                                } else {
                                    if (PREV_ST_STATUS != PlatformManagerBootstrap.ST_PLATFORM_IS_UP) {
                                        if (!PlatformManagerBootstrap.ST_PLATFORM_IS_UP) {
                                            al_chats.add("~PLATFORM~DOWN~");
                                            ShowMessages(al_chats);
                                            if(!custom.isSilent_One())mp_platform_down.start();
                                        } else {
                                            al_chats.add("~PLATFORM~UP~");
                                            ShowMessages(al_chats);
                                            if(!custom.isSilent_One())mp_platform_up.start();
                                        }
                                        PREV_ST_STATUS = PlatformManagerBootstrap.ST_PLATFORM_IS_UP;
                                    }
                                }


                            }
                        });

                    }

                }

                default: {
                    {

                    }

                }
            }

        }

    }

    public class ShowButtonsThread extends Thread {
        public ShowButtonsThread() {
        }

        public ShowButtonsThread(ArrayList<String> array_list) {
        }

        @Override
        public void run() {
            while (true) {

                try {

                    if (SHOWGROUPS == true)
                        SHOWGROUPS = false;
                    this.interrupt();

                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

            }
        }
    }


    // Send Button BK-Task---
    public class BKGroundTask_DialogTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... arg0) {
            ArrayList<String> temp_ar = new ArrayList<String>();
            try {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (PlatformManagerBootstrap.ST_PLATFORM_IS_UP || PlatformManagerNode.ST_PLATFORM_IS_UP || (System.currentTimeMillis() - systimemillis) > 10000) {
                                {
                                    busy.dismiss();
                                    // Notify the user that  he can only use Local
                                    if (!custom.getPreferred_bs_ip().isEmpty())
                                        if (!custom.is_BOOTSTRAP_IP_() && (PlatformManagerBootstrap.ST_PLATFORM_IS_UP || PlatformManagerNode.ST_PLATFORM_IS_UP)) {
                                            if (custom.getLanguage() == 0)
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_7));
                                            else if (custom.getLanguage() == 1)
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_7_sv));
                                            else if (custom.getLanguage() == 2)
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_7_sp));
                                            else if (custom.getLanguage() == 3)
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_7_pr));
                                            else if (custom.getLanguage() == 4)
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_7_fr));
                                            else
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_7));


                                        } else {
                                            if (custom.getLanguage() == 0)
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_8));
                                            else if (custom.getLanguage() == 1)
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_8_sv));
                                            else if (custom.getLanguage() == 2)
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_8_sp));
                                            else if (custom.getLanguage() == 3)
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_8_pr));
                                            else if (custom.getLanguage() == 4)
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_8_fr));
                                            else
                                                displayCustomizedToast(MainActivity.this, getResources().getString(R.string.string_main_one_8));
                                        }

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


                });

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Intent intent = new Intent(MainActivity.this,
                        ErrorActivity.class);
                intent.putExtra("error",
                        e.getLocalizedMessage());
                startActivity(intent);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Thread.sleep(100);
                if (busy.isShowing()) new BKGroundTask_DialogTask().execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

    // Message Manipulation Background Task----
    public class BKGroundTask_1 extends AsyncTask<String, String, String> {

        boolean flag = false;
        String _message_0;


        @Override
        protected String doInBackground(String... arg0) {
            this.flag = false;
            ArrayList<String> temp_ar = new ArrayList<String>();
            try {

                groups = new Groups(MainActivity.this);
                peers = new Peers(MainActivity.this);
                setAppFolder("");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Intent intent = new Intent(MainActivity.this,
                        ErrorActivity.class);
                intent.putExtra("error",
                        e.getLocalizedMessage());
                startActivity(intent);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                result = "done";
                this.flag = true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public String get_message_0() {
            return _message_0;
        }

        public void set_message_0(String _message_0) {
            this._message_0 = _message_0;
        }
    }

}