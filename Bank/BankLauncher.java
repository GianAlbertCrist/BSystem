package Bank;
import Accounts.Account;
import Accounts.CreditAccount;
import Accounts.SavingsAccount;
import Main.Field;
import Main.Main;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * The BankLauncher class serves as the main entry point for managing banks within the system.
 * It allows users to create, log in, and manage banks, as well as perform operations such as 
 * viewing accounts and creating new accounts.
 */
public class BankLauncher {
    // Attributes
    /** List of registered banks in the system. */
    private final static ArrayList<Bank> BANKS = new ArrayList<>();
    /** Stores the currently logged-in bank. */
    private static Bank loggedBank = null;


    public static Bank getLoggedBank() {
        return loggedBank;
    }

    public static ArrayList<Bank> getBANKS() {
        return BANKS;
    }

    // Methods
    /**
     * Checks if a bank is currently logged in.
     * @return true if a bank is logged in, false otherwise.
     */
    public static boolean isLogged() {
        return loggedBank != null;
    }
    /**
     * Initializes the bank system by resetting the list of banks.
     */
    public static void bankInit() {

        boolean login = true;
        while (login) {
            Main.showMenuHeader("Bank Menu");
            Main.showMenu(31);
            Main.setOption();
            Main.showMenu( Main.getOption(),31);

            switch(Main.getOption()){
                case 1:
                    showAccounts();
                    break;
                case 2:
                    newAccounts();
                    break;
                case 3:
                    logout();
                    login = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }


    /**
     * Displays all accounts in the currently logged-in bank.
     */
    private static void showAccounts() {
        boolean exit = true;
        while (exit) {
            Main.showMenuHeader("Show Accounts Menu");
            Main.showMenu(32);
            Main.setOption();
            Main.showMenu(Main.getOption(), 32);

            switch(Main.getOption()){
                case 1:
                    Main.showMenuHeader("Credit Accounts ");
                    getLoggedBank().showAccounts(CreditAccount.class);
                    break;
                case 2:
                    Main.showMenuHeader("Saving Accounts ");
                    getLoggedBank().showAccounts(SavingsAccount.class);
                    break;
                case 3:
                    Main.showMenuHeader("All Account ");
                    getLoggedBank().showAccounts(Account.class);
                    break;

                case 4:
                    exit= false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

    }

    /**
     * Creates a new account in the currently logged-in bank.
     */
    private static void newAccounts() {
        Main.showMenuHeader("Create new Account");
        Main.showMenu(33);
        Main.setOption();
        Main.showMenu(Main.getOption(), 33);

        switch (Main.getOption()) {
            case 1:
                CreditAccount creditAccount = getLoggedBank().createNewCreditAccount();
                getLoggedBank().addNewAccount(creditAccount);
                break;
            case 2:
                SavingsAccount savingsAccount = getLoggedBank().createNewSavingsAccount();
                getLoggedBank().addNewAccount(savingsAccount);
                break;
        
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    /**
     * Bank interaction when attempting to login to the banking module using a bank user's credentials.
     */
    public static void bankLogin() {
        Main.showMenuHeader("Bank Login");
        Main.showMenu(3);
        Main.setOption();
        Main.showMenu(Main.getOption(), 3);

        switch (Main.getOption()) {
            case 1:
                if (BANKS.isEmpty()) {
                    System.out.println("No banks registered. Please create a bank first.");
                    return;
                }

                int ID = Integer.parseInt(Main.prompt("Enter Bank ID: ", true));
                String name = Main.prompt("Enter Bank Name: ", true);
                String passcode = Main.prompt("Enter pin: ", true);
                
                for (Bank bank : BANKS) {
                    //Must use getBank()
                    if (bank.getID() == ID && bank.getName().equals(name) && bank.getPasscode().equals(passcode)) {
                        setLogSession(bank);
                        bankInit();
                        break;
                    }
                    System.out.println("Invalid ID or passcode or Name. Please try again."); 
                }
                break;
            case 2:
                break;
            default:
                System.out.println("Invalid option. Please try again.");
            }
        }
    /**
     * Sets the currently logged-in bank.
     * @param b The bank to be logged in.
     */
    private static void setLogSession(Bank b) {
        loggedBank = b;
    }

    /**
     * Logs out the currently logged-in bank.
     */
    private static void logout() {
        if (loggedBank != null) {
            System.out.println("Logging out from " + loggedBank.getName());
            loggedBank = null;
        } else {
            System.out.println("No bank is currently logged in.");
        }
    }
    /**
     * Creates a new bank and adds it to the system.
     */
//    public static void createNewBank() throws NumberFormatException {
//        Field<Integer,Integer> idField = new Field<Integer, Integer>("ID", Integer.class, -1, new Field.IntegerFieldValidator());
//        Field<String,String> nameField = new Field<String, String>("Name", String.class, "", new Field.StringFieldValidator());
//        Field<String,Integer> passcodeField = new Field<String, Integer>("Passcode", String.class, 4, new Field.StringFieldLengthValidator());
//        Field<Double,Double> depositLimitField = new Field<Double, Double>("Deposit Limit", Double.class, 0.0, new Field.DoubleFieldValidator());
//        Field<Double,Double> withdrawLimitField = new Field<Double, Double>("Witdraw Limit", Double.class, 0.0, new Field.DoubleFieldValidator());
//        Field<Double,Double> creditLimitField = new Field<Double, Double>("Credit Limit", Double.class, 0.0, new Field.DoubleFieldValidator());
//        Field<Double,Double> processingFeeField = new Field<Double, Double>("Processing Fee", Double.class, 0.0, new Field.DoubleFieldValidator());
//
//        Bank newBank;
//        try {
//            idField.setFieldValue("Bank ID: ");
//            nameField.setFieldValue("Bank Name: ");
//            passcodeField.setFieldValue("Bank Passcode: ");
//            depositLimitField.setFieldValue("Deposit Limit(0 for default): ");
//            if (depositLimitField.getFieldValue() == 0) {
//                newBank = new Bank(idField.getFieldValue(), nameField.getFieldValue(), passcodeField.getFieldValue());
//            } else {
//                withdrawLimitField.setFieldValue("Withdraw Limit: ");
//                creditLimitField.setFieldValue("Credit Limit: ");
//                processingFeeField.setFieldValue("Processing Fee: ");
//
//                newBank = new Bank(idField.getFieldValue(), nameField.getFieldValue(), passcodeField.getFieldValue(),
//                        depositLimitField.getFieldValue(), withdrawLimitField.getFieldValue(),
//                        creditLimitField.getFieldValue(), processingFeeField.getFieldValue());
//            }
//        } catch (NumberFormatException e) {
//            System.out.println("Invalid input format! Please enter a valid number.");
//            return;
//        }
//
//        addBank(newBank);
//    }
    public static void createNewBank() throws NumberFormatException {
        Field<Integer,Integer> idField = new Field<Integer, Integer>("ID", Integer.class, -1, new Field.IntegerFieldValidator());
        Field<String,String> nameField = new Field<String, String>("Name", String.class, "", new Field.StringFieldValidator());
        Field<String,Integer> passcodeField = new Field<String, Integer>("Passcode", String.class, 4, new Field.StringFieldLengthValidator());
        Field<Double,Double> depositLimitField = new Field<Double, Double>("Deposit Limit", Double.class, 0.0, new Field.DoubleFieldValidator());
        Field<Double,Double> withdrawLimitField = new Field<Double, Double>("Witdraw Limit", Double.class, 0.0, new Field.DoubleFieldValidator());
        Field<Double,Double> creditLimitField = new Field<Double, Double>("Credit Limit", Double.class, 0.0, new Field.DoubleFieldValidator());
        Field<Double,Double> processingFeeField = new Field<Double, Double>("Processing Fee", Double.class, 0.0, new Field.DoubleFieldValidator());

        Bank newBank;
        try {
            idField.setFieldValue("Bank ID: ");
            nameField.setFieldValue("Bank Name: ");
            passcodeField.setFieldValue("Bank Passcode: ");
            depositLimitField.setFieldValue("Deposit Limit(0 for default): ");
            if (depositLimitField.getFieldValue() == 0) {
                newBank = new Bank(idField.getFieldValue(), nameField.getFieldValue(), passcodeField.getFieldValue());
            } else {
                withdrawLimitField.setFieldValue("Withdraw Limit: ");
                creditLimitField.setFieldValue("Credit Limit: ");
                processingFeeField.setFieldValue("Processing Fee: ");

                newBank = new Bank(idField.getFieldValue(), nameField.getFieldValue(), passcodeField.getFieldValue(),
                        depositLimitField.getFieldValue(), withdrawLimitField.getFieldValue(),
                        creditLimitField.getFieldValue(), processingFeeField.getFieldValue());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format! Please enter a valid number.");
            return;
        }

        addBank(newBank);
    }

    /**
     * Displays a list of all registered banks.
     */
    public static void showBanksMenu() {
        Main.showMenuHeader("List of Banks");
        if (getBANKS().isEmpty()){
            System.out.println("No banks registered.");
            return;
        }
        
        ArrayList<Bank> sortedBanks = new ArrayList<>(BANKS);
        sortedBanks.sort(new Bank.BankIdComparator());
        
        int i = 1;
        for (Bank bank : BANKS) {
            System.out.printf("%d.) (%d) %s%n", i++, bank.getID(), bank.getName());
        }
    }
    /**
     * Adds a new bank to the list of banks.
     * @param b The bank to be added.
     */
    private static void addBank(Bank b) {
        BANKS.add(b);
    }

    /**
     * Retrieves a bank using a comparator.
     * @param comparator The comparator used for finding the bank.
     * @param bank The reference bank to compare against.
     * @return The matching bank, or null if no match is found.
     */
    public static Bank getBank(Comparator<Bank> comparator, Bank bank) {
        if (getBANKS() == null || bankSize() == 0) {
            return null;
        }

        for (Bank b : getBANKS()) {
            if (comparator.compare(b, bank) == 0) {
                return b;
            }
        }
        return null;
    }

    /**
     * Searches for an account across all banks by account number.
     * @param accountNum The account number to search for.
     * @return The matching account, or null if not found.
     */
   public static Account findAccount(String accountNum) {
       for (Bank bank : getBANKS()) {
            if (Bank.accountExist(bank, accountNum)) {
                return bank.getBankAccount(bank, accountNum);
            }
        }
       return null;
   }

    public static int bankSize() {
        return getBANKS().size();
    }
}
