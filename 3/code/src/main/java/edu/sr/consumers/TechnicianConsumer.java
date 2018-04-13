package edu.sr.consumers;

import com.rabbitmq.client.Envelope;
import edu.sr.enums.InjuryType;
import edu.sr.exceptions.InjuryTypeNameIsNotValidException;
import edu.sr.exceptions.ToLessArgumentsException;
import edu.sr.producers.Producer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TechnicianConsumer extends AbstractConsumer {
    private final Producer producer;

    public TechnicianConsumer(InjuryType injuryType) throws IOException, TimeoutException {
        super("exam."+injuryType.toString(), false, "exam_"+injuryType.toString()+"_queue");
        this.producer = new Producer();
    }

    @Override
    public void handle(String message, Envelope envelope) throws IOException {
        try {
            String[] data = message.split(":");
            if (data.length < 3) {
                throw new ToLessArgumentsException();
            }
            if (!InjuryType.isTypeNameValid(data[1])) {
                throw new InjuryTypeNameIsNotValidException();
            }
            String doctorId = data[0];
            String injuryTypeName = data[1];
            String patientName = data[2];

            System.out.println("[TC]: "+message);

            // perform examination
            Thread.sleep(4000);

            String response = String.format("%s:%s:done", patientName, injuryTypeName);
            producer.send(String.format("res.%s", doctorId), response);
            producer.send("log", response);

        } catch (InterruptedException | ToLessArgumentsException | InjuryTypeNameIsNotValidException e) {
            e.printStackTrace();
        } finally {
            channel.basicAck(envelope.getDeliveryTag(), false);
        }

    }
}
