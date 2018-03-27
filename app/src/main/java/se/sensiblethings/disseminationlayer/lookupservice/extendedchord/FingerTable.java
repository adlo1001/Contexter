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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class FingerTable implements Serializable,Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5340805017762579931L;
	ArrayList<FingerEntry> table;
	private String identity;
	
	public FingerTable (String identity) {
		this.identity = identity;
		table = new ArrayList<FingerEntry>();
	}

	private FingerTable(FingerTable other) {
		this.identity = other.identity;
		this.table = new ArrayList<FingerEntry>();
		synchronized(other.table){
			for (Iterator<FingerEntry> iterator = other.table.iterator(); iterator.hasNext();) {
				FingerEntry fingerEntry =  iterator.next();
				table.add(fingerEntry.clone());
			}
		}
	}

	public FingerEntry remove(String identity) {
		if(identity == null){
			return null;
		}
		FingerEntry entry = null;
		synchronized (table) {
			
			while(contains(identity)){
				for (Iterator<FingerEntry> iterator = table.iterator(); iterator
						.hasNext();) {
					FingerEntry finger = iterator.next();
					if (finger.getFingerIdentity().equals(identity)) {
						entry = finger;
						iterator.remove();
						break;
					}
	
				}
			}
		}
		return entry;
	}

	public void put(FingerEntry entry) {
		if(entry.getIndex() == 0){
			FingerEntry currentSuccessor = get(0);
			if(currentSuccessor == null){
				synchronized (table) {
					table.add(entry);
				}
			}else{
				currentSuccessor.setNode(entry.getNode());
				currentSuccessor.setFingerIdentity(entry.getFingerIdentity());
			}
		}else{
			FingerEntry currentFinger = get(entry.getIndex());
			if(currentFinger != null){
//				currentFinger.merge(entry);
				currentFinger.setNode(entry.getNode());
				currentFinger.setFingerIdentity(entry.getFingerIdentity());
			}else{
				synchronized (table) {
					table.add(entry);
				}
			}
		}
		
	}


	public boolean isEmpty() {
		synchronized (table) {
			if (table.isEmpty()) {
				return true;
			} else {
				return false;
			}
		}
	}

	public FingerEntry getClosestPreceedingFinger(String requestedIdentity) {
		synchronized (table) {
			if (table.isEmpty()) {
				return null;
			}
			Collections.sort(table, Collections.reverseOrder(FingerEntry.indexComparator));
			for (Iterator<FingerEntry> iterator = table.iterator(); iterator
					.hasNext();) {
				FingerEntry candidateEntry = iterator.next();
				if (FingerEntry.isBetween(this.identity, candidateEntry.getFingerIdentity(), requestedIdentity)) {
					return candidateEntry;
				}
			}
			return get(0);
		}
	}

	public int size() {
		synchronized (table) {
			return table.size();
		}
	}


	public FingerEntry get(String fingerIdentity) {
		
		synchronized (table) {
			for (Iterator<FingerEntry> iterator = table.iterator(); iterator
					.hasNext();) {
				FingerEntry entry = iterator.next();
				if (fingerIdentity.equals(entry.getFingerIdentity())) {
					return entry;
				}
			}
		}
		return null;
	}

	public void addAll(FingerTable foreignFingerTable) {
		synchronized (table) {
			if(table.isEmpty())
			for (Iterator<FingerEntry> iterator = foreignFingerTable.table.iterator(); iterator.hasNext();) {
				FingerEntry entry = iterator.next();
				entry.setParentIdentity(identity);
				entry.updateIndex(1);
				entry.calculateMaximumIndex();
				FingerEntry existing = get(entry.getIndex());
				if(existing != null){
					if(FingerEntry.isBetween(entry.getIdealFingerIdentity(), entry.getFingerIdentity(), existing.getFingerIdentity())){
						//existing.merge(entry);
						existing.setNode(entry.getNode());
						existing.setFingerIdentity(entry.getFingerIdentity());
					}
				}
				else{
					table.add(entry);
				}
				
			}
		}
	}

	public boolean contains(String fingerIdentity) {
		synchronized (table) {
			for (Iterator<FingerEntry> iterator = table.iterator(); iterator
					.hasNext();) {
				FingerEntry entry = iterator.next();
				if (fingerIdentity.equals(entry.getFingerIdentity())) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean contains(int fingerIndex) {
		synchronized (table) {
			for (Iterator<FingerEntry> iterator = table.iterator(); iterator
					.hasNext();) {
				FingerEntry entry = iterator.next();
				if (entry.getIndex() == fingerIndex) {
					return true;
				}
			}
		}
		return false;
	}
	public void log(String string){
		if(DoubleChordSettings.DEBUG){
			System.out.println("[DHT:Finger:"+identity+"]\n"+string);
		}
	}
	/**
	 * 
	 * @param index - index of the finger 
	 * @return the finger which corresponds to this index, or null if not existant
	 */
	public FingerEntry get(int index) {
		synchronized (table) {
			for (Iterator<FingerEntry> iterator = table.iterator(); iterator
					.hasNext();) {
				FingerEntry entry = iterator.next();
				if (entry.getIndex() == index) {
					return entry;
				}
			}
		}
		return null;
	}
	
	public FingerEntry getTabledFinger(int tableIndex){
		synchronized (table) {
			if (tableIndex >= 0 && tableIndex < table.size()) {
				Collections.sort(table, FingerEntry.indexComparator);
				return table.get(tableIndex);
			} else {
				return null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		synchronized (table) {
			Collections.sort(table, FingerEntry.indexComparator);
			int i = 0;
			StringBuilder sb = new StringBuilder();
			for (Iterator<FingerEntry> iterator = table.iterator(); iterator
					.hasNext(); sb.append("\n")) {
				FingerEntry entry = iterator.next();
				sb.append("index: ").append(i++).append(" ").append(entry.toString());

			}
			return sb.toString();
		}
	}

	public void remove(FingerEntry finger) {
		log("Removing:" + finger.toString() + "\n");
		remove(finger.getFingerIdentity());
	}

	public FingerEntry remove(int index) {
		
		synchronized (table) {
			for (Iterator<FingerEntry> iterator = table.iterator(); iterator
					.hasNext();) {
				FingerEntry entry = iterator.next();
				if (entry.getIndex() == index) {
					iterator.remove();
					return entry;
				}
			}
		}
		return null;
	}

	public FingerEntry removeTabledFinger(int i) {
		synchronized (table) {
			return table.remove(i);
		}
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public FingerTable clone()  {
		FingerTable other = new FingerTable(this);
		return other;
	}

	public String getIdentity() {
		return identity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identity == null) ? 0 : identity.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
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
		FingerTable other = (FingerTable) obj;
		if (identity == null) {
			if (other.identity != null) {
				return false;
			}
		} else if (!identity.equals(other.identity)) {
			return false;
		}
		if (table == null) {
			if (other.table != null) {
				return false;
			}
		} else {
			synchronized(table){
				synchronized(other.table){
					if (!table.equals(other.table)) {
						return false;
					}
				}
			}
		}
		return true;
	}


	
	
}
