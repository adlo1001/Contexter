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

package se.sensiblethings.interfacelayer;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import se.sensiblethings.addinlayer.AddInManager;
import se.sensiblethings.disseminationlayer.communication.Communication;
import se.sensiblethings.disseminationlayer.disseminationcore.DisseminationCore;
import se.sensiblethings.disseminationlayer.disseminationcore.GetEventListener;
import se.sensiblethings.disseminationlayer.disseminationcore.GetResponseListener;
import se.sensiblethings.disseminationlayer.disseminationcore.ResolveResponseListener;
import se.sensiblethings.disseminationlayer.disseminationcore.SetEventListener;
import se.sensiblethings.disseminationlayer.lookupservice.LookupService;
import se.sensiblethings.sensoractuatorlayer.SensorActuatorManager;

/**
 * The SensibleThings platform itself, which exposes all functionality towards
 * the application developers.
 */
public class SensibleThingsPlatform {

    private DisseminationCore disseminationCore = null;
    private AddInManager addInManager = null;
    private SensorActuatorManager sensorActuatorManager = null;

    /**
     * Initializes the SensibleThings platform. Must be called before using the
     * any other functions. This is now the suggested way to start
     * SensibleThings. It uses normal DHT lookup and RUDP. But switches to Proxy
     * automatically if you are behind NAT.
     *
     * @param listener The SensibleThingsListener, for all callbacks
     */
    public SensibleThingsPlatform(SensibleThingsListener listener) {
        initalize(LookupService.KELIPS, Communication.RUDP);
        //if (isBehindNat())
           // initalize(LookupService.KELIPS, Communication.PROXY_RUDP);
        //else {

        //}
        setGetResponseListener(listener);
        setResolveResponseListener(listener);
        setSetEventListener(listener);
        setGetEventListener(listener);
    }

    /**
     * Initializes the SensibleThings platform with a specific lookup and
     * communication
     *
     * @param lookupServiceTypeClassName Takes a Lookup Service Type, ex. LookupService.DISTRIBUTED
     * @param communicationTypeClassName Takes a Communication Type, ex. Communication.RUDP
     * @param listener                   The SensibleThings listener, for all callbacks
     */
    public SensibleThingsPlatform(String lookupServiceTypeClassName,
                                  String communicationTypeClassName, SensibleThingsListener listener) {
        initalize(lookupServiceTypeClassName, communicationTypeClassName);

        // Listeners
        setGetResponseListener(listener);
        setResolveResponseListener(listener);
        setSetEventListener(listener);
        setGetEventListener(listener);
    }

    /**
     * Initializes the SensibleThings platform with a specific lookup and
     * communication In this constructor you also register the callbacks one by
     * one.
     *
     * @param lookupServiceTypeClassName Takes a Lookup Service Type, ex. LookupService.DISTRIBUTED
     * @param communicationTypeClassName Takes a Communication Type, ex. Communication.RUDP
     * @param getResponseListener        The GET response listener
     * @param resolveResponseListener    The RESOLVE response listener
     * @param getEventListener           The GET event listener
     * @param setEventListener           The SET event listener
     */
    public SensibleThingsPlatform(String lookupServiceTypeClassName,
                                  String communicationTypeClassName,
                                  GetResponseListener getResponseListener,
                                  ResolveResponseListener resolveResponseListener,
                                  GetEventListener getEventListener, SetEventListener setEventListener) {
        initalize(lookupServiceTypeClassName, communicationTypeClassName);

        setGetResponseListener(getResponseListener);
        setResolveResponseListener(resolveResponseListener);
        setGetEventListener(getEventListener);
        setSetEventListener(setEventListener);
    }


