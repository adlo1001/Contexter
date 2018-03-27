package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerNode;
import se.sensiblethings.app.chitchato.extras.Busy;
import se.sensiblethings.app.chitchato.extras.Constants;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.DialogOne;
import se.sensiblethings.app.chitchato.kernel.Groups;
import se.sensiblethings.disseminationlayer.communication.Message;

public class SearchGroupActivity extends Activity {
    // Date Parameters
    static String date;
    // Service Messagenger---
    static Messenger mPlatformManagerNodeMessenger;
    protected Groups groups;
    protected SharedPreferences mPrefs;
    protected Customizations custom;
    TextView tv, ed_1;
    EditText ed_2, ed_3, ed_4, ed_5;
    Button btn;
    ProgressBar prog_bar;
    LinearLayout linear_layout;
    long l;
    String temp;
    ArrayList activenodes;
    String[] a;
    Message message;
    String nodes = "%";
    ListView lv_1, lv_2;
    GridView gv_1;
    String _received_message = "#";
    String date_pattern = "yyyy-MM-dd HH:mm:ss";
    Format format = new SimpleDateFormat(date_pattern);
    android.os.Message msg = android.os.Message.obtain(null,
            PlatformManagerNode.NEW_MESSAGE_FLAG, 0, 0);
    Messenger mActivityMessenger = new Messenger(new ActivityHandler(this));
    Intent service_intent;
    DialogOne dialogOne;
    boolean _BOUND = false;
    Long initial_time = 0L;
    Long end_time = 0L;
    String search_key_str;
    int count = 1; //test purpose
    boolean flag_search_ok = false; //test
    private ArrayList<String> temp_ar = new ArrayList<String>();
    private boolean SGA_VISISTED = false;
    private Busy busy, busy_;
    private String Message = "";
    private String incoming_message = "#";
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mPlatformManagerNodeMessenger = new Messenger(service);
            _BOUND = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            _BOUND = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        custom = new Customizations(this, -1);
        setContentView(custom.getSearchGroupsScreen());
        setTitle("");

        SGA_VISISTED = true;
        tv = (TextView) findViewById(R.id.tv_search_title);
        ed_1 = (EditText) findViewById(R.id.edt_group_name);
        ed_2 = (EditText) findViewById(R.id.edt_interest_search);
        ed_3 = (EditText) findViewById(R.id.edt_keyword_search);
        ed_4 = (EditText) findViewById(R.id.edt_leader_search);
        ed_5 = (EditText) findViewById(R.id.edt_vicinity_name_);
        btn = (Button) findViewById(R.id.btn_send_search);
        prog_bar = (ProgressBar) findViewById(R.id.progress_bar_1);
        linear_layout = (LinearLayout) findViewById(R.id.linear2_search);

        // Font face
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
        Typeface tf_pala = Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");

        tv.setTypeface(tf);
        ed_1.setTypeface(tf_pala);
        ed_2.setTypeface(tf_pala);
        ed_3.setTypeface(tf_pala);
        ed_4.setTypeface(tf_pala);
        ed_5.setTypeface(tf_pala);
        btn.setTypeface(tf_pala);

