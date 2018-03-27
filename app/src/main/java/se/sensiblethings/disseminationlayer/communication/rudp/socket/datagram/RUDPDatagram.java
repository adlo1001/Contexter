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

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class RUDPDatagram extends RUDPAbstractDatagram {
	private InetSocketAddress address;
	private byte[] data;
	
	public RUDPDatagram(InetSocketAddress socketAddress,byte[] data) {
		//Datagram contains data
		this.address = socketAddress;
		this.data = data;
	}

	public RUDPDatagram(InetAddress address,int port,byte[] data) {
		//Datagram contains data
		this.address = new InetSocketAddress(address,port);
		this.data = data;
	}

	public InetSocketAddress getSocketAddress() {
		return address;
	}

	public int getPort() {
		return address.getPort();
	}
	
	public byte[] getData() {
		return data;
	}
}
