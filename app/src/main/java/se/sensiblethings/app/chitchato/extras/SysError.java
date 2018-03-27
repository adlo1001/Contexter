package se.sensiblethings.app.chitchato.extras;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import se.sensiblethings.app.R;

public class SysError extends Dialog {

	private TextView tv;
	private ListView lv;
	private ArrayList<String> al;
	Bundle extras;
	Thread thread;
	Intent int_t1, int_t2, int_t3, int_t4;
	private Context cntxt;

	public SysError(Context context, boolean cancelable) {
		super(context);
		// TODO Auto-generated constructor stub
		cntxt = context;
		// al = al_;

		this.getWindow().getDecorView()
				.setBackgroundResource(R.drawable.rectangletv_3);

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater layout_inflater = (LayoutInflater) cntxt
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layout_inflater.inflate(R.layout.error, null);

		setContentView(view);
		TextView tv = (TextView) findViewById(R.id.tv_error_dialog);

		this.setTitle(">>>>Oops... ");
		tv.setText("Error Encountered ...! ");

	}

}
