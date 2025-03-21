package Accounts;

import Bank.Bank;
import Bank.BankLauncher;
import Main.*;
import Processes.IllegalAccountType;
import Database.JSONDatabase;
import java.util.ArrayList;
/**
 * A class primarily used for interacting with the account module.
 */
public class AccountLauncher {
    //Account object of logged account user.
    private static Account loggedAccount;
    //Selected associated bank when attempting to log in in the account module.
    private static Bank assocBank;
    //The name of the file where account information is stored.
    private static final String ACCOUNTS_FILE = "Database/Accounts.json";

    // Static block to load accounts from the JSON file when the class is loaded.
    static {
        loadAccounts();
    }

    public void setAssocBank(Bank assocBank) {
        AccountLauncher.assocBank = assocBank;
    }

    //Getters
    protected static Account getLoggedAccount() {
        return loggedAccount;
    }

    /**
     * Verifies if some account is currently logged in.
     *
     * @return true if an account is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return loggedAccount != null;
    }

    /**
     * Login an account. Bank must be selected first before logging in. Account existence will depend
     * on the selected bank.
     */
    public void accountLogin() throws IllegalAccountType {
        // Check if the associated bank is selected
        if (assocBank == null) {
            System.out.println("Bank selection failed.");
            return;
        }
        // Check if the bank has any accounts
        if (assocBank.getBankAccounts().isEmpty()) {
            System.out.println("No accounts found in this bank. Please create an account first.");
            return;
        }
        // Check if there is already a logged-in account
        if (isLoggedIn()) {
            System.out.println("Another account is already logged in. Please log out first.");
            return;
        }

        // Display account types
        Main.showMenuHeader("Select Account Type");
        Main.showMenu(33);
        Main.setOption();
        // Determine the account type based on user selection
        Class<? extends Account> accountType;

        switch (Main.getOption()) {
            case 1 -> accountType = CreditAccount.class; //User selected Credit Account
            case 2 -> accountType = SavingsAccount.class; //User selected Savings Account
            case 3 -> accountType = StudentAccount.class; //User selected Student Account
            case 4 -> accountType = BusinessAccount.class; //User selected Business Account
            default -> {
                System.out.println("Invalid option. Returning to main menu.");
                return;
            }
        }

        // Prompt user for account number and PIN
        Field<String, Integer> accountField = new Field<String, Integer>("Account Number", String.class, 5, new Field.StringFieldLengthValidator());
        accountField.setFieldValue("Enter Account Number: ");

        Field<String, Integer> pinField = new Field<String, Integer>("4-digit PIN", String.class, 4, new Field.PinFieldValidator());
        pinField.setFieldValue("Enter 4-digit PIN: ");

        String accountNumber = accountField.getFieldValue();
        String pin = pinField.getFieldValue();

        // Retrieve account
        loggedAccount = checkCredentials(accountNumber, pin);
        // Check if account exists
        if (loggedAccount == null) {
            System.out.println("Account not found. Please try again.");
            return;
        }
        
        setLogSession(loggedAccount);

        String greet = "Login successful. Welcome, " + loggedAccount.getOwnerFname() + "!";

        // Use the accountType variable to check the account type
        if (accountType == CreditAccount.class && loggedAccount instanceof CreditAccount) {
            System.out.println(greet);
            CreditAccountLauncher.creditAccountInit();
        } else if (accountType == SavingsAccount.class && loggedAccount instanceof SavingsAccount) {
            System.out.println(greet);
            SavingsAccountLauncher.savingsAccountInit();
        } else if (accountType == StudentAccount.class && loggedAccount instanceof  StudentAccount) {
            System.out.println(greet);
//            StudentAccountLauncher.studentAccountInit();
        } else if (accountType == BusinessAccount.class && loggedAccount instanceof BusinessAccount) {
            System.out.println(greet);
//            BusinessAccountLauncher.BusinessAccountInit();
        } else {
            System.out.println("Invalid account type. Returning to main menu.");
            destroyLogSession();
            return;
        }
        destroyLogSession();
    }

