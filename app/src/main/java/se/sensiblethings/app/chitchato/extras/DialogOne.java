package se.sensiblethings.app.chitchato.extras;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.activities.ChoicesActivity;
import se.sensiblethings.app.chitchato.activities.CreateGroupActivity;
import se.sensiblethings.app.chitchato.activities.EditProfileActivity;
import se.sensiblethings.app.chitchato.activities.ErrorActivity;
import se.sensiblethings.app.chitchato.activities.MainActivity;
import se.sensiblethings.app.chitchato.activities.MoreActivity;
import se.sensiblethings.app.chitchato.activities.PrivateActivity;
import se.sensiblethings.app.chitchato.activities.WelcomeActivity;
import se.sensiblethings.app.chitchato.kernel.Groups;

public class DialogOne extends Dialog {

    Bundle extras;
    Thread thread;
    Intent int_t1, int_t2, int_t3, int_t4;
    SharedPreferences mPrefs;
    boolean dialog_15_done = false;
    String txt_toDisplay = "";
    private TextView tv;
    private EditText edt;
    private ListView lv;
    private Spinner groups_spinner;
    private ArrayList<String> al;
    private Context cntxt;
    private int serial_no = 0;
    private String dialog_title = "";
    private String dialog_message = "";
    private Dialog dialog_;
    private Customizations custom;

