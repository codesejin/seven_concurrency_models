package threadlocks.Day3.ProducerConsumer;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * a Wikipedia dump 를 읽어서 단어의 빈도수를 계산하는 프로그램<br>
 * 해당 클래스는 Producer-Consumer 패턴을 사용하여 페이지를 읽고 단어를 세는 기능을 수행한다.<br>
 * [WordCountV1] 와 차이점은 Producer-Consumer 패턴을 사용해서 페이지를 읽는 작업과 단어를 세는 작업을 분리했다.
 * @see WordCountV1
 **/
public class WordCountV2 {

	public static void main(String[] args) throws Exception {
		ArrayBlockingQueue<Page> queue = new ArrayBlockingQueue<Page>(100);
		HashMap<String, Integer> counts = new HashMap<String, Integer>();

		Thread counter = new Thread(new Counter(queue, counts));
		Thread parser = new Thread(new Parser(queue));
		long start = System.currentTimeMillis();

		counter.start();
		parser.start();
		parser.join();
		queue.put(new PoisonPill());
		counter.join();
		long end = System.currentTimeMillis();
		System.out.println("Elapsed time: " + (end - start) + "ms");

		// for (Map.Entry<String, Integer> e: counts.entrySet()) {
		//   System.out.println(e);
		// }
	}
}
