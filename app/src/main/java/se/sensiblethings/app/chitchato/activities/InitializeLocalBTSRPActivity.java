package se.sensiblethings.app.chitchato.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.chitchato_services.PlatformManagerBootstrap;
import se.sensiblethings.app.chitchato.extras.Customizations;
import se.sensiblethings.app.chitchato.extras.WifiApiManager;
import se.sensiblethings.disseminationlayer.communication.Message;

public class InitializeLocalBTSRPActivity extends Activity {

    TextView tv, tv_note, tv_bootstraper, tv_staller;
    EditText edt;
    LinearLayout ll;
    long l;
    String temp;
    ArrayList activenodes;
    String[] a;
    Message message;
    String nodes = "%";
    ListView lv_1, lv_2;
    GridView gv_1;
    private boolean About_VISISTED = false;
    private Customizations custom;
    Animation animation;

    public static String PREFERRED_IP = "0.0.0.0";
    boolean _BOOTSTRAP_IP_ = false;
    SharedPreferences mPrefs;
    Set<String> bootstraps;
    protected TreeMap bs_map = new TreeMap<String, String>();
    String SSID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mPrefs = getSharedPreferences("bootstraps", 0);
        bootstraps = mPrefs.getStringSet("bootstraps", bs_map.keySet());
        _BOOTSTRAP_IP_ = mPrefs.getBoolean("BOOTSTRAP_IP", false);
        PREFERRED_IP = mPrefs.getString("PREFERRED_IP", PREFERRED_IP);
        custom = new Customizations(this, -1);

        custom = new Customizations(this, -1);
        setContentView(custom.getGalleryScreen());
        // Keep the orientation portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle("");

        tv = (TextView) findViewById(R.id.tv_empty_bar_aboutscreen);
        edt = (EditText) findViewById(R.id.edt_box);
        tv_bootstraper = (TextView) findViewById(R.id.choices_btn);
        ll = (LinearLayout) findViewById(R.id.ll_staller_dialog);
        tv = (TextView) findViewById(R.id.tv_empty_bar_aboutscreen);
        tv_staller = (TextView) findViewById(R.id.tv_staller_dialog);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/COMIC.TTF");

        Typeface tf_pala = Typeface.createFromAsset(getAssets(),
                "fonts/pala.ttf");

        tv.setTypeface(tf);
        edt.setTypeface(tf_pala);
        tv_bootstraper.setTypeface(tf);

        animation = AnimationUtils.loadAnimation(InitializeLocalBTSRPActivity.this, R.anim.move_tv_to_right_side);

        final WifiApiManager wifiApManager = new WifiApiManager(this);

        Random random = new Random();
        SSID = "~0btstrp" + random.nextInt(100);

        Iterator itr = bootstraps.iterator();
        Map.Entry me;
        String ip_ = "";
        while (itr.hasNext()) {
            ip_ = (String) itr.next();
            bs_map.put(ip_, ip_);
        }

        if (PlatformManagerBootstrap.ST_PLATFORM_IS_UP) {
            tv_bootstraper.setText(". . . \n");
            tv_bootstraper.append("STOP");

        }

