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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import se.sensiblethings.app.chitchato.adapters.ListAdapter;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerNode;
import se.sensiblethings.app.chitchato.context.ContextManager;
import se.sensiblethings.app.chitchato.extras.Busy;
import se.sensiblethings.app.chitchato.extras.ChitchatDelayTimer;
import se.sensiblethings.app.chitchato.extras.Constants;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.ExceptionHandlerOne;
import se.sensiblethings.app.chitchato.extras.LocalStorageGroup;
import se.sensiblethings.app.chitchato.extras.SysError;
import se.sensiblethings.app.chitchato.kernel.ChitchatoParametersRetrieval;
import se.sensiblethings.app.chitchato.kernel.ChitchatoPlatform;
import se.sensiblethings.app.chitchato.kernel.Groups;
import se.sensiblethings.app.chitchato.kernel.Peers;
import se.sensiblethings.app.chitchato.kernel.PublicChats;
import se.sensiblethings.app.chitchato.kernel.RESTHandler;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class DebugActivity extends Activity {
    TextView tv_txt, textView, tv_;
    Button btn;
    ImageView image_view_profile = null, image_to_send = null;
    ProgressBar progress_bar;
    GridView gridView1 = null;
    EditText edit_txt_send;
    ProgressBar progress = null;
    private ProgressDialog prgDialog;
    long l;
    String temp;
    String[] a;
    Message message;
    String nodes = "%";

    GridView gv_1;
    LinearLayout linear_layout;
    ChitchatoPlatform sensibleThingsPlatform;
    Communication communication;
    SensibleThingsNode sensiblethingsnode;
    ChitchatoParametersRetrieval parms;
    protected SharedPreferences mPrefs, mPrefs_;
    protected boolean CHAT_MODE = true;
    protected String MODE = "Ordinary Mode";
    protected boolean PRIVATE_MODE = false;
    protected boolean PUBLIC_MODE = true;
    private Groups groups;
    private Peers peers;
    private PublicChats public_chats;
    private String Message = "";
    private String chatMessage = "";
    private String contextMessage = "";
    private String localPeerName = "";
    private String groupName = "#None";
    private boolean MA_VISITED = false;
    private static ArrayList<String> al_chats = null;
    private ArrayList<String> al_chats_err = null;

    private ListAdapter listadapter;
    protected Customizations custom;
    protected boolean SHOWGROUPS = true;
    protected ContextManager context_manager;
    protected ChitchatDelayTimer delay_timer;
    private Busy busy, busy_;
    private SysError error;
    private EditProfileActivity.GetProfile profile;
    protected Bundle instance;
    public String SELECTED_GROUP = "*ALL#";
    public String SELECTED_PEER = "";

    // Date Parameters
    static String date;
    String date_pattern = "yyyy-MM-dd HH:mm:ss";
    Format format = new SimpleDateFormat(date_pattern);
    private static final int SELECT_PROFILE_IMAGE = 1;
    private String selectedImagePath;
    private String filemanagerstring;
    private String incoming_message = "#";

    // Service Messagenger---
    Messenger mPlatformManagerNodeMessenger;
    android.os.Message msg = android.os.Message.obtain(null,
            PlatformManagerNode.NEW_MESSAGE_FLAG, 0, 0);
    android.os.Message msg_ = android.os.Message.obtain(null,
            PlatformManagerNode.NEW__GROUP_MESSAGE_FLAG, 0, 0);

    Messenger mActivityMessenger = new Messenger(
            new ActivityHandler(this));
    Intent service_intent;
    boolean SERVICE_BOUND = false;

    String _received_message = "Initial";
    PlatformManagerNode local_service;


    // Sensor Manager
    SensorManager mSensorManager;

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

            setContentView(custom.getDebugScreen());
            //setTitle(MODE);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            btn = (Button) findViewById(R.id.btn_send_chatscreen);
            // progress_bar = (ProgressBar) findViewById(R.id.progressBar1);
            tv_txt = (TextView) findViewById(R.id.edt_chatScreen_main_logger);
            edit_txt_send = (EditText) findViewById(R.id.edt_chatScreen);
            tv_ = (TextView) findViewById(R.id.tv_empty_bar_chatscreen);

            Typeface tf = Typeface.createFromAsset(getAssets(),
                    "fonts/COMIC.TTF");

            Typeface tf_pala = Typeface.createFromAsset(getAssets(),
                    "fonts/pala.ttf");

            // Font face
            tv_txt.setTypeface(tf_pala);
            edit_txt_send.setTypeface(tf_pala);
            tv_txt.setMovementMethod(new ScrollingMovementMethod());

            btn.setTypeface(tf_pala);
            tv_.setTypeface(tf);


            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view_ = inflater.inflate(R.layout.dialog_one, null);
            TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);
            LinearLayout linearLayout = (LinearLayout) view_.findViewById(R.id.linear1_1_dialog_1_1);
            linearLayout.setVisibility(View.GONE);
            if (custom.getLanguage() == 0) {
                tv_0_0_.setText("   Send: W, X , Y or Z    ");
                tv_0_0_.append("\n      W: Groups");
                tv_0_0_.append("\n      X: Advertisements");
                tv_0_0_.append("\n      Y: Contexting");
                tv_0_0_.append("\n      Z: Platform  \n");

            } else if (custom.getLanguage() == 1) {
                tv_0_0_.setText("   Skicka: W, X , Y or Z    ");
                tv_0_0_.append("\n      W: Groups");
                tv_0_0_.append("\n      X: Advertisements");
                tv_0_0_.append("\n      Y: Contexting");
                tv_0_0_.append("\n      Z: Platform  \n");

            } else if (custom.getLanguage() == 2) {
                tv_0_0_.setText("   Enviar: W, X , Y or Z    ");
                tv_0_0_.append("\n      W: Groups");
                tv_0_0_.append("\n      X: Advertisements ");
                tv_0_0_.append("\n      Y: Contexting ");
                tv_0_0_.append("\n      Z: Platform  \n");

            } else if (custom.getLanguage() == 3) {

                tv_0_0_.setText("   Enviar: W, X , Y or Z    ");
                tv_0_0_.append("\n      W: Groups");
                tv_0_0_.append("\n      X: Advertisements");
                tv_0_0_.append("\n      Y: Contexting");
                tv_0_0_.append("\n      Z: Platform  \n");

            } else if (custom.getLanguage() == 4) {
                tv_0_0_.setText("   Envoyer: W, X , Y or Z    ");
                tv_0_0_.append("\n      W: Groups");
                tv_0_0_.append("\n      X: Advertisements");
                tv_0_0_.append("\n      Y: Contexting ");
                tv_0_0_.append("\n      Z: Platform  \n");

            } else {
                tv_0_0_.setText("   Send_en: W, X , Y or Z    ");
                tv_0_0_.append("\n      W: Groups");
                tv_0_0_.append("\n      X: Advertisements");
                tv_0_0_.append("\n      Y: Contexting");
                tv_0_0_.append("\n      Z: Platform  \n");
            }


            Toast toast = new Toast(DebugActivity.this);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(view_);
            toast.show();



            final ArrayList<String> peers = new ArrayList<String>();
            busy = new Busy(this, false, -1);
            delay_timer = new ChitchatDelayTimer(this, error);

            service_intent = new Intent(this, PlatformManagerNode.class);
            bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);

            // Connect to ST Platform
            new BKGroundTask_0_().execute();


            // Context Information//
            if (!CHAT_MODE) {

                mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                context_manager = new ContextManager(getBaseContext(), localPeerName, mSensorManager);
            }






            btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    try {
                        if (isInternetAvailable()) {
                            String _str_00 = edit_txt_send.getText().toString().trim();
                            if (!(_str_00.equalsIgnoreCase("W") || _str_00.equalsIgnoreCase("X") || _str_00.equalsIgnoreCase("Y") || _str_00.equalsIgnoreCase("Z"))) {
                                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view_ = inflater.inflate(R.layout.dialog_one, null);
                                TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);
                                LinearLayout linearLayout = (LinearLayout) view_.findViewById(R.id.linear1_1_dialog_1_1);
                                linearLayout.setVisibility(View.GONE);
                                tv_0_0_.setText("   Send: W, X , Y or Z    ");
                                tv_0_0_.append("\n      W: Groups");
                                tv_0_0_.append("\n      X: Advertisements are working ");
                                tv_0_0_.append("\n      Y: Contexting is working ");
                                tv_0_0_.append("\n      Z: General  \n");
                                Toast toast = new Toast(DebugActivity.this);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(view_);
                                toast.show();
                            } else if (CHAT_MODE == false && mPlatformManagerNodeMessenger != null) {
                                chatMessage = edit_txt_send.getText().toString().trim();
                                contextMessage = context_manager.getUserContext()
                                        .getLocation()
                                        + "~::~"
                                        + context_manager.getUserContext()
                                        .getTemprature()
                                        + "~::~"
                                        + context_manager.getUserContext()
                                        .getLimunosity()
                                        + "~::~"
                                        + context_manager.getUserContext()
                                        .getAcce()
                                        + "~::~"
                                        + context_manager.getUserContext()
                                        .getPressure();

                                Message = chatMessage + "~::~";
                                Message = Message + contextMessage + "~::~"
                                        + format.format(new Date());
                                //Messsage Formats
                                //MESSAGETYPE~::~GROUPNAME~::~UCI~::~MESSAGE~::~SPEED~::~LUMINOSITY~::~SOUNDPRESSURE~::~ADDRESS~::~TIME~::~MESSAGESERIAL~::~PW
                                Message = Constants.DEBUG + "~::~" + PlatformManagerNode._mGroup + "~::~" + PlatformManagerNode._mUCI + "~::~" +
                                        Message + "~::~" + getUniqueSerialNumber(3, 9) + "~::~" + "######";

                                //Message = edit_txt_send.getText().toString();
                                // ---Send New Message Flag---//


                                Bundle b = new Bundle();
                                b.putString("NEW_MESSAGE_FLAG", Message);
                                b.putString("NEW_MESSAGE_FLAG_FA", Message);

                                android.os.Message replyMsg = android.os.Message
                                        .obtain(null,
                                                PlatformManagerNode.NEW_MESSAGE_FLAG,
                                                0, 0);
                                replyMsg.setData(b);
                                msg.replyTo = mActivityMessenger;

                                //android.os.Message message_ = new android.os.Message();
                                //message_.setData(b);
                                mPlatformManagerNodeMessenger.send(replyMsg);


                                // ---Initiate Receiving New Message Flag---//
                                android.os.Message msg = android.os.Message.obtain(null,
                                        PlatformManagerNode.NEW_MESSAGE_FLAG_TEST, 0, 0);
                                msg.replyTo = mActivityMessenger;
                                mPlatformManagerNodeMessenger.send(msg);
                                //tv_txt.append("\n _mess  >>  Message Sent! >>"  );
                                Message = Message.replaceAll("~::~","  ");
                                tv_txt.append("\n " + PlatformManagerNode._mUCI + " _mess  >>  " +  Message+ " >>");
                                edit_txt_send.setText("");


                            } else if (CHAT_MODE == true && mPlatformManagerNodeMessenger != null) {

                                Message = edit_txt_send.getText().toString() + "~::~";
                                //Messsage Formats
                                //MESSAGETYPE~::~GROUPNAME~::~UCI~::~MESSAGE~::~TIME~::~MESSAGESERIAL~::~PW
                                Message = Constants.DEBUG + "~::~" + PlatformManagerNode._mGroup + "~::~" + PlatformManagerNode._mUCI + "~::~" +
                                        Message + format.format(new Date()) + "~::~" + getUniqueSerialNumber(3, 9) + "~::~" + "######";

                                try {
                                    // ---Send New Message Flag---//
                                    Bundle b = new Bundle();
                                    b.putString("NEW_MESSAGE_FLAG", Message);
                                    b.putString("NEW_MESSAGE_FLAG_FA", Message);

                                    android.os.Message replyMsg = android.os.Message
                                            .obtain(null,
                                                    PlatformManagerNode.NEW_MESSAGE_FLAG,
                                                    0, 0);
                                    replyMsg.setData(b);
                                    msg.replyTo = mActivityMessenger;

                                    //android.os.Message message_ = new android.os.Message();
                                    //message_.setData(b);
                                    mPlatformManagerNodeMessenger.send(replyMsg);

                                    // ---Initiate Receiving New Message Flag---//
                                    android.os.Message msg = android.os.Message.obtain(null,
                                            PlatformManagerNode.NEW_MESSAGE_FLAG_TEST, 0, 0);
                                    msg.replyTo = mActivityMessenger;
                                    mPlatformManagerNodeMessenger.send(msg);

                                } catch (RemoteException re) {
                                }



                                Message = Message.replaceAll("~::~","  ");
                                tv_txt.append("\n " + PlatformManagerNode._mUCI + " _mess  >>  " + Message + " >>");
                                edit_txt_send.setText("");

                            } else {

                                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view_ = inflater.inflate(R.layout.dialog_one, null);
                                TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);
                                tv_0_0_.setText("Service not Started!  Restart the App.");
                                Toast toast = new Toast(DebugActivity.this);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(view_);
                                toast.show();

                            }

                        } else
                        {
                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view_ = inflater.inflate(R.layout.dialog_one, null);
                            TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);
                            tv_0_0_.setText("Platform out of Sync!  Check Your Network.");
                            Toast toast = new Toast(DebugActivity.this);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(view_);
                            toast.show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent intent = new Intent(DebugActivity.this, ErrorActivity.class);
                        intent.putExtra("error", e.getStackTrace());
                        intent.putExtra("ACTIVITY", "DebugActivity");
                        startActivity(intent);
                    }

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(this, ErrorActivity.class);
            intent.putExtra("error", e.getLocalizedMessage());
            startActivity(intent);
        }
    }

    public String get_received_message() {
        return _received_message;
    }

    public void set_received_message(String _received_message) {
        this._received_message = _received_message;
    }

    PlatformManagerNode update_service;

    class ActivityHandler extends Handler {
        private final WeakReference<DebugActivity> mActivity;
        int count = 1;
        ArrayList<String> temp_list_of_messages = new ArrayList<String>();

        public ActivityHandler(DebugActivity activity) {
            mActivity = new WeakReference<DebugActivity>(activity);
        }

        String tfnt_previous = "#", tfnt_current = "#";

        @Override
        public void handleMessage(final Message msg) {
            tv_txt.append( " * * * " );
            switch (msg.what) {
                case PlatformManagerNode.NEW_MESSAGE_FLAG: {
                    incoming_message = msg.getData().getString("NEW_MESSAGE_FLAG");
                }
                case PlatformManagerNode.NEW_MESSAGE_FLAG_FA: {
                    Toast.makeText(mActivity.get(),
                            "From Service: " + msg.getData().getString("NEW_MESSAGE_FLAG_FA"),
                            Toast.LENGTH_SHORT).show();
                    incoming_message = msg.getData().getString("NEW_MESSAGE_FLAG_FA");
                }
                case PlatformManagerNode.NEW_MESSAGE_FLAG_TEST: {
                    _received_message = msg.getData().getString("_mess");
                    set_received_message(msg.getData().getString("_mess"));
                    Runnable _runnable_00 = new Runnable() {
                        @Override
                        public void run() {
                            try {

                                android.os.Message msg_ = android.os.Message.obtain(null,
                                        PlatformManagerNode.NEW_MESSAGE_FLAG_TEST, 0, 0);
                                msg_.replyTo = mActivityMessenger;

                                tfnt_current = msg.getData().toString();
                                if (mPlatformManagerNodeMessenger != null)
                                    mPlatformManagerNodeMessenger.send(msg_);

                                if (tfnt_current.equalsIgnoreCase(tfnt_previous))
                                    Thread.sleep(0);
                                else {
                                    tfnt_previous = tfnt_current;
                                }


                            } catch (RemoteException re) {
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    };
                    Thread _mthread_00 = new Thread(_runnable_00);
                    _mthread_00.start();


                    DebugActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (get_received_message().contains("[Resolve Response]")) {
                                {
                                    if (temp_list_of_messages.contains(get_received_message()) == false) {
                                      String temp_mess = get_received_message().replaceAll("~::~","  ");
                                        tv_txt.append("\n _mess  >>  -- RESOLVE RESPONSE --\n " + temp_mess + "");
                                        temp_list_of_messages.add(get_received_message());
                                    }
                                }
                            } else if (get_received_message().split("]").length > 1)
                                if (temp_list_of_messages.contains(get_received_message().split("]")[1]) == false) {

                                    String temp_mess = get_received_message().split("]")[1].replaceAll("~::~","  ");
                                    tv_txt.append("\n _mess  >> " + temp_mess);
                                    temp_list_of_messages.add(get_received_message().split("]")[1]);
                                }

                        }
                    });

                }

                default: {
                    {

                    }
                }
            }
        }

    }

    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mPlatformManagerNodeMessenger = new Messenger(service);
            SERVICE_BOUND = true;

        }

        public void onServiceDisconnected(ComponentName className) {
            update_service = null;
            SERVICE_BOUND = false;
            Toast.makeText(DebugActivity.this, "Disconnected",
                    Toast.LENGTH_SHORT).show();
        }
    };

    public void ShowMessages(final ArrayList<String> al) {

        DebugActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (delay_timer.isSHOW_ERROR_DAILOG()) {
                    error.show();
                    delay_timer.setSHOW_ERROR_DAILOG(false);
                }

                if (al.size() == 0)
                    al.add("#No Messages#");
                delay_timer.setSHOW_BUSY_DAILOG(false);
                delay_timer.setSHOW_ERROR_DAILOG(false);
                delay_timer.interrupt();

            }
        });
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PROFILE_IMAGE) {
            Uri selectedImageUri = data.getData();

            // OI FILE Manager
            filemanagerstring = selectedImageUri.getPath();
            // MEDIA GALLERY
            selectedImagePath = getPath(selectedImageUri);


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

                intent = new Intent(DebugActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_advanced:

                intent = new Intent(DebugActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(DebugActivity.this, SensorReadingsActivity.class);
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
        service_intent = new Intent(this, PlatformManagerNode.class);
        if (!SERVICE_BOUND) bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);


    }

    @Override
    public void onStop() {
        super.onStop();
        mPrefs = getSharedPreferences("cache", 0);
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("MA_VISITED", true);
        ed.commit();

        // Send Message to Stop the Platform
        Bundle b = new Bundle();
        b.putString("NEW__SERVICE_STOP_MESSAGE_FLAG", "STOP");

        android.os.Message replyMsg = android.os.Message.obtain(null,
                PlatformManagerNode.NEW__SERVICE_STOP_MESSAGE_FLAG, 0, 0);
        replyMsg.setData(b);

        msg.replyTo = mActivityMessenger;

        try {
            if (mPlatformManagerNodeMessenger != null)
                mPlatformManagerNodeMessenger.send(replyMsg);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (SERVICE_BOUND) {
            unbindService(mConnection);
            mPlatformManagerNodeMessenger = null;

        }

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



    String continity = " . ";

    public class BKGroundTask_0_ extends AsyncTask<String, String, String> {
        boolean flag = false;

        @Override
        protected String doInBackground(String... arg0) {
            this.flag = false;

            DebugActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //
                    // progress_bar.setVisibility(View.VISIBLE);
                    try {
                        if (mPlatformManagerNodeMessenger == null) {
                            bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);
                            tv_txt.append(continity);
                            Thread.sleep(1000);
                            continity = continity + " .";
                            new BKGroundTask_0_().execute();
                        } else {
                            try {
                                if (mPlatformManagerNodeMessenger != null) {
                                    Bundle b = new Bundle();
                                    b.putString("NEW_MESSAGE_FLAG", Message);
                                    b.putString("NEW_MESSAGE_FLAG_FA", Message);

                                    android.os.Message replyMsg = android.os.Message
                                            .obtain(null,
                                                    PlatformManagerNode.NEW_MESSAGE_FLAG,
                                                    0, 0);
                                    replyMsg.setData(b);
                                    msg.replyTo = mActivityMessenger;

                                    //android.os.Message message_ = new android.os.Message();
                                    //message_.setData(b);
                                    mPlatformManagerNodeMessenger.send(replyMsg);

                                    // ---Initiate Receiving New Message Flag---//
                                    android.os.Message msg = android.os.Message.obtain(null,
                                            PlatformManagerNode.NEW_MESSAGE_FLAG_TEST, 0, 0);
                                    msg.replyTo = mActivityMessenger;
                                    mPlatformManagerNodeMessenger.send(msg);
                                    mPlatformManagerNodeMessenger.send(replyMsg);
                                    flag = true;
                                    tv_txt.append("\n   ***************");

                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Intent intent = new Intent(DebugActivity.this,
                                ErrorActivity.class);
                        intent.putExtra("error", "Connection to the Bootstrap Server Failed!! ");
                        startActivity(intent);
                    }

                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }

    }


    //Message Receiver BG worker
    public class BKGroundTask_0_0 extends AsyncTask<String, String, String> {
        boolean flag = false;

        @Override
        protected String doInBackground(String... arg0) {
            this.flag = false;
            try {
                // wait until the service is up
                while (mPlatformManagerNodeMessenger == null) {
                    Thread.sleep(4000);

                }

                // Keep recieving messages...
                //mActivityMessenger = new Messenger(
                //      new ActivityHandler(MainActivity.this));
                //this.wait(10000);

                android.os.Message msg = android.os.Message.obtain(null,
                        PlatformManagerNode.NEW_MESSAGE_FLAG_TEST, 0, 0);
                msg.replyTo = mActivityMessenger;
                try {
                    mPlatformManagerNodeMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                Thread.sleep(2000);



            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Intent intent = new Intent(DebugActivity.this,
                        ErrorActivity.class);
                intent.putExtra("error",
                        "Connection to the Bootstrap Server Failed!! ");
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

    }


    // Check if the Internet is Available
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public int getUniqueSerialNumber(int init_index, int fin_index) {
        int unique_number = 0;
        long l = System.currentTimeMillis();
        long l_ = l / 1000;
        unique_number = Integer.parseInt(String.valueOf(l_).substring(init_index));
        return unique_number;
    }


}