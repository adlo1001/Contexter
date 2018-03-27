package se.sensiblethings.app.chitchato.adapters;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.sensiblethings.app.R;
import se.sensiblethings.app.R.color;
import se.sensiblethings.app.chitchato.activities.ErrorActivity;
import se.sensiblethings.app.chitchato.activities.GroupContainerActivity;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerBootstrap;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerNode;
import se.sensiblethings.app.chitchato.extras.CustomImageView;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.DialogOne;
import se.sensiblethings.app.chitchato.extras.WifiApiManager;

public class ListAdapter extends BaseAdapter {

    // Date Parameters
    static String date;
    Button btn_accept = null;
    Button btn_decline = null;
    String date_pattern = "yyyy-MM-dd HH:mm:ss";
    Format format = new SimpleDateFormat(date_pattern);
    View view;
    // 0-- own 1- other
    int small_gridVW = 0;
    private Context context_;
    private int index = 0;
    private String curr_active_grp = "Contexter";
    private boolean CHAT_MODE = false;
    private TreeMap<String, String> tm_groups;
    private ArrayList<String> ar_groups;
    private int list_serial = 0;
    private boolean setProfilePictureGone = false;
    private int language_code = 0;
    private Customizations custom;
    private MediaPlayer mp;

    public ListAdapter(Context c) {
        this.context_ = c;
    }

    public ListAdapter(Context c, int i, boolean chat_mode) {
        this.context_ = c;
        this.index = i;
        custom = new Customizations(context_, -1);
    }

    public ListAdapter(Context c, int i, boolean chat_mode,
                       ArrayList<String> array_list, int list_serial) {
        this.context_ = c;
        this.index = i;
        this.CHAT_MODE = chat_mode;
        this.ar_groups = array_list;
        this.list_serial = list_serial;
        custom = new Customizations(context_, -1);
        mp = MediaPlayer.create(context_, R.raw.button_click_one);

    }

