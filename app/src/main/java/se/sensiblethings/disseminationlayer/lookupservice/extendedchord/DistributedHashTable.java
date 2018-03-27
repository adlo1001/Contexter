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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import se.sensiblethings.disseminationlayer.communication.DestinationNotReachableException;
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

public class DistributedHashTable {

	

	
	public static enum State {
		CONNECTED, DISCONNECTED, CONNECTING, DISCONNECTING, UNKNOWN
	};

	private static abstract class ResponseHandler {
		abstract void handleResponse(DHTMessage message);

		abstract void handleFailedDelivery(DHTMessage message, Reason reason);
		public enum Reason{IDENTITY_NOT_AVAILABLE, DESTINATION_NOT_AVAILABLE, TIMEOUT_EXCEEDED};
		private String targetIdentity;
		private DHTMessage targetMessage;
		private long timestamp = System.currentTimeMillis();

		void setID(String id) {
			 updateTimestamp();
			targetIdentity = id;
		}

		void setMessage(DHTMessage request) {
			updateTimestamp();
			targetMessage = request;
		}

		String getID() {
			return targetIdentity;
		}

		DHTMessage getMessage() {
			return targetMessage;
		}

		public long getTimestamp() {
			return timestamp;
		}

		private void updateTimestamp() {
			timestamp = System.currentTimeMillis();
		}

		@Override
		public String toString() {
			return "Handler#" + targetMessage.getSerialNumber() + " for "
					+ getID() + " with message "
					+ getMessage().toString() + " last updated on "
					+ new Date(timestamp);
		}
	}

	public static interface PutResponseListener {
		public void PutSuccessful(String key);

		public String passwordRequired(String key);

		public void PutFailed(String key, String error);
	}

	public static interface DHTFeedbackListener {

		SensibleThingsNode getLocalNode();

		void sendMessage(DHTMessage message)
				throws DestinationNotReachableException;

		void sendMessageToIdentity(String id, DHTMessage message,
				FailedMessageDeliveryListener failedMessageDeliveryListener);

		SensibleThingsNode getBootstrap();

		String sha1hash(String uci);

		void execute(Runnable runnable);
		public ScheduledFuture<?> scheduleMaintenance(Runnable run, long initialDelay, long period, TimeUnit timeUnit);
		void log(String string);

	}

	public static interface StateChangeListener {
		void stateChanged(DistributedHashTable dht, State newState);
	}

	// Must be changed if the hash-function is changed
	public static final int BITS_IN_HASH = 160;

	private static final long JOIN_TIMEOUT = 3000; // 3 seconds

	
	
	/*
	 * 
	 * 	private static final long CHECKSUCCESSOR_DELAY = 20*1000;
	 *	private static final long CHECKSUCCESSORTABLE_DELAY = 10*60*1000;
	 *	private static final long CHECKPREDECESSOR_DELAY = 40*1000;
	 *	private static final long CHECKFINGER_DELAY = 1*60*1000;
	 *	private static final long CHECKBOOTSTRAP_DELAY = 10*60*1000;
	 * 
	 * 
	 * 
	 */
	
	
	
	private static final int JOIN_SERIAL_NUMBER = 1;


	private DHTFeedbackListener parent;
	private String currentRing, identity;
	private FingerTable fingerTable; 

	/**
	 * @return the fingerTable
	 */
	final FingerTable getFingerTable() {
		return fingerTable.clone();
	}
	private Map<Integer, ResponseHandler> responseHandlers;
	private List<FingerEntry> successorTable;
	private volatile FingerEntry predecessor; 
	/**
	 * @return the predecessor
	 */
	final FingerEntry getPredecessor() {
		return predecessor.clone();
	}
	private Object predecessorLock =  new Object();
	
	//Remote entries
	private final SortedMap<String, DHTEntry> dataStorage;
	private Map<DHTEntry, String> passwordStorage;
	//Local Entries
	private final ArrayList<DHTEntry> localDataStorage;
	private Map<String, String> localPasswordStorage;
	
	private Deque<Integer> logNEstimation;
	private long timeOfJoinRequest;
	// private long nextStaleData;
	private StateChangeListener stateListener;
	private Random random;

	private State currentState = State.UNKNOWN;

	// private int joinRetries;

	private long lastCheckSuccessor;
	private long lastCheckSuccessorTable;
	private long lastCheckPredecessor;
	private long lastCheckFinger;
	private long lastCheckBootstrap;
	private int currentSerialNumber;

	private DHTSettings settings;

	private ScheduledFuture<?> maintenanceWorker;
	
	public String getCurrentRing(){
		return currentRing;
	}

	public DistributedHashTable(DHTSettings settings, DHTFeedbackListener listener, String ring,
			String key, StateChangeListener stateListener) {
		this.stateListener = stateListener;
		this.settings = settings;
		parent = listener;
		currentRing = ring;
		identity = key;
		fingerTable = new FingerTable(identity);
		dataStorage = new ConcurrentSkipListMap<String, DHTEntry>();
		passwordStorage = new ConcurrentHashMap<DHTEntry, String>(16, 0.9f, 1);
		localDataStorage = new ArrayList<DHTEntry>();
		localPasswordStorage = new ConcurrentHashMap<String, String>(16, 0.9f, 1); //Recommended parameters from the net
		responseHandlers = new ConcurrentHashMap<Integer, ResponseHandler>(16, 0.9f, 1);
		successorTable = new ArrayList<FingerEntry>();
		logNEstimation = new LinkedList<Integer>();
		random = new Random();
		joinDHT();
		setMaintenance();
	}

	private void joinDHT() {
		if(getState().equals(State.DISCONNECTING)){
			return;
		}
		log("Join DHT " + currentRing);
		timeOfJoinRequest = System.currentTimeMillis();
		// joinRetries = 0;
		if (!getState().equals(State.CONNECTING)) {
			setState(State.CONNECTING);
		}

		// If fingertable, join closest one
		// Else try bootstrap
		// Else start self
		FingerEntry myself = new FingerEntry(identity, identity, 0);
		if (!currentRing.equals(DoubleChord.INNER_DHT)) {
			myself.setNode(parent.getLocalNode());
		}
		Join joinMessage = new Join(parent.getLocalNode(), null, currentRing,
				myself);
		joinMessage.setSerialNumber(JOIN_SERIAL_NUMBER);
		FindSuccessor request = new FindSuccessor(parent.getLocalNode(), null,
				currentRing, identity, joinMessage);
		request.setSerialNumber(JOIN_SERIAL_NUMBER);
		FingerEntry entry = fingerTable.getClosestPreceedingFinger(identity);
		if (entry != null) {
			request.setToNode(entry.getNode());
			sendMessage(request, entry.getFingerIdentity(), null);
		} else {
			if (!parent.getLocalNode().equals(parent.getBootstrap())) {
				request.setToNode(parent.getBootstrap());
				sendMessage(request, null, new ResponseHandler() {

					@Override
					public void handleResponse(DHTMessage message) {
						//Response is handled globally
						// if(message instanceof Join){
						// handleJoin((Join)message);
						// }
					}

					@Override
					public void handleFailedDelivery(DHTMessage message, Reason reason) {
						startNewDHT();
					}
				});
			} else {
				startNewDHT();
			}
		}
	}
	
