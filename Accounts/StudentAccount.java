package Accounts;

import Bank.Bank;

public class StudentAccount extends Account implements Deposit, Withdrawal {

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

    /**
     * Check if the account holder is eligible for a student account based on age (18-25).
     *
     * @return true if eligible, false otherwise.
     */
    private boolean isEligibleForStudentAccount() {
        int currentYear = java.time.Year.now().getValue();
        int age = currentYear - yearOfBirth;
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
    public void adjustAccountBalance(double amount) {
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
     * Deposit funds into the student account. The deposit cannot exceed the deposit limit for student accounts.
     *
     * @param amount - The amount to be deposited.
     * @return true if the deposit is successful, false otherwise.
     */
    @Override
    public boolean cashDeposit(double amount) {
        if (amount > depositLimit()) {
            System.out.println("Deposit exceeded the Maximum Limit");
            return false;
        }
        this.adjustAccountBalance(amount);

        // Add transaction log for the deposit
        this.addNewTransaction(this.getAccountNumber(), Transaction.Transactions.Deposit,
                "Deposited Php " + amount);

        return true;
    }

    /**
     * Withdraw funds from the student account. Withdrawal cannot exceed the available balance or the withdrawal limit.
     *
     * @param amount - The amount to be withdrawn.
     * @return true if the withdrawal is successful, false otherwise.
     */
    @Override
    public boolean withdrawal(double amount) {
        if (amount <= 0 || amount > this.savingsBalance || amount > getBank().getWithdrawLimit()) {
            insufficientBalance();
            return false; // Cannot withdraw more than available balance or withdrawal limit
        }

        // Adjust balance and log transaction
        adjustAccountBalance(-amount);
        addNewTransaction(getAccountNumber(), Transaction.Transactions.Withdraw,
                String.format("Withdraw Php %.2f", amount));

        return true;
    }

    /**
     * Get the account balance statement for this student account.
     *
     * @return a string representing the account balance .
     */
    public String getAccountBalanceStatement() {
        return String.format("StudentAccount{Account Number: %s, Owner: %s, Balance: Php %.2f, Has Perks: %b}",
                this.getAccountNumber(), getOwnerFullName(), this.savingsBalance);
    }


    public double getAccountBalance() {
        return this.savingsBalance;
    }

}
