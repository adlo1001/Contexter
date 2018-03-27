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

package se.sensiblethings.disseminationlayer.lookupservice.extendedchord.messages;

import java.util.Map;

import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.DHTEntry;
import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.FingerEntry;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class JoinResponse extends DHTMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6816920495667771464L;
	private Map<DHTEntry, String> data;
	private String identity;
	private FingerEntry predecessor;
	public JoinResponse(SensibleThingsNode fromNode, SensibleThingsNode toNode,
			String ring,String identity,  Map<DHTEntry, String> data2, FingerEntry predecessor) {
		super(fromNode, toNode, ring);
		this.data = data2;
		this.identity = identity;
		this.predecessor = predecessor;
	}

	/**
	 * @return the data
	 */
	public Map<DHTEntry, String> getData() {
		return data;
	}
	
	public String getIdentity(){
		return identity;
	}
	public FingerEntry getPredecessor(){
		return predecessor;
	}

}
