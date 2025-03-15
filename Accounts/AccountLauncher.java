package Accounts;
import Bank.Bank;
import Bank.BankLauncher;
import Main.Field;
import Main.Main;

/**
 * A class primarily used for interacting with the account module.
 */
public class AccountLauncher {
    private static Account loggedAccount;
    private static Bank assocBank;

    public static Bank getAssocBank() {
        // assocBank is already assigned, no need to return it
        return assocBank;
    }

    public static boolean isLoggedIn() {
        return loggedAccount != null;
    }

    /**
     * Login an account. Bank must be selected first before logging in.
     * Account existence will depend on the selected bank.
     */
    public static void accountLogin() {
        if (isLoggedIn()) {
            destroyLogSession();
        }

        assocBank = selectBank();
        if (assocBank == null) {
            System.out.println("Invalid bank selection. Returning to main menu.");
            return;
        }

        Main.showMenuHeader("Account Login");
        String accountNum = Main.prompt("Enter account number: ", true);
        String pin = Main.prompt("Enter  PIN: ", true);

        loggedAccount = checkCredentials(accountNum, pin);

        if (loggedAccount != null) {
            System.out.println("Login successful.");
            setLogSession(loggedAccount);
            if (loggedAccount.getClass() == SavingsAccount.class) {
                SavingsAccountLauncher.savingsAccountInit();
            }
            else if (loggedAccount.getClass() == CreditAccount.class) {
                CreditAccountLauncher.creditAccountInit();
            }
        } else {
            System.out.println("Account doesn't exist!");
        }
    }

    /**
     * Bank selection screen before the user is prompted to log in.
     * User is prompted for the Bank ID with corresponding bank name.
     *
     * @return Bank object based on selected ID
     */
    private static Bank selectBank() {
        Main.showMenuHeader("Bank Selection");
        BankLauncher.showBanksMenu();
        Field<Integer, Integer> bankID = new Field<Integer, Integer>("ID", Integer.class, -1, new Field.IntegerFieldValidator());
        Field<String, String> bankName = new Field<String, String>("Name", String.class, "", new Field.StringFieldValidator());
        bankID.setFieldValue("Enter bank id: ");
        bankName.setFieldValue("Enter bank name: ");

        for (Bank bank : BankLauncher.getBANKS()) {
            if (bank.getID() == bankID.getFieldValue() && bank.getName().equals(bankName.getFieldValue())) {
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
     * @param account Account that has successfully logged in
     */
    public static void setLogSession(Account account) {
        loggedAccount = account;
        System.out.println("Session is set for account: " + account.getOwnerFullName());
    }

    /**
     * Destroy the log session of the previously logged user account.
     */
    public static void destroyLogSession() {
        if (loggedAccount != null) {
            System.out.println("Logging out account: " + loggedAccount.getOwnerFullName());
            loggedAccount = null;
        } else {
            System.out.println("No account is currently logged in.");
        }
    
        String response = Main.prompt("Do you also want to clear the bank session? (Y/N): ", true);
        if (response.equalsIgnoreCase("y")) {
            System.out.println("Clearing bank session.");
            assocBank = null;
        }
    }

    /**
     * Checks inputted credentials during account login.
     *
     * @param accountNum Account number
     * @param pin 4-digit pin
     * @return Account object if it passes verification, null if not
     */
    public static Account checkCredentials(String accountNum, String pin) {
        if (assocBank == null) {
            System.out.println("No bank selected.");
            return null;
        }

        Account selAccount = assocBank.getBankAccount(getAssocBank(), accountNum);
        if (selAccount != null && selAccount.getPin().equals(pin)) {
            return selAccount;
        } else {
            // Try to find the account in other banks if not found in the selected bank
            Account foundAccount = BankLauncher.findAccount(accountNum);
            if (foundAccount != null && foundAccount.getPin().equals(pin)) {
                System.out.println("Account found in a different bank: " + foundAccount.getBank().getName());
                assocBank = foundAccount.getBank(); // Update the associated bank
                return foundAccount;
            }
            System.out.println("Invalid account number or PIN for account " + accountNum + ".");
            return null;
        }
    }
    //         System.out.println("Invalid account number or PIN for account " + accountNum + ".");
    //         return null;
    //     }
    // }


    protected static Account getLoggedAccount() {
        if (isLoggedIn()) {
            return loggedAccount;
        } else {
            System.out.println("There is no logged account.");
            return null;
        }
    }
}