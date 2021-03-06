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

package se.sensiblethings.disseminationlayer.communication.rudp.socket;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import se.sensiblethings.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagram;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagramBuilder;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagramPacket;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagramPacketIn;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagramPacketOut;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.listener.RUDPReceiveListener;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.rangelist.DeltaRangeList;


public class RUDPReceiver {
	private static final int MAX_ACK_DELAY = 100;

	//Link; Socket interface to send raw UDP datagrams
	private RUDPDatagramPacketSenderInterface sendInterface;
	private RUDPReceiveListener receiveListener;
	private InetSocketAddress remoteAddress;
	
	//Is this receiver shut down??
	private boolean isShutdown;
	
	//Timer
	private Timer timer;
	
	//Receiver list
	private int receiverWindowStart;
	private int receiverWindowEnd;
	private int lastSentReceiverWindowStart;
	private int deliverWindowPos;
	private boolean receiverWindowSynced;
	
	private HashMap<Integer,RUDPDatagramBuilder> packetBuffer_in;
	
	//Acknowledge stuff
	private DeltaRangeList ackRange;
	private TimerTask task_ack;

	public RUDPReceiver(RUDPDatagramPacketSenderInterface sendInterface,RUDPReceiveListener receiveListener,InetSocketAddress remoteAddress,Timer timer) {
		this.sendInterface = sendInterface;
		this.timer = timer;
		this.receiveListener = receiveListener;
		this.remoteAddress = remoteAddress;
		
		packetBuffer_in = new HashMap<Integer,RUDPDatagramBuilder>();
		ackRange = new DeltaRangeList();
	}

	public void handlePayloadData(RUDPDatagramPacketIn packet) {
		RUDPDatagramBuilder dgram;
		int newRangeElement;
		boolean sendBoostACK = false;
		List<RUDPDatagram> readyDatagrams = null;
		
		//Process data packet
		synchronized(this) {
			if(!isShutdown) {
				if(packet.getFlag(RUDPDatagramPacket.FLAG_DATA)) {
					//Check if packet is within window bounds
					if((packet.getPacketSeq() - receiverWindowStart) < 0 || (packet.getPacketSeq() - receiverWindowStart) > RUDPLink.WINDOW_SIZE) {
						//send up to date ACK packet ?! 
						//System.out.println("INVALID PACKET RECEIVED - PACKET SEQ OUT OF WINDOW BOUNDS " + packet.getId() + " - " + packet.getPacketSeq());
					}
					else {
						//Insert into receiving packet buffer
						if(packet.getFragmentCount() > 1) {
							if((dgram = packetBuffer_in.get(packet.getPacketSeq() - packet.getFragmentNr())) == null) {
								//Create new fragmented datagram
								dgram = new RUDPDatagramBuilder(remoteAddress,packet.getFragmentCount());
								dgram.assimilateFragment(packet);
	
								packetBuffer_in.put(packet.getPacketSeq() - packet.getFragmentNr(),dgram);
							}
							else {
								//We are the Borg, and we will...
								dgram.assimilateFragment(packet);
							}
						}
						else {
							//Create new non-fragmented datagram
							dgram = new RUDPDatagramBuilder(remoteAddress,packet);
							packetBuffer_in.put(packet.getPacketSeq() - packet.getFragmentNr(),dgram);
						}
						
						//Calculate relative position and add to packet range list
						newRangeElement = packet.getPacketSeq() - receiverWindowStart;
						ackRange.add((short)newRangeElement);
						
						//Shift end of window
						if(packet.getPacketSeq() - receiverWindowStart <= receiverWindowEnd - receiverWindowStart) {
							receiverWindowEnd = packet.getPacketSeq() + 1;
						}
	
				//-----
						
						//Add ready datagrams to deploy
						readyDatagrams = new ArrayList<RUDPDatagram>();
						
						while(true) {
							//Get packet
							dgram = packetBuffer_in.get(deliverWindowPos);
							
							if(dgram != null && dgram.isComplete()) {
								readyDatagrams.add(dgram.toRUDPDatagram());
								deliverWindowPos += dgram.getFragmentCount(); 
							}
							else {
								break;
							}
						}
						
						//Start ACK-timer, if necessary...
						//...or send immediately if it is the very first packet
						//to speed up the window-size negotiation process
						if(packet.getFlag(RUDPDatagramPacket.FLAG_FIRST) || packet.getPacketSeq() - lastSentReceiverWindowStart == RUDPLink.WINDOW_SIZE_BOOST) {
							//Send an empty packet, that will get ACK data (automatically)
							if(task_ack != null) {
								task_ack.cancel();
								task_ack = null;
							}

							//Send boost ACK
							sendBoostACK = true;
						}
						else {
							//Wait some time for more packets, so we can combine several ACKs
							if(task_ack == null) {
								task_ack = new AcknowledgeTask(this);
								timer.schedule(task_ack, MAX_ACK_DELAY);
							}
						}
					}
				}
			}
		}
		
		//Send boost ACK
		if(sendBoostACK) {
			//Send new empty packet that will contain ACK data
			RUDPDatagramPacketOut boostACK = new RUDPDatagramPacketOut();
			sendInterface.sendDatagramPacket(boostACK);
		}
		
		//Forward all ready datagrams to upper layer
		if(readyDatagrams != null) {
			for(RUDPDatagram d: readyDatagrams) receiveListener.onRUDPDatagramReceive(d);
		}
	}

