package Accounts;

import Bank.Bank;
import Processes.Deposit;
import Processes.Withdrawal;
import Processes.TransactionManager;

public final class StudentAccount extends Account implements Deposit, Withdrawal {

    private double savingsBalance;
    private final int yearOfBirth;  // To calculate age for eligibility
    private final String studentId;

    /**
     * Constructor for StudentAccount.
     * @param bank - The bank associated with this student account.
     * @param accountNumber - The unique account number.
     * @param pin - Security PIN for authentication.
     * @param ownerFname - Owner's first name.
     * @param ownerLname - Owner's last name.
     * @param ownerEmail - Owner's email address.
     * @param yearOfBirth - The year of birth of the student (to check eligibility).
     * @param studentId - The unique student ID.
     * @throws IllegalArgumentException If the student is not eligible (not between 18 and 25 years old).
     */
    public StudentAccount(Bank bank, String accountNumber, String pin, String ownerFname,
                          String ownerLname, String ownerEmail, int yearOfBirth, String studentId) {
        super(bank, accountNumber, pin, ownerFname, ownerLname, ownerEmail);
        this.studentId = studentId;
        this.yearOfBirth = yearOfBirth;
        this.savingsBalance = 0.0;

        // Check age eligibility (18 to 25 years)
        if (!isEligibleForStudentAccount()) {
            throw new IllegalArgumentException("Account holder must be between 18 and 25 years old.");
        }
    }

    //Getters
    public double getSavingsBalance() {
        return savingsBalance;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public String getStudentId() {
        return studentId;
    }

    /**
     * Check if the account holder is eligible for a student account based on age (18-25).
     *
     * @return true if eligible, false otherwise.
     */
    public boolean isEligibleForStudentAccount() {
        int currentYear = java.time.Year.now().getValue();
        int age = currentYear - this.yearOfBirth;
        return age >= 18 && age <= 25;
    }

    /**
     * Get the deposit limit for this student account. It is half of the bank's standard deposit limit.
     *
     * @return the deposit limit for this student account.
     */
    public double depositLimit() {
        return getBank().getDepositLimit() / 2;
    }

    /**
     * Adjust the balance of the student account by the specified amount.
     * If the balance goes below 0, it is reset to 0.
     *
     * @param amount - Amount to be added or subtracted from the balance.
     */
    private void adjustAccountBalance(double amount) {
        this.savingsBalance += amount;
        if (this.savingsBalance < 0) {
            this.savingsBalance = 0.0;
        }
    }

    /**
     * Warns the account holder that the balance is insufficient to complete the transaction.
     */
    public void insufficientBalance() {
        System.out.println("Warning: Insufficient balance to complete the transaction.");
    }

    /**
     * Deposit funds into the student account using TransactionManager.
     *
     * @param amount - The amount to be deposited.
     * @return true if the deposit is successful, false otherwise.
     */
    @Override
    public boolean cashDeposit(double amount) {
        if (TransactionManager.deposit(this, amount)) {
            adjustAccountBalance(amount);
            return true;
        }
        return false;
    }

    /**
     * Withdraw funds from the student account using TransactionManager.
     *
     * @param amount - The amount to be withdrawn.
     * @return true if the withdrawal is successful, false otherwise.
     */
    @Override
    public boolean withdrawal(double amount) {
        if (TransactionManager.withdraw(this, amount)) {
            adjustAccountBalance(-amount);
            return true;
        }
        return false;
    }

    /**
     * Get the account balance statement for this student account.
     *
     * @return a string representing the account balance .
     */
    public String getAccountBalanceStatement() {
        return String.format("StudentAccount{Account Number: %s, Owner: %s, Balance: Php %.2f",
                this.getAccountNumber(), getOwnerFullName(), this.savingsBalance);
    }

    /**
     * Transfers an amount of money from this account to another student account.
     * Cannot proceed if one of the following is true:
     * <ul>
     *     <li>Insufficient balance from source account.</li>
     *     <li>Recipient account does not exist.</li>
     *     <li>Recipient account is from another bank.</li>
     * </ul>
     * @param account – Account number of recipient
     * @param amount – Amount of money to be supposedly adjusted from this account’s balance.
     * @return Flag if fund transfer transaction is successful or not.
     */
    public boolean transfer(StudentAccount account, double amount) {
        if (!isEligibleForStudentAccount()) {
            throw new IllegalArgumentException("Account holder must be between 18 and 25 years old.");
        }

        if (amount <= 0 || amount > this.savingsBalance || amount > getBank().getWithdrawLimit()) {
            insufficientBalance();
            return false;
        }

        // Deduct from sender and add to recipient
        adjustAccountBalance(-amount);
        account.adjustAccountBalance(amount);

        // Log transactions for both accounts
        addNewTransaction(account.getAccountNumber(), Transaction.Transactions.FundTransfer,
                String.format("Transferred Php %.2f to %s", amount, account.getAccountNumber()));
        account.addNewTransaction(getAccountNumber(), Transaction.Transactions.ReceiveTransfer,
                String.format("Received Php %.2f from %s", amount, getAccountNumber()));

        return true;
    }

    public double getAccountBalance() {
        return this.savingsBalance;
    }
}
