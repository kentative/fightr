package com.bytes.gamr.model.action;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ActionQueue<E> extends AbstractQueue<E> {

	private int capacity;
	private List<E> data;
	
	/**
	 * Constructor
	 * @param capacity - the queue's capacity
	 */
	public ActionQueue(int capacity) {
		this.capacity = capacity;
		this.data = new ArrayList<E>();
	}
	
	
	 /**
     * Inserts the specified element into this queue if it is possible to do
     * so immediately without violating capacity restrictions.
     * When using a capacity-restricted queue, this method is generally
     * preferable to {@link #add}, which can fail to insert an element only
     * by throwing an exception.
     *
     * @param e the element to add
     * @return {@code true} if the element was added to this queue, else
     *         {@code false}
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null and
     *         this queue does not permit null elements
     * @throws IllegalArgumentException if some property of this element
     *         prevents it from being added to this queue
     */
	public boolean offer(E e) {
		
		if (data.size() < capacity) {
			return data.add(e);
		}
		return false;
	}

	/**
     * Retrieves and removes the head of this queue,
     * or returns {@code null} if this queue is empty.
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
	public E poll() {
		
		if (!data.isEmpty()) {
			return data.remove(0);
		}
		return null;
	}

    /**
     * Retrieves, but does not remove, the head of this queue,
     * or returns {@code null} if this queue is empty.
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
	public E peek() {
		int head = data.size() -1;
		if (head >= 0) {
			return data.get(head);
		}
		return null;
	}

	@Override
	public Iterator<E> iterator() {
		return data.iterator();
	}

	@Override
	public int size() {
		return data.size();
	}

    /**
     * Removes all of the elements from this queue.
     * The queue will be empty after this call returns.
     *
     * Override to directly clear the data list
     */
    public void clear() {
    	data.clear();
    }
}
