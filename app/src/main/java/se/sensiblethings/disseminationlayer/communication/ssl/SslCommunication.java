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
package se.sensiblethings.disseminationlayer.communication.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import se.sensiblethings.disseminationlayer.communication.BasicSensibleThingsNode;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.DestinationNotReachableException;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.disseminationlayer.communication.MessageSerializer;
import se.sensiblethings.disseminationlayer.communication.serializer.ObjectSerializer;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class SslCommunication extends Communication implements Runnable {

    private MessageSerializer messageSerializer = new ObjectSerializer();
    private SSLServerSocket ss;
    private int communicationPort = 0;
    public static int initCommunicationPort = 0;
    private ExecutorService executorPool;
    private SensibleThingsNode localSensibleThingsNode = null;
    private SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    private final String[] enabledCipherSuites = {"SSL_DH_anon_WITH_RC4_128_MD5"};
    private boolean runCommunication = true;

    public SslCommunication() {
        try {
        	communicationPort = initCommunicationPort;
            createLocalNode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	private void createListener() {
		try {
        	//SSL specifics
            executorPool = Executors.newCachedThreadPool();
            SSLServerSocketFactory ssocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            this.ss = (SSLServerSocket) ssocketFactory.createServerSocket(initCommunicationPort);
            communicationPort = ss.getLocalPort();
            ss.setEnabledCipherSuites(enabledCipherSuites);
            
			Thread t = new Thread(this);
			t.setName("SSLCommunication");
			t.start();
		} catch (IOException e) {
			System.err.println("SSL Socket not created, printing error for debugging:");
			e.printStackTrace();
		}

	}
    @Override
    public void shutdown() {
        try {
            runCommunication = false;
            ss.close();
            executorPool.shutdownNow();
            setState(COMMUNICATION_STATE_DISCONNECTED);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(Message message) throws DestinationNotReachableException {
        SSLSocket s = null;
        try {

            String toHost = message.getToNode().toString();
            String[] split = toHost.split(":");
            String toIp = split[0];
            int toPort = Integer.parseInt(split[1]);

            //System.out.println("ToHost: " + toHost);
            //Socket s = new Socket(toIp, toPort);


            s = (SSLSocket) socketFactory.createSocket(toIp, toPort);
            s.setEnabledCipherSuites(enabledCipherSuites);

            byte[] data = messageSerializer.serializeMessage(message);

            OutputStream os = s.getOutputStream();
            os.write(data);

            os.flush();
            
        }catch(ConnectException e){
        	throw new DestinationNotReachableException("to:" +  message.getToNode() + " from:" + message.getFromNode() + " exception: " + e.getMessage());
        	
        } catch (IOException e) {
            //e.printStackTrace();
            throw new DestinationNotReachableException("to:" +  message.getToNode() + " from:" + message.getFromNode() + " exception: " + e.getMessage());
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }
    }

    public void run() {
    	setState(COMMUNICATION_STATE_CONNECTED);
        while (runCommunication) {
            try {
                Socket s = ss.accept();

                executorPool.execute(new ConnectionHandler(s));


            } catch (IOException e) {
            	setState(COMMUNICATION_STATE_DISCONNECTED);
            	try {
					Thread.sleep(250);
				} catch (InterruptedException e1) {
					//e1.printStackTrace();
				}
            	createLocalNode();
            	return;
            }
        }
    }
    
    //byte[] buffer = new byte[1048576];
    protected synchronized void handleConnection(Socket s) {
    	try {
			byte[] buffer = new byte[2048];
			int numberOfReadBytes = 0;
			int position = 0;			
			InputStream is = s.getInputStream();			
			do {
				numberOfReadBytes = is.read(buffer, position, buffer.length-position);				
				position = position + numberOfReadBytes;
				if(buffer.length-position <=0){
					buffer = Arrays.copyOf(buffer, buffer.length*2);
					
				}
			} while (numberOfReadBytes != -1);
			is.close();
			s.close();
			
			byte[] messageBuffer = new byte[position];
			for(int i = 0; i != messageBuffer.length; i++){
				messageBuffer[i] = buffer[i];	
			}
			
			Message message = messageSerializer.deserializeMessage(messageBuffer);

			//Send the message to the "PostOffice"
			dispatchMessageToPostOffice(message);

		} catch (Exception e) {
			e.printStackTrace();
		}
    	/*
    	try {
            InputStream is = s.getInputStream();
            is.read(buffer);

            //String stringRepresentation = new String(buffer);

            Message message = messageSerializer.deserializeMessage(buffer);

            //Send the message to the "PostOffice"
            dispatchMessageToPostOffice(message);

            is.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
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

    private class ConnectionHandler implements Runnable {

        private Socket s;

        public ConnectionHandler(Socket s) {
            this.s = s;
        }

        public void run() {
            handleConnection(s);
        }
    }

}
