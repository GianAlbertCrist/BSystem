package Database;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
}