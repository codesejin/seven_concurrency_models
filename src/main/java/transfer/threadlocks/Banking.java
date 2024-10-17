package transfer.threadlocks;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Banking {
	// Banking에서 해야할 것.
	private static final BigDecimal MIN_DWT_AMOUNT = BigDecimal.ONE;
	private static final BigDecimal MAX_DWT_AMOUNT = new BigDecimal("100000");

	private Map<String, Account> accounts;

	public Banking() {
		this.accounts = new HashMap<>();
		accounts.put("1", new Account("123-123-123"));
		accounts.put("2", new Account("456-456-456"));
	}

	/**
	 * 계좌 조회
	 * @param memberId 회원 ID
	 * @return 계좌
	 **/
	public Account getAccount(String memberId) {
		Account account = accounts.get(memberId);
		if (account == null) {
			throw new IllegalArgumentException("해당 회원ID에 대한 계좌가 존재하지 않습니다.");
		}
		return account;
	}

	/**
	 * 잔액 조회
	 * @param memberId 회원 ID
	 **/
	public void getBalance(String memberId) {
		Account account = getAccount(memberId);
		System.out.printf("현재 잔액은 : %s 원 입니다 %n", account.getBalance());
	}

	/**
	 * 입금하기
	 * @param memberId 회원 ID
	 * @param amount 입금액
	 */
	public void deposit(String memberId, BigDecimal amount) {
		try {
			if (amount.compareTo(MIN_DWT_AMOUNT) <= 0) {
				throw new IllegalArgumentException("입금액은 0보다 커야 합니다.");
			}
			if (amount.compareTo(MAX_DWT_AMOUNT) > 0) {
				throw new IllegalArgumentException("입금액은 10만원 미만여야 합니다.");
			}
			Account account = getAccount(memberId);
			account.deposit(amount);
		} catch (Exception e) {
			System.out.printf(">> ERROR : %s %n", e.getMessage());
		}
	}

	/**
	 * 출금하기
	 * @param memberId 회원 ID
	 * @param amount 출금액
	 */
	public void withdraw(String memberId, BigDecimal amount) {
		try {
			if (amount.compareTo(MIN_DWT_AMOUNT) <= 0) {
				throw new IllegalArgumentException("출금액은 0보다 커야 합니다.");
			}
			if (amount.compareTo(MAX_DWT_AMOUNT) > 0) {
				throw new IllegalArgumentException("출금액은 10만원 미만여야 합니다.");
			}
			Account account = getAccount(memberId);
			account.withdraw(amount);
		} catch (Exception e) {
			System.out.printf(">> ERROR : %s %n", e.getMessage());
		}
	}

	/**
	 * 이체하기
	 * @param fromId
	 * @param toId
	 * @param amount 이체액
	 * @return true: 이체 성공, false: 이체 실패
	 **/
	public void transfer(String fromId, String toId, BigDecimal amount) {
		try {
			if (amount.compareTo(MIN_DWT_AMOUNT) <= 0) {
				throw new IllegalArgumentException("이체액은 0보다 커야 합니다.");
			}
			if (amount.compareTo(MAX_DWT_AMOUNT) > 0) {
				throw new IllegalArgumentException("이체액은 10만원 미만여야 합니다.");
			}
			Account fromAccount = getAccount(fromId);
			Account toAccount = getAccount(toId);
			fromAccount.transfer(toAccount, amount);
		} catch (Exception e) {
			System.out.printf(">> ERROR : %s %n", e.getMessage());
		}
	}

	public void getTransactionHistory(String memberId) {
		Account account = getAccount(memberId);
		System.out.println(account.getTransactionHistory());
	}
}
