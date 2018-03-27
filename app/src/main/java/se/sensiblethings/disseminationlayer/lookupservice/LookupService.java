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

package se.sensiblethings.disseminationlayer.lookupservice;

import java.util.HashSet;
import java.util.Iterator;

import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.disseminationcore.DisseminationCore;

public abstract class LookupService {
	
	public final static String SERVER = "se.sensiblethings.disseminationlayer.lookupservice.server.ServerLookup";
	public final static String DISTRIBUTED = "se.sensiblethings.disseminationlayer.lookupservice.distributed.DistributedLookup";	
	public final static String KELIPS = "se.sensiblethings.disseminationlayer.lookupservice.kelips.KelipsLookup";
	public final static String EXTENDED_CHORD = "se.sensiblethings.disseminationlayer.lookupservice.extendedchord.DoubleChord";
	public final static String SINGLE_CHORD = "se.sensiblethings.disseminationlayer.lookupservice.extendedchord.SingleChord";	
	
	public final static String STATE_CONNECTED = "LOOKUP_SERVICE_CONNECTED";
	public final static String STATE_DISCONNECTED = "LOOKUP_SERVICE_DISCONNECTED";
	public final static String STATE_CONNECTING = "LOOKUP_SERVICE_CONNECTING";
	public final static String STATE_DISCONNECTING = "LOOKUP_SERVICE_DISCONNECTING";
	public final static String STATE_UNKNOWN = "LOOKUP_SERVICE_UNKNOWN";
	
	public abstract void resolve(String uci);
	public abstract void register(String uci);
	public abstract void register(String uci, String password);
	public abstract void shutdown();		
		
	protected Communication communication;
	protected DisseminationCore disseminationCore;
	
	private String currentState = STATE_UNKNOWN;
	private HashSet<LookupServiceStateListener> listeners;
	 
	public LookupService(Communication communication, DisseminationCore disseminationCore) {
		currentState = STATE_DISCONNECTED;
		this.communication = communication;
		this.disseminationCore = disseminationCore;
		listeners = new HashSet<LookupServiceStateListener>();
	}
	
	public String getState(){
		return currentState;
	}
	protected void setState(String newState){
		currentState = newState;
		callListeners();
	}
	
	public void addStateListener(LookupServiceStateListener listener){
		listeners.add(listener);
		listener.onNewLookupServiceState(this, getState());
	}
	
	public void removeStateListener(LookupServiceStateListener listener){
		listeners.remove(listener);
	}
	
	private void callListeners(){
		for (Iterator<LookupServiceStateListener> iterator = listeners.iterator(); iterator.hasNext();) {
			LookupServiceStateListener listener = iterator.next();
			listener.onNewLookupServiceState(this, getState());
		}
	}
        
}
