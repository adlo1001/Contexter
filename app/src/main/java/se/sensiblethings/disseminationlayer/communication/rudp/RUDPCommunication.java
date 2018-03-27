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

package se.sensiblethings.disseminationlayer.communication.rudp;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

import se.sensiblethings.disseminationlayer.communication.BasicSensibleThingsNode;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.DestinationNotReachableException;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.disseminationlayer.communication.MessageSerializer;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.RUDPSocket;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagram;
import se.sensiblethings.disseminationlayer.communication.rudp.socket.exceptions.RUDPDestinationNotReachableException;
import se.sensiblethings.disseminationlayer.communication.serializer.ObjectSerializer;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class RUDPCommunication extends Communication implements Runnable {
	
	private int communicationPort = 0;
	public static int initCommunicationPort = 0;
    public static String ip_ = "192.168.0.101";
	// public static String _ip = "127.0.0.1";
 	

//	private int communicationPort = defaultCommunicationPort;
	private SensibleThingsNode localSensibleThingsNode = null;

	/*
	@Override
	public int getCommunicationPort() {
		return communicationPort;
	}

	@Override
	public void setCommunicationPort(int communicationPort) {
		this.communicationPort = communicationPort;
	}
	*/

	private MessageSerializer messageSerializer = new ObjectSerializer();
	//private MessageSerializer messageSerializer = new EnterSeparatedMessageSerializer();
	
	
	private RUDPSocket socket;
	
	private boolean runCommunication = true;
	private Thread communicationThread;
	
	
	public RUDPCommunication(){		
		//this(0);
		this(initCommunicationPort);

	}

	public RUDPCommunication(int localPort) {					
			//this.socket = new RUDPSocket(communicationPort, InetAddress.getByName(getLocalIp()));
			communicationPort = localPort;
			try {
				this.socket = new RUDPSocket(new InetSocketAddress(communicationPort));
				communicationPort = socket.getLocalPort();
				createLocalNode();	
			} catch (SocketException e) {
				System.err.println("rUDP Socket not created, printing error for debugging:");
				e.printStackTrace();
			}

	}
	private void createListener() {
		//Start the Listener!
		communicationThread = new Thread(this);
		communicationThread.setName("RUDP_Communication");
		communicationThread.start();


	}
	@Override
	public void shutdown() {
		runCommunication = false;
		socket.shutdown();
		communicationThread.interrupt();
		setState(COMMUNICATION_STATE_DISCONNECTED);
	}
	


	long startTime = System.currentTimeMillis();
	int numMsg= 0; 
	
	@Override
	public void sendMessage(Message message) throws DestinationNotReachableException {
		
		//Measure the amount of messages sent per minute
		System.out.println("Msg SENT!");		
		numMsg++;
		if(System.currentTimeMillis() - startTime > 60 * 1000){			
			System.out.println("Msg/min = " + numMsg);
			numMsg = 0;
			startTime = System.currentTimeMillis();			
		}

		String to = message.getToNode().getAddress().split(":")[0];		
		int port = communicationPort;
		try {
			URI uri = new URI("ip://" + message.getToNode().getAddress());
			to = uri.getHost();
			if(uri.getPort() != -1)
				port = uri.getPort();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			throw new DestinationNotReachableException(e1.getMessage());
		}


		InetSocketAddress address = new InetSocketAddress(to , port);
		RUDPDatagram dgram;
		
		//Prepare datagram
		byte[] data = messageSerializer.serializeMessage(message);
		dgram  = new RUDPDatagram(address,data);
		//NotifyMessage nt_msg = (NotifyMessage)message;
		//String s = new String(nt_msg.value);
		System.out.println("To " + to + "  XXXX " + port);
		//System.out.println("Data " + s );
		//Send
		try {
			socket.send(dgram);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		RUDPDatagram dgram;
		if(runCommunication){//Debug: thread gets started during disconnection
			setState(COMMUNICATION_STATE_CONNECTED);
		}
		while (runCommunication) {
            try {
            	//Receive and handle data
            	dgram = socket.receive();
            	handleData(dgram);
            } catch (RUDPDestinationNotReachableException e) {
            	//System.out.println(e.getMessage());
            	
            	//Rehabilitate socket and try again
            	socket.rehabilitateLink(e.getInetSocketAddress());
            } catch (Exception e) {
            	setState(COMMUNICATION_STATE_DISCONNECTED);
            	try {
					Thread.sleep(250);
				} catch (InterruptedException e1) {
					if(!runCommunication){
						return;
					}
					e1.printStackTrace();
				}
            	createLocalNode();
            	return;
            }
        }				
	}
	
	protected void handleData(RUDPDatagram dgram) {
		try {
			Message message = messageSerializer.deserializeMessage(dgram.getData());
			//Send the message to the "PostOffice"
			if(message != null) {
				dispatchMessageToPostOffice(message);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//BIG workaround because Linux is stupid...
	
	private void createLocalNode() {
		Runnable r = new Runnable() {				
			public void run() {
				try {				
					//Workaround because Linux is stupid...	
			        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
			            NetworkInterface intf = en.nextElement();				           
			            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
			                InetAddress inetAddress = enumIpAddr.nextElement();
			                if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
			                	if(!(inetAddress instanceof Inet6Address)){ //Remove this line for IPV6 compatability
				                	localSensibleThingsNode = new BasicSensibleThingsNode(inetAddress.getHostAddress(), communicationPort);
				                	//localSensibleThingsNode =  new BasicSensibleThingsNode(ip_, communicationPort);
			                	}
			                }
			            }
			        }
				}
		        catch (Exception e) {
		        	localSensibleThingsNode =  new BasicSensibleThingsNode("127.0.0.1", communicationPort);
				}
				//Start the Listener!
				createListener();
			}
		};
		Thread t = new Thread(r);
        t.start();
	}
	
	@Override
	public SensibleThingsNode getLocalSensibleThingsNode() {
		return localSensibleThingsNode;
	}

	@Override
	public SensibleThingsNode createSensibleThingsNode(String ip,
			int port) {
		return new BasicSensibleThingsNode(ip, port);
	}
}
