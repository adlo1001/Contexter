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

package se.sensiblethings.disseminationlayer.disseminationcore;

import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class NotifyMessage extends Message {
	
	//PreByte
	//private static final long serialVersionUID = -1162756086539932836L;
	
	private static final long serialVersionUID = 7064077729158961917L;
	public String uci;
	public byte[] value;
	
	public NotifyMessage(String uci, byte[] value, SensibleThingsNode toNode, SensibleThingsNode fromNode) {
		super(fromNode, toNode);		
		this.value = value;
		this.uci = uci;
		String s = new String(value);
		//System.out.println("Data " + s );
	}
	

}
