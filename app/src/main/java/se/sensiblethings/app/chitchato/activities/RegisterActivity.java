package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerNode;
import se.sensiblethings.app.chitchato.extras.Constants;
import se.sensiblethings.app.chitchato.extras.CustomImageView;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.DialogOne;
import se.sensiblethings.app.chitchato.kernel.Groups;
import se.sensiblethings.disseminationlayer.communication.Message;

public class RegisterActivity extends Activity {
    TextView tv_1, tv_2, tv_3, tv_4, tv_5;
    EditText ed_1, ed_2, ed_3, ed_4, ed_5;
    Spinner spinner;
    Button btn_1, btn_2;
    ImageButton img_btn_1;
    CustomImageView customImageView;
    ProgressBar prog_bar;
    LinearLayout linear_layout;
    long l;
    String temp;

    String[] a;

    Message message;
    String nodes = "%";
    ListView lv_1, lv_2;
    GridView gv_1;
    String data = null;

    private boolean RA_VISISTED = false;
    protected SharedPreferences mPrefs;
    protected Customizations custom;
    protected Groups groups;

    private static final int SELECT_PROFILE_IMAGE = 1;
    private String selectedImagePath;
    private String filemanagerstring;
    private String uri_str = "";
    private String nick_name_str = "";
    private String name_str = "";
    private int age_str = 0;
    private String sex_str = "";
    private String group_name_str = "";
    private String DEVICE_FINGER_PRINT = "UNKNOWN";
    private String Message = "";
    private String incoming_message = "#";
    String _received_message = "#";

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
    boolean _BOUND = false;
    Intent service_intent;
    DialogOne dialogOne;


