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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Messenger;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerNode;
import se.sensiblethings.app.chitchato.extras.CustomImageView;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.DialogOne;
import se.sensiblethings.app.chitchato.kernel.Groups;
import se.sensiblethings.disseminationlayer.communication.Message;

public class CreateGroupActivity extends Activity {

    TextView tv_1;
    Button btn_1;
    ImageButton img_btn_1;
    CustomImageView cust_image_view;
    EditText ed_1, ed_2, ed_3, ed_4, ed_5;
    ProgressBar prog_bar;
    LinearLayout linear_layout;
    long l;
    String temp;
    String[] a;

    private static final int SELECT_GROUP_PROFILE_IMAGE = 1;
    private String selectedImagePath;
    private String filemanagerstring;
    private Uri selectedImageUri = null;
    private static String path = "";
    private String uri_str = "#Nothing#";
    Message message;
    String nodes = "%";
    ListView lv_1, lv_2;
    GridView gv_1;
    protected String new_group_created = "";
    protected SharedPreferences mPrefs;
    protected Customizations custom;
    protected String group_Name = "";
    protected String group_Interest = "";
    protected int group_age_limit = 0;
    protected String group_Leader = "";
    protected String group_password = "";
    private Groups groups, _groups_1;
    protected android.os.Messenger mPlatformManagerNodeMessenger;
    Intent service_intent;
    DialogOne _dialog_one_;


    static String date;
    String date_pattern = "yyyy-MM-dd HH:mm:ss";
    Format format = new SimpleDateFormat(date_pattern);

    // Page Label for Creating a Group
    String page_label = "CREATE";
    boolean isGROUP_FOUND = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("myprofile", 0);
        custom = new Customizations(this, -1);
        setContentView(custom.getGroupCreationScreen());

        setTitle("");
        tv_1 = (TextView) findViewById(R.id.tv_create_text);
        ed_1 = (EditText) findViewById(R.id.edt_group_name);
        ed_2 = (EditText) findViewById(R.id.edt_group_interest);
        ed_3 = (EditText) findViewById(R.id.edt_group_age_limit);
        ed_4 = (EditText) findViewById(R.id.edt_group_leader);
        ed_5 = (EditText) findViewById(R.id.edt_group_secret_code);
        prog_bar = (ProgressBar) findViewById(R.id.progress_bar_1);
        linear_layout = (LinearLayout) findViewById(R.id.linear2);

        btn_1 = (Button) findViewById(R.id.button_create_group);
        img_btn_1 = (ImageButton) findViewById(R.id.img_btn_profile_PIC);
        cust_image_view = (CustomImageView) findViewById(R.id.cust_img_btn_profile_PIC);


        img_btn_1.setImageURI(Uri.parse(uri_str));

        _dialog_one_ = new DialogOne(this, true, 11);
        final Bundle extras = getIntent().getExtras();

        // Font face
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
        Typeface tf_pala = Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");
        tv_1.setTypeface(tf);

        ed_1.setTypeface(tf_pala);
        ed_2.setTypeface(tf_pala);
        ed_3.setTypeface(tf_pala);
        ed_4.setTypeface(tf_pala);
        ed_5.setTypeface(tf_pala);
        btn_1.setTypeface(tf_pala);
        groups = new Groups(this, "", true);
        _groups_1 = new Groups(this, "params");


