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

package se.sensiblethings.disseminationlayer.lookupservice.distributed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.DestinationNotReachableException;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.disseminationlayer.disseminationcore.DisseminationCore;
import se.sensiblethings.disseminationlayer.disseminationcore.MessageListener;
import se.sensiblethings.disseminationlayer.lookupservice.LookupService;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.broadcast.BroadcastMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.broadcast.KeepAliveBroadcastMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.broadcast.NodeSuspiciousBroadcastMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.broadcast.NotifyJoinBroadcastMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.broadcast.NotifyLeaveBroadcastMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.CheckPredecessorMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.CheckPredecessorResponseMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.CheckSuccessorMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.CheckSuccessorResponseMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.DuplicateNodeIdMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.FindPredecessorMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.FindPredecessorResponseMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.JoinAckMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.JoinBusyMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.JoinFinalizeMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.JoinMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.JoinResponseMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.KeepAliveMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.NodeSuspiciousMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.NotifyJoinMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.NotifyLeaveMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.RegisterMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.RegisterResponseMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.ResolveMessage;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.ResolveResponseMessage;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class DistributedLookup extends LookupService implements Runnable, ResolveFailListener, MessageListener {

	
    public static boolean startAsBootstrap = true;
    //public static String bootstrapIp = "193.10.119.33";
    public static String bootstrapIp = "192.168.0.102";
    

    
	//Communication
	private SensibleThingsNode bootstrapNode;

	//Own state in the DHT
	private TreeMap<FingerEntry,FingerEntry> finger;
	private FingerEntry identity;
	private FingerEntry predecessor;
	
	//Keep alive
	private static final int CONNECT_PERIOD = 5000;
	private static final int KEEP_ALIVE_PERIOD = 10000;
	private static final int KEEP_ALIVE_RANDOM_PERIOD = 10000;
	private static final int JOIN_BLOCK_PERIOD = 15000;
	private static final int JOIN_FINALIZE_PERIOD = 15000;
	
	private static final int CHECK_PREDECESSOR_PERIOD = 2000; //Original 10000
	private static final int CHECK_SUCCESSOR_PERIOD = 2000; //Original 10000
	private static final int FIND_PREDECESSOR_PERIOD = 10000;
	
	private static final int CHECK_REMOTE_SENSORS_PERIOD = 10000;
	private static final int REREGISTER_REMOTE_SENSORS_PERIOD = 60000;
	
	//Actions
	private static final int ACTION_CONNECT = 1;
	private static final int ACTION_SHUTDOWN = 2;
	private static final int ACTION_KEEP_ALIVE = 3;
	private static final int ACTION_CHECK_PREDECESSOR = 4;
	private static final int ACTION_CHECK_SUCCESSOR = 5;
	private static final int ACTION_FIND_PREDECESSOR = 6;
	private static final int ACTION_FINALIZE_TIMEOUT = 7;
	private static final int ACTION_UNBLOCK_JOINBLOCK = 8;
	private static final int ACTION_CHECK_REMOTE_SENSORS = 9;
	private static final int ACTION_REREGISTER_SENSORS = 10;
	private static final int ACTION_KILL = 11;
	
	//Action-queue
	BlockingQueue<Integer> actionQueue;
	
	//The timer and his tasks
	private Timer timer = null;
	private TimerTask connectTask;
	private TimerTask keepAlive;
	private TimerTask blockTask;
	private TimerTask finalizeTask;
	private TimerTask findPredecessorTask;
	private TimerTask checkPredecessorTask;
	private TimerTask checkSuccessorTask;
	private TimerTask checkRemoteSensorsTask;
	private TimerTask reRegisterSensorsTask;
	
	//Connection state
	private boolean connected = false;
	
	private FingerEntry blockJoinFor = null;
	private FingerEntry futureSuccessor = null;
	private FingerEntry futurePredecessor = null;
	
	//Data storage
	//Own Sensors, already stored at the right place in the DHT
	private SensorList sensorsAt;
	
	//Sensors, that this node is responsible for to store
	private SensorList sensorsResponsibleFor;
	
	//Remember resolves, until they got an response, or failed
	private HashMap<Sensor,ResolveService> activeResolves;
	
	//Thread loop
	private Thread thread;
	private boolean runLookup = true;
	
	public DistributedLookup(Communication communication, DisseminationCore disseminationCore) {
		super(communication, disseminationCore);
		
		SensibleThingsNode bootstrapNode;
		if(DistributedLookup.startAsBootstrap){
			//We are the bootstrap
			bootstrapNode = null;
		} else {			
			
			//Else start as a normal node			
			bootstrapNode = communication.createSensibleThingsNode(bootstrapIp, 9009);			
			//bootstrapNode = communication.createSensibleThingsNode("193.10.119.33", 9009);
			
			
		}
		
		
		this.communication = communication;
		this.disseminationCore = disseminationCore;
		
		//Register message Listeners
		communication.registerMessageListener(JoinMessage.class.getName(), this);
		communication.registerMessageListener(JoinResponseMessage.class.getName(), this);		
		communication.registerMessageListener(JoinAckMessage.class.getName(), this);		
		communication.registerMessageListener(JoinBusyMessage.class.getName(), this);		
		communication.registerMessageListener(JoinFinalizeMessage.class.getName(), this);		
		communication.registerMessageListener(DuplicateNodeIdMessage.class.getName(), this);		
		communication.registerMessageListener(NotifyJoinMessage.class.getName(), this);		
		communication.registerMessageListener(KeepAliveMessage.class.getName(), this);		
		communication.registerMessageListener(NotifyLeaveMessage.class.getName(), this);		
		communication.registerMessageListener(FindPredecessorMessage.class.getName(), this);		
		communication.registerMessageListener(FindPredecessorResponseMessage.class.getName(), this);		
		communication.registerMessageListener(NodeSuspiciousMessage.class.getName(), this);		
		communication.registerMessageListener(CheckPredecessorMessage.class.getName(), this);		
		communication.registerMessageListener(CheckPredecessorResponseMessage.class.getName(), this);		
		communication.registerMessageListener(CheckSuccessorMessage.class.getName(), this);		
		communication.registerMessageListener(CheckSuccessorResponseMessage.class.getName(), this);		
		communication.registerMessageListener(RegisterMessage.class.getName(), this);	
		communication.registerMessageListener(RegisterResponseMessage.class.getName(), this);	
		communication.registerMessageListener(ResolveMessage.class.getName(), this);	
		communication.registerMessageListener(ResolveResponseMessage.class.getName(), this);
		
		communication.registerMessageListener(BroadcastMessage.class.getName(), this);	
		communication.registerMessageListener(KeepAliveBroadcastMessage.class.getName(), this);	
		communication.registerMessageListener(NodeSuspiciousBroadcastMessage.class.getName(), this);	
		communication.registerMessageListener(NotifyJoinBroadcastMessage.class.getName(), this);	
		communication.registerMessageListener(NotifyLeaveBroadcastMessage.class.getName(), this);	
		
		//Initialize finger-table
		finger = new TreeMap<FingerEntry, FingerEntry>();
		
		activeResolves = new HashMap<Sensor,ResolveService>();
		
		//Generate hash from the local network address		
		//Ask stefan if inclusion of port address is reasonable for future work.
		//System.out.println("ASDAFSDFDDFAG" + communication.getLocalNode().getAddress());
		byte[] hash = SHA1Generator.SHA1(communication.getLocalSensibleThingsNode().toString());
		
		//Init timer
		this.timer = new Timer();

		//Set identity
		setIdentity(hash);
		//successor = identity;
		predecessor = null;
		
		//Save bootstrap address
		this.bootstrapNode = bootstrapNode;
		
		//Create blocking queue for action forwarding to worker thread
		actionQueue = new LinkedBlockingQueue<Integer>(1);
		
		//Notify connect
		//notify(ACTION_CONNECT);
		connectTask = startTask(connectTask,ACTION_CONNECT,0);
		
		//Init data storage objects
		sensorsAt = new SensorList();
				
		sensorsResponsibleFor = new SensorList();
		
		//Start thread
		this.thread = new Thread(this);
		this.thread.setName("DistributedLookupService");
		this.thread.start();
	}

	@Override
	public void resolve(String uci) {
		//Generate the hash value
		Sensor sensor = new Sensor(new NodeID(SHA1Generator.SHA1(uci)),null);
		TreeSet<Sensor> sensors = new TreeSet<Sensor>();
		
		//think about this again, because i changed the type from hashset to treeset
		sensors.addAll(sensorsAt.getAllSensors());
		sensors.addAll(sensorsResponsibleFor.getAllSensors());
		
		if(sensors.contains(sensor)) {
			//Ceiling is a workaround to return the element
			sensor = sensors.ceiling(sensor);
			
			disseminationCore.callResolveResponseListener(uci, sensor.getOwner().getSensibleThingsNode());
		}
		else {
			//Send resolve message
			ResolveService rs;
			
			synchronized(activeResolves) {
				if((rs = activeResolves.get(sensor)) == null || rs.hasFailed()) {
					//Remember resolve message
					activeResolves.put(sensor, new ResolveService(sensor,uci,timer,this));
				}
			}
		}
	}

	@Override
	public void register(String uci) {
		register(uci,null);
	}
	
	@Override
	public void register(String uci, String password) {
		//Generate the hash value
		NodeID sensorHash = new NodeID(SHA1Generator.SHA1(uci));
		Sensor sensor = new Sensor(sensorHash,identity);
		
		//Put it to the HashSet with the local sensors
		sensorsAt.put(identity,sensor);
		register(sensor);
	}
	
	private void register(Sensor sensor) {
		FingerEntry predecessorOfSensor;
		
		
		predecessorOfSensor = getSensorPredecessor(sensor);
		
		//Forward or answer?
		if(predecessorOfSensor.equals(identity)) {
			//We are responsible
			sensorsResponsibleFor.put(identity, sensor);			
		}
		else {
			//we have to send a register Message but only if we are connected
			if(connected) {
				sendMessage(new RegisterMessage(identity.getSensibleThingsNode(),predecessorOfSensor.getSensibleThingsNode(),sensor.getSensorHash(),identity.getNodeID(),identity.getSensibleThingsNode()), predecessorOfSensor.getNodeID());
			}
		}
	}

	@Override
	public synchronized void shutdown() {
		
		//Schedule immediately
		//notify(ACTION_SHUTDOWN);
		startTask(null,ACTION_SHUTDOWN,0);
		
		try {
			//Wait for messages to be sent out!			
			Thread.sleep(2000);			
			
			//Discontinue thread
			actionQueue.clear();
			runLookup = false;
			this.thread.interrupt();				
		} catch (Exception e){
			e.printStackTrace();
			//Whatever...
		}		
	}

	
	public void handleMessage(Message message) {
		// Don't process message if it was not for us!!
		if (!message.getToNode().toString().equalsIgnoreCase(identity.getSensibleThingsNode().toString())) {
			return;
		}

		// Analyze message
		if (message instanceof JoinMessage) {
			if (connected) {
				handleJoinMessage((JoinMessage) message);
			}
			
		} else if (message instanceof JoinResponseMessage) {
			handleJoinResponseMessage((JoinResponseMessage) message);

		} else if (message instanceof JoinAckMessage) {
			handleJoinAckMessage((JoinAckMessage) message);

		} else if (message instanceof JoinBusyMessage) {
			// react on this?

		} else if (message instanceof JoinFinalizeMessage) {
			handleJoinFinalizeMessage((JoinFinalizeMessage) message);

		} else if (message instanceof DuplicateNodeIdMessage) {
			handleDuplicateNodeIDMessage((DuplicateNodeIdMessage) message);

		} else if (message instanceof NotifyJoinMessage) {
			handleNotifyJoinMessage((NotifyJoinMessage) message);

		} else if (message instanceof KeepAliveMessage) {
			handleKeepAliveMessage((KeepAliveMessage) message);

		} else if (message instanceof NotifyLeaveMessage) {
			handleNotifyLeaveMessage((NotifyLeaveMessage) message);

		} else if (message instanceof FindPredecessorMessage) {
			handleFindPredecessorMessage((FindPredecessorMessage) message);

		} else if (message instanceof FindPredecessorResponseMessage) {
			handleFindPredecessorResponseMessage((FindPredecessorResponseMessage) message);

		} else if (message instanceof NodeSuspiciousMessage) {
			handleNodeSuspiciousMessage((NodeSuspiciousMessage) message);

		} else if (message instanceof CheckPredecessorMessage) {
			handleCheckPredecessorMessage((CheckPredecessorMessage) message);

		} else if (message instanceof CheckPredecessorResponseMessage) {
			handleCheckPredecessorResponseMessage((CheckPredecessorResponseMessage) message);

		} else if (message instanceof CheckSuccessorMessage) {
			handleCheckSuccessorMessage((CheckSuccessorMessage) message);

		} else if (message instanceof CheckSuccessorResponseMessage) {
			handleCheckSuccessorResponseMessage((CheckSuccessorResponseMessage) message);

		} else if (message instanceof RegisterMessage) {
			handleRegisterMessage((RegisterMessage) message);

		} else if (message instanceof RegisterResponseMessage) {
			handleRegisterResponseMessage((RegisterResponseMessage) message);
			
		} else if (message instanceof ResolveMessage) {
			handleResolveMessage((ResolveMessage) message);
			
		} else if (message instanceof ResolveResponseMessage) {
			handleResolveResponseMessage((ResolveResponseMessage) message);

		} else if (message instanceof BroadcastMessage) {
			BroadcastMessage bcast_msg = (BroadcastMessage) message;
			
			// Process broadcast content
			handleMessage(bcast_msg.extractMessage());

			// Forward broadcast
			sendBroadcast(bcast_msg, bcast_msg.getStartKey(),
			bcast_msg.getEndKey());
			
		} else {
			System.out.println("Did not handle message: " + message);			
		}
	}
	
	public void run() {
		int currentAction;
		
		//Main loop
		while(runLookup) {
			//Get next element from queue
			try {
				currentAction = actionQueue.take();
			}
			catch (InterruptedException e) {
				//Thread was interrupted
				break;
			}
			
			switch(currentAction) {
				case ACTION_CONNECT:
					if(!connected) {
						connect(bootstrapNode);
						
						//Restart task
						connectTask = startTask(connectTask,ACTION_CONNECT,CONNECT_PERIOD);
					}
					break;
				case ACTION_SHUTDOWN:
					
					//Clear everything else, its not important anymore //Stefan
					timer.cancel();
					actionQueue.clear();
					
					//Close gently by sending a NOTIFY_LEAVE_BROADCAST and than exiting the thread
					sendBroadcast(new NotifyLeaveBroadcastMessage(null, null, null, null,identity.getNodeID(),getSuccessor(null).getNodeID(),getSuccessor(null).getSensibleThingsNode()),identity.getNodeID(),identity.getNodeID().sub(1));
					
					//Register all sensor we are responsible for to our predecessor
					if(predecessor != null) {
						FingerEntry suc = getSuccessor(null);
						
						//Send notify leave message to inform our predecessor first, so it will not try to send back the message
						//The predecessor may get the message twice, which does not matter 
						sendMessage(new NotifyLeaveMessage(identity.getSensibleThingsNode(),predecessor.getSensibleThingsNode(),identity.getNodeID(),suc.getNodeID(),suc.getSensibleThingsNode()),null);
						
						for(Sensor s: sensorsResponsibleFor.getAllSensors()) {
							FingerEntry owner = s.getOwner();
							sendMessage(new RegisterMessage(identity.getSensibleThingsNode(),predecessor.getSensibleThingsNode(),s.getSensorHash(),owner.getNodeID(),owner.getSensibleThingsNode()),null);
						}
					}
					
					//System.out.println("Shutdown Queued");
					
					//Discontinue thread
					//runLookup = false;
					//this.interrupt();					
					break;
				case ACTION_CHECK_PREDECESSOR:
					synchronized(this) {
						if(predecessor != null) {
							//Send message to check the successor->predecessor link
							sendMessage(new CheckPredecessorMessage(identity.getSensibleThingsNode(), predecessor.getSensibleThingsNode(), identity.getNodeID()),predecessor.getNodeID());
						}
						checkPredecessorTask = startTask(checkPredecessorTask, ACTION_CHECK_PREDECESSOR, CHECK_PREDECESSOR_PERIOD);
					}
					break;
				case ACTION_CHECK_SUCCESSOR:
					synchronized(this) {
						//Send message to check the predecessor->successor link
						if(predecessor != null) {
							sendMessage(new CheckSuccessorMessage(identity.getSensibleThingsNode(), predecessor.getSensibleThingsNode(), identity.getNodeID()),predecessor.getNodeID());
							checkSuccessorTask = startTask(checkSuccessorTask, ACTION_CHECK_SUCCESSOR, CHECK_SUCCESSOR_PERIOD);
						}
					}
					break;
				case ACTION_FIND_PREDECESSOR:
					//Send FIND_PREDECESSOR message to get a better predecessor
					synchronized(this) {
						FingerEntry dst = getPredecessor(null);
						sendMessage(new FindPredecessorMessage(identity.getSensibleThingsNode(), dst.getSensibleThingsNode(),identity.getNodeID(),identity.getSensibleThingsNode()),dst.getNodeID());
					}
					
					//Reschedule task
					findPredecessorTask = startTask(findPredecessorTask,ACTION_CHECK_PREDECESSOR,FIND_PREDECESSOR_PERIOD);
					break;
				case ACTION_KEEP_ALIVE:
					//trigger the keepalive and start period for the next one
					triggerKeepAliveTimer();
					keepAlive = startTask(keepAlive, ACTION_KEEP_ALIVE, KEEP_ALIVE_PERIOD + new Random().nextInt(KEEP_ALIVE_RANDOM_PERIOD));
					//resetKeepAliveTimer();
					break;
				
				case ACTION_FINALIZE_TIMEOUT:
					//Timeout triggered
					triggerFinalizeTimeout();
					break;
					
				case ACTION_UNBLOCK_JOINBLOCK:
					//Unblock timeout
					triggerUnblockJoinBlock();
					break;
					
				case ACTION_CHECK_REMOTE_SENSORS:
					//Check sensor mappings
					checkRemoteSensorMapping();
					rearrangeLocalSensorMapping();
					
					//Restart task
					checkRemoteSensorsTask = startTask(checkRemoteSensorsTask, ACTION_CHECK_REMOTE_SENSORS, CHECK_REMOTE_SENSORS_PERIOD);
					break;
					
				case ACTION_REREGISTER_SENSORS:
					for(Sensor sensor: sensorsAt.getAllSensors()) {
						register(sensor);
					}
					reRegisterSensorsTask = startTask(reRegisterSensorsTask, ACTION_REREGISTER_SENSORS, REREGISTER_REMOTE_SENSORS_PERIOD);
					break;
					
				case ACTION_KILL:
					this.thread.interrupt();
					break;
			}
		}

		//Shutdown timer
		synchronized(this) {			
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}
	
	public FingerEntry getIdentity() {
		return identity;
	}
	
	
	private FingerEntry getPredecessor(NodeID nodeID) {
		FingerEntry hash;
		FingerEntry result;
		
		//null means identity
		if(nodeID == null) nodeID = identity.getNodeID();
		hash = new FingerEntry(nodeID,null);

		//Add identity to the finger-table - IMPORTANT: remove them before return
		synchronized(finger) {
			finger.put(identity, identity);

			//Find predecessor of a node
			result = finger.lowerKey(hash);
			if(result == null) {
				//There is no lower key in the finger tree
				result = finger.lastKey();
			}
			
			//Remove identity and successor from the finger-table
			finger.remove(identity);
		}
		
		//Temporary check
		if(nodeID.equals(identity.getNodeID()) && !result.equals(predecessor)) {
			assert(false);
		}

		return result;
	}
	
	//For searching a predecessor for a sensor we have to add 1 to its hash...
	private FingerEntry getSensorPredecessor(Sensor sensor) {
		return getPredecessor(sensor.getSensorHash().add(1));
	}
	
	public FingerEntry getSuccessor(NodeID nodeID) {
		FingerEntry hash;
		FingerEntry result;

		//null means identity
		if(nodeID == null) nodeID = identity.getNodeID();
		hash = new FingerEntry(nodeID,null);

		//Our successor
		//if(nodeID.equals(identity)) return successor;
		
		synchronized(finger) {
			//Add identity and successor to the finger-table - IMPORTANT: remove them before return
			finger.put(identity, identity);

			//Get successor of us
			result = finger.higherKey(hash);
			if(result == null) { 
				//There is no higher key in the finger tree
				result = finger.firstKey();
			}
			
			//Remove identity and successor from the finger-table
			finger.remove(identity);
		}

		return result;
	}
	
	private void setIdentity(byte[] hash) {
		//Set identity
		identity = new FingerEntry(new NodeID(hash),communication.getLocalSensibleThingsNode());
		
		//(Re-)initialize finger table
		//Always add ourselves to the finger table
		//finger = new TreeMap<FingerEntry,FingerEntry>();
		//finger.put(identity,identity);
	}
	
	//Check if new node can be inserted into finger table
	public void updateFingerTable(FingerEntry newFinger) {
		FingerEntry suc;
		NodeID hash_finger;
		NodeID hash_suc;
		NodeID hash_log2;
		int log2floor;
		
		//Check for dont's
		synchronized(finger) {
			if(newFinger.equals(identity)) return;
			if(finger.containsKey(newFinger)) return;
		}
		
		//To decide if we have to reorganise the sensors
		FingerEntry oldSuc;
		
		//1 - Rotate hash to the "origin"
		//2 - Then get the logarithm of base 2, rounded down (floor)
		//3 - Calculate the new hash
		
		hash_finger = newFinger.getNodeID().sub(identity.getNodeID());
		log2floor = NodeID.logTwoFloor(hash_finger);
		hash_log2 = NodeID.powerOfTwo(log2floor);

		//Get previous successor - Shift to original position first
		synchronized(finger) {
			suc = getSuccessor(hash_log2.add(identity.getNodeID()));
			oldSuc = getSuccessor(null);
		}
		hash_suc = suc.getNodeID().sub(identity.getNodeID());
		
		if(suc.equals(identity)) {
			//In this case, there is no successor => just add the new finger
			synchronized(finger) {
				finger.put(newFinger,newFinger);
			}
			
			//debug Fire event
			//fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_ADD, identity, newFinger);

		}
		//Check if the new finger is smaller than the successor
		else if(hash_finger.compareTo(hash_suc) < 0) {
			//Also add the new node in this case...
			synchronized(finger) {
				finger.put(newFinger,newFinger);
			}

			//...but also check if the successor was the old successor
			//and, if so, remove it
			//Old successor means, that it is between [log2floor,log2floor + 1)
			if(log2floor == ((NodeID.ADDRESS_SIZE * 8) - 1) || hash_suc.compareTo(NodeID.powerOfTwo(log2floor + 1)) < 0) {
				synchronized(finger) {
					finger.remove(suc);
				}
				
				//Fire events
				//fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_REMOVE_WORSE, identity, suc);
				//fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_ADD_BETTER, identity, newFinger);
			}
			else {
				//Only fire ADD event, because nothing was removed in change
				//fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_ADD, identity, newFinger);
			}
			
		}
		
		//Re-register sensors that belong to new successor if it changed
		synchronized (this) {
			FingerEntry newSuc = getSuccessor(null);
			if(!oldSuc.equals(newSuc)) rearrangeLocalSensorMapping();
		}
	}
	
	private void sendMessage(Message message,NodeID dstNode) {
		//Don't send to ourselves
		if(identity.getNodeID().equals(dstNode)) {
			return;
		}
		
		try {
			communication.sendMessage(message);
		} catch(DestinationNotReachableException e) {
			//Handle failing node if we now him
			if(dstNode != null) {
				//handle the failing node
				handleFailingNode(dstNode);
				
				//inform the network
				sendBroadcast(new NodeSuspiciousBroadcastMessage(null, null, null, null, dstNode), identity.getNodeID(), identity.getNodeID().sub(1));
			}
		}
	}
	
	private void sendBroadcast(BroadcastMessage bcast_msg, NodeID startKey,NodeID endKey) {
		FingerEntry suc,next;
		SensibleThingsNode from,to;
		NodeID newStartKey,newEndKey;
		BroadcastMessage new_bcast_msg;
		
		//Don't do...
		if(!connected) {
			//System.out.println("NOT CONNECTED " + identity.getNetworkAddress());
			return;
		}
		
		//Prepare packet
		from = identity.getSensibleThingsNode();

		//Get first successor
		suc = getSuccessor(null);
		if(suc.equals(identity)) {
			return;
		}
		next = getSuccessor(suc.getNodeID());

		//For each finger
		do {
			//Check and set range
			if(suc.getNodeID().between(startKey,endKey)) {
				newStartKey = suc.getNodeID();
				
				//Set endKey to next NodeID or the end of the range, whatever is smallest
				if(next.getNodeID().between(startKey,endKey)) {
					newEndKey = next.getNodeID().sub(1);
				}
				else {
					newEndKey = endKey;
				}
				
				//Send message
				to = suc.getSensibleThingsNode();
				new_bcast_msg = bcast_msg.cloneWithNewAddresses(from, to,newStartKey,newEndKey);
				
				sendMessage(new_bcast_msg,suc.getNodeID());
				
			}

			//Move to next range
			suc = next;
			next = getSuccessor(suc.getNodeID());
		} while(!suc.equals(identity));
	}
	
	//for DEBUG
/*	private void fireFingerChangeEvent(int eventType,FingerEntry node,FingerEntry finger) {
		communication.fireFingerChangeEvent(eventType,node,finger);
	}*/
	
	//for DEBUG
	//Remove later
	public TreeMap<FingerEntry,FingerEntry> getFingerTable() {
		TreeMap<FingerEntry,FingerEntry> newMap;
		
		//We need to clone the map and synchronize this operation!
		synchronized(finger) {
			newMap = new TreeMap<FingerEntry,FingerEntry>(finger);
			newMap.put(identity,identity);
		}
		
		return newMap;
	}
	
	private void triggerKeepAliveTimer() {
		KeepAliveBroadcastMessage msg;
		
		//Fire event at network layer
		//only for debugging
		//communication.fireKeepAliveEvent(identity.getNodeID(),identity.getNetworkAddress());
		
		//Send broadcast
		msg = new KeepAliveBroadcastMessage(null,null,null,null,identity.getNodeID(),identity.getSensibleThingsNode());
		sendBroadcast(msg, identity.getNodeID(),identity.getNodeID().sub(1));
		
		//Reset time
		//resetKeepAliveTimer();
	}

	//debug function
	public boolean getStateConnected() {
		return connected;
	}

	//debug function
	public FingerEntry getStateBlockJoinFor() {
		return blockJoinFor;
	}
	
	private void triggerUnblockJoinBlock() {
		//Unblock node if new successor did not answer
		synchronized(this) {
			if(blockJoinFor != null) {
				blockJoinFor = null;
			}
		}
	}
	
	private void triggerFinalizeTimeout() {
		//The finalize timed out => Try to reconnect
		synchronized(this) {
			if(futureSuccessor == null) {
				//Timer has not been deactivated yet, but we are already connected!
				//Do nothing then
				return; 
			}
			
			futurePredecessor = null;
			futureSuccessor = null;
			connected = false;
		}
		
		//start to reconnect
		//think about a senseful address to reconnect
		startTask(null,ACTION_CONNECT,0);
	}

	synchronized private FingerEntry updatePredecessor(FingerEntry newFinger) {
		//Predecessor removed
		if(newFinger == null) {
			//debug stuff
			//Fire event ...
			//fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_REMOVE, identity, predecessor);
			
			//...and set to null.
			predecessor = null;
			return null;
		}
		
		//Nothing to do
		if(newFinger.equals(identity) || newFinger.equals(predecessor)) return null;
		
		//Check first if the new one really is a better predecessor
		if(predecessor == null || newFinger.getNodeID().sub(identity.getNodeID()).compareTo(predecessor.getNodeID().sub(identity.getNodeID())) > 0) {
			FingerEntry oldPredecessor = predecessor;

			//It is a better predecessor
			predecessor = newFinger;
						
			//Fire fingerChange event
/*			if(oldPredecessor == null || oldPredecessor.equals(identity)) {
				fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_ADD,identity, predecessor);
			}
			else {
				fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_REMOVE_WORSE,identity, oldPredecessor);
				fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_ADD_BETTER,identity, predecessor);
			}*/
			
			//Check if it might be a finger
			//updateFingerTableEntry(oldPredecessor);
			//return the old predecessor, maybe we can still use it
			return oldPredecessor;
		} 
		else {
			//we did not update anything
			return null;
		}
	}
	
	private void connect(SensibleThingsNode address) {
		synchronized(this) {
			//If the futureSuccessor (or predecessor) is null, then we are not
			//currently in the process of connection
			if(!connected && (futureSuccessor == null || futurePredecessor == null)) {
				
				//Reset node!!
				finger.clear();
				
				//No address means, WE are the beginning of the DHT
				//If we are a bootstrapping node, that means bootstrapping address is null or is our address,
				//we are always connected !!
				if(address == null || address.equals(communication.getLocalSensibleThingsNode())) {
					//We are connected and we are our own successor
					connected = true;
					
					//Start predecessor, successor refresh and keep-alive and checking the remoteSensors
					checkPredecessorTask = startTask(checkPredecessorTask, ACTION_CHECK_PREDECESSOR, CHECK_PREDECESSOR_PERIOD);
					checkSuccessorTask = startTask(checkSuccessorTask, ACTION_CHECK_SUCCESSOR, CHECK_SUCCESSOR_PERIOD);
					keepAlive = startTask(keepAlive, ACTION_KEEP_ALIVE, KEEP_ALIVE_PERIOD + new Random().nextInt(KEEP_ALIVE_RANDOM_PERIOD));
					checkRemoteSensorsTask = startTask(checkRemoteSensorsTask, ACTION_CHECK_REMOTE_SENSORS, CHECK_REMOTE_SENSORS_PERIOD);
					reRegisterSensorsTask = startTask(reRegisterSensorsTask, ACTION_REREGISTER_SENSORS, REREGISTER_REMOTE_SENSORS_PERIOD);
					
					//Cancel timer-task
					if(connectTask != null) connectTask.cancel();
				}
				else {			
					//Try to connect to DHT
					sendMessage(new JoinMessage(identity.getSensibleThingsNode(),address,identity.getSensibleThingsNode(),identity.getNodeID()),null);
				}
			}
		}
	}
	
	private void handleJoinMessage(JoinMessage join_msg) {
		Message answer = null;

		FingerEntry predecessorOfJoiningNode = getPredecessor(join_msg.getKey());
		
		//Forward or answer?
		if(predecessorOfJoiningNode.equals(identity)) {
			//It's us => reply on JOIN

			//Check if it exists
			FingerEntry newFingerEntry = new FingerEntry(join_msg.getKey(),join_msg.getOriginatorNode());
			FingerEntry tempFinger = null;
			
			//Do we know the finger already? (As successor or finger entry)
			synchronized(finger) {
				if(getSuccessor(null).equals(newFingerEntry)) {
					tempFinger = getSuccessor(null);
				}
				else {
					tempFinger = finger.get(newFingerEntry);
				}
			}
			
			//If another node tried to enter the DHT with the same key, send duplicate message
			if(tempFinger != null && !tempFinger.getSensibleThingsNode().equals(newFingerEntry.getSensibleThingsNode())) {
				//Key not allowed message
				answer = new DuplicateNodeIdMessage(identity.getSensibleThingsNode(), join_msg.getFromNode(),join_msg.getKey());
			}
			else {
				synchronized(this) {
					if(blockJoinFor != null) {
						//Send busy message
						answer = new JoinBusyMessage(identity.getSensibleThingsNode(),join_msg.getOriginatorNode());
					}
					else {
						//Prepare answer
						answer = new JoinResponseMessage(identity.getSensibleThingsNode(), join_msg.getOriginatorNode(),join_msg.getKey(), getSuccessor(null).getSensibleThingsNode(),getSuccessor(null).getNodeID(),identity.getNodeID());
						blockJoinFor = newFingerEntry;
						
						//Start timer for node unblocking
						blockTask = startTask(blockTask,ACTION_UNBLOCK_JOINBLOCK,JOIN_BLOCK_PERIOD);
					}
				}
			}
				
			//Send
			sendMessage(answer,join_msg.getKey());
		}
		else {
			//Forward to the best fitting predecessor
			FingerEntry dst = getPredecessor(join_msg.getKey());
			answer = new JoinMessage(identity.getSensibleThingsNode(),dst.getSensibleThingsNode(), join_msg.getOriginatorNode(), join_msg.getKey());
			sendMessage(answer,dst.getNodeID());
		}
	}
	
	private void handleJoinResponseMessage(JoinResponseMessage jrm) {
		//Ignore JOIN_RESPONSE message if the node is already connected!
		synchronized(this) {
			if(!connected) {
				//Check if the join id is really us!
				if(jrm.getJoinKey().equals(identity.getNodeID())) {
					//Add finger
					futureSuccessor = new FingerEntry(jrm.getSuccessor(), jrm.getSuccessorNode());
					futurePredecessor = new FingerEntry(jrm.getPredecessor(),jrm.getFromNode());
					
					//Inform the node that we got the message
					sendMessage(new JoinAckMessage(identity.getSensibleThingsNode(), futurePredecessor.getSensibleThingsNode(), identity.getNodeID()),futurePredecessor.getNodeID());
					
					//Start finalizeTask timer
					finalizeTask = startTask(finalizeTask,ACTION_FINALIZE_TIMEOUT,JOIN_FINALIZE_PERIOD);
				}
				else {
					//Ignore this because the key does not match!!!
					//react on this?
				}
			}
		}
	}
	
	private synchronized void handleJoinAckMessage(JoinAckMessage jam) {
		//Skip if not blocked or it is a faked message
		if(blockJoinFor != null && jam.getJoinKey().equals(blockJoinFor.getNodeID())) {
			//Set successor to new node and update finger-table with old successor
			
			//Cancel block timer first!!
			if(blockTask != null)  {
				blockTask.cancel();
			}
			
			//Notify everybody of the new node
			//Do it before the new node is integrated in our structure, so we dont rely on
			//its capability to forward the broadcast
			sendBroadcast(new NotifyJoinBroadcastMessage(null,null,null,null,blockJoinFor.getSensibleThingsNode(),blockJoinFor.getNodeID()),identity.getNodeID(),identity.getNodeID().sub(1));
			
			//Update successor and predecessor
			updateFingerTable(blockJoinFor);
			FingerEntry oldPredecessor = updatePredecessor(getSuccessor(null));
			
			//check if the old ones are good as fingers
			if(oldPredecessor != null) updateFingerTable(oldPredecessor);
			
			//Notify the new node, that it is connected
			sendMessage(new JoinFinalizeMessage(identity.getSensibleThingsNode(), jam.getFromNode(), jam.getJoinKey()),jam.getJoinKey());

			//unblock
				blockJoinFor = null;
		}
	}
	
	private void handleJoinFinalizeMessage(JoinFinalizeMessage jfm) {
		synchronized(this) {
			//Only if we are not connected
			if(connected || futurePredecessor == null || futureSuccessor == null) return;
			
			//First, cancel timeout-timer
			if(finalizeTask != null) finalizeTask.cancel();
			
			//If the joinKey equals our hash
			if(jfm.getJoinKey().equals(identity.getNodeID())) {
				synchronized (finger) {
					updateFingerTable(futureSuccessor);
					updatePredecessor(futurePredecessor);
					//predecessor = futurePredecessor;
					futurePredecessor = null;
					futureSuccessor = null;
				}
							
				
				connected = true;

				//Start predecessor and keep-alive tasks
				checkPredecessorTask = startTask(checkPredecessorTask, ACTION_CHECK_PREDECESSOR, CHECK_PREDECESSOR_PERIOD);
				checkSuccessorTask = startTask(checkSuccessorTask, ACTION_CHECK_SUCCESSOR, CHECK_SUCCESSOR_PERIOD);
				keepAlive = startTask(keepAlive, ACTION_KEEP_ALIVE, KEEP_ALIVE_PERIOD + new Random().nextInt(KEEP_ALIVE_RANDOM_PERIOD));
				checkRemoteSensorsTask = startTask(checkRemoteSensorsTask, ACTION_CHECK_REMOTE_SENSORS, CHECK_REMOTE_SENSORS_PERIOD);
				reRegisterSensorsTask = startTask(reRegisterSensorsTask, ACTION_REREGISTER_SENSORS, REREGISTER_REMOTE_SENSORS_PERIOD);
				
			}
		}
	}
	
	private void handleDuplicateNodeIDMessage(DuplicateNodeIdMessage dupMsg) {
		if(dupMsg.getDuplicateKey().equals(identity.getNodeID())) {
			//it is okay, a join that I send before has reached my predecessor
		} else {
			//If the node is not connected allow the change of the identity
			//Check the duplicate id also
			if(!connected && dupMsg.getDuplicateKey().equals(identity.getNodeID())) {
				//what shall we do here?????
				assert(false);
			}
		}
	}
	
	private void handleNotifyJoinMessage(NotifyJoinMessage njm) {
		FingerEntry newFinger;
		FingerEntry oldPredecessor;
		
		//Check if this node can use the newly added node
		//for the finger table
		newFinger = new FingerEntry(njm.getHash(),njm.getNetworkAddress());
		oldPredecessor = updatePredecessor(newFinger);
		updateFingerTable(newFinger);
		
		//if the predecessor was changed check the old one for the finger table
		if(oldPredecessor != null) updateFingerTable(oldPredecessor);
		
		//Send advertisement if we probably are a finger of the joining node
		synchronized(this) {
			if(predecessor != null) {
				int log2_pre = NodeID.logTwoFloor(predecessor.getNodeID().sub(newFinger.getNodeID()));
				int log2_this = NodeID.logTwoFloor(identity.getNodeID().sub(newFinger.getNodeID()));
				
				if(log2_pre < log2_this) {
					//Send advertisement
					sendMessage(new KeepAliveMessage(identity.getSensibleThingsNode(),newFinger.getSensibleThingsNode(),identity.getNodeID(),identity.getSensibleThingsNode()),newFinger.getNodeID());
				}
			}
		}
	}
	
	private void handleKeepAliveMessage(KeepAliveMessage kam) {
		FingerEntry advertisedFinger;
		
		//Reset timer
		//resetKeepAliveTimer();
		keepAlive = startTask(keepAlive, ACTION_KEEP_ALIVE, KEEP_ALIVE_PERIOD + new Random().nextInt(KEEP_ALIVE_RANDOM_PERIOD));

		//Handle keep-alive message
		advertisedFinger = new FingerEntry(kam.getAdvertisedID(),kam.getAdvertisedNetworkNode());
		updateFingerTable(advertisedFinger);
		FingerEntry oldPredecessor = updatePredecessor(advertisedFinger);
		
		//if the predecessor was changed check the old one for the finger table
		if(oldPredecessor != null) updateFingerTable(oldPredecessor);
	}
	
	private void handleNotifyLeaveMessage(NotifyLeaveMessage nlm) {		
		//System.out.println("Handling NotifyLeave Message");
				
		handleFailingNode(nlm.getHash());
		
		
		FingerEntry leavingNodeSuccessor = new FingerEntry(nlm.getSuccessorHash(),nlm.getSuccessorNetworkNode());
		
		if (predecessor != null && nlm.getHash().equals(predecessor.getNodeID())) {
			//System.out.println("Predec");
			
			//Remove leaving finger			
			FingerEntry removedFinger = finger.remove(new FingerEntry(nlm.getHash(),null));
			//finger.remove(new FingerEntry(nlm.getHash(),null));
			if(removedFinger != null){
				System.out.println("Removed: " + removedFinger.getSensibleThingsNode());				
			}
					
			updatePredecessor(leavingNodeSuccessor);
			
		} else {
			//System.out.println("Succ");
			
			//Remove leaving finger			
			FingerEntry removedFinger = finger.remove(new FingerEntry(nlm.getHash(),null));
			//finger.remove(new FingerEntry(nlm.getHash(),null));
			if(removedFinger != null){
				System.out.println("Removed: " + removedFinger.getSensibleThingsNode());				
			}
			
			//Change the successor to the next one
			updateFingerTable(leavingNodeSuccessor);			
		}		
	}
	
	private void handleNodeSuspiciousMessage(NodeSuspiciousMessage nsm) {
		handleFailingNode(nsm.getHash());
	}

	private class NotifyTask extends TimerTask {
		
		private int action;
		//private Node notifyObj;
		
		public NotifyTask(int action) {
			this.action = action;
			//this.notifyObj = notifyObj;
		}
		
		@Override
		public void run() {
			//Queue current action
			try {
				actionQueue.put(action);
			}
			catch (InterruptedException e) {
				//Do nothing
			}
		}
	}
	
	public void killMe() {
		//notify(ACTION_KILL);
		startTask(null, ACTION_KILL, 0);
	}
	
	private synchronized TimerTask startTask(TimerTask timerTask,int action,int period) {
		//Stop if appropriate
		if(timerTask != null) {
			timerTask.cancel();
		}
		
		try {
			//Timer 
			if(timer != null) {
				//Schedule new event
				timerTask = new NotifyTask(action);
				timer.schedule(timerTask, period);
				
				//Return new task
				return timerTask;
			}
		} catch (Exception e){
			//e.printStackTrace();
		}
		return null;
	}
	
	private void handleFindPredecessorMessage(FindPredecessorMessage fpm) {
		FingerEntry findFinger;
		
		//Get responsible finger
		findFinger = getPredecessor(fpm.getHash());
		
		if(findFinger.equals(identity)) {
			//Its us => reply
			sendMessage(new FindPredecessorResponseMessage(identity.getSensibleThingsNode(),fpm.getOrigNode(),identity.getNodeID(),fpm.getHash()),null);
			
			//check if this is good or not
			updateFingerTable(new FingerEntry(fpm.getHash(),fpm.getOrigNode()));
		}
		else {
			//Forward to the best finger
			sendMessage(new FindPredecessorMessage(identity.getSensibleThingsNode(),findFinger.getSensibleThingsNode(),fpm.getHash(),fpm.getOrigNode()),findFinger.getNodeID());
		}
	}

	private void handleFindPredecessorResponseMessage(FindPredecessorResponseMessage fprm) {
		//Check if hash fits; ignore otherwise
		if(identity.getNodeID().equals(fprm.getOrigHash())) {
			//Update predecessor if it has changed
			FingerEntry newPredecessor = new FingerEntry(fprm.getPredecessorHash(),fprm.getFromNode());
			
			//Update predecessor
			updatePredecessor(newPredecessor);
/*			synchronized (this) {
				if(predecessor == null || !predecessor.equals(newPredecessor)) {
					//debugging
					if(predecessor != null) {
						fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_REMOVE_WORSE, identity, predecessor);
						fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_ADD_BETTER, identity, newPredecessor);
					}else {
						fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_ADD, identity, newPredecessor);
					}
					
					
					predecessor = newPredecessor;
				}
			}*/
			
			//We have a predecessor - stop trying to find one
			if(findPredecessorTask != null) {
				findPredecessorTask.cancel();
			}
				
		}
	}
	
	private synchronized void handleFailingNode(NodeID dst) {
		if(getSuccessor(null).getNodeID().equals(dst)) {
			//later fire events
//			fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_REMOVE_WORSE, identity, getSuccessor(null));
//			fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_ADD_BETTER, identity, getSuccessor(getSuccessor(null).getNodeID()));
			
			//Successor failed
			//Remove successor the next in the list will
			//automatically be the best next successor
			finger.remove(getSuccessor(null));
		}
		else if(predecessor != null && predecessor.getNodeID().equals(dst)) {
			/*//later fire events
			fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_REMOVE, identity, predecessor);
			
			//Predecessor failed
			predecessor = null;*/
			updatePredecessor(null);
			
			//Start recovery task
			//notify(ACTION_CHECK_PREDECESSOR);
			findPredecessorTask = startTask(findPredecessorTask,ACTION_CHECK_PREDECESSOR,0);
		}
		else if(finger.containsKey(new FingerEntry(dst, null))) {
			//Finger failed
			//FingerEntry removedFinger = finger.remove(new FingerEntry(dst, null));
			finger.remove(new FingerEntry(dst, null));
			//debugging
//			fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_REMOVE,identity, removedFinger);
		}
		
		//-----
		//Handle sensors that are involed with the failed node
		//-----
		FingerEntry failedFinger = new FingerEntry(dst,null);
		
		//Remove sensors, that do not longer exist
		sensorsResponsibleFor.remove(failedFinger);
		
		//Re-register nodes that got lost
		Set<Sensor> sensors = sensorsAt.get(failedFinger);
		FingerEntry sensorPre;
		FingerEntry owner;
		
		if(sensors != null) {
			for(Sensor s: sensors) {
				//put it to the "private sensortable"
				sensorsAt.put(identity, s);
				
				//Send immediate a register message
				sensorPre = getSensorPredecessor(s);
				owner = s.getOwner();
				sendMessage(new RegisterMessage(identity.getSensibleThingsNode(),sensorPre.getSensibleThingsNode(),s.getSensorHash(), owner.getNodeID(),owner.getSensibleThingsNode()),sensorPre.getNodeID());
			}
		}
	}
	
	public void debugBreak() {
		assert(false);
	}
	
	
	//////////////ARE THESE CORRECT????////////////////////// by steavi
	private void handleCheckPredecessorMessage(CheckPredecessorMessage cpm) {
		if(connected) {
			//Check if the predecessor is a better one
			FingerEntry newPredecessor = new FingerEntry(cpm.getHash(),cpm.getFromNode());
			updatePredecessor(newPredecessor);
			
			//Send answer with our predecessor
			sendMessage(new CheckPredecessorResponseMessage(identity.getSensibleThingsNode(), cpm.getFromNode(), predecessor.getSensibleThingsNode(), predecessor.getNodeID()),cpm.getHash());
		}
	}

	//////////////ARE THESE CORRECT????////////////////////// by steavi
	private void handleCheckPredecessorResponseMessage(CheckPredecessorResponseMessage cprm) {
		if(connected) {
			FingerEntry newSuccessor = new FingerEntry(cprm.getPreHash(),cprm.getPreNetworkNode());
			
			//Check if the successor if a better one
			updateFingerTable(newSuccessor);
		}
	}

	//////////////ARE THESE CORRECT????////////////////////// by steavi
	private void handleCheckSuccessorMessage(CheckSuccessorMessage csm) {
		if(connected) {
			//Check if the predecessor is a better one
			FingerEntry newSuccessor = new FingerEntry(csm.getHash(),csm.getFromNode());
			updateFingerTable(newSuccessor);
			
			//Send answer with our predecessor
			sendMessage(new CheckSuccessorResponseMessage(identity.getSensibleThingsNode(), csm.getFromNode(), getSuccessor(null).getSensibleThingsNode(), getSuccessor(null).getNodeID()),csm.getHash());
		}
	}

	//////////////ARE THESE CORRECT????////////////////////// by steavi	
	private void handleCheckSuccessorResponseMessage(CheckSuccessorResponseMessage csrm) {
		if(connected) {
			FingerEntry newPredecessor = new FingerEntry(csrm.getSucHash(),csrm.getSucNetworkNode());
			
			//Check if the successor if a better one
			updatePredecessor(newPredecessor);
		}
	}
		
	private void handleRegisterMessage(RegisterMessage rm) {
		FingerEntry registeringNode = new FingerEntry(rm.getOrigHash(), rm.getOrigNode());
		Sensor sensor = new Sensor(rm.getSensor(), registeringNode);
		
		//Check if we are responsible
		FingerEntry responsibleNode = getSensorPredecessor(sensor);
		
		if(responsibleNode.equals(identity)) {
			//We are responsible
			sensorsResponsibleFor.put(registeringNode, sensor);
			
			//send response message
			sendMessage(new RegisterResponseMessage(identity.getSensibleThingsNode(),rm.getOrigNode(),rm.getSensor(),identity.getNodeID()), rm.getOrigHash());
		}
		else {
			//Forward the message
			sendMessage(rm.cloneWithNewAddress(identity.getSensibleThingsNode(), responsibleNode.getSensibleThingsNode()) , responsibleNode.getNodeID());
		}
	}
	
	private void handleRegisterResponseMessage(RegisterResponseMessage rrm) {
		FingerEntry storage = new FingerEntry(rrm.getOrigHash(), rrm.getFromNode());
		Sensor sensor = new Sensor(rrm.getSensor(),identity);
		
		//Is it one of our sensors
		if(sensorsAt.contains(sensor)) {
			//Enter it with its storage link
			sensorsAt.put(storage, sensor);
		}
	}
	
	private void handleResolveMessage(ResolveMessage rm) {
		FingerEntry sensorFinger;
		FingerEntry sensorPredecessor;
		Sensor sensor = new Sensor(rm.getSensorHash(),null);
		
		//Check sensor responsibility
		sensorFinger = sensorsAt.get(sensor);
		if(sensorFinger == null) {
			sensorFinger = sensorsResponsibleFor.get(sensor);
		}
		
		if(sensorFinger == null) {
			//Forward to best finger
			sensorPredecessor = getSensorPredecessor(sensor);
			
			if(sensorPredecessor != null && !sensorPredecessor.equals(identity)) {
				//Forward
				sendMessage(rm.cloneWithNewAddress(identity.getSensibleThingsNode(), sensorPredecessor.getSensibleThingsNode()),sensorPredecessor.getNodeID());
			}
		}
		else {
			//Answer with response message
			sendMessage(new ResolveResponseMessage(identity.getSensibleThingsNode(), rm.getOrigNode(), sensor.getSensorHash(), sensorFinger.getSensibleThingsNode()),null);
		}
	}
	
	private void handleResolveResponseMessage(ResolveResponseMessage rrm) {
		//Abort all other attempts
		ResolveService rs;
		
		//Cancel resolve timer
		synchronized(activeResolves) {
			rs = activeResolves.remove(new Sensor(rrm.getSensor(),null));
		}
		
		if(rs != null) {
			//Abort timer and remove from list
			rs.abort();
			
			//Trigger successful resolve event
			disseminationCore.callResolveResponseListener(rs.uci, rrm.getSensorNode());
		}
		
	}
	
	private void rearrangeLocalSensorMapping() {
		List<Sensor> sensors;
		FingerEntry suc;
		
		synchronized (this) {
			suc = getSuccessor(null);
			//Get sensors we have to change
			sensors = sensorsResponsibleFor.getSensorInRange(getSuccessor(null).getNodeID(),identity.getNodeID());
			
			//Re-register all
			for(Sensor sensor: sensors) {
				FingerEntry owner = sensor.getOwner();
				sendMessage(new RegisterMessage(identity.getSensibleThingsNode(),suc.getSensibleThingsNode(),sensor.getSensorHash(),owner.getNodeID(),owner.getSensibleThingsNode()),suc.getNodeID());
				sensorsResponsibleFor.remove(sensor);
			}
		}
		
	}
	
	private void checkRemoteSensorMapping() {
		Set<Sensor> sensors = sensorsAt.get(identity);
		Set<Sensor> sensorsSelfResponsible = sensorsResponsibleFor.get(identity);
		
		if(sensors != null && sensorsSelfResponsible != null) {
			for(Sensor s: sensors) {
				if(!sensorsSelfResponsible.contains(s)) {
					//An other node should be responsible for this sensor...
					//Register it - send to the best fitting finger!
					FingerEntry sensorPredecessor = getSensorPredecessor(s);
					sendMessage(new RegisterMessage(identity.getSensibleThingsNode(), sensorPredecessor.getSensibleThingsNode(), s.getSensorHash(), s.getOwner().getNodeID(), s.getOwner().getSensibleThingsNode()), sensorPredecessor.getNodeID());
				}
			}
		}
	}
	
	public Map<Sensor,FingerEntry> getSensors() {
		HashMap<Sensor, FingerEntry> res = new HashMap<Sensor, FingerEntry>();
		res.putAll(sensorsAt.getCopyOfAll());
		res.putAll(sensorsResponsibleFor.getCopyOfAll());
		return res;
	}
	
	//-----
	//Private helper class to resolve a sensor several times
	//with different timeouts
	//Perhaps find a better way to not have a big private class like this
	//-----
	
	private class ResolveService {
		
		//Maximum number of attempts
		private final static int ATTEMPTS = 4;
		private int attempt;
		
		private Timer timer;
		private Sensor sensor;
		private String uci;
		
		private TimerTask currentTask;
		private ResolveFailListener listener;
		
		public ResolveService(Sensor sensor, String uci, Timer timer,ResolveFailListener listener) {
			//Init variables
			this.attempt = 0;
			this.uci = uci;
			this.sensor = sensor;
			this.timer = timer;
			this.listener = listener; 
			
			//Start the chain
			currentTask = new ResolveTask();
		}
		
		public synchronized void abort() {
			if(currentTask != null) {
				currentTask.cancel();
				currentTask = null;
			}
		}
		
		public synchronized boolean hasFailed() {
			return currentTask == null;
		}
		
		private class ResolveTask extends TimerTask {
			public ResolveTask() {
				try{
					if(attempt == 0) {
						//Send directly at first resolve request
						FingerEntry sensorPre = getSensorPredecessor(sensor);
						sendMessage(new ResolveMessage(identity.getSensibleThingsNode(), sensorPre.getSensibleThingsNode(), sensor.getSensorHash(), identity.getSensibleThingsNode()), sensorPre.getNodeID());
					}
					
					//Schedule timer
					timer.schedule(this,(1 << attempt) * 1000);
					
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			
			@Override
			public void run() {
				//Shall we try another attempt?
				if(++attempt < ATTEMPTS) {
					//Send new resolve request
					FingerEntry sensorPre = getSensorPredecessor(sensor);
					sendMessage(new ResolveMessage(identity.getSensibleThingsNode(), sensorPre.getSensibleThingsNode(), sensor.getSensorHash(), identity.getSensibleThingsNode()), sensorPre.getNodeID());

					//Start new ResolveTask
					synchronized(this) {
						//Re-schedule timer
						currentTask = new ResolveTask();
					}
				}
				else {
					//Object might remain
					//currentTask = null means that resolve failed in the last try
					synchronized(this) {
						currentTask = null;
					}
					
					//Resolve failed event
					listener.OnResolveFail(sensor,uci);
				}
			}
		}
	}

	public void OnResolveFail(Sensor sensor,String uci) {
		
		//Forward fail to dissemination layer
		
		//REMOVED... just kill it
		//disseminationCore.callResolveResponseListener(uci, null);
		
		//Remove failed resolveService entry
		activeResolves.remove(sensor);
	}
}
