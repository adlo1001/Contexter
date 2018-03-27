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

package se.sensiblethings.disseminationlayer.lookupservice.kelips.messages;

import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class KelipsResolveMessage extends Message {


	/**
	 * 
	 */
	private static final long serialVersionUID = 9114866475022936879L;
	private String uci;
	private String uciHash;
	private int groupNumber;
	private int id;
	
	public KelipsResolveMessage(SensibleThingsNode fromNode, SensibleThingsNode toNode, String uci, String uciHash, int groupNumber, int id) {		
		super(fromNode, toNode);
		this.uci = uci;
		this.uciHash = uciHash;
		this.id = id;
		this.groupNumber = groupNumber;
	}
		
	public String getUci() {
		return uci;
	}
	
	public String getUciHash() {
		return uciHash;
	}
	
	public int getId(){
		return id;
	}
	
	public int getGroupNumber() {
		return groupNumber;
	}
	
}
