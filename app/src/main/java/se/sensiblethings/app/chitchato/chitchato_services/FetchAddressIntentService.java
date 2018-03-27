package se.sensiblethings.app.chitchato.chitchato_services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import se.sensiblethings.app.R;
import se.sensiblethings.app.chitchato.extras.Constants;

public class FetchAddressIntentService extends IntentService {

	protected ResultReceiver mReceiver;

	public FetchAddressIntentService() {
		super("Chitchat Circle Location Service");
		mReceiver = new AddressResultReceiver(null);
	}

	public FetchAddressIntentService(String name) {
		super(name);

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		String errorMessage = "";
		Location location = intent
				.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

		List<Address> addresses = null;

		try {

			addresses = geocoder.getFromLocation(location.getLatitude(),
					location.getLongitude(), 1);
		} catch (IOException ioException) {
			errorMessage = getString(R.string.service_not_available);
			Log.e(Constants.TAG, errorMessage, ioException);
		} catch (IllegalArgumentException illegalArgumentException) {
			errorMessage = getString(R.string.invalid_lat_long_used);
			Log.e(Constants.TAG,
					errorMessage + "." + "Latitude= " + location.getLatitude()
							+ ", Longitude = " + location.getLongitude(),
					illegalArgumentException);
		}

		if (addresses == null || addresses.size() == 0) {
			if (errorMessage.isEmpty()) {
				errorMessage = getString(R.string.no_address_found);
				Log.e(Constants.TAG, errorMessage);
			}
			deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);

		} else {
			Address address = addresses.get(0);
			ArrayList<String> addressFragments = new ArrayList<String>();

			for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
				addressFragments.add(address.getAddressLine(i));
			}
			//Log.i(Constants.TAG, getString(R.string.address_found));
			deliverResultToReceiver(Constants.SUCCESS_RESULT, TextUtils.join(
					System.getProperty("line.separator"), addressFragments));
		}

	}

	private void deliverResultToReceiver(int resultCode, String message) {
		Bundle bundle = new Bundle();
		bundle.putString(Constants.RESULT_DATA_KEY, message);
		mReceiver.send(resultCode, bundle);
	}

	class AddressResultReceiver extends ResultReceiver {

		public AddressResultReceiver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			// String mAddressOutput = resultData
			// .getString(Constants.RESULT_DATA_KEY);
			// System.out.println(resultData.get(Constants.RESULT_DATA_KEY));

		}
	}

}
