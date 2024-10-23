package threadlocks;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class Downloader extends Thread {
	private InputStream in;
	private OutputStream out;
	private ArrayList<ProgressListener> listeners;

	public Downloader(URL url, String outputFilename) throws IOException {
		in = url.openConnection().getInputStream();
		out = new FileOutputStream(outputFilename);
		listeners = new ArrayList<ProgressListener>();
	}

	public synchronized void addListener(ProgressListener listener) {
		listeners.add(listener);
	}

	public synchronized void removeListener(ProgressListener listener) {
		listeners.remove(listener);
	}

	// // 데드락 발생 -> 외부 메소드가 호출될때 중첩잠금이 발생할 수 있음
	// private synchronized void updateProgress(int n) {
	// 	for (ProgressListener listener: listeners) {
	// 		listener.onProgress(n); // 외부 메서드 호출
	// 	}
	// }

	private void updateProgress(int n) {
		ArrayList<ProgressListener> listenersCopy;
		synchronized(this) {
			listenersCopy = (ArrayList<ProgressListener>) listeners.clone();
		}
		for (ProgressListener listener: listenersCopy) {
			listener.onProgress(n);
		}
	}

	public void run() {
		int n = 0, total = 0;
		byte[] buffer = new byte[1024];

		try {
			while ((n = in.read(buffer)) != -1) {
				out.write(buffer, 0, n);
				total += n;
				updateProgress(total);
			}
			out.flush();
		} catch (IOException e) {
		}
	}
}
