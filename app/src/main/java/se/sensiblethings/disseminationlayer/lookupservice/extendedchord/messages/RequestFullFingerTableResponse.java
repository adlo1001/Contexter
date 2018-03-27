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

import se.sensiblethings.disseminationlayer.lookupservice.extendedchord.FingerTable;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class RequestFullFingerTableResponse extends DHTMessage {



	/**
	 * 
	 */
	private static final long serialVersionUID = -2166856689842920281L;
	private FingerTable fingerTable;

	public RequestFullFingerTableResponse(SensibleThingsNode fromNode,
			SensibleThingsNode toNode, String ring, FingerTable fingerTable) {
		super(fromNode, toNode, ring);
		this.fingerTable = fingerTable;
	}

	public FingerTable getFingerTable() {
		return fingerTable;
	}
	
	

}
