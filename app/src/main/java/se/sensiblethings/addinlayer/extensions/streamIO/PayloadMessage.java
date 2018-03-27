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

import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

/**
 *
 * @author Maksym Aryefyev
 */
public class PayloadMessage extends Message {

    private static final long serialVersionUID = 10L;
    public String uci;
    private byte[] buffer;
    public boolean moreFiles = false;//for later maybe    
    private int packetSequenceNumber;
    private int lastPacketNumber;
    private String fileName;    
    

    public PayloadMessage(String uci, SensibleThingsNode toNode, SensibleThingsNode fromNode, byte[] buffer, 
            boolean moreFiles, int packetSequenceNumber, int lastPacketNumber, String fileName) {
        super(fromNode, toNode);
        this.uci = uci;
        this.buffer = buffer;
        this.moreFiles = moreFiles;
        this.lastPacketNumber = lastPacketNumber;
        this.fileName = fileName;
        this.packetSequenceNumber = packetSequenceNumber;
    }//end constructor
    
    public byte[] getPayload(){
        return this.buffer;
    }//end method
    
    public int getPacketSequenceNumber() {
        return packetSequenceNumber;
    }

    public int getLastPacketNumber() {
        return lastPacketNumber;
    }

    public String getFileName() {
        return fileName;
    }
}//end class
