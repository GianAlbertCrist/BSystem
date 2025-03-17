package Accounts;

import Bank.BankLauncher;
import Bank.Bank;
import Main.*;

/**
 * SavingsAccountLauncher handles user interactions for Savings Accounts,
 * allowing deposits, withdrawals, and fund transfers.
 */
public class SavingsAccountLauncher {

    private static SavingsAccount loggedAccount;

    /**
     * Initializes the Savings Account menu after login.
     */
    public static void savingsAccountInit() throws IllegalAccountType {
        if (loggedAccount == null) {
            System.out.println("No account logged in.");
            return;
        }

        while (true) {
            Main.showMenuHeader("Savings Account Menu");
            Main.showMenu(51);
            Main.setOption();

            switch (Main.getOption()) {
                case 1 -> System.out.println(loggedAccount.getAccountBalanceStatement());
                case 2 -> depositProcess();
                case 3 -> withdrawProcess();
                case 4 -> fundTransfer();
                case 5 -> System.out.println(loggedAccount.getTransactionsInfo());
                case 6 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    /**
     * Handles the deposit process.
     */
    public static void depositProcess() {
        Field<Double, Double> amountField = new Field<Double, Double>("Deposit Amount", Double.class, 1.0, new Field.DoubleFieldValidator());
        amountField.setFieldValue("Enter deposit amount: ");

        double amount = amountField.getFieldValue();
        if (loggedAccount.cashDeposit(amount)) {
            System.out.println("Deposit successful.");
        } else {
            System.out.println("Deposit failed. Amount exceeds limit or is invalid.");
        }
    }

    /**
     * Handles the withdrawal process.
     */
    public static void withdrawProcess() {
        Field<Double, Double> amountField = new Field<Double, Double>("Withdrawal Amount", Double.class, 1.0, new Field.DoubleFieldValidator());
        amountField.setFieldValue("Enter withdrawal amount: ");

        double amount = amountField.getFieldValue();
        if (loggedAccount.withdrawal(amount)) {
            System.out.println("Withdrawal successful.");
        } else {
            System.out.println("Withdrawal failed. Insufficient balance or exceeds withdrawal limit.");
        }
    }

    public static void fundTransfer() throws IllegalAccountType {
        if (loggedAccount == null) {
            System.out.println("No account logged in.");
            return;
        }

        // Prompt user for transaction type (Internal or External)
        Main.showMenuHeader("Fund Transfer Type");
        System.out.println("[1] Internal Transfer (Same Bank)");
        System.out.println("[2] External Transfer (Different Bank)");
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

        if (transferType == 1) { // Internal Transfer
            Account recipient = loggedAccount.getBank().getBankAccount(recipientAccountNum);

            if (!(recipient instanceof SavingsAccount)) {
                System.out.println("❌ Recipient account not found or is not a Savings Account.");
                return;
            }

            if (loggedAccount.transfer(recipient, amount)) {
                System.out.println("✅ Internal transfer successful.");
            } else {
                System.out.println("❌ Transfer failed. Insufficient funds or limit exceeded.");
            }


        } else if (transferType == 2) { // External Transfer
            // Get recipient Bank ID instead of name
            Field<Integer, Integer> recipientBankField = new Field<Integer, Integer>("Recipient Bank ID", Integer.class, 1, new Field.IntegerFieldValidator());
            recipientBankField.setFieldValue("Enter recipient bank ID: ");
            int recipientBankId = recipientBankField.getFieldValue();

            Bank recipientBank = null;
            for (Bank bank : BankLauncher.getBanks()) {
                if (bank.getBankId() == recipientBankId) {  // ✅ Use == for primitive int comparison
                    recipientBank = bank;
                    break;
                }
            }

            if (recipientBank == null) {
                System.out.println("❌ Recipient bank not found.");
                return;
            }

            Account recipient = recipientBank.getBankAccount(recipientAccountNum);

            if (!(recipient instanceof SavingsAccount)) {
                System.out.println("❌ Recipient account not found or is not a Savings Account.");
                return;
            }

            if (loggedAccount.transfer(recipientBank, recipient, amount)) {
                System.out.println("✅ External transfer successful. Processing fee of $" +
                        loggedAccount.getBank().getProcessingFee() + " applied.");
            } else {
                System.out.println("❌ Transfer failed. Insufficient funds or limit exceeded.");
            }

        } else {
            System.out.println("❌ Invalid selection.");
        }
    }


    /**
     * Sets the currently logged-in Savings Account.
     *
     * @param account The logged-in SavingsAccount.
     */
    public static void setLoggedAccount(SavingsAccount account) {
        loggedAccount = account;
    }

    /**
     * Gets the currently logged-in Savings Account.
     *
     * @return The logged-in SavingsAccount.
     */
    public SavingsAccount getLoggedAccount() {
        return loggedAccount;
    }
}