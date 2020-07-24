/*
 * Copyright Onycom. All Rights Reserved. 
 * 
 * This software is the proprietary information of Onycom. 
 * Use is subject to license terms. 
 */

//package
package com.onycom.test;

public class LinkPacket {
	public LinkPacket() {
		super();
		// TODO Auto-generated constructor stub
	}
	public LinkPacket(short code, byte deviceNo, final byte [] data) {
		super();
		
		this.code = code;
		this.deviceNo = deviceNo;
		
		if (data != null) {
			this.dataSize = data.length;
			this.data = new byte [data.length];
			System.arraycopy(data, 0, this.data, 0, data.length);
		}
	}
	///command code
	public short code = 0;
	///device number. starts from 1
	public byte deviceNo = 0;
	///data length.(bytes)
	public int dataSize = 0;
	///data
	public byte [] data = null;
	///rx/tx time
	public long time = 0;
	///response data
	public LinkPacket response = null;
}
//LinkPacket
