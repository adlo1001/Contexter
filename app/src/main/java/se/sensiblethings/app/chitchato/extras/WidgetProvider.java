package se.sensiblethings.app.chitchato.extras;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import se.sensiblethings.app.R;


public class WidgetProvider extends AppWidgetProvider {

	private static final String ACTION_CLICK = "ACTION_CLICK";
	ListView lv;
	View view;
	TextView tv1, tv2, tv3, tv4;
	ArrayList<CharSequence> status_update = new ArrayList<CharSequence>();
	Date date = new Date();

	Calendar cal = new GregorianCalendar();

    
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		cal.setTime(date);
		Boolean[] b = null;
        
		//SharedPreferences mPrefs = context
				//.getSharedPreferences("preference", 0);
	
         
		RemoteViews remoteviews = new RemoteViews(context.getPackageName(),se.sensiblethings.app.R.layout.widget_lo);
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = li.inflate(R.layout.widget_lo, null);
		Intent intent = new Intent(context, LocalWordServiceCC.class);

		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteviews.setOnClickPendingIntent(view.getId(), pendingIntent);

	}
}
