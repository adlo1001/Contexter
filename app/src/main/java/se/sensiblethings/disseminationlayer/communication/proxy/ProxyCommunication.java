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

package se.sensiblethings.disseminationlayer.communication.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import se.sensiblethings.disseminationlayer.communication.BasicSensibleThingsNode;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.DestinationNotReachableException;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.disseminationlayer.communication.MessageSerializer;
import se.sensiblethings.disseminationlayer.communication.serializer.ObjectSerializer;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public abstract class ProxyCommunication extends Communication implements
		Runnable {

	public static String proxyHost = "193.10.119.42";
//	public static String proxyHost = "192.168.0.103";

	protected abstract Socket createSocket() throws UnknownHostException,
			IOException;

	final class RestartConnectionOnExceptionHandler implements
			UncaughtExceptionHandler {

		public void uncaughtException(Thread thread, Throwable exception) {
			if (thread == ProxyCommunication.this.communicationThread) {
				if (ProxyCommunication.this.runCommunication) {
					System.out
							.println("ProxyCommunication caught an unhandled exception while running, reestablishing tunnel");
					ProxyCommunication.this.reestablishTunnel();
				}
			}
		}
	}

	private RestartConnectionOnExceptionHandler restartConnectionOnExceptionHandler = new RestartConnectionOnExceptionHandler();
	int communicationPort = 0;
	protected SensibleThingsNode localNode;
	private Socket socket;
	private MessageSerializer messageSerializer = new ObjectSerializer();
	protected boolean runCommunication = true;
	protected Thread communicationThread;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	protected long sessionID;
	private static final int MAXIMUM_CONNECTION_RETRIES = 10;
	private static final long TEMPORARY_BAN_DURATION = 1000*10;
	private int retries = 0;
	private LinkedList<ProxyPayload> backlog;
	private Map<String, Long> blocklist;

	public ProxyCommunication() {
		blocklist = new HashMap<String, Long>();
		sessionID = 0;

		try {
			communicationThread = new Thread(this);
			communicationThread.setName("Proxy_Communication");
			communicationThread
					.setUncaughtExceptionHandler(restartConnectionOnExceptionHandler);
			communicationThread.start();

		} catch (Exception e) {
			e.printStackTrace();
		}

		//long startTime = System.currentTimeMillis(); 
//		while (localNode == null) {
//			try {
//				Thread.sleep(25);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		//long endDime = System.currentTimeMillis();
		//System.out.println("asfdas" + (endDime - startTime));
		

		// System.out.println("NAT detected, proxy connected as: " + localNode);
	}

	@Override
	public void shutdown() {
		runCommunication = false;
		writePayloadToTunnel(new ProxyPayload(0, null, null, null));
		try {
			communicationThread.interrupt();
			socket.close();
			socket = null;
			communicationThread = null;
		} catch (Exception e) {
			// e.printStackTrace();
		}
		setState(COMMUNICATION_STATE_DISCONNECTED);
	}
	
	protected void reestablishTunnel() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		if (retries++ < MAXIMUM_CONNECTION_RETRIES && runCommunication == true) {
			if(communicationThread.equals(Thread.currentThread())){
				Thread oldThread = communicationThread;
				communicationThread = new Thread(this);
				communicationThread.setName("Proxy_Communication");
				communicationThread
						.setUncaughtExceptionHandler(restartConnectionOnExceptionHandler);
	
				communicationThread.start();
	
				if (oldThread != null)
					oldThread.interrupt();
			}
		}else if(getState().equals(COMMUNICATION_STATE_CONNECTED)){
			setState(COMMUNICATION_STATE_DISCONNECTED);
		}
	}

	@Override
	public void sendMessage(Message message)
			throws DestinationNotReachableException {
		if(isBlocked(message.getToNode())){
			throw new DestinationNotReachableException("Destination is temporary banned");
		}
		if(retries > MAXIMUM_CONNECTION_RETRIES && runCommunication == true){
			System.err.println("ProxyTunnelHost not reachable");
			setState(COMMUNICATION_STATE_DISCONNECTED);
			throw new DestinationNotReachableException("ProxyTunnelHost not reachable");
			
		}
		
		if(runCommunication == false){
			System.err.println("ProxyCommunication is shut down");
			throw new DestinationNotReachableException("ProxyCommunication is shut down");
		}
		try {
			// Send it over the tunnel
			byte[] data = messageSerializer.serializeMessage(message);
			ProxyPayload payload = new ProxyPayload(sessionID, data,
					message.getFromNode(), message.getToNode());
			if (backlog == null) {
				writePayloadToTunnel(payload);
			} else {
				// In case a multithreaded program sends messages while backlog
				// is processed
				// there could be a situation where backlog is finished but not
				// cleared
				// therefore we look for a NullPointerException
				try {
					addPayloadToBacklog(payload);
				} catch (NullPointerException e) {
					writePayloadToTunnel(payload);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		// To avoid network in communication thread on android, creation of
		// socket is located here
		//long startTime = System.currentTimeMillis(); 
		try {
			socket = createSocket();
			//long endDime = System.currentTimeMillis();
			//System.out.println("asfdas666   " + (endDime - startTime));

			//UTAN ZIP			
			OutputStream os = socket.getOutputStream();
			out = new ObjectOutputStream(os);
			InputStream is = socket.getInputStream();
			in = new ObjectInputStream(is);
			
			
			//Testar med GZIP
			//Verkar inte funka
			/*
			OutputStream os = socket.getOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(os);			
			out = new ObjectOutputStream(gos);			
			InputStream is = socket.getInputStream();
			GZIPInputStream gis = new GZIPInputStream(is);    
			in = new ObjectInputStream(gis);
			 */

		} catch (Exception e) {
			e.printStackTrace();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				//Thread is superseded by new thread, aborting reestablishment.
				return;
			}
			reestablishTunnel();
			return;
		}
		//long endDime = System.currentTimeMillis();
		//System.out.println("asfdas111   " + (endDime - startTime));
				
		// Send initial message to proxy extension
		ProxyPayload payload = new ProxyPayload(sessionID, null,
				getLocalSensibleThingsNode(), getLocalSensibleThingsNode());
		writePayloadToTunnel(payload);
		// Clear message backlog
		handleBacklog();
		retries = 0;
		
		//endDime = System.currentTimeMillis();
		//System.out.println("asfdas2222   " + (endDime - startTime));
		while (runCommunication && communicationThread.equals(Thread.currentThread())) {
			// Receive and handle data
			try {
				payload = (ProxyPayload) in.readUnshared();
				

		        try {
		        	ByteArrayOutputStream bos = new ByteArrayOutputStream();
		        	GZIPOutputStream gos = new GZIPOutputStream(bos);
		        	ObjectOutputStream oos = new ObjectOutputStream(gos);
		            oos.writeUnshared(payload);            
		            oos.flush();            
					oos.close();
					gos.close();
					bos.close();
//		            byteArray = bos.toByteArray();
//		            System.out.println("***************");        
//		            System.out.println("In Tunnel Len:" + byteArray.length);
//		            System.out.println("***************");        
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }

		        
				//endDime = System.currentTimeMillis();
				//System.out.println("asfdas333   " + (endDime - startTime));
				
				handlePayload(payload);
				//endDime = System.currentTimeMillis();
				//System.out.println("asfdas444   " + (endDime - startTime));
			} 
			catch (Exception e) {
				if (runCommunication) {
					System.out
							.println("Exception in proxy tunnel, printing stacktrace for debug purposes and restarting tunnel");
					e.printStackTrace();
					reestablishTunnel();
					return;
				}
			}
		}
	}

	private synchronized void writePayloadToTunnel(ProxyPayload payload) {
		try {

			//ProxyPayloadWrapper str = new ProxyPayloadWrapper(
			//		ProxyPayload.serializePayload(payload));
			

//	        byte[] byteArray = null;
	        try {
	        	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        	GZIPOutputStream gos = new GZIPOutputStream(bos);
	        	ObjectOutputStream oos = new ObjectOutputStream(gos);
	            oos.writeUnshared(payload);            
	            oos.flush();            
				oos.close();
				gos.close();
				bos.close();
//	            byteArray = bos.toByteArray();
//	            System.out.println("***************");        
//	            System.out.println("Out Tunnel Len:" + byteArray.length);
//	            System.out.println("***************");        
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }

			out.writeUnshared(payload);
			out.flush();
			out.reset();
		} catch (Exception e) {

			addPayloadToBacklog(payload);
			reestablishTunnel();
		}
	}

	private void addPayloadToBacklog(ProxyPayload payload) {
		if (backlog == null) {
			backlog = new LinkedList<ProxyPayload>();
		}
		if(runCommunication == true)
			backlog.add(payload);
	}

	private void handleBacklog() {
		if (backlog != null) {
			while (!backlog.isEmpty()) {
				writePayloadToTunnel(backlog.poll());
			}
			backlog = null;
		}
	}

	private void handlePayload(ProxyPayload p) {
		try {
			if (localNode == null) {
				localNode = p.getTo();
				sessionID = p.getSessionID();
				//Session is now open
				setState(COMMUNICATION_STATE_CONNECTED);
				return;
			}

			if (p.getMessage() == null) {
				// System.out.println("Payload is null!");
				if(p.getFrom() != null && p.getTo() != null && !p.getFrom().getAddress().contentEquals(p.getTo().getAddress())){
					//Block destination
					blockPeer(p.getTo());
				}
				else if (p.getFrom() != null && p.getTo() != null && 
						p.getFrom().getAddress().contentEquals(p.getTo().getAddress()) && 
						!p.getFrom().getAddress().contentEquals(this.getLocalSensibleThingsNode().getAddress())){
					//Hotswapping
					localNode = p.getTo();
					sessionID = p.getSessionID();
					System.out.println("EXPERIMANTAL FEATURE: ProxyTunnelHost have been rebooted in mid-operation, hotswapping new assigned session id and address. Restart SHOULD not be needed.");
					//Shutdown
//					System.err.print("ProxyHost have been rebooted, old data is:");
//					System.err.print(" Local Node:"+this.getLocalSensibleThingsNode());
//					System.err.println("sessionID: " + this.sessionID);
//					System.err.print("New data is:");
//					System.err.print(" From:"+p.getFrom());
//					System.err.print(" To: "+ p.getTo());
//					System.err.print("sessionID: " + p.getSessionID());
//					System.err.println(", shutting down communication");
//					this.shutdown();
					return;
				}
				else{
					writePayloadToTunnel(p);
				}
				return;
			}
			if (p.getSessionID() != sessionID) {
				System.out.println("ProxyTunnel SessionID missmatch during message delivery, ");
				System.out.println("you are running an outdated version of the proxy tunnel protocol.");
			}
			Message message = messageSerializer.deserializeMessage(p
					.getMessage());

			// Send the message to the "PostOffice"
			dispatchMessageToPostOffice(message);

		} catch (Exception e) {
			e.printStackTrace();
			reestablishTunnel();
		}
	}

	private void blockPeer(SensibleThingsNode p){
//		System.out.println("Banning:" + p.getAddress() + " for " + TEMPORARY_BAN_DURATION + "ms");
		blocklist.put(p.getAddress(), System.currentTimeMillis());
	}
	
	private boolean isBlocked(SensibleThingsNode p){
		String key = p.getAddress();
		long time = System.currentTimeMillis();
		if(blocklist.containsKey(key)){
			if(time - blocklist.get(key) < TEMPORARY_BAN_DURATION){
//				System.out.println(p.getAddress() + " is temporary banned, throwing exception");
				return true;
			}
			else{
//				System.out.println(p.getAddress() + " is is not banned any longer");
				blocklist.remove(key);
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public void setCommunicationPort(int communicationPort) {
		this.communicationPort = communicationPort;
	}

	public int getCommunicationPort() {
		return communicationPort;
	}

	
	@Override
	public SensibleThingsNode getLocalSensibleThingsNode() {
		return localNode;
	}

	@Override
	public SensibleThingsNode createSensibleThingsNode(String ip,
			int port) {
		return new BasicSensibleThingsNode(ip, port);
	}
	
	
}