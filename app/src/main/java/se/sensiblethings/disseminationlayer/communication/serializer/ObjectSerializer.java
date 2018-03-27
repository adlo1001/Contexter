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
package se.sensiblethings.disseminationlayer.communication.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import se.sensiblethings.disseminationlayer.communication.Message;
import se.sensiblethings.disseminationlayer.communication.MessageSerializer;

public class ObjectSerializer implements MessageSerializer {

    public byte[] serializeMessage(Message message) {
        ByteArrayOutputStream bos;
        ObjectOutputStream oos;
        GZIPOutputStream gos;
        byte[] byteArray = null;
        try {
            bos = new ByteArrayOutputStream();
            gos = new GZIPOutputStream(bos);
            oos = new ObjectOutputStream(gos);
            oos.writeUnshared(message);            
            oos.flush();            
			oos.close();
			gos.close();
			bos.close();
            byteArray = bos.toByteArray();

        } catch (Exception e) {
        	e.printStackTrace();
        }
        oos = null;
        bos = null;
        gos = null;
        
        
//        System.out.println("***************");        
//        System.out.println("Out MsgType:" + message.getClass());
//        System.out.println("Out MsgLen:" + byteArray.length);
//        System.out.println("***************");        
        
        
        return byteArray;
    }

    public Message deserializeMessage(byte[] byteRepresentation) {
        if (byteRepresentation == null) {
            return null;
        }        

        ByteArrayInputStream bis;
        ObjectInputStream ois;
        GZIPInputStream gis;
        Message message;
        try {

            bis = new ByteArrayInputStream(byteRepresentation);
            gis = new GZIPInputStream(bis);            
            ois = new ObjectInputStream(gis);            
            message = (Message) ois.readUnshared();
            ois.close();
            gis.close();
            bis.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        
//        System.out.println("***************");
//        System.out.println("In MsgType:" + message.getClass());
//        System.out.println("In MsgLen:" + byteRepresentation.length);        
//        System.out.println("***************");        

        return message;
    }
}
