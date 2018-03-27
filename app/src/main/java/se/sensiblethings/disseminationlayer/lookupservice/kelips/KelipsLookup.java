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

package se.sensiblethings.disseminationlayer.lookupservice.kelips;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.CommunicationStateListener;
import se.sensiblethings.disseminationlayer.communication.DestinationNotReachableException;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.disseminationlayer.disseminationcore.DisseminationCore;
import se.sensiblethings.disseminationlayer.disseminationcore.MessageListener;
import se.sensiblethings.disseminationlayer.lookupservice.LookupService;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.messages.KelipsHeartBeatMessage;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.messages.KelipsJoinMessage;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.messages.KelipsJoinResponseMessage;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.messages.KelipsRegisterMessage;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.messages.KelipsResolveMessage;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.messages.KelipsResolveResponseMessage;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.messages.KelipsSyncCheckMessage;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.messages.KelipsSyncMessage;
import se.sensiblethings.disseminationlayer.lookupservice.kelips.messages.KelipsSyncResponseMessage;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class KelipsLookup extends LookupService implements MessageListener {


    Random r = new Random(System.currentTimeMillis());

    public static boolean BOOTSTRAP = false;
    public static String bootstrapIp = "192.168.0.103";
    public static final int bootstrapPort = 9009;
    private boolean bootstrap = false;

    String selfHash = null;
    int selfGroup = -1;
    Finger selfFinger = null;
    boolean initalized = false;
    boolean runLookupService = true;
    AffinityGroup groups[] = new AffinityGroup[8];
    Thread heartBeatThread = new HeartBeatThread();
    HashMap<Integer, String> resolveMap = new HashMap<Integer, String>();

    //One is used by a normal node, the other by the bootstrap
    ConcurrentHashMap<String, TimestampedNode> lookupTable = new ConcurrentHashMap<String, TimestampedNode>();
    ArrayList<ConcurrentHashMap<String, TimestampedNode>> bootstraplookupTables = new ArrayList<ConcurrentHashMap<String, TimestampedNode>>();

    public KelipsLookup(final Communication communication, DisseminationCore disseminationCore) {
        super(communication, disseminationCore);
        bootstrap = BOOTSTRAP;
        try {
            communication.registerMessageListener(KelipsHeartBeatMessage.class.getName(), this);
            communication.registerMessageListener(KelipsJoinMessage.class.getName(), this);
            communication.registerMessageListener(KelipsJoinResponseMessage.class.getName(), this);
            communication.registerMessageListener(KelipsResolveMessage.class.getName(), this);
            communication.registerMessageListener(KelipsResolveResponseMessage.class.getName(), this);
            communication.registerMessageListener(KelipsSyncCheckMessage.class.getName(), this);
            communication.registerMessageListener(KelipsSyncMessage.class.getName(), this);
            communication.registerMessageListener(KelipsSyncResponseMessage.class.getName(), this);
            communication.registerMessageListener(KelipsRegisterMessage.class.getName(), this);
            final Thread startThread = new Thread(new Runnable() {
                public void run() {
                    String unique = System.currentTimeMillis() + ":" + r.nextDouble();
                    selfHash = sha256hash(unique);
                    selfGroup = findAffinityGroup(selfHash);
                    selfFinger = new Finger(communication.getLocalSensibleThingsNode());

                    if (bootstrap) {
                        //I am the bootstrap
                        //Create a lookuptable for each group
                        for (int i = 0; i != 8; i++) {
                            bootstraplookupTables.add(new ConcurrentHashMap<String, TimestampedNode>());
                        }

                        //Fill the groups ourself
                        Finger selfFinger = new Finger(communication.getLocalSensibleThingsNode());
                        for (int i = 0; i != 8; i++) {
                            groups[i] = new AffinityGroup();
                            groups[i].setFinger(selfFinger, 0);
                            groups[i].setFinger(selfFinger, 1);
                            groups[i].setFinger(selfFinger, 2);
                        }
                        initalized = true;
                    } else {
                        //Not a bootstrap
                        //Send out join message
                        SensibleThingsNode bootstrapNode = communication.createSensibleThingsNode(bootstrapIp, bootstrapPort);
                        try {
                            communication.sendMessage(new KelipsJoinMessage(communication.getLocalSensibleThingsNode(), bootstrapNode));
                        } catch (DestinationNotReachableException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            startThread.setName("Kelips initialization thread");
            if (communication.getState().equals(Communication.COMMUNICATION_STATE_CONNECTED)) {
                startThread.start();
            } else {
                communication.addStateListener(new CommunicationStateListener() {

                    public void onNewCommunicationState(Communication communication,
                                                        String state) {
                        if (state.equals(Communication.COMMUNICATION_STATE_CONNECTED)) {
                            startThread.start();
                            communication.removeStateListener(this);
                        }

                    }
                });
            }
            //Wait for answer...// little modification for contexter--Adlo
            int counter_unknown_lookup_lim = 1;// max -- 10sec
            do {
                Thread.sleep(100);
                if (counter_unknown_lookup_lim++ == 10) break;
            } while (!initalized);

            if (initalized) {
                setState(STATE_CONNECTED);
                //Start the heart beating
                heartBeatThread.start();
            } else {
                setState(STATE_DISCONNECTING);
                runLookupService = false;
                heartBeatThread.interrupt();
                setState(STATE_DISCONNECTED);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resolve(String uci) {
        try {
            //Hash uci
            String uciHash = sha256hash(uci);
            int targetGroup = findAffinityGroup(uciHash);
            int id = r.nextInt();
            resolveMap.put(id, uci);
            //Send message to all fingers in the correct affinity group
            for (int i = 0; i != 3; i++) {
                Finger finger = groups[targetGroup].getFinger(i);
                KelipsResolveMessage resolveMessage = new KelipsResolveMessage(communication.getLocalSensibleThingsNode(), finger.getNode(), uci, uciHash, targetGroup, id);
                try {
                    communication.sendMessage(resolveMessage);
                } catch (DestinationNotReachableException e) {
                    //e.printStackTrace();
                    groups[targetGroup].setFinger(new Finger(communication.createSensibleThingsNode(bootstrapIp, bootstrapPort)), i);
                }
            }
            //Wait for an answer from the first answer
            //return that to the user

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(String uci) {
        register(uci, null);
    }

    @Override
    public void register(String uci, String password) {
        try {
            //Put into own lookupTable

            if (bootstrap) {
                int group = findAffinityGroup(sha256hash(uci));
                ConcurrentHashMap<String, TimestampedNode> hm = bootstraplookupTables.get(group);
                hm.put(sha256hash(uci), new TimestampedNode(communication.getLocalSensibleThingsNode(), System.currentTimeMillis()));
                //Start first Sync
                for (int i = 0; i != 3; i++) {
                    KelipsSyncMessage syncMessage = new KelipsSyncMessage(communication.getLocalSensibleThingsNode(), groups[group].getFinger(i).getNode(), group, hm);
                    try {
                        communication.sendMessage(syncMessage);
                    } catch (DestinationNotReachableException e) {
                        //e.printStackTrace();
                        groups[group].setFinger(new Finger(communication.createSensibleThingsNode(bootstrapIp, bootstrapPort)), i);
                    }
                }

            } else {
                String uciHash = sha256hash(uci);
                int targetGroup = findAffinityGroup(uciHash);

                for (int i = 0; i != 3; i++) {
                    KelipsRegisterMessage registerMessage = new KelipsRegisterMessage(communication.getLocalSensibleThingsNode(), groups[targetGroup].getFinger(i).getNode(), uci, uciHash, targetGroup, new TimestampedNode(communication.getLocalSensibleThingsNode(), System.currentTimeMillis()));
                    try {
                        communication.sendMessage(registerMessage);
                    } catch (DestinationNotReachableException e) {
                        //e.printStackTrace();
                        groups[targetGroup].setFinger(new Finger(communication.createSensibleThingsNode(bootstrapIp, bootstrapPort)), i);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        disseminationCore.callSuccessfulRegisterListener(uci);
    }

    @Override
    public void shutdown() {
        setState(STATE_DISCONNECTING);
        runLookupService = false;
        heartBeatThread.interrupt();

        //Send leave to all fingers with the other 2 fingers in my group
        //NOT NEEDED, but is nice...

        setState(STATE_DISCONNECTED);
    }

    public void handleMessage(Message message) {
        try {

            if (message instanceof KelipsJoinMessage) {
                //Join
                //Send the whole fingertable
                KelipsJoinMessage joinMessage = (KelipsJoinMessage) message;
                KelipsJoinResponseMessage joinResponseMessage = new KelipsJoinResponseMessage(communication.getLocalSensibleThingsNode(), joinMessage.getFromNode(), groups);
                try {
                    communication.sendMessage(joinResponseMessage);
                } catch (DestinationNotReachableException e) {
                    //e.printStackTrace();
                }

            } else if (message instanceof KelipsJoinResponseMessage) {
                //JoinAnswe
                //Insert all fingers into the affinity groups
                KelipsJoinResponseMessage joinResponseMessage = (KelipsJoinResponseMessage) message;
                this.groups = joinResponseMessage.getGroups();

                //Insert self into the group
                this.groups[selfGroup].replaceRandomFinger(selfFinger);
                //Start first Sync
                for (int i = 0; i != 3; i++) {
                    KelipsSyncMessage syncMessage = new KelipsSyncMessage(communication.getLocalSensibleThingsNode(), groups[selfGroup].getFinger(i).getNode(), selfGroup, lookupTable);
                    try {
                        communication.sendMessage(syncMessage);
                    } catch (DestinationNotReachableException e) {
                        groups[selfGroup].setFinger(new Finger(communication.createSensibleThingsNode(bootstrapIp, bootstrapPort)), i);
                        //e.printStackTrace();
                    }
                }


                //Thread.sleep(1000);
                //We are all set!
                initalized = true;

            } else if (message instanceof KelipsSyncCheckMessage) {
                KelipsSyncCheckMessage syncCheckMessage = (KelipsSyncCheckMessage) message;


                int incomingTableHash = syncCheckMessage.getTableHash();
                int groupNumber = syncCheckMessage.getGroupNumber();

                if (bootstrap) {
                    ConcurrentHashMap<String, TimestampedNode> table = bootstraplookupTables.get(groupNumber);

                    if (table.hashCode() != incomingTableHash) {
                        KelipsSyncMessage syncMessage = new KelipsSyncMessage(communication.getLocalSensibleThingsNode(), syncCheckMessage.getFromNode(), groupNumber, table);
                        try {
                            communication.sendMessage(syncMessage);
                        } catch (DestinationNotReachableException e) {
                            //e.printStackTrace();
                        }
                    }

                } else {

//	        		System.out.println("self:" + lookupTable.hashCode() + " inc:" + incomingTableHash);

                    if (lookupTable.hashCode() != incomingTableHash) {
                        KelipsSyncMessage syncMessage = new KelipsSyncMessage(communication.getLocalSensibleThingsNode(), syncCheckMessage.getFromNode(), groupNumber, lookupTable);
                        try {
                            communication.sendMessage(syncMessage);
                        } catch (DestinationNotReachableException e) {
                            //e.printStackTrace();
                        }
                    }
                }

            } else if (KelipsSyncMessage.class.isAssignableFrom(message.getClass())) {
                KelipsSyncMessage syncMessage = (KelipsSyncMessage) message;

                //Get Table, insert all posts.

                if (bootstrap) {
                    ConcurrentHashMap<String, TimestampedNode> incomingTable = syncMessage.getLookupTable();
                    int groupNumber = syncMessage.getGroupNumber();
                    ConcurrentHashMap<String, TimestampedNode> hm = bootstraplookupTables.get(groupNumber);

                    //iterating over keys only
                    for (String key : incomingTable.keySet()) {
                        TimestampedNode incomingNode = incomingTable.get(key);
                        if (hm.containsKey(key)) {
                            TimestampedNode node = hm.get(key);
                            if (incomingNode.getTimestamp() > node.getTimestamp()) {
                                hm.put(key, incomingNode);
                            }
                        } else {
                            hm.put(key, incomingNode);
                        }
                    }
                    //hm.putAll(incomingTable);

                    //Send back the correct hashtable
                    KelipsSyncResponseMessage syncResponseMessage = new KelipsSyncResponseMessage(communication.getLocalSensibleThingsNode(), syncMessage.getFromNode(), groupNumber, hm);
                    try {
                        communication.sendMessage(syncResponseMessage);
                    } catch (DestinationNotReachableException e) {
                        //e.printStackTrace();
                    }

                } else {
                    ConcurrentHashMap<String, TimestampedNode> incomingTable = syncMessage.getLookupTable();
                    //iterating over keys only
                    for (String key : incomingTable.keySet()) {
                        TimestampedNode incomingNode = incomingTable.get(key);
                        if (lookupTable.containsKey(key)) {
                            TimestampedNode node = lookupTable.get(key);
                            if (incomingNode.getTimestamp() > node.getTimestamp()) {
                                lookupTable.put(key, incomingNode);
                            }
                        } else {
                            lookupTable.put(key, incomingNode);
                        }
                    }
                    //lookupTable.putAll(incomingTable);


                    //Send back our own hashtable
                    KelipsSyncResponseMessage syncResponseMessage = new KelipsSyncResponseMessage(communication.getLocalSensibleThingsNode(), syncMessage.getFromNode(), selfGroup, lookupTable);
                    try {
                        communication.sendMessage(syncResponseMessage);
                    } catch (DestinationNotReachableException e) {
                        //e.printStackTrace();
                    }
                }


            } else if (message instanceof KelipsSyncResponseMessage) {
                KelipsSyncResponseMessage syncResponseMessage = (KelipsSyncResponseMessage) message;

                //Get Table, insert all posts.
                if (bootstrap) {
                    ConcurrentHashMap<String, TimestampedNode> incomingTable = syncResponseMessage.getLookupTable();
                    int groupNumber = syncResponseMessage.getGroupNumber();
                    ConcurrentHashMap<String, TimestampedNode> hm = bootstraplookupTables.get(groupNumber);
                    for (String key : incomingTable.keySet()) {
                        TimestampedNode incomingNode = incomingTable.get(key);
                        if (hm.containsKey(key)) {
                            TimestampedNode node = hm.get(key);
                            if (incomingNode.getTimestamp() > node.getTimestamp()) {
                                hm.put(key, incomingNode);
                            }
                        } else {
                            hm.put(key, incomingNode);
                        }
                    }
                    //hm.putAll(incomingTable);

                } else {
                    ConcurrentHashMap<String, TimestampedNode> incomingTable = syncResponseMessage.getLookupTable();
                    for (String key : incomingTable.keySet()) {
                        TimestampedNode incomingNode = incomingTable.get(key);
                        if (lookupTable.containsKey(key)) {
                            TimestampedNode node = lookupTable.get(key);
                            if (incomingNode.getTimestamp() > node.getTimestamp()) {
                                lookupTable.put(key, incomingNode);
                            }
                        } else {
                            lookupTable.put(key, incomingNode);
                        }
                    }
                    //lookupTable.putAll(incomingTable);
                }

            } else if (message instanceof KelipsHeartBeatMessage) {
                KelipsHeartBeatMessage heartBeatMessage = (KelipsHeartBeatMessage) message;

                //Heartbeat
                //Insert the the beating node as one of the fingers in that group
                Finger remoteFinger = heartBeatMessage.getFinger();
                int remoteGroup = heartBeatMessage.getGroupNumber();
                if (groups[remoteGroup].fingerExists(remoteFinger)) {
                    //Already exists, do nothing
                } else {
                    //Else replace random finger
                    groups[remoteGroup].replaceRandomFinger(remoteFinger);
                }
            } else if (message instanceof KelipsResolveMessage) {
                KelipsResolveMessage resolveMessage = (KelipsResolveMessage) message;
                String uciHash = resolveMessage.getUciHash();

                //Resolve requiest
                //Look into hashtable
                //Answer with answer

                if (bootstrap) {
                    int groupNumber = resolveMessage.getGroupNumber();
                    ConcurrentHashMap<String, TimestampedNode> table = bootstraplookupTables.get(groupNumber);
                    TimestampedNode node = table.get(uciHash);
                    KelipsResolveResponseMessage resolveResponseMessage = new KelipsResolveResponseMessage(communication.getLocalSensibleThingsNode(), resolveMessage.getFromNode(), resolveMessage.getUci(), resolveMessage.getUciHash(), node, resolveMessage.getId());
                    try {
                        communication.sendMessage(resolveResponseMessage);
                    } catch (DestinationNotReachableException e) {
                        //e.printStackTrace();
                    }

                } else {
                    TimestampedNode node = lookupTable.get(uciHash);
                    KelipsResolveResponseMessage resolveResponseMessage = new KelipsResolveResponseMessage(communication.getLocalSensibleThingsNode(), resolveMessage.getFromNode(), resolveMessage.getUci(), resolveMessage.getUciHash(), node, resolveMessage.getId());
                    try {
                        communication.sendMessage(resolveResponseMessage);
                    } catch (DestinationNotReachableException e) {
                        //e.printStackTrace();
                    }
                }


            } else if (message instanceof KelipsResolveResponseMessage) {
                KelipsResolveResponseMessage resolveResponseMessage = (KelipsResolveResponseMessage) message;

                //Resolve Answer
                //Check if null, discard
                //Check if it has already been answered
                //If not return to user
                // else discard

                String uci = resolveResponseMessage.getUci();
                int id = resolveResponseMessage.getId();
                TimestampedNode timednode = resolveResponseMessage.getNode();
                if (timednode != null) {
                    SensibleThingsNode node = timednode.getNode();

                    if (resolveMap.containsKey(id)) {
                        resolveMap.remove(id);
                        disseminationCore.callResolveResponseListener(uci, node);
                    } else {
                        //Already found
                        //Do nothing
                    }
                } else {

                }

            } else if (message instanceof KelipsRegisterMessage) {
                KelipsRegisterMessage registerMessage = (KelipsRegisterMessage) message;

                TimestampedNode node = registerMessage.getNode();
                String uciHash = registerMessage.getUciHash();
                int groupNumber = registerMessage.getGroupNumber();

                if (bootstrap) {
                    ConcurrentHashMap<String, TimestampedNode> table = bootstraplookupTables.get(groupNumber);
                    table.put(uciHash, node);

                } else {
                    lookupTable.put(uciHash, node);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private class HeartBeatThread extends Thread {

        @Override
        public void run() {

            //Loop
            while (runLookupService) {
                try {
                    //Sleep
                    Thread.sleep(59000);//Correct

//					Thread.sleep(5000);//DEBUG


                    //SyncCheck with finger 0
                    //SyncCheck with finger 1
                    //SyncCheck with finger 2
                    if (bootstrap) {
                        for (int i = 0; i != 8; i++) {
                            for (int j = 0; j != 3; j++) {
                                ConcurrentHashMap<String, TimestampedNode> hm = bootstraplookupTables.get(i);
                                //KelipsSyncMessage syncMessage = new KelipsSyncMessage(communication.getLocalSensibleThingsNode(), groups[i].getFinger(j).getNode(), i, hm);
                                KelipsSyncCheckMessage syncCheckMessage = new KelipsSyncCheckMessage(communication.getLocalSensibleThingsNode(), groups[i].getFinger(j).getNode(), i, hm.hashCode());

                                try {
                                    communication.sendMessage(syncCheckMessage);
                                } catch (DestinationNotReachableException e) {
                                    //e.printStackTrace();
                                    groups[i].setFinger(new Finger(communication.createSensibleThingsNode(bootstrapIp, bootstrapPort)), j);
                                }
                            }
                        }
                    } else {
                        for (int i = 0; i != 3; i++) {
                            //KelipsSyncMessage syncMessage = new KelipsSyncMessage(communication.getLocalSensibleThingsNode(), groups[selfGroup].getFinger(i).getNode(), selfGroup, lookupTable);
                            KelipsSyncCheckMessage syncCheckMessage = new KelipsSyncCheckMessage(communication.getLocalSensibleThingsNode(), groups[selfGroup].getFinger(i).getNode(), selfGroup, lookupTable.hashCode());
                            try {
                                communication.sendMessage(syncCheckMessage);
                            } catch (DestinationNotReachableException e) {
                                //e.printStackTrace();
                                groups[selfGroup].setFinger(new Finger(communication.createSensibleThingsNode(bootstrapIp, bootstrapPort)), i);
                            }
                        }
                    }

                    //Random 0-3
                    int randomFinger = r.nextInt(3);
                    for (int i = 0; i != 8; i++) {
                        //Heartbeat with finger 0r
                        //Heartbeat with finger 1r
                        //Heartbeat with finger 2r
                        //Heartbeat with finger 3r
                        //Heartbeat with finger 4r
                        //Heartbeat with finger 5r
                        //Heartbeat with finger 6r
                        //Heartbeat with finger 7r
                        if (bootstrap) {
                            KelipsHeartBeatMessage hbMessage = new KelipsHeartBeatMessage(communication.getLocalSensibleThingsNode(), groups[i].getFinger(randomFinger).getNode(), i, selfFinger);
                            try {
                                communication.sendMessage(hbMessage);
                            } catch (DestinationNotReachableException e) {
                                //e.printStackTrace();
                                groups[i].setFinger(new Finger(communication.createSensibleThingsNode(bootstrapIp, bootstrapPort)), randomFinger);
                            }
                        } else {
                            KelipsHeartBeatMessage hbMessage = new KelipsHeartBeatMessage(communication.getLocalSensibleThingsNode(), groups[i].getFinger(randomFinger).getNode(), selfGroup, selfFinger);
                            try {
                                communication.sendMessage(hbMessage);
                            } catch (DestinationNotReachableException e) {
                                //e.printStackTrace();
                                groups[i].setFinger(new Finger(communication.createSensibleThingsNode(bootstrapIp, bootstrapPort)), randomFinger);
                            }
                        }
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                //Redo
            }
        }
    }

    private MessageDigest digest = null;

    private String sha256hash(String string) {
        try {
            if (digest == null) {
                digest = MessageDigest.getInstance("SHA-256");
            }
            byte[] hash = digest.digest(string.getBytes("UTF-8"));
            //Convert to Hex
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int findAffinityGroup(String hash) {
        if (hash.endsWith("0") || hash.endsWith("1")) {
            return 0;
        } else if (hash.endsWith("2") || hash.endsWith("3")) {
            return 1;
        } else if (hash.endsWith("4") || hash.endsWith("5")) {
            return 2;
        } else if (hash.endsWith("6") || hash.endsWith("7")) {
            return 3;
        } else if (hash.endsWith("8") || hash.endsWith("9")) {
            return 4;
        } else if (hash.endsWith("a") || hash.endsWith("b")) {
            return 5;
        } else if (hash.endsWith("c") || hash.endsWith("d")) {
            return 6;
        } else if (hash.endsWith("e") || hash.endsWith("f")) {
            return 7;
        }
        return -1;
    }

    public String getHashTableStrings() {
        String output = "";
        for (int i = 0; i != 8; i++) {
            ConcurrentHashMap<String, TimestampedNode> table = bootstraplookupTables.get(i);
            output = output + i + " " + table.keySet() + "\n";
            output = output + i + " " + table.values() + "\n";
        }
        return output;

    }

    public String getFingerTableStrings() {
        String output = "";
        for (int i = 0; i != 8; i++) {
            output = output + "Group " + i + "\n";
            for (int j = 0; j != 3; j++) {
                output = output + groups[i].getFinger(j).getNode().toString() + "\n";
            }
        }
        return output;

    }
}
