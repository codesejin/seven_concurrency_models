package threadlocks.Day2;

import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentSortedList {

	private class Node {
		int value;
		Node prev;
		Node next;
		ReentrantLock lock = new ReentrantLock();

		Node() {}

		Node(int value, Node prev, Node next) {
			this.value = value;
			this.prev = prev;
			this.next = next;
		}
	}

	private final Node head;
	private final Node tail;

	public ConcurrentSortedList() {
		head = new Node();
		tail = new Node();
		head.next = tail;
		tail.prev = head;
	}

	public void insert(int value) {
		Node current = head;
		current.lock.lock();
		Node next = current.next;
		try {
			while (true) {
				next.lock.lock();
				try {
					if (next == tail || next.value < value) {
						Node newNode = new Node(value, current, next);
						next.prev = newNode;
						current.next = newNode;
						return;
					}
				} finally {
					current.lock.unlock();
				}
				current = next;
				next = current.next;
			}
		} finally {
			next.lock.unlock();
		}
	}
	// 리스트에 몇 개의 노드가 존재하는지 세는 메소드
	public int size() {
		Node current = tail;
		int count = 0;

		while (current.prev != head) {
			ReentrantLock lock = current.lock;
			lock.lock();
			try {
				++count;
				current = current.prev;
			} finally {
				lock.unlock();
			}
		}
		return count;
	}
}
