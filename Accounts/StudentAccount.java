package Accounts;

import Bank.Bank;

public class StudentAccount extends SavingsAccount {
    private static final double MAX_WITHDRAWAL_LIMIT = 1000.00;
    /**
     * Constructor for StudentAccount.
     *
     * @param bank          The bank associated with this student account.
     * @param accountNumber The unique account number.
     * @param ownerFname    Owner's first name.
     * @param ownerLname    Owner's last name.
     * @param email         Owner's email address.
     * @param pin           Security PIN for authentication.
     * @param balance       The initial deposit amount.
     */
    public StudentAccount(Bank bank, String accountNumber, String ownerFname, String ownerLname,
                          String email, String pin, double balance) {
        super(bank, accountNumber, ownerFname, ownerLname, email, pin, balance);
    }

    /**
     * Withdraws an amount from this student account.
     *
     * @param amount The amount to withdraw.
     * @return True if withdrawal is successful, false otherwise.
     */
    @Override
    public boolean withdrawal(double amount) {
        if (amount <= 0 || amount > MAX_WITHDRAWAL_LIMIT || !hasEnoughBalance(amount)) {
            System.out.println("Transaction failed: Insufficient balance.");
            return false; // Student accounts have stricter withdrawal limits
        }

        // Adjust balance and log transaction
        adjustAccountBalance(-amount);
        addNewTransaction(this.getAccountNumber(), Transaction.Transactions.WITHDRAWAL,
                "Withdrew $" + String.format("%.2f", amount) + " from Student Account.");
        return true;
    }

    /**
     * Transfers funds to another SavingsAccount (students may have restrictions on fund transfers).
     *
     * @param recipient The recipient account.
     * @param amount    The amount to transfer.
     * @return True if transfer is successful, false otherwise.
     */
    @Override
    public boolean transfer(Account recipient, double amount) {
        if (!(recipient instanceof SavingsAccount)) {
            throw new IllegalArgumentException("Student accounts can only transfer to Savings Accounts.");
        }

        if (!hasEnoughBalance(amount) || amount <= 0 || amount > MAX_WITHDRAWAL_LIMIT) {
            System.out.println("Transaction failed: Insufficient balance.");
            return false; // Student transfers cannot exceed set limits
        }

        // Deduct from sender and add to recipient
        adjustAccountBalance(-amount);
        ((SavingsAccount) recipient).adjustAccountBalance(amount);

        // Log transactions for both accounts
        addNewTransaction(recipient.getAccountNumber(), Transaction.Transactions.FUNDTRANSFER,
                "Transferred $" + String.format("%.2f", amount) + " to " + recipient.getAccountNumber());
        recipient.addNewTransaction(getAccountNumber(), Transaction.Transactions.FUNDTRANSFER,
                "Received $" + String.format("%.2f", amount) + " from Student Account.");
        return true;
    }

    @Override
    public String toString() {
        return "StudentAccount{" +
                "accountNumber='" + getAccountNumber() + '\'' +
                ", owner='" + getOwnerFname() + " " + getOwnerLname() + '\'' +
                ", balance=$" + String.format("%.2f", getAccountBalance()) +
                ", withdrawalLimit=$" + String.format("%.2f", MAX_WITHDRAWAL_LIMIT) +
                '}';
    }

}

