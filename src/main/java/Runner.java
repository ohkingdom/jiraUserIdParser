import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class Runner {

    private String issueFile = "TestImport.csv";
    private String userIdFile = "export-users.csv";
    private List<String[]> issueList;
    private List<String[]> userIdList;
    private Map<String, String> userIdMap = new HashMap<>();


    public static void main(String[] args) throws IOException, CsvException {
        Runner instance = new Runner();
    }

    public Runner () throws IOException, CsvException {
        // JIRA Issue Export and UserIDs imported from .csv to List
        importFilesToList();
        // Create a Map with the UserIDs as the key, and the Name as the value
        stringToMap();
        // Replace UserIDs with Name
        replaceIdWithName();
        // Write updated strings to the CSV
        writeToCsv();
    }

    public void replaceIdWithName() {
        String stringFromList = "";
        String formattedString = "";
        String userId = "";
        String userName = "";
        for (int i = 0; i < issueList.size(); i++) {
            // Gets position 'i' in the list, and converts it to a string
            stringFromList = Arrays.toString(issueList.get(i));
            // Takes the converted string, and removes the first and last characters (because they are '[' and ']')
            formattedString = stringFromList.substring(1, stringFromList.length() - 1);
            for (String key : userIdMap.keySet()) {
                // Set String userId to the Hashmap key (trim the leading '[')
                userId = key;
                // Set the String userName to the Hashmap value (trim the leading/ending '[' and ']')
                userName = userIdMap.get(key);
                // If the formatted string contains the userId
                if (formattedString.contains(userId)) {
                    // Replace all occurrences of the userId with the userName
                    // Pattern.quote used to get a strict regex of the userId
                    formattedString = formattedString.replaceAll(Pattern.quote(userId), userName);
                    // Update the converted string with the new values
                    // Lazy Debugging (part 1)
                    System.out.println("Debug " + "(" + i + ") "+ "Old Value: " + Arrays.toString(issueList.get(i)));
                    // I'm unsure about this part here, might be doing something weird
                    issueList.set(i, new String[] {formattedString});
                    // Lazy Debugging (part 2)
                    System.out.println("Debug " + "(" + i + ") "+ "New Value: " + Arrays.toString(issueList.get(i)));
                    // Lazy Debugging (part 3)
                    System.out.println(Arrays.toString(issueList.get(i)));
                }
            }
        }
    }

    public void stringToMap() {
        // Splits UserId and Username into a Key/Value pair
        int i = 0;
        for (String[] userId : userIdList) {
            if (i == 0){
                //skip header
                i++;
                continue;
            }

            String key = userId[0];
            String value = userId[1];
            userIdMap.put(key, value);
        }
    }

    private void importFilesToList() throws IOException, CsvException {
        // Jira Export to List
        try (CSVReader reader = new CSVReader(new FileReader(this.getClass().getResource(issueFile).getPath()))) {
            issueList = reader.readAll();
        }
        // UserIDs to List
        try (CSVReader reader = new CSVReader(new FileReader(this.getClass().getResource(userIdFile).getPath()))) {
            userIdList = reader.readAll();
        }
    }

    public void writeToCsv() throws IOException {
        String fileName = "test_output.csv";
        try (FileOutputStream fos = new FileOutputStream(fileName);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {

            writer.writeAll(issueList);
        }
    }

}
