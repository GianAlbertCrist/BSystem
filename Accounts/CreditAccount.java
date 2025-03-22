package Accounts;

import Bank.Bank;
import Processes.Payment;
import Processes.Recompense;
import Processes.TransactionManager;

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
                                    getAccountNumber(), getOwnerFullName(), loanBalance);
    }

    //Getter
    public double getLoan() {
        return this.loanBalance;
    }

    /**
     * Checks if this credit account can do additional credit transactions if the amount to credit will not
     * exceed the credit limit set by the bank associated to this Credit Account.
     *
     * @param amountAdjustment – The amount of credit to be adjusted once the said transaction is
     * processed.
     * @return Flag if this account can continue with the credit transaction.
     */
    private boolean canCredit(double amountAdjustment) {
        return (loanBalance + amountAdjustment) <= getBank().getCreditLimit();
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
        if (canCredit(amount) && TransactionManager.pay(this, account, amount)) {
            return true;
        }
        System.out.println("Payment failed: Exceeds credit limit.");
        return false;
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
       return TransactionManager.recompense(this, amount);
    }

    @Override
    public String toString(){
        return "Credit Account " + super.toString();
    }
}