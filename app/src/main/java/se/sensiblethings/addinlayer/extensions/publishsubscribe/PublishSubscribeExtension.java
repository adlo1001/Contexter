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

package se.sensiblethings.addinlayer.extensions.publishsubscribe;

import java.util.HashMap;
import java.util.Vector;

import se.sensiblethings.addinlayer.extensions.Extension;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.DestinationNotReachableException;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.disseminationlayer.disseminationcore.DisseminationCore;
import se.sensiblethings.disseminationlayer.disseminationcore.MessageListener;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public class PublishSubscribeExtension implements Extension, MessageListener {
	
	SubscriptionResponseListener subscriptionResponseListener = null;	
	SensibleThingsPlatform platform = null;
	DisseminationCore core = null;
	Communication communication = null;
	
	//Handles all the subscription listeners
	private MultiMap outgoingSubscriptions = new MultiMap();
	
	//New stuff
	private HashMap<String, SensibleThingsNode> incomingSubscriptions = new HashMap<String, SensibleThingsNode>();
		
	public void loadAddIn(SensibleThingsPlatform platform) {
		this.platform = platform;
		this.core = platform.getDisseminationCore();
		this.communication = core.getCommunication();
		
		//Register our own message types in the post office
		communication.registerMessageListener(EndSubscribeMessage.class.getName(), this);		
		communication.registerMessageListener(StartSubscribeMessage.class.getName(), this);		
		communication.registerMessageListener(NotifySubscribersMessage.class.getName(), this);		
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
		
        if(message instanceof StartSubscribeMessage) {
        	//Start the subscription in the addIn
			StartSubscribeMessage startSubMessage = (StartSubscribeMessage) message;				
			outgoingSubscriptions.put(startSubMessage.uci, startSubMessage.getFromNode());				
        }
        else if(message instanceof EndSubscribeMessage) {	
	        //End the subscription
			EndSubscribeMessage endSubMessage = (EndSubscribeMessage) message;						
	    	outgoingSubscriptions.remove(endSubMessage.uci, endSubMessage.getFromNode());	
	        }
        else if(message instanceof NotifySubscribersMessage) {
        	//Call the listener!
			NotifySubscribersMessage notifydSubMessage = (NotifySubscribersMessage) message;
			subscriptionResponseListener.subscriptionResponse(notifydSubMessage.uci, notifydSubMessage.value);
        }

	}
	
	/**
	 * This will start a subscription for a specific UCI. 
	 * Given that the other end point is also running the publish/subscribe interface.
	 * @param uci the UCI to be subscribed to
	 * @param ip the IP end point which handles the UCI, which has previously been resolved. 
	 */
	public void startSubscription(String uci, SensibleThingsNode node){

		//New stuff
		incomingSubscriptions.put(uci, node);
		
		//Send out the startSubscribe Message
		StartSubscribeMessage message = new StartSubscribeMessage(uci, node, communication.getLocalSensibleThingsNode());

		
		try {
			communication.sendMessage(message);
		}
		catch(DestinationNotReachableException e) {
			//Do nothing
			//e.printStackTrace();
		}
	}
	
	/**
	 * This will end the subscription for a specific UCI.
	 * All notify messages from that UCI should now be stopped.
	 * @param uci the UCI which is not longer wanted
	 * @param ip the IP end point which handles the UCI, which has previously been resolved. 
	 */
	public void endSubscription(String uci){
				
		SensibleThingsNode node = incomingSubscriptions.get(uci);
		incomingSubscriptions.remove(uci);

		//Send out the endSubscribe Message
		EndSubscribeMessage message = new EndSubscribeMessage(uci, node, communication.getLocalSensibleThingsNode());

		try {
			communication.sendMessage(message);
		}
		catch(DestinationNotReachableException e) {
			//Do nothing
			//e.printStackTrace();
		}
	}
	
	/**
	 * This is called to notify all subscribers of a new value.
	 * Should be called when a value is updated.
	 * @param uci the UCI that was just updated
	 * @param value the new value, which will be sent to all subscribers
	 */
	public void notifySubscribers(String uci, String value){
		
		//Attend to the subscriptions
		SensibleThingsNode[] subsriberNode = outgoingSubscriptions.get(uci);
		
		for(int i = 0; i != subsriberNode.length; i++){							
			NotifySubscribersMessage message = new NotifySubscribersMessage(uci, value, subsriberNode[i], communication.getLocalSensibleThingsNode());

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
	 * Sets the SubscriptionResponseListener 
	 * @param listener SubscriptionResponseListener
	 */
	public void setSubscriptionResponseListener(SubscriptionResponseListener listener){
		this.subscriptionResponseListener = listener;		
	}
	
	/**
     * Used to call the listener and create a callback
     * @param uci the UCI which is being received
     * @param value the new value which the UCI has
     */
	public void callSubscriptionEventListener(String uci, String value){
		if(subscriptionResponseListener != null){
			subscriptionResponseListener.subscriptionResponse(uci, value);		
		}
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