    /**
     * Bank selection screen before the user is prompted to log in. User is prompted for the Bank ID
     *
     * @return Bank object based on selected ID.
     */
    public static Bank selectBank() {
        // Check if there are any banks available
        if (BankLauncher.bankSize() == 0) {
            System.out.println("No banks are available. Please create a bank first.");
            return null;
        }

        boolean on = true;
        while (on) {
            Main.showMenuHeader("Select a Bank");
            BankLauncher.showBanksMenu();

            Field<Integer, Integer> bankidField = new Field<Integer, Integer>("Bank ID", Integer.class, 0, new Field.IntegerFieldValidator());
            bankidField.setFieldValue("Enter Bank ID: ");

            Field<String, String> bankNameField = new Field<String, String>("Bank Name", String.class, null, new Field.StringFieldValidator());
            bankNameField.setFieldValue("Enter Bank Name: ");
            //Finding Bank
            for (Bank bank : BankLauncher.getBanks()) {
            if (bank.getBankId() == bankidField.getFieldValue() && bank.getName().equals(bankNameField.getFieldValue())) {
                System.out.println("Bank selected: " + bank.getName());
                return bank;
            }
            }
            System.out.println("Bank does not exist. Please try again");

            // Prompt user to continue or exit
            String userInput = Main.prompt("Do you want to try again? (Y/N): ", true).trim().toUpperCase();
            switch (userInput) {
                case "Y" -> {continue;}
                case "N" -> {return null;}
                default -> {
                    System.out.println("Invalid input. Returning to main menu.");
                    on = false;
                }
            }
        }
        return null;
    }

    /**
     * Create a login session based on the logged user account.
     *
     * @param account – Account that has successfully logged in.
     */
    private void setLogSession(Account account) {
        loggedAccount = account;
    }

    /**
     * Destroy the log session of the previously logged user account.
     */
    private void destroyLogSession() {
        if (!isLoggedIn()) {
            System.out.println("No active session to log out from.");
            return;
        }

        System.out.println("Logging out of " + loggedAccount.getAccountNumber());
        loggedAccount = null;
    }

    /**
     * Checks inputted credentials during account login.
     *
     * @param accountNumber Account number.
     * @param pin 4-digit pin.
     * @return Account object if it passes verification.
     */
    public Account checkCredentials(String accountNumber, String pin) {
        Account account = assocBank.getBankAccount(assocBank, accountNumber);
        if (account != null && account.getPin().equals(pin)) {
            return account;
        } else {
            return null;
        }
    }

    /**
     * Saves all the accounts from all banks to a JSON file.
     * This method iterates through all the banks, retrieves their accounts,
     * and then saves them to a JSON file using the JSONDatabase class.
     */
    public static void saveAccounts() {
        ArrayList<Account> allAccounts = new ArrayList<>();
        // Iterate through all banks
        for (Bank bank : BankLauncher.getBanks()) {
            // Add all accounts from the current bank to the list
            allAccounts.addAll(bank.getBankAccounts());
        }
        // Save the list of accounts to a JSON file
        JSONDatabase.saveData(allAccounts, ACCOUNTS_FILE);
    }

    /**
     * Loads all account data from the JSON file and associates each account with its respective bank.
     * The method uses the JSONDatabase class to load the account data from the specified file.
     * For each loaded account, it retrieves the associated bank and adds the account to the bank's list of accounts.
     */
    public static void loadAccounts() {
        // Load account data from the JSON file
        ArrayList<Account> loadedAccounts = JSONDatabase.loadData(ACCOUNTS_FILE, Account.class);
        // Iterate through each loaded account
        for (Account account : loadedAccounts) {
            // Retrieve the associated bank
            Bank bank = account.getBank();
            // Add the account to the bank's list of accounts
            bank.addNewAccount(account);
        }
    }
}
