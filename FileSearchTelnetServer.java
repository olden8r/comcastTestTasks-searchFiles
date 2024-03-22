package thirdTask;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class FileSearchTelnetServer {
	
    private static final BlockingQueue<SearchTask> taskQueue = new LinkedBlockingQueue<>();
    private static Path rootPath; // The path to the root directory

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java FileSearchTelnetServer <serverPort> <rootPath>");
            return;
        }

        // Parse the command line arguments
        int serverPort = Integer.parseInt(args[0]); // The port which the server listens
        rootPath = Paths.get(args[1]);


        // Start the single file system search thread
        new Thread(FileSearchTelnetServer::processTasks).start();

        // Start the server to listen for client connections
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Server listening on port " + serverPort);

            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        }
    }

    /**
     * Handles client connections for a multi-client server application.
     * This class manages individual client sessions, reading requests,
     * processing them, and sending back responses. Each instance is associated
     * with a single client and runs in its own thread.
     */
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        /**
         * Initializes a new client handler with the specified socket.
         * 
         * @param clientSocket the socket connected to the client
         */
        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        /**
         * Main execution method for the client handler. Reads client requests,
         * processes them, and sends responses back to the client.
         */
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                out.println("Connected to the File Search Server.");
                out.println("Enter search parameters in the format: depth mask");

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] tokens = inputLine.split(" ");
                    if (tokens.length == 2) {
                        try {
                            int depth = Integer.parseInt(tokens[0]);
                            String mask = tokens[1];
                            taskQueue.put(new SearchTask(depth, mask, out));
                        } catch (NumberFormatException e) {
                            out.println("Error: Depth must be a number.");
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        out.println("Error: Incorrect number of arguments. Usage: depth mask");
                    }
                }
            } catch (IOException e) {
                System.err.println("Client handler exception: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Method for processing search jobs
     * 
     */
    private static void processTasks() {
        while (true) {
            try {
                SearchTask task = taskQueue.take();
                searchFiles(rootPath, task.depth, task.mask, task.clientOut);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Search method that put in the queue all elements of the file system tree 
     * located at depth from the root of the tree 
     * that contain the string mask in their name.
     * 
     * @param rootPath Path to the root of the tree
     * @param depth Depth of the search
     * @param mask String
     * @param clientOut The PrintWriter associated with the client socket, 
     * 					used for sending text data to the client.
     * 
     */
    private static void searchFiles(Path rootPath, int depth, String mask, PrintWriter clientOut) {
        try (Stream<Path> stream = Files.walk(rootPath, depth)) {
            stream.filter(Files::isRegularFile)
                  .filter(path -> path.getFileName().toString().contains(mask))
                  .forEach(path -> clientOut.println(path));
        } catch (IOException e) {
            clientOut.println("Search error: " + e.getMessage());
        }
    }

    /**
     *  Class for storing information about the search task.
     *  
     */
    private static class SearchTask {
        final int depth;
        final String mask;
        final PrintWriter clientOut;

        SearchTask(int depth, String mask, PrintWriter clientOut) {
            this.depth = depth;
            this.mask = mask;
            this.clientOut = clientOut;
        }
    }
}
