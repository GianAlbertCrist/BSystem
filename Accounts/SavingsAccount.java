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
    private void adjustAccountBalance(double amount) {
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
        if (TransactionManager.deposit(this, amount)) {
            adjustAccountBalance(amount);
            return true;
        }
        return false;
    }

    /**
     * Withdraw an amount of money from this savings account using TransactionManager.
     * @param amount – Amount of money to be withdrawn.
     * @return Flag if transaction is successful or not.
     */
    @Override
    public boolean withdrawal(double amount) {
        if (TransactionManager.withdraw(this, amount)) {
            adjustAccountBalance(-amount);
            return true;
        }
        return false;
    }

    /**
     * Transfer funds to another account using TransactionManager.
     * @param account - The recipient account.
     * @param amount - The amount to transfer.
     * @return true if transfer is successful, false otherwise.
     */
    @Override
    public boolean transfer(Account account, double amount) throws IllegalAccountType {
        if (TransactionManager.internalTransfer(this, account, amount)) {
            adjustAccountBalance(-amount);
            return true;
        }
        return false;
    }

    /**
     * Transfer funds to an external bank account using TransactionManager.
     * @param bank - The recipient's bank.
     * @param account - The recipient account.
     * @param amount - The amount to transfer.
     * @return true if transfer is successful, false otherwise.
     */
    @Override
    public boolean transfer(Bank bank, Account account, double amount) throws IllegalAccountType {
        if (TransactionManager.externalTransfer(this.getBank(), this, bank, account, amount)) {
            adjustAccountBalance(-amount);
            return true;
        }
        return false;
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