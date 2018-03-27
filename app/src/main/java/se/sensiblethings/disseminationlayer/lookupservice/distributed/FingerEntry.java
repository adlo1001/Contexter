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

package se.sensiblethings.disseminationlayer.lookupservice.distributed;

import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class FingerEntry implements Comparable<FingerEntry> {
	private NodeID nodeID;
	private SensibleThingsNode node;
	
	/*R.I.P
	//Static constants for max and min positions on the DHT 
	public static final FingerEntry MIN_POS_FINGER = new FingerEntry(NodeID.MIN_POSITION(),null);
	public static final FingerEntry MAX_POS_FINGER = new FingerEntry(NodeID.MAX_POSITION(),null);
	*/
	
	//for later optimization and fault handling
	//private int avg_delay;
	//private boolean bad_connection = false;
	private int lastKeepAliveTime = -1;

	public FingerEntry(NodeID nodeID, SensibleThingsNode sensibleThingsNode) {
		this.nodeID = nodeID;
		this.node = sensibleThingsNode;
	}
	
	public int compareTo(FingerEntry comp) {
		return nodeID.compareTo(comp.nodeID);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeID == null) ? 0 : nodeID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
//		if (getClass() != obj.getClass())
//			return false;
		FingerEntry other = (FingerEntry) obj;
		assert other != null;
		if (nodeID == null) {
			if (other.nodeID != null)
				return false;
		} else if (!nodeID.equals(other.nodeID))
			return false;
		return true;
	}
	
	public void setKeepAliveTime(int time) {
		lastKeepAliveTime = time;
	}
	
	public int getKeepAliveTime() {
		return lastKeepAliveTime;
	}

	public NodeID getNodeID() {
		return nodeID;
	}
	
	public SensibleThingsNode getSensibleThingsNode() {
		return node;
	}
	
	public String toString() {
		//Prepend network address
		return "(" + node.toString() + ")-" + nodeID.toString();
	}
}
