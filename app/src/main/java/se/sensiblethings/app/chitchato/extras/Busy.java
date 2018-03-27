package se.sensiblethings.app.chitchato.extras;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import se.sensiblethings.app.R;

public class Busy extends Dialog {

    Bundle extras;
    Thread thread;
    Intent int_t1, int_t2, int_t3, int_t4;
    SharedPreferences mPrefs;
    boolean dialog_15_done = false;
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

    public Busy(Context context, boolean cancelable, int serial) {
        super(context);
        // TODO Auto-generated constructor stub
        cntxt = context;
        this.serial_no = serial;
        custom = new Customizations(cntxt, -1);
        this.setCancelable(false);
        this.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        //this.getWindow().setTitleColor(context.getResources().getColor(R.color.chitchato_mebratu_blue));
        this.getWindow().setTitle("  " + getDialog_title());
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layout_inflater = (LayoutInflater) cntxt
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layout_inflater.inflate(R.layout.splash, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_staller_dialog);
        Typeface tf = Typeface.createFromAsset(cntxt.getAssets(),
                "fonts/COMIC.TTF");
        Typeface tf_pala = Typeface.createFromAsset(cntxt.getAssets(),
                "fonts/pala.ttf");
        tv.setTypeface(tf_pala);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(cntxt,
                R.layout.spinner_item_one);
        if ((cntxt.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            tv.setTextSize(20.0f);
        }

        Animation animation = AnimationUtils.loadAnimation(cntxt, R.anim.move_tv_to_right_side);
        tv.setAnimation(animation);
        setContentView(view);
        this.setTitle(dialog_title);




    }

    public String getDialog_title() {
        return dialog_title;
    }

    public void setDialog_title(String dialog_title) {
        this.dialog_title = dialog_title;
    }
}
