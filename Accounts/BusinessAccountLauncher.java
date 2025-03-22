package Accounts;

import Bank.*;
import Main.*;
import Processes.IllegalAccountType;
import Processes.TransactionManager;

public class BusinessAccountLauncher extends AccountLauncher {

    /**
     * Initializes the Business Account Menu for the logged-in account.
     * 
     * This method displays a menu with options for the user to interact with their
     * business account. The available options include viewing the account balance
     * statement, depositing funds, withdrawing funds, transferring funds, and
     * viewing transaction information. The menu will continue to be displayed
     * until the user chooses to exit.
     * 
     * @throws IllegalAccountType if the logged-in account is not a business account.
     */
    public static void BusinessAccountInit () throws IllegalAccountType {
        if (getLoggedAccount() == null) {
            System.out.println("No account logged in.");
            return;
        }

        while (true) {
            Main.showMenuHeader("Business Account Menu");
            Main.showMenu(61);
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
     * Handles the deposit process for the logged-in account.
     * Prompts the user to enter a deposit amount and validates the input.
     * If the deposit is successful, a success message is printed.
     * If the deposit fails due to exceeding the limit or invalid input, a failure message is printed.
     */
    private static void depositProcess() {
        // Create a field for deposit amount with a minimum value of 1.0 and a validator for double values
        Field<Double, Double> amountField = new Field<Double, Double>("Deposit Amount", Double.class, 1.0, new Field.DoubleFieldValidator());
        
        // Prompt the user to enter the deposit amount
        amountField.setFieldValue("Enter deposit amount: ");

        // Retrieve the entered deposit amount
        double amount = amountField.getFieldValue();
        
        // Attempt to deposit the entered amount into the logged-in account
        if (getLoggedAccount().cashDeposit(amount)) {
            // If the deposit is successful, print a success message
            System.out.println("Deposit successful.");
        } else {
            // If the deposit fails, print a failure message
            System.out.println("Deposit failed. Amount exceeds limit or is invalid.");
        }
    }

    /**
     * Processes a withdrawal from the logged-in account.
     * Prompts the user to enter a withdrawal amount and attempts to withdraw
     * the specified amount from the account. If the withdrawal is successful,
     * a success message is printed. Otherwise, an error message is displayed
     * indicating insufficient balance or exceeding the withdrawal limit.
     */
    private static void withdrawProcess() {
        // Create a field for withdrawal amount with a minimum value of 1.0 and a validator for double values
        Field<Double, Double> amountField = new Field<Double, Double>("Withdrawal Amount", Double.class, 1.0, new Field.DoubleFieldValidator());
        
        // Prompt the user to enter the withdrawal amount
        amountField.setFieldValue("Enter withdrawal amount: ");

        // Retrieve the entered withdrawal amount
        double amount = amountField.getFieldValue();
        
        // Attempt to withdraw the entered amount from the logged-in account
        if (getLoggedAccount().withdrawal(amount)) {
            // If the withdrawal is successful, print a success message
            System.out.println("Withdrawal successful.");
        } else {
            // If the withdrawal fails, print a failure message
            System.out.println("Withdrawal failed. Insufficient balance or exceeds withdrawal limit.");
        }
    }

    /**
     * Processes a fund transfer for the logged-in account.
     * This method handles both internal and external transfers.
     * It prompts the user to select the transfer type, recipient account number, and transfer amount.
     * It then validates the input and performs the transfer accordingly.
     * If the transfer is successful, a success message is printed.
     * If the transfer fails due to insufficient funds, invalid input, or exceeding the transfer limit,
     * an error message is displayed.
     *
     * @throws IllegalAccountType if the recipient account is not a business account.
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

            if (!(recipient instanceof BusinessAccount)) {
                throw new IllegalAccountType("Cannot transfer funds to other Accounts other than a Business Account.");
            }

            if (recipientAccountNum.equals(getLoggedAccount().getAccountNumber())) {
                System.out.println("Warning: You are transferring to your own account. Transfer failed.");
            } else if (((BusinessAccount) getLoggedAccount()).transfer(recipient, amount)) {
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

            if (!(recipient instanceof BusinessAccount)) {
                System.out.println("Recipient account not found or is not a Savings Account or in Credit Account.");
                return;
            }

            if (((BusinessAccount) getLoggedAccount()).transfer(recipientBank, recipient, amount)) {
                System.out.println("External transfer successful. Processing fee of Php" +
                        getLoggedAccount().getBank().getProcessingFee() + " applied.");
            } else {
                System.out.println("Transfer failed. Insufficient funds or limit exceeded.");
            }

        } else {
            System.out.println("Invalid selection.");
        }
    }

    protected static BusinessAccount getLoggedAccount() {
        Account account = AccountLauncher.getLoggedAccount();
        if (account == null) {
            System.out.println("No logged-in account.");
            return null;
        }

        if (account instanceof BusinessAccount BusinessAccount) {
            return BusinessAccount;
        } else {
            System.out.println("No logged-in business account found.");
            return null;
        }
    }
    
}