package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerNode;
import se.sensiblethings.app.chitchato.context.ContextManager;
import se.sensiblethings.app.chitchato.extras.Constants;
import se.sensiblethings.app.chitchato.extras.CustomImageView;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.DialogOne;
import se.sensiblethings.app.chitchato.kernel.ChitchatoGroups;
import se.sensiblethings.app.chitchato.kernel.Groups;
import se.sensiblethings.app.chitchato.kernel.PublicChats;
import se.sensiblethings.disseminationlayer.communication.Message;

public class EditProfileActivity extends Activity {
    TextView tv_1, tv_2, tv_3, tv_4, tv_5, tv_6, tv_7;
    EditText edt_1, edt_2, edt_3, edt_4, edt_5;
    Button btn_1;
    static public ImageView image_view;
    static public ImageButton img_btn_1;
    CustomImageView cust_image_view, cust_image_view_orig;
    Spinner spinner;
    long l;
    String temp;
    ArrayList activenodes;
    String[] a;

    Message message;
    String nodes = "%";
    GridView gv_1;
    protected SharedPreferences mPrefs, mPrefs_;
    protected boolean CHAT_MODE = true;
    private ChitchatoGroups groups;
    private Customizations custom;
    private String peer_name = "";
    private String peer_nick_name = "";
    private int peer_age = -0;
    private String peer_sex = "M";
    private String uri_str = "#Nothing#";
    private Uri selectedImageUri = null;
    private String group_name_str = "#Nothing#";
    // private Busy busy;

    private static final int SELECT_PROFILE_IMAGE = 1;
    private String selectedImagePath = "#Nothing#";
    private String filemanagerstring = "#Nothing#";
    private String groupsCreatedByLocalPeer = "";
    private String DEVICE_FINGER_PRINT = "UNKNOWN";
    private static String path = "";

    DialogOne dialogOne;


    private String Message = "";
    private String incoming_message = "#";
    String _received_message = "#";

    // Service Messagenger---
    static Messenger mPlatformManagerNodeMessenger;
    android.os.Message msg = android.os.Message.obtain(null,
            PlatformManagerNode.NEW_MESSAGE_FLAG, 0, 0);
    // Messenger mActivityMessenger = new Messenger(new ActivityHandler(this));
    Intent service_intent;
    boolean _BOUND = false;

    // Location Information--
    protected ContextManager context_manager;
    // Sensor Manager
    SensorManager mSensorManager;
    String device_uniqe_number = "00000";
    PublicChats _thread_0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("myprofile", 0);
        // CHAT_MODE = mPrefs.getBoolean("OChat", true);
        peer_name = mPrefs.getString("peer_name", "unknown Chatter");
        peer_nick_name = mPrefs.getString("peer_nick_name", "unknown_nickname");
        peer_age = Integer.parseInt(mPrefs.getString("peer_age", "0"));
        peer_sex = mPrefs.getString("peer_sex", "unknown");
        uri_str = mPrefs.getString("URI", uri_str);
        group_name_str = mPrefs.getString("peer_group", group_name_str);

        mPrefs_ = getSharedPreferences("mygroups_cache", 0);
        groupsCreatedByLocalPeer = mPrefs.getString("groups", "");

        custom = new Customizations(this, -1);
        setContentView(custom.getEditProfileScreen());

