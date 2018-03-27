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

import java.util.ArrayList;

import se.sensiblethings.interfacelayer.SensibleThingsPlatform;

public class AddInManager {

	SensibleThingsPlatform platform = null;
	
	ArrayList<AddIn> addInList = new ArrayList<AddIn>();
	
	/**
	 * Creates the Add in manager, takes the parent platform as argument
	 * @param platform the parent platform
	 */
	public AddInManager(SensibleThingsPlatform platform) {
		this.platform = platform;
	}	

	/**
	 * Use this method to load any optional add ins the SensibleThings platform.
	 * After this call, the add in will both be loaded and started inside the platform. 
	 * @param addIn the add in to be loaded and started
	 */
	public void loadAddIn(AddIn addIn){

		addIn.loadAddIn(platform);
		addIn.startAddIn();		
		addInList.add(addIn);		
	}
	
	/**
	 * Use this method to unload any add in currently running inside the SensibleThings platform.
	 * After this call, it will be both stopped and unloaded. 
	 * 
	 * @param addIn the add in to be unloaded and stopped
	 */
	public void unloadAddIn(AddIn addIn){

		addIn.stopAddIn();
		addIn.unloadAddIn();		
		addInList.remove(addIn);		
	}
	
	/**
	 * This method will unload and stop all running add ins in the SensibleThings platform.
	 */
	public void unloadAllAddIns(){
		
		while(addInList.size() != 0){
			AddIn addIn = addInList.get(0);
			unloadAddIn(addIn);									
		}
	}	
}
