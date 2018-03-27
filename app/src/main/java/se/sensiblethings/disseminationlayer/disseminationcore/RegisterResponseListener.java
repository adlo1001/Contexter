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

package se.sensiblethings.disseminationlayer.disseminationcore;

public interface RegisterResponseListener {
	
	
	/**
	 * Called when a UCI is successfully registered
	 * @param uci
	 */
	public void onSuccessfulRegister(String uci);
	/**
	 * Called when a registration is password protected and/or the provided password is wrong
	 * Only the first provided non null password will be used in case of multiple responding listeners
	 * @param uci the uci to be registered
	 * @return a new password, or null to abort
	 */
	public String onRegisterPasswordRequired(String uci);
	/**
	 * Called whenever a registration has failed
	 * @param uci the failed uci
	 * @param error the reason for failing
	 */
	public void onFailedRegister(String uci, String error);
}
