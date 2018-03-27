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

package se.sensiblethings.addinlayer.optimizations.buffer;

import java.util.HashMap;

import se.sensiblethings.addinlayer.optimizations.Optimization;
import se.sensiblethings.disseminationlayer.disseminationcore.DisseminationCore;
import se.sensiblethings.disseminationlayer.disseminationcore.GetEventListener;
import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public class BufferOptimization implements Optimization, GetEventListener{

	SensibleThingsPlatform platform = null;		
	DisseminationCore core = null;
	HashMap<String, String> buffer = new HashMap<String, String>();
	
	boolean runAddIn = true;
		
	private GetEventListener savedGetEventListener = null;
	
	public void loadAddIn(SensibleThingsPlatform platform) {
		this.platform = platform;	
		this.core = platform.getDisseminationCore();
	}

	public void startAddIn() {		
		runAddIn = true;
		Thread t = new HijackThread();
		t.start();
	}

	public void stopAddIn() {
		runAddIn = false;		
	}

	public void unloadAddIn() {
		buffer.clear();
	}
	
	/**
	 * This function will buffer a value for a sensor.
	 * It will then protect the sensors from hammering, etc.
	 * @param uci the UCI related to the value
	 * @param value the actual value
	 */
	public void bufferContextValue(String uci, String value){		
		buffer.put(uci, value);
	}
		
	public void getEvent(SensibleThingsNode source, String uci) {
		
		//Get stored value
		String value = buffer.get(uci);
		
		if(value != null){
			//If exists, notify, 
			platform.notify(source, uci, value);			
		} else {
			//Run a normal event from the saved listener
			if(savedGetEventListener != null){
				savedGetEventListener.getEvent(source, uci);				
			}
		}
	}
	
	//This thread hijacks the getEvents and takes care of them
	private class HijackThread extends Thread {
		
		@Override
		public void run() {			
			while(runAddIn){
				try {
				
					if(core.getGetEventListener() != null && core.getGetEventListener() != BufferOptimization.this){						
						savedGetEventListener = core.getGetEventListener();						
					}
					core.setGetEventListener(BufferOptimization.this);//Take over the listener!
					
					Thread.sleep(500);

				} catch (Exception e) {
					//e.printStackTrace();					
				}
			}
		}
	}
}
