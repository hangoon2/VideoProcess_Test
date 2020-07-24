package com.onycom.test.exceptions;

@SuppressWarnings("serial")
public class EmptyQueueException extends RuntimeException {

	public EmptyQueueException() {super("queue from an empty queue");}

}