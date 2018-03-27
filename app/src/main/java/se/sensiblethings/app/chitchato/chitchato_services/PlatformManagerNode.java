package se.sensiblethings.app.chitchato.chitchato_services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.activities.ChoicesActivity;
import se.sensiblethings.app.chitchato.activities.EditProfileActivity;
import se.sensiblethings.app.chitchato.activities.ErrorActivity;
import se.sensiblethings.app.chitchato.activities.GroupContainerActivity;
import se.sensiblethings.app.chitchato.activities.MainActivity;
import se.sensiblethings.app.chitchato.extras.Constants;
import se.sensiblethings.app.chitchato.extras.CustomImageView;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.MDecoder;
import se.sensiblethings.app.chitchato.kernel.Groups;
import se.sensiblethings.app.chitchato.kernel.Peers;
import se.sensiblethings.app.chitchato.kernel.PublicChats;
import se.sensiblethings.disseminationlayer.disseminationcore.BinaryGetResponseListener;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.KelipsLookup;
import se.sensiblethings.interfacelayer.SensibleThingsListener;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public class PlatformManagerNode extends Service {
    public static final int NEW_MESSAGE_FLAG = 102;
    public static final int NEW_MESSAGE_FLAG_FA = 104;
    public static final int NEW_IMAGE_MESSAGE_FLAG = 105;
    public static final int NEW__SERVICE_STOP_MESSAGE_FLAG = 106;
    public static final int NEW__GROUP_MESSAGE_FLAG = 108;
    public static final int NEW_MESSAGE_FLAG_TEST = 1000;
    public static boolean ST_PLATFORM_IS_UP = false;
    public static boolean IS_IMAGE_MESSAGE_READY = false;
    public static String _mUCI = "@miun.se/mobi";
    public static String BOOTSTRAP_IP = "0.0.0.0";
    public static String BOOTSTRAP_NAME = "bootstrap@miun.se/random";
    public static String STARTUP_TIME = "- - -";
    public static String contexter_domain = "@miun.se/mobi";  // Contexter domain for test
    public static ArrayList<String> _peers_1;
    public static String _mMessage = "NO#";
    public static String _mGroup = "*ALL#@*ALL#"; // Default Group --- used for test and debingging
    public static GetPlatformUpdate getPlatformUpdate;
    static public ArrayList<String> PEERS_IN_SELECTED_GROUP = new ArrayList<String>();
    protected static boolean STOP_SERVICE = false;
    static File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "test_big.jpg");// Test
    //previous messsage sent time in millies -- sent to screen
    static Long MESSAGE_UPDATE_PREV = 0L;
    private static String new_message = "no messages";//incoming message
    private static String new_message_to_send = "no messages"; // outgoing message
    private static String new_image_message_to_send = "no messages";
    private static String new_message_to_send_prev = "no messages"; // outgoing message
    private static String new_image_meessage_to_send_prev = "no messages";
    public final IBinder mBinder = new LocalBinder();
    // list of uci
    public Peers _peers_0 = new Peers(getBaseContext(), MainActivity.SELECTED_GROUP);
    public Peers _peers_0_requesting_list = new Peers(getBaseContext(), MainActivity.SELECTED_GROUP, "rqsting"); // List of requesting nodes to join a group
    public Groups _groups_0 = new Groups(getBaseContext());
    public Context app_context;
    public Customizations custom;
    protected SharedPreferences mPrefs;

    //Tests
    String randomUci = "bootstrap@miun.se/random";
    int counter_000 = 0;// Just for test
    String randomMobi2 = "@miun.se/mobi";
    // static FileOutputStream fout; // Image writer
    FileOutputStream fout_out; // Image Writer
    FileInputStream fis_val;
    // NODE-UCI Mapper
    TreeMap<String, String> _node_uci_map = new TreeMap<String, String>();
    int counter = 1;
    // Image byte holders--- used for image file
    ArrayList<byte[]> array_byte = new ArrayList<byte[]>();
    TreeMap<Integer, byte[]> disarray_byte_holder_per = new TreeMap<Integer, byte[]>();
    TreeMap<String, Boolean> image_recieved_info_holder = new TreeMap<String, Boolean>();
    TreeMap<String, ArrayList> temp_image_byte_holder = new TreeMap<String, ArrayList>();
    TreeMap<String, TreeMap> disarray_byte_holder = new TreeMap<String, TreeMap>();
    TreeMap<String, String> _image_meta_data_temp = new TreeMap<String, String>();
    ArrayList<String> al_advertisements = new ArrayList<String>();
    File file_ = null, file_good = null;
    // Service Messagenger---
    android.os.Messenger mLocalWordServiceCCMessenger;
    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mLocalWordServiceCCMessenger = new Messenger(service);
            android.os.Message replyMsg = android.os.Message
                    .obtain(null,
                            PlatformManagerNode.NEW_MESSAGE_FLAG,
                            0, 0);
            try {
                mLocalWordServiceCCMessenger.send(replyMsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
        }
    };
    android.os.Message msg = android.os.Message.obtain(null,
            PlatformManagerNode.NEW_MESSAGE_FLAG, 0, 0);
    android.os.Message _replyMsg_flag_test = android.os.Message.obtain(null,
            PlatformManagerNode.NEW_MESSAGE_FLAG_TEST, 0, 0);
    Messenger mMessageHandler = new Messenger(
            new LocalWordServiceCCHandler(this));


    /// To Handle Repeated Notifcations
    TreeMap<String, String> temp_ipc_queque = new TreeMap<String, String>();// stores unique messages
    String date_pattern = "yyyy-MM-dd HH:mm:ss"; // Time format for Contexter
    Format format = new SimpleDateFormat(date_pattern);//
    // previous  update time holder [in millis] -- for bootstraps and nodes
    long LAST_UPDATE_TIME = 0;
    int COUNTER_0_000 = 0; // For test
    String BOOTSTRAPS = "~::~";// Bootstraps -- String
    String NODES = "~::~"; // Nodes --String
    Messenger _messenger = new Messenger(new LocalWordServiceCCHandler(
            this));
    private ArrayList<String> list = new ArrayList<String>();
    //test---
    private Chronometer mChronometer;
    private Long MESSAGE_DELIVARY_TIME_OLD = 0L;
    private Long MESSAGE_DELIVARY_TIME_NEW = 0L;
    private MediaPlayer mp_media_sense_stopped, mp_media_sense_running;


    public static String getNew_message() {
        return new_message;
    }

    public static void setNew_message(String nm) {
        new_message = nm;
    }

    public static void setMESSAGE_UPDATE_PREV(Long MESSAGE_UPDATE_PREV_) {
        MESSAGE_UPDATE_PREV = MESSAGE_UPDATE_PREV_;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        custom = new Customizations(this, -1);
        try {
            randomMobi2 = custom.getPeer_nick_name() + contexter_domain;
            _mUCI = randomMobi2;
            mp_media_sense_stopped = MediaPlayer.create(getBaseContext(), R.raw.pin_drop);
            mp_media_sense_running = MediaPlayer.create(getBaseContext(), R.raw.activate_secondary);


            //Start the Platform
            getPlatformUpdate = new GetPlatformUpdate("192.168.0.100");
            getPlatformUpdate.getBackGroundRunning();

            if (!custom.isFLAG_DISP_NO_IP()) {
                displayCustomizedToast(getApplicationContext(), "                   Welcome! \n" +
                        " MediaSense is starting up. ", false);
            }

            ///Save service states
            custom.setSERVICE_UP(true);
            mPrefs = getSharedPreferences("myprofile", 0);
            SharedPreferences.Editor ed = mPrefs.edit();
            ed.putBoolean("IS_SERVICE_UP", true);
            ed.commit();

            app_context = this.getApplicationContext();
            return Service.START_NOT_STICKY;

        } catch (Exception e) {
            if (isInternetAvailable())
                if (custom.isFLAG_DISP_NO_IP()) {
                    displayCustomizedToast(getApplicationContext(), "No valid or working BootStrap IP \n" + " Add IP.", false);
                }

            return Service.START_REDELIVER_INTENT;
        }

    }

    // test---
    @Override
    public void onCreate() {
        super.onCreate();
        mChronometer = new Chronometer(this);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return _messenger.getBinder();
    }

    // Check if the Internet is Available
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public synchronized void getImageToSend(FileInputStream fin,
                                            SensibleThingsPlatform platform, SensibleThingsNode source,
                                            String uci, String file_url, int cat) throws IOException {
        int flag = 0, i;
        byte _image_[];
        String extn = "";
        long file_size_byte = -1;

        //static int chunk_size = 1024;
        int chunk_num = -1;
        File file_to_send = new File(file_url);
        extn = ".jpg";

        if (extn.equalsIgnoreCase(".jpg") || extn.equals(".png") || extn.equals(".gif") || extn.equals(".tif") || extn.equals(".JPEG")) {
            try {
                String str = "";
                file_size_byte = file_to_send.length();
                chunk_num = (int) file_size_byte / 1024;
                System.out.println("Chunk num:" + chunk_num + "  " + (Long) (file_size_byte / 1024));
                for (int index = 1; index <= chunk_num + 1; index++) {
                    if (index == chunk_num + 1) {
                        _image_ = new byte[(int) (file_size_byte)];
                        fin.read(_image_, chunk_num * 1024,
                                (int) (file_size_byte - chunk_num * 1024));


                        //final chunck
                        platform.getDisseminationCore().notify(source, uci,
                                _image_);
                        // send image metadata
                        String _meta_data = Constants.IMAGEMETADATA + "~::~" + cat + "~::~" + MainActivity.SELECTED_GROUP + "~::~" + custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~" + extn + "~::~" + Uri.parse(file_url).getLastPathSegment() + "~::~" + file_size_byte + "~::~" + "123456";
                        platform.getDisseminationCore().notify(source, uci, _meta_data);

                    } else {
                        _image_ = new byte[index * 1024];
                        if (index == 1)
                            fin.read(_image_, 0, 1024);
                        else
                            fin.read(_image_, (index - 1) * 1024, 1024);


                        platform.getDisseminationCore().notify(source, uci,
                                _image_);

                    }

                }

                fin.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {

            }

        } else {

        }
    }

    public void MessageNotifier(View view, String mess_1, String mess_2, String mess_3, boolean status, boolean vibrate) {
        Intent intent = new Intent(getApplicationContext(),
                ErrorActivity.class);
        intent.putExtra("error", "Notifier");
        intent.putExtra("INDEX", "NOTIFICATIONS");
        intent.putExtra("ST_STATUS", status);
        intent.putExtra("ST_DESC", mess_1);
        PendingIntent pintent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
        Notification _mNotification = null;
        if (status)
            _mNotification = new Notification.Builder(getApplicationContext()).setContentTitle("Contexter")
                    .setContentText(mess_1).setSmallIcon(R.drawable.ic_launcher_mini).setContentIntent(pintent)
                    .addAction(R.drawable.button_custom, "See Detail", pintent)
                    .addAction(R.drawable.button_custom, "Cancel this", pintent)
                    .build();
        else
            _mNotification = new Notification.Builder(getApplicationContext()).setContentTitle("Contexter")
                    .setContentText(mess_1).setSmallIcon(R.drawable.ic_launcher_mini).setContentIntent(pintent)
                    .build();

        if (vibrate) {
            final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(30);
        }
        NotificationManager _notification_manager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        _mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        //id-- 0--Platform status
        _notification_manager.notify(0, _mNotification);
    }

    public void MessageNotifier(View view, String mess_1, String mess_2, String mess_3, boolean status, int id) {
        Intent intent = new Intent(getApplicationContext(),
                GroupContainerActivity.class);
        Intent intent_can = new Intent(getApplicationContext(),
                ChoicesActivity.class);
        intent.putExtra("INDEX", "NOTIFICATIONS");
        intent.putExtra("error", "Notifier");
        intent.putExtra("INDEX", "NOTIFICATIONS");
        intent.putExtra("ST_STATUS", status);

        PendingIntent pintent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
        PendingIntent pintent_cancel = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent_can, 0);
        Notification _mNotification = null;
        if (status)
            _mNotification = new Notification.Builder(getApplicationContext()).setContentTitle("Contexter")
                    .setContentText(mess_1).setSmallIcon(R.drawable.ic_launcher_mini).setContentIntent(pintent)
                    .addAction(R.drawable.button_custom, "See Detail", pintent)
                    .addAction(R.drawable.button_custom, "Cancel this", pintent_cancel)
                    .build();
        else
            _mNotification = new Notification.Builder(getApplicationContext()).setContentTitle("Contexter")
                    .setContentText(mess_1).setSmallIcon(R.drawable.ic_launcher_mini).setContentIntent(pintent)
                    .build();
        NotificationManager _notification_manager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        _mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //id-- 0--Platform status ~~~ 1
        _notification_manager.notify(id, _mNotification);
        //   _notification_manager.cancel(0);
    }

    public void MessageNotifier(View view, String mess_1, String mess_2, String mess_3, boolean status, String class_name, int id) {
        try {
            Class<?> targetActivity = Class.forName(class_name);
            Intent intent = new Intent(getApplicationContext(),
                    targetActivity.getClass());
            Intent intent_can = new Intent(getApplicationContext(),
                    ChoicesActivity.class);
            intent.putExtra("INDEX", "NOTIFICATIONS");
            intent.putExtra("error", "Notifier");
            intent.putExtra("INDEX", "NOTIFICATIONS");
            intent.putExtra("ST_STATUS", status);
            intent.putExtra("groupName", "Contexter");

            PendingIntent pintent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
            PendingIntent pintent_cancel = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent_can, 0);
            Notification _mNotification = null;
            if (status)
                _mNotification = new Notification.Builder(getApplicationContext()).setContentTitle("Contexter")
                        .setContentText(mess_1).setSmallIcon(R.drawable.ic_launcher_mini).setContentIntent(pintent)
                        .addAction(R.drawable.button_custom, "See Detail", pintent)
                        .addAction(R.drawable.button_custom, "Cancel this", pintent_cancel)
                        .build();
            else
                _mNotification = new Notification.Builder(getApplicationContext()).setContentTitle("Contexter")
                        .setContentText(mess_1).setSmallIcon(R.drawable.ic_launcher_mini).setContentIntent(pintent)
                        .build();
            NotificationManager _notification_manager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            _mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
            final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            //id-- 0--Platform status ~~~ 1
            _notification_manager.notify(id, _mNotification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isJPEGValid(FileInputStream fis) {
        boolean isValid = false;

        try {
            Bitmap bi = BitmapFactory.decodeStream(fis);
            bi = bi.copy(Bitmap.Config.ARGB_8888, true);
            isValid = true;
        } catch (Exception e) {
            isValid = false;
        }
        return isValid;
    }

    public int getUniqueSerialNumber(int init_index, int fin_index) {
        int unique_number = 0;
        long l = System.currentTimeMillis();
        long l_ = l / 1000;
        unique_number = Integer.parseInt(String.valueOf(l_).substring(init_index));
        return unique_number;
    }

    public void displayCustomizedToast(final Context _context_, String message, final boolean vibrate) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view_ = inflater.inflate(R.layout.dialog_one, null);
        TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);
        TextView tv_0_1_ = (TextView) view_.findViewById(R.id.tv_dialog_one_title);
        ImageView img_vi = (ImageView) view_.findViewById(R.id.imageView_grditem_1);
        tv_0_0_.setText(message);
        if (message.contains("~Wisper")) {
            tv_0_1_.setText(" Wisper ");
            tv_0_0_.setTextColor(_context_.getResources().getColor(R.color.chitchato_mebratu_blue));
        } else if (message.contains("MediaSense is running")) {
            img_vi.setImageResource(R.drawable.ic_bootstrapper_cyan_small);
            tv_0_1_.setText(" Bootstrap Ok ");
            tv_0_0_.setTextColor(_context_.getResources().getColor(R.color.white_overlay));
        }

        Handler _handler_ = new Handler(Looper.getMainLooper());
        if (!getTopActivity(_context_).contains("PrivateActivity"))
            _handler_.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = new Toast(_context_);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(view_);
                    toast.show();
                    if (vibrate) {
                        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        vibrator.vibrate(30);
                        MediaPlayer mp = MediaPlayer.create(_context_, R.raw.sms_alert_daniel_simon);
                        mp.start();
                    }
                }
            });
    }

    public void displayCustomizedToastWisp(final Context _context_, String value, final boolean vibrate) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view_ = inflater.inflate(R.layout.chatscreengriditem, null);
        view_.setBackground(_context_.getResources().getDrawable(R.drawable.rectangletv_5));
        CustomImageView cust_imageview = (CustomImageView) view_.findViewById(R.id.cust_imageView_grditem_1);
        cust_imageview.setTag("profile_image_android");
        TextView textview = (TextView) view_.findViewById(R.id.editText_grditem_3);

        LinearLayout llo = (LinearLayout) view_.findViewById(R.id.imageView_lo_);
        LinearLayout llo_ = (LinearLayout) view_
                .findViewById(R.id.linear_grditem_4);
        LinearLayout llo_s_item = (LinearLayout) view_
                .findViewById(R.id.imageView_llo_sub_item);
        LinearLayout llo_s_item_ = (LinearLayout) view_
                .findViewById(R.id.imageView_llo_);
        ScrollView llo_s_message_holder = (ScrollView) view_.findViewById(R.id.imageView_svo_);

        ImageView image_msg = (ImageView) view_
                .findViewById(R.id.image_tosend_chat_screen);
        TextView textview_chatter = (TextView) view_
                .findViewById(R.id.tv_chat_screen_context_chatter);
        TextView textview_time = (TextView) view_
                .findViewById(R.id.tv_chat_screen_context_chat_time);
        textview_chatter = (TextView) view_
                .findViewById(R.id.tv_chat_screen_context_chatter);
        textview.setTextColor(_context_.getResources().getColor(R.color.white_overlay));
        textview_chatter.setTextColor(_context_.getResources().getColor(R.color.white_overlay));
        textview_time.setTextColor(_context_.getResources().getColor(R.color.white_overlay));

        final String[] split = value.split("~::~");

        textview_time.setText(split[5]);
        textview.setText(split[4]);
        textview_chatter.setText(split[2]);

        Handler _handler_ = new Handler(Looper.getMainLooper());
        if (!getTopActivity(_context_).contains("PrivateActivity"))
            _handler_.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = new Toast(_context_);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(view_);
                    toast.show();
                    if (vibrate) {
                        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        vibrator.vibrate(50);
                        MediaPlayer mp = MediaPlayer.create(_context_, R.raw.sms_alert_daniel_simon);
                        mp.start();
                    }
                }
            });
    }

    public String getTopActivity(Context c) {
        String className = "";
        ActivityManager am = (ActivityManager) c.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task_info = am.getRunningTasks(1);
        ComponentName componentName = task_info.get(0).topActivity;
        className = componentName.getClassName();
        return className;
    }

    public class LocalBinder extends Binder {
        public PlatformManagerNode getService() {
            return PlatformManagerNode.this;
        }
    }

    class LocalWordServiceCCHandler extends Handler {
        private final WeakReference<PlatformManagerNode> _service;

        String _previous_message = "~::~", _curr_message = "~::~";

        public LocalWordServiceCCHandler(PlatformManagerNode service) {
            _service = new WeakReference<PlatformManagerNode>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            MESSAGE_DELIVARY_TIME_NEW = msg.getWhen();
            if (getPlatformUpdate != null)
                switch (msg.what) {
                    case NEW_MESSAGE_FLAG: {
                        try {
                            _mMessage = getPlatformUpdate.getReceived_message();
                            Messenger activityMessenger = msg.replyTo;
                            Bundle b = new Bundle();
                            Message replyMsg = Message.obtain(null, NEW_MESSAGE_FLAG);
                            replyMsg.setData(b);
                            String temp = msg.getData().getString("NEW_MESSAGE_FLAG");
                            String[] tempAr = temp.split("~::~");
                            setNew_message(temp);
                            new_message_to_send = temp;
                            activityMessenger.send(replyMsg);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    MESSAGE_DELIVARY_TIME_OLD = MESSAGE_DELIVARY_TIME_NEW;
                    break;
                    case NEW_MESSAGE_FLAG_FA: {
                        try {
                            _mMessage = getPlatformUpdate.getReceived_message();
                            Messenger activityMessenger = msg.replyTo;
                            Bundle b = new Bundle();
                            Message replyMsg = Message.obtain(null, NEW_MESSAGE_FLAG_FA);
                            replyMsg.setData(b);
                            activityMessenger.send(replyMsg);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    MESSAGE_DELIVARY_TIME_OLD = MESSAGE_DELIVARY_TIME_NEW;
                    break;

                    case NEW_IMAGE_MESSAGE_FLAG: {
                        try {
                            _mMessage = getPlatformUpdate.getReceived_message();
                            Bundle b = new Bundle();
                            Message replyMsg = Message.obtain(null, NEW_IMAGE_MESSAGE_FLAG);
                            replyMsg.setData(b);
                            String temp = msg.getData().getString("NEW_IMAGE_MESSAGE_FLAG");
                            new_image_message_to_send = temp;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    case NEW__SERVICE_STOP_MESSAGE_FLAG: {
                        String temp = msg.getData().getString(
                                "NEW__SERVICE_STOP_MESSAGE_FLAG");
                        if (temp == null) {
                            STOP_SERVICE = true;
                        } else if (temp.contains("STOP"))
                            STOP_SERVICE = true;
                        else
                            STOP_SERVICE = false;

                        MESSAGE_DELIVARY_TIME_OLD = MESSAGE_DELIVARY_TIME_NEW;
                        break;


                    }

                    case NEW_MESSAGE_FLAG_TEST:

                        final Messenger activityMessenger = msg.replyTo;
                        try {
                            Runnable _runnable_00 = new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        Bundle b = new Bundle();
                                        Message replyMsg = Message.obtain(null, NEW_MESSAGE_FLAG_TEST);
                                        b.putString("_mess", " " + getNew_message());
                                        replyMsg.setData(b);
                                        _curr_message = getNew_message();
                                        if (_curr_message.equalsIgnoreCase(_previous_message)) {
                                            Thread.sleep(1000);
                                            if (activityMessenger != null)
                                                activityMessenger.send(replyMsg);
                                        } else {
                                            Thread.sleep(0);
                                            _previous_message = _curr_message;
                                            if (activityMessenger != null)
                                                activityMessenger.send(replyMsg);
                                        }


                                    } catch (InterruptedException e) {
                                        getPlatformUpdate.platform.shutdown();
                                        e.printStackTrace();
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }

                                }
                            };
                            Thread _mthread_00 = new Thread(_runnable_00);
                            _mthread_00.start();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        MESSAGE_DELIVARY_TIME_OLD = MESSAGE_DELIVARY_TIME_NEW;
                        break;
                    default:

                        super.handleMessage(msg);
                        MESSAGE_DELIVARY_TIME_OLD = MESSAGE_DELIVARY_TIME_NEW;
                        break;
                }

        }
    }

    public class MyBinder extends Binder {
        public PlatformManagerNode getService() {
            return PlatformManagerNode.this;

        }
    }

    public class GetPlatformUpdate implements SensibleThingsListener, BinaryGetResponseListener {
        SensibleThingsPlatform platform;
        String received_message = "NO#";
        SharedPreferences mPrefs = getSharedPreferences("bootstraps", 0);
        TreeMap bs_map = new TreeMap<String, String>();
        Set<String> bootstraps = mPrefs.getStringSet("bootstraps", bs_map.keySet());
        String preferred_bs_ip = mPrefs.getString("PREFERRED_IP", "193.10.119.42");
        Iterator itr = bootstraps.iterator();

        // Intent -- Messaging Service
        Intent service_intent = new Intent(PlatformManagerNode.this, MainActivity.class);
        String _message_1 = "";
        ArrayList<String> temp_list_of_messages_rec = new ArrayList<String>();


        public GetPlatformUpdate(String bootstrap_ip) {
            if (bootstraps.size() == 0) {
                custom.setFLAG_DISP_NO_IP(true);
                mPrefs = getSharedPreferences("myprofile", 0);
                SharedPreferences.Editor ed = mPrefs.edit();
                ed.putBoolean("FLAG_DISP_NO_IP", true);
                ed.commit();
                bootstrap_ip = "193.10.119.42";
            } else {
                bootstrap_ip = preferred_bs_ip;
            }
            KelipsLookup.bootstrapIp = bootstrap_ip;
            KelipsLookup.BOOTSTRAP = false;
            if (isInternetAvailable() && !custom.IS_NODE_BOOTSTRAP_()) {
                //Platform Convergence
                BKGroundTask_Init background_init = new BKGroundTask_Init(this, this);
                background_init.execute();
                bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);

            } else if (!isInternetAvailable() && custom.IS_NODE_BOOTSTRAP_()) {
                MessageNotifier(null, "MediaSense needs connection", "", "", false, false);
            }
            if (platform != null)
                if (platform.isInitalized()) {
                    ///Save Platform states
                    custom.setST_UP(true);
                    custom.setPLATFORM_STARTUP_TRIAL(0);
                    ST_PLATFORM_IS_UP = true;
                    mPrefs = getSharedPreferences("myprofile", 0);
                    SharedPreferences.Editor ed = mPrefs.edit();
                    ed.putBoolean("IS_ST_UP", true);
                    ed.putInt("PLATFORM_STARTUP_TRIAL", 0);
                    ed.commit();

                    BKGroundTask_0 background_0 = new BKGroundTask_0();
                    background_0.execute("", "", "ST");
                }


        }

        public void getBackGroundRunning() {
            BKGroundTask_0 background_0 = new BKGroundTask_0();
            if (isInternetAvailable())
                background_0.execute();

        }

        @Override
        public void getResponse(String uci, byte[] value, SensibleThingsNode fromNode) {
            System.out.println("GetResponse Byte[]### ");
            try {
                if (_node_uci_map.get(fromNode.toString()) == null) {
                } else if (_node_uci_map.get(fromNode.toString()) == randomUci) {
                } else if (_node_uci_map.get(fromNode.toString()) == custom.getPeer_nick_name() + contexter_domain) {
                } else if (_node_uci_map.get(fromNode.toString()) != randomUci)
                    if (image_recieved_info_holder.get(_node_uci_map.get(fromNode
                            .toString())) == false) {
                        if (!temp_image_byte_holder.isEmpty()) {
                            if (temp_image_byte_holder.containsKey(_node_uci_map
                                    .get(fromNode.toString()))) {
                                if (value.length % 1024 == 0) {
                                    array_byte = temp_image_byte_holder
                                            .get(_node_uci_map.get(fromNode.toString()));
                                    if (array_byte.size() == 0) {

                                        array_byte.add(value);
                                        temp_image_byte_holder.remove(_node_uci_map
                                                .get(fromNode.toString()));
                                        temp_image_byte_holder.put(_node_uci_map
                                                        .get(fromNode.toString()),
                                                array_byte);

                                    } else {
                                        if (value.length
                                                - array_byte.get(array_byte.size() - 1).length == 1024) {
                                            array_byte.add(value);
                                            temp_image_byte_holder.remove(_node_uci_map
                                                    .get(fromNode.toString()));
                                            temp_image_byte_holder.put(_node_uci_map
                                                            .get(fromNode.toString()),
                                                    array_byte);
                                        } else {
                                            if (!disarray_byte_holder.isEmpty()) {
                                                if (disarray_byte_holder
                                                        .containsKey(_node_uci_map
                                                                .get(fromNode
                                                                        .toString()))) {
                                                    disarray_byte_holder_per = disarray_byte_holder
                                                            .get(_node_uci_map.get(fromNode
                                                                    .toString()));
                                                    disarray_byte_holder
                                                            .remove(_node_uci_map.get(fromNode
                                                                    .toString()));
                                                    disarray_byte_holder_per.put(
                                                            value.length / 1024, value);
                                                    disarray_byte_holder.put(
                                                            _node_uci_map.get(fromNode
                                                                    .toString()),
                                                            disarray_byte_holder_per);
                                                } else {
                                                    disarray_byte_holder_per = new TreeMap<Integer, byte[]>();
                                                    disarray_byte_holder_per.put(
                                                            value.length / 1024, value);
                                                    disarray_byte_holder.put(
                                                            _node_uci_map.get(fromNode
                                                                    .toString()),
                                                            disarray_byte_holder_per);
                                                }
                                            } else {
                                                disarray_byte_holder_per = new TreeMap<Integer, byte[]>();
                                                disarray_byte_holder_per.put(
                                                        value.length / 1024, value);
                                                disarray_byte_holder.put(_node_uci_map
                                                                .get(fromNode.toString()),
                                                        disarray_byte_holder_per);
                                            }

                                        }
                                    }
                                } else {
                                    disarray_byte_holder_per = disarray_byte_holder
                                            .get(_node_uci_map.get(fromNode.toString()));
                                    array_byte = temp_image_byte_holder
                                            .get(_node_uci_map.get(fromNode.toString()));
                                    for (Map.Entry<Integer, byte[]> entry : disarray_byte_holder_per
                                            .entrySet()) {
                                        if (array_byte.size() >= entry.getKey() - 1)
                                            array_byte.add(entry.getKey() - 1,
                                                    (byte[]) entry.getValue());
                                    }
                                    // Add the last value
                                    array_byte.add(value);
                                    String image_name = "%%%%%%%%";
                                    if (_node_uci_map.containsKey(fromNode.toString())) {
                                        image_name = _node_uci_map.get(fromNode
                                                .toString());
                                    }
                                    for (int indexx = 0; indexx < array_byte.size(); indexx++) {
                                        byte[] bb = array_byte.get(indexx);
                                        int offset = 0;
                                        if (indexx == 0 && bb != null) {
                                            if (image_name.equalsIgnoreCase(randomUci)) {

                                            }

                                            file_ = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/Media/" + image_name.replace(
                                                    "/", "_").replace("@", "%")
                                                    + ".jpg");

                                            if (file_.exists()) {
                                                fout_out.flush();
                                                fout_out.close();
                                                _node_uci_map.remove(fromNode
                                                        .toString());
                                                _node_uci_map.put(fromNode.toString(),
                                                        image_name + "%%");

                                                if (image_name.endsWith("%%%%%%%%")) {
                                                    image_recieved_info_holder
                                                            .remove(_node_uci_map.get(fromNode
                                                                    .toString()));
                                                    image_recieved_info_holder
                                                            .put(_node_uci_map.get(fromNode
                                                                            .toString()),
                                                                    true);


                                                    image_name = _node_uci_map.get(fromNode.toString());
                                                    // Four copies of images recieved --to choose the correctlly recieved image
                                                    File img_file_1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/Media/" + image_name.replace("/", "_").replace("@", "%") + ".jpg");
                                                    File img_file_2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/Media/" + image_name.replace("/", "_").replace("@", "%") + "%%" + ".jpg");
                                                    File img_file_3 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/Media/" + image_name.replace("/", "_").replace("@", "%") + "%%%%" + ".jpg");
                                                    File img_file_4 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/Media/" + image_name.replace("/", "_").replace("@", "%") + "%%%%%%" + ".jpg");
                                                    if (img_file_1.exists()) {

                                                        fis_val = new FileInputStream(img_file_1);

                                                        if (isJPEGValid(fis_val)) {
                                                            String tmp = file_.getName();
                                                            tmp = tmp.replace("%%", "@").replace("_", "/");
                                                            tmp = tmp.replace("%%%%", "@").replace("_", "/");
                                                            tmp = tmp.replace("%%%%%%", "@").replace("_", "/");

                                                            file_good = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + tmp.replace("/", "_").replace("@", "___"));
                                                            file_.renameTo(file_good);

                                                            ArrayList<byte[]> bt = getImage(file_.getAbsolutePath());
                                                            saveImage(bt, tmp, "group_name_str");

                                                        } else {
                                                            // Delete Corrupted Image
                                                            //file_.delete();


                                                        }
                                                    }//--
                                                    if (img_file_2.exists()) {
                                                        fis_val = new FileInputStream(img_file_2);

                                                        if (isJPEGValid(fis_val)) {
                                                            // keep the image

                                                            // Delete Corrupted Image
                                                            //file_.delete();


                                                        }
                                                    }//--
                                                    if (img_file_3.exists()) {

                                                        fis_val = new FileInputStream(img_file_3);

                                                        if (isJPEGValid(fis_val)) {
                                                            // keep the image

                                                        } else {
                                                            // Delete Corrupted Image
                                                            //file_.delete();


                                                        }
                                                    }//--
                                                    if (img_file_4.exists()) {
                                                        fis_val = new FileInputStream(img_file_4);

                                                        if (isJPEGValid(fis_val)) {
                                                            // keep the image

                                                        } else {
                                                            // Delete Corrupted Image
                                                            //file_.delete();


                                                        }
                                                    }//--

                                                    image_recieved_info_holder
                                                            .remove(_node_uci_map.get(fromNode
                                                                    .toString()));
                                                    image_recieved_info_holder
                                                            .put(_node_uci_map.get(fromNode
                                                                            .toString()),
                                                                    true);
                                                    System.gc();
                                                } else {
                                                    image_name = _node_uci_map
                                                            .get(fromNode.toString());
                                                    file_ = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/Media/" + image_name.replace(
                                                            "/", "__").replace("@", "___")
                                                            + ".jpg");
                                                    fout_out = new FileOutputStream(
                                                            file_);
                                                }

                                            } else {
                                                fout_out = new FileOutputStream(file_);
                                            }

                                            fout_out.write(bb);


                                        } else if (indexx < array_byte.size() - 1) {

                                            fout_out.write(bb, indexx * 1024, 1024);


                                        } else if (indexx == array_byte.size() - 1) {
                                            fout_out.write(bb, indexx * 1024,
                                                    (bb.length - (indexx) * 1024));

                                            temp_image_byte_holder.remove(_node_uci_map
                                                    .get(fromNode.toString()));
                                            disarray_byte_holder.remove(_node_uci_map
                                                    .get(fromNode.toString()));
                                            disarray_byte_holder_per.clear();
                                            try {
                                                fout_out.flush();
                                                fout_out.close();

                                                // Delete corrupted Images --
                                                if (file_.exists()) {
                                                    if (indexx == array_byte.size() - 1) {


                                                        if (isJPEGValid(fis_val)) {
                                                            // keep the image

                                                        } else {
                                                            // Delete Corrupted Image
                                                            //file_.delete();

                                                        }
                                                    }
                                                }//--


                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }

                                        } else {

                                        }
                                        if (indexx == array_byte.size() - 1) {

                                        }
                                    }
                                }
                            } else if (!temp_image_byte_holder.containsKey(uci)) {
                                array_byte = new ArrayList<byte[]>();
                                array_byte.add(value);
                                temp_image_byte_holder.put(
                                        _node_uci_map.get(fromNode.toString()),
                                        array_byte);
                            }
                        } else {
                            array_byte = new ArrayList<byte[]>();
                            array_byte.add(value);
                            temp_image_byte_holder.put(
                                    _node_uci_map.get(fromNode.toString()), array_byte);

                        }
                    }
            } catch (IOException io) {
                io.printStackTrace();
            } catch (NullPointerException npe) {
            } finally {

            }

        }

        public void getResponse(String uci, String value,
                                SensibleThingsNode node) {

            System.out.println("GetResponse ### ");
            try {
                MDecoder md = new MDecoder("US-ASCII");
                boolean isUTF8 = md.check(value, "UTF-8");

                if (isInternetAvailable()) {
                    // If the Characters are UTF-8 then
                    if (isUTF8 && !uci.equalsIgnoreCase(custom.getPeer_nick_name() + contexter_domain) && !temp_list_of_messages_rec.contains(uci + value)) {
                        System.out.println("GetResponse ### " + value);
                        if (temp_list_of_messages_rec.contains(uci + value) == false) {
                            temp_list_of_messages_rec.add(uci + value);
                            if (temp_list_of_messages_rec.size() > 100)
                                temp_list_of_messages_rec.clear();
                        }
                        //Add node -uci entry.
                        if (!_node_uci_map.containsKey(node.toString())) {
                            _node_uci_map.put(node.toString(), uci);
                            image_recieved_info_holder.put(uci, false);
                        }

                        // Manage each of the arriving messages
                        if (value.startsWith(String.valueOf(Constants.PUBLIC))) {
                            _message_1 = "[GetResponse] " + uci + ":" + value;
                            setNew_message("[Get Response]" + uci + ":" + value);
                            this.setReceived_message(_message_1);


                            Bundle b = new Bundle();
                            _replyMsg_flag_test = Message.obtain(null, NEW_MESSAGE_FLAG_TEST);
                            b.putString("_mess", " " + getNew_message());

                            try {
                                mMessageHandler.send(_replyMsg_flag_test);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }


                        } else if (value.startsWith(String.valueOf(Constants.DEBUG))) {
                            _message_1 = "[GetResponse] " + uci + ":" + value;
                            setNew_message("[Get Response]" + uci + ":" + value);
                            this.setReceived_message(_message_1);


                            Bundle b = new Bundle();
                            _replyMsg_flag_test = Message.obtain(null, NEW_MESSAGE_FLAG_TEST);
                            b.putString("_mess", " " + getNew_message());
                            _replyMsg_flag_test.setData(b);
                            try {
                                mMessageHandler.send(_replyMsg_flag_test);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        } else if (value.startsWith(String.valueOf(Constants.PRIVATE))) {
                            _message_1 = "[GetResponse] " + uci + ":" + value;
                            setNew_message("[Get Response]" + uci + ":" + value);
                            MessageNotifier(null, "Wisper " + value.split("~::~")[2], "", "", false, "PrivateActivity", getUniqueSerialNumber(3, 9));
                            displayCustomizedToastWisp(getBaseContext(), value, true);
                        } else if (value.startsWith(String.valueOf(Constants.REGISTER))) {
                            custom.set_NEW_INFO_AVAILABLE_(true);
                            custom.set_NOTIFICATIONS_AVAILABLE(true);
                            MessageNotifier(null, "Group Join Request from  " + value.split("~::~")[2], "", "", false, getUniqueSerialNumber(3, 9));
                            _peers_0_requesting_list.addPeer(value, 1);
                            _peers_0_requesting_list.savePeers(MainActivity.SELECTED_GROUP, "rqsting", _peers_0_requesting_list.getmPeers());
                        } else if (value.startsWith(String.valueOf(Constants.REGISTERED))) {
                            MessageNotifier(null, value.split("~::~")[1] + " Group Join Request Accepted ", "", "", false, getUniqueSerialNumber(3, 9));
                            displayCustomizedToast(getBaseContext(), value.split("~::~")[1] + ": Group Join Request Accepted  ", true);
                            Groups _groups_1 = new Groups(getBaseContext(), "params");
                            Long group_clock = Long.valueOf(value.split("~::~")[4]) - System.currentTimeMillis();
                            _groups_1.addGroupParam(value.split("~::~")[1] + "~::~" + value.split("~::~")[2] + "~::~" + value.split("~::~")[3] + "~::~" + group_clock);

                        } else if (value.startsWith(String.valueOf(Constants.SEARCH))) {
                            String search_key_str = value.split("~::~")[3];
                            ArrayList<String> al = _groups_0.getmGroups();
                            for (String str : al) {
                                // Send back search result
                                if (str.contains(":::")) str = str.split(":::")[1];
                                // new_message_to_send = Constants.SEARCHED + "~::~" + _mGroup + "~::~" + _mUCI + "~::~" + str + "~::~" + format.format(new Date());

                                try {
                                    platform.notify(node, uci, Constants.SEARCHED + "~::~" + _mGroup + "~::~" + _mUCI + "~::~" + str + "~::~" + format.format(new Date()));
                                    Thread.sleep(250);
                                    platform.notify(node, uci, Constants.SEARCHED + "~::~" + _mGroup + "~::~" + _mUCI + "~::~" + str + "~::~" + format.format(new Date()));
                                    Thread.sleep(250);
                                    platform.notify(node, uci, Constants.SEARCHED + "~::~" + _mGroup + "~::~" + _mUCI + "~::~" + str + "~::~" + format.format(new Date()));
                                } catch (InterruptedException e) {

                                    platform.shutdown();
                                    e.printStackTrace();
                                }
                                platform.notify(node, uci, Constants.SEARCHED + "~::~" + _mGroup + "~::~" + _mUCI + "~::~" + str + "~::~" + format.format(new Date()));
                            }
                            platform.notify(node, uci, Constants.SEARCHED + "~::~" + _mGroup + "~::~" + _mUCI + "~::~" + "~Test~" + "~::~" + format.format(new Date()));


                        } else if (value.startsWith(String.valueOf(Constants.SEARCHED))) {
                            setNew_message("[Get Response]" + uci + ":" + value);
                            this.setReceived_message(_message_1);

                        } else if (value.startsWith(String.valueOf(Constants.PUBLICIMAGEFILE))) {


                            _message_1 = "[GetResponse] " + uci + ":" + value;
                            setNew_message("[Get Response]" + uci + ":" + value);
                            this.setReceived_message(_message_1);


                            //Send Image  ---
                            if (IS_IMAGE_MESSAGE_READY) {
                                FileInputStream fin;
                                try {
//                                    String file_url = custom.get_image_message_uri();
                                    String file_url = value.split("~::~")[3];
                                    fin = new FileInputStream(file_url);
                                    // getImageToSend(fin, platform, node, uci, file_url, Constants.PUBLICIMAGEFILE);
                                } catch (FileNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        IS_IMAGE_MESSAGE_READY = false;
                                        //fout.flush();
                                        //fout.close();
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }

                            }


                        } else if (value.startsWith(String.valueOf(Constants.IMAGEMETADATA))) {
                            if (!_image_meta_data_temp.containsValue(value))
                                _image_meta_data_temp.put(uci, value);

                        } else if (value.startsWith(String.valueOf(Constants.NODES))) {
                            _peers_0.addPeer(value, 0);

                        } else if (value.startsWith(String.valueOf(Constants.PROFILEIMAGEFILE))) {
                            //Send profile image ---
                            // Send Profile Image whever Changed
                            FileInputStream fin;
                            try {
                                String file_url = custom.getProfile_image_uri();
                                fin = new FileInputStream(file_url);
                                getImageToSend(fin, platform, node, uci, file_url, Constants.PROFILEIMAGEFILED);
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } finally {
                                try {

                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                        } else if (value.startsWith(String.valueOf(Constants.PROFILEIMAGEFILED))) {
                            if (!_image_meta_data_temp.containsValue(value))
                                _image_meta_data_temp.put(uci, value);

                        } else if (value.startsWith(String.valueOf(Constants.NODES))) {
                            _peers_0.addPeer(value, 0);

                        } else if (value.startsWith(String.valueOf(Constants.BOOTSTRAPS))) {

                        } else if (value.startsWith(String.valueOf(Constants.PASSWORD))) {

                        } else if (value.startsWith(String.valueOf(Constants.GROUPS))) {

                        } else if (value.startsWith(String.valueOf(Constants.ADS))) {
                            System.out.println("Advertisement  " + value);
                            if (custom.getCONTEXTER_ADVS().contains(value))
                                custom.setCONTEXTER_ADVS(value);
                            PublicChats _thread_0 = new PublicChats(getBaseContext(), MainActivity.SELECTED_GROUP, "advs");
                            long current_time_mills = System.currentTimeMillis();
                            long modified_time_mills = _thread_0.getFileModifiedTimeMills();
                            if ((current_time_mills - modified_time_mills) / 3600000 > 1) {
                                _thread_0.clearPublicChats();
                                _thread_0.saveADVs("*ALL#", "advs", al_advertisements);
                            }


                            al_advertisements = _thread_0.getmPublicChat("");
                            String adv_tmp = value.split("~::~")[2] + ":" + value.split("~::~")[3];
                            if (!al_advertisements.contains(adv_tmp)) {
                                al_advertisements.add(adv_tmp);
                                _thread_0.saveADVs("*ALL#", "advs", al_advertisements);
                            }
                        }

                    } else {

                    }
                } else {
                    platform.shutdown();

                }


            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
            }


        }

        public void resolveResponse(String uci, SensibleThingsNode node) {
            // TODO Auto-generated method stub
            int count_0 = 1;
            System.out.println("ResolveResponse ### " + uci + ": " + node);
            if (isInternetAvailable()) {
                String _message_2 = "[ResolveResponse] " + uci + ": " + node;
                this.setReceived_message(_message_2);
                try {
                    msg.replyTo = mLocalWordServiceCCMessenger;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Bundle bundle_ = new Bundle();
                bundle_.putString("m_2", " _message_2");
                platform.get(uci, node);
            } else {
                platform.shutdown();
            }
        }

        public void getEvent(final SensibleThingsNode source, final String uci) {
            Runnable runnable_000 = new Runnable() {
                @Override
                public void run() {
                    System.out.println("GetEvent ### ");

                    platform.get(uci, source);
                    try {
                        if (temp_ipc_queque.get(uci) == null) {
                            System.out.println("GetEvent ### " + new_message_to_send);
                            platform.notify(source, randomMobi2, new_message_to_send);
                            temp_ipc_queque.put(uci, new_message_to_send);
                            if (custom.is_IMAGE_SEND_()) {
                                // Send  Image
                                FileInputStream fin;
                                try {
                                    String file_url = custom.get_image_message_uri();
                                    fin = new FileInputStream(file_url);
                                    // getImageToSend(fin, platform, source, uci, file_url, Constants.PUBLICIMAGEFILE);

                                } catch (FileNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } finally {
                                    try {

                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }


                                mPrefs = getSharedPreferences("myprofile", 0);
                                SharedPreferences.Editor ed = mPrefs.edit();
                                ed.putBoolean("_IMAGE_SEND_", false);
                                custom.set_IMAGE_SEND_(false);


                            }


                        } else {
                            if (!temp_ipc_queque.get(uci).equalsIgnoreCase(new_message_to_send)) {
                                //Image Message
                                if (custom.is_IMAGE_SEND_()) {
                                    // Send Profile Image whever Changed
                                    FileInputStream fin;
                                    try {
                                        String file_url = custom.get_image_message_uri();
                                        fin = new FileInputStream(file_url);
                                        // getImageToSend(fin, platform, source, uci, file_url, Constants.PUBLICIMAGEFILE);

                                    } catch (FileNotFoundException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } finally {
                                        try {

                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }


                                    mPrefs = getSharedPreferences("myprofile", 0);
                                    SharedPreferences.Editor ed = mPrefs.edit();
                                    ed.putBoolean("_IMAGE_SEND_", false);
                                    custom.set_IMAGE_SEND_(false);


                                } else {
                                    platform.notify(source, randomMobi2, new_message_to_send);
                                    if (custom.isPROFILE_IMAGE_CHANGED()) {

                                        // Notify Bootstraps and Nodes every 10 seconds
                                        // Send Profile Image whever Changed
                                        FileInputStream fin;
                                        String file_url = custom.getProfile_image_uri();
                                        fin = new FileInputStream(file_url);
                                        //getImageToSend(fin, platform, source, uci, file_url, Constants.PROFILEIMAGEFILE);
                                        mPrefs = getSharedPreferences("myprofile", 0);
                                        SharedPreferences.Editor ed = mPrefs.edit();
                                        ed.putBoolean("PROFILE_CHANGED", false);
                                        custom.setPROFILE_IMAGE_CHANGED(false);
                                    }
                                }
                                temp_ipc_queque.put(uci, new_message_to_send);
                            } else {
                            }
                        }


                        if (LAST_UPDATE_TIME == 0) {

                            LAST_UPDATE_TIME = System.currentTimeMillis() / 1000;

                        } else {
                            Long ln_ = (System.currentTimeMillis() / 1000);
                            if (ln_ - LAST_UPDATE_TIME > 30) {
                                platform.notify(source, uci, Constants.NODES + NODES);
                                platform.notify(source, uci, Constants.BOOTSTRAPS + BOOTSTRAPS);
                                LAST_UPDATE_TIME = ln_;
                                ///Test////
                                if (COUNTER_0_000 < 10) {
                                    FileInputStream fin;

                                    //String file_url = EditProfileActivity.getAppFolder("") + "Media/" + custom.getPeer_nick_name() + "__miun.se_mobi" + ".jpg";
                                    String file_url = EditProfileActivity.getAppFolder("") + custom.getPeer_nick_name() + "___miun.se__mobi" + ".jpg";
                                    fin = new FileInputStream(file_url);
                                    System.out.println("____path_________________" + file_url);
                                    // Run this only if SDK>19
                                    // if(Build.VERSION.SDK_INT>=19)
                                    getImageToSend(fin, platform, source, uci, file_url, Constants.PROFILEIMAGEFILE);
                                    COUNTER_0_000++;
                                }

                            } else {

                            }
                        }
                        Thread.sleep(1000, 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }
            };
            Thread _mthread = new Thread(runnable_000);
            _mthread.start();
            try {
                Thread.sleep(2000, 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        public void setEvent(SensibleThingsNode source, String uci, String value) {
            System.out.println("SetEvent ### ");
        }

        public String getReceived_message() {
            return received_message;
        }

        public void setReceived_message(String received_message) {
            this.received_message = received_message;
        }

        public void saveImage(ArrayList<byte[]> array_byte, String image_name, String group_name) {
            // Application Directory
            String path = getAppFolder("");
            String destName = path + image_name;
            String destName_dp = path + "Media/" + image_name;
            File file = new File(destName);
            File file_dp = new File(destName_dp);
            if (!file.exists()) {
                setAppFolder("");
            }
            if (!file_dp.exists()) {
                setAppFolderFiles("Media");
            }

            destName = destName + ".jpg";
            destName_dp = destName_dp + ".jpg";
            file = new File(destName);
            file_dp = new File(destName_dp);
            file.setWritable(true);
            file_dp.setWritable(true);
            FileOutputStream fout = null, fout_dp = null;

            byte[] image_file;
            byte[] readData = new byte[1024];
            try {
                fout = new FileOutputStream(file);
                fout_dp = new FileOutputStream(file_dp);
                for (int indexx = 0; indexx < array_byte.size(); indexx++) {
                    byte[] bb = array_byte.get(indexx);
                    if (indexx == 0) {
                        fout.write(bb);
                        fout_dp.write(bb);
                    } else if (indexx < array_byte.size() - 1) {

                        fout.write(bb, indexx * 1024, 1024);
                        fout_dp.write(bb, indexx * 1024, 1024);

                    } else if (indexx == array_byte.size() - 1) {
                        fout.write(bb, indexx * 1024, (bb.length - (indexx) * 1024));
                        fout_dp.write(bb, indexx * 1024, (bb.length - (indexx) * 1024));

                    } else {
                    }

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {

                    fout.flush();
                    fout_dp.flush();
                    fout.close();
                    fout_dp.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }

        public ArrayList<byte[]> getImage(String image_name) {
            ArrayList<byte[]> Image_file = new ArrayList<byte[]>();
            String image_url = image_name;
            String extn = "", str = "";
            byte[] readData = null;
            DataOutputStream dout;
            int chunk_num = -1;
            byte _image_[];
            long file_size_byte = -1;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int flag = 0, i;
            for (i = 0; i < image_url.length(); i++) {
                if (image_url.charAt(i) == '.' || flag == 1) {
                    flag = 1;
                    extn += image_url.charAt(i);
                }
            }

            if (extn.equalsIgnoreCase(".jpg") || extn.equals(".png") || extn.equals(".gif") || extn.equals(".tif") || extn.equals(".JPEG")) {
                try {
                    File file = new File(image_url);
                    FileInputStream fin = new FileInputStream(file);
                    file_size_byte = file.length();
                    chunk_num = (int) file_size_byte / 1024;
                    if (file_size_byte < 1048576) {
                        for (int index = 1; index <= chunk_num + 1; index++) {
                            if (index == chunk_num + 1) {
                                _image_ = new byte[(int) (file_size_byte)];
                                fin.read(_image_, chunk_num * 1024,
                                        (int) (file_size_byte - chunk_num * 1024));
                            } else {
                                _image_ = new byte[index * 1024];
                                if (index == 1)
                                    fin.read(_image_, 0, 1024);
                                else
                                    fin.read(_image_, (index - 1) * 1024, 1024);


                            }
                            Image_file.add(_image_);
                        }

                        fin.close();
                    } else {
                        Toast.makeText(getApplicationContext(), "Image size needs to be 1MB max", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Unsupported Image Format ", Toast.LENGTH_LONG).show();
            }
            return Image_file;
        }

        //Get Path for Home Folder
        public String getAppFolder(String file_name) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/";
        }

        // / Set Path for Home Folder
        public void setAppFolder(String file_name) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato");
            if (!file.exists()) {
                new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato").mkdir();
            }
        }

        public void setAppFolderFiles(String file_name) {
            String path = getAppFolder("");
            File file = new File(path + file_name + "/");
            if (!file.exists()) {
                new File(path + file_name + "/").mkdir();
            }
        }

        public class BKGroundTask_0 extends AsyncTask<String, String, String> {
            boolean flag = false;
            int counter_0_0 = 14;

            BKGroundTask_0() {
            }

            @Override
            protected String doInBackground(String... arg0) {
                this.flag = false;

                if (platform != null)
                    while (platform.isInitalized()) {
                        try {
                            if (isInternetAvailable()) {

                                // check if the user has made nickname change while  connected
                                randomMobi2 = custom.getPeer_nick_name() + contexter_domain;
                                _mUCI = randomMobi2;
                                System.out.println("Platform Running ### ");

                                platform.resolve(randomUci);
                                _peers_1 = _peers_0.getmPeers();
                                NODES = "~::~";
                                BOOTSTRAPS = "~::~";
                                for (String uci_temp : _peers_1) {
                                    if (!randomMobi2.equalsIgnoreCase(uci_temp)) {
                                        platform.resolve(uci_temp);
                                        NODES = NODES + uci_temp + "~::~";
                                    }
                                }

                                platform.register(randomMobi2);
                                Thread.sleep(2000);
                                counter_0_0 = 1;

                                custom.setST_UP(true);
                                ST_PLATFORM_IS_UP = true;
                            } else {
                                custom.setST_UP(false);
                                ST_PLATFORM_IS_UP = false;
                            }
                            if (STOP_SERVICE) {
                                ST_PLATFORM_IS_UP = false;
                                platform.shutdown();
                                //break;
                            }
                            Thread.sleep(2000);
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
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

        public class BKGroundTask_Init extends AsyncTask<String, String, String> {
            boolean flag = false;
            SensibleThingsListener lstnr = null;
            BinaryGetResponseListener b_lstnr = null;

            BKGroundTask_Init(SensibleThingsListener listner, BinaryGetResponseListener b_listner) {
                lstnr = listner;
                b_lstnr = b_listner;

            }

            @Override
            protected String doInBackground(String... arg0) {
                this.flag = false;
                try {

                    platform = new SensibleThingsPlatform(lstnr);
                    platform.getDisseminationCore().setBinaryGetResponseListener(b_lstnr);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    if (platform.isInitalized()) {

                        custom.setST_UP(true);
                        custom.setPLATFORM_STARTUP_TRIAL(0);

                        ST_PLATFORM_IS_UP = true;
                        mPrefs = getSharedPreferences("myprofile", 0);
                        SharedPreferences.Editor ed = mPrefs.edit();
                        ed.putBoolean("IS_SERVICE_UP", true);
                        ed.putBoolean("IS_ST_UP", true);
                        ed.putInt("PLATFORM_STARTUP_TRIAL", 0);
                        ed.commit();
                        //
                        BOOTSTRAP_IP = KelipsLookup.bootstrapIp;
                        BOOTSTRAP_NAME = randomUci;
                        STARTUP_TIME = format.format(new Date());

                        // Notification -- if there are any new information on Contexter
                        if (PlatformManagerNode.ST_PLATFORM_IS_UP) {
                            if (!custom.isNoNotification()) {
                                MessageNotifier(null, "MediaSense is running", "", "", true, true);
                                mp_media_sense_running.start();
                            }

                            //displayCustomizedToast(getBaseContext(), "MediaSense is running", true);
                        } else
                            MessageNotifier(null, "MediaSense is running yet something is wrong. \nMediaSense needs to be restarted", "", "", true, false);

                    } else {
                        SharedPreferences mPrefs = getSharedPreferences("bootstraps", 0);
                        TreeMap bs_map = new TreeMap<String, String>();
                        Set<String> bootstraps = mPrefs.getStringSet("bootstraps", bs_map.keySet());

                        // Adverts
                        if (ST_PLATFORM_IS_UP)
                            MessageNotifier(null, custom.getCONTEXTER_ADVS(), "", "", true, 3);
                        if (bootstraps != null) {
                            if (bootstraps.size() == 0) {
                                if (!custom.isNoNotification()) {
                                    mp_media_sense_stopped.start();
                                    MessageNotifier(null, "No valid or working BootStrap IP \n", "", "", false, false);
                                }

                                custom.setFLAG_DISP_NO_IP(true);
                                mPrefs = getSharedPreferences("myprofile", 0);
                                SharedPreferences.Editor ed = mPrefs.edit();
                                ed.putBoolean("FLAG_DISP_NO_IP", true);
                                ed.commit();
                            } else if (custom.getPLATFORM_STARTUP_TRIAL() > 120 && !isInternetAvailable()) {
                                MessageNotifier(null, "MediaSense needs connection", "", "", false, false);
                            } else {
                                if (!custom.isNoNotification()) {
                                    mp_media_sense_stopped.start();
                                    MessageNotifier(null, "MediaSense is not running", "", "", false, false);
                                }

                                //if (!custom.getCONTEXTER_ADVS().contains("none"))
                                custom.setFLAG_DISP_NO_IP(false);
                                mPrefs = getSharedPreferences("myprofile", 0);
                                SharedPreferences.Editor ed = mPrefs.edit();
                                ed.putBoolean("FLAG_DISP_NO_IP", true);
                                ed.commit();
                                platform.shutdown();
                            }
                        } else {
                            if (!custom.isNoNotification()) {
                                mp_media_sense_stopped.start();
                                displayCustomizedToast(getBaseContext(), "No valid or working BootStrap IP \n" + "      Add IP     ", false);
                            }
                        }
                    }
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

}

