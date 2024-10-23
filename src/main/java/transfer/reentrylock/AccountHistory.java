package transfer.threadlocks;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountHistory {

	private TransactionType transactionType;
	private BigDecimal amount;
	private LocalDateTime transactionTime;
	private String fromAccount;

	enum TransactionType {
		DEPOSIT, WITHDRAW, TRANSFER
	}

	public AccountHistory(TransactionType transactionType, BigDecimal amount, String fromAccount,
		LocalDateTime transactionTime) {
		this.transactionType = transactionType;
		this.amount = amount;
		this.fromAccount = fromAccount;
		this.transactionTime = transactionTime;
	}

	@Override
	public String toString() {
		return "{" +
			"거래 타입 =" + transactionType +
			", 금액 =" + amount +
			", 거래 시간 =" + transactionTime +
			", 송금인 ='" + fromAccount + '\'' +
			'}';
	}
}
