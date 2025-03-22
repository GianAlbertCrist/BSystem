package Accounts;

import Bank.*;
import Main.*;
import Processes.FundTransfer;
import Processes.IllegalAccountType;

public class StudentAccountLauncher extends AccountLauncher {

    /**
     * Method that deals with all things about student accounts.
     * Mainly utilized for showing the main menu after Student Account users log in to the application.
     */
    public static void studentAccountInit() throws IllegalAccountType {
        if (getLoggedAccount() == null) {
            System.out.println("No account logged in.");
            return;
        }

        while (true) {
            Main.showMenuHeader("Student Account Menu");
            Main.showMenu(52);
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

            if (!(recipient instanceof SavingsAccount || recipient instanceof StudentAccount)) {
                throw new IllegalAccountType("Cannot transfer funds to a CreditAccount.");
            }

            if (recipientAccountNum.equals(getLoggedAccount().getAccountNumber())) {
                System.out.println("Warning: You are transferring to your own account. Transfer failed.");
            } else if (((StudentAccount) getLoggedAccount()).transfer((StudentAccount) recipient, amount)) {
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

            if (!(recipient instanceof SavingsAccount || recipient instanceof StudentAccount)) {
                System.out.println("Recipient account not found or is not a Savings/Student Account.");
                return;
            }

            if (recipientAccountNum.equals(getLoggedAccount().getAccountNumber())) {
                System.out.println("Warning: You are transferring to your own account. Transfer failed.");
            } else if (((StudentAccount) getLoggedAccount()).transfer((StudentAccount) recipient, amount)) {
                System.out.println("External transfer successful. Processing fee of Php" +
                        getLoggedAccount().getBank().getProcessingFee() + " applied.");
            } else if ((SavingsAccountLauncher.getLoggedAccount()).transfer((SavingsAccount) recipient, amount)) {
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
     * Get the Student Account instance of the currently logged account.
     *
     * @return StudentAccount object
     */
    protected static StudentAccount getLoggedAccount() {
        Account account = AccountLauncher.getLoggedAccount();
        if (account == null) {
            System.out.println("No logged-in account.");
            return null;
        }

        if (account instanceof StudentAccount studentAccount) {
            return studentAccount;
        } else {
            System.out.println("No logged-in student account found.");
            return null;
        }
    }
}