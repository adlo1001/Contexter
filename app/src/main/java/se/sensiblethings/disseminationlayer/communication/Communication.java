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

package se.sensiblethings.disseminationlayer.communication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import se.sensiblethings.disseminationlayer.disseminationcore.MessageListener;
import se.sensiblethings.interfacelayer.SensibleThingsNode;


public abstract class Communication {
			
	public final static String TCP = "se.sensiblethings.disseminationlayer.communication.tcp.TcpCommunication";
	public final static String RUDP = "se.sensiblethings.disseminationlayer.communication.rudp.RUDPCommunication";
	public final static String PROXY_RUDP = "se.sensiblethings.disseminationlayer.communication.proxy.rudp.RudpProxyCommunication";
	public final static String SSL = "se.sensiblethings.disseminationlayer.communication.ssl.SslCommunication";
	public final static String PROXY_SSL = "se.sensiblethings.disseminationlayer.communication.proxy.ssl.SslProxyCommunication";
	public final static String PROXY_STEAVI = "se.sensiblethings.disseminationlayer.communication.steaviproxy.SteaviProxyCommunication";
	
	public final static String COMMUNICATION_STATE_CONNECTED = "COMMUNICATION_CONNECTED";
	public final static String COMMUNICATION_STATE_DISCONNECTED = "COMMUNICATION_DISCONNECTED";
	public final static String COMMUNICATION_STATE_UNDEFINED = "COMMUNICATION_UNDEFINED";
	
	private String currentState = COMMUNICATION_STATE_UNDEFINED;
	private HashSet<CommunicationStateListener> listeners = new HashSet<CommunicationStateListener>();
	
	//Abstract functions
	public abstract void sendMessage(Message message) throws DestinationNotReachableException;
	
	public abstract void shutdown();			
	
	/*
	 * New functions to support mobility by enabling the communication-layer to swap internal endpoint representation  
	 */
	
	public abstract SensibleThingsNode getLocalSensibleThingsNode();
	
	public abstract SensibleThingsNode createSensibleThingsNode(String ip, int port);
	

	
	
	public final String getState(){
		return currentState;
	}
	protected final void setState(String newState){
		currentState = newState;
		callListeners();
	}
	
	public final void addStateListener(final CommunicationStateListener listener){
		final String state = getState();
		listeners.add(listener);
		if(!executor.isShutdown())
			executor.execute(new Runnable() {
				
				public void run() {
					//We give the invoking class a chance to finish initialization 
//					try {
//						Thread.sleep(15);//
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						//e.printStackTrace();
//					}
					listener.onNewCommunicationState(Communication.this, state);
				}
			});
		
	}
	
	public final void removeStateListener(CommunicationStateListener listener){
		listeners.remove(listener);
	}
	
	private final void callListeners(){
		if(listeners == null){
			return;
		}
		for (Iterator<CommunicationStateListener> iterator = listeners.iterator(); iterator.hasNext();) {
			
			final CommunicationStateListener listener = iterator.next();
//			executor.execute(new Runnable() {	
//				public void run() {
					listener.onNewCommunicationState(Communication.this, getState());
//				}
//			});
			
		}
	}
	
	
	//POST OFFICE code////////////////////////////////////////////////	
	//private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	private ExecutorService executor = Executors.newCachedThreadPool();
    private HashMap<String, Vector<MessageListener>> messageListeners = new HashMap<String, Vector<MessageListener>>();

	public void shutdownPostOffice(){
		executor.shutdownNow();
	}
    public void setExecutor(ExecutorService newExecutor) {
    	final ExecutorService oldExecutor = executor;
		this.executor = newExecutor;
		oldExecutor.shutdown();
		executor.execute(new Runnable() {
			
			public void run() {
				try {
					oldExecutor.awaitTermination(5, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!oldExecutor.isTerminated()){
					List<Runnable> list = oldExecutor.shutdownNow();
					if(list!=null){
						for (Iterator<Runnable> iterator = list.iterator(); iterator.hasNext();) {
							Runnable runnable = iterator.next();
							executor.execute(runnable);
						}
					}
				}
			}
		});
	}
    
    public void registerMessageListener(String messageType, MessageListener listener) {
       if(messageListeners.containsKey(messageType))
       {
           messageListeners.get(messageType).add(listener);
       }
       else
       {            
           messageListeners.put(messageType, new Vector<MessageListener>());
           messageListeners.get(messageType).add(listener);           
       }
    }

	public void removeMessageListener(String messageType, MessageListener listener) {
		if (messageListeners.containsKey(messageType)) {
			messageListeners.get(messageType).remove(listener);
		}
	}
    
	public void dispatchMessageToPostOffice(final Message message) {
		//Do Sanity check here!
        if(messageListeners.containsKey(message.getType())) 
        {          	
        	final Vector<MessageListener> v = messageListeners.get(message.getType());        	        	
        	for(int i = 0; i < v.size(); i++){
        		final int index = i;
        		Runnable r = new Runnable() {					
					public void run() {
		        		v.get(index).handleMessage(message);        								
					}
				};
				executor.execute(r);
        	}
        } else {
        	System.out.println("No Message Listener registered for " + message.getType());
        }
    }
    //POST OFFICE code ////////////////////////////////////////////////
		
}
