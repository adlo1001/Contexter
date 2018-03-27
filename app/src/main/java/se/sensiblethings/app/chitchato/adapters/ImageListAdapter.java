package se.sensiblethings.app.chitchato.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.activities.EditProfileActivity;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerBootstrap;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerNode;
import se.sensiblethings.app.chitchato.extras.Constants;
import se.sensiblethings.app.chitchato.extras.CustomImageView;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.kernel.PublicChats;


public class ImageListAdapter extends BaseAdapter {

    // Date Parameters
    static String date;
    String date_pattern = "yyyy-MM-dd HH:mm:ss";
    Format format = new SimpleDateFormat(date_pattern);
    String local_ip_ = "#Nothing#";
    Animation animation = null;
    private Context context_;
    private int language_ = 0;
    private boolean CHAT_MODE = true;
    private int adapaterSerial = 0;
    private int index = 0;
    private int old_index = -1;
    private boolean isPlatformRecieving = false;
    private ArrayList<String> array_list = null;
    private String context_message;
    private boolean setProfilePictureGone = false;
    private boolean MA_VISITED = false;
    private boolean CGA_VISITED = false;
    private boolean RA_VISITED = false;
    private boolean LA_VISITED = false;
    private boolean HA_VISITED = false;
    private boolean About_VISITED = false;
    private boolean SGA_VISITED = false;
    private Calendar cal;
    private String update_time = null;
    private ImageView profile_image, image_msg = null;
    private Uri Profile_Image_URI = null;
    private Uri Profile_Image_URI_Other = null;
    private Uri _image_message_uri = null;
    private boolean _image_message_ready = false;
    private Customizations custom;
    private Integer[] mIcons = {R.drawable.profile_image,
            R.drawable.profile_image, R.drawable.profile_image,
            R.drawable.profile_image, R.drawable.profile_image,
            R.drawable.profile_image, R.drawable.profile_image,
            R.drawable.profile_image
    };
    private ArrayList<ImageView> PICS;

    public ImageListAdapter(Context c, boolean str, boolean chat_mode,
                            int adapter_no) {
        this.context_ = c;
        // language_ = str;
        this.CHAT_MODE = chat_mode;
        this.adapaterSerial = adapter_no;
        cal = new GregorianCalendar();
        custom = new Customizations(context_, -1);
    }

