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

package se.sensiblethings.addinlayer.extensions.smart;

import java.util.HashMap;
import java.util.Vector;

import se.sensiblethings.addinlayer.extensions.Extension;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.DestinationNotReachableException;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.disseminationlayer.disseminationcore.DisseminationCore;
import se.sensiblethings.disseminationlayer.disseminationcore.MessageListener;
import se.sensiblethings.interfacelayer.SensibleThingsListener;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public class SmartExtension implements Extension, MessageListener, SensibleThingsListener {
	
	SmartGetResponseListener smartGetResponseListener = null;	
	SmartGetEventListener smartGetEventListener = null;	
	SmartSetEventListener smartSetEventListener = null;	
	SmartSubscriptionResponseListener smartSubscriptionResponseListener = null;		
	SmartAuthenticationListener smartAuthListener = null;
	

	

	
	
	SensibleThingsPlatform platform = null;
	DisseminationCore core = null;
	Communication communication = null;
	
	//Handles the subscriptions
	private MultiMap outgoingSubscriptions = new MultiMap();
	private HashMap<String, SensibleThingsNode> incomingSubscriptions = new HashMap<String, SensibleThingsNode>();

	//Handles the smart Get, Set, Resolve
	private HashMap<String, SensibleThingsNode> resolveCache = new HashMap<String, SensibleThingsNode>();	
	private HashMap<String, String> publishCache = new HashMap<String, String>();	
	private MultiMap getEventQueue = new MultiMap();
	HashMap<String, String> secrets = new HashMap<String, String>();

	
	public SmartExtension(SmartListener listener) {
		this.smartGetEventListener = listener;
		this.smartSetEventListener = listener;
		this.smartGetResponseListener = listener;
		this.smartSubscriptionResponseListener = listener;
		this.smartAuthListener = listener;
	}
	
	public void loadAddIn(SensibleThingsPlatform platform) {
		this.platform = platform;
		this.core = platform.getDisseminationCore();
		this.communication = core.getCommunication();
	
		//Take control over the listeners!
		platform.setGetEventListener(this);
		platform.setSetEventListener(this);
		platform.setGetResponseListener(this);
		platform.setResolveResponseListener(this);
		
		//Register our own message types in the post office
		communication.registerMessageListener(SmartEndSubscribeMessage.class.getName(), this);		
		communication.registerMessageListener(SmartStartSubscribeMessage.class.getName(), this);		
		communication.registerMessageListener(SmartNotifySubscribersMessage.class.getName(), this);
		
		communication.registerMessageListener(SmartAuthenticatedGetMessage.class.getName(), this);
		communication.registerMessageListener(SmartAuthenticatedSetMessage.class.getName(), this);	
	
	}


	
	
	public void startAddIn() {
		//No extra stuff to start		
	}

	public void stopAddIn() {
		//No extra stuff to stop
	}

	public void unloadAddIn() {
		outgoingSubscriptions.clear();		
	}
	
	public void handleMessage(Message message) {
		
        if(message instanceof SmartStartSubscribeMessage) {
        	//Start the subscription in the addIn
			SmartStartSubscribeMessage startSubMessage = (SmartStartSubscribeMessage) message;				
			outgoingSubscriptions.put(startSubMessage.uci, startSubMessage.getFromNode());				
        }
        else if(message instanceof SmartEndSubscribeMessage) {	
	        //End the subscription
			SmartEndSubscribeMessage endSubMessage = (SmartEndSubscribeMessage) message;						
	    	outgoingSubscriptions.remove(endSubMessage.uci, endSubMessage.getFromNode());	
        }
        else if(message instanceof SmartNotifySubscribersMessage) {
        	//Call the listener!
			SmartNotifySubscribersMessage notifydSubMessage = (SmartNotifySubscribersMessage) message;			
			if(smartSubscriptionResponseListener != null){
				smartSubscriptionResponseListener.smartSubscriptionResponse(notifydSubMessage.uci, notifydSubMessage.value);				
			}
        }
        else if(message instanceof SmartAuthenticatedGetMessage) {
        	SmartAuthenticatedGetMessage authGetMessage = (SmartAuthenticatedGetMessage) message;
        	SensibleThingsNode source = authGetMessage.getFromNode();
        	String uci = authGetMessage.uci;
        	String secret = authGetMessage.secret;
        	
        	//Check if secret is ok
        	if(secrets.get(uci).equalsIgnoreCase(secret)){
        		//Check cache
        		if(publishCache.containsKey(uci)){
        			String value = publishCache.get(uci);			
        			platform.notify(source, uci, value);			
        		} else {
        			
        			getEventQueue.put(uci, source);
        			
        			if(smartAuthListener != null){
                		smartAuthListener.authenticatedSmartGetEvent(uci);
                	}				
        		}
        	}
        }
        else if(message instanceof SmartAuthenticatedSetMessage) {
        	SmartAuthenticatedSetMessage authSetMessage = (SmartAuthenticatedSetMessage) message;
        	String uci = authSetMessage.uci;
        	String value = authSetMessage.value;
        	String secret = authSetMessage.secret;
        	        	
        	//Check if secret is ok
        	if(secrets.get(uci).equalsIgnoreCase(secret)){
            	if(smartAuthListener != null){
            		smartAuthListener.authenticatedSmartSetEvent(uci, value);
            	}
        	}
        }
	}
		
	public void get(final String uci){				
		if(resolveCache.containsKey(uci)){			
			SensibleThingsNode node = resolveCache.get(uci);
			platform.get(uci, node);								
		} else {			
			platform.resolve(uci);
			
			//Wait for it to arrive
			new Thread(new Runnable() {
				public void run() {					
					while(!resolveCache.containsKey(uci)){
						try {
							Thread.sleep(100);							
						} catch (Exception e){
							//e.printStackTrace();
						}
					}
					SensibleThingsNode node = resolveCache.get(uci);
					platform.get(uci, node);
				}
			}).start();
		}
	}
	
	public void set(final String uci, final String value){				
		if(resolveCache.containsKey(uci)){			
			SensibleThingsNode node = resolveCache.get(uci);
			platform.set(uci, value, node);								
		} else {			
			platform.resolve(uci);
			
			//Wait for it to arrive
			new Thread(new Runnable() {
				public void run() {					
					while(!resolveCache.containsKey(uci)){
						try {
							Thread.sleep(100);							
						} catch (Exception e){
							//e.printStackTrace();
						}
					}
					SensibleThingsNode node = resolveCache.get(uci);
					platform.set(uci, value, node);	
				}
			}).start();
		}
	}
	
	
	public void getResponse(String uci, String value, SensibleThingsNode fromNode) {		
		if(smartGetResponseListener != null){
			smartGetResponseListener.smartGetResponse(uci, value);			
		}		
	}
	

	public void resolveResponse(String uci, SensibleThingsNode node) {
		resolveCache.put(uci, node);			
	}


	public void getEvent(SensibleThingsNode source, String uci) {
		//Check cache
		if(publishCache.containsKey(uci)){
			String value = publishCache.get(uci);			
			platform.notify(source, uci, value);			
		} else {
			
			getEventQueue.put(uci, source);
			
			if(smartGetEventListener != null){
				smartGetEventListener.smartGetEvent(uci);			
			}				
		}
	}


	public void setEvent(SensibleThingsNode source, String uci, String value) {
		if(smartSetEventListener != null){
			smartSetEventListener.smartSetEvent(source, uci, value);			
		}		
	}
	
	
	public void register(String uci){		
		if(!resolveCache.containsKey(uci)){
			platform.register(uci);
			resolveCache.put(uci, communication.getLocalSensibleThingsNode());
		}
	}
	
	/**
	 * This is called to notify all subscribers of a new value.
	 * Should be called when a value is updated.
	 * @param uci the UCI that was just updated
	 * @param value the new value, which will be sent to all subscribers
	 */
	public void publish(String uci, String value){
		
		//Make sure it is registered
		register(uci);
		
		//Cache the value
		publishCache.put(uci, value);
		
		//Also Notify
		notify(uci, value);		
			
	}
	
	public void notify(String uci, String value){
		//Check the GetEventQueue
		SensibleThingsNode[] nodes = getEventQueue.get(uci);		
		for(int i = 0; i != nodes.length; i++){
			SensibleThingsNode node = nodes[i];
			platform.notify(node, uci, value);
			getEventQueue.remove(uci, node);
		}
		
		//Attend to the subscriptions
		SensibleThingsNode[] subsriberNode = outgoingSubscriptions.get(uci);		
		for(int i = 0; i != subsriberNode.length; i++){							
			SmartNotifySubscribersMessage message = new SmartNotifySubscribersMessage(uci, value, subsriberNode[i], communication.getLocalSensibleThingsNode());

			try {
				communication.sendMessage(message);						
			}
			catch(DestinationNotReachableException e) {
				//Not reachable, remove that subscriber from the list
				outgoingSubscriptions.remove(uci, subsriberNode[i]);				
			}
		}	
	}	
	
	
	/**
	 * This will start a subscription for a specific UCI. 
	 * Given that the other end point is also running the publish/subscribe interface.
	 * @param uci the UCI to be subscribed to
	 * @param ip the IP end point which handles the UCI, which has previously been resolved. 
	 */
	public void startSubscription(final String uci){
		
		if(resolveCache.containsKey(uci)){			
			try {
				SensibleThingsNode node = resolveCache.get(uci);
				incomingSubscriptions.put(uci, node);
				
				//Send out the startSubscribe Message
				SmartStartSubscribeMessage message = new SmartStartSubscribeMessage(uci, node, communication.getLocalSensibleThingsNode());
				communication.sendMessage(message);
			}
			catch(Exception e) {
				//e.printStackTrace();
			}			
		} else {			
			platform.resolve(uci);			
			//Wait for it to arrive
			new Thread(new Runnable() {
				public void run() {					
					while(!resolveCache.containsKey(uci)){
						try {
							Thread.sleep(100);							
						} catch (Exception e){
							//e.printStackTrace();
						}
					}
					//Restart the function!
					startSubscription(uci);
				}
			}).start();
		}
	}
	
	/**
	 * This will end the subscription for a specific UCI.
	 * All notify messages from that UCI should now be stopped.
	 * @param uci the UCI which is not longer wanted
	 * @param ip the IP end point which handles the UCI, which has previously been resolved. 
	 */
	public void endSubscription(String uci){
		try {		
			SensibleThingsNode node = incomingSubscriptions.get(uci);
			incomingSubscriptions.remove(uci);
	
			//Send out the endSubscribe Message
			SmartEndSubscribeMessage message = new SmartEndSubscribeMessage(uci, node, communication.getLocalSensibleThingsNode());
			communication.sendMessage(message);
		}
		catch(Exception e) {
			//e.printStackTrace();
		}
	}
	
	
	//Auth a secret
	public void autenticate(String uci, String secret){
		secrets.put(uci, secret);		
	}


	//Remove nodes from these!
	public void authGet(final String uci, final String secret) {
		try {				
			if(resolveCache.containsKey(uci)){			
				SensibleThingsNode node = resolveCache.get(uci);
				SmartAuthenticatedGetMessage authGetMessage = new SmartAuthenticatedGetMessage(uci, secret, node, communication.getLocalSensibleThingsNode());		
				communication.sendMessage(authGetMessage);
			} else {			
				platform.resolve(uci);
				
				//Wait for it to arrive
				new Thread(new Runnable() {
					public void run() {					
						while(!resolveCache.containsKey(uci)){
							try {
								Thread.sleep(100);							
							} catch (Exception e){
								//e.printStackTrace();
							}
						}
						SensibleThingsNode node = resolveCache.get(uci);
						SmartAuthenticatedGetMessage authGetMessage = new SmartAuthenticatedGetMessage(uci, secret, node, communication.getLocalSensibleThingsNode());		
						try {
							communication.sendMessage(authGetMessage);
						} catch (Exception e) {
							//e.printStackTrace();
						}
					}
				}).start();
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public void authSet(final String uci, final String value, final String secret) {
		try {	
			
			if(resolveCache.containsKey(uci)){			
				SensibleThingsNode node = resolveCache.get(uci);

				SmartAuthenticatedSetMessage authSetMessage = new SmartAuthenticatedSetMessage(uci, value, secret, node, communication.getLocalSensibleThingsNode());		
				communication.sendMessage(authSetMessage);							
			} else {			
				platform.resolve(uci);
				
				//Wait for it to arrive
				new Thread(new Runnable() {
					public void run() {					
						while(!resolveCache.containsKey(uci)){
							try {
								Thread.sleep(100);							
							} catch (Exception e){
								//e.printStackTrace();
							}
						}
						SensibleThingsNode node = resolveCache.get(uci);

						SmartAuthenticatedSetMessage authSetMessage = new SmartAuthenticatedSetMessage(uci, value, secret, node, communication.getLocalSensibleThingsNode());		
						try {
							communication.sendMessage(authSetMessage);
						} catch (Exception e) {
							//e.printStackTrace();
						}
					}
				}).start();
			}

		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	
	
	
	//All the Listener Management
	/**
	 * Sets the SubscriptionResponseListener 
	 * @param listener SubscriptionResponseListener
	 */
	public void setSmartSubscriptionResponseListener(SmartSubscriptionResponseListener listener){
		this.smartSubscriptionResponseListener = listener;		
	}	
	/**
	 * Sets the SmartGetResponseListener 
	 * @param listener SmartGetResponseListener
	 */
	public void setSmartGetResponseListener(SmartGetResponseListener listener){
		this.smartGetResponseListener = listener;		
	}	
	/**
	 * Sets the SmartGetEventListener 
	 * @param listener SmartGetEventListener
	 */
	public void setSmartGetEventListener(SmartGetEventListener listener){
		this.smartGetEventListener = listener;		
	}	
	/**
	 * Sets the SmartSetEventListener 
	 * @param listener SmartSetEventListener
	 */
	public void setSmartGetEventListener(SmartSetEventListener listener){
		this.smartSetEventListener = listener;		
	}
	
	
	
	
	
	
	
	//MultiMap for handling the Subscriptions
	private class MultiMap{
		
		HashMap<String, Vector<SensibleThingsNode>> map = new HashMap<String, Vector<SensibleThingsNode>>();
		
		public void put(String uci, SensibleThingsNode node){		
			Vector<SensibleThingsNode> v = map.get(uci);		
			if(v == null){
				v = new Vector<SensibleThingsNode>();
			}
			v.add(node);
			map.put(uci, v);
		}
		
		public SensibleThingsNode[] get(String uci){		
			Vector<SensibleThingsNode> v = map.get(uci);
			if(v == null){
				return new SensibleThingsNode[0];
			} else {
				return v.toArray(new SensibleThingsNode[0]);
			}
		}
		
		public void remove(String uci, SensibleThingsNode node){
			Vector<SensibleThingsNode> v = map.get(uci);
			
			//Find the node and remove it
			for(int i = 0; i != v.size(); i++){
				if(v.get(i).toString().equalsIgnoreCase(node.toString())){
					v.remove(i);
					break;
				}				
			}			
			//map.put(uci, v);
		}
		
		public void clear(){
			map.clear();
		}		
	}

	
}