	public void datagramConsumed() {
		RUDPDatagramBuilder dgram;
		boolean windowReOpened = false;
		
		//A datagram has been consumed => shift receive window one step forward
		synchronized(this) {
			if(!isShutdown && receiverWindowSynced) {
				dgram = packetBuffer_in.get(receiverWindowStart);
				if(dgram != null) {
					//Has the window been reopened?
					if((receiverWindowEnd - receiverWindowStart) == RUDPLink.WINDOW_SIZE) windowReOpened = true;
					
					//if(dgram.isAckSent()) {
					packetBuffer_in.remove(receiverWindowStart);
	
					//Shift receive pointer
					receiverWindowStart += dgram.getFragmentCount();
		
					//Shift range and foreign window
					ackRange.shiftRanges((short)(-1 * dgram.getFragmentCount()));
				}
			}
		}

		//Inform the sender about the window reopening
		if(windowReOpened) {
			//Send new empty packet that will contain ACK data
			RUDPDatagramPacketOut packet = new RUDPDatagramPacketOut();
			sendInterface.sendDatagramPacket(packet);
		}
	}
	
	public void setAckStream(RUDPDatagramPacketOut packet) {
		List<Short> ackList;
		
		//Check if we can acknowledge something
		synchronized(this) {
			if(!isShutdown && receiverWindowSynced) {
				//Set the window start the sender could know about to the actual receiver window start
				lastSentReceiverWindowStart = receiverWindowStart;
	
				//Put ACK stream into packet
				ackList = ackRange.toDifferentialArray();
				packet.setACKData(receiverWindowStart,ackList);
	
				//debug output
				if(ackList.size() > RUDPDatagramPacket.RESERVED_ACK_COUNT) {
					System.out.println("RESERVED_ACK_COUNT OVERFLOW");
				}
				
				//there could be more ranges then we are able to send
				//in one packet. test if this is fine
				//Disable ACK timer
				if(task_ack != null) {
					task_ack.cancel();
					task_ack = null;
				}
			}
		}
	}

	private class AcknowledgeTask extends TimerTask {
		Object taskSync;
		
		public AcknowledgeTask(Object taskSync) {
			this.taskSync = taskSync;
		}
		
		@Override
		public void run() {
			//Send new empty packet that will contain ACK data
			RUDPDatagramPacketOut packet = new RUDPDatagramPacketOut();
			sendInterface.sendDatagramPacket(packet);
			
			//Set taskAck null
			synchronized(taskSync) {
				task_ack = null;
			}
		}
	}
	
	public void setReceiverWindowStart(int newReceiverWindowStart) {
		//Initialize receiver window
		if(!receiverWindowSynced) {
			receiverWindowStart = newReceiverWindowStart;
			receiverWindowEnd = newReceiverWindowStart;
			deliverWindowPos = newReceiverWindowStart;
			lastSentReceiverWindowStart = newReceiverWindowStart;
			receiverWindowSynced = true;
		}
		else {
			System.out.println("RECEIVER WINDOW ALREADY SET!");
		}
	}
	
	public void reset() {
		synchronized(this) {
			//Stop acknowledge task
			if(task_ack != null) {
				task_ack.cancel();
				task_ack = null;
			}
			
			//Reset window
			receiverWindowStart = 0;
			receiverWindowEnd = 0;
			lastSentReceiverWindowStart = 0;
			deliverWindowPos = 0;
			receiverWindowSynced = false;
			
			//Clear internal data structures to have a new start
			ackRange.clear();
			packetBuffer_in.clear();
		}
	}
	
	public void shutdown() {
		synchronized(this) {
			//Reset state
			reset();
			isShutdown = true;
		}
	}
	
	public void rehabilitate() {
		synchronized(this) {
			isShutdown = false;
		}
	}
}
