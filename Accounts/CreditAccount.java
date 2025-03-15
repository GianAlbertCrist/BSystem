package Accounts;
import Bank.Bank;

public class CreditAccount extends Account implements Payment, Recompense
{
    private double loan = 0.0;

    public CreditAccount(Bank bank, String accountNum, String ownerFName, String ownerLName, String ownerEmail, String pin)
    {
        super(bank, accountNum, ownerFName, ownerLName, ownerEmail, pin);
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
        if (amount <= 0) {
            System.out.println("Recompense amount must be greater than 0.");
            return false;
        }

        if (!canCredit(-amount)) {
            System.out.println("Recompense amount cannot be greater than current loan of ₱" + this.loan);
            return false;
        }

        adjustLoanAmount(-amount);
        addNewTransaction(getAccountNumber(), Transaction.Transactions.Recompense,
                "Recompensed ₱" + amount + " to bank");

        return true;
    }

    /**
     * Pay an amount of money to a selected account. Such an account cannot be of type
     * CreditAccount.
     *
     * @param account Target account to pay money into.
     * @return True if pay transaction was successful, false otherwise
     * @throws IllegalAccountType Credit Accounts cannot pay to other Credit Accounts
     */

    @Override
    public boolean pay(Account account, double amount) throws IllegalAccountType {
        if (account instanceof CreditAccount) {
            throw new IllegalAccountType("Credit Accounts cannot pay to other Credit Accounts!");
        }

        if (!canCredit(amount)) {
            return false;
        }

        if (account instanceof SavingsAccount savingsAccount) {
            savingsAccount.adjustAccountBalance(amount);
            adjustLoanAmount(amount);

            // Log transactions
            addNewTransaction(getAccountNumber(), Transaction.Transactions.Payment,
                    String.format("Payment to %s: ₱%.2f", account.getAccountNumber(), amount));
            account.addNewTransaction(getAccountNumber(), Transaction.Transactions.Deposit,
                    String.format("Payment from %s: ₱%.2f", getAccountNumber(), amount));

            return true;
        }
        return false;
    }

    /**
     * Loan statement of this credit account.
     *
     * @return String loan statement.
     */
    public String getLoanStatement() {
        return "Current Loan: ₱" + this.loan;
    }

    /**
     * Checks if this credit account can do additional credit transactions if the amount to credit will not
     * exceed the credit limit set by the bank associated to this Credit Account.
     *
     * @param amountAdjustment The amount of credit to be adjusted once the said transaction is
     * processed.
     * @return Flag if this account can continue with the credit transaction.
     */
    public boolean canCredit(double amountAdjustment) {
        return (loan + amountAdjustment) <= getBank().getCreditLimit();
    }


    /**
     * Adjust the owner’s current loan. Result of adjustment cannot be less than 0.
     *
     * @param amountAdjustment Amount to be adjusted to the loan of this credit account.
     */
    public void adjustLoanAmount(double amountAdjustment) {
        double newLoan = loan + amountAdjustment;
        if (newLoan < 0) {
            this.loan = 0.0;
        } else {
            this.loan = newLoan;
        }
    }

    /**
     * String representation of the credit account
     *
     * @return String representation of the credit account
     */
    @Override
    public String toString() {
        return String.format("%s\n%s", super.toString(), getLoanStatement());
    }

    public double getLoan() {
        return loan;
    }
}
