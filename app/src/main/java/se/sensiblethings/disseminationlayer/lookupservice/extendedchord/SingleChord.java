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

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
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
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages.DHTMessage;
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
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class SingleChord extends LookupService implements MessageListener,
		CommunicationStateListener, DHTFeedbackListener, StateChangeListener {

	private static final String SINGLE_DHT = "SINGLE_DHT";
	public static final long MAX_HOPCOUNT_FOR_RELAY = 50; // Assuming near
															// logarithmic
															// efficiency make
															// 50 correspond to
															// 1*10^20 number of
															// nodes in the
															// system
	public static boolean DEBUG = true;

	public void log(String string) {
		if (DEBUG) {
			System.out.println("[E-Lookup " + new Date() + "]\n" + string);
		}
	}

	public static String bootstrapIP = "193.10.119.42";
	public static int defaultBootstrapPort = 45023;
	private MessageDigest digest;

	private SensibleThingsNode bootstrap;
	private SensibleThingsNode IP;
	private String keyIP;
	private DistributedHashTable dht;
	private Queue<DHTMessage> backlog;

	ScheduledExecutorService maintenaceService;
	private ArrayList<DHTEntry> localDataStorage;
	private ConcurrentHashMap<String, String> localPasswordStorage;

	public SingleChord(Communication communication,
			DisseminationCore disseminationCore) {
		super(communication, disseminationCore);
		dhtSettings = new DHTSettings();
		bootstrap = communication.createSensibleThingsNode(bootstrapIP,
				defaultBootstrapPort);
		IP = communication.getLocalSensibleThingsNode();
		backlog = new LinkedList<DHTMessage>();
		localDataStorage = new ArrayList<DHTEntry>();
		localPasswordStorage = new ConcurrentHashMap<String, String>();


		maintenaceService = Executors.newScheduledThreadPool(2);
		// Register messages with post office
		registerMessages();
		// Update state
		setState(STATE_CONNECTING);
		// Add comm state-listener, this will trigger one state update.
		// The IP object is not complete until the communication state is
		// connected.
		communication.addStateListener(this);
		// Initiation continues in onNewCommunicationState
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
	}

	ScheduledFuture<?> checkIdentityCacheForStaleData;
	ScheduledFuture<?> handleBacklog;
	ScheduledFuture<?> checkOuterEntry;
	private DHTSettings dhtSettings;
	private void createMaintenanceThreads() {
//		if (maintenanceThread != null) {
//			return;
//			// runningMaintenance = false;
//			// maintenanceThread.interrupt();maintenanceThread = null;
//		}
//		runningMaintenance = true;
//		maintenanceThread = new Thread(this, "OverlayMaintenaceThread");
//		//ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
//		//kexecutor.scheduleWithFixedDelay(this,DoubleChordSettings.MAINTENANCE_DELAY, DoubleChordSettings.MAINTENANCE_DELAY, TimeUnit.MILLISECONDS);
//		maintenanceThread.start();
//		
		checkIdentityCacheForStaleData = scheduleMaintenance(new Runnable(){
			public void run() {
				if (getState().equals(STATE_CONNECTED)) {
					try {
						checkForStaleData();
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
	}
	
	public ScheduledFuture<?> scheduleMaintenance(Runnable run, long initialDelay, long period, TimeUnit timeUnit){
		return maintenaceService.scheduleAtFixedRate(run, initialDelay, period, timeUnit);
	}

	private void checkForStaleData() {
		Collection<DHTEntry> values = localDataStorage;
		DHTEntry[] valueArray = values.toArray(new DHTEntry[0]);
		for (int i = 0; i < valueArray.length; i++) {
			if ((valueArray[i].getLastUpdated() + dhtSettings.getStoredDataTimeout() / 2) < System
					.currentTimeMillis()) {
				updateRecord(valueArray[i]);
			}
		}
	}

	private void updateRecord(DHTEntry entry) {
		entry.setLastUpdated();
		String password = localPasswordStorage.get(entry.getUci());
		insert(entry.getUci(), password, entry, false);
	}

	@Override
	public void resolve(final String uci) {
		dht.get(sha1hash(uci), new DHTGetResponseListener() {

			public void negativeGetResponse(String identity) {
				disseminationCore.callNegativeResolveListener(uci);
			}

			public void getResponse(String identity, SensibleThingsNode node,
					DHTEntry entry) {
				disseminationCore.callResolveResponseListener(uci,
						entry.getOwnerNode());
			}
		});
	}

	@Override
	public void register(String uci) {
		register(uci, "");

	}

	@Override
	public void register(final String uci, String password) {
		DHTEntry newEntry = new DHTEntry(this.keyIP, uci, getLocalNode());
		insert(uci, password, newEntry, true);
	}

	private void insert(final String uci, final String password,
			final DHTEntry newEntry, final boolean doCoreFeedback) {
		dht.put(sha1hash(uci), newEntry, password, new PutResponseListener() {

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
				if (!localDataStorage.contains(newEntry)) {
					localDataStorage.add(newEntry);
					localPasswordStorage.put(newEntry.getUci(), password);
				}

				if (doCoreFeedback)
					disseminationCore.callSuccessfulRegisterListener(uci);
			}

			public void PutFailed(String key, String error) {
				disseminationCore.callFailedRegisterListener(uci, error);
			}
		});
	}

	@Override
	public void shutdown() {
		setState(STATE_DISCONNECTING);
		checkIdentityCacheForStaleData.cancel(true);
		handleBacklog.cancel(true);
		checkOuterEntry.cancel(true);
		dht.shutdown();
	}

	public void handleMessage(Message message) {
		if (message instanceof DHTMessage) {
			DHTMessage dmessage = (DHTMessage) message;
			if (dmessage.getRing().equals(SINGLE_DHT)) {
				dht.handleMessage(dmessage);
			}

		}
	}

	public void execute(Runnable runner){
		Thread temporary = new Thread(runner, "Temporary Runner");
		temporary.start();
	}
	
	public String sha1hash(String string) {
		try {
			if (digest == null) {
				digest = MessageDigest.getInstance("SHA-1");
			}
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void onNewCommunicationState(Communication communication,
			String state) {
		if (Communication.COMMUNICATION_STATE_CONNECTED.equals(state)) {
			IP = communication.getLocalSensibleThingsNode();
			if (LookupService.STATE_CONNECTING.equals(getState())) {
				log("local address toString:" + IP.toString());
				log("local address:" + IP.getAddress());
				keyIP = sha1hash(IP.getAddress());
				
				dht = new DistributedHashTable(dhtSettings, this, SingleChord.SINGLE_DHT,
						keyIP, this);
			} else if (LookupService.STATE_CONNECTED.equals(getState())) {
				handleBacklog();
			}
		}
	}

	public void stateChanged(DistributedHashTable dht, State newState) {
		if (DistributedHashTable.State.CONNECTED.equals(newState)
				&& LookupService.STATE_CONNECTING.equals(getState())) {
			setState(STATE_CONNECTED);
			createMaintenanceThreads();
		} else if (DistributedHashTable.State.DISCONNECTED.equals(newState)
				&& LookupService.STATE_DISCONNECTING.equals(getState())) {
			setState(STATE_DISCONNECTED);
			maintenaceService.shutdown();
		} else if (DistributedHashTable.State.CONNECTING.equals(newState)
				&& LookupService.STATE_CONNECTED.equals(getState())) {
			setState(STATE_CONNECTING);
		}
	}

	public SensibleThingsNode getLocalNode() {
		return communication.getLocalSensibleThingsNode();
	}

	public void sendMessage(DHTMessage message)
			throws DestinationNotReachableException {
		if (communication.getState().equals(
				Communication.COMMUNICATION_STATE_CONNECTED)) {
			if (message.getToNode() != null) {
				if (message.getHopCount() < MAX_HOPCOUNT_FOR_RELAY) {
					message.increaseHopCount();
					communication.sendMessage(message);
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

	public void sendMessageToIdentity(String id, DHTMessage message,
			FailedMessageDeliveryListener failedMessageDeliveryListener) {
		throw new RuntimeException(
				"sendMessageToIdentity not implemented in single dht");
	}

	public SensibleThingsNode getBootstrap() {
		return bootstrap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (dht != null) {
			return "Single: identity=" + keyIP + "\n" + "State:" + getState()
					+ "\n"
					+ "************************DHT********************\n"
					+ dht.toString()
					+ "*****************localDataStorage*************\n"
					+ getLocalDataString();
		} else {
			return "ExtendedChord: identity=" + keyIP + "\n" + "State:"
					+ getState() + "\n"
					+ "******************Inner DHT********************\n"
					+ getLocalDataString();
		}
	}

	private String getLocalDataString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<DHTEntry> iterator = localDataStorage.iterator(); iterator
				.hasNext();) {
			DHTEntry entry = iterator.next();
			long delta = entry.getLastUpdated()
					+ dhtSettings.getStoredDataTimeout()
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

			sb.append("UCI: " + entry.getUci() + ", key:" + entry.getKey()
					+ ", owner:" + entry.getOwnerNode() + " time left:"
					+ duration + "\n");
		}
		return sb.toString();
	}


}
