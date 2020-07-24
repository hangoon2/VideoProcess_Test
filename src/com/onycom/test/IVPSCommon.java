package com.onycom.test;

public interface IVPSCommon {
	
	public static final int MAXCHCNT = 8;
	
	public static final byte START_FLAG = 0x7F;
	public static final byte END_FLAG = (byte)0xEF;
	
	public static final int CMD_START = 1;
	public static final int CMD_STOP = 2;
	
	public static final int CMD_RESOURCE_USAGE_NETWORK = 16;
	public static final int CMD_RESOURCE_USAGE_CPU = 17;
	public static final int CMD_RESOURCE_USAGE_MEMORY = 18;
	
	public static final int CMD_CMD_ACK = 10001;
	public static final int CMD_CMD_LOGCAT = 10003;
	
	public static final int CMD_CAPTURE_COMPLETED = 10004;
	
	public static final int CMD_JPG_DATA = 20001;
	public static final int CMD_JPG_DATA_ROTATE = 20011;
	
	public static final int CMD_H264_DATA = 20002;
	public static final int CMD_AUDIO_AAC_DATA = 20003;
	
	public static final int CMD_JPG_DEV_VERT_IMG_VERT = 20004;
	public static final int CMD_JPG_DEV_HORI_IMG_HORI = 20005;
	public static final int CMD_JPG_DEV_VERT_IMG_HORI = 20006;
	public static final int CMD_JPG_DEV_HORI_IMG_VERT = 20007;
	
	public static final int CMD_MIRRORING_STOPPED = 22002;
	public static final int CMD_MIRRORING_JPEG_CAPTURE_FAILED = 32401;
	
	public static final int CMD_DEVICE_DISCONNECTED = 22003;
	
	public static final int CMD_ID = 30000;
	public static final int CMD_ID_AUDIO = 30005;
	public static final int CMD_CONVERTER_MIRACAST_POWER_ONOFF = 30006;
	public static final int CMD_ID_GUEST = 30007;
	public static final int CMD_ID_MONITOR = 30008;
	public static final int CMD_ID_VNC = 30009;
	
	public static final int CMD_PLAYER_QUALITY = 30001;
	public static final int CMD_PLAYER_FPS = 30002;
	public static final int CMD_PLAYER_EXIT = 30010;
	
	public static final int CMD_DISCONNECT_GUEST = 30011;	// 특정 Guest를 종료시킬 목적으로 Host로부터 받음
	public static final int CMD_UPDATE_SERVICE_TIME = 30012;	// Guest 시간 업데이트
	
	public static final int CMD_GUEST_UPDATED = 31001;	// Guest 연결 or 연결 해제
	
	public static final int CMD_MONITOR_VPS_HEARBEAT = 32000;
	public static final int CMD_MONITOR_VIDEO_FPS_STATUS = 32001;
	public static final int CMD_MONITOR_POWER_CONVERTER_STATUS = 32002;
	
	public static final int CMD_MONITOR_VD_HEARTBEAT = 32100;
	
	public static final int CMD_DUPLICATED_CLIENT = 32201;
	
	public static final int CMD_STOP_RECORDING = 1007;
	
	public static final int CMD_VPS_VERSION = 32760;
	
}
