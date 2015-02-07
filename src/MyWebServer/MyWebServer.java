package MyWebServer;/*--------------------------------------------------------

1. Name / Date:
Kahlil Bernard Chase
8 February 2015

2. Java version used, if not the official version for the class:

build 1.8.0_31-b13

3. Precise command-line compilation examples / instructions:

> javac MyWebServer.MyWebServer.java

4. Precise examples / instructions to run this program:

> java MyWebServer.MyWebServer

    a. add .txt or .html file to directory containing MyWebServer.MyWebServer
    b. add addnums.html file to directory containing MyWebServer.MyWebServer
    c. add subdirectories to directory containing MyWebServer.MyWebServer
    b. load "localhost:2540" in Firefox

5. List of files needed for running the program.

 a. MyWebServer.MyWebServer.java

6. Notes:

----------------------------------------------------------*/

import java.io.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.StringBuilder;
import java.net.*;


public class MyWebServer {
    /**
     * Starts a server which listens on a port and starts a new MyWebServer.Worker thread for every request made.
     */
    public static boolean controlSwitch = true;

    public static void main(String a[]) throws IOException {
        int q_len = 6;      // max queue length
        int port = 2540;    // server port
        Socket sock;
        ServerSocket servsock = new ServerSocket(port, q_len);

        // start server
        System.out.println("Kahlil Bernard Chase's MyWebServer.MyWebServer starting up, listening at port 2540.\n");
        while (controlSwitch) {
            sock = servsock.accept();

            // pass incoming connection to MyWebServer.Worker thread
            if (controlSwitch) new Worker(sock).start();
        }
        servsock.close();
    }
}

class Worker extends Thread {
    /**
     * Constructs a MyWebServer.Worker object that handles an incoming connection.
     */
    Socket sock;
    Worker(Socket s) {
        sock = s;
    }

    public void run() {
        /**
         * Create MyWebServer.Worker input stream in and output stream out;
         * check for GET request;
         * respond to GET request by creating MIME header and returning file requested.
         */
        PrintStream out = null;
        BufferedReader in = null;
        String textToClient;

        try {
            // create input and output streams
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintStream(sock.getOutputStream());

            // read request made to server
            textToClient = in.readLine();
            System.out.println(textToClient);
            String[] textSplit = textToClient.split(" ");

            // process GET request
            if (textSplit[0].matches("GET")) {

                // get file name requested
                String file = textSplit[1].substring(1);
                if (file.matches("")) {
                    file = "./";
                }

                // check if GET request is for a file or directory
                File checker = new File(file);

                // if directory requested, return directory tree
                if (checker.isDirectory()) {
                    String header = createHeader(file);
                    pushDir(header, file, out);
                }

                // if file requested, return file
                else {
                    // if file type is cgi, execute fake-cgi script
                    if (file.startsWith("cgi")) {
                        int ind = file.indexOf("?") + 1;
                        String input = file.substring(ind);
                        addnums(input, out);
                    }

                    // return all other file types
                    else {
                        String header = createHeader(file);
                        pushFile(header, file, out);
                    }
                }
            }

            sock.close();
        } catch (IOException ioe) { ioe.printStackTrace(); }
    }

    private String createHeader(String file) {
        /**
         * Generate MIME header.
         * @param file the name of the file for which MIME header is created.
         * @return string MIME header.
         */
        StringBuilder toClient = new StringBuilder();
        String contentType = "text/html";
        File fIn = new File(file);

        // define the content type of the file requested
        if (file.endsWith(".txt")) {
            contentType = "text/plain";
        } else if (file.endsWith(".html")) {
            contentType = "text/html";
        }

        // build MIME header
        toClient.append("HTTP/1.1 200 OK\n");
        toClient.append("Content-Length: " + fIn.length() + "\n");
        toClient.append("Content-Type: " + contentType);
        toClient.append("\r\n\r\n");

        return toClient.toString();
    }

    private void pushFile(String header, String file, PrintStream out) {
        /**
         * Send requested file to web client.
         * @param header the HTML header to return to web client.
         * @param file the name of the file to return to web client.
         * @param out the PrintStream connection to web client.
         */
        BufferedReader in;
        String fileText;

        try {
            // create input stream for requested file
            in = new BufferedReader(new FileReader(file));

            // read requested file and return to web client
            try {
                out.println(header);
                System.out.println(header);
                while ((fileText = in.readLine()) != null) {
                    out.println(fileText);
                    System.out.println(fileText);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    private void pushDir(String header, String path, PrintStream out) {
        /**
         * Send directory tree structure to web client dynamically.
         * @param header the HTML header to return to web client.
         * @param path the pathname of the directory to return to web client.
         * @param out the PrintStream connection to web client.
         */
        File f = new File(path);

        // Send MIME header
        out.println(header);
        System.out.println(header);

        // Generate directory title
        out.printf("<h1>Index of %s</h1>\n", path);
        System.out.printf("<h1>Index of %s</h1>\n", path);
        out.println("<pre>\n");
        System.out.println("<pre>\n");

        // Get all files and subdirectories
        File[] listDir = f.listFiles();

        // Dynamically return list of all files and subdirectories as HTML
        String fileFormat = "<a href=\"%s\">%s</a>\n";

        for (File g : listDir) {
            if (!g.isHidden()) {
                String fOut = g.toString();

                // Truncate pathname from HTML output of files and subdirectories
                int ind = fOut.lastIndexOf("/") + 1;
                String fTrunc = fOut.substring(ind);

                out.format(fileFormat, fOut, fTrunc);
                System.out.printf(fileFormat, fOut, fTrunc);
                out.println();
                System.out.println();
                out.flush();
            }
        }

        out.println("</pre>"); out.flush();
        System.out.println("</pre>");
    }

    private void addnums(String inputIn, PrintStream out) {
        /**
         * Takes input from fake-cgi and returns sum.
         * @param inputIn the string input to parse.
         * @param out the PrintStream connection to web client.
         */
        // parse user name from cgi input
        int nInd = inputIn.indexOf("=") + 1;
        String name = inputIn.substring(nInd);

        // parse first argument beginning location from cgi input
        int numOneInd = name.indexOf("=") + 1;
        String numOne = name.substring(numOneInd);

        // parse second argument beginning location from cgi input
        int numTwoInd = numOne.indexOf("=") + 1;
        String numTwo = numOne.substring(numTwoInd);
        int n2 = Integer.parseInt(numTwo);

        // parse user name end location from cgi input
        int nIndEnd = name.indexOf("&");
        name = name.substring(0, nIndEnd);

        // parse first argument end location from cgi input
        int numOneIndEnd = numOne.indexOf("&");
        numOne = numOne.substring(0, numOneIndEnd);
        int n1 = Integer.parseInt(numOne);

        // calculate sum
        int sum = n1 + n2;

        // create response
        String response = "Dear " + name + ", the sum of " + n1 + " and " + n2 + " is " + sum + ".";

        // build response header
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK\n");
        sb.append("Content-Length: " + response.length() + "\n");
        sb.append("Content-Type: text/html");
        sb.append("\r\n\r\n");
        String header = sb.toString();

        // output response
        out.println(header);
        out.println(response);
        out.flush();

        System.out.println(header);
        System.out.println(response);
    }
}