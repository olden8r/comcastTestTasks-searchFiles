# Java File Search Applications

This repository contains three separate Java applications, each demonstrating different approaches to file searching within a file system. 
Applications were written as part of a test task.

## Projects Overview

### 1. FileSearchWithoutRecursion
A console application that takes three parameters:

- **rootPath** - path to the starting directory
- **depth** - search depth - non-negative integer
- **mask** - string

The application must find all elements of the file system tree located at a depth from rootPath that contain mask in their name.

**Requirements**:
- The application must be implemented without using recursion.
- Use standard library

### 2. FileSearchMultiThreaded
An enhanced version of the first file search application that utilizes multithreading to handle multiple search queries simultaneously.

**Requirements**:
- one thread performs the search
- another thread prints the results to the console as they appear.

### 3. FileSearchTelnetServer
A mock multi-user telnet server allowing multiple clients to connect and perform file searches concurrently.
An enhanced version of the first two file search applications.

**Requirements**:
The application accepts two parameters:
- **serverPort** - the port that it will “listen to”
- **rootPath** - path to the starting directory

Search criteria (depth and mask) are set via the telnet client console
(you should to use standard programs for this: telnet, putty, ...)

Requirements:
- all access to the file system must be made from a single thread.
  (There should be a thread on the server, from which and only from it the file system is accessed.)
- the “telnet server” must be multi-user + interactive
  (If 5 clients access the server at the same time and each sets a “search query”,
  then the results should arrive to the clients in parallel, not sequentially)


## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java JDK 11 or later
- Maven 3.6.0 or later

### Installing and Running

To install and run each application, follow these steps:

1. Save the repository and enter its directory.

2. Build the project with Maven:
   ```bash
   mvn clean install
   
3. Go to the target directory
   ```bash
   cd ProjectName/target
   
4. To run standard applications (1.FileSearchWithoutRecursion, 2.FileSearchMultiThreaded) use:
   ```bash
   mvn exec:java -Dexec.mainClass="firstTask.FileSearchWithoutRecursion" -Dexec.args="<rootPath> <depth> \"<mask>\" "
   mvn exec:java -Dexec.mainClass="secondTask.FileSearchMultiThreaded" -Dexec.args="<rootPath> <depth> \"<mask>\" "

5. For the Telnet server application (FileSearchTelnetServer), start the mock server:
   ```bash
   mvn exec:java -Dexec.mainClass="thirdTask.FileSearchTelnetServer" -Dexec.args="<serverPort> <rootPath>"

6. Open another terminal window to connect to the server.
   ```bash
   telnet localhost <serverPort>
   
7. After connecting to the server, you should see a welcome message or other output indicating that the connection was successful.
8. Follow the server instructions.