	private void setMaintenance(){
		long[] d = {settings.getCheckbootstrapDelay(),
				settings.getCheckfingerDelay(),
					settings.getCheckpredecessorDelay(),
		settings.getChecksuccessorDelay(),
		settings.getChecksuccessortableDelay(),
		settings.getMessageTimeout(), settings.getStoredDataTimeout()
		};
		
		Arrays.sort(d);
		
		maintenanceWorker = parent.scheduleMaintenance(new Runnable() {
			
			public void run() {
				try {
					doMaintenance();
				} catch (Exception e) {
					System.err.println("Error in dht maintenance: " + e.toString());
					e.printStackTrace();
				}
			}
		}, d[0], d[0], TimeUnit.MILLISECONDS);
		
	}
	
	
	
	
	/**
	 * Sends a DHTmessage to target node if the message has one, otherwise it will relay message through parent to identity 
	 * @param request message to be sent
	 * @param id the identity of the intended recipient
	 * @param responseHandler handler to handle failed delivery and responses. 
	 */
	private void sendMessage(final DHTMessage request, final String id,
			final ResponseHandler responseHandler) {
		// Check serialnumber and add if = 0;
		if (request.getSerialNumber() == 0) {
			request.setSerialNumber(getNextSerialNumber());
		}
		// Check sender identity, add local identity if null
		if (request.getRequestIdentity() == null) {
			request.setRequestIdentity(identity);
		}

		if (request instanceof FindSuccessor) {
			DHTMessage innerMessage = ((FindSuccessor) request).getMessage();
			if (innerMessage != null) {
				if (innerMessage.getSerialNumber() == 0) {
					innerMessage.setSerialNumber(request.getSerialNumber());
				}
				if (innerMessage.getRequestIdentity() == null) {
					innerMessage.setRequestIdentity(request
							.getRequestIdentity());
				}
			}
		}

		// If response handler != null, add handler to list
		if (responseHandler != null && request.getRequestIdentity().equals(identity)) {
			responseHandler.setID(id);
			responseHandler.setMessage(request);
			if (responseHandlers.containsKey(request.getSerialNumber()) && request.getSerialNumber() != 1) {
				throw new RuntimeException("There may only be one handler per request");
			} else {
				responseHandlers.put(request.getSerialNumber(), responseHandler);

			}

		}
		if (request.getRequestIdentity() == null)
			log(request.toString());

		// If has node, send to node. Else send to identity
		if (request.getToNode() != null) {

				try {
					parent.sendMessage(request);
				} catch (DestinationNotReachableException e) {
					if(id != null){
						removeFinger(id);
					}
					if(responseHandler!= null){
						removeHandlers(request);
						log("failed to send message " + request.toString()  + " invoking response handler");
						responseHandler.handleFailedDelivery(request, ResponseHandler.Reason.DESTINATION_NOT_AVAILABLE);
					}
				}
		} else {
			if (id != null) {
				parent.sendMessageToIdentity(id, request,
						new FailedMessageDeliveryListener() {
							public void onFailedDelivery(String identity,
									DHTMessage message) {
								if(responseHandler != null){
									removeHandlers(request);
									responseHandler.handleFailedDelivery(message, ResponseHandler.Reason.DESTINATION_NOT_AVAILABLE);
								}else{
									removeFinger(id);
								}
								
							}
						});
			} else {
				if (responseHandler != null) {
					responseHandler.handleFailedDelivery(request, ResponseHandler.Reason.DESTINATION_NOT_AVAILABLE);
				}
			}
		}
	}

	private synchronized int getNextSerialNumber() {
		++currentSerialNumber;
		if (currentSerialNumber == 0 || currentSerialNumber == 1) {
			currentSerialNumber = 2;
		}
		return currentSerialNumber;
	}

	private void invokeHandlers(DHTMessage message) {
		if (!identity.equals(message.getRequestIdentity())) {
			return;
		}
		int serialNumber = message.getSerialNumber();
		ResponseHandler responseHandler = responseHandlers.remove(serialNumber);
		if(responseHandler != null){
			responseHandler.handleResponse(message);
		}
	}

	private void removeHandlers(DHTMessage message) {
		if(message.getRequestIdentity().equals(identity)){
			int serialNumber = message.getSerialNumber();
			responseHandlers.remove(serialNumber);
		}
	}

	protected void startNewDHT() {
		if(getState().equals(State.CONNECTING)){
		FingerEntry self = new FingerEntry(identity, identity, 0);
		if (!currentRing.equals(DoubleChord.INNER_DHT)) {
			self.setNode(parent.getLocalNode());
		}
		setPredecessorIfBetter(null);
		setSuccessor(self);
		setState(State.CONNECTED);
		}

	}

	private synchronized void setState(final State state) {
		this.currentState = state;
		parent.execute(new Runnable(){
			public void run() {
				try {
					Thread.sleep(15);
				} catch (InterruptedException ignore) {
				};
				stateListener.stateChanged(DistributedHashTable.this, state);
			}
			
		});
		
	}

	public State getState() {
		return this.currentState;
	}

	public void get(String key, DHTGetResponseListener listener) {
		if(key == null){
			throw new RuntimeException("requested key may not be null");
		}
		DHTEntry entry;
		synchronized (dataStorage) {
			entry = dataStorage.get(key);
		}
		if (entry != null) {
			if (listener != null) {
				listener.getResponse(key, parent.getLocalNode(), entry);
			}
		} else {
			Get get = new Get(parent.getLocalNode(), null, currentRing,
					key);
			findSuccessor(key, get, new GetResponseHandler(key, get, listener));
		}

	}

	private final class GetResponseHandler extends ResponseHandler {
		private final String key;
		private final Get get;
		private final DHTGetResponseListener listener;
		private int retries;

		private GetResponseHandler(String key, Get get,
				DHTGetResponseListener listener) {
			this.key = key;
			this.get = get;
			this.listener = listener;
			this.retries = 0;
		}

		@Override
		public void handleResponse(DHTMessage message) {
			if (message instanceof GetResponse) {
				GetResponse response = (GetResponse) message;
				DHTEntry entry = response.getEntry();
				if (entry != null){
					listener.getResponse(key, response.getFromNode(),
							entry);
				}
				else{
					listener.negativeGetResponse(key);
				}
			}
		}

		@Override
		public void handleFailedDelivery(DHTMessage message, Reason reason) {
			if (message instanceof FindSuccessor) {
				if(reason.equals(Reason.TIMEOUT_EXCEEDED)){
					return;
				}else if (++retries < settings.getMaxTransmissionRetries()){
					get.setSerialNumber(0);
					findSuccessor(key, get, this);
					return;
				}
			} 
			listener.negativeGetResponse(key);
		}
	}
	
	public void put(String keyIdentity, DHTEntry newEntry,
			PutResponseListener listener) {
		put(keyIdentity, newEntry, "", listener);
	}

	public void put(String key, DHTEntry newEntry,
			String password, PutResponseListener listener) {
		Put request = new Put(parent.getLocalNode(), null, currentRing, key,
				newEntry, password);
		findSuccessor(key, request, new PutHandler(key, newEntry, password, listener, request));
	}
	
	private final class PutHandler extends ResponseHandler {
		private final Put request;
		private final String password;
		private final DHTEntry newEntry;
		private final String key;
		private final PutResponseListener listener;
		private int failedAttempts = 0;

		private PutHandler(String key, DHTEntry newEntry, String password,
				PutResponseListener listener, Put request) {
			this.request = request;
			this.password = password;
			this.newEntry = newEntry;
			this.key = key;
			this.listener = listener;
			failedAttempts = 0;
		}

		@Override
		public void handleResponse(DHTMessage message) {
			if (message instanceof PutResponse) {
				PutResponse response = (PutResponse) message;
				if (response.isSuccessful()) {
					synchronized (localDataStorage) {
						if (!localDataStorage.contains(newEntry)) {
							localDataStorage.add(newEntry);
							localPasswordStorage.put(newEntry.getUci(),
									password);
						}
						listener.PutSuccessful(key);
					}
				} else if (response.passwordRequired()) {
					String newPassword = listener.passwordRequired(response
							.getKey());
					if (newPassword != null
							&& !newPassword.equals(password)) {
						put(key, newEntry, newPassword, listener);
					} else {
						listener.PutFailed(key, "Wrong password");
					}
				} else {
					listener.PutFailed(key,
							"Record not found in lookup service");
				}
			}
		}

		@Override
		public void handleFailedDelivery(DHTMessage message, Reason reason) {
			if(reason.equals(Reason.TIMEOUT_EXCEEDED)){
				findSuccessor(key, request, this);
			}else if(reason.equals(Reason.DESTINATION_NOT_AVAILABLE)){
				if(!fingerTable.isEmpty() && ++failedAttempts < settings.getMaxTransmissionRetries()){
					findSuccessor(key, request, this);
					log("resending request " + request.toString() + " to " + key);
				}
				else{
					//Ignore request and drop it
					//throw new RuntimeException("Max retransmission exceeded");
				}
			}
		}
	}

