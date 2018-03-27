package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.adapters.ListAdapter;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerNode;
import se.sensiblethings.app.chitchato.extras.Constants;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.kernel.Groups;
import se.sensiblethings.disseminationlayer.communication.Message;

public class SearchResultsActivity extends Activity {

    TextView tv;
    Button btn, btn_;
    ProgressBar progress_bar;
    long l;
    String temp;
    ArrayList activenodes;
    String[] a;
    Message message;
    String nodes = "%";
    ListView lv_1, lv_2;
    ArrayList<String> search_result = new ArrayList<String>();
    ArrayList<Integer> chosen_index;
    protected Customizations custom;
    ArrayList<String> new_favorite_list = new ArrayList<String>();
    Groups groups;

    ListAdapter list_adapter;
    private String Message = "";
    private String incoming_message = "#";
    String _received_message = "#";
    String SEARCH_KEY = "Topper";//"Topper" -- for test
    boolean SEARCH_ON = true;

    // Date Parameters
    static String date;
    String date_pattern = "yyyy-MM-dd HH:mm:ss";
    Format format = new SimpleDateFormat(date_pattern);

    // Service Messagenger---
    Messenger mPlatformManagerNodeMessenger;
    android.os.Message msg = android.os.Message.obtain(null,
            PlatformManagerNode.NEW_MESSAGE_FLAG, 0, 0);
    android.os.Message msg_ = android.os.Message.obtain(null,
            PlatformManagerNode.NEW__GROUP_MESSAGE_FLAG, 0, 0);
    Intent service_intent;
    boolean _BOUND = false;


