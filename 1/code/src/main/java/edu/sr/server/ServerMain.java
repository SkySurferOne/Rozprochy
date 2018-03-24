package edu.sr.server;

import java.io.IOException;

public class ServerMain {

    public static void main(String[] args) {
        try {
            int portNumber = 9000;
            int maxCliensNumber = 100;
            if (args.length >= 1) {
                portNumber = Integer.parseInt(args[0]);
            }
            if (args.length >= 2) {
                maxCliensNumber = Integer.parseInt(args[1]);
            }

            Thread thread = new Thread(new Server(portNumber, maxCliensNumber));
            thread.start();
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

}