	private boolean isPasswordOk(DHTEntry newEntry, String password) {
		if (password == null) {
			return false;
		}
		if (passwordStorage.containsKey(newEntry)) {
                    return password.equals(passwordStorage.get(newEntry));
		} else {
			passwordStorage.put(newEntry, password);
			return true;
		}
	}

	private void findSuccessor(String hash, DHTMessage message,
			final ResponseHandler handler) {
		FingerEntry successor = getSuccessor();
		if(hash == null){
			throw new RuntimeException("Hash may not be null");
		}
		
		if (message != null && message.getRequestIdentity() == null) {
			message.setRequestIdentity(identity);
			message.setSerialNumber(getNextSerialNumber());
		}

		if (successor != null && FingerEntry.isBetween(identity, hash, successor.getFingerIdentity())) {
			if (message != null) {
				message.setToNode(successor.getNode());
				sendMessage(message, successor.getFingerIdentity(), handler);
			} else {
				FindSuccessorResponse response = new FindSuccessorResponse(
						parent.getLocalNode(), parent.getLocalNode(),
						currentRing, hash, successor.clone());
				handler.handleResponse(response);
			}
		} else {
			FingerEntry peer = fingerTable.getClosestPreceedingFinger(hash);
			if(peer == null){
				peer = new FingerEntry(identity,null,0);
				peer.setNode(parent.getBootstrap());
			}else if(peer.getFingerIdentity().equals(identity)){
					
			}
			
			FindSuccessor request = new FindSuccessor(parent.getLocalNode(),
					peer.getNode(), currentRing, hash, message);
			if (message != null) {
				request.setRequestIdentity(identity);
				request.setSerialNumber(message.getSerialNumber());
			}
			sendMessage(request, peer.getFingerIdentity(), handler);
		}
	}

	

	private void getPredecessor(final FingerEntry successor,
			final ResponseHandler handler) {
		String targetIdentity = successor.getFingerIdentity();
		GetPredecessor request = new GetPredecessor(parent.getLocalNode(),
				successor.getNode(), currentRing, targetIdentity);
		sendMessage(request, targetIdentity, handler);
	}

	private void getSuccessor(final FingerEntry query,
			final ResponseHandler handler) {
		String queryIdentity = query.getFingerIdentity();
		GetSuccessor request = new GetSuccessor(parent.getLocalNode(),
				query.getNode(), currentRing, queryIdentity);
		sendMessage(request, queryIdentity, handler);
	}

	public void handleMessage(DHTMessage message) {
		if (!message.getRing().equals(currentRing)) {
			return;
		}
		updateLogN(message.getHopCount());

		if (message instanceof FindSuccessor) {
			handleFindSuccessor((FindSuccessor) message);
		} else if (message instanceof FindSuccessorResponse) {
			handleFindSuccessorResponse((FindSuccessorResponse) message);
		} else if (message instanceof RequestFullFingerTable) {
			handleRequestFullFingerTable((RequestFullFingerTable) message);
		} else if (message instanceof RequestFullFingerTableResponse) {
			handleRequestFullFingerTableResponse((RequestFullFingerTableResponse) message);
		} else if (message instanceof GetSuccessorTable) {
			handleGetSuccessorTable((GetSuccessorTable) message);
		} else if (message instanceof GetSuccessorTableResponse) {
			handleGetSuccessorTableResponse((GetSuccessorTableResponse) message);
		} else if (message instanceof Join) {
			handleJoin((Join) message);
		} else if (message instanceof JoinResponse) {
			handleJoinResponse((JoinResponse) message);
		} else if (message instanceof Put) {
			handlePut((Put) message);
		} else if (message instanceof PutResponse) {
			handlePutResponse((PutResponse) message);
		} else if (message instanceof Leave) {
			handleLeave((Leave) message);
		} else if (message instanceof GetPredecessor) {
			handleGetPredecessor((GetPredecessor) message);
		} else if (message instanceof GetPredecessorResponse) {
			handleGetPredecessorResponse((GetPredecessorResponse) message);
		} else if (message instanceof UpdatePredecessor) {
			handleUpdatePredecessor((UpdatePredecessor) message);
		} else if (message instanceof GetSuccessor) {
			handleGetSuccessor((GetSuccessor) message);
		} else if (message instanceof GetSuccessorResponse) {
			handleGetSuccessorResponse((GetSuccessorResponse) message);
		} else if (message instanceof Get) {
			handleGet((Get) message);
		} else if (message instanceof GetResponse) {
			handleGetResponse((GetResponse) message);
		}else if(message instanceof DataSync){
			handleDataSync((DataSync) message);
		}else if(message instanceof UpdateSuccessor){
			handleUpdateSuccessor((UpdateSuccessor) message);
		}
		else {
			System.err.println("DEBUG: Lookupservice didn't handle a "
					+ message.getClass().getName() + " message");
		}
	}

	private void handleDataSync(DataSync message) {
		mergeRemoteData(message.getData());
	}

	private void handleGetResponse(GetResponse message) {
		invokeHandlers(message);
	}

	private void handleGet(Get message) {
		 log("Get message for:" + message.getRequestedKey());
		String key = message.getRequestedKey();
		DHTEntry entry = null;
		synchronized (dataStorage) {
			if (dataStorage.containsKey(key)) {
				log("Datastore contained identity");
				entry = dataStorage.get(key);
			}
		}
		GetResponse response = new GetResponse(parent.getLocalNode(),
				message.getFromNode(), currentRing, key, entry);
		response.setSerialNumber(message.getSerialNumber());
		response.setRequestIdentity(message.getRequestIdentity());
		sendMessage(response, message.getRequestIdentity(), null);
	}
	
	/**
	 * 		If myID<soughtID<mySuccessor
	 *		then if internal message
	 *		then Unpack message and send to my successor
	 *		else return mySuccessor to requester
	 *		else send request to closest finger.
	 * @param message
	 */
	private void handleFindSuccessor(FindSuccessor message) {
		FingerEntry successor = getSuccessor();

		//This checks if this is a getmessage that we know the answer to, then we shortcircut the request and reply.
		if(message.hasMessage() && message.getMessage() instanceof Get){
			Get get =  (Get) message.getMessage();
			synchronized (dataStorage) {
				if (dataStorage.containsKey(get.getRequestedKey())) {
					handleGet(get);
					return;
				}
			}
		}
		
		
		if (FingerEntry.isBetween(identity, message.getKey(),
				successor.getFingerIdentity())) {//TODO find and fix null-pointer
			if (message.hasMessage()) {

				DHTMessage request = message.getMessage();
				request.setToNode(successor.getNode());
				log("FindSuccessor for " + message.getKey()
						+ " have internal message " + request.toString()
						+ " forwarding now.");
				sendMessage(request, successor.getFingerIdentity(), null);
			} else {
				FindSuccessorResponse response = new FindSuccessorResponse(
						parent.getLocalNode(), message.getFromNode(),
						currentRing, message.getKey(), successor);
				response.setSerialNumber(message.getSerialNumber());
				response.setRequestIdentity(message.getRequestIdentity());
				sendMessage(response, null, null);
			}
		} else {
			FingerEntry peer = fingerTable.getClosestPreceedingFinger(message
					.getKey());
			if(peer != null){
			message.setToNode(peer.getNode());
			log("FindSuccessor for " + message.getKey()
					+ " are not for my successor, forwarding "
					+ message.toString() + "to best finger:" + peer.toString());
			sendMessage(message, peer.getFingerIdentity(), null);
			}
			else{
				message.setToNode(successor.getNode());
				sendMessage(message, successor.getFingerIdentity(), null);
			}
		}
	}


