package Bank;

import Accounts.*;
import Main.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * BankLauncher handles interactions with the bank module, allowing login,
 * account management, and bank creation.
 */
public class BankLauncher {

    private final static ArrayList<Bank> banks = new ArrayList<>();
    private static Bank loggedBank;

    /**
     * Initializes the banking module, allowing users to log in or create a new bank.
     */
    public static void bankInit() {
        while (isLogged()) {
            Main.showMenuHeader("Banking System");
            Main.showMenu(Menu.BankMenu.menuIdx);
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
     * Checks if there is a currently logged-in bank session.
     *
     * @return true if a bank is logged in, false otherwise.
     */
    public static boolean isLogged() {
        return loggedBank != null;
    }

    /**
     * Adds a new bank to the list of registered banks.
     *
     * @param b The bank to be added.
     */
    public static void addBank(Bank b) {
        if (!banks.contains(b)) {
            banks.add(b);
            System.out.println("✅ Bank successfully added: " + b.getName());
        } else {
            System.out.println("⚠ Bank already exists.");
        }
    }

    /**
     * Handles the bank login process.
     */
    public static void bankLogin() {
        if (banks.isEmpty()) {
            System.out.println("No banks registered yet. Create a new bank first.");
            return;
        }

        // Show available banks
        showBanksMenu();

        // Ask for Bank Name
        String bankName = Main.prompt("Enter Bank Name: ", false).trim();

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
            System.out.println("❌ Error: No bank found with the name \"" + bankName + "\".");
            return;
        }

        // Request Passcode
        String passcode = Main.prompt("Enter Bank Passcode: ", true).trim();

        // Validate Passcode
        if (!selectedBank.getPasscode().equals(passcode)) {
            System.out.println("❌ Error: Incorrect passcode. Access denied.");
            return;
        }

        // Set logged-in session
        setLogSession(selectedBank);
        System.out.println("✅ Successfully logged into " + loggedBank.getName());
        bankInit();
    }


    /**
     * Displays a menu of all registered banks.
     */
    public static void showBanksMenu() {
        if (banks.isEmpty()) {
            System.out.println("No banks have been registered yet.");
            return;
        }
        System.out.println("\n📌 List of Registered Banks:");
        System.out.printf("%-3s | %-30s | %s%n", "#", "Bank Name", "Bank ID");
        System.out.println("-----------------------------------------------------");
        for (int i = 0; i < banks.size(); i++) {
            System.out.printf("%-3d | %-30s | %s%n", i + 1, banks.get(i).getName(), banks.get(i).getBankId());
        }
    }

    /**
     * Retrieves a bank that matches the given comparator.
     *
     * @param bankComparator Comparator to determine the matching criteria.
     * @param bank Bank object to be compared.
     * @return The matching Bank object, or null if no match is found.
     */
    public static Bank getBank(Comparator<Bank> bankComparator, Bank bank) {
        return banks.stream().filter(b -> bankComparator.compare(b, bank) == 0).findFirst().orElse(null);
    }

    /**
     * Show the accounts registered to this bank.
     * Must prompt the user to select which type of accounts to show:
     * (1) Credit Accounts, (2) Savings Accounts, (3) All, and (4) Create New Account.
     */
    public static void showAccounts() {
        if (loggedBank == null) {
            System.out.println("No bank logged in.");
            return;
        }

        Main.showMenuHeader("Show Accounts");
        Main.showMenu(Menu.ShowAccounts.menuIdx);
        Main.setOption();

        switch (Main.getOption()) {
            case 1 -> displayAccounts(CreditAccount.class);
            case 2 -> displayAccounts(SavingsAccount.class);
            case 3 -> displayAllAccounts();
            default -> System.out.println("Invalid option. Try again.");
        }
    }

    /**
     * Display all accounts registered under the logged-in bank.
     */
    private static void displayAllAccounts() {
        System.out.println("Showing all accounts:");
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
     * Handles the creation of a new account within the currently logged-in bank.
     */
    public static void newAccounts() {
        if (loggedBank == null) {
            System.out.println("No bank logged in.");
            return;
        }

        Main.showMenuHeader("Create a New Account");
        Main.showMenu(Menu.AccountTypeSelection.menuIdx);
        Main.setOption();

        switch (Main.getOption()) {
            case 1 -> System.out.println("Credit Account created: " + loggedBank.createNewCreditAccount());
            case 2 -> System.out.println("Savings Account created: " + loggedBank.createNewSavingsAccount());
            default -> System.out.println("Invalid choice.");
        }
    }

    /**
     * Logs into a selected bank session.
     *
     * @param bank The bank to log into.
     */
    public static void setLogSession(Bank bank) {
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
     * Creates a new bank and registers it in the system.
     */
    public static void createNewBank() {

        Field<String, String> bankNameField = new Field<String, String>("Bank Name", String.class, null, new Field.StringFieldValidator());
        bankNameField.setFieldValue("Enter Bank Name: ", false);

        if (bankNameField.getFieldValue().isEmpty()) {
            System.out.println("❌ Error: Bank Name is required!");
            return; // Exit early
        }

        Field<String, Integer> bankPasscodeField = new Field<String, Integer>("Bank Passcode", String.class, 4, new Field.StringFieldLengthValidator());
        bankPasscodeField.setFieldValue("Enter Bank Passcode: ");

        if (bankPasscodeField.getFieldValue() == null || bankPasscodeField.getFieldValue().length() < 4) {
            System.out.println("❌ Error: Passcode must be at least 4 characters long.");
            return; // Exit early
        }

        // Ask user if they want to set custom limits
        System.out.println("Do you want to set custom deposit, withdrawal, and credit limits? (Y/N): ");
        String choice = Main.prompt("", true).trim().toUpperCase();

        Bank newBank;

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
        } else {
            // Create Bank with default values
            newBank = new Bank(
                    bankSize(),
                    bankNameField.getFieldValue(),
                    bankPasscodeField.getFieldValue()
            );
        }

        // Add Bank to the List
        addBank(newBank);
        System.out.println("✅ Bank created successfully: " + newBank);
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
     * Retrieves a bank by index.
     *
     * @param index The index of the bank.
     * @return Optional of Bank.
     */
    public static Optional<Bank> getBankByIndex(int index) {
        if (index > 0 && index <= banks.size()) {
            return Optional.of(banks.get(index - 1));
        }
        return Optional.empty();
    }

    public static ArrayList<Bank> getBanks() {
        return banks;
    }

    /**
     * Finds an account by account number in all registered banks.
     *
     * @param accountNum The account number to search for.
     * @return The account if found, otherwise null.
     */
    public static Account findAccount(String accountNum) {
        return banks.stream()
                .map(bank -> bank.getBankAccount(accountNum))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}