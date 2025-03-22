package Accounts;

import Bank.Bank;
import Processes.*;

public class BusinessAccount extends Account implements Deposit, Withdrawal, FundTransfer {
    private double businessBalance;
    private final String businessPermitID;
    private String businessName;
    private double bankAnnualIncome;
    public static final double minimumInitialDeposit = 50000.0 ;

    public BusinessAccount(Bank bank, String accountNumber, String pin, String ownerFname,
                           String ownerLname, String ownerEmail, String businessPermitID, String businessName,
                           double bankAnnualIncome, double initialDeposit){
        super(bank, accountNumber, pin, ownerFname, ownerLname, ownerEmail);

        if (initialDeposit < minimumInitialDeposit){
            throw new IllegalArgumentException("Initial deposit must be above" + minimumInitialDeposit);
        }
        this.businessPermitID = businessPermitID;
        this.businessName = businessName;
        this.bankAnnualIncome = bankAnnualIncome;
        this.businessBalance = initialDeposit;
    }
    public double getBusinessBalane(){
        return this.businessBalance;
    }

    public String getBusinessPermitID() {
        return this.businessPermitID;
    }

    public String getBusinessName() {
        return this.businessName;
    }

    public boolean hasEnoughBalance(double amount) {
        return this.businessBalance >= amount;
    }

    public String getAccountBalanceStatement() {
        return String.format("SavingsAccount{Account Number: %s, Business Name: %s, Permit ID: %s Balance: Php %.2f}",
                this.getAccountNumber(), getBusinessName(), getBusinessPermitID(), this.businessBalance);
    }


    public double depositLimit() {
        return bankAnnualIncome * 20; // custom Limit for BusinessAccount
    }

    private void adjustAccountBalance(double amount) {
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
        if (TransactionManager.externalTransfer(bank, this, bank, account, amount)) {
            adjustAccountBalance(-amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean transfer(Account account, double amount) throws IllegalAccountType {
        if (TransactionManager.internalTransfer(this, account, amount)) {
            adjustAccountBalance(-amount);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "BusinessAccount{" +
                "businessBalance=" + businessBalance +
                ", businessPermitID='" + businessPermitID + '\'' +
                ", businessName='" + businessName + '\'' +
                ", bankAnnualIncome=" + bankAnnualIncome +
                '}';
    }
}