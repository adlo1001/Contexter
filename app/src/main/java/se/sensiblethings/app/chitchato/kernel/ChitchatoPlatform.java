package se.sensiblethings.app.chitchato.kernel;

import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.disseminationcore.DisseminationCore;
import se.sensiblethings.interfacelayer.SensibleThingsListener;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public class ChitchatoPlatform implements SensibleThingsListener {

	SensibleThingsPlatform sensibleThingsPlatform = new SensibleThingsPlatform(
			this);
	protected Communication communication;
	protected DisseminationCore dessiminationCore;
	protected SensibleThingsNode sensiblethingsnode;
	private String response_message = "#none@nobody#";
	private String resolve_message = "#none#";
	

	public ChitchatoPlatform() {

		communication = sensibleThingsPlatform.getDisseminationCore()
				.getCommunication();
		dessiminationCore = sensibleThingsPlatform.getDisseminationCore();
		sensibleThingsPlatform.register("Addis_test@chitchato.com");
		sensibleThingsPlatform.resolve("Addis_test@chitchato.com");
		this.runCC();
		
	}

	public void runCC() {
		while (true) {
			try {
				System.out.println("Client to Local Bootstrap is now running");
		        sensibleThingsPlatform.resolve("Addis_test@chitchato.com");
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	
	@Override
	public void getResponse(final String uci, final String value,
			final SensibleThingsNode fromNode) {

		new Thread(new Runnable() {
			@Override
			public void run() {

				response_message = "[GetResponse] " + uci + " : " + value
						+ " : " + fromNode;
				System.out.println(response_message +"\n");
				getMessage();

			}
		});

	}

	@Override
	public void resolveResponse(final String uci, final SensibleThingsNode node) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				resolve_message = "\n[ResolveResponse] " + uci + ": "
						+ node.toString() + "\n";

				sensibleThingsPlatform.get(uci, node);
				
				System.out.println(resolve_message +"\n");
				getMessage_();

			}
		});

	}

	public void getEvent(SensibleThingsNode source, String uci) {
		// TODO Auto-generated method stub

		sensibleThingsPlatform.get(uci, source);
		sensibleThingsPlatform
				.notify(source, uci, "Here is message from Addis");

	}

	@Override
	public void setEvent(SensibleThingsNode fromNode, String uci, String value) {
		// Log.i("SET EVENT- IChat", value);
		sensibleThingsPlatform.set(uci, value, fromNode);
		// sensibleThingsPlatform.notifyAll();

	}

	public String getMessage() {
		return this.response_message;

	}

	public String getMessage_() {
		return this.resolve_message;

	}

	public SensibleThingsPlatform getSensibleThingsPlatform() {
		return sensibleThingsPlatform;
	}

	public void setSensibleThingsPlatform(
			SensibleThingsPlatform sensibleThingsPlatform) {
		this.sensibleThingsPlatform = sensibleThingsPlatform;
	}

}