	private void Stabilize() {
		long currentStablilization = System.currentTimeMillis();
		 
		if (lastCheckSuccessor + settings.getChecksuccessorDelay()< currentStablilization) {
			log("Stabilization - checking successor");
			checkSuccessor();
			lastCheckSuccessor = currentStablilization;
		}
		if (lastCheckPredecessor + settings.getCheckpredecessorDelay() < currentStablilization) {
			log("Stabilization - checking predecessor");
			fixPredecessor();
			lastCheckPredecessor = currentStablilization;
		}
		if (lastCheckFinger + settings.getCheckfingerDelay() < currentStablilization) {
			fixNextFinger();
			lastCheckFinger = currentStablilization;
		}
		if (lastCheckSuccessorTable + settings.getChecksuccessortableDelay() < currentStablilization) {
			updateSuccessorTable(fingerTable.get(0));
			lastCheckSuccessorTable = currentStablilization;
		}
		
		if (lastCheckBootstrap + settings.getCheckbootstrapDelay() < currentStablilization) {
			askBootstrapForSuccessor();
			lastCheckBootstrap = currentStablilization;
		}
		



	}

	private void cleanRequests() {
		if (responseHandlers.isEmpty()) {
			return;
		}
		for (Iterator<ResponseHandler> iterator = responseHandlers
				.values().iterator(); iterator.hasNext();) {
			ResponseHandler responseHandler = iterator.next();
			if (responseHandler.getTimestamp() + DHTSettings.MESSAGE_TIMEOUT < System
					.currentTimeMillis()) {
				log("found timed out request: "
						+ responseHandler.toString());
				iterator.remove();
			}
		}
	}
	
	private void askBootstrapForSuccessor(){
		log("probing bootstrap");
		GetPredecessor innerRequest = new GetPredecessor(parent.getLocalNode(),
				null, currentRing, identity);
		FindSuccessor request = new FindSuccessor(parent.getLocalNode(), parent.getBootstrap(), currentRing, identity, innerRequest);
		sendMessage(request, null, null);
	}

	private void checkSuccessor() {
		log("Check successor");
		synchronized (fingerTable) {
			final FingerEntry successor = fingerTable.get(0);
			if (successor != null) {
				log("checking predecessor of: " + successor);
				getPredecessor(successor, new CheckSuccessorHandler(successor));
			} else {
				if (!fingerTable.isEmpty()) {

					FingerEntry finger = fingerTable
							.getClosestPreceedingFinger(identity);
					log("Successor was null, promoting new successor: "
							+ finger);
					removeFinger(finger.getFingerIdentity());
					setSuccessor(finger);
				} else {
					if (getState().equals(State.CONNECTED)) {
						joinDHT();
					}
				}

			}
		}
	}
	private final class CheckSuccessorHandler extends ResponseHandler {
		private FingerEntry tempSuccessor;

		private CheckSuccessorHandler(FingerEntry successor) {
			this.tempSuccessor = successor;
		}

		@Override
		void handleResponse(DHTMessage message) {
			if (message instanceof GetPredecessorResponse) {
				GetPredecessorResponse response = (GetPredecessorResponse) message;
				FingerEntry remotePredecessor = response.getPredecessor();
				log(response.getSuccessorIdentity() + " have " + remotePredecessor + " as predecessor");
				synchronized(fingerTable){
					FingerEntry successor = fingerTable.get(0);
					//If null, suggest us
					//Else if we are suggesting ourself to ourself while having ourself as a successor, abort!
					//else if predecessor is a better successor, update successor and suggest us.
					//else if predecessor is not us, suggest us
					if (remotePredecessor == null) {
						log("Suggesting myself as predecessor");
						notifyFingerOfNewPredecessorCandidate(successor);
					} else if(fingerTable.get(0) != null && fingerTable.get(0).getFingerIdentity().equals(identity) && remotePredecessor.getFingerIdentity().equals(identity)){
						return;
					}else if (fingerTable.get(0) == null || fingerTable.get(0).getFingerIdentity().equals(identity) || FingerEntry.isBetween(identity,
							remotePredecessor.getFingerIdentity(),
							fingerTable.get(0).getFingerIdentity())) {
						log("Found a better successor");
						setSuccessor(remotePredecessor);
						checkSuccessor();//We got a new successor, lets check if we can find a better one
					} else if(!identity.equals(remotePredecessor.getFingerIdentity())){
						log("Suggesting myself as predecessor");
						notifyFingerOfNewPredecessorCandidate(successor);
					}
				}
			} else {
				log("handler-cast error, missmatched handler invocation?");
			}
			
		}

		@Override
		void handleFailedDelivery(DHTMessage message, Reason reason) {
			if(tempSuccessor != null){
				if(reason.equals(Reason.TIMEOUT_EXCEEDED) || reason.equals(Reason.DESTINATION_NOT_AVAILABLE)){
					removeSuccessor(tempSuccessor);
				}
			}
		}
	}

	private void notifyFingerOfNewPredecessorCandidate(FingerEntry successor) {
		// Updating successor on new predecessor
		if (successor != null) {
			FingerEntry finger = new FingerEntry(identity, identity, 0);
			if (!currentRing.equals(DoubleChord.INNER_DHT)) {
				finger.setNode(parent.getLocalNode());
			}
			updatePredecessor(successor.getFingerIdentity(),
					successor.getNode(), finger);
		}
	}

	private void fixPredecessor() {
		if (predecessor == null) {
			return;
		}
		log("Fix predecessor");

		getSuccessor(predecessor, new ResponseHandler() {

			@Override
			public void handleFailedDelivery(DHTMessage successor, Reason reason) {
				if(reason.equals(Reason.TIMEOUT_EXCEEDED) || reason.equals(Reason.DESTINATION_NOT_AVAILABLE)){
					log("Failed to contact predecessor, removing entry");
					setPredecessorIfBetter(null);
				}
				

			}

			@Override
			public void handleResponse(DHTMessage message) {
				if(message instanceof GetSuccessorResponse){
					//Here we could at least theoretically look at the remote successor and incorporate it in our table. 
				}
			}

		});
	}
	private int lastCheckedIndex = BITS_IN_HASH;

	 
	private void fixNextFinger() {
		log("Stabilization - fixing finger");

		if (fingerTable.isEmpty()) {
			if(getState().equals(State.CONNECTED)){
				joinDHT();
			}
			return;
		}
		// Generate a random valid index by first find which index our successor
		// would have to exclude low indexes
		FingerEntry successor = fingerTable.get(0).clone();
		int index = 1;
		if (successor != null) {
			successor.updateIndex(1);
			index = successor.getIndex();
			log("Lower index bound is: " + index);
		}
		if(lastCheckedIndex < index){
			lastCheckedIndex = BITS_IN_HASH;
			log("Resetting last checked index");
		}
		// Check if selected index is in table or create new entry
		FingerEntry finger = fingerTable.get(--lastCheckedIndex);
		if (finger == null) {
			finger = new FingerEntry(identity, null, lastCheckedIndex);
		}
		// Update Finger
		log("Fixing finger: " + finger);
		updateFinger(finger);

		// Update an existing finger finger with list index
		if (fingerTable.size() < 2) {

			return;
		}
		index = random.nextInt(fingerTable.size() - 1);
		finger = fingerTable.getTabledFinger(index + 1);
		log("Fixing finger with table index " + index +":"+ finger);
		updateFinger(finger);
		
		int logN = getLogN();
		synchronized (successorTable) {
			if (successorTable.size() > logN) {

				Collections.sort(successorTable,
						new FingerEntry.BaseIdentityComparator(identity));
				successorTable = successorTable.subList(0, logN);

			} else {
				updateSuccessorTable(successor);
			}
		}
	}

	private void updatePredecessor(String target,
			SensibleThingsNode targetNode, FingerEntry candidate) {
			UpdatePredecessor message = new UpdatePredecessor(
					parent.getLocalNode(), targetNode, currentRing, candidate);
			sendMessage(message, target, null);
	}

	private void handleFindSuccessorResponse(FindSuccessorResponse message) {
		invokeHandlers(message);
	}

