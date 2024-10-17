package transfer.threadlocks;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import transfer.threadlocks.AccountHistory.TransactionType;

public class Account {

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
	public void transfer(Account toAccount, BigDecimal amount) {
		synchronized (this) {
			// 이체액은 계좌 잔액보다 작아야 한다.
			if (isBalanceMoreThan(amount)) {
				throw new IllegalArgumentException("이체액은 계좌 잔액보다 작거나 같아야 합니다.");
			}
			try {
				balance = balance.subtract(amount);
				toAccount.receiveTransfer(this, amount);
				transactionHistory.add(new AccountHistory(TransactionType.TRANSFER, amount, "", LocalDateTime.now()));
			} catch (Exception e) {
				throw new IllegalArgumentException("이체를 실패했습니다. : " + e.getMessage());
			}
		}
	}

	/**
	 * 이체 받기
	 * @param fromAccount 이체한 계좌
	 * @param amount 이체액
	 **/
	public void receiveTransfer(Account fromAccount, BigDecimal amount) {
		synchronized (this) {
			// 계좌 잔액은 20억을 넘길 수 없다.
			if (!isBalanceLessThanMaxBalance(fromAccount, amount)) {
				throw new IllegalArgumentException("계좌 잔액은 20억을 넘을 수 없습니다.");
			}
			balance = balance.add(amount);
		}
		transactionHistory.add(new AccountHistory(TransactionType.TRANSFER, amount, fromAccount.getAccountNumber(),
			LocalDateTime.now()));

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