    Messenger mActivityMessenger = new Messenger(
            new ActivityHandler(this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        custom = new Customizations(this, -1);

        mPrefs = getSharedPreferences("myprofile", 0);
        name_str = mPrefs.getString("peer_name", "Contexter_0_0");
        nick_name_str = mPrefs.getString("peer_nick_name", "contexter ");
        age_str = Integer.parseInt(mPrefs.getString("peer_age", "0"));
        sex_str = mPrefs.getString("peer_sex", "NA");
        uri_str = mPrefs.getString("URI", uri_str);
        group_name_str = mPrefs.getString("peer_group", group_name_str);

        setContentView(custom.getRegisterScreen());
        setTitle("");

        tv_1 = (TextView) findViewById(R.id.tv_register_title);
        ed_2 = (EditText) findViewById(R.id.edt_name_rgistr);
        ed_1 = (EditText) findViewById(R.id.edt_nick_name_rgistr);
        ed_4 = (EditText) findViewById(R.id.edt_age_rgistr);
        ed_3 = (EditText) findViewById(R.id.edt_sex_rgistr);
        ed_5 = (EditText) findViewById(R.id.edt_phone_rgistr);
        spinner = (Spinner) findViewById(R.id.spinner_group_id_rgistr);
        prog_bar = (ProgressBar) findViewById(R.id.progress_bar_1);
        linear_layout = (LinearLayout) findViewById(R.id.linear2_register);

        btn_1 = (Button) findViewById(R.id.btn_send_register);
        img_btn_1 = (ImageButton) findViewById(R.id.img_btn_profile_PIC);
        customImageView = (CustomImageView) findViewById(R.id.cust_image_view_profile_PIC);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
        Typeface tf_pala = Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");

        // Font Face
        tv_1.setTypeface(tf);

        ed_1.setTypeface(tf_pala);
        ed_2.setTypeface(tf_pala);
        ed_3.setTypeface(tf_pala);
        ed_4.setTypeface(tf_pala);
        ed_5.setTypeface(tf_pala);

        btn_1.setTypeface(tf_pala);
        ed_2.setText(nick_name_str);
        ed_1.setText(name_str);
        ed_4.setText("0");
        ed_3.setText(sex_str);
        img_btn_1.setImageURI(Uri.parse(uri_str));

        if (!uri_str.contains("#Nothing#")) {
            Uri uri = Uri.parse(uri_str);
            img_btn_1.setImageURI(uri);
            customImageView.setImageURI(uri);

        }

        File _file_00 = new File(uri_str);
        if (_file_00.exists()) {
            customImageView.setTag(uri_str);
            FileInputStream _fis_00 = null;
            try {
                _fis_00 = new FileInputStream(_file_00);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (!isJPEGValid(_fis_00))
                customImageView.setTag("profile_image_android");
        } else
            customImageView.setTag("profile_image_android");


        if (customImageView.getTag().equals("profile_image_android"))
            customImageView.setImageResource(R.drawable.profile_image_android);
        dialogOne = new DialogOne(this, true, 13);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item_one);
        // Get the Favorite Groups
        Groups groups = new Groups(this);
        final ArrayList<String> al_ = groups.getmGroups();
        HashSet hs_ = new HashSet();

        //Default Group-- Contexter
        adapter.add("Contexter");

        for (String temp : al_) {
            if (temp.contains(":::")) {
                String _temp_0 = temp.split(":::")[1];
                if (_temp_0.contains("~::~")) {
                    String[] _temp_ = _temp_0.split("~::~");
                    if (_temp_.length > 4) {
                        String group_name = _temp_[0];
                        String group_interest = _temp_[1];
                        String group_leader = _temp_[2];
                        String group_age_limit = _temp_[4];
                        String CreationDate = _temp_[5];
                        //
                        temp = group_name;
                    }
                }
            }
            hs_.add(temp);
        }
        adapter.addAll(hs_);
        if (al_.size() == 0) {
            if (custom.getLanguage() == 1) {
                displayCustomizedToast(RegisterActivity.this, getResources().getString(R.string.notification_three_sv));
            } else if (custom.getLanguage() == 2) {
                displayCustomizedToast(RegisterActivity.this, getResources().getString(R.string.notification_three_sp));
            } else if (custom.getLanguage() == 3) {
                displayCustomizedToast(RegisterActivity.this, getResources().getString(R.string.notification_three_pr));
            } else if (custom.getLanguage() == 4) {
                displayCustomizedToast(RegisterActivity.this, getResources().getString(R.string.notification_three_fr));
            } else if (custom.getLanguage() == 5) {
                displayCustomizedToast(RegisterActivity.this, getResources().getString(R.string.notification_three_am));
            } else {
                displayCustomizedToast(RegisterActivity.this, getResources().getString(R.string.notification_three_en));
            }
        }

        // Populate Spinner
        spinner.setAdapter(adapter);
        //
        // mPlatformManagerNodeMessenger = MainActivity.mPlatformManagerNodeMessenger;
        service_intent = new Intent(this, PlatformManagerNode.class);

        //----Bind Service ---
        bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);
        btn_1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String nickname_ptr = "\\S+";
                final Pattern pattern_nick = Pattern.compile(nickname_ptr);
                String age = "([01]?\\d\\d?|2[0-4]\\d|15[0-5])$";
                final Pattern pattern = Pattern.compile(age);
                Matcher matcher = pattern.matcher(ed_4.getText().toString().trim());
                Matcher matcher_nick = pattern_nick.matcher(ed_1.getText().toString().trim());
                if (_BOUND)
                    if (matcher.matches()) {
                        if (matcher_nick.matches()) {
                            if (al_.size() > 0)
                                try {
                                    // start service
                                    if (mPlatformManagerNodeMessenger == null) {
                                        //prog_bar.setVisibility(View.VISIBLE);
                                        linear_layout.setVisibility(View.GONE);
                                        bindService(service_intent, mConnection,
                                                Context.BIND_AUTO_CREATE);
                                        displayCustomizedToast(RegisterActivity.this, "Error - Platform is out of Sync");
                                    } else {
                                        if ((ed_1.getText().toString().trim() != "")
                                                && (ed_2.getText().toString().trim() != "")
                                                && (ed_3.getText().toString().trim() != "")
                                                && (ed_3.getText().toString().trim() != "")) {

                                            // String Containing new peer information.
                                            data = "~::~" + spinner.getSelectedItem() + "~::~"
                                                    + ed_1.getText().toString() + "~::~"
                                                    + ed_2.getText().toString() + "~::~"
                                                    + ed_3.getText().toString() + "~::~"
                                                    + ed_4.getText().toString() + "~::~";

                                            nick_name_str = ed_1.getText().toString();
                                            name_str = ed_2.getText().toString();
                                            age_str = Integer.parseInt(ed_4.getText().toString());
                                            sex_str = ed_3.getText().toString();
                                            group_name_str = spinner.getSelectedItem()
                                                    .toString();

                                            // Save the registration information in the preferences
                                            mPrefs = getSharedPreferences("myprofile", 0);
                                            SharedPreferences.Editor ed = mPrefs.edit();
                                            ed.putString("peer_name", name_str);
                                            ed.putString("peer_nick_name", nick_name_str);
                                            ed.putString("peer_sex", sex_str);
                                            ed.putString("peer_age", age_str + "");
                                            ed.putString("peer_group", group_name_str);
                                            ed.putString("URI", uri_str);
                                            ed.commit();

                                            // update the customization
                                            custom.setPeer_name(name_str);
                                            custom.setPeer_nick_name(nick_name_str);
                                            custom.setPeer_sex(sex_str);
                                            custom.setProfile_image_uri(uri_str);
                                            custom.setPeer_age(String.valueOf(age_str));
                                            custom.setLOCAL_PEER_REGISTERED(true);
                                            ed.putBoolean("LOCAL_PEER_REGISTERED", true);

                                            //Messsage Format
                                            // MESSAGETYPE~::~GROUPNAME~::~UCI~::~MESSAGE~::~TIME
                                            Message = Constants.REGISTER + "~::~" + group_name_str + "~::~" + nick_name_str + "~::~" + name_str + "~::~" + format.format(new Date());
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
                                            displayCustomizedToast(RegisterActivity.this, "Join Request Delivered.");

                                        } else if (ed_1.getText().toString().trim() == "") {
                                            displayCustomizedToast(RegisterActivity.this, "Group Name Invalid!");
                                        } else if (ed_2.getText().toString().trim() == "") {
                                            displayCustomizedToast(RegisterActivity.this, "Invalid Group Interest!");
                                        } else if (ed_3.getText().toString().trim() == "") {
                                            displayCustomizedToast(RegisterActivity.this, "Age Limit Invalid!");
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Intent intent = new Intent(RegisterActivity.this,
                                            ErrorActivity.class);
                                    intent.putExtra("error",
                                            e.getLocalizedMessage());
                                }
                        } else {
                            displayCustomizedToast(RegisterActivity.this, "Oops .. Invalid Nickname! Use single word");
                        }
                    } else {
                        displayCustomizedToast(RegisterActivity.this, "Oops .. Invalid Age Value!");
                    }
                else {
                    displayCustomizedToast(RegisterActivity.this, "Oops .. Something Went wrong!");
                }


            }


        });


