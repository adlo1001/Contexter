package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.kernel.ChitchatConfig;
import se.sensiblethings.app.chitchato.kernel.Groups;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class PurgeActivity extends Activity {

    TextView tv, textView, tv_, tv_group, tv_uci_, tv_pub_, tv_pri_;
    Button btn, btn_bar, btn_pub_, btn_pri_, btn_exit;
    EditText edt_uci, edt_pub_, edt_pri_;
    RadioButton radio_btn_0, radio_btn_1, radio_btn_2, radio_btn_3;
    Spinner mygroups_spinner;
    long l;
    String temp;
    ArrayList activenodes;
    String[] a;

    Message message;
    String nodes = "%";
    ListView lv_1, lv_2;
    GridView gv_1;
    SensibleThingsNode sensiblethingsnode;
    protected Customizations custom;

    private CheckDeletionTaskThread thread_1;
    private Groups groups;
    private SharedPreferences _mPrefs_;
    int pri_message_thread_len = 0;
    int pub_message_thread_len = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _mPrefs_ = getSharedPreferences("myprofile", 0);
        custom = new Customizations(this, -1);

        setContentView(custom.getPurgeScreen());
        setTitle("");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        pri_message_thread_len = custom.getPri_message_thread_len();
        pub_message_thread_len = custom.getPub_message_thread_len();

        groups = new Groups(getApplicationContext());

        try {
            tv = (TextView) findViewById(R.id.tv_purge_chitchat);
            btn = (Button) findViewById(R.id.btn_purge_);
            btn_exit = (Button)findViewById(R.id.btn_purge_exit) ;
            btn_bar = (Button) findViewById(R.id.btn_bar_peer);
            tv_group = (TextView) findViewById(R.id.tv_label_grps);
            edt_uci = (EditText) findViewById(R.id.edt_bar_peer);
            radio_btn_0 = (RadioButton) findViewById(R.id.radio0_purge_all_user_files);
            radio_btn_1 = (RadioButton) findViewById(R.id.radio1_purge_only_own_group);
            radio_btn_2 = (RadioButton) findViewById(R.id.radio2_delete_favorite_groups);
            radio_btn_3 = (RadioButton) findViewById(R.id.radio3_return_toFreshly_installed);
            tv_uci_ = (TextView) findViewById(R.id.tv_bar_peer);

            tv_pub_ = (TextView) findViewById(R.id.tv_public_message_len);
            tv_pri_ = (TextView) findViewById(R.id.tv_private_message_len);

            edt_pub_ = (EditText) findViewById(R.id.edt_public_message_len);
            edt_pri_ = (EditText) findViewById(R.id.edt_private_message_len);

            btn_pub_ = (Button) findViewById(R.id.btn_public_message_len);
            btn_pri_ = (Button) findViewById(R.id.btn_private_message_len);
            mygroups_spinner  = (Spinner) findViewById(R.id.spinner_my_groups_purg);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_item_one);

            Typeface tf = Typeface.createFromAsset(getAssets(),
                    "fonts/COMIC.TTF");

            Typeface tf_pala = Typeface.createFromAsset(getAssets(),
                    "fonts/pala.ttf");
            // Font face

            tv.setTypeface(tf);
            btn.setTypeface(tf_pala);
            btn_exit.setTypeface(tf_pala);
            radio_btn_0.setTypeface(tf_pala);
            radio_btn_1.setTypeface(tf_pala);
            radio_btn_2.setTypeface(tf_pala);
            radio_btn_3.setTypeface(tf_pala);
            btn_bar.setTypeface(tf_pala);
            tv_group.setTypeface(tf_pala);
            edt_uci.setTypeface(tf_pala);
            tv_uci_.setTypeface(tf_pala);

            tv_pri_.setTypeface(tf_pala);
            tv_pub_.setTypeface(tf_pala);
            edt_pri_.setTypeface(tf_pala);
            edt_pub_.setTypeface(tf_pala);
            btn_pri_.setTypeface(tf_pala);
            btn_pub_.setTypeface(tf_pala);

            // fetch from preferences and display
            edt_pri_.setText(custom.getPri_message_thread_len() + "");
            edt_pub_.setText(custom.getPub_message_thread_len() + "");

            thread_1 = new CheckDeletionTaskThread();
            //delay_timer = new ChitchatDelayTimer(this);
            // Get my own Groups
            Groups groups = new Groups(this,"",true);
            final ArrayList<String> al_ = groups.getmGroups();

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
            mygroups_spinner.setAdapter(adapter);

            btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    try {
                        if (radio_btn_0.isChecked()) {
                            boolean deleted = ChitchatConfig.DeleteAppFolder();
                            if (deleted) {
                                Toast.makeText(PurgeActivity.this, "Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                displayCustomizedToast(PurgeActivity.this, "Deleted Successfully!");
                            } else {

                                displayCustomizedToast(PurgeActivity.this, "Error! Files not deleted!");
                            }
                        } else if (radio_btn_1.isChecked()) {

                            try {
                                Groups _mGroups = new Groups(PurgeActivity.this, "", true);
                                _mGroups.clearGroups();

                                displayCustomizedToast(PurgeActivity.this, "--  Deleted!--");
                            } catch (Exception e) {

                                displayCustomizedToast(PurgeActivity.this, "--  Error--");

                            }
                        } else if (radio_btn_2.isChecked()) {


                            // groups.getDeleteImportedGroups(getApplicationContext());
                            Intent intent = new Intent(PurgeActivity.this,
                                    RemoveGroupsActivity.class);
                            startActivity(intent);

                        } else if (radio_btn_3.isChecked()) {
                            getCacheDir().delete();
                            File file = getCacheDir();
                            if (_deleteCache_(file))

                                displayCustomizedToast(PurgeActivity.this, "Application Restored!");
                            else
                                displayCustomizedToast(PurgeActivity.this, "Application Not Restored!-- No File To Restore--");

                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }
            });



            btn_bar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            String pat = "([01]?\\d\\d?|2[0-4]\\d|15[0-5])$";
            final Pattern pattern = Pattern.compile(pat);

            btn_pri_.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Matcher matcher = pattern.matcher(edt_pri_.getText().toString().trim());

                    if (matcher.matches()) {
                        SharedPreferences.Editor ed = _mPrefs_.edit();
                        custom.setPri_message_thread_len(Integer.parseInt(edt_pri_.getText().toString()));
                        ed.putInt("PRIVATE_MESSAGE_THREAD_LEN", Integer.parseInt(edt_pri_.getText().toString()));
                        ed.commit();
                    } else {
                        displayCustomizedToast(PurgeActivity.this, "Invalid Input. \n Range [10 upto 100]");
                    }

                }
            });
            btn_pub_.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Matcher matcher = pattern.matcher(edt_pub_.getText().toString().trim());
                    if (matcher.matches()) {
                        SharedPreferences.Editor ed = _mPrefs_.edit();
                        custom.setPub_message_thread_len(Integer.parseInt(edt_pub_.getText().toString()));
                        ed.putInt("PUBLIC_MESSAGE_THREAD_LEN", Integer.parseInt(edt_pub_.getText().toString()));
                        ed.commit();
                    } else {
                        displayCustomizedToast(PurgeActivity.this, "Invalid Input. \n Range [10 upto 100]");
                    }
                }
            });

            btn_exit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {



                    Intent intent = new Intent(PurgeActivity.this, ChoicesActivity.class);
                    startActivity(intent);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

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

                intent = new Intent(PurgeActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_advanced:

                intent = new Intent(PurgeActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(PurgeActivity.this,
                        SensorReadingsActivity.class);
                startActivity(intent);
                return true;

			/*
             * case R.id.action_create:
			 * 
			 * intent = new Intent(SearchResultsActivity.this,
			 * SearchResultsActivity.class); startActivity(intent); return true;
			 * case R.id.action_settings:
			 * 
			 * intent = new Intent(SearchResultsActivity.this,
			 * PurgeActivity.class); startActivity(intent); return true; case
			 * R.id.action_search:
			 * 
			 * intent = new Intent(SearchResultsActivity.this,
			 * SearchResultsActivity.class); startActivity(intent); return true;
			 * case R.id.action_language:
			 * 
			 * intent = new Intent(SearchResultsActivity.this,
			 * LanguageActivity.class); startActivity(intent); return true; case
			 * R.id.action_register:
			 * 
			 * intent = new Intent(SearchResultsActivity.this,
			 * RegisterActivity.class); startActivity(intent); return true; case
			 * R.id.action_help:
			 * 
			 * intent = new Intent(SearchResultsActivity.this,
			 * HelpActivity.class); startActivity(intent); return true; case
			 * R.id.action_about:
			 * 
			 * intent = new Intent(SearchResultsActivity.this,
			 * AboutActivity.class); startActivity(intent); return true;
			 */

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Create the platform itself
        // if (sensibleThingsPlatform == null) {
        // sensibleThingsPlatform = new ChitchatoPlatform();
        // }
    }

    // @Override
    public void onClick(View arg0) {

    }

    public class CheckDeletionTaskThread extends Thread {
        public CheckDeletionTaskThread() {

        }

        public CheckDeletionTaskThread(ArrayList<String> array_list) {

        }

        @Override
        public void run() {
            Log.v("Purge--->", "Show Buttons Thread");

            while (true) {

                try {
                    // check if the deletion is done every 5 seconds
                    this.sleep(5000);
                    // this.interrupt();

                } catch (Exception e) {
                    // Log.v("Main", "Thread Interepted!");
                    e.printStackTrace();
                    return;
                }

            }
        }
    }


    public static boolean _deleteCache_(File file) {
        if (file != null && file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = _deleteCache_(new File(file, children[i]));
                if (!success) {
                    return false;
                }
            }
            return file.delete();
        } else if (file != null && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }

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
