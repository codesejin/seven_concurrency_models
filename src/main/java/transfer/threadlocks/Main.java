package transfer.threadlocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

public class Main {

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		Banking banking = new Banking();
		System.out.println("[1번] : 입금, [2번] : 출금, [3번] : 이체, [4번] : 계좌 잔액 조회, [5번] : 계좌 목록 조회");
		System.out.println("은행 서비스를 종료하려면 'exit'을 입력하세요.");
		String input = br.readLine();
		while (!input.equals("exit")) {
			switch (input) {
				case "1":
					System.out.println("입금 서비스를 시작합니다.");
					System.out.println("회원 ID와 입금액을 입력하세요. [구분자 : ',']");
					String[] inputs = br.readLine().split(",");
					banking.deposit(inputs[0], BigDecimal.valueOf(Long.parseLong(inputs[1])));
					break;
				case "2":
					System.out.println("출금 서비스를 시작합니다.");
					System.out.println("회원 ID와 출금액을 입력하세요. [구분자 : ',']");
					inputs = br.readLine().split(",");
					banking.withdraw(inputs[0], BigDecimal.valueOf(Long.parseLong(inputs[1])));
					break;
				case "3":
					System.out.println("이체 서비스를 시작합니다.");
					System.out.println("보내는 계좌번호, 받는 계좌번호, 이체액을 입력하세요. [구분자 : ',']");
					inputs = br.readLine().split(",");
					banking.transfer(inputs[0], inputs[1], BigDecimal.valueOf(Long.parseLong(inputs[2])));
					break;
				case "4":
					System.out.println("계좌 잔액 조회 서비스를 시작합니다.");
					System.out.println("회원 ID를 입력하세요.");
					String memberId = br.readLine();
					banking.getBalance(memberId);
					break;
				case "5":
					System.out.println("계좌 목록 조회 서비스를 시작합니다.");
					System.out.println("회원 ID를 입력하세요.");
					memberId = br.readLine();
					banking.getTransactionHistory(memberId);
					break;
				default:
					System.out.printf(">> ERROR : %s %n", "잘못된 입력입니다.");
					break;
			}
			System.out.println("[1번] : 입금, [2번] : 출금, [3번] : 이체, [4번] : 계좌 잔액 조회, [5번] : 계좌 목록 조회");
			System.out.println("은행 서비스를 종료하려면 'exit'을 입력하세요.");
			input = br.readLine();
		}

	}
}
