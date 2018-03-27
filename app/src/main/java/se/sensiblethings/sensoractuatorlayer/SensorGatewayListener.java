package se.sensiblethings.sensoractuatorlayer;

public interface SensorGatewayListener {
	
	public String getEvent(String uci);
	
	public void setEvent(String uci, String value);	
	

}
