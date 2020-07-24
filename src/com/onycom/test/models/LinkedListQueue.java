package com.onycom.test.models;

import com.onycom.test.exceptions.EmptyQueueException;

class LinkedListQueue<E> implements Queue<E> {

    private Entry<E> header = new Entry<E>(null, null, null);
    private Entry<E> tail = new Entry<E>(null, null, null);
    private int size = 0;

    public LinkedListQueue() {
        header.next = tail;
        tail.previous = header;
    }

    /**
     * Check if the queue is empty
     */
    public boolean isEmpty() {
        return header.next == tail;
    }

    /**
     * Add the data to the bottom of the queueu
     */
    public void enqueue(E item) {
        Entry<E> newEntry = new Entry<E>(item, tail, tail.previous);
        tail.previous.next = newEntry;
        tail.previous = newEntry;
        size++;
    }

    /**
     * Get the item from the head of the queue
     */
    public E dequeue() {
        if(isEmpty()){
            throw new EmptyQueueException();
        }
        Entry<E> entry = header.next;
        if (entry.next != null) {
            entry.next.previous = header;
            header.next = entry.next;
        }
        size--;
        return entry.element;
    }

    /**
     * Get the size of the queue
     */
    public int size() {
        return size;
    }

    private static class Entry<E>{
        E element;
        Entry<E> next;
        Entry<E> previous;
        Entry(E element, Entry<E> next, Entry<E> previous){
            this.element = element;
            this.next = next;
            this.previous = previous;
        }
    }
}
