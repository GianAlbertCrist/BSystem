package Bank;

import Accounts.*;
import Main.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import Database.JSONDatabase;

/**
 *A class primarily used for interacting with the bank module
 */
public class BankLauncher {
    //List of banks currently registered in this session.
    private final static ArrayList<Bank> banks = new ArrayList<>();
    //The Bank object currently logged in. Null by default, or when no bank is currently logged in.
    private static Bank loggedBank;
    //The name of the file where account information is stored.
    private static final String BANKS_FILE = "Database/Banks.json";

    /**
     * Checks if there is a currently logged-in bank session.
     *
     * @return true if a bank is logged in, false otherwise.
     */
    public static boolean isLogged() {
        return loggedBank != null;
    }

    /**
     * Bank interaction initialization. Utilized only when logged in.
     */
    public static void bankInit() {
        while (isLogged()) {
            Main.showMenuHeader("Banking System");
            Main.showMenu(31);
            Main.setOption();

            switch (Main.getOption()) {
                case 1 -> showAccounts();
                case 2 -> newAccounts();
                case 3 -> {
                    logout();
                    System.out.println("Exiting banking system...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    /**
     * Show the accounts registered to this bank.
     * Must prompt the user to select which type of accounts to show:
     * (1) Credit Accounts, (2) Savings Accounts, (3) All, and (4) Create New Account.
     */
    private static void showAccounts() {
        // Check if a bank is logged in
        if (loggedBank == null) {
            System.out.println("No bank logged in.");
            return;
        }

        Main.showMenuHeader("Show Accounts");
        Main.showMenu(32);
        Main.setOption();

        switch (Main.getOption()) {
            case 1 -> displayAccounts(CreditAccount.class);
            case 2 -> displayAccounts(SavingsAccount.class);
            case 3 -> displayAccounts(StudentAccount.class);
            case 4 -> displayAccounts(BusinessAccount.class);
            case 5 -> displayAllAccounts();
            default -> System.out.println("Invalid option. Try again.");
        }
    }

    /**
     * Handles the creation of a new account within the currently logged-in bank.
     */
    private static void newAccounts() {
        // Check if a bank is logged in
        if (loggedBank == null) {
            System.out.println("No bank logged in.");
            return;
        }

        Main.showMenuHeader("Create a New Account");
        Main.showMenu(33);
        Main.setOption();

        switch (Main.getOption()) {
            case 1 -> loggedBank.createNewCreditAccount();
            case 2 -> loggedBank.createNewSavingsAccount();
            case 3 -> loggedBank.createNewStudentAccount();
            case 4 -> loggedBank.createNewBusinessAccount();
            default -> System.out.println("Invalid choice.");
        }
    }

    /**
     * Bank interaction when attempting to log in to the banking module using a bank user's credentials.
     */
    public static void bankLogin() {
        // Check if there are any banks registered
        if (banks.isEmpty()) {
            System.out.println("No banks registered yet. Create a new bank first.");
            return;
        }

        // Show available banks
        showBanksMenu();

        // Prompt user for bank name and passcode
        Field<String, String> bankNameField = new Field<>("Bank Name", String.class, null, new Field.StringFieldValidator());
        bankNameField.setFieldValue("Enter Bank Name: ");
        String bankName = bankNameField.getFieldValue();

        // Find the bank based on Name
        Bank selectedBank = null;
        for (Bank bank : banks) {
            if (bank.getName().equalsIgnoreCase(bankName)) {
                selectedBank = bank;
                break;
            }
        }

        // If bank is not found
        if (selectedBank == null) {
            System.out.println("Error: No bank found with the name \"" + bankName + "\".");
            return;
        }

        Field<String, String> passcodeField = new Field<>("Bank Passcode", String.class, null, new Field.StringFieldValidator());
        passcodeField.setFieldValue("Enter Bank Passcode: ");
        String passcode = passcodeField.getFieldValue();

        // Validate Passcode
        if (!selectedBank.getPasscode().equals(passcode)) {
            System.out.println("Error: Incorrect passcode. Access denied.");
            return;
        }
        // Set the associated bank in AccountLauncher
        AccountLauncher.setAssocBank(selectedBank);

        // Set logged-in session
        setLogSession(selectedBank);
        System.out.println("Successfully logged into " + loggedBank.getName());
        System.out.println(loggedBank);
        bankInit();
    }

    /**
     * Logs into a selected bank session.
     *
     * @param bank The bank to log into.
     */
    private static void setLogSession(Bank bank) {
        loggedBank = bank;
    }

    /**
     * Logs out from the current bank session.
     */
    private static void logout() {
        if (loggedBank != null) {
            System.out.println("Logging out from " + loggedBank.getName());
        }
        loggedBank = null;
    }

    /**
     * Adds a new bank to the list of registered banks.
     *
     * @param b The bank to be added.
     */
    private static void addBank(Bank b) {
        banks.add(b);
        System.out.println("Bank successfully added: " + b.getName());
    }

    /**
     * Output a menu of all registered or created banks in this session.
     */
    public static void showBanksMenu() {
        if (banks.isEmpty()) {
            System.out.println("No banks have been registered yet.");
            return;
        }
        System.out.println("\nList of Registered Banks:");
        System.out.printf("%-3s | %-30s | %s%n", "#", "Bank Name", "Bank ID");
        System.out.println("-----------------------------------------------------");
        for (int i = 0; i < banks.size(); i++) {
            System.out.printf("%-3d | %-30s | %s%n", i + 1, banks.get(i).getName(), banks.get(i).getBankId());
        }
    }

    /**
     * Checks if a bank exists based on some criteria.
     * 
     * @param comparator – Criteria for searching.
     * @param bank – Bank object to be compared.
     * @return Bank object if it passes the criteria. Null if none.
     */
    public static Bank getBank(Comparator<Bank> comparator, Bank bank) {
        return banks.stream().filter(b -> comparator.compare(b, bank) == 0).findFirst().orElse(null);
    }

    /**
     * Display all accounts registered under the logged-in bank.
     */
    private static void displayAllAccounts() {
        loggedBank.showAccounts(null);
    }

    /**
     * Display accounts of a specific type (Credit or Savings).
     * @param accountType The class type of accounts to display.
     */
    private static void displayAccounts(Class<? extends Account> accountType) {
        System.out.println("Showing " + (accountType == CreditAccount.class ? "Credit" : "Savings") + " Accounts:");
        loggedBank.showAccounts(accountType);
    }

    /**
     * Creates a new bank and registers it in the system.
     */
    public static void createNewBank() {
        // Bank Name Field
        Field<String, String> bankNameField = new Field<String, String>("Bank Name", String.class, null, new Field.StringFieldValidator());
        bankNameField.setFieldValue("Enter Bank Name: ", false);
        // Validate Bank Name
        if (bankNameField.getFieldValue() == null || bankNameField.getFieldValue().isEmpty()) {
            System.out.println("Error: Bank Name is required!");
            return; // Exit early
        }
        // Bank ID Field
        Field<String, Integer> bankPasscodeField = new Field<String, Integer>("Bank Passcode", String.class, 4, new Field.StringFieldLengthValidator());
        bankPasscodeField.setFieldValue("Enter Bank Passcode: ");
        // Validate Bank Passcode
        if (bankPasscodeField.getFieldValue() == null || bankPasscodeField.getFieldValue().length() < 4) {
            System.out.println("Error: Passcode must be at least 4 characters long.");
            return; // Exit early
        }
    
        Bank newBank = null;
    
        boolean ans = true;
        while (ans) {
            // Ask user if they want to set custom limits
            String choice = Main.prompt("Do you want to set custom deposit, withdrawal, credit limits and processing fee? (Y/N):", true).trim().toUpperCase();
    
            if (choice.equals("Y")) {
                // Custom Limits Fields
                Field<Double, Double> depositLimitField = new Field<Double, Double>("Deposit Limit", Double.class, 0.0, new Field.DoubleFieldValidator());
                depositLimitField.setFieldValue("Enter Deposit Limit: ");

                Field<Double, Double> withdrawLimitField = new Field<Double, Double>("Withdraw Limit", Double.class, 0.0, new Field.DoubleFieldValidator());
                withdrawLimitField.setFieldValue("Enter Withdraw Limit: ");
    
                Field<Double, Double> creditLimitField = new Field<Double, Double>("Credit Limit", Double.class, 0.0, new Field.DoubleFieldValidator());
                creditLimitField.setFieldValue("Enter Credit Limit: ");
    
                Field<Double, Double> processingFeeField = new Field<Double, Double>("Processing Fee", Double.class, 0.0, new Field.DoubleFieldValidator());
                processingFeeField.setFieldValue("Enter Processing Fee: ");
    
                // Validate custom fields
                if (depositLimitField.getFieldValue() == null || withdrawLimitField.getFieldValue() == null ||
                    creditLimitField.getFieldValue() == null || processingFeeField.getFieldValue() == null) {
                    System.out.println("Error: All custom limits must be provided.");
                    return; // Exit early
                }
    
                // Create Bank with custom values
                newBank = new Bank(
                        bankSize(),
                        bankNameField.getFieldValue(),
                        bankPasscodeField.getFieldValue(),
                        depositLimitField.getFieldValue(),
                        withdrawLimitField.getFieldValue(),
                        creditLimitField.getFieldValue(),
                        processingFeeField.getFieldValue()
                );
                ans = false;
    
            } else if (choice.equals("N")) {
                // Create Bank with default values
                newBank = new Bank(
                        bankSize(),
                        bankNameField.getFieldValue(),
                        bankPasscodeField.getFieldValue()
                );
                ans = false;
    
            } else {
                System.out.println("Invalid input: Input Y or N only. Please try again.");
            }
        }
    
        // Add Bank to the List
        if (getBank(new Bank.BankComparator(), newBank) == null) {
            System.out.println("Bank created successfully: " + newBank);
            addBank(newBank);
        } else {
            System.out.printf("Bank %s already exists!\n", newBank.getName());
        }
    }

    public static ArrayList<Bank> getBanks() {
        return banks;
    }

    /**
     * Returns the number of registered banks.
     *
     * @return The number of banks.
     */
    public static int bankSize() {
        return banks.size();
    }

    /**
     * Finds an account by account number in all registered banks.
     *
     * @param accountNum The account number to search for.
     * @return The account if found, otherwise null.
     */
    public static Account findAccount(String accountNum) {
        return banks.stream()
                .map(bank -> bank.getBankAccount(bank, accountNum))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    /**
     * Saves the list of banks to a JSON file.
     * This method is used to persist the bank data between program executions.
     */
    public static void saveBanks() {
        JSONDatabase.saveData(new ArrayList<>(banks), BANKS_FILE);
    }


    /**
     * Loads the list of banks from a JSON file.
     * This method is used to restore the bank data from a previous program execution.
     */
    public static void loadBanks() {
        // Load bank data from the JSON file
        ArrayList<Bank> loadedBanks = JSONDatabase.loadData(BANKS_FILE, Bank.class);
        // Clear the current list of banks
        banks.clear();
        // Add the loaded banks to the current list
        banks.addAll(loadedBanks);
    }
}