package edu.sr.client;

import edu.sr.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ClientIn implements Runnable {
    private final ObjectInputStream objectInputStream;

    public ClientIn(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message message = (Message) objectInputStream.readObject();
                message.print();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
