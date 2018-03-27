package se.sensiblethings.sensoractuatorlayer;

public abstract class SensorGateway {

	protected SensorGatewayListener listener;
	
	public SensorGateway(SensorGatewayListener sensorGatewayListener) {		
		this.listener = sensorGatewayListener;
		
	
		
	}

}
