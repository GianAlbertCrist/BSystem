package Accounts;

import Bank.Bank;
import Processes.Transaction;
import java.util.ArrayList;

/**
 * An abstract account class that has comparators to compare itself with different account objects.
 */
public abstract class Account {
    //A constant bank object associated to this account.
    private final Bank bank;
    //accountNumber - Account number of this account object. Cannot be modified once set.
    private final String accountNumber, ownerFname, ownerLname, ownerEmail;
    private String pin;
    /**
     * Transactions refer to the transaction logs recorded in this account.
     * A transaction is logged based on the following:
     * 1. A successful withdrawal.
     * 2. A successful deposit.
     * 3. A successful payment.
     * 4. A successful fund transfer.
     */
    private final ArrayList<Transaction> transactions;

    //Constructor
    public Account(Bank bank, String accountNumber, String pin, String ownerFname,
                   String ownerLname, String ownerEmail) {
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.ownerFname = ownerFname;
        this.ownerLname = ownerLname;
        this.ownerEmail = ownerEmail;
        this.transactions = new ArrayList<>();
    }

    //Getters
    public String getOwnerFname() {
        return ownerFname;
    }

    public String getOwnerLname() {
        return ownerLname;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getPin() {
        return pin;
    }

    public Bank getBank() {
        return bank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public ArrayList<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public String getOwnerFullName() {
        return String.format("%s %s",this.ownerFname, this.ownerLname);
    }

    /**
     * Add a new transaction log to this account.
     * 
     * @param accountNum – Account number of source account that triggered this transaction
     * @param type – Type of transaction triggered.
     * @param description – Description of the transaction.
     */
    public void addNewTransaction(String accountNum, Transaction.Transactions type, String description) {
        transactions.add(new Transaction(accountNum, type, description));
        
    }

    /**
     * Get all information for every transaction that has been logged into this account.
     *
     * @return A formatted string containing all transaction details.
     */
    public String getTransactionsInfo() {
        // Check if there are no transactions
        if (transactions.isEmpty()) {
            // Return a message indicating no transactions found
            return "No transactions found for this account.";
        }

        // Initialize a StringBuilder to build the transaction log
        StringBuilder transactionLog = new StringBuilder("Transaction History:\n");
        
        // Iterate over each transaction in the transactions list
        for (Transaction transaction : transactions) {
            // Append the transaction details to the log
            transactionLog.append(transaction.toString()).append("\n");
        }
        
        // Return the complete transaction log as a string
        return transactionLog.toString();
        }

        @Override
        public String toString() {
        return String.format("{Owner: %s, Email: %s, Bank: %s, Account Number: %s, Transactions Count: %d}",
                            getOwnerFullName(), ownerEmail, bank.getName(), accountNumber, transactions.size());
    }
}