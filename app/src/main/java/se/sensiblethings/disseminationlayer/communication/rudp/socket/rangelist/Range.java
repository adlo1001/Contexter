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

package se.sensiblethings.disseminationlayer.communication.rudp.socket.rangelist;

//For range definition
public class Range implements Comparable<Range> {
	private short start,end;
	
	public Range(short start,short end) {
		this.start = start;
		this.end = end;
	}

	public short getStart() {
		return start;
	}

	public short getEnd() {
		return end;
	}
	
	public Range merge(Range merge_range) {
		//Not merge-able cases
		if(merge_range == null) return null;
		if(this.start > merge_range.end + 1 || merge_range.start > this.end + 1) return null;
		
		//Merge
		return new Range(this.start < merge_range.start ? this.start : merge_range.start,this.end > merge_range.end ? this.end : merge_range.end);
	}

	public int compareTo(Range o) {
		return start - o.start;
	}
}
