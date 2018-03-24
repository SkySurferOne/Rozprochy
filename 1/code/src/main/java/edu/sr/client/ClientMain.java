package edu.sr.client;

import java.io.IOException;

public class ClientMain {

    public static void main(String[] args) {
        try {
            String hostName = args[0];
            int portNumber = Integer.parseInt(args[1]);
            String nick = args[2];

            Client client = new Client(hostName, portNumber, nick);
            Thread thread = new Thread(client);
            thread.start();
        } catch (IllegalThreadStateException | NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

}
