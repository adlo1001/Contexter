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

package se.sensiblethings.addinlayer;

import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public interface AddIn {
	
	/**
	 * This will load and initialize the specific add in
	 * @param platform The parent SensibleThings platform
	 */
	public void loadAddIn(SensibleThingsPlatform platform);
	
	/**
	 * This will start the specific add in. Should be called after the loadAddIn().
	 */
	public void startAddIn();
		
	/**
	 * This will stop the specific add in. Should be called before the unloadAddIn().
	 */
	public void stopAddIn();
	
	/**
	 * This will unload the specific add in, removing all trace from it.
	 */
	public void unloadAddIn();

}
