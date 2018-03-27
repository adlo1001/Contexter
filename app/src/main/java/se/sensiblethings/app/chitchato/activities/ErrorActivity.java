package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.Set;
import java.util.TreeMap;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.adapters.ListAdapter;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerNode;
import se.sensiblethings.app.chitchato.extras.CustomImageView;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.kernel.PublicChats;
import se.sensiblethings.disseminationlayer.communication.Message;

public class ErrorActivity extends Activity {
    private static final int SELECT_IMAGE_1 = 1;
    private static final int SELECT_IMAGE_2 = 2;
    private static final int SELECT_IMAGE_3 = 3;
    private static final int SELECT_IMAGE_4 = 4;
    private static final int SELECT_IMAGE_5 = 5;
    private static final int SELECT_IMAGE_6 = 6;
    private static final int SELECT_IMAGE_7 = 7;
    private static final int SELECT_IMAGE_8 = 8;
    protected Customizations custom;
    protected SharedPreferences mPrefs;
    protected SharedPreferences.Editor ed;
    protected TreeMap bs_map = new TreeMap<String, String>();
    TextView tv_1;
    CustomImageView cust_img_cntxt, cust_img_grp, cust_img_uci, cust_img_src, cust_img_hlp, cust_img_abt, cust_img_q;
    EditText edt;
    Button btn, btn_reset;
    ListView lv;
    long l;
    String temp;
    ArrayList activenodes;
    String[] a;
    Message message;
    String nodes = "%";
    ListView lv_1, lv_2;
    GridView gv_1;
    String error = "No Error.";
    String activity_name = "ChoicesActivity";
    ListAdapter listadapter;
    LinearLayout ll;
    private String uri_str = "#Nothing#";
    private Uri selectedImageUri = null;
    private String selectedImagePath = "#Nothing#";
    private String filemanagerstring = "#Nothing#";
    private ArrayList<String> al_ = new ArrayList<String>();
    private Set<String> bootstraps;

