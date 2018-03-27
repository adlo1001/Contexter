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

package se.sensiblethings.disseminationlayer.communication.proxy;

import java.io.Serializable;

import se.sensiblethings.interfacelayer.SensibleThingsNode;

public class ProxyPayload implements Serializable {

    //private static final long serialVersionUID = 3205987051130812191L; //Before session ID
    private static final long serialVersionUID = 2549011646931506469L;
    private byte[] payload;
    private SensibleThingsNode from;
    private SensibleThingsNode to;
    private long id;

    public ProxyPayload(long sessionID, byte[] data, SensibleThingsNode from, SensibleThingsNode to) {
        this.id = sessionID;
        payload = data;
        this.from = from;
        this.to = to;
    }

    public byte[] getMessage() {
        return payload;
    }

    public SensibleThingsNode getFrom() {
        return from;
    }

    public SensibleThingsNode getTo() {
        return to;
    }

    public long getSessionID() {
        return id;
    }

//    public static byte[] serializePayload(ProxyPayload payload) {
//        try {
//            byte[] byteArray;
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                ObjectOutputStream out;
//                out = new ObjectOutputStream(bos);
//                out.writeUnshared(payload);
//                byteArray = bos.toByteArray();
//                out.close();
//            return byteArray;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static ProxyPayload deserializeMessage(byte[] serPayload) {
//        if (serPayload == null) {
//            return null;
//        }
//
//
//        try {
//            ByteArrayInputStream bis = new ByteArrayInputStream(serPayload);
//
//
//            ObjectInputStream in = new ObjectInputStream(bis);
//
//
//            ProxyPayload message = (ProxyPayload) in.readUnshared();
//            bis.close();
//            in.close();
//            return message;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

}
