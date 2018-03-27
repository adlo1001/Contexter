package se.sensiblethings.app.chitchato.context;

public class RemoteContext implements MyContext {

	private String peerID;
	private String group;

	public RemoteContext(String id, String group) {

	}

	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocation(double latitude, double longitude) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLimunosity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAcce() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAcce(float x, float y, float z) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getOrientation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOrientation(float yaw, float pitch, float roll) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getPressure() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPressure(float p) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTemprature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTemprature(float temp) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTime(long time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLimunosity(float l) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSoundLevel() {
		// TODO Auto-generated method stub
		return null;
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
