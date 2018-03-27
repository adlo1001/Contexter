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

package se.sensiblethings.disseminationlayer.lookupservice.distributed.messages.unicast;

import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.disseminationlayer.lookupservice.distributed.NodeID;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class NotifyLeaveMessage extends Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7208320916687141822L;
	private NodeID hash;
	private NodeID successorHash;
	private SensibleThingsNode successorNetworkNode;

	public NotifyLeaveMessage(SensibleThingsNode fromNode, SensibleThingsNode toNode, NodeID hash, NodeID successorHash, SensibleThingsNode successorNetworkNode) {
		super(fromNode, toNode);
		this.hash = hash;
		this.successorHash = successorHash;
		this.successorNetworkNode = successorNetworkNode;
	}

	public NodeID getSuccessorHash() {
		return successorHash;
	}

	public SensibleThingsNode getSuccessorNetworkNode() {
		return successorNetworkNode;
	}

	public NodeID getHash() {
		return hash;
	}

	public String toString() {
		// Return message info
		return super.toString() + " hash: " + hash.toString() + " - sucHash: " + successorHash.toString() + " - SucAddr: ("	+ successorNetworkNode + ")";
	}
}
