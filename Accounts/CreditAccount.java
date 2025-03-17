package Accounts;

import Bank.Bank;

/**
 * CreditAccount class representing a bank account that operates on credit.
 * It allows credit transactions while ensuring credit limits are enforced.
 */
public class CreditAccount extends Account implements Payment, Recompense {

    private double loanBalance;  // The current outstanding loan

    /**
     * Constructor for CreditAccount.
     *
     * @param bank        The bank associated with this credit account.
     * @param accountNumber The unique account number.
     * @param ownerFname Owner's first name.
     * @param ownerLname Owner's last name.
     * @param ownerEmail       Owner's email address.
     * @param pin         Security PIN for authentication.
     */
    public CreditAccount(Bank bank, String accountNumber, String pin, String ownerFname,
                         String ownerLname, String ownerEmail) {
        super(bank, accountNumber, pin, ownerFname, ownerLname, ownerEmail);
        this.loanBalance = 0.0; // Start with no credit used
    }

    /**
     * Gets the current loan statement.
     *
     * @return Formatted loan statement.
     */
    public String getLoanStatement() {
        return "CreditAccount{" +
                "Account Number='" + accountNumber + '\'' +
                ", Owner='" + ownerFname + " " + ownerLname + '\'' +
                ", Loan Balance=$" + String.format("%.2f", loanBalance) +
                '}';
    }

    /**
     * Checks if the account can take additional credit without exceeding the bank's limit.
     *
     * @param amountAdjustment The amount to be credited.
     * @return True if the credit transaction can proceed, otherwise false.
     */
    public boolean canCredit(double amountAdjustment) {
        double creditLimit = bank.getCreditLimit(); // Retrieve credit limit from the bank
        return (loanBalance + amountAdjustment) <= creditLimit;
    }

    /**
     * Adjusts the loan balance of the credit account.
     *
     * @param amountAdjustment The amount to adjust.
     *                         - Positive values increase the loan balance (credit usage).
     *                         - Negative values decrease the loan balance (repayment).
     */
    public void adjustLoanAmount(double amountAdjustment) {
        this.loanBalance += amountAdjustment;
        if (this.loanBalance < 0) {
            this.loanBalance = 0; // Ensure loan balance never goes negative
        }
    }

    /**
     * Pays a certain amount to a SavingsAccount.
     * CreditAccount **cannot pay another CreditAccount**.
     *
     * @param recipient The target account to pay into.
     * @param amount    The amount to pay.
     * @return True if the payment was successful, false otherwise.
     * @throws IllegalArgumentException If trying to pay to another CreditAccount.
     */
    public boolean pay(Account recipient, double amount) {
        if (!(recipient instanceof SavingsAccount)) {
            throw new IllegalArgumentException("Credit Accounts can only pay to Savings Accounts.");
        }

        // Check if the Credit Account is allowed to increase loan
        if (!canCredit(amount)) {
            System.out.println("Payment failed: Not enough credit available.");
            return false;
        }

        // Increase loan balance (because payment is borrowing money)
        adjustLoanAmount(amount);

        // Add the paid amount to the recipient's balance
        SavingsAccount savingsRecipient = (SavingsAccount) recipient;
        savingsRecipient.adjustAccountBalance(amount);

        // Log the transaction for both accounts
        addNewTransaction(recipient.getAccountNumber(), Transaction.Transactions.PAYMENT,
                "Paid $" + String.format("%.2f", amount) + " to " + recipient.getAccountNumber());

        savingsRecipient.addNewTransaction(this.accountNumber, Transaction.Transactions.RECEIVE_TRANSFER,
                "Received $" + String.format("%.2f", amount) + " from Credit Account " + this.accountNumber);

        System.out.println("Payment successful. New loan balance: $" + loanBalance);
        return true;
    }

    /**
     * Recompense the bank by reducing the recorded loan balance.
     *
     * @param amount The amount to recompense.
     * @return True if successful, false otherwise.
     */
    public boolean recompense(double amount) {
        if (amount <= 0 || amount > loanBalance) {
            return false; // Invalid amount or exceeding owed loan
        }

        // Deduct from the loan balance and log the recompense
        adjustLoanAmount(-amount);
        return true;
    }

    public double getLoan() {
        return this.loanBalance;
    }
}
