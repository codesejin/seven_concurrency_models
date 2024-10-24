package threadlocks.Day3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServerV2 {
	public static void main(String[] args) throws IOException {
		// 스레드 풀 크기를 시스템의 가용 프로세서 수를 기준으로 설정
		int threadPoolSize = Runtime.getRuntime().availableProcessors() * 2;
		ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize); // 고정 크기의 스레드 풀 생성

		class ConnectionHandler implements Runnable {
			InputStream in;
			OutputStream out;

			ConnectionHandler(Socket socket) throws IOException {
				in = socket.getInputStream();
				out = socket.getOutputStream();
			}

			public void run() {
				try {
					int n;
					byte[] buffer = new byte[1024];
					while ((n = in.read(buffer)) != -1) {
						out.write(buffer, 0, n);
						out.flush();
					}
				} catch (IOException e) {
				}
			}
		}

		ServerSocket server = new ServerSocket(4567);
		System.out.println("Echo server started on port 4567...");

		while (true) {
			try {
				Socket socket = server.accept(); // 클라이언트 연결 대기
				// 스레드를 직접 생성하지 않고 스레드 풀에 작업을 제출
				executor.execute(new ConnectionHandler(socket));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
