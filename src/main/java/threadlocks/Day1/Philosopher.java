package threadlocks.Day1;

import java.util.Random;

public class Philosopher extends Thread {
	// // 데드락이 발생하는 코드
	// private Chopstick left, right;
	// private Random random;

	// public Philosopher(Chopstick left, Chopstick right) {
	// 	this.left = left; this.right = right;
	// 	random = new Random();
	// }
	//
	// public void run() {
	// 	try {
	// 		while (true) {
	// 			Thread.sleep(random.nextInt(1000)); // 잠시 생각한다.
	// 			synchronized(left) { // 왼쪽 젓가락을 들어올린다.
	// 				synchronized(right) { // 오른쪽 젓가락을 들어올린다.
	// 					Thread.sleep(random.nextInt(1000)); // 잠시 먹는다.
	// 				}
	// 			}
	// 		}
	// 	} catch (InterruptedException e) {}
	// }

	// 데드락이 발생하지 않는 코드

	private Chopstick first, second;
	private Random random;
	public Philosopher (Chopstick left, Chopstick right) {
		if (left.getId() < right.getId()) {
			first = left;
			second = right;
		} else {
			first = right;
			second = left;
		}
		random = new Random();
	}
	public void run() {
		try {
			while (true) {
				Thread.sleep(random.nextInt(1000)); // 잠시 생각한다.
				synchronized(first) { // 왼쪽 젓가락을 들어올린다.
					synchronized(second) { // 오른쪽 젓가락을 들어올린다.
						Thread.sleep(random.nextInt(1000)); // 잠시 먹는다.
					}
				}
			}
		} catch (InterruptedException e) {}
	}

}

class Chopstick {

	private int id;

	public Chopstick(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}