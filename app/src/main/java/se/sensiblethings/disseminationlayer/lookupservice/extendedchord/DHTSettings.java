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

public class DHTSettings {

	public static long STORED_DATA_TIMEOUT = 1000 * 60 * 2; // 20 minutes
															// //TODO:
															// investigate: 1000
															// *60*20
	public static long MESSAGE_TIMEOUT = 6000; // Milliseconds
	private  static int MAX_TRANSMISSION_RETRIES = 3;
	private static final int MAX_LOGN_HOPCOUNT_HISTORY = 20;
	private static final long CHECKSUCCESSOR_DELAY = 20 * 1000;
	private static final long CHECKSUCCESSORTABLE_DELAY = 10 * 60 * 1000;
	private static final long CHECKPREDECESSOR_DELAY = 20 * 1000;
	private static final long CHECKFINGER_DELAY = 20 * 1000;
	private static final long CHECKBOOTSTRAP_DELAY = 1 * 60 * 1000;
	private boolean debug;
	private long checkbootstrapDelay;
	private long checkfingerDelay;
	private long checkpredecessorDelay;
	private long checksuccessortableDelay;
	private long checksuccessorDelay;
	private int maxTransmissionRetries;
	private long messageTimeout;
	private long storedDataTimeout;
	private int maxLognHopcountHistory;
	private boolean replication;

	
	public DHTSettings() {
		this.checkbootstrapDelay = CHECKBOOTSTRAP_DELAY;
		this.checkfingerDelay = CHECKFINGER_DELAY;
		this.checkpredecessorDelay = CHECKPREDECESSOR_DELAY;
		this.checksuccessorDelay = CHECKSUCCESSOR_DELAY;
		this.checksuccessortableDelay = CHECKSUCCESSORTABLE_DELAY;
		this.messageTimeout = MESSAGE_TIMEOUT;
		this.maxTransmissionRetries = MAX_TRANSMISSION_RETRIES;
		this.maxLognHopcountHistory = MAX_LOGN_HOPCOUNT_HISTORY;
		this.storedDataTimeout = STORED_DATA_TIMEOUT;
		this.debug = false;
		this.setReplication(false);
	}
	
	
	
	/**
	 * @return the storedDataTimeout in ms
	 */
	public final long getStoredDataTimeout() {
		return storedDataTimeout;
	}

	/**
	 * @param storedDataTimeout
	 *            the storedDataTimeout to set
	 */
	public final DHTSettings setStoredDataTimeout(long storedDataTimeout) {
		this.storedDataTimeout = storedDataTimeout;
		return this;
	}

	/**
	 * @return the messageTimeout in ms
	 */
	public final long getMessageTimeout() {
		return messageTimeout;
	}

	/**
	 * @param messageTimeout
	 *            the messageTimeout to set
	 */
	public final DHTSettings setMessageTimeout(long messageTimeout) {
		this.messageTimeout = messageTimeout;
		return this;
	}

	/**
	 * @return the maxTransmissionRetries
	 */
	public final int getMaxTransmissionRetries() {
		return maxTransmissionRetries;
	}

	/**
	 * @param maxTransmissionRetries
	 *            the maxTransmissionRetries to set
	 */
	public final DHTSettings setMaxTransmissionRetries(
			int maxTransmissionRetries) {
		this.maxTransmissionRetries = maxTransmissionRetries;
		return this;
	}

	/**
	 * @return the maxLognHopcountHistory
	 */
	public final int getMaxLognHopcountHistory() {
		return maxLognHopcountHistory;
	}

	/**
	 * @param maxLognHopcountHistory
	 *            the maxLognHopcountHistory to set
	 */
	public final DHTSettings setMaxLognHopcountHistory(
			int maxLognHopcountHistory) {
		this.maxLognHopcountHistory = maxLognHopcountHistory;
		return this;
	}

	/**
	 * @return the checksuccessorDelay in ms
	 */
	public final long getChecksuccessorDelay() {
		return checksuccessorDelay;
	}

	/**
	 * @param checksuccessorDelay
	 *            the checksuccessorDelay to set
	 */
	public final DHTSettings setChecksuccessorDelay(long checksuccessorDelay) {
		this.checksuccessorDelay = checksuccessorDelay;
		return this;
	}

	/**
	 * @return the checksuccessortableDelay in ms
	 */
	public final long getChecksuccessortableDelay() {
		return checksuccessortableDelay;
	}

	/**
	 * @param checksuccessortableDelay
	 *            the checksuccessortableDelay to set
	 */
	public final DHTSettings setChecksuccessortableDelay(
			long checksuccessortableDelay) {
		this.checksuccessortableDelay = checksuccessortableDelay;
		return this;
	}

	/**
	 * @return the checkpredecessorDelay in ms
	 */
	public final long getCheckpredecessorDelay() {
		return checkpredecessorDelay;
	}

	/**
	 * @param checkpredecessorDelay
	 *            the checkpredecessorDelay to set
	 */
	public final DHTSettings setCheckpredecessorDelay(long checkpredecessorDelay) {
		this.checkpredecessorDelay = checkpredecessorDelay;
		return this;
	}

	/**
	 * @return the checkfingerDelay in ms
	 */
	public final long getCheckfingerDelay() {
		return checkfingerDelay;
	}

	/**
	 * @param checkfingerDelay
	 *            the checkfingerDelay to set
	 */
	public final DHTSettings setCheckfingerDelay(long checkfingerDelay) {
		this.checkfingerDelay = checkfingerDelay;
		return this;
	}

	/**
	 * @return the checkbootstrapDelay in ms
	 */
	public final long getCheckbootstrapDelay() {
		return checkbootstrapDelay;
	}

	/**
	 * @param checkbootstrapDelay
	 *            the checkbootstrapDelay to set
	 * @return 
	 */
	public final DHTSettings setCheckbootstrapDelay(long checkbootstrapDelay) {
		this.checkbootstrapDelay = checkbootstrapDelay;
		return this;
	}

	public final boolean isDebug() {
		return debug;
	}
	
	public final DHTSettings setDebug(boolean b){
		debug = b;
		return this;
	}



	public final boolean doReplication() {
		return replication;
	}



	public final DHTSettings setReplication(boolean replication) {
		this.replication = replication;
		return this;
	}

}
