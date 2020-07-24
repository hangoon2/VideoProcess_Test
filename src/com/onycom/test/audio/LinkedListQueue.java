package com.onycom.test.audio;

public class LinkedListQueue<E> implements Queue<E> {
	
	private Entry<E> header = new Entry<E>(null, null, null);
	private Entry<E> tail = new Entry<E>(null, null, null);
	private int size = 0;
	
	public LinkedListQueue() {
		// TODO Auto-generated constructor stub
		header.next = tail;
		tail.previous = header;
	}

	@Override
	public void enqueue(E item) {
		// TODO Auto-generated method stub
		Entry<E> newEntry = new Entry<E>(item, tail, tail.previous);
		tail.previous.next = newEntry;
		tail.previous = newEntry;
		size++;
	}

	@Override
	public E dequeue() {
		// TODO Auto-generated method stub
		if( isEmpty() ) {
			throw new EmptyQueueException();
		}
		
		Entry<E> entry = header.next;
		if(entry.next != null) {
			entry.next.previous = header;
			header.next = entry.next;
		}
		size--;
		
		return entry.element;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return header.next == tail;
	}
	
	private static class Entry<E> {
		E element;
		Entry<E> next;
		Entry<E> previous;
		
		Entry(E element, Entry<E> next, Entry<E> previous) {
			this.element = element;
			this.next = next;
			this.previous = previous;
		}
	}

}
