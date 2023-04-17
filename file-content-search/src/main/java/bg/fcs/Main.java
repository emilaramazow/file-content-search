package bg.fcs;

import java.io.*;
import java.util.*;


public class Main {
    public static void main(String[] args) throws IOException {

        /*
        Program can accept path to a directory and text to search for on the command line :

        if (args.length < 2) {
            System.err.println("Usage: java TextSearcher <directory> <search text>");
            System.exit(1);
        }

        String directory = args[0];
        String searchText = args[1];
        */

        Scanner scanner = new Scanner(System.in);
        String[] input = scanner.nextLine().split(" ");

        String directory = input[0];
        String searchText = input[1];

        TextSearcher textSearcher = new TextSearcher(directory, searchText);
        List<File> foundFiles = textSearcher.searchText();

        foundFiles.sort(Comparator.comparingLong(File::length));
        foundFiles.forEach(System.out::println);

    }

}
