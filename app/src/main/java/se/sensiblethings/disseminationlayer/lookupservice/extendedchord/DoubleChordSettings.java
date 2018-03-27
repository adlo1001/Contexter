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

public class DoubleChordSettings {

	public static final long MAX_HOPCOUNT_FOR_RELAY = 50; 	// Assuming near logarithmic
															// efficiency make
															// 50 correspond to
															// 1*10^20 number of
															// nodes in the
															// system
	public static final long MAINTENANCE_DELAY = 20000;
	public static final long CACHE_TIMEOUT = 90*1000;
	public static final boolean DEBUG = false;
	public static final boolean FILEDEBUG = false;

}