	/**
	 * Helper function, correctly adds new successor to successor table
	 * 
	 * @param newSuccessor
	 */
	private void setSuccessor(final FingerEntry successor) {
		if (successor == null) {
			return;
		}
		FingerEntry newSuccessor;
		synchronized (fingerTable) {
			log("FingerTableS1: " + fingerTable);
			newSuccessor = successor.clone();
			newSuccessor.setParentIdentity(identity);
			newSuccessor.updateIndex(0);
			//Debug
			if (settings.isDebug() && fingerTable.get(0) != null
					&& FingerEntry.isBetween(identity, fingerTable.get(0)
							.getFingerIdentity(), newSuccessor
							.getFingerIdentity())) {
				log("Replacing 'better' successor:" + fingerTable.get(0)
						+ " with " + newSuccessor);
			}
			log("Setting successor:" + newSuccessor);
			if (fingerTable.contains(newSuccessor.getFingerIdentity())) {
				log("Successor already exists - updating record");
				String id = newSuccessor.getFingerIdentity();
				FingerEntry entry = fingerTable.get(id);
				entry.updateIndex(0);
				//entry.merge(successor);
				entry.setNode(newSuccessor.getNode());
				fingerTable.remove(id);
				fingerTable.put(entry);
				log("FingerTableS2: " + fingerTable);

			} else {
				log("Successor did not exist");
				FingerEntry newFinger = fingerTable.get(0);
				if (newFinger != null) {
					newFinger = newFinger.clone();
				}
				fingerTable.put(newSuccessor);
				log("FingerTableS3: " + fingerTable);
				log("Trying " + newFinger + " as a potential finger");
				if (newFinger != null
						&& !newFinger.getFingerIdentity().equals(identity)) {

					newFinger.updateIndex(160);
					log("Inserting old successor as finger:" + newFinger);
					insertFingerIfBetter(newFinger);
				} else {
					if (newFinger != null) {
						log("Finger was rejected");
					}
				}

				// log("Successor "+ successor + "inserted into finger table");
			}
		}
		synchronized (successorTable) {
			if (successorTable.contains(newSuccessor)) {
				successorTable.remove(newSuccessor);
			}
		}
		
		notifyFingerOfNewPredecessorCandidate(newSuccessor);
		
		checkSuccessor();

	}

