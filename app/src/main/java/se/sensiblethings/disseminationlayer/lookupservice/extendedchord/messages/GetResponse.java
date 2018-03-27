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

import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.DHTEntry;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class GetResponse extends DHTMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3864922579745013380L;
	private DHTEntry entry;
	private String requestedKey;

	public GetResponse(SensibleThingsNode fromNode, SensibleThingsNode toNode,
			String ring, String requestedKey, DHTEntry entry) {
		super(fromNode, toNode, ring);
		this.entry = entry;
		this.requestedKey = requestedKey;
	}

	public DHTEntry getEntry() {
		return entry;
	}

	public String getIdentity() {
		return requestedKey;
	}

}
