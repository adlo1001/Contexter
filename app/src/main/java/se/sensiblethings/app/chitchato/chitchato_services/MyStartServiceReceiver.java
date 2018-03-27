package se.sensiblethings.app.chitchato.chitchato_services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.activities.GroupContainerActivity;
import se.sensiblethings.app.chitchato.activities.InitializeLocalBTSRPActivity;
import se.sensiblethings.app.chitchato.extras.Customizations;


public class MyStartServiceReceiver extends BroadcastReceiver {
    StringBuilder contexter_adverts;

    @Override
    public void onReceive(final Context context, Intent intent) {
        final Customizations custom = new Customizations(context, -1);

        if (isInternetAvailable(context) && !custom.IS_NODE_BOOTSTRAP_())
            if (!custom.isSERVICE_UP()) {
                Intent service = new Intent(context, PlatformManagerNode.class);
                context.startService(service);
            } else if (!custom.isST_UP() || !PlatformManagerNode.ST_PLATFORM_IS_UP) {
                Intent service = new Intent(context, PlatformManagerNode.class);
                context.startService(service);
            } else {
                // custom.setPLATFORM_STARTUP_TRIAL(0);
                //SharedPreferences mPrefs = context.getSharedPreferences("myprofile", 0);
                //SharedPreferences.Editor ed = mPrefs.edit();
                //ed.putInt("PLATFORM_STARTUP_TRIAL", 0);
                //ed.commit();
            }
        else if ((custom.isHotSpotON() || isInternetAvailable(context)) && custom.IS_NODE_BOOTSTRAP_())
            if (!custom.isSERVICE_UP()) {
                Intent service = new Intent(context, PlatformManagerBootstrap.class);
                context.startService(service);
                System.out.println("Service_______________________________________________________________SERVICE01");
            } else if (!custom.isST_UP() || !PlatformManagerBootstrap.ST_PLATFORM_IS_UP) {
                Intent service = new Intent(context, PlatformManagerBootstrap.class);
                context.startService(service);
                System.out.println("Service_______________________________________________________________SERVICE02");
            } else {
                //custom.setPLATFORM_STARTUP_TRIAL(0);
                //SharedPreferences mPrefs = context.getSharedPreferences("myprofile", 0);
                //SharedPreferences.Editor ed = mPrefs.edit();
                //ed.putInt("PLATFORM_STARTUP_TRIAL", 0);
                //ed.commit();
                System.out.println("Service_______________________________________________________________SERVICE03");
            }

        System.out.println("Service_______________________________________________________________SERVICE00");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> wifi_list = wifiManager.getScanResults();
        contexter_adverts = new StringBuilder(custom.getCONTEXTER_ADVS());
        Random random = new Random();
        for (ScanResult sr : wifi_list) {

            if (!contexter_adverts.toString().contains("~::~" + sr.SSID + "~::~"))
                if (sr.SSID.startsWith("~0") && sr.SSID.endsWith("0~")) {
                    contexter_adverts.append("~::~" + sr.SSID);
                    MessageNotifier(null, context, "New Bootstraps around:" + sr.SSID, sr.SSID, "", true, 10);
                    displayCustomizedToast(context, sr.SSID + "\nIP :" + InitializeLocalBTSRPActivity.getIPfromPattern(sr.SSID.split("#")[1]), "New Bootstraps around", true);
                }
            //   System.out.println("_________________________" + sr.SSID);
        }


        custom.setCONTEXTER_ADVS(contexter_adverts.toString());


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SharedPreferences mPrefs = context.getSharedPreferences("myprofile", 0);
                SharedPreferences.Editor ed = mPrefs.edit();
                ed.putString("CONTEXTER_ADVS", contexter_adverts.toString());
                if (!PlatformManagerNode.ST_PLATFORM_IS_UP || !PlatformManagerBootstrap.ST_PLATFORM_IS_UP) {
                    custom.setPLATFORM_STARTUP_TRIAL(custom.getPLATFORM_STARTUP_TRIAL() + 30);
                    ed.putInt("PLATFORM_STARTUP_TRIAL", custom.getPLATFORM_STARTUP_TRIAL());
                }
                ed.commit();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

        System.out.println("________________________________Information_________________________________________");
        System.out.println("___________" + "SERVICE IS UP: " + custom.isSERVICE_UP() + "________________________");
        System.out.println("___________" + "PLATFORM UP: " + custom.isST_UP() + "_______________________________");
        System.out.println("___________" + "ST_IP " + PlatformManagerNode.ST_PLATFORM_IS_UP + "_________________");
        System.out.println("___________" + "ST_IP_BT " + PlatformManagerBootstrap.ST_PLATFORM_IS_UP + "_________");
        System.out.println("___________" + "custom.IS_NODE_BOOTSTRAP_" + custom.IS_NODE_BOOTSTRAP_() + "________");
        System.out.println("___________" + custom.getPLATFORM_STARTUP_TRIAL() + "_______________________________");
        System.out.println("_____________________________________End____________________________________________");
    }

