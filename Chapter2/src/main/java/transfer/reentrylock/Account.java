package transfer.reentrylock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import transfer.reentrylock.AccountHistory.TransactionType;

public class Account {
	private Lock lock = new ReentrantLock();

	private static final BigDecimal MAX_ACCOUNT_BALANCE = new BigDecimal("200000000");
	private String accountNumber;
	private BigDecimal balance;
	private List<AccountHistory> transactionHistory;

	public Account(String accountNumber) {
		this.accountNumber = accountNumber;
		this.balance = BigDecimal.ZERO;
		this.transactionHistory = new ArrayList<>();
	}

	/**
	 * 입금하기
	 * - 계좌 잔액은 20억을 넘길 수 없다.
	 * @param amount 입금액
	 **/
	public void deposit(BigDecimal amount) {
		synchronized (this) {
			// 계좌 잔액은 20억을 넘길 수 없다.
			if (!isBalanceLessThanMaxBalance(this, amount)) {
				throw new IllegalArgumentException("계좌 잔액은 20억을 넘을 수 없습니다.");
			}
			balance = balance.add(amount);
		}
		transactionHistory.add(new AccountHistory(TransactionType.DEPOSIT, amount, "", LocalDateTime.now()));
	}

	/**
	 * 계좌 잔액은 20억을 넘길 수 없다.
	 * @param amount 입금액, 출금액, 이체액
	 * @return true: 20억을 넘지 않는 경우, false: 20억을 넘는 경우
	 */
	private boolean isBalanceLessThanMaxBalance(Account account, BigDecimal amount) {
		BigDecimal accumulatedBalance = account.balance.add(amount);
		if (accumulatedBalance.compareTo(MAX_ACCOUNT_BALANCE) > 0) {
			return false;
		}
		return true;
	}

	/**
	 * 출금하기
	 * @param amount 출금액
	 **/
	public void withdraw(BigDecimal amount) {
		synchronized (this) {
			// 출금액은 계좌 잔액보다 작아야 한다.
			if (isBalanceMoreThan(amount)) {
				throw new IllegalArgumentException("출금액은 계좌 잔액보다 작거나 같아야 합니다.");
			}
			balance = balance.subtract(amount);
		}
		transactionHistory.add(new AccountHistory(TransactionType.WITHDRAW, amount, "", LocalDateTime.now()));

	}

	/**
	 * 출금액 또는 이체액은 계좌잔액 보다 작거나 같아야 한다.
	 * @param amount 출금액, 이체액
	 * @return true: 출금액 또는 이체액이 계좌잔액보다 작거나 같은 경우, false: 출금액 또는 이체액이 계좌잔액보다 큰 경우
	 **/
	private boolean isBalanceMoreThan(BigDecimal amount) {
		if (balance.compareTo(amount) >= 0) {
			return false;
		}
		return true;
	}

	/**
	 * 이체하기
	 * @param toAccount 이체할 계좌
	 * @param amount 이체액
	 **/
	public void transfer(Account toAccount, BigDecimal amount) throws InterruptedException {
		Account firstLock = this;
		Account secondLock = toAccount;

		if (this.getAccountNumber().compareTo(toAccount.getAccountNumber()) > 0) {
			firstLock = toAccount;
			secondLock = this;
		}

		if (firstLock.lock.tryLock(1, TimeUnit.SECONDS)) { // 타임아웃 지정
			try {
				if (secondLock.lock.tryLock(1, TimeUnit.SECONDS)) {
					try {
						// 이체 로직
						balance = balance.subtract(amount);
						toAccount.receiveTransfer(this, amount);
						transactionHistory.add(new AccountHistory(TransactionType.TRANSFER, amount, "", LocalDateTime.now()));
					} finally {
						secondLock.lock.unlock();
					}
				} else {
					throw new IllegalStateException("수신 계좌의 락을 획득할 수 없습니다.");
				}
			} finally {
				firstLock.lock.unlock();
			}
		} else {
			throw new IllegalStateException("송신 계좌의 락을 획득할 수 없습니다.");
		}
	}


	/**
	 * 이체 받기
	 * @param fromAccount 이체한 계좌
	 * @param amount 이체액
	 **/
	public void receiveTransfer(Account fromAccount, BigDecimal amount) throws InterruptedException {
		// 수신 계좌 락을 타임아웃 기반으로 시도
		if (lock.tryLock(1, TimeUnit.SECONDS)) {
			try {
				// 계좌 잔액이 20억을 넘지 않는지 확인
				if (!isBalanceLessThanMaxBalance(this, amount)) {
					throw new IllegalArgumentException("계좌 잔액은 20억을 넘을 수 없습니다.");
				}
				balance = balance.add(amount);
				transactionHistory.add(new AccountHistory(TransactionType.TRANSFER, amount, fromAccount.getAccountNumber(),
					LocalDateTime.now()));
			} finally {
				lock.unlock();
			}
		} else {
			throw new IllegalStateException("수신 계좌의 락을 획득할 수 없습니다.");
		}
	}


	/**
	 * 송신 계좌 잔액 롤백
	 * 수신 계좌에 돈을 보내는데 실패하면 송신 계좌에 다시 원래 금액을 추가
	 * @param originalBalance 원래 잔액
	 **/
	private void rollbackBalance(BigDecimal originalBalance) {
		synchronized (this) {
			balance = originalBalance;
		}
	}

	public synchronized BigDecimal getBalance() {
		return balance;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * 거래내역 조회
	 * @return 거래내역
	 **/
	public List<AccountHistory> getTransactionHistory() {
		return transactionHistory;
	}
}
