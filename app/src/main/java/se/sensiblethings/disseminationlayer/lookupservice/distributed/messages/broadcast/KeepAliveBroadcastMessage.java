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
import se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast.KeepAliveMessage;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class KeepAliveBroadcastMessage extends BroadcastMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2757968606154496787L;
	
	private SensibleThingsNode advertisedNetworkNode;
	private NodeID advertisedID;
	
	public KeepAliveBroadcastMessage(SensibleThingsNode fromNode,SensibleThingsNode toNode,NodeID startKey,NodeID endKey,NodeID advertisedID,SensibleThingsNode advertisedNetworkNode) {
		super(fromNode,toNode,startKey,endKey);
		this.advertisedID = advertisedID;
		this.advertisedNetworkNode = advertisedNetworkNode;
	}

	@Override
	public Message extractMessage() {
		return new KeepAliveMessage(this.getFromNode(),this.getToNode(),advertisedID,advertisedNetworkNode);
	}
	
	@Override
	public String toString() {
		return super.toString() + extractMessage().toString();
	}

	@Override
	public BroadcastMessage cloneWithNewAddresses(SensibleThingsNode fromNode, SensibleThingsNode toNode, NodeID startKey,NodeID endKey) {
		return new KeepAliveBroadcastMessage(fromNode,toNode,startKey,endKey,advertisedID,advertisedNetworkNode);
	}
	
}
