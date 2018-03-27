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

package se.sensiblethings.sensoractuatorlayer;

import java.util.ArrayList;

import se.sensiblethings.interfacelayer.SensibleThingsNode;
import se.sensiblethings.interfacelayer.SensibleThingsPlatform;


public class SensorActuatorManager {

    SensibleThingsPlatform platform = null;
    ArrayList<SensorActuator> sensorActuatorList = new ArrayList<SensorActuator>();

    /**
     * Creates the Sensor & Actuator in manager, takes the parent platform as argument
     * @param platform the parent platform
     */
    public SensorActuatorManager(SensibleThingsPlatform platform) {
        this.platform = platform;
    }

    /**
     * Use this method to connect any sensor/actuator in the network.
     * After this call, the sensor/actuator will be registered and available inside the platform.
     * @param sensorActuator the sensor/actuator object to be connected
     */
    public void connectSensorActuator(SensorActuator sensorActuator) {
    	//Add to list
        sensorActuatorList.add(sensorActuator);

        //Register in the platform
        platform.register(sensorActuator.getUci());
    }

    /**
     * Use this method to disconnect a sensor/actuator currently running in the SensibleThings platform.
     * @param sensorActuator the sensor/actuator to be disconnected
     */
    public void disconnectSensorActuator(SensorActuator sensorActuator) {    	
    	//We just remove it from our list
        sensorActuatorList.remove(sensorActuator);
    }

    /**
     * This method will disconnect all connected sensors of this node in the SensibleThings platform.
     */
    public void disconnectAllSensorActuators() {
        while (sensorActuatorList.size() != 0) {
            SensorActuator sensorActuator = sensorActuatorList.get(0);
            disconnectSensorActuator(sensorActuator);
        }
    }

    /**
     * This method is called from the outside, when we want to forward a getEvent to the sensors
     */
    public void handleGetEvent(SensibleThingsNode source, String uci) {
    	try {
	        //Find the correct sensor and set it
	        for (int i = 0; i != sensorActuatorList.size(); i++) {
	            SensorActuator sensorActuator = sensorActuatorList.get(i);
	            if (sensorActuator.getUci().equalsIgnoreCase(uci)) {
	                String value = "";					
					value = sensorActuator.getValue();					
	                platform.notify(source, uci, value);
	            }
	        }
        } catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * This method is called from the outside, when we want to forward a setEvent to the sensors
     */
    public void handleSetEvent(String uci, String value) {
    	try {
	        //Find the correct sensor and set it
	        for (int i = 0; i != sensorActuatorList.size(); i++) {
	            SensorActuator sensorActuator = sensorActuatorList.get(i);
	            if (sensorActuator.getUci().equalsIgnoreCase(uci)) {
					sensorActuator.setValue(value);						
	            }
	        }
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
