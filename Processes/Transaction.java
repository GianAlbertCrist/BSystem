package Processes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import Database.JSONDatabase;
import java.util.ArrayList;

/**
 * The Transaction class records details of a specific account transaction.
 * Transactions are immutable once created.
 */
public class Transaction {

    // Enum for transaction types
    public enum Transactions {
        Deposit,
        Withdraw,
        FundTransfer,
        ReceiveTransfer,
        ExternalTransfer,
        Payment,
        Recompense, Credit, ReceivePayment
    }

    /**
     * Account number that triggered this transaction.
     */
    public String accountNumber;
    /**
     * Type of transaction that was triggered.
     */
    public Transactions transactionType;
    /**
     * Description of the transaction.
     */
    public String description;

    private final LocalDateTime timestamp;

    private static final String TRANSACTIONS_FILE = "Database/Transactions.json";

    private static final ArrayList<Transaction> transactions = new ArrayList<>();

    public Transaction(String accountNumber, Transactions transactionType, String description) {
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        registerTransaction();
        }

    public Transaction(String accountNumber, Transactions transactionType, String description, LocalDateTime timestamp) {
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.description = description;
        this.timestamp = timestamp;
        registerTransaction();
    }

    private void registerTransaction() {
        transactions.add(this);
        saveTransactions();
}

    /**
     * Retrieves the timestamp when this transaction occurred.
     *
     * @return Transaction timestamp.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Provides a formatted string representation of this transaction.
     *
     * @return Formatted transaction details.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("Transaction{Time: %s, Source: %s, Type: %s, Description: %s}",
                getTimestamp().format(formatter), accountNumber, transactionType, description);
    }

    public static void saveTransactions() {
        JSONDatabase.saveData(transactions, TRANSACTIONS_FILE);
    }

    public static void loadTransactions() {
        ArrayList<Transaction> loadedTransactions = JSONDatabase.loadData(TRANSACTIONS_FILE, Transaction.class);
        transactions.clear();
        transactions.addAll(loadedTransactions);
    }
}
