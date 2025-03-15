package Bank;
import Accounts.Account;
import Accounts.SavingsAccount;
import Accounts.CreditAccount;
import Main.Field;
import java.util.ArrayList;
import java.util.Comparator;

public class Bank {
    private final int ID;
    private String name, passcode;
    private final double DEPOSITLIMIT, WITHDRAWLIMIT, CREDITLIMIT;
    private double processingFee;
    private final ArrayList <Account> BANKACCOUNTS = new ArrayList<>();

    public Bank(int ID, String name, String passcode) {
        this.ID = ID;
        this.name = name;
        this.passcode = passcode;
        this.DEPOSITLIMIT = 50000.0;
        this.WITHDRAWLIMIT = 50000.0;
        this.CREDITLIMIT = 100000.0;
        this.processingFee = 10.0;

    }
    
    public Bank(int ID, String name, String passcode, double depositLimit, double withdrawLimit, double creditLimit, double processingFee) {
        this.ID = ID;
        this.name = name;
        this.passcode = passcode;
        this.DEPOSITLIMIT = depositLimit;
        this.WITHDRAWLIMIT = withdrawLimit;
        this.CREDITLIMIT = creditLimit;
        this.processingFee = processingFee;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public double getDepositLimit() {
        return DEPOSITLIMIT;
    }

    public double getWithdrawLimit() {
        return WITHDRAWLIMIT;
    }

    public double getCreditLimit() {
        return CREDITLIMIT;
    }

    public double getProcessingFee() {
        return processingFee;
    }

    public ArrayList<Account> getBANKACCOUNTS() {
        return BANKACCOUNTS;
    }

    public void showAccounts(Class<? extends Account> accountType) {
        if (accountType == Account.class) {
            for (Account account : getBANKACCOUNTS()) {
                System.out.println(account);
            }
            return;
        }
        for (Account account : getBANKACCOUNTS()) {
            if (account.getClass() == accountType) {
                System.out.println(account);
            }
        }
    }

    public Account getBankAccount(Bank bank, String accountNum) {
        for (Account account : BANKACCOUNTS) {
            if (account.getAccountNumber().equals(accountNum)) {
                return account;
            }
        }
        return null;
    }

    public ArrayList<Field<String, ?>> createNewAccount() {
        ArrayList<Field<String, ?>> createNew = new ArrayList<>();

        Field<String, Integer> accountNum = new Field<String, Integer>("Account Number", String.class, 4, new Field.StringFieldLengthValidator());
        Field<String, Integer> fNameField = new Field<String, Integer>("FIrst Name", String.class, 3, new Field.StringFieldLengthValidator());
        Field<String, Integer> lNameField = new Field<String, Integer>("Last name", String.class, 3, new Field.StringFieldLengthValidator());
        Field<String, Integer> emailField = new Field<String, Integer>("Email", String.class, 12, new Field.StringFieldLengthValidator());
        Field<String, Integer> pinField = new Field<String, Integer>("Pin", String.class, 4, new Field.StringFieldLengthValidator());

        accountNum.setFieldValue("Enter account number: ");
        createNew.add(accountNum);
        fNameField.setFieldValue("Enter first name: ");
        createNew.add(fNameField);
        lNameField.setFieldValue("Enter last name: ");
        createNew.add(lNameField);
        emailField.setFieldValue("Enter email: ");
        createNew.add(emailField);
        pinField.setFieldValue("Enter pin (4 digits): ");
        createNew.add(pinField);

        return createNew;
    }

    public CreditAccount createNewCreditAccount() {
        ArrayList<Field<String, ?>> fields = createNewAccount();    
        Bank bank = BankLauncher.getLoggedBank();
        CreditAccount credit;

        String accountNum = fields.get(0).getFieldValue();
        String firstName = fields.get(1).getFieldValue();
        String lastName = fields.get(2).getFieldValue();
        String email = fields.get(3).getFieldValue();
        String pin = fields.get(4).getFieldValue();

        credit = new CreditAccount(bank, accountNum, firstName, lastName, email, pin);
        return credit;
    }

    public SavingsAccount createNewSavingsAccount() {
        ArrayList<Field<String, ?>> fields = createNewAccount();    
        Bank bank = BankLauncher.getLoggedBank();
        SavingsAccount savings;
    
        String accountNum = fields.get(0).getFieldValue();
        String firstName = fields.get(1).getFieldValue();
        String lastName = fields.get(2).getFieldValue();
        String email = fields.get(3).getFieldValue();
        String pin = fields.get(4).getFieldValue();
        
        while (true) {
            Field<Double, Double> balField = new Field<Double,Double>("Credit", Double.class, 500.0, new Field.DoubleFieldValidator());
            balField.setFieldValue("Enter deposit amount: ", true);
            if (balField.getFieldValue() <= this.DEPOSITLIMIT) {
                double balance = balField.getFieldValue();
                savings = new SavingsAccount(bank, accountNum, firstName, lastName, email, pin, balance);
                addNewAccount(savings);
                return savings;
            } else {
                System.out.println("Deposit must be less than " + this.DEPOSITLIMIT);
            }
        }
    }
    public void addNewAccount(Account account) {
        boolean exists = accountExist(account.getBank(), account.getAccountNumber());
        if (!exists) {
            BANKACCOUNTS.add(account);
            System.out.println("New account added successfully!");
        } else {
            System.out.println("Account number already exists in the bank. Cannot add duplicate account.");
        }
    }

    public static boolean accountExist(Bank bank, String accountNum) {
        ArrayList<Account> accounts = bank.getBANKACCOUNTS();

        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNum)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Bank Info:\n%s\n%s\n%s", this.getID(), this.getName(), this.getPasscode());
    }

    public class BankComparator implements Comparator<Bank> {
        /**
         * This compares two Bank objects
         * 
         * b1 - first bank object to compare
         * b2 - second bank object to compare
         */
        @Override
        public int compare(Bank b1, Bank b2) {
            // Compare IDs first
            if (b1.getID() != b2.getID()) {
                return Integer.compare(b1.getID(), b2.getID());
            }
            
            // If IDs are the same, compare names
            int nameComparison = b1.getName().compareTo(b2.getName());
            if (nameComparison != 0) {
                return nameComparison;
            }
            
            // If names are the same, compare passcodes
            return b1.getPasscode().compareTo(b2.getPasscode());
        }
    }

    public static class BankIdComparator implements Comparator<Bank> {
        @Override
        public int compare(Bank b1, Bank b2) {
            return Integer.compare(b1.getID(), b2.getID());
        }
    }

    public class BankCredentialsComparator implements Comparator<Bank> {
        @Override
        public int compare(Bank b1, Bank b2) {
            // Compare the first account of each bank (assuming at least one account exists)
            if (b1.getBANKACCOUNTS().isEmpty() || b2.getBANKACCOUNTS().isEmpty()) {
                return Integer.compare(b1.getBANKACCOUNTS().size(), b2.getBANKACCOUNTS().size());
            }
    
            Account account1 = b1.getBANKACCOUNTS().get(0);
            Account account2 = b2.getBANKACCOUNTS().get(0);
    
            // Compare by first name, last name, and email
            int firstNameComparison = account1.getOwnerFname().compareTo(account2.getOwnerFname());
            if (firstNameComparison != 0) {
                return firstNameComparison;
            }
    
            int lastNameComparison = account1.getOwnerLname().compareTo(account2.getOwnerLname());
            if (lastNameComparison != 0) {
                return lastNameComparison;
            }
    
            return account1.getOwnerEmail().compareTo(account2.getOwnerEmail());
        }
    }
} 