    public DialogOne(Context context, boolean cancelable, int serial) {
        super(context);
        cntxt = context;
        this.serial_no = serial;
        custom = new Customizations(cntxt, -1);
        this.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        this.getWindow().setTitleColor(context.getResources().getColor(R.color.chitchato_mebratu_blue));
        this.getWindow().setTitle("  Contexter  ");

    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layout_inflater = (LayoutInflater) cntxt
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layout_inflater.inflate(R.layout.splashone, null);
        TextView tv = (TextView) view.findViewById(R.id.dialog_one_tv);
        groups_spinner = (Spinner) view.findViewById(R.id.spinner_group_id_dialog);
        Button btn_yes = (Button) view.findViewById(R.id.dialog_one_btn_yes);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView_grditem_1);
        Button btn_no = (Button) view.findViewById(R.id.dialog_one_btn_no);
        Typeface tf = Typeface.createFromAsset(cntxt.getAssets(),
                "fonts/COMIC.TTF");
        Typeface tf_pala = Typeface.createFromAsset(cntxt.getAssets(),
                "fonts/pala.ttf");
        tv.setTypeface(tf_pala);
        btn_yes.setTypeface(tf);
        btn_no.setTypeface(tf);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(cntxt,
                R.layout.spinner_item_one);
        if ((cntxt.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            tv.setTextSize(20.0f);
        }
        setContentView(view);

        if (custom.getLanguage() == 0) {
            btn_yes.setText(getContext().getResources().getString(R.string.string_dialog_one_0_0));
            btn_no.setText(getContext().getResources().getString(R.string.string_dialog_one_0_1));

        } else if (custom.getLanguage() == 1) {
            btn_yes.setText(getContext().getResources().getString(R.string.string_dialog_one_0_0_sv));
            btn_no.setText(getContext().getResources().getString(R.string.string_dialog_one_0_1_sv));

        } else if (custom.getLanguage() == 2) {
            btn_yes.setText(getContext().getResources().getString(R.string.string_dialog_one_0_0_sp));
            btn_no.setText(getContext().getResources().getString(R.string.string_dialog_one_0_1_sp));

        } else if (custom.getLanguage() == 3) {
            btn_yes.setText(getContext().getResources().getString(R.string.string_dialog_one_0_0_pr));
            btn_no.setText(getContext().getResources().getString(R.string.string_dialog_one_0_1_pr));

        } else if (custom.getLanguage() == 4) {
            btn_yes.setText(getContext().getResources().getString(R.string.string_dialog_one_0_0_fr));
            btn_no.setText(getContext().getResources().getString(R.string.string_dialog_one_0_1_fr));

        } else {
            btn_yes.setText(getContext().getResources().getString(R.string.string_dialog_one_0_0));
            btn_no.setText(getContext().getResources().getString(R.string.string_dialog_one_0_1));

        }


        // Get my own Groups
        Groups groups = new Groups(cntxt, "", true);
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
        groups_spinner.setAdapter(adapter);


        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serial_no == 5) {
                    Toast.makeText(cntxt, "Contexter Restored to Freshly Installed!", Toast.LENGTH_SHORT).show();
                    DialogOne.this.cancel();
                } else if (serial_no == 7) {
                    Intent intent = new Intent(getContext(), ErrorActivity.class);
                    intent.putExtra("error", "CON_READINGS");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getContext().startActivity(intent);
                } else if (serial_no == 8) {

                } else if (serial_no == 9) {
                    Intent intent = new Intent(getContext(),
                            WelcomeActivity.class);
                    intent.putExtra("from", 1);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getContext().startActivity(intent);
                } else if (serial_no == 12) {
                    Intent intent = new Intent(getContext(),
                            MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getContext().startActivity(intent);
                } else if (serial_no == 13) {
                    Intent intent = new Intent(getContext(),
                            ChoicesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getContext().startActivity(intent);
                }
                else if (serial_no == 130) {
                    Intent intent = new Intent(getContext(),
                            MoreActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getContext().startActivity(intent);
                }

                else if (serial_no == 15) {

                    mPrefs = getContext().getSharedPreferences("myprofile", 0);
                    SharedPreferences.Editor ed = mPrefs.edit();
                    ed.putString("URI", "#Nothing#");
                    custom.setProfile_image_uri("#Nothing#");
                    ed.commit();
                    EditProfileActivity.img_btn_1.setBackground(getContext().getResources().getDrawable(R.drawable.profile_image));
                    EditProfileActivity.img_btn_1.setImageURI(Uri.parse("#Nothing#"));
                    DialogOne.this.cancel();

                } else if (serial_no == 16) {
                    Intent intent = new Intent(getContext(),
                            PrivateActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getContext().startActivity(intent);
                } else if (serial_no == 17) {

                    //if (groups_spinner.isSelected()) {
                    System.out.println("+++++++++++++++++++++" + getDialog_message() + "  Selected Item " + (String) groups_spinner.getSelectedItem());
                    if (getDialog_message().equalsIgnoreCase("UPDATE")) {
                        Intent intent = new Intent(getContext(),
                                CreateGroupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("INDEX", "Update");
                        intent.putExtra("NAME", (String) groups_spinner.getSelectedItem());
                        getContext().startActivity(intent);

                    } else if (getDialog_message().equalsIgnoreCase("Delete")) {
                        Intent intent = new Intent(getContext(),
                                CreateGroupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("INDEX", "Delete");
                        intent.putExtra("NAME", (String) groups_spinner.getSelectedItem());
                        getContext().startActivity(intent);
                    }
                    //}
                }
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogOne.this.cancel();
            }
        });

        if (this.serial_no == 0) {
            dialog_title = "Warning. . .";
            tv.setText("            " + getTxt_toDisplay() + "      ");
            btn_no.setVisibility(View.INVISIBLE);
            btn_yes.setVisibility(View.INVISIBLE);
        } else if (this.serial_no == 1)
            tv.setText(txt_toDisplay);
        else if (this.serial_no == 2)
            dialog_title = "Error>> Contexter Crash  (0 | 0) ";
        else if (this.serial_no == 3)
            dialog_title = "Quitting  . . .";
        else if (this.serial_no == 4)
            dialog_title = "Deleting Your Group . . .";
        else if (this.serial_no == 5)
            dialog_title = "Delete Files . . .?";
        else if (this.serial_no == 6) {
            tv.setText(txt_toDisplay);
        } else if (this.serial_no == 7) {
            if (custom.getLanguage() == 0)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_6));
            else if (custom.getLanguage() == 1)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_6_sv));
            else if (custom.getLanguage() == 2)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_6_sp));
            else if (custom.getLanguage() == 3)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_6_pr));
            else if (custom.getLanguage() == 4)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_6_fr));
            else
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_6));
        } else if (this.serial_no == 8) {
            //dialog_title = " Information";
            btn_no.setVisibility(View.INVISIBLE);
            btn_yes.setVisibility(View.INVISIBLE);
            if (custom.getLanguage() == 0)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_7));
            else if (custom.getLanguage() == 1)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_7_sv));
            else if (custom.getLanguage() == 2)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_7_sp));
            else if (custom.getLanguage() == 3)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_7_pr));
            else if (custom.getLanguage() == 4)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_7_fr));
            else
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_7));
        } else if (this.serial_no == 9) {
            //dialog_title = " Information";
            //imageView.setImageURI(getContext().getPackageResourcePath());
            //tv.setText("    Do you want to quit this?     ");

            if (custom.getLanguage() == 0)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0));
            else if (custom.getLanguage() == 1)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_sv));
            else if (custom.getLanguage() == 2)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_sp));
            else if (custom.getLanguage() == 3)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_pr));
            else if (custom.getLanguage() == 4)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_fr));
            else
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0));

        } else if (this.serial_no == 10) {
            // dialog_title = " Information";
            btn_no.setVisibility(View.INVISIBLE);
            btn_yes.setVisibility(View.INVISIBLE);
            //tv.setText("Local Contexting Possible.  You need to add public bootstraps. ");
            if (custom.getLanguage() == 0)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_8));
            else if (custom.getLanguage() == 1)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_8_sv));
            else if (custom.getLanguage() == 2)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_8_sp));
            else if (custom.getLanguage() == 3)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_8_pr));
            else if (custom.getLanguage() == 4)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_8_fr));
            else
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_8));


        } else if (this.serial_no == 11) {
            // dialog_title = " Information";
            //tv.setText("         Delete Message  ?       ");

            if (custom.getLanguage() == 0)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_9));
            else if (custom.getLanguage() == 1)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_9_sv));
            else if (custom.getLanguage() == 2)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_9_sp));
            else if (custom.getLanguage() == 3)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_9_pr));
            else if (custom.getLanguage() == 4)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_9_fr));
            else
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_9));
        } else if (this.serial_no == 12) {
            // dialog_title = " Information";
            //tv.setText("    Do you want to quit this?    ");

            if (custom.getLanguage() == 0)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0));
            else if (custom.getLanguage() == 1)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_sv));
            else if (custom.getLanguage() == 2)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_sp));
            else if (custom.getLanguage() == 3)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_pr));
            else if (custom.getLanguage() == 4)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_fr));
            else
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0));
        } else if (this.serial_no == 13) {
            //dialog_title = " Information";
            // tv.setText("    Do you want to quit this?     ");
            if (custom.getLanguage() == 0)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0));
            else if (custom.getLanguage() == 1)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_sv));
            else if (custom.getLanguage() == 2)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_sp));
            else if (custom.getLanguage() == 3)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_pr));
            else if (custom.getLanguage() == 4)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_fr));
            else
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0));
        }
        else if (this.serial_no == 130) {
            //dialog_title = " Information";
            // tv.setText("    Do you want to quit this?     ");
            if (custom.getLanguage() == 0)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0));
            else if (custom.getLanguage() == 1)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_sv));
            else if (custom.getLanguage() == 2)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_sp));
            else if (custom.getLanguage() == 3)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_pr));
            else if (custom.getLanguage() == 4)
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0_fr));
            else
                tv.setText(getContext().getResources().getString(R.string.string_dialog_one_0));
        }
        else if (this.serial_no == 14) {
            // dialog_title = "Information";
            btn_no.setVisibility(View.INVISIBLE);
            btn_yes.setVisibility(View.INVISIBLE);
            tv.setText(getDialog_message());
        } else if (this.serial_no == 15) {
            dialog_title = "Warning";
            tv.setText(getDialog_message());
        } else if (this.serial_no == 17) {
            // dialog_title = "Information";
            if (adapter.getCount() == 0) {
                btn_yes.setVisibility(View.GONE);
                btn_no.setVisibility(View.GONE);
                tv.setVisibility(View.VISIBLE);
                groups_spinner.setVisibility(View.GONE);
                tv.setText("    Groups not found.  Import or create groups.   ");
                btn_yes.setText("Next");
                btn_no.setText("Cancel");

                if (custom.getLanguage() == 0) {
                    btn_yes.setText(getContext().getResources().getString(R.string.string_dialog_one_11_0));
                    btn_no.setText(getContext().getResources().getString(R.string.string_dialog_one_11_1));
                    tv.setText(getContext().getResources().getString(R.string.string_dialog_one_10));
                } else if (custom.getLanguage() == 1) {
                    btn_yes.setText(getContext().getResources().getString(R.string.string_dialog_one_11_0_sv));
                    btn_no.setText(getContext().getResources().getString(R.string.string_dialog_one_11_1_sv));
                    tv.setText(getContext().getResources().getString(R.string.string_dialog_one_10_sv));
                } else if (custom.getLanguage() == 2) {
                    btn_yes.setText(getContext().getResources().getString(R.string.string_dialog_one_11_0_sp));
                    btn_no.setText(getContext().getResources().getString(R.string.string_dialog_one_11_1_sp));
                    tv.setText(getContext().getResources().getString(R.string.string_dialog_one_10_sp));
                } else if (custom.getLanguage() == 3) {
                    btn_yes.setText(getContext().getResources().getString(R.string.string_dialog_one_11_0_pr));
                    btn_no.setText(getContext().getResources().getString(R.string.string_dialog_one_11_1_pr));
                    tv.setText(getContext().getResources().getString(R.string.string_dialog_one_10_pr));
                } else if (custom.getLanguage() == 4) {
                    btn_yes.setText(getContext().getResources().getString(R.string.string_dialog_one_11_0_fr));
                    btn_no.setText(getContext().getResources().getString(R.string.string_dialog_one_11_1_fr));
                    tv.setText(getContext().getResources().getString(R.string.string_dialog_one_10_fr));
                } else {
                    btn_yes.setText(getContext().getResources().getString(R.string.string_dialog_one_11_0));
                    btn_no.setText(getContext().getResources().getString(R.string.string_dialog_one_11_1));
                    tv.setText(getContext().getResources().getString(R.string.string_dialog_one_10));
                }


            } else {
                btn_yes.setVisibility(View.VISIBLE);
                tv.setVisibility(View.GONE);
                groups_spinner.setVisibility(View.VISIBLE);
                btn_yes.setText("Next");
                btn_no.setText("Cancel");
            }
        } else ;

        this.setTitle(dialog_title);
    }

    public boolean isDialog_15_done() {
        return dialog_15_done;
    }

    public void setDialog_15_done(boolean dialog_15_done) {
        this.dialog_15_done = dialog_15_done;
    }

    public String getDialog_message() {
        return dialog_message;
    }

    public void setDialog_message(String dialog_message) {
        this.dialog_message = dialog_message;
    }

    public String getTxt_toDisplay() {
        return txt_toDisplay;
    }

    public void setTxt_toDisplay(String txt_toDisplay) {
        this.txt_toDisplay = txt_toDisplay;
    }
}
