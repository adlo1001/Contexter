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

package se.sensiblethings.disseminationlayer.lookupservice.distributed;

import java.io.Serializable;
import java.math.BigInteger;

@SuppressWarnings("serial")
public class NodeID implements Comparable<NodeID>,Serializable {
	//This is the address size, 160 bytes for SHA1
	public static final int ADDRESS_SIZE = 20;
	
	//Calculation constants
	private static final BigInteger bigModulo = BigInteger.ONE.shiftLeft(ADDRESS_SIZE * 8);

	//private byte[] id = new byte[ADDRESS_SIZE];
	private BigInteger id;
	
	private NodeID(BigInteger bigInt) {
		//Test that!!!
		assert bigInt.signum() >= 0;
		id = bigInt;
	}
	
	public NodeID(byte[] id) {
		//Do modulo, so the number is never bigger than the address size
		this.id = new BigInteger(1,id).mod(bigModulo);
		
		byte[] ar = BigIntToHashArray(this.id);
		assert id.equals(ar);
	}
	
	private NodeID(NodeID that){
		id = that.id;	
	}
	public Object clone(){
		return new NodeID(this);
	}
	
	public byte[] getID() {
		//Return hash array
		return BigIntToHashArray(id);
	}
	
	public String toString() {
		//Shorter version
		String result = SHA1Generator.convertToHex(getID());
		return "{" + result.substring(0,4) + "...}"; // + result.substring(36,40); 
	}

	public int compareTo(NodeID comp) {
		//Its us!
		return id.compareTo(comp.id); 
	}
	
	//Add two node hash values
	public NodeID add(NodeID hash) {
		//Add value and make sure to keep it in the range!
		return new NodeID(id.add(hash.id).mod(bigModulo));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		//if (getClass() != obj.getClass())
		//	return false;
		NodeID other = (NodeID) obj;
		assert other != null;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static NodeID powerOfTwo(int n) {
		//Return 2^n modulo 2^ADDRESS_SIZE;
		return new NodeID(BigInteger.ONE.shiftLeft(n).mod(bigModulo));
	}

	//Subtract two node hash values
	public NodeID sub(NodeID hash) {
		BigInteger result = id.subtract(hash.id);
		
		//Test for underflow, correct and return
		if(result.signum() < 0) result = result.add(bigModulo);
		return new NodeID(result);
	}
	
	//Subtract integers
	public NodeID sub(int n) {
		BigInteger result = id.subtract(BigInteger.valueOf(n));
		
		//Test for underflow, correct and return
		if(result.signum() < 0) result = result.add(bigModulo);
		return new NodeID(result);
	}
	
	//Add integers
	public NodeID add(int n) {
		BigInteger result = id.add(BigInteger.valueOf(n));
		
		//Test for overflow
		if(result.compareTo(bigModulo) > 0) result = result.subtract(bigModulo);
		
		return new NodeID(result);
	}

	//Figure out if this works...
	public static int logTwoFloor(NodeID nodeID) {
		return nodeID.id.bitLength() - 1;
	}

	public boolean between(NodeID start,NodeID end) {
		//Check if THIS node is in [start,end]
		if(start.compareTo(end) < 0) {
			//Node must be INSIDE of the range to be in between
			if(this.compareTo(start) >= 0 && this.compareTo(end) <= 0) {
				return true;
			}
			else {
				return false;
			}
		}
		else if(start.compareTo(end) > 0) {
			//Node must be OUTSIDE of the range to be in between ;-)
			if(this.compareTo(start) >= 0 || this.compareTo(end) <= 0) {
				return true;
			}
			else {
				return false;
			}
		}
		else return false;
	}
	
	private static byte[] BigIntToHashArray(BigInteger bigInt) {
		byte[] temp = bigInt.toByteArray();
		byte[] result = new byte[ADDRESS_SIZE];
		
		//Length is fine!
		if(temp.length == ADDRESS_SIZE) return temp;
		
		//Correct length
		if(temp.length != ADDRESS_SIZE) {
			for(int i = 0; i < (temp.length > 20 ? 20 : temp.length); i++) {
				result[result.length - i - 1] = temp[temp.length - i - 1];
			}
		}
		
		return result;
	}
}
