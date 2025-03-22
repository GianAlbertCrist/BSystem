package Database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Accounts.Account;
import Bank.Bank;
import Bank.BankLauncher;
import Processes.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides methods for loading and saving JSON data.
 */
public class JSONDatabase {

    // Logger instance for logging errors
    private static final Logger LOGGER = Logger.getLogger(JSONDatabase.class.getName());
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Loads JSON data from a file.
     *
     * @param filename The path to the JSON file.
     * @return A JSONArray containing the loaded data. If an error occurs during loading, an empty JSONArray is returned.
     */
    public static JSONArray load(String filename) {
        JSONParser parser = new JSONParser();
        File file = new File(filename);
        if (!file.exists()) {
            // Create an empty JSON file if it does not exist
            save(new JSONArray(), filename);
        }
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
     * @param data The JSONArray containing the data to be saved.
     * @param filename The path to the JSON file.
     */
    public static void save(JSONArray items, String filename) {
        File file = new File(filename);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs(); // Create the directories if they do not exist
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            // Write the JSON data to the file with pretty printing
            String jsonString = GSON.toJson(items);
            fileWriter.write(jsonString);
            fileWriter.flush();
        } catch (IOException e) {
            // Log the error
            LOGGER.log(Level.SEVERE, "Error saving JSON file", e);
        }
    }

    /**
     * Converts an object to a JSONObject.
     *
     * @param data The object to be converted.
     * @return A JSONObject representing the input object.
     */  
    @SuppressWarnings("unchecked")
    public static JSONObject dataToDict(Object data) {
        JSONObject jsonObject = new JSONObject();
        // Add code to populate the jsonObject based on the type of data
        // If the data is an instance of Bank, populate the jsonObject with bank details
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
        // If the data is an instance of Account, populate the jsonObject with account details
        } else if (data instanceof Account account) {
            jsonObject.put("bankId", account.getBank().getBankId());
            jsonObject.put("accountNumber", account.getAccountNumber());
            jsonObject.put("ownerFname", account.getOwnerFname());
            jsonObject.put("ownerLname", account.getOwnerLname());
            jsonObject.put("ownerEmail", account.getOwnerEmail());
            jsonObject.put("pin", account.getPin());
            jsonObject.put("accountType", account.getClass().getSimpleName()); 

            JSONArray transactionsArray = new JSONArray();
            for (Transaction transaction : account.getTransactions()) {
                transactionsArray.add(dataToDict(transaction));
            }
            jsonObject.put("transactions", transactionsArray);
        // If the data is an instance of Transaction, populate the jsonObject with transaction details    
        } else if (data instanceof Transaction transaction) {
            jsonObject.put("accountNum", transaction.accountNumber);
            jsonObject.put("type", transaction.transactionType);
            jsonObject.put("description", transaction.description);
            jsonObject.put("time", transaction.getTimestamp().toString());
        }
        return jsonObject;
    }

    /**
     * Converts a JSONObject to an object of the specified class.
     *
     * @param jsonObject The JSONObject to be converted.
     * @param clazz The class of the object to be created.
     * @return An object of the specified class, populated with data from the JSONObject.
     */
    public static Object dataFromDict(JSONObject jsonObject, Class<?> clazz) {
        // Add code to create an object of the specified class and populate it with data from the jsonObject
        // If the class is Bank, create a new Bank object and populate it with bank details
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
                assert account != null;
                bank.addNewAccount(account);
            }
            return bank;
        // If the clazz is Account, create a new Account object and populate it with account details
        } else if (clazz == Account.class) {
            String accountNumber = (String) jsonObject.get("accountNumber");
            String ownerFname = (String) jsonObject.get("ownerFname");
            String ownerLname = (String) jsonObject.get("ownerLname");
            String ownerEmail = (String) jsonObject.get("ownerEmail");
            String pin = (String) jsonObject.get("pin");

            Bank bank = BankLauncher.findAccount(accountNumber).getBank();
            Account account = new Account(bank, accountNumber, pin, ownerFname, ownerLname, ownerEmail) {};

            JSONArray transactionsArray = (JSONArray) jsonObject.get("transactions");
            for (Object obj : transactionsArray) {
                JSONObject transactionObject = (JSONObject) obj;
                Transaction transaction = (Transaction) dataFromDict(transactionObject, Transaction.class);
                assert transaction != null;
                account.addNewTransaction(transaction.accountNumber, transaction.transactionType, transaction.description);
            }
            return account;
        // If the clazz is Transaction, create a new Transaction object and populate it with transaction details
        } else if (clazz == Transaction.class) {
            String accountNum = (String) jsonObject.get("accountNum");
            Transaction.Transactions type = Transaction.Transactions.valueOf((String) jsonObject.get("type"));
            String description = (String) jsonObject.get("description");
            LocalDateTime time = LocalDateTime.parse((String) jsonObject.get("time"));
            return new Transaction(accountNum, type, description, time);
        }
        return null;
    }

    /**
     * Loads data from a JSON file and converts it to a list of objects of the specified class.
     *
     * @param filename The path to the JSON file.
     * @param clazz The class of the objects to be created.
     * @return A list of objects of the specified class, populated with data from the JSON file.
     */
    public static <T> ArrayList<T> loadData(String filename, Class<T> clazz) {
        JSONArray jsonArray = load(filename);
        ArrayList<T> dataList = new ArrayList<>();
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            dataList.add(clazz.cast(dataFromDict(jsonObject, clazz)));
        }
        return dataList;
    }

    /**
     * Saves a list of objects to a JSON file.
     *
     * @param dataList The list of objects to be saved.
     * @param filename The path to the JSON file.
     */   
    @SuppressWarnings("unchecked")
    public static <T> void saveData(ArrayList<T> dataList, String filename) {
        JSONArray jsonArray = new JSONArray();
        for (T data : dataList) {
            jsonArray.add(dataToDict(data));
        }
        save(jsonArray, filename);
    }
}