    public SensibleThingsPlatform(LookupService lookupService,
                                  Communication communication, SensibleThingsListener listener) {
        disseminationCore = new DisseminationCore(this);
        disseminationCore.useCommunication(communication);
        disseminationCore.useLookupService(lookupService);
        this.addInManager = new AddInManager(this);
        this.sensorActuatorManager = new SensorActuatorManager(this);

        setGetResponseListener(listener);
        setResolveResponseListener(listener);
        setSetEventListener(listener);
        setGetEventListener(listener);
    }

    // New init function with class loader
    private boolean initalize(String lookupServiceTypeClassName,
                              String communicationTypeClassName) {
        try {

            // Create the Core
            disseminationCore = new DisseminationCore(this);

            // Class loader code
            // All communication classes must have a default constructor.
            // long comStart = System.currentTimeMillis();
            Class<?> communicationLoader = Class
                    .forName(communicationTypeClassName);
            Communication communication = (Communication) communicationLoader
                    .newInstance();
            // long comEnd = System.currentTimeMillis();

            // All communication classes must have a
            // constructor(Communication,DisseminationCore)
            // long dissStart = System.currentTimeMillis();
            Class<?> lookupServiceLoader = Class
                    .forName(lookupServiceTypeClassName);
            Constructor<?> lookupServiceConstructor = lookupServiceLoader
                    .getConstructor(Communication.class,
                            DisseminationCore.class);

            LookupService lookupService = (LookupService) lookupServiceConstructor
                    .newInstance(communication, disseminationCore);
            // long dissEnd = System.currentTimeMillis();

            // System.out.println("Communication: " + (comEnd - comStart) +
            // "ms\nLookup: " + (dissEnd - dissStart) +"ms");

            disseminationCore.useCommunication(communication);
            disseminationCore.useLookupService(lookupService);

            this.addInManager = new AddInManager(this);
            this.sensorActuatorManager = new SensorActuatorManager(this);

            // Give stuff some time to converge
            // This timer should be tested...
            // Thread.sleep(3000);


            if (disseminationCore.isInitalized()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Closes down the SensibleThings platform. It unloads all addIn's,
     * disconnects from the LookupService, and lastly closes the Communication
     */
    public void shutdown() {

        addInManager.unloadAllAddIns();
        sensorActuatorManager.disconnectAllSensorActuators();

        if (disseminationCore != null) {
            disseminationCore.shutdown();
        }
    }

    /**
     * Returns the dissemination core, which is used to call primitive functions
     * directly
     *
     * @return the running dissemination core
     */
    public DisseminationCore getDisseminationCore() {

        return disseminationCore;
    }

    /**
     * Returns the add-in manager, which handles loading and unloading of
     * add-ins.
     *
     * @return the add-in manager
     */
    public AddInManager getAddInManager() {

        return addInManager;
    }

    /**
     * Returns the sensor/actuator manager, which handles connecting and
     * disconnecting COAP based sensors
     *
     * @return the sensor/actuator manager
     */
    public SensorActuatorManager getSensorActuatorManager() {

        return sensorActuatorManager;
    }

    /**
     * Returns true if the platform is successfully initialized, started, and
     * running
     *
     * @return true if the platform is running
     */
    public boolean isInitalized() {

        return disseminationCore.isInitalized();
    }

    // Primitive function pass through

    /**
     * The RESOLVE primitive action, which resolves an UCI in the lookupService.
     * Fires off a resolveReponse callback with the answer.
     *
     * @param uci the UCI to be resolved
     */
    public void resolve(String uci) {
        if (disseminationCore
                .haveState(DisseminationCore.DISSEMINATION_CORE_STATE_CONNECTED)) {
            disseminationCore.resolve(uci);
        }

    }

    /**
     * The REGISTER primitive action, which registers an UCI in the
     * lookupService.
     *
     * @param uci the UCI to be registered
     */
    public void register(String uci) {
        disseminationCore.register(uci);
    }

    /**
     * The REGISTER primitive action, which registers an UCI in the
     * lookupService. Password is used to secure UCI ownership
     *
     * @param uci      the UCI to be registered
     * @param password the selected password for the uci
     */
    public void register(String uci, String password) {
        disseminationCore.register(uci, password);
    }

    /**
     * The GET primitive action, which fetches the value from another entity.
     * Fires off a getReponse callback with the answer.
     *
     * @param uci  the UCI to be fetched
     * @param node the end point which has been previously been resolved to
     *             manage the UCI
     */
    public void get(String uci, SensibleThingsNode node) {
        disseminationCore.get(uci, node);
    }

    /**
     * The SET primitive action, which pushes a value to another entity.
     *
     * @param uci   the UCI to be set on the remote entity
     * @param value the value which the UCI shall be set to
     * @param node  the end point which has been previously been resolved to
     *              manage the UCI
     */
    public void set(String uci, String value, SensibleThingsNode node) {
        disseminationCore.set(uci, value, node);
    }

    /**
     * The NOTIFY primitive action, which sends a value back to a previously
     * asking entity. This is the return call for the GetEvent callback.
     *
     * @param node  the SensibleThingsNode which the value should be sent to
     * @param uci   the UCI of the value
     * @param value the actual value of the UCI
     */
    public void notify(SensibleThingsNode node, String uci, String value) {
        disseminationCore.notify(node, uci, value);
    }

    /**
     * Sets the SensibleThingsListener Which contains all other listeners
     *
     * @param listener SensibleThingsListener
     */
    public void setSensibleThingsListener(SensibleThingsListener listener) {
        disseminationCore.setGetResponseListener(listener);
        disseminationCore.setResolveResponseListener(listener);
        disseminationCore.setSetEventListener(listener);
        disseminationCore.setGetEventListener(listener);
    }

    /**
     * Sets the ResolveResponseListener
     *
     * @param listener ResolveResponseListener
     */
    public void setResolveResponseListener(ResolveResponseListener listener) {
        disseminationCore.setResolveResponseListener(listener);
    }

    /**
     * Sets the GetResponseListener
     *
     * @param listener GetResponseListener
     */
    public void setGetResponseListener(GetResponseListener listener) {
        disseminationCore.setGetResponseListener(listener);
    }

    /**
     * Sets the SetEventListener
     *
     * @param listener SetEventListener
     */
    public void setSetEventListener(SetEventListener listener) {
        disseminationCore.setSetEventListener(listener);
    }

    /**
     * Sets the GetEventListener
     *
     * @param listener GetEventListener
     */
    public void setGetEventListener(GetEventListener listener) {
        disseminationCore.setGetEventListener(listener);
    }

    // For the NAT check ONLY
    private InetAddress localAddress = null;

    private boolean isBehindNat() {
        try {
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        // Workaround because Linux is stupid...
                        for (Enumeration<NetworkInterface> en = NetworkInterface
                                .getNetworkInterfaces(); en.hasMoreElements(); ) {
                            NetworkInterface intf = en.nextElement();
                            for (Enumeration<InetAddress> enumIpAddr = intf
                                    .getInetAddresses(); enumIpAddr
                                         .hasMoreElements(); ) {
                                InetAddress inetAddress = enumIpAddr
                                        .nextElement();
                                if (!inetAddress.isLoopbackAddress()
                                        && !inetAddress.isLinkLocalAddress()) {
                                   // if (!(inetAddress instanceof Inet6Address)) { // Remove
                                        // this
                                        // line
                                        // for
                                        // IPV6
                                        // compatability
                                        localAddress = inetAddress;
                                    //}
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            Thread t = new Thread(r);
            t.setName("PlatformInterfaceEnumerator");
            t.start();

            while (localAddress == null) {
                Thread.sleep(1000);
                break;
            }

            if (((localAddress.getAddress()[0] == 0x64) && (localAddress
                    .getAddress()[1] & 0xC0) == 0x40)) { // For Carrier Grade
                // NAT
                return true;
            }
            return localAddress.isSiteLocalAddress();

        } catch (Exception e) {

            return false;
        }
    }

}
