package se.sensiblethings.app.chitchato.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.activities.EditProfileActivity;
import se.sensiblethings.app.chitchato.activities.ErrorActivity;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerNode;
import se.sensiblethings.app.chitchato.extras.CustomImageView;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.DialogOne;

public class ImageExpandableAdapter extends BaseExpandableListAdapter {
    protected ArrayList<String> ar_list_par = new ArrayList<String>();
    protected ArrayList<String> ar_list_child = new ArrayList<String>();
    protected TreeMap<String, ArrayList<String>> holder_tm = new TreeMap<String, ArrayList<String>>();
    protected int adapter_serial = -1;
    private Context context_;
    private ImageView iv = null;
    private int index = 0;
    private Customizations custom;
    private Integer[] mIcons = {R.drawable.profile_image,
            R.drawable.profile_image, R.drawable.profile_image,
            R.drawable.profile_image, R.drawable.profile_image,
            R.drawable.profile_image, R.drawable.profile_image,
            R.drawable.profile_image

    };

    public ImageExpandableAdapter(Context c) {
        this.context_ = c;
    }

    public ImageExpandableAdapter(Context c, int i) {
        this.context_ = c;
        this.adapter_serial = i;
        custom = new Customizations(context_, -1);
    }

    public ImageExpandableAdapter(Context c, TreeMap tm, int i) {
        this.context_ = c;
        this.adapter_serial = i;
        holder_tm = tm;
        ar_list_par = new ArrayList<String>();
        Set<String> set = tm.keySet();
        for (String str : set) {
            ar_list_par.add(str);
        }
        custom = new Customizations(context_, -1);
    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
                                             int reqHeight) {

        Bitmap bm = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }

    public ImageView getSelectedImage() {

        return iv;
    }

    public void setIndex(int i) {
        this.index = i;
    }

