package Bank;

import Accounts.Account;
import Accounts.CreditAccount;
import Accounts.SavingsAccount;
import Main.Field;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * The Bank class represents a banking institution that manages multiple accounts.
 * It enforces banking rules such as deposit/withdrawal limits and credit limits.
 */
public class Bank {
    private String bankName, passcode;
    private final int bankId;
    private final ArrayList<Account> bankAccounts;

    // Banking Limits
    private final double depositLimit, withdrawLimit, creditLimit;
    private final double processingFee;

    /**
     * Constructor for Bank.
     *
     * @param bankName The name of the bank.
     * @param bankId   The unique identifier for the bank.
     */
    public Bank(int bankId, String bankName, String passcode) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.passcode = passcode;
        this.bankAccounts = new ArrayList<>();

        // Default banking limits
        this.depositLimit = 50000.0;
        this.withdrawLimit = 50000.0;
        this.creditLimit = 100000.0;
        this.processingFee = 10.0;
    }

    public Bank(int bankId, String bankName, String passcode, double depositLimit, double withdrawLimit, double creditLimit, double processingFee) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.passcode = passcode;
        this.bankAccounts = new ArrayList<>();

        this.depositLimit = depositLimit;
        this.withdrawLimit = withdrawLimit;
        this.creditLimit = creditLimit;
        this.processingFee = processingFee;
    }

    public <T extends Account> void showAccounts(Class<T> accountType) {
        if (accountType == null) {
            for (Account account : bankAccounts) {
                System.out.println(account);
            }
        } else {
            System.out.println("Showing accounts of type: " + accountType.getSimpleName());

            for (Account account : bankAccounts) {
                if (accountType.isInstance(account)) {
                    System.out.println(account);
                }
            }
        }
    }

    /**
     * Retrieves an account from this bank using an account number.
     *
     * @param accountNum The account number to search for.
     * @return The account if found, otherwise null.
     */
    public Account getBankAccount(String accountNum) {
        for (Account account : bankAccounts) { // Assuming `accounts` is a list of accounts
            if (account.getAccountNumber().equals(accountNum)) {
                return account;
            }
        }
        return null; // Return null if not found
    }

    /**
     * Creates a new account with validated input fields.
     *
     * @return A list of Field objects containing account details.
     */
    public ArrayList<Field<?, ?>> createNewAccount() {
        ArrayList<Field<?, ?>> accountFields = new ArrayList<>();

        // Create fields with appropriate validation
        Field<String, Integer> accountNumberField = new Field<String, Integer>("Account Number", String.class, 5, new Field.StringFieldLengthValidator());

        Field<String, Integer> pinField = new Field<String, Integer>("PIN", String.class, 3, new Field.StringFieldLengthValidator());

        Field<String, String> firstNameField = new Field<String, String>("First Name", String.class, null, new Field.StringFieldValidator());

        Field<String, String> lastNameField = new Field<String, String>("Last Name", String.class, null, new Field.StringFieldValidator());

        Field<String, String> emailField = new Field<String, String>("Email", String.class, null, new Field.StringFieldValidator());


        // Array of fields to prompt user input
        Field<?, ?>[] fields = {accountNumberField, pinField, firstNameField, lastNameField, emailField};

        for (Field<?, ?> field : fields) {
            field.setFieldValue("Enter " + field.getFieldName() + ": ");
            accountFields.add(field);
        }

        return accountFields;
    }

    /**
     * Creates and registers a new SavingsAccount using validated fields.
     *
     * @return The newly created SavingsAccount.
     */
    public SavingsAccount createNewSavingsAccount() {
        ArrayList<Field<?, ?>> accountData = createNewAccount();
        String accountNumber = (String) accountData.get(0).getFieldValue();
        String pin = (String) accountData.get(1).getFieldValue();
        String firstName = (String) accountData.get(2).getFieldValue();
        String lastName = (String) accountData.get(3).getFieldValue();
        String email = (String) accountData.get(4).getFieldValue();

        // Use Main.prompt() instead of new Scanner(System.in)
        double initialDeposit = Double.parseDouble(Main.Main.prompt("Enter Initial Deposit: ", true));

        SavingsAccount newAccount = new SavingsAccount(this, accountNumber, pin, firstName, lastName, email, initialDeposit);
        addNewAccount(newAccount);
        return newAccount;
    }

    /**
     * Creates and registers a new CreditAccount using validated fields.
     *
     * @return The newly created CreditAccount.
     */
    public CreditAccount createNewCreditAccount() {
        ArrayList<Field<?, ?>> accountData = createNewAccount();
        String accountNumber = (String) accountData.get(0).getFieldValue();
        String pin = (String) accountData.get(1).getFieldValue();
        String firstName = (String) accountData.get(2).getFieldValue();
        String lastName = (String) accountData.get(3).getFieldValue();
        String email = (String) accountData.get(4).getFieldValue();

        CreditAccount newAccount = new CreditAccount(this, accountNumber, pin, firstName, lastName, email);
        addNewAccount(newAccount);
        return newAccount;
    }

    /**
     * Adds a new account to the bank if the account number is unique.
     *
     * @param account The account to add.
     */
    public void addNewAccount(Account account) {
        if (accountExists(this, account.getAccountNumber())) { // Only check within the same bank
            System.out.println("Account number already exists in this bank! Registration failed.");
            return;
        }
        bankAccounts.add(account);
        System.out.println("✅ Account successfully registered.");
    }

    /**
     * Checks if an account exists in the given bank based on an account number.
     * This method is static because it does not require an instance of the Bank class.
     *
     * @param bank        The bank to search for the account.
     * @param accountNum  The account number to check.
     * @return true if the account exists, false otherwise.
     */
    public static boolean accountExists(Bank bank, String accountNum) {
        if (bank == null || bank.getBankAccounts() == null) {
            return false; // Handle null cases safely
        }

        return bank.getBankAccounts().stream()
                .anyMatch(account -> account.getAccountNumber().equals(accountNum));
    }

    // ========================= GETTERS =========================

    public String getName() {
        return bankName;
    }

    public int getBankId() {
        return this.bankId;
    }

    public String getPasscode() {
        return passcode;
    }

    public ArrayList<Account> getBankAccounts() {
        return new ArrayList<>(bankAccounts); // Return a copy to prevent external modification
    }

    public double getDepositLimit() {
        return depositLimit;
    }

    public double getWithdrawLimit() {
        return withdrawLimit;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public double getProcessingFee() {
        return processingFee;
    }

    // ========================= COMPARATORS =========================

    @Override
    public String toString() {
        return "Bank{" +
                "Bank ID='" + bankId + '\'' +
                "Bank Name='" + bankName + '\'' +
                ", Bank Passcode='" + passcode + '\'' +
                ", Accounts Registered=" + bankAccounts.size() +
                '}';
    }

    public static class BankCredentialsComparator implements Comparator<Bank> {
        @Override
        public int compare(Bank b1, Bank b2) {
            BankComparator name = new BankComparator();
            int compareName = name.compare(b1, b2);

            if (compareName != 0) {
                return compareName;
            }

            return Integer.compare(b1.getBankId(), b2.getBankId());
        }
    }

    public static class BankIdComparator implements Comparator<Bank> {
        @Override
        public int compare(Bank b1, Bank b2) {
            return Integer.compare(b1.getBankId(), b2.getBankId());
        }
    }

    public static class BankComparator implements Comparator<Bank> {
        @Override
        public int compare(Bank b1, Bank b2) {
            return b1.getName().compareTo(b2.getName());
        }
    }
}