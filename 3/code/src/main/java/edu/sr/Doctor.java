package edu.sr;

import edu.sr.consumers.DoctorConsumer;
import edu.sr.consumers.LoggerConsumer;
import edu.sr.enums.InjuryType;
import edu.sr.exceptions.InjuryTypeNameIsNotValidException;
import edu.sr.exceptions.ToLessArgumentsException;
import edu.sr.producers.Producer;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Doctor {

    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                throw new Exception("Provide one options");
            }
            String doctorId = args[0];
            System.out.println("Welcome doctor ("+doctorId+").");

            Producer producer = new Producer();

            DoctorConsumer doctorConsumer = new DoctorConsumer(doctorId);
            LoggerConsumer loggerConsumer = new LoggerConsumer("info", "[ADMIN INFO]");
            doctorConsumer.start();
            loggerConsumer.start();

            try (Scanner input = new Scanner(System.in)) {
                while (true) {
                    try {
                        System.out.println("Write a command (<injury>:<patient_name>): ");
                        String message = input.nextLine();
                        if (message.equals("exit")) {
                            break;
                        }
                        String[] cmd = message.split(":");
                        if (cmd.length < 2) {
                            throw new ToLessArgumentsException();
                        }
                        String injuryTypeName = cmd[0];
                        if (!InjuryType.isTypeNameValid(injuryTypeName)) {
                            throw new InjuryTypeNameIsNotValidException();
                        }

                        String request = String.format("%s:%s", doctorId, message);
                        producer.send(String.format("exam.%s", injuryTypeName), request);
                        producer.send("log", request);
                    } catch (ToLessArgumentsException | InjuryTypeNameIsNotValidException e) {
                        e.printStackTrace();
                    }
                }
            }

            doctorConsumer.close();
            loggerConsumer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
