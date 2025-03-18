package Accounts;

import Bank.Bank;

/**
 * BusinessAccount class representing a business credit account with higher limits
 * and business-focused transactions.
 */
public class BusinessAccount extends CreditAccount {

    private static final double BUSINESS_CREDIT_LIMIT_MULTIPLIER = 2.0; // Business credit is usually higher

    /**
     * Constructor for BusinessAccount.
     *
     * @param bank          The bank associated with this business account.
     * @param accountNumber The unique account number.
     * @param ownerFname    Owner's first name.
     * @param ownerLname    Owner's last name.
     * @param email         Owner's email address.
     * @param pin           Security PIN for authentication.
     * @param initialLoan   Initial loan balance (if any).
     */
    public BusinessAccount(Bank bank, String accountNumber, String ownerFname, String ownerLname,
                           String email, String pin, double initialLoan) {
        super(bank, accountNumber, ownerFname, ownerLname, email, pin);
    }

    /**
     * Gets the credit limit for the Business Account.
     *
     * @return The business credit limit.
     */
    public double getBusinessCreditLimit() {
        return super.getBank().getCreditLimit() * BUSINESS_CREDIT_LIMIT_MULTIPLIER;
    }

    /**
     * Determines whether the business account can take additional credit.
     *
     * @param amount The amount to check.
     * @return True if within credit limit, otherwise false.
     */
    public boolean canBusinessCredit(double amount) {
        return (getLoan() + amount) <= getBusinessCreditLimit();
    }

    /**
     * Makes a business payment to a savings account.
     *
     * @param account The recipient account.
     * @param amount    The amount to pay.
     * @return True if successful, false otherwise.
     */
    @Override
    public boolean pay(Account account, double amount) {
        if (!(account instanceof SavingsAccount savingsRecipient)) {
            throw new IllegalArgumentException("Business accounts can only pay to Savings Accounts.");
        }

        if (!canBusinessCredit(amount)) {
            System.out.println("Payment failed: Business credit limit exceeded.");
            return false;
        }

        adjustLoanAmount(amount); // Increase loan balance (business payments are credit-based)

        savingsRecipient.adjustAccountBalance(amount);

        // Log the transaction
        addNewTransaction(account.getAccountNumber(), Transaction.Transactions.FUNDTRANSFER,
                String.format("Paid Php %.2f to %s (Business Transaction)", amount, account.getAccountNumber()));

        savingsRecipient.addNewTransaction(getAccountNumber(), Transaction.Transactions.FUNDTRANSFER,
                String.format("Received Php %.2f from Business Account %s", amount, getAccountNumber()));

        System.out.println("Business payment successful. New loan balance: Php" + getLoan());
        return true;
    }

    /**
     * Business accounts can recompense higher amounts than standard credit accounts.
     *
     * @param amount The amount to recompense.
     * @return True if successful, false otherwise.
     */
    @Override
    public boolean recompense(double amount) {
        if (amount <= 0 || amount > getLoan()) {
            return false; // Invalid amount or exceeding owed loan
        }

        adjustLoanAmount(-amount); // Deduct from loan balance

        // Log recompense transaction
        addNewTransaction(getAccountNumber(), Transaction.Transactions.COMPENSATION,
                String.format("Recompensed Php %.2f to the bank from Business Account.", amount));

        return true;
    }
    @Override
    public String toString() {
        return String.format("BusinessAccount{Account Number: %s, Owner: %s, Loan Ammount: %s, Business Credit Limit: %s}",
                                    getAccountNumber(), getOwnerFullName(), getLoan(), getBusinessCreditLimit());
    }


}
