package Accounts;
import Bank.Bank;

public class SavingsAccount extends Account implements Withdrawal, Deposit, FundTransfer
{
    // Balance of this bank account.
    private double balance;

    //Constructor Method.
    public SavingsAccount(Bank bank, String accountNumber, String ownerFName, String ownerLName, 
                          String ownerEmail, String pin, double initialBalance) {
        super(bank, accountNumber, ownerFName, ownerLName, ownerEmail, pin);
        this.balance = initialBalance;
    }

    public double getAccountBalance() {
        return balance;
    }

    /**
     * Withdraws an amount of money using a given medium.
     * @param amount Amount of money to be withdrawn from.
     */
    @Override
    public boolean withdrawal(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be greater than 0.");
            return false;
        }
        if (amount > getBank().getWithdrawLimit()) {
            System.out.println("Withdrawal amount exceeds the bank's limit: ₱" + getBank().getWithdrawLimit());
            return false;
        }
        if (!hasEnoughBalance(amount)) {
            insufficientBalance();
            return false;
        }
        adjustAccountBalance(-amount);
        addNewTransaction(getAccountNumber(), Transaction.Transactions.Withdraw, 
                          "Cash withdrawal: ₱" + amount);
        return true;
    }

    /**
     * Deposit an amount of money to some given account.
     * @param amount Amount to be deposited.
     * @return Flag if transaction is successful or not.
     */
    @Override
    public boolean cashDeposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit amount must be greater than ₱0.");
            return false;
        }

        if (amount > getBank().getCreditLimit()) {
            System.out.println("Deposit amount exceeds the bank's deposit limit of ₱" + getBank().getCreditLimit());
            return false;
        }

        adjustAccountBalance(amount);
        addNewTransaction(getAccountNumber(), Transaction.Transactions.Deposit,
                "Cash deposit: ₱" + amount);

        return true;
    }

    /**
     * Transfer money from one account on the same bank, using the
     * recepient's account number.
     * <br><br>
     * Cannot proceed if one of the following is true:
     * <ul>
     *     <li>Insufficient balance from source account.</li>
     *     <li>Recepient account does not exist.</li>
     *     <li>Recepient account is from another bank.</li>
     * </ul>
     * @param account Accounts.Account number of the recepient.
     * @param amount Amount of money to be transferred.
     * @throws IllegalAccountType This error is thrown depending on the rules set upon. Generally occurs
     * when fund transferring from an incompatible account type.
     */
    @Override
    public boolean transfer(Account account, double amount) throws IllegalAccountType {
        if (account instanceof CreditAccount) {
            throw new IllegalAccountType("Cannot transfer funds to a Credit Account.");
        }
        if (amount <= 0) {
            System.out.println("Transfer amount must be greater than 0.");
            return false;
        }
        if (amount > getBank().getWithdrawLimit()) {
            System.out.println("Transfer amount exceeds the bank's limit: ₱" + getBank().getWithdrawLimit());
            return false;
        }

        double totalDeduction = amount + getBank().getProcessingFee();

        if (!hasEnoughBalance(totalDeduction)) {
            insufficientBalance();
            return false;
        }

        if (account instanceof SavingsAccount) {
            SavingsAccount recipient = (SavingsAccount) account;

            adjustAccountBalance(-totalDeduction);
            recipient.adjustAccountBalance(amount);

            addNewTransaction(getAccountNumber(), Transaction.Transactions.FundTransfer,
                    String.format("Transfer to %s: ₱%.2f", account.getAccountNumber(), amount));
            account.addNewTransaction(getAccountNumber(), Transaction.Transactions.Deposit,
                    String.format("Transfer from %s: ₱%.2f", getAccountNumber(), amount));

                    return true;
                }
                return false;
    }

    /**
     * Transfers an amount of money from this account to another savings account.
     * Should be used when transferring to other banks.
     *
     * @param bank Bank object of the recipient
     * @param account Account number of recipient
     * @param amount Amount of money to be supposedly adjusted from this account's balance
     * @return Flag if fund transfer transaction is successful or not
     * @throws IllegalAccountType Cannot fund transfer when the other account is of type CreditAccount
     */
    @Override
    public boolean transfer(Bank bank, Account account, double amount) throws IllegalAccountType {
        if (account instanceof CreditAccount) {
            throw new IllegalAccountType("Cannot transfer funds to a Credit Account");
        }

        if (amount <= 0) {
            System.out.println("Transfer amount must be greater than 0.");
            return false;
        }

        if (amount > getBank().getWithdrawLimit()) {
            System.out.println("Transfer amount exceeds the bank's withdrawal limit of ₱" + getBank().getWithdrawLimit());
            return false;
        }

        if (!hasEnoughBalance(amount + getBank().getProcessingFee())) {
            System.out.println("Insufficient balance for this fund transfer. Please consider the processing fee.");
            return false;
        }

        if (account instanceof SavingsAccount) {
            SavingsAccount recipient = (SavingsAccount) account;

            // Adjust balances with processing fee
            adjustAccountBalance(-(amount + getBank().getProcessingFee()));
            recipient.adjustAccountBalance(amount);

            // Log transactions
            addNewTransaction(getAccountNumber(), Transaction.Transactions.FundTransfer,
                    String.format("Transfer to %s (Bank: %s): ₱%.2f (Fee: ₱%.2f)",
                            account.getAccountNumber(), bank.getName(), amount, getBank().getProcessingFee()));
            account.addNewTransaction(getAccountNumber(), Transaction.Transactions.Deposit,
                    String.format("Transfer from %s (Bank: %s): ₱%.2f",
                    getAccountNumber(), getBank().getName(), amount));

            return true;
        }

        return false;
    }

    /**
     * Get the account balance statement of this savings account.
     * @return - String balance statement.
     */
    public String getAccountBalanceStatement() {
        return "Current Balance: ₱" + balance;
    }

    /**
     * Validates whether this savings account has enough balance to proceed with such a transaction
     * based on the amount that is to be adjusted.
     * @param amount - Amount of money to be supposedly adjusted from this account’s balance.
     * @return - Flag if transaction can proceed by adjusting the account balance by the amount to be
     * changed.
     */
    public boolean hasEnoughBalance(double amount) {
        return balance >= amount;
    }

    /**
     * Warns the account owner that their balance is not enough for the transaction to proceed
     * successfully.
     */
    public void insufficientBalance() {
        System.out.println("Insufficient balance for the requested transaction.");
    }

    /**
     * Adjust the account balance of this savings account based on the amount to be adjusted. If it
     * results to the account balance going less than 0.0, then it is forcibly reset to 0.0.
     * @param amount - Amount to be added or subtracted from the account balance.
     */
    public void adjustAccountBalance(double amount) {
        this.balance += amount;
        if (balance < 0.0) {
            balance = 0.0;
        }
    }

    /**
     * String representation of the savings account
     *
     * @return String representation of the savings account
     */
    @Override
    public String toString() {
        return String.format("%sn%s", super.toString(), getAccountBalanceStatement());
    }
}
