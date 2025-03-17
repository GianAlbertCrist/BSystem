package Accounts;

import Bank.Bank;
import java.util.ArrayList;

/**
 * Abstract Account class that serves as a base for different account types.
 * It includes personal details, basic account attributes, comparators, and transaction logging.
 */
public abstract class Account {

    protected final Bank bank;
    protected final String accountNumber;
    protected final ArrayList<Transaction> transactions;
    protected final String ownerFname, ownerLname, ownerEmail;
    protected final String pin;

    /**
     * Constructor for an Account.
     *
     * @param bank          The bank associated with this account.
     * @param accountNumber The unique account number.
     * @param ownerFname    Owner's first name.
     * @param ownerLname    Owner's last name.
     * @param ownerEmail         Owner's email address.
     * @param pin           Security PIN (in real-world, store hashed PINs).
     */
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

    public String getOwnerFullName() {
        return String.format("%s %s",this.ownerFname, this.ownerLname);
    }

    /**
     * Adds a new transaction log to this account.
     *
     * @param sourceAccount The source account number that triggered this transaction.
     * @param type          The type of transaction performed.
     * @param description   A brief description of the transaction.
     */
    public void addNewTransaction(String sourceAccount, Transaction.Transactions type, String description) {
        transactions.add(new Transaction(sourceAccount, type, description));
    }

    /**
     * Retrieves all transaction logs recorded for this account.
     *
     * @return A formatted string containing all transaction details.
     */
    public String getTransactionsInfo() {
        if (transactions.isEmpty()) {
            return "No transactions found for this account.";
        }

        StringBuilder transactionLog = new StringBuilder("Transaction History:\n");
        for (Transaction transaction : transactions) {
            transactionLog.append(transaction.toString()).append("\n");
        }
        return transactionLog.toString();
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

    /**
     * Provides a string representation of the account details.
     *
     * @return Formatted account details.
     */
    @Override
    public String toString() {
        return String.format("Account{Owner: %s, Email: %s, Bank: %s, Account Number: %s, Transactions Count: %d}",
                                    getOwnerFullName(), ownerEmail, bank.getName(), accountNumber, transactions.size());
    }
}