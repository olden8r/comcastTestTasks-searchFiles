package secondTask;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

public class FileSearchMultiThreaded {
	
    private static final Path POISON_PILL = Paths.get(""); // Signal to stop the consumer thread

    public static void main(String[] args) {
       if (args.length != 3) {
            System.out.println("Usage: java FileSearchMultiThreaded <rootPath> <depth> <mask>");
            return;
        }

        // Initialize search parameters
        Path rootPath = Paths.get(args[0]);
        int depth = Integer.parseInt(args[1]);
        String mask = args[2];

    	
        // Create a LinkedBlockingQueue for exchanging file paths between threads
        BlockingQueue<Path> queue = new LinkedBlockingQueue<>(); // No capacity limit

        // Thread for searching files
        Thread searchThread = new Thread(() -> {
            try {
                searchFiles(rootPath, depth, mask, queue);
            } catch (IOException e) {
                System.err.println("An IO exception occurred: " + e.getMessage());
            } finally {
                try {
                    queue.put(POISON_PILL); // Put the poison pill into the queue to signal completion
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Thread for printing found files
        Thread printThread = new Thread(() -> {
            try {
                Path path;
                while (!(path = queue.take()).equals(POISON_PILL)) { // Wait until the poison pill appears
                    System.out.println(path);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Start both threads
        searchThread.start();
        printThread.start();
    }

    /**
     * Search method that put in the queue all elements of the file system tree 
     * located at depth from the root of the tree 
     * that contain the string mask in their name.
     * 
     * @param rootPath Path to the root of the tree
     * @param depth Depth of the search
     * @param mask String
     * @param queue Queue for exchanging file paths between threads
     * 
     */
    public static void searchFiles(Path rootPath, int depth, String mask, BlockingQueue<Path> queue) throws IOException {
        try (Stream<Path> pathStream = Files.walk(rootPath, depth)) {
            pathStream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().contains(mask))
                    .forEach(path -> {
                        try {
                            queue.put(path); // Put each found path into the queue
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
        }
    }
}