    public ImageListAdapter(Context c, boolean str, boolean chat_mode,
                            int adapter_no, ArrayList<String> al_) {
        this.context_ = c;
        this.CHAT_MODE = chat_mode;
        this.adapaterSerial = adapter_no;
        this.array_list = al_;

        custom = new Customizations(context_, -1);

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

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (array_list == null || array_list.isEmpty())
            return mIcons.length;
        else
            return array_list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ImageView imageview = null;
        CustomImageView cust_imageview = null;
        TextView textview = null, textview_subitem = null, textview_context_subitem = null, textview_time = null, textview_chatter = null, textview_concentric_circle__ = null, tv_run = null, tv_thermo = null, tv_loud = null, tv_location = null;
        Button button_concentric_circle;
        ImageView iv_run, iv_thermo, iv_loud, iv_location;
        LayoutInflater li = (LayoutInflater) context_
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout llo, llo_, llo_s_item, llo_s_item_;
        ScrollView llo_s_message_holder;
        CheckBox check_box;


        // Message box orientation varies depending on the owner of  the message
        if (array_list == null) {
            view = li.inflate(R.layout.chatscreengriditem_reverse_180_yellow, null);
            view.setTag("Y");
        } else if (array_list.size() > 0) {
            if (array_list.get(position).contains("~::~" + custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~") && array_list.get(position).startsWith("100")
                    || array_list.get(position).contains("~::~" + custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~") && array_list.get(position).startsWith("102")) {
                //view = li.inflate(R.layout.chatscreengriditem_reverse_180_yellow, null);
                String tmppp = array_list.get(position);
                String[] tmp_00 = tmppp.split("~::~");

                //Texting
                if (tmp_00.length < 8 && array_list.get(position).startsWith("100")) {
                    {
                        if (tmp_00[4].toString().endsWith("failed")) {
                            view = li.inflate(R.layout.chatscreengriditem_reverse_180, null);
                            view.setTag("F");
                        } else {
                            view = li.inflate(R.layout.chatscreengriditem_reverse_180_yellow, null);
                            view.setTag("Y");
                        }
                    }
                }
                // Contexting
                else if (tmp_00.length > 8 && array_list.get(position).startsWith("100")) {
                    if (tmp_00[8].toString().endsWith("failed")) {
                        view = li.inflate(R.layout.chatscreengriditem_reverse_180, null);
                        view.setTag("F");
                    } else {
                        view = li.inflate(R.layout.chatscreengriditem_reverse_180_yellow, null);
                        view.setTag("Y");
                    }

                } else {
                    view = li.inflate(R.layout.chatscreengriditem_reverse_180, null);
                    view.setTag("Y");
                }


            }
            //Private Message format
            else if (array_list.get(position).contains("~::~" + custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~") && array_list.get(position).startsWith("101")
                    || array_list.get(position).contains("~::~" + custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~") && array_list.get(position).startsWith("103")) {
                String[] tmp_00 = array_list.get(position).split("~::~");
                //Texting
                if (tmp_00.length > 5 && array_list.get(position).startsWith("101")) {
                    if (tmp_00[5].toString().endsWith("failed")) {
                        view = li.inflate(R.layout.chatscreengriditem_reverse_180, null);
                        view.setTag("F");
                    } else {
                        if (tmp_00[2].equalsIgnoreCase(custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain)) {
                            view = li.inflate(R.layout.chatscreengriditem_reverse_180_yellow, null);
                            view.setTag("Y");
                        } else {
                            view = li.inflate(R.layout.chatscreengriditem_green, null);
                            view.setTag("G");
                        }
                    }
                }
                // Contexting
                else if (tmp_00.length > 9 && array_list.get(position).startsWith("103")) {
                    if (tmp_00[9].toString().endsWith("failed")) {
                        view = li.inflate(R.layout.chatscreengriditem_reverse_180, null);
                        view.setTag("F");
                    } else {
                        view = li.inflate(R.layout.chatscreengriditem_reverse_180_yellow, null);
                        view.setTag("Y");
                    }
                } else {
                    view = li.inflate(R.layout.chatscreengriditem_reverse_180_yellow, null);
                    view.setTag("Y");
                }

            } else if (!array_list.get(position).contains("~::~")) {
                view = li.inflate(R.layout.chatscreengriditem_reverse_180_yellow, null);
                view.setTag("Y");
            } else {
                view = li.inflate(R.layout.chatscreengriditem_green, null);
                view.setTag("G");
            }

        } else {
            view = li.inflate(R.layout.chatscreengriditem_reverse_180_yellow, null);
            view.setTag("Y");


        }

        llo = (LinearLayout) view.findViewById(R.id.imageView_lo_);
        llo_ = (LinearLayout) view
                .findViewById(R.id.linear_grditem_4);
        llo_s_item = (LinearLayout) view
                .findViewById(R.id.imageView_llo_sub_item);
        llo_s_item_ = (LinearLayout) view
                .findViewById(R.id.imageView_llo_);
        llo_s_message_holder = (ScrollView) view.findViewById(R.id.imageView_svo_);

        //
        textview = (TextView) view.findViewById(R.id.editText_grditem_3);
        button_concentric_circle = (Button) view
                .findViewById(R.id.choices_btn_concentric);
        image_msg = (ImageView) view
                .findViewById(R.id.image_tosend_chat_screen);
        image_msg.setTag("contexter");
        iv_run = (ImageView) view.findViewById(R.id.imageView_run);
        iv_thermo = (ImageView) view.findViewById(R.id.imageView_thermo);
        iv_loud = (ImageView) view.findViewById(R.id.imageView_loud);
        iv_location = (ImageView) view.findViewById(R.id.imageView_location);

        tv_run = (TextView) view.findViewById(R.id.tv_run);
        tv_thermo = (TextView) view.findViewById(R.id.tv_thermo);
        tv_loud = (TextView) view.findViewById(R.id.tv_loud);
        tv_location = (TextView) view.findViewById(R.id.tv_location);
        GridView gv_adv = (GridView) view.findViewById(R.id.gv_adv_list);

        Typeface tf = Typeface.createFromAsset(this.context_.getAssets(), "fonts/pala.ttf");
        llo_s_item.setVisibility(View.GONE);
        llo.setVisibility(View.GONE);

        //
        String[] array = null;
        String[] array_ = null;


        try {
            if (this.adapaterSerial == 0) {
                animation = AnimationUtils.loadAnimation(context_,
                        R.anim.move_tv_to_right_side);

                imageview = (ImageView) view
                        .findViewById(R.id.imageView_grditem_1);
                cust_imageview = (CustomImageView) view.findViewById(R.id.cust_imageView_grditem_1);
                cust_imageview.setTag("profile_image_android");

                image_msg = (ImageView) view
                        .findViewById(R.id.image_tosend_chat_screen);
                image_msg.setTag("contexter");
                textview_time = (TextView) view
                        .findViewById(R.id.tv_chat_screen_context_chat_time);
                textview_chatter = (TextView) view
                        .findViewById(R.id.tv_chat_screen_context_chatter);

                if (array_list.isEmpty() || array_list == null) {
                    textview.setText(" ~Welcome~ ! \n You may start sending messages!");
                    if (PlatformManagerNode.ST_PLATFORM_IS_UP)
                        textview_time.setText("Platform up");
                    else
                        textview_time.setText("Platform down.");
                    textview.setMaxLines(10);
                    imageview.setVisibility(View.GONE);
                    cust_imageview.setVisibility(View.GONE);
                    llo.setVisibility(View.GONE);
                    llo_.setVisibility(View.VISIBLE);
                    llo_s_item.setVisibility(View.GONE);
                    iv_run.setVisibility(View.GONE);
                    iv_thermo.setVisibility(View.GONE);
                    iv_loud.setVisibility(View.GONE);
                    iv_location.setVisibility(View.GONE);

                    tv_run.setVisibility(View.GONE);
                    tv_thermo.setVisibility(View.GONE);
                    tv_loud.setVisibility(View.GONE);
                    tv_location.setVisibility(View.GONE);
                    image_msg.setVisibility(View.GONE);
                    if (position > 0) {
                        textview.setVisibility(View.GONE);
                        llo_.setVisibility(View.GONE);
                        llo_s_item.setVisibility(View.GONE);
                    }

                } else {
                    llo.setVisibility(View.VISIBLE);
                    if (CHAT_MODE)
                        llo_s_item.setVisibility(View.GONE);
                    else
                        llo_s_item.setVisibility(View.VISIBLE);
                    String temp = array_list.get(position);
                    if (temp.contains("failed~::~")) {
                        temp = temp.replace("failed", "");
                    }

                    if (temp.contains("~::~")) {
                        String[] split_context = temp.split("~::~");
                        final String[] split = temp.split("~::~");
                        textview_chatter.setText(split[2]);
                        if (split.length == 7 || split.length == 8) {
                            gv_adv.setVisibility(View.GONE);
                            if (Integer.parseInt(split[0]) == Constants.PUBLIC) {
                                textview_time.append("        " + split[4]);
                                textview.setText(split[3]);

                                if (split[2] != null) {
                                    if (new File(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg").exists()) {
                                        if (new File(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg").length() > 0) {
                                            setProfileImage_URI(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg");
                                            String _uri_00 = EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg";
                                            File _file_00 = new File(_uri_00);
                                            if (_file_00.exists()) {
                                                FileInputStream _fis_00 = new FileInputStream(_file_00);
                                                if (!isJPEGValid(_fis_00))
                                                    cust_imageview.setTag("profile_image_android");
                                            } else
                                                cust_imageview.setTag("profile_image_android");
                                            // textview.append("\n\n" + cust_imageview.getTag().toString());
                                        }

                                    }

                                    if (getProfileImage_URI(split[2].toString().replace(":", "")) != null) {
                                        if (custom.getProfile_image_uri().equals("#Nothing#") && (custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain).equalsIgnoreCase(split[2].toString())) {
                                            cust_imageview.setImageURI(Uri.parse("#Nothing#"));
                                            cust_imageview.setTag("profile_image_android");

                                            ;
                                        } else {

                                            cust_imageview.setImageBitmap(decodeSampledBitmapFromResource(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0), 55, 55));
                                            cust_imageview.setTag(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0));

                                            String _uri_00 = getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0).toString();
                                            File _file_00 = new File(_uri_00);

                                            if (_file_00.exists()) {
                                                FileInputStream _fis_00 = new FileInputStream(_file_00);

                                                if (!isJPEGValid(_fis_00))
                                                    cust_imageview.setTag("profile_image_android");
                                            } else
                                                cust_imageview.setTag("profile_image_android");

                                            // textview.append("\n\n" + cust_imageview.getTag().toString());

                                        }
                                    }
                                } else {
                                    textview_chatter.setText("#Unknown#");
                                }

                                imageview.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {

                                        LayoutInflater inflater = (LayoutInflater) context_
                                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View view_ = inflater.inflate(R.layout.zoomprofileimage, null);
                                        TextView tv_0_0_ = (TextView) view_.findViewById(R.id.tv_chat_screen_context_chatter);
                                        ImageView img_0_0 = (ImageView) view_.findViewById(R.id.imageView_grditem_1);
                                        img_0_0.setImageURI(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0));
                                        tv_0_0_.setText(split[2]);
                                        Toast toast = new Toast(context_);
                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                                        toast.setView(view_);
                                        toast.show();
                                        return false;
                                    }
                                });
                                if (!cust_imageview.getTag().equals("profile_image_android"))
                                    cust_imageview.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {

                                            LayoutInflater inflater = (LayoutInflater) context_
                                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            View view_ = inflater.inflate(R.layout.zoomprofileimage, null);
                                            TextView tv_0_0_ = (TextView) view_.findViewById(R.id.tv_chat_screen_context_chatter);
                                            ImageView img_0_0 = (ImageView) view_.findViewById(R.id.imageView_grditem_1);
                                            img_0_0.setImageURI(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0));
                                            tv_0_0_.setText(split[2]);
                                            Toast toast = new Toast(context_);
                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                                            toast.setView(view_);
                                            toast.show();
                                            return false;
                                        }
                                    });


                            } else if (Integer.parseInt(split[0]) == Constants.PRIVATE) {
                                textview_time.setText(split[5]);
                                textview.setText(split[4]);

                                if (split[2] != null) {
                                    if (new File(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg").exists()) {
                                        if (new File(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg").length() > 0) {
                                            setProfileImage_URI(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg");
                                            String _uri_00 = EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg";
                                            File _file_00 = new File(_uri_00);
                                            if (_file_00.exists()) {
                                                FileInputStream _fis_00 = new FileInputStream(_file_00);
                                                if (!isJPEGValid(_fis_00))
                                                    cust_imageview.setTag("profile_image_android");
                                            } else
                                                cust_imageview.setTag("profile_image_android");
                                            // textview.append("\n\n" + cust_imageview.getTag().toString());
                                        }

                                    }

                                    if (getProfileImage_URI(split[2].toString().replace(":", "")) != null) {
                                        if (custom.getProfile_image_uri().equals("#Nothing#") && (custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain).equalsIgnoreCase(split[2].toString())) {
                                            cust_imageview.setImageURI(Uri.parse("#Nothing#"));
                                            cust_imageview.setTag("profile_image_android");
                                            // textview.append("\n\n" + cust_imageview.getTag().toString());
                                            ;
                                        } else {


                                            cust_imageview.setImageBitmap(decodeSampledBitmapFromResource(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0), 55, 55));
                                            cust_imageview.setTag(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0));

                                            String _uri_00 = getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0).toString();
                                            File _file_00 = new File(_uri_00);

                                            if (_file_00.exists()) {
                                                FileInputStream _fis_00 = new FileInputStream(_file_00);

                                                if (!isJPEGValid(_fis_00))
                                                    cust_imageview.setTag("profile_image_android");
                                            } else
                                                cust_imageview.setTag("profile_image_android");

                                            // textview.append("\n\n" + cust_imageview.getTag().toString());

                                        }
                                    }
                                } else {
                                    textview_chatter.setText("#Unknown#");
                                }

                                imageview.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {

                                        LayoutInflater inflater = (LayoutInflater) context_
                                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View view_ = inflater.inflate(R.layout.zoomprofileimage, null);
                                        TextView tv_0_0_ = (TextView) view_.findViewById(R.id.tv_chat_screen_context_chatter);
                                        ImageView img_0_0 = (ImageView) view_.findViewById(R.id.imageView_grditem_1);
                                        img_0_0.setImageURI(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0));
                                        tv_0_0_.setText(split[2]);
                                        Toast toast = new Toast(context_);
                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                                        toast.setView(view_);
                                        toast.show();
                                        return false;
                                    }
                                });
                                if (!cust_imageview.getTag().equals("profile_image_android"))
                                    cust_imageview.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {

                                            LayoutInflater inflater = (LayoutInflater) context_
                                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            View view_ = inflater.inflate(R.layout.zoomprofileimage, null);
                                            TextView tv_0_0_ = (TextView) view_.findViewById(R.id.tv_chat_screen_context_chatter);
                                            ImageView img_0_0 = (ImageView) view_.findViewById(R.id.imageView_grditem_1);
                                            img_0_0.setImageURI(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0));
                                            tv_0_0_.setText(split[2]);
                                            Toast toast = new Toast(context_);
                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                                            toast.setView(view_);
                                            toast.show();
                                            return false;
                                        }
                                    });


                            } else if (Integer.parseInt(split[0]) == Constants.PUBLICIMAGEFILE) {
                                textview_time.setText(split[4]);
                                if (split[2] != null) {
                                    if (new File(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg").exists()) {
                                        if (new File(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg").length() > 0) {
                                            setProfileImage_URI(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg");
                                            String _uri_00 = EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg";
                                            File _file_00 = new File(_uri_00);
                                            if (_file_00.exists()) {
                                                FileInputStream _fis_00 = new FileInputStream(_file_00);
                                                if (!isJPEGValid(_fis_00))
                                                    cust_imageview.setTag("profile_image_android");
                                            } else
                                                cust_imageview.setTag("profile_image_android");
                                            // textview.append("\n\n" + cust_imageview.getTag().toString());
                                        }

                                    }

                                    if (getProfileImage_URI(split[2].toString().replace(":", "")) != null) {
                                        if (custom.getProfile_image_uri().equals("#Nothing#") && (custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain).equalsIgnoreCase(split[2].toString())) {
                                            cust_imageview.setImageURI(Uri.parse("#Nothing#"));
                                            cust_imageview.setTag("profile_image_android");

                                        } else {
                                            cust_imageview.setImageBitmap(decodeSampledBitmapFromResource(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0), 55, 55));
                                            cust_imageview.setTag(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0));

                                            String _uri_00 = getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0).toString();
                                            File _file_00 = new File(_uri_00);

                                            if (_file_00.exists()) {
                                                FileInputStream _fis_00 = new FileInputStream(_file_00);

                                                if (!isJPEGValid(_fis_00))
                                                    cust_imageview.setTag("profile_image_android");
                                            } else
                                                cust_imageview.setTag("profile_image_android");

                                        }
                                    }
                                } else {
                                    textview_chatter.setText("#Unknown#");
                                }
                                textview.setVisibility(View.GONE);
                                image_msg = (ImageView) view
                                        .findViewById(R.id.image_tosend_chat_screen);
                                image_msg.setTag("contexter");
                                image_msg.setVisibility(View.VISIBLE);
                                cust_imageview.setVisibility(View.VISIBLE);
                                //image_msg.setImageURI(Uri.parse(split[3]));
                                File file_ = new File(split[3].toString());

                                if (file_.exists()) {
                                    image_msg.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(split[3].toString()), 65, 65));

                                } else {
                                    image_msg.setImageResource(R.drawable.image_message_default);
                                    image_msg.setTag("image_message_default");
                                }

                                imageview.setVisibility(View.GONE);
                                llo_s_message_holder.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));

                                if (!image_msg.getTag().equals("image_message_default"))
                                    image_msg.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            LayoutInflater inflater = (LayoutInflater) context_
                                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            View view_ = inflater.inflate(R.layout.zoomprofileimage, null);
                                            TextView tv_0_0_ = (TextView) view_.findViewById(R.id.tv_chat_screen_context_chatter);
                                            ImageView img_0_0 = (ImageView) view_.findViewById(R.id.imageView_grditem_1);
                                            //img_0_0.setScaleX(5.0f);
                                            // img_0_0.setScaleY(5.0f);
                                            img_0_0.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(split[3].toString()), 330, 330));
                                            tv_0_0_.setText("");
                                            Toast toast = new Toast(context_);
                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                                            toast.setView(view_);
                                            toast.show();
                                            return false;
                                        }
                                    });


                            } else if (Integer.parseInt(split[0]) == Constants.PRIVATEIMAGEFILE) {
                                textview_time.setText(split[5]);
                                textview.setText(split[4]);

                                if (split[2] != null) {
                                    if (new File(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg").exists()) {
                                        if (new File(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg").length() > 0) {
                                            setProfileImage_URI(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg");
                                            String _uri_00 = EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg";
                                            File _file_00 = new File(_uri_00);
                                            if (_file_00.exists()) {
                                                FileInputStream _fis_00 = new FileInputStream(_file_00);
                                                if (!isJPEGValid(_fis_00))
                                                    cust_imageview.setTag("profile_image_android");
                                            } else
                                                cust_imageview.setTag("profile_image_android");
                                        }

                                    }

                                    if (getProfileImage_URI(split[2].toString().replace(":", "")) != null) {
                                        if (custom.getProfile_image_uri().equals("#Nothing#") && (custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain).equalsIgnoreCase(split[2].toString())) {
                                                  cust_imageview.setImageURI(Uri.parse("#Nothing#"));
                                            cust_imageview.setTag("profile_image_android");
                                        } else {
                                            cust_imageview.setImageBitmap(decodeSampledBitmapFromResource(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0), 55, 55));
                                            cust_imageview.setTag(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0));

                                            String _uri_00 = getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0).toString();
                                            File _file_00 = new File(_uri_00);

                                            if (_file_00.exists()) {
                                                FileInputStream _fis_00 = new FileInputStream(_file_00);

                                                if (!isJPEGValid(_fis_00))
                                                    cust_imageview.setTag("profile_image_android");
                                            } else
                                                cust_imageview.setTag("profile_image_android");


                                        }
                                    }
                                } else {
                                    textview_chatter.setText("#Unknown#");
                                }

                                imageview.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {

                                        LayoutInflater inflater = (LayoutInflater) context_
                                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View view_ = inflater.inflate(R.layout.zoomprofileimage, null);
                                        TextView tv_0_0_ = (TextView) view_.findViewById(R.id.tv_chat_screen_context_chatter);
                                        ImageView img_0_0 = (ImageView) view_.findViewById(R.id.imageView_grditem_1);
                                        img_0_0.setImageURI(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0));
                                        tv_0_0_.setText(split[2]);
                                        Toast toast = new Toast(context_);
                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                                        toast.setView(view_);
                                        toast.show();
                                        return false;
                                    }
                                });
                                if (!cust_imageview.getTag().equals("profile_image_android"))
                                    cust_imageview.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {

                                            LayoutInflater inflater = (LayoutInflater) context_
                                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            View view_ = inflater.inflate(R.layout.zoomprofileimage, null);
                                            TextView tv_0_0_ = (TextView) view_.findViewById(R.id.tv_chat_screen_context_chatter);
                                            ImageView img_0_0 = (ImageView) view_.findViewById(R.id.imageView_grditem_1);
                                            img_0_0.setImageURI(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0));
                                            tv_0_0_.setText(split[2]);
                                            Toast toast = new Toast(context_);
                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                                            toast.setView(view_);
                                            toast.show();
                                            return false;
                                        }
                                    });


                            } else if (Integer.parseInt(split[0]) == Constants.DEBUG) {
                                if (split[2] != null) {
                                    //setProfileImage_URI(EditProfileActivity.getAppFolder("") + (split[2].toString().replace(":", "")).split("@")[0] + ".jpg");
                                    textview_chatter.setText(split[2]);
                                }

                                if (split[4] != null) {
                                    //textview_time.setText(split[4]);
                                } else {
                                    //textview_time.setText("--");
                                }
                                if (split[3] != null) {
                                    {
                                        // textview.setText(split[3]);
                                    }
                                } else {
                                    //textview.setText("");
                                }

                            } else {

                            }
                        } else if (split.length == 11 || split.length == 12) {
                            if (Integer.parseInt(split[0]) == Constants.PUBLIC) {
                                textview_time.setText(split[8]);
                                tv_location.setVisibility(View.GONE);
                                tv_loud.setVisibility(View.GONE);
                                tv_run.setVisibility(View.GONE);
                                tv_thermo.setVisibility(View.GONE);
                                textview.setText(split[3]);

                                // Context readings
                                LinearLayout ll = (LinearLayout) view.findViewById(R.id.imageView_llo_sub_item_advs);
                                ArrayList<String> al_ = new ArrayList<String>();


                                SharedPreferences mPrefs = context_.getSharedPreferences("bootstraps", 0);
                                TreeMap bs_map = new TreeMap<String, String>();
                                Set<String> bootstraps = mPrefs.getStringSet("bootstraps", bs_map.keySet());
                                for (String tmp_0 : bootstraps) {
                                    al_.add(tmp_0 + "::BS:");
                                }

                                PublicChats _thread_0 = new PublicChats(context_, "*ALL#", "advs");
                                ArrayList<String> temp_ar = _thread_0.getmPublicChat("");
                                for (String temp_00 : temp_ar)
                                    if (!al_.contains(temp_00 + "::ADV"))
                                        al_.add(temp_00 + "::ADV");

                                ListAdapter listadapter = new ListAdapter(context_, -1, false, al_, 18);

                                if (view.getTag() == "G") {
                                    listadapter.setSmall_gridVW(1);
                                } else
                                    listadapter.setSmall_gridVW(0);
                                gv_adv.setAdapter(listadapter);
                                if (PlatformManagerNode.ST_PLATFORM_IS_UP || PlatformManagerBootstrap.ST_PLATFORM_IS_UP) {
                                    gv_adv.setNumColumns(3);
                                    // Hide --small advs
                                    gv_adv.setVisibility(View.GONE);
                                }

                                ll.setVisibility(View.VISIBLE);
                                gv_adv.setSelector(R.color.cust_color_1);

                                if (split[7] != null) {
                                    iv_location.setBackground(context_.getResources().getDrawable(R.drawable.location_stage_two));
                                    tv_location.setText("");
                                    tv_location.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            displayCustomizedToast(context_, Constants.getLocationDescription(Float.parseFloat(split[7])) + split[7] + "\n " + split[8]);
                                            return false;
                                        }
                                    });
                                } else {
                                    iv_location.setBackground(context_.getResources().getDrawable(R.drawable.location_stage_one));
                                    tv_location.setText("--");
                                }
                                if (split[4] != null) {

                                    if (Float.valueOf(split[4]) > 10)
                                        iv_loud.setBackground(context_.getResources().getDrawable(R.drawable.loud_stage_two));
                                    else
                                        iv_loud.setBackground(context_.getResources().getDrawable(R.drawable.loud_stage_two));
                                    tv_loud.setText("");
                                    iv_loud.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            displayCustomizedToast(context_, Constants.getSoundDescription(Float.parseFloat(split[4])) + split[4] + "\n " + split[8]);
                                            return false;

                                        }
                                    });
                                } else {
                                    iv_loud.setBackground(context_.getResources().getDrawable(R.drawable.loud_stage_two));
                                    tv_loud.setText("--");
                                }

                                if (split[6] != null) {
                                    tv_run.setText("");

                                    if (Float.valueOf(split[6]) > 10)
                                        iv_run.setBackground(context_.getResources().getDrawable(R.drawable.run_stage_two));
                                    else
                                        iv_run.setBackground(context_.getResources().getDrawable(R.drawable.run_stage_two));

                                    iv_run.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            displayCustomizedToast(context_, Constants.getAccDescription(Float.parseFloat(split[6])) + split[6] + "\n " + split[8]);
                                            return false;

                                        }
                                    });
                                } else {
                                    iv_run.setBackground(context_.getResources().getDrawable(R.drawable.run_stage_two));
                                    tv_run.setText("--");

                                }
                                if (split[5] != null) {
                                    tv_thermo.setText("");
                                    if (Float.valueOf(split[5]) > 10)
                                        iv_thermo.setBackground(context_.getResources().getDrawable(R.drawable.thermo_stage_two));
                                    else
                                        iv_thermo.setBackground(context_.getResources().getDrawable(R.drawable.thermo_stage_two));
                                    iv_thermo.setVisibility(View.VISIBLE);

                                    iv_thermo.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            displayCustomizedToast(context_, Constants.getLuminanceDescription(Float.parseFloat(split[5])) + split[5] + "\n " + split[8]);
                                            return false;

                                        }
                                    });

                                } else {
                                    iv_thermo.setBackground(context_.getResources().getDrawable(R.drawable.thermo_stage_two));
                                    tv_thermo.setText("--");
                                }
                                if (split[2] != null) {
                                    setProfileImage_URI(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg");
                                    if (getProfileImage_URI(split[2].toString().replace(":", "")) != null) {
                                        if (custom.getProfile_image_uri().equals("#Nothing#") && (custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain).equalsIgnoreCase(split[2].toString())) {
                                                 cust_imageview.setImageURI(Uri.parse("Nothing"));
                                            cust_imageview.setTag("profile_image_android");
                                        } else {
                                            cust_imageview.setImageBitmap(decodeSampledBitmapFromResource(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___") + ".jpg"), 55, 55));
                                            cust_imageview.setTag(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___") + ".jpg"));
                                        }

                                    }
                                } else
                                    textview_chatter.setText("#Unknown#");


                            } else if (Integer.parseInt(split[0]) == Constants.PRIVATE) {
                                textview_time.setText(split[9]);
                                textview.setText(split[4]);
                                tv_location.setVisibility(View.GONE);
                                tv_loud.setVisibility(View.GONE);
                                tv_run.setVisibility(View.GONE);
                                tv_thermo.setVisibility(View.GONE);


                                // Context readings
                                LinearLayout ll = (LinearLayout) view.findViewById(R.id.imageView_llo_sub_item_advs);
                                ArrayList<String> al_ = new ArrayList<String>();


                                SharedPreferences mPrefs = context_.getSharedPreferences("bootstraps", 0);
                                TreeMap bs_map = new TreeMap<String, String>();
                                Set<String> bootstraps = mPrefs.getStringSet("bootstraps", bs_map.keySet());
                                for (String tmp_0 : bootstraps) {
                                    al_.add(tmp_0 + "::BS:");
                                }


                                PublicChats _thread_0 = new PublicChats(context_, "*ALL#", "advs");
                                ArrayList<String> temp_ar = _thread_0.getmPublicChat("");
                                for (String temp_00 : temp_ar)
                                    if (!al_.contains(temp_00 + "::ADV"))
                                        al_.add(temp_00 + "::ADV");

                                ListAdapter listadapter = new ListAdapter(context_, -1, false, al_, 18);

                                if (view.getTag() == "G") {
                                    listadapter.setSmall_gridVW(1);
                                } else
                                    listadapter.setSmall_gridVW(0);
                                gv_adv.setAdapter(listadapter);
                                if (PlatformManagerNode.ST_PLATFORM_IS_UP || PlatformManagerBootstrap.ST_PLATFORM_IS_UP) {
                                    gv_adv.setNumColumns(al_.size());
                                    gv_adv.setVisibility(View.VISIBLE);
                                }
                                ll.setVisibility(View.VISIBLE);
                                gv_adv.setSelector(R.color.cust_color_1);


                                if (split[8] != null) {
                                    iv_location.setBackground(context_.getResources().getDrawable(R.drawable.location_stage_two));
                                    tv_location.setText("");
                                    tv_location.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            displayCustomizedToast(context_, Constants.getLocationDescription(Float.parseFloat(split[8])) + split[8] + "\n " + split[9]);
                                            return false;
                                        }
                                    });
                                } else {
                                    iv_location.setBackground(context_.getResources().getDrawable(R.drawable.location_stage_one));
                                    tv_location.setText("--");
                                }
                                if (split[5] != null) {

                                    if (Float.valueOf(split[5]) > 10)
                                        iv_loud.setBackground(context_.getResources().getDrawable(R.drawable.loud_stage_two));
                                    else
                                        iv_loud.setBackground(context_.getResources().getDrawable(R.drawable.loud_stage_two));
                                    tv_loud.setText("");
                                    iv_loud.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            displayCustomizedToast(context_, Constants.getSoundDescription(Float.parseFloat(split[5])) + split[5] + "\n " + split[9]);
                                            return false;

                                        }
                                    });
                                } else {
                                    iv_loud.setBackground(context_.getResources().getDrawable(R.drawable.loud_stage_two));
                                    tv_loud.setText("--");
                                }

                                if (split[7] != null) {
                                    tv_run.setText("");

                                    if (Float.valueOf(split[7]) > 10)
                                        iv_run.setBackground(context_.getResources().getDrawable(R.drawable.run_stage_two));
                                    else
                                        iv_run.setBackground(context_.getResources().getDrawable(R.drawable.run_stage_two));

                                    iv_run.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            displayCustomizedToast(context_, Constants.getAccDescription(Float.parseFloat(split[7])) + split[7] + "\n " + split[9]);
                                            return false;

                                        }
                                    });
                                } else {
                                    iv_run.setBackground(context_.getResources().getDrawable(R.drawable.run_stage_two));
                                    tv_run.setText("--");

                                }
                                if (split[6] != null) {
                                    tv_thermo.setText("");
                                    if (Float.valueOf(split[6]) > 10)
                                        iv_thermo.setBackground(context_.getResources().getDrawable(R.drawable.thermo_stage_two));
                                    else
                                        iv_thermo.setBackground(context_.getResources().getDrawable(R.drawable.thermo_stage_two));
                                    iv_thermo.setVisibility(View.VISIBLE);

                                    iv_thermo.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            displayCustomizedToast(context_, Constants.getSoundDescription(Float.parseFloat(split[6])) + split[6] + "\n " + split[9]);
                                            return false;

                                        }
                                    });

                                } else {
                                    iv_thermo.setBackground(context_.getResources().getDrawable(R.drawable.thermo_stage_two));
                                    tv_thermo.setText("--");
                                }
                                if (split[2] != null) {
                                    setProfileImage_URI(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg");
                                    if (getProfileImage_URI(split[2].toString().replace(":", "")) != null) {
                                        cust_imageview.setImageURI(Uri.parse("#Nothing#"));
                                        cust_imageview.setTag("profile_image_android");
                                       /* } else {
                                            imageview.setImageURI(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0));
                                            //cust_imageview.setImageURI(getProfileImage_URI(split[2].toString().replace("/", "_").replace("@", "%"), 0));
                                            cust_imageview.setImageBitmap(decodeSampledBitmapFromResource(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0), 55, 55));
                                            cust_imageview.setTag(getProfileImage_URI(split[2].toString().replace("/", "__").replace("@", "___"), 0));
                                        }*/
                                    }
                                } else
                                    textview_chatter.setText("#Unknown#");


                            } else {

                            }

                        } else {
                            textview.setText(array_list.get(position));
                            llo.setVisibility(View.VISIBLE);
                            image_msg.setVisibility(View.VISIBLE);
                            if (temp.contains("IMAGEFILE#") && !isSetProfilePictureGone()) {
                                image_msg.setVisibility(View.VISIBLE);
                                textview.setText("");
                            }
                        }
                        textview_chatter.setText(split[2]);
                        // if context chat mode is chosen
                        if (!CHAT_MODE) {
                            imageview.setVisibility(View.GONE);// previouse case
                            cust_imageview.setVisibility(View.VISIBLE);
                            llo_s_item.setVisibility(View.VISIBLE);

                            llo_.setVisibility(View.VISIBLE);
                            iv_run.setVisibility(View.VISIBLE);
                            iv_thermo.setVisibility(View.VISIBLE);
                            iv_loud.setVisibility(View.VISIBLE);
                            iv_location.setVisibility(View.VISIBLE);

                            tv_run.setVisibility(View.VISIBLE);
                            tv_thermo.setVisibility(View.VISIBLE);
                            tv_loud.setVisibility(View.VISIBLE);
                            tv_location.setVisibility(View.VISIBLE);
                            // textview_subitem.setText("kjfaks dfjasdf");

                        } else {
                            imageview.setVisibility(View.GONE);// previouse case
                            cust_imageview.setVisibility(View.VISIBLE);
                            llo_.setVisibility(View.VISIBLE);
                            llo_s_item.setVisibility(View.GONE);

                            iv_run.setVisibility(View.GONE);
                            iv_thermo.setVisibility(View.GONE);
                            iv_loud.setVisibility(View.GONE);
                            iv_location.setVisibility(View.GONE);
                            gv_adv.setVisibility(View.GONE);

                            tv_run.setVisibility(View.GONE);
                            tv_thermo.setVisibility(View.GONE);
                            tv_loud.setVisibility(View.GONE);
                            tv_location.setVisibility(View.GONE);
                        }

                        if (!this.setProfilePictureGone) {
                            llo.setVisibility(View.VISIBLE);
                            if (Profile_Image_URI != null) ;
                            else {
                                imageview.setImageResource(mIcons[6]);
                                cust_imageview.setImageResource(mIcons[6]);
                                cust_imageview.setTag("profile_image_android");
                            }
                            if (temp.contains("#IMAGEFILE")) {
                                textview.setVisibility(View.GONE);
                                image_msg.setImageURI(Uri.parse(temp.split("#IMAGEFILE")[1]));
                                String tmp = temp.split("#IMAGEFILE")[0];
                                textview_time.setText(tmp.split("~::~")[1]);

                            }

                        } else {
                            llo_s_item_.setVisibility(View.GONE);
                            llo_s_message_holder.setBackground(context_.getResources().getDrawable(R.drawable.rectangletv_4_reverse_180));
                            llo_s_message_holder.setBackgroundColor(context_.getResources().getColor(R.color.background_color_1));
                            imageview.setVisibility(View.GONE);
                            cust_imageview.setVisibility(View.GONE);

                            textview.setVisibility(View.VISIBLE);
                            llo_.setVisibility(View.VISIBLE);
                            image_msg.setVisibility(View.GONE);
                        }

                    } else {
                        if (array_list.get(position).equals("~PLATFORM~DOWN~")) {
                            if (position > 0)
                                if (!array_list.get(position - 1).equals("~PLATFORM~DOWN~") && (!PlatformManagerBootstrap.ST_PLATFORM_IS_UP && !PlatformManagerNode.ST_PLATFORM_IS_UP)) {
                                    image_msg.setVisibility(View.VISIBLE);
                                    image_msg.setImageResource(R.drawable.ic_bootstrapper_grey_small);
                                    textview_time.setText("Platform Down " + format.format(new Date()));
                                    textview.setText("Connection Problem. " + custom.getPreferred_bs_ip());
                                    textview.setTextColor(context_.getResources().getColor(R.color.background_color_1));

                                    imageview.setVisibility(View.GONE);
                                    llo_s_item_.setMinimumWidth(400);
                                    llo_s_message_holder.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                    llo_s_item_.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                    textview.setTextSize(10.0f);
                                    cust_imageview.setVisibility(View.GONE);
                                    textview_chatter.setVisibility(View.GONE);
                                    llo.setVisibility(View.VISIBLE);
                                } else {
                                    image_msg.setVisibility(View.VISIBLE);
                                    image_msg.setImageResource(R.drawable.ic_bootstrapper_grey_small);
                                    textview_time.setText("Platform Down " + format.format(new Date()));
                                    textview.setText("Connection Problem. " + custom.getPreferred_bs_ip());
                                    textview.setTextColor(context_.getResources().getColor(R.color.background_color_1));

                                    imageview.setVisibility(View.GONE);
                                    llo_s_item_.setMinimumWidth(400);
                                    llo_s_message_holder.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                    llo_s_item_.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                    textview.setTextSize(10.0f);
                                    cust_imageview.setVisibility(View.GONE);
                                    textview_chatter.setVisibility(View.GONE);
                                    llo.setVisibility(View.VISIBLE);


                                }
                            else {
                                textview.setTextSize(10.0f);
                                textview.setText(R.string.about_text_sv);
                                llo_s_message_holder.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                view.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                textview.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                imageview.setVisibility(View.GONE);
                                cust_imageview.setVisibility(View.GONE);
                                llo.setVisibility(View.GONE);
                            }
                        } else if (array_list.get(position).equals("~PLATFORM~UP~")) {
                            if (position > 0) {
                                if (!array_list.get(position - 1).equals("~PLATFORM~UP~") && (PlatformManagerBootstrap.ST_PLATFORM_IS_UP || PlatformManagerNode.ST_PLATFORM_IS_UP)) {
                                    image_msg.setVisibility(View.VISIBLE);
                                    // if bootstrap is private or public
                                    String ip = getStringByPattern(custom.getPreferred_bs_ip(), "");
                                    if (custom.IS_NODE_BOOTSTRAP_()) {
                                        image_msg.setImageResource(R.drawable.ic_bootstrapper_oregon_small);
                                    } else {
                                        if (ip.equalsIgnoreCase("~::~"))
                                            image_msg.setImageResource(R.drawable.ic_bootstrapper_cyan_small);
                                        else
                                            image_msg.setImageResource(R.drawable.ic_bootstrapper_blue_small);
                                    }

                                    textview_time.setText("Platform Up " + format.format(new Date()));
                                    textview.setText("      Bootstrap Ok. ip " + custom.getPreferred_bs_ip());
                                    textview.setTextColor(context_.getResources().getColor(R.color.background_color_1));
                                    // textview_time.setText("...");
                                    llo_s_item_.setMinimumWidth(400);
                                    llo_s_message_holder.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                    llo_s_item_.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                    textview.setTextSize(10.0f);
                                    imageview.setVisibility(View.GONE);
                                    cust_imageview.setVisibility(View.GONE);
                                    textview_chatter.setVisibility(View.GONE);
                                    llo.setVisibility(View.VISIBLE);
                                } else {
                                    textview.setTextSize(10.0f);
                                    textview.setText(R.string.about_text_sv);
                                    llo_s_message_holder.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                    view.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                    textview.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                    imageview.setVisibility(View.GONE);
                                    cust_imageview.setVisibility(View.GONE);
                                    llo.setVisibility(View.GONE);
                                }
                            } else {
                                textview.setTextSize(10.0f);
                                textview.setText(R.string.about_text_sv);
                                llo_s_message_holder.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                view.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                textview.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                imageview.setVisibility(View.GONE);
                                cust_imageview.setVisibility(View.GONE);
                                llo.setVisibility(View.GONE);
                            }

                        } else if (array_list.get(position).equals("~PLATFORM~MORE~")) {
                            if (position == 0 || position == 1) {
                                image_msg.setVisibility(View.VISIBLE);
                                // if bootstrap is private or public
                                //
                                image_msg.setImageResource(R.drawable.ic_restore);
                                image_msg.setTag("~PLATFORM~MORE~");
                                textview_time.setText("            View More Messages            ");
                                textview.setText("                  ");

                                // textview.setVisibility(View.GONE);
                                llo_s_item_.setMinimumWidth(400);
                                llo_s_message_holder.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                llo_s_item_.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                textview.setTextSize(10.0f);
                                imageview.setVisibility(View.GONE);
                                cust_imageview.setVisibility(View.GONE);
                                textview_chatter.setVisibility(View.GONE);
                                llo.setVisibility(View.VISIBLE);
                            } else {
                                textview.setTextSize(10.0f);
                                textview.setText(R.string.about_text_sv);
                                llo_s_message_holder.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                view.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                textview.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                                imageview.setVisibility(View.GONE);
                                cust_imageview.setVisibility(View.GONE);
                                llo.setVisibility(View.GONE);
                            }

                        } else if (array_list.get(position).equals("~ADV~PLATFORM~")) {
                            textview.setTextSize(10.0f);
                            textview.setText(R.string.about_text_sv);
                            llo_s_message_holder.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                            view.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                            textview.setBackgroundColor(context_.getResources().getColor(android.R.color.transparent));
                            imageview.setVisibility(View.GONE);
                            cust_imageview.setVisibility(View.GONE);
                            llo.setVisibility(View.GONE);

                            PublicChats _thread_0 = new PublicChats(context_, "*ALL#", "advs");
                            ArrayList<String> temp_ar = _thread_0.getmPublicChat("");
                            for (String temp_00 : temp_ar)
                                //if (!al_.contains(temp_00 + "::ADV"))
                                textview.append(temp_00 + " ");

                        } else {
                            textview.setText(array_list.get(position));
                            imageview.setVisibility(View.GONE);
                            cust_imageview.setVisibility(View.GONE);
                            textview_chatter.setVisibility(View.GONE);
                            llo.setVisibility(View.GONE);


                        }

                    }
                }

                // default profile image if Image doesnt exist
                if (cust_imageview.getTag().equals("profile_image_android"))
                    cust_imageview.setImageResource(R.drawable.profile_image_android);


                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(context_.getApplicationContext(), "? ? ?", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });


                // Font face
                textview.setTypeface(tf);
                imageview.setPadding(0, 0, 10, 0);
                view.setPadding(0, 10, 0, 10);

            } else if (this.adapaterSerial == 1) {


                if (language_ == 0) {
                    array = context_.getResources().getStringArray(
                            R.array.menu_array_en_);
                    array_ = context_.getResources().getStringArray(
                            R.array.menu_array_en);
                } else if (language_ == 1) {
                    array = context_.getResources().getStringArray(
                            R.array.menu_array_en_);
                    array_ = context_.getResources().getStringArray(
                            R.array.menu_array_sv);
                } else if (language_ == 2) {
                    array = context_.getResources().getStringArray(
                            R.array.menu_array_en_);
                    array_ = context_.getResources().getStringArray(
                            R.array.menu_array_sp);
                } else if (language_ == 3) {
                    array = context_.getResources().getStringArray(
                            R.array.menu_array_en_);
                    array_ = context_.getResources().getStringArray(
                            R.array.menu_array_pr);
                } else if (language_ == 4) {
                    array = context_.getResources().getStringArray(
                            R.array.menu_array_en_);
                    array_ = context_.getResources().getStringArray(
                            R.array.menu_array_fr);
                } else if (language_ == 5) {
                    array = context_.getResources().getStringArray(
                            R.array.menu_array_en_);
                    array_ = context_.getResources().getStringArray(
                            R.array.menu_array_am);
                } else
                    array = context_.getResources().getStringArray(
                            R.array.menu_array_en_);
                view = li.inflate(R.layout.chitchato_choices_griditem, null);
                textview = (TextView) view.findViewById(R.id.choices_btn);
                cust_imageview = (CustomImageView) view.findViewById(R.id.cust_img_btn_choices_PIC);
                button_concentric_circle = (Button) view
                        .findViewById(R.id.choices_btn_concentric);
                button_concentric_circle.setFocusable(false);
                button_concentric_circle.setClickable(false);
                textview_subitem = (TextView) view
                        .findViewById(R.id.tv_choices_sub_item);
                button_concentric_circle.setText(array[position]);
                button_concentric_circle.setTypeface(tf);
                // Sub item --> Choice buttons
                textview_subitem.setText(array_[position]);
                textview_subitem.setTypeface(tf);






                if (position == 0) {
                    if (!custom.getContext_image_message_uri().equals("#Nothing#"))
                        cust_imageview.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/"+custom.getContext_image_message_uri()+".jpg"), 100, 100));
                        //cust_imageview.setImageURI(Uri.parse(custom.getContext_image_message_uri()));
                } else if (position == 1) {
                    if (!custom.getGroup_image_message_uri().equals("#Nothing#"))
                        cust_imageview.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/"+custom.getGroup_image_message_uri() +".jpg"), 100, 100));
                } else if (position == 2) {
                    if (!custom.getUci_image_message_uri().equals("#Nothing#"))
                        cust_imageview.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/"+custom.getUci_image_message_uri()+".jpg"), 100, 100));
                       // cust_imageview.setImageURI(Uri.parse(custom.getUci_image_message_uri()));
                } else if (position == 3) {
                    if (!custom.getSearch_image_message_uri().equals("#Nothing#"))
                        cust_imageview.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/"+custom.getSearch_image_message_uri()+".jpg"), 100, 100));
                       // cust_imageview.setImageURI(Uri.parse(custom.getSearch_image_message_uri()));
                } else if (position == 4) {
                    if (!custom.getLang_image_message_uri().equals("#Nothing#"))
                        cust_imageview.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/"+custom.getLang_image_message_uri() +".jpg"), 100, 100));
                        //cust_imageview.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(custom.getLang_image_message_uri()), 30, 30));
                       // cust_imageview.setImageURI(Uri.parse(custom.getLang_image_message_uri()));
                } else if (position == 5) {
                    if (!custom.getHelp_image_message_uri().equals("#Nothing#"))
                        cust_imageview.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/"+custom.getHelp_image_message_uri() +".jpg"), 100, 100));
                        //cust_imageview.setImageURI(Uri.parse(custom.getHelp_image_message_uri()));
                } else if (position == 6) {
                    if (!custom.getAbt_image_message_uri().equals("#Nothing#"))
                        cust_imageview.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/"+custom.getAbt_image_message_uri()+".jpg"), 60, 60));
                        //cust_imageview.setImageURI(Uri.parse(custom.getAbt_image_message_uri()));
                }
                else if (position == 7) {
                    if (!custom.getAbt_image_message_uri().equals("#Nothing#"))
                        cust_imageview.setImageBitmap(decodeSampledBitmapFromResource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chitchato/" + "Media/"+custom.getAbt_image_message_uri() +".jpg"), 60, 60));
                    //cust_imageview.setImageURI(Uri.parse(custom.getAbt_image_message_uri()));
                }

                if (position == 7)
                    textview.setBackgroundResource(R.drawable.circlebtn_red);
                // if context chat mode is chosen
                if (this.About_VISITED && position == 6)
                    textview.setBackgroundResource(R.drawable.circlebtn_blue_2);
                else if (this.CGA_VISITED && position == 1)
                    textview.setBackgroundResource(R.drawable.circlebtn_blue_2);
                else if (this.MA_VISITED && position == 0)
                    textview.setBackgroundResource(R.drawable.circlebtn_blue_2);
                else if (this.HA_VISITED && position == 5)
                    textview.setBackgroundResource(R.drawable.circlebtn_blue_2);
                else if (this.LA_VISITED && position == 4)
                    textview.setBackgroundResource(R.drawable.circlebtn_blue_2);
                else if (this.SGA_VISITED && position == 3)
                    textview.setBackgroundResource(R.drawable.circlebtn_blue_2);
                else if (this.RA_VISITED && position == 2)
                    textview.setBackgroundResource(R.drawable.circlebtn_blue_2);
                if (isPlatformRecieving) {
                    textview.setBackgroundResource(R.drawable.circlebtn_red_with_border_large_screen);
                }
                view.setPadding(0, 10, 0, 10);

            } else if (this.adapaterSerial == 2) {

                view = li.inflate(R.layout.privatechatscreengriditem, null);
                LinearLayout linear_layout = (LinearLayout) view
                        .findViewById(R.id.imageView_pr_llo_);

                textview = (TextView) view
                        .findViewById(R.id.editText_grditem_pr_3);
                textview_time = (TextView) view
                        .findViewById(R.id.tv_prchat_screen_context_chat_time);
                textview_chatter = (TextView) view
                        .findViewById(R.id.tv_prchat_screen_context_chatter);
                imageview = (ImageView) view
                        .findViewById(R.id.imageView_grditem_pr_1);
                textview_subitem = (TextView) view
                        .findViewById(R.id.tv_chat_screen_context_sub_pr_item);

                // if context chat mode is chosen
                if (!CHAT_MODE) {
                    textview_subitem.setText(context_message);
                    llo_s_item.setVisibility(View.VISIBLE);
                    iv_run.setVisibility(View.VISIBLE);
                    iv_thermo.setVisibility(View.VISIBLE);
                    iv_loud.setVisibility(View.VISIBLE);
                    iv_location.setVisibility(View.VISIBLE);
                } else {
                    textview_subitem.setVisibility(View.GONE);
                    // imageview.setVisibility(View.GONE);
                    llo_.setVisibility(View.GONE);
                    llo_s_item.setVisibility(View.GONE);
                    iv_run.setVisibility(View.GONE);
                    iv_thermo.setVisibility(View.GONE);
                    iv_loud.setVisibility(View.GONE);
                    iv_location.setVisibility(View.GONE);

                }

                String temp = array_list.get(position);
                String[] split = temp.split("::");

                if (split.length > 4) {
                    if (split[1] != null)
                        textview.setText(split[1]);
                    else
                        textview.setText("#None#");
                    if (split[3] != null)
                        textview_time.setText(split[3]);
                    else
                        textview_time.setText("#None#");
                    if (split[2] != null)
                        textview_chatter.setText(split[2]);
                    else
                        textview_chatter.setText("#None#");
                } else
                    textview.setText(array_list.get(position));
                linear_layout.setVisibility(View.GONE);

                // Font Face
                textview.setTypeface(tf);
                textview_subitem.setTypeface(tf);
                textview_chatter.setTypeface(tf);
                textview.setTextColor(context_.getResources().getColor(
                        R.color.oregon));
                // imageview.setImageResource(mIcons[position]);
                imageview.setPadding(0, 0, 10, 0);

                view.setPadding(0, 10, 0, 10);

            } else if (this.adapaterSerial == 3) {
                view = li.inflate(R.layout.searchresultsgriditem, null);
                textview = (TextView) view
                        .findViewById(R.id.editText_grditem_3_1);
                check_box = (CheckBox) view.findViewById(R.id.chck_bx_addgroup);

                if (array_list == null || array_list.isEmpty())
                    textview.setText(array[position]);
                else {
                    textview.setText(array_list.get(position));
                    if (array_list.get(position).startsWith(
                            "Sorry! No Results Found")) {
                        check_box.setVisibility(View.GONE);
                    }
                }
                // Font Face
                textview.setTypeface(tf);

                // imageview.setImageResource(mIcons[position]);
                view.setPadding(0, 10, 0, 10);

            } else
                ;

        } catch (
                Exception e
                )

        {
            e.printStackTrace();

        }

        return view;
    }

    public void setPosition(int j) {
        this.index = j;

    }

    public void setIsPlatformRecieving(boolean b) {
        this.isPlatformRecieving = b;
    }

    public int getOldPosition() {
        return this.old_index;
    }

    public void setOldPosition(int j) {
        this.old_index = j;
    }

    public boolean isSetProfilePictureGone() {
        return setProfilePictureGone;
    }

    public void setSetProfilePictureGone(boolean setProfilePictureGone) {
        this.setProfilePictureGone = setProfilePictureGone;
    }

    public void addItem(ArrayList<String> items) {
        this.array_list = items;
    }

    public void setLanguage(int l) {
        this.language_ = l;
    }

    public boolean isMA_VISITED() {
        return MA_VISITED;
    }

    public void setMA_VISITED(boolean mA_VISITED) {
        MA_VISITED = mA_VISITED;
    }

    public boolean isCGA_VISITED() {
        return CGA_VISITED;
    }

    public void setCGA_VISITED(boolean cGA_VISITED) {
        CGA_VISITED = cGA_VISITED;
    }

    public boolean isRA_VISITED() {
        return RA_VISITED;
    }

    public void setRA_VISITED(boolean rA_VISITED) {
        RA_VISITED = rA_VISITED;
    }

    public boolean isLA_VISITED() {
        return LA_VISITED;
    }

    public void setLA_VISITED(boolean lA_VISITED) {
        LA_VISITED = lA_VISITED;
    }

    public boolean isHA_VISITED() {
        return HA_VISITED;
    }

    public void setHA_VISITED(boolean hA_VISITED) {
        HA_VISITED = hA_VISITED;
    }

    public boolean isAbout_VISITED() {
        return About_VISITED;
    }

    public void setAbout_VISITED(boolean about_VISITED) {
        About_VISITED = about_VISITED;
    }

    public boolean isSGA_VISITED() {
        return SGA_VISITED;
    }

    public void setSGA_VISITED(boolean sGA_VISITED) {
        SGA_VISITED = sGA_VISITED;
    }

    public ImageView getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(ImageView profile_image) {
        this.profile_image = profile_image;
    }

    public ImageView getMessage_image() {
        return image_msg;
    }

    public void setMessage_image(ImageView image) {
        this.image_msg = image;
    }

    public void setMessage_imageURI(Uri uri) {
        this.image_msg.setImageURI(uri);
    }

    public Uri get_image_message_uri() {
        return _image_message_uri;
    }

    public void set_image_message_uri(Uri _image_message_uri) {
        this._image_message_uri = _image_message_uri;
    }

    public boolean is_image_message_ready() {
        return _image_message_ready;
    }

    public void set_image_message_ready(boolean _image_message_ready) {
        this._image_message_ready = _image_message_ready;
    }

    public Uri getProfileImage_URI() {
        return Profile_Image_URI;
    }

    public void setProfileImage_URI(String str_uri) {
        this.Profile_Image_URI = Uri.parse(str_uri);
    }

    public Uri getProfileImage_URI(String uci) {
        return Uri.parse(custom.getProfile_image_uri(uci));
    }

    public Uri getProfileImage_URI(String uci, int i) {
        return Uri.parse(EditProfileActivity.getAppFolder("") + uci + ".jpg");
    }

    public String getContext_message() {
        return context_message;
    }

    public void setContext_message(String context_message) {
        this.context_message = context_message;
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

    public void displayCustomizedToast(final Context _context_, String message) {

        LayoutInflater inflater = (LayoutInflater) _context_.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public String getStringByPattern(String _input_, String pat) {
        String ptr = "~::~";
        // if ip is public bootstrap
        final String ip_address_pattern_public = "^([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(?<!172\\." +
                "(16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31))(?<!127)(?<!^10)(?<!^0)\\.([0-9]|[1-9]" +
                "[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(?<!192\\.168)(?<!172\\.(16|17|18|19|20|21|22|23|24|25" +
                "|26|27|28|29|30|31))\\.([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9]" +
                "{2}|2[0-4][0-9]|25[0-5])(?<!\\.0$)(?<!\\.255$)$";


        //String ip_address_pattern = "\\b10.1.90.247\\b";
        final Pattern pattern = Pattern.compile(ip_address_pattern_public);

        Matcher matcher = pattern.matcher(_input_);
        while (matcher.find()) {
            int ind_b = matcher.start();
            int ind_e = matcher.end();
            ptr = _input_.substring(ind_b, ind_e);

        }
        return ptr;
    }

    private String getIPAddress() {

        Runnable r = new Runnable() {
            public void run() {
                try {
                    //Workaround because Linux is stupid...
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                                if (!(inetAddress instanceof Inet6Address)) { //Remove this line for IPV6 compatability
                                    local_ip_ = inetAddress.getHostAddress();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // local_ip_ = e.getLocalizedMessage();
                }
                //Start the Listener!
            }
        };
        Thread t = new Thread(r);
        t.start();
        return local_ip_;
    }
}