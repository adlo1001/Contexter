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

package se.sensiblethings.disseminationlayer.lookupservice.extendedchord;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class DHTEntry implements Serializable,Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2013403342793333377L;
	
	public static final Comparator<DHTEntry> AscendingUpdatedTime = new Comparator<DHTEntry>(){
				public int compare(DHTEntry o1, DHTEntry o2) {
					return (int) (o1.getLastUpdated()-o2.getLastUpdated());
				}
};
	
	
	
	private String keyName;
	private SensibleThingsNode owner;
	private String uci;
	private Map<String, String> properties;
	private long lastUpdated;
	/**
	 * The key corresponds to KeyIdentity in the inner ring storing the uci for later external resolves
	 * the DHT stores this value at location KeyName retrieved from hashing the uci
	 * The key corresponds to ID in the outer ring storing the senibleThings node to whom it belongs to.
	 * The DHT stores this entry at location KeyIdentity created from hashing the user selected Name
	 * @param key
	 * @param uci
	 */
	public DHTEntry(String key, String uci){
		this(key,uci,null);
		this.lastUpdated = System.currentTimeMillis();
	}
	public DHTEntry(String keyName, String uci, SensibleThingsNode ownerNode){
		setIdentity(keyName);
		setUci(uci);
		setOwnerNode(ownerNode);
	}
	private DHTEntry(DHTEntry that){
		this.keyName = that.keyName;
		this.lastUpdated = that.lastUpdated;
		if(that.owner != null)
			this.owner = (SensibleThingsNode) that.owner.clone();
		this.uci = that.uci;
		if(that.properties != null){
			properties = new HashMap<String, String>();
			for (Iterator<Entry<String, String>> iterator = that.properties.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, String> entry = iterator.next();
				properties.put(entry.getKey().intern(), entry.getValue().intern());
			}
		}
	}

	public SensibleThingsNode getOwnerNode() {
		return owner;
	}

	public String getKey() {
		return keyName;
	}

	public void setUci(String uci) {
		this.uci = uci;
		
	}
	public String getUci() {
		return uci;
	}
	
	void setOwnerNode(SensibleThingsNode owner){
		this.owner = owner;
		setLastUpdated();
	}

	public void setIdentity(String keyName) {
		this.keyName = keyName;
		setLastUpdated();
		
	}
	
	public long getLastUpdated(){
		return lastUpdated;
	}
	
	public void setLastUpdated(){
		lastUpdated = System.currentTimeMillis();
	}
	
	public void setProperty(String key, String value){
		setLastUpdated();
		if(properties == null){
			properties = new ConcurrentHashMap<String, String>();
		}
		if(value != null){
			properties.put(key, value);
		}else{
			properties.remove(key);
		}
	}
	
	public String getProperty(String key, String defaultValue){
		if(properties.containsKey(key)){
			return properties.get(key);
		}else{
			return defaultValue;
		}
	}
	public void clearProperties(){
		if(properties !=null){
			properties.clear();
		}
	}
	/**
	 * Updates instance properties with that of the newEntry
	 * @param newEntry instance containing new data 
	 * @return true if entry was updated 
	 */
	public boolean updateWith(DHTEntry newEntry) {
		setLastUpdated();
		if(this.keyName.equals(newEntry.keyName)){
			this.uci = newEntry.uci;
			this.owner = newEntry.owner;
			if(newEntry.properties != null){
				if(properties != null){
					properties.putAll(newEntry.properties);
				}
				else{
					properties = newEntry.properties;
				}
			}
			return true;
		}
		else{
			return false;
		}
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
		result = prime * result + ((uci == null) ? 0 : uci.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DHTEntry other = (DHTEntry) obj;
		if (keyName == null) {
			if (other.keyName != null) {
				return false;
			}
		} else if (!keyName.equals(other.keyName)) {
			return false;
		}
		if (uci == null) {
			if (other.uci != null) {
				return false;
			}
		} else if (!uci.equals(other.uci)) {
			return false;
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DHTEntry [keyName=" + keyName + ", owner=" + owner + ", uci="
				+ uci + ", properties=" + properties + ", lastUpdated="
				+ new Date(lastUpdated) + "]";
	}
	
	@Override
	public Object clone() {
		return new DHTEntry(this);
	}
}