    // Check if the Internet is Available
    public boolean isInternetAvailable(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void MessageNotifier(View view, Context c, String mess_1, String mess_2, String mess_3, boolean status, int notification_id) {
        Intent intent = new Intent(c.getApplicationContext(),
                GroupContainerActivity.class);
        intent.putExtra("error", "Notifier");
        intent.putExtra("INDEX", "NEW BOOTSTRAPS");
        intent.putExtra("ST_STATUS", status);
        intent.putExtra("PAT", mess_2);
        PendingIntent pintent = PendingIntent.getActivity(c.getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
        Notification _mNotification = null;

        if (notification_id == 10) {
            if (status)
                _mNotification = new Notification.Builder(c.getApplicationContext()).setContentTitle("Contexter")
                        .setContentText(mess_1).setSmallIcon(R.drawable.ic_launcher_mini).setContentIntent(pintent)
                        .addAction(R.drawable.button_custom, "See Detail", pintent)
                        .addAction(R.drawable.button_custom, "Cancel this", pintent)
                        .build();
            else
                _mNotification = new Notification.Builder(c.getApplicationContext()).setContentTitle("Contexter")
                        .setContentText(mess_1).setSmallIcon(R.drawable.ic_launcher_mini).setContentIntent(pintent)
                        .build();
            NotificationManager _notification_manager = (NotificationManager) c.getSystemService(c.getApplicationContext().NOTIFICATION_SERVICE);
            _mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
            _notification_manager.notify(notification_id, _mNotification);
        } else {
            if (status)
                _mNotification = new Notification.Builder(c.getApplicationContext()).setContentTitle("Contexter")
                        .setContentText(mess_1).setSmallIcon(R.drawable.ic_launcher_mini).setContentIntent(pintent)
                        .addAction(R.drawable.button_custom, "See Detail", pintent)
                        .addAction(R.drawable.button_custom, "Cancel this", pintent)
                        .build();
            else
                _mNotification = new Notification.Builder(c.getApplicationContext()).setContentTitle("Contexter")
                        .setContentText(mess_1).setSmallIcon(R.drawable.ic_launcher_mini).setContentIntent(pintent)
                        .build();
            NotificationManager _notification_manager = (NotificationManager) c.getSystemService(c.getApplicationContext().NOTIFICATION_SERVICE);
            _mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
            _notification_manager.notify(notification_id, _mNotification);
        }
    }

    public void displayCustomizedToast(final Context _context_, String message, String message_title, final boolean vibrate) {
        LayoutInflater inflater = (LayoutInflater) _context_.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view_ = inflater.inflate(R.layout.dialog_one, null);
        TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);
        TextView tv_0_1_ = (TextView) view_.findViewById(R.id.tv_dialog_one_title);
        tv_0_0_.setText(message);
        tv_0_1_.setText(message_title);
        Handler _handler_ = new Handler(Looper.getMainLooper());
        if (!getTopActivity(_context_).contains("MainActivity"))
            _handler_.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = new Toast(_context_);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(view_);
                    toast.show();
                    if (vibrate) {
                        final Vibrator vibrator = (Vibrator) _context_.getSystemService(_context_.VIBRATOR_SERVICE);
                        vibrator.vibrate(50);
                    }
                }
            });
    }

    public String getTopActivity(Context c) {
        String className = "";
        ActivityManager am = (ActivityManager) c.getSystemService(c.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task_info = am.getRunningTasks(1);
        ComponentName componentName = task_info.get(0).topActivity;
        className = componentName.getClassName();
        return className;
    }
}
