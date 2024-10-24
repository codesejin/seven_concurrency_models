package Day1;

public class Puzzle {
	static boolean answerReady = false;
	static int answer = 0;
	static Thread t1 = new Thread() {
		public void run() {
			answer = 42;
			answerReady = true;
		}
	};
	static Thread t2 = new Thread() {
		public void run() {
			if (answerReady)
				System.out.println("The meaning of life is: " + answer);
			else
				System.out.println("I don't know the answer");
		}
		// public void run() {
		// 	while (!answerReady) {
		// 			Thread.sleep(100);
		// 		System.out.println("The meaning of life is: " + answer);
		// 	}
		// }
	};

	public static void main(String[] args) throws InterruptedException {
		t1.start(); t2.start();
		t1.join(); t2.join();
	}
}
