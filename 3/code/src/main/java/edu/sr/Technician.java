package edu.sr;

import edu.sr.consumers.AbstractConsumer;
import edu.sr.consumers.LoggerConsumer;
import edu.sr.consumers.TechnicianConsumer;
import edu.sr.enums.InjuryType;

import static edu.sr.enums.InjuryType.isTypeNameValid;

public class Technician {

    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                throw new Exception("Provide two options");
            }
            if (!isTypeNameValid(args[0]) || !isTypeNameValid(args[1])) {
                throw new Exception("Injury types are not valid");
            }
            System.out.println("Welcome technician ("+args[0]+", "+args[1]+").");

            AbstractConsumer technicianConsumer1 = new TechnicianConsumer(InjuryType.getEnumByTypeName(args[0]));
            AbstractConsumer technicianConsumer2 = new TechnicianConsumer(InjuryType.getEnumByTypeName(args[1]));
            AbstractConsumer infoLoggerConsumer = new LoggerConsumer("info", "[ADMIN INFO]");

            technicianConsumer1.start();
            technicianConsumer2.start();
            infoLoggerConsumer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
