package se.sensiblethings.app.chitchato.context;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

public class LocalContext implements MyContext {
	ContextManager context_Manager;
	Context context_;

	public LocalContext(Context c, String id, SensorManager mSensorManager) {
		this.context_ = c;
		context_Manager = new ContextManager(c, id, mSensorManager);
		// UpdateContextInfo update_context_info = new UpdateContextInfo();
		// update_context_info.start();

	}

	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return context_Manager.getUserContext().getLocation();
	}

	@Override
	public void setLocation(double latitude, double longitude) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLimunosity() {
		// TODO Auto-generated method stub
		return context_Manager.getUserContext().getLimunosity();
	}

	@Override
	public String getAcce() {
		// TODO Auto-generated method stub
		return context_Manager.getUserContext().getAcce();
	}

	@Override
	public void setAcce(float x, float y, float z) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getOrientation() {
		// TODO Auto-generated method stub
		return context_Manager.getUserContext().getOrientation();
	}

	@Override
	public void setOrientation(float yaw, float pitch, float roll) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getPressure() {
		// TODO Auto-generated method stub
		return context_Manager.getUserContext().getPressure();
	}

	@Override
	public void setPressure(float p) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTemprature() {
		// TODO Auto-generated method stub
		return context_Manager.getUserContext().getTemprature();
	}

	@Override
	public void setTemprature(float temp) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getTime() {
		// TODO Auto-generated method stub
		return context_Manager.getUserContext().getTime();
	}

	@Override
	public void setTime(long time) {
		// TODO Auto-generated method stub

	}

	public class UpdateContextInfo extends Thread {
		public UpdateContextInfo() {

		}

		public UpdateContextInfo(ArrayList<String> array_list) {

		}

		@Override
		public void run() {
			Log.v("Context--->", "Thread");

			while (true) {

				try {

					context_Manager.updateContextData();

					this.sleep(6000);

				} catch (Exception e) {
					// Log.v("Main", "Thread Interepted!");
					e.printStackTrace();
					return;
				}

			}
		}
	}

	public void pause() {
		// context_Manager.disconnect();
	}

	@Override
	public void setLimunosity(float l) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSoundLevel() {
		// TODO Auto-generated method stub
		return context_Manager.getUserContext().getSoundLevel();
	}

	@Override
	public void setSoundLevel(float l) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAddress(String address) {
		// TODO Auto-generated method stub
		
	}

}
