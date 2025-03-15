package Accounts;
import Bank.Bank;
import java.util.ArrayList;

public abstract class Account {
    private final Bank BANK;
    private final String ACCOUNTNUMBER, OWNERFNAME, OWNERLNAME, OWNEREMAIL;
    private String pin;
    private final ArrayList<Transaction> TRANSACTIONS = new ArrayList<>();

    public Account(Bank bank, String accountNumber, String ownerFName, String ownerLName, String ownerEmail, String pin) {
        this.BANK = bank;
        this.ACCOUNTNUMBER = accountNumber;
        this.OWNERFNAME = ownerFName;
        this.OWNERLNAME = ownerLName;
        this.OWNEREMAIL = ownerEmail;
        this.pin = pin;
    }

    public String getAccountNumber(){
        return ACCOUNTNUMBER;
    }

    public String getPin(){
        return pin;
    }

    public Bank getBank() {
        return BANK;
    }

    public String getOwnerFname() {
        return OWNERFNAME;
    }

    public String getOwnerLname() {
        return OWNERLNAME;
    }

    public String getOwnerEmail() {
        return OWNEREMAIL;
    }

    public ArrayList<Transaction> getTRANSACTIONS() {
        return TRANSACTIONS;
    }

    public String getOwnerFullName() {
        return String.format("%s %s",this.OWNERFNAME, this.OWNERLNAME);
    }

    public void addNewTransaction(String accountNum, Transaction.Transactions type, String description) {
        TRANSACTIONS.add(new Transaction(accountNum, type, description));
    }

    public String getTransactionsInfo() {
        StringBuilder transactionDetails = new StringBuilder();
        transactionDetails.append("Transaction History:\n");
        for (Transaction transaction : TRANSACTIONS) {
            transactionDetails.append(transaction.toString()).append("\n");
        }
        return transactionDetails.toString();
    }

    @Override
    public String toString() {
        return String.format("Bank: %s\nAccount Number: %s\nOwner: %s\nEmail: %s",
                              BANK, ACCOUNTNUMBER, getOwnerFullName(), OWNEREMAIL);
    }
}
