package edu.sr;

import edu.sr.consumers.LoggerConsumer;
import edu.sr.producers.Producer;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Administrator {

    public static void main(String[] args) {
        try {
            System.out.println("Welcome administrator.");
            Producer producer = new Producer();
        
            LoggerConsumer loggerConsumer = new LoggerConsumer("log", "[LOG]");
            loggerConsumer.start();

            try (Scanner input = new Scanner(System.in)) {
                while (true) {
                    System.out.println("Write a message info for all: ");
                    String message = input.nextLine();
                    if (message.equals("exit")) {
                        break;
                    }

                    producer.send("info", message);
                }
            }

            loggerConsumer.close();

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

}
