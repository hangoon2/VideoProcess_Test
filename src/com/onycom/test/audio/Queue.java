package com.onycom.test.audio;

public interface Queue<E> {
	
	public void enqueue(E item);
	public E dequeue();
	public int size();
	public boolean isEmpty();

}
