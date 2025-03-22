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
        return TransactionManager.deposit(this, amount);
    }

    @Override
    public boolean withdrawal(double amount) {
        return TransactionManager.withdraw(this, amount);
    }

    @Override
    public boolean transfer(Bank bank, Account account, double amount) throws IllegalAccountType {
        return TransactionManager.externalTransfer(this.getBank(), this, bank, account, amount);
    }

    @Override
    public boolean transfer(Account account, double amount) throws IllegalAccountType {
        return TransactionManager.internalTransfer(this, account, amount);
    }

    @Override
    public String toString() {
        return String.format("BusinessAccount{Business Balance: %s, Business Permit ID: %s, Business Name: %s, Bank Annual Income",
                                    businessBalance, businessPermitID, businessName, bankAnnualIncome);
    }
}