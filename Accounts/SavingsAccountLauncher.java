package Accounts;

import Bank.*;
import Main.*;
import Processes.IllegalAccountType;
import Processes.TransactionManager;

/**
 * Savings Account Launcher class for handling savings account operations
 */
public class SavingsAccountLauncher {

    /**
     * Method that deals with all things about savings accounts.
     * Mainly utilized for showing the main menu after Savings Account users log in to the application.
     */
    public static void savingsAccountInit() throws IllegalAccountType {
        if (getLoggedAccount() == null) {
            System.out.println("No account logged in.");
            return;
        }

        while (true) {
            Main.showMenuHeader("Savings Account Menu");
            Main.showMenu(51);
            Main.setOption();

            switch (Main.getOption()) {
                case 1 -> System.out.println(getLoggedAccount().getAccountBalanceStatement());
                case 2 -> depositProcess();
                case 3 -> withdrawProcess();
                case 4 -> fundTransfer();
                case 5 -> System.out.println(getLoggedAccount().getTransactionsInfo());
                case 6 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    /**
     * A method that deals with the deposit process transaction.
     */
    private static void depositProcess() {
        Field<Double, Double> amountField = new Field<Double, Double>("Deposit Amount", Double.class, 1.0, new Field.DoubleFieldValidator());
        amountField.setFieldValue("Enter deposit amount: ");

        double amount = amountField.getFieldValue();
        if (getLoggedAccount().cashDeposit(amount)) {
            System.out.println("Deposit successful.");
        } else {
            System.out.println("Deposit failed. Amount exceeds limit or is invalid.");
        }
    }

    /**
     * A method that deals with the withdrawal process transaction.
     */
    private static void withdrawProcess() {
        Field<Double, Double> amountField = new Field<Double, Double>("Withdrawal Amount", Double.class, 1.0, new Field.DoubleFieldValidator());
        amountField.setFieldValue("Enter withdrawal amount: ");

        double amount = amountField.getFieldValue();
        if (getLoggedAccount().withdrawal(amount)) {
            System.out.println("Withdrawal successful.");
        } else {
            System.out.println("Withdrawal failed. Insufficient balance or exceeds withdrawal limit.");
        }
    }

    /**
     * A method that deals with the fund transfer process transaction.
     */
    private static void fundTransfer() throws IllegalAccountType {
        if (getLoggedAccount() == null) {
            System.out.println("No account logged in.");
            return;
        }

        // Prompt user for transaction type (Internal or External)
        Main.showMenuHeader("Fund Transfer Type");
        System.out.println("[1] Internal Transfer (Account Within this Bank)");
        System.out.println("[2] External Transfer (Account from Different Bank)");
        Main.setOption();
        int transferType = Main.getOption();

        // Get recipient account number
        Field<String, Integer> recipientField = new Field<String, Integer>("Recipient Account Number", String.class, 5, new Field.StringFieldLengthValidator());
        recipientField.setFieldValue("Enter recipient account number: ");
        String recipientAccountNum = recipientField.getFieldValue();

        // Get transfer amount
        Field<Double, Double> amountField = new Field<Double, Double>("Transfer Amount", Double.class, 1.0, new Field.DoubleFieldValidator());
        amountField.setFieldValue("Enter transfer amount: ");
        double amount = amountField.getFieldValue();

        if (transferType == 1) {
            Account recipient = getLoggedAccount().getBank().getBankAccount(getLoggedAccount().getBank(), recipientAccountNum);

            if (!(recipient instanceof SavingsAccount)) {
                throw new IllegalAccountType("Cannot transfer funds to a CreditAccount.");
            }

            if (recipientAccountNum.equals(getLoggedAccount().getAccountNumber())) {
                System.out.println("Warning: You are transferring to your own account. Transfer failed.");
            } else if (getLoggedAccount().transfer(recipient, amount)) {
                System.out.println("Internal transfer successful.");
            } else {
                System.out.println("Transfer failed. Insufficient funds or limit exceeded.");
            }

         // External Transfer
        } else if (transferType == 2) {
            // Get recipient Bank ID instead of name
            Field<Integer, Integer> recipientBankField = new Field<Integer, Integer>("Recipient Bank ID", Integer.class, -1, new Field.IntegerFieldValidator());
            recipientBankField.setFieldValue("Enter recipient bank ID: ");
            int recipientBankId = recipientBankField.getFieldValue();

            Bank recipientBank = null;
            for (Bank bank : BankLauncher.getBanks()) {
                if (bank.getBankId() == recipientBankId) {
                    recipientBank = bank;
                    break;
                }
            }

            if (recipientBank == null) {
                System.out.println("Recipient bank not found.");
                return;
            }

            Account recipient = recipientBank.getBankAccount(recipientBank, recipientAccountNum);

            if (!(recipient instanceof SavingsAccount)) {
                System.out.println("Recipient account not found or is not a Savings Account.");
                return;
            }

            if (getLoggedAccount().transfer(recipientBank, recipient, amount)) {
                System.out.println("External transfer successful. Processing fee of Php" +
                        getLoggedAccount().getBank().getProcessingFee() + " applied.");
            } else {
                System.out.println("Transfer failed. Insufficient funds or limit exceeded.");
            }

        } else {
            System.out.println("Invalid selection.");
        }
    }

    /**
     * Get the Savings Account instance of the currently logged account.
     *
     * @return SavingsAccount object
     */
    protected static SavingsAccount getLoggedAccount() {
        Account account = AccountLauncher.getLoggedAccount();
        if (account == null) {
            System.out.println("No logged-in account.");
            return null;
        }

        if (account instanceof SavingsAccount savingsAccount) {
            return savingsAccount;
        } else {
            System.out.println("No logged-in savings account found.");
            return null;
        }
    }
}