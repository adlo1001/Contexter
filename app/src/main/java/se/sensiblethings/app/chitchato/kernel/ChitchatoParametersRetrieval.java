package se.sensiblethings.app.chitchato.kernel;

public class ChitchatoParametersRetrieval {

	
	static ChitchatoPlatform chitchatoPlatform = new ChitchatoPlatform();
	
	public ChitchatoParametersRetrieval()
	{}
	
	public static ChitchatoPlatform getChitchatoPlatform()
	{
		return chitchatoPlatform;
	}
	
	

}
