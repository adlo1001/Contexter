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

package se.sensiblethings.disseminationlayer.lookupservice.kelips;

import java.io.Serializable;

import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class TimestampedNode implements Serializable{
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimestampedNode other = (TimestampedNode) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3043545657005678284L;

	private SensibleThingsNode node;
	
	private long timestamp;
	
	public TimestampedNode(SensibleThingsNode node, long timestamp) {
		this.node = node;
		this.timestamp = timestamp;
	}

	public SensibleThingsNode getNode() {
		return node;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	@Override
	public String toString() {
		return node.toString() + " : " + timestamp;
	}
}
