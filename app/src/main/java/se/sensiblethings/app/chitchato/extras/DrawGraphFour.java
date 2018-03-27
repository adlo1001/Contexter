package se.sensiblethings.app.chitchato.extras;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Random;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.context.ContextManager;

@SuppressLint("NewApi")
public class DrawGraphFour extends ViewGroup {

    Paint mPaintone = new Paint();
    Paint mPainttwo = new Paint();
    Paint mPaintthree = new Paint();
    Paint mPaintfour = new Paint();
    private RectF mBounds = new RectF();
    private int[] GP_Value_X = new int[30];
    private int[] GP_Value_Y = new int[10];
    private int[] GP_X = new int[100];
    private int[] GP_Y = new int[100];
    private int[] GP_Y_ = new int[100];
    private int sqr_side;
    protected Context _mcontext = null;
    private int temp_x = -1, temp_y = -1, x_cl = 0, y_cl = 0, origin_x = 0,
            origin_y = 0;
    Random rand1, rand2;
    private String max_value = "0.0", min_value = "0.0", val1 = "0.0",
            val2 = "0.0", val3 = "0.0", val4 = "0.0", val5 = "0.0",
            val6 = "0.0", val7 = "0.0", val8 = "0.0", val9 = "0.0",
            val10 = "0.0";
    private ArrayList<String> al;
    private Float val;
    private String sensor_name = "Location";
    private boolean READ_SENSORS = false;
    private String address_ = "Address not detected",
            location_ = "";
    private float longi_ = 0.0f, lati_ = 0.0f;
    private ContextManager _context_manager;

    public DrawGraphFour(Context context) {
        super(context);

        _mcontext = context;
        initialize();

    }

    public DrawGraphFour(Context context, AttributeSet attrs_) {
        super(context, attrs_);

        _mcontext = context;
        initialize();

    }