    //Get Path for Home Folder
    public static String getAppFolder(String file_name) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/";
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);


        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences("myprofile", 0);
        custom = new Customizations(this, -1);
        setContentView(R.layout.errorscreen);
        setTitle("");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        tv_1 = (TextView) findViewById(R.id.tv_error);
        btn = (Button) findViewById(R.id.btn_error_try_again);
        edt = (EditText) findViewById(R.id.edt_error_box);
        lv = (ListView) findViewById(R.id.lv_err_list);

        ll = (LinearLayout) findViewById(R.id.linear_other_settings);
        cust_img_cntxt = (CustomImageView) findViewById(R.id.cust_img_btn_contexter);
        cust_img_grp = (CustomImageView) findViewById(R.id.cust_img_btn_group);
        cust_img_uci = (CustomImageView) findViewById(R.id.cust_img_btn_uci);
        cust_img_src = (CustomImageView) findViewById(R.id.cust_img_btn_search);
        cust_img_hlp = (CustomImageView) findViewById(R.id.cust_img_btn_help);
        cust_img_abt = (CustomImageView) findViewById(R.id.cust_img_btn_about);
        cust_img_q = (CustomImageView) findViewById(R.id.cust_img_btn_quit);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");
        Typeface tf_pala = Typeface.createFromAsset(getAssets(), "fonts/pala.ttf");
        tv_1.setTypeface(tf);
        edt.setTypeface(tf);
        btn.setTypeface(tf);
        Bundle extras = getIntent().getExtras();
        error = extras.getString("error");


        if (error != null)
            if (error.equalsIgnoreCase("Notifier")) {
                ll.setVisibility(View.GONE);
                String platform_desc = getResources().getString(R.string.tips_text_en);
                if (extras != null)
                    platform_desc = extras.getString("ST_DESC", "");
                boolean st_status = extras.getBoolean("ST_STATUS");
                btn.setVisibility(View.GONE);
                edt.setTextColor(getResources().getColor(R.color.chitchato_mebratu_blue));
                tv_1.setText("MediaSense");
                edt.setGravity(Gravity.LEFT);
                edt.setTypeface(tf_pala);
                // for Tabs-- font size
                if (getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE)
                    edt.setTextSize(14.0f);
                if (st_status) {
                    edt.append("\n Bootstrap IP Address:\n " + PlatformManagerNode.BOOTSTRAP_IP + "\n");
                    edt.append("\n Bootstrap Name:\n" + PlatformManagerNode.BOOTSTRAP_NAME + "\n");
                    edt.append("\n Start up Time:\n" + PlatformManagerNode.STARTUP_TIME + "\n");
                    edt.append("\n Scope: Local");
                    edt.append("\n Capacity: unknown");
                    edt.append("\n Rate: 10");
                    edt.append("\n Trusted: Yes");
                    edt.append("\n\n" + platform_desc);
                } else {
                    edt.append("\n Bootstrap IP Address:" + PlatformManagerNode.BOOTSTRAP_IP + "\n");
                    edt.append("\n Bootstrap Name:" + PlatformManagerNode.BOOTSTRAP_NAME + "\n");
                    edt.append("\n Start up time:" + PlatformManagerNode.STARTUP_TIME + "\n");
                    edt.append("\n\n" + platform_desc + "\n\n");
                    edt.append(getResources().getString(R.string.tips_text_en));

                    edt.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url_2 = "http://contexter.mobi";
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url_2));
                            startActivity(intent);
                        }
                    });
                }

            } else if (error.equalsIgnoreCase("GROUPS")) {
                ll.setVisibility(View.GONE);
                String group_details = extras.getString("GROUPS");
                btn.setVisibility(View.GONE);
                edt.setTextColor(getResources().getColor(R.color.chitchato_mebratu_blue));
                edt.setText("");
                tv_1.setText("Groups Information ");
                edt.setGravity(Gravity.LEFT);
                edt.setTypeface(tf_pala);
                // for Tabs-- font size
                if (getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE)
                    edt.setTextSize(14.0f);
                edt.append(group_details);


            } else if (error.equalsIgnoreCase("CON_READINGS")) {
                ll.setVisibility(View.GONE);
                btn.setVisibility(View.GONE);
                LinearLayout ll = (LinearLayout) findViewById(R.id.linear1_1);
                edt.setTextColor(getResources().getColor(R.color.chitchato_mebratu_blue));
                tv_1.setText("Contexter Readings");
                edt.setGravity(Gravity.LEFT);
                edt.setTypeface(tf_pala);
                edt.setText("");
                ll.setBackgroundColor(getResources().getColor(R.color.chitchato_mebratu_blue));

                String tmp = custom.getCONTEXTER_ADVS();


                if (true/*tmp.contains("~::~")*/) {

                    lv.setVisibility(View.VISIBLE);
                    edt.setVisibility(View.GONE);
                    String[] tmp_00 = tmp.split("::");
                    for (int i = 0; i < tmp_00.length; i++) {
                        if (!tmp_00[i].trim().isEmpty())
                            al_.add(tmp_00[i] + "::GP:");
                    }

                    mPrefs = getSharedPreferences("bootstraps", 0);
                    bootstraps = mPrefs.getStringSet("bootstraps", bs_map.keySet());
                    for (String tmp_0 : bootstraps) {
                        if (tmp_0.trim().isEmpty())
                            al_.add(tmp_0 + "::BS:    Bootstrap ");
                    }

                    PublicChats _thread_0 = new PublicChats(getBaseContext(), MainActivity.SELECTED_GROUP, "advs");
                    ArrayList<String> temp_ar = _thread_0.getmPublicChat("");
                    for (String temp_00 : temp_ar) {
                        if (temp_00.trim().isEmpty())
                            al_.add(temp_00 + "::ADV");
                    }

                    if (!custom.getCONTEXTER_ADVS().trim().isEmpty())
                        al_.add(custom.getCONTEXTER_ADVS() + "::ADV");
                    if (al_.size() > 0) al_.add("~E~N~D~");
                    LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    listadapter = new ListAdapter(this, -1, false, al_, 17);
                    //lv.addFooterView();

                    View view = li.inflate(R.layout.chitchato_group_btn, null);
                    ImageView iv = (ImageView) view.findViewById(R.id.img_refresh);
                    iv.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ErrorActivity.this, ErrorActivity.class);
                            intent.putExtra("error", "CON_READINGS");
                            startActivity(intent);

                        }
                    });
                    lv.addHeaderView(view);
                    lv.setAdapter(listadapter);

                } else {
                    // for Tabs-- font size
                    if (getResources().getConfiguration().screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE)
                        edt.setTextSize(14.0f);
                    if (custom.isST_UP()) {
                        edt.append("\n Name: " + custom.getCONTEXTER_ADVS() + "\n");
                        edt.append("\n Interest:" + "N/A");
                        edt.append("\n Time:" + "N/A");
                        edt.append("\n Location:" + "N/A");
                        edt.append("\n Purpose: unknown");
                        edt.append("\n BootStarp: N/A");
                        edt.append("\n Group: N/A ");
                        edt.append("\n ADV: Yes ");
                        edt.append("\n Trusted: N/A \n\n\n");
                    } else {

                        edt.append("\n Contexter Name: " + "N/A" + "");
                        edt.append("\n Interest: " + "N/A" + "");
                        edt.append("\n Time: " + "N/A" + "");
                        edt.append("\n Location: " + "N/A" + "");
                        edt.append("\n Purpose: unknown" + "");
                        edt.append("\n BootStarp: N/A ");
                        edt.append("\n Group: N/A ");
                        edt.append("\n ADV: Yes ");
                        edt.append("\n Trusted: N/A \n\n\n");
                    }
                }

            } else if (error.equalsIgnoreCase("OTHER")) {
                edt.setVisibility(View.GONE);
                tv_1.setText("Other Settings");
                mPrefs = getSharedPreferences("myprofile", 0);
                ed = mPrefs.edit();

                btn.setText("Exit");
                btn.setTypeface(tf_pala);

                ll.setVisibility(View.VISIBLE);
                final CheckBox silent_ckbx_one = (CheckBox) findViewById(R.id.checkBox_beep_one);
                final CheckBox silent_ckbx_two = (CheckBox) findViewById(R.id.checkBox_beep_two);
                final CheckBox silent_ckbx_three = (CheckBox) findViewById(R.id.checkBox_beep_three);
                final CheckBox pattern_ckbx_four = (CheckBox) findViewById(R.id.checkBox_bk_grnd_img);
                final CheckBox ckbx_five = (CheckBox) findViewById(R.id.checkBox_notification);

                final EditText edt_adv_name = (EditText) findViewById(R.id.edt_adv_name);
                final EditText edt_adv_desc = (EditText) findViewById(R.id.edt_adv_dsc);
                final EditText edt_adv_need_to_pay = (EditText) findViewById(R.id.edt_need_to_pay);
                Button btn_add_adv = (Button) findViewById(R.id.button_add_adv);
                btn_reset = (Button) findViewById(R.id.button_reset_defaults);
                TextView tv_name_adv = (TextView) findViewById(R.id.tv_adv_name);

                //update the status from preferences
                silent_ckbx_one.setChecked(custom.isSilent_One());
                silent_ckbx_two.setChecked(custom.isSilent_Two());
                silent_ckbx_three.setChecked(custom.isSilent_Three());
                pattern_ckbx_four.setChecked(custom.isNoPattern());
                ckbx_five.setChecked(custom.isNoNotification());

                silent_ckbx_one.setTypeface(tf_pala);
                silent_ckbx_two.setTypeface(tf_pala);
                silent_ckbx_three.setTypeface(tf_pala);
                pattern_ckbx_four.setTypeface(tf_pala);
                ckbx_five.setTypeface(tf_pala);
                edt_adv_name.setTypeface(tf_pala);
                edt_adv_desc.setTypeface(tf_pala);
                btn_add_adv.setTypeface(tf_pala);
                tv_name_adv.setTypeface(tf_pala);
                edt_adv_need_to_pay.setTypeface(tf_pala);


                //System.out.println("%%%%%%%%%" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/"+custom.getGroup_image_message_uri());

                if (!custom.getContext_image_message_uri().equals("#Nothing#") && Uri.parse(custom.getContext_image_message_uri()) != null)
                    cust_img_cntxt.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getContext_image_message_uri() + ".jpg"), 30, 30));

                if (!custom.getGroup_image_message_uri().equals("#Nothing#") && Uri.parse(custom.getGroup_image_message_uri()) != null) {
                    cust_img_grp.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getGroup_image_message_uri() + ".jpg"), 30, 30));
                }

                if (!custom.getUci_image_message_uri().equals("#Nothing#") && Uri.parse(custom.getUci_image_message_uri()) != null)
                    cust_img_uci.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getUci_image_message_uri() + ".jpg"), 30, 30));

                if (!custom.getSearch_image_message_uri().equals("#Nothing#") && custom.getSearch_image_message_uri() != null)
                    cust_img_src.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getSearch_image_message_uri()), 30, 30));
                if (!custom.getLang_image_message_uri().equals("#Nothing#") && Uri.parse(custom.getLang_image_message_uri()) != null)
                    cust_img_q.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getLang_image_message_uri() + ".jpg"), 30, 30));

                if (!custom.getHelp_image_message_uri().equals("#Nothing#") && Uri.parse(custom.getHelp_image_message_uri()) != null)
                    cust_img_hlp.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getHelp_image_message_uri() + ".jpg"), 30, 30));

                if (!custom.getAbt_image_message_uri().equals("#Nothing#") && Uri.parse(custom.getAbt_image_message_uri()) != null)
                    cust_img_abt.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getAbt_image_message_uri() + ".jpg"), 30, 30));


                if (custom.getLanguage() == 1) {

                    String[] settings = getResources().getStringArray(R.array.settings_sv);
                    tv_1.setText(settings[9]);
                    silent_ckbx_one.setText(getResources().getString(R.string.string_more_extra_0_sv));
                    silent_ckbx_two.setText(getResources().getString(R.string.string_more_extra_1_sv));
                    silent_ckbx_three.setText(getResources().getString(R.string.string_more_extra_2_sv));
                    pattern_ckbx_four.setText(getResources().getString(R.string.string_more_extra_3_sv));
                    ckbx_five.setText(getResources().getString(R.string.string_more_extra_4_sv));
                    btn.setText(getResources().getString(R.string.exit_sv));
                    //  edt_adv_need_to_pay.setText(getResources().getString(R.string.extra_settings_sv));
                } else if (custom.getLanguage() == 2) {

                    String[] settings = getResources().getStringArray(R.array.settings_sp);
                    tv_1.setText(settings[9]);
                    silent_ckbx_one.setText(getResources().getString(R.string.string_more_extra_0_sp));
                    silent_ckbx_two.setText(getResources().getString(R.string.string_more_extra_1_sp));
                    silent_ckbx_three.setText(getResources().getString(R.string.string_more_extra_2_sp));
                    pattern_ckbx_four.setText(getResources().getString(R.string.string_more_extra_3_sp));
                    ckbx_five.setText(getResources().getString(R.string.string_more_extra_4_sp));
                    btn.setText(getResources().getString(R.string.exit_sp));
                    //  edt_adv_need_to_pay.setText(getResources().getString(R.string.extra_settings_sv));
                } else if (custom.getLanguage() == 3) {

                    String[] settings = getResources().getStringArray(R.array.settings_pr);
                    tv_1.setText(settings[9]);
                    silent_ckbx_one.setText(getResources().getString(R.string.string_more_extra_0_pr));
                    silent_ckbx_two.setText(getResources().getString(R.string.string_more_extra_1_pr));
                    silent_ckbx_three.setText(getResources().getString(R.string.string_more_extra_2_pr));
                    pattern_ckbx_four.setText(getResources().getString(R.string.string_more_extra_3_pr));
                    ckbx_five.setText(getResources().getString(R.string.string_more_extra_4_pr));
                    btn.setText(getResources().getString(R.string.exit_pr));
                    //  edt_adv_need_to_pay.setText(getResources().getString(R.string.extra_settings_sv));
                } else if (custom.getLanguage() == 4) {

                    String[] settings = getResources().getStringArray(R.array.settings_fr);
                    tv_1.setText(settings[9]);
                    silent_ckbx_one.setText(getResources().getString(R.string.string_more_extra_0_fr));
                    silent_ckbx_two.setText(getResources().getString(R.string.string_more_extra_1_fr));
                    silent_ckbx_three.setText(getResources().getString(R.string.string_more_extra_2_fr));
                    pattern_ckbx_four.setText(getResources().getString(R.string.string_more_extra_3_fr));
                    ckbx_five.setText(getResources().getString(R.string.string_more_extra_4_fr));
                    btn.setText(getResources().getString(R.string.exit_fr));
                    //  edt_adv_need_to_pay.setText(getResources().getString(R.string.extra_settings_sv));
                } else {

                    String[] settings = getResources().getStringArray(R.array.settings_en);
                    tv_1.setText(settings[9]);
                    silent_ckbx_one.setText(getResources().getString(R.string.string_more_extra_0));
                    silent_ckbx_two.setText(getResources().getString(R.string.string_more_extra_1));
                    silent_ckbx_three.setText(getResources().getString(R.string.string_more_extra_2));
                    pattern_ckbx_four.setText(getResources().getString(R.string.string_more_extra_3));
                    ckbx_five.setText(getResources().getString(R.string.string_more_extra_4));
                    btn.setText("Exit");
                    //  edt_adv_need_to_pay.setText(getResources().getString(R.string.extra_settings_sv));
                }


                silent_ckbx_one.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (silent_ckbx_one.isChecked()) {
                            {
                                custom.setSilent_One(true);
                                ed.putBoolean("SILENT_ONE", true);
                            }
                        } else {
                            custom.setSilent_One(false);
                            ed.putBoolean("SILENT_ONE", false);
                        }
                        ed.commit();
                    }
                });
                silent_ckbx_two.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (silent_ckbx_two.isChecked()) {
                            custom.setSilent_Two(true);
                            ed.putBoolean("SILENT_TWO", true);
                        } else {
                            custom.setSilent_Two(false);
                            ed.putBoolean("SILENT_TWO", false);
                        }


                        ed.commit();
                    }

                });
                silent_ckbx_three.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (silent_ckbx_three.isChecked()) {
                            custom.setSilent_Three(true);
                            ed.putBoolean("SILENT_THREE", true);
                        } else {

                            custom.setSilent_Three(false);
                            ed.putBoolean("SILENT_THREE", false);
                        }
                        ed.commit();
                    }
                });
                pattern_ckbx_four.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (pattern_ckbx_four.isChecked()) {
                            custom.setNoPattern(true);
                            ed.putBoolean("NO_PATTERN", true);
                        } else {
                            custom.setNoPattern(false);
                            ed.putBoolean("NO_PATTERN", false);
                        }
                        ed.commit();
                    }
                });
                ckbx_five.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ckbx_five.isChecked()) {
                            custom.setNoNotification(true);
                            ed.putBoolean("NO_NOTIFICATION", true);
                        } else {
                            custom.setNoNotification(false);
                            ed.putBoolean("NO_NOTIFICATION", false);
                        }
                        ed.commit();
                    }
                });
                btn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ErrorActivity.this, MoreActivity.class);
                        startActivity(intent);
                    }
                });
                btn_add_adv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!edt_adv_name.getText().toString().trim().isEmpty() && !edt_adv_desc.getText().toString().trim().isEmpty()) { // Save Advertisements ---
                            PublicChats _thread_0 = new PublicChats(getBaseContext(), MainActivity.SELECTED_GROUP, "advs");
                            ArrayList<String> temp_ar = _thread_0.getmPublicChat("");
                            temp_ar.add(edt_adv_name.getText() + ":" + edt_adv_desc.getText());
                            _thread_0.saveADVs(MainActivity.SELECTED_GROUP, "advs", temp_ar);
                            edt_adv_name.setText("");
                            edt_adv_desc.setText("");
                            displayCustomizedToast(ErrorActivity.this, "Advertisements Added.");
                        } else {
                            displayCustomizedToast(ErrorActivity.this, "Invalid Input. Empty not allowed");
                        }
                    }
                });

                btn_reset.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (!custom.getContext_image_message_uri().equals("#Nothing#") && Uri.parse(custom.getContext_image_message_uri()) != null) {
                            File file = new File("Environment.getExternalStorageDirectory().getAbsolutePath() + \"/Chitchato/\" + \"Media/\"+custom.getContext_image_message_uri()+\".jpg\"");
                            file.delete();
                            cust_img_cntxt.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getContext_image_message_uri() + ".jpg"), 30, 30));
                        }

                        if (!custom.getGroup_image_message_uri().equals("#Nothing#") && Uri.parse(custom.getGroup_image_message_uri()) != null) {
                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getGroup_image_message_uri() + ".jpg");
                            file.delete();
                            cust_img_grp.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getGroup_image_message_uri() + ".jpg"), 30, 30));

                        }
                        if (!custom.getUci_image_message_uri().equals("#Nothing#") && Uri.parse(custom.getUci_image_message_uri()) != null) {

                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getUci_image_message_uri() + ".jpg");
                            file.delete();

                            cust_img_uci.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getUci_image_message_uri() + ".jpg"), 30, 30));

                        }

                        if (!custom.getSearch_image_message_uri().equals("#Nothing#") && custom.getSearch_image_message_uri() != null) {

                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getSearch_image_message_uri() + ".jpg");
                            file.delete();
                            cust_img_src.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getSearch_image_message_uri() + ".jpg"), 30, 30));
                        }
                        if (!custom.getLang_image_message_uri().equals("#Nothing#") && Uri.parse(custom.getLang_image_message_uri()) != null) {

                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getLang_image_message_uri() + ".jpg");
                            file.delete();
                            cust_img_q.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getLang_image_message_uri() + ".jpg"), 30, 30));

                        }

                        if (!custom.getHelp_image_message_uri().equals("#Nothing#") && Uri.parse(custom.getHelp_image_message_uri()) != null) {

                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getHelp_image_message_uri() + ".jpg");
                            file.delete();
                            cust_img_hlp.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getHelp_image_message_uri() + ".jpg"), 30, 30));
                        }

                        if (!custom.getAbt_image_message_uri().equals("#Nothing#") && Uri.parse(custom.getAbt_image_message_uri()) != null) {

                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getAbt_image_message_uri() + ".jpg");
                            file.delete();
                            cust_img_abt.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + custom.getAbt_image_message_uri() + ".jpg"), 30, 30));

                        }


                        ed.putString("CON-URI-IMAGE", "#Nothing#");
                        ed.putString("GRP-URI-IMAGE", "#Nothing#");
                        ed.putString("UCI-URI-IMAGE", "#Nothing#");
                        ed.putString("HELP-URI-IMAGE", "#Nothing#");
                        ed.putString("SRC-URI-IMAGE", "#Nothing#");
                        ed.putString("ABT-URI-IMAGE", "#Nothing#");
                        ed.putString("LNG-URI-IMAGE", "#Nothing#");
                        ed.commit();


                        custom.setContext_image_message_uri("#Nothing#");
                        custom.setGroup_image_message_uri("#Nothing#");
                        custom.setUci_image_message_uri("#Nothing#");
                        custom.setSearch_image_message_uri("#Nothing#");
                        custom.setHelp_image_message_uri("#Nothing#");
                        custom.setAbt_image_message_uri("#Nothing#");
                        custom.setLang_image_message_uri("#Nothing#");


                        displayCustomizedToast(ErrorActivity.this, "Successfully reset to default.");
                    }
                });


                cust_img_cntxt.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.putExtra("ACTIVITY", "MainActivity");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select Picture"),
                                SELECT_IMAGE_1);

                    }
                });
                cust_img_grp.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.putExtra("ACTIVITY", "MainActivity");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select Picture"),
                                SELECT_IMAGE_2);

                    }
                });

                cust_img_uci.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.putExtra("ACTIVITY", "MainActivity");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select Picture"),
                                SELECT_IMAGE_3);

                    }
                });

                cust_img_src.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.putExtra("ACTIVITY", "MainActivity");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select Picture"),
                                SELECT_IMAGE_4);

                    }
                });

                cust_img_hlp.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.putExtra("ACTIVITY", "MainActivity");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select Picture"),
                                SELECT_IMAGE_5);

                    }
                });

                cust_img_abt.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.putExtra("ACTIVITY", "MainActivity");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select Picture"),
                                SELECT_IMAGE_6);

                    }
                });

                cust_img_q.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.putExtra("ACTIVITY", "MainActivity");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select Picture"),
                                SELECT_IMAGE_7);

                    }
                });


            } else {
                ll.setVisibility(View.GONE);
                edt.setText("Oops...! error encountered!\n " + error);
            }
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Intent intent = new Intent(ErrorActivity.this, activity_name.getClass());
                Intent intent = new Intent(ErrorActivity.this, ChoicesActivity.class);
                startActivity(intent);

            }
        });

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

                intent = new Intent(ErrorActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_advanced:

                intent = new Intent(ErrorActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(ErrorActivity.this, SensorReadingsActivity.class);
                startActivity(intent);
                return true;


			/*
        case R.id.action_create:

			intent = new Intent(ErrorActivity.this, ErrorActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_settings:

			intent = new Intent(ErrorActivity.this, PurgeActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_search:

			intent = new Intent(ErrorActivity.this, SearchGroupActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_language:

			intent = new Intent(ErrorActivity.this, LanguageActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_register:

			intent = new Intent(ErrorActivity.this, RegisterActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_help:

			intent = new Intent(ErrorActivity.this, HelpActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_about:

			intent = new Intent(ErrorActivity.this, AboutActivity.class);
			startActivity(intent);
			return true;*/

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Create the platform itself
        // if (sensibleThingsPlatform == null) {
        // sensibleThingsPlatform = new SensibleThingsPlatform(this);
        // }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (error.equalsIgnoreCase("OTHER")) {
            SharedPreferences mPrefs_ = getSharedPreferences("myprofile", 0);
            SharedPreferences.Editor ed = mPrefs_.edit();
            ed = mPrefs_.edit();
            ed.putBoolean("LA_VISITED", true);
            ed.putBoolean("SILENT_ONE", custom.isSilent_One());
            ed.putBoolean("SILENT_TWO", custom.isSilent_Two());
            ed.putBoolean("SILENT_THREE", custom.isSilent_Three());
            ed.putBoolean("NO_PATTERN", custom.isNoPattern());
            ed.putBoolean("NO_NOTIFICATION", custom.isNoNotification());
            ed.commit();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            mPrefs = getSharedPreferences("myprofile", 0);
            SharedPreferences.Editor ed = mPrefs.edit();

            if (requestCode == SELECT_IMAGE_1) {
                if (data == null) {
                } else {
                    selectedImageUri = data.getData();
                    // OI FILE Manager
                    filemanagerstring = selectedImageUri.getPath();
                    // MEDIA GALLERY
                    selectedImagePath = getPath(selectedImageUri);
                    cust_img_cntxt.setImageURI(selectedImageUri);
                    ed.putString("CON-URI-IMAGE", "con_default___miun_se__mobi");
                    custom.setContext_image_message_uri("con_default___miun_se__mobi");

                    ArrayList<byte[]> bt = getImage(selectedImagePath);
                    saveImage(bt, "con_default___miun_se__mobi", "context_name_str");

                }
            } else if (requestCode == SELECT_IMAGE_2) {
                if (data == null) {
                } else {
                    selectedImageUri = data.getData();
                    // OI FILE Manager
                    filemanagerstring = selectedImageUri.getPath();
                    // MEDIA GALLERY
                    selectedImagePath = getPath(selectedImageUri);

                    cust_img_grp.setImageURI(selectedImageUri);
                    ed.putString("GRP-URI-IMAGE", "grp_default___miun_se__mobi");
                    custom.setGroup_image_message_uri("grp_default___miun_se__mobi");

                    ArrayList<byte[]> bt = getImage(selectedImagePath);
                    saveImage(bt, "grp_default___miun_se__mobi", "group_name_str");

                }
            } else if (requestCode == SELECT_IMAGE_3) {
                if (data == null) {
                } else {
                    selectedImageUri = data.getData();
                    // OI FILE Manager
                    filemanagerstring = selectedImageUri.getPath();
                    // MEDIA GALLERY
                    selectedImagePath = getPath(selectedImageUri);
                    cust_img_uci.setImageURI(selectedImageUri);
                    ed.putString("UCI-URI-IMAGE", "uci_default___miun_se__mobi");
                    custom.setUci_image_message_uri("uci_default___miun_se__mobi");

                    ArrayList<byte[]> bt = getImage(selectedImagePath);
                    saveImage(bt, "uci_default___miun_se__mobi", "group_name_str");

                }
            } else if (requestCode == SELECT_IMAGE_4) {
                if (data == null) {
                } else {
                    selectedImageUri = data.getData();
                    // OI FILE Manager
                    filemanagerstring = selectedImageUri.getPath();
                    // MEDIA GALLERY
                    selectedImagePath = getPath(selectedImageUri);

                    cust_img_src.setImageURI(selectedImageUri);
                    ed.putString("SRC-URI-IMAGE", "src_default___miun_se__mobi");
                    custom.setSearch_image_message_uri("src_default___miun_se__mobi");

                    ArrayList<byte[]> bt = getImage(selectedImagePath);
                    saveImage(bt, "src_default___miun_se__mobi", "group_name_str");

                }
            } else if (requestCode == SELECT_IMAGE_5) {
                if (data == null) {
                } else {
                    selectedImageUri = data.getData();
                    // OI FILE Manager
                    filemanagerstring = selectedImageUri.getPath();
                    // MEDIA GALLERY
                    selectedImagePath = getPath(selectedImageUri);

                    cust_img_hlp.setImageURI(selectedImageUri);
                    ed.putString("HELP-URI-IMAGE", "help_default___miun_se__mobi");
                    custom.setHelp_image_message_uri("help_default___miun_se__mobi");

                    ArrayList<byte[]> bt = getImage(selectedImagePath);
                    saveImage(bt, "help_default___miun_se__mobi", "group_name_str");

                }
            } else if (requestCode == SELECT_IMAGE_6) {
                if (data == null) {
                } else {
                    selectedImageUri = data.getData();
                    // OI FILE Manager
                    filemanagerstring = selectedImageUri.getPath();
                    // MEDIA GALLERY
                    selectedImagePath = getPath(selectedImageUri);

                    cust_img_hlp.setImageURI(selectedImageUri);
                    ed.putString("ABT-URI-IMAGE", "abt_default___miun_se__mobi");
                    custom.setAbt_image_message_uri("abt_default___miun_se__mobi");

                    ArrayList<byte[]> bt = getImage(selectedImagePath);
                    saveImage(bt, "abt_default___miun_se__mobi", "group_name_str");

                }
            } else if (requestCode == SELECT_IMAGE_7) {
                if (data == null) {
                } else {
                    selectedImageUri = data.getData();
                    // OI FILE Manager
                    filemanagerstring = selectedImageUri.getPath();
                    // MEDIA GALLERY
                    selectedImagePath = getPath(selectedImageUri);
                    // Toast.makeText(this.getApplicationContext(), selectedImageUri.toString(),
                    //       Toast.LENGTH_SHORT).show();

                    cust_img_q.setImageURI(selectedImageUri);
                    ed.putString("LNG-URI-IMAGE", "lang_default___miun_se__mobi");
                    custom.setAbt_image_message_uri("lang_default___miun_se__mobi");

                    ArrayList<byte[]> bt = getImage(selectedImagePath);
                    saveImage(bt, "lang_default___miun_se__mobi", "group_name_str");

                }
            } else if (requestCode == SELECT_IMAGE_8) {
                if (data == null) {
                } else {
                    selectedImageUri = data.getData();
                    // OI FILE Manager
                    filemanagerstring = selectedImageUri.getPath();
                    // MEDIA GALLERY
                    selectedImagePath = getPath(selectedImageUri);

                    cust_img_q.setImageURI(selectedImageUri);
                    ArrayList<byte[]> bt = getImage(selectedImagePath);
                    saveImage(bt, "quit_default___miun.se__mobi", "group_name_str");

                }
            }
            ed.commit();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error! Upload failed", Toast.LENGTH_LONG).show();
        }

    }

    public void displayCustomizedToast(final Context _context_, String message) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view_ = inflater.inflate(R.layout.dialog_one, null);
        TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);
        tv_0_0_.setText(message);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = new Toast(_context_);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view_);
                toast.show();

            }
        });

    }

    public void saveImage(ArrayList<byte[]> array_byte, String image_name, String group_name) {
        // String destName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + image_name;
        String destName_dp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/" + image_name;
        //  File file = new File(destName);
        File file_dp = new File(destName_dp);

        if (!file_dp.exists()) {
            setAppFolderFiles("Media");
        }


        //  destName = destName + ".jpg";
        destName_dp = destName_dp + ".jpg";
        //  file = new File(destName);
        file_dp = new File(destName_dp);
        // file.setWritable(true);
        file_dp.setWritable(true);
        FileOutputStream fout_dp = null;
        //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] image_file;
        byte[] readData = new byte[1024];
        try {
            // fout = new FileOutputStream(file);
            fout_dp = new FileOutputStream(file_dp);
            uri_str = destName_dp;
            for (int indexx = 0; indexx < array_byte.size(); indexx++) {
                byte[] bb = array_byte.get(indexx);
                if (indexx == 0) {
                    // fout.write(bb);
                    fout_dp.write(bb);
                } else if (indexx < array_byte.size() - 1) {

                    //fout.write(bb, indexx * 1024, 1024);
                    fout_dp.write(bb, indexx * 1024, 1024);

                } else if (indexx == array_byte.size() - 1) {
                    //  fout.write(bb, indexx * 1024, (bb.length - (indexx) * 1024));
                    fout_dp.write(bb, indexx * 1024, (bb.length - (indexx) * 1024));

                } else {
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {

                //fout.flush();
                fout_dp.flush();
                //fout.close();
                fout_dp.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


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

    public void setAppFolderFiles(String file_name) {
        String path = getAppFolder("");
        File file = new File(path + file_name + "/");
        if (!file.exists()) {
            new File(path + file_name + "/").mkdir();
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
        System.out.println("image_url" + image_url);
        for (i = 0; i < image_url.length(); i++) {

            if ((image_url.charAt(i) == '.' && i == image_url.lastIndexOf('.')) || flag == 1) {
                flag = 1;
                extn += image_url.charAt(i);
            }
        }
        System.out.println("image_url:" + image_url + "extn:" + extn);

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


}