        service_intent = new Intent(this, PlatformManagerNode.class);
        bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);

        page_label = extras.getString("INDEX");
        tv_1.setText(page_label + " Group");
        btn_1.setText(page_label);


        // Application Directory
        path = getAppFolder("");


        cust_image_view.setImageResource(R.drawable.profile_image_android);
        cust_image_view.setTag("profile_image_android");
        if (page_label.equalsIgnoreCase("Update") || page_label.equalsIgnoreCase("Delete")) {
            String update_group_nm = extras.getString("NAME");

            Groups _groups_0 = new Groups(getBaseContext());
            ArrayList<String> ar_groups = _groups_0.getmGroups();
            String group_val = "";
            for (String tmp : ar_groups) {
                if (tmp.contains(":::")) {
                    String _temp_0 = tmp.split(":::")[1];
                    if (_temp_0.contains("~::~")) {
                        String[] _temp_ = _temp_0.split("~::~");
                        if (_temp_.length > 4) {
                            String group_name = _temp_[0];
                            String group_intereste = _temp_[1];
                            String group_leader = _temp_[2];
                            String group_age_limit = _temp_[4];
                            String group_pass = _temp_[3];
                            String CreationDate = "\nCreation Date:   " + _temp_[5];

                            if (group_name.equalsIgnoreCase(update_group_nm)) {
                                ed_1.setText(group_name);
                                ed_2.setText(group_intereste);
                                ed_4.setText(group_leader);
                                ed_3.setText(group_age_limit);
                                ed_5.setText(group_pass);
                                uri_str = mPrefs.getString(group_name + "-URI", "#Nothing#");
                                uri_str = path + group_name + "%group%miun.se_mobi" + ".jpg";
                                System.out.println("___________________________________uri_str" + uri_str);
                                System.out.println("__________________ index___" + group_name + "-URI " + uri_str);


                                File _image_file_ = new File(uri_str);
                                if (_image_file_.exists())
                                    cust_image_view.setImageURI(Uri.parse(uri_str));
                                else
                                    uri_str = "#Nothing#";

                                isGROUP_FOUND = true;
                                break;
                            } else
                                displayCustomizedToast(CreateGroupActivity.this, "Error Encountered -- Unable to load group");
                        }


                    }
                }

            }

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

        }

       // if (cust_image_view.getTag().equals("profile_image_android"))
         //   cust_image_view.setImageResource(R.drawable.profile_image_android);

        btn_1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (page_label.equalsIgnoreCase("Create"))
                    try {
                        if (mPlatformManagerNodeMessenger == null) {
                            // prog_bar.setVisibility(View.VISIBLE);
                            // linear_layout.setVisibility(View.GONE);

                            service_intent.putExtra("SERVICE_STARTED", true);
                            bindService(service_intent, mConnection,
                                    Context.BIND_AUTO_CREATE);

                        } else {
                            String age = "([01]?\\d\\d?|2[0-4]\\d|15[0-5])$";
                            final Pattern pattern = Pattern.compile(age);
                            final Matcher matcher;
                            matcher = pattern.matcher(ed_3.getText().toString());
                            if (matcher.matches())
                                if ((ed_1.getText().toString().trim() != "")
                                        && (ed_2.getText().toString().trim() != "")
                                        && (ed_3.getText().toString().trim() != "")
                                        && (ed_4.getText().toString().trim() != "")) {

                                    group_Name = ed_1.getText().toString();
                                    group_Interest = ed_2.getText().toString();
                                    group_age_limit = Integer.parseInt(ed_3.getText()
                                            .toString());
                                    group_Leader = ed_4.getText().toString();
                                    group_password = ed_5.getText().toString();

                                    // New Group String...
                                    new_group_created = "GROUP:::" + group_Name + "~::~" + group_Interest + "~::~" + group_Leader + "~::~" +
                                            group_password + "~::~" + group_age_limit + "~::~" + format.format(new Date()) + "~::~" + "123456";

                                    groups.addGroup(new_group_created, true);

                                    // update --params--

                                    _groups_1.addGroupParam(group_Name + "~::~" + group_Leader + "~::~" + group_password + "~::~" + 0);
                                    //
                                    displayCustomizedToast(CreateGroupActivity.this, "Congratulations! \n Group created Successfully!");

                                    //BKGroundTask bk_ground_task = new BKGroundTask(
                                    //      group_Name);
                                    //bk_ground_task.execute();

                                } else if (ed_1.getText().toString().trim() == "") {

                                    displayCustomizedToast(CreateGroupActivity.this, "Group Name Invalid!");
                                } else if (ed_2.getText().toString().trim() == "") {

                                    displayCustomizedToast(CreateGroupActivity.this, "Invalid Group Interest!");
                                } else if (ed_3.getText().toString().trim() == "") {
                                    displayCustomizedToast(CreateGroupActivity.this, "Age Limit Invalid!");

                                } else if (ed_4.getText().toString().trim() == "") {
                                    displayCustomizedToast(CreateGroupActivity.this, "Group Leader Name Invalid!");

                                } else {
                                    displayCustomizedToast(CreateGroupActivity.this, "Invalid! Add [M F or NA]");

                                }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent intent = new Intent(CreateGroupActivity.this,
                                ErrorActivity.class);
                        intent.putExtra("error", e.getLocalizedMessage());
                        startActivity(intent);
                    }
                else if (page_label.equalsIgnoreCase("Update"))
                    try {
                        if (isGROUP_FOUND)
                            if (mPlatformManagerNodeMessenger == null) {
                                // prog_bar.setVisibility(View.VISIBLE);
                                // linear_layout.setVisibility(View.GONE);

                                service_intent.putExtra("SERVICE_STARTED", true);
                                bindService(service_intent, mConnection,
                                        Context.BIND_AUTO_CREATE);

                            } else {
                                String age = "([01]?\\d\\d?|2[0-4]\\d|15[0-5])$";
                                final Pattern pattern = Pattern.compile(age);
                                final Matcher matcher;
                                matcher = pattern.matcher(ed_3.getText().toString());
                                if (matcher.matches())
                                    if ((ed_1.getText().toString().trim() != "")
                                            && (ed_2.getText().toString().trim() != "")
                                            && (ed_3.getText().toString().trim() != "")
                                            && (ed_4.getText().toString().trim() != "")) {

                                        group_Name = ed_1.getText().toString();
                                        group_Interest = ed_2.getText().toString();
                                        group_age_limit = Integer.parseInt(ed_3.getText()
                                                .toString());
                                        group_Leader = ed_4.getText().toString();
                                        group_password = ed_5.getText().toString();

                                        // New Group String...
                                        new_group_created = "GROUP:::" + group_Name + "~::~" + group_Interest + "~::~" + group_Leader + "~::~" +
                                                group_password + "~::~" + group_age_limit + "~::~" + format.format(new Date()) + "~::~" + "123456";

                                        //groups.addGroup(new_group_created, true);

                                        // update --params--

                                        //_groups_1.addGroupParam(group_Name + "~::~" + group_Leader + "~::~" + group_password + "~::~" + 0);

                                        displayCustomizedToast(CreateGroupActivity.this, "Congratulations! \n Group updated successfully!");

                                    } else if (ed_1.getText().toString().trim() == "") {

                                        displayCustomizedToast(CreateGroupActivity.this, "Group Name Invalid!");
                                    } else if (ed_2.getText().toString().trim() == "") {

                                        displayCustomizedToast(CreateGroupActivity.this, "Invalid Group Interest!");
                                    } else if (ed_3.getText().toString().trim() == "") {
                                        displayCustomizedToast(CreateGroupActivity.this, "Age Limit Invalid!");

                                    } else if (ed_4.getText().toString().trim() == "") {
                                        displayCustomizedToast(CreateGroupActivity.this, "Group Leader Name Invalid!");

                                    } else {
                                        displayCustomizedToast(CreateGroupActivity.this, "Invalid! Add [M F or NA]");

                                    }
                            }
                        else {
                            displayCustomizedToast(CreateGroupActivity.this, "Error Encountered -- Unable to load group");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent intent = new Intent(CreateGroupActivity.this,
                                ErrorActivity.class);
                        intent.putExtra("error", e.getLocalizedMessage());
                        startActivity(intent);
                    }
                else if (page_label.equalsIgnoreCase("Delete"))
                    try {
                        String delete_group_nm = extras.getString("NAME");
                        boolean isGROUP_FOUND = false;

                        // startActivity(intent);
                        Groups _groups_0 = new Groups(getBaseContext(), "", true);
                        ArrayList<String> ar_groups = _groups_0.getmGroups();
                        String group_val = "";
                        for (String tmp : ar_groups) {
                            if (tmp.contains(":::")) {
                                String _temp_0 = tmp.split(":::")[1];
                                if (_temp_0.contains("~::~")) {
                                    String[] _temp_ = _temp_0.split("~::~");
                                    if (_temp_.length > 4) {
                                        String group_name = _temp_[0];
                                        String group_intereste = _temp_[1];
                                        String group_leader = _temp_[2];
                                        String group_age_limit = _temp_[4];
                                        String group_pass = _temp_[3];
                                        String CreationDate = "\nCreation Date:   " + _temp_[5];

                                        if (group_name.equalsIgnoreCase(delete_group_nm)) {
                                            ed_1.setText(group_name);
                                            ed_2.setText(group_intereste);
                                            ed_3.setText(group_Leader);
                                            ed_4.setText(group_age_limit);
                                            ed_5.setText(group_pass);
                                            isGROUP_FOUND = true;
                                            System.out.print("GROUP FOUND " + isGROUP_FOUND + tmp);
                                            break;
                                        } else
                                            displayCustomizedToast(CreateGroupActivity.this, "Error Encountered -- Unable to Load Group");

                                    }


                                }
                            }

                        }


                        if (isGROUP_FOUND)
                            if (mPlatformManagerNodeMessenger == null) {

                                service_intent.putExtra("SERVICE_STARTED", true);
                                bindService(service_intent, mConnection,
                                        Context.BIND_AUTO_CREATE);

                            } else {
                                String age = "([01]?\\d\\d?|2[0-4]\\d|15[0-5])$";
                                final Pattern pattern = Pattern.compile(age);
                                final Matcher matcher;
                                matcher = pattern.matcher(ed_3.getText().toString());
                                if (matcher.matches())
                                    if ((ed_1.getText().toString().trim() != "")
                                            && (ed_2.getText().toString().trim() != "")
                                            && (ed_3.getText().toString().trim() != "")
                                            && (ed_4.getText().toString().trim() != "")) {

                                        group_Name = ed_1.getText().toString();
                                        group_Interest = ed_2.getText().toString();
                                        group_age_limit = Integer.parseInt(ed_3.getText()
                                                .toString());
                                        group_Leader = ed_4.getText().toString();
                                        group_password = ed_5.getText().toString();

                                        // New Group String...
                                        new_group_created = "GROUP:::" + group_Name + "~::~" + group_Interest + "~::~" + group_Leader + "~::~" +
                                                group_password + "~::~" + group_age_limit + "~::~" + format.format(new Date()) + "~::~" + "123456";

                                        //groups.addGroup(new_group_created, true);

                                        // update --params--

                                        // _groups_1.addGroupParam(group_Name + "~::~" + group_Leader + "~::~" + group_password + "~::~" + 0);
                                        //
                                        displayCustomizedToast(CreateGroupActivity.this, "Congratulations! \n Group deleted successfully!");


                                    } else if (ed_1.getText().toString().trim() == "") {

                                        displayCustomizedToast(CreateGroupActivity.this, "Group Name Invalid!");
                                    } else if (ed_2.getText().toString().trim() == "") {

                                        displayCustomizedToast(CreateGroupActivity.this, "Invalid Group Interest!");
                                    } else if (ed_3.getText().toString().trim() == "") {
                                        displayCustomizedToast(CreateGroupActivity.this, "Age Limit Invalid!");

                                    } else if (ed_4.getText().toString().trim() == "") {
                                        displayCustomizedToast(CreateGroupActivity.this, "Group Leader Name Invalid!");

                                    } else {
                                        displayCustomizedToast(CreateGroupActivity.this, "Invalid! Add [M F or NA]");

                                    }
                            }
                        else {
                            displayCustomizedToast(CreateGroupActivity.this, "Error Encountered -- Unable to load group");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent intent = new Intent(CreateGroupActivity.this,
                                ErrorActivity.class);
                        intent.putExtra("error", e.getLocalizedMessage());
                        startActivity(intent);
                    }

            }
        });
        img_btn_1.setOnClickListener(new OnClickListener() {

                                         @Override
                                         public void onClick(View arg0) {
                                             try {


                                             } catch (Exception e) {
                                                 Intent intent = new Intent(CreateGroupActivity.this,
                                                         ErrorActivity.class);
                                                 intent.putExtra("error", e.getLocalizedMessage());
                                                 startActivity(intent);
                                             }

                                         }

                                     }

        );
        cust_image_view.setOnClickListener(new

                                                   OnClickListener() {

                                                       @Override
                                                       public void onClick(View arg0) {
                                                           try {

                                                               group_Name = ed_1.getText().toString();
                                                               group_Interest = ed_2.getText().toString();
                                                               group_age_limit = Integer.parseInt(ed_3.getText()
                                                                       .toString());
                                                               group_Leader = ed_4.getText().toString();
                                                               group_password = ed_5.getText().toString();

                                                               if (!group_Name.trim().isEmpty()) {
                                                                   Intent intent = new Intent();
                                                                   intent.setType("image/*");
                                                                   intent.setAction(Intent.ACTION_GET_CONTENT);
                                                                   intent.putExtra("ACTIVITY", "RegisterActivity");

                                                                   startActivityForResult(
                                                                           Intent.createChooser(intent, "Select Picture"),
                                                                           SELECT_GROUP_PROFILE_IMAGE);
                                                                   // mPrefs = getSharedPreferences("myprofile", 0);
                                                                   // SharedPreferences.Editor ed = mPrefs.edit();
                                                                   // ed.putString(group_Name + "-URI", uri_str);
                                                                   // ed.commit();
                                                               } else {
                                                                   displayCustomizedToast(CreateGroupActivity.this, "Add Group Name ");
                                                               }
                                                           } catch (Exception e) {
                                                               Intent intent = new Intent(CreateGroupActivity.this,
                                                                       ErrorActivity.class);
                                                               intent.putExtra("error",
                                                                       "Error Encountered while  Creating. Try with different group name! ");
                                                               startActivity(intent);
                                                           }
                                                       }

                                                   }
        );
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

                intent = new Intent(CreateGroupActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_advanced:

                intent = new Intent(CreateGroupActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(CreateGroupActivity.this,
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

        try {
            if (requestCode == SELECT_GROUP_PROFILE_IMAGE) {
                if (!group_Name.trim().isEmpty()) {
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
                        saveImage(bt, group_Name + "__group__miun.se_mobi", group_Name);

                    }
                } else {
                    displayCustomizedToast(CreateGroupActivity.this, "Add Group Name");
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            displayCustomizedToast(getApplicationContext(), "Error! profile picture upload failed");
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
        ed.putBoolean("CGA_VISITED", true);

        ed.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent _intent = new Intent(CreateGroupActivity.this, GroupContainerActivity.class);
        _intent.putExtra("INDEX", "GROUPS");
        startActivity(_intent);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mPlatformManagerNodeMessenger = new Messenger(service);

        }

        public void onServiceDisconnected(ComponentName className) {
            Toast.makeText(CreateGroupActivity.this, "Disconnected",
                    Toast.LENGTH_SHORT).show();
        }
    };


    public class BKGroundTask extends AsyncTask<String, Void, String> {
        String group_str = "";
        boolean flag = false;


        android.os.Message msg = android.os.Message.obtain(null,
                PlatformManagerNode.NEW_MESSAGE_FLAG, 0, 0);

        public BKGroundTask(String new_group_string) {
            group_str = new_group_string;
        }


        @Override
        protected String doInBackground(String... arg0) {
            try {

                Bundle b = new Bundle();
                b.putString("NEW_MESSAGE_FLAG", group_str);
                b.putString("NEW_MESSAGE_FLAG_FA", group_str);

                android.os.Message replyMsg = android.os.Message
                        .obtain(null,
                                PlatformManagerNode.NEW_MESSAGE_FLAG,
                                0, 0);
                replyMsg.setData(b);
                //  mPlatformManagerNodeMessenger.send(replyMsg);
                flag = true;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Intent intent = new Intent(CreateGroupActivity.this,
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

                try {

                    CreateGroupActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            prog_bar.setVisibility(View.GONE);
                            linear_layout.setVisibility(View.VISIBLE);
                            _dialog_one_.show();
                        }
                    });

                    if (flag) {

                        if (custom.getLanguage() == 1)
                            Toast.makeText(getApplicationContext(),
                                    R.string.notification_five_sv,
                                    Toast.LENGTH_LONG).show();
                        else if (custom.getLanguage() == 2)
                            Toast.makeText(getApplicationContext(),
                                    R.string.notification_five_sp,
                                    Toast.LENGTH_LONG).show();
                        else if (custom.getLanguage() == 3)
                            Toast.makeText(getApplicationContext(),
                                    R.string.notification_five_pr,
                                    Toast.LENGTH_LONG).show();
                        else if (custom.getLanguage() == 4)
                            Toast.makeText(getApplicationContext(),
                                    R.string.notification_five_fr,
                                    Toast.LENGTH_LONG).show();
                        else if (custom.getLanguage() == 5)
                            Toast.makeText(getApplicationContext(),
                                    R.string.notification_five_am,
                                    Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(),
                                    R.string.notification_five_en,
                                    Toast.LENGTH_LONG).show();
                    } else {
                        if (custom.getLanguage() == 1)
                            Toast.makeText(getApplicationContext(),
                                    R.string.notification_six_sv,
                                    Toast.LENGTH_LONG).show();
                        else if (custom.getLanguage() == 2)
                            Toast.makeText(getApplicationContext(),
                                    R.string.notification_six_sp,
                                    Toast.LENGTH_LONG).show();
                        else if (custom.getLanguage() == 3)
                            Toast.makeText(getApplicationContext(),
                                    R.string.notification_six_pr,
                                    Toast.LENGTH_LONG).show();
                        else if (custom.getLanguage() == 4)
                            Toast.makeText(getApplicationContext(),
                                    R.string.notification_six_fr,
                                    Toast.LENGTH_LONG).show();
                        else if (custom.getLanguage() == 5)
                            Toast.makeText(getApplicationContext(),
                                    R.string.notification_six_am,
                                    Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(),
                                    R.string.notification_six_en,
                                    Toast.LENGTH_LONG).show();
                    }

                    flag = false;

                } catch (Exception e) {

                    Toast.makeText(getApplicationContext(),
                            "Sorry, Registration Not Successful.",
                            Toast.LENGTH_LONG).show();

                    if (custom.getLanguage() == 1)
                        Toast.makeText(getApplicationContext(),
                                R.string.notification_seven_sv,
                                Toast.LENGTH_LONG).show();
                    else if (custom.getLanguage() == 2)
                        Toast.makeText(getApplicationContext(),
                                R.string.notification_seven_sp,
                                Toast.LENGTH_LONG).show();
                    else if (custom.getLanguage() == 3)
                        Toast.makeText(getApplicationContext(),
                                R.string.notification_seven_pr,
                                Toast.LENGTH_LONG).show();
                    else if (custom.getLanguage() == 4)
                        Toast.makeText(getApplicationContext(),
                                R.string.notification_seven_fr,
                                Toast.LENGTH_LONG).show();
                    else if (custom.getLanguage() == 5)
                        Toast.makeText(getApplicationContext(),
                                R.string.notification_seven_am,
                                Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplicationContext(),
                                R.string.notification_six_en,
                                Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
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

    // / Set Path for Home Folder
    public void setAppFolder(String file_name) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato");
        if (!file.exists()) {
            new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato").mkdir();
        }
    }

    public void saveImage(ArrayList<byte[]> array_byte, String image_name, String group_name) {
        String destName = path + image_name;
        File file = new File(destName);
        if (!file.exists()) {
            setAppFolder("");
        }

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
            uri_str = destName;
            for (int indexx = 0; indexx < array_byte.size(); indexx++) {
                byte[] bb = array_byte.get(indexx);
                if (indexx == 0) {
                    fout.write(bb);
                } else if (indexx < array_byte.size() - 1) {
                    {
                        fout.write(bb, indexx * 1024, 1024);
                    }
                } else if (indexx == array_byte.size() - 1) {
                    fout.write(bb, indexx * 1024, (bb.length - (indexx) * 1024));

                } else {
                }

            }

            // Save the images
            SharedPreferences.Editor ed = mPrefs.edit();
            mPrefs = getSharedPreferences("myprofile", 0);
            System.out.println("__________________ index" + group_name + "-URI");
            System.out.println("__________________ index___" + group_name + "-URI  " + uri_str);
            ed.putString(group_name + "-URI", uri_str);
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


    //Get Path for Home Folder
    public static String getAppFolder(String file_name) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/";
    }


}