    public DrawGraphFour(Context context, AttributeSet attrs, String Symbol) {
        super(context, attrs);
        _mcontext = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.draw_graph_view, 0, 0);
        initialize();
    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        // TODO Auto-generated method stub
    }

    // @SuppressLint("ResourceAsColor")
    @Override
    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        // int color_1 = R.color.oregon;
        mPaintone.setColor(Color.RED);
        mPaintone.setStrokeWidth(2);
        mPaintone.setTextSize(40);
        mPainttwo.setColor(Color.GRAY);
        mPainttwo.setTextSize(30);
        mPaintthree.setColor(Color.LTGRAY);
        mPaintthree.setTextSize(40);
        mPaintthree.setStrokeWidth(2);
        mPaintfour.setColor(Color.GREEN);
        mPaintfour.setStrokeCap(Cap.ROUND);
        mPaintfour.setStrokeWidth(30);
        mPaintfour.setTextSize(40);
        int d = getMeasuredWidth() - getMeasuredHeight();

        origin_x = getMeasuredWidth() / 2;
        origin_y = getMeasuredHeight() / 2;
        x_cl = 3 * (getMeasuredWidth() - 60) / 4;
        y_cl = 3 * (getMeasuredHeight() - 100) / 4;
        sqr_side = x_cl / 30;

        val = Float.parseFloat(this.getMaxValue());

        int index = 1;

        for (int i = 1, j = 31; i <= 33; i++, j--) {
            rand2 = new Random();

            // canvas.drawLine(origin_x + x_cl / 2 - i * sqr_side, origin_y -
            // y_cl
            // / 2, origin_x + x_cl / 2 - i * sqr_side, origin_y + y_cl
            // / 2, mPainttwo);

            if (i == 1 || i == 7 || i == 14 || i == 21 || i == 28) {
                canvas.drawText(j + "", origin_x + x_cl / 2 - i * sqr_side,
                        origin_y + y_cl / 2 + 20, mPainttwo);

            }

            if (i * sqr_side < y_cl) {
                {
                    // canvas.drawLine(origin_x + x_cl / 2, origin_y + y_cl / 2
                    // - i * sqr_side, origin_x - x_cl / 2 - 75, origin_y
                    // + y_cl / 2 - i * sqr_side, mPainttwo);

                    if (i % 3 == 0 && i != 1 && i != 2) {

                        if ((origin_y + y_cl / 2 - (y_cl
                                * (Float.parseFloat(al.get(i))) / val))
                                - (origin_y + y_cl / 2 - (y_cl
                                * (Float.parseFloat(al.get(i - 1))) / val)) > sqr_side)
                            canvas.drawText(
                                    getValue(i / 3),
                                    origin_x + x_cl / 2 + sqr_side,
                                    2 * (origin_y + y_cl / 2 - (y_cl
                                            * (Float.parseFloat(al.get(i))) / val)),
                                    mPainttwo);
                        else if ((origin_y + y_cl / 2 - (y_cl
                                * (Float.parseFloat(al.get(i))) / val))
                                - (origin_y + y_cl / 2 - (y_cl
                                * (Float.parseFloat(al.get(i - 2))) / val)) > sqr_side)
                            canvas.drawText(
                                    getValue(i / 3),
                                    origin_x + x_cl / 2 + sqr_side,
                                    2 * (origin_y + y_cl / 2 - (y_cl
                                            * (Float.parseFloat(al.get(i))) / val)),
                                    mPainttwo);

                    }

                }
            }
        }


        Typeface tf = Typeface.createFromAsset(_mcontext.getAssets(),
                "fonts/COMIC.TTF");
        mPaintthree.setTypeface(tf);


        canvas.drawText(this.sensor_name,
                (origin_x - sensor_name.length() / 2) - 100, origin_y - y_cl
                        / 2 - 30, mPaintthree);

        if ((_mcontext.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            mPaintthree.setTextSize(30);
            canvas.drawText(this.getLocation() + "", origin_x - 280, origin_y
                    + y_cl / 2 - 100, mPaintthree);

            canvas.drawText(this.getAddress() + "", origin_x - 300, origin_y + y_cl
                    / 2, mPaintthree);
        } else {
            mPaintthree.setTextSize(40);
            canvas.drawText(this.getLocation() + "", origin_x - 280, origin_y
                    + y_cl / 2 - 100, mPaintthree);
            canvas.drawText(this.getAddress() + "", origin_x - 500, origin_y + y_cl
                    / 2, mPaintthree);
        }




    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = 0;
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int widthWithoutPadding = w - getPaddingLeft() - getPaddingRight();
        int heigthWithoutPadding = h - getPaddingTop() - getPaddingBottom();

        // if (widthWithoutPadding > heigthWithoutPadding) {
        // size = heigthWithoutPadding;
        // } else {
        // size = widthWithoutPadding;
        // }

        setMeasuredDimension(w + getPaddingLeft() + 5 * getPaddingRight(), h
                + getPaddingTop() + getPaddingBottom());

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //
        // Set dimensions for text, pie chart, etc
        //
        // Account for padding
        // float xpad = (float) (getPaddingLeft() + getPaddingRight());
        // float ypad = (float) (getPaddingTop() + getPaddingBottom());

        // float ww = (float) w - xpad;
        // float hh = (float) h - ypad;

        // mBounds = new RectF(0.0f, 0.0f, ww, hh);
        // mBounds.offsetTo(getPaddingLeft(), getPaddingTop());

    }

    private void setLayerToSW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    private void setLayerToHW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    private void initialize() {
        setLayerToSW(this);
    }

    public void setMinValue(String value) {
        this.min_value = value;
        this.invalidate();
        requestLayout();
    }

    public void setMaxValue(String value) {
        this.max_value = value;
        this.invalidate();
        requestLayout();
    }

    public void setValueOne(String value) {
        this.val1 = value;
        invalidate();
        requestLayout();
    }

    public void setValueTwo(String value) {
        this.val2 = value;
        invalidate();
        requestLayout();
    }

    public void setValueThree(String value) {
        this.val3 = value;
        invalidate();
        requestLayout();
    }

    public void setValueFour(String value) {
        this.val4 = value;
        invalidate();
        requestLayout();
    }

    public void setValueFive(String value) {
        this.val5 = value;
        invalidate();
        requestLayout();
    }

    public void setValueSix(String value) {
        this.val6 = value;
        invalidate();
        requestLayout();
    }

    public void setValueSeven(String value) {
        this.val7 = value;
        invalidate();
        requestLayout();
    }

    public void setValueEight(String value) {
        this.val8 = value;
        invalidate();
        requestLayout();
    }

    public void setValueNine(String value) {
        this.val9 = value;
        invalidate();
        requestLayout();
    }

    public void setValueTen(String value) {
        this.val10 = value;
        invalidate();
        requestLayout();
    }

    public String getValue(int i) {
        String temp = "0.0";
        if (i == 1)
            temp = val1;
        else if (i == 2)
            temp = val2;
        else if (i == 3)
            temp = val3;
        else if (i == 4)
            temp = val4;
        else if (i == 5)
            temp = val5;
        else if (i == 6)
            temp = val6;
        else if (i == 7)
            temp = val7;
        else if (i == 8)
            temp = val8;
        else if (i == 9)
            temp = val9;
        else if (i == 10)
            temp = val10;

        return temp;
    }

    public String getMinValue() {
        return this.min_value;
    }

    public String getMaxValue() {
        return this.max_value;

    }

    public void setDailyValue(ArrayList<String> al) {
        this.al = al;
        invalidate();
        requestLayout();
    }

    public void setMonth(String s) {
        this.sensor_name = s;
        invalidate();
        requestLayout();
    }

    public void setReadSensors(boolean rEAD_SENSORS) {
        this.READ_SENSORS = rEAD_SENSORS;

    }

    public boolean isREAD_SENSORS() {
        return READ_SENSORS;
    }

    public void setContextManager(ContextManager context_manager) {

        this._context_manager = context_manager;
    }

    public void setLongitude(float parseFloat) {
        // TODO Auto-generated method stub
        this.longi_ = parseFloat;

    }

    public void setLatitude(float parseFloat) {
        // TODO Auto-generated method stub
        this.lati_ = parseFloat;

    }

    public void setAddress(String info) {
        // TODO Auto-generated method stub
        this.address_ = info;

    }

    public float getLongitude() {
        return this.longi_;
    }

    public float getLatitude() {
        return this.lati_;
    }

    public String getAddress() {
        return this.address_;
    }

    public String getLocation() {
        return this.location_;
    }

    public void setLocation(String lat_log) {
        this.location_ = lat_log;
    }

    public String getSensor_name() {
        return sensor_name;
    }

    public void setSensor_name(String sensor_name) {
        this.sensor_name = sensor_name;
    }

}
