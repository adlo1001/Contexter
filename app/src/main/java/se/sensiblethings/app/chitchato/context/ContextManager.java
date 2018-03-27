package se.sensiblethings.app.chitchato.context;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.activities.ErrorActivity;
import se.sensiblethings.app.chitchato.chitchato_services.FetchAddressIntentService;
import se.sensiblethings.app.chitchato.extras.Constants;

public class ContextManager implements LocationListener, SensorEventListener,
        ConnectionCallbacks, OnConnectionFailedListener {
    public SoundPressureLevel spl;
    protected Location mLastLocation;
    SensorManager sm;
    UserContext userContext;
    Context context_;
    Sensor sensor_;
    BKGroundTask_1 backgroundtask_1;
    // SensorEventListener sensor_l = this;
    SensorManager mSensorManager_;
    private double long_ = 0, lat_ = 0;
    private String _mlocation_address = "Address not found";
    private float acc_x = 0, acc_y = 0, acc_z = 0;
    private float orient_x = 0, orient_y = 0;
    private float light = 0;
    private float press_ = 0;
    private float temp = 0;
    private float sound_level = 0.0f;
    private long time;
    private GoogleApiClient mGoogleApiClient;
    private AddressResultReceiver mResultReceiver;


    public ContextManager(Context c, String id, SensorManager mSensorManager) {
        this.context_ = c;
        userContext = new UserContext(id);
        mSensorManager_ = mSensorManager;
        spl = new SoundPressureLevel(id);

        mGoogleApiClient = new GoogleApiClient.Builder(context_)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        //mResultReceiver = new AddressResultReceiver(null);
        mResultReceiver = new AddressResultReceiver(null);
        BKGroundTask_0 backgroundtask = new BKGroundTask_0();
        backgroundtask.execute();

        backgroundtask_1 = new BKGroundTask_1();
        // backgroundtask_1.execute();

        spl.FLAG = false;
        spl.startListening();

    }

    @Override
    public void onLocationChanged(Location arg0) {
        if (arg0 != null) {
            // lat_ = arg0.getLatitude();
            // long_ = arg0.getLongitude();
            // this.time = arg0.getTime();
            userContext.setLocation(this.long_, this.lat_);

        } else {
            Log.e("Location", "no values");
        }

    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensor = event.sensor.getType();
        float[] allValues = new float[9];
        float[] values = event.values;
        this.sensor_ = event.sensor;

        synchronized (this) {
            switch (sensor) {
                case Sensor.TYPE_ACCELEROMETER:
                    this.acc_x = values[0];
                    this.acc_y = values[1];
                    this.acc_z = values[2];


                    // Log.i("Acceleration:", acc_x + "" + acc_y);
                    userContext.setAcce(values[0], values[1], values[2]);

                    break;
                case Sensor.TYPE_ORIENTATION:
                    this.orient_x = values[0];
                    this.orient_y = values[1];

                    break;
                case Sensor.TYPE_PRESSURE:
                    this.press_ = values[0];
                    userContext.setPressure(values[0]);

                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    this.temp = values[0];
                    userContext.setTemprature(temp);
                    break;
                case Sensor.TYPE_LIGHT:
                    this.light = values[0];
                    userContext.setLimunosity(values[0]);
                    break;

            }

        }

    }

    private Context getBaseContext() {
        // TODO Auto-generated method stub
        return null;
    }

    public void updateContextData() {

        Float temp_sound_level = 0.0f;
        userContext.setTime(time);
        userContext.setTemprature(this.temp);
        userContext.setLocation(this.long_, this.lat_);
        userContext.setAddress(this._mlocation_address);
        userContext.setSoundLevel(this.sound_level);
        userContext.setPressure(this.press_);
        userContext.setOrientation(this.orient_x, this.orient_y, 0.0f);
        userContext.setAcce(acc_x, acc_y, 0.0f);
        temp_sound_level = Float.parseFloat(spl.getSoundDb() + "");
        userContext.setSoundLevel(temp_sound_level);
        //if (mLastLocation != null)
          //  getNodeAddress();
        userContext.setAddress(_mlocation_address);
        // System.out.println("Address " + _mlocation_address);

        if (isInternetAvailable()) if (this._mlocation_address.startsWith("Oops...! No Internet"))
            this._mlocation_address = "Problem fetching Location. \n Check Settings ";

    }

    public UserContext getUserContext() {
        return userContext;
    }

    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub
        System.out.println("GoogleAPiClient Connection Failed:" + arg0.toString());
        if (isInternetAvailable())
            displayCustomizedToast(context_.getApplicationContext(), "Loction not Found: \n " + arg0.toString());
        else
            displayCustomizedToast(context_.getApplicationContext(), this._mlocation_address);

    }

    @Override
    public void onConnected(Bundle arg0) {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        System.out
                .println("::: >>>>>>>>>>>>>>>> Google Api Client is  Connected");

        if (mLastLocation != null) {
            System.out
                    .println("::: >>>>>>>>>>>>>>>> Google Api Client is Connected");
            if (Geocoder.isPresent()) {
                Toast.makeText(context_, "Geocoder Available",
                        Toast.LENGTH_SHORT).show();

                this.lat_ = mLastLocation.getLatitude();
                this.long_ = mLastLocation.getLongitude();
                userContext.setLocation(this.long_, this.lat_);
                getNodeAddress();
                userContext.setAddress(_mlocation_address);

                return;
            }
            startIntentService();
        }

    }

    protected void startIntentService() {

        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            System.out
                    .println("Intent Service: >>>>>>>>>>>>>>>> Google Api Intent Service II");
            Intent intent = new Intent(this.context_,
                    FetchAddressIntentService.class);
            intent.putExtra(Constants.RECEIVER, mResultReceiver);
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

            System.out.println("Location:" + mLastLocation.getLatitude()
                    + "~~~" + mLastLocation.getLongitude());

            context_.startService(intent);
        } else {
            System.out
                    .println("Address: >>>>>>>>>>>>>>>> Google Api Client is not Connected");

        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

    }

    protected String getNodeAddress() {

        String _mAddress = "Oops...! No Internet. No Address";
        if (isInternetAvailable()) {
            _mAddress = "Check Location in the Settings";
        }

        // TODO Auto-generated method stub
        Geocoder geocoder = new Geocoder(this.context_, Locale.getDefault());
        String errorMessage = "";
        List<Address> addresses = null;

        if (this.isInternetAvailable()) {
            try {

                addresses = geocoder.getFromLocation(
                        mLastLocation.getLatitude(),
                        mLastLocation.getLongitude(), 1);
            } catch (IOException ioException) {
                errorMessage = context_
                        .getString(R.string.service_not_available);
                Log.e(Constants.TAG, errorMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                errorMessage = context_
                        .getString(R.string.invalid_lat_long_used);
                Log.e(Constants.TAG, errorMessage + "." + "Latitude= "
                                + mLastLocation.getLatitude() + ", Longitude = "
                                + mLastLocation.getLongitude(),
                        illegalArgumentException);
            }

            if (addresses == null || addresses.size() == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = context_
                            .getString(R.string.no_address_found);
                    Log.e(Constants.TAG, errorMessage);
                }

            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
                _mAddress = TextUtils.join(
                        System.getProperty("line.separator"), addressFragments);

              Log.i(Constants.TAG, context_.getString(R.string.address_found));

            }
        }

        _mlocation_address = _mAddress;
        return _mlocation_address;
    }

    // Check if the Internet is Available
    public boolean isInternetAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context_
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    public class BKGroundTask_0 extends AsyncTask<String, String, String> {
        boolean flag = false;

        @Override
        protected String doInBackground(String... arg0) {
            this.flag = false;
            // ArrayList<String> temp_ar = new ArrayList<String>();
            try {
                mSensorManager_
                        .registerListener(
                                ContextManager.this,
                                mSensorManager_
                                        .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                                SensorManager.SENSOR_DELAY_GAME);
                mSensorManager_.registerListener(ContextManager.this,
                        mSensorManager_
                                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_GAME);
                mSensorManager_.registerListener(ContextManager.this,
                        mSensorManager_
                                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                        SensorManager.SENSOR_DELAY_GAME);
                mSensorManager_.registerListener(ContextManager.this,
                        mSensorManager_.getDefaultSensor(Sensor.TYPE_LIGHT),
                        SensorManager.SENSOR_DELAY_GAME);
                mSensorManager_
                        .registerListener(
                                ContextManager.this,
                                mSensorManager_
                                        .getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE),
                                SensorManager.SENSOR_DELAY_GAME);
                //startIntentService();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Intent intent = new Intent(getBaseContext(),
                        ErrorActivity.class);
                intent.putExtra("error", "Data from Sensors Not Readable! ");
                context_.startActivity(intent);
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

    public class BKGroundTask_1 extends AsyncTask<String, String, String> {

        boolean flag = false;

        @Override
        protected String doInBackground(String... arg0) {
            this.flag = false;
            // ArrayList<String> temp_ar = new ArrayList<String>();
            try {

                getNodeAddress();
                userContext.setAddress(_mlocation_address);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);

        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String mAddressOutput = resultData
                    .getString(Constants.RESULT_DATA_KEY);

            System.out.println("Address: >>>>>>>>>>>>>>>>" + mAddressOutput);
            System.out.println(resultData.get(Constants.RESULT_DATA_KEY));
            Toast.makeText(context_, "Address: >>>" + mAddressOutput,
                    Toast.LENGTH_LONG).show();

        }
    }


}
