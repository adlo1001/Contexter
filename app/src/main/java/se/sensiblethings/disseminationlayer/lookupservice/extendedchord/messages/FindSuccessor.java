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

import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class FindSuccessor extends DHTMessage {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8139844598588366245L;
	private String key;
	private DHTMessage message;

	public FindSuccessor(SensibleThingsNode fromNode, SensibleThingsNode toNode, String ring, String key, DHTMessage message) {
		super(fromNode, toNode, ring);		
		this.key = key;
		this.message = message;
	}

	public String getKey() {
		return key;
	}
	
	public DHTMessage getMessage(){
		return message;
	}
	
	public boolean hasMessage(){
		return message != null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(hasMessage())
			return super.toString() + " searching for " + key + " with an internal message: " + message.toString();
		else
			return super.toString() + " searching for " + key;
	}
	
	

}
