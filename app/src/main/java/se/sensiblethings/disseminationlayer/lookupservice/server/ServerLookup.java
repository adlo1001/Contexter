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

package se.sensiblethings.disseminationlayer.lookupservice.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.disseminationlayer.disseminationcore.DisseminationCore;
import se.sensiblethings.disseminationlayer.lookupservice.LookupService;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class ServerLookup extends LookupService{

	//This is the same code as before, to make it backwards compatible...
	//The Serverlookup is not intended for baseline usage either way...
	
	public static  String serverIp = "sensiblethings.se";
		
	//String serverIp = "193.10.119.33";
	//String serverIp = "192.168.3.2";
	//String serverIp = "193.10.119.42";

	private int serverPort = 8008;
	
	public ServerLookup(Communication communication, DisseminationCore disseminationCore) {
		super(communication, disseminationCore);		
	}

	
	@Override
	public void resolve(final String uci) {
		try {				        									
			Socket s = new Socket(serverIp, serverPort);
            PrintWriter out = new PrintWriter(s.getOutputStream());
            out.println("RESOLVE");
            out.println(uci);	                    	                    
            out.flush();
            
            //SLEEP?
            Thread.sleep(100);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String response = in.readLine();
            String split[] = response.split(":");     
            
            //System.out.println(uci + ":" +response);
            
            SensibleThingsNode node = communication.createSensibleThingsNode(split[0], Integer.parseInt(split[1]));
            disseminationCore.callResolveResponseListener(uci, node);
            out.close();
            in.close();
            s.close();
                                    
		} catch(Exception e){
			//e.printStackTrace();
			//System.out.print("The value probably did not exists in the DHT...");
		}		
	}

	@Override
	public void register(String uci) {
		register(uci,null);
	}
	
	@Override
	public void register(String uci, String password) {
		try {				        								
			Socket s = new Socket(serverIp, serverPort);
            PrintWriter out = new PrintWriter(s.getOutputStream());
            out.println("REGISTER");
            out.println(uci);	                    
            String localIp = communication.getLocalSensibleThingsNode().toString();	                    
            out.println(localIp);
            out.flush();
            
            //SLEEP?
            
            out.close();
            s.close();
                                                    
		} catch(Exception e){
			e.printStackTrace();
		}	
	}


	@Override
	public void shutdown() {
		//Should actually de-register from server as well (not supported right now...)
	}

	public void handleMessage(Message message) {
		//Do nothing..
	}
}
