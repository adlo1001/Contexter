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

package se.sensiblethings.addinlayer.extensions.publishsubscribe;

import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class StartSubscribeMessage extends Message {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8307350286079260156L;
	public String uci;
	
	public StartSubscribeMessage(String uci, SensibleThingsNode toNode, SensibleThingsNode fromNode) {
		super(fromNode, toNode);
		
		this.uci = uci;
		
	}

}