    @Override
    public int getGroupCount() {
        return ar_list_par.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList al_tmp = holder_tm.get(getGroup(groupPosition));
        return al_tmp.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return ar_list_par.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return (Object) holder_tm.get(ar_list_par.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int position, boolean isExpanded, View view, ViewGroup parent) {
        ImageView imageview = null;
        TextView btn = null, textview_subitem;
        Typeface tf = Typeface.createFromAsset(this.context_.getAssets(), "fonts/pala.ttf");
        Typeface tf_goud_sout = Typeface.createFromAsset(this.context_.getAssets(), "fonts/GOUDYSTO.TTF");
        // Button btn;
        LayoutInflater li = (LayoutInflater) context_
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = li.inflate(R.layout.chitchato_settings_radio, null);
        TextView tv_language = (TextView) view
                .findViewById(R.id.tv_lang_checkbox);
        ImageView img_view_small_icon = (ImageView) view.findViewById(R.id.img_settings_radio_small_icon);
        ImageView imageView = (ImageView) view.findViewById(R.id.lang_chat_radio);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll_check_box);
        LinearLayout ll_1 = (LinearLayout) view.findViewById(R.id.linear_layout);

        tv_language.setTypeface(tf);
        if (this.adapter_serial == 0) {
            tv_language.setText(ar_list_par.get(position));
            img_view_small_icon.setVisibility(View.GONE);
            imageView.setImageResource(mIcons[0]);
            ll_1.setVisibility(View.GONE);

        }

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup par_view) {

        {
            try {
                Typeface tf = Typeface.createFromAsset(this.context_.getAssets(), "fonts/pala.ttf");
                Typeface tf_goud_sout = Typeface.createFromAsset(this.context_.getAssets(), "fonts/GOUDYSTO.TTF");
                // Button btn;
                //CheckBox ck_box = null;
                // LinearLayout ll_1, ll_2, ll_3;
                ImageView img_view_small_icon, imageView;
                LayoutInflater li = (LayoutInflater) context_
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // view = li.inflate(R.layout.chatscreengriditem_green, null);


                ar_list_child = holder_tm.get(getGroup(groupPosition));
                final String group_val = ar_list_child.get(childPosition);


                // Message box orientation varies depending on the owner of  the message
                if (ar_list_child == null) {
                    view = li.inflate(R.layout.chatscreengriditem_reverse_180_yellow, null);
                    view.setTag("Y");
                } else if (ar_list_child.size() > 0) {
                    if (ar_list_child.get(childPosition).contains("~::~" + custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~") && ar_list_child.get(childPosition).startsWith("100")
                            || ar_list_child.get(childPosition).contains("~::~" + custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~") && ar_list_child.get(childPosition).startsWith("102")) {
                        //view = li.inflate(R.layout.chatscreengriditem_reverse_180_yellow, null);
                        String tmppp = ar_list_child.get(childPosition);
                        String[] tmp_00 = tmppp.split("~::~");

                        //Texting
                        if (tmp_00.length < 8 && ar_list_child.get(childPosition).startsWith("100")) {
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
                        else if (tmp_00.length > 8 && ar_list_child.get(childPosition).startsWith("100")) {
                            if (tmp_00[8].toString().endsWith("failed")) {
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


                    }
                    //Private Message format
                    else if (ar_list_child.get(childPosition).contains("~::~" + custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~") && ar_list_child.get(childPosition).startsWith("101")
                            || ar_list_child.get(childPosition).contains("~::~" + custom.getPeer_nick_name() + PlatformManagerNode.contexter_domain + "~::~") && ar_list_child.get(childPosition).startsWith("103")) {

                        String[] tmp_00 = ar_list_child.get(childPosition).split("~::~");

                        //Texting
                        if (tmp_00.length > 5 && ar_list_child.get(childPosition).startsWith("101")) {
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
                        else if (tmp_00.length > 9 && ar_list_child.get(childPosition).startsWith("103")) {
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

                    } else if (!ar_list_child.get(childPosition).contains("~::~")) {
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

                ImageView imageview = (ImageView) view
                        .findViewById(R.id.imageView_grditem_1);
                CustomImageView cust_imageview = (CustomImageView) view.findViewById(R.id.cust_imageView_grditem_1);
                cust_imageview.setTag("profile_image_android");

                ImageView image_msg = (ImageView) view
                        .findViewById(R.id.image_tosend_chat_screen);
                TextView textview_time = (TextView) view
                        .findViewById(R.id.tv_chat_screen_context_chat_time);
                TextView textview_chatter = (TextView) view
                        .findViewById(R.id.tv_chat_screen_context_chatter);
                TextView textview = (TextView) view.findViewById(R.id.editText_grditem_3);


                // ll_1 = (LinearLayout) view.findViewById(R.id.ll_check_box);
                //  ll_2 = (LinearLayout) view.findViewById(R.id.ll_check_box_);
                // ll_3 = (LinearLayout) view.findViewById(R.id.linear_layout);
                textview.setTypeface(tf);


                final String[] split = group_val.split("~::~");
                textview.setText(group_val);
                textview.setGravity(Gravity.CENTER);
                textview_chatter.setVisibility(View.GONE);
                image_msg.setVisibility(View.GONE);
                cust_imageview.setVisibility(View.GONE);
                // ll_3.setVisibility(View.INVISIBLE);
                textview.setPadding(0, 0, 0, 0);

                if (split.length > 7) {
                    textview_time.setText(split[5]);
                    textview.setText(split[4]);

                    if (split[2] != null) {
                        if (new File(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg").exists()) {
                            if (new File(EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg").length() > 0) {
                                String _uri_00 = EditProfileActivity.getAppFolder("") + split[2].toString().replace("/", "__").replace("@", "___") + ".jpg";
                                File _file_00 = new File(_uri_00);
                                if (_file_00.exists()) {
                                    FileInputStream _fis_00 = new FileInputStream(_file_00);
                                } else
                                    cust_imageview.setTag("profile_image_android");
                                // textview.append("\n\n" + cust_imageview.getTag().toString());
                            }

                        }
                    } else {
                        textview_chatter.setText("#Unknown#");
                    }

                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogOne dialog_one = new DialogOne(context_, false, 0);
                        dialog_one.setTitle("");
                        dialog_one.setTxt_toDisplay(group_val);
                        dialog_one.show();
                        //displayCustomizedToast(context_, group_val);


                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(this.context_, ErrorActivity.class);
                intent.putExtra("error", "System Error");
                this.context_.startActivity(intent);
            }
        }
        return view;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
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

}
