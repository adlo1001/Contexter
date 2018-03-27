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

public class Put extends DHTMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5916315072982954980L;
	private String password;
	private DHTEntry entry;
	private String key;
	private boolean isReplica;

	public Put(SensibleThingsNode fromNode, SensibleThingsNode toNode,
			String ring, String key, DHTEntry entry, String password) {
		super(fromNode, toNode, ring);
		this.key = key;
		this.entry = entry;
		this.password = password;
		isReplica = false;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the entry
	 */
	public DHTEntry getEntry() {
		return entry;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	public boolean getReplica(){
		return isReplica;
	}
	public void setReplica(boolean b) {
		isReplica = b;
	}

}
