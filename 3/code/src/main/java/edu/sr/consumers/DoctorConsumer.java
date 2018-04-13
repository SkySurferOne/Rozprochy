package edu.sr.consumers;

import com.rabbitmq.client.Envelope;
import edu.sr.exceptions.ToLessArgumentsException;

import java.io.IOException;

public class DoctorConsumer extends AbstractConsumer {
    private final String id;

    public DoctorConsumer(String id) {
        super("res." + id, true);
        this.id = id;
    }

    @Override
    public void handle(String message, Envelope envelope) {
        try {
            String[] data = message.split(":");
            if (data.length < 3) {
                throw new ToLessArgumentsException();
            }
            System.out.println("[DC]: " + message);

        } catch (ToLessArgumentsException e) {
            e.printStackTrace();
        }
    }
}
