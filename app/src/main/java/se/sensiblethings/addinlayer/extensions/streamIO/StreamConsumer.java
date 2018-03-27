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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Maksym Aryefyev
 */
public class StreamConsumer extends InputStream implements IncomingPacketListener {

    private static final int INITIAL_PACKET_BUFFER_SIZE = 4096;
    private static final int MAXIMUM_QUEUE_LENGTH = 5242880;
    private AtomicInteger currentQueueLength = new AtomicInteger();
    private Queue<byte[]> queue = new ConcurrentLinkedQueue<byte[]>();
    private byte[] recievedData = new byte[INITIAL_PACKET_BUFFER_SIZE];
    private int position = INITIAL_PACKET_BUFFER_SIZE;
    private int value;
    private boolean closed = false;
    private boolean lastPacketOfFileRecieved = false;
    //flow control
    private boolean[] recievedPacketsChecker;
    private Map<String, byte[]> recievedPackets;
    private int nextPacketExpected = 1;
    //delete after testing
    int recievedB = 0;

    public StreamConsumer() {
    }//end constructor

    @Override
    public int read() throws IOException {
        if (this.closed) {
            throw new IOException("Attempting to read from closed stream.");
        }

        if (position == recievedData.length) {
            getMore();//populate buffer from queue
        }

        value = recievedData[position] & 0x0ff;
        position++;
        return value;
    }//end method

    @Override
    public int read(byte[] buffer) throws IOException {
        if (this.closed) {
            throw new IOException("Attempting to read from closed stream.");
        }
        if (buffer == null) {
            throw new NullPointerException("null reference is passed as a parameter");
        }
        return read(buffer, 0, buffer.length);
    }//end method

    @Override
    public int read(byte[] buffer, int off, int len) throws IOException {
        if (this.closed) {
            throw new IOException("Attempting to read from closed stream.");
        }
        if (buffer == null) {
            throw new NullPointerException("null reference is passed as a parameter");
        }
        if (position == recievedData.length) {
            getMore();//populate buffer from queue
            if (position == recievedData.length) {
                System.err.println("Lets return -1");
                return -1;
            }
        }

        int remainingLength = len;

        while (recievedData.length - position < remainingLength) {
            System.arraycopy(recievedData, position, buffer, off + (len - remainingLength), recievedData.length - position);
            remainingLength -= recievedData.length - position;
            getMore();
            if (position == recievedData.length) {
                return len - remainingLength;
            }
        }

        System.arraycopy(recievedData, position, buffer, off + (len - remainingLength), remainingLength);
        position += remainingLength;
        return len;

    }//end method

    @Override
    public long skip(long len) throws IOException {
        if (this.closed) {
            throw new IOException("Attempting to read from closed stream.");
        }
        if (this.lastPacketOfFileRecieved) {
            throw new IOException("End of stream is reached.");
        }
        if (position == recievedData.length) {
            getMore();
        }

        long remainingLength = len;

        while (recievedData.length - position < remainingLength) {
            remainingLength -= available();
            getMore();
        }

        position += (int) remainingLength;
        return len;
    }//end method

    private void getMore() throws IOException {

        position = 0;
        if (recievedData.length != INITIAL_PACKET_BUFFER_SIZE) {
            recievedData = new byte[INITIAL_PACKET_BUFFER_SIZE];
        }

        while (true) {

            byte[] packet = queue.peek();//get but not remove just yet
            if (packet != null) {
                if (packet.length <= recievedData.length - position) {
                    queue.poll();//remove
                    System.arraycopy(packet, 0, recievedData, position, packet.length);
                    currentQueueLength.addAndGet(packet.length * -1);

                    position += packet.length;
                    if (position == recievedData.length) {
                        position = 0;
                        break;
                    }
                } else {
                    byte[] temp = recievedData;
                    recievedData = new byte[position];
                    System.arraycopy(temp, 0, recievedData, 0, recievedData.length);
                    position = 0;
                    break;
                }
            } else if (lastPacketOfFileRecieved) {
                if (position != 0) {

                    byte[] temp = recievedData;
                    recievedData = new byte[position];
                    System.arraycopy(temp, 0, recievedData, 0, recievedData.length);
                    position = 0;
                    break;
                } else {
                    position = recievedData.length;
                    System.err.println("Amount of recieved data is " + recievedB);
                    break;
                }
            }
        }//end while
    }//end method

    public void recieve(PayloadMessage msg) {

        if (nextPacketExpected == 1) {
            recievedPacketsChecker = new boolean[msg.getLastPacketNumber() + 1];
            recievedPackets = new HashMap<String, byte[]>();
        }

        recievedPacketsChecker[msg.getPacketSequenceNumber()] = true;
        recievedPackets.put(Integer.toString(msg.getPacketSequenceNumber()), msg.getPayload());

        byte[] payLoadnn = msg.getPayload();//for test
        recievedB += payLoadnn.length;//for test

        while (recievedPacketsChecker[nextPacketExpected]) {
            byte[] payLoad = recievedPackets.get(Integer.toString(nextPacketExpected));
            if (payLoad.length != 0) {
                if (currentQueueLength.get() < MAXIMUM_QUEUE_LENGTH) {
                    queue.add(payLoad);
                    currentQueueLength.addAndGet(payLoad.length);
                    recievedPackets.remove(Integer.toString(nextPacketExpected));
                    nextPacketExpected++;
                    if(nextPacketExpected == recievedPacketsChecker.length){
                        break;
                    }
                }else{
                    break;
                }
            }
        }


        if ((nextPacketExpected) == msg.getLastPacketNumber() + 1) {
            this.lastPacketOfFileRecieved = true;
            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
//            nextPacketExpected = 1;
        }

    }//end method

    @Override
    public int available() throws IOException {
        if (this.closed) {
            throw new IOException("Attempting to read from closed stream.");
        }
        return currentQueueLength.get();
    }//end method

    @Override
    public void mark(int readlimit) {
    }

    @Override
    public void reset() throws IOException {
        throw new IOException("Marks is not supported by this InputStream.");
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void close() throws IOException {
        //need to remove listener from StreamIOextension
        this.closed = true;

    }//end method
}//end class
