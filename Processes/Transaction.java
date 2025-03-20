package Processes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        Recompense
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

    public Transaction(String accountNumber, Transactions transactionType, String description) {
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.description = description;
        this.timestamp = LocalDateTime.now();
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
}
