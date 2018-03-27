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
import java.net.SocketException;

import se.sensiblethings.sensoractuatorlayer.SensorGateway;
import se.sensiblethings.sensoractuatorlayer.SensorGatewayListener;

public class CoapSensorGateway extends SensorGateway implements Runnable{
    
    DatagramSocket socket = null;
    String response=null;
    DatagramPacket packet = null;
    byte[] outData = new byte[256];
    byte[] inData = null;
    int method = -1; //0:GET, 1:SET
    byte[] messageId = new byte[2];
    String uri = "";
    

    public CoapSensorGateway(SensorGatewayListener sensorGatewayListener) {
        super(sensorGatewayListener);
        
        Thread t = new Thread(this);
        t.start();
		
	}
        
        public void run() {
            	
            try
            {
                
                try {
                        socket = new DatagramSocket(61616);
                        socket.setSoTimeout( 0 ); 
                        System.out.println("Socket created");

                      } catch (SocketException e) {
                        System.out.println("Problems in creating socket: ");
                        e.printStackTrace();
                        System.exit(1);
                      }
                
                //Start listening to incoming COAP packets                
                while(true)
                {
                    
                    try {
                        // setting the receiver buffer
                        inData = new byte[1024];
                        packet = new DatagramPacket(inData, inData.length);
                        packet.setData(inData);
                        socket.receive(packet);
                      } catch (IOException e) {
                        System.out.println("Problems in receiving the UDP packet: ");
                        e.printStackTrace();
                      }
                    
                    byte [] recData = packet.getData(); //received data
                    boolean errFlag=false;
                    byte [] responseMsg=null;
                    System.out.println("================================================================================");
                    System.out.println("Packet received: ");
                    System.out.println(binaryToHex(recData,recData.length));
                    
                    if(recData[0] != (byte) 0x41 && recData[0] != (byte) 0x43)
                    {
                        System.out.println("Error in the packet header");
                        errFlag=true; 
                    }
                    
                    if((recData[1] != (byte) 0x01 && recData[1] != (byte) 0x03))
                    {
                        System.out.println("Invalid Method!");
                        errFlag=true;
                    }
                    else if(recData[1] == (byte) 0x01) //GET 
                        method = 0;
                    else if(recData[1] == (byte) 0x03) //SET
                        method = 1;
                    
                    messageId[0] = recData[2];
                    messageId[1] = recData[3];
                    
                    int length=packetLength(recData);
                        
                    if(method==0) //if it is a get
                    {
                        int optionLength=length-5; //length of the option payload
                        System.out.println("optionLength: "+optionLength);
                        if(recData[4]!=((byte)(0x90+optionLength))) //it must be 0x90+option length
                        {
                            System.out.println("Error in the option header");
                            errFlag=true;
                        }
                        
                        //extracting the sensor uri
                        for(int i=5;i<length;i++)
                            uri=uri+(char)recData[i];

                        String value = listener.getEvent(uri); 
                        System.out.println("Received value: "+value);

                        //Send response with value
     
                        if(!errFlag && value != null)
                        {
                            byte [] hex=value.getBytes();
                            responseMsg = new byte[4+hex.length];
                            responseMsg[0] = (byte) 0x60; //Ver: 1; Transaction Type: 2 (Ack), Option count: 0
                            responseMsg[1] = (byte) 0x50;//Response code: 50h--> 80 dec (corresponds to HTTP 200, OK)
                            
                            for(int i=0,c=4;i<hex.length;i++,c++)
                                responseMsg[c]=hex[i]; //sets the packet data payload
                            
                        }
                        else
                        {
                            responseMsg = new byte[4];
                            responseMsg[0] = (byte) 0x60;
                            responseMsg[1] = (byte) 0xA0; //Bad request code: 160dec (HTTP: Bad Request, 400)
                        }
                    }
                    else if(method == 1) //it is a put
                    {
                        String value=null; //value to set
                        if((recData[4] != (byte) 0x11 || recData[5] != (byte) 0x2A))
                        {
                            System.out.println("Error in the first option field");
                            errFlag=true;
                        }
                        
                        int uriLength=-1;
                        int i;
                        for(i=7;i<length;i++) //defining the URI length and checking the third option field
                        {
                            if(i==length)
                            {
                                System.out.println("Error in the third option field");
                                errFlag=true;
                                break;
                            }
                             
                            else if(recData[i] == (byte) 0x22 && recData[i+1] == (byte) 0x33 && recData[i+2] == (byte) 0x61)
                            {
                                uriLength = i-7;
                                System.out.println("Uri length: "+uriLength);
                                break;
                            }
                            else
                                uri=uri+(char)recData[i];    
                        }
                        
                        if(recData[6] != (byte) (0x80+uriLength))
                        {
                            System.out.println("Error in the second option field");
                            errFlag=true;
                            
                        }
                        
                        responseMsg=new byte[7];
                        responseMsg[0]=(byte) 0x61; //Ver: 1; Transaction Type: 2 (Ack), Option count: 1
                        responseMsg[4]=(byte) 0xB2; //Option field: Option delta: B hex-->11 dec (Token); Lenght: 2 bytes 
                        responseMsg[5]=(byte) 0x33; //ASCII code for '3'
                        responseMsg[6]=(byte) 0x61; //ASCII code for 'a'
                        
                        if(!errFlag)
                        {   
                            i=i+3;//index at the beginning of the payload
                            byte [] data=new byte[length-i];
                            for(int c=0;i<length;c++,i++) //extracts the data to send within the set request
                                data[c]=recData[i];
                            value=new String(data);
                            System.out.println("value to set: "+value);
                            listener.setEvent(uri, value);
                            responseMsg[1]=(byte) 0x50; //Response code: OK
                        }
                        else
                            responseMsg[1]=(byte) 0xA0; //Response code: Bad request
                        
                    }
                    
                    try
                    {
                        responseMsg[2] = messageId[0];
                        responseMsg[3] = messageId[1];
                        packet.setData(responseMsg);
                        System.out.println("Message created: "+binaryToHex(responseMsg,responseMsg.length));
                        socket.send(packet);

                    } catch (IOException e) {
                        System.out.println("Problems in sending the UDP packet: ");
                        e.printStackTrace();
                      }
                        
                 responseMsg=null;
                 uri="";
                    
                }
                
            } catch (Exception e) {
                    e.printStackTrace();
            }    
                
            socket.close();
        }
        
        private String binaryToHex(byte [] data, int length){
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<length;i++) {
                 sb.append(String.format("%02x", data[i]));
            }
            return sb.toString();   

        }
        
        private int packetLength(byte [] packet)
        {
            int length=-1;
            for(int i=0;i<packet.length;i++)
            {
                if(packet[i]==(byte) 0x00 && packet[i+1]==(byte) 0x00 && packet[i+2]==(byte) 0x00 && packet[i+3]==(byte) 0x00 && packet[i+4]==(byte) 0x00 && packet[i+5]==(byte) 0x00)
                {
                    length=i;
                    System.out.println("Packet Length: "+length);
                    break;
                }
            }
            return length;

        }
}
