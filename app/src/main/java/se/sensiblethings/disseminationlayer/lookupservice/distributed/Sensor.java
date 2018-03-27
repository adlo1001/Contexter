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

public class Sensor implements Comparable<Sensor> {
	private NodeID sensorHash;
	private FingerEntry owner;
	
	public Sensor(NodeID sensor,FingerEntry owner) {
		this.sensorHash = sensor;
		this.owner = owner;
	}

	public NodeID getSensorHash() {
		return sensorHash;
	}

	public FingerEntry getOwner() {
		return owner;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sensorHash == null) ? 0 : sensorHash.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		Sensor other = (Sensor) obj;
		if (sensorHash == null) {
			if (other.sensorHash != null)
				return false;
		} else if (!sensorHash.equals(other.sensorHash))
			return false;
		return true;
	}

	public int compareTo(Sensor o) {
		return sensorHash.compareTo(o.sensorHash);
	}
}
