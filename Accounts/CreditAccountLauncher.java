package Accounts;
import Main.Field;
import Main.Main;

/**
 * Credit Account Launcher class for handling credit account operations
 */
public class CreditAccountLauncher extends AccountLauncher {
    /**
     * Method that deals with all things about credit accounts.
     * Mainly utilized for showing the main menu after Credit Account users log in to the application.
     */
    public static void creditAccountInit() {
        while (true) {
            Main.showMenuHeader("Credit Account Menu");
            Main.showMenu(41);
            Main.setOption();
            Main.showMenu(Main.getOption(), 41);

            switch (Main.getOption()) {
                case 1:
                    Main.showMenuHeader("Loan Statement");
                    System.out.println(getLoggedAccount().getLoanStatement());
                    break;
                case 2:
                    creditPaymentProcess();
                    break;
                case 3:
                    creditRecompenseProcess();
                    break;
                case 4:
                    Main.showMenuHeader("Transaction History");
                    System.out.println(getLoggedAccount().getTransactionsInfo());
                    break;
                case 5:
                    destroyLogSession();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Method that is utilized to process the credit payment transaction.
     *
     * @throws IllegalAccountType
     */
    public static void creditPaymentProcess() {
        Main.showMenuHeader("Credit Payment Process");

        String accountNum = Main.prompt("Account Number: ", true);
        double amount = Double.parseDouble(Main.prompt("Amount: ", true));

        if (getLoggedAccount() == null) {
            System.out.println("No logged-in credit account.");
            return;
        }

        Account account = getAssocBank().getBankAccount(getAssocBank(), accountNum);
        if (account == null) {
            System.out.println("The specified account does not exist.");
            return;
        }

        try {
            if (getLoggedAccount().pay(account, amount)) {
            getLoggedAccount().addNewTransaction(
                getLoggedAccount().getAccountNumber(),
                Transaction.Transactions.Payment, "A successful payment.");
            System.out.println("Payment successful!");
            } else {
            System.out.println("Payment unsuccessful!");
            }
        } catch (IllegalAccountType e) {
            System.out.println("An error occurred during the payment process: " + e.getMessage());
        }
    }


    /**
     * Method that is utilized to process the credit compensation transaction.
     */
    public static void creditRecompenseProcess() {
        if (getLoggedAccount() == null) {
            System.out.println("No logged-in credit account.");
            return;
        }
    
        // Create a field validator for positive values
        Field<Double, Double> amountField = new Field<>(
            "Recompense Amount", 
            Double.class, 
            0.01,
            new Field.DoubleFieldValidator()
        );
    
        // Prompt the user for input and validate it
        amountField.setFieldValue("Enter recompense amount: ");
    
        double amount = amountField.getFieldValue();
    
        if (getLoggedAccount().recompense(amount)) {
            getLoggedAccount().addNewTransaction(
                getLoggedAccount().getAccountNumber(), 
                Transaction.Transactions.Recompense, 
                "A successful recompense."
            );
            System.out.println("Recompense successful!");
        } else {
            System.out.println("Recompense unsuccessful!");
        }
    }

    /**
     * Get the Credit Account instance of the currently logged account.
     *
     * @return CreditAccount object
     */
    public static CreditAccount getLoggedAccount() {
        Account account = AccountLauncher.getLoggedAccount();
        if (account == null) {
            System.out.println("No logged-in account.");
            return null;
        }
        
        if (account instanceof CreditAccount) {
            return (CreditAccount) account;
        } else {
            System.out.println("Logged-in account is not a credit account.");
            return null;
        }
    }
}