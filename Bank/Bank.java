package Bank;

import Accounts.*;
import Main.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * The Bank class represents a banking institution that manages multiple accounts.
 * It enforces banking rules such as deposit/withdrawal limits and credit limits.
 */
public class Bank {
    private String bankName, passcode;
    private final int bankId;
    /**
     * depositLimit - The amount of money each Savings Account registered to this bank can deposit at every
     * transaction. Defaults to 50,000.0
     * withdrawLimit - The amount of money withdrawal / transferable at once, restricted to every Savings Account
     * registered to this bank. Defaults to 50,000.0
     * creditLimit - Limits the amount of credit or loan that all Credit Accounts, registered on this bank, can handle all
     * at once. Defaults to 100,000.
     */
    private final double depositLimit, withdrawLimit, creditLimit;
    //Processing fee added when some transaction is involved with another bank. Cannot be lower
    //than 0.0. Defaults to 10.00
    private final double processingFee;
    //List of accounts registered to this bank.
    private final ArrayList<Account> bankAccounts;

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
        this.depositLimit = 50000.0;
        this.withdrawLimit = 50000.0;
        this.creditLimit = 100000.0;
        this.processingFee = 10.0;
        this.bankAccounts = new ArrayList<>();
    }

    public Bank(int bankId, String bankName, String passcode, double depositLimit, double withdrawLimit, double creditLimit, double processingFee) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.passcode = passcode;
        this.depositLimit = depositLimit;
        this.withdrawLimit = withdrawLimit;
        this.creditLimit = creditLimit;
        this.processingFee = processingFee;
        this.bankAccounts = new ArrayList<>();
    }

    /**
     * Show accounts based on option.
     * @param accountType – Type of account to be shown.
     */
    public <T extends Account> void showAccounts(Class<T> accountType) {
        // Check if there are any accounts
        if (bankAccounts.isEmpty()) {
            System.out.println("No accounts have been created.");
            return;
        }
        // If no account type specified, show all accounts in the bank.
        if (accountType == null) {
            System.out.println("Showing all accounts:");
            int count = 1;
            for (Account account : bankAccounts) {
                System.out.println(count + ".) " + account);
                count++;
            }
        } else { // accountType specified, show only accounts of that type
            boolean hasAccounts = false;
            int count = 1;
            for (Account account : bankAccounts) {
                if (accountType.isInstance(account)) {
                    System.out.println(count + ".) " + account);
                    hasAccounts = true;
                    count++;
                }
            }
            if (!hasAccounts) {
                System.out.println("No " + accountType.getSimpleName() + " accounts have been created.");
            }
        }
    }

    /**
     * Get the Account object (if it exists) from a given bank.
     * @param accountNum – Account number of target account.
     * @return The account associated with the provided account number, or null if no such account exists.
     */
    public Account getBankAccount(Bank bank, String accountNum) {
        for (Account account : bank.bankAccounts) {
            if (account.getAccountNumber().equals(accountNum)) {
                return account;
            }
        }
        return null; // Return null if not found
    }

    /**
     * Handles the processing of inputting the basic information of the account.
     * 
     * @return Array list of Field objects, which are the basic account information of the account user.
     */
    public ArrayList<Field<?, ?>> createNewAccount() {
        ArrayList<Field<?, ?>> accountFields = new ArrayList<>();

        // Create fields with appropriate validation
        Field<String, Integer> accountNumberField = new Field<String, Integer>("Account Number", String.class, 5, new Field.StringFieldLengthValidator());
        Field<String, Integer> pinField = new Field<String, Integer>("PIN", String.class, 4, new Field.PinFieldValidator());
        Field<String, String> firstNameField = new Field<String, String>("First Name", String.class, null, new Field.StringFieldValidator());
        Field<String, String> lastNameField = new Field<String, String>("Last Name", String.class, null, new Field.StringFieldValidator());
        Field<String, String> emailField = new Field<String, String>("Email", String.class, null, new Field.StringFieldValidator());

        // Array of fields to prompt user input
        Field<?, ?>[] fields = {accountNumberField, pinField, firstNameField, lastNameField, emailField};
        // Prompt user for input
        for (Field<?, ?> field : fields) {
            field.setFieldValue("Enter " + field.getFieldName() + ": ");
            accountFields.add(field);
        }
        return accountFields;
    }

    public void createNewStudentAccount() {
        String accountNumber = Main.prompt("Enter Account Number: ", false);
        String pin = Main.prompt("Enter PIN: ", false);
        String ownerFname = Main.prompt("Enter First Name: ", false);
        String ownerLname = Main.prompt("Enter Last Name: ", false);
        String ownerEmail = Main.prompt("Enter Email: ", false);
        int yearOfBirth = Integer.parseInt(Main.prompt("Enter Year of Birth: ", false));
        String studentId = Main.prompt("Enter Student ID: ", false);

        try {
            StudentAccount tempAccount = new StudentAccount(this, accountNumber, pin, ownerFname, ownerLname, ownerEmail, yearOfBirth, studentId);
            if (!tempAccount.isEligibleForStudentAccount()) {
                System.out.println("Error: Age must be between 18 and 25 to create a Student Account.");
                return;
            }
            bankAccounts.add(tempAccount);
            System.out.println("Student Account successfully registered.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void createNewBusinessAccount() {
        String accountNumber = Main.prompt("Enter Account Number: ", false);
        String pin = Main.prompt("Enter PIN: ", false);
        String ownerFname = Main.prompt("Enter First Name: ", false);
        String ownerLname = Main.prompt("Enter Last Name: ", false);
        String ownerEmail = Main.prompt("Enter Email: ", false);
        String businessPermitID = Main.prompt("Enter Business Permit ID: ", false);
        String businessName = Main.prompt("Enter Business Name: ", false);
        double bankAnnualIncome = Double.parseDouble(Main.prompt("Enter Bank Annual Income: ", false));
        double initialDeposit = Double.parseDouble(Main.prompt("Enter Initial Deposit: ", false));

        try {
            BusinessAccount newAccount = new BusinessAccount(this, accountNumber, pin, ownerFname, ownerLname, ownerEmail, businessPermitID, businessName, bankAnnualIncome, initialDeposit);
            bankAccounts.add(newAccount);
            System.out.println("Business Account successfully registered.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Create a new savings account. Utilizes the createNewAccount() method.
     * 
     * @return New savings account
     */
    public SavingsAccount createNewSavingsAccount() {
        // Get account information
        ArrayList<Field<?, ?>> accountData = createNewAccount();
        String accountNumber = (String) accountData.get(0).getFieldValue();
        String pin = (String) accountData.get(1).getFieldValue();
        String firstName = (String) accountData.get(2).getFieldValue();
        String lastName = (String) accountData.get(3).getFieldValue();
        String email = (String) accountData.get(4).getFieldValue();

        double initialDeposit = Double.parseDouble(Main.prompt("Enter Initial Deposit: ", true));

        SavingsAccount newAccount = new SavingsAccount(this, accountNumber, pin, firstName, lastName, email, initialDeposit);
        addNewAccount(newAccount);
        return newAccount;
    }

    /**
     * Create a new credit account. Utilizes the createNewAccount() method.
     * 
     * @return New credit account.
     */
    public CreditAccount createNewCreditAccount() {
        // Get account information
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
     * Adds a new account to this bank, if the account number of the new account does not exist inside
     * the bank.
     * @param account – Account object to be added into this bank.
     */
    public void addNewAccount(Account account) {
        // Check if account number already exists in this bank
        if (accountExists(this, account.getAccountNumber())) {
            System.out.println("Account number already exists in this bank! Registration failed.");
            return;
        }
        // Add the account to the bank
        bankAccounts.add(account);
        AccountLauncher.saveAccounts();
        System.out.println(account);
        System.out.println("Account successfully registered.");
    }

    /**
     * Checks if an account object exists into a given bank based on some account number.
     * 
     * @param bank – Bank to check if account exists.
     * @param accountNum – Account number of target account to check.
     * @return true if an account with the specified account number exists, false otherwise
     */
    public static boolean accountExists(Bank bank, String accountNum) {
        // Check if bank or bank accounts are null
        if (bank == null || bank.getBankAccounts() == null) {
            return false;
        }
        // Check if account number exists in the bank
        return bank.getBankAccounts().stream()
                .anyMatch(account -> account.getAccountNumber().equals(accountNum));
    }

    //Getters
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
        return new ArrayList<>(bankAccounts);
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

    @Override
    public String toString() {
        return String.format("Bank{Bank ID: %d,Bank Name: %s, Bank Passcode: %s, # of Account Registered: %d}",
                                    bankId, bankName, "*".repeat(passcode.length()), bankAccounts.size());
    }

    //Comparators
    /**
     * A comparator that compares if two bank objects are the same.
     */
    public static class BankCredentialsComparator implements Comparator<Bank> {
        @Override
        public int compare(Bank b1, Bank b2) {
            // Compare IDs first
            if (b1.getBankId() != b2.getBankId()) {
                return Integer.compare(b1.getBankId(), b2.getBankId());
            }

            // If IDs are the same, compare names
            return b1.getName().compareTo(b2.getName());
        }
    }

    /**
     * A comparator that compares if two bank objects have the same bank id.
     */
    public static class BankIdComparator implements Comparator<Bank> {
        @Override
        public int compare(Bank b1, Bank b2) {
            return Integer.compare(b1.getBankId(), b2.getBankId());
        }
    }

    /**
     * A comparator that compares if two bank objects are the same.
     */
    public static class BankComparator implements Comparator<Bank> {
        @Override
        public int compare(Bank b1, Bank b2) {
            return b1.getName().compareTo(b2.getName());
        }
    }
}