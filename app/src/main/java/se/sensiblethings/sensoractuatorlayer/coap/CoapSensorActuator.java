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

package se.sensiblethings.sensoractuatorlayer.coap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import se.sensiblethings.sensoractuatorlayer.SensorActuator;


public class CoapSensorActuator extends SensorActuator {
    
        private String uri=null;
        private String ip=null;

	public CoapSensorActuator(String ip, String uci) {
		super(ip.substring(0, ip.lastIndexOf("/")), uci);
                this.ip=ip.substring(0, ip.lastIndexOf("/"));
                uri=ip.substring(ip.lastIndexOf("/")+1);
	}

	@Override
	public synchronized String getValue() {
            
            InetAddress addr = null;
            int port = -1;
  
             try {
                addr = InetAddress.getByName(ip);
                port = 61616; //default port used by CoapBlib on Telos b motes
            } catch (UnknownHostException e) {
              System.out
                  .println("Problems in determining the InetAddress...");
              e.printStackTrace();
              System.exit(2);
            }

            DatagramSocket socket = null;
            DatagramPacket inPacket = null;
            DatagramPacket outPacket = null;
            byte[] outData = new byte[256];
            byte[] inData = new byte[1024];
            //byte[] data = null;
            String result=null;
            

            try {
              socket = new DatagramSocket();
              socket.setSoTimeout( 4000 ); 
              outPacket = new DatagramPacket(outData, outData.length, addr, port);
              inPacket = new DatagramPacket(inData, inData.length);
              System.out.println("GET: Socket created");

            } catch (SocketException e) {
              System.out.println("Problems in creating socket: ");
              e.printStackTrace();
              System.exit(1);
            }
            
            try{
                byte [] coapMsg = createCoapGetMessage();
                  
                //Send COAP GET message to ip and uci
                outPacket.setData(coapMsg);
                System.out.println("Message created: "+binaryToHex(coapMsg,coapMsg.length));
                socket.send(outPacket);
                System.out.println("GET: Packet sent to: " + addr + ", " + port);
                
            } catch (IOException e) {
                System.out.println("Problems in sending the UDP packet: ");
                e.printStackTrace();
              }
            
            
            try {
                // setting the receiver buffer
                inPacket.setData(inData);
                socket.receive(inPacket);
                System.out.println("GET: Packet received");
              } catch (IOException e) {
                System.out.println("Problems in receiving the UDP packet: "+e.toString());
                socket.close();
                
                return result;
                
              }
            
            //Parse incoming message with the value		
            byte [] recData = inPacket.getData(); //received data
            System.out.println("Packet received: ");
            System.out.println(binaryToHex(recData,recData.length));
            result=readResult(recData);
            	
	    //Return the value
            socket.close();
 
            return result;
	}

	@Override
	public synchronized void setValue(String value) {
            
            InetAddress addr = null;
            int port = -1;
            
             try {
                addr = InetAddress.getByName(ip);
                port = 61616;
            } catch (UnknownHostException e) {
              System.out
                  .println("Problems in determining InetAddress...");
              e.printStackTrace();
              System.exit(2);
            }
		
            DatagramSocket socket = null;
            DatagramPacket inPacket = null;
            DatagramPacket outPacket = null;
            byte[] outData = new byte[256];
            byte[] inData = new byte[1024];
            //byte[] data = null;

            try {
              socket = new DatagramSocket();
              socket.setSoTimeout( 4000 ); 
              outPacket = new DatagramPacket(outData, outData.length, addr, port);
              inPacket = new DatagramPacket(inData, inData.length);
              System.out.println("PUT: Socket created");

            } catch (SocketException e) {
              System.out.println("Problems in creating socket: ");
              e.printStackTrace();
              System.exit(1);
            }
            
            byte [] coapMsg = createCoapPutMessage(value);
            
            try
            {
                
                outPacket.setData(coapMsg);
                System.out.println("Message created: "+binaryToHex(coapMsg,coapMsg.length));
                socket.send(outPacket);
                System.out.println("PUT: Packet sent to: " + addr + ", " + port);
                
            } catch (IOException e) {
                System.out.println("Problems in sending the UDP packet: ");
                e.printStackTrace();
              }
            
            try {
                // setting the receiver buffer
                inPacket.setData(inData);
                socket.receive(inPacket);
                System.out.println("PUT: Packet received");
              } catch (IOException e) {
                System.out.println("Problems in receiving the UDP packet: "+e.toString());
                                
                socket.close();
                
              }
            
            byte [] recData = inPacket.getData(); //received data
            System.out.println("PUT: Message received: "+binaryToHex(recData,recData.length));
            if(recData[1]==(byte) 0x50)
                System.out.println("PUT request correctly sent!");
            else
                System.out.println("Error in the transmission of the PUT request");
            
            
            socket.close();

	}
        
        
        private byte[] createCoapGetMessage()
        {
            byte [] coapMsg = new byte[5+uri.length()]; //the minimum coap get packet is 5 bytes
            Random rand = new Random();
            coapMsg[0]=(byte) 0x41; //Protocol version=1; Transaction Type=0 (Confirmable message); Number of options=1
            coapMsg[1]=(byte) 0x01; //Method code=1 (GET)
            //setting the 2 bytes random Transaction ID
            coapMsg[2]=(byte)(rand.nextInt(0xff) + 0x00);
            coapMsg[3]=(byte)(rand.nextInt(0xff) + 0x00);
            
            int option=0x90+uri.length(); //option field: 9(URI) + URI bytes length
            coapMsg[4]=(byte)option;
            
            for(int i=0,c=5;i<uri.length();i++,c++)
            {
               int val=uri.charAt(i);
               coapMsg[c]=(byte) val; //it gives the hex value of that char
            }
            
            return coapMsg;
        }
        
