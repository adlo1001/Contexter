package se.sensiblethings.app.chitchato.context;

public interface MyContext {

	//
	public long getTime();

	public void setTime(long time);

	
	public String getAddress();
	public void setAddress (String address);
	//
	public String getLocation();

	public void setLocation(double latitude, double longitude);

	//
	public String getLimunosity();

	public void setLimunosity(float l);

	//
	public String getAcce();

	public void setAcce(float x, float y, float z);

	//
	public String getSoundLevel();

	public void setSoundLevel(float l);

	//

	public String getOrientation();

	public void setOrientation(float yaw, float pitch, float roll);

	//

	public String getPressure();

	public void setPressure(float p);

	// public proximity//

	public String getTemprature();

	public void setTemprature(float temp);

}
