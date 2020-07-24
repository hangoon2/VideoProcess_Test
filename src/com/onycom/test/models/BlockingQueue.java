package com.onycom.test.models;

@SuppressWarnings("rawtypes")
public class BlockingQueue<E> {
    
    private final Queue queue = new LinkedListQueue();
    private int queueCount = 0;
    private boolean isEnvyLogic = false;
    
    /**
     * Check if the queue is empty
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }
    
    public int getCount(){
        return queueCount;
    }
    
    public void setEnviLogic(boolean isEnvyLogic) {
    	this.isEnvyLogic = isEnvyLogic;
    }
    
    /**
     * This method pushes the item to the end of the
     * queue and then notifies one of the thread
     */
    @SuppressWarnings("unchecked")
	public void enqueue(E item){
    	if(isEnvyLogic) {
    		while (queueCount >= 25) {
				try {
					dequeue();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
    	}
    	
        synchronized(queue){
        	queueCount++;
            queue.enqueue(item);
            queue.notify();
        }
    }
    
    /**
     * Get the item from the head of the queue
     * This operation blocks until either an item is returned
     * or the thread is interrupted, in which case it throws an
     * InterruptedException.
     */
    @SuppressWarnings("unchecked")
	public E dequeue() throws InterruptedException {
        synchronized(queue) {
            while (queue.isEmpty()) {
                queue.wait();
            }
            
            queueCount--;
            return (E) queue.dequeue();
        }
    }
    
    /**
     * Get the size of the queue
     */
    public int size() {
        return queue.size();
    }
    
}
