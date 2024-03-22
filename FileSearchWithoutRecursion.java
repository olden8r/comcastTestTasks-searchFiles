package firstTask;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class FileSearchWithoutRecursion {
	
    public static void main(String[] args) { 	
        // Ensure that the correct number of arguments are provided
        if (args.length != 3) {
            System.out.println("Usage: java FileSearchWithoutRecursion <rootPath> <depth> <mask>");
            return;
        }
        
        /* I do not check the arguments for correctness, 
         * because according to the conditions of the task
		 * we get a positive integer as depth and string as a mask.
		 * 
		 * If the mask is an empty string, application will display all files at a given depth.
		 */

        // Parse the command line arguments
        Path rootPath = Paths.get(args[0]);
        int depth = Integer.parseInt(args[1]);
        String mask = args[2];

        // Perform the file search
        try {
            searchFiles(rootPath, depth, mask);
        } catch (IOException e) {
            // Handle potential IO exceptions
            System.err.println("An IO exception occurred: " + e.getMessage());
        }
    }

    /**
     * Search method that prints all elements of the file system tree 
     * located at depth from the root of the tree 
     * that contain the string mask in their name.
     * 
     * @param rootPath Path to the root of the tree
     * @param depth Depth of the search
     * @param mask String
     * 
     */
    public static void searchFiles(Path rootPath, int depth, String mask) throws IOException {
        // Use Files.walk to get a stream of paths starting from rootPath
        try (Stream<Path> pathStream = Files.walk(rootPath, depth)) {
            pathStream
                // Filter to include only regular files
                .filter(Files::isRegularFile)
                // Filter to include only files that match the mask
                .filter(path -> path.getFileName().toString().contains(mask))
                // Print the full path of each remaining file
                .forEach(System.out::println);
        }
    }
}
