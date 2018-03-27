/*
 * Copyright 2013 The SensibleThings Consortium
 * This file is part of The SensibleThings Platform.
 *
 * The SensibleThings Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The SensibleThings Platform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with The SensibleThings Platform.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.sensiblethings.disseminationlayer.disseminationcore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.CommunicationStateListener;
import se.sensiblethings.disseminationlayer.communication.DestinationNotReachableException;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.disseminationlayer.lookupservice.LookupService;
import se.sensiblethings.disseminationlayer.lookupservice.LookupServiceStateListener;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public class DisseminationCore implements MessageListener, LookupServiceStateListener, CommunicationStateListener{

	public final static String DISSEMINATION_CORE_STATE_CONNECTED = "DISSEMINATION_CORE_ONLINE";
	public final static String DISSEMINATION_CORE_STATE_UNDEFINED = "DISSEMINATION_CORE_UNDEFINED";
	public final static String DISSEMINATION_CORE_STATE_DISCONNECTED = "DISSEMINATION_CORE_OFFLINE";

	//Parent platform
	private SensibleThingsPlatform platform = null;
	private ExecutorService executorPool;
	private String currentState = DISSEMINATION_CORE_STATE_UNDEFINED;
	//Interfaces
	private LookupService lookupService = null;
	private Communication communication = null;

	//Response Listeners
	private ResolveResponseListener resolveResponseListener = null;
	private GetResponseListener getResponseListener = null;
	private BinaryGetResponseListener binaryGetResponseListener = null;

	//Event Listeners
	private SetEventListener setEventListener = null;
	private BinarySetEventListener binarySetEventListener = null;
	private GetEventListener getEventListener = null;

	//StateListeners
	private Set<DisseminationCoreStateListener> sensibleStateListeners = new HashSet<DisseminationCoreStateListener>();

	//Negative feedback listeners
	private Set<NegativeResolveResponseListener> negativeResolveResponseListener = new HashSet<NegativeResolveResponseListener>();

	//RegisterListener
	private Set<RegisterResponseListener> registerResponseListener = new HashSet<RegisterResponseListener>();

	/**
	 * Creates the Dissemination core, takes the parent platform as argument
	 *
	 * @param platform the parent platform
	 */
	public DisseminationCore(SensibleThingsPlatform platform) {
		this.platform = platform;
		executorPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//		executorPool = Executors.newCachedThreadPool();


	}

	/**
	 * Makes so that the dissemination core uses a specified lookup service
	 *
	 * @param lookupService the lookup service to be used
	 */
	public void useLookupService(LookupService lookupService){
		this.lookupService = lookupService;
		lookupService.addStateListener(this);
	}

	/**
	 * Makes so that the dissemination core uses a specified communication type
	 *
	 * @param communication the communication type to be used
	 */
	public void useCommunication(Communication communication){
		this.communication = communication;
		communication.setExecutor(executorPool);
		communication.addStateListener(this);
		communication.registerMessageListener(GetMessage.class.getName(), this);
		communication.registerMessageListener(SetMessage.class.getName(), this);
		communication.registerMessageListener(NotifyMessage.class.getName(), this);
	}

	/**
	 * Check if both lookup service and communication is running
	 *
	 * @return true if everything is initialized
	 */
	public boolean isInitalized(){

		if(lookupService != null && communication != null){
			if(lookupService.getState().equals(LookupService.STATE_CONNECTED)
					&& communication.getState().equals(Communication.COMMUNICATION_STATE_CONNECTED)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Closes down the communication and the lookup service
	 *
	 */
	public void shutdown(){
		try {
			lookupService.removeStateListener(this);

			lookupService.addStateListener(new LookupServiceStateListener() {

				public void onNewLookupServiceState(LookupService service, String newState) {
					if(newState.equals(LookupService.STATE_DISCONNECTED)){

						communication.shutdown();
					}

				}
			});
			communication.addStateListener(new CommunicationStateListener() {

				public void onNewCommunicationState(Communication communication,
													String state) {
					if(state.equals(Communication.COMMUNICATION_STATE_DISCONNECTED)){
						communication.shutdownPostOffice();
						executorPool.shutdownNow();
					}
				}
			});
			lookupService.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the parent SensibleThingsPlatform
	 *
	 * @return the parent platform
	 */
	public SensibleThingsPlatform getSensibleThingsPlatform(){
		return platform;
	}

	/**
	 * Returns the communication which currently is in use
	 *
	 * @return the communication in use
	 */
	public Communication getCommunication(){
		return communication;
	}

	/**
	 * Returns the lookup service which currently is in use
	 *
	 * @return the lookup service in use
	 */
	public LookupService getLookupService(){
		return lookupService;
	}


	//Listener interfaces towards the user application
	/**
	 * Sets the ResolveResponseListener
	 * @param listener ResolveResponseListener
	 */
	public void setResolveResponseListener(ResolveResponseListener listener){
		this.resolveResponseListener = listener;
	}
	/**
	 * Adds a NegativeResolveResponseListener
	 * @param listener ResolveResponseListener
	 */
	public void addNegativeResolveResponseListener(NegativeResolveResponseListener listener){
		this.negativeResolveResponseListener.add(listener);
	}
	/**
	 * Adds a NegativeResolveResponseListener
	 * @param listener ResolveResponseListener
	 */
	public void removeNegativeResolveResponseListener(NegativeResolveResponseListener listener){
		this.negativeResolveResponseListener.remove(listener);
	}
	/**
	 * Sets the GetResponseListener
	 * @param listener GetResponseListener
	 */
	public void setGetResponseListener(GetResponseListener listener){
		this.getResponseListener = listener;
	}
	/**
	 * Sets the GetBinaryResponseListener
	 * If set, will trigger in addition to GetResponseListener
	 * @param listener GetResponseListener
	 */
	public void setBinaryGetResponseListener(BinaryGetResponseListener listener){
		this.binaryGetResponseListener = listener;
	}
	/**
	 * Sets the SetEventListener
	 * @param listener SetEventListener
	 */
	public void setSetEventListener(SetEventListener listener){
		this.setEventListener = listener;
	}
	/**
	 * Sets the BinarySetEventListener
	 * If set, will trigger in addition to SetEventListener
	 * @param listener SetEventListener
	 */
	public void setBinarySetEventListener(BinarySetEventListener listener){
		this.binarySetEventListener = listener;
	}
	/**
	 * Sets the GetEventListener
	 * @param listener GetEventListener
	 */
	public void setGetEventListener(GetEventListener listener){
		this.getEventListener = listener;
	}

	/**
	 * Gets the ResolveResponseListener
	 * @return the ResolveResponseListener in use
	 */
	public ResolveResponseListener getResolveResponseListener(){
		return resolveResponseListener;
	}
	/**
	 * Gets the GetResponseListener
	 * @return the GetResponseListener in use
	 */
	public GetResponseListener getGetResponseListener(){
		return getResponseListener;
	}
	/**
	 * Gets the BinaryGetResponseListener
	 * @return the GetResponseListener in use
	 */
	public BinaryGetResponseListener getBinaryGetResponseListener(){
		return binaryGetResponseListener;
	}
	/**
	 * Gets the SetEventListener
	 * @return the SetEventListener in use
	 */
	public SetEventListener getSetEventListener(){
		return setEventListener;
	}
	/**
	 * Gets the BinarySetEventListener
	 * @return the SetEventListener in use
	 */
	public BinarySetEventListener getBinarySetEventListener(){
		return binarySetEventListener;
	}
	/**
	 * Gets the GetEventListener
	 * @return the GetEventListener in use
	 */
	public GetEventListener getGetEventListener(){
		return getEventListener;
	}

	/**
	 * Adds a register response listener
	 * @param listener
	 */
	public void addRegisterResponseListener(RegisterResponseListener listener){
		registerResponseListener.add(listener);
	}
	/**
	 * Removes a register response listener
	 * @param listener
	 */
	public void removeRegisterResponseListener(RegisterResponseListener listener){
		registerResponseListener.remove(listener);
	}



	//Create events by calling these
	/**
	 * Used to call the listener and create a callback
	 *
	 * @param uci the UCI which have been resolved
	 * @param node the SensibleThingsNode which the UCI has been resolved to
	 */
	public void callResolveResponseListener(String uci, SensibleThingsNode node){
		if(resolveResponseListener != null){
			resolveResponseListener.resolveResponse(uci, node);
		}
	}
	/**
	 * Used to call the listener and create a callback
	 *
	 * @param uci the UCI which have been fetched
	 * @param value the value of the UCI
	 */
	public void callGetResponseListener(String uci, byte[] value, SensibleThingsNode fromNode){
		if(getResponseListener != null){
			String stringValue = new String(value);
			getResponseListener.getResponse(uci, stringValue, fromNode);
		}
	}
	/**
	 * Used to call the listener and create a callback
	 *
	 * @param uci the UCI which have been fetched
	 * @param value the value of the UCI
	 */
	public void callBinaryGetResponseListener(String uci, byte[] value, SensibleThingsNode fromNode){
		if(binaryGetResponseListener != null){
			binaryGetResponseListener.getResponse(uci, value, fromNode);
		}
	}
	/**
	 * Used to call the listener and create a callback
	 *
	 * @param uci the UCI which is being set
	 * @param value the value which the UCI should be set to
	 */
	public void callSetEventListener(SensibleThingsNode fromNode, String uci, byte[] value){
		if(setEventListener != null){
			String stringValue = new String(value);
			setEventListener.setEvent(fromNode, uci, stringValue);
		}
	}
	/**
	 * Used to call the listener and create a callback
	 *
	 * @param uci the UCI which is being set
	 * @param value the value which the UCI should be set to
	 */
	public void callBinarySetEventListener(SensibleThingsNode fromNode, String uci, byte[] value){
		if(binarySetEventListener != null){
			binarySetEventListener.setEvent(fromNode, uci, value);
		}
	}
	/**
	 * Used to call the listener and create a callback
	 *
	 * @param node the end point which is trying to fetch a value
	 * @param uci the UCI which is trying to be fetched
	 */
	public void callGetEventListener(SensibleThingsNode node, String uci){
		if(getEventListener != null){
			getEventListener.getEvent(node, uci);
		}
	}

	/**
	 * Used to call the listener and create a callback
	 *
	 * @param uci the UCI which is trying to be fetched
	 */
	public void callNegativeResolveListener(String uci){
		for (Iterator<NegativeResolveResponseListener> iterator = negativeResolveResponseListener.iterator(); iterator.hasNext();) {
			NegativeResolveResponseListener listener = iterator.next();
			listener.negativeResolveResponse(uci);

		}
	}


	/**
	 * Used to call the listener and create a callback
	 *
	 * @param uci the UCI which is trying to be fetched
	 */
	public void callSuccessfulRegisterListener(String uci){
		for (Iterator<RegisterResponseListener> iterator = registerResponseListener.iterator(); iterator.hasNext();) {
			RegisterResponseListener listener = iterator.next();
			listener.onSuccessfulRegister(uci);
		}
	}

	/**
	 * Used to call the listener and create a callback
	 *
	 * @param uci the UCI which is trying to be fetched
	 * @return a list of passwords returned by all listeners
	 */
	public List<String> callRegisterPasswordRequiredListener(String uci){
		ArrayList<String> passwords = new ArrayList<String>();
		for (Iterator<RegisterResponseListener> iterator = registerResponseListener.iterator(); iterator.hasNext();) {
			RegisterResponseListener listener = iterator.next();
			passwords.add(listener.onRegisterPasswordRequired(uci));
		}
		return passwords;
	}

	/**
	 * Used to call the listener and create a callback
	 *
	 * @param uci the UCI which is trying to be fetched
	 */
	public void callFailedRegisterListener(String uci, String error){
		for (Iterator<RegisterResponseListener> iterator = registerResponseListener.iterator(); iterator.hasNext();) {
			RegisterResponseListener listener = iterator.next();
			listener.onFailedRegister(uci, error);
		}
	}


	//Primitive functions!
	/**
	 * The RESOLVE primitive action, which resolves an UCI in the lookupService.
	 * Fires off a resolveReponse callback with the answer.
	 *
	 * @param uci the UCI to be resolved
	 */
	public void resolve(final String uci){
		Runnable r = new Runnable() {
			public void run() {
				if(lookupService != null){
					lookupService.resolve(uci);
				}
			}
		};
		executorPool.execute(r);
	}
	/**
	 * The REGISTER primitive action, which registers an UCI in the lookupService.
	 * Is the same as calling register(uci,"");
	 * @param uci the UCI to be registered
	 */
	public void register(final String uci){
		register(uci,"");
	}
	/**
	 * The REGISTER primitive action with optional password protection, which registers an UCI in the lookupService using the selected password.
	 * Password protected storage implementation of lookupservice is optional.
	 * Password protection is to ensure ownership of a UCI, and prevent it from being overwritten.
	 *
	 * @param uci the UCI to be registered
	 * @param password the password to be registered
	 */
	public void register(final String uci, final String password){
		Runnable r = new Runnable() {
			public void run() {
				if(lookupService != null){
					lookupService.register(uci, password);
				}
			}
		};
		executorPool.execute(r);
	}
	/**
	 * The GET primitive action, which fetches the value from another entity.
	 * Fires off a getReponse callback with the answer.
	 *
	 * @param uci the UCI to be fetched
	 * @param node the end point which has been previously been resolved to manage the UCI
	 */
	public void get(final String uci, final SensibleThingsNode node){
		Runnable r = new Runnable() {
			public void run() {
				if(communication != null){
					GetMessage message = new GetMessage(uci, node, communication.getLocalSensibleThingsNode());

					try {
						communication.sendMessage(message);
					}
					catch (DestinationNotReachableException e) {
						e.printStackTrace();
					}
				}
			}
		};
		executorPool.execute(r);
	}
	/**
	 * The SET primitive action, which pushes a value to another entity.
	 *
	 * @param uci the UCI to be set on the remote entity
	 * @param value the value which the UCI shall be set to
	 * @param node the end point which has been previously been resolved to manage the UCI
	 */
	public void set(final String uci, final String value, final SensibleThingsNode node){
		set(uci, value.getBytes(), node);
	}
	/**
	 * The SET primitive action, which pushes a value to another entity.
	 *
	 * @param uci the UCI to be set on the remote entity
	 * @param value the value which the UCI shall be set to
	 * @param node the end point which has been previously been resolved to manage the UCI
	 */
	public void set(final String uci, final byte[] value, final SensibleThingsNode node){
		Runnable r = new Runnable() {
			public void run() {
				if(communication != null){
					SetMessage message = new SetMessage(uci, value, node, communication.getLocalSensibleThingsNode());

					try {
						communication.sendMessage(message);
					}
					catch (DestinationNotReachableException e) {
						//Do nothing
					}
				}
			}
		};
		executorPool.execute(r);
	}
	/**
	 * The NOTIFY primitive action, which sends a value back to a previously asking entity.
	 * This is the return call for the GetEvent callback.
	 *
	 * @param node the SensibleThingsNode which the value should be sent to
	 * @param uci the UCI of the value
	 * @param value the actual value of the UCI
	 */
	public void notify(final SensibleThingsNode node, final String uci, final String value){
		notify(node,uci,value.getBytes());
	}
	/**
	 * The NOTIFY primitive action, which sends a value back to a previously asking entity.
	 * This is the return call for the GetEvent callback.
	 *
	 * @param node the SensibleThingsNode which the value should be sent to
	 * @param uci the UCI of the value
	 * @param value the binary value of the UCI
	 */
	public void notify(final SensibleThingsNode node, final String uci, final byte[] value){
		Runnable r = new Runnable() {
			public void run() {
				if(communication != null){
					NotifyMessage  message = new NotifyMessage(uci, value, node, communication.getLocalSensibleThingsNode());

					try {
						communication.sendMessage(message);
					}
					catch (DestinationNotReachableException e) {
						//Do nothing
					}
				}
			}
		};
		executorPool.execute(r);
	}

	//Handle the primitive messages ourself from the post office
	public void handleMessage(Message message) {

		if(message instanceof GetMessage) {
			//Fire off the getEvent!
			GetMessage getMessage = (GetMessage) message;
			callGetEventListener(getMessage.getFromNode(), getMessage.uci);
		}
		else if(message instanceof SetMessage) {
			//Fire off the SetEvent!
			SetMessage setMessage = (SetMessage) message;
			callBinarySetEventListener(setMessage.getFromNode(), setMessage.uci, setMessage.value);
			callSetEventListener(setMessage.getFromNode(), setMessage.uci, setMessage.value);
		}
		else if(message instanceof NotifyMessage) {
			//Fire off the getResponseEvent!
			NotifyMessage notifyMessage = (NotifyMessage) message;
			callBinaryGetResponseListener(notifyMessage.uci, notifyMessage.value, notifyMessage.getFromNode());
			callGetResponseListener(notifyMessage.uci, notifyMessage.value, notifyMessage.getFromNode());
		}

	}

	public void onNewCommunicationState(Communication communication,
										String newState) {
		if(lookupService==null){
			return;
		}
		if(newState.equals(Communication.COMMUNICATION_STATE_CONNECTED)
				&&lookupService.getState().equals(LookupService.STATE_CONNECTED)){
			if(currentState.equals(DISSEMINATION_CORE_STATE_DISCONNECTED)|| currentState.equals(DISSEMINATION_CORE_STATE_UNDEFINED)){
				setState(DISSEMINATION_CORE_STATE_CONNECTED);
			}
		}else{
			if(currentState.equals(DISSEMINATION_CORE_STATE_CONNECTED)){
				setState(DISSEMINATION_CORE_STATE_DISCONNECTED);
			}
		}

	}

	public void onNewLookupServiceState(LookupService service, String newState) {
		if(communication==null){
			return;
		}
		if(newState.equals(LookupService.STATE_CONNECTED)
				&&communication.getState().equals(Communication.COMMUNICATION_STATE_CONNECTED)){
			if(currentState.equals(DISSEMINATION_CORE_STATE_DISCONNECTED)|| currentState.equals(DISSEMINATION_CORE_STATE_UNDEFINED)){
				setState(DISSEMINATION_CORE_STATE_CONNECTED);
			}
		}else{
			if(currentState.equals(DISSEMINATION_CORE_STATE_CONNECTED)){
				setState(DISSEMINATION_CORE_STATE_DISCONNECTED);
			}
		}
	}

	public String getState(){
		return currentState;
	}
	protected void setState(String newState){
		currentState = newState;
		callListeners();
	}
	public boolean haveState(String state) {
		if(state != null && state.equals(getState())){
			return true;
		}
		return false;
	}

	public void addStateListener(DisseminationCoreStateListener listener){
		sensibleStateListeners.add(listener);
		listener.onNewCoreState(this, getState());
	}

	public void removeStateListener(DisseminationCoreStateListener listener){
		sensibleStateListeners.remove(listener);
	}

	private void callListeners(){
		for (Iterator<DisseminationCoreStateListener> iterator = sensibleStateListeners.iterator(); iterator.hasNext();) {
			DisseminationCoreStateListener listener = iterator.next();
			listener.onNewCoreState(this, getState());
		}
	}


}
