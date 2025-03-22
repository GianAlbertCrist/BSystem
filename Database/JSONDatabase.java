package Database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Accounts.*;
import Bank.Bank;
import Bank.BankLauncher;
import Processes.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides methods for loading and saving JSON data.
 */
public class JSONDatabase {

    // Logger instance for logging errors
    private static final Logger LOGGER = Logger.getLogger(JSONDatabase.class.getName());

    // Custom TypeAdapter for LocalDateTime
    private static final TypeAdapter<LocalDateTime> LOCAL_DATE_TIME_ADAPTER = new TypeAdapter<>() {
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            out.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            return LocalDateTime.parse(in.nextString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    };

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, LOCAL_DATE_TIME_ADAPTER)
            .setPrettyPrinting()
            .create();

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
     * Loads JSON data from a file.
     *
     * @param filename The path to the JSON file.
     * @return A JSONObject containing the loaded data. If an error occurs during loading, an empty JSONObject is returned.
     */
    public static JSONObject loadObject(String filename) {
        JSONParser parser = new JSONParser();
        File file = new File(filename);
        if (!file.exists()) {
            // Create an empty JSON file if it does not exist
            save(new JSONObject(), filename);
        }
        try (FileReader reader = new FileReader(filename)) {
            // Parse the JSON file and return the JSONObject
            Object obj = parser.parse(reader);
            return (JSONObject) obj;
        } catch (IOException | ParseException e) {
            // Log the error and return an empty JSONObject
            LOGGER.log(Level.SEVERE, "Error loading JSON file", e);
            return new JSONObject();
        }
    }

    /**
     * Saves JSON data to a file.
     *
     * @param data The JSONArray containing the data to be saved.
     * @param filename The path to the JSON file.
     */
    public static void save(JSONArray data, String filename) {
        try (FileWriter file = new FileWriter(filename)) {
            // Write the JSON data to the file with pretty printing
            String jsonString = GSON.toJson(data);
            file.write(jsonString);
            file.flush();
        } catch (IOException e) {
            // Log the error
            LOGGER.log(Level.SEVERE, "Error saving JSON file", e);
        }
    }

    /**
     * Saves JSON data to a file.
     *
     * @param data The JSONObject containing the data to be saved.
     * @param filename The path to the JSON file.
     */
    public static void save(JSONObject data, String filename) {
        try (FileWriter file = new FileWriter(filename)) {
            // Write the JSON data to the file with pretty printing
            String jsonString = GSON.toJson(data);
            file.write(jsonString);
            file.flush();
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
        } else if (data instanceof Transaction transaction) {
            jsonObject.put("accountNum", transaction.accountNumber);
            jsonObject.put("type", transaction.transactionType.toString());
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
    public static <T> T dataFromDict(JSONObject jsonObject, Class<T> clazz) {
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
                Account account = dataFromDict(accountObject, Account.class);
                if (account != null) {
                    bank.addNewAccount(account);
                }
            }
            return clazz.cast(bank);
        } else if (clazz == Account.class) {
            int bankId = ((Long) jsonObject.get("bankId")).intValue();
            Bank bank = BankLauncher.getBankById(bankId);
            if (bank == null) {
                LOGGER.log(Level.SEVERE, "Bank not found for ID: {0}", bankId);
                return null;
            }
            String accountNumber = (String) jsonObject.get("accountNumber");
            String ownerFname = (String) jsonObject.get("ownerFname");
            String ownerLname = (String) jsonObject.get("ownerLname");
            String ownerEmail = (String) jsonObject.get("ownerEmail");
            String pin = (String) jsonObject.get("pin");
            String accountType = (String) jsonObject.get("accountType");

            Account account;
            switch (accountType) {
                case "CreditAccount":
                    account = new CreditAccount(bank, accountNumber, pin, ownerFname, ownerLname, ownerEmail);
                    break;
                case "SavingsAccount":
                    Double initialDeposit = (Double) jsonObject.get("initialDeposit");
                    if (initialDeposit == null) {
                        LOGGER.log(Level.SEVERE, "Initial deposit not found for SavingsAccount: {0}", accountNumber);
                        return null;
                    }
                    account = new SavingsAccount(bank, accountNumber, pin, ownerFname, ownerLname, ownerEmail, initialDeposit);
                    break;
                case "StudentAccount":
                    int yearOfBirth = ((Long) jsonObject.get("yearOfBirth")).intValue();
                    String studentId = (String) jsonObject.get("studentId");
                    account = new StudentAccount(bank, accountNumber, pin, ownerFname, ownerLname, ownerEmail, yearOfBirth, studentId);
                    break;
                case "BusinessAccount":
                    String businessPermitID = (String) jsonObject.get("businessPermitID");
                    String businessName = (String) jsonObject.get("businessName");
                    Double bankAnnualIncome = (Double) jsonObject.get("bankAnnualIncome");
                    Double initialDepositBusiness = (Double) jsonObject.get("initialDeposit");
                    if (bankAnnualIncome == null || initialDepositBusiness == null) {
                        LOGGER.log(Level.SEVERE, "Bank annual income or initial deposit not found for BusinessAccount: {0}", accountNumber);
                        return null;
                    }
                    account = new BusinessAccount(bank, accountNumber, pin, ownerFname, ownerLname, ownerEmail, businessPermitID, businessName, bankAnnualIncome, initialDepositBusiness);
                    break;
                default:
                    LOGGER.log(Level.SEVERE, "Unknown account type: {0}", accountType);
                    return null;
            }

            JSONArray transactionsArray = (JSONArray) jsonObject.get("transactions");
            for (Object obj : transactionsArray) {
                JSONObject transactionObject = (JSONObject) obj;
                Transaction transaction = dataFromDict(transactionObject, Transaction.class);
                if (transaction != null) {
                    account.addNewTransaction(transaction.accountNumber, transaction.transactionType, transaction.description);
                }
            }
            return clazz.cast(account);
        } else if (clazz == Transaction.class) {
            String accountNum = (String) jsonObject.get("accountNum");
            Transaction.Transactions type = Transaction.Transactions.valueOf((String) jsonObject.get("type"));
            String description = (String) jsonObject.get("description");
            LocalDateTime time = LocalDateTime.parse((String) jsonObject.get("time"));
            return clazz.cast(new Transaction(accountNum, type, description, time));
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
            dataList.add(clazz.cast(dataFromDict((JSONObject) obj, clazz)));
        }
        return dataList;
    }

    /**
     * Saves a list of objects to a JSON file.
     *
     * @param dataList The list of objects to be saved.
     * @param filename The path to the JSON file.
     */
    public static <T> void saveData(ArrayList<T> dataList, String filename) {
        JSONArray jsonArray = new JSONArray();
        for (T data : dataList) {
            jsonArray.add(dataToDict(data));
        }
        save(jsonArray, filename);
    }
}