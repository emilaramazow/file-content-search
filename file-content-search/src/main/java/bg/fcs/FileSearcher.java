package bg.fcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * The FileSearcher class provides methods for searching text in a file.
 */
public class FileSearcher {

    /**
     * Searches for the given text in the specified file.
     *
     * @param file the file to search in.
     * @param searchText the text to search for.
     * @return true if the text is found in the file, false otherwise.
     */
    public static boolean searchTextInFile(File file, String searchText) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.contains(searchText)) {
                    return true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