	private void updateSuccessorTable(FingerEntry successor) {
		if(successor == null){
			return;
		}
		getSuccessor(successor, new ResponseHandler() {
			
			@Override
			void handleResponse(DHTMessage message) {
				if (message instanceof GetSuccessorResponse) {
					GetSuccessorResponse response = (GetSuccessorResponse) message;
					FingerEntry recursiveSuccessor = response.getSuccessor();
					synchronized (fingerTable) {
						synchronized (successorTable) {
							if (recursiveSuccessor != null) {
								if (!successorTable.contains(recursiveSuccessor)
										&& !recursiveSuccessor.getFingerIdentity().equals(identity)) {
									if (!fingerTable.contains(recursiveSuccessor.getFingerIdentity())) {
										successorTable.add(recursiveSuccessor.clone());
										if (successorTable.size() < getLogN()) {
											getSuccessor(recursiveSuccessor,this);
										}
									} else {
										if (fingerTable.contains(recursiveSuccessor.getFingerIdentity())) {
											if (fingerTable.get(recursiveSuccessor.getFingerIdentity()).getIndex() != 0) {// TODO: Nullpointer!!
												successorTable.add(recursiveSuccessor.clone());
												if (successorTable.size() < getLogN()) {
													getSuccessor(recursiveSuccessor, this);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			@Override
			void handleFailedDelivery(DHTMessage message, Reason reason) {
				// Stop adding new successors for now
			}
		});
	}
	
	private FingerEntry getSuccessor(){
		FingerEntry currentSuccessor = fingerTable.get(0);
		if (currentSuccessor == null) {
			synchronized (successorTable) {
				if (!successorTable.isEmpty()) {
					currentSuccessor = successorTable.get(0);
				} else if (!fingerTable.isEmpty()) {
					currentSuccessor = fingerTable.getClosestPreceedingFinger(identity);
				} else {
					currentSuccessor = new FingerEntry(identity, null, 0);
					currentSuccessor.setNode(parent.getBootstrap());
				}
			}
                        if(currentSuccessor == null){
                            currentSuccessor = new FingerEntry(identity,null,0);
                            currentSuccessor.setNode(parent.getBootstrap());
                        }
		}
		return currentSuccessor;
	}
	private void removeSuccessor(FingerEntry successor){
		if(successor == null){
			return;
		}
		synchronized (fingerTable) {
			FingerEntry currentSuccessor = fingerTable.get(0);
			if (successor.equals(currentSuccessor)) {
				fingerTable.remove(successor);
				if (!successorTable.isEmpty()) {
					synchronized (successorTable) {
						FingerEntry newsuccessor = successorTable.get(0);
						setSuccessor(newsuccessor.clone());
						successorTable.remove(newsuccessor);
					}
				} else if (!fingerTable.isEmpty()) {
					setSuccessor(fingerTable
							.getClosestPreceedingFinger(identity));
				} else {
					if (getState().equals(State.CONNECTED)) {
						joinDHT();
					}
				}
			} else {

			}
		}
	}

	/*
	 * Direct message
	 */
	private void handleRequestFullFingerTable(RequestFullFingerTable message) {
		// log("Full fingertable requested, sending fingertable");
		RequestFullFingerTableResponse response = new RequestFullFingerTableResponse(
				parent.getLocalNode(), message.getFromNode(), currentRing,
				fingerTable.clone());
		response.setSerialNumber(message.getSerialNumber());
		response.setRequestIdentity(message.getRequestIdentity());
		sendMessage(response, message.getRequestIdentity(), null);
	}

	/*
	 * Direct message
	 */
	private void handleRequestFullFingerTableResponse(
			RequestFullFingerTableResponse message) {
		log("Full fingertable recieved, merging into fingertable");
		// Foreign fingers
		FingerTable foreignFingerTable = message.getFingerTable();
		fingerTable.addAll(foreignFingerTable);
		log("Full fingertable merged");
	}

	/*
	 * 
	 */
	private void updateFinger(final FingerEntry fingerEntry) {
		if (fingerEntry == null || fingerEntry.getIndex() == 0 || identity.equals(fingerEntry.getFingerIdentity())) {
			log("Trying to update successor, a null finger or selfreference using updateFinger maintenance, ignoring");
			return;
		}
		log("Beginning update for finger:" + fingerEntry.toString());
		findSuccessor(fingerEntry.getIdealFingerIdentity(), null,
				new ResponseHandler() {
					@Override
					public void handleResponse(DHTMessage message) {

						if (message instanceof FindSuccessorResponse) {
							
							FindSuccessorResponse response = (FindSuccessorResponse) message;
							FingerEntry finger = response.getSuccessor();
							finger.setParentIdentity(identity);
							log("Finger found: " + finger);
							finger.updateIndex(160);
							insertFingerIfBetter(finger);
						}
					}

					@Override
					public void handleFailedDelivery(DHTMessage message, Reason reason) {
						log("finger " + fingerEntry.toString()
								+ " is not responding");
					}
				});
	}
	
	private void removeFinger(String identity){
		log("Removing finger:" + identity);
		if (identity == null) {
			throw new RuntimeException("Dy ska ikke kasta NULL");
		}
		synchronized (fingerTable) {
			FingerEntry successor = fingerTable.get(0);
			if (predecessor != null
					&& identity.equals(predecessor.getFingerIdentity())) {
				log("finger was predecessor, clearing");
				setPredecessorIfBetter(null);
			} else if (successor != null
					&& successor.getFingerIdentity().equals(identity)) {
				log("finger was successor, removing");
				removeSuccessor(successor);
			} else if (fingerTable.contains(identity)) {
				log("finger was in fingertable, removing");
				fingerTable.remove(identity);
			} else {
				synchronized (successorTable) {
					for (ListIterator<FingerEntry> iterator = successorTable
							.listIterator(); iterator.hasNext();) {
						FingerEntry successorEntry = iterator.next();
						if (successorEntry.getFingerIdentity().equals(iterator)) {
							log("finger was in successortable, removing");
							successorTable.remove(successorEntry);
							return;
						}
					}
				}
			}
		}
	}
	

	private void handleGetSuccessor(GetSuccessor message) {
		FingerEntry successor = fingerTable.get(0);
		if (successor == null) {
			successor = new FingerEntry(identity, identity, 0);
			if (!currentRing.equals(DoubleChord.INNER_DHT)) {
				successor.setNode(parent.getLocalNode());
			}
		}
		GetSuccessorResponse response = new GetSuccessorResponse(
				parent.getLocalNode(), message.getFromNode(), currentRing,
				message.getRequestingIdentity(), successor);
		response.setSerialNumber(message.getSerialNumber());
		response.setRequestIdentity(message.getRequestIdentity());
		sendMessage(response, message.getRequestIdentity(), null);
	}

	private void handleGetSuccessorResponse(GetSuccessorResponse message) {
		invokeHandlers(message);

	}

	private void handleGetSuccessorTable(GetSuccessorTable message) {
		// log("Successor table requested");
		FingerEntry[] table;
		synchronized (successorTable) {
			table = successorTable.toArray(new FingerEntry[0]);
		}
		GetSuccessorTableResponse response = new GetSuccessorTableResponse(
				parent.getLocalNode(), message.getFromNode(), currentRing,
				table);
		response.setSerialNumber(message.getSerialNumber());
		response.setRequestIdentity(message.getRequestIdentity());
		sendMessage(response, message.getRequestIdentity(), null);
	}

	private void handleGetSuccessorTableResponse(
			GetSuccessorTableResponse message) {
		// log("Successor table recieved");
		FingerEntry[] foreignSuccessorTable = message.getSuccessorTable();
		synchronized (successorTable) {
		int originalSize = successorTable.size();
			for (int i = 0; i < foreignSuccessorTable.length; i++) {
				FingerEntry fingerEntry = foreignSuccessorTable[i];
				if (!fingerTable.contains(fingerEntry.getFingerIdentity()) && !fingerEntry.getFingerIdentity().equals(identity)) {
					successorTable.add(fingerEntry);
				}
			}
		
			if (originalSize != successorTable.size()) {
				Collections.sort(successorTable,
						new FingerEntry.BaseIdentityComparator(identity));
				int logN = getLogN();
				if (successorTable.size() > logN) {
					successorTable = successorTable.subList(0, logN);
				}
			}
		}
	}

	private void handleJoin(Join message) {
		log("Join message recieved");
		FingerEntry key = message.getIdentity();
		log("handle join message - for" + key.toString());
		key.setParentIdentity(identity);
		key.updateIndex(160);
		
		synchronized (fingerTable) {
			if (fingerTable.get(0) == null
					|| FingerEntry.isBetween(identity, key.getFingerIdentity(),
							fingerTable.get(0).getFingerIdentity())) {
				setSuccessor(key);
			} else {
				insertFingerIfBetter(key);
			}
		}
		Map<DHTEntry, String> data;
		synchronized (dataStorage) {
			Collection<DHTEntry> values = dataStorage.values();
			data = getPasswords(values.toArray(new DHTEntry[0]));
		}
		JoinResponse response = new JoinResponse(parent.getLocalNode(),
				message.getFromNode(), currentRing, identity, data, predecessor);
		response.setRequestIdentity(message.getRequestIdentity());
		response.setSerialNumber(message.getSerialNumber());
		sendMessage(response, message.getRequestIdentity(), null);
		
		//This optimization may create several circles instead. 
//		if(predecessor == null || predecessor.getFingerIdentity().equals(identity) || FingerEntry.isBetween(predecessor.getFingerIdentity(), key.getFingerIdentity(), identity)){
//			setPredecessor(key.clone());
//		}
	}

	/**
	 * @param key
	 */
	private void setPredecessorIfBetter(FingerEntry candidate) {
		if(candidate == null){
			return;
		}
		synchronized (predecessorLock ) {
			if (predecessor == null) {
				log("Update predecessor - have no predecessor, accepting new proposal: "
						+ candidate);
				predecessor = candidate;
				return;
			} else {
				log("Update predecessor - testing candidate:" + candidate);
				//			 log("initially comparing:\n" + predecessor.getFingerIdentity() +
				//			 " and\n"
				//			 + candidate.getFingerIdentity() + " and\n" + identity);
				if (!candidate.equals(predecessor)
						&& FingerEntry.isBetween(
								predecessor.getFingerIdentity(),
								candidate.getFingerIdentity(), identity)) {
					//				 log("comparing:\n" + predecessor.getFingerIdentity() +
					//				 " and\n" + candidate.getFingerIdentity());
					//				 log("comparing:\n" + candidate.getFingerIdentity() + " and\n"
					//				 + identity);
					log("Update predecessor - predecessor accepted");
					predecessor = candidate;
					return;
				}
				log("Update predecessor - predecessor rejected");
			}
		}
		sendData(candidate.getFingerIdentity(),candidate.getNode());
		checkSuccessor();
	}

	private void insertFingerIfBetter(FingerEntry key) {
		log("Inserting finger:" + key + " if better");
//		log("FingerTable:"  + fingerTable);
		key.updateIndex(160);
		log("recalculated index:" + key);
		synchronized (fingerTable) {
			if (idealFingerTable != null) {
				if (idealFingerTable.contains(key.getIndex())) {
					if (idealFingerTable.get(key.getIndex()).equals(key)) {
						log("Found ideal finger: " + key);
					} else {
						log("Ideal finger for index is: "
								+ idealFingerTable.get(key.getIndex()));
					}
				} else {
					log("Ideal table does not contain index: " + key.getIndex());
				}
			}
			if (key != null && !fingerTable.contains(key.getFingerIdentity())) {
				if ((!fingerTable.contains(key.getIndex()) || FingerEntry
						.isBetween(key.getIdealFingerIdentity(), key
								.getFingerIdentity(),
								fingerTable.get(key.getIndex())
										.getFingerIdentity()))) {
					fingerTable.put(key);
					log("Finger" + key + " inserted.");
					//log("FingerTable2:"  + fingerTable);
				} else {
					log(key + " rejected in favour for:"
							+ fingerTable.get(key.getIndex()));
				}
			} else if (key != null) {
				log("Finger" + key + " is already in table on another index.");
				if (fingerTable.get(key.getFingerIdentity()).getIndex() != 0) {
					fingerTable.remove(key.getFingerIdentity());
					fingerTable.put(key);
				}
				//log("FingerTable3:"  + fingerTable);
			}
			if (idealFingerTable != null
					&& !idealFingerTable.equals(fingerTable)) {
				log("RealFingerTable:" + fingerTable);
				log("IdealFingerTable:" + idealFingerTable);
			} else if (idealFingerTable != null) {
				log("RealFingerTable is ideal!");
			}
		}
	}

	private Map<DHTEntry, String> getPasswords(DHTEntry[] data) {
		Map<DHTEntry, String> storage = new HashMap<DHTEntry, String>();
		for (int i = 0; i < data.length; i++) {
			String password = passwordStorage.get(data[i]);
				if (password != null)
					storage.put(data[i], password);
				else
					storage.put(data[i], "");
		}
		return storage;
	}


	private void handleJoinResponse(JoinResponse message) {
		log("join message response");
		mergeRemoteData(message.getData());
		log("Join response - found my successor: " + message.getIdentity()
				+ " at " + message.getFromNode());
		final FingerEntry successor = new FingerEntry(identity,
				message.getIdentity(), 0);
		if (!currentRing.equals(DoubleChord.INNER_DHT)) {
			successor.setNode(message.getFromNode());
		}
		setSuccessor(successor);
		
		log("Join response - requesting fingertable");
		DHTMessage request = new RequestFullFingerTable(parent.getLocalNode(),
				successor.getNode(), currentRing);

		sendMessage(request, successor.getFingerIdentity(), null);

		log("Join response - Requesting successor's successors");
		request = new GetSuccessorTable(parent.getLocalNode(),
				successor.getNode(), currentRing);
		sendMessage(request, null, null);

		log("join data merged, updating state");
		removeHandlers(message);
		
		checkSuccessor();
		setState(State.CONNECTED);
		log("Join response - setting " + message.getPredecessor() + " as predecessor");
		setPredecessorIfBetter(message.getPredecessor());
	}

	private void mergeRemoteData(Map<DHTEntry, String> data) {
		Set<Entry<DHTEntry, String>> set = data.entrySet();
		synchronized (dataStorage) {
			for (Iterator<Entry<DHTEntry, String>> iterator = set.iterator(); iterator
					.hasNext();) {
				Entry<DHTEntry, String> entry = iterator.next();
				DHTEntry dhtEntry = entry.getKey();
				String key = parent.sha1hash(dhtEntry.getUci());
				String password = entry.getValue();
				dataStorage.put(key, dhtEntry);
				passwordStorage.put(dhtEntry, password);
			}
		}
	}

	private void handlePut(Put message) {
		 log("Put message recieved");
		DHTEntry entry = message.getEntry();
		PutResponse response = new PutResponse(parent.getLocalNode(),
				message.getFromNode(), currentRing, message.getKey());
		response.setSerialNumber(message.getSerialNumber());
		response.setRequestIdentity(message.getRequestIdentity());
		log("Put message - is responsible for key");
		if (isPasswordOk(entry, message.getPassword())) {
			 log("Put message - password is ok, storing:" + message.getKey() +
			 " as: " + entry);
			synchronized (dataStorage) {
				dataStorage.put(message.getKey(), message.getEntry());
			}
			response.setSuccessful(true);
		} else {
			 log("Put message - password is NOT ok");
			response.setSuccessful(false);
			response.setPasswordRequired(message.getPassword() != null);
		}
		log("Put message - sending response");
		if(!message.getReplica()){
			sendMessage(response, message.getRequestIdentity(), null);
		}
		if(settings.doReplication()){
			if(response.isSuccessful() && message.getReplica() == false){
				 message.setReplica(true);
				 message.setRequestIdentity(identity);
				 message.setSerialNumber(0);
				 synchronized (successorTable) {
					for (Iterator<FingerEntry> iterator2 = successorTable
							.iterator(); iterator2.hasNext();) {
						FingerEntry type = (FingerEntry) iterator2.next();
						message.setToNode(type.getNode());
						sendMessage(message, type.getFingerIdentity(), null);
					}
				}
				if(predecessor != null){
					 message.setToNode(predecessor.getNode());
					 sendMessage(message, predecessor.getFingerIdentity(), null);
				 }
			 }
		}
	}

	private void handlePutResponse(PutResponse message) {
		invokeHandlers(message);
	}

	private void handleLeave(Leave message) {
		
		forceLocalDataUpdate();
		// if(predecessor== null && message.getPredecessor() != null){
		// predecessor= message.getPredecessor();
		// }else if (predecessor != null && message.getPredecessor() != null) {
		// }
		// predecessor = null;
	}

	private void handleGetPredecessor(GetPredecessor message) {
		log("Get predecessor recieved from" + message.getRequestIdentity());
		if (!identity.equals(message.getIdentity())) {
			log(message.getFromNode()
					+ " requested someone else's predecessor from me?!");
		}
		GetPredecessorResponse response = new GetPredecessorResponse(
				parent.getLocalNode(), message.getFromNode(), currentRing,
				message.getIdentity(), identity, predecessor);
		response.setRequestIdentity(message.getRequestIdentity());
		response.setSerialNumber(message.getSerialNumber());
		sendMessage(response, message.getRequestIdentity(), null);
	}

	private void handleGetPredecessorResponse(GetPredecessorResponse message) {
		invokeHandlers(message);

	}

	private void handleUpdatePredecessor(UpdatePredecessor message) {
		 log("Update predecessor recieved");
		FingerEntry candidate = message.getPredecessorCandidate();
		if (candidate == null) {
			log("Update predecessor null-candidate");
			return;
		}else{
			candidate = candidate.clone();
			candidate.setParentIdentity(identity);
			if (!currentRing.equals(DoubleChord.INNER_DHT)) {
				candidate.setNode(message.getFromNode());
			}
		}
		setPredecessorIfBetter(candidate);
	}
	
	private void handleUpdateSuccessor(UpdateSuccessor message) {
		FingerEntry candidate = message.getSuccessorCandidate();
		if(candidate == null){
			return;
		}
		if(!currentRing.equals(DoubleChord.INNER_DHT)){
			candidate.setNode(message.getFromNode());
		}
		synchronized (fingerTable) {
			if (fingerTable.get(0) == null
					|| FingerEntry.isBetween(identity, candidate
							.getFingerIdentity(), fingerTable.get(0)
							.getFingerIdentity())) {
				setSuccessor(candidate);
			}
		}
	}
	
	

	private void sendData(String targetIdentity, SensibleThingsNode targetNode) {
		Map<DHTEntry, String> data;
		synchronized (dataStorage) {
			Collection<DHTEntry> values = dataStorage.values();
			data = getPasswords(values
					.toArray(new DHTEntry[0]));
		}
		
		DataSync msg = new DataSync(parent.getLocalNode(),targetNode,currentRing,data);
		sendMessage(msg, targetIdentity, null);
	}

	private int getLogN() {
		int hopCount = getHopCountEstimation();
		int keySpaceEstimation = getKeySpaceEstimation();
		return hopCount + keySpaceEstimation / 2;
	}

	private int getHopCountEstimation() {
		synchronized(logNEstimation){
			try {
				int sum = 0;
				int size = logNEstimation.size();
				if (!logNEstimation.isEmpty()) {
					Integer[] array = new Integer[0];
					array = logNEstimation.toArray(array);
					for (int i = 0; i < array.length; i++) {
						if(array[i]!= null)
							sum += array[i];
					}
					if (sum / size > 0) {
						return sum / size;
					} else {
						return 1;
					}
				}
				return 1;
			} catch (IndexOutOfBoundsException e) {
				return 1;
			}
		}

	}

	private int getKeySpaceEstimation() {
		if (predecessor == null || fingerTable.isEmpty())
			return 1;
		else if (fingerTable.get(0) == null) {
			return 1;
		} else {
			String successor = fingerTable.get(0).getFingerIdentity();
			int first = FingerEntry.getLogDifference(
					predecessor.getFingerIdentity(), identity);
			int second = FingerEntry.getLogDifference(identity, successor);
			if ((first + second) / 2 > 0) {
				return (first + second) / 2;
			} else {
				return 1;
			}
		}
	}

	private void updateLogN(int hopCount) {
		synchronized(logNEstimation){
			if (logNEstimation.size() >= settings.getMaxLognHopcountHistory()) {
				logNEstimation.pop();
			}
			logNEstimation.addLast(hopCount);
		}
	}

	public void start() {
		if (getState().equals(State.DISCONNECTED)) {
			responseHandlers.clear();
			joinDHT();
		}
	}

	public void shutdown() {
		this.setState(State.DISCONNECTING);
		FingerEntry successor = fingerTable.get(0);
		if (successor != null) {
			Leave message = new Leave(parent.getLocalNode(),
					successor.getNode(), currentRing, predecessor);
			sendMessage(message, successor.getFingerIdentity(), null);
		}
		synchronized (dataStorage) {
			for (Iterator<DHTEntry> iterator = dataStorage.values().iterator(); iterator
					.hasNext();) {
				DHTEntry entry = iterator.next();
				Leave message = new Leave(parent.getLocalNode(),
						entry.getOwnerNode(), currentRing, predecessor);
				sendMessage(message, entry.getKey(), null);

			}
		}
		maintenanceWorker.cancel(true);
		this.setState(State.DISCONNECTED);
	}

	/**
	 * Prints a log message, if debug flag is enabled
	 * 
	 * @param string
	 */
	public void log(String string) {
		parent.log("[Chordbased:DHT:" + currentRing + "] "+ string);
	}

	/**
	 * Perform one maintenance cycle. Retransmit requests, perform stabilization
	 * and/or retransmit join request.
	 */
	private void doMaintenance() {
		checkForStaleData();
		checkLocalDataForStaleData();
		if (getState().equals(State.CONNECTED)) {
			// Do regular maintenance
			try {
				cleanRequests();
			} catch (Exception e) {
				log("Error in retransmitRequests during maintenance");
				if (settings.isDebug())
					e.printStackTrace();
			}
			try {
				Stabilize();
			} catch (Exception e) {
				log("Error in Stabilize during maintenance");
				if (settings.isDebug())
					e.printStackTrace();
			}
		} else if (getState().equals(State.CONNECTING)) {
			// Retransmit join request
			if ((System.currentTimeMillis() - timeOfJoinRequest) > JOIN_TIMEOUT) {// &&
																					// joinRetries++
																					// <
																					// MAX_TRANSMISSION_RETRIES){
				joinDHT();
			} 
		} else if (getState().equals(State.DISCONNECTING)) {
			// Do nothing, we're disconnecting
		} else if (getState().equals(State.DISCONNECTED)) {
			// Do nothing, we are disconnected
		} else {
			// Really do nothing, since this should not happen
		}
	}

	/**
	 * Removes stale data from table
	 */
	private void checkForStaleData() {
		synchronized (dataStorage) {
			for (Iterator<Entry<String, DHTEntry>> iterator = dataStorage
					.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, DHTEntry> entry = iterator.next();
				long delta = entry.getValue().getLastUpdated()
						+ settings.getStoredDataTimeout() - System.currentTimeMillis();
				if (delta < 0) {
					iterator.remove();
					passwordStorage.remove(entry);
				}

			}
		}
	}
	
	private void checkLocalDataForStaleData() {
		synchronized (localDataStorage) {
			Collection<DHTEntry> values = localDataStorage;
			DHTEntry[] valueArray = values.toArray(new DHTEntry[0]);
			for (int i = 0; i < valueArray.length; i++) {
				if ((valueArray[i].getLastUpdated() + settings.getStoredDataTimeout() / 2) < System
						.currentTimeMillis()) {
					// log(valueArray[i] + " is old");
					updateRecord(valueArray[i]);
				} else {
					// log(valueArray[i] + " is ok");
				}
			}
		}
	}
	
	
	
	private void updateRecord(final DHTEntry entry) {
		entry.setLastUpdated();
		String password = localPasswordStorage.get(entry.getUci());
		put(parent.sha1hash(entry.getUci()), entry, password, new PutResponseListener() {

			public void PutSuccessful(String key) {
				return;
			}

			public String passwordRequired(String key) {
				log("Failed to update record "+ entry.toString() + " - password required");
				return null;
			}

			public void PutFailed(String key, String error) {
				log("Failed to update record "+ entry.toString() + " - " + error);
			}});
	}
	
	
	private void forceLocalDataUpdate() {
		synchronized (localDataStorage) {
			Collection<DHTEntry> values = localDataStorage;
			DHTEntry[] valueArray = values.toArray(new DHTEntry[0]);
			for (int i = 0; i < valueArray.length; i++) {
				updateRecord(valueArray[i]);

			}
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("DistributedHashTable: ").append(currentRing)
		.append("\n").append("=================================================\n")
		.append("Identity=").append(identity).append("\n");
		String count = countPendingResponseHandlers();
		output.append("pendingRequests=").append(count).append("\n");
		output.append("PendingRequest types=").append(getPendingRequestTypes()).append("\n");

		output.append("State=").append(getState()).append("\n");
		output.append("LogN=").append(getLogN()).append("\n");
		if (predecessor != null) {
			output.append("Predecessor= ").append(predecessor.getFingerIdentity()).append(" at: ").append(predecessor.getNode()).append("\n");
		} else {
			output.append("Predecessor=null at: null \n");
		}
		output.append("================Finger Table=====================").append("\n");
		output.append(getFingerTableString());
		if(idealFingerTable != null){
			if(fingerTable.equals(idealFingerTable)){
				output.append("\n").append("=DEBUG=OK=Ideal Finger Table=DEBUG=OK=OK=OK=DEBUG").append("\n");
			}
			else{
				output.append("\n").append("=DEBUG====Ideal Finger Table=DEBUG===DEBUG==DEBUG").append("\n");
				output.append(getIdealFingerTableString()).append("\n");
			}
		}
		output.append("================Successor Table==================").append("\n");
		output.append(getSuccessorTableString()).append("\n");
		output.append("================Local Data=======================").append("\n");
		output.append(getLocalDataString());
		output.append("================Remote Data======================").append("\n");
		output.append(getDataString());
		output.append("================End of Line======================").append("\n");

		return output.toString();
	}

	/**
	 * Used in toString
	 * 
	 * @return the number of pending put requests
	 */
	private String countPendingResponseHandlers() {
		int count = responseHandlers.size();
		return count + " handlers for requests";
	}

	private String getPendingRequestTypes() {
		StringBuilder output = new StringBuilder("");
		for (Iterator<Entry<Integer, ResponseHandler>> iterator = responseHandlers.entrySet().iterator(); iterator.hasNext();) {
			ResponseHandler type = iterator.next().getValue();
			output.append(type.toString()).append("\n");
		}
		return output.toString();
	}

	/**
	 * Used in toString
	 * 
	 * @return local stored data
	 */
	private String getDataString() {
		StringBuilder sb = new StringBuilder();
		synchronized (dataStorage) {
			for (Iterator<Entry<String, DHTEntry>> iterator = dataStorage
					.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, DHTEntry> entry = iterator.next();
				long delta = entry.getValue().getLastUpdated()
						+ settings.getStoredDataTimeout() - System.currentTimeMillis();
				String duration = String.format(
						"%02d:%02d:%02d",
						TimeUnit.MILLISECONDS.toHours(delta),
						TimeUnit.MILLISECONDS.toMinutes(delta)
								- TimeUnit.HOURS
										.toMinutes(TimeUnit.MILLISECONDS
												.toHours(delta)),
						TimeUnit.MILLISECONDS.toSeconds(delta)
								- TimeUnit.MINUTES
										.toSeconds(TimeUnit.MILLISECONDS
												.toMinutes(delta)));
				sb.append("Key:" + entry.getKey() + " UCI: "
						+ entry.getValue().getUci() + ", identity:"
						+ entry.getValue().getKey() + ", owner:"
						+ entry.getValue().getOwnerNode() + " time left:"
						+ duration + "\n");
			}
		}
		return sb.toString();
	}
	
	private String getLocalDataString() {
		StringBuilder sb = new StringBuilder();
		synchronized (localDataStorage) {
			for (Iterator<DHTEntry> iterator = localDataStorage.iterator(); iterator
					.hasNext();) {
				DHTEntry entry = iterator.next();
				long delta = entry.getLastUpdated()
						+ settings.getStoredDataTimeout()
						- System.currentTimeMillis();
				String duration = String.format(
						"%02d:%02d:%02d",
						TimeUnit.MILLISECONDS.toHours(delta),
						TimeUnit.MILLISECONDS.toMinutes(delta)
								- TimeUnit.HOURS
										.toMinutes(TimeUnit.MILLISECONDS
												.toHours(delta)),
						TimeUnit.MILLISECONDS.toSeconds(delta)
								- TimeUnit.MINUTES
										.toSeconds(TimeUnit.MILLISECONDS
												.toMinutes(delta)));

				sb.append("UCI: ").append(entry.getUci()).append(", key:")
						.append(entry.getKey()).append(", owner:")
						.append(entry.getOwnerNode()).append(" time left:")
						.append(duration).append("\n");
			}
		}
		return sb.toString();
	}

	private String getSuccessorTableString() {
		StringBuilder sb = new StringBuilder();
		synchronized (successorTable) {
			for (Iterator<FingerEntry> iterator = successorTable.iterator(); iterator
					.hasNext();) {
				FingerEntry entry = iterator.next();
				sb.append(entry.toString()).append("\n");
			}
		}
		return sb.toString();
	}

	/**
	 * Used in toString
	 * 
	 * @return the finger table
	 */
	private String getFingerTableString() {
		return fingerTable.toString();
	}
	
	private FingerTable idealFingerTable = null;
	public void setIdealFingerTable(FingerTable table) {
		if(idealFingerTable == null){
			idealFingerTable = table;
		}
	}
	private String getIdealFingerTableString() {
		return idealFingerTable.toString();
	}

}
