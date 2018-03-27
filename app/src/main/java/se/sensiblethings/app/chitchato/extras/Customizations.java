package se.sensiblethings.app.chitchato.extras;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.fitness.data.Device;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.kernel.Groups;
import se.sensiblethings.app.chitchato.kernel.PublicChats;

public class Customizations {

    protected SharedPreferences mPrefs, mPrefs_, mPrefs__, mPrefs___;
    protected Typeface tf = null;
    protected View view = null;
    // default language english
    protected int language_no = 0;
    protected Context context;
    protected ArrayList<String> _mFavoriteGroups = new ArrayList<String>();
    //_BOOTSTRAP_IP_===> public ip
    protected boolean _BOOTSTRAP_IP_ = false;
    protected String preferred_bs_ip = "";
    // Is the Node itself bootstrap
    protected boolean _IS_NODE_BOOTSTRAP_ = false;
    int style = 0;
    String language = "en";
    LayoutInflater layout_inflater;
    boolean LOCAL_PEER_REGISTERED = true;
    String peer_name;
    String peer_nick_name;
    // peer_age = Integer.parseInt(mPrefs.getString("peer_age", "-1"));
    String peer_sex;
    String peer_age;
    String profile_image_uri = "#Nothing#";
    String _image_message_uri = "#Nothing";

    // Choices Menu -- Change defaults
    String context_image_message_uri = "#Nothing#";
    String group_image_message_uri = "#Nothing#";
    String uci_image_message_uri = "#Nothing#";
    String search_image_message_uri = "#Nothing#";
    String help_image_message_uri = "#Nothing#";
    String abt_image_message_uri = "#Nothing#";
    String lang_image_message_uri = "#Nothing#";

    boolean PROFILE_IMAGE_CHANGED = false;
    boolean _IMAGE_SEND_ = false;
    boolean _NEW_INFO_AVAILABLE_ = false;
    boolean _NOTIFICATIONS_AVAILABLE = false;
    boolean _MESSAGES_AVAILABLE = false;
    int pri_message_thread_len = 100;
    int pub_message_thread_len = 100;
    String profile_group_uci = "#Arsenal#";
    //
    Button btn_1, btn_2, btn_3, btn_4, btn_5;
    ImageButton img_btn_1;
    TextView tv_1, tv_2, tv_3, tv_4, tv_5, tv_6, tv_7;
    EditText edt_1, edt_2, edt_3, edt_4, edt_5;
    RadioButton rdio_btn_1, rdio_btn_2, rdio_btn_3, rdio_btn_4;


    // states of the service and the platform
    boolean FLAG_DISP_NO_IP = true;
    boolean IS_SERVICE_UP = false;
    boolean IS_ST_UP = false;
    int PLATFORM_STARTUP_TRIAL = 0;// tells for how long the platform is trying to connect --reset to zero in the running state
    boolean IS_FIRST_TIME_LOGIN = true;
    String CONTEXTER_ADVS = "";
    // Other Settings //
    boolean isSilent_One = false;
    boolean isSilent_Two = false;
    boolean isSilent_Three = false;
    boolean isNoPattern = false;
    boolean isNoNotification = false;
    boolean isHotSpotON = false;
    private boolean aBoolean;
    //

    public Customizations() {
        mPrefs = context.getSharedPreferences("language", 0);

        // Local peer---> to be reviewed
        mPrefs_ = context.getSharedPreferences("myprofile", 0);
        // CHAT_MODE = mPrefs.getBoolean("OChat", true);
    }

    public Customizations(Context c, int val) {

        this.context = c;
        // this.language_no = val;
        mPrefs = context.getSharedPreferences("language", 0);
        mPrefs_ = context.getSharedPreferences("myprofile", 0);
        mPrefs__ = context.getSharedPreferences("groups_cache", 0);
        mPrefs___ = context.getSharedPreferences("bootstraps", 0);


        language = mPrefs.getString("language", "En");

        peer_name = mPrefs_.getString("peer_name", "context 0_0" + Device.CREATOR);
        peer_nick_name = mPrefs_
                .getString("peer_nick_name", "contexter_0_0");
        // peer_age = Integer.parseInt(mPrefs.getString("peer_age", "-1"));
        peer_sex = mPrefs_.getString("peer_sex", "unknown");
        profile_group_uci = mPrefs_.getString("peer_group", "#Nothing#");
        profile_image_uri = mPrefs_.getString("URI", profile_image_uri);
        _image_message_uri = mPrefs_.getString("URI-IMAGE", _image_message_uri);
        LOCAL_PEER_REGISTERED = mPrefs_.getBoolean("LOCAL_PEER_REGISTERED",
                true);
        PROFILE_IMAGE_CHANGED = mPrefs_.getBoolean("PROFILE_CHANGED", false);
        _IMAGE_SEND_ = mPrefs_.getBoolean("_IMAGE_SEND_", false);

        _NEW_INFO_AVAILABLE_ = mPrefs_.getBoolean("_NEW_INFO_AVAILABLE_", false);
        _NOTIFICATIONS_AVAILABLE = mPrefs_.getBoolean("_NOTIFICATIONS_AVAILABLE", false);
        _MESSAGES_AVAILABLE = mPrefs_.getBoolean("_MESSAGES_AVAILABLE", false);

        pri_message_thread_len = mPrefs_.getInt("PRIVATE_MESSAGE_THREAD_LEN", 99);
        pub_message_thread_len = mPrefs_.getInt("PUBLIC_MESSAGE_THREAD_LEN", 99);

        //FLAG_DISP_NO_IP = mPrefs_.getBoolean("DISP_NO_IP", true);
        IS_SERVICE_UP = mPrefs_.getBoolean("IS_SERVICE_UP", false);
        IS_ST_UP = mPrefs_.getBoolean("IS_ST_UP", false);
        IS_FIRST_TIME_LOGIN = mPrefs_.getBoolean("IS_FIRST_TIME_LOGIN", true);
        PLATFORM_STARTUP_TRIAL = mPrefs_.getInt("PLATFORM_STARTUP_TRIAL", 0);
        //PLATFORM_STARTUP_TRIAL = 0;// 0 --whenever the app starts

        _BOOTSTRAP_IP_ = mPrefs___.getBoolean("BOOTSTRAP_IP", false);

        preferred_bs_ip = mPrefs___.getString("PREFERRED_IP", "193.10.119.42");

        _IS_NODE_BOOTSTRAP_ = mPrefs___.getBoolean("_IS_NODE_BOOTSTRAP_", false);
        CONTEXTER_ADVS = mPrefs_.getString("CONTEXTER_ADVS", CONTEXTER_ADVS);

        isSilent_One = mPrefs_.getBoolean("SILENT_ONE", false);
        isSilent_Two = mPrefs_.getBoolean("SILENT_TWO", false);
        isSilent_Three = mPrefs_.getBoolean("SILENT_THREE", false);
        isNoPattern = mPrefs_.getBoolean("NO_PATTERN", false);
        isNoNotification = mPrefs_.getBoolean("NO_NOTIFICATION", false);


        // Choices Menu--- default images
        context_image_message_uri = mPrefs_.getString("CON-URI-IMAGE", context_image_message_uri);
        group_image_message_uri = mPrefs_.getString("GRP-URI-IMAGE", group_image_message_uri);
        uci_image_message_uri = mPrefs_.getString("UCI-URI-IMAGE", uci_image_message_uri);
        search_image_message_uri = mPrefs_.getString("SRC-URI-IMAGE", search_image_message_uri);
        help_image_message_uri = mPrefs_.getString("HELP-URI-IMAGE", help_image_message_uri);
        abt_image_message_uri = mPrefs_.getString("ABT-URI-IMAGE", abt_image_message_uri);
        lang_image_message_uri = mPrefs_.getString("LNG-URI-IMAGE", lang_image_message_uri);



        // Fetch Favorite Groups from Local Machine
        Set<String> temp_set_ = mPrefs__.getStringSet("FAV_GROUPS", null);
        if (temp_set_ != null) {
            TreeSet<String> sortedKeys = new TreeSet<String>(temp_set_);
            for (String k : sortedKeys) {
                _mFavoriteGroups.add(k);
            }
        }

        if (val == -1)
            this.language_no = this.getLanguage(language);
        else
            this.language_no = val;
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/COMIC.TTF");

        TreeMap bs_map = new TreeMap<String, String>();
        Set<String> bootstraps = mPrefs___.getStringSet("bootstraps", bs_map.keySet());
        Iterator itr = bootstraps.iterator();

        if (bootstraps.size() == 0) {
            setFLAG_DISP_NO_IP(false);
            SharedPreferences.Editor ed = mPrefs.edit();
            ed.putBoolean("FLAG_DISP_NO_IP", false);
            ed.commit();
        }
        if (CONTEXTER_ADVS.contains("~::~")) {
            String[] temp = CONTEXTER_ADVS.split("~::~");
            if (temp.length > 20) {
                CONTEXTER_ADVS = " ";
                setFLAG_DISP_NO_IP(false);
                SharedPreferences.Editor ed = mPrefs_.edit();
                ed.putString("CONTEXTER_ADVS", CONTEXTER_ADVS);
                ed.commit();
            }
        }


        if (getPeer_nick_name().equalsIgnoreCase("contexter_0_0") || getPeer_nick_name().startsWith("@")) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String fl_nm = telephonyManager.getSimSerialNumber();
            if (fl_nm == null) {
                fl_nm = "00000";
            }
            PublicChats _thread_0 = new PublicChats(context, fl_nm, "nicks");
            ArrayList<String> temp_ar = _thread_0.getmPublicChat("*ALL#");
            for (String tmp_000 : temp_ar) {
                if (tmp_000.contains(":::") && tmp_000.contains("~::~")) {
                    String t = tmp_000.split(":::")[1];
                    String[] tt = t.split("~::~");
                    setPeer_nick_name(tt[0]);
                    setPeer_name(tt[1]);
                    setPeer_age(tt[3]);
                    setPeer_sex(tt[2]);
                    setProfile_image_uri(tt[4]);
                }
            }


        }

