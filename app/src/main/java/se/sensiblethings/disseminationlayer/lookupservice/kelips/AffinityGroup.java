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

package se.sensiblethings.disseminationlayer.lookupservice.kelips;

import java.io.Serializable;
import java.util.Random;


public class AffinityGroup implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1735318406567427675L;
	private Random r = new Random(System.currentTimeMillis());	
	final int NUM_FUNGERS = 3; 
		
	private Finger[] fingers = new Finger[NUM_FUNGERS];
	
	
	public void replaceRandomFinger(Finger finger){
		
		//Check if already exists
		for(int i = 0; i != NUM_FUNGERS; i++){
			if(fingers[i].getNode().getAddress().equalsIgnoreCase(finger.getNode().getAddress())){				
				return;
			}							
		}		

		//If not, replace a random finger
		int randomPos = r.nextInt(NUM_FUNGERS);
		fingers[randomPos] = finger;		
		
		
	}
	
	public void setFinger(Finger finger, int i){		
		this.fingers[i] = finger;		
	}
	
	public Finger getFinger(int i){
		return fingers[i];
	}
	
	public Finger getRandomFinger(){
		return fingers[r.nextInt(NUM_FUNGERS)];
		
	}

	public boolean fingerExists(Finger finger) {
		
		//Check if already exists
		for(int i = 0; i != NUM_FUNGERS; i++){
			if(fingers[i].getNode().getAddress().equalsIgnoreCase(finger.getNode().getAddress())){				
				return true;
			}							
		}			
		return false;
	}
}
