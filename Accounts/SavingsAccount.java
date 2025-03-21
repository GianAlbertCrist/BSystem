package Accounts;

import Bank.Bank;
import Processes.*;

/**
 * SavingsAccount class representing a standard savings account with balance tracking.
 * It allows deposits, withdrawals, and fund transfers while enforcing banking rules.
 */
public class SavingsAccount extends Account implements Withdrawal, Deposit, FundTransfer {
    // The current balance of the savings account
    private double balance;
    private TransactionManager transactionManager = new TransactionManager();

    /**
     * Constructor for SavingsAccount.
     *
     * @param bank - The bank associated with this savings account.
     * @param accountNumber - The unique account number.
     * @param ownerFname - Owner's first name.
     * @param ownerLname - Owner's last name.
     * @param ownerEmail - Owner's email address.
     * @param pin - Security PIN for authentication.
     * @param balance - The initial deposit amount.
     * @throws IllegalArgumentException If the initial deposit is below 0.
     */
    public SavingsAccount(Bank bank, String accountNumber, String pin, String ownerFname,
                          String ownerLname, String ownerEmail, double balance) {
        super(bank, accountNumber, pin, ownerFname, ownerLname, ownerEmail);
        if (balance < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative.");
        }
        this.balance = balance;
    }



    /**
     * Adjust the account balance of this savings account.
     * @param amount - Amount to be added or subtracted from the balance.
     */
    public void adjustAccountBalance(double amount) {
        this.balance += amount;
        if (this.balance < 0) {
            this.balance = 0.0;
        }
    }

    /**
     * Deposit some cash into this account using TransactionManager.
     * @param amount – Amount of money to be deposited.
     * @return Flag if transaction is successful or not.
     */
    @Override
    public boolean cashDeposit(double amount) {
        return TransactionManager.deposit(this, amount);
    }

    /**
     * Withdraw an amount of money from this savings account using TransactionManager.
     * @param amount – Amount of money to be withdrawn.
     * @return Flag if transaction is successful or not.
     */
    @Override
    public boolean withdrawal(double amount) {
        return TransactionManager.withdraw(this, amount);
    }

    /**
     * Validates whether this savings account has enough balance to proceed with such a transaction
     * based on the amount that is to be adjusted.
     * 
     * @param amount - Amount of money to be supposedly adjusted from this account’s balance.
     * @return - Flag if transaction can proceed by adjusting the account balance by the amount to be
     * changed.
     */
    private boolean hasEnoughBalance(double amount) {
        return balance >= amount;
    }

    private void insufficientBalance() {
        System.out.println("Warning: Insufficient balance to complete the transaction.");
    }


    /**
     * Transfers an amount of money from this account to another savings account. Is extensively used
     * by the other transfer() method
     * <br><br>
     * Cannot proceed if one of the following is true:
     * <ul>
     *     <li>Insufficient balance from source account.</li>
     *     <li>Recipient account does not exist.</li>
     *     <li>Recipient account is from another bank.</li>
     * </ul>
     * @param account – Account number of recipient
     * @param amount – Amount of money to be supposedly adjusted from this account’s balance.
     * @throws IllegalAccountType Cannot fund transfer when the other account is of type
     * CreditAccount.
     * @return Flag if fund transfer transaction is successful or not.
     */
    @Override
    public boolean transfer(Account account, double amount) throws IllegalAccountType {
        if (!(account instanceof SavingsAccount)) {
            throw new IllegalAccountType("Cannot transfer funds to a CreditAccount.");
        }

        if (!hasEnoughBalance(amount) || amount <= 0 || amount > getBank().getWithdrawLimit()) {
            insufficientBalance();
            return false;
        }

        // Deduct from sender and add to recipient
        adjustAccountBalance(-amount);
        ((SavingsAccount) account).adjustAccountBalance(amount);

        // Log transactions for both accounts
        addNewTransaction(account.getAccountNumber(), Transaction.Transactions.FundTransfer,
                String.format("Transferred Php %.2f to %s", amount, account.getAccountNumber()));
        account.addNewTransaction(getAccountNumber(), Transaction.Transactions.ReceiveTransfer,
                String.format("Received Php %.2f from %s", amount, getAccountNumber()));

        return true;
    }

    /**
     * Transfers an amount of money from this account to another savings account.
     * Should be used when transferring to other banks.
     *
     * @param bank Bank object of the recipient
     * @param account Account number of recipient
     * @param amount Amount of money to be supposedly adjusted from this account's balance
     * @return Flag if fund transfer transaction is successful or not
     * @throws IllegalAccountType Cannot fund transfer when the other account is of type CreditAccount
     */
    @Override
    public boolean transfer(Bank bank, Account account, double amount) throws IllegalAccountType {
        double totalAmount = amount + this.getBank().getProcessingFee();

        if (!hasEnoughBalance(totalAmount) || amount <= 0 || totalAmount > this.getBank().getWithdrawLimit()) {
            insufficientBalance();
            return false; // Insufficient funds or exceeding withdrawal limit
        }

        // Deduct full amount from sender including processing fee
        adjustAccountBalance(-totalAmount);

        // Credit only the transferred amount (not including fee) to recipient
        ((SavingsAccount) account).adjustAccountBalance(amount);

        // Log transactions for both accounts
        addNewTransaction(account.getAccountNumber(), Transaction.Transactions.ExternalTransfer,
                String.format("Transferred Php %.2f to %s at %s (Fee: Php %.2f)", 
                                    amount, account.getAccountNumber(), bank.getName(), this.getBank().getProcessingFee()));

        account.addNewTransaction(getAccountNumber(), Transaction.Transactions.ReceiveTransfer,
                String.format("Received Php %.2f from %s at %s", amount, this.getAccountNumber(), this.getBank().getName()));

        return true;
    }

    @Override
    public String toString (){
        return "Savings Account " + super.toString();
    }
    public double getAccountBalance() {
        return this.balance;
    }
    public String getAccountBalanceStatement() {
        return String.format("SavingsAccount{Account Number: %s, Owner: %s, Balance: Php %.2f}", 
                this.getAccountNumber(), getOwnerFullName(), this.balance);
    }
}