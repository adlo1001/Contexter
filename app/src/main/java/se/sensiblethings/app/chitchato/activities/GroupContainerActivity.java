package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.adapters.ImageExpandableAdapter;
import se.sensiblethings.app.chitchato.adapters.ListAdapter;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerNode;
import se.sensiblethings.app.chitchato.contactslist.ui.ContactsListActivity;
import se.sensiblethings.app.chitchato.extras.Constants;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.DialogOne;
import se.sensiblethings.app.chitchato.kernel.Groups;
import se.sensiblethings.app.chitchato.kernel.Peers;
import se.sensiblethings.app.chitchato.kernel.PrivateChats;
import se.sensiblethings.disseminationlayer.communication.Message;

public class GroupContainerActivity extends Activity {

    public static String PREFERRED_IP = "0.0.0.0";
    static Messenger mPlatformManagerNodeMessenger;
    protected int lang_number = 0;
    ///for adding bootstrap ip
    protected TreeMap bs_map = new TreeMap<String, String>();
    TextView tv;
    CheckBox ch_box_1, ch_box_2, ch_box_3, ch_box_4, ch_box_5;
    ListView lv;
    ExpandableListView exp_lv;
    Button btn;
    long l;
    String temp;
    ArrayList activenodes;
    String[] a;
    Message message;
    String nodes = "%";
    GridView gv_1;
    String language = "En";
    ListAdapter list_adapter;
    boolean _BOUND = false;
    android.os.Message msg = android.os.Message.obtain(null,
            PlatformManagerNode.NEW_MESSAGE_FLAG, 0, 0);
    String date_pattern = "yyyy-MM-dd HH:mm:ss";
    Format format = new SimpleDateFormat(date_pattern);
    SharedPreferences mPrefs, mPrefs_;
    Set<String> bootstraps;
    //bootstrap ip is public -- default --false
    boolean _BOOTSTRAP_IP_ = false;
    String container_cat = "";
    private boolean LA_VISISTED = false;
    private Customizations custom;
    private Groups groups;
    private Intent service_intent;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            mPlatformManagerNodeMessenger = new Messenger(binder);
            _BOUND = true;

        }

        public void onServiceDisconnected(ComponentName className) {
            displayCustomizedToast(GroupContainerActivity.this, "# Service disconnected #");
            _BOUND = false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("language", 0);
        language = mPrefs.getString("language", "En");

        mPrefs_ = getSharedPreferences("bootstraps", 0);
        bootstraps = mPrefs_.getStringSet("bootstraps", bs_map.keySet());
        _BOOTSTRAP_IP_ = mPrefs_.getBoolean("BOOTSTRAP_IP", false);
        PREFERRED_IP = mPrefs_.getString("PREFERRED_IP", PREFERRED_IP);

        custom = new Customizations(this, -1);
        setContentView(custom.getGroupContainerScreen());
        setTitle("");
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
        tv = (TextView) findViewById(R.id.tv_empty_bar_languagescreen);
        tv.setTypeface(tf);
        // ///
        Bundle index_cat = getIntent().getExtras();
        container_cat = index_cat.getString("INDEX");
        lv = (ListView) findViewById(R.id.lv_language);
        exp_lv = (ExpandableListView) findViewById(R.id.expandableListView_lan);
        lv.setSelector(R.color.chitchato_mebratu_cyan);
        lv.setDrawSelectorOnTop(false);
        lv.setClickable(false);


        groups = new Groups(this);


        // ///
        if (container_cat.equals("NOTIFICATIONS")) {

            service_intent = new Intent(this, PlatformManagerNode.class);
            bindService(service_intent, mConnection,
                    Context.BIND_AUTO_CREATE);

            // Swedish
            if (custom.getLanguage() == 1) {

                String[] settings = getResources().getStringArray(R.array.settings_sv);
                tv.setText(settings[5]);

            }
            // Spanish
            else if (custom.getLanguage() == 2) {

                String[] settings = getResources().getStringArray(R.array.settings_sp);
                tv.setText(settings[5]);
            }
            // Portugese
            else if (custom.getLanguage() == 3) {

                String[] settings = getResources().getStringArray(R.array.settings_pr);
                tv.setText(settings[5]);
            }
            // French
            else if (custom.getLanguage() == 4) {

                String[] settings = getResources().getStringArray(R.array.settings_fr);
                tv.setText(settings[5]);

            }
            // Amharic
            else if (custom.getLanguage() == 5) {
                String[] settings = getResources().getStringArray(R.array.settings_am);
                tv.setText(settings[5]);
            }
            // English
            else {
                String[] settings = getResources().getStringArray(R.array.settings_en);
                tv.setText(settings[5]);
            }

            Peers _peers_0_requesting_list = new Peers(getBaseContext(), MainActivity.SELECTED_GROUP, "rqsting");
            final ArrayList<String> al_ = _peers_0_requesting_list.getmPeers();
            if (al_.isEmpty() || al_ == null)
                al_.add("#Requesting UCIs 0");

            String str = custom.getCONTEXTER_ADVS();
            if (str.contains("~::~")) {
                String[] str_00 = str.split("~::~");
                for (String s : str_00) if (!s.trim().isEmpty()) if (!al_.contains(s)) al_.add(s);
            }
            //contexter_0_0@miun.se/mobi:201~::~Dexter ~::~Contexter_0_0~::~contexter ~::~2016-12-11 13:33:56
            list_adapter = new ListAdapter(this, 0, true, al_, 13);
            lv.setAdapter(list_adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    if (_BOUND) {
                        unbindService(mConnection);
                        bindService(service_intent, mConnection, Context.BIND_AUTO_CREATE);
                    }
                    final TextView btn_accept = (TextView) view.findViewById(R.id.btn_accept);
                    final TextView btn_decline = (TextView) view.findViewById(R.id.btn_decline);
                    displayCustomizedToast(GroupContainerActivity.this, "Tap:> Accept or Decline");
                    btn_accept.setOnClickListener(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {
                                                          if (custom.isST_UP()) {
                                                              if (_BOUND) {
                                                                  String _temp_0 = al_.get(position);
                                                                  if (_temp_0.contains("~::~")) {
                                                                      String[] temp_params = _temp_0.split("~::~");
                                                                      if (temp_params.length > 2) {
                                                                          String _group_name_0 = temp_params[1];
                                                                          String _nickname_0 = temp_params[2];
                                                                          String _name_0 = temp_params[2];

                                                                          Groups _groups_0 = new Groups(getBaseContext(), _group_name_0, true);
                                                                          String pass = _groups_0.getGroupPassword(_group_name_0);
                                                                          if (pass.equals("#")) {
                                                                              displayCustomizedToast(GroupContainerActivity.this, "Oops ... You cant do this.");
                                                                          } else {
                                                                              String Message = Constants.REGISTERED + "~::~" + _group_name_0 + "~::~" + _nickname_0 + "~::~" + pass +
                                                                                      "~::~" + (System.currentTimeMillis()) + "~::~" + format.format(new Date());

                                                                              Bundle b = new Bundle();
                                                                              b.putString("NEW_MESSAGE_FLAG", Message);
                                                                              b.putString("NEW_MESSAGE_FLAG_FA", Message);
                                                                              android.os.Message replyMsg = android.os.Message
                                                                                      .obtain(null, PlatformManagerNode.NEW_MESSAGE_FLAG, 0, 0);
                                                                              replyMsg.setData(b);
                                                                              //msg.replyTo = mActivityMessenger;
                                                                              try {
                                                                                  mPlatformManagerNodeMessenger.send(replyMsg);
                                                                              } catch (RemoteException e) {
                                                                                  e.printStackTrace();
                                                                              }

                                                                              custom.set_NEW_INFO_AVAILABLE_(false);
                                                                              custom.set_NOTIFICATIONS_AVAILABLE(false);
                                                                              displayCustomizedToast(getApplicationContext(), "Join Response Delivered!");
                                                                              btn_accept.setVisibility(View.VISIBLE);
                                                                              btn_decline.setVisibility(View.VISIBLE);

                                                                              SharedPreferences mPrefs_ = getSharedPreferences("myprofile", 0);
                                                                              SharedPreferences.Editor ed = mPrefs_.edit();
                                                                              // ed.putBoolean("_NEW_INFO_AVAILABLE_", false);
                                                                              //ed.putBoolean("_NOTIFICATIONS_AVAILABLE", false);
                                                                              //ed.commit();
                                                                          }
                                                                      } else
                                                                          displayCustomizedToast(GroupContainerActivity.this, "Something went wrong.. Try again");
                                                                  } else
                                                                      displayCustomizedToast(GroupContainerActivity.this, "Something went wrong.. Try again");

                                                              } else

                                                              {
                                                                  bindService(service_intent, mConnection,
                                                                          Context.BIND_AUTO_CREATE);
                                                                  displayCustomizedToast(getApplicationContext(), "Action Failed!. Platform out of Sync.");
                                                              }
                                                          } else {
                                                              displayCustomizedToast(getApplicationContext(), "Action Failed. Platform is not running");
                                                          }


                                                      }
                                                  }

                    );
                    btn_decline.setOnClickListener(new View.OnClickListener()

                                                   {
                                                       @Override
                                                       public void onClick(View v) {
                                                           custom.set_NEW_INFO_AVAILABLE_(false);
                                                           custom.set_NOTIFICATIONS_AVAILABLE(false);
                                                           displayCustomizedToast(getApplicationContext(), "Join Request Declined!");
                                                           btn_accept.setVisibility(View.VISIBLE);
                                                           btn_decline.setVisibility(View.VISIBLE);
                                                       }
                                                   }

                    );

                }

            });
        }
        // ///
        if (container_cat.equals("NEW BOOTSTRAPS")) {
            service_intent = new Intent(this, PlatformManagerNode.class);
            bindService(service_intent, mConnection,
                    Context.BIND_AUTO_CREATE);

            // Swedish
            if (custom.getLanguage() == 1) {

                String[] settings = getResources().getStringArray(R.array.settings_sv);
                tv.setText(settings[7]);

            }
            // Spanish
            else if (custom.getLanguage() == 2) {

                String[] settings = getResources().getStringArray(R.array.settings_sp);
                tv.setText(settings[7]);
            }
            // Portugese
            else if (custom.getLanguage() == 3) {

                String[] settings = getResources().getStringArray(R.array.settings_pr);
                tv.setText(settings[7]);
            }
            // French
            else if (custom.getLanguage() == 4) {

                String[] settings = getResources().getStringArray(R.array.settings_fr);
                tv.setText(settings[7]);

            }
            // Amharic
            else if (custom.getLanguage() == 5) {
                String[] settings = getResources().getStringArray(R.array.settings_am);
                tv.setText(settings[7]);
            }
            // English
            else {
                String[] settings = getResources().getStringArray(R.array.settings_en);
                tv.setText(settings[7]);
            }

            String ip_address_pattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
            final String ip_address_pattern_public = "^([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(?<!172\\." +
                    "(16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31))(?<!127)(?<!^10)(?<!^0)\\.([0-9]|[1-9]" +
                    "[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(?<!192\\.168)(?<!172\\.(16|17|18|19|20|21|22|23|24|25" +
                    "|26|27|28|29|30|31))\\.([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9]" +
                    "{2}|2[0-4][0-9]|25[0-5])(?<!\\.0$)(?<!\\.255$)$";
            final Pattern pattern = Pattern.compile(ip_address_pattern);
            final Pattern pattern_public = Pattern.compile(ip_address_pattern_public);


            ArrayList<String> al_ = new ArrayList<String>();

            String str_frombuider = custom.getCONTEXTER_ADVS();
            String temp_item;
            String bootstrap_pat = index_cat.getString("PAT");
            String nw_bt_ip = InitializeLocalBTSRPActivity.getIPfromPattern(bootstrap_pat.split("#")[1]);
            al_.add(nw_bt_ip);
            if (str_frombuider.contains("~::~")) {
                if (str_frombuider.contains("~::~~0btstrp")) {
                    int index = 0;
                    String[] _temp_0 = str_frombuider.split("~::~");
                    while (_temp_0.length - 1 > index) {
                        if (_temp_0[index].contains("#")) {
                            String[] tmp_001 = _temp_0[index].split("#");
                            if (tmp_001.length > 4) {
                                String _tmp_0001 = tmp_001[0];
                                String _tmp_0002 = tmp_001[1];
                                String _tmp_0003 = tmp_001[2];
                                String _tmp_0004 = tmp_001[3];
                                String _tmp_0005 = tmp_001[4];
                                //String CreationDate = "\nCreation Date:" + _temp_[5];
                                temp_item = _tmp_0002 + "." + _tmp_0003 + "." + _tmp_0004 + "." + _tmp_0005;
                                // btn_accept.setText("Add");
                                // btn_decline.setVisibility(View.GONE);
                                //customImageView.setVisibility(View.GONE);
                                al_.add(temp_item);
                            }
                        }
                        index++;
                    }

                } else {
                    String[] _temp_0 = str_frombuider.split("~::~");
                    if (_temp_0.length > 4) {
                        String group_name = _temp_0[1];
                        String peer_name = _temp_0[2];
                        String peer_nick = _temp_0[3];
                        String date = _temp_0[4];
                        //String CreationDate = "\nCreation Date:" + _temp_[5];
                        temp_item = group_name + "\n" + peer_name + " " + peer_nick + "\n " + date;
                        al_.add(temp_item);
                    }

                }
            }


            if (al_.isEmpty() || al_ == null)
                al_.add("#Requesting UCIs 0");

            //contexter_0_0@miun.se/mobi:201~::~Dexter ~::~Contexter_0_0~::~contexter ~::~2016-12-11 13:33:56
            list_adapter = new ListAdapter(this, 0, true, al_, 19);
            lv.setAdapter(list_adapter);
            Iterator itr = bootstraps.iterator();
            Map.Entry me;
            String ip_ = "";
            while (itr.hasNext()) {
                ip_ = (String) itr.next();
                bs_map.put(ip_, ip_);
            }
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    final TextView tv_bootstrap_ip = (TextView) view.findViewById(R.id.btn_m2m_btn);
                    final TextView tv_select_btn = (TextView) view.findViewById(R.id.btn_m2m_on_off_btn);
                    // displayCustomizedToast(GroupContainerActivity.this, "Tap 'Add' Button");
                    tv_select_btn.setOnClickListener(new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {
                                                             Matcher matcher, matcher_public_ip;
                                                             if (bootstraps == null)
                                                                 bootstraps = bs_map.keySet();
                                                             String temp_ip_address = tv_bootstrap_ip.getText().toString();
                                                             matcher = pattern.matcher(temp_ip_address);
                                                             if (matcher.matches() || (temp_ip_address.trim().isEmpty() && bootstraps.size() > 0)) {
                                                                 PREFERRED_IP = temp_ip_address;
                                                                 if (temp_ip_address.trim().isEmpty() && bootstraps.size() > 0) {
                                                                     bs_map.remove(PREFERRED_IP);
                                                                     bs_map.put(PREFERRED_IP, PREFERRED_IP);
                                                                 } else {
                                                                     bs_map.put(temp_ip_address, temp_ip_address);
                                                                     PREFERRED_IP = temp_ip_address;
                                                                 }
                                                                 bootstraps = bs_map.keySet();
                                                                 matcher_public_ip = pattern_public.matcher(temp_ip_address);
                                                                 if (matcher_public_ip.matches())
                                                                     _BOOTSTRAP_IP_ = true;

                                                                 SharedPreferences.Editor ed = mPrefs_.edit();
                                                                 ed.putStringSet("bootstraps", bootstraps);
                                                                 ed.putBoolean("BOOTSTRAP_IP", _BOOTSTRAP_IP_);
                                                                 ed.putString("PREFERRED_IP", PREFERRED_IP);
                                                                 ed.commit();

                                                                 System.out.println("__________________BootstarpIP:" + PREFERRED_IP);
                                                                 displayCustomizedToast(getApplicationContext(), "Bootstrap Added");
                                                                 Intent intent = new Intent(GroupContainerActivity.this,
                                                                         AddBootStrapActivity.class);
                                                                 startActivity(intent);
                                                             } else {
                                                                 displayCustomizedToast(GroupContainerActivity.this, "Invalid IP. BootStrap List not Updated!");
                                                                 System.out.println("__________________BootstarpIP:" + PREFERRED_IP);
                                                             }


                                                         }
                                                     }

                    );


                }

            });
        } else if (container_cat.equalsIgnoreCase("M_AND_C")) {

            // Keep the orientation portrait
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            // Swedish
            if (custom.getLanguage() == 1) {

                String[] settings = getResources().getStringArray(R.array.settings_sv);
                tv.setText(settings[6]);

            }
            // Spanish
            else if (custom.getLanguage() == 2) {

                String[] settings = getResources().getStringArray(R.array.settings_sp);
                tv.setText(settings[6]);
            }
            // Portugese
            else if (custom.getLanguage() == 3) {

                String[] settings = getResources().getStringArray(R.array.settings_pr);
                tv.setText(settings[6]);
            }
            // French
            else if (custom.getLanguage() == 4) {

                String[] settings = getResources().getStringArray(R.array.settings_fr);
                tv.setText(settings[6]);

            }
            // Amharic
            else if (custom.getLanguage() == 5) {
                String[] settings = getResources().getStringArray(R.array.settings_am);
                tv.setText(settings[6]);
            }
            // English
            else {
                String[] settings = getResources().getStringArray(R.array.settings_en);
                tv.setText(settings[6]);
            }

            TreeMap<String, ArrayList<String>> tm = new TreeMap<String, ArrayList<String>>();
            ArrayList<String> al_;
            int _counter_ = 0;
            // Sample data
            //al_.add("#0 Message");
            //al_.add("#1 Message");
            //  tm.put("Tabber", al_);
            //   tm.put("Phoner", al_);


            PrivateChats _thread_0;
            String group_name, peer_name, file_name;
            File[] private_message__dir_files = getPrivateMsgfromDir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/local/");
            for (File f : private_message__dir_files)
            // replace prchat instead
            {
                al_ = new ArrayList<String>();
                //al_.add("#0 Message");
                //al_.add("#1 Message");

                file_name = f.getName().replace(".", ":");
                if (file_name.split(":").length == 3) {
                    group_name = file_name.split(":")[0];
                    peer_name = file_name.split(":")[1];
                    _thread_0 = new PrivateChats(getBaseContext(), group_name, peer_name);
                    ArrayList<String> temp_ar = _thread_0.getmPrivateChat(group_name);
                    TreeMap<Integer, String> seq_map = new TreeMap<Integer, String>();

                    Set set = seq_map.entrySet();
                    Iterator itr = set.iterator();

                    for (String str : temp_ar) {
                        if (temp_ar.size() - temp_ar.indexOf(str) < 25) al_.add(str);
                    }


                    tm.put(f.getName().replace(".prchat", "").replace(".", "#"), al_);
                }

            }


            lv.setVisibility(View.GONE);
            exp_lv.setVisibility(View.VISIBLE);
            ImageExpandableAdapter imageExpandableAdapter = new ImageExpandableAdapter(this, tm, 0);
            exp_lv.setAdapter(imageExpandableAdapter);

        } else if (container_cat.equalsIgnoreCase("GROUPS")) {
            ArrayList<String> al_ = new ArrayList<String>();

            // Swedish
            if (custom.getLanguage() == 1) {
                String[] settings = getResources().getStringArray(R.array.settings_sv);
                tv.setText(settings[8]);
                al_.add(getResources().getString(R.string.string_group_container_0_sv));
                al_.add(getResources().getString(R.string.string_group_container_1_sv));
                al_.add(getResources().getString(R.string.string_group_container_2_sv));
                al_.add(getResources().getString(R.string.string_group_container_3_sv));


            }
            // Spanish
            else if (custom.getLanguage() == 2) {
                String[] settings = getResources().getStringArray(R.array.settings_sp);
                tv.setText(settings[8]);
                al_.add(getResources().getString(R.string.string_group_container_0_sp));
                al_.add(getResources().getString(R.string.string_group_container_1_sp));
                al_.add(getResources().getString(R.string.string_group_container_2_sp));
                al_.add(getResources().getString(R.string.string_group_container_3_sp));
            }
            // Portugese
            else if (custom.getLanguage() == 3) {

                String[] settings = getResources().getStringArray(R.array.settings_pr);
                tv.setText(settings[8]);
                al_.add(getResources().getString(R.string.string_group_container_0_pr));
                al_.add(getResources().getString(R.string.string_group_container_1_pr));
                al_.add(getResources().getString(R.string.string_group_container_2_pr));
                al_.add(getResources().getString(R.string.string_group_container_3_pr));
            }
            // French
            else if (custom.getLanguage() == 4) {

                String[] settings = getResources().getStringArray(R.array.settings_fr);
                tv.setText(settings[8]);
                al_.add(getResources().getString(R.string.string_group_container_0_fr));
                al_.add(getResources().getString(R.string.string_group_container_1_fr));
                al_.add(getResources().getString(R.string.string_group_container_2_fr));
                al_.add(getResources().getString(R.string.string_group_container_3_fr));

            }
            // Amharic
            else if (custom.getLanguage() == 5) {
                String[] settings = getResources().getStringArray(R.array.settings_am);
                tv.setText(settings[8]);
                al_.add("New Group_AM");
                al_.add("Update Group_AM");
                al_.add("Delete Group_AM");
                al_.add("View   Group_AM");
            }
            // English
            else {
                String[] settings = getResources().getStringArray(R.array.settings_en);
                tv.setText(settings[8]);
                al_.add(getResources().getString(R.string.string_group_container_0));
                al_.add(getResources().getString(R.string.string_group_container_1));
                al_.add(getResources().getString(R.string.string_group_container_2));
                al_.add(getResources().getString(R.string.string_group_container_3));
            }

            list_adapter = new ListAdapter(this, 0, true, al_, 12);
            lv.setAdapter(list_adapter);
            lv.setSelector(R.color.background_color_1);
            lv.setDrawSelectorOnTop(false);
            lv.setVisibility(View.VISIBLE);


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent;
                    if (position == 0) {
                        intent = new Intent(GroupContainerActivity.this, CreateGroupActivity.class);
                        intent.putExtra("INDEX", "Create");
                        startActivity(intent);
                    } else if (position == 1) {
                        DialogOne dialog_one = new DialogOne(GroupContainerActivity.this, false, 17);
                        dialog_one.setDialog_message("Update");
                        dialog_one.show();

                    } else if (position == 2) {

                        DialogOne dialog_one = new DialogOne(GroupContainerActivity.this, false, 17);
                        dialog_one.setDialog_message("Delete");
                        dialog_one.show();

                        // startActivity(intent);
                    } else if (position == 3) {
                        // startActivity(intent);
                        Groups _groups_0 = new Groups(getBaseContext());
                        ArrayList<String> ar_groups = _groups_0.getmGroups();
                        String group_val = "";
                        for (String tmp : ar_groups) {
                            if (tmp.contains(":::")) {
                                String _temp_0 = tmp.split(":::")[1];
                                if (_temp_0.contains("~::~")) {
                                    String[] _temp_ = _temp_0.split("~::~");
                                    if (_temp_.length > 4) {
                                        String group_name = "Name:     " + _temp_[0];
                                        String group_intereste = "\nGroup Interest:   " + _temp_[1];
                                        String group_leader = "\nLeader:    " + _temp_[2];
                                        String group_age_limit = "\nAge Limit: " + _temp_[4] + "(+)";
                                        String CreationDate = "\nCreation Date:   " + _temp_[5];
                                        group_val = group_val + group_name + group_intereste + group_leader + group_age_limit + CreationDate + "\n\n";
                                    }

                                }
                            }

                        }
                        intent = new Intent(GroupContainerActivity.this, ErrorActivity.class);
                        intent.putExtra("error", "GROUPS");
                        intent.putExtra("GROUPS", group_val);
                        startActivity(intent);

                    }
                }
            });
        } else if (container_cat.equalsIgnoreCase("UCIs")) {
            ArrayList<String> al_ = new ArrayList<String>();

            // Swedish
            if (custom.getLanguage() == 1) {
                String[] settings = getResources().getStringArray(R.array.menu_array_sv);
                tv.setText(settings[2]);
                al_.add(getResources().getString(R.string.action_register_sv));
                al_.add(getResources().getString(R.string.all_cleints_sv));
            }
            // Spanish
            else if (custom.getLanguage() == 2) {
                String[] settings = getResources().getStringArray(R.array.menu_array_sp);
                tv.setText(settings[2]);
                al_.add(getResources().getString(R.string.action_register_sp));
                al_.add(getResources().getString(R.string.all_cleints_sp));
            }
            // Portugese
            else if (custom.getLanguage() == 3) {

                String[] settings = getResources().getStringArray(R.array.menu_array_pr);
                tv.setText(settings[2]);
                al_.add(getResources().getString(R.string.action_register_pr));
                al_.add(getResources().getString(R.string.all_cleints_pr));
            }
            // French
            else if (custom.getLanguage() == 4) {

                String[] settings = getResources().getStringArray(R.array.menu_array_fr);
                tv.setText(settings[2]);
                al_.add(getResources().getString(R.string.action_register_fr));
                al_.add(getResources().getString(R.string.all_cleints_fr));
            }
            // Amharic
            else if (custom.getLanguage() == 5) {
                String[] settings = getResources().getStringArray(R.array.menu_array_am);
                tv.setText(settings[2]);
                al_.add("Register UCI");
                al_.add("All Peers");
            }
            // English
            else {
                String[] settings = getResources().getStringArray(R.array.menu_array_en);
                tv.setText(settings[2]);
                al_.add(getResources().getString(R.string.action_register));
                al_.add(getResources().getString(R.string.all_cleints));
            }

            list_adapter = new ListAdapter(this, 0, true, al_, 12);
            lv.setAdapter(list_adapter);
            lv.setSelector(R.color.chitchato_mebratu_blue);
            lv.setDrawSelectorOnTop(false);
            lv.setVisibility(View.VISIBLE);


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent;
                    if (position == 0) {
                        intent = new Intent(GroupContainerActivity.this,
                                RegisterActivity.class);
                        intent.putExtra("INDEX", "Create");
                        startActivity(intent);
                    } else if (position == 1) {
                        intent = new Intent(GroupContainerActivity.this, ContactsListActivity.class);
                        startActivity(intent);
                    }
                }
            });
        } else if (container_cat.equalsIgnoreCase("SEARCH")) {
            ArrayList<String> al_ = new ArrayList<String>();

            // Swedish
            if (custom.getLanguage() == 1) {
                String[] settings = getResources().getStringArray(R.array.menu_array_sv);
                tv.setText(settings[3]);
                al_.add(getResources().getString(R.string.search_group_sv));
                al_.add(getResources().getString(R.string.advanced_search_sv));
            }
            // Spanish
            else if (custom.getLanguage() == 2) {
                String[] settings = getResources().getStringArray(R.array.menu_array_sp);
                tv.setText(settings[3]);
                al_.add(getResources().getString(R.string.search_group_sp));
                al_.add(getResources().getString(R.string.advanced_search_sp));
            }
            // Portugese
            else if (custom.getLanguage() == 3) {

                String[] settings = getResources().getStringArray(R.array.menu_array_pr);
                tv.setText(settings[3]);
                al_.add(getResources().getString(R.string.search_group_pr));
                al_.add(getResources().getString(R.string.advanced_search_pr));
            }
            // French
            else if (custom.getLanguage() == 4) {

                String[] settings = getResources().getStringArray(R.array.menu_array_fr);
                tv.setText(settings[3]);
                al_.add(getResources().getString(R.string.search_group_fr));
                al_.add(getResources().getString(R.string.advanced_search_fr));
            }
            // Amharic
            else if (custom.getLanguage() == 5) {
                String[] settings = getResources().getStringArray(R.array.menu_array_am);
                tv.setText(settings[3]);
                al_.add("Search");
                al_.add("Advanced");
            }
            // English
            else {
                String[] settings = getResources().getStringArray(R.array.menu_array_en);
                tv.setText(settings[3]);
                al_.add(getResources().getString(R.string.action_search));
                al_.add(getResources().getString(R.string.advanced_search));
            }

            list_adapter = new ListAdapter(this, 0, true, al_, 120);
            lv.setAdapter(list_adapter);
            lv.setSelector(R.color.chitchato_mebratu_blue);
            lv.setDrawSelectorOnTop(false);
            lv.setVisibility(View.VISIBLE);


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent;
                    if (position == 0) {
                        intent = new Intent(GroupContainerActivity.this,
                                SearchGroupActivity.class);
                        intent.putExtra("INDEX", "NOR_SEARCH");
                        startActivity(intent);
                    } else if (position == 1) {
                        intent = new Intent(GroupContainerActivity.this,
                                SearchGroupActivity.class);
                        intent.putExtra("INDEX", "ADV_SEARCH");
                        startActivity(intent);
                    }
                }
            });
        } else if (container_cat.equalsIgnoreCase("Sense")) {

            ArrayList<String> al_ = new ArrayList<String>();

            // Swedish
            if (custom.getLanguage() == 1) {

                String[] settings = getResources().getStringArray(R.array.settings_sv);
                tv.setText(getResources().getString(R.string.action_sensor_sv));
                al_.add(getResources().getString(R.string.sensor_readings_sv));
                al_.add(getResources().getString(R.string.contexter_readings_sv));
            }
            // Spanish
            else if (custom.getLanguage() == 2) {

                String[] settings = getResources().getStringArray(R.array.settings_sp);
                tv.setText(getResources().getString(R.string.action_sensor_sp));
                al_.add(getResources().getString(R.string.sensor_readings_sp));
                al_.add(getResources().getString(R.string.contexter_readings_sp));
            }
            // Portugese
            else if (custom.getLanguage() == 3) {

                String[] settings = getResources().getStringArray(R.array.settings_pr);
                tv.setText(getResources().getString(R.string.action_sensor_pr));
                al_.add(getResources().getString(R.string.sensor_readings_pr));
                al_.add(getResources().getString(R.string.contexter_readings_pr));
            }
            // French
            else if (custom.getLanguage() == 4) {

                String[] settings = getResources().getStringArray(R.array.settings_fr);
                tv.setText(getResources().getString(R.string.action_sensor_fr));
                al_.add(getResources().getString(R.string.sensor_readings_fr));
                al_.add(getResources().getString(R.string.contexter_readings_fr));

            }
            // Amharic
            else if (custom.getLanguage() == 5) {
                String[] settings = getResources().getStringArray(R.array.settings_am);
                tv.setText(getResources().getString(R.string.action_sensor_am));
                al_.add("Sensor Readings");
                al_.add("Contexter Readings");
            }
            // English
            else {
                String[] settings = getResources().getStringArray(R.array.settings_en);
                tv.setText(getResources().getString(R.string.action_sensor_en));
                al_.add(getResources().getString(R.string.sensor_readings));
                al_.add(getResources().getString(R.string.contexter_readings));
            }
            list_adapter = new ListAdapter(this, 0, true, al_, 16);
            lv.setAdapter(list_adapter);
            lv.setSelector(R.color.chitchato_mebratu_blue);
            lv.setDrawSelectorOnTop(false);
            lv.setVisibility(View.VISIBLE);


            lv.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent;
                            if (position == 0) {
                                intent = new Intent(GroupContainerActivity.this, SensorReadingsActivity.class);
                                startActivity(intent);
                            } else if (position == 1) {
                                intent = new Intent(GroupContainerActivity.this, ErrorActivity.class);
                                intent.putExtra("error", "CON_READINGS");
                                startActivity(intent);
                            }
                        }
                    });
        } else if (container_cat.equalsIgnoreCase("OTHER")) {

        } else

        {
            ArrayList<String> al_ = new ArrayList<String>();
            al_.add("");
            list_adapter = new ListAdapter(this, 0, true, al_, 12);
            lv.setAdapter(list_adapter);

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
                intent = new Intent(GroupContainerActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_advanced:
                intent = new Intent(GroupContainerActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(GroupContainerActivity.this,
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
        // setContentView(custom.getLanguage());
        // Create the platform itself
        // if (sensibleThingsPlatform == null) {
        // sensibleThingsPlatform = new SensibleThingsPlatform(this);
        // }
    }

    @Override
    protected void onStop() {
        super.onStop();

        NotificationManager _notification_manager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        _notification_manager.cancel(0);
        SharedPreferences.Editor ed = mPrefs.edit();
        String language = "En";
        if (lang_number == 0)
            language = "En";
        else if (lang_number == 1)
            language = "Sv";
        else if (lang_number == 2)
            language = "Sp";
        else if (lang_number == 3)
            language = "Pr";
        else if (lang_number == 4)
            language = "Fr";
        else if (lang_number == 5)
            language = "Am";

        // ed.putString("language", language);
        // ed.commit();

        //
        mPrefs = getSharedPreferences("cache", 0);
        ed = mPrefs.edit();
        ed.putBoolean("LA_VISITED", true);
        ed.commit();
        // this.finishAffinity();

    }

    public int getLanguage(String lan) {
        int lang_serial = 0;
        if (lan.contains("Sv")) {
            lang_serial = 1;
        } else if (lan.contains("Sp")) {
            lang_serial = 2;
        } else if (lan.contains("Pr")) {
            lang_serial = 3;
        } else if (lan.contains("Fr")) {
            lang_serial = 4;
        } else if (lan.contains("Am")) {
            lang_serial = 5;
        } else ;
        return lang_serial;
    }


    public void displayCustomizedToast(Context _context_, String message) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_ = inflater.inflate(R.layout.dialog_one, null);
        TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);
        tv_0_0_.setText(message);
        Toast toast = new Toast(_context_);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view_);
        toast.show();
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

    @Override
    public void onBackPressed() {

        if (container_cat.equalsIgnoreCase("Sense") || container_cat.equalsIgnoreCase("UCIs") || container_cat.equalsIgnoreCase("GROUPS") || container_cat.equalsIgnoreCase("SEARCH")) {
            Intent intent = new Intent(this, ChoicesActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MoreActivity.class);
            startActivity(intent);
        }

        return;
    }


}