    //Get Path for Home Folder
    public static String getAppFolder(String file_name) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/";
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSamplesize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSamplesize) >= reqHeight && (halfWidth / inSamplesize) >= reqWidth) {
                inSamplesize *= 2;
            }
        }
        return inSamplesize;

    }

    public static Bitmap decodeSampledBitmapFromResource(Uri uri, int reqWidth, int reqHeight) {
        File file = new File(uri.getPath());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        return bitmap;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub

        if (ar_groups == null) {
            return 0;
        } else {
            if (ar_groups.isEmpty())
                return 0;
            else
                return ar_groups.size();
        }
    }

    @Override
    public String getItem(int arg0) {
        // TODO Auto-generated method stub
        return ar_groups.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void addItem(ArrayList<String> items) {
        this.ar_groups = items;
    }

    public int getLanguage_code() {
        return language_code;
    }

    public void setLanguage_code(int language_code) {
        this.language_code = language_code;
    }

    public int getSmall_gridVW() {
        return small_gridVW;
    }

    public void setSmall_gridVW(int small_gridVW) {
        this.small_gridVW = small_gridVW;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        view = convertView;

        TextView btn = null, textview_subitem;
        Typeface tf = Typeface.createFromAsset(this.context_.getAssets(), "fonts/pala.ttf");
        Typeface tf_goud_sout = Typeface.createFromAsset(this.context_.getAssets(), "fonts/GOUDYSTO.TTF");
        // Button btn;

        LayoutInflater li;

        /* Main Screen Activity--List Adapter */
        if (list_serial == 0)
            try {

                final ArrayList<String> groups = this.ar_groups;

                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_favorite_group_btn, null);
                textview_subitem = (TextView) view
                        .findViewById(R.id.tv_chat_screen_sub_item);
                btn = (TextView) view.findViewById(R.id.favorite_group_btn);
                CustomImageView cust_img_view = (CustomImageView) view.findViewById(R.id.cust_image_view_one);

                // Font face

                textview_subitem.setTypeface(tf);
                // btn.setTypeface(tf);
                btn.setTypeface(tf, 1);


                if (position == this.index) {
                    if ((context_.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                        btn.setBackgroundResource(R.drawable.circlebtn_red_with_border_large_screen);
                        cust_img_view.setBackground(context_.getResources().getDrawable(color.chitchato_mebratu_cyan));

                    } else {
                        btn.setBackgroundResource(R.drawable.circlebtn_red_with_border);
                        cust_img_view.setBackground(context_.getResources().getDrawable(color.chitchato_mebratu_cyan));
                    }

                    textview_subitem.setVisibility(View.VISIBLE);
                    textview_subitem.setText(groups.get(position));
                } else {

                    textview_subitem.setVisibility(View.GONE);
                }

                if (!((String) groups.get(position)).contains("#none#")) {
                    btn.setText(groups.get(position).subSequence(0, 1));
                    btn.setTextColor(context_.getResources().getColor(color.chitchato_mebratu_cyan));
                    btn.setTypeface(tf_goud_sout);
                    textview_subitem.setText(groups.get(position));
                    if ((context_.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE)
                        btn.setTextSize(50.0f);
                    else
                        btn.setTextSize(30.0f);

                } else {
                    btn.setText("Quit");
                    // textview_subitem.setText("To quit click here!");
                }


                btn.setClickable(false);
                String path = getAppFolder("");
                //final String uri_str = path + groups.get(position) + "%group%miun.se_mobi"+ ".jpg";

                final String uri_str = path + groups.get(position) + "__group__miun.se_mobi"+ ".jpg";
                File _image_file_ = new File(uri_str);

                if (_image_file_.exists())
                    cust_img_view.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(uri_str), 55, 55));

                else
                    cust_img_view.setImageResource(R.drawable.world_map_temp_two___________);

                view.setPadding(0, 10, 0, 10);

            } catch (Exception e) {

                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        else if (list_serial == 1) {
            // add here
        } /* Language Activity */ else if (list_serial == 2) {
            try {

                CheckBox ck_box = null;
                TextView tv_language = null;

                String[] lang_s = context_.getResources().getStringArray(
                        R.array.languages);

                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_language_radio, null);
                ck_box = (CheckBox) view.findViewById(R.id.lang_chat_radio);
                tv_language = (TextView) view
                        .findViewById(R.id.tv_lang_checkbox);
                tv_language.setText(lang_s[position]);
                tv_language.setTypeface(tf);
                ck_box.setTypeface(tf);

                Button btn_accept = null;
                Button btn_decline = null;
                btn_accept = (Button) view.findViewById(R.id.btn_accept);
                btn_decline = (Button) view.findViewById(R.id.btn_decline);
                btn_accept.setVisibility(View.INVISIBLE);
                btn_decline.setVisibility(View.INVISIBLE);

                if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    tv_language.setTextSize(20);
                }
                view.setPadding(0, 10, 0, 10);

                if (this.index == position) {
                    ck_box.setChecked(true);
                } else
                    ck_box.setChecked(false);

            } catch (Exception e) {

                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        }
        /* Purge Activity */
        else if (list_serial == 3) {
            try {

                CheckBox ck_box = null;
                TextView tv_language = null;
                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_language_radio, null);
                ck_box = (CheckBox) view.findViewById(R.id.lang_chat_radio);
                tv_language = (TextView) view
                        .findViewById(R.id.tv_lang_checkbox);
                tv_language.setText(ar_groups.get(position));
                tv_language.setTypeface(tf);
                ck_box.setTypeface(tf);
                Button btn_accept = null;
                Button btn_decline = null;
                btn_accept = (Button) view.findViewById(R.id.btn_accept);
                btn_decline = (Button) view.findViewById(R.id.btn_decline);
                btn_accept.setVisibility(View.INVISIBLE);
                btn_decline.setVisibility(View.INVISIBLE);


                if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    tv_language.setTextSize(20);
                }

                view.setPadding(0, 10, 0, 10);

            } catch (Exception e) {

                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        }
        /* Group Search results Activity */
        else if (list_serial == 4) {
            try {

                CheckBox ck_box = null;
                TextView tv = null;

                // String[] lang_s = context_.getResources().getStringArray(
                // R.array.languages);

                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // view = li.inflate(R.layout.searchresultsgriditem, null);
                // ck_box = (CheckBox) view.findViewById(R.id.chck_bx_addgroup);
                // tv = (TextView) view
                // .findViewById(R.id.editText_grditem_3_1);

                view = li.inflate(R.layout.chitchato_language_radio, null);
                ck_box = (CheckBox) view.findViewById(R.id.lang_chat_radio);
                tv = (TextView) view.findViewById(R.id.tv_lang_checkbox);
                tv.setText(ar_groups.get(position));
                tv.setTypeface(tf);
                ck_box.setTypeface(tf);

                Button btn_accept = null;
                Button btn_decline = null;
                btn_accept = (Button) view.findViewById(R.id.btn_accept);
                btn_decline = (Button) view.findViewById(R.id.btn_decline);
                btn_accept.setVisibility(View.INVISIBLE);
                btn_decline.setVisibility(View.INVISIBLE);


                if (ar_groups.get(position).startsWith("No Results"))
                    ck_box.setVisibility(View.GONE);

                if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    tv.setTextSize(20);
                }
                view.setPadding(0, 10, 0, 10);

            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        }
        /* Private Chat Messsages Activity */
        else if (list_serial == 5) {
            try {

                CheckBox ck_box = null;
                TextView tv_0, tv_1 = null, tv_2 = null, tv_3 = null;
                ImageView image_view = null;

                // String[] lang_s = context_.getResources().getStringArray(
                // R.array.languages);

                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // view = li.inflate(R.layout.searchresultsgriditem, null);
                // ck_box = (CheckBox) view.findViewById(R.id.chck_bx_addgroup);
                // tv = (TextView) view
                // .findViewById(R.id.editText_grditem_3_1);

                view = li.inflate(R.layout.privatechatscreengriditem, null);
                ck_box = (CheckBox) view.findViewById(R.id.pr_chat_radio_new);

                tv_0 = (TextView) view.findViewById(R.id.editText_grditem_pr_3);
                tv_1 = (TextView) view
                        .findViewById(R.id.tv_chat_screen_context_sub_pr_item);
                tv_2 = (TextView) view
                        .findViewById(R.id.tv_prchat_screen_context_chatter);
                tv_3 = (TextView) view
                        .findViewById(R.id.tv_prchat_screen_context_chat_time);
                image_view = (ImageView) view
                        .findViewById(R.id.imageView_grditem_pr_1);

                LinearLayout llo = (LinearLayout) view
                        .findViewById(R.id.imageView_pr_llo_);
                LinearLayout llo_ = (LinearLayout) view
                        .findViewById(R.id.linear_lo_checkbx__pr_item);

                ck_box.setVisibility(View.GONE);
                if (this.setProfilePictureGone)
                    image_view.setVisibility(View.GONE);
                else
                    image_view.setVisibility(View.VISIBLE);
                tv_0.setVisibility(View.GONE);
                tv_1.setVisibility(View.GONE);
                tv_2.setVisibility(View.VISIBLE);
                tv_3.setVisibility(View.GONE);
                llo_.setVisibility(View.GONE);
                llo.setVisibility(View.GONE);

                tv_2.setTypeface(tf);
                // ck_box.setTypeface(tf);

                tv_2.setText(ar_groups.get(position));
                tv_2.setTextSize(20);
                view.setPadding(0, 10, 0, 10);

            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        }// List of Peers within a group
        else if (list_serial == 6) {
            try {

                TextView tv_0, tv_1 = null, tv_2 = null;

                // String[] lang_s = context_.getResources().getStringArray(
                // R.array.languages);

                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // view = li.inflate(R.layout.searchresultsgriditem, null);
                // ck_box = (CheckBox) view.findViewById(R.id.chck_bx_addgroup);
                // tv = (TextView) view
                // .findViewById(R.id.editText_grditem_3_1);

                view = li.inflate(R.layout.chitchato_peers_btn, null);
                tv_0 = (TextView) view
                        .findViewById(R.id.favorite_group_btn_prs);
                tv_1 = (TextView) view
                        .findViewById(R.id.tv_chat_screen_sub_item_prs);
                //tv_2 = (TextView) view.findViewById(R.id.tv_arrow__prs);
                tv_0.setTypeface(tf);

                tv_0.setText(ar_groups.get(position));
                if (ar_groups.get(position).toString().contains("No Peers")) {
                    tv_0.setVisibility(View.GONE);
                    //tv_2.setVisibility(View.GONE);
                    tv_1.setTextSize(20);
                    //tv_2.setTextColor(context_.getResources().getColor(
                    //		R.color.chitchato_blue));
                }
                if (position > 0) {
                    //tv_2.setVisibility(View.INVISIBLE);
                }
                tv_1.setTypeface(tf);
                tv_1.setText(ar_groups.get(position));

            } catch (Exception e) {

                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        }
        // M2M Menu
        else if (list_serial == 7) {

            try {

                Set<String> devices;
                TreeMap devices_map = new TreeMap<String, String>();
                ArrayList<String> ar_list = new ArrayList<String>();
                SharedPreferences mPrefs;
                mPrefs = context_.getSharedPreferences("devices", 0);
                devices = mPrefs.getStringSet("devices", devices_map.keySet());

                Iterator itr = devices.iterator();
                Map.Entry me;
                String ip_ = "", device_name = "";
                while (itr.hasNext()) {
                    device_name = (String) itr.next();
                    ar_list.add(device_name);
                }

                TextView item_name_set = null;
                TextView item_set_btn = null;
                EditText item_set_edt = null;

                Object[] lang_s = ar_list.toArray();

                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_m2m_griditem, null);
                item_set_btn = (TextView) view.findViewById(R.id.btn_m2m_btn);
                // item_set_edt = (EditText) view.findViewById(R.id.tv_m2m_edt);
                item_name_set = (TextView) view.findViewById(R.id.btn_m2m_on_off_btn);

                //item_set_btn.setTextColor(context_.getResources().getColor(
                //		color.chitchato_mebratu_blue));
                item_set_btn.setText((String) lang_s[position]);
                item_set_btn.setTypeface(tf);

                item_name_set.setTextColor(context_.getResources().getColor(
                        color.chitchato_mebratu_blue));
                item_name_set.setText("NA");
                item_set_btn.setText(ar_groups.get(position));
                item_name_set.setTypeface(tf);
                if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    item_name_set.setTextSize(20);
                }
                view.setPadding(0, 10, 0, 10);

            } catch (Exception e) {

                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        }
        // Settings
        else if (list_serial == 8) {
            try {
                CheckBox ck_box = null;
                TextView tv_val = null;
                ImageView im_view = null;
                ImageView img_view_small_icon = null;
                LinearLayout linear_layout = null;
                ImageButton image_btn_1 = null;

                String[] lang_s = context_.getResources().getStringArray(
                        R.array.settings_en);

                if (language_code == 0) {
                    lang_s = context_.getResources().getStringArray(
                            R.array.settings_en);
                } else if (language_code == 1) {
                    lang_s = context_.getResources().getStringArray(
                            R.array.settings_sv);
                } else if (language_code == 2) {
                    lang_s = context_.getResources().getStringArray(
                            R.array.settings_sp);
                } else if (language_code == 3) {
                    lang_s = context_.getResources().getStringArray(
                            R.array.settings_pr);
                } else if (language_code == 4) {
                    lang_s = context_.getResources().getStringArray(
                            R.array.settings_fr);
                } else if (language_code == 5) {
                    lang_s = context_.getResources().getStringArray(
                            R.array.settings_am);
                }

                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_settings_radio, null);
                // ck_box = (CheckBox) view.findViewById(R.id.lang_chat_radio);
                tv_val = (TextView) view.findViewById(R.id.tv_lang_checkbox);
                //linear_layout = (LinearLayout) view.findViewById();
                img_view_small_icon = (ImageView) view.findViewById(R.id.img_settings_radio_small_icon);
                linear_layout = (LinearLayout) view.findViewById(R.id.ll_check_box_);


                im_view = (ImageView) view.findViewById(R.id.lang_chat_radio);
                tv_val.setText(lang_s[position]);
                tv_val.setTypeface(tf);
                im_view.setBackground(context_.getResources().getDrawable(R.drawable.circlebtn_backgrd_color_));

                linear_layout.setPadding(2, 2, 2, 2);

                if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    tv_val.setTextSize(20);

                }
                if (position == 0) {
                    im_view.setImageResource(se.sensiblethings.app.R.drawable.ic_bycredit);
                    img_view_small_icon.setVisibility(View.GONE);
                } else if (position == 1) {
                    im_view.setImageResource(R.drawable.ic_add_bootstrap_);
                    img_view_small_icon.setVisibility(View.GONE);
                } else if (position == 2) {
                    im_view.setImageResource(se.sensiblethings.app.R.drawable.ic_add_uci);
                    img_view_small_icon.setVisibility(View.GONE);
                } else if (position == 3) {
                    im_view.setImageResource(se.sensiblethings.app.R.drawable.ic_add_device);
                    img_view_small_icon.setVisibility(View.GONE);
                } else if (position == 4) {
                    im_view.setImageResource(se.sensiblethings.app.R.drawable.ic_action_privacy);
                    img_view_small_icon.setVisibility(View.GONE);
                } else if (position == 5) {


                    im_view.setImageResource(se.sensiblethings.app.R.drawable.ic_notification);
                    if (custom.is_NOTIFICATIONS_AVAILABLE()) {
                        img_view_small_icon.setVisibility(View.VISIBLE);
                    } else
                        img_view_small_icon.setVisibility(View.INVISIBLE);

                } else if (position == 6) {
                    im_view.setImageResource(se.sensiblethings.app.R.drawable.ic_message_calls);
                    if (custom.is_MESSAGES_AVAILABLE()) {
                        img_view_small_icon.setVisibility(View.VISIBLE);
                    } else
                        img_view_small_icon.setVisibility(View.INVISIBLE);
                } else if (position == 7) {
                    im_view.setImageResource(R.drawable.ic_addbootstrap);
                    img_view_small_icon.setVisibility(View.GONE);
                } else if (position == 8) {
                    img_view_small_icon.setVisibility(View.GONE);
                    im_view.setImageResource(R.drawable.ic_groups);
                } else if (position == 9) {
                    {
                        im_view.setImageResource(R.drawable.ic_extras);
                        img_view_small_icon.setVisibility(View.GONE);
                    }
                }
                view.setPadding(0, 10, 0, 10);

            } catch (Exception e) {

                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        }
        // Help Container
        else if (list_serial == 9) {
            try {

                CheckBox ck_box = null;
                TextView tv_val = null;
                ImageView im_view = null;
                String[] lang_s = context_.getResources().getStringArray(
                        R.array.help_container_en);
                if (language_code == 0) {
                    lang_s = context_.getResources().getStringArray(
                            R.array.help_container_en);
                } else if (language_code == 1) {
                    lang_s = context_.getResources().getStringArray(
                            R.array.help_container_sv);
                } else if (language_code == 2) {

                    lang_s = context_.getResources().getStringArray(
                            R.array.help_container_sp);
                } else if (language_code == 3) {

                    lang_s = context_.getResources().getStringArray(
                            R.array.help_container_pr);
                } else if (language_code == 4) {

                    lang_s = context_.getResources().getStringArray(
                            R.array.help_container_fr);
                } else if (language_code == 5) {

                    lang_s = context_.getResources().getStringArray(
                            R.array.help_container_am);
                }

                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_settings_radio, null);
                // ck_box = (CheckBox) view.findViewById(R.id.lang_chat_radio);
                tv_val = (TextView) view.findViewById(R.id.tv_lang_checkbox);
                im_view = (ImageView) view.findViewById(R.id.lang_chat_radio);
                ImageView img_view_small_icon = null;
                img_view_small_icon = (ImageView) view.findViewById(R.id.img_settings_radio_small_icon);
                img_view_small_icon.setVisibility(View.GONE);

                tv_val.setText(lang_s[position]);
                tv_val.setTypeface(tf);

                if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    tv_val.setTextSize(20);
                }

                if (position == 0)
                    im_view.setImageResource(se.sensiblethings.app.R.drawable.circlebtn_limon_with_border);
                else if (position == 1)
                    im_view.setImageResource(se.sensiblethings.app.R.drawable.circlebtn_limon_with_border);
                else if (position == 2)
                    im_view.setImageResource(se.sensiblethings.app.R.drawable.circlebtn_limon_with_border);
                else if (position == 3)
                    im_view.setImageResource(se.sensiblethings.app.R.drawable.circlebtn_limon_with_border);
                else if (position == 4)
                    im_view.setImageResource(se.sensiblethings.app.R.drawable.circlebtn_limon_with_border);
                else if (position == 5) {
                    view.setVisibility(View.GONE);
                    // im_view.setImageResource(se.sensiblethings.app.R.drawable.ic_copyrights);
                }
                view.setPadding(0, 10, 0, 10);
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        }
        /*Private Activity Peers List..*/
        else if (list_serial == 10)
            try {
                ArrayList<String> groups = this.ar_groups;
                CustomImageView custom_imageview;
                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_favorite_group_btn, null);
                textview_subitem = (TextView) view
                        .findViewById(R.id.tv_chat_screen_sub_item);
                btn = (TextView) view.findViewById(R.id.favorite_group_btn);
                custom_imageview = (CustomImageView) view.findViewById(R.id.cust_image_view_one);
                // Font face
                textview_subitem.setTypeface(tf);
                custom_imageview.setImageResource(R.drawable.profile_image_android);
                // btn.setTypeface(tf);
                btn.setTypeface(tf, 1);
                btn.setVisibility(View.GONE);
                if (position == this.index) {
                    if ((context_.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE) {

                        custom_imageview.setMaxHeight(40);
                        custom_imageview.setMaxHeight(40);
                        btn.setBackgroundResource(R.drawable.circlebtn_red_with_border_large_screen);
                        custom_imageview.setBackground(context_.getResources().getDrawable(color.oregon));
                        custom_imageview.setImageResource(R.drawable.profile_image_big);
                        //custom_imageview.setPadding(12, 12, 12, 12);
                    } else

                    {
                        btn.setBackgroundResource(R.drawable.circlebtn_red_with_border);
                        custom_imageview.setBackground(context_.getResources().getDrawable(color.oregon));
                    }

                    textview_subitem.setVisibility(View.VISIBLE);
                    textview_subitem.setText(groups.get(position));
                } else {

                    textview_subitem.setVisibility(View.GONE);
                }

                if (!((String) groups.get(position)).contains("#none#") || groups.get(position) != "") {

                    if (groups.get(position).toString().length() > 1) {
                        if (groups.get(position).toString().equalsIgnoreCase(PlatformManagerNode._mUCI)) {
                            btn.setVisibility(View.GONE);
                            textview_subitem.setVisibility(View.GONE);

                        } else {
                            btn.setText(groups.get(position).subSequence(0, 1));
                        }
                    } else {
                        btn.setVisibility(View.GONE);
                        textview_subitem.setVisibility(View.GONE);
                    }

                    if (PlatformManagerNode._peers_1 != null) {
                        if (PlatformManagerNode._peers_1.contains(groups.get(position).toString())) {
                            btn.setTextColor(context_.getResources().getColor(color.chitchato_mebratu_cyan));

                        }

                    } else if (PlatformManagerBootstrap._peers_1 != null) {
                        if (PlatformManagerBootstrap._peers_1.contains(groups.get(position).toString()))
                            btn.setTextColor(context_.getResources().getColor(color.chitchato_mebratu_cyan));
                    } else {
                        btn.setTextColor(context_.getResources().getColor(color.background_color_1));
                        custom_imageview.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                        custom_imageview.setImageResource(R.drawable.profile_image_big);
                    }
                    btn.setTypeface(tf_goud_sout);
                    textview_subitem.setText(groups.get(position));
                    if ((context_.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE)
                        btn.setTextSize(50.0f);
                    else
                        btn.setTextSize(30.0f);

                } else {
                    btn.setText("Quit");
                    // textview_subitem.setText("To quit click here!");
                }


                btn.setClickable(false);

                // btn.setBackgroundColor(android.graphics.Color.RED);

                String path = getAppFolder("");
                String img_name = groups.get(position).toString().replace("/", "__").replace("@", "___") + ".jpg";

                final String uri_str = path + img_name;
                File _image_file_ = new File(uri_str);
                if (_image_file_.exists())
                    // custom_imageview.setImageURI(Uri.parse(uri_str));
                    custom_imageview.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(uri_str), 55, 55));
                else
                    custom_imageview.setImageResource(R.drawable.profile_image_android);


                // Shouldnt display own image
                if (groups.get(position).equalsIgnoreCase(PlatformManagerNode._mUCI))
                    view.setVisibility(View.GONE);


                view.setPadding(0, 10, 0, 10);


            } catch (Exception e) {

                e.printStackTrace();
                //Intent intent = new Intent(this.context_, ErrorActivity.class);
                //intent.putExtra("error", "System Error");
                // this.context_.startActivity(intent);
            }
         /*Private Activity Long Click Item..*/
        else if (list_serial == 11)
            try {
                ArrayList<String> groups = this.ar_groups;
                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (position == this.index) {
                    if ((context_.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                        view = li.inflate(R.layout.chitchato_favorite_group_btn_private, null);
                        textview_subitem = (TextView) view
                                .findViewById(R.id.tv_chat_screen_sub_item);
                        btn = (TextView) view.findViewById(R.id.favorite_group_btn);
                    } else {
                        view = li.inflate(R.layout.chitchato_favorite_group_btn_private, null);
                        textview_subitem = (TextView) view
                                .findViewById(R.id.tv_chat_screen_sub_item);
                        btn = (TextView) view.findViewById(R.id.favorite_group_btn);
                    }
                    textview_subitem.setVisibility(View.VISIBLE);
                    textview_subitem.setText(groups.get(position));

                    if (!((String) groups.get(position)).contains("#none#")) {
                        // btn.setText(groups.get(position).subSequence(0, 4));
                        textview_subitem.setText(groups.get(position));
                    } else {
                        btn.setText("Quit");
                        // textview_subitem.setText("To quit click here!");
                    }

                } else {

                    view = li.inflate(R.layout.chitchato_favorite_group_btn, null);
                    textview_subitem = (TextView) view
                            .findViewById(R.id.tv_chat_screen_sub_item);
                    btn = (TextView) view.findViewById(R.id.favorite_group_btn);
                    // Font face
                    textview_subitem.setTypeface(tf);
                    // btn.setTypeface(tf);
                    btn.setTypeface(tf, 1);
                    textview_subitem.setVisibility(View.GONE);
                    if (!((String) groups.get(position)).contains("#none#")) {
                        //btn.setText(groups.get(position).subSequence(0, 4));
                        btn.setText(groups.get(position));
                        textview_subitem.setText(groups.get(position));
                    } else {
                        btn.setText("Quit");
                        // textview_subitem.setText("To quit click here!");
                    }
                }


                btn.setClickable(false);

                // btn.setBackgroundColor(android.graphics.Color.RED);

                view.setPadding(0, 10, 0, 10);

            } catch (Exception e) {

                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }

            //* Group Container-- Group Details*//
        else if (list_serial == 12) {
            try {
                CheckBox ck_box = null;
                TextView tv_language = null;
                ImageView img_view_small_icon = null, imageView = null;
                LinearLayout ll;
                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_settings_radio, null);
                tv_language = (TextView) view
                        .findViewById(R.id.tv_lang_checkbox);
                img_view_small_icon = (ImageView) view.findViewById(R.id.img_settings_radio_small_icon);
                imageView = (ImageView) view.findViewById(R.id.lang_chat_radio);
                ll = (LinearLayout) view.findViewById(R.id.ll_check_box);
                tv_language.setTypeface(tf);
                img_view_small_icon.setVisibility(View.GONE);

                String group_val = ar_groups.get(position);
                tv_language.setText(group_val);
                tv_language.setTextSize(20.0f);
                tv_language.setGravity(Gravity.CENTER);
                if (ar_groups.size() == 4) {
                    imageView.setImageResource(R.drawable.circlebtn_limon_with_border);
                    //  if (position == 0) imageView.setImageResource(R.drawable.ic_groups);
                    //  else if (position == 1) imageView.setImageResource(R.drawable.ic_groups);
                    //  else if (position == 2) imageView.setImageResource(R.drawable.ic_groups);
                    //  else if (position == 3) imageView.setImageResource(R.drawable.ic_groups);
                } else if (ar_groups.size() == 2) {
                    if (position == 0) {
                        imageView.setImageResource(R.drawable.circlebtn_limon_with_border);
                    } else {
                        imageView.setImageResource(R.drawable.circlebtn_limon_with_border);
                    }
                }


                if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    tv_language.setTextSize(23);
                    view.setPadding(0, 10, 0, 10);
                    ll.setMinimumHeight(150);

                } else {
                    ll.setMinimumHeight(100);
                    view.setPadding(0, 5, 0, 5);

                }
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        }
        //* Search Container-- Group Details*//
        else if (list_serial == 120) {
            try {
                CheckBox ck_box = null;
                TextView tv_language = null;
                ImageView img_view_small_icon = null, imageView = null;
                LinearLayout ll;
                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_settings_radio, null);
                tv_language = (TextView) view
                        .findViewById(R.id.tv_lang_checkbox);
                img_view_small_icon = (ImageView) view.findViewById(R.id.img_settings_radio_small_icon);
                imageView = (ImageView) view.findViewById(R.id.lang_chat_radio);
                ll = (LinearLayout) view.findViewById(R.id.ll_check_box);
                tv_language.setTypeface(tf);
                img_view_small_icon.setVisibility(View.GONE);

                String group_val = ar_groups.get(position);
                tv_language.setText(group_val);
                tv_language.setTextSize(20.0f);
                tv_language.setGravity(Gravity.CENTER);

                if (position == 0) {
                    imageView.setImageResource(android.R.drawable.ic_menu_search);
                } else {
                    imageView.setImageResource(android.R.drawable.ic_menu_search);
                }

                if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    tv_language.setTextSize(23);
                    ll.setMinimumHeight(100);
                    tv_language.setMinimumHeight(100);
                    view.setMinimumHeight(100);

                } else {
                    ll.setMinimumHeight(250);
                    view.setPadding(0, 5, 0, 5);

                }
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        }
        //* Group Container-- Notifications Details*//
        else if (list_serial == 13) {
            try {

                CheckBox ck_box = null;
                TextView tv_language = null;

                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_language_radio_noti, null);
                tv_language = (TextView) view
                        .findViewById(R.id.tv_lang_checkbox);
                TextView btn_accept = (TextView) view.findViewById(R.id.btn_accept);
                TextView btn_decline = (TextView) view.findViewById(R.id.btn_decline);
                CustomImageView customImageView = (CustomImageView) view.findViewById(R.id.cust_imageView_grditem_1);

                btn_accept.setVisibility(View.VISIBLE);
                btn_decline.setVisibility(View.VISIBLE);

                tv_language.setTypeface(tf);
                String group_val = ar_groups.get(position);
                if (ar_groups.get(position).contains(":::")) {
                    String _temp_0 = ar_groups.get(position).split(":::")[1];
                    if (_temp_0.contains("~::~")) {
                        String[] _temp_ = _temp_0.split("~::~");
                        if (_temp_.length > 4) {
                            String group_name = "Name:" + _temp_[0];
                            String group_intereste = "\nNick:" + _temp_[1];
                            String group_age_limit = "\nAge:" + _temp_[4];
                            //String CreationDate = "\nCreation Date:" + _temp_[5];
                            group_val = group_name + group_intereste + group_age_limit;
                        }

                    }

                } else if (ar_groups.get(position).contains("~::~")) {
                    if (ar_groups.get(position).contains("~::~~0btstrp")) {
                        int index = 0;
                        String[] _temp_0 = ar_groups.get(position).split("~::~");
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
                                    group_val = _tmp_0002 + "." + "\n" + _tmp_0003 + "." + " " + _tmp_0004 + "." + _tmp_0005 + "." +
                                            "+ \n\n " + format.format(new Date());
                                    btn_accept.setText("Add");
                                    btn_decline.setVisibility(View.GONE);
                                    customImageView.setVisibility(View.GONE);
                                }
                            }
                            index++;
                        }

                    } else {
                        String[] _temp_0 = ar_groups.get(position).split("~::~");
                        if (_temp_0.length > 4) {
                            String group_name = _temp_0[1];
                            String peer_name = _temp_0[2];
                            String peer_nick = _temp_0[3];
                            String date = _temp_0[4];
                            //String CreationDate = "\nCreation Date:" + _temp_[5];
                            group_val = group_name + "\n" + peer_name + " " + peer_nick + "\n " + date;
                        }

                    }
                } else {
                    //

                    String ip = getStringByPattern(group_val, "");
                    if (ip.equalsIgnoreCase("~::~")) {
                        view.setVisibility(View.GONE);
                    } else {
                        btn_accept.setText("Add");
                        btn_decline.setBackgroundResource(R.drawable.rectangletv_1);
                        btn_decline.setText(format.format(new Date()));
                        customImageView.setVisibility(View.GONE);
                    }

                }
                //Show Notification


                tv_language.setText(group_val);
                tv_language.setTextSize(12.0f);

                if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    tv_language.setTextSize(16.0f);
                }
                view.setPadding(0, 10, 0, 10);

                custom.set_NEW_INFO_AVAILABLE_(true);
                custom.set_NOTIFICATIONS_AVAILABLE(true);


            } catch (Exception e) {

                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        }// List all Accessible Global Bootstraps
        else if (list_serial == 14) {
            try {
                EditText edt_txt = null;
                Button btn_ = null;
                ArrayList<String> bootstraps = this.ar_groups;

                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_m2m_griditem, null);
                final TextView tv_bootstrap_ip = (TextView) view.findViewById(R.id.btn_m2m_btn);
                final TextView tv_select_btn = (TextView) view.findViewById(R.id.btn_m2m_on_off_btn);

                //ck_box = (CheckBox) view.findViewById(R.id.lang_chat_radio);

                tv_bootstrap_ip.setTypeface(tf);
                tv_select_btn.setTypeface(tf);
                tv_select_btn.setText("Add");
                tv_bootstrap_ip.setText(bootstraps.get(position));
                tv_select_btn.setClickable(true);
                tv_select_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if ((context_.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE) {

                            tv_select_btn.setBackgroundResource(R.drawable.circlebtn_red_with_border);

                        } else

                            tv_select_btn.setBackgroundResource(R.drawable.circlebtn_red_with_border);


                        //DialogOne dialogOne = new DialogOne(context_, true, 14);
                        //dialogOne.setDialog_message("Unable to add Bootstrap");
                        //dialogOne.show();
                    }
                });

                if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    tv_bootstrap_ip.setTextSize(20);
                    tv_select_btn.setTextSize(20);
                }
                view.setPadding(0, 10, 0, 10);

            } catch (Exception e) {

                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        } else if (list_serial == 15) {
            try {
                EditText edt_txt = null;
                Button btn_ =   null;
                ArrayList<String> bootstraps = this.ar_groups;
                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_m2m_griditem, null);
                final TextView tv_bootstrap_ip = (TextView) view.findViewById(R.id.btn_m2m_btn);
                final TextView tv_select_btn = (TextView) view.findViewById(R.id.btn_m2m_on_off_btn);

                tv_bootstrap_ip.setTypeface(tf);
                tv_select_btn.setTypeface(tf);
                tv_select_btn.setText("ADV");
                tv_bootstrap_ip.setText(bootstraps.get(position));
                tv_select_btn.setClickable(true);

                final WifiApiManager wifiApManager = new WifiApiManager(context_);
                final String SSID = tv_bootstrap_ip.getText().toString();
                tv_select_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((context_.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                            tv_select_btn.setBackgroundResource(R.drawable.circlebtn_red_with_border);
                        } else
                            tv_select_btn.setBackgroundResource(R.drawable.circlebtn_red_with_border);

                        //DialogOne dialogOne = new DialogOne(context_, true, 14);
                        //dialogOne.setDialog_message("Unable to add Bootstrap");
                        //dialogOne.show();
                        if (tv_select_btn.getText().toString().equals("...")) {
                            {
                                //lv_1.setAdapter(list_adapter_1);
                                tv_select_btn.setText("ADV");
                                displayCustomizedToast(context_.getApplicationContext(), "Group Add OFF: " + SSID);
                                wifiApManager.setWifiApConfiguration(null);
                                wifiApManager.setSSID(SSID);

                                if (wifiApManager.isWifiApEnabled()) {
                                    wifiApManager.setWifiEnable(null, false);
                                }
                            }
                        } else {
                            tv_select_btn.setText("...");
                            displayCustomizedToast(context_.getApplicationContext(), "Group Add ON: " + SSID);
                            wifiApManager.setWifiApConfiguration(null);
                            wifiApManager.setSSID(SSID);

                            if (wifiApManager.isWifiApEnabled()) {
                                wifiApManager.setWifiEnable(null, false);
                            } else
                                wifiApManager.setWifiEnable(null, true);

                        }

                        MessageNotifier(null, "Advertising Group:" + SSID, "", "", true);

                    }
                });

                if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    tv_bootstrap_ip.setTextSize(20);
                    tv_select_btn.setTextSize(20);
                }
                view.setPadding(0, 10, 0, 10);

            } catch (Exception e) {

                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        } else if (list_serial == 16) {
            try {

                CheckBox ck_box = null;
                TextView tv_language = null;
                Button btn_accept = null;
                Button btn_decline = null;
                LinearLayout ll_1, ll_2, ll_small;
                ImageView img_view_small_icon, imageView;
                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_settings_radio, null);
                img_view_small_icon = (ImageView) view.findViewById(R.id.img_settings_radio_small_icon);
                imageView = (ImageView) view.findViewById(R.id.lang_chat_radio);
                tv_language = (TextView) view
                        .findViewById(R.id.tv_lang_checkbox);
                ll_1 = (LinearLayout) view.findViewById(R.id.ll_check_box);
                ll_2 = (LinearLayout) view.findViewById(R.id.ll_check_box_);
                ll_small = (LinearLayout) view.findViewById(R.id.linear_layout);
                tv_language.setTypeface(tf);

                String group_val = ar_groups.get(position);
                tv_language.setText(group_val);
                tv_language.setTextSize(20.0f);
                tv_language.setGravity(Gravity.CENTER);
                imageView.setBackground(context_.getResources().getDrawable(R.drawable.circlebtn_backgrd_color_));
                if (position == 0)
                    imageView.setImageResource(R.drawable.ic_sensors);
                else if (position == 1)
                    imageView.setImageResource(R.drawable.bootstrapper);
                img_view_small_icon.setVisibility(View.GONE);
                ll_small.setVisibility(View.GONE);


                // if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                if ((context_.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    tv_language.setTextSize(23);
                    // view.setPadding(0, 5, 0, 5);
                    ll_2.setMinimumHeight(100);
                    //tv_language.setMinimumHeight(100);
                    view.setPadding(0, 10, 0, 10);
                    //view.setMinimumHeight(200);
                    //imageView.setMinimumHeight(73);
                    //imageView.setMinimumWidth(73);
                    //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER.CENTER);


                    if (position == 0)
                        imageView.setImageResource(R.drawable.ic_sensors_big);
                    else if (position == 1)
                        imageView.setImageResource(R.drawable.bootstrapper);
                } else {
                    //view.setMinimumHeight(200);
                    //tv_language.setMinimumHeight(250);
                    ll_2.setMinimumHeight(250);
                    view.setPadding(0, 5, 0, 5);
                }


            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        }
        //Sense Screen-- CON_Readings//
        else if (list_serial == 17) {
            try {
                CheckBox ck_box = null;
                TextView tv_language = null, tv_btn_add = null;
                Button btn_accept = null;
                Button btn_decline = null;
                LinearLayout ll_1, ll_2, ll_3;
                ImageView img_view_small_icon, imageView;
                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_settings_radio, null);
                img_view_small_icon = (ImageView) view.findViewById(R.id.img_settings_radio_small_icon);
                imageView = (ImageView) view.findViewById(R.id.lang_chat_radio);
                tv_language = (TextView) view
                        .findViewById(R.id.tv_lang_checkbox);

                ll_1 = (LinearLayout) view.findViewById(R.id.ll_check_box);
                ll_2 = (LinearLayout) view.findViewById(R.id.ll_check_box_);
                ll_3 = (LinearLayout) view.findViewById(R.id.linear_layout);
                tv_language.setTypeface(tf);

                final String group_val = ar_groups.get(position);
                tv_language.setText(group_val.replace("::BS:","").replace("::GP:","").replace("::ADV","") + " " + format.format(new Date()));
                tv_language.setTextSize(20.0f);
                tv_language.setGravity(Gravity.CENTER);
                img_view_small_icon.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                ll_3.setVisibility(View.GONE);
                tv_language.setPadding(0, 0, 0, 0);


                if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    tv_language.setTextSize(14);
                    tv_language.setMinimumWidth(ll_1.getWidth());
                    view.setPadding(15, 15, 15, 15);
                    view.setMinimumHeight(200 + (tv_language.getText().toString().length() / 15) * 40);
                    view.setMinimumWidth(ll_1.getWidth());

                } else {
                    tv_language.setTextSize(14);
                    tv_language.setPadding(0, 0, 0, 0);
                    tv_language.setMinimumWidth(ll_1.getWidth());
                    view.setMinimumHeight(300 + (tv_language.getText().toString().length() / 15) * 40);
                    view.setPadding(1, 1, 1, 1);
                    view.setMinimumWidth(ll_1.getWidth());
                }

                final Animation animation = AnimationUtils.loadAnimation(context_, R.anim.show_the_latest_messages_lv);
                view.setAnimation(animation);
                if (group_val.contains(":GP:")) {
                    if (view.getAnimation().isInitialized())
                        view.clearAnimation();
                    else
                        view.startAnimation(animation);

                    if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                        view.setMinimumHeight(20 + (tv_language.getText().toString().length() / 15) * 40);
                        view.setMinimumWidth(ll_1.getWidth());

                    } else {
                        view.setMinimumHeight(30 + (tv_language.getText().toString().length() / 15) * 40);
                        view.setPadding(1, 1, 1, 1);
                        view.setMinimumWidth(ll_1.getWidth());
                    }
                    ll_1.setBackground(context_.getResources().getDrawable(R.drawable.rectangletv_4_normal_transparent));
                    ll_2.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                    ll_3.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                    tv_language.setTextColor(context_.getResources().getColor(color.indigo));

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            view.clearAnimation();
                            DialogOne dialog_one = new DialogOne(context_, false, 6);
                            //dialog_one.setDialog_message("Do you need to add Bootstrap IP: \n"+ group_val);
                            dialog_one.setTxt_toDisplay("Do you need to add Group : \n" + group_val);
                            dialog_one.show();

                        }
                    });
                    ll_2.setMinimumHeight(ll_1.getWidth());
                    ll_3.setMinimumHeight(ll_1.getWidth());
                } else if (group_val.contains(":BS:")) {
                    ll_1.setBackground(context_.getResources().getDrawable(R.drawable.rectangletv_4_normal_transparent));
                    ll_2.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                    ll_3.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                    ll_2.setMinimumHeight(ll_1.getWidth());
                    ll_3.setMinimumHeight(ll_1.getWidth());
                    tv_language.setTextColor(context_.getResources().getColor(color.maroon));

                    if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                        view.setMinimumHeight(20 + (tv_language.getText().toString().length() / 15) * 40);
                        view.setMinimumWidth(ll_1.getWidth());

                    } else {
                        view.setMinimumHeight(30 + (tv_language.getText().toString().length() / 15) * 40);
                        view.setPadding(1, 1, 1, 1);
                        view.setMinimumWidth(ll_1.getWidth());
                    }


                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            view.clearAnimation();
                            String ip = getStringByPattern(group_val, "");
                            if (ip.equalsIgnoreCase("~::~"))
                                ip = group_val + "\n" + "This is not valid IP. \n Try other.";
                            DialogOne dialog_one = new DialogOne(context_, false, 6);
                            //dialog_one.setDialog_message("Do you need to add Bootstrap IP: \n"+ group_val);
                            dialog_one.setTxt_toDisplay("Do you want to add Bootstrap IP: \n" + ip);
                            dialog_one.show();
                        }
                    });


                } else if (group_val.contains("~E~N~D~")) {
                    ll_1.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                    ll_2.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                    ll_3.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                    ll_2.setMinimumHeight(ll_1.getWidth());
                    ll_3.setMinimumHeight(ll_1.getWidth());
                    tv_language.setTextColor(context_.getResources().getColor(color.chitchato_mebratu_cyan));
                    tv_language.append("\n" + format.format(new Date()));
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            view.clearAnimation();
                            DialogOne dialog_one = new DialogOne(context_, false, 0);
                            //dialog_one.setDialog_message("Do you need to add Bootstrap IP: \n"+ group_val);
                            dialog_one.setTitle("");
                            dialog_one.setTxt_toDisplay("Advertisements \n" + group_val);
                            dialog_one.show();

                        }
                    });
                    if(position==0) view.clearAnimation();



                } else {
                    ll_1.setBackground(context_.getResources().getDrawable(R.drawable.rectangletv_4_normal_transparent));
                    // ll_1.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                    ll_2.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                    ll_3.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                    ll_2.setMinimumHeight(ll_1.getWidth());
                    ll_3.setMinimumHeight(ll_1.getWidth());
                    tv_language.setTextColor(context_.getResources().getColor(color.green_other));
                    tv_language.setLines(15);
                    tv_language.setTextSize(24);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            view.clearAnimation();
                            DialogOne dialog_one = new DialogOne(context_, false, 0);
                            dialog_one.setTitle("");
                            dialog_one.setTxt_toDisplay("Advertisements \n" + group_val);
                            dialog_one.show();

                        }
                    });
                }


            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        }

        //Adv small //
        else if (list_serial == 18) {
            try {
                CheckBox ck_box = null;
                TextView tv_language = null;
                Button btn_accept = null;
                Button btn_decline = null;
                LinearLayout ll_1, ll_2, ll_3;
                ImageView img_view_small_icon, imageView;
                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.spinner_item_one, null);
                tv_language = (TextView) view
                        .findViewById(R.id.spinner_item_one);

                tv_language.setTypeface(tf);

                String group_val = ar_groups.get(position);
                tv_language.setText(group_val);
                tv_language.setTextSize(12.0f);
                tv_language.setGravity(Gravity.CENTER);
                if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    tv_language.setTextSize(10);
                    view.setPadding(1, 1, 1, 1);

                    tv_language.setMaxLines(20);
                    tv_language.setMinLines(2);

                } else {
                    tv_language.setTextSize(10);
                    view.setPadding(1, 1, 1, 1);
                    tv_language.setMaxLines(20);
                    tv_language.setMinLines(2);
                }
                if (group_val.contains(":GP:")) {
                    // view.setBackground(context_.getResources().getDrawable(R.drawable.rectangletv_2));
                } else if (group_val.contains(":BS:")) {
                    //view.setBackground(context_.getResources().getDrawable(R.drawable.rectangletv_2));

                } else {
                    //ll_1.setBackground(context_.getResources().getDrawable(R.drawable.rectangletv_4_normal));

                }
                Animation animation;


                if (getSmall_gridVW() == 0)
                    animation = AnimationUtils.loadAnimation(context_, R.anim.move_tv_to_right_side_two);
                else {
                    animation = AnimationUtils.loadAnimation(context_, R.anim.move_tv_to_left_side_two);
                }
                // animation.setDuration(180000);
                // view.setAnimation(animation);
                view.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context_, ErrorActivity.class);
                        intent.putExtra("error", "CON_READINGS");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context_.startActivity(intent);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                //this.context_.startActivity(intent);
            }
        } else if (list_serial == 19) {
            try {
                EditText edt_txt = null;
                Button btn_ = null;

                ArrayList<String> bootstraps = this.ar_groups;

                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_m2m_griditem, null);
                final TextView tv_bootstrap_ip = (TextView) view.findViewById(R.id.btn_m2m_btn);
                final TextView tv_select_btn = (TextView) view.findViewById(R.id.btn_m2m_on_off_btn);

                tv_bootstrap_ip.setTypeface(tf);
                tv_select_btn.setTypeface(tf);
                tv_select_btn.setText("ADD");
                tv_bootstrap_ip.setText(bootstraps.get(position));
                tv_select_btn.setClickable(true);
                tv_select_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if ((context_.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE) {

                            tv_select_btn.setBackgroundResource(R.drawable.circlebtn_red_with_border);

                        } else

                            tv_select_btn.setBackgroundResource(R.drawable.circlebtn_red_with_border);

                    }
                });
                if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    tv_bootstrap_ip.setTextSize(20);
                    tv_select_btn.setTextSize(20);
                }
                view.setPadding(0, 10, 0, 10);

            } catch (Exception e) {

                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        }

        //MEssagess -- for expandable List view//
        else if (list_serial == 20) {

            try {

                CheckBox ck_box = null;
                TextView tv_language = null;
                ImageView img_view_small_icon = null, imageView = null;
                LinearLayout ll, ll_1, ll_2, ll_3;
                li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.chitchato_settings_radio, null);
                tv_language = (TextView) view
                        .findViewById(R.id.tv_lang_checkbox);
                img_view_small_icon = (ImageView) view.findViewById(R.id.img_settings_radio_small_icon);
                imageView = (ImageView) view.findViewById(R.id.lang_chat_radio);
                ll = (LinearLayout) view.findViewById(R.id.ll_check_box);
                ll_1 = (LinearLayout) view.findViewById(R.id.ll_check_box);
                ll_2 = (LinearLayout) view.findViewById(R.id.ll_check_box_);
                ll_3 = (LinearLayout) view.findViewById(R.id.linear_layout);
                tv_language.setTypeface(tf);


                img_view_small_icon.setVisibility(View.GONE);


                String group_val = ar_groups.get(position);
                tv_language.setText(group_val);
                tv_language.setTextSize(20.0f);
                tv_language.setGravity(Gravity.CENTER);


                if (context_.getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                    tv_language.setTextSize(14);
                    view.setPadding(0, 5, 0, 5);
                    //  tv_language.setMinimumHeight(200);
                    ll.setMinimumHeight(150);

                } else {
                    view.setPadding(0, 5, 0, 5);
                    //  tv_language.setMinimumHeight(250);
                    ll.setMinimumHeight(150);

                }

                ll_1.setBackground(context_.getResources().getDrawable(R.drawable.rectangletv_8));
                ll_2.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                ll_3.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));


            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        } else
            ;

        return view;
    }

    public void setPosition(int j) {
        this.index = j;

    }

    public String getCurrActivePosition() {
        return curr_active_grp;
    }

    public void setCurrActivePosition(String grp_nm) {
        this.curr_active_grp = grp_nm;
    }

    public void setGroups_(TreeMap map) {
        this.tm_groups = map;
        ar_groups = new ArrayList<String>();
        String[] split;
        String value;
        String[] temp;
        Set set = tm_groups.entrySet();
        Iterator itr = new HashSet(tm_groups.entrySet()).iterator();
        while (itr.hasNext()) {

            Map.Entry me = (Map.Entry) itr.next();
            value = (String) me.getValue();
            temp = value.split(":");
            Log.i("value", temp[1]);
            ar_groups.add(temp[1]);

        }

    }

    public ArrayList getGroups() {
        return this.ar_groups;
    }

    public void setGroups(ArrayList array_list) {
        this.ar_groups = array_list;

    }

    public boolean isSetProfilePictureGone() {
        return setProfilePictureGone;
    }

    public void setSetProfilePictureGone(boolean setProfilePictureGone) {
        this.setProfilePictureGone = setProfilePictureGone;
    }

    public void MessageNotifier(View view) {
        Intent intent = new Intent(context_.getApplicationContext(),
                GroupContainerActivity.class);
        intent.putExtra("INDEX", "NOTIFICATIONS");
        PendingIntent pintent = PendingIntent.getActivity(context_.getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
        PendingIntent pintent_ = PendingIntent.getActivity(context_.getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
        Notification _mNotification = new Notification.Builder(context_.getApplicationContext()).setContentTitle("Contexter")
                .setContentText("You have New Group Information").setSmallIcon(R.drawable.ic_launcher_mini).setContentIntent(pintent)
                .addAction(R.drawable.button_custom, "See Message", pintent)
                .addAction(R.drawable.button_custom, "Cancel this", pintent)
                .build();

        NotificationManager _notification_manager = (NotificationManager) context_.getSystemService(context_.getApplicationContext().NOTIFICATION_SERVICE);
        _mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        _notification_manager.notify(0, _mNotification);
        //   _notification_manager.cancel(View.VISIBLE);

    }

    public void MessageNotifier(View view, String mess_1, String mess_2, String mess_3, boolean status) {
        Intent intent = new Intent(context_.getApplicationContext(), GroupContainerActivity.class);
        intent.putExtra("error", "Notifier");
        intent.putExtra("INDEX", "NOTIFICATIONS");
        intent.putExtra("ST_STATUS", status);
        PendingIntent pintent = PendingIntent.getActivity(context_.getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
        Notification _mNotification = null;
        if (status)
            _mNotification = new Notification.Builder(context_.getApplicationContext()).setContentTitle("Contexter")
                    .setContentText(mess_1).setSmallIcon(R.drawable.ic_launcher_mini).setContentIntent(pintent)
                    .addAction(R.drawable.button_custom, "See Detail", pintent)
                    .addAction(R.drawable.button_custom, "Cancel this", pintent)
                    .build();
        else
            _mNotification = new Notification.Builder(context_.getApplicationContext()).setContentTitle("Contexter")
                    .setContentText(mess_1).setSmallIcon(R.drawable.ic_launcher_mini).setContentIntent(pintent)
                    .build();


        NotificationManager _notification_manager = (NotificationManager) context_.getSystemService(context_.getApplicationContext().NOTIFICATION_SERVICE);
        _mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        //id -3 -- group advertisment status
        _notification_manager.notify(3, _mNotification);
        //   _notification_manager.cancel(0);

    }

    public void displayCustomizedToast(Context _context_, String message) {

        LayoutInflater inflater = (LayoutInflater) context_.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_ = inflater.inflate(R.layout.dialog_one, null);
        TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);
        tv_0_0_.setText(message);
        Toast toast = new Toast(_context_);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view_);
        toast.show();
    }

    public String getStringByPattern(String _input_, String pat) {
        String ptr = "~::~";
        String ip_address_pattern = "\\b([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\b";
        //String ip_address_pattern = "\\b10.1.90.247\\b";
        final Pattern pattern = Pattern.compile(ip_address_pattern);

        Matcher matcher = pattern.matcher(_input_);
        while (matcher.find()) {
            int ind_b = matcher.start();
            int ind_e = matcher.end();
            ptr = _input_.substring(ind_b, ind_e);
        }
        return ptr;
    }


}
