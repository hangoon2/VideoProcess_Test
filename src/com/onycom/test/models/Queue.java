package com.onycom.test.models;

interface Queue<E> {

	/**
     * Add the data to the bottom of the queueu
     */
    public void enqueue(E item);
    
    /**
     * Get the item from the head of the queue
     */
    public E dequeue();
    
    /**
     * Get the size of the queue
     */
    public int size();
   
    /**
     * Check if the queue is empty
     */
    public boolean isEmpty();
    
}
