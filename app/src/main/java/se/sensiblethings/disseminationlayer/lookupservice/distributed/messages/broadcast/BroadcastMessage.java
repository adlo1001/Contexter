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

package se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.broadcast;

import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.NodeID;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public abstract class BroadcastMessage extends Message {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3659556456476684490L;
	
	//Definition of the region the broadcast is responsible for
	private NodeID startKey;
	private NodeID endKey;
	
	public BroadcastMessage(SensibleThingsNode fromNode, SensibleThingsNode toNode, NodeID startKey, NodeID endKey) {
		super(fromNode,toNode);
		this.startKey = startKey;
		this.endKey = endKey;
	}
	
	public NodeID getStartKey() {
		return startKey;
	}

	public NodeID getEndKey() {
		return endKey;
	}
	
	protected String toString(String text) {
		return super.toString() + " | " + startKey.toString() + " -> " + endKey.toString() + " | " + extractMessage().toString();
	}
	
	//Extract unicast message
	public abstract Message extractMessage();
	
	//Clone with new addresses to easily forward messages
	public abstract BroadcastMessage cloneWithNewAddresses(SensibleThingsNode fromNode, SensibleThingsNode toNode, NodeID startKey,NodeID endKey);
	
}
