package Day3.ProducerConsumer;

import java.util.HashMap;

/**
 * a Wikipedia dump 를 읽어서 단어의 빈도수를 계산하는 프로그램<br>
 * 해당 클래스는 단일 스레드에서 페이지를 읽고 단어를 세는 기능을 수행한다.
 **/
public class WordCountV1 {

	private static final HashMap<String, Integer> counts = new HashMap<String, Integer>();

	public static void main(String[] args) {
		Iterable<Page> pages = new Pages(10000, "enwiki.xml");
		for(Page page: pages) {
			Iterable<String> words = new Words(page.getText());
			for(String word: words) {
				countWord(word);
			}
		}
	}

	public static void countWord(String word) {
		Integer currentCount = counts.get(word);
		if (currentCount == null) {
			counts.put(word, 1);
		} else {
			counts.put(word, currentCount + 1);
		}
	}
}
