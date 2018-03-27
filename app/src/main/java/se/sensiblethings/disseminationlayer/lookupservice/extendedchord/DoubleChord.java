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

package se.sensiblethings.disseminationlayer.lookupservice.extendedchord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.CommunicationStateListener;
import se.sensiblethings.disseminationlayer.communication.DestinationNotReachableException;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.disseminationlayer.disseminationcore.DisseminationCore;
import se.sensiblethings.disseminationlayer.disseminationcore.MessageListener;
import se.sensiblethings.disseminationlayer.lookupservice.LookupService;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.DistributedHashTable.DHTFeedbackListener;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.DistributedHashTable.PutResponseListener;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.DistributedHashTable.State;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.DistributedHashTable.StateChangeListener;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.DoubleChord.PerThreadLogFormatter.PerThreadFileNameFormatter;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.DHTMessage;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.DataSync;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.FindSuccessor;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.FindSuccessorResponse;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.Get;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.GetPredecessor;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.GetPredecessorResponse;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.GetResponse;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.GetSuccessor;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.GetSuccessorResponse;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.GetSuccessorTable;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.GetSuccessorTableResponse;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.Join;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.JoinResponse;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.Leave;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.Put;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.PutResponse;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.RequestFullFingerTable;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.RequestFullFingerTableResponse;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.UpdatePredecessor;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.UpdateSuccessor;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class DoubleChord extends LookupService implements MessageListener,
		CommunicationStateListener, DHTFeedbackListener, StateChangeListener {

	private static final String OUTER_DHT = "OUTER";
	static final String INNER_DHT = "INNER";
	

	private static final String NO_USERNAME_SELECTED = "NO USERNAME SELECTED";
	
	private boolean debug = false;
	private boolean filedebug = false;
	private File debugFile;
	private BufferedWriter fileOut;
	public void log(String string) {
		if (debug) {
			System.out.println("[E-Lookup " + PerThreadLogFormatter.getDateFormatter().format(new Date()) + "]\n" + string);
		}
		if(filedebug){
			try {
				fileOut.write(PerThreadLogFormatter.getDateFormatter().format(new Date()) + ":" + string);
				fileOut.newLine();
				fileOut.flush();
			} catch (IOException e) {
				filedebug = false;
				System.err.println("Error in writing logfile:" + debugFile.toString());
			}
			
		}
	}

	public static String bootstrapIP = "192.168.154.1";
	public static int defaultBootstrapPort = 45023;
	public static String userID = NO_USERNAME_SELECTED;
	public static String userPW = "";
	
	private MessageDigest digest;

	private SensibleThingsNode bootstrap;
	private SensibleThingsNode IP;
	private String keyIP;
	private String identity = userID;
	private String identityPassword;
	private String keyIdentity;
	
	private DistributedHashTable outerDHT, innerDHT;
	private Queue<DHTMessage> backlog;
	ScheduledExecutorService maintenaceService;
	private Map<String, DHTEntry> identityCache;

	private volatile long lastIdentityUpdate;
	private volatile DHTGetResponseListener currentIdentityCheck;
	private DHTSettings outerSettings;
	private DHTSettings innerSettings;
	private int index = 0;
	
	public DoubleChord(Communication communication,
			DisseminationCore disseminationCore) {
		super(communication, disseminationCore);
		debug = DoubleChordSettings.DEBUG;
		filedebug = DoubleChordSettings.FILEDEBUG;
		maintenaceService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
			
			 
			public Thread newThread(Runnable r) {
				Thread th = new Thread(r);
				th.setName("DoubleChord/"+identity + "/thread" + ++index);
				th.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					
					 
					public void uncaughtException(Thread arg0, Throwable arg1) {
						System.err.print("Uncaught exception in thread " + arg0.getName());
						arg1.printStackTrace();
					}
				});
				return th;
			}
		});
		outerSettings = new DHTSettings();
		outerSettings.setReplication(true).setDebug(debug||filedebug);
		innerSettings = new DHTSettings();
		innerSettings.setReplication(false).setDebug(debug||filedebug);
		
		bootstrap = communication.createSensibleThingsNode(bootstrapIP,
				defaultBootstrapPort);
		IP = communication.getLocalSensibleThingsNode();
		backlog = new LinkedList<DHTMessage>();
		identityCache = new ConcurrentHashMap<String, DHTEntry>(16, 0.9f, 1);
		
		if(!userID.contentEquals(NO_USERNAME_SELECTED)){
			identity = userID;
		}
		if(filedebug){
			Date date = new Date() ;
			DateFormat dateFormat = PerThreadFileNameFormatter.getDateFormatter();
			debugFile = new File(identity+dateFormat.format(date)+".txt");
			try {
				fileOut = new BufferedWriter(new FileWriter(debugFile));
			} catch (FileNotFoundException e) {
				System.err.println("Could not open debugfile:" + debugFile.toString());
				filedebug = false;
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Other error opening debugfile:" + debugFile.toString());
				filedebug = false;
			}
		}
		identityPassword = userPW;
		
		// Register messages with post office
		registerMessages();
		// Update state
		setState(STATE_CONNECTING);
		// Add comm state-listener, this will trigger one state update.
		// The IP object is not complete until the communication state is
		// connected.
		communication.addStateListener(this);
		// Initiation continues in onNewCommunicationState
		createMaintenanceThreads();
	}

	private void registerMessages() {
		communication.registerMessageListener(FindSuccessor.class.getName(),
				this);
		communication.registerMessageListener(
				FindSuccessorResponse.class.getName(), this);
		communication.registerMessageListener(
				RequestFullFingerTable.class.getName(), this);
		communication.registerMessageListener(
				RequestFullFingerTableResponse.class.getName(), this);
		communication.registerMessageListener(
				GetSuccessorTable.class.getName(), this);
		communication.registerMessageListener(
				GetSuccessorTableResponse.class.getName(), this);
		communication.registerMessageListener(Join.class.getName(), this);
		communication.registerMessageListener(JoinResponse.class.getName(),
				this);
		communication.registerMessageListener(Get.class.getName(), this);
		communication
				.registerMessageListener(GetResponse.class.getName(), this);
		communication.registerMessageListener(Put.class.getName(), this);
		communication
				.registerMessageListener(PutResponse.class.getName(), this);
		communication.registerMessageListener(Leave.class.getName(), this);
		communication.registerMessageListener(GetPredecessor.class.getName(),
				this);
		communication.registerMessageListener(
				GetPredecessorResponse.class.getName(), this);
		communication.registerMessageListener(
				UpdatePredecessor.class.getName(), this);
		communication.registerMessageListener(GetSuccessor.class.getName(),
				this);
		communication.registerMessageListener(
				GetSuccessorResponse.class.getName(), this);
		communication.registerMessageListener(
				DataSync.class.getName(), this);
		communication.registerMessageListener(UpdateSuccessor.class.getName(), this);
	}
	ScheduledFuture<?> checkIdentityCacheForStaleData;
	ScheduledFuture<?> handleBacklog;
	ScheduledFuture<?> checkOuterEntry;
	private void createMaintenanceThreads() {
		checkIdentityCacheForStaleData = scheduleMaintenance(new Runnable(){
			 
			public void run() {
				if (getState().equals(STATE_CONNECTED)) {
					try {
						checkIdentityCacheForStaleData();
					} catch (Exception e) {
						log("Error in checkIdentityCacheForStaleData: " + e.getMessage()
								+ " stacktrace:");
						e.printStackTrace();
					}
				}
			}
		}, DoubleChordSettings.MAINTENANCE_DELAY, DoubleChordSettings.MAINTENANCE_DELAY, TimeUnit.MILLISECONDS);
		handleBacklog = scheduleMaintenance(new Runnable(){
			 
			public void run() {
				if (getState().equals(STATE_CONNECTED)) {
					try {
						handleBacklog();
					} catch (Exception e) {
						log("Error in handleBacklog: " + e.getMessage()
								+ " stacktrace:");
						e.printStackTrace();
					}
				}
			}
		}, DoubleChordSettings.MAINTENANCE_DELAY, DoubleChordSettings.MAINTENANCE_DELAY, TimeUnit.MILLISECONDS);
		checkOuterEntry = scheduleMaintenance(new Runnable(){
			 
			public void run() {
				if (getState().equals(STATE_CONNECTED)) {
					try {
						checkOuterEntry();
					} catch (Exception e) {
						log("Error in checkOuterEntry: " + e.getMessage()
								+ " stacktrace:");
						e.printStackTrace();
					}
				}
			}
		}, DoubleChordSettings.MAINTENANCE_DELAY, DoubleChordSettings.MAINTENANCE_DELAY, TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> scheduleMaintenance(Runnable run, long initialDelay, long period, TimeUnit timeUnit){
		
		return maintenaceService.scheduleAtFixedRate(run, initialDelay, period, timeUnit);
	}


	private void checkIdentityCacheForStaleData() {
		for (Iterator<Entry<String, DHTEntry>> iterator = identityCache.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, DHTEntry> entry = iterator.next();
			if ((entry.getValue().getLastUpdated() + DoubleChordSettings.CACHE_TIMEOUT) < System.currentTimeMillis()) {
				iterator.remove();
			}
			
		}
	}
	
	
	private void checkOuterEntry(){
		long currentTime = System.currentTimeMillis();
		if(currentIdentityCheck == null && (lastIdentityUpdate + outerSettings.getStoredDataTimeout()/2) < currentTime){
			currentIdentityCheck = new DHTGetResponseListener() {
				
				 
				public void negativeGetResponse(String identity) {
					log("Outer entry is missing, updating.");
					storeIdentityInOuter();
					currentIdentityCheck = null;
				}
				
				 
				public void getResponse(String identity, SensibleThingsNode node,
						DHTEntry entry) {
					lastIdentityUpdate = System.currentTimeMillis();
					currentIdentityCheck = null;
				}
			};
			outerDHT.get(keyIdentity, currentIdentityCheck);
			return;
		}
	}

	 
	public void resolve(final String uci) {
		innerDHT.get(sha1hash(uci), new DHTGetResponseListener() {

			 
			public void negativeGetResponse(String identity) {
				disseminationCore.callNegativeResolveListener(uci);
			}

			 
			public void getResponse(String identity, SensibleThingsNode node,
					DHTEntry entry) {
				if(identityCache.containsKey(entry.getKey())){
					disseminationCore.callResolveResponseListener(uci,identityCache.get(entry.getKey()).getOwnerNode());
				} else{
					outerDHT.get(entry.getKey(), new DHTGetResponseListener() {
						
						 
						public void negativeGetResponse(String identity) {
							disseminationCore.callNegativeResolveListener(uci);
						}
						
						 
						public void getResponse(String identity, SensibleThingsNode node,
								DHTEntry entry) {
							disseminationCore.callResolveResponseListener(uci,entry.getOwnerNode());
						}
					});
				}
			}
		});
	}

	 
	public void register(String uci) {
		register(uci, "");

	}

	 
	public void register(final String uci, String password) {
		DHTEntry newEntry = new DHTEntry(this.keyIdentity, uci);
		insert(uci, password, newEntry, true);
	}

	private void insert(final String uci, final String password,
			final DHTEntry newEntry, final boolean doCoreFeedback) {
		innerDHT.put(sha1hash(uci), newEntry, password, new PutResponseListener() {

			 
			public String passwordRequired(String key) {
				List<String> passwords = disseminationCore
						.callRegisterPasswordRequiredListener(uci);
				if (passwords.isEmpty()) {
					if (doCoreFeedback)
						disseminationCore.callFailedRegisterListener(uci,
								"wrong password");
				} else {
					for (Iterator<String> iterator = passwords.iterator(); iterator
							.hasNext();) {
						String string = iterator.next();
						if (!string.isEmpty())
							return string;
					}
				}
				return null;
			}

			 
			public void PutSuccessful(String key) {

				if (doCoreFeedback)
					disseminationCore.callSuccessfulRegisterListener(uci);
			}

			 
			public void PutFailed(String key, String error) {
				disseminationCore.callFailedRegisterListener(uci, error);
			}
		});
	}

	 
	public void shutdown() {
		setState(STATE_DISCONNECTING);
		checkIdentityCacheForStaleData.cancel(true);
		handleBacklog.cancel(true);
		checkOuterEntry.cancel(true);
		if(innerDHT != null){
			innerDHT.shutdown();
		}else{
			maintenaceService.shutdown();
		}
	}

	 
	public void handleMessage(Message message) {
		if (message instanceof DHTMessage) {
			DHTMessage dmessage = (DHTMessage) message;
			if (dmessage.getRing().equals(OUTER_DHT)) {
				if(outerDHT != null){
					outerDHT.handleMessage(dmessage);
				}
			}else if(dmessage.getRing().equals(INNER_DHT)){
				if(innerDHT != null){
					innerDHT.handleMessage(dmessage);
				}
			}

		}
	}
	
	public void execute(Runnable runner){
		maintenaceService.submit(runner);
	}
	
	public String sha1hash(String string) {
		try {
			if (digest == null) {
				digest = MessageDigest.getInstance("SHA-1");
			}
			synchronized (digest) {
				byte[] hash = digest.digest(string.getBytes("UTF-8"));
				// Convert to Hex
				StringBuffer hexString = new StringBuffer();
				for (int i = 0; i < hash.length; i++) {
					String hex = Integer.toHexString(0xff & hash[i]);
					if (hex.length() == 1)
						hexString.append('0');
					hexString.append(hex);
				}
				return hexString.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	 
	public void onNewCommunicationState(Communication communication,
			String state) {
		if (Communication.COMMUNICATION_STATE_CONNECTED.equals(state)) {

			if (LookupService.STATE_CONNECTING.equals(getState())) {
				IP = communication.getLocalSensibleThingsNode();
				if(identity.contentEquals(NO_USERNAME_SELECTED)){
					identity = generateUserID();
				}
				
				log("local address toString:" + IP.toString());
				log("local address:" + IP.getAddress());
				keyIP = sha1hash(IP.getAddress());
				keyIdentity = sha1hash(identity);
				
				
				outerDHT = new DistributedHashTable(outerSettings, this, DoubleChord.OUTER_DHT,
						keyIP, this);
			} else if (LookupService.STATE_CONNECTED.equals(getState())) {
				handleBacklog();
			}
		}
	}
	
	private String generateUserID() {
		return communication.getLocalSensibleThingsNode().getAddress() + new Date().toString();
	}

	 
	public void stateChanged(DistributedHashTable dht, State newState) {
		if((dht.equals(outerDHT) || dht.getCurrentRing().equals(OUTER_DHT)) && newState.equals(State.CONNECTED)){
			storeIdentityInOuter();
			if(innerDHT == null){
				innerDHT = new DistributedHashTable(innerSettings, this, INNER_DHT, keyIdentity, this);
			}else{
				innerDHT.start();
			}
		}else if (DistributedHashTable.State.CONNECTED.equals(newState) && dht.equals(innerDHT)
				&& LookupService.STATE_CONNECTING.equals(getState())) {
			setState(STATE_CONNECTED);
			
		} else if (outerDHT != null && DistributedHashTable.State.DISCONNECTED.equals(outerDHT.getState()) && innerDHT != null && DistributedHashTable.State.DISCONNECTED.equals(innerDHT.getState())
				&& LookupService.STATE_DISCONNECTING.equals(getState())) {
			maintenaceService.shutdown();
			setState(STATE_DISCONNECTED);
		} else if(dht.equals(innerDHT) && newState.equals(State.DISCONNECTED) && LookupService.STATE_DISCONNECTING.equals(getState())){
			outerDHT.shutdown();
		} else if(newState.equals(State.DISCONNECTED) && (getState().equals(STATE_CONNECTED) || getState().equals(STATE_CONNECTING))){
			dht.start();
		}
		
		
	}
	
	private void storeIdentityInOuter() {
		if(outerDHT == null){
			return;
		}
		
		DHTEntry newEntry = new DHTEntry(this.keyIP,this.identity, getLocalNode());
		//newEntry.setIdentity(KeyIdentity);
		lastIdentityUpdate = System.currentTimeMillis();
		outerDHT.put(keyIdentity,newEntry,identityPassword,  new PutResponseListener() {
			
			 
			public String passwordRequired(String key) {
				List<String> passwords = disseminationCore.callRegisterPasswordRequiredListener(identity);
				for (Iterator<String> iterator = passwords.iterator(); iterator.hasNext();) {
					String string = iterator.next();
					if(string != null)
						return string;
				}
				return null;
			}
			
			 
			public void PutSuccessful(String key) {
				log("userkey:" + key + "registered in outer DHT");
				//TODO include successful outer registration in connected state
				//disseminationCore.callSuccessfulRegisterListener(identity);
				
			}
			
			 
			public void PutFailed(String key, String error) {
				log("userkey:" + key + "NOT registered in outer DHT");
				//disseminationCore.callFailedRegisterListener(identity, error);
			}
		});
	}

	 
	public SensibleThingsNode getLocalNode() {
		return communication.getLocalSensibleThingsNode();
	}

	 
	public void sendMessage(DHTMessage message)
			throws DestinationNotReachableException {
		if (communication.getState().equals(
				Communication.COMMUNICATION_STATE_CONNECTED)) {
			if (message.getToNode() != null) {
				if (message.getHopCount() < DoubleChordSettings.MAX_HOPCOUNT_FOR_RELAY) {
					message.increaseHopCount();
					communication.sendMessage(message);
					if(message.getType().contains("Response") && message.getRing().equals(INNER_DHT)){
						if(identityCache.containsKey(message.getRequestIdentity())){
							identityCache.get(message.getRequestIdentity()).setOwnerNode(message.getToNode());
						}else{
							DHTEntry entry = new DHTEntry(message.getRequestIdentity(), message.getRequestIdentity(), message.getToNode());
							identityCache.put(message.getRequestIdentity(), entry);
						}
					}
				}
			}
		} else {
			synchronized (backlog) {
				backlog.add(message);
			}
		}
	}

	private void handleBacklog() {
		synchronized (backlog) {
			if (backlog.isEmpty()
					|| !LookupService.STATE_CONNECTED.equals(getState())) {
				return;
			}
			int i = backlog.size();
			for (; i > 0; i--) {
				DHTMessage message = backlog.poll();
				if (message == null) {
					continue;
				}
				try {
					sendMessage(message);
				} catch (DestinationNotReachableException e) {
					// TODO: Silently ignore undelivered messages
				}
			}
		}
	}

	 
	public void sendMessageToIdentity(final String id, final DHTMessage message,
			final FailedMessageDeliveryListener failedMessageDeliveryListener) {
		if(!message.getRing().contentEquals(INNER_DHT)){
			throw new RuntimeException("Why are you sending by ID?");
		}
		//Check if Id is in local storage and valid
		if(identityCache.containsKey(id)){
			DHTEntry entry = identityCache.get(id);
			if(entry.getLastUpdated() + DoubleChordSettings.CACHE_TIMEOUT > System.currentTimeMillis()){
				message.setToNode(entry.getOwnerNode());
				try {
//					log("Send message to id:" + id + "  which belong to:" + entry.getOwnerNode());
					sendMessage(message);
					entry.setLastUpdated();
					return;
				} catch (DestinationNotReachableException e) {
					if(communication.getState().equals(Communication.COMMUNICATION_STATE_CONNECTED)){
						identityCache.remove(id);
					}
				}
			}
		}
		//If not, get new ID
		
//		log("Send message to id:" + id + " message:" + message.toString());
		outerDHT.get(id, new DHTGetResponseListener() {
			
			 
			public void negativeGetResponse(String identity) {
				failedMessageDeliveryListener.onFailedDelivery(identity, message);
				
			}
			
			 
			public void getResponse(String identity, SensibleThingsNode node,
					DHTEntry entry) {
				if(entry != null){
					entry.setLastUpdated();
					if(identityCache.containsKey(id)){
						identityCache.get(id).updateWith(entry);
					}
					else{
						identityCache.put(id, entry);
					}
					message.setToNode(entry.getOwnerNode());
					try {
//						log("Send message to id:" + id + "  which belong to:" + entry.getOwnerNode());
						sendMessage(message);
						
					} catch (DestinationNotReachableException e) {
						identityCache.remove(id);
						failedMessageDeliveryListener.onFailedDelivery(identity, message);
					}
				}else{
					failedMessageDeliveryListener.onFailedDelivery(identity, message);
				}
			}
		});
		
		
	}

	 
	public SensibleThingsNode getBootstrap() {
		return bootstrap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	 
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Double Ring DHT status\n");
		if (innerDHT != null) {
			sb.append("Double_inner: identity=" + keyIdentity + "\n" + "State:").append(getState()).append("\n");
			sb.append("******************Inner DHT********************\n");
			sb.append(innerDHT.toString());
		} 
		if (outerDHT != null) {
			sb.append("Double_outer: identity=" + keyIP + "\n" + "State:").append(getState()).append("\n");
			sb.append("******************Outer DHT********************\n");
			sb.append(outerDHT.toString());
		} 
		if(innerDHT == null && outerDHT == null) {
			sb.append("DoubleChord:\nState:").append(getState()).append("\n");
		}
		else{
		}
		sb.append("*****************local Cache******************\n").append(getCacheString());
		return sb.toString();
	}

	
	private String getCacheString(){
		StringBuilder sb = new StringBuilder();
		for (Iterator<Entry<String, DHTEntry>> iterator = identityCache.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, DHTEntry> type =  iterator.next();
			sb.append(type.getKey()).append(" is located at ").append(type.getValue().getOwnerNode().toString()).append(". Entry expires in ");
			long delta = type.getValue().getLastUpdated()
					+ DoubleChordSettings.CACHE_TIMEOUT
					- System.currentTimeMillis();
			String duration = String.format(
					"%02d:%02d:%02d",
					TimeUnit.MILLISECONDS.toHours(delta),
					TimeUnit.MILLISECONDS.toMinutes(delta)
							- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
									.toHours(delta)),
					TimeUnit.MILLISECONDS.toSeconds(delta)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
									.toMinutes(delta)));
			sb.append(duration).append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * DEBUG
	 * @return
	 */

	public String getInnerSuccessorId() {
		if(innerDHT != null && innerDHT.getFingerTable().get(0) != null)
			return innerDHT.getFingerTable().get(0).getFingerIdentity();
		else
			return "00000XXXXX00000000000000000000XXXXX00000";
	}
	/**
	 * DEBUG
	 * @return
	 */
	public String getOuterSuccessorId() {
		if(outerDHT != null && outerDHT.getFingerTable().get(0) != null)
			return outerDHT.getFingerTable().get(0).getFingerIdentity();
		else
			return "00000XXXXX00000000000000000000XXXXX00000";
	}
	/**
	 * DEBUG
	 * @return
	 */
	public String getInnerPredecessorId() {
		if(innerDHT != null && innerDHT.getPredecessor() != null){
			return innerDHT.getPredecessor().getFingerIdentity();
		}
		return "XXXXX000000000000000000000000000000XXXXX";
	}
	/**
	 * DEBUG
	 * @return
	 */
	public String getOuterPredecessorId() {
		if(outerDHT != null && outerDHT.getPredecessor() != null){
			return outerDHT.getPredecessor().getFingerIdentity();
		}
		return "XXXXX000000000000000000000000000000XXXXX";
	}
	/**
	 * DEBUG
	 * @return
	 */
	public FingerTable getOuterFingerTable() {
		if(outerDHT != null && outerDHT.getFingerTable() != null)
			return outerDHT.getFingerTable().clone();
		else
			return new FingerTable("0000000000000000000000000000000000000000");
	}
	/**
	 * DEBUG
	 * @return
	 */
	public FingerTable getInnerFingerTable() {
		if(innerDHT != null && innerDHT.getFingerTable() != null)
			return innerDHT.getFingerTable().clone();
		else
			return new FingerTable("0000000000000000000000000000000000000000");
	}
	/**
	 * DEBUG
	 * @return
	 */
	public void setIdealFingerTable(FingerTable inner, FingerTable outer) {
		innerDHT.setIdealFingerTable(inner);
		outerDHT.setIdealFingerTable(outer);
	}
	
	/*
	 * Thread Safe implementation of SimpleDateFormat
	 * Each Thread will get its own instance of SimpleDateFormat which will not be shared between other threads. *
	 * Borrowed from: http://javarevisited.blogspot.sg/2012/05/how-to-use-threadlocal-in-java-benefits.html
	 */
	static class PerThreadLogFormatter {

	    private static final ThreadLocal<SimpleDateFormat> dateFormatHolder = new ThreadLocal<SimpleDateFormat>() {

	        /*
	         * initialValue() is called
	         */
	         
	        protected SimpleDateFormat initialValue() {
	            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	        }
	    };
	    //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	    /*
	     * Every time there is a call for DateFormat, ThreadLocal will return calling
	     * Thread's copy of SimpleDateFormat
	     */
	    public static DateFormat getDateFormatter() {
	        return dateFormatHolder.get();
	    }
	    
	    public static class PerThreadFileNameFormatter {

		    private static final ThreadLocal<SimpleDateFormat> dateFormatHolder = new ThreadLocal<SimpleDateFormat>() {

		        /*
		         * initialValue() is called
		         */
		         
		        protected SimpleDateFormat initialValue() {
		            return new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		        }
		    };
		    //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		    /*
		     * Every time there is a call for DateFormat, ThreadLocal will return calling
		     * Thread's copy of SimpleDateFormat
		     */
		    public static DateFormat getDateFormatter() {
		        return dateFormatHolder.get();
		    }
		}
	}
}