    Messenger mActivityMessenger = new Messenger(new ActivityHandler(this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        custom = new Customizations(this, -1);
        setContentView(custom.getSearchResultsScreen());
        setTitle("");
        tv = (TextView) findViewById(R.id.tv_search_text_results);
        btn = (Button) findViewById(R.id.btn_search_results);
        btn_ = (Button) findViewById(R.id.btn_exit_search_results);
        lv_1 = (ListView) findViewById(R.id.ListView1_search_results);
        progress_bar = (ProgressBar) findViewById(R.id.progressBar1);

        // Font face
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
        Typeface tf_pala = Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");
        tv.setTypeface(tf);
        btn.setTypeface(tf_pala);
        btn_.setTypeface(tf_pala);

        Bundle extras = getIntent().getExtras();
        SEARCH_KEY = "Contexter";
        chosen_index = new ArrayList<Integer>();

        search_result = extras.getStringArrayList("search_result");
        list_adapter = new ListAdapter(this, 0, true, search_result, 4);
        lv_1.setAdapter(list_adapter);

        //
        groups = new Groups(this, "", false);
        service_intent = new Intent(this, PlatformManagerNode.class);
        //if (!_BOUND) bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);
        //BKGroundTask bk_ground_task = new BKGroundTask(SEARCH_KEY);
        //bk_ground_task.execute("", "", "Result");

        lv_1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                TextView sub_item = (TextView) view
                        .findViewById(R.id.tv_lang_checkbox);
                CheckBox check_box = (CheckBox) view
                        .findViewById(R.id.lang_chat_radio);
                check_box.setChecked(!check_box.isChecked());
                if (check_box.isChecked()) {
                    chosen_index.add(position);
                    new_favorite_list.add((String) sub_item.getText());
                }
            }
        });

        lv_1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int position, long arg3) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });


        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                //  progress_bar.setVisibility(View.VISIBLE);

                for (String _group_ : new_favorite_list) {
                    groups.addGroup(_group_, false);
                    displayCustomizedToast(SearchResultsActivity.this, "Group Added");
                }
                new_favorite_list.clear();

            }
        });

        btn_.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SEARCH_ON = false;
                Intent intent = new Intent(SearchResultsActivity.this,
                        GroupContainerActivity.class);
                intent.putExtra("INDEX", "SEARCH");

                startActivity(intent);
            }
        });

    }

    public void ShowResults(ArrayList<String> al, String str) {
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

                intent = new Intent(SearchResultsActivity.this,
                        ChoicesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_advanced:

                intent = new Intent(SearchResultsActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_sensor_en:
                intent = new Intent(SearchResultsActivity.this,
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

    public void ShowMessages(final ArrayList<String> al) {

        SearchResultsActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                list_adapter.addItem(al);
                lv_1.setAdapter(list_adapter);

            }
        });
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mPlatformManagerNodeMessenger = new Messenger(service);
            _BOUND = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            Toast.makeText(SearchResultsActivity.this, "Disconnected",
                    Toast.LENGTH_SHORT).show();
            _BOUND = false;
        }
    };


    class ActivityHandler extends Handler {
        private final WeakReference<SearchResultsActivity> mActivity;
        int count = 1;
        ArrayList<String> temp_list_of_messages = new ArrayList<String>();

        public ActivityHandler(SearchResultsActivity activity) {
            mActivity = new WeakReference<SearchResultsActivity>(activity);
        }

        @Override
        public void handleMessage(final android.os.Message msg) {
            switch (msg.what) {
                case PlatformManagerNode.NEW_MESSAGE_FLAG: {
                    Toast.makeText(mActivity.get(),
                            "From Service: " + msg.getData().getString("NEW_MESSAGE_FLAG"),
                            Toast.LENGTH_SHORT).show();
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

                    SearchResultsActivity.this.runOnUiThread(new Runnable() {
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
                            if (get_received_message().contains(String.valueOf(Constants.SEARCHED))) {
                                String tmp = get_received_message();
                                String _tmp_0 = tmp.split(":")[1];
                                if (_tmp_0.startsWith(String.valueOf(Constants.SEARCHED))) {
                                    String[] _tmp_00 = _tmp_0.split("~::~");
                                    //new_favorite_list.add(tmp.substring(tmp.indexOf("~::~") + 1));
                                    new_favorite_list.add(_tmp_00[3] + "~::~" + _tmp_00[4] + "~::~" + _tmp_00[5] + "~::~" + _tmp_00[6] + "~::~" + _tmp_00[7] + "~::~" + _tmp_00[8] + "~::~" + _tmp_00[9] + "~::~" + _tmp_00[10]);
                                    if (!search_result.contains(_tmp_0)) {
                                        search_result.add(_tmp_00[3]);
                                    }
                                    ShowMessages(search_result);
                                }

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
        //String search_key = "#";

        public BKGroundTask(String str) {
            // this.search_key = str;
        }

        @Override
        protected String doInBackground(String... arg0) {
            this.flag = false;
            this.flag = false;
            //temp_ar = new ArrayList<String>();
            try {
                if (mPlatformManagerNodeMessenger == null) {
                    if (_BOUND) {
                        unbindService(mConnection);
                    }
                    bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);
                } else {
                    Bundle b = new Bundle();
                    b.putString("NEW_SERV8ICE_STOP_MESSAGE_FLAG", "STOP");
                    android.os.Message replyMsg = android.os.Message
                            .obtain(null,
                                    PlatformManagerNode.NEW__SERVICE_STOP_MESSAGE_FLAG,
                                    0, 0);
                    replyMsg.setData(b);
                    msg.replyTo = mActivityMessenger;
                    try {
                        if (mPlatformManagerNodeMessenger != null)
                            mPlatformManagerNodeMessenger.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }


                // Begin Searching
                SearchResultsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BKGroundTaskSearch search_bk_ground_task = new BKGroundTaskSearch();
                        search_bk_ground_task.execute("", "", "Result");
                    }
                });

                flag = true;


            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(SearchResultsActivity.this,
                        ErrorActivity.class);
                intent.putExtra("Error",
                        "Bootstrap Server Cannot be reached!\n Please Check Your Internet Connection");
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


    public class BKGroundTaskSearch extends AsyncTask<String, String, String> {
        boolean flag = false;

        @Override
        protected String doInBackground(String... arg0) {
            this.flag = false;
            ArrayList<String> temp_ar = new ArrayList<String>();
            try {
                //while(SEARCH_ON)
                if (mPlatformManagerNodeMessenger == null) {
                    //service_intent.putExtra("SERVICE_STARTED", true);
                    // startService(service_intent);
                    if (_BOUND) unbindService(mConnection);
                    bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);
                } else {
                    Bundle b = new Bundle();
                    Message = "SEARCH:::" + SEARCH_KEY;
                    b.putString("NEW_MESSAGE_FLAG", Message);
                    b.putString("NEW_MESSAGE_FLAG_FA", Message);
                    android.os.Message replyMsg = android.os.Message.obtain(null,
                            PlatformManagerNode.NEW_MESSAGE_FLAG, 0, 0);

                    replyMsg = android.os.Message
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
                }
                flag = true;

            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(SearchResultsActivity.this,
                        ErrorActivity.class);
                intent.putExtra("Error",
                        "Bootstrap Server Cannot be reached!\n Please Check Your Internet Connection");
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
}
