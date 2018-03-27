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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import se.sensiblethings.app.R;
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
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.rudp.RUDPCommunication;
import se.sensiblethings.disseminationlayer.disseminationcore.BinaryGetResponseListener;
import se.sensiblethings.disseminationlayer.disseminationcore.DisseminationCore;
import se.sensiblethings.disseminationlayer.disseminationcore.DisseminationCoreStateListener;
import se.sensiblethings.disseminationlayer.lookupservice.LookupService;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.KelipsLookup;
import se.sensiblethings.interfacelayer.SensibleThingsListener;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public class PlatformManagerBootstrap extends Service {
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
    public static String STARTUP_TIME = "- - -";
    public static String contexter_domain = "@miun.se/mobi";  // Contexter domain for test
    public static ArrayList<String> _peers_1;
    public static String _mMessage = "NO#";
    public static String _mGroup = "*ALL#@*ALL#"; // Default Group --- used for test and debingging
    public static GetPlatformUpdate getPlatformUpdate;
    public static boolean STOP_SERVICE = false;
    static public ArrayList<String> PEERS_IN_SELECTED_GROUP = new ArrayList<String>();
    static Random random = new Random();
    public static String BOOTSTRAP_NAME = "bootstrap" + random.nextInt(1000) + "_" + "@miun.se/random";
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
    ArrayList<String> al_advertisements = new ArrayList<String>();
    //Tests
    String randomUci = "bootstrap@miun.se/random";
    int counter_000 = 0;// Just for test
    String randomMobi2 = "@miun.se/mobi";
    // static FileOutputStream fout; // Image writer
    FileOutputStream fout_out; // Image Writer
    FileInputStream fis_val;
    // NODE-UCI Mapper
    TreeMap<String, String> _node_uci_map = new TreeMap<String, String>();
    int counter = 1, count = 1;
    // Image byte holders--- used for image file
    ArrayList<byte[]> array_byte = new ArrayList<byte[]>();
    TreeMap<Integer, byte[]> disarray_byte_holder_per = new TreeMap<Integer, byte[]>();
    TreeMap<String, ArrayList> temp_image_byte_holder = new TreeMap<String, ArrayList>();
    TreeMap<String, TreeMap> disarray_byte_holder = new TreeMap<String, TreeMap>();
    TreeMap<String, String> _image_meta_data_temp = new TreeMap<String, String>();
    File file_ = null;
    // Service Messagenger---
    android.os.Messenger mLocalWordServiceCCMessenger;
    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mLocalWordServiceCCMessenger = new Messenger(service);
            android.os.Message replyMsg = android.os.Message
                    .obtain(null,
                            PlatformManagerBootstrap.NEW_MESSAGE_FLAG,
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
            PlatformManagerBootstrap.NEW_MESSAGE_FLAG, 0, 0);
    android.os.Message _replyMsg_flag_test = android.os.Message.obtain(null,
            PlatformManagerBootstrap.NEW_MESSAGE_FLAG_TEST, 0, 0);
    Messenger mMessageHandler = new Messenger(
            new LocalWordServiceCCHandler(this));
    Messenger defualtActivityMessenger = null;
    /// To Handle Repeated Notifcations
    TreeMap<String, String> temp_ipc_queque = new TreeMap<String, String>();// stores unique messages
    String date_pattern = "yyyy-MM-dd HH:mm:ss"; // Time format for Contexter
    Format format = new SimpleDateFormat(date_pattern);//
    // previous  update time holder [in millis] -- for bootstraps and nodes
    long LAST_UPDATE_TIME = 0;
    long LAST_UPDATE_TIME_ = 0;
    int COUNTER_0_000 = 0; // For test
    String BOOTSTRAPS = "~::~";// Bootstraps -- String
    String NODES = "~::~"; // Nodes --String
    Peers peers;
    Messenger _messenger = new Messenger(new LocalWordServiceCCHandler(
            this));
    private ArrayList<String> list = new ArrayList<String>();
    // UCI-- to be forwareded to the connecting nodes
    private ArrayList<String> _mListofUCI = new ArrayList<String>();
    //test---
    private Chronometer mChronometer;
    private Long MESSAGE_DELIVARY_TIME_OLD = 0L;
    private Long MESSAGE_DELIVARY_TIME_NEW = 0L;

    public static String getNew_message() {
        return new_message;
    }

    public static void setNew_message(String nm) {
        new_message = nm;
    }

    public static Long getMESSAGE_UPDATE_PREV() {
        return MESSAGE_UPDATE_PREV;
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
            //Start the Platform
            getPlatformUpdate = new GetPlatformUpdate("192.168.0.100");
            getPlatformUpdate.getBackGroundRunning();


            if (!custom.isFLAG_DISP_NO_IP()) {
                displayCustomizedToast(getApplicationContext(), "                   Welcome! \n" +
                        "     MediaSense is starting up.    ", false);
            }
            ///Save service states
            custom.setSERVICE_UP(true);
            //custom.setPLATFORM_STARTUP_TRIAL(0);
            mPrefs = getSharedPreferences("myprofile", 0);
            SharedPreferences.Editor ed = mPrefs.edit();
            ed.putBoolean("IS_SERVICE_UP", true);
            //ed.putInt("PLATFORM_STARTUP_TRIAL", 0);
            ed.commit();

            app_context = this.getApplicationContext();
            return Service.START_NOT_STICKY;

        } catch (Exception e) {
            if (custom.isHotSpotON() || isInternetAvailable())
                if (custom.isFLAG_DISP_NO_IP()) {
                    displayCustomizedToast(getApplicationContext(), "Something went wrong with Hotspot or Bootstrap \n", false);
                }

            return Service.START_REDELIVER_INTENT;
        }

    }

    public Context getApp_context() {
        return app_context;
    }

    public void setApp_context(Context app_context) {
        this.app_context = app_context;
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
        // return mBinder;
    }

    public List<String> getWordList() {
        return list;
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
        FileOutputStream fout_;
        int counter_tmp = 1;
        long file_size_byte = -1;

        //static int chunk_size = 1024;
        int chunk_num = -1;
        File file_to_send = new File(file_url);


       /* for (i = 0; i < file_url.length(); i++) {
            if (file_url.charAt(i) == '.' || flag == 1) {
                flag = 1;
                extn += file_url.charAt(i);
            }
        }*/
        extn = ".jpg";
        if (extn.equalsIgnoreCase(".jpg") || extn.equals(".png") || extn.equals(".gif") || extn.equals(".tif") || extn.equals(".JPEG")) {
            try {
                String str = "";
                file_size_byte = file_to_send.length();
                chunk_num = (int) file_size_byte / 1024;

                // System.out.println("Chunk num:" + chunk_num + "  " + (Long) (file_size_byte / 1024));
                for (int index = 1; index <= chunk_num + 1; index++) {
                    if (index == chunk_num + 1) {
                        _image_ = new byte[(int) (file_size_byte)];
                        fin.read(_image_, chunk_num * 1024,
                                (int) (file_size_byte - chunk_num * 1024));


                        //final chunck
                        platform.getDisseminationCore().notify(source, uci,
                                _image_);
                        // send image metadata
                        String _meta_data = Constants.IMAGEMETADATA + "~::~" + cat + "~::~" + MainActivity.SELECTED_GROUP + "~::~" + custom.getPeer_nick_name() + PlatformManagerBootstrap.contexter_domain + "~::~" + extn + "~::~" + Uri.parse(file_url).getLastPathSegment() + "~::~" + file_size_byte + "~::~" + "123456";
                        platform.getDisseminationCore().notify(source, uci,
                                _meta_data);

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
                //System.out.println("Image ::" + ex);
            } finally {

            }
            // File
        } else {

        }
    }

    public void MessageNotifier(View view, String mess_1, String mess_2, String mess_3, boolean status) {
        Intent intent = new Intent(getApplicationContext(),
                ErrorActivity.class);
        intent.putExtra("error", "Notifier");
        intent.putExtra("INDEX", "NOTIFICATIONS");
        intent.putExtra("ST_STATUS", status);
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


        NotificationManager _notification_manager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        _mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        //id-- 0--Platform status
        _notification_manager.notify(0, _mNotification);
        //   _notification_manager.cancel(0);

    }

    public void MessageNotifier(View view, String mess_1, String mess_2, String mess_3, boolean status, int id) {
        Intent intent = new Intent(getApplicationContext(),
                GroupContainerActivity.class);
        intent.putExtra("INDEX", "NOTIFICATIONS");
        intent.putExtra("error", "Notifier");
        intent.putExtra("INDEX", "NOTIFICATIONS");
        intent.putExtra("ST_STATUS", status);

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
        NotificationManager _notification_manager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        _mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //vibrator.vibrate(50);
        //id-- 0--Platform status ~~~ 1
        _notification_manager.notify(id, _mNotification);
        //   _notification_manager.cancel(0);
    }

    public void MessageNotifier(View view, String mess_1, String mess_2, String mess_3, boolean status, String class_name, int id) {
        try {
            Class<?> targetActivity = Class.forName(class_name);
            Intent intent = new Intent(getApplicationContext(),
                    targetActivity.getClass());
            intent.putExtra("INDEX", "NOTIFICATIONS");
            intent.putExtra("error", "Notifier");
            intent.putExtra("INDEX", "NOTIFICATIONS");
            intent.putExtra("ST_STATUS", status);
            intent.putExtra("groupName", "Contexter");

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
            NotificationManager _notification_manager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            _mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
            final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            //vibrator.vibrate(50);
            //id-- 0--Platform status ~~~ 1
            _notification_manager.notify(id, _mNotification);
            //   _notification_manager.cancel(0);
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
        tv_0_0_.setText(message);
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
                    }
                }
            });
    }

    public void displayCustomizedToastWisp(final Context _context_, String value, final boolean vibrate) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view_ = inflater.inflate(R.layout.chatscreengriditem_green, null);
        TextView textview_time = (TextView) view_.findViewById(R.id.editText_grditem_3);
        CustomImageView cust_imageview = (CustomImageView) view_.findViewById(R.id.cust_imageView_grditem_1);
        cust_imageview.setTag("profile_image_android");
        TextView textview = (TextView) view_.findViewById(R.id.editText_grditem_3);

        ImageView image_msg = (ImageView) view_
                .findViewById(R.id.image_tosend_chat_screen);
        TextView textview_chatter = (TextView) view_
                .findViewById(R.id.tv_chat_screen_context_chatter);

        final String[] split = value.split("~::~");

        textview_time.setText(split[5]);
        textview.setText(split[4]);

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


    public enum Extentions {
        JPEG, png, jpg, gif, TIF, tif
    }

    public class LocalBinder extends Binder {
        public PlatformManagerBootstrap getService() {
            return PlatformManagerBootstrap.this;
        }
    }

    class LocalWordServiceCCHandler extends Handler {
        private final WeakReference<PlatformManagerBootstrap> _service;
        int counter_0_00 = 1;
        String _previous_message = "~::~", _curr_message = "~::~";

        public LocalWordServiceCCHandler(PlatformManagerBootstrap service) {
            _service = new WeakReference<PlatformManagerBootstrap>(service);
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
                            System.out.println("___________________________________NullPointer Exception" + new_message_to_send);
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
                            // String[] tempAr = temp.split("~::~");
                            // setNew_message(temp);
                            new_image_message_to_send = temp;
                            // activityMessenger.send(replyMsg);
                            System.out.println("___________________________________ No NullPointer Exception Image" + new_image_message_to_send);

                        } catch (Exception e) {
                            System.out.println("___________________________________NullPointer Exception Image" + new_image_message_to_send);
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
                                            //_previous_message = _curr_message;
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
                            //System.out.println("__________>>>>>>><<<<<<<<<<<<<<___________________");
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
        public PlatformManagerBootstrap getService() {
            return PlatformManagerBootstrap.this;

        }
    }

    public class GetPlatformUpdate implements SensibleThingsListener, DisseminationCoreStateListener, BinaryGetResponseListener {
        SensibleThingsPlatform platform;
        String received_message = "NO#";
        SharedPreferences mPrefs = getSharedPreferences("bootstraps", 0);
        TreeMap bs_map = new TreeMap<String, String>();
        Set<String> bootstraps = mPrefs.getStringSet("bootstraps", bs_map.keySet());
        String preferred_bs_ip = mPrefs.getString("PREFERRED_IP", "193.10.119.42");
        Iterator itr = bootstraps.iterator();

        // Intent -- Messaging Service
        Intent service_intent = new Intent(PlatformManagerBootstrap.this, MainActivity.class);
        String _message_1 = "";
        // count_0=1;
        // }
        int counter_0_0 = 45;

        public GetPlatformUpdate(String bootstrap_ip) {
            if (bootstraps.size() == 0) {
                custom.setFLAG_DISP_NO_IP(true);
                mPrefs = getSharedPreferences("myprofile", 0);
                SharedPreferences.Editor ed = mPrefs.edit();
                ed.putBoolean("FLAG_DISP_NO_IP", true);
                ed.commit();
                // bootstrap_ip = "193.10.119.42";
            } else {
                bootstrap_ip = preferred_bs_ip;
            }
            KelipsLookup.bootstrapIp = bootstrap_ip;
            KelipsLookup.BOOTSTRAP = true;
            if ((custom.isHotSpotON() || isInternetAvailable()) && custom.IS_NODE_BOOTSTRAP_()) {
                //Platform Convergence
                BKGroundTask_Init background_init = new BKGroundTask_Init(this, this, this);
                background_init.execute();
                //platform = new SensibleThingsPlatform(this);
                bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);

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
                    if ((custom.isHotSpotON() || isInternetAvailable()) && custom.IS_NODE_BOOTSTRAP_())
                        background_0.execute("", "", "ST");
                }


        }

        public void getBackGroundRunning() {
            BKGroundTask_0 background_0 = new BKGroundTask_0();
            if ((custom.isHotSpotON() || isInternetAvailable()) && custom.IS_NODE_BOOTSTRAP_())
                background_0.execute();

        }

        @Override
        public void getResponse(String uci, byte[] value, SensibleThingsNode fromNode) {
            System.out.println("GetResponse Byte[]### ");
            try {
                if (_node_uci_map.get(fromNode.toString()) == null) {
                } else if (_node_uci_map.get(fromNode.toString()) == randomUci) {
                } else if (_node_uci_map.get(fromNode.toString()) != randomUci && _node_uci_map.get(fromNode.toString()) != custom.getPeer_nick_name() + contexter_domain)
                    if (!temp_image_byte_holder.isEmpty()) {
                        if (temp_image_byte_holder.containsKey(_node_uci_map
                                .get(fromNode.toString()))) {
                            if (value.length % 1024 == 0) {
                                array_byte = temp_image_byte_holder
                                        .get(_node_uci_map.get(fromNode.toString()));
                                if (array_byte.size() == 0) {
                                    {
                                        array_byte.add(value);
                                        temp_image_byte_holder.remove(_node_uci_map
                                                .get(fromNode.toString()));
                                        temp_image_byte_holder.put(_node_uci_map
                                                        .get(fromNode.toString()),
                                                array_byte);
                                    }
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
                                // System.out.println("Data Length: >>>>" +
                                // value.length
                                // + ":"
                                // + count);

                            } else {
                                disarray_byte_holder_per = disarray_byte_holder
                                        .get(_node_uci_map.get(fromNode.toString()));
                                array_byte = temp_image_byte_holder
                                        .get(_node_uci_map.get(fromNode.toString()));
                                for (Map.Entry<Integer, byte[]> entry : disarray_byte_holder_per
                                        .entrySet()) {
                                    // System.out.println("Key : " + entry.getKey()
                                    // + " Value : ");
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
                                    if (indexx == 0 && bb != null) {
                                        if (image_name.equalsIgnoreCase(randomUci)) {
                                            System.out.println("____________________" + uci + "___" + fromNode);
                                            // Request Image Again
                                        }
                                        //file_ = new File(image_name.replace(
                                        //"/", "_").replace("@", "%")
                                        //+ ".jpg");

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
                                                // Receive Images only three
                                                // duplicates
                                                // -- due to error
                                                System.out
                                                        .println("________________________________________________"
                                                                + image_name);
                                                System.out
                                                        .println("________________________We have done receiving Images!");


                                                /// Choose the correctly recieved image--
                                                image_name = _node_uci_map.get(fromNode.toString());
                                                // Four copies of images recieved --to choose the correctlly recieved image
                                                File img_file_1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/Media/" + image_name.replace("/", "_").replace("@", "%") + ".jpg");
                                                File img_file_2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/Media/" + image_name.replace("/", "_").replace("@", "%") + "%%" + ".jpg");
                                                File img_file_3 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/Media/" + image_name.replace("/", "_").replace("@", "%") + "%%%%" + ".jpg");
                                                File img_file_4 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/Media/" + image_name.replace("/", "_").replace("@", "%") + "%%%%%%" + ".jpg");
                                                if (img_file_1.exists()) {

                                                    fis_val = new FileInputStream(img_file_1);
                                                    System.out.println("_________________________________________file" + img_file_1);
                                                    if (isJPEGValid(fis_val)) {
                                                        // keep the image
                                                        System.out.println(img_file_1.getAbsolutePath() + "_________________________________________No Corrupted Image Deleted" + isJPEGValid(fis_val));
                                                    } else {
                                                        // Delete Corrupted Image
                                                        //file_.delete();
                                                        System.out.println(img_file_1.getAbsolutePath() + "_________________________________________Corrupted Image  Deleted");

                                                    }
                                                }//--
                                                if (img_file_2.exists()) {

                                                    fis_val = new FileInputStream(img_file_2);
                                                    System.out.println("_________________________________________file" + img_file_2);
                                                    if (isJPEGValid(fis_val)) {
                                                        // keep the image
                                                        System.out.println(img_file_2.getAbsolutePath() + "_________________________________________No Corrupted Image Deleted" + isJPEGValid(fis_val));
                                                    } else {
                                                        // Delete Corrupted Image
                                                        //file_.delete();
                                                        System.out.println(img_file_2.getAbsolutePath() + "_________________________________________Corrupted Image  Deleted");

                                                    }
                                                }//--
                                                if (img_file_3.exists()) {

                                                    fis_val = new FileInputStream(img_file_3);
                                                    System.out.println("_________________________________________file" + img_file_3);
                                                    if (isJPEGValid(fis_val)) {
                                                        // keep the image
                                                        System.out.println(img_file_3.getAbsolutePath() + "_________________________________________No Corrupted Image Deleted" + isJPEGValid(fis_val));
                                                    } else {
                                                        // Delete Corrupted Image
                                                        //file_.delete();
                                                        System.out.println(img_file_3.getAbsolutePath() + "_________________________________________Corrupted Image  Deleted");

                                                    }
                                                }//--
                                                if (img_file_4.exists()) {

                                                    fis_val = new FileInputStream(img_file_4);
                                                    System.out.println("_________________________________________file" + img_file_4);
                                                    if (isJPEGValid(fis_val)) {
                                                        // keep the image
                                                        System.out.println(img_file_4.getAbsolutePath() + "_________________________________________No Corrupted Image Deleted" + isJPEGValid(fis_val));
                                                    } else {
                                                        // Delete Corrupted Image
                                                        //file_.delete();
                                                        System.out.println(img_file_4.getAbsolutePath() + "_________________________________________Corrupted Image  Deleted");

                                                    }
                                                }//--


                                            } else {
                                                image_name = _node_uci_map
                                                        .get(fromNode.toString());
                                                // file_ = new File(image_name
                                                //       .replace("/", "_").replace(
                                                //             "@", "%")
                                                //   + ".jpg");
                                                file_ = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/Media/" + image_name.replace(
                                                        "/", "_").replace("@", "%")
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
                                            // ar_saved_image.add(fromNode);

                                            fout_out.flush();
                                            fout_out.close();

                                            // Delete corrupted Images --
                                            if (file_.exists()) {
                                                if (indexx == array_byte.size() - 1) {
                                                    fis_val = new FileInputStream(file_);
                                                    System.out.println("_________________________________________file" + file_);
                                                    if (isJPEGValid(fis_val)) {
                                                        // keep the image
                                                        System.out.println(file_.getAbsolutePath() + "_________________________________________Corrupted Image Not  Deleted" + isJPEGValid(fis_val));
                                                    } else {
                                                        // Delete Corrupted Image
                                                        //file_.delete();
                                                        // System.out.println(file_.getAbsolutePath() + "_________________________________________Corrupted Image  Deleted");

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
            } catch (IOException io) {

                // platform.notify(
                // fromNode,
                // this._node_uci_map.get(fromNode.toString()),
                // Constants.PROFILEIMAGEFILE + "~::~" + "Contexter" + "~::~"
                // + this.randomUci + "~::~"
                // + format.format(new Date()));
                io.printStackTrace();
            } catch (NullPointerException npe) {
            } finally {

            }

        }

        @Override
        public void onNewCoreState(DisseminationCore core, String newState) {
            if (newState.equals(DisseminationCore.DISSEMINATION_CORE_STATE_CONNECTED)) {
                System.out.println("Registering:" + randomUci);
                platform.register(randomUci);
                System.out.println("RUDP Bootstrap is operational");
            }
        }

        public void getResponse(String uci, String value,
                                SensibleThingsNode node) {

            System.out.println("GetResponse ### ");
            try {
                MDecoder md = new MDecoder("US-ASCII");
                boolean isUTF8 = md.check(value, "UTF-8");

                if (custom.isHotSpotON() || isInternetAvailable()) {
                    // If the Characters are UTF-8 then
                    if (isUTF8 && !uci.equalsIgnoreCase(custom.getPeer_nick_name() + contexter_domain)) {
                        System.out.println("GetResponse ### " + value);
                        //Add node -uci entry.
                        if (!_node_uci_map.containsKey(node.toString()))
                            _node_uci_map.put(node.toString(), uci);

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
                            MessageNotifier(null, "Wisper " + value.split("~::~")[1], "", "", false, "PrivateActivity", getUniqueSerialNumber(3, 9));
                            displayCustomizedToastWisp(getBaseContext(), value, true);


                        } else if (value.startsWith(String.valueOf(Constants.REGISTER))) {
                            custom.set_NEW_INFO_AVAILABLE_(true);
                            custom.set_NOTIFICATIONS_AVAILABLE(true);
                            //String _str_00 = value.split("~::~")[2];
                            //String[] _str_000 = _str_00.split("~::~");
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
                            //Groups _groups_0 = new Groups(getBaseContext());

                            ArrayList<String> al = _groups_0.getmGroups();
                            for (String str : al) {
                                // if (str.contains(search_key_str))
                                // Send back search result
                                if (str.contains(":::")) str = str.split(":::")[1];
                                System.out.println("_____________________________group" + str);
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
                            System.out.println("_____________________________group" + _groups_0.getmGroups().size());
                            platform.notify(node, uci, Constants.SEARCHED + "~::~" + _mGroup + "~::~" + _mUCI + "~::~" + "~Test~" + "~::~" + format.format(new Date()));


                        } else if (value.startsWith(String.valueOf(Constants.SEARCHED))) {
                            setNew_message("[Get Response]" + uci + ":" + value);
                            this.setReceived_message(_message_1);

                        } else if (value.startsWith(String.valueOf(Constants.PUBLICIMAGEFILE))) {
                            //Send Image  ---
                            if (IS_IMAGE_MESSAGE_READY) {
                                FileInputStream fin;
                                try {
//                                    String file_url = custom.get_image_message_uri();
                                    String file_url = value.split("~::~")[3];
                                    fin = new FileInputStream(file_url);
                                    getImageToSend(fin, platform, node, uci, file_url, Constants.PUBLICIMAGEFILE);
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
                            //System.out.println(value);
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
                                    //fout.flush();
                                    //fout.close();
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                        } else if (value.startsWith(String.valueOf(Constants.PROFILEIMAGEFILED))) {
                            if (!_image_meta_data_temp.containsValue(value))
                                _image_meta_data_temp.put(uci, value);

                        } else if (value.startsWith(String.valueOf(Constants.NODES))) {
                            //System.out.println(value);
                            _peers_0.addPeer(value, 0);

                        } else if (value.startsWith(String.valueOf(Constants.BOOTSTRAPS))) {
                            //_peers_0.addPublicBS(value, 0);

                        } else if (value.startsWith(String.valueOf(Constants.PASSWORD))) {

                        } else if (value.startsWith(String.valueOf(Constants.GROUPS))) {

                        }

                    } else {
                        //System.out.println("Not Supported format!!!");
                        // Do nothing!  it is not UTF-8 format -- this could be an image
                    }

                    if (!_mListofUCI.contains(uci)) {
                        _mListofUCI.add(uci);
                        // peers.addPair(uci, fromNode.toString());
                        // peers.saveProperties();
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
            System.out.println("ResolveResponse ### ");

            //if(count_0++%15==0){

            //setNew_message("[Resolve Response]" + node + ":" + uci);
            System.out.println("[Resolve Response]" + node + ":" + uci);

            if (custom.isHotSpotON() || isInternetAvailable()) {
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

        public void getEvent(SensibleThingsNode source, String uci) {
            System.out.println("GetEvent ### " + new_message_to_send);
            try {
                platform.get(uci, source);

                if (temp_ipc_queque.get(uci) == null) {
                    platform.notify(source, randomMobi2, new_message_to_send);
                    temp_ipc_queque.put(uci, new_message_to_send);

                    if (custom.is_IMAGE_SEND_()) {
                        // Send  Image
                        FileInputStream fin;
                        try {
                            String file_url = custom.get_image_message_uri();
                            fin = new FileInputStream(file_url);
                            getImageToSend(fin, platform, source, uci, file_url, Constants.PUBLICIMAGEFILE);

                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } finally {
                            try {
                                //fout.flush();
                                //fout.close();
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
                                    //fout.flush();
                                    //fout.close();
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
                                //fin = new FileInputStream(file_url);
                                // getImageToSend(fin, platform, source, uci, file_url, Constants.PROFILEIMAGEFILE);
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
                    if ((System.currentTimeMillis() / 1000) - LAST_UPDATE_TIME > 30) {
                        platform.notify(source, uci, Constants.NODES + NODES);
                        platform.notify(source, uci, Constants.BOOTSTRAPS + BOOTSTRAPS);
                        LAST_UPDATE_TIME = System.currentTimeMillis() / 1000;

                        ///Test////
                        if (COUNTER_0_000 < 10) {
                            FileInputStream fin;
                            //String file_url = custom.getProfile_image_uri();
                            // String file_url = EditProfileActivity.getAppFolder("") + "bill_.jpg";
                            String file_url = EditProfileActivity.getAppFolder("") + "Media/" + custom.getPeer_nick_name() + "%miun.se_mobi" + ".jpg";
                            System.out.println("______________________Image Sending________________________________" + file_url);
                            fin = new FileInputStream(file_url);
                            getImageToSend(fin, platform, source, uci, file_url, Constants.PROFILEIMAGEFILE);
                            COUNTER_0_000++;
                        }

                    } else {

                    }
                }

                String temp = "~::~";
                boolean _FLAG = false;
                for (String tt : _mListofUCI) {
                    _FLAG = true;
                    temp = "~::~" + tt + temp;
                }
                if (LAST_UPDATE_TIME_ == 0) {
                    LAST_UPDATE_TIME_ = System.currentTimeMillis() / 1000;
                    System.out.println(Constants.NODES + temp);
                } else {
                    if ((System.currentTimeMillis() / 1000) - LAST_UPDATE_TIME_ > 10) {
                        platform.notify(source, uci, Constants.NODES + temp);
                        LAST_UPDATE_TIME_ = System.currentTimeMillis() / 1000;
                        System.out.println(Constants.NODES + temp);
                        for (String str_adv : al_advertisements) {
                            str_adv = Constants.ADS + "~::~" + MainActivity.SELECTED_GROUP + "~::~" + randomUci + "~::~" + str_adv + "~::~" + "~::~" + "123456";
                            platform.notify(source, randomUci, str_adv);
                        }
                    } else {

                    }
                }
                Thread.sleep(3000);

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
                    //fout.flush();
                    //fout.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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

        public void saveImage(byte[] received_b, String image_name, String group_name) {
            String destName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + image_name;
            File file = new File(destName);
            destName = destName + ".jpg";
            file = new File(destName);
            file.setWritable(true);
            FileOutputStream fout = null;

            //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] image_file;
            byte[] readData = new byte[1024];
            try {
                fout = new FileOutputStream(file);
                //System.out.println("Dest." + destName);
                String uri_str = destName;
                byte[] b = new byte[1024];
                readData = received_b;
                fout.write(readData);
                // Save the images
                SharedPreferences.Editor ed = mPrefs.edit();
                mPrefs = getSharedPreferences("myprofile", 0);
                ed.putString("URI", uri_str);
                ed.commit();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {

                    fout.flush();
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
                while (custom.IS_NODE_BOOTSTRAP_() && platform.isInitalized()) {
                    try {

                        if ((custom.isHotSpotON() || isInternetAvailable()) && custom.IS_NODE_BOOTSTRAP_()) {
                            //_mListofUCI = chitchat_config_0.getPropertiesArrayList();
                            if (count++ % 15 == 0) {
                                platform.register(randomUci);
                                count = 1;
                            }
                            // check if the user has made nickname change while  connected
                            randomMobi2 = custom.getPeer_nick_name() + contexter_domain;
                            _mUCI = randomMobi2;
                            System.out.println("Bootstrap Running ### ");
                            _peers_1 = _peers_0.getmPeers();
                            NODES = "~::~";
                            BOOTSTRAPS = "~::~";
                            for (String uci_temp : _peers_1) {
                                if (!randomMobi2.equalsIgnoreCase(uci_temp)) {
                                    platform.resolve(uci_temp);
                                    NODES = NODES + uci_temp + "~::~";
                                }
                            }

                            // platform.register(randomMobi2);
                            // Thread.sleep(2000);
                            counter_0_0 = 1;

                            custom.setST_UP(true);
                            ST_PLATFORM_IS_UP = true;

                            if (!custom.IS_NODE_BOOTSTRAP_()) {
                                ST_PLATFORM_IS_UP = false;
                                platform.shutdown();
                                custom.setST_UP(false);
                                break;
                            }
                        } else {
                            custom.setST_UP(false);
                            ST_PLATFORM_IS_UP = false;
                        }
                        if (STOP_SERVICE) {
                            ST_PLATFORM_IS_UP = false;
                            platform.shutdown();

                        }

                        PublicChats _thread_0 = new PublicChats(getBaseContext(), MainActivity.SELECTED_GROUP, "advs");
                        ArrayList<String> temp_ar = _thread_0.getmPublicChat("");
                        for (String temp_00 : temp_ar)
                            if (!al_advertisements.contains(temp_00 + "::ADV"))
                                al_advertisements.add(temp_00 + "::ADV");
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
            DisseminationCoreStateListener core_lstnr = null;

            BKGroundTask_Init(SensibleThingsListener listner, DisseminationCoreStateListener core_listner, BinaryGetResponseListener b_listner) {
                lstnr = listner;
                b_lstnr = b_listner;
                core_lstnr = core_listner;
                try {
                    // fout = new FileOutputStream(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... arg0) {
                this.flag = false;
                try {
                    // Init the platform-- Node
                    //platform = new SensibleThingsPlatform(lstnr);
                    //platform.getDisseminationCore().setBinaryGetResponseListener(b_lstnr);

                    // Init the platform-- Bootstrap
                    if (custom.IS_NODE_BOOTSTRAP_()) {

                        RUDPCommunication.initCommunicationPort = 9009;
                        KelipsLookup.BOOTSTRAP = true;
                        platform = new SensibleThingsPlatform(LookupService.KELIPS,
                                Communication.RUDP, lstnr);

                        RUDPCommunication.initCommunicationPort = 0;
                        platform.getDisseminationCore().addStateListener(core_lstnr);
                        platform.getDisseminationCore().setBinaryGetResponseListener(b_lstnr);


                    }


                    peers = new Peers(getBaseContext(), "strapper.ucis");
                    _mListofUCI = peers.getmPeers();


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // platform.getDisseminationCore().getLookupService().getState() == "STATE_CONNECTED"
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
                        if (PlatformManagerBootstrap.ST_PLATFORM_IS_UP)
                            MessageNotifier(null, "MediaSense is running", "", "", true);
                        else
                            MessageNotifier(null, "MediaSense is running yet something is wrong. MediaSense needs to be restarted", "", "", true);


                        if (ST_PLATFORM_IS_UP)
                            MessageNotifier(null, custom.getCONTEXTER_ADVS(), "", "", true, 3);
                        //displayCustomizedToast(getBaseContext(), custom.getCONTEXTER_ADVS(),false);

                    } else {
                        SharedPreferences mPrefs = getSharedPreferences("bootstraps", 0);
                        TreeMap bs_map = new TreeMap<String, String>();
                        Set<String> bootstraps = mPrefs.getStringSet("bootstraps", bs_map.keySet());

                        // Adverts
                        if (ST_PLATFORM_IS_UP)
                            MessageNotifier(null, custom.getCONTEXTER_ADVS(), "", "", true, 3);

                        if (bootstraps != null) {
                            if (bootstraps.size() == 0) {
                                // displayCustomizedToast(getBaseContext(), "No valid or working BootStrap IP \n" + " Add IP.");
                                MessageNotifier(null, "No valid or working BootStrap IP \n", "", "", false);
                                custom.setFLAG_DISP_NO_IP(true);
                                mPrefs = getSharedPreferences("myprofile", 0);
                                SharedPreferences.Editor ed = mPrefs.edit();
                                ed.putBoolean("FLAG_DISP_NO_IP", true);
                                ed.commit();
                            } else if (custom.getPLATFORM_STARTUP_TRIAL() > 120 && (custom.isHotSpotON() || isInternetAvailable())) {
                                MessageNotifier(null, "MediaSense needs connection", "", "", false);
                            } else {
                                MessageNotifier(null, "MediaSense is not running", "", "", false);
                                //if (!custom.getCONTEXTER_ADVS().contains("none"))
                                custom.setFLAG_DISP_NO_IP(false);
                                mPrefs = getSharedPreferences("myprofile", 0);
                                SharedPreferences.Editor ed = mPrefs.edit();
                                ed.putBoolean("FLAG_DISP_NO_IP", true);
                                ed.commit();
                                platform.shutdown();
                            }
                        } else
                            displayCustomizedToast(getBaseContext(), "No valid or working BootStrap IP \n" + " Add IP.", false);
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

        public class BootStrapInitiliaztionThread extends Thread {
            SensibleThingsListener lstnr = null;

            Intent intent = new Intent();

            public BootStrapInitiliaztionThread(SensibleThingsListener listner) {
                lstnr = listner;
                platform = new SensibleThingsPlatform(lstnr);
            }

            @Override
            public void run() {
                Log.v("Main--->", "BootStrap Initialization Began...");
                while (!platform.isInitalized()) {
                    try {

                        if (platform.isInitalized()) {

                            intent.putExtra("STOP_SERVICE", false);
                            //sendBroadcast(intent);
                            this.interrupt();
                        } else {
                            this.interrupt();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                }
            }
        }

    }

    public class PlatformCommunicator {

        private String new_group = "";
        private String new_uci = "";
        private String new_message = "";
        private String new_search_key = "";
        private boolean NEW_GROUP_FLAG = false;
        private boolean NEW_UCI_FLAG = false;
        private boolean NEW_MESSAGE_FLAG = false;
        private boolean NEW_SEARCH_KEY = false;

        public PlatformCommunicator() {

        }

        public String getNew_group() {
            return new_group;
        }

        public void setNew_group(String new_group) {
            this.new_group = new_group;
        }

        public String getNew_uci() {
            return new_uci;
        }

        public void setNew_uci(String new_uci) {
            this.new_uci = new_uci;
        }

        public String getNew_message() {
            return new_message;
        }

        public void setNew_message(String new_message) {
            this.new_message = new_message;
        }

        public String getNew_search_key() {
            return new_search_key;
        }

        public void setNew_search_key(String new_search_key) {
            this.new_search_key = new_search_key;
        }

        public boolean isNEW_GROUP_FLAG() {
            return NEW_GROUP_FLAG;
        }

        public void setNEW_GROUP_FLAG(boolean nEW_GROUP_FLAG) {
            NEW_GROUP_FLAG = nEW_GROUP_FLAG;
        }

        public boolean isNEW_UCI_FLAG() {
            return NEW_UCI_FLAG;
        }

        public void setNEW_UCI_FLAG(boolean nEW_UCI_FLAG) {
            NEW_UCI_FLAG = nEW_UCI_FLAG;
        }

        public boolean isNEW_MESSAGE_FLAG() {
            return NEW_MESSAGE_FLAG;
        }

        public void setNEW_MESSAGE_FLAG(boolean nEW_MESSAGE_FLAG) {
            NEW_MESSAGE_FLAG = nEW_MESSAGE_FLAG;
        }

        public boolean isNEW_SEARCH_KEY() {
            return NEW_SEARCH_KEY;
        }

        public void setNEW_SEARCH_KEY(boolean nEW_SEARCH_KEY) {
            NEW_SEARCH_KEY = nEW_SEARCH_KEY;
        }

    }
}
