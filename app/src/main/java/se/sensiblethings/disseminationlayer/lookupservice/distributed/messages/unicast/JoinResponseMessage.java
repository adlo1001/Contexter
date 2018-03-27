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

public class JoinResponseMessage extends Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5383815447534659869L;

	// Send the own key to prevent exploits and duplicate entries
	private NodeID joinKey;

	// NodeID and network address of the successor
	private NodeID successor;
	private SensibleThingsNode successorNode;
	private NodeID predecessor;

	//Which information could be already provided in a JoinResponse
	public JoinResponseMessage(SensibleThingsNode fromNode, SensibleThingsNode toNode, NodeID joinKey,
			SensibleThingsNode successorNode, NodeID successor, NodeID predecessor) {
		super(fromNode, toNode);
		this.joinKey = joinKey;
		this.successor = successor;
		this.successorNode = successorNode;
		this.predecessor = predecessor;
	}

	public String toString() {
		return super.toString() + " | joinKey: " + joinKey.toString() + " suc: " + successor.toString()	+ " pre : " + predecessor.toString();
	}

	public NodeID getJoinKey() {
		return this.joinKey;
	}

	public NodeID getSuccessor() {
		return this.successor;
	}

	public SensibleThingsNode getSuccessorNode() {
		return successorNode;
	}

	public NodeID getPredecessor() {
		return predecessor;
	}

}
