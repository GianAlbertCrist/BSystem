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
        if (account instanceof SavingsAccount) {
            ((SavingsAccount) account).adjustAccountBalance(amount);
        } else if (account instanceof StudentAccount studentAccount) {
            studentAccount.adjustAccountBalance(amount);
        } else if (account instanceof BusinessAccount businessAccount) {
            businessAccount.adjustAccountBalance(amount);
        }
        account.addNewTransaction(account.getAccountNumber(), Transaction.Transactions.Deposit,
                "Deposited Php " + amount);
        return true;
    }

    public static boolean withdraw(Account account, double amount) {
        if (amount <= 0 || amount > account.getBank().getWithdrawLimit()) {
            System.out.println("Warning: Insufficient balance or exceeds withdrawal limit.");
            return false;
        }
        if (account instanceof SavingsAccount savingsAccount) {
            savingsAccount.adjustAccountBalance(-amount);
        } else if (account instanceof StudentAccount studentAccount) {
            studentAccount.adjustAccountBalance(-amount);
        } else if (account instanceof BusinessAccount businessAccount) {
            businessAccount.adjustAccountBalance(-amount);
        }
        account.addNewTransaction(account.getAccountNumber(), Transaction.Transactions.Withdraw,
                String.format("Withdraw Php %.2f", amount));
        return true;
    }

    public static boolean internalTransfer(Account sender, Account recipient, double amount) throws IllegalAccountType {
        if (amount <= 0 || amount > sender.getBank().getWithdrawLimit()) {
            System.out.println("Transfer failed: Insufficient balance or exceeds withdrawal limit.");
            return false;
        }
        if (sender instanceof SavingsAccount savingsAccount) {
            return savingsAccount.transfer(recipient, amount);
        } else if (sender instanceof BusinessAccount businessAccount) {
            return businessAccount.transfer(recipient, amount);
        }
        System.out.println("Internal transfer failed: Unsupported account type.");
        return false;
    }
    
    public static boolean externalTransfer(Bank senderBank, Account sender, Bank recipientBank, Account recipient, double amount) throws IllegalAccountType {
        double totalAmount = amount + senderBank.getProcessingFee();
        if (amount <= 0 || totalAmount > sender.getBank().getWithdrawLimit()) {
            System.out.println("External transfer failed: Insufficient balance or exceeds withdrawal limit.");
            return false;
        }
        if (sender instanceof SavingsAccount savingsAccount) {
            return savingsAccount.transfer(recipientBank, recipient, amount);
        } else if (sender instanceof BusinessAccount businessAccount) {
            return businessAccount.transfer(recipientBank, recipient, amount);
        }
        System.out.println("External transfer failed: Unsupported account type.");
        return false;
    }
    

    public static boolean credit(Account account, double amount) {
        if (!(account instanceof CreditAccount)) {
            System.out.println("Credit failed: Only CreditAccounts can be credited.");
            return false;
        }
        if (amount <= 0 || amount > account.getBank().getCreditLimit()) {
            System.out.println("Credit failed: Invalid or exceeded credit limit.");
            return false;
        }
        CreditAccount creditAccount = (CreditAccount) account;
        creditAccount.adjustLoanAmount(amount);
        creditAccount.addNewTransaction(creditAccount.getAccountNumber(), Transaction.Transactions.Credit,
                "Credited Php " + amount);
        return true;
    }

    public static boolean recompense(Account account, double amount) {
        if (!(account instanceof CreditAccount)) {
            System.out.println("Recompense failed: Only CreditAccounts can recompense.");
            return false;
        }
        CreditAccount creditAccount = (CreditAccount) account;
        if (amount <= 0 || amount > creditAccount.getLoan()) {
            System.out.println("Recompense failed: Invalid amount or amount exceeds current loan balance.");
            return false;
        }
        creditAccount.adjustLoanAmount(-amount);
        creditAccount.addNewTransaction(creditAccount.getAccountNumber(), Transaction.Transactions.Recompense,
                "Recompensed Php " + amount);
        System.out.println("Recompense successful.");
        return true;
    }

    public static boolean pay(Account sender, Account recipient, double amount) {
        if (!(sender instanceof CreditAccount) || !(recipient instanceof SavingsAccount)) {
            System.out.println("Payment failed: CreditAccounts can only pay to SavingsAccounts.");
            return false;
        }
        CreditAccount creditSender = (CreditAccount) sender;
        SavingsAccount savingsRecipient = (SavingsAccount) recipient;
    
        // Increase the loan balance by the payment amount
        creditSender.adjustLoanAmount(amount);
        savingsRecipient.adjustAccountBalance(amount);
    
        // Add transactions for both accounts
        creditSender.addNewTransaction(savingsRecipient.getAccountNumber(), Transaction.Transactions.Payment,
                "Paid Php " + amount + " to " + savingsRecipient.getAccountNumber());
        savingsRecipient.addNewTransaction(creditSender.getAccountNumber(), Transaction.Transactions.ReceivePayment,
                "Received Php " + amount + " from " + creditSender.getAccountNumber());
    
        System.out.println("Payment successful. New loan balance: Php " + creditSender.getLoan());
        return true;
    }
}