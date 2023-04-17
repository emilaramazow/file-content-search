package bg.fcs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static bg.fcs.ArchiveSearcher.isArchive;


/**
 * The TextSearcher class is used to search for a specific text in all files in a given directory and its subdirectories.
 */
public class TextSearcher {
    private String directory;
    private String searchText;

    public TextSearcher(String directory, String searchText) {
        this.directory = directory;
        this.searchText = searchText;
    }

    public TextSearcher() {
    }

    /**
     * Searches for the specified text in all files in the directory and its subdirectories.
     *
     * @return A list of files containing the specified text, sorted by file size in ascending order.
     * @throws IOException If an I/O error occurs.
     */
    public List<File> searchText() throws IOException {
        List<File> foundFiles = new ArrayList<>();
        File[] files = new File(directory).listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    DirectorySearcher directorySearcher = new DirectorySearcher(file, searchText);
                    foundFiles.addAll(directorySearcher.searchDirectory());
                } else if (!isArchive(file)) {
                    if (FileSearcher.searchTextInFile(file, searchText)) {
                        foundFiles.add(file);
                    }
                } else {
                    ArchiveSearcher archiveSearcher = new ArchiveSearcher(file, searchText);
                    foundFiles.addAll(archiveSearcher.searchArchive());
                }
            }
        }

        foundFiles.sort(Comparator.comparingLong(File::length));

        return foundFiles;
    }
}
