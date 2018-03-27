package se.sensiblethings.app.chitchato.extras;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by user on 11/20/2016.
 */
public class WifiApiManager {
    private Context context;
    private WifiManager _mWifiManager;

    public WifiApiManager(Context _cont_) {
        this.context = _cont_;
        _mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

    }

    public boolean setWifiEnable(WifiConfiguration wifi_config, boolean enabled) {
        try {
            if (enabled) _mWifiManager.setWifiEnabled(false);
            Method method = _mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            return (Boolean) method.invoke(_mWifiManager, wifi_config, enabled);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setSSID(String _ssid_name_) {
        try {
            Method getConfigMethod = _mWifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(_mWifiManager);
            wifiConfig.SSID = _ssid_name_;

            Method setConfigMethod = _mWifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            setConfigMethod.invoke(_mWifiManager, wifiConfig);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public WifiConfiguration getWifiApConfiguration() {
        try {
            Method method = _mWifiManager.getClass().getMethod("getWifiApConfiguration");
            return (WifiConfiguration) method.invoke(_mWifiManager);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return null;
        }
    }

    public boolean setWifiApConfiguration(WifiConfiguration wifiConfig) {
        try {
            Method method = _mWifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            wifiConfig.SSID = "cntxter%%%%%conxter";
            return (Boolean) method.invoke(_mWifiManager, wifiConfig);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return false;
        }
    }


    public WIFIAPISTATE getWifiApState() {
        try {
            Method method = _mWifiManager.getClass().getMethod("getWifiApState");

            int tmp = ((Integer) method.invoke(_mWifiManager));
            if (tmp >= 10) {
                tmp = tmp - 10;
            }

            return WIFIAPISTATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            return WIFIAPISTATE.WIFI_AP_STATE_FAILED;
        }
    }


    public boolean isWifiApEnabled() {
        return getWifiApState() == WIFIAPISTATE.WIFI_AP_STATE_ENABLED;
    }


    public enum WIFIAPISTATE {
        WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING, WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
    }

    public WifiManager get_mWifiManager() {
        return _mWifiManager;
    }

    public void set_mWifiManager(WifiManager _mWifiManager) {
        this._mWifiManager = _mWifiManager;
    }
}
