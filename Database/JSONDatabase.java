package Database;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Accounts.Account;
import Bank.Bank;
import Bank.BankLauncher;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides methods for loading and saving JSON data.
 */
public class JSONDatabase {

    // Logger instance for logging errors
    private static final Logger LOGGER = Logger.getLogger(JSONDatabase.class.getName());

    /**
     * Loads JSON data from a file.
     *
     * @param filename The path to the JSON file.
     * @return A JSONArray containing the loaded data. If an error occurs during loading, an empty JSONArray is returned.
     */
    public static JSONArray load(String filename) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(filename)) {
            // Parse the JSON file and return the JSONArray
            Object obj = parser.parse(reader);
            return (JSONArray) obj;
        } catch (IOException | ParseException e) {
            // Log the error and return an empty JSONArray
            LOGGER.log(Level.SEVERE, "Error loading JSON file", e);
            return new JSONArray();
        }
    }

    /**
     * Saves JSON data to a file.
     *
     * @param items The JSONArray containing the data to be saved.
     * @param filename The path to the JSON file.
     */
    public static void save(JSONArray items, String filename) {
        try (FileWriter file = new FileWriter(filename)) {
            // Write the JSON data to the file
            file.write(items.toJSONString());
            file.flush();
        } catch (IOException e) {
            // Log the error
            LOGGER.log(Level.SEVERE, "Error saving JSON file", e);
        }
    }

    public static JSONObject dataToDict(Object data) {
        JSONObject jsonObject = new JSONObject();
        if (data instanceof Bank bank) {
            jsonObject.put("bankId", bank.getBankId());
            jsonObject.put("bankName", bank.getName());
            jsonObject.put("passcode", bank.getPasscode());
            jsonObject.put("depositLimit", bank.getDepositLimit());
            jsonObject.put("withdrawLimit", bank.getWithdrawLimit());
            jsonObject.put("creditLimit", bank.getCreditLimit());
            jsonObject.put("processingFee", bank.getProcessingFee());

            JSONArray accountsArray = new JSONArray();
            for (Account account : bank.getBankAccounts()) {
                accountsArray.add(dataToDict(account));
            }
            jsonObject.put("accounts", accountsArray);

        } else if (data instanceof Account account) {
            jsonObject.put("accountNumber", account.getAccountNumber());
            jsonObject.put("ownerFname", account.getOwnerFname());
            jsonObject.put("ownerLname", account.getOwnerLname());
            jsonObject.put("ownerEmail", account.getOwnerEmail());
            jsonObject.put("pin", account.getPin());

            JSONArray transactionsArray = new JSONArray();
            for (Transaction transaction : account.getTransactions()) {
                transactionsArray.add(dataToDict(transaction));
            }
            jsonObject.put("transactions", transactionsArray);
            
        } else if (data instanceof Transaction transaction) {
            jsonObject.put("accountNum", transaction.getAccountNum());
            jsonObject.put("type", transaction.getType().toString());
            jsonObject.put("description", transaction.getDescription());
        }
        return jsonObject;
    }

    public static Object dataFromDict(JSONObject jsonObject, Class<?> clazz) {
        if (clazz == Bank.class) {
            int bankId = ((Long) jsonObject.get("bankId")).intValue();
            String bankName = (String) jsonObject.get("bankName");
            String passcode = (String) jsonObject.get("passcode");
            double depositLimit = (Double) jsonObject.get("depositLimit");
            double withdrawLimit = (Double) jsonObject.get("withdrawLimit");
            double creditLimit = (Double) jsonObject.get("creditLimit");
            double processingFee = (Double) jsonObject.get("processingFee");

            Bank bank = new Bank(bankId, bankName, passcode, depositLimit, withdrawLimit, creditLimit, processingFee);

            JSONArray accountsArray = (JSONArray) jsonObject.get("accounts");
            for (Object obj : accountsArray) {
                JSONObject accountObject = (JSONObject) obj;
                Account account = (Account) dataFromDict(accountObject, Account.class);
                bank.addNewAccount(account);
            }
            return bank;

        } else if (clazz == Account.class) {
            String accountNumber = (String) jsonObject.get("accountNumber");
            String ownerFname = (String) jsonObject.get("ownerFname");
            String ownerLname = (String) jsonObject.get("ownerLname");
            String ownerEmail = (String) jsonObject.get("ownerEmail");
            String pin = (String) jsonObject.get("pin");

            Bank bank = BankLauncher.findAccount(accountNumber);
            Account account = new Account(bank, accountNumber, pin, ownerFname, ownerLname, ownerEmail) {};

            JSONArray transactionsArray = (JSONArray) jsonObject.get("transactions");
            for (Object obj : transactionsArray) {
                JSONObject transactionObject = (JSONObject) obj;
                Transaction transaction = (Transaction) dataFromDict(transactionObject, Transaction.class);
                account.addNewTransaction(transaction.getAccountNum(), transaction.getType(), transaction.getDescription());
            }
            return account;

        } else if (clazz == Transaction.class) {
            String accountNum = (String) jsonObject.get("accountNum");
            Transaction.Transactions type = Transaction.Transactions.valueOf((String) jsonObject.get("type"));
            String description = (String) jsonObject.get("description");
            return new Transaction(accountNum, type, description);
        }
        return null;
    }

    public static ArrayList<Object> loadData(String filename, Class<?> clazz) {
        JSONArray jsonArray = load(filename);
        ArrayList<Object> dataList = new ArrayList<>();
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            dataList.add(dataFromDict(jsonObject, clazz));
        }
        return dataList;
    }

    public static void saveData(ArrayList<Object> dataList, String filename) {
        JSONArray jsonArray = new JSONArray();
        for (Object data : dataList) {
            jsonArray.add(dataToDict(data));
        }
        save(jsonArray, filename);
    }
}