package com.onycom.test.audio;

public class BlockingQueue<E> {
	
	@SuppressWarnings("rawtypes")
	private final Queue queue = new LinkedListQueue();
	private int queueCount = 0;
	
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public int getCount() {
		return queueCount;
	}
	
	@SuppressWarnings("unchecked")
	public void enqueue(E item) {
		synchronized(queue) {
			queueCount++;
			queue.enqueue(item);
			queue.notify();
		}
	}
	
	@SuppressWarnings("unchecked")
	public E dequeue() throws InterruptedException {
		synchronized (queue) {
			while(queue.isEmpty()) {
				queue.wait();
			}
			
			queueCount--;
			return (E) queue.dequeue();
		}
	}
	
	public int size() {
		return queue.size();
	}

}
