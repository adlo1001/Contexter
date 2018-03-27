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

public abstract class Message implements Serializable{
	private static final long serialVersionUID = 1200836213525463450L;
	
	private SensibleThingsNode fromNode;
	private SensibleThingsNode toNode;
	
	public SensibleThingsNode getFromNode() {
		return fromNode;
	}
	
	public SensibleThingsNode getToNode() {
		return toNode;
	}
	
	public String getType() {
		return getClass().getName();
	}
	
	public Message(SensibleThingsNode fromNode, SensibleThingsNode toNode) {
		this.fromNode = fromNode;
		this.toNode = toNode;
	}
	
	public String toString() {
		return getClass().getSimpleName() + " from: (" + fromNode + ") - to: (" + toNode + ")";
	}
	
	//Used for statistics in the simulator
	public int getDataAmount() {
		//2 x ip-address + 1 type
		return 4 + 4 + 1;
	}	


	public void setToNode(SensibleThingsNode sensibleThingsNode) {
		toNode = sensibleThingsNode;
	}	    
}
