/*--------------------------------------------------------

1. Name / Date:
Kahlil Bernard Chase
15 January 2015

2. Java version used, if not the official version for the class:

build 1.7.0_67-b01

3. Precise command-line compilation examples / instructions:

> javac InetServer.java


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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

class Worker extends Thread {
    /*
     * Constructs a Worker object that handles an incoming connection.
     */
    Socket sock;
    Worker (Socket s) { sock = s; }

    public void run() {
		/*
		 * Create Worker input stream in and output stream out;
		 * check for shutdown or quit request;
		 * respond to request by calling printRemoteAddress.
		 */
        PrintStream out = null;
        BufferedReader in = null;

        try {
            in = new BufferedReader (new InputStreamReader(sock.getInputStream()));
            out = new PrintStream(sock.getOutputStream());

            try {
                while (true) {
                    String textFromServer = in.readLine();
                    if (textFromServer == null || textFromServer.length() == 0)
                        break;
                    System.out.println(textFromServer);
                }
            } catch (IOException x) {
                System.out.println("Server read error");
                x.printStackTrace();
            }
            sock.close();
        } catch (IOException ioe) { System.out.println(ioe); }
    }
}

public class MyListener {
    /*
     * Starts a server which listens on a port and starts a new Worker thread for every request made.
     */
    public static boolean controlSwitch = true;

    public static void main(String a[]) throws IOException {
        int q_len = 6;
        int port = 2540;
        Socket sock;
        ServerSocket servsock = new ServerSocket(port, q_len);

        System.out.println("Kahlil Bernard Chase's MyListener starting up, listening at port 2540.\n");
        while (controlSwitch) {
            sock = servsock.accept();
            if (controlSwitch) new Worker(sock).start();
            // try { Thread.sleep(10000); }
        }
        servsock.close();
    }
}
