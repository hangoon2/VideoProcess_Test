package com.onycom.test.audio;

import java.io.IOException;
import java.io.InputStream;

public class VDPipedInputStream extends InputStream {
	
	boolean closedByWriter = false;
	volatile boolean closedByReader = false;
	boolean connected = false;
	
	Thread readSide;
	Thread writeSide;
	
	private static final int DEFAULT_PIPE_SIZE = 1024;
	
	protected static final int PIPE_SIZE = DEFAULT_PIPE_SIZE;
	
	protected byte buffer[];
	
	protected int in = -1;
	
	protected int out = 0;
	
	public VDPipedInputStream(VDPipedOutputStream src) throws IOException {
		// TODO Auto-generated constructor stub
		this(src, DEFAULT_PIPE_SIZE);
	}
	
	public VDPipedInputStream(VDPipedOutputStream src, int pipeSize) throws IOException {
		// TODO Auto-generated constructor stub
		initPipe(pipeSize);
		connect(src);
	}
	
	public VDPipedInputStream() {
		// TODO Auto-generated constructor stub
		initPipe(DEFAULT_PIPE_SIZE);
	}
	
	public VDPipedInputStream(int pipeSize) {
		initPipe(pipeSize);
	}
	
	private void initPipe(int pipeSize) {
		if(pipeSize <= 0) {
			throw new IllegalArgumentException("Pipe Size <= 0");
		}
		
		buffer = new byte[pipeSize];
	}
	
	public void connect(VDPipedOutputStream src) throws IOException {
		src.connect(this);
	}
	
	protected synchronized void receive(int b) throws IOException {
		checkStateForReceive();
		
		writeSide = Thread.currentThread();
		
		if(in == out)
			awaitSpace();
		
		if(in < 0) {
			in = 0;
			out = 0;
		}
		
		buffer[in++] = (byte)(b & 0xFF);
		if(in >= buffer.length) {
			in = 0;
		}
	}
	
	synchronized void receive(byte b[], int off, int len) throws IOException {
		checkStateForReceive();
		
		writeSide = Thread.currentThread();
		
		int bytesToTransfer = len;
		while(bytesToTransfer > 0) {
			if(in == out)
				awaitSpace();
			
			int nextTransferAmount = 0;
			if(out < in) {
				nextTransferAmount = buffer.length - in;
			} else if(in < out) {
				if(in == -1) {
					in = out = 0;
					nextTransferAmount = buffer.length - in;
				} else {
					nextTransferAmount = out - in;
				}
			}
			
			if(nextTransferAmount > bytesToTransfer)
				nextTransferAmount = bytesToTransfer;
			
			assert(nextTransferAmount > 0);
			
			System.arraycopy(b, off, buffer, in, nextTransferAmount);
			
			bytesToTransfer -= nextTransferAmount;
			off += nextTransferAmount;
			in += nextTransferAmount;
			
			if(in >= buffer.length) {
				in = 0;
			}
		}
	}
	
	private void checkStateForReceive() throws IOException {
		if(!connected) {
			throw new IOException("Pipe not connected");
		} else if(closedByWriter || closedByReader) {
			throw new IOException("Pipe closed");
		} else if(readSide != null && !readSide.isAlive()) {
			throw new IOException("Read end dead");
		}
	}
	
	private void awaitSpace() throws IOException {
		if(in == out) {
			checkStateForReceive();
			
			in = -1;
			out = 0;
		}
	}
	
	synchronized void receivedLast() {
		closedByWriter = true;
		notifyAll();
	}

	@Override
	public synchronized int read() throws IOException {
		// TODO Auto-generated method stub
		if(!connected) {
			throw new IOException("Pipe not connected");
		} else if(closedByReader) {
			throw new IOException("Pipe closed");
		} else if(writeSide != null && !closedByWriter && in < 0) {
			throw new IOException("Write end dead");
		}
		
		readSide = Thread.currentThread();
		
		int trials = 2;
		while(in < 0) {
			if(closedByWriter) {
				return -1;
			}
			
			if(writeSide != null && --trials < 0) {
				throw new IOException("Pipe broken");
			}
		}
		
		int ret = buffer[out++] & 0xFF;
		if(out >= buffer.length) {
			out = 0;
		}
		
		if(in == out) {
			in = -1;
		}
		
		return ret;
	}
	
	public synchronized int read(byte b[], int off, int len) throws IOException {
		if(b == null) {
			throw new NullPointerException();
		} else if(off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		} else if(len == 0) {
			return 0;
		}
		
		int c = read();
		if(c < 0) {
			return -1;
		}
		
		b[off] = (byte)c;
		int rlen = 1;
		while( (in >= 0) && (len > 1) ) {
			int available;
			
			if(in > out) {
				available = Math.min(buffer.length - out, in - out);
			} else {
				available = buffer.length - out;
			}
			
			if(available > len - 1) {
				available = len - 1;
			}
			
			System.arraycopy(buffer, out, b, off + rlen, available);
			
			out += available;
			rlen += available;
			len -= available;
			
			if(out >= buffer.length) {
				out = 0;
			}
			
			if(in == out) {
				in = -1;
			}
		}
		
		return rlen;
	}
	
	public synchronized int available() throws IOException {
		if(in < 0)
			return 0;
		else if(in == out)
			return buffer.length;
		else if(in > out)
			return in - out;
		else
			return in + buffer.length - out;
	}
	
	public void close() throws IOException {
		closedByReader = true;
		
		synchronized (this) {
			in = -1;
		}
	}

}