        dialogOne = new DialogOne(this, true, 13);
        service_intent = new Intent(this, PlatformManagerNode.class);
        bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);

        busy = new Busy(this, false, 6);

        Bundle extras = getIntent().getExtras();
        String TYPE_SEARCH = extras.getString("INDEX");
        if (TYPE_SEARCH.equals("ADV_SEARCH")) {

            tv.setText("Advanced Search");
            ed_1.setHint("Group Name");
            // ed_2.setTypeface(tf_pala);
            // ed_3.setTypeface(tf_pala);
            // ed_4.setTypeface(tf_pala);
            ed_2.setVisibility(View.VISIBLE);
            ed_3.setVisibility(View.VISIBLE);
            ed_4.setVisibility(View.VISIBLE);
            ed_5.setVisibility(View.VISIBLE);
        } else {
            tv.setText("Search");
            //ed_1.setHint("Group Name");
            ed_2.setVisibility(View.INVISIBLE);
            ed_3.setVisibility(View.INVISIBLE);
            ed_4.setVisibility(View.INVISIBLE);
            ed_5.setVisibility(View.INVISIBLE);
        }


        btn.setOnClickListener(new OnClickListener() {
                                   @Override
                                   public void onClick(View arg0) {

                                       if (isInternetAvailable()) {
                                           if (custom.isST_UP()) {
                                               try {
                                                   search_key_str = ed_1.getText().toString().trim();
                                                   initial_time = System.currentTimeMillis();
                                                   end_time = initial_time + 10000; // Search for 5 sec
                                                   // ShowResults(new ArrayList<String>(), search_key_str);
                                                   if (search_key_str.trim() != "" && !search_key_str.trim().isEmpty()) {
                                                       if (mPlatformManagerNodeMessenger != null) {
                                                           prog_bar.setVisibility(View.VISIBLE);
                                                           linear_layout.setVisibility(View.GONE);
                                                       }
                                                       Runnable runnable_0_0 = new Runnable() {
                                                           @Override
                                                           public void run() {
                                                               do {
                                                                   if (mPlatformManagerNodeMessenger == null) {
                                                                       if (_BOUND) unbindService(mConnection);
                                                                       bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);
                                                                   } else {
                                                                       if (count++ % 4 == 0) {
                                                                           Bundle b = new Bundle();
                                                                           Message = Constants.SEARCH + "~::~" + PlatformManagerNode._mGroup + "~::~" + PlatformManagerNode._mUCI + "~::~" + search_key_str + "~::~" + format.format(new Date());
                                                                           b.putString("NEW_MESSAGE_FLAG", Message);
                                                                           b.putString("NEW_MESSAGE_FLAG_FA", Message);
                                                                           android.os.Message replyMsg = android.os.Message.obtain(null,
                                                                                   PlatformManagerNode.NEW_MESSAGE_FLAG, 0, 0);
                                                                           replyMsg.setData(b);
                                                                           msg.replyTo = mActivityMessenger;
                                                                           try {
                                                                               mPlatformManagerNodeMessenger.send(replyMsg);
                                                                               // ---Initiate Receiving New Message Flag---//
                                                                               android.os.Message msg = android.os.Message.obtain(null,
                                                                                       PlatformManagerNode.NEW_MESSAGE_FLAG_TEST, 0, 0);
                                                                               msg.replyTo = mActivityMessenger;
                                                                               mPlatformManagerNodeMessenger.send(msg);
                                                                           } catch (RemoteException e) {
                                                                               e.printStackTrace();
                                                                           }
                                                                           System.out.println("__________________________" + _BOUND + "__" + count + " Searching " + search_key_str + "..." + PlatformManagerNode.ST_PLATFORM_IS_UP);
                                                                       }

                                                                   }
                                                                   initial_time = System.currentTimeMillis();
                                                                   // Toast.makeText(SearchGroupActivity.this,  count +" Searching " + search_key_str + "...", Toast.LENGTH_SHORT).show();

                                                                   try {
                                                                       Thread.sleep(200);
                                                                   } catch (InterruptedException e) {
                                                                       e.printStackTrace();
                                                                   }
                                                               }
                                                               while (initial_time < end_time - 2000)
                                                                       ; // Send Search Message for 4 Sec
                                                           }
                                                       };


                                                       Thread _mthread = new Thread(runnable_0_0);
                                                       _mthread.start();

                                                       //ShowResults(new ArrayList<String>(), search_key_str);


                                                   } else {
                                                       displayCustomizedToast(SearchGroupActivity.this, "Invalid Search Key!  Empty  not allowed. ");
                                                   }
                                               } catch (Exception e) {
                                               }
                                           } else {
                                               displayCustomizedToast(SearchGroupActivity.this, "Platform not started. Try another Bootstrap!");
                                           }
                                       } else {
                                           displayCustomizedToast(SearchGroupActivity.this, " Contexter needs connection. \n Platform not started         ");
                                       }
                                   }
                               }

        );
    }

    public void ShowResults(ArrayList<String> al, String str) {
        Intent intent = new Intent(SearchGroupActivity.this,
                SearchResultsActivity.class);
        intent.putStringArrayListExtra("search_result", al);
        intent.putExtra("SEARCH_KEY", str);
        intent.putExtra("XML", str);

        SearchGroupActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
            }
        });

        startActivity(intent);
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
                intent = new Intent(SearchGroupActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_advanced:
                intent = new Intent(SearchGroupActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_sensor_en:

                intent = new Intent(SearchGroupActivity.this,
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

    @Override
    public void onStop() {
        super.onStop();
        mPrefs = getSharedPreferences("cache", 0);
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("SGA_VISITED", true);
        ed.commit();
    }

    public String getIncoming_message() {
        return incoming_message;
    }

    public void setIncoming_message(String incoming_message) {
        this.incoming_message = incoming_message;
    }

    public String get_received_message() {
        return _received_message;
    }

    public void set_received_message(String _received_message) {
        this._received_message = _received_message;
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

    class ActivityHandler extends Handler {
        private final WeakReference<SearchGroupActivity> mActivity;
        int count = 1;
        ArrayList<String> temp_list_of_messages = new ArrayList<String>();

        public ActivityHandler(SearchGroupActivity activity) {
            mActivity = new WeakReference<SearchGroupActivity>(activity);
        }

        @Override

        public void handleMessage(final android.os.Message msg) {

            initial_time = System.currentTimeMillis();
            if (initial_time >= end_time) {
                if (temp_ar.size() == 0) temp_ar.add("No Results");
                //prog_bar.setVisibility(View.GONE);
                initial_time = 0L;
                end_time = 0L;
                // if (_BOUND) unbindService(mConnection);
                ShowResults(temp_ar, search_key_str);

            } else
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
                        SearchGroupActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    android.os.Message msg = android.os.Message.obtain(null,
                                            PlatformManagerNode.NEW_MESSAGE_FLAG_TEST, 0, 0);
                                    msg.replyTo = mActivityMessenger;
                                    mPlatformManagerNodeMessenger.send(msg);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }

                                if (get_received_message().contains(String.valueOf(Constants.SEARCHED))) {
                                    String tmp = get_received_message();
                                    String[] _tmp_00 = tmp.split("~::~");
                                    if (_tmp_00.length > 3)
                                        if (!temp_ar.contains(_tmp_00[3])) temp_ar.add(_tmp_00[3]);
                                }
                            }
                        });
                        try {
                            Thread.sleep(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    default: {
                        {
                        }

                    }
                }
        }
    }

    public class BKGroundTask extends AsyncTask<String, String, String> {

        boolean flag = false;

        @Override
        protected String doInBackground(String... arg0) {
            this.flag = false;
            //temp_ar = new ArrayList<String>();

            try {
                if (mPlatformManagerNodeMessenger == null) {
                    bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);
                } else {
                    Bundle b = new Bundle();
                    b.putString("NEW_SERV8ICE_STOP_MESSAGE_FLAG", "STOP");
                    android.os.Message replyMsg = android.os.Message
                            .obtain(null,
                                    PlatformManagerNode.NEW__SERVICE_STOP_MESSAGE_FLAG,
                                    0, 0);
                    replyMsg.setData(b);
                    // msg.replyTo = mActivityMessenger;
                    try {
                        if (mPlatformManagerNodeMessenger != null)
                            mPlatformManagerNodeMessenger.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                flag = true;

            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(SearchGroupActivity.this,
                        ErrorActivity.class);
                intent.putExtra("Error",
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

    }
}
