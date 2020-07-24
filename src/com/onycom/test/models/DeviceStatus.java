package com.onycom.test.models;

import com.onycom.test.AudioSourceType;

public class DeviceStatus {

	public static final	int DEVICE_STATUS_NOINFO = -1;
	public static final	int DEVICE_STATUS_AVAILABLE = 0;
	public static final	int DEVICE_STATUS_OFFLINE = 1;
	public static final	int DEVICE_STATUS_BUSY = 2;

	public static final int RECORD_STATUS_NOT_RECORDING = 0;
	public static final int RECORD_STATUS_RECORDING = 1;
	public static final int RECORD_STATUS_STOPPING_AND_DOWNLOADING = 2;

	public int videoRecordStatus = RECORD_STATUS_NOT_RECORDING;
	public boolean isSoundOn = false;
	public boolean isDeviceViewSucceeded = false;
	public boolean isShareSucceeded = false;
	public long initRemainingTime = 0;
	public long remainingTime = 0; 
	public boolean isShareEmailSent = false;

	public boolean isUserInputLock = false;

	public String statusNm = null; 

	public boolean doNotSendShareStopMessage = false;

	public boolean doNotTryToReconnect = false;
	
	public AudioSourceType audioSourceType = AudioSourceType.AUDIO_SOURCE_TYPE_ENVYLOGIC;
}
