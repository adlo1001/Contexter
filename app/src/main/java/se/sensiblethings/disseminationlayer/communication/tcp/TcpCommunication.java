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

package se.sensiblethings.disseminationlayer.communication.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

import se.sensiblethings.disseminationlayer.communication.BasicSensibleThingsNode;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.DestinationNotReachableException;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.disseminationlayer.communication.MessageSerializer;
import se.sensiblethings.disseminationlayer.communication.serializer.ObjectSerializer;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class TcpCommunication extends Communication implements Runnable {

	private MessageSerializer messageSerializer = new ObjectSerializer();
		
	private ServerSocket ss;
	private int communicationPort = 0;
	public static int initCommunicationPort = 0;
	
	private SensibleThingsNode localSensibleThingsNode = null;
	
	private boolean runCommunication = true;
	
	public TcpCommunication() {			
			communicationPort = initCommunicationPort;
			createLocalNode();	
	
	}

	private void createListener() {
		try {
			this.ss = new ServerSocket(communicationPort);
			communicationPort = ss.getLocalPort();
			Thread t = new Thread(this);
			t.setName("TcpCommunication");
			t.start();
		} catch (IOException e) {
			System.err.println("Socket not created, printing error for debugging:");
			e.printStackTrace();
		}

	}
	
	@Override
	public void shutdown() {
		try {
			runCommunication = false;
			ss.close();					
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
		setState(COMMUNICATION_STATE_DISCONNECTED);
	}
	

	@Override
	public void sendMessage(Message message) throws DestinationNotReachableException {
		try {
			
			String toHost = message.getToNode().toString();
			String[] split = toHost.split(":");
			String toIp = split[0];
			int toPort = Integer.parseInt(split[1]);
			
			//System.out.println("ToHost: " + toHost);
			Socket s = new Socket(toIp, toPort);
						
			byte[] data = messageSerializer.serializeMessage(message);
	
			OutputStream os = s.getOutputStream();
			os.write(data);

			os.flush();
			os.close();
			s.close();
			
		} catch (Exception e) {
			//e.printStackTrace();
			throw new DestinationNotReachableException(e.getMessage());
		}
	}

	public void run() {
    	setState(COMMUNICATION_STATE_CONNECTED);
		while (runCommunication) {
            try {
                 final Socket s = ss.accept();
                 Thread t = new Thread(new Runnable() {
					public void run() {
						handleConnection(s);								
					}
					
				});
				t.start();
				
            } 
            catch (IOException e) {
            	setState(COMMUNICATION_STATE_DISCONNECTED);
            	try {
					Thread.sleep(250);
				} catch (InterruptedException e1) {
					if(!runCommunication){
						return;
					}
				}
            	createLocalNode();
            	return;
            }
        }				
	}
	
	
	private void handleConnection(Socket s) {
		try {
			byte[] buffer = new byte[1048576];
			int numberOfReadBytes = 0;
			int position = 0;			
			InputStream is = s.getInputStream();			
			do {
				numberOfReadBytes = is.read(buffer, position, buffer.length-position);				
				position = position + numberOfReadBytes;				
			} while (numberOfReadBytes != -1);
			is.close();
			s.close();
			
			Message message = messageSerializer.deserializeMessage(buffer);

			//Send the message to the "PostOffice"
			dispatchMessageToPostOffice(message);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

		
	//BIG workaround because Linux is stupid...
	//private static InetAddress localAddress = null;
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
				                	//Start the Listener!
				        			
			                	}
			                }
			            }
			        }
				}
		        catch (Exception e) {
		        	localSensibleThingsNode =  new BasicSensibleThingsNode("127.0.0.1", communicationPort);
				}
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
