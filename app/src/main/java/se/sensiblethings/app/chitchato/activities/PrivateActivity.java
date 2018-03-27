package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.WeakReference;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import se.sensiblethings.app.chitchato.extras.Constants;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.DialogOne;
import se.sensiblethings.app.chitchato.extras.LocalStorageGroup;
import se.sensiblethings.app.chitchato.kernel.Groups;
import se.sensiblethings.app.chitchato.kernel.Peers;
import se.sensiblethings.app.chitchato.kernel.PrivateChats;
import se.sensiblethings.app.chitchato.kernel.RESTHandler;
import se.sensiblethings.app.chitchato.kernel.RESTHandler.RequestMethod;

public class PrivateActivity extends Activity {
    private static final int SELECT_IMAGE = 1;
    //15 is default num of messages to display
    public static int THREAD_LENGTH_TO_DISP = 15;
    public String SELECTED_GROUP = "";
    public String SELECTED_PEER = "";
    public ArrayList<String> al_Peers = new ArrayList<String>();
    protected ContextManager context_manager;
    protected boolean CHAT_MODE = true;
    protected Customizations custom;
    protected boolean SHOWPEERS = true;

    protected String groupName = null;

    // Holder for private conversations---- K-->uci --- V--->ArrayList
    protected TreeMap<String, ArrayList> private_chat_holder = new TreeMap<String, ArrayList>();
    protected ArrayList<String> temp_private_chat_holder = new ArrayList<String>();
    protected LocalStorageGroup storage_temp;
    TextView tv_1, tv_2;
    EditText edit_txt;
    Button btn;
    ImageButton img_btn, img_btn_show, img_btn_hide;
    ImageView image_view_profile = null, image_to_send = null;
    long l;
    String temp;
    ArrayList activenodes;
    String[] a;
    Message message;
    String nodes = "%";
    ListView lv_1;
    GridView gv_1;
    ProgressBar progress_bar;
    ArrayList<String> favorite_peers;

