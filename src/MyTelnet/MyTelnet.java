package MyTelnet;/*--------------------------------------------------------

1. Name / Date:
Kahlil Bernard Chase
15 January 2015

2. Java version used, if not the official version for the class:

build 1.7.0_67-b01

3. Precise command-line compilation examples / instructions:

> javac InetClient.java


4. Precise examples / instructions to run this program:

In separate shell windows:

> java InetServer
> java InetClient

At InetClient shell prompt:

 a. enter a hostname or IP address
 b. select quit to terminate the client
 c. select shutdown to terminate the server

5. List of files needed for running the program.

 a. InetServer.java
 b. InetClient.java

5. Notes:

----------------------------------------------------------*/

import java.io.*;
import java.net.*;
import java.lang.StringBuilder;

public class MyTelnet {
    /*
     * Starts a client program that prompts the user for input and sends the request to a server.
     */
    public static void main(String args[]) {
        String serverName;
        String port;
        if (args.length < 1) {
            serverName = "localhost";
            port = "80";
        }
        else {
            serverName = args[0];
            port = args[1];
        }

        System.out.println("Kahlil Bernard Chase's MyTelnet.MyTelnet 1.0\n");
        System.out.println("Using server: " + serverName + ", Port: 80");
        BufferedReader in = new BufferedReader (new InputStreamReader(System.in));

        try {
            String name;
            StringBuilder message = new StringBuilder();

            do {
                System.out.print("Enter input to send to server: ");
                System.out.flush();
                name = in.readLine();
                message.append(name);
                message.append("\n");

                if (name.length() < 1) {
                    setInput(message.toString(), serverName, Integer.parseInt(port));
                }

            } while (name.indexOf("quit") < 0);
            System.out.println("Cancelled by user request.");
        } catch (IOException x) { x.printStackTrace(); }
    }

    static void setInput(String name, String serverName, int port) {
		/*
		 * Gets Internet address.
		 * @param name the name of the host or IP address to look up.
		 * @param serverName the name of the server from which the lookup is requested.
		 */
        Socket sock;
        BufferedReader fromServer;
        PrintStream toServer;
        String textFromServer;

        try {
            sock = new Socket(serverName, port);

            fromServer = new BufferedReader (new InputStreamReader(sock.getInputStream()));
            toServer = new PrintStream(sock.getOutputStream());

            toServer.println(name); toServer.flush();

            while ((textFromServer=fromServer.readLine()) != null) {
                System.out.println(textFromServer);
            }

            sock.close();
        } catch (IOException x) {
            System.out.println("Socket error.");
            x.printStackTrace();
        }
    }
}
