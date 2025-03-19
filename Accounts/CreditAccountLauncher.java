package Accounts;

import Bank.*;
import Main.*;

/**
 * Credit Account Launcher class for handling credit account operations.
 */
public class CreditAccountLauncher {

    private static CreditAccount loggedAccount;

    /**
     * Method that deals with all things about credit accounts. Mainly utilized for showing the main
     * menu after Credit Account users log in to the application.
     */
    public static void creditAccountInit() {
        if (loggedAccount == null) {
            System.out.println("No account logged in.");
            return;
        }

        while (true) {
            Main.showMenuHeader("Credit Account Menu");
            Main.showMenu(41);
            Main.setOption();

            switch (Main.getOption()) {
                case 1 -> System.out.println(loggedAccount.getLoanStatement());
                case 2 -> creditPaymentProcess();
                case 3 -> creditRecompenseProcess();
                case 4 -> System.out.println(loggedAccount.getTransactionsInfo());
                case 5 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    /**
     * Method that is utilized to process the credit payment transaction.
     */
    public static void creditPaymentProcess() {
        Field<String, Integer> recipientField = new Field<String, Integer>("Recipient Account Number", String.class, 5, new Field.StringFieldLengthValidator());
        recipientField.setFieldValue("Enter recipient Savings Account number: ");

        Field<Double, Double> amountField = new Field<Double, Double>("Payment Amount", Double.class, 1.0, new Field.DoubleFieldValidator());
        amountField.setFieldValue("Enter payment amount: ");

        String recipientAccountNum = recipientField.getFieldValue();
        double amount = amountField.getFieldValue();

        Bank recipientBank = loggedAccount.getBank();
        Account recipientAccount = recipientBank.getBankAccount(recipientAccountNum);

        if (!(recipientAccount instanceof SavingsAccount)) {
            System.out.println("Recipient account not found or is not a Savings Account.");
            return;
        }

        if (loggedAccount.pay(recipientAccount, amount)) {
            System.out.println("Credit payment successful.");
        } else {
            System.out.println("Credit payment failed. Insufficient funds or invalid amount.");
        }

    }

    /**
     * Method that is utilized to process the credit compensation transaction.
     */
    public static void creditRecompenseProcess() {
        Field<Double, Double> amountField = new Field<Double, Double>("Recompense Amount", Double.class, 1.0, new Field.DoubleFieldValidator());
        amountField.setFieldValue("Enter recompense amount: ");

        double amount = amountField.getFieldValue();

        if (loggedAccount.recompense(amount)) {
            System.out.println("Recompense successful.");
        } else {
            System.out.println("Recompense failed. Amount exceeds loan balance.");
        }
    }

    /**
     * Get the Credit Account instance of the currently logged account.
     * @return The currently logged account
     */
    private static CreditAccount getLoggedAccount() {
        return loggedAccount;
    }

    /**
     * Sets the currently logged-in Credit Account.
     *
     * @param account The logged-in CreditAccount.
     */
    public static void setLoggedAccount(CreditAccount account) {
        loggedAccount = account;
    }
}