        tv_bootstraper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (custom.IS_NODE_BOOTSTRAP_() || PlatformManagerBootstrap.ST_PLATFORM_IS_UP) {
                    {
                        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                            tv_bootstraper.setBackgroundResource(R.drawable.circlebtn_oregon_with_border_large_screen);
                        } else
                            tv_bootstraper.setBackgroundResource(R.drawable.circlebtn_oregon_with_border_large_screen);
                        ll.setVisibility(View.GONE);
                        if (tv_staller.getAnimation() != null)
                            tv_staller.clearAnimation();
                        //lv_1.setAdapter(list_adapter_1);
                        tv_bootstraper.setText("START");
                        //displayCustomizedToast(getApplicationContext(), "Local Bootstrap OFF: " + SSID);
                        //wifiApManager.setWifiApConfiguration(null);

                        if (!isInternetAvailable()) {
                            if (wifiApManager.isWifiApEnabled()) {
                                wifiApManager.setWifiEnable(null, false);
                                custom.setHotSpotStatus(false);

                            }
                        } else {
                            // nothing happens to the bootstrap-- just
                            custom.setHotSpotStatus(false);
                        }
                        MessageNotifier(null, "Local Bootstrap:> stoppped", "", "", true);
                        SharedPreferences mPrefs = getSharedPreferences("bootstraps", 0);
                        SharedPreferences.Editor ed = mPrefs.edit();
                        // ed.putStringSet("bootstraps", bootstraps);
                        // ed.putBoolean("BOOTSTRAP_IP", isPublicIP(PREFERRED_IP));
                        ed.putBoolean("_IS_NODE_BOOTSTRAP_", false);
                        // ed.putString("PREFERRED_IP", PREFERRED_IP);
                        ed.commit();
                        custom.set_IS_NODE_BOOTSTRAP_(false);
                        PlatformManagerBootstrap.STOP_SERVICE = true;
                        custom.setHotSpotStatus(false);
                    }
                } else {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            displayCustomizedToast(getApplicationContext(), "Something went wrong with connection.\n Trying Personal Hotspot\n");
                            wifiApManager.setWifiApConfiguration(null);
                            // wifiApManager.setSSID(SSID + "192#168#1#1");
                            InitializeLocalBTSRPActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ll.setVisibility(View.VISIBLE);
                                    tv_staller.setAnimation(animation);
                                }
                            });
                            int trial_seconds = 0;// Max-- five seconds
                            if (!isInternetAvailable())
                                do {
                                    if (wifiApManager.isWifiApEnabled()) {
                                        wifiApManager.setWifiEnable(null, false);
                                        custom.setHotSpotStatus(false);
                                    } else {
                                        try {
                                            if (!getIPAddress().equalsIgnoreCase("#Nothing#")) {
                                                SSID = SSID + "#" + getAdvBootstrapPattern(getIPAddress()) + "#0~";
                                                SSID = SSID.replace(".", "#");
                                                wifiApManager.setWifiApConfiguration(null);
                                                wifiApManager.setSSID(SSID);
                                                wifiApManager.setWifiEnable(null, true);
                                                custom.setHotSpotStatus(true);
                                                Thread.sleep(500);
                                                break;
                                            } else {
                                                wifiApManager.setWifiEnable(null, true);
                                                custom.setHotSpotStatus(true);
                                                Thread.sleep(500);
                                            }
                                            // give it 5sec to start
                                            trial_seconds = trial_seconds + 500;

                                            System.out.println("_________________________________________" + SSID);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                while (trial_seconds < 5000);
                            else {

                            }

                            if (getIPAddress().equalsIgnoreCase("#Nothing#")) {
                                //displayCustomizedToast(getApplicationContext(), "Unable to start bootstrap. \nCheck your connection and Try gain. \n");
                                InitializeLocalBTSRPActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (tv_staller.getAnimation() != null)
                                            tv_staller.clearAnimation();
                                    }
                                });
                            } else {
                                InitializeLocalBTSRPActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_LARGE) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                                            tv_bootstraper.setBackgroundResource(R.drawable.circlebtn_oregon_with_border_large_screen);
                                        } else
                                            tv_bootstraper.setBackgroundResource(R.drawable.circlebtn_oregon_with_border_large_screen);
                                        if (PlatformManagerBootstrap.ST_PLATFORM_IS_UP) {
                                            tv_bootstraper.setText(". . . \n");
                                            tv_bootstraper.append("STOP");

                                        }

                                        String temp_ip_address;
                                        temp_ip_address = getIPAddress();
                                        PREFERRED_IP = temp_ip_address;
                                        ll.setVisibility(View.VISIBLE);
                                        tv_staller.setAnimation(animation);
                                        if (temp_ip_address.trim().isEmpty() && bootstraps.size() > 0) {
                                            bs_map.remove(PREFERRED_IP);
                                            bs_map.put(PREFERRED_IP, PREFERRED_IP);
                                        } else {
                                            bs_map.put(temp_ip_address, temp_ip_address);
                                            PREFERRED_IP = temp_ip_address;
                                        }

                                        //update bootstrap ip
                                        SharedPreferences mPrefs = getSharedPreferences("bootstraps", 0);
                                        SharedPreferences.Editor ed = mPrefs.edit();
                                        ed.putStringSet("bootstraps", bootstraps);
                                        ed.putBoolean("BOOTSTRAP_IP", isPublicIP(PREFERRED_IP));
                                        ed.putBoolean("_IS_NODE_BOOTSTRAP_", true);
                                        ed.putString("PREFERRED_IP", PREFERRED_IP);
                                        ed.commit();
                                        custom.set_IS_NODE_BOOTSTRAP_(true);
                                        PlatformManagerBootstrap.STOP_SERVICE = false;

                                    }
                                });
                                displayCustomizedToast(getApplicationContext(), "Local Bootstrap running: \n + Bootstrap advertisement on.\n Name" + SSID + "\nIP:" + getAdvBootstrapPattern(getIPAddress()));
                                MessageNotifier(null, "Local Bootstrap running:>" + getAdvBootstrapPattern(getIPAddress()), "", "", true);
                            }

                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();


                }

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
                intent = new Intent(InitializeLocalBTSRPActivity.this, ChoicesActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_advanced:

                intent = new Intent(InitializeLocalBTSRPActivity.this, PurgeActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sensor_en:

                intent = new Intent(InitializeLocalBTSRPActivity.this,
                        SensorReadingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void MessageNotifier(View view, String mess_1, String mess_2, String mess_3, boolean status) {
        Intent intent = new Intent(getApplicationContext(),
                GroupContainerActivity.class);
        intent.putExtra("error", "Notifier");
        intent.putExtra("INDEX", "NOTIFICATIONS");
        intent.putExtra("ST_STATUS", status);
        PendingIntent pintent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
        Notification _mNotification = null;
        if (status)
            _mNotification = new Notification.Builder(getApplicationContext()).setContentTitle("Contexter")
                    .setContentText(mess_1).setSmallIcon(R.drawable.ic_launcher_mini).setContentIntent(pintent)
                    .addAction(R.drawable.button_custom, "See Detail", pintent)
                    .addAction(R.drawable.button_custom, "Cancel this", pintent)
                    .build();
        else
            _mNotification = new Notification.Builder(getApplicationContext()).setContentTitle("Contexter")
                    .setContentText(mess_1).setSmallIcon(R.drawable.ic_launcher_mini).setContentIntent(pintent)
                    .build();


        NotificationManager _notification_manager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        _mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        //id -3 -- group advertisment status
        _notification_manager.notify(3, _mNotification);
        //   _notification_manager.cancel(0);

    }

    public void displayCustomizedToast(final Context _context_, String message) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view_ = inflater.inflate(R.layout.dialog_one, null);
        TextView tv_0_0_ = (TextView) view_.findViewById(R.id.btn_dialog_one_enter);
        tv_0_0_.setText(message);
        android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
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

    String local_ip_ = "#Nothing#";

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

    public boolean isPublicIP(String ip_) {
        Matcher matcher_public_ip;
        final String ip_address_pattern_public = "^([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(?<!172\\." +
                "(16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31))(?<!127)(?<!^10)(?<!^0)\\.([0-9]|[1-9]" +
                "[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(?<!192\\.168)(?<!172\\.(16|17|18|19|20|21|22|23|24|25" +
                "|26|27|28|29|30|31))\\.([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9]" +
                "{2}|2[0-4][0-9]|25[0-5])(?<!\\.0$)(?<!\\.255$)$";

        Pattern pattern_public = Pattern.compile(ip_address_pattern_public);
        matcher_public_ip = pattern_public.matcher(ip_);
        if (matcher_public_ip.matches())
            return true;
        else
            return false;

    }


    // Check if the Internet is Available
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static String getAdvBootstrapPattern(String _ip_) {
        String pattern = "";

        String ip_address_pattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern pt = Pattern.compile(ip_address_pattern);
        Matcher matcher = pt.matcher(_ip_);
        System.out.println("_______________________________________ip_" + _ip_);
        if (matcher.matches()) {
            String tmp = _ip_.replace(".", ":");
            String[] tmp_00 = tmp.split(":");
            String tmp_0001 = tmp_00[0];
            String tmp_0002 = tmp_00[1];
            String tmp_0003 = tmp_00[2];
            String tmp_0004 = tmp_00[3];

            tmp_0001 = tmp_0001.replace("0", "z").replace("1", "q").replace("2", "r").replace("3", "s").
                    replace("4", "t").replace("5", "u").replace("6", "v").replace("7", "w").replace("8", "x").
                    replace("9", "y");
            tmp_0002 = tmp_0002.replace("0", "z").replace("1", "q").replace("2", "r").replace("3", "s").
                    replace("4", "t").replace("5", "u").replace("6", "v").replace("7", "w").replace("8", "x").
                    replace("9", "y");
            tmp_0003 = tmp_0003.replace("0", "z").replace("1", "q").replace("2", "r").replace("3", "s").
                    replace("4", "t").replace("5", "u").replace("6", "v").replace("7", "w").replace("8", "x").
                    replace("9", "y");
            tmp_0004 = tmp_0004.replace("0", "z").replace("1", "q").replace("2", "r").replace("3", "s").
                    replace("4", "t").replace("5", "u").replace("6", "v").replace("7", "w").replace("8", "x").
                    replace("9", "y");
            if (tmp_0001.length() == 1) {
                tmp_0001 = "zz" + tmp_0001;
            } else if (tmp_0001.length() == 2) {
                tmp_0001 = "z" + tmp_0001;
            }
            if (tmp_0002.length() == 1) {
                tmp_0002 = "zz" + tmp_0002;
            } else if (tmp_0002.length() == 2) {
                tmp_0002 = "z" + tmp_0002;
            }
            if (tmp_0003.length() == 1) {
                tmp_0003 = "zz" + tmp_0003;
            } else if (tmp_0003.length() == 2) {
                tmp_0003 = "z" + tmp_0003;
            }
            if (tmp_0004.length() == 1) {
                tmp_0004 = "zz" + tmp_0004;
            } else if (tmp_0004.length() == 2) {
                tmp_0004 = "z" + tmp_0004;
            }


            pattern = tmp_0001 + tmp_0002 + tmp_0003 + tmp_0004;
            System.out.println(_ip_ + "_________________________________________ADVIP" + pattern);
            return pattern;
        } else
            return _ip_;
    }

    public static String getIPfromPattern(String pt) {
        String ip = "";


        String tmp_0001 = pt.substring(0, 3);
        String tmp_0002 = pt.substring(3, 6);
        String tmp_0003 = pt.substring(6, 9);
        String tmp_0004 = pt.substring(9, 12);


        tmp_0001 = tmp_0001.replace("z", "0").replace("q", "1").replace("r", "2").replace("s", "3").
                replace("t", "4").replace("u", "5").replace("v", "6").replace("w", "7").replace("x", "8").
                replace("y", "9");
        tmp_0002 = tmp_0002.replace("z", "0").replace("q", "1").replace("r", "2").replace("s", "3").
                replace("t", "4").replace("u", "5").replace("v", "6").replace("w", "7").replace("x", "8").
                replace("y", "9");
        tmp_0003 = tmp_0003.replace("z", "0").replace("q", "1").replace("r", "2").replace("s", "3").
                replace("t", "4").replace("u", "5").replace("v", "6").replace("w", "7").replace("x", "8").
                replace("y", "9");
        tmp_0004 = tmp_0004.replace("z", "0").replace("q", "1").replace("r", "2").replace("s", "3").
                replace("t", "4").replace("u", "5").replace("v", "6").replace("w", "7").replace("x", "8").
                replace("y", "9");

        int tmp_0001_ = Integer.valueOf(tmp_0001);
        int tmp_0002_ = Integer.valueOf(tmp_0002);
        int tmp_0003_ = Integer.valueOf(tmp_0003);
        int tmp_0004_ = Integer.valueOf(tmp_0004);
        ip = tmp_0001_ + "." + tmp_0002_ + "." + tmp_0003_ + "." + tmp_0004_;
        return ip;


    }

}
