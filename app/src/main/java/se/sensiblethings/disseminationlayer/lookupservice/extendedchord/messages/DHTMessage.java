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

import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public abstract class DHTMessage extends Message {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8327775368245846542L;
	int hopCount;
	String ring;
	String requestIdentity;
	int serialNumber;
	
	public DHTMessage(SensibleThingsNode fromNode, SensibleThingsNode toNode, String ring) {
		super(fromNode, toNode);
		this.ring = ring;
		hopCount = 0;
	}

	public final String getRing() {
		return ring;
	}
	public final void increaseHopCount(){
		hopCount+=1;
	}
	public final int getHopCount(){
		return hopCount;
	}
	
	/**
	 * @return the requestIdentity
	 */
	public final String getRequestIdentity() {
		return requestIdentity;
	}
	/**
	 * @param requestIdentity the senderIdentity to set
	 */
	public final void setRequestIdentity(String requestIdentity) {
		this.requestIdentity = requestIdentity;
	}
	/**
	 * @return the serialNumber
	 */
	public final int getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public final void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}
	/* (non-Javadoc)
	 * @see se.sensiblethings.disseminationlayer.communication.Message#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + " in " + getRing() + " from " + requestIdentity;
	}
	
	
}
