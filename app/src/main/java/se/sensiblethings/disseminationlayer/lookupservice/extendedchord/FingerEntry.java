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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class FingerEntry implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5938149748223527777L;

	public static final Comparator<FingerEntry> indexComparator = new Comparator<FingerEntry>() {

		public int compare(FingerEntry o1, FingerEntry o2) {
			if (o1.index - o2.index == 0) {
				return identityComparator.compare(o1, o2);
			} else
				return o1.index - o2.index;
		}
	};

	public static Comparator<FingerEntry> identityComparator = new Comparator<FingerEntry>() {

		public int compare(FingerEntry o1, FingerEntry o2) {
			return o1.fingerIdentity.compareTo(o2.fingerIdentity);
		}
	};

	public static final class BaseIdentityComparator implements
			Comparator<FingerEntry> {
		private String base;

		public BaseIdentityComparator(String identity) {
			base = identity;
		}

		public int compare(FingerEntry o1, FingerEntry o2) {
			if(FingerEntry.isBetween(base, o1.fingerIdentity,o2.fingerIdentity)){
				return 1;
			}else return -1;
		}
	}

	public static int getLogDifference(String first, String second) {
		BigInteger firstValue = new BigInteger(first, 16);
		BigInteger secondValue = new BigInteger(second, 16);
		BigInteger difference = (secondValue.subtract(firstValue)).abs();
		BigDecimal diff = new BigDecimal(difference);
		if (diff.equals(BigDecimal.ZERO)) {
			return 0;
		}
		diff = BigDecimal.ONE.divide(diff, 5, BigDecimal.ROUND_HALF_UP)
				.multiply(new BigDecimal(modulo));
		double logDiff = diff.doubleValue();
		if (!Double.isNaN(logDiff) || !Double.isInfinite(logDiff)) {
			return (int) Math.round(Math.log(logDiff));
		} else {
			return 0;
		}
	}

	private final static BigInteger modulo = BigInteger.valueOf(2).pow(
			DistributedHashTable.BITS_IN_HASH);
	// private final static BigInteger modulo = BigInteger.valueOf(2).pow(3);

	private String fingerIdentity;
	private int index;
	private String idealIdentity;
	private SensibleThingsNode node;
	private String parentIdentity;

	public FingerEntry(String parentIdentity, String fingerIdentity, int index) {
		this.parentIdentity = parentIdentity!= null ? parentIdentity.trim(): null;
		this.fingerIdentity = fingerIdentity != null ? fingerIdentity.trim() : null;
		this.index = index;
		idealIdentity = calculateIdealFingerIdentity(this, index);
	}

	private FingerEntry(FingerEntry that) {
		this.fingerIdentity = that.fingerIdentity;
		this.idealIdentity = that.idealIdentity;
		this.parentIdentity = that.parentIdentity;
		this.index = that.index;
		if(that.node != null)
			this.node = (SensibleThingsNode)that.node.clone();
	}

	@Override
	public FingerEntry clone() {
		return new FingerEntry(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public synchronized String toString() {
		String stringIndex = String.format("%0"+Integer.toString(DistributedHashTable.BITS_IN_HASH).length()+"d", index);
		
		return "Finger[" + stringIndex + "] Identity: " + fingerIdentity + ", owner:"
				+ node +" parent: " +  getParentIdentity() + " ideal entry: " + getIdealFingerIdentity();
	}

	@Override
	public synchronized int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fingerIdentity == null) ? 0 : fingerIdentity.hashCode());
		 result = prime * result + index;
		// result = prime * result + ((node == null) ? 0 : node.hashCode()); //Node is removed since it doesn't impact on if two objects are equal
		 result = prime * result
		 + ((parentIdentity == null) ? 0 : parentIdentity.hashCode());
		return result;
	}

	@Override
	public synchronized boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FingerEntry other = (FingerEntry) obj;
		if (fingerIdentity == null) {
			if (other.fingerIdentity != null)
				return false;
		} else if (!fingerIdentity.equals(other.fingerIdentity))
			return false;
		// if (index != other.index)
		// return false;
		// if (node == null) {
		// if (other.node != null)
		// return false;
		// } else if (!node.equals(other.node))
		// return false;
		// if (parentIdentity == null) {
		// if (other.parentIdentity != null)
		// return false;
		// } else if (!parentIdentity.equals(other.parentIdentity))
		// return false;
		return true;
	}

	public synchronized boolean hasNode() {
		return node != null;
	}

	public synchronized SensibleThingsNode getNode() {
		return node;
	}

	public synchronized void setNode(SensibleThingsNode node) {
		this.node = node;
	}

	public synchronized String getFingerIdentity() {
		return fingerIdentity;
	}

	public synchronized void setFingerIdentity(String fingerIdentity) {
		this.fingerIdentity = fingerIdentity.trim();
	}

	public synchronized int getIndex() {
		return index;
	}

	public synchronized void updateIndex(int index){
		if (index != this.index
				&& index > 0) {
			this.index = DistributedHashTable.BITS_IN_HASH;
			calculateMaximumIndex();
		}
		else if (index != this.index
				&& index == 0){
			this.index = 0;
		}
	}
	private synchronized void setIndex(int index) {
		if (index != this.index
				&& index >= 0) {
			if(index < DistributedHashTable.BITS_IN_HASH){
			idealIdentity = calculateIdealFingerIdentity(this, index);
			this.index = index;
			}
			else{
				idealIdentity = calculateIdealFingerIdentity(this, DistributedHashTable.BITS_IN_HASH-1);
				this.index = DistributedHashTable.BITS_IN_HASH;
			}
		}
	}

	/**
	 * 
	 * @return the identity which is used as the base for calculating validity
	 */
	public synchronized String getParentIdentity() {
		return parentIdentity;
	}

	public synchronized void setParentIdentity(String parentIdentity) {
		this.parentIdentity = parentIdentity.trim();
		calculateMaximumIndex();
		if (!parentIdentity.equals(this.parentIdentity)) {
			idealIdentity = calculateIdealFingerIdentity(this, getIndex());
		}

	}

	public synchronized String getIdealFingerIdentity() {
		if (index == 0) {
			return fingerIdentity;
		} else {
			return idealIdentity;
		}
	}

	private static String calculateIdealFingerIdentity(FingerEntry parent,
			int index) {
		if (index == 0) {
			return parent.getFingerIdentity();
		}
		BigInteger start = new BigInteger(parent.getParentIdentity(), 16);
		BigInteger addition = BigInteger.valueOf(2).pow(index - 1);
		BigInteger sum = start.add(addition);
		sum = sum.remainder(modulo);

		return toStringWithPadding(sum, 16,
				DistributedHashTable.BITS_IN_HASH / 4);
	}

	private static String toStringWithPadding(BigInteger theNumber, int radix,
			int characters) {
		String value = theNumber.toString(radix);
		while (value.length() < characters) {
			value = "0" + value;
		}
		return value;
	}

	/**
	 * Second implementation
	 * 
	 * @param beforeIdentity
	 * @param betweenIdentity
	 * @param afterIdentity
	 * @return
	 */
	public static boolean isBetween(String beforeIdentity,
			String betweenIdentity, String afterIdentity) {
		if (beforeIdentity == null || afterIdentity == null) {
			return false;
		}
		if (beforeIdentity.equals(afterIdentity)) {
			return true;
		}
		int answer = beforeIdentity.compareTo(afterIdentity);
		if (answer > 0) {
			BigInteger before = new BigInteger(beforeIdentity, 16);
			BigInteger between = new BigInteger(betweenIdentity, 16);
			BigInteger after = new BigInteger(afterIdentity, 16);
			BigInteger diff = modulo.subtract(before);
			BigInteger before2 = BigInteger.ZERO;
			BigInteger between2 = between.add(diff).mod(modulo);
			BigInteger after2 = after.add(diff).mod(modulo);
			if (before2.compareTo(between2) < 0
					&& between2.compareTo(after2) < 0) {
				return true;
			} else {
				return false;
			}
		}

		if (beforeIdentity.compareTo(betweenIdentity) < 0
				&& betweenIdentity.compareTo(afterIdentity) < 0) {
			return true;
		} else {
			return false;
		}
	}


	public synchronized void calculateMaximumIndex() { //TODO: Go backwards instead!!!!!!!!
		if (getIndex() == 0)
			return;
		int localIindex = DistributedHashTable.BITS_IN_HASH;
		do{
			setIndex(--localIindex);
		}while (!FingerEntry.isBetween(parentIdentity, idealIdentity, fingerIdentity) && localIindex > 0);
		setIndex(localIindex);

	}
}