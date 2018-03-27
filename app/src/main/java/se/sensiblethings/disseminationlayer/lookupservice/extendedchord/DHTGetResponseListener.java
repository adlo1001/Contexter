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

import se.sensiblethings.interfacelayer.SensibleThingsNode;

public interface DHTGetResponseListener {

	/**
	 * Callback for DHT get
	 * @param identity the key retrieved
	 * @param node the node that replied with the entry
	 * @param entry the entry corresponding to the key
	 */
	void getResponse(String identity, SensibleThingsNode node, DHTEntry entry);
	void negativeGetResponse(String identity);
}
