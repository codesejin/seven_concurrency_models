package transfer.threadlocks;


import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Concurrency {

	public static void main(String[] args) {
		Banking banking = new Banking();
		// 100개의 작업이 완료될 때까지 대기할 CountDownLatch 생성
		CountDownLatch depositLatch = new CountDownLatch(1000);
		ExecutorService executorService = Executors.newFixedThreadPool(10); // 10개의 스레드 풀 생성

		// 1000번 입금 (멀티스레드)
		for (int i = 0; i < 1000; i++) {
			executorService.submit(() -> {
				banking.deposit("1", BigDecimal.valueOf(100000));
				banking.deposit("2", BigDecimal.valueOf(100000));
				depositLatch.countDown(); // 입금이 끝날 때마다 countDown 호출
			});
		}
		// 모든 입금이 끝날 때까지 대기
		try {
			depositLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("입금 후 1번 계좌 잔액 " + banking.getAccount("1").getBalance());
		System.out.println("입금 후 2번 계좌 잔액 " + banking.getAccount("2").getBalance());


		CountDownLatch transferLatch = new CountDownLatch(1000);
		// 1000번 이체 (멀티스레드)
		for (int i = 0; i < 1000; i++) {
			executorService.submit(() -> {
				banking.transfer("1","2", BigDecimal.valueOf(2000));
				// 이체 중간에 지연을 추가하여 동시성 문제 유발 가능성 증가
				try {
					Thread.sleep(10);  // 고의로 지연 추가 (충돌 가능성 높임)
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				banking.transfer("2", "1", BigDecimal.valueOf(2000));

				transferLatch.countDown(); // 입금이 끝날 때마다 countDown 호출
			});
		}
		// 모든 이체가 끝날 때까지 대기
		try {
			transferLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("이체 후 1번 계좌 잔액 " + banking.getAccount("1").getBalance());
		System.out.println("이체 후 2번 계좌 잔액 " + banking.getAccount("2").getBalance());

		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
				executorService.shutdownNow();
			}
		} catch (InterruptedException e) {
			executorService.shutdown();
		}
	}
}
