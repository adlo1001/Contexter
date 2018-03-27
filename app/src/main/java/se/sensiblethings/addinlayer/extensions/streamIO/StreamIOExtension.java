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

package se.sensiblethings.addinlayer.extensions.streamIO;

import java.io.InputStream;
import java.util.HashMap;

import se.sensiblethings.addinlayer.extensions.Extension;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.DestinationNotReachableException;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.disseminationlayer.disseminationcore.DisseminationCore;
import se.sensiblethings.disseminationlayer.disseminationcore.MessageListener;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

/**
 *
 * @author Maksym Aryefyev
 */
public class StreamIOExtension implements Extension, MessageListener {

    private SensibleThingsPlatform platform = null;
    private DisseminationCore core = null;
    Communication communication = null;
    private final Object sync = new Object();
    //incomming files
    private HashMap<String, IncomingPacketListener> incomingFiles = new HashMap<String, IncomingPacketListener>();
    //file requests
    private HashMap<String, FileRequestListener> outgoingFiles = new HashMap<String, FileRequestListener>();

    public void loadAddIn(SensibleThingsPlatform platform) {
        this.platform = platform;
        this.core = platform.getDisseminationCore();
        this.communication = core.getCommunication();

        //Register our own message types in the post office

        communication.registerMessageListener(GetFileRequestMessage.class.getName(), this);
        communication.registerMessageListener(PayloadMessage.class.getName(), this);
    }

    public void startAddIn() {
    }

    public void stopAddIn() {
    }

    public void unloadAddIn() {
    }

    public void handleMessage(Message msg) {

        if (msg instanceof GetFileRequestMessage) {
            //Start the subscription in the addIn
            GetFileRequestMessage getFileMessage = (GetFileRequestMessage) msg;
            FileRequestListener frl = outgoingFiles.get(getFileMessage.uci);
            System.out.println("Get file request from: " + getFileMessage.getFromNode()
                    + " for feed " + getFileMessage.uci);
            if (frl != null) {
                frl.getFileRequest(getFileMessage.uci, getFileMessage.getFromNode());
            }


        } else if (msg instanceof PayloadMessage) {
            synchronized (sync) {
                PayloadMessage payloadMessage = (PayloadMessage) msg;
                //redirect to listener
                System.out.println("----- " + payloadMessage.getPacketSequenceNumber());
                IncomingPacketListener ipl = incomingFiles.get(payloadMessage.uci);
                if (ipl != null) {
                    ipl.recieve(payloadMessage);
                }
            }
        }//end if
    }//end method

    void sendFileRequest(String uci, SensibleThingsNode node) {

        //Send out the startSubscribe Message
        GetFileRequestMessage message = new GetFileRequestMessage(uci, node, communication.getLocalSensibleThingsNode());

        try {
            communication.sendMessage(message);
        } catch (DestinationNotReachableException e) {
            //Do nothing
            //e.printStackTrace();
        }
    }//end method

    //addin must be loaded to manager before you can use this    
    public void registerFeed(String uci, FileRequestListener listener) {

        FileRequestListener frl = outgoingFiles.get(uci);
        if (frl == null) {
            platform.register(uci);
            outgoingFiles.put(uci, listener);
        } else {
            //probably need to throw something to say that already exists            
        }
    }//end method

    public void unregistreFeed(String uci) {
    }

    //addin must be loaded to manager before you can use this
    public void tryToGetFileFromSensor(String uci, SensibleThingsNode node, FileReceiverListener fileListener) {
        InputStream stream = new StreamConsumer();
        incomingFiles.put(uci, (IncomingPacketListener) stream);
        sendFileRequest(uci, node);
        fileListener.getFileFromSensor(uci, stream);
    }//end method    
}//end class
