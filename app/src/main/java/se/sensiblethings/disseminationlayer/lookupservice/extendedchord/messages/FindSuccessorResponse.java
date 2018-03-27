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

import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.FingerEntry;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class FindSuccessorResponse extends DHTMessage{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2224960447312938719L;
	private String identity;
	private FingerEntry successor;

	public FindSuccessorResponse(SensibleThingsNode fromNode,
			SensibleThingsNode toNode, String ring, String identity, FingerEntry successor) {
		super(fromNode, toNode, ring);
		this.identity = identity;
		this.successor = successor;

	}

	public String getIdentity() {
		return identity;
	}

	public FingerEntry getSuccessor() {
		return successor;
	}


}