        setTitle("");
        btn_1 = (Button) findViewById(R.id.button_refresh_my_profile);
        img_btn_1 = (ImageButton) findViewById(R.id.img_btn_profile_PIC);
        cust_image_view = (CustomImageView) findViewById(R.id.cust_image_view_profile_PIC);
        cust_image_view.setTag("profile_image_android");
        edt_1 = (EditText) findViewById(R.id.edt_peerPro_name);
        edt_2 = (EditText) findViewById(R.id.edt_peerPro_nick);
        edt_3 = (EditText) findViewById(R.id.edt_peerPro_sex);
        edt_4 = (EditText) findViewById(R.id.edt_peerPro_age);
        edt_5 = (EditText) findViewById(R.id.edt_peerPro_phone);
        tv_1 = (TextView) findViewById(R.id.tv_localPeer);
        tv_2 = (TextView) findViewById(R.id.tv_peerPro_name);
        tv_3 = (TextView) findViewById(R.id.tv_peerPro_nick);
        tv_4 = (TextView) findViewById(R.id.tv_peerPro_age);
        tv_5 = (TextView) findViewById(R.id.tv_peerPro_sex);
        tv_6 = (TextView) findViewById(R.id.tv_peerPro_group);
        tv_7 = (TextView) findViewById(R.id.tv_peerPro_phone);

        spinner = (Spinner) findViewById(R.id.spinner_my_profile_portofolio);
        dialogOne = new DialogOne(this, true, 13);

        // Setup home folder for the files-- if it doesnt exist
        setAppFolder("");

        if (peer_nick_name.equalsIgnoreCase("unknown_nickname")) {
            {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String fl_nm = telephonyManager.getSimSerialNumber();
                if (fl_nm == null) {
                    fl_nm = "00000";
                }
                PublicChats _thread_0 = new PublicChats(this, fl_nm, "nicks");
                ArrayList<String> temp_ar = _thread_0.getmPublicChat("*ALL#");
                for (String tmp_000 : temp_ar) {
                    if (tmp_000.contains(":::") && tmp_000.contains("~::~")) {
                        String t = tmp_000.split(":::")[1];
                        String[] tt = t.split("~::~");

                        peer_name = tt[1];
                        peer_nick_name = tt[0];
                        peer_age = Integer.parseInt(tt[3]);
                        peer_sex = tt[2];
                        setPeer_nick_name(peer_nick_name);
                        setPeer_name(peer_name);
                        setPeer_age(peer_age);
                        setPeer_sex(peer_sex);
                        uri_str = tt[4];

                        edt_1.setText(peer_name);
                        edt_2.setText(peer_nick_name);
                        edt_4.setText(peer_age + "");
                        edt_3.setText(peer_sex);
                    }
                }


            }

        } else {
            edt_1.setText(peer_name);
            edt_2.setText(peer_nick_name);
            edt_4.setText(peer_age + "");
            edt_3.setText(peer_sex);
        }

        img_btn_1.setImageURI(Uri.parse(uri_str));
        cust_image_view.setImageURI(Uri.parse(uri_str));

        System.out.println("Profile Image Path:" + uri_str);
        if (!uri_str.contains("#Nothing#")) {
            Uri uri = Uri.parse(uri_str);
            img_btn_1.setImageURI(uri);
            cust_image_view.setImageURI(uri);
            cust_image_view.setTag(uri);
        }
        File _file_00 = new File(uri_str);
        if (_file_00.exists()) {
            FileInputStream _fis_00 = null;
            try {
                _fis_00 = new FileInputStream(_file_00);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (!isJPEGValid(_fis_00))
                cust_image_view.setTag("profile_image_android");
        } else
            cust_image_view.setTag("profile_image_android");

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
        Typeface tf_pala = Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");

        // Font Face
        tv_1.setTypeface(tf);

        edt_1.setTypeface(tf);
        edt_2.setTypeface(tf);
        edt_3.setTypeface(tf);
        edt_3.setTypeface(tf);
        edt_4.setTypeface(tf);

        tv_2.setTypeface(tf);
        tv_3.setTypeface(tf);
        tv_4.setTypeface(tf);
        tv_5.setTypeface(tf);
        tv_6.setTypeface(tf);
        tv_7.setTypeface(tf);

        btn_1.setTypeface(tf_pala);


        if (cust_image_view.getTag().equals("profile_image_android"))
            cust_image_view.setImageResource(R.drawable.profile_image_android);


        // Application Directory
        path = getAppFolder("");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item_one);
        // Get the Favorite Groups
        Groups groups = new Groups(this);
        final ArrayList<String> al_ = groups.getmGroups();

