package Accounts;

import Bank.Bank;

/**
 * CreditAccount class representing a bank account that operates on credit.
 * It allows credit transactions while ensuring credit limits are enforced.
 */
public class CreditAccount extends Account implements Payment, Recompense {

    private double loanBalance;

    /**
     * Constructor for CreditAccount.
     *
     * @param bank - The bank associated with this credit account.
     * @param accountNumber - The unique account number.
     * @param ownerFname - Owner's first name.
     * @param ownerLname - Owner's last name.
     * @param ownerEmail - Owner's email address.
     * @param pin - Security PIN for authentication.
     */
    public CreditAccount(Bank bank, String accountNumber, String pin, String ownerFname,
                         String ownerLname, String ownerEmail) {
        super(bank, accountNumber, pin, ownerFname, ownerLname, ownerEmail);
        this.loanBalance = 0.0;
    }

    /**
     * Loan statement of this credit account.
     * 
     * @return String loan statement.
     */
    public String getLoanStatement() {
        return String.format("CreditAccount{Account Number: %s, Owner: %s, Loan Balance: Php %.2f}",
                                    accountNumber, getOwnerFullName(), loanBalance);
    }

    /**
     * Checks if this credit account can do additional credit transactions if the amount to credit will not
     * exceed the credit limit set by the bank associated to this Credit Account.
     *
     * @param amountAdjustment – The amount of credit to be adjusted once the said transaction is
     * processed.
     * @return Flag if this account can continue with the credit transaction.
     */
    public boolean canCredit(double amountAdjustment) {
        return (loanBalance + amountAdjustment) <= bank.getCreditLimit();
    }

 
    /**
     * Adjust the owner’s current loan. Result of adjustment cannot be less than 0.
     *
     * @param amountAdjustment Amount to be adjusted to the loan of this credit account.
     */
    public void adjustLoanAmount(double amountAdjustment) {
        this.loanBalance += amountAdjustment;
        if (this.loanBalance < 0) {
            this.loanBalance = 0.0;
        }
    }

    /**
     * Pay an amount of money to a selected account. Such an account cannot be of type
     * CreditAccount.
     *
     * @param account Target account to pay money into.
     * @return True if pay transaction was successful, false otherwise
     */
    @Override
    public boolean pay(Account account, double amount) {
        if (!(account instanceof SavingsAccount savingsAccount)) {
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
        savingsAccount.adjustAccountBalance(amount);

        // Log the transaction for both accounts
        addNewTransaction(account.getAccountNumber(), Transaction.Transactions.Payment,
                String.format("Paid Php %.2f to %s", amount, account.getAccountNumber()));

        savingsAccount.addNewTransaction(this.accountNumber, Transaction.Transactions.ReceiveTransfer,
                String.format("Received Php %.2f from Credit Account %s", amount, this.accountNumber));

        System.out.println("Payment successful. New loan balance: Php" + this.loanBalance);
        return true;
    }

    /**
     * Recompense some amount of money to the bank and reduce the value of loan recorded in this
     * account. Must not be greater than the current credit.
     *
     * @param amount Amount of money to be recompensed.
     * @return Flag if compensation was successful.
     */
    @Override
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
