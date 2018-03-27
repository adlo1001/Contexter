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

package se.sensiblethings.disseminationlayer.communication.rudp.socket.datagram;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import se.sensiblethings.disseminationlayer.communication.rudp.socket.exceptions.InvalidRUDPPacketException;


public class RUDPDatagramPacketIn extends RUDPDatagramPacket {

	//This constructor deserializes a packet, and throws an exception
	//if the data is not a valid packet
	public RUDPDatagramPacketIn(byte[] packet) throws InvalidRUDPPacketException {
		ByteArrayInputStream bis;
		DataInputStream dis;
		int flag;
		int ack_count;
		
		bis = new ByteArrayInputStream(packet);
		dis = new DataInputStream(bis);
		
		//Read flag
		try {
			//debug
			id = dis.readInt();
			
			//Read and analyze flags
			flag = dis.readByte();
			flag_first = (flag & FLAG_FIRST) != 0 ? true : false; 
			flag_reset = (flag & FLAG_RESET) != 0 ? true : false; 
			flag_ack = (flag & FLAG_ACK) != 0 ? true : false; 
			flag_data = (flag & FLAG_DATA) != 0 ? true : false; 
			flag_resend = (flag & FLAG_RESEND) != 0 ? true : false;
			flag_persist = (flag & FLAG_PERSIST) != 0 ? true : false;
			
			//Read static fields
			packet_seq = dis.readInt();
			window_size = dis.readInt();
			frag_nr = dis.readShort();
			frag_count = dis.readShort();
			
			//Read variable length ACK data, if available
			if(flag_ack) {
				//ACK window start sequence
				ack_window_start = dis.readInt();

				//Count means the number of ranges!
				ack_count = dis.readShort();
				
				//Check that the ACK field is not too long
				if(ack_count > RESERVED_ACK_COUNT) throw new InvalidRUDPPacketException();
				
				//Get size of ACK field
				ack_seq_data = new ArrayList<Short>();
				
				//Read all elements 
				for(int i = 0; i < ack_count * 2; i++) {
					ack_seq_data.add(dis.readShort());				
				}
			}

			//Read data if available
			if(flag_data) {
				int dataSize = bis.available();
				if(dataSize < 0) throw new InvalidRUDPPacketException();
				data = new byte[dataSize];
				dis.readFully(data,0,dataSize);
			}
			
			dis.close();
			bis.close();
		}
		catch (IOException e) {
			//Transform to invalid packet exception
			throw new InvalidRUDPPacketException();
		}
	}
}