    //Time Formats --
    String date_pattern = "yyyy-MM-dd HH:mm:ss";
    Format format = new SimpleDateFormat(date_pattern);
    // Service Messagenger---
    android.os.Messenger mPlatformManagerNodeMessenger;
    android.os.Message msg = android.os.Message.obtain(null,
            PlatformManagerNode.NEW_MESSAGE_FLAG, 0, 0);
    android.os.Messenger mActivityMessenger = new Messenger(
            new ActivityHandler(this));
    Intent service_intent;
    boolean _BOUND = false;
    String _received_message = "Initial";
    // Sensor Manager
    SensorManager mSensorManager;
    // previuos messaages
    PrivateChats _thread_0;
    PlatformManagerNode update_service;
    // temp conatiners
    TreeMap<String, ArrayList<String>> tm;
    ArrayList<String> al_;
    private LinearLayout ll = null;
    private SharedPreferences mPrefs;
    private String peerName = "#None";
    private String chatMessage = "";
    private String contextMessage = "";
    private String localPeerName = "";
    private Groups groups;
    private Peers peers;
    private ImageListAdapter imageListAdapter = null;
    private ListAdapter list_adapter = null;
    private ListAdapter list_adapter_lon = null;
    private ArrayList<String> al_prchats = new ArrayList<String>();
    private ArrayList<String> al_prchats_er = null;
    private String Message = "";
    private String selectedImagePath;
    private String filemanagerstring;
    private String incoming_message = "#";
    private String uri_str_mimage_messsage = "#Nothing#";
    private DialogOne dialog_one;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mPlatformManagerNodeMessenger = new Messenger(service);
            _BOUND = true;

        }

        public void onServiceDisconnected(ComponentName className) {
            update_service = null;
            Toast.makeText(PrivateActivity.this, "Disconnected",
                    Toast.LENGTH_SHORT).show();

            _BOUND = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        custom = new Customizations(this, -1);
        setContentView(custom.getPrivateChatScreen());

        mPrefs = getSharedPreferences("preference_1", 0);
        CHAT_MODE = mPrefs.getBoolean("OChat", true);

        tv_1 = (TextView) findViewById(R.id.tv_pr_chat_title);

        edit_txt = (EditText) findViewById(R.id.editText3_pr_chat);
        btn = (Button) findViewById(R.id.button1_pr_chat);
        img_btn = (ImageButton) findViewById(R.id.img_btn_add_prfile);
        img_btn_show = (ImageButton) findViewById(R.id.btn_show);
        img_btn_hide = (ImageButton) findViewById(R.id.btn_hide);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");

        // Font face
        tv_1.setTypeface(tf);
        edit_txt.setTypeface(tf);
        btn.setTypeface(tf);
        tv_1.setText("");

        lv_1 = (ListView) findViewById(R.id.ListView1_pr_chat);
        gv_1 = (GridView) findViewById(R.id.gridView1_pr_chat);
        ll = (LinearLayout) findViewById(R.id.linear2_pr_chat);
        progress_bar = (ProgressBar) findViewById(R.id.progressBar2);
        dialog_one = new DialogOne(this, false, 8);

        Bundle extras = getIntent().getExtras();
        groupName = extras.getString("groupName");


        if (custom.IS_NODE_BOOTSTRAP_()) {
            service_intent = new Intent(PrivateActivity.this, PlatformManagerBootstrap.class);
            al_Peers = PlatformManagerBootstrap._peers_1;
            favorite_peers = al_Peers;
            if (PlatformManagerBootstrap._peers_1 != null) {
                ShowPeers(favorite_peers);
            } else {
                favorite_peers = new ArrayList<String>();

            }


        } else {
            service_intent = new Intent(PrivateActivity.this, PlatformManagerNode.class);
            bindService(service_intent, mConnection,
                    Context.BIND_AUTO_CREATE);
            al_Peers = PlatformManagerNode._peers_1;
            favorite_peers = al_Peers;

            if (PlatformManagerNode._peers_1 != null) {
                ShowPeers(favorite_peers);
            } else {
                favorite_peers = new ArrayList<String>();
                // favorite_peers.add("Contexter");

            }

        }

        // Vibrattionfor public and private mode
        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final MediaPlayer mp = MediaPlayer.create(PrivateActivity.this, R.raw.button_click_one);
        final MediaPlayer mp_long_tap = MediaPlayer.create(PrivateActivity.this, R.raw.sms_alert_daniel_simon);
        tm = new TreeMap<String, ArrayList<String>>();
        Runnable runnable_0000 = new Runnable() {
            @Override
            public void run() {
                al_ = new ArrayList<String>();
                String group_name, peer_name, file_name;
                File[] private_message__dir_files = getPrivateMsgfromDir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/local/");
                for (File f : private_message__dir_files) {
                    al_.clear();
                    file_name = f.getName().replace(".", ":");
                    if (file_name.split(":").length == 3) {
                        group_name = file_name.split(":")[0];
                        peer_name = file_name.split(":")[1];
                        _thread_0 = new PrivateChats(getBaseContext(), group_name, peer_name);
                        ArrayList<String> temp_ar = _thread_0.getmPrivateChat(group_name);

                        TreeMap<Integer, String> seq_map = new TreeMap<Integer, String>();
                        for (String _tmp_ : temp_ar) {
                            String[] tmp_0_0 = _tmp_.split("~::~");
                            if (tmp_0_0.length > 5 && tmp_0_0.length < 8) {
                                seq_map.put(Integer.parseInt(tmp_0_0[6]), _tmp_);
                            } else if (tmp_0_0.length > 8) {
                                seq_map.put(Integer.parseInt(tmp_0_0[10]), _tmp_);
                            } else ;
                            //al_prchats.add(_tmp_);
                        }

                        Set set = seq_map.entrySet();
                        Iterator itr = set.iterator();
                        Map.Entry me = null;
                        while (itr.hasNext()) {
                            me = (Map.Entry) itr.next();
                            String str_tmp = String.valueOf(me.getValue());
                            al_.add(str_tmp);
                        }
                        tm.put(f.getName().replace(".prchat", "").replace(".", "#").split("#")[1], al_);

                        if (!tm.isEmpty()) {
                            Set<String> _set_0 = tm.keySet();
                            for (String str : _set_0) {
                                if (!favorite_peers.contains(str))
                                    if (!favorite_peers.contains(str + PlatformManagerNode.contexter_domain))
                                        favorite_peers.add(str + PlatformManagerNode.contexter_domain);
                            }
                        }
                        ShowPeers(favorite_peers);

                    }

                }

            }
        };

        Thread _mthread_00 = new Thread(runnable_0000);
        _mthread_00.start();


        ShowPeers(favorite_peers);
        // Context Information//
        if (!CHAT_MODE) {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            context_manager = new ContextManager(getBaseContext(),
                    localPeerName, mSensorManager);
            context_manager.updateContextData();
        }

        if (al_prchats.isEmpty())
            al_prchats.add("Tap your friend to begin private messaging \nTap-Long your friend to make call");

        progress_bar.setVisibility(View.GONE);
        list_adapter = new ListAdapter(this, -1, CHAT_MODE, al_Peers, 10);

        if (favorite_peers == null || favorite_peers.isEmpty()) {
            list_adapter.setSetProfilePictureGone(true);
        } else {
            list_adapter.setSetProfilePictureGone(true);
        }

        // Change this later
        lv_1.setAdapter(list_adapter);
        imageListAdapter = new ImageListAdapter(getBaseContext(), false,
                CHAT_MODE, 0, al_prchats);

        // lv_2.setAdapter(listadapter);
        gv_1.setAdapter(imageListAdapter);

        lv_1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {

                //vibrator.vibrate(20);
                mp.start();
                try {
                    lv_1.setSelection(position);
                    list_adapter.setPosition(position);
                    lv_1.setSelection(position);
                    lv_1.setAdapter(list_adapter);

                    TextView sub_item = (TextView) view
                            .findViewById(R.id.tv_chat_screen_sub_item);
                    sub_item.setVisibility(View.VISIBLE);
                    TextView item = (TextView) view
                            .findViewById(R.id.favorite_group_btn);
                    SELECTED_PEER = sub_item.getText().toString();
                    SELECTED_GROUP = groupName;

                    //Change lable
                    if (!SELECTED_PEER.equalsIgnoreCase(""))
                        tv_1.setText(SELECTED_PEER.replace("@", " "));
                    else
                        tv_1.setText("");//

                    if (mPlatformManagerNodeMessenger == null) {
                        if (_BOUND) {
                            unbindService(mConnection);
                            bindService(service_intent, mConnection,
                                    Context.BIND_AUTO_CREATE);
                        } else
                            bindService(service_intent, mConnection,
                                    Context.BIND_AUTO_CREATE);
                    }


                    peerName = list_adapter.getItem(position);
                    SELECTED_PEER = sub_item.getText().toString();
                    gv_1.setVisibility(View.GONE);

                    Runnable _runnable_00 = new Runnable() {
                        @Override
                        public void run() {

                            ArrayList<String> temp_ar = new ArrayList<String>();
                            al_prchats.clear();

                            String _file_name_ = SELECTED_PEER;
                            if (_file_name_.contains("@")) {
                                _file_name_ = _file_name_.replace("@", ":");
                                _file_name_ = _file_name_.split(":")[0];
                            }
                            _thread_0 = new PrivateChats(getBaseContext(), SELECTED_GROUP, _file_name_);
                            temp_ar = _thread_0.getmPrivateChat(SELECTED_GROUP, THREAD_LENGTH_TO_DISP);
                            if (_thread_0.getmPrivateChatSize() >= 15)
                                al_prchats.add("~PLATFORM~MORE~");
                            TreeMap<Integer, String> seq_map = new TreeMap<Integer, String>();
                            Time t = null;
                            for (String _tmp_ : temp_ar) {
                                String[] tmp_0_0 = _tmp_.split("~::~");
                                if (tmp_0_0.length > 5 && tmp_0_0.length < 8) {
                                    seq_map.put(Integer.parseInt(tmp_0_0[6]), _tmp_);
                                } else if (tmp_0_0.length > 8) {
                                    seq_map.put(Integer.parseInt(tmp_0_0[10]), _tmp_);
                                } else
                                    al_prchats.add(_tmp_);

                            }

                            Set set = seq_map.entrySet();
                            Iterator itr = set.iterator();

                            Map.Entry me = null;
                            while (itr.hasNext()) {
                                me = (Map.Entry) itr.next();
                                String str_tmp = String.valueOf(me.getValue());
                                al_prchats.add(str_tmp);
                            }
                            ShowMessages(al_prchats);
                        }
                    };
                    Thread _mthread_00 = new Thread(_runnable_00);
                    _mthread_00.start();


                    // Hide List of peers on the right side
                    img_btn_show.setVisibility(View.VISIBLE);
                    img_btn_hide.setVisibility(View.GONE);

                    if (!SELECTED_PEER.equalsIgnoreCase(""))
                        tv_1.setText(SELECTED_PEER.replace("@", " "));
                    LinearLayout ll = (LinearLayout) findViewById(R.id.linear2_pr_chat_);
                    ll.setVisibility(View.GONE);


                } catch (Exception e) {
                    e.printStackTrace();
                    Intent intent = new Intent(PrivateActivity.this, ErrorActivity.class);
                    intent.putExtra("error", e.getLocalizedMessage());
                    startActivity(intent);
                }
            }
        });

        lv_1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int position, long arg3) {

                mp_long_tap.start();
                displayCustomizedToast(PrivateActivity.this, "This version doesnt support voice. \nCheck out the next version.");
                return false;

            }
        });

        btn.setOnClickListener(new OnClickListener() {
                                   @Override
                                   public void onClick(View arg0) {

                                       chatMessage = edit_txt.getText().toString();
                                       edit_txt.setText("");

                                       Runnable runnable_00 = new Runnable() {
                                           @Override
                                           public void run() {
                                               MediaPlayer mp = MediaPlayer.create(PrivateActivity.this, R.raw.tiny_button_push);
                                               MediaPlayer mp_failed = MediaPlayer.create(PrivateActivity.this, R.raw.outgoing_fg);
                                               if (al_Peers != null)
                                                   if (al_Peers.size() == 0) {
                                                       displayCustomizedToast(PrivateActivity.this, "Invalid operation! You need friends.");
                                                   } else {
                                                       if (PlatformManagerNode.ST_PLATFORM_IS_UP || PlatformManagerBootstrap.ST_PLATFORM_IS_UP)
                                                           try {
                                                               if (al_Peers.size() > 1) {
                                                                   if (chatMessage.trim().isEmpty()) {
                                                                       // displayCustomizedToast(PrivateActivity.this, "Invalid Message! Enter Message");
                                                                   } else if (CHAT_MODE == false) {
                                                                       //chatMessage = edit_txt.getText().toString().trim();
                                                                       // DrawGraphOne.READ_SENSORS = true;
                                                                       contextMessage = context_manager.getUserContext()
                                                                               .getTemprature()
                                                                               + "~::~"
                                                                               + context_manager.getUserContext()
                                                                               .getLimunosity()
                                                                               + "~::~"
                                                                               + context_manager.getUserContext()
                                                                               .getAcce()
                                                                               + "~::~"
                                                                               + context_manager.getUserContext()
                                                                               .getAddress();

                                                                       if (custom.getPeer_nick_name() == null)
                                                                           custom.setPeer_nick_name(PlatformManagerNode._mUCI);

                                                                       //Message Formats--
                                                                       //
                                                                       //MESSAGETYPE~::~GROUPNAME~::~UCI~::~RECEIVER_UCI~::~MESSAGE~::~SPEED~::~LUMINOSITY~::~SOUNDPRESSURE~::~ADDRESS~::~TIME~::~MESSAGESERIAL~::~PW
                                                                       if (PlatformManagerNode.ST_PLATFORM_IS_UP || PlatformManagerBootstrap.ST_PLATFORM_IS_UP) {
                                                                           Message = Constants.PRIVATE + "~::~" + SELECTED_GROUP + "~::~" + custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~" + SELECTED_PEER + "~::~" +
                                                                                   chatMessage + "~::~" + contextMessage + "~::~" + format.format(new Date()) + "~::~" + getUniqueSerialNumber(3, 9) + "~::~" + "123456";
                                                                           if (!custom.isSilent_One()) mp.start();

                                                                       } else

                                                                       {
                                                                           Message = Constants.PRIVATE + "~::~" + SELECTED_GROUP + "~::~" + custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~" + SELECTED_PEER + "~::~" +
                                                                                   chatMessage + "~::~" + contextMessage + "~::~" + format.format(new Date()) + "failed~::~" + getUniqueSerialNumber(3, 9) + "~::~" + "123456";
                                                                           if (!custom.isSilent_One()) mp_failed.start();
                                                                       }


                                                                       al_prchats.add("" + Message + "");
                                                                       ShowMessages(al_prchats);
                                                                       // edit_txt.setText("");


                                                                       if (custom.getPeer_nick_name().equalsIgnoreCase("contexter_0_0") || custom.getPeer_nick_name().startsWith("@"))
                                                                           displayCustomizedToast(PrivateActivity.this, "Invalid UCI!  Message not delivered.");
                                                                       else {
                                                                           if (PlatformManagerNode.ST_PLATFORM_IS_UP || PlatformManagerBootstrap.ST_PLATFORM_IS_UP)
                                                                               if (_BOUND) {
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
                                                                                   mPlatformManagerNodeMessenger.send(replyMsg);

                                                                                   // ---Initiate Receiving New Message Flag---//
                                                                                   android.os.Message msg = android.os.Message.obtain(null,
                                                                                           PlatformManagerNode.NEW_MESSAGE_FLAG_TEST, 0, 0);
                                                                                   msg.replyTo = mActivityMessenger;
                                                                                   mPlatformManagerNodeMessenger.send(msg);

                                                                               } else {
                                                                                   PrivateActivity.this.runOnUiThread(new Runnable() {
                                                                                       @Override
                                                                                       public void run() {
                                                                                           edit_txt.setText(chatMessage);
                                                                                       }
                                                                                   });
                                                                                   if (isInternetAvailable() && !custom.IS_NODE_BOOTSTRAP_()) {
                                                                                       displayCustomizedToast(PrivateActivity.this, "Something went wrong with the platform.\n Try sending message again");
                                                                                       startService(service_intent);
                                                                                   }

                                                                               }
                                                                       }

                                                                   } else if (CHAT_MODE == true) {

                                                                       // ---Send New Message Flag---//
                                                                       if (custom.getPeer_nick_name() == null)
                                                                           custom.setPeer_nick_name(PlatformManagerNode._mUCI);
                                                                       //Message Formats--
                                                                       //
                                                                       //MESSAGETYPE~::~GROUPNAME~::~UCI~::~RECEIVER_UCI~::~MESSAGE~::~TIME~::~MESSAGESERIAL~::~PW
                                                                       if (PlatformManagerNode.ST_PLATFORM_IS_UP || PlatformManagerBootstrap.ST_PLATFORM_IS_UP) {
                                                                           Message = Constants.PRIVATE + "~::~" + SELECTED_GROUP + "~::~" + custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~" + SELECTED_PEER + "~::~" +
                                                                                   chatMessage + "~::~" + format.format(new Date()) + "~::~" + getUniqueSerialNumber(3, 9) + "~::~" + "123456";
                                                                           if (!custom.isSilent_One()) mp.start();
                                                                       } else {
                                                                           Message = Constants.PRIVATE + "~::~" + SELECTED_GROUP + "~::~" + custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~" + SELECTED_PEER + "~::~" +
                                                                                   chatMessage + "~::~" + format.format(new Date()) + "failed~::~" + getUniqueSerialNumber(3, 9) + "~::~" + "123456";
                                                                           if (!custom.isSilent_One()) mp_failed.start();
                                                                       }

                                                                       al_prchats.add(Message + "");
                                                                       ShowMessages(al_prchats);
                                                                       //  edit_txt.setText("");


                                                                       if (custom.getPeer_nick_name().equalsIgnoreCase("contexter_0_0") || custom.getPeer_nick_name().startsWith("@"))
                                                                           displayCustomizedToast(PrivateActivity.this, "Invalid UCI!  Message not delivered.");
                                                                       else {
                                                                           if (PlatformManagerNode.ST_PLATFORM_IS_UP || PlatformManagerBootstrap.ST_PLATFORM_IS_UP)
                                                                               if (_BOUND) {

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
                                                                                   mPlatformManagerNodeMessenger.send(replyMsg);

                                                                                   // ---Initiate Receiving New Message Flag---//
                                                                                   android.os.Message msg = android.os.Message.obtain(null,
                                                                                           PlatformManagerNode.NEW_MESSAGE_FLAG_TEST, 0, 0);
                                                                                   msg.replyTo = mActivityMessenger;
                                                                                   mPlatformManagerNodeMessenger.send(msg);

                                                                               } else {
                                                                                   PrivateActivity.this.runOnUiThread(new Runnable() {
                                                                                       @Override
                                                                                       public void run() {
                                                                                           edit_txt.setText(chatMessage);
                                                                                       }
                                                                                   });
                                                                                   if (isInternetAvailable() && !custom.IS_NODE_BOOTSTRAP_()) {
                                                                                       displayCustomizedToast(PrivateActivity.this, "Something went wrong with the platform.\n Try sending message again");
                                                                                       startService(service_intent);
                                                                                   }

                                                                               }
                                                                       }


                                                                   }

                                                               } else {

                                                               }
                                                           } catch (Exception e) {

                                                               e.printStackTrace();
                                                               Intent intent = new Intent(PrivateActivity.this, ErrorActivity.class);
                                                               intent.putExtra("error", e.getLocalizedMessage());
                                                               startActivity(intent);
                                                           }

                                                       else {
                                                           displayCustomizedToast(PrivateActivity.this, "Something went wrong with the platform.\n Try sending message again");
                                                           custom.setST_UP(false);
                                                           SharedPreferences mPrefs = getSharedPreferences("myprofile", 0);
                                                           SharedPreferences.Editor ed = mPrefs.edit();
                                                           ed.putBoolean("IS_ST_UP", false);
                                                           ed.commit();
                                                       }
                                                   }
                                               else {
                                                   displayCustomizedToast(PrivateActivity.this, "Acive peers Not detected.\n Try sending message again");
                                               }


                                           }
                                       };

                                       Thread _mthread = new Thread(runnable_00);
                                       _mthread.start();
                                       if (!isInternetAvailable() && !custom.IS_NODE_BOOTSTRAP_()) {
                                           displayCustomizedToast(PrivateActivity.this, "Contexter needs connection.\n Message not sent\n Unable to reach the Platform");
                                       } else {
                                       }
                                   }
                               }

        );


        img_btn.setOnClickListener(new

                                           OnClickListener() {

                                               @Override
                                               public void onClick(View arg0) {
                                                   if (SELECTED_GROUP.equals("*ALL#")) {
                                                       displayCustomizedToast(PrivateActivity.this, "Invalid Option! Please Choose Your Group. ");
                                                   } else if (progress_bar.getVisibility() != View.GONE) {
                                                       Toast.makeText(getBaseContext(),
                                                               "Oops! Sending Picture...Just a momonent ",
                                                               Toast.LENGTH_SHORT).show();
                                                   } else {
                                                       Intent intent = new Intent();
                                                       intent.setType("image/*");
                                                       intent.setAction(Intent.ACTION_GET_CONTENT);
                                                       intent.putExtra("ACTIVITY", "PrivateActivity");
                                                       startActivityForResult(
                                                               Intent.createChooser(intent, "Select Picture"),
                                                               SELECT_IMAGE);
                                                   }

                                               }
                                           }

        );

        edit_txt.setOnTouchListener(new View.OnTouchListener()

                                    {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            //Hide Group buttons

                                            Runnable runnable = new Runnable() {
                                                @Override
                                                public void run() {
                                                    PrivateActivity.this.runOnUiThread(new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            img_btn_show.setVisibility(View.VISIBLE);
                                                            img_btn_hide.setVisibility(View.GONE);

                                                            if (!SELECTED_PEER.equalsIgnoreCase(""))
                                                                tv_1.setText(SELECTED_PEER.replace("@", " "));
                                                            LinearLayout ll = (LinearLayout) findViewById(R.id.linear2_pr_chat_);
                                                            ll.setVisibility(View.GONE);
                                                        }
                                                    });

                                                }
                                            };
                                            return false;

                                        }
                                    }

        );

        img_btn_show.setOnClickListener(new

                                                OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        Runnable runnable = new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                PrivateActivity.this.runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {

                                                                        img_btn_show.setVisibility(View.GONE);
                                                                        img_btn_hide.setVisibility(View.VISIBLE);
                                                                        if (!SELECTED_PEER.equalsIgnoreCase(""))
                                                                            tv_1.setText(SELECTED_PEER.replace("@", " "));
                                                                        LinearLayout ll = (LinearLayout) findViewById(R.id.linear2_pr_chat_);
                                                                        ll.setVisibility(View.VISIBLE);
                                                                        // SHOWGROUPS = false;
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
                                                }

        );

        img_btn_hide.setOnClickListener(new

                                                OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {


                                                        Runnable runnable = new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                PrivateActivity.this.runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {


                                                                        img_btn_show.setVisibility(View.VISIBLE);
                                                                        img_btn_hide.setVisibility(View.GONE);

                                                                        if (!SELECTED_PEER.equalsIgnoreCase(""))
                                                                            tv_1.setText(SELECTED_PEER.replace("@", " "));
                                                                        LinearLayout ll = (LinearLayout) findViewById(R.id.linear2_pr_chat_);
                                                                        ll.setVisibility(View.GONE);

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
                                                }

        );


        gv_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final ImageView image_msg = (ImageView) view
                        .findViewById(R.id.image_tosend_chat_screen);
                image_msg.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progress_bar.setVisibility(View.VISIBLE);
                        gv_1.setVisibility(View.GONE);

                        String _tag_ = image_msg.getTag().toString();
                        if (_tag_ != null)
                            if (_tag_.equalsIgnoreCase("~PLATFORM~MORE~")) {

                                Runnable runnable_004 = new Runnable() {
                                    @Override
                                    public void run() {

                                        THREAD_LENGTH_TO_DISP = THREAD_LENGTH_TO_DISP + 10;
                                        al_prchats.clear();
                                        _thread_0 = new PrivateChats(getBaseContext(), SELECTED_GROUP, SELECTED_PEER);
                                        ArrayList<String> temp_ar = _thread_0.getmPrivateChat(SELECTED_GROUP, THREAD_LENGTH_TO_DISP);
                                        if (_thread_0.getmPrivateChatSize() >= THREAD_LENGTH_TO_DISP)
                                            al_prchats.add("~PLATFORM~MORE~");

                                        TreeMap<Integer, String> seq_map = new TreeMap<Integer, String>();
                                        Time t = null;

                                        for (String _tmp_ : temp_ar) {
                                            String[] tmp_0_0 = _tmp_.split("~::~");
                                            if (tmp_0_0.length > 5 && tmp_0_0.length < 8) {
                                                seq_map.put(Integer.parseInt(tmp_0_0[6]), _tmp_);
                                            } else if (tmp_0_0.length > 8) {
                                                seq_map.put(Integer.parseInt(tmp_0_0[10]), _tmp_);
                                            } else
                                                al_prchats.add(_tmp_);
                                        }


                                        Set set = seq_map.entrySet();
                                        Iterator itr = set.iterator();

                                        Map.Entry me = null;
                                        while (itr.hasNext()) {
                                            me = (Map.Entry) itr.next();
                                            String str_tmp = String.valueOf(me.getValue());
                                            al_prchats.add(str_tmp);

                                        }

                                        PrivateActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                imageListAdapter = new ImageListAdapter(getBaseContext(), false,
                                                        CHAT_MODE, 0, al_prchats);
                                                gv_1.setAdapter(imageListAdapter);
                                                gv_1.setSelection(imageListAdapter.getCount() - 1);
                                            }
                                        });

                                        // Collections.reverse(al_chats);
                                        ShowMessages(al_prchats);

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


    }

    public String get_received_message() {
        return _received_message;
    }

    public void set_received_message(String _received_message) {
        this._received_message = _received_message;
    }

    public void ShowPeers(final ArrayList<String> ar_list) {

        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (ar_list.contains(PlatformManagerNode._mUCI))
                    ar_list.remove(PlatformManagerNode._mUCI);

                list_adapter = new ListAdapter(getApplicationContext(), -1,
                        CHAT_MODE, ar_list, 10);
                lv_1.setAdapter(list_adapter);
                LinearLayout ll = (LinearLayout) findViewById(R.id.linear2_pr_chat);

            }
        });
    }

    public void ShowMessages(final ArrayList<String> al) {

        Runnable _runnable_00 = new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if (al_prchats.size() > custom.getPub_message_thread_len() - 5 && custom.getPub_message_thread_len() - al_prchats.size() > 0) {
                            if (al_prchats.size() % 2 == 0)
                                displayCustomizedToast(PrivateActivity.this, "NOTE: Messages are going to purged after " + (custom.getPub_message_thread_len() - al_prchats.size()));
                        }
                        gv_1.setVisibility(View.VISIBLE);
                        progress_bar.setVisibility(View.GONE);

                        if (al.size() == 0)
                            al.add(" ~Welcome~ ! \n" +
                                    " You may start sending messages!\"");
                        imageListAdapter.setSetProfilePictureGone(false);
                        imageListAdapter.addItem(al);
                        Log.w("Main ...", "Show Messages");
                        gv_1.setAdapter(imageListAdapter);
                        gv_1.setSelection(imageListAdapter.getCount() - 1);


                        String _file_name_ = SELECTED_PEER;
                        if (_file_name_.contains("@")) {

                            _file_name_ = _file_name_.replace("@", ":");
                            _file_name_ = _file_name_.split(":")[0];
                        }
                        if (al.size() == 1) {
                            // if (al.get(0).toString().equalsIgnoreCase(" ~Welcome~ ! \n" +
                            //       " You may start sending messages!\"")) ;
                            //else
                            _thread_0.savePrivateChats(SELECTED_GROUP, _file_name_, "prchat", al);
                        } else
                            _thread_0.savePrivateChats(SELECTED_GROUP, _file_name_, "prchat", al);
                    }
                });
            }
        };
        Thread _mthread = new Thread(_runnable_00);
        _mthread.start();
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

    public ArrayList<String> Peers(String pr) {
        if (favorite_peers == null) {
            this.favorite_peers = new ArrayList<String>();
            if (!favorite_peers.contains(pr))
                favorite_peers.add(pr);
        } else if (!favorite_peers.contains(pr))
            favorite_peers.add(pr);
        return favorite_peers;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Intent intent= i.getData();

        try {
            if (requestCode == SELECT_IMAGE) {
                Uri selectedImageUri = data.getData();
                File file = new File(getPath(selectedImageUri));
                if (file.length() <= 1048576) {
                    // OI FILE Manager
                    filemanagerstring = selectedImageUri.getPath();

                    // MEDIA GALLERY
                    selectedImagePath = getPath(selectedImageUri);
                    uri_str_mimage_messsage = selectedImagePath.toString();
                    Toast.makeText(this.getApplicationContext(), uri_str_mimage_messsage,
                            Toast.LENGTH_SHORT).show();

                    Message = Message + "~::~" + getUniqueSerialNumber(3, 9);

                    // Message Format ---
                    // MESSAGETYPE~::~GROUPNAME~::~UCI~::~RECIEVER_UCI~::~MESSAGE~::~TIME~::~MESSAGESERIAL~::~PW
                    al_prchats.add(Constants.PRIVATEIMAGEFILE + "~::~" + SELECTED_GROUP + "~::~" + PlatformManagerNode._mUCI + "~::~" + SELECTED_PEER + "~::~" + selectedImagePath.toString() + "~::~" + format.format(new Date()) + "~::~" + getUniqueSerialNumber(3, 9) + "~::~" + "123456");
                    imageListAdapter.setMessage_imageURI(Uri.parse(selectedImagePath));
                    imageListAdapter.setSetProfilePictureGone(false);
                    imageListAdapter.addItem(al_prchats);
                    imageListAdapter.set_image_message_uri(Uri.parse(uri_str_mimage_messsage));

                    // Inform ---image is waiting to be sent
                    mPrefs = getSharedPreferences("myprofile", 0);
                    SharedPreferences.Editor ed = mPrefs.edit();
                    ed.putBoolean("_IMAGE_SEND_", true);
                    ed.putString("URI-IMAGE", uri_str_mimage_messsage);
                    ed.commit();

                    // show images
                    ShowMessages(al_prchats);
                }
            } else {
                Toast.makeText(this.getApplicationContext(), "Image size too big. It should be 1MB max.",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.action_chitchato:

                intent = new Intent(PrivateActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_advanced:

                intent = new Intent(PrivateActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(PrivateActivity.this, SensorReadingsActivity.class);
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
    public void onStop() {
        super.onStop();
        mPrefs = getSharedPreferences("cache", 0);
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("MA_VISITED", true);
        ed.commit();

        // Send Message to Stop the Platform
        /*Bundle b = new Bundle();
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
        }*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPrefs = getSharedPreferences("cache", 0);
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("MA_VISITED", true);
        ed.commit();


    }

    public int getUniqueSerialNumber(int init_index, int fin_index) {
        int unique_number = 0;
        long l = System.currentTimeMillis();
        long l_ = l / 1000;
        unique_number = Integer.parseInt(String.valueOf(l_).substring(init_index));
        return unique_number;
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

    // Check if the Internet is Available
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public File[] getPrivateMsgfromDir(String d) {
        File dir = new File(d);
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                // change this to prchat
                return pathname.getName().endsWith(".prchat");
            }
        });
        return files;

    }

    class ActivityHandler extends Handler {
        private final WeakReference<PrivateActivity> mActivity;
        ArrayList<String> temp_list_of_messages = new ArrayList<String>();

        public ActivityHandler(PrivateActivity activity) {
            mActivity = new WeakReference<PrivateActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case PlatformManagerNode.NEW_MESSAGE_FLAG: {
                    incoming_message = msg.getData().getString("NEW_MESSAGE_FLAG");
                    al_prchats.add(incoming_message);
                    ShowMessages(al_prchats);

                }
                case PlatformManagerNode.NEW_MESSAGE_FLAG_FA: {
                    incoming_message = msg.getData().getString("NEW_MESSAGE_FLAG_FA");
                    al_prchats.add(incoming_message);
                    ShowMessages(al_prchats);
                }
                case PlatformManagerNode.NEW_MESSAGE_FLAG_TEST: {
                    _received_message = msg.getData().getString("_mess");
                    set_received_message(msg.getData().getString("_mess"));
                    PrivateActivity.this.runOnUiThread(new Runnable() {

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
                            if (get_received_message().contains("[Resolve Response]")) {
                                {
                                    if (temp_list_of_messages.contains(get_received_message()) == false) {

                                        temp_list_of_messages.add(get_received_message());
                                    }
                                }
                            } else if (get_received_message().contains(":101~::~")) {
                                _tmp_msg_0_0 = get_received_message().substring(get_received_message().indexOf(":") + 1);

                                if (temp_list_of_messages.contains(_tmp_msg_0_0) == false) {
                                    if (!al_prchats.contains(_tmp_msg_0_0)) {
                                        al_prchats.add(_tmp_msg_0_0);
                                        ShowMessages(al_prchats);
                                        temp_list_of_messages.add(_tmp_msg_0_0);
                                    }
                                }
                            } else if (get_received_message().contains(":100~::~")) {

                            } else if (get_received_message().split("]").length > 1) {

                            } else {
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



}
