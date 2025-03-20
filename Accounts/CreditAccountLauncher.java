package Accounts;

import Bank.*;
import Main.*;

/**
 * Credit Account Launcher class for handling credit account operations.
 */
public class CreditAccountLauncher extends AccountLauncher {

    /**
     * Method that deals with all things about credit accounts. Mainly utilized for showing the main
     * menu after Credit Account users log in to the application.
     */
    public static void creditAccountInit() {
        if (getLoggedAccount() == null) {
            System.out.println("No account logged in.");
            return;
        }

        while (true) {
            Main.showMenuHeader("Credit Account Menu");
            Main.showMenu(41);
            Main.setOption();

            switch (Main.getOption()) {
                case 1 -> System.out.println(getLoggedAccount().getLoanStatement());
                case 2 -> creditPaymentProcess();
                case 3 -> creditRecompenseProcess();
                case 4 -> System.out.println(getLoggedAccount().getTransactionsInfo());
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
    private static void creditPaymentProcess() {
        Field<String, Integer> recipientField = new Field<String, Integer>("Recipient Account Number", String.class, 5, new Field.StringFieldLengthValidator());
        recipientField.setFieldValue("Enter recipient Savings Account number: ");

        Field<Double, Double> amountField = new Field<Double, Double>("Payment Amount", Double.class, 1.0, new Field.DoubleFieldValidator());
        amountField.setFieldValue("Enter payment amount: ");

        String recipientAccountNum = recipientField.getFieldValue();
        double amount = amountField.getFieldValue();

        Bank recipientBank = getLoggedAccount().getBank();
        Account recipientAccount = recipientBank.getBankAccount(recipientBank, recipientAccountNum);

        if (!(recipientAccount instanceof SavingsAccount)) {
            System.out.println("Recipient account not found or is not a Savings Account.");
            return;
        }

        if (getLoggedAccount().pay(recipientAccount, amount)) {
            System.out.println("Credit payment successful.");
        } else {
            System.out.println("Credit payment failed. Insufficient funds or invalid amount.");
        }

    }

    /**
     * Method that is utilized to process the credit compensation transaction.
     */
    private static void creditRecompenseProcess() {
        Field<Double, Double> amountField = new Field<Double, Double>("Recompense Amount", Double.class, 1.0, new Field.DoubleFieldValidator());
        amountField.setFieldValue("Enter recompense amount: ");

        double amount = amountField.getFieldValue();

        if (getLoggedAccount().recompense(amount)) {
            System.out.println("Recompense successful.");
        } else {
            System.out.println("Recompense failed. Amount exceeds loan balance.");
        }
    }

    /**
     * Get the Credit Account instance of the currently logged account.
     * @return The currently logged account
     */
    public static CreditAccount getLoggedAccount() {
        Account account = AccountLauncher.getLoggedAccount();
        if (account == null) {
            System.out.println("No logged-in account.");
            return null;
        }

        if (account instanceof CreditAccount creditAccount) {
            return creditAccount;
        } else {
            System.out.println("Logged-in account is not a credit account.");
            return null;
        }
    }
}