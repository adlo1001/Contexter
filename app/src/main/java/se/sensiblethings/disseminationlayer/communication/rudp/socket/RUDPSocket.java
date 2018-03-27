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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;

import se.sensiblethings.disseminationlayer.communication.rudp.socket.datagram.RUDPAbstractDatagram;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagram;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagramPacket;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagramPacketOut;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.datagram.RUDPExceptionDatagram;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.exceptions.RUDPDestinationNotReachableException;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.listener.RUDPLinkTimeoutListener;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.listener.RUDPReceiveListener;

public class RUDPSocket extends Thread implements RUDPSocketInterface,RUDPLinkTimeoutListener,RUDPReceiveListener {
	DatagramSocket sock;
	private DatagramPacket recv_buffer;
	
	private LinkedBlockingQueue<RUDPAbstractDatagram> recv_queue;
	private HashMap<InetSocketAddress,RUDPLink> links;
	private Timer timer;
	
	private boolean runSocket = true;
	
	public RUDPSocket(DatagramSocket sock) {
		//Set socket
		this.sock = sock;
		//System.out.println(this.sock.getLocalAddress().toString());
		
		//Receive buffer, timer and link map
		recv_buffer = new DatagramPacket(new byte[RUDPDatagramPacket.MAX_PACKET_SIZE],RUDPDatagramPacket.MAX_PACKET_SIZE);
		recv_queue = new LinkedBlockingQueue<RUDPAbstractDatagram>();
		timer = new Timer("RUDP timer");
		links = new HashMap<InetSocketAddress,RUDPLink>();
		
		//Start receive thread
		this.start();
	}
	
	public RUDPSocket() throws SocketException {
		this(new DatagramSocket());
	}

	public RUDPSocket(int port)  throws SocketException {
		this(new DatagramSocket(port));
	}
	
	public RUDPSocket(SocketAddress bindaddr) throws SocketException {
		this(new DatagramSocket(bindaddr));
	}

	public RUDPSocket(int port,InetAddress inetaddr) throws SocketException {
		this(new DatagramSocket(port,inetaddr));
	}
	
	@Override
	public void run() {
		RUDPLink link;
		InetSocketAddress sa;
		
		//Set buffer as big as WINDOW_SIZE * MAX_PACKET_SIZE;
		try {
			//int size = sock.getReceiveBufferSize();
			sock.setReceiveBufferSize(RUDPLink.WINDOW_SIZE * RUDPDatagramPacket.MAX_PACKET_SIZE);
		}
		catch (SocketException e) {
			System.out.println(e.getMessage());
		}
		
		//Optimization of bufferusages by Victor
		byte[] buffer =new byte[RUDPDatagramPacket.MAX_PACKET_SIZE];
		recv_buffer = new DatagramPacket(buffer,buffer.length);
		//Receive thread
		while(runSocket) {
			try {
				//Receive datagram
				
				sock.receive(recv_buffer);
				sa = new InetSocketAddress(recv_buffer.getAddress(),recv_buffer.getPort());

				//Debug output...
				//System.out.println("RECV: " + sa.toString());				
				//String s = new String(recv_buffer.getData());
				//System.out.println("Recv Raw Data: " + s.trim());
				
				//Find or create link
				synchronized(links) {
					link = links.get(sa);
					if(link == null) {
						link = new RUDPLink(sa,this,this,this,timer);
						links.put(sa,link);
					}
				}
								
				//Forward
				//link.putReceivedData(packetBuffer);
				byte[] data = Arrays.copyOfRange(recv_buffer.getData(), recv_buffer.getOffset(), recv_buffer.getLength());
				link.putReceivedData(data);

			}
			catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}

	public void send(RUDPDatagram datagram) throws RUDPDestinationNotReachableException,InterruptedException {
		//If the socket is available...
		if(runSocket){			
			RUDPLink link;
			InetSocketAddress sa;
			
			//Get or create link
			sa = datagram.getSocketAddress();

			synchronized(links) {
				link = links.get(sa);
				
				if(link == null) {
					link = new RUDPLink(sa,this,this,this,timer);
					links.put(sa, link);
				}
			}				
			//Process send request in link
			link.sendDatagram(datagram);	
		}
	}

	public void onLinkTimeout(RUDPLink link) {
		//Link timed out => remove it from list
		synchronized(links) {
			links.remove(link.getSocketAddress());
		}
	}

	public void onRUDPDatagramReceive(RUDPAbstractDatagram datagram) {
		//Enqueue datagram for delivering
		try {
			recv_queue.put(datagram);
		}
		catch (InterruptedException e) {
			//Do nothing here
		}
	}

	public RUDPDatagram receive() throws InterruptedException,RUDPDestinationNotReachableException {
		RUDPAbstractDatagram abstractDgram;
		RUDPExceptionDatagram exceptionDgram;
		RUDPDatagram dgram;
		RUDPLink link;
		
		//Take the next datagram
		abstractDgram = recv_queue.take();
		
		//Cast
		if(abstractDgram.getClass().equals(RUDPDatagram.class)) {
			dgram = (RUDPDatagram)abstractDgram;
			
			//Inform link about datagram consumption
			synchronized(links) {
				link = links.get(dgram.getSocketAddress());
				if(link != null) {
					link.datagramConsumed();
				}
			}
			
			//Return data
			return dgram;
		}
		else {
			//Link failed
			exceptionDgram = (RUDPExceptionDatagram)abstractDgram;
			throw exceptionDgram.getException();
		}
	}

	public void sendDatagramPacket(RUDPDatagramPacketOut packet,InetSocketAddress sa) {
		DatagramPacket dgram;
		byte[] data;
		
		//Serialize 
		data = packet.serializePacket();
		
		try {
			//debug output
			//System.out.println("SEND\n" + packet.toString(sa.getPort()) + "\n");

			//Create UDP datagram and send it
			dgram = new DatagramPacket(data,data.length,sa);
			
			//System.out.println("SEND " + dgram.getSocketAddress().toString());
			sock.send(dgram);
		}
		catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public void shutdown() {
		synchronized(links) {
			for(RUDPLink link: links.values()) {
				link.shutdown();
			}
			
			links.clear();
		}
		runSocket = false;
		sock.close();
		timer.cancel();
		
		if(recv_queue != null){
			recv_queue.clear();
		}
		recv_queue = null;
	}	
	
	public void rehabilitateLink(InetSocketAddress sockAddr) {
		RUDPLink link;
		
		synchronized(links) {
			link = links.get(sockAddr);
			if(link != null) {
				link.rehabilitate();
			}
		}
	}

	public int getLocalPort() {
			return sock.getLocalPort();
	}
}
