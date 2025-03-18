package Accounts;

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

    private final String sourceAccount;
    private final Transactions transactionType;
    private final String description;
    private final LocalDateTime timestamp;

    public Transaction(String sourceAccount, Transactions transactionType, String description) {
        this.sourceAccount = sourceAccount;
        this.transactionType = transactionType;
        this.description = description;
        this.timestamp = LocalDateTime.now(); // Auto-generate timestamp upon creation
    }

    /**
     * Retrieves the account number that initiated this transaction.
     *
     * @return Source account number.
     */
    public String getSourceAccount() {
        return sourceAccount;
    }

    /**
     * Retrieves the type of this transaction.
     *
     * @return Transaction type.
     */
    public Transactions getTransactionType() {
        return transactionType;
    }

    /**
     * Retrieves the description of this transaction.
     *
     * @return Transaction description.
     */
    public String getDescription() {
        return description;
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
                getTimestamp().format(formatter), getSourceAccount(), getTransactionType(), getDescription());
    }
}