        _thread_0 = new PublicChats(getBaseContext(), ".nicks");


        // Services
        service_intent = new Intent(this, PlatformManagerNode.class);
        bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        device_uniqe_number = telephonyManager.getSimSerialNumber();
        if (device_uniqe_number == null) device_uniqe_number = "00000";
        // displayCustomizedToast(this, "Device Telephone No:" + device_uniqe_number);

        //Default Group-- Contexter
        adapter.add("Contexter");

        for (String temp : al_) {

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
                        //
                        temp = group_name;
                    }
                }
            }
            adapter.add(temp);
        }
        // Populate Spinner
        spinner.setAdapter(adapter);
        if (al_.size() == 0) {


            if (custom.getLanguage() == 1) {

                displayCustomizedToast(EditProfileActivity.this, getResources().getString(R.string.notification_three_sv));
            } else if (custom.getLanguage() == 2) {
                displayCustomizedToast(EditProfileActivity.this, getResources().getString(R.string.notification_three_sp));
            } else if (custom.getLanguage() == 3) {
                displayCustomizedToast(EditProfileActivity.this, getResources().getString(R.string.notification_three_pr));
            } else if (custom.getLanguage() == 4) {
                displayCustomizedToast(EditProfileActivity.this, getResources().getString(R.string.notification_three_fr));
            } else if (custom.getLanguage() == 5) {
                displayCustomizedToast(EditProfileActivity.this, getResources().getString(R.string.notification_three_am));
            } else {
                displayCustomizedToast(EditProfileActivity.this, getResources().getString(R.string.notification_three_en));
            }


        }
        String age = "([01]?\\d\\d?|2[0-4]\\d|15[0-5])$";
        final Pattern pattern = Pattern.compile(age);

        // nickname pattern-- a single word-- uci is derived frm nickname
        //String nickname_ptr = "\\\\s+([a-zA-Z]+)";
        String nickname_ptr = "\\S+";
        final Pattern pattern_nick = Pattern.compile(nickname_ptr);

        // DEVICE_FINGER_PRINT = Build.FINGERPRINT;
        // DEVICE_FINGER_PRINT = DEVICE_FINGER_PRINT.replace("/", "~");
        btn_1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    Matcher matcher = pattern.matcher(edt_4.getText().toString().trim());
                    Matcher matcher_nick = pattern_nick.matcher(edt_2.getText().toString().trim());
                    if (matcher.matches()) {
                        if (matcher_nick.matches()) {
                            if (edt_3.getText().toString().equalsIgnoreCase("M") || edt_3.getText().toString().equalsIgnoreCase("F") || edt_3.getText().toString().equalsIgnoreCase("NA")) {
                                peer_name = edt_1.getText().toString();
                                peer_nick_name = edt_2.getText().toString();
                                peer_sex = edt_3.getText().toString();
                                group_name_str = spinner.getSelectedItem().toString();
                                peer_age = Integer.parseInt(edt_4.getText().toString());
                                if (peer_name.contains("unknown Node")
                                        || peer_nick_name.contains("unknown nickname")
                                        || peer_sex.contains("unknown")) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Oops .. Profile Not Registered! Fill all the fields.",
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    mPrefs = getSharedPreferences("myprofile", 0);
                                    SharedPreferences.Editor ed = mPrefs.edit();
                                    ed.putString("peer_name", peer_name);
                                    ed.putString("peer_nick_name", peer_nick_name);
                                    ed.putString("peer_sex", peer_sex);
                                    ed.putString("peer_age", peer_age + "");
                                    ed.putString("URI", uri_str);
                                    ed.putBoolean("PROFILE_CHANGED", true);

                                    custom.setPeer_name(peer_name);
                                    custom.setPeer_nick_name(peer_nick_name);
                                    custom.setPeer_sex(peer_sex);
                                    custom.setProfile_image_uri(uri_str);
                                    custom.setPeer_age(peer_age + "");
                                    custom.setProfile_group_uci(group_name_str);


                                    // Location Information//
                                    mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                                    context_manager = new ContextManager(getBaseContext(),
                                            peer_nick_name, mSensorManager);
                                    context_manager.updateContextData();
                                    String user_location = context_manager.getUserContext().getLocation();

                                    PublicChats _thread_0 = new PublicChats(getBaseContext(), device_uniqe_number, "nicks");
                                    ArrayList<String> temp_ar = _thread_0.getmPublicChat("");
                                    String reg_str = device_uniqe_number + ":::" + peer_nick_name + "~::~" + peer_name + "~::~" + peer_sex + "~::~" + peer_age + "~::~" + uri_str;
                                    if (!temp_ar.contains(reg_str)) {
                                        temp_ar.add(reg_str);
                                        _thread_0.saveADVs(device_uniqe_number, "nicks", temp_ar);
                                    }

                                    // Just for test
                                    displayCustomizedToast(EditProfileActivity.this, "User Location:" + user_location);


                                    custom.setLOCAL_PEER_REGISTERED(true);
                                    ed.putBoolean("LOCAL_PEER_REGISTERED", true);
                                    ed.commit();


                                    if (_BOUND) {
                                        Runnable runnable_00 = new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(1000);
                                                    // update other nodes about the profile image change
                                                    Message = Constants.PROFILEIMAGEFILED + "~::~" + MainActivity.SELECTED_GROUP + "~::~" + selectedImagePath + "~::~" + getUniqueSerialNumber(3, 9) + "~::~" + "123456";
                                                    Bundle b = new Bundle();
                                                    //al_chats.add(Message + "");
                                                    b.putString("NEW_IMAGE_MESSAGE_FLAG", Message);
                                                    android.os.Message replyMsg = android.os.Message
                                                            .obtain(null,
                                                                    PlatformManagerNode.NEW_IMAGE_MESSAGE_FLAG,
                                                                    0, 0);
                                                    replyMsg.setData(b);

                                                    mPlatformManagerNodeMessenger.send(replyMsg);
                                                } catch (RemoteException e) {
                                                    e.printStackTrace();
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        Thread _mthread = new Thread(runnable_00);
                                        _mthread.start();

                                    } else
                                        displayCustomizedToast(EditProfileActivity.this, "Profile Picture Saved. Failed to put on the platform");
                                    if (al_.size() == 0) {
                                        if (adapter.getCount() == 1) {
                                            displayCustomizedToast(EditProfileActivity.this, "Search and Import Groups! Registered on a Default Group");
                                        }
                                    } else
                                        displayCustomizedToast(EditProfileActivity.this, "Profile Registered!");

                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Oops .. Invalid  Value. use M , F or NA!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Oops .. Invalid Nickname! Use single word",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Oops .. Invalid Age Value!",
                                Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    displayCustomizedToast(EditProfileActivity.this, "Oops .. Profile Not Registered! \n\n  ");
                }
            }
        });

        img_btn_1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra("ACTIVITY", "MainActivity");
                startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"),
                        SELECT_PROFILE_IMAGE);
            }
        });

        cust_image_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra("ACTIVITY", "MainActivity");
                startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"),
                        SELECT_PROFILE_IMAGE);

            }
        });

        img_btn_1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                DialogOne dialog_one = new DialogOne(EditProfileActivity.this, false, 15);
                dialog_one.setDialog_message("Do you want to remove profile picture?");
                dialog_one.show();

                if (dialog_one.isDialog_15_done()) {
                    img_btn_1.setBackground(getResources().getDrawable(R.drawable.profile_image));
                    img_btn_1.setImageURI(Uri.parse("#Nothing#"));
                    dialog_one.setDialog_15_done(false);
                }

                return false;
            }
        });

    }

    //Get Path for Home Folder
    public static String getAppFolder(String file_name) {
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

                intent = new Intent(EditProfileActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_advanced:

                intent = new Intent(EditProfileActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(EditProfileActivity.this,
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == SELECT_PROFILE_IMAGE) {
                if (data == null) {
                } else {
                    selectedImageUri = data.getData();
                    // OI FILE Manager
                    filemanagerstring = selectedImageUri.getPath();
                    // MEDIA GALLERY
                    selectedImagePath = getPath(selectedImageUri);
                    // Toast.makeText(this.getApplicationContext(), selectedImageUri.toString(),
                    //       Toast.LENGTH_SHORT).show();

                    // uri_str = selectedImageUri.toString();
                    img_btn_1.setImageURI(selectedImageUri);
                    cust_image_view.setImageURI(selectedImageUri);
                    ArrayList<byte[]> bt = getImage(selectedImagePath);
                    saveImage(bt, peer_nick_name + "___miun.se__mobi", "group_name_str");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error! Upload failed", Toast.LENGTH_LONG).show();
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


    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor ed = mPrefs.edit();
        String language = "En";

        try {
            peer_name = edt_1.getText().toString();
            peer_nick_name = edt_2.getText().toString();
            peer_sex = edt_3.getText().toString();
            peer_age = Integer.parseInt(edt_4.getText().toString());

            mPrefs = getSharedPreferences("myprofile", 0);
            ed.putString("peer_name", peer_name);
            ed.putString("peer_nick_name", peer_nick_name);
            ed.putString("peer_sex", peer_sex);
            ed.putString("peer_age", peer_age + "");
            //   ed.putString("URI", uri_str);
           // ed.commit();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
    }


    public void setPeer_name(String peer_name) {
        this.peer_name = peer_name;
    }

    public void setPeer_nick_name(String peer_nick_name) {
        this.peer_nick_name = peer_nick_name;
    }

    public void setPeer_age(int peer_age) {
        this.peer_age = peer_age;
    }

    public void setPeer_sex(String peer_sex) {
        this.peer_sex = peer_sex;
    }

    public void setImage_view(ImageView image_view) {
        this.image_view = image_view;
    }

    public String getPeer_name() {
        return peer_name;
    }

    public String getPeer_nick_name() {
        return peer_nick_name;
    }

    public int getPeer_age() {
        return peer_age;
    }

    public String getPeer_sex() {
        return peer_sex;
    }

    public ImageView getImage_view() {
        return image_view;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public class GetProfile {
        GetProfile() {
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

        public String getPeer_name() {
            return peer_name;
        }

        public String getPeer_nick_name() {
            return peer_nick_name;
        }

        public int getPeer_age() {
            return peer_age;
        }

        public String getPeer_sex() {
            return peer_sex;
        }

        public ImageView getImage_view() {
            return image_view;
        }

    }


    public void saveImage(ArrayList<byte[]> array_byte, String image_name, String group_name) {
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
        //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] image_file;
        byte[] readData = new byte[1024];
        try {
            fout = new FileOutputStream(file);
            fout_dp = new FileOutputStream(file_dp);
            uri_str = destName;
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
                    ;
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Unsupported Image Format ", Toast.LENGTH_LONG).show();
        }
        return Image_file;
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

    public void displayCustomizedToast(Context _context_, String message) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_ = inflater.inflate(R.layout.dialog_one, null);
        TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);
        tv_0_0_.setText(message);
        Toast toast = new Toast(_context_);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 150);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view_);
        toast.show();
    }

    public int getUniqueSerialNumber(int init_index, int fin_index) {
        int unique_number = 0;
        long l = System.currentTimeMillis();
        long l_ = l / 1000;
        unique_number = Integer.parseInt(String.valueOf(l_).substring(init_index));
        return unique_number;
    }
}