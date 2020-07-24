package com.onycom.test.audio;

import java.io.IOException;
import java.io.OutputStream;

public class VDPipedOutputStream extends OutputStream {
	
	private VDPipedInputStream sink;
	
	public VDPipedOutputStream(VDPipedInputStream snk) throws IOException {
		// TODO Auto-generated constructor stub
		connect(snk);
	}
	
	public VDPipedOutputStream() {
		// TODO Auto-generated constructor stub
	}
	
	public synchronized void connect(VDPipedInputStream snk) throws IOException {
		if(snk == null) {
			throw new NullPointerException();
		} else if(sink != null || snk.connected) {
			throw new IOException("Already connected");
		}
		
		sink = snk;
		snk.in = -1;
		snk.out = 0;
		snk.connected = true;
	}

	@Override
	public void write(int b) throws IOException {
		// TODO Auto-generated method stub
		if(sink == null) {
			throw new IOException("Pipe not connected");
		}
		
		sink.receive(b);
	}
	
	public void write(byte b[], int off, int len) throws IOException {
		if(sink == null) {
			throw new IOException("Pipe not connected");
		} else if(b == null) {
			throw new NullPointerException();
		} else if( (off < 0) || (off > b.length) || (len < 0) ) {
			throw new IndexOutOfBoundsException();
		} else if(len == 0) {
			return;
		}
		
		sink.receive(b, off, len);
	}
	
	public synchronized void flush() throws IOException {
		if(sink == null) {
			synchronized (sink) {
				sink.notifyAll();
			}
		}
	}
	
	public void close() throws IOException {
		if(sink != null) {
			sink.receivedLast();
		}
	}

}
