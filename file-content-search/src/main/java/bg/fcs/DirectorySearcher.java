package bg.fcs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static bg.fcs.Constants.ARCHIVE_EXTENSIONS;

/**
 The DirectorySearcher class represents a utility for searching a directory and its subdirectories for files containing a given search text.
 */
public class DirectorySearcher {

    private File directory;
    private String searchText;

    public DirectorySearcher(File directory, String searchText) {
        this.directory = directory;
        this.searchText = searchText;
    }

    /**
     * Searches the directory and its subdirectories for files containing the search text.
     *
     * @return a list of files containing the search text.
     * @throws IOException if an I/O error occurs.
     */
    public List<File> searchDirectory() throws IOException {
        List<File> foundFiles = new ArrayList<>();
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    DirectorySearcher directorySearcher = new DirectorySearcher(file, searchText);
                    foundFiles.addAll(directorySearcher.searchDirectory());
                } else if (isArchive(file)) {
                    ArchiveSearcher archiveSearcher = new ArchiveSearcher(file, searchText);
                    foundFiles.addAll(archiveSearcher.searchArchive());
                } else {
                    if (FileSearcher.searchTextInFile(file, searchText)) {
                        foundFiles.add(file);
                    }
                }
            }
        }

        return foundFiles;
    }

    /**
     * Check if a given file is an archive.
     *
     * @param file File object representing the file to check.
     * @return true if the file is an archive, false otherwise.
     */
    private static boolean isArchive(File file) {
        String fileName = file.getName();

        for (String archiveExtension : ARCHIVE_EXTENSIONS) {
            if (fileName.endsWith(archiveExtension)) {
                return true;
            }
        }

        return false;
    }
}
