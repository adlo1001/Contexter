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

public class ResolveMessage extends Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 786773931083687404L;
	private NodeID sensorHash;
	private SensibleThingsNode origNode;

	public ResolveMessage(SensibleThingsNode fromNode, SensibleThingsNode toNode, NodeID sensorHash, SensibleThingsNode origNode) {
		super(fromNode, toNode);
		this.sensorHash = sensorHash;
		this.origNode = origNode;
	}

	public NodeID getSensorHash() {
		return sensorHash;
	}

	public ResolveMessage cloneWithNewAddress(SensibleThingsNode fromNode, SensibleThingsNode toNode) {
		return new ResolveMessage(fromNode, toNode, sensorHash, origNode);
	}

	public SensibleThingsNode getOrigNode() {
		return origNode;
	}

	public String toString() {
		// Return message info
		return super.toString() + " sensor: " + sensorHash;
	}
	
}
