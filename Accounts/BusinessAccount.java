package Accounts;

import Bank.Bank;
import Processes.*;

public class BusinessAccount extends Account implements Deposit, Withdrawal, FundTransfer {
    private String businessName;
    private final String businessPermitID;
    private double bankAnnualIncome, businessBalance;

    public BusinessAccount(Bank bank, String accountNumber, String pin, String ownerFname,
                           String ownerLname, String ownerEmail, String businessPermitID, String businessName,
                           double bankAnnualIncome, double initialDeposit) {
        super(bank, accountNumber, pin, ownerFname, ownerLname, ownerEmail);
        this.businessPermitID = businessPermitID;
        this.businessName = businessName;
        this.bankAnnualIncome = bankAnnualIncome;
        this.businessBalance = initialDeposit;
    }

    //Getters
    public double getBusinessBalance() {
        return businessBalance;
    }

    public double getBankAnnualIncome() {
        return bankAnnualIncome;
    }

    public String getBusinessPermitID() {
        return businessPermitID;
    }

    public String getBusinessName() {
        return businessName;
    }

    public boolean hasEnoughBalance(double amount) {
        return this.businessBalance >= amount;
    }

    public String getAccountBalanceStatement() {
        return String.format("BusinessAccount{Account Number: %s, Business Name: %s, Permit ID: %s Balance: Php %.2f}",
                this.getAccountNumber(), getBusinessName(), getBusinessPermitID(), this.businessBalance);
    }

    public void adjustAccountBalance(double amount) {
        this.businessBalance += amount;
        if (this.businessBalance < 0) {
            this.businessBalance = 0.0;
        }
    }

    public void insufficientBalance() {
        System.out.println("Warning: Insufficient balance to complete the transaction.");
    }

    @Override
    public boolean cashDeposit(double amount) {
        if (TransactionManager.deposit(this, amount)) {
            adjustAccountBalance(amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean withdrawal(double amount) {
        if (TransactionManager.withdraw(this, amount)) {
            adjustAccountBalance(-amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean transfer(Bank bank, Account account, double amount) throws IllegalAccountType {
        if (!(account instanceof BusinessAccount)) {
            throw new IllegalAccountType("Can only transfer funds to a Business account.");
        }
        return false;
    }

    @Override
    public boolean transfer(Account account, double amount) throws IllegalAccountType {
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
                        amount, account.getAccountNumber(), getBank().getName(), this.getBank().getProcessingFee()));

        account.addNewTransaction(getAccountNumber(), Transaction.Transactions.ReceiveTransfer,
                String.format("Received Php %.2f from %s at %s", amount, this.getAccountNumber(), this.getBank().getName()));

        return true;
    }

    @Override
    public String toString() {
        return String.format("BusinessAccount{Business Balance: %s, Business Permit ID: %s, Business Name: %s, Bank Annual Income",
                                    businessBalance, businessPermitID, businessName, bankAnnualIncome);
    }
}