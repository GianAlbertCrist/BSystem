package Main;

import Accounts.AccountLauncher;
import Processes.IllegalAccountType;
import Processes.Transaction;
import Bank.*;
import java.util.Scanner;
import java.io.File;

public class Main
{

    private static final Scanner input = new Scanner(System.in);
    /**
     * Option field used when selection options during menu prompts. Do not create a different
     * option variable in menus. Just use this instead. <br>
     * As to how to utilize Field objects properly, refer to the following:
     *
     * @see #prompt(String, boolean)
     * @see #setOption() How Field objects are used.
     */
    public static Field<Integer, Integer> option = new Field<Integer, Integer>("Option",
            Integer.class, -1, new Field.IntegerFieldValidator());

    public static void main(String[] args) throws IllegalAccountType {
        // Load banks, accounts, and transactions from JSON files if they exist
        if (new File("Database/Banks.json").exists()) {
            BankLauncher.loadBanks();
            System.out.println("Banks loaded.");
        }
        if (new File("Database/Accounts.json").exists()) {
            AccountLauncher.loadAccounts();
            System.out.println("Accounts loaded.");
        }
        if (new File("Database/Transactions.json").exists()) {
            Transaction.loadTransactions();
        }

        while (true)
        {
            showMenuHeader("Main Menu");
            showMenu(1);
            setOption();

            // Account Option
            switch (getOption()) {
                //Accounts Login
                case 1:
                    showMenuHeader("Account Login");
                    showMenu(2);
                    setOption();

                    if (getOption() == 1) {
                        Bank selectedBank = AccountLauncher.selectBank();
                        
                        AccountLauncher accountLauncher = new AccountLauncher();
                        accountLauncher.setAssocBank(selectedBank);
                        accountLauncher.accountLogin();
                    }   break;

                //Bank Login
                case 2:
                    showMenuHeader("Bank Operations");
                    showMenu(3);
                    setOption();
                    switch (getOption()) {
                        case 1 -> BankLauncher.bankLogin();
                        case 2 -> System.out.println("Exiting Bank Operations");
                        default -> System.out.println("Invalid bank menu option.");
                    }   break;

                // Create New Bank
                case 3:
                    showMenuHeader("Create New Bank");
                    BankLauncher.createNewBank();
                    break;
                // Exit
                case 4:
                    System.out.println("Exiting. Thank you for banking!");
                    //Saving the latest state of banks, accounts, and transactions to JSON files
                    Transaction.saveTransactions();
                    AccountLauncher.saveAccounts();
                    BankLauncher.saveBanks();
                    System.exit(0);

                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }
    }

    /**
     * Show menu based on index given. <br>
     * Refer to Menu enum for more info about menu indexes. <br>
     * Use this method if you want a single menu option every line.
     *
     * @param menuIdx Main.Menu index to be shown
     */
    public static void showMenu(int menuIdx)
    {
        showMenu(menuIdx, 1);
    }

    /**
     * Show menu based on index given. <br>
     * Refer to Menu enum for more info about menu indexes.
     *
     * @param menuIdx Main.Menu index to be shown
     * @param inlineTexts Number of menu options in a single line. Set to 1 if you only want a
     *        single menu option every line.
     * @see Menu
     */
    public static void showMenu(int menuIdx, int inlineTexts)
    {
        String[] menu = Menu.getMenuOptions(menuIdx);
        if (menu == null)
        {
            System.out.println("Invalid menu index given!");
        }
        else
        {
            String space = inlineTexts == 0 ? "" : "%-20s";
            String fmt = "[%d] " + space;
            int count = 0;
            for (String s : menu)
            {
                count++;
                System.out.printf(fmt, count, s);
                if (count % inlineTexts == 0)
                {
                    System.out.println();
                }
            }
        }
    }

    /**
     * Prompt some input to the user. Only receives on non-space containing String. This string can
     * then be parsed into targeted data type using DataTypeWrapper.parse() method.
     *
     * @param phrase Prompt to the user.
     * @param inlineInput A flag to ask if the input is just one entire String or receive an entire
     *        line input. <br>
     *        Set to <b>true</b> if receiving only one String input without spaces. <br>
     *        Set to <b>false</b> if receiving an entire line of String input.
     * @return Value of the user's input.
     * @see Field#setFieldValue(String, boolean) How prompt is utilized in Field.
     */
    public static String prompt(String phrase, boolean inlineInput)
    {
        System.out.print(phrase);
        if (inlineInput)
        {
            String val = input.next();
            input.nextLine();
            return val;
        }
        return input.nextLine();
    }

    /**
     * Prompts user to set an option based on menu outputted.
     *
     * @throws NumberFormatException May happen if the user attempts to input something other than
     *         numbers.
     */
    public static void setOption() throws NumberFormatException
    {
        option.setFieldValue("Select an option: ");
    }

    /**
     * @return Recently inputted option by the user.
     */
    public static int getOption()
    {
        return Main.option.getFieldValue();
    }

    /**
     * Used for printing the header whenever a new menu is accessed.
     *
     * @param menuTitle Title of the menu to be outputted.
     */
    public static void showMenuHeader(String menuTitle)
    {
        System.out.printf("<---- %s ----->\n", menuTitle);
    }
}