        final WifiApiManager wifiApManager = new WifiApiManager(context);
        // wifiApManager.setWifiApConfiguration(null);
        setHotSpotStatus(wifiApManager.isWifiApEnabled());


    }

    public ArrayList<String> get_mFavoriteGroups() {
        return _mFavoriteGroups;
    }

    public void set_mFavoriteGroups(ArrayList<String> _mFavoriteGroups) {
        this._mFavoriteGroups = _mFavoriteGroups;
    }

    public ArrayList getMyGroups(Context c) {
        this.context = c;
        Groups groups = new Groups(this.context, "", true);
        ArrayList<String> al_ = groups.getmGroups();
        return al_;
    }

    public String getPasswordString(Context c, ArrayList<String> _ar_, String groupName) {
        String pass_str = "#";

        for (int i = 0; i < _ar_.size(); i++) {
            if (_ar_.get(i).contains(":::")) {
                String _temp_0 = _ar_.get(i).split(":::")[1];
                if (_temp_0.contains("~::~")) {
                    String[] _temp_ = _temp_0.split("~::~");
                    if (_temp_.length > 4) {
                        String group_name = "Group Name:       " + _temp_[0];
                        String group_intereste = "\nGroup Interest:   " + _temp_[1];
                        String group_leader = "\nGroup Leader:    " + _temp_[2];
                        String group_password = "\nGroup Password: " + _temp_[3];
                        String group_age_limit = "\nGroup Age Limit: " + _temp_[4];
                        String CreationDate = "\nCreation Date:   " + _temp_[5];
                        pass_str = group_password;
                    }

                }
            }
        }

        return pass_str;


    }

    public View getWelComeScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.welcomscreen, null);
        btn_1 = (Button) view.findViewById(R.id.btn_welcome_);
        tv_1 = (TextView) view.findViewById(R.id.tv_welcomescreen);
        tv_2 = (TextView) view.findViewById(R.id.tv_welcomescreen_2);
        rdio_btn_1 = (RadioButton) view
                .findViewById(R.id.radio0_ordinary_chat_mode);
        rdio_btn_2 = (RadioButton) view
                .findViewById(R.id.radio1_context_chat_mode);

        // Swedish
        if (this.language_no == 1) {
            btn_1.setText(R.string.send_btn_sv);
            tv_1.setText(R.string.Welcome_tv_sv);
            rdio_btn_1.setText(R.string.ordinary_chat_text_sv);
            rdio_btn_2.setText(R.string.context_chat_text_sv);
        }
        // Spanish
        else if (this.language_no == 2) {
            btn_1.setText(R.string.send_btn_sp);
            tv_1.setText(R.string.Welcome_tv_sp);
            rdio_btn_1.setText(R.string.ordinary_chat_text_sp);
            rdio_btn_2.setText(R.string.context_chat_text_sp);
        }
        // Portugese
        else if (this.language_no == 3) {
            btn_1.setText(R.string.send_btn_pr);
            tv_1.setText(R.string.Welcome_tv_pr);
            rdio_btn_1.setText(R.string.ordinary_chat_text_pr);
            rdio_btn_2.setText(R.string.context_chat_text_pr);
        }
        // French
        else if (this.language_no == 4) {
            btn_1.setText(R.string.send_btn_fr);
            tv_1.setText(R.string.Welcome_tv_fr);
            rdio_btn_1.setText(R.string.ordinary_chat_text_fr);
            rdio_btn_2.setText(R.string.context_chat_text_fr);
        }
        // Amharic
        else if (this.language_no == 5) {
            btn_1.setText(R.string.send_btn_am);
            tv_1.setText(R.string.Welcome_tv_am);
            rdio_btn_1.setText(R.string.ordinary_chat_text_am);
            rdio_btn_2.setText(R.string.context_chat_text_am);
        }


        return view;
    }

    public View getRegisterScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.registerscreen, null);

        tv_1 = (TextView) view.findViewById(R.id.tv_register_title);
        tv_2 = (TextView) view.findViewById(R.id.tv_click_profile_iamge);
        btn_1 = (Button) view.findViewById(R.id.btn_send_register);
        img_btn_1 = (ImageButton) view
                .findViewById(R.id.img_btn_profile_PIC);
        edt_1 = (EditText) view.findViewById(R.id.edt_nick_name_rgistr);
        edt_2 = (EditText) view.findViewById(R.id.edt_name_rgistr);
        edt_3 = (EditText) view.findViewById(R.id.edt_age_rgistr);
        edt_4 = (EditText) view.findViewById(R.id.edt_sex_rgistr);

        // Swedish
        if (this.language_no == 1) {

            tv_1.setText(R.string.action_register_sv);
            tv_2.setText(R.string.enter_profile_pic_sv);
            btn_1.setText(R.string.action_register_sv);
            edt_1.setHint(R.string.hint_enter_group_nickname_sv);
            edt_2.setHint(R.string.hint_enter_group_name_sv);
            edt_3.setHint(R.string.hint_enter_group_age_limit_sv);
            edt_4.setHint(R.string.sex_sv);

        }
        // Spanish
        else if (this.language_no == 2) {

            tv_1.setText(R.string.action_register_sp);
            tv_2.setText(R.string.enter_profile_pic_sp);
            btn_1.setText(R.string.action_register_sp);
            edt_1.setHint(R.string.hint_enter_group_nickname_sp);
            edt_2.setHint(R.string.hint_enter_group_name_sp);
            edt_3.setHint(R.string.hint_enter_group_age_limit_sp);
            edt_4.setHint(R.string.sex_sp);
        }
        // Portugese
        else if (this.language_no == 3) {

            tv_1.setText(R.string.action_register_pr);
            tv_2.setText(R.string.enter_profile_pic_pr);
            btn_1.setText(R.string.action_register_pr);
            edt_1.setHint(R.string.hint_enter_group_nickname_pr);
            edt_2.setHint(R.string.hint_enter_group_name_pr);
            edt_3.setHint(R.string.hint_enter_group_age_limit_pr);
            edt_4.setHint(R.string.sex_pr);
        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.action_register_fr);
            tv_2.setText(R.string.enter_profile_pic_fr);
            btn_1.setText(R.string.action_register_fr);
            edt_1.setHint(R.string.hint_enter_group_nickname_fr);
            edt_2.setHint(R.string.hint_enter_group_name_fr);
            edt_3.setHint(R.string.hint_enter_group_age_limit_fr);
            edt_4.setHint(R.string.sex_fr);

        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.action_register_am);
            tv_2.setText(R.string.enter_profile_pic_am);
            btn_1.setText(R.string.action_register_am);
            edt_1.setHint(R.string.hint_enter_group_nickname_am);
            edt_2.setHint(R.string.hint_enter_group_name_am);
            edt_3.setHint(R.string.hint_enter_group_age_limit_am);
            edt_4.setHint(R.string.sex_am);

        }

        return view;
    }

    public View getChoicesScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.choicescreen, null);

        tv_1 = (TextView) view.findViewById(R.id.tv_empty_bar_choicescreen);

        btn_1 = (Button) view
                .findViewById(R.id.button_choicescreen_profile_edit);
        btn_2 = (Button) view.findViewById(R.id.m2m_btn);
        btn_3 = (Button) view.findViewById(R.id.btn_sensors);
        btn_4 = (Button) view.findViewById(R.id.btn_purge);
        btn_5 = (Button) view.findViewById(R.id.img_btn_debug);
        img_btn_1 = (ImageButton) view.findViewById(R.id.img_btn_more_small_notification);

        if (is_NOTIFICATIONS_AVAILABLE() || is_NEW_INFO_AVAILABLE_() || is_MESSAGES_AVAILABLE()) {
            img_btn_1.setVisibility(View.VISIBLE);
        } else
            img_btn_1.setVisibility(View.INVISIBLE);

        // Swedish
        if (this.language_no == 1) {
            // tv_1.setText(R.string.menu_tv_en);
            btn_1.setText(R.string.my_profile_sv);
            btn_2.setText(R.string.control_sv);
            btn_3.setText(R.string.action_sense_sv);
            btn_4.setText(R.string.action_advanced_sv);
            btn_5.setText(R.string.action_debug_sv);


        }
        // Spanish
        else if (this.language_no == 2) {
            // tv_1.setText(R.string.menu_tv_en);
            btn_1.setText(R.string.my_profile_sp);
            btn_2.setText(R.string.control_sp);
            btn_3.setText(R.string.action_sense_sp);
            btn_4.setText(R.string.action_advanced_sp);
            btn_5.setText(R.string.action_debug_sp);
        }
        // Portugese
        else if (this.language_no == 3) {
            // tv_1.setText(R.string.menu_tv_en);
            btn_1.setText(R.string.my_profile_pr);
            btn_2.setText(R.string.control_pr);
            btn_3.setText(R.string.action_sense_pr);
            btn_4.setText(R.string.action_advanced_pr);
            btn_5.setText(R.string.action_debug_pr);

        }
        // French
        else if (this.language_no == 4) {

            // tv_1.setText(R.string.menu_tv_en);
            btn_1.setText(R.string.my_profile_fr);
            btn_2.setText(R.string.control_fr);
            btn_3.setText(R.string.action_sense_fr);
            btn_4.setText(R.string.action_advanced_fr);
            btn_5.setText(R.string.action_debug_fr);

        }
        // Amharic
        else if (this.language_no == 5) {

            // tv_1.setText(R.string.Menu_tv_am);
            btn_1.setText(R.string.my_profile_am);
            btn_2.setText(R.string.control_am);
            btn_3.setText(R.string.action_sense_am);
            btn_4.setText(R.string.action_advanced_am);
            btn_5.setText(R.string.action_debug_am);

        }

        return view;

    }

    public View getMainScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.chatscreen, null);

        tv_1 = (TextView) view.findViewById(R.id.tv_empty_bar_choicescreen);
        btn_1 = (Button) view.findViewById(R.id.btn_send_chatscreen);
        edt_1 = (EditText) view.findViewById(R.id.edt_chatScreen);

        // Swedish
        if (this.language_no == 1) {

            btn_1.setText(R.string.send_btn_sv_);
            edt_1.setHint(R.string.hint_enter_txt_sv);

        }
        // Spanish
        else if (this.language_no == 2) {
            btn_1.setText(R.string.send_btn_sp_);
            edt_1.setHint(R.string.hint_enter_txt_sp);
        }
        // Portugese
        else if (this.language_no == 3) {
            edt_1.setHint(R.string.hint_enter_txt_pr);
            btn_1.setText(R.string.send_btn_pr_);
        }
        // French
        else if (this.language_no == 4) {

            edt_1.setHint(R.string.hint_enter_txt_fr);
            btn_1.setText(R.string.send_btn_fr_);

        }
        // Amharic
        else {

            edt_1.setHint(R.string.hint_enter_txt_en);
            btn_1.setText("Send");

        }

        return view;
    }

    public View getGroupCreationScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.creategroup, null);

        tv_1 = (TextView) view.findViewById(R.id.tv_create_text);
        btn_1 = (Button) view.findViewById(R.id.button_create_group);
        tv_2 = (TextView)view.findViewById(R.id.tv_click_profile_iamge);
        img_btn_1 = (ImageButton) view.findViewById(R.id.img_btn_profile_PIC);
        edt_1 = (EditText) view.findViewById(R.id.edt_group_name);
        edt_2 = (EditText) view.findViewById(R.id.edt_group_interest);
        edt_3 = (EditText) view.findViewById(R.id.edt_group_age_limit);
        edt_4 = (EditText) view.findViewById(R.id.edt_group_leader);
        edt_5 = (EditText) view.findViewById(R.id.edt_group_secret_code);

        // Swedish
        if (this.language_no == 1) {

            tv_1.setText(R.string.create_group_sv);
            btn_1.setText(R.string.action_create_sv);
            tv_2.setText(R.string.enter_profile_pic_sv);
            edt_1.setHint(R.string.hint_enter_group_name_sv);
            edt_2.setHint(R.string.hint_enter_group_interest_sv);
            edt_3.setHint(R.string.hint_enter_group_age_limit_sv);
            edt_4.setHint(R.string.hint_enter_group_leader_sv);
            edt_5.setHint(R.string.hint_enter_group_password_sv);

        }
        // Spanish
        else if (this.language_no == 2) {
            tv_1.setText(R.string.create_group_sp);
            btn_1.setText(R.string.action_create_sp);
            tv_2.setText(R.string.enter_profile_pic_sp);
            edt_1.setHint(R.string.hint_enter_group_name_sp);
            edt_2.setHint(R.string.hint_enter_group_interest_sp);
            edt_3.setHint(R.string.hint_enter_group_age_limit_sp);
            edt_4.setHint(R.string.hint_enter_group_leader_sp);
            edt_5.setHint(R.string.hint_enter_group_password_sp);
        }
        // Portugese
        else if (this.language_no == 3) {
            tv_1.setText(R.string.create_group_pr);
            btn_1.setText(R.string.action_create_pr);
            tv_2.setText(R.string.enter_profile_pic_pr);
            edt_1.setHint(R.string.hint_enter_group_name_pr);
            edt_2.setHint(R.string.hint_enter_group_interest_pr);
            edt_3.setHint(R.string.hint_enter_group_age_limit_pr);
            edt_4.setHint(R.string.hint_enter_group_leader_pr);
            edt_5.setHint(R.string.hint_enter_group_password_pr);
        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.create_group_fr);
            btn_1.setText(R.string.action_create_fr);
            tv_2.setText(R.string.enter_profile_pic_fr);
            edt_1.setHint(R.string.hint_enter_group_name_fr);
            edt_2.setHint(R.string.hint_enter_group_interest_fr);
            edt_3.setHint(R.string.hint_enter_group_age_limit_fr);
            edt_4.setHint(R.string.hint_enter_group_leader_fr);
            edt_5.setHint(R.string.hint_enter_group_password_fr);

        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.create_group_am);
            btn_1.setText(R.string.action_create_am);
            tv_2.setText(R.string.enter_profile_pic_am);
            edt_1.setHint(R.string.hint_enter_group_name_am);
            edt_2.setHint(R.string.hint_enter_group_interest_am);
            edt_3.setHint(R.string.hint_enter_group_age_limit_am);
            edt_4.setHint(R.string.hint_enter_group_leader_am);
            edt_5.setHint(R.string.hint_enter_group_password_am);

        }

        return view;
    }

    public View getPrivateChatScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.privatechatscreen, null);

        tv_1 = (TextView) view.findViewById(R.id.tv_pr_chat_title);
        btn_1 = (Button) view.findViewById(R.id.button1_pr_chat);
        edt_1 = (EditText) view.findViewById(R.id.editText3_pr_chat);

        // Swedish
        if (this.language_no == 1) {

            tv_1.setText(R.string.pr_chat_tv_sv);
            btn_1.setText(R.string.send_btn_sv_);
            edt_1.setHint(R.string.hint_enter_txt_sv);

        }
        // Spanish
        else if (this.language_no == 2) {

            tv_1.setText(R.string.pr_chat_tv_sp);
            btn_1.setText(R.string.send_btn_sp_);
            edt_1.setHint(R.string.hint_enter_txt_sp);
        }
        // Portugese
        else if (this.language_no == 3) {

            tv_1.setText(R.string.pr_chat_tv_pr);
            btn_1.setText(R.string.send_btn_pr_);
            edt_1.setHint(R.string.hint_enter_txt_pr);
        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.pr_chat_tv_fr);
            btn_1.setText(R.string.send_btn_fr_);
            edt_1.setHint(R.string.hint_enter_txt_fr);

        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.pr_chat_tv_am);
            btn_1.setText(R.string.send_btn_am);
            edt_1.setHint(R.string.hint_enter_txt_am);

        }

        return view;
    }

    public View getSearchGroupsScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.search, null);

        tv_1 = (TextView) view.findViewById(R.id.tv_search_title);
        btn_1 = (Button) view.findViewById(R.id.btn_send_search);

        edt_1 = (EditText) view.findViewById(R.id.edt_group_name);
        edt_2 = (EditText) view.findViewById(R.id.edt_interest_search);
        edt_3 = (EditText) view.findViewById(R.id.edt_keyword_search);
        edt_4 = (EditText) view.findViewById(R.id.edt_leader_search);
        edt_5 = (EditText) view.findViewById(R.id.edt_vicinity_name_);

        // Swedish
        if (this.language_no == 1) {

            tv_1.setText(R.string.search_group_sv);
            btn_1.setText(R.string.action_search_sv);

            edt_1.setHint(R.string.hint_enter_group_name_sv);
            edt_2.setHint(R.string.hint_enter_group_interest_sv);
            edt_3.setHint(R.string.hint_enter_group_age_limit_sv);
            edt_4.setHint(R.string.hint_enter_group_leader_sv);
            edt_5.setHint(R.string.hint_enter_group_vicinity_sv);

        }
        // Spanish
        else if (this.language_no == 2) {
            tv_1.setText(R.string.search_group_sp);

            btn_1.setText(R.string.action_search_sp);
            edt_1.setHint(R.string.hint_enter_group_name_sp);
            edt_2.setHint(R.string.hint_enter_group_interest_sp);
            edt_3.setHint(R.string.hint_enter_group_age_limit_sp);
            edt_4.setHint(R.string.hint_enter_group_leader_sp);
            edt_5.setHint(R.string.hint_enter_group_vicinity_sp);
        }
        // Portugese
        else if (this.language_no == 3) {
            tv_1.setText(R.string.search_group_pr);

            btn_1.setText(R.string.action_search_pr);
            edt_1.setHint(R.string.hint_enter_group_name_pr);
            edt_2.setHint(R.string.hint_enter_group_interest_pr);
            edt_3.setHint(R.string.hint_enter_group_age_limit_pr);
            edt_4.setHint(R.string.hint_enter_group_leader_pr);
            edt_5.setHint(R.string.hint_enter_group_vicinity_pr);
        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.search_group_fr);

            btn_1.setText(R.string.action_search_fr);
            edt_1.setHint(R.string.hint_enter_group_name_fr);
            edt_2.setHint(R.string.hint_enter_group_interest_fr);
            edt_3.setHint(R.string.hint_enter_group_age_limit_fr);
            edt_4.setHint(R.string.hint_enter_group_leader_fr);
            edt_5.setHint(R.string.hint_enter_group_vicinity_fr);

        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.search_group_am);

            btn_1.setText(R.string.action_search_am);
            edt_1.setHint(R.string.hint_enter_group_name_am);
            edt_2.setHint(R.string.hint_enter_group_interest_am);
            edt_3.setHint(R.string.hint_enter_group_age_limit_am);
            edt_4.setHint(R.string.hint_enter_group_leader_am);
            edt_5.setHint(R.string.hint_enter_group_vicinity_am);

        }

        return view;
    }

    public View getSearchResultsScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.searchresults, null);
        tv_1 = (TextView) view.findViewById(R.id.tv_search_text_results);
        btn_1 = (Button) view.findViewById(R.id.btn_search_results);
        btn_2 = (Button) view.findViewById(R.id.btn_exit_search_results);

        // Swedish
        if (this.language_no == 1) {

            tv_1.setText(R.string.search_results_tv_sv);
            btn_1.setText(R.string.add_to_favorite_groups_sv);
            btn_2.setText(R.string.exit_sv);

        }
        // Spanish
        else if (this.language_no == 2) {
            tv_1.setText(R.string.search_results_tv_sp);
            btn_1.setText(R.string.add_to_favorite_groups_sp);
            btn_2.setText(R.string.exit_sp);
        }
        // Portugese
        else if (this.language_no == 3) {

            tv_1.setText(R.string.search_results_tv_pr);
            btn_1.setText(R.string.add_to_favorite_groups_pr);
            btn_2.setText(R.string.exit_pr);
        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.search_results_tv_fr);
            btn_1.setText(R.string.add_to_favorite_groups_fr);
            btn_2.setText(R.string.exit_fr);
        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.search_results_tv_am);
            btn_1.setText(R.string.add_to_favorite_groups_am);
            btn_2.setText(R.string.exit_am);

        }

        return view;
    }

    public View getM2MScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.m2mscreen, null);
        tv_1 = (TextView) view.findViewById(R.id.tv_empty_bar_m2mscreen);
        btn_1 = (Button) view.findViewById(R.id.button_m2m_exit);

        // Swedish
        if (this.language_no == 1) {

            tv_1.setText(R.string.action_devices_sv);
            btn_1.setText(R.string.exit_sv);
        }
        // Spanish
        else if (this.language_no == 2) {

            tv_1.setText(R.string.action_devices_sp);
            btn_1.setText(R.string.exit_sp);
        }
        // Portugese
        else if (this.language_no == 3) {

            tv_1.setText(R.string.action_devices_pr);
            btn_1.setText(R.string.exit_pr);
        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.action_devices_fr);
            btn_1.setText(R.string.exit_pr);

        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.action_devices_am);
            btn_1.setText(R.string.exit_am);
        }

        return view;
    }

    public View getMoreScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.morescreen, null);
        tv_1 = (TextView) view.findViewById(R.id.tv_empty_bar_m2mscreen);
        tv_2 = (TextView) view.findViewById(R.id.tv_more_screen_one);
        tv_3 = (TextView) view.findViewById(R.id.tv_more_screen_two);
        tv_4 = (TextView) view.findViewById(R.id.tv_more_screen_three);
        tv_5 = (TextView) view.findViewById(R.id.tv_more_screen_four);
        tv_6 = (TextView) view.findViewById(R.id.tv_more_screen_one_);
        btn_1 = (Button) view.findViewById(R.id.btn_more_screen_one);



        // Swedish
        if (this.language_no == 1) {

            tv_1.setText(R.string.action_more_sv);
            btn_1.setText(R.string.exit_sv);
            tv_3.setText(R.string.social_sv);
            tv_4.setText(R.string.action_adv_group_sv);
            tv_5.setText(R.string.action_settings_sv);
            tv_6.setText("Bootstraps -" + "Över hela världen");
            btn_1.setText(R.string.exit_sv);

        }
        // Spanish
        else if (this.language_no == 2) {

            tv_1.setText(R.string.action_more_sp);
            btn_1.setText(R.string.exit_sp);
            tv_3.setText(R.string.social_sp);
            tv_4.setText(R.string.action_adv_group_sp);
            tv_5.setText(R.string.action_settings_sp);
            btn_1.setText(R.string.exit_sp);
            tv_6.setText("Bootstraps -" + "Alrededor del mundo");
        }
        // Portugese
        else if (this.language_no == 3) {

            tv_1.setText(R.string.action_more_pr);
            btn_1.setText(R.string.exit_pr);
            tv_3.setText(R.string.social_pr);
            tv_4.setText(R.string.action_adv_group_pr);
            tv_5.setText(R.string.action_settings_pr);
            tv_6.setText("Bootstraps -" + "Através do mundo");
            btn_1.setText(R.string.exit_pr);

        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.action_more_fr);
            btn_1.setText(R.string.exit_fr);
            tv_3.setText(R.string.social_fr);
            tv_4.setText(R.string.action_adv_group_fr);
            tv_5.setText(R.string.action_settings_fr);
            tv_6.setText("Bootstraps -À travers le monde");
            btn_1.setText(R.string.exit_fr);
        }

        // Amharic
        else if (this.language_no == 5) {
            tv_1.setText(R.string.action_more_am);
            btn_1.setText(R.string.exit_am);
            tv_3.setText(R.string.social_am);

            tv_5.setText(R.string.action_settings_am);
            tv_6.setText("Bootstraps -በመላው ዓለም  ");
            btn_1.setText(R.string.exit_am);
        }

        return view;
    }

    public View getHelpContainerScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.helpcontainerscreen, null);
        tv_1 = (TextView) view.findViewById(R.id.tv_empty_bar_m2mscreen);
        tv_2 = (TextView) view.findViewById(R.id.tv_more_screen_one);
        tv_3 = (TextView) view.findViewById(R.id.tv_more_screen_two);
        tv_4 = (TextView) view.findViewById(R.id.tv_more_screen_three);
        tv_5 = (TextView) view.findViewById(R.id.tv_more_screen_four);
        btn_1 = (Button) view.findViewById(R.id.btn_more_screen_one);


        // Swedish
        if (this.language_no == 1) {
            tv_1.setText(R.string.action_help_sv);
            btn_1.setText(R.string.exit_sv);

        }
        // Spanish
        else if (this.language_no == 2) {
            tv_1.setText(R.string.action_help_sp);
            btn_1.setText(R.string.exit_sp);
        }
        // Portugese
        else if (this.language_no == 3) {

            tv_1.setText(R.string.action_help_pr);
            btn_1.setText(R.string.exit_pr);
        }
        // French
        else if (this.language_no == 4) {
            tv_1.setText(R.string.action_help_fr);
            btn_1.setText(R.string.exit_fr);

        }
        // Amharic
        else if (this.language_no == 5) {
            tv_1.setText(R.string.action_help_am);
            btn_1.setText(R.string.exit_am);
        }

        return view;
    }

    public View getBSScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.add_bootstrap, null);
        tv_1 = (TextView) view.findViewById(R.id.tv_empty);
        tv_2 = (TextView) view.findViewById(R.id.tv_bootstrp_one);
        tv_3 = (TextView) view.findViewById(R.id.tv_bootstrp_two);

        btn_1 = (Button) view.findViewById(R.id.button_refresh_my_bs);
        btn_2 = (Button) view.findViewById(R.id.button_clear_my_bs);
        btn_3 = (Button) view.findViewById(R.id.button_exit_my_bs);


        // Swedish
        if (this.language_no == 1) {
            tv_1.setText(R.string.action_add_bootstrap_sv);
            tv_2.setText(R.string.action_bootstrap_sv);
            tv_3.setText(R.string.action_bootstrap_ip_sv);
            btn_1.setText(R.string.update_sv);
            btn_2.setText(R.string.delete_sv);
            btn_3.setText(R.string.exit_sv);

        }
        // Spanish
        else if (this.language_no == 2) {

            tv_1.setText(R.string.action_add_bootstrap_sp);
            tv_2.setText(R.string.action_bootstrap_ip_sp);
            tv_3.setText(R.string.action_bootstrap_sp);

            btn_1.setText(R.string.update_sp);
            btn_2.setText(R.string.delete_sp);
            btn_3.setText(R.string.exit_sp);
        }
        // Portugese
        else if (this.language_no == 3) {

            tv_1.setText(R.string.action_add_bootstrap_pr);
            tv_2.setText(R.string.action_bootstrap_ip_pr);
            tv_3.setText(R.string.action_bootstrap_pr);

            btn_1.setText(R.string.update_pr);
            btn_2.setText(R.string.delete_pr);
            btn_3.setText(R.string.exit_pr);
        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.action_add_bootstrap_fr);
            tv_2.setText(R.string.action_bootstrap_ip_fr);
            tv_3.setText(R.string.action_bootstrap_fr);

            btn_1.setText(R.string.update_fr);
            btn_2.setText(R.string.delete_fr);
            btn_3.setText(R.string.exit_fr);

        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.action_add_bootstrap_am);
            tv_2.setText(R.string.action_bootstrap_ip_am);
            tv_3.setText(R.string.action_bootstrap_am);

            btn_1.setText(R.string.update_am);
            btn_2.setText(R.string.delete_am);
        }

        return view;
    }

    public View getGlobalBSScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.global_bootstrap_screen, null);
        tv_1 = (TextView) view.findViewById(R.id.tv_empty_bar_m2mscreen);
        tv_2 = (TextView) view.findViewById(R.id.button_bootstrp_exit);
        btn_2 = (Button) view.findViewById(R.id.button_bootstrp_exit);


        // Swedish
        if (this.language_no == 1) {
            tv_1.setText(R.string.action_add_bootstrap_sv);
            tv_2.setText(R.string.action_bootstrap_sv);
            // tv_3.setText(R.string.action_bootstrap_ip_sv);
            btn_2.setText(R.string.exit_sv);

        }
        // Spanish
        else if (this.language_no == 2) {

            tv_1.setText(R.string.action_add_bootstrap_sp);
            tv_2.setText(R.string.action_bootstrap_ip_sp);
            //  tv_3.setText(R.string.action_bootstrap_sp);

            btn_2.setText(R.string.exit_sp);
        }
        // Portugese
        else if (this.language_no == 3) {

            tv_1.setText(R.string.action_add_bootstrap_pr);
            tv_2.setText(R.string.action_bootstrap_ip_pr);
            // tv_3.setText(R.string.action_bootstrap_pr);

            //btn_1.setText(R.string.update_pr);
            btn_2.setText(R.string.exit_pr);
        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.action_add_bootstrap_fr);
            tv_2.setText(R.string.action_bootstrap_ip_pr);
            //  tv_3.setText(R.string.action_bootstrap_pr);

            //btn_1.setText(R.string.update_pr);
            btn_2.setText(R.string.exit_fr);

        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.action_add_bootstrap_am);
            tv_2.setText(R.string.action_bootstrap_ip_am);
            tv_3.setText(R.string.action_bootstrap_am);
            btn_1.setText(R.string.update_am);
            btn_2.setText(R.string.delete_am);
        }

        return view;
    }


    public View getDevicesScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.add_device, null);
        tv_1 = (TextView) view.findViewById(R.id.tv_empty);
        tv_2 = (TextView) view.findViewById(R.id.tv_bootstrp_zero);
        tv_3 = (TextView) view.findViewById(R.id.tv_bootstrp_one);
        tv_4 = (TextView) view.findViewById(R.id.tv_bootstrp_two);
        btn_1 = (Button) view.findViewById(R.id.button_add_devices);
        btn_2 = (Button) view.findViewById(R.id.button_clr_devices);
        btn_3 = (Button) view.findViewById(R.id.button_exit_devices);

        // Swedish
        if (this.language_no == 1) {

            String[] settings = context.getResources().getStringArray(R.array.settings_sv);

            tv_1.setText(settings[3]);
            tv_2.setText(R.string.name_sv);
            tv_3.setText("IP");
            tv_3.setText(R.string.action_devices_sv);
            btn_1.setText(R.string.update_sv);
            btn_2.setText(R.string.delete_sv);
            btn_3.setText(R.string.exit_sv);

        }
        // Spanish
        else if (this.language_no == 2) {

            String[] settings = context.getResources().getStringArray(R.array.settings_sp);
            tv_1.setText(settings[3]);
            tv_2.setText(R.string.name_sp);
            tv_3.setText("IP");
            tv_3.setText(R.string.action_devices_sp);
            btn_1.setText(R.string.update_sp);
            btn_2.setText(R.string.delete_sp);
            btn_3.setText(R.string.exit_sp);

        }
        // Portugese
        else if (this.language_no == 3) {
            String[] settings = context.getResources().getStringArray(R.array.settings_pr);
            tv_1.setText(settings[3]);
            tv_2.setText(R.string.name_pr);
            tv_3.setText("IP");
            tv_3.setText(R.string.action_devices_pr);
            btn_1.setText(R.string.update_pr);
            btn_2.setText(R.string.delete_pr);
            btn_3.setText(R.string.exit_pr);
        }
        // French
        else if (this.language_no == 4) {
            String[] settings = context.getResources().getStringArray(R.array.settings_fr);
            tv_1.setText(settings[3]);
            tv_2.setText(R.string.name_fr);
            tv_3.setText("IP");
            tv_3.setText(R.string.action_devices_fr);
            btn_1.setText(R.string.update_fr);
            btn_2.setText(R.string.delete_fr);
            btn_3.setText(R.string.exit_fr);

        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.action_language_am);
        }

        return view;
    }

    public View getSensorReadingscreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.sensors_screen, null);
        tv_1 = (TextView) view.findViewById(R.id.tv_empty_bar_m2mscreen);

        // Swedish
        if (this.language_no == 1) {
            tv_1.setText(R.string.action_sensor_sv);

        }
        // Spanish
        else if (this.language_no == 2) {

            tv_1.setText(R.string.action_sensor_sp);
        }
        // Portugese
        else if (this.language_no == 3) {

            tv_1.setText(R.string.action_sensor_sp);
        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.action_sensor_pr);

        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.action_sensor_am);
        }

        return view;
    }

    public View getLanguageScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.languagescreen, null);
        tv_1 = (TextView) view.findViewById(R.id.tv_empty_bar_languagescreen);
        // Swedish
        if (this.language_no == 1) {

            tv_1.setText(R.string.action_language_sv);

        }
        // Spanish
        else if (this.language_no == 2) {

            tv_1.setText(R.string.action_language_sp);
        }
        // Portugese
        else if (this.language_no == 3) {

            tv_1.setText(R.string.action_language_pr);
        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.action_language_fr);

        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.action_language_am);
        }

        return view;
    }

    public View getGroupContainerScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.languagescreen, null);
        tv_1 = (TextView) view.findViewById(R.id.tv_empty_bar_languagescreen);
        //tv = (TextView) findViewById(R.id.tv_empty_bar_languagescreen);
        tv_1.setTypeface(tf);

        // Swedish
        if (this.language_no == 1) {

            String[] settings = context.getResources().getStringArray(R.array.settings_sv);
            tv_1.setText(settings[8]);

        }
        // Spanish
        else if (this.language_no == 2) {

            String[] settings = context.getResources().getStringArray(R.array.settings_sp);
            tv_1.setText(settings[8]);
        }
        // Portugese
        else if (this.language_no == 3) {

            String[] settings = context.getResources().getStringArray(R.array.settings_pr);
            tv_1.setText(settings[8]);
        }
        // French
        else if (this.language_no == 4) {

            String[] settings = context.getResources().getStringArray(R.array.settings_fr);
            tv_1.setText(settings[8]);

        }
        // Amharic
        else if (this.language_no == 5) {
            String[] settings = context.getResources().getStringArray(R.array.settings_am);
            tv_1.setText(settings[8]);
        }
        // English
        else {
            String[] settings = context.getResources().getStringArray(R.array.settings_en);
            tv_1.setText(settings[8]);
        }

        return view;
    }


    public View getHelpScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.help, null);

        tv_1 = (TextView) view.findViewById(R.id.tv_empty_bar_helpscreen);

        // Swedish
        if (this.language_no == 1) {

            tv_1.setText(R.string.action_help_sv);

        }
        // Spanish
        else if (this.language_no == 2) {

            tv_1.setText(R.string.action_help_sp);

        }
        // Portugese
        else if (this.language_no == 3) {

            tv_1.setText(R.string.action_help_pr);

        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.action_help_fr);

        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.action_help_am);

        }

        return view;
    }

    public View getAboutScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.about, null);

        tv_1 = (TextView) view.findViewById(R.id.tv_empty_bar_aboutscreen);
        edt_1 = (EditText) view.findViewById(R.id.edt_about_box);

        // Swedish
        if (this.language_no == 1) {

            tv_1.setText(R.string.action_about_sv);
            edt_1.setText(R.string.about_text_sv);

        }
        // Spanish
        else if (this.language_no == 2) {

            tv_1.setText(R.string.action_about_sp);
            edt_1.setText(R.string.about_text_sp);
        }
        // Portugese
        else if (this.language_no == 3) {

            tv_1.setText(R.string.action_about_pr);
            edt_1.setText(R.string.about_text_pr);
        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.action_about_fr);
            edt_1.setText(R.string.about_text_fr);

        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.action_about_am);
            edt_1.setText(R.string.about_text_am);

        }

        return view;
    }

    public View getErrorScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.errorscreen, null);

        tv_1 = (TextView) view.findViewById(R.id.tv_error);

        // Swedish
        if (this.language_no == 1) {

            tv_1.setText(R.string.error_sv);

        }
        // Spanish
        else if (this.language_no == 2) {

            tv_1.setText(R.string.error_sp);

        }
        // Portugese
        else if (this.language_no == 3) {

            tv_1.setText(R.string.error_pr);

        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.error_fr);

        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.error_am);

        }

        return view;
    }

    public View getPurgeScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.purgescreen, null);

        tv_1 = (TextView) view.findViewById(R.id.tv_purge_chitchat);
        tv_2 = (TextView) view.findViewById(R.id.btn_purge_);
        rdio_btn_1 = (RadioButton) view
                .findViewById(R.id.radio1_purge_only_own_group);
        rdio_btn_2 = (RadioButton) view
                .findViewById(R.id.radio2_delete_favorite_groups);
        rdio_btn_3 = (RadioButton) view
                .findViewById(R.id.radio3_return_toFreshly_installed);
        rdio_btn_4 = (RadioButton) view
                .findViewById(R.id.radio0_purge_all_user_files);

        // Swedish
        if (this.language_no == 1) {

            tv_1.setText(R.string.purge_tv_sv);
            tv_2.setText(R.string.action_advanced_sv);
            rdio_btn_1.setText(R.string.purge_tv_1_sv);
            rdio_btn_2.setText(R.string.purge_tv_2_sv);
            rdio_btn_3.setText(R.string.purge_tv_3_sv);
            rdio_btn_4.setText(R.string.purge_tv_4_sv);

        }
        // Spanish
        else if (this.language_no == 2) {
            tv_1.setText(R.string.purge_tv_sp);
            tv_2.setText(R.string.action_advanced_sp);
            rdio_btn_1.setText(R.string.purge_tv_1_sp);
            rdio_btn_2.setText(R.string.purge_tv_2_sp);
            rdio_btn_3.setText(R.string.purge_tv_3_sp);
            rdio_btn_4.setText(R.string.purge_tv_4_sp);
        }
        // Portugese
        else if (this.language_no == 3) {
            tv_1.setText(R.string.purge_tv_pr);
            tv_2.setText(R.string.action_advanced_pr);
            rdio_btn_1.setText(R.string.purge_tv_1_pr);
            rdio_btn_2.setText(R.string.purge_tv_2_pr);
            rdio_btn_3.setText(R.string.purge_tv_3_pr);
            rdio_btn_4.setText(R.string.purge_tv_4_pr);
        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.purge_tv_fr);
            tv_2.setText(R.string.action_advanced_fr);
            rdio_btn_1.setText(R.string.purge_tv_1_fr);
            rdio_btn_2.setText(R.string.purge_tv_2_fr);
            rdio_btn_3.setText(R.string.purge_tv_3_fr);
            rdio_btn_4.setText(R.string.purge_tv_4_fr);

        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.purge_tv_am);
            tv_2.setText(R.string.action_advanced_am);
            rdio_btn_1.setText(R.string.purge_tv_1_am);
            rdio_btn_2.setText(R.string.purge_tv_2_am);
            rdio_btn_3.setText(R.string.purge_tv_3_am);
            rdio_btn_4.setText(R.string.purge_tv_4_am);

        }

        return view;
    }


    public View getDebugScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.debugscreen, null);

        tv_1 = (TextView) view.findViewById(R.id.tv_empty_bar_chatscreen);
        btn_1 = (Button) view.findViewById(R.id.btn_send_chatscreen);
        edt_1 = (EditText) view.findViewById(R.id.edt_chatScreen);

        // Swedish
        if (this.language_no == 1) {

            tv_1.setText(R.string.purge_tv_sv);
            btn_1.setText(R.string.send_btn_sv_);
            edt_1.setHint(R.string.hint_enter_txt_sv);


        }
        // Spanish
        else if (this.language_no == 2) {
            tv_1.setText(R.string.purge_tv_sp);
            btn_1.setText(R.string.send_btn_sp_);
            edt_1.setHint(R.string.hint_enter_txt_sp);
        }
        // Portugese
        else if (this.language_no == 3) {
            tv_1.setText(R.string.purge_tv_pr);
            btn_1.setText(R.string.send_btn_pr_);
            edt_1.setHint(R.string.hint_enter_txt_pr);
        }
        // French
        else if (this.language_no == 4) {
            tv_1.setText(R.string.purge_tv_fr);
            btn_1.setText(R.string.send_btn_fr_);
            edt_1.setHint(R.string.hint_enter_txt_fr);

        }
        // Amharic
        else if (this.language_no == 5) {
            tv_1.setText(R.string.purge_tv_am);
            btn_1.setText(R.string.send_btn_am_);
            edt_1.setHint(R.string.hint_enter_txt_am);

        }

        return view;
    }

    public View getDeleteGroupsScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.deletegroupschatscreen, null);

        tv_1 = (TextView) view.findViewById(R.id.tv_delete_groups_title);
        btn_1 = (Button) view.findViewById(R.id.button1_delete_group);

        // Swedish
        if (this.language_no == 1) {
            tv_1.setText(R.string.delete_groups_sv);
            btn_1.setText(R.string.remove_sv);

        }
        // Spanish
        else if (this.language_no == 2) {
            tv_1.setText(R.string.delete_groups_sp);
            btn_1.setText(R.string.remove_sp);
        }
        // Portugese
        else if (this.language_no == 3) {
            tv_1.setText(R.string.delete_groups_pr);
            btn_1.setText(R.string.remove_pr);
        }
        // French
        else if (this.language_no == 4) {

            tv_1.setText(R.string.delete_groups_fr);
            btn_1.setText(R.string.remove_fr);

        }
        // Amharic
        else if (this.language_no == 5) {

            tv_1.setText(R.string.delete_groups_am);
            btn_1.setText(R.string.remove_am);

        }

        return view;
    }

    public View getGalleryScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.imagegallareyscreen, null);

        return view;

    }

    public View getEditProfileScreen() {

        layout_inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layout_inflater.inflate(R.layout.myprofile, null);
        btn_1 = (Button) view.findViewById(R.id.button_refresh_my_profile);
        edt_1 = (EditText) view.findViewById(R.id.edt_peerPro_name);
        edt_2 = (EditText) view.findViewById(R.id.edt_peerPro_nick);
        edt_3 = (EditText) view.findViewById(R.id.edt_peerPro_sex);
        edt_4 = (EditText) view.findViewById(R.id.edt_peerPro_age);
        tv_1 = (TextView) view.findViewById(R.id.tv_peerPro_name);
        tv_2 = (TextView) view.findViewById(R.id.tv_peerPro_nick);
        tv_3 = (TextView) view.findViewById(R.id.tv_peerPro_sex);
        tv_4 = (TextView) view.findViewById(R.id.tv_peerPro_age);
        tv_5 = (TextView) view.findViewById(R.id.tv_peerPro_group);
        tv_6 = (TextView) view.findViewById(R.id.tv_localPeer);
        tv_7 = (TextView) view.findViewById(R.id.tv_click_profile_iamge);

        // Swedish
        if (this.language_no == 1) {
            btn_1.setText(R.string.update_sv);
            tv_1.setText(R.string.name_sv);
            tv_2.setText(R.string.nick_sv);
            tv_3.setText(R.string.sex_sv);
            tv_4.setText(R.string.age_sv);
            tv_5.setText(R.string.portfolio_sv);
            tv_6.setText(R.string.my_profile_sv);
            tv_7.setText(R.string.tap_here_to_add_image_sv);
        }
        // Spanish
        else if (this.language_no == 2) {

            btn_1.setText(R.string.update_sp);
            tv_1.setText(R.string.name_sp);

            tv_2.setText(R.string.nick_sp);
            tv_3.setText(R.string.sex_sp);
            tv_4.setText(R.string.age_sp);
            tv_5.setText(R.string.portfolio_sp);
            tv_6.setText(R.string.my_profile_sp);
            tv_7.setText(R.string.tap_here_to_add_image_sp);
        }
        // Portugese
        else if (this.language_no == 3) {
            btn_1.setText(R.string.update_pr);
            tv_1.setText(R.string.name_pr);

            tv_2.setText(R.string.nick_pr);
            tv_3.setText(R.string.sex_pr);
            tv_4.setText(R.string.age_pr);
            tv_5.setText(R.string.portfolio_pr);
            tv_6.setText(R.string.my_profile_pr);
            tv_7.setText(R.string.tap_here_to_add_image_pr);
        }
        // French
        else if (this.language_no == 4) {

            btn_1.setText(R.string.update_fr);
            tv_1.setText(R.string.name_fr);

            tv_2.setText(R.string.nick_fr);
            tv_3.setText(R.string.sex_fr);
            tv_4.setText(R.string.age_fr);
            tv_5.setText(R.string.portfolio_fr);
            tv_6.setText(R.string.my_profile_fr);
            tv_7.setText(R.string.tap_here_to_add_image_fr);

        }
        // Amharic
        else if (this.language_no == 5) {

            btn_1.setText(R.string.update_am);
            tv_1.setText(R.string.name_am);

            tv_2.setText(R.string.nick_am);
            tv_3.setText(R.string.sex_am);
            tv_4.setText(R.string.age_am);
            tv_5.setText(R.string.portfolio_am);
            tv_6.setText(R.string.my_profile_am);
            tv_7.setText(R.string.tap_here_to_add_image_am);

        }

        return view;

    }

    // Languages--> getLanguage number
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
        } else
            ;
        return lang_serial;
    }

    public String getChithChatTime() {
        // TODO Auto-generated method stub
        String chitchat_time = "";
        Calendar cal;
        Date date;
        cal = new GregorianCalendar();
        date = new Date();
        cal.setTime(date);
        chitchat_time = cal.getTime().getDate() + "/"
                + cal.getTime().getMonth() + "/" + cal.getTime().getYear()
                + "  " + +cal.getTime().getHours() + ":"
                + cal.getTime().getMinutes() + ":" + cal.getTime().getSeconds();
        return chitchat_time;
    }

    public int getLanguage() {
        return this.language_no;
    }

    public Typeface getTf() {
        return tf;
    }

    public void setTf(String typeface) {
        this.tf = Typeface.create(Typeface.DEFAULT, style);

    }

    public String getPeer_name() {
        return peer_name;
    }

    public void setPeer_name(String peer_name) {
        this.peer_name = peer_name;
    }

    public String getPeer_nick_name() {
        return peer_nick_name;
    }

    public void setPeer_nick_name(String peer_nick_name) {
        this.peer_nick_name = peer_nick_name;
    }

    public String getPeer_sex() {
        return peer_sex;
    }

    public void setPeer_sex(String peer_sex) {
        this.peer_sex = peer_sex;
    }

    public String getProfile_image_uri() {
        return profile_image_uri;
    }

    public void setProfile_image_uri(String profile_image_uri) {
        this.profile_image_uri = profile_image_uri;
    }

    public String getProfile_image_uri(String uci) {
        mPrefs_ = context.getSharedPreferences("myprofile", 0);
        profile_image_uri = mPrefs_.getString(uci.replace("/", "_"), profile_image_uri);
        return profile_image_uri;
    }

    public String get_image_message_uri() {
        return _image_message_uri;
    }

    public void set_image_message_uri(String _image_message_uri) {
        this._image_message_uri = _image_message_uri;
    }

    public String getContext_image_message_uri() {
        return context_image_message_uri;
    }

    public void setContext_image_message_uri(String context_image_message_uri) {
        this.context_image_message_uri = context_image_message_uri;
    }

    public String getGroup_image_message_uri() {
        return group_image_message_uri;
    }

    public void setGroup_image_message_uri(String group_image_message_uri) {
        this.group_image_message_uri = group_image_message_uri;
    }

    public String getUci_image_message_uri() {
        return uci_image_message_uri;
    }

    public void setUci_image_message_uri(String uci_image_message_uri) {
        this.uci_image_message_uri = uci_image_message_uri;
    }

    public String getSearch_image_message_uri() {
        return search_image_message_uri;
    }

    public void setSearch_image_message_uri(String search_image_message_uri) {
        this.search_image_message_uri = search_image_message_uri;
    }

    public String getHelp_image_message_uri() {
        return help_image_message_uri;
    }

    public void setHelp_image_message_uri(String help_image_message_uri) {
        this.help_image_message_uri = help_image_message_uri;
    }

    public String getAbt_image_message_uri() {
        return abt_image_message_uri;
    }

    public void setAbt_image_message_uri(String abt_image_message_uri) {
        this.abt_image_message_uri = abt_image_message_uri;
    }

    public String getLang_image_message_uri() {
        return lang_image_message_uri;
    }

    public void setLang_image_message_uri(String lang_image_message_uri) {
        this.lang_image_message_uri = lang_image_message_uri;
    }

    public String getPeer_age() {
        return peer_age;
    }

    public void setPeer_age(String peer_age) {
        this.peer_age = peer_age;
    }

    public String getProfile_group_uci() {
        return profile_group_uci;
    }

    public void setProfile_group_uci(String profile_group_uci) {
        this.profile_group_uci = profile_group_uci;
    }

    public boolean isLOCAL_PEER_REGISTERED() {
        return LOCAL_PEER_REGISTERED;
    }

    public void setLOCAL_PEER_REGISTERED(boolean lOCAL_PEER_REGISTERED) {
        LOCAL_PEER_REGISTERED = lOCAL_PEER_REGISTERED;
    }

    public boolean isPROFILE_IMAGE_CHANGED() {
        return PROFILE_IMAGE_CHANGED;
    }

    public void setPROFILE_IMAGE_CHANGED(boolean PROFILE_IMAGE_CHANGED) {
        this.PROFILE_IMAGE_CHANGED = PROFILE_IMAGE_CHANGED;
    }

    public boolean is_IMAGE_SEND_() {
        return _IMAGE_SEND_;
    }

    public void set_IMAGE_SEND_(boolean _IMAGE_SEND_) {
        this._IMAGE_SEND_ = _IMAGE_SEND_;
    }

    public boolean is_NEW_INFO_AVAILABLE_() {
        return _NEW_INFO_AVAILABLE_;
    }

    public void set_NEW_INFO_AVAILABLE_(boolean _NEW_INFO_AVAILABLE_) {
        this._NEW_INFO_AVAILABLE_ = _NEW_INFO_AVAILABLE_;

        mPrefs = context.getSharedPreferences("myprofile", 0);
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("_NEW_INFO_AVAILABLE_", _NEW_INFO_AVAILABLE_);
        ed.commit();
    }

    public boolean is_NOTIFICATIONS_AVAILABLE() {
        return _NOTIFICATIONS_AVAILABLE;
    }

    public void set_NOTIFICATIONS_AVAILABLE(boolean _NOTIFICATIONS_AVAILABLE) {
        this._NOTIFICATIONS_AVAILABLE = _NOTIFICATIONS_AVAILABLE;
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("_NOTIFICATIONS_AVAILABLE", _NOTIFICATIONS_AVAILABLE);
        ed.commit();
    }

    public boolean is_MESSAGES_AVAILABLE() {
        return _MESSAGES_AVAILABLE;
    }

    public void set_MESSAGES_AVAILABLE(boolean _MESSAGES_AVAILABLE) {
        this._MESSAGES_AVAILABLE = _MESSAGES_AVAILABLE;
    }

    public boolean is_BOOTSTRAP_IP_() {
        return _BOOTSTRAP_IP_;
    }

    public void set_BOOTSTRAP_IP_(boolean _BOOTSTRAP_IP_) {
        this._BOOTSTRAP_IP_ = _BOOTSTRAP_IP_;
    }

    public boolean IS_NODE_BOOTSTRAP_() {
        return _IS_NODE_BOOTSTRAP_;
    }

    public void set_IS_NODE_BOOTSTRAP_(boolean b) {
        this._IS_NODE_BOOTSTRAP_ = b;
    }

    public boolean isFLAG_DISP_NO_IP() {
        return FLAG_DISP_NO_IP;
    }

    public void setFLAG_DISP_NO_IP(boolean FLAG_DISP_NO_IP) {
        this.FLAG_DISP_NO_IP = FLAG_DISP_NO_IP;
    }

    public boolean isSERVICE_UP() {
        return IS_SERVICE_UP;
    }

    public void setSERVICE_UP(boolean IS_SERVICE_UP) {
        this.IS_SERVICE_UP = IS_SERVICE_UP;
    }

    public boolean isST_UP() {
        return IS_ST_UP;
    }

    public void setST_UP(boolean IS_ST_UP) {
        this.IS_ST_UP = IS_ST_UP;
    }

    public int getPLATFORM_STARTUP_TRIAL() {
        return PLATFORM_STARTUP_TRIAL;
    }

    public void setPLATFORM_STARTUP_TRIAL(int PLATFORM_STARTUP_TRIAL) {
        this.PLATFORM_STARTUP_TRIAL = PLATFORM_STARTUP_TRIAL;
    }

    public int getPri_message_thread_len() {
        return pri_message_thread_len;
    }

    public void setPri_message_thread_len(int pri_message_thread_len) {
        this.pri_message_thread_len = pri_message_thread_len;
    }

    public int getPub_message_thread_len() {
        return pub_message_thread_len;
    }

    public void setPub_message_thread_len(int pub_message_thread_len) {
        this.pub_message_thread_len = pub_message_thread_len;
    }

    public String getCONTEXTER_ADVS() {
        return CONTEXTER_ADVS;
    }

    public void setCONTEXTER_ADVS(String CONTEXTER_ADVS) {
        this.CONTEXTER_ADVS = CONTEXTER_ADVS;
    }

    public boolean IS_FIRST_TIME_LOGIN() {
        return IS_FIRST_TIME_LOGIN;
    }

    public void setFIRST_TIME_LOGIN(boolean IS_FIRST_TIME_LOGIN) {
        this.IS_FIRST_TIME_LOGIN = IS_FIRST_TIME_LOGIN;
    }

    public boolean isSilent_One() {
        return isSilent_One;
    }

    public void setSilent_One(boolean silent_One) {
        isSilent_One = silent_One;
    }

    public boolean isHotSpotON() {
        return isHotSpotON;
    }

    public void setHotSpotStatus(boolean s) {
        isHotSpotON = s;
    }

    public boolean isSilent_Two() {
        return isSilent_Two;
    }

    public void setSilent_Two(boolean silent_Two) {
        isSilent_Two = silent_Two;
    }

    public boolean isSilent_Three() {
        return isSilent_Three;
    }

    public void setSilent_Three(boolean silent_Three) {
        isSilent_Three = silent_Three;
    }

    public boolean isNoPattern() {
        return isNoPattern;
    }

    public void setNoPattern(boolean noPattern) {
        isNoPattern = noPattern;
    }

    public boolean isNoNotification() {
        return isNoNotification;
    }

    public void setNoNotification(boolean noNotification) {
        isNoNotification = noNotification;
    }

    public String getPreferred_bs_ip() {
        return preferred_bs_ip;
    }

    public void setPreferred_bs_ip(String preferred_bs_ip) {
        this.preferred_bs_ip = preferred_bs_ip;
    }
}
