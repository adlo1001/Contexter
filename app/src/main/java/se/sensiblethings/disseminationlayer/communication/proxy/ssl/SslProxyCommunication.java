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

package se.sensiblethings.disseminationlayer.communication.proxy.ssl;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import se.sensiblethings.disseminationlayer.communication.proxy.ProxyCommunication;



public class SslProxyCommunication extends ProxyCommunication  {
	
	private static int proxyPort = 14524;
	

	@Override
	protected Socket createSocket() throws UnknownHostException, IOException {
		SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) socketFactory.createSocket(proxyHost, proxyPort);
		
	    final String[] enabledCipherSuites = { "SSL_DH_anon_WITH_RC4_128_MD5" };
	    socket.setEnabledCipherSuites(enabledCipherSuites);	
	    return socket;
	}

}
