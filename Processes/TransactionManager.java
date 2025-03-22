package Processes;

import Accounts.Account;
import Accounts.BusinessAccount;
import Accounts.CreditAccount;
import Accounts.SavingsAccount;
import Accounts.StudentAccount;
import Bank.Bank;

public class TransactionManager {

    public static boolean deposit(Account account, double amount) {
        if (amount > account.getBank().getDepositLimit()) {
            System.out.println("Deposit amount exceeds the bank's limit.");
            return false;
        }
        if (account instanceof BusinessAccount) {
            return ((BusinessAccount) account).cashDeposit(amount);
        } else if (account instanceof SavingsAccount) {
            return ((SavingsAccount) account).cashDeposit(amount);
        } else if (account instanceof StudentAccount) {
            return ((StudentAccount) account).cashDeposit(amount);
        } else {
            System.out.println("Deposit failed: Unsupported account type.");
            return false;
        }
    }

    public static boolean withdraw(Account account, double amount) {
        if (amount <= 0 || amount > account.getBank().getWithdrawLimit()) {
            System.out.println("Warning: Insufficient balance to complete the transaction.");
            return false;
        }
        if (account instanceof BusinessAccount) {
            return ((BusinessAccount) account).withdrawal(amount);
        } else if (account instanceof SavingsAccount) {
            return ((SavingsAccount) account).withdrawal(amount);
        } else if (account instanceof StudentAccount) {
            return ((StudentAccount) account).withdrawal(amount);
        } else {
            System.out.println("Withdrawal failed: Unsupported account type.");
            return false;
        }
    }

    public static boolean internalTransfer(Account sender, Account recipient, double amount) throws IllegalAccountType {
        if (sender instanceof BusinessAccount) {
            return ((BusinessAccount) sender).transfer(recipient, amount);
        } else if (sender instanceof SavingsAccount) {
            return ((SavingsAccount) sender).transfer(recipient, amount);
        } else {
            System.out.println("Internal transfer failed: Unsupported account type.");
            return false;
        }
    }

    public static boolean externalTransfer(Bank senderBank, Account sender, Bank recipientBank, Account recipient, double amount) throws IllegalAccountType {
        if (sender instanceof BusinessAccount) {
            return ((BusinessAccount) sender).transfer(recipientBank, recipient, amount);
        } else if (sender instanceof SavingsAccount) {
            return ((SavingsAccount) sender).transfer(recipientBank, recipient, amount);
        } else {
            System.out.println("External transfer failed: Unsupported account type.");
            return false;
        }
    }

    public static boolean credit(Account account, double amount) {
        if (amount <= 0) {
            System.out.println("Credit failed: Invalid amount.");
            return false;
        }
        return deposit(account, amount); // Credit is essentially a deposit
    }

    public static boolean recompense(Account account, double amount) {
        if (!(account instanceof CreditAccount)) {
            System.out.println("Recompense failed: Only CreditAccounts can recompense.");
            return false;
        }
        return ((CreditAccount) account).recompense(amount);
    }

    public static boolean pay(Account sender, Account recipient, double amount) {
        if (!(sender instanceof CreditAccount) || !(recipient instanceof SavingsAccount)) {
            System.out.println("Payment failed: CreditAccounts can only pay to SavingsAccounts.");
            return false;
        }
        return ((CreditAccount) sender).pay(recipient, amount);
    }

    public static boolean transfer(BusinessAccount sender, Bank bank, Account account, double amount) throws IllegalAccountType {
        return sender.transfer(bank, account, amount);
    }
}
