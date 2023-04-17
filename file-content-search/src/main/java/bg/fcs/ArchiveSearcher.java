package bg.fcs;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static bg.fcs.Constants.ARCHIVE_EXTENSIONS;

/**
 The ArchiveSearcher class provides methods to search for a text string in a compressed archive file.
 **/

public class ArchiveSearcher {
    private File archiveFile;
    private String searchText;

    public ArchiveSearcher(File archiveFile, String searchText) {
        this.archiveFile = archiveFile;
        this.searchText = searchText;
    }

    /**
     * Search for a text string in a compressed archive file.
     *
     * @return List of File objects representing the files in the archive that contain the search text.
     * @throws IOException if an error occurs while reading the archive file.
     */
    public List<File> searchArchive() throws IOException {
        List<File> foundFiles = new ArrayList<>();

        try (InputStream inputStream = new FileInputStream(archiveFile)) {
            byte[] fileBytes = inputStream.readAllBytes();

            ArchiveInputStream archiveInputStream = createArchiveInputStream(new ByteArrayInputStream(fileBytes));
            ArchiveEntry entry;

            List<String> fileNames = new ArrayList<>();

            while ((entry = archiveInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    fileNames.add(entry.getName());
                }
            }

            archiveInputStream.close();

            List<String> matchingFileNames = checkFilesInArchive(archiveFile, fileNames);

            for (String matchingFileName : matchingFileNames) {
                foundFiles.add(new File(archiveFile.getPath() + " " + matchingFileName));
            }

            foundFiles.sort(Comparator.comparingLong(File::length));

            return foundFiles;
        }
    }


    /**
     Searches for file names in a given archive file and returns a list of files.
     *
     @param archiveFile the archive file to search for the files in.
     @param fileNames a list of file names to search for.
     @return a list of file names that are present in the archive file.
     @throws IOException if an I/O error occurs while reading the archive file.
     */
    public static List<String> checkFilesInArchive(File archiveFile, List<String> fileNames) throws IOException {
        List<String> foundFiles = new ArrayList<>();

        try (ArchiveInputStream archiveInputStream = createArchiveInputStream(new FileInputStream(archiveFile))) {
            ArchiveEntry entry;

            while ((entry = archiveInputStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }

                for (String fileName : fileNames) {
                    if (entry.getName().equals(fileName)) {
                        foundFiles.add(fileName);
                        break;
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
    public static boolean isArchive(File file) {
        String fileName = file.getName();

        for (String archiveExtension : ARCHIVE_EXTENSIONS) {
            if (fileName.endsWith(archiveExtension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Create an ArchiveInputStream based on the file format of the input stream.
     *
     * @param inputStream InputStream containing the compressed archive file.
     * @return ArchiveInputStream object for the compressed archive file.
     * @throws IOException if an error occurs while reading the archive file.
     */
    private static ArchiveInputStream createArchiveInputStream(InputStream inputStream) throws IOException {
        byte[] fileBytes = inputStream.readAllBytes();

        if (ZipArchiveInputStream.matches(fileBytes, fileBytes.length)) {
            return new ZipArchiveInputStream(new ByteArrayInputStream(fileBytes));
        } else if (TarArchiveInputStream.matches(fileBytes, fileBytes.length)) {
            return new TarArchiveInputStream(new ByteArrayInputStream(fileBytes));
        } else if (CpioArchiveInputStream.matches(fileBytes, fileBytes.length)) {
            return new CpioArchiveInputStream(new ByteArrayInputStream(fileBytes));
        } else if (ArArchiveInputStream.matches(fileBytes, fileBytes.length)) {
            return new ArArchiveInputStream(new ByteArrayInputStream(fileBytes));
        } else if (GzipCompressorInputStream.matches(fileBytes, 0)) {
            return new TarArchiveInputStream(new GzipCompressorInputStream(new ByteArrayInputStream(fileBytes)));
        } else if (BZip2CompressorInputStream.matches(fileBytes, 0)) {
            return new TarArchiveInputStream(new BZip2CompressorInputStream(new ByteArrayInputStream(fileBytes)));
        } else if (XZCompressorInputStream.matches(fileBytes, 0)) {
            return new TarArchiveInputStream(new XZCompressorInputStream(new ByteArrayInputStream(fileBytes)));
        } else if (JarArchiveInputStream.matches(fileBytes, 0)) {
            return new JarArchiveInputStream(new ByteArrayInputStream(fileBytes));
        } else {
            throw new IllegalArgumentException("File format is not supported");
        }
    }
}

