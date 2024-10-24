package monitor;

public class ConditionSynchronizationExample {

	private boolean isAvailable = false;

	public synchronized void produce() {
		while (isAvailable) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		System.out.println("생산됨");
		isAvailable = true;
		notify();
	}

	public synchronized void consume() {
		while (!isAvailable) {
			try {
				wait(); // Long timeout 을 사용해서 일정 시간동안 기하도록 타임아웃을 설정할 수 있고, 경과하면 스레드는 자동으로 깨어난다.
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		System.out.println("소비됨");
		isAvailable = false;
		notify();
	}

	public static void main(String[] args) {
		ConditionSynchronizationExample example = new ConditionSynchronizationExample();

		new Thread(() -> {
			for (int i = 0; i < 10; i++) {
				example.produce();
			}
		}).start();

		new Thread(() -> {
			for (int i = 0; i < 10; i++) {
				example.consume();
			}
		}).start();
	}
}
