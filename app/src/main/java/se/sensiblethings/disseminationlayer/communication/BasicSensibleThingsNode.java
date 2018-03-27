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

package se.sensiblethings.disseminationlayer.communication;

import java.io.Serializable;

import se.sensiblethings.interfacelayer.SensibleThingsNode;


/**
 *This is the default implementation of a SensibleThingsNode endpoint. 
 *The implementation can vary depending on the underlying communication protocol
 */
public class BasicSensibleThingsNode extends SensibleThingsNode implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5086339935363579468L;
	String ip;
	int port;
	
	public BasicSensibleThingsNode(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}


	public String getIp(){
		return ip;
	}
	
	public int getPort(){
		return port;
	}


	@Override
	public String getAddress() {
		return getIp()+":"+getPort();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + port;
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BasicSensibleThingsNode other = (BasicSensibleThingsNode) obj;
		if (ip == null) {
			if (other.ip != null) {
				return false;
			}
		} else if (!ip.equals(other.ip)) {
			return false;
		}
		if (port != other.port) {
			return false;
		}
		return true;
	}


	private BasicSensibleThingsNode(
			BasicSensibleThingsNode that) {
		this.ip = that.ip;	
		this.port = that.port;
	}

	@Override
	public Object clone() {
		return new BasicSensibleThingsNode(this);
	}
	
	
}