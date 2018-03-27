package se.sensiblethings.app.chitchato.extras;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.context.ContextManager;

@SuppressLint("NewApi")
public class DrawGraphOne extends ViewGroup {

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
	protected Context _mcontext = null;

	private int sqr_side;
	private int temp_x = -1, temp_y = -1, x_cl = 0, y_cl = 0, origin_x = 0,
			origin_y = 0;
	Random rand1, rand2;
	private String max_value = "0.0", min_value = "0.0", val1 = "0.0",
			val2 = "0.0", val3 = "0.0", val4 = "0.0", val5 = "0.0",
			val6 = "0.0", val7 = "0.0", val8 = "0.0", val9 = "0.0",
			val10 = "0.0";

	private ArrayList<String> al;
	private Float val;
	private String sensor_name = "Motion";
	private ContextManager _context_manager;
	public static boolean READ_SENSORS = false;
	TreeMap<Float, Float> temp_tm = new TreeMap<Float, Float>();
	ArrayList<Float> temp_arr = new ArrayList<Float>();
	private Float point_x=0.0f, point_y = 0.0f;



	public DrawGraphOne(Context context) {
		super(context);
		_mcontext = context;

		initialize();

	}

	public DrawGraphOne(Context context, AttributeSet attrs_) {
		super(context, attrs_);
		_mcontext = context;

		initialize();

	}

	public DrawGraphOne(Context context, AttributeSet attrs, String Symbol) {
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
		mPaintfour.setStrokeWidth(5);
		mPaintfour.setTextSize(40);
		int d = getMeasuredWidth() - getMeasuredHeight();

		origin_x = getMeasuredWidth() / 2;
		origin_y = getMeasuredHeight() / 2;
		x_cl = 3 * (getMeasuredWidth() - 60) / 4;
		y_cl = 3 * (getMeasuredHeight() - 100) / 4;
		sqr_side = x_cl / 30;

		val = Float.parseFloat(this.getMaxValue());
		int index = 1;

		Set set = temp_tm.entrySet();
		float x_one, y_one, x_two, y_two;
		Iterator itr_ = set.iterator();
		Map.Entry me = null;
		y_two = this.getPoint_y();
		x_one = origin_x + x_cl / 2;
		x_two = (origin_x + x_cl / 2) - sqr_side;
		y_one = origin_y;

		float temp_y_previous = 0.0f;
		if (temp_arr.size() > 1) {

			int ii = 1;

			for (Float temp_y : temp_arr) {
				x_two = (origin_x + x_cl / 2) - ii * sqr_side;
				x_one = (origin_x + x_cl / 2) - (ii - 1) * sqr_side;

				canvas.drawLine(x_one, -sqr_side * temp_y_previous + origin_y,
						x_two, -sqr_side * temp_y + origin_y, mPaintfour);

				// canvas.drawLine(x_one,
				// sqr_side*temp_arr.get(temp_arr.indexOf(temp_y) - 1)
				// + origin_y , x_two, sqr_side*temp_y + origin_y ,
				// mPaintfour);

				temp_y_previous = temp_arr.get(ii - 1);
				ii++;
			}

		}

		y_two = this.getPoint_y();
		x_one = origin_x + x_cl / 2;
		x_two = (origin_x + x_cl / 2) - sqr_side;
		y_one = origin_y;
		if (temp_arr.size() > 25)
			temp_arr.remove(0);
		temp_arr.add(y_two);
		// canvas.drawLine(x_one, y_one, x_two, origin_y + sqr_side*y_two,
		// mPaintfour);

		canvas.drawText(y_two + "", origin_x + x_cl / 2, origin_y + y_cl / 2
				+ 20, mPainttwo);

		for (int i = 1, j = 33; i < 30; i++, j--) {

			canvas.drawLine(origin_x + x_cl / 2 - i * sqr_side, origin_y - y_cl
					/ 2, origin_x + x_cl / 2 - i * sqr_side, origin_y + y_cl
					/ 2, mPainttwo);

			if (i > 1) {

				x_one = origin_x + x_cl / 2;
				x_two = origin_x + x_cl / 2 + sqr_side;
				y_one = origin_y;
				_context_manager.updateContextData();
				y_two = Float.parseFloat(_context_manager.getUserContext()
						.getAcce());

			}
			if (i == 1)
				canvas.drawText("0.0", origin_x + x_cl / 2 + sqr_side,
						origin_y, mPainttwo);

			// canvas.drawText(
			// getValue(1),
			// origin_x + x_cl / 2 + sqr_side,
			// 2 * (origin_y + y_cl / 2 - (y_cl
			// * (Float.parseFloat(al.get(1))) / val)), mPainttwo);
			// canvas.drawText(
			// String.valueOf(Float.parseFloat(this.getMaxValue())),
			// origin_x + x_cl / 2 + sqr_side,
			// 2 * (origin_y + y_cl / 2 - (y_cl
			// * (Float.parseFloat(this.max_value)) / val)),
			// mPainttwo);

			if (i * sqr_side < y_cl) {
				{
					canvas.drawLine(origin_x + x_cl / 2, origin_y + y_cl / 2
							- i * sqr_side, origin_x - x_cl / 2, origin_y
							+ y_cl / 2 - i * sqr_side, mPainttwo);

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

			// canvas.drawText(, getMeasuredWidth() - 60, sqr_side, mPainttwo);

		}

		canvas.drawLine(origin_x + x_cl / 2, origin_y - y_cl / 2, origin_x
				+ x_cl / 2, origin_y + y_cl / 2, mPaintone);
		canvas.drawLine(origin_x + x_cl / 2, origin_y, origin_x - x_cl / 2,
				origin_y, mPaintone);

		Typeface tf = Typeface.createFromAsset(_mcontext.getAssets(),
				"fonts/COMIC.TTF");
		mPaintthree.setTypeface(tf);
		canvas.drawText(this.sensor_name,
				origin_x - (sensor_name.length() / 2),
				origin_y - y_cl / 2 - 30, mPaintthree);

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

	public void setContextManager(ContextManager context_manager) {
		this._context_manager = context_manager;

	}

	public void setReadSensors(boolean rEAD_SENSORS) {
		this.READ_SENSORS = rEAD_SENSORS;

	}

	public boolean isREAD_SENSORS() {
		return READ_SENSORS;
	}

	public class BKGroundTask_0 extends AsyncTask<String, String, String> {
		boolean flag = false;
		@Override
		protected String doInBackground(String... arg0) {
			this.flag = false;
			temp_tm = new TreeMap<Float, Float>();
			float x_one, y_one, x_two, y_two;
			try {
				x_one = origin_x + x_cl / 2;
				y_one = origin_y;
				temp_tm.put(x_one, y_one);
				for (int i = 0; i < 11; i++) {
					x_two = origin_x + x_cl / 2 + sqr_side;
					_context_manager.updateContextData();
					setContextManager(_context_manager);
					y_two = Float.parseFloat(_context_manager.getUserContext()
							.getAcce());
					temp_tm.put(x_two, y_two);

				}
				Thread.sleep(1000);

			}

			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				result = "done";
				this.flag = true;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public Float getPoint_x() {
		return point_x;
	}

	public void setPoint_x(Float point_x) {
		this.point_x = point_x;
	}

	public Float getPoint_y() {
		return point_y;
	}

	public void setPoint_y(Float point_y) {
		this.point_y = point_y;
	}

	public String getSensor_name() {
		return sensor_name;
	}

	public void setSensor_name(String sensor_name) {
		this.sensor_name = sensor_name;
	}

}
