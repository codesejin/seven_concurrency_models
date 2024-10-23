package threadlocks.Day2;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PhilosopherV2 extends Thread {
	private boolean eating;
	private PhilosopherV2 left;
	private PhilosopherV2 right;
	private ReentrantLock table;
	private Condition condition;
	private Random random;

	public PhilosopherV2(ReentrantLock table) {
		eating = false;
		this.table = table;
		condition = table.newCondition();
		random = new Random();
	}

	public void setLeft(PhilosopherV2 left) { this.left = left; }
	public void setRight(PhilosopherV2 right) { this.right = right; }

	public void run() {
		try {
			while (true) {
				think();
				eat();
			}
		} catch (InterruptedException e) {}
	}

	private void think() throws InterruptedException {
		table.lock();
		try {
			eating = false;
			left.condition.signal();
			right.condition.signal();
		} finally {
			table.unlock();
		}
		Thread.sleep(1000);
	}

	private void eat() throws InterruptedException {
		table.lock();
		try {
			while (left.eating || right.eating) {
				condition.await();
			}
			eating = true;
		} finally {
			table.unlock();
		}
		Thread.sleep(1000);
	}
}
