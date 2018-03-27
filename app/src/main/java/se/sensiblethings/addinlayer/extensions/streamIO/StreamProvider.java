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

import java.io.IOException;
import java.io.OutputStream;

import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.DestinationNotReachableException;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

//import java.nio.file.Files;
//import java.nio.file.Path;

/**
 *
 * @author Maksym Aryefyev
 */
public class StreamProvider extends OutputStream {

    private Communication communication;
    private SensibleThingsNode subscriber;
    private String uci;
    public static final int DEFAULT_BUFFER_SIZE = 512;//8Kb kiils a network connection
    public static final int DEFAULT_MAX_BUFFER_SIZE = 8192;
    private byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    private byte[] outdata = null;
    private int position = 0; // next empty position in array
    private int bufferMax = DEFAULT_MAX_BUFFER_SIZE;
    private boolean closed = false;
    
    
    
    private int packetSequenceNumber = 0;
    private int lastPacketNumber = 0;
    private String fileName;

    public StreamProvider(String uci, SensibleThingsNode subscriber, SensibleThingsPlatform platform) {
        this.communication = platform.getDisseminationCore().getCommunication();
        this.subscriber = subscriber;
        this.uci = uci;
    }//end constructor

    //--------------------------buffers-----------------------------------//
    public int getBufferSize() {
        return buffer.length;
    }//end method

    public void setBufferSize(int bufferSize) throws IOException {
        flush();

        if (bufferSize == buffer.length) {
            return;//we already have this size
        } else if (bufferSize > 0) {

            if (bufferSize > bufferMax) {
                this.buffer = new byte[this.bufferMax];
            } else {
                this.buffer = new byte[bufferSize];
            }

        } else {
            return;//integer is less than zero - do nothing
        }
    }//end method

    public void setMaxBufferSize(int buffMax) {
        this.bufferMax = buffMax;
    }//end method

    //---------------------------end buffers------------------------------//
    @Override
    public void write(int b) throws IOException {
        if (this.closed) {
            throw new IOException("Attempting to write to closed stream.");
        }

        buffer[position] = (byte) (b & 0x0ff);
        position++;

        if (position >= buffer.length) {
            flush();
        }

    }//end method

    //do I need to handel nullpointerexception here? if data == null
    @Override
    public void write(byte[] data, int off, int len) throws IOException {
        if (this.closed) {
            throw new IOException("Attempting to write to closed stream.");
        }
        if (data == null) {
            throw new NullPointerException("null reference is passed as a parameter");
        }

        int remainingLength = len;

        while (buffer.length - position <= remainingLength) {

            System.arraycopy(data, off + (len - remainingLength), buffer, position, buffer.length - position);
            remainingLength -= buffer.length - position;
            position = buffer.length;
            flush();

        }//end while

        if (remainingLength == 0) {
            return;
        }

        System.arraycopy(data, off + (len - remainingLength), buffer, position, remainingLength);
        position += remainingLength;

    }//end method

    //do I need to handel nullpointerexception here? if data == null
    @Override
    public void write(byte[] data) throws IOException {
        if (this.closed) {
            throw new IOException("Attempting to write to closed stream.");
        }
        if (data == null) {
            throw new NullPointerException("null reference is passed as a parameter");
        }
        write(data, 0, data.length);
    }//end method

    @Override
    public void flush() throws IOException {
        if (this.closed) {
            throw new IOException("Attempting to write to closed stream.");
        }
        

        //copy the content of the buffer to new array
        //or if buffer is full use it directly

        if (position == buffer.length) {
            outdata = buffer;
        } else {
            outdata = new byte[position];
            System.arraycopy(buffer, 0, outdata, 0, position);
        }

        packetSequenceNumber++;
        PayloadMessage message = new PayloadMessage(uci, subscriber, communication.getLocalSensibleThingsNode(), outdata, false, this.packetSequenceNumber, 
                this.lastPacketNumber, this.fileName);
        try {
            communication.sendMessage(message);
        } catch (DestinationNotReachableException e) {
//                e.printStackTrace();
        }

        System.out.println(packetSequenceNumber + "  of " + lastPacketNumber);
        //reset position in buffer
        position = 0;
        try {
            Thread.sleep(3);//need to slow down, cause recieving side gets outofmemory exception Java heap space; on files bigger then 15Mb
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }//end method 

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            this.communication = null;
            this.position = 0;
        }
    }//end close
    /*
    public void sendFile(Path file) throws IOException{
        if(Files.exists(file)){
            this.fileName = file.getFileName().toString();
            this.lastPacketNumber = (int)Math.ceil((double)Files.size(file) / (double)buffer.length);
            Files.copy(file, this);
            this.flush();
        }
        
        
    }//end method*/
}//end class
