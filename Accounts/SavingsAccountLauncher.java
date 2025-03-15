package Accounts;
import Bank.Bank;
import Bank.BankLauncher;
import Main.Main;

/**
 * Savings Account Launcher class for handling savings account operations
 */
public class SavingsAccountLauncher extends AccountLauncher {
    /**
     * Method that deals with all things about savings accounts.
     * Mainly utilized for showing the main menu after Savings Account users log in to the application.
     */
    public static void savingsAccountInit() {
        if (!isLoggedIn()) {
            System.out.println("No savings account is currently logged in.");
            return;
        }

        while (true) {
            Main.showMenuHeader("Savings Account Menu");
            Main.showMenu(51);
            Main.setOption();
            Main.showMenu(Main.getOption(), 51);
            
            switch (Main.getOption()) {
                case 1:
                    Main.showMenuHeader("Balance Statement");
                    System.out.println(getLoggedAccount().getAccountBalanceStatement());
                    continue;
                case 2:
                    depositProcess();
                    continue;
                case 3:
                    withdrawProcess();
                    continue;
                case 4:
                    fundTransferProcess();
                    continue;
                case 5:
                    System.out.println(getLoggedAccount().getTransactionsInfo());
                    break;
                case 6:
                    destroyLogSession();
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    /**
     * A method that deals with the deposit process transaction.
     */
    private static void depositProcess() {
        SavingsAccount loggedAccount = getLoggedAccount();

        if (loggedAccount == null) {
            System.out.println("No account found!");
            return;
        }

        double depositAmount;

        while (true) {
            String depositAmount_str = Main.prompt("Enter deposit amount: ", true);

            try {
                depositAmount = Double.parseDouble(depositAmount_str);
                if (depositAmount <= 0) {
                    System.out.println("Invalid amount!");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount!");
            }
        }

        boolean depositSuccess = loggedAccount.cashDeposit(depositAmount);
        if (depositSuccess) {
            System.out.println("Deposit successful!");
            System.out.println("Balance: " + loggedAccount.getAccountBalance());
            getLoggedAccount().addNewTransaction(getLoggedAccount().getAccountNumber(), Transaction.Transactions.Deposit, "A successful deposit.");
        } else {
            System.out.println("Deposit failed. Please try again.");
        }
    }

    /**
     * A method that deals with the withdrawal process transaction.
     */
    public static void withdrawProcess() {
        SavingsAccount loggedAccount = getLoggedAccount();
        // Check if a savings account is logged in
        if (loggedAccount != null) {
            loggedAccount.toString();
            String withdrawAmount = Main.prompt("Enter the amount to withdraw: ", true);
            double amount = Double.parseDouble(withdrawAmount);
                        
            if (amount <= 0) {
                // Check if the withdrawal amount is valid
                System.out.println("Invalid amount. Please enter a positive value.");
                return;
            }
            
            if (loggedAccount.getAccountBalance() >= amount) {
                if (loggedAccount.withdrawal(amount)) { 
                    System.out.println("Balance: " + loggedAccount.getAccountBalance());
                    getLoggedAccount().addNewTransaction(getLoggedAccount().getAccountNumber(), Transaction.Transactions.Withdraw, "A successful withdrawal.");
                } else {
                    System.out.println("Withdrawal failed");
                }
            } else {
                System.out.println("Insufficient balance for withdrawal.");
            }
        } else {
            System.out.println("No account logged in.");
        }
    }

    /**
     * A method that deals with the fund transfer process transaction.
     */
    private static void fundTransferProcess() {
        SavingsAccount loggedAccount = getLoggedAccount();
    
        System.out.println("[1] Internal transfer \n[2] External transfer");
        Main.setOption();
    
        switch (Main.getOption()) {
            case 1:
                String internalAccNum = Main.prompt("Account Number: ", true);
                double internalAmount = Double.parseDouble(Main.prompt("Amount: ", true));
    
                SavingsAccount internalAccount = (SavingsAccount) loggedAccount.getBank().getBankAccount(loggedAccount.getBank(), internalAccNum);
                if (internalAccount != null) {
                    try {
                        if (loggedAccount.transfer(internalAccount, internalAmount)) {
                            getLoggedAccount().addNewTransaction(getLoggedAccount().getAccountNumber(), Transaction.Transactions.FundTransfer, "A successful fund transfer.");
                            System.out.println("Transfer successful. New balance: " + loggedAccount.getAccountBalanceStatement());
                        } else {
                            System.out.println("Transfer unsuccessful!");
                        }
                    } catch (IllegalAccountType e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("Recipient account not found in this bank.");
                }
                break;
    
            case 2:
                int externalBankID = Integer.parseInt(Main.prompt("Bank ID: ", true));
                String externalAccNum = Main.prompt("Account Number: ", true);
                double externalAmount = Double.parseDouble(Main.prompt("Amount: ", true));
    
                for (Bank bank : BankLauncher.getBANKS()) {
                    if (bank.getID() == externalBankID) {
                        Account externalAccount = bank.getBankAccount(bank, externalAccNum);
                        if (externalAccount != null) {
                            try {
                                if (loggedAccount.transfer(bank, externalAccount, externalAmount)) {
                                    getLoggedAccount().addNewTransaction(getLoggedAccount().getAccountNumber(), Transaction.Transactions.FundTransfer, "A successful fund transfer.");
                                    System.out.println("Transfer successful. New balance: " + loggedAccount.getAccountBalanceStatement());
                                } else {
                                    System.out.println("Transfer unsuccessful!");
                                }
                            } catch (IllegalAccountType e) {
                                System.out.println(e.getMessage());
                            }
                        } else {
                            System.out.println("Recipient account not found in the selected bank.");
                        }
                        break;
                    }
                }
                System.out.println("Invalid bank ID.");
                break;
    
            default:
                System.out.println("Invalid option.");
                break;
        }
    }

    /**
     * Get the Savings Account instance of the currently logged account.
     *
     * @return SavingsAccount object
     */
    public static SavingsAccount getLoggedAccount() {
        Account account = AccountLauncher.getLoggedAccount();
        if (account instanceof SavingsAccount) {
            return (SavingsAccount) account;
        } else {
            System.out.println("No logged-in savings account found.");
            return null;
        }
    }
}