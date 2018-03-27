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
import se.sensiblethings.disseminationlayer.lookupservice.kelips.TimestampedNode;
import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class KelipsResolveResponseMessage extends Message {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3666369547561432147L;
	private String uci;
	private String uciHash;
	private TimestampedNode node;
	private int id;
	
	public KelipsResolveResponseMessage(SensibleThingsNode fromNode, SensibleThingsNode toNode, String uci, String uciHash, TimestampedNode node, int id) {
		super(fromNode, toNode);
		this.id = id;
		this.uci = uci;
		this.uciHash = uciHash;
		this.node = node;
	}
	
	public String getUci() {
		return uci;
	}
	
	public TimestampedNode getNode() {
		return node;
	}
	
	public String getUciHash() {
		return uciHash;
	}
	
	public int getId(){
		return id;
	}
}
