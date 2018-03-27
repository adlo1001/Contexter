package se.sensiblethings.app.chitchato.extras;


import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import se.sensiblethings.app.chitchato.activities.MainActivity;
import se.sensiblethings.app.chitchato.kernel.Peers;
import se.sensiblethings.disseminationlayer.disseminationcore.BinaryGetResponseListener;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.KelipsLookup;
import se.sensiblethings.interfacelayer.SensibleThingsListener;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;


public class LocalWordServiceCC extends Service {
    private final IBinder mBinder = new MyBinder();
    private ArrayList<String> list = new ArrayList<String>();
    String test_message_flip_flop = "ST Platform CC Service Running ... ";
    public static final int NEW_GROUP_FLAG = 100;
    public static final int NEW_UCI_FLAG = 101;
    public static final int NEW_MESSAGE_FLAG = 102;
    public static final int NEW_MESSAGE_FLAG_FA = 104;
    public static final int NEW_SEARCH_KEY = 103;
    public static final int NEW__SERVICE_STOP_MESSAGE_FLAG = 106;
    public static final int NEW__GROUP_MESSAGE_FLAG = 108;
    public static final int NEW_MESSAGE_FLAG_TEST = 1000;
    private String new_group = "";
    private String new_uci = "";
    private static String new_message = "no messages";
    private static String new_message_to_send = "no messages";
    private String new_search_key = "";
    public static String _mUCI = "@miun.se/mobi";
    //Tests
    String randomUci = "bootstrap@miun.se/random";
    String randomMobi2 = "@miun.se/mobi";
    byte[] readData = new byte[1024];
    static FileOutputStream fout;
    static File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "RecievedImage.jpg");

    // list of uci
    public Peers _peers_0 = new Peers(getBaseContext(), MainActivity.SELECTED_GROUP);
    public static ArrayList<String> _peers_1;
    public static String _mMessage = "NO#";
    public static String _mGroup = "*ALL#@*ALL#";
    public static String _mPeer = "*NO#";
    public static String _mValue = "*NO#";
    public static GetPlatformUpdate getPlatformUpdate;
    public Context app_context;
    protected Customizations custom;
    protected static boolean STOP_SERVICE = false;
    static public ArrayList<String> PEERS_IN_SELECTED_GROUP = new ArrayList<String>();
    Bundle extras;

    // Service Messagenger---
    android.os.Messenger mLocalWordServiceCCMessenger;
    android.os.Message msg = android.os.Message.obtain(null,
            LocalWordServiceCC.NEW_MESSAGE_FLAG, 0, 0);
    android.os.Messenger mServiceMessenger = new Messenger(
            new LocalWordServiceCCHandler(this));

    public String getTest_message_flip_flop() {
        return test_message_flip_flop;
    }

    public void setTest_message_flip_flop(String test_message_flip_flop) {
        this.test_message_flip_flop = test_message_flip_flop;
    }

    public class LocalBinder extends Binder {
        public LocalWordServiceCC getService() {
            return LocalWordServiceCC.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            getPlatformUpdate = new GetPlatformUpdate("192.168.0.100");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "No Valid or Working IP for BootStrap \n Please add IP. ", Toast.LENGTH_LONG).show();
        }
        custom = new Customizations(this, -1);
        randomMobi2 = custom.getPeer_nick_name() + randomMobi2;
        _mUCI = randomMobi2;
        app_context = this.getApplicationContext();
        System.out.println(test_message_flip_flop);
        Toast.makeText(getApplicationContext(),
                "ST Platform CC Service Running ... ", Toast.LENGTH_LONG)
                .show();


        return Service.START_NOT_STICKY;
    }


    //test---
    private Chronometer mChronometer;

    static class LocalWordServiceCCHandler extends Handler {
        private final WeakReference<LocalWordServiceCC> _service;
        String temp_received_mess = "";
        ArrayList<String> temp_list_of_messages = new ArrayList<String>();

        public LocalWordServiceCCHandler(LocalWordServiceCC service) {
            _service = new WeakReference<LocalWordServiceCC>(service);
        }

        @Override
        public void handleMessage(Message msg) {

            if (getPlatformUpdate != null)
                switch (msg.what) {
                    case NEW_GROUP_FLAG: {
                        Messenger activityMessenger = msg.replyTo;
                        Bundle b = new Bundle();
                        b.putString("NEW_GROUP", "New Group Create Requested");
                        Message replyMsg = Message.obtain(null, NEW_GROUP_FLAG);
                        replyMsg.setData(b);
                        try {
                            System.out.println("Hello From Activity:"
                                    + msg.getData().getString("NEW_MESSAGE_FLAG"));
                            activityMessenger.send(replyMsg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case NEW_UCI_FLAG: {
                        Messenger activityMessenger = msg.replyTo;
                        Bundle b = new Bundle();
                        b.putString("NEW_UCI_FLAG", "New UCI ... ");
                        Message replyMsg = Message.obtain(null, NEW_UCI_FLAG);
                        replyMsg.setData(b);

                        try {
                            activityMessenger.send(replyMsg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case NEW_MESSAGE_FLAG: {
                        try {
                            _mMessage = getPlatformUpdate.getReceived_message();
                            Messenger activityMessenger = msg.replyTo;

                            // Messages to send to chat screen
                            Bundle b = new Bundle();
                            b.putString("NEW_MESSAGE_FLAG", "Hello From Service<<<<<");
                            Message replyMsg = Message.obtain(null, NEW_MESSAGE_FLAG);
                            replyMsg.setData(b);


                            String temp = msg.getData().getString("NEW_MESSAGE_FLAG");
                            System.out.println("Hello From Service:>>>>>>>" + temp);
                            String[] tempAr = temp.split("~::~");

                            setNew_message(temp);
                            new_message_to_send = temp;

                            System.out
                                    .println("" + temp);

                            activityMessenger.send(replyMsg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case NEW_MESSAGE_FLAG_FA: {
                        try {
                            _mMessage = getPlatformUpdate.getReceived_message();
                            Messenger activityMessenger = msg.replyTo;
                            // Messages to send to chat screen

                            Bundle b = new Bundle();
                            b.putString("NEW_MESSAGE_FLAG_FA", "Hello From Service<<<<<");
                            Message replyMsg = Message.obtain(null, NEW_MESSAGE_FLAG_FA);
                            replyMsg.setData(b);
                            activityMessenger.send(replyMsg);

                            // activityMessenger.send(replyMsg);
                            System.out.println("LS--->"
                                    + msg.getData().getString("NEW_MESSAGE_FLAG_FA"));
                            // System.out.println("LS--->"+msg.getData().getString("NEW_MESSAGE_FLAG"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case NEW_SEARCH_KEY: {
                        Messenger activityMessenger = msg.replyTo;
                        Bundle b = new Bundle();
                        b.putString("NEW_SEARCH_KEY", "New Search Key ... ");
                        Message replyMsg = Message.obtain(null, NEW_SEARCH_KEY);
                        replyMsg.setData(b);
                        try {
                            activityMessenger.send(replyMsg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case NEW__SERVICE_STOP_MESSAGE_FLAG: {
                        String temp = msg.getData().getString(
                                "NEW__SERVICE_STOP_MESSAGE_FLAG");
                        if (temp.contains("STOP"))
                            STOP_SERVICE = true;
                        else
                            STOP_SERVICE = false;

                        System.out.println("Service STop REquested" + STOP_SERVICE);
                        break;
                    }

                    case NEW__GROUP_MESSAGE_FLAG: {
                        // _mMessage = getPlatformUpdate.getReceived_message();

                        // Messenger activityMessenger = msg.replyTo;
                        Message replyMsg = Message.obtain(null,
                                NEW__GROUP_MESSAGE_FLAG, 0, 0);
                        ArrayList<String> temp_arr = msg.getData().getStringArrayList(
                                "PEERS_IN_SELECTED_GROUP");

                        PEERS_IN_SELECTED_GROUP = temp_arr;

                    }
                    break;

                    case NEW_MESSAGE_FLAG_TEST:
                        long elapsedMillis = SystemClock.elapsedRealtime()
                                - _service.get().mChronometer.getBase();
                        int hours = (int) (elapsedMillis / 3600000);
                        int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
                        int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;
                        int millis = (int) (elapsedMillis - hours * 3600000 - minutes
                                * 60000 - seconds * 1000);

                        Messenger activityMessenger = msg.replyTo;
                        Bundle b = new Bundle();
                        //b.putString("timestamp", hours + ":" + minutes + ":" + seconds
                        //+ ":" + millis);
                        //b.putString("timestamp", "Hi..Hi...");

                        getPlatformUpdate.getBackGroundRunning();
                        //b.putString("_mess", getNew_message());
                        Random rand = new Random();
                        b.putString("_mess", rand.nextInt(50) + " " + getNew_message());
                        //Add Message to Temperary list of messages

                        Message replyMsg = Message.obtain(null, NEW_MESSAGE_FLAG_TEST);
                        replyMsg.setData(b);

                        try {

                            activityMessenger.send(replyMsg);


                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:

                        super.handleMessage(msg);
                        break;
                }

        }
    }

    final Messenger _messenger = new Messenger(new LocalWordServiceCCHandler(
            this));

    public Context getApp_context() {
        return app_context;
    }

    // test---
    @Override
    public void onCreate() {
        super.onCreate();
        mChronometer = new Chronometer(this);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }

    public void setApp_context(Context app_context) {
        this.app_context = app_context;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return _messenger.getBinder();
        // return mBinder;
    }

    public class MyBinder extends Binder {
        public LocalWordServiceCC getService() {
            return LocalWordServiceCC.this;

        }
    }

    public List<String> getWordList() {
        return list;
    }

    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mLocalWordServiceCCMessenger = new Messenger(service);
            android.os.Message replyMsg = android.os.Message
                    .obtain(null,
                            LocalWordServiceCC.NEW_MESSAGE_FLAG,
                            0, 0);
            try {
                System.out.println("Message Sent!!!! From Service !!!!!");
                mLocalWordServiceCCMessenger.send(replyMsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        public void onServiceDisconnected(ComponentName className) {
        }
    };

    public class GetPlatformUpdate implements SensibleThingsListener, BinaryGetResponseListener {
        SensibleThingsPlatform platform;
        String received_message = "NO#";
        SharedPreferences mPrefs = getSharedPreferences("bootstraps", 0);
        TreeMap bs_map = new TreeMap<String, String>();
        Set<String> bootstraps = mPrefs.getStringSet("bootstraps", bs_map.keySet());
        Iterator itr = bootstraps.iterator();

        // Intent -- Messaging Service
        Intent service_intent = new Intent(LocalWordServiceCC.this, MainActivity.class);

        public GetPlatformUpdate(String bootstrap_ip) {
            bootstrap_ip = (String) itr.next();
            KelipsLookup.bootstrapIp = bootstrap_ip;
            KelipsLookup.BOOTSTRAP = false;

            if (isInternetAvailable()) {

                //Platform Convergence
                BKGroundTask_Init background_init = new BKGroundTask_Init(this, this);
                background_init.execute();
                //platform = new SensibleThingsPlatform(this);
                bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);

            }
            if (platform != null)
                if (platform.isInitalized()) {
                    //platform.register(_mUCI);
                    System.out
                            .println("Client to Local Bootstrap is now running");
                    // this.runClient();
                    BKGroundTask_0 background_0 = new BKGroundTask_0();
                    background_0.execute("", "", "ST");

                    // platform.resolve(_mUCI);
                    System.out.println("Contexter resolving...");
                }
        }

        public void getBackGroundRunning() {
            BKGroundTask_0 background_0 = new BKGroundTask_0();
            background_0.execute();

        }

        @Override
        public void getResponse(String uci, byte[] value, SensibleThingsNode fromNode) {

            try {
                readData = value;
                // ImageTransfer.receiveImage(rawData, count);
                fout.write(readData, 0, 1024);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {

                try {
                    fout.flush();
                    fout.close();
                    File file_ = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + uci.replace("/", "_") + ".jpg");
                    if (!file_.exists()) {
                        file.renameTo(file_);
                        SharedPreferences.Editor ed = mPrefs.edit();
                        mPrefs = getSharedPreferences("myprofile", 0);
                        ed.putString(uci.replace("/", "_"), Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + uci.replace("/", "_") + ".jpg");
                        ed.commit();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }

        public class BKGroundTask_0 extends AsyncTask<String, String, String> {
            boolean flag = false;

            BKGroundTask_0() {
            }

            @Override
            protected String doInBackground(String... arg0) {
                this.flag = false;
                while (platform.isInitalized()) {
                    try {
                        System.out
                                .println("_________________________-_____________________________");
                        if (isInternetAvailable()) {
                            System.out
                                    .println("______________________________________________________");
                            System.out.println("Platform Running ### ");

                            platform.resolve(randomUci);

                            // Send message to all active peers
                            _peers_1 = _peers_0.getmPeers();
                            for (String uci_temp : _peers_1) {
                                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + uci_temp);
                                if (!randomMobi2.equalsIgnoreCase(uci_temp))
                                    platform.resolve(uci_temp);
                            }

                            platform.register(randomMobi2);

                            Thread.sleep(2000);
                        } else {
                            // if (platform.isInitalized())
                            // platform.shutdown();
                        }
                        if (STOP_SERVICE) {
                            //platform.shutdown();
                            //break;
                        }
                        Thread.sleep(2000);
                    } catch (Exception e) {

                        System.out
                                .println("_________________________-____-_________________________");
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
                try {
                    fout = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... arg0) {
                this.flag = false;


                while (platform == null) {
                    try {
                        platform = new SensibleThingsPlatform(lstnr);
                        platform.getDisseminationCore().setBinaryGetResponseListener(b_lstnr);

                    } catch (Exception e) {
                        System.out
                                .println("_________________________-____-_________________________");
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


        // Main loop of the client node
        public void runClient() {
            while (true) {
                try {
                    if (isInternetAvailable()) {
                        // System.out.println("Contexter resolving...");
                        platform.resolve("est_addis@m2m_chitchat.com");
                    } else {
                        // if (platform.isInitalized())
                        platform.shutdown();
                    }
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void getResponse(String uci, String value,
                                SensibleThingsNode node) {


            System.out.println("[Get Response]" + uci + ":" + value);
            setNew_message("[Get Response]" + uci + ":" + value);
            System.out.println("[Get Response----peers]" + _peers_0.getmPeers().size());

            if (value.startsWith("NODES:::"))
                _peers_0.addPeer(value, 0);

            if (isInternetAvailable()) {
                String _message_1 = "[GetResponse] " + uci + ":" + value;
                this.setReceived_message(_message_1);
                System.out.println(_message_1);

                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                platform.shutdown();
            }
        }

        public void resolveResponse(String uci, SensibleThingsNode node) {
            // TODO Auto-generated method stub
            System.out.println("[Resolve Response]" + node + ":" + uci);
            setNew_message("[Resolve Response]" + node + ":" + uci);
            if (isInternetAvailable()) {
                String _message_2 = "[ResolveResponse] " + uci + ": " + node;
                this.setReceived_message(_message_2);
                // System.out.println(_message_2);
                // Thread.sleep(2000);
                try {
                    msg.replyTo = mLocalWordServiceCCMessenger;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Bundle bundle_ = new Bundle();
                bundle_.putString("m_2", " _message_2");
                // receiver_.send(100, bundle_);
                // platform.notify(node, uci, "message__");
                platform.get(uci, node);
            } else {
                // platform.shutdown();
            }
        }

        public void getEvent(SensibleThingsNode source, String uci) {

            // TODO Auto-generated method stub
            platform.get(uci, source);

            // Send Message to the BootStrap
            platform.notify(source, randomMobi2, new_message_to_send);
        }

        public void setEvent(SensibleThingsNode source, String uci, String value) {
            // System.out.print("Please Enter Your message");
            //platform.set(uci, "message", source);
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
                System.out.println("Dest." + destName);
                String uri_str = destName;
                byte[] b = new byte[40 * 4096];
                readData = received_b;
                fout.write(readData, 0, 40 * 4096);
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
                        /*
                         * sleep(30 * 1000); if (!platform.isInitalized()) {
						 * STOP_SERVICE = true; intent.putExtra("STOP_SERVICE",
						 * STOP_SERVICE); sendBroadcast(intent);
						 * 
						 * }
						 */

                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                }
            }
        }

    }

    public static String getNew_message() {
        return new_message;
    }

    public static void setNew_message(String nm) {
        new_message = nm;
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

    // Check if the Internet is Available
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