        private byte[] createCoapPutMessage(String data)
        {
            byte[] binary;
            if (uci.contains("tinyos"))//if the UCI is related to a Telos b sensor 
                binary=fromHexString(data); //it converts a hex string to a byte array
            else
                binary=data.getBytes(); //otherwise it converts a normal string to a byte array
            
            byte[] coapMsg=new byte[10+uri.length()+binary.length]; 
            System.out.println("Coap message length: "+coapMsg.length+" "+data);
            Random rand = new Random();
            int c=7;//index of the first sensor URI byte within the CoapMsg
            
            coapMsg[0]=(byte) 0x43; //Protocol version=1; Transaction Type=0 (Confirmable message); Number of options=3
            coapMsg[1]=(byte) 0x03; //Method code=3 (POST)
            coapMsg[2]=(byte)(rand.nextInt(0xff) + 0x00);
            coapMsg[3]=(byte)(rand.nextInt(0xff) + 0x00);
            coapMsg[4]=(byte) 0x11; //First option header: Type=1 (Content type), length: 1B
            coapMsg[5]=(byte) 0x2A; //ASCII encoding of '*' (plain/text)
            coapMsg[6]=(byte) (0x80+uri.length());//Second option header: Type=9 (URI), length: uri lenght
            for(int i=0;i<uri.length();i++,c++) //setting the sensor URI
            {
               int val=uri.charAt(i);
               coapMsg[c]=(byte) val;
            }
            
            coapMsg[c]=(byte) 0x22;//Third option header: Type=11 (Token), length: 2B
            coapMsg[++c]=(byte) 0x33; //ASCII encoding of '3'
            coapMsg[++c]=(byte) 0x61; //ASCII encoding of 'a'
            
            for(int i=0;i<binary.length;i++) //setting the payload data
                coapMsg[++c]=binary[i];
            
            return coapMsg;      
                
        }
                
        
        private String readResult(byte[] recData)
        {
            String result=null;
            //byte [] val=null;
            
            if(recData[1]==(byte) 0x50) //Response code= OK
            {
                byte [] temp = null;
                //since a Coap message doesn't contain a symbol for identifying the end of message, a message is consiered finished when a sequence of 5 '00' bytes is found 
                for(int i=4;i<recData.length;i++)
                {
                    if(recData[i+1]==(byte) 0x00 && recData[i+2]==(byte) 0x00 && recData[i+3]==(byte) 0x00 && recData[i+4]==(byte) 0x00 && recData[i+5]==(byte) 0x00)
                    {
                        temp=new byte[i-3];
                        break;
                    }
                }
                

                for(int i=0,c=4;i<temp.length;i++,c++) //extract the payload data
                    temp[i]=recData[c];
                    
                if(uci.contains("tinyos")) //if the response comes from a Telos b, data must be
                                           //converted to hex format
                    result=binaryToHex(temp,temp.length);
                else
                    result=new String(temp); 
                
                System.out.println("CoapSensorActuator: result: "+result);
               
            }
            else
            {
                System.out.println("Error code in the response!");
                result="Error code in the response!";
            }
            
            return result;
        }
        
        private byte[] fromHexString(String encoded) {
            if ((encoded.length() % 2) != 0)
                encoded="0"+encoded;

            final byte result[] = new byte[encoded.length()/2];
            final char enc[] = encoded.toCharArray();
            for (int i = 0; i < enc.length; i += 2) {
                StringBuilder curr = new StringBuilder(2);
                curr.append(enc[i]).append(enc[i + 1]);
                result[i/2] = (byte) Integer.parseInt(curr.toString(), 16);
            }
            return result;
        }
        
        private String binaryToHex(byte [] data, int length){
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<length;i++) {
                 sb.append(String.format("%02x", data[i]));
            }
            return sb.toString();   

        }

}