        img_btn_1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    intent.putExtra("ACTIVITY", "RegisterActivity");

                    startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            SELECT_PROFILE_IMAGE);
                    mPrefs = getSharedPreferences("myprofile", 0);
                    SharedPreferences.Editor ed = mPrefs.edit();
                    ed.putString("URI", uri_str);
                    ed.commit();
                } catch (Exception e) {
                    Intent intent = new Intent(RegisterActivity.this,
                            ErrorActivity.class);
                    intent.putExtra("error",
                            e.getLocalizedMessage());
                    startActivity(intent);
                }

            }

        });

        customImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.putExtra("ACTIVITY", "RegisterActivity");

                    startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            SELECT_PROFILE_IMAGE);
                    mPrefs = getSharedPreferences("myprofile", 0);
                    SharedPreferences.Editor ed = mPrefs.edit();
                    ed.putString("URI", uri_str);
                    ed.commit();

                } catch (Exception e) {
                    Intent intent = new Intent(RegisterActivity.this,
                            ErrorActivity.class);

                    intent.putExtra("error",
                            R.string.notification_four_en);

                    startActivity(intent);
                }

            }
        });


    }

    //Get Path for Home Folder
    public static String getAppFolder(String file_name) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/";
    }

    // Set Path for Home Folder
    public void setAppFolder(String file_name) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato");
        if (!file.exists()) {
            new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato").mkdir();
        } else {

        }
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

                intent = new Intent(RegisterActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_advanced:

                intent = new Intent(RegisterActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(RegisterActivity.this,
                        SensorReadingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Intent intent= i.getData();

        if (requestCode == SELECT_PROFILE_IMAGE) {
            if (data == null) {
            } else {
                Uri selectedImageUri = data.getData();
                // OI FILE Manager
                filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                selectedImagePath = getPath(selectedImageUri);

                uri_str = selectedImagePath.toString();
                Toast.makeText(this.getApplicationContext(), selectedImagePath,
                        Toast.LENGTH_SHORT).show();

            }
        } else if (requestCode == 0) {
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
        mPrefs = getSharedPreferences("cache", 0);
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("RA_VISITED", true);
        ed.commit();
        if (_BOUND) {
            unbindService(mConnection);
        }

    }


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


    class ActivityHandler extends Handler {
        private final WeakReference<RegisterActivity> mActivity;
        int count = 1;
        ArrayList<String> temp_list_of_messages = new ArrayList<String>();

        public ActivityHandler(RegisterActivity activity) {
            mActivity = new WeakReference<RegisterActivity>(activity);
        }

        @Override
        public void handleMessage(final android.os.Message msg) {
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
                    RegisterActivity.this.runOnUiThread(new Runnable() {
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

                            if (get_received_message().contains(String.valueOf(Constants.REGISTERED))) {
                                String tmp = get_received_message();
                                String[] _tmp_00 = tmp.split("~::~");
                                MessageNotifier(null, "Group Join Request Accepted!" + _tmp_00[1], "", "", true, 3);
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

    public class BKGroundTask extends AsyncTask<String, Void, String> {
        boolean flag = false;
        private int response_code = 0;
        String register_str = "#";

        public BKGroundTask(String str) {
            register_str = str;
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                if (mPlatformManagerNodeMessenger == null) {
                    service_intent.putExtra("SERVICE_STARTED", true);
                    bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);
                }
                flag = true;

            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(RegisterActivity.this,
                        ErrorActivity.class);
                intent.putExtra("Error",
                        "Bootstrap Server Cannot be reached!\n Check Your Connection");
                startActivity(intent);

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {

                RegisterActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        prog_bar.setVisibility(View.GONE);
                        // linear_layout.setVisibility(0);
                    }
                });

                flag = false;

            } catch (Exception e) {
                // e.printStackTrace();
            }
        }

    }


    public boolean isJPEGValid(FileInputStream fis) {
        boolean isValid = false;

        try {

            //new ImageView(file);

            // BitmapFactory.Options options = new BitmapFactory.Options();
            // options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            // options.inDither = false;
            Bitmap bi = BitmapFactory.decodeStream(fis);
            bi = bi.copy(Bitmap.Config.ARGB_8888, true);
            isValid = true;
        } catch (Exception e) {
            isValid = false;
        }

        return isValid;
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
        //id-- 0--Platform status ~~~ 1
        _notification_manager.notify(id, _mNotification);
        //   _notification_manager.cancel(0);

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
