package se.sensiblethings.disseminationlayer.lookupservice;

public interface LookupServiceStateListener {
	public void onNewLookupServiceState(LookupService service, String newState);
}
