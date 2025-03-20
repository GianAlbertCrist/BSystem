package Accounts;

import Bank.Bank;
import Bank.BankLauncher;
import Main.*;

/**
 * A class primarily used for interacting with the account module.
 */
public class AccountLauncher {
    //Account object of logged account user.
    private static Account loggedAccount;
    //Selected associated bank when attempting to log in in the account module.
    private static Bank assocBank;

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
        if (assocBank == null) {
            System.out.println("Bank selection failed. Please try again.");
            return;
        }

        if (assocBank.getBankAccounts().isEmpty()) {
            System.out.println("No accounts found in this bank. Please create an account first.");
            return;
        }

        if (isLoggedIn()) {
            System.out.println("Another account is already logged in. Please log out first.");
            return;
        }
        //This is wrong
        Main.showMenuHeader("Select Account Type");
        Main.showMenu(33);
        Main.setOption();

        Class<? extends Account> accountType;
        
        switch (Main.getOption()) {
            case 1:
                accountType = CreditAccount.class;
                break;
            case 2:
                accountType = SavingsAccount.class;
                break;
            default:
                System.out.println("Invalid option. Returning to main menu.");
                return;
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

        if (loggedAccount == null) {
            System.out.println("Account not found. Please try again.");
            return;
        }
        setLogSession(loggedAccount);

        // Use the accountType variable to check the account type
        if (accountType == CreditAccount.class && loggedAccount instanceof CreditAccount) {
            System.out.println("Login successful. Welcome, " + loggedAccount.getOwnerFname() + "!");
            CreditAccountLauncher.creditAccountInit();
        } else if (accountType == SavingsAccount.class && loggedAccount instanceof SavingsAccount) {
            System.out.println("Login successful. Welcome, " + loggedAccount.getOwnerFname() + "!");
            SavingsAccountLauncher.savingsAccountInit();
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
        if (BankLauncher.bankSize() == 0) {
            System.out.println("No banks are available. Please create a bank first.");
            return null;
        }

        Main.showMenuHeader("Select a Bank");
        BankLauncher.showBanksMenu();

        Field <Integer, Integer> bankidField = new Field<Integer, Integer>("Bank ID", Integer.class, 0, new Field.IntegerFieldValidator());
        bankidField.setFieldValue("Enter Bank ID: ");

        Field <String, String> bankNameField = new Field<String, String>("Bank Name", String.class, null, new Field.StringFieldValidator());
        bankNameField.setFieldValue("Enter Bank Name: ");

        for (Bank bank : BankLauncher.getBanks()) {
            if (bank.getBankId() == bankidField.getFieldValue() && bank.getName().equals(bankNameField.getFieldValue())) {
                System.out.println("Bank selected: " + bank.getName());
                return bank;
            }
        }
        System.out.println("Bank does not exist.");
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
}