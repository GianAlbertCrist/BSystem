package Processes;

import Accounts.Account;
import Accounts.BusinessAccount;
import Accounts.CreditAccount;
import Accounts.SavingsAccount;
import Accounts.StudentAccount;
import Bank.Bank;

public class TransactionManager {
    
    /**
     * This function is responsible for depositing an amount into a specified account.
     *
     * @param account The account into which the deposit will be made.
     * @param amount The amount to be deposited.
     *
     * @return A boolean value indicating the success of the deposit operation.
     *         Returns true if the deposit is successful, false otherwise.
     *
     * @throws IllegalArgumentException If the deposit amount exceeds the bank's deposit limit.
     */
    public static boolean deposit(Account account, double amount) {
        // Check if the amount is within the bank's deposit limit
        if (amount > account.getBank().getDepositLimit()) {
            System.out.println("Deposit amount exceeds the bank's limit.");
            return false;
        }
        
        // Adjust the account balance based on the account type
        if (account instanceof SavingsAccount) {
            ((SavingsAccount) account).adjustAccountBalance(amount);
        } else if (account instanceof StudentAccount studentAccount) {
            studentAccount.adjustAccountBalance(amount);
        } else if (account instanceof BusinessAccount businessAccount) {
            businessAccount.adjustAccountBalance(amount);
        }
        
        // Add a new transaction record for the deposit
        account.addNewTransaction(account.getAccountNumber(), Transaction.Transactions.Deposit,
                "Deposited Php " + amount);
        return true;
    }

    /**
     * This function is responsible for withdrawing an amount from a specified account.
     *
     * @param account The account from which the withdrawal will be made.
     * @param amount The amount to be withdrawn.
     *
     * @return A boolean value indicating the success of the withdrawal operation.
     *         Returns true if the withdrawal is successful, false otherwise.
     *         If the withdrawal amount is less than or equal to zero or exceeds the withdrawal limit,
     *         the function will print a warning message and return false.
     *
     * @throws IllegalArgumentException If the withdrawal amount exceeds the bank's withdrawal limit.
     */
    public static boolean withdraw(Account account, double amount) {
        // Check if the amount is valid and within the bank's withdrawal limit
        if (amount <= 0 || amount > account.getBank().getWithdrawLimit()) {
            System.out.println("Warning: Insufficient balance or exceeds withdrawal limit.");
            return false;
        }
        
        // Adjust the account balance based on the account type
        if (account instanceof SavingsAccount savingsAccount) {
            savingsAccount.adjustAccountBalance(-amount);
        } else if (account instanceof StudentAccount studentAccount) {
            studentAccount.adjustAccountBalance(-amount);
        } else if (account instanceof BusinessAccount businessAccount) {
            businessAccount.adjustAccountBalance(-amount);
        }
        
        // Add a new transaction record for the withdrawal
        account.addNewTransaction(account.getAccountNumber(), Transaction.Transactions.Withdraw,
                String.format("Withdraw Php %.2f", amount));
        return true;
    }

    /**
     * This function is responsible for transferring funds from one account to another within the same bank.
     *
     * @param sender The account from which the funds will be transferred.
     * @param recipient The account to which the funds will be transferred.
     * @param amount The amount to be transferred.
     *
     * @return A boolean value indicating the success of the transfer operation.
     *         Returns true if the transfer is successful, false otherwise.
     *         If the transfer amount is less than or equal to zero or exceeds the withdrawal limit,
     *         the function will print a warning message and return false.
     *
     * @throws IllegalAccountType If the sender account is not a SavingsAccount or BusinessAccount.
     */
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

    /**
     * This function is responsible for crediting an amount to a specified credit account.
     *
     * @param account The credit account to which the credit will be applied.
     * @param amount The amount to be credited.
     *
     * @return A boolean value indicating the success of the credit operation.
     *         Returns true if the credit is successful, false otherwise.
     *         If the credit amount is less than or equal to zero or exceeds the credit limit,
     *         the function will print a warning message and return false.
     *
     * @throws IllegalArgumentException If the credit account is not a CreditAccount.
     */
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


    /**
     * This function is responsible for recompensing a specified amount from a credit account.
     *
     * @param account The credit account from which the recompense will be applied.
     * @param amount The amount to be recompensed.
     *
     * @return A boolean value indicating the success of the recompense operation.
     *         Returns true if the recompense is successful, false otherwise.
     *         If the recompense amount is less than or equal to zero or exceeds the current loan balance,
     *         the function will print a warning message and return false.
     *
     * @throws IllegalArgumentException If the account is not a CreditAccount.
     */
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


    /**
     * This function is responsible for processing a payment from a CreditAccount to a SavingsAccount.
     *
     * @param sender The CreditAccount from which the payment will be made.
     * @param recipient The SavingsAccount to which the payment will be received.
     * @param amount The amount to be paid.
     *
     * @return A boolean value indicating the success of the payment operation.
     *         Returns true if the payment is successful, false otherwise.
     *         If the sender is not a CreditAccount or the recipient is not a SavingsAccount,
     *         the function will print a warning message and return false.
     *
     * @throws IllegalArgumentException If the payment amount is less than or equal to zero.
     */
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