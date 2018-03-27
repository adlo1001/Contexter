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

package se.sensiblethings.disseminationlayer.communication.rudp.socket.exceptions;

import java.net.InetSocketAddress;

import se.sensiblethings.disseminationlayer.communication.DestinationNotReachableException;

@SuppressWarnings("serial")
public class RUDPDestinationNotReachableException extends DestinationNotReachableException {
	private static final String ERROR_MESSAGE = "RUDP destination not reachable. Link failed";

	//Address of failed link - optional
	private InetSocketAddress socketAddress = null;
	
	public RUDPDestinationNotReachableException() {
		super(ERROR_MESSAGE);
	}

	public RUDPDestinationNotReachableException(InetSocketAddress socketAddress) {
		super(ERROR_MESSAGE);
		this.socketAddress = socketAddress;
	}
	
	public InetSocketAddress getInetSocketAddress() {
		return socketAddress;
	}